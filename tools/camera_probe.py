#!/usr/bin/env python3
"""Samsung ST150F (SSC) protocol probe.

Reproduces, from a Linux host, the two end-to-end proofs claimed for the
Samsung Smart Camera protocol:

  1. bare GET of a DIDL-Lite <res> URL -> HTTP 200 with real bytes
  2. ffprobe of the Remote Viewfinder stream URL from GetInfomation

and records enough raw material to document every claim verbatim.

The camera's Wi-Fi stack is slow to answer (SYN-ACK latency 0.02-5.5 s while
power-save is engaged), so every step keeps a 0.3 s ping flowing and uses
generous timeouts. Never run concurrent port scans against this device: under
load it drops SYN for every port, including ones that are open.

Usage:
  python3 tools/camera_probe.py ml  [--wait 900] [--no-reconnect]
  python3 tools/camera_probe.py rvf [--wait 900] [--no-reconnect]
  python3 tools/camera_probe.py sp  [--wait 900] [--no-reconnect]

Exit codes: 0 proof captured, 2 wrong camera state, 3 blocked, 4 partial.
"""

import argparse
import http.server
import os
import re
import select
import socket
import subprocess
import sys
import threading
import time
import urllib.parse
import xml.etree.ElementTree as ET

WLAN = "wlan0"
NM_PROFILE = "8115cf70-e6b7-4831-a749-1d1ab88d80ea"
CAMERA_SSID = "AP_SSC_ST150F_0-7D:41:4B"
EXPECTED_OCTET = {"ml": 101, "rvf": 102, "sp": 104}
EVENT_PORT = 8059
EVENT_URI = "/evetSub"
SSDP_GROUP = "239.255.255.250"
UA_PREFIX = {"ml": "SEC_DSC_", "rvf": "SEC_RVF_", "sp": "SEC_SP_"}
DESC_FALLBACK_UA = ("SEC_RVF_", "SEC_DSC_", "SEC_RVF_ML_")
BLIND_DESC_PATHS = (
    "/smp_2_", "/", "/description.xml", "/desc.xml", "/rootDesc.xml", "/dev0/desc.xml",
    "/upnp/desc.xml", "/device.xml", "/dd.xml",
)
BLIND_CONTROL_PATHS = (
    "/control/ContentDirectory", "/upnp/control/ContentDirectory",
    "/ContentDirectory/control", "/control", "/upnp/control", "/",
)
CD_SERVICE = "urn:schemas-upnp-org:service:ContentDirectory:1"


# ---------------------------------------------------------------- utilities

def out(key, value=""):
    print(f"{key}={value}", flush=True)


def note(msg):
    print(f"# {msg}", flush=True)


def sh(cmd, timeout=30):
    try:
        p = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
        return p.returncode, p.stdout, p.stderr
    except (subprocess.TimeoutExpired, OSError) as exc:
        return 1, "", str(exc)


def wlan_mac():
    try:
        with open(f"/sys/class/net/{WLAN}/address") as fh:
            return fh.read().strip().lower()
    except OSError:
        return "00:00:00:00:00:00"


def wlan_ip():
    rc, stdout, _ = sh(["ip", "-4", "addr", "show", WLAN])
    for line in stdout.splitlines():
        m = re.search(r"inet (\d+\.\d+\.\d+\.\d+)/", line)
        if m:
            return m.group(1)
    return None


def wlan_gw():
    rc, stdout, _ = sh(["ip", "route", "show", "dev", WLAN])
    for line in stdout.splitlines():
        m = re.search(r"default via (\d+\.\d+\.\d+\.\d+)", line)
        if m:
            return m.group(1)
    for line in stdout.splitlines():
        m = re.search(r"^(\d+\.\d+\.\d+\.\d+)/\d+", line)
        if m:
            return m.group(1)
    return None


def octet(ip):
    try:
        return int(ip.split(".")[2])
    except (AttributeError, IndexError, ValueError):
        return -1


def ssid():
    rc, stdout, _ = sh(["iw", "dev", WLAN, "link"])
    for line in stdout.splitlines():
        m = re.search(r"SSID:\s*(.+)", line)
        if m:
            return m.group(1).strip()
    return "none"


class Keepalive(threading.Thread):
    """ping -i 0.3 in the background; without it this camera stalls."""

    def __init__(self, gw):
        super().__init__(daemon=True)
        self.gw = gw
        self.proc = None

    def run(self):
        try:
            self.proc = subprocess.Popen(
                ["ping", "-i", "0.3", "-W", "1", "-c", "100000", self.gw],
                stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            self.proc.wait()
        except OSError:
            pass

    def stop(self):
        if self.proc and self.proc.poll() is None:
            self.proc.kill()


def ensure_link(mode, wait, reconnect, expected=None):
    """Wait for the camera AP; reconnect the saved profile when it drops."""
    expected = set(expected or [EXPECTED_OCTET[mode]])
    deadline = time.time() + wait
    last = None
    next_nudge = 0.0
    while True:
        ip = wlan_ip()
        gw = wlan_gw()
        state = (ip, gw, ssid())
        if state != last:
            note(f"link ip={ip} gw={gw} ssid={state[2]}")
            last = state
        if ip and gw and octet(gw) in expected:
            return ip, gw
        if time.time() >= deadline:
            out("STATE_MISMATCH", f"octet={octet(gw) if gw else 'none'}")
            out("LINK_SSID", state[2])
            return None, None
        # Only reconnect when the host is *not* associated with the camera AP;
        # while the camera is up in another mode, only the user can change it.
        if reconnect and state[2] != CAMERA_SSID and time.time() >= next_nudge:
            sh(["nmcli", "connection", "up", NM_PROFILE], timeout=60)
            next_nudge = time.time() + 30
        time.sleep(5)


def tcp_open(host, port, timeout=30):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(timeout)
    try:
        return s.connect_ex((host, port)) == 0
    except OSError:
        return False
    finally:
        s.close()


def raw_http(host, port, payload, timeout=30, read_all=False):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(timeout)
    try:
        s.connect((host, port))
        s.sendall(payload)
        chunks = []
        while True:
            try:
                data = s.recv(65536)
            except socket.timeout:
                break
            if not data:
                break
            chunks.append(data)
            if not read_all and b"\r\n\r\n" in b"".join(chunks):
                break
        return b"".join(chunks)
    except OSError as exc:
        return b"ERR " + str(exc).encode()
    finally:
        s.close()


def curl_http(url, method=None, headers=None, data=None, timeout=60):
    """Return (rc, status_line, headers, body, stderr) using curl -i."""
    cmd = ["curl", "-sS", "--max-time", str(timeout), "-i"]
    if method:
        cmd += ["-X", method]
    for key, value in (headers or {}).items():
        cmd += ["-H", f"{key}: {value}"]
    if data is not None:
        cmd += ["--data-binary", data]
    cmd.append(url)
    rc, stdout, stderr = sh(cmd, timeout=timeout + 15)
    if rc != 0:
        return rc, "", {}, "", stderr
    # :7676 answers with LF-only line endings, :7679 with CRLF; accept both.
    head, body = stdout, ""
    for sep in ("\r\n\r\n", "\n\n"):
        idx = stdout.find(sep)
        if idx != -1:
            head, body = stdout[:idx], stdout[idx + len(sep):]
            break
    lines = re.split(r"\r?\n", head)
    status = lines[0] if lines else ""
    hdrs = {}
    for line in lines[1:]:
        if ":" in line:
            k, v = line.split(":", 1)
            hdrs[k.strip().lower()] = v.strip()
    return 0, status, hdrs, body, ""


def curl_download(url, path, timeout=120):
    rc, stdout, stderr = sh([
        "curl", "-sS", "--max-time", str(timeout), "-o", path,
        "-w", "%{http_code} %{content_type} %{size_download}", url,
    ], timeout=timeout + 15)
    if rc != 0:
        return rc, "", "", stderr
    return 0, stdout.strip(), path, ""


# ------------------------------------------------------------- SSDP / NOTIFY

def notify_listen(gw, seconds=25, msearch=True):
    """The camera never answers M-SEARCH; it multicasts NOTIFY with LOCATION
    when the station associates. Listen for it, optionally probing M-SEARCH."""
    ip = wlan_ip()
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    try:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
    except OSError:
        pass
    sock.bind(("", 1900))
    try:
        sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP,
                        socket.inet_aton(SSDP_GROUP) + socket.inet_aton(ip))
        sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_IF,
                        socket.inet_aton(ip))
        sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_LOOP, 0)
    except OSError:
        pass
    sock.setblocking(False)
    msg = (f"M-SEARCH * HTTP/1.1\r\nHOST: {SSDP_GROUP}:1900\r\n"
           'MAN: "ssdp:discover"\r\nMX: 3\r\nST: ssdp:all\r\n\r\n').encode()
    location, seen = None, set()
    end = time.time() + seconds
    while time.time() < end:
        # The link may come up after the socket was created; re-join so the
        # camera's post-association NOTIFY burst is not missed.
        cur_ip = wlan_ip()
        if cur_ip:
            try:
                sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP,
                                socket.inet_aton(SSDP_GROUP) + socket.inet_aton(cur_ip))
            except OSError:
                pass
        if msearch:
            try:
                sock.sendto(msg, (SSDP_GROUP, 1900))
            except OSError:
                pass
        ready, _, _ = select.select([sock], [], [], 1.0)
        for _ in ready:
            try:
                data, addr = sock.recvfrom(4096)
            except OSError:
                break
            if addr[0] != gw or data in seen:
                continue
            seen.add(data)
            print("NOTIFY_BEGIN", flush=True)
            print(data.decode("utf-8", "replace"), flush=True)
            print("NOTIFY_END", flush=True)
            m = re.search(rb"(?im)^LOCATION:\s*(\S+)", data)
            if m and location is None:
                location = m.group(1).decode()
    sock.close()
    out("NOTIFY_COUNT", len(seen))
    return location


def force_assoc(wait=25):
    """Cycle the profile: the camera announces itself on association."""
    note("re-associating to trigger the camera's SSDP NOTIFY")
    sh(["nmcli", "connection", "down", NM_PROFILE], timeout=30)
    time.sleep(2)
    sh(["nmcli", "connection", "up", NM_PROFILE], timeout=60)
    deadline = time.time() + wait
    while time.time() < deadline:
        if wlan_ip() and wlan_gw():
            return True
        time.sleep(2)
    return False


def discover(gw, seconds):
    """LOCATION via NOTIFY. The camera announces only on association, so when
    the first listen is silent the link is cycled *while* listening."""
    location = notify_listen(gw, seconds=min(seconds, 20))
    if location:
        return location
    note("re-associating while listening for NOTIFY")
    holder = {}

    def worker():
        holder["loc"] = notify_listen(wlan_gw() or gw, seconds=max(seconds, 40))

    thread = threading.Thread(target=worker, daemon=True)
    thread.start()
    time.sleep(2)
    sh(["nmcli", "connection", "down", NM_PROFILE], timeout=30)
    time.sleep(2)
    sh(["nmcli", "connection", "up", NM_PROFILE], timeout=60)
    thread.join(timeout=max(seconds, 40) + 15)
    location = holder.get("loc")
    if location:
        return location
    # Deterministic fallback: Samsung's description path is fixed per firmware.
    for port in (7676, 7679):
        if tcp_open(gw, port, timeout=10):
            url = f"http://{gw}:{port}/smp_2_"
            out("LOCATION_FALLBACK", url)
            return url
    return None


# ---------------------------------------------------------------------- SSDP

def ssdp_probe(gw, ports, seconds=60, rounds=5):
    """M-SEARCH from source ports 1900/1901 to multicast and unicast."""
    socks = []
    for sport in (1900, 1901):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        try:
            s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        except OSError:
            pass
        try:
            s.bind(("", sport))
        except OSError:
            s.close()
            continue
        try:
            s.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_IF,
                         socket.inet_aton(wlan_ip() or "0.0.0.0"))
            s.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 2)
        except OSError:
            pass
        s.settimeout(0.5)
        socks.append(s)
    if not socks:
        out("SSDP", "no_socket")
        return []

    seen = {}
    deadline = time.time() + seconds
    per_round = max(1.0, seconds / rounds)
    for _ in range(rounds):
        if time.time() >= deadline:
            break
        for sport_sock in socks:
            for port in ports:
                for dst in (SSDP_GROUP, gw):
                    msg = (
                        "M-SEARCH * HTTP/1.1\r\n"
                        f"HOST: {SSDP_GROUP}:{port}\r\n"
                        'MAN: "ssdp:discover"\r\n'
                        "MX: 3\r\n"
                        "ST: ssdp:all\r\n\r\n"
                    ).encode()
                    try:
                        sport_sock.sendto(msg, (dst, port))
                    except OSError:
                        pass
        round_end = min(deadline, time.time() + per_round)
        while time.time() < round_end:
            for s in socks:
                try:
                    data, addr = s.recvfrom(4096)
                except (socket.timeout, OSError):
                    continue
                key = (addr[0], data)
                if key not in seen:
                    seen[key] = addr
    replies = sorted(seen.items(), key=lambda kv: kv[0][0])
    for (src, data), _addr in replies:
        text = data.decode("utf-8", "replace")
        tag = "SSDP_REPLY" if src == gw else "SSDP_OTHER"
        print(f"{tag}_BEGIN src={src}", flush=True)
        print(text, flush=True)
        print(f"{tag}_END", flush=True)
        m = re.search(r"(?im)^LOCATION:\s*(\S+)", text)
        if m:
            out("SSDP_LOCATION", m.group(1))
    return [d.decode("utf-8", "replace") for (_src, d), _ in replies]


# --------------------------------------------------------------- XML helpers

def local(tag):
    return tag.rsplit("}", 1)[-1]


def child(elem, name):
    for c in list(elem):
        if local(c.tag) == name:
            return c
    return None


def descendants(elem, name):
    return [e for e in elem.iter() if local(e.tag) == name]


def parse_description(body):
    root = ET.fromstring(body)
    for dev in descendants(root, "device"):
        dt = child(dev, "deviceType")
        if dt is None or "MediaServer" not in (dt.text or ""):
            continue
        sl = child(dev, "serviceList")
        if sl is None:
            continue
        for svc in list(sl):
            if local(svc.tag) != "service":
                continue
            st = child(svc, "serviceType")
            if st is None or "ContentDirectory" not in (st.text or ""):
                continue
            return {
                "device_type": (dt.text or "").strip(),
                "service_type": (st.text or "").strip(),
                "control": (child(svc, "controlURL").text or "").strip()
                if child(svc, "controlURL") is not None else "",
                "eventsub": (child(svc, "eventSubURL").text or "").strip()
                if child(svc, "eventSubURL") is not None else "",
                "scpd": (child(svc, "SCPDURL").text or "").strip()
                if child(svc, "SCPDURL") is not None else "",
            }
    return None


def soap_body(service_type, action, args_xml):
    return (
        '<?xml version="1.0" encoding="utf-8"?>'
        '<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" '
        's:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>'
        f'<u:{action} xmlns:u="{service_type}">{args_xml}</u:{action}>'
        "</s:Body></s:Envelope>"
    )


def soap_call(control_url, service_type, action, args_xml, timeout=60, headers=None):
    hdrs = {
        "Content-Type": 'text/xml; charset="utf-8"',
        "SOAPACTION": f'"{service_type}#{action}"',
    }
    if headers:
        hdrs.update(headers)
    return curl_http(
        control_url, method="POST",
        headers=hdrs,
        data=soap_body(service_type, action, args_xml),
        timeout=timeout,
    )


def result_xml(body):
    try:
        root = ET.fromstring(body)
    except ET.ParseError:
        return None
    for elem in descendants(root, "Result"):
        return elem.text or ""
    return None


def didl_entries(didl):
    """Yield (kind, id, title, [res dicts])."""
    if not didl:
        return
    try:
        root = ET.fromstring(didl)
    except ET.ParseError:
        note(f"didl_parse_failed head={didl[:120]!r}")
        return
    for elem in root.iter():
        kind = local(elem.tag)
        if kind not in ("container", "item"):
            continue
        title = child(elem, "title")
        res = []
        for r in list(elem):
            if local(r.tag) == "res":
                res.append({
                    "url": (r.text or "").strip(),
                    "protocol_info": r.attrib.get("protocolInfo", ""),
                    "size": r.attrib.get("size", ""),
                    "resolution": r.attrib.get("resolution", ""),
                })
        yield kind, elem.attrib.get("id", ""), (title.text or "") if title is not None else "", res


def browse_page(control_url, service_type, object_id, start, count, auth=None):
    args = (
        f"<ObjectID>{object_id}</ObjectID>"
        "<BrowseFlag>BrowseDirectChildren</BrowseFlag>"
        "<Filter>*</Filter>"
        f"<StartingIndex>{start}</StartingIndex>"
        f"<RequestedCount>{count}</RequestedCount>"
        "<SortCriteria></SortCriteria>"
    )
    rc, status, _hdrs, body, err = soap_call(control_url, service_type, "Browse", args, headers=auth)
    if rc != 0:
        out("BROWSE_ERROR", err.strip())
        return None, 0
    if " 200 " not in status:
        out("BROWSE_STATUS", status)
        print(body[:2000], flush=True)
        return None, 0
    xml_text = result_xml(body)
    try:
        root = ET.fromstring(body)
        returned = child(root, "NumberReturned")
        total = child(root, "TotalMatches")
        n = int(returned.text) if returned is not None and returned.text else 0
        out("BROWSE_RETURNED", n)
        out("BROWSE_TOTAL", (total.text or "").strip() if total is not None else "")
    except (ET.ParseError, ValueError):
        n = 0
    return xml_text, n


def browse_all(control_url, service_type, max_requests=40, auth=None):
    """Walk the library: page the root, then descend up to two container levels."""
    items, containers = [], []
    xml_text, n = browse_page(control_url, service_type, "0", 0, 100, auth=auth)
    if xml_text is None:
        return items, containers
    for kind, oid, title, res in didl_entries(xml_text):
        for r in res:
            out("RES", f"{r['protocol_info']}|{r['size']}|{r['resolution']}|{r['url']}")
        if kind == "item":
            out("ITEM", f"{oid}|{title}")
            items.append({"id": oid, "title": title, "res": res})
        else:
            out("CONTAINER", f"{oid}|{title}")
            containers.append({"id": oid, "title": title})

    start = 100
    while n == 100 and start < 500 and len(items) + len(containers) < max_requests * 100:
        xml_text, n = browse_page(control_url, service_type, "0", start, 100, auth=auth)
        if xml_text is None:
            break
        for kind, oid, title, res in didl_entries(xml_text):
            for r in res:
                out("RES", f"{r['protocol_info']}|{r['size']}|{r['resolution']}|{r['url']}")
            if kind == "item":
                out("ITEM", f"{oid}|{title}")
                items.append({"id": oid, "title": title, "res": res})
            else:
                out("CONTAINER", f"{oid}|{title}")
                containers.append({"id": oid, "title": title})
        start += 100

    if items:
        return items, containers

    # Root held only containers: descend depth-first, two levels, 5 per level.
    requests = 0
    queue = [(c["id"], 1) for c in containers[:5]]
    while queue and requests < max_requests and not items:
        oid, depth = queue.pop(0)
        requests += 1
        xml_text, _n = browse_page(control_url, service_type, oid, 0, 100, auth=auth)
        if xml_text is None:
            continue
        for kind, cid, title, res in didl_entries(xml_text):
            for r in res:
                out("RES", f"{r['protocol_info']}|{r['size']}|{r['resolution']}|{r['url']}")
            if kind == "item":
                out("ITEM", f"{cid}|{title}")
                items.append({"id": cid, "title": title, "res": res})
            elif depth < 2:
                out("CONTAINER", f"{cid}|{title}")
                queue.append((cid, depth + 1))
    return items, containers


# ------------------------------------------------------------ GENA + download

class NotifyHandler(http.server.BaseHTTPRequestHandler):
    def do_NOTIFY(self):
        length = int(self.headers.get("Content-Length") or 0)
        body = self.rfile.read(length).decode("utf-8", "replace")
        out("NOTIFY", " ".join(body.split()))
        self.send_response(200)
        self.end_headers()

    def log_message(self, *args):
        pass


def start_event_server():
    http.server.ThreadingHTTPServer.allow_reuse_address = True
    try:
        srv = http.server.ThreadingHTTPServer(("", EVENT_PORT), NotifyHandler)
    except OSError as exc:
        out("EVENTSERVER", f"failed {exc}")
        return None
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    out("EVENTSERVER", f"listening {EVENT_PORT}{EVENT_URI}")
    return srv


def do_subscribe(gw, http_port, path, host_ip, auth=None):
    extra = "".join(f"{k}: {v}\r\n" for k, v in (auth or {}).items())
    req = (
        f"SUBSCRIBE {path} HTTP/1.1\r\n"
        f"HOST: {gw}:{http_port}\r\n"
        f"CALLBACK: <http://{host_ip}:{EVENT_PORT}{EVENT_URI}>\r\n"
        "NT: upnp:event\r\n"
        "TIMEOUT: Second-1800\r\n"
        f"{extra}"
        "\r\n"
    ).encode()
    reply = raw_http(gw, http_port, req, timeout=8)
    text = reply.decode("utf-8", "replace")
    print("SUBSCRIBE_BEGIN", flush=True)
    print(text, flush=True)
    print("SUBSCRIBE_END", flush=True)
    m = re.search(r"(?im)^SID:\s*(\S+)", text)
    if m:
        out("SID", m.group(1))
    return text


def first_url(items, needles):
    for item in items:
        for res in item["res"]:
            pi = res["protocol_info"]
            if any(n in pi for n in needles) and res["url"]:
                return res
    return None


def download_proof(items):
    thumb = first_url(items, ("JPEG_TN", "JPEG_SM", "PNG_TN"))
    orig = first_url(items, ("JPEG_LRG", "JPEG_MED", "video/mp4", "video/"))
    if not thumb and not orig:
        out("DOWNLOAD", "no_res")
        return False
    ok = False
    for label, res, path in (("THUMB", thumb, "/tmp/proof_thumb.jpg"),
                             ("ORIG", orig, "/tmp/proof_orig.bin")):
        if not res:
            out(f"DOWNLOAD_{label}", "none")
            continue
        rc, stats, saved, err = curl_download(res["url"], path)
        if rc != 0:
            out(f"DOWNLOAD_{label}", f"curl_failed {err.strip()}")
            continue
        out(f"DOWNLOAD_{label}", stats)
        rc, ftype, _ = sh(["file", "-b", saved])
        out(f"FILE_{label}", ftype.strip())
        try:
            size = os.path.getsize(saved)
            out(f"SIZE_{label}", size)
            ok = ok or (stats.startswith("200") and size > 1000)
        except OSError:
            pass
    return ok


# ------------------------------------------------------------------ fallbacks

def blind_description(gw, host_ip, mac):
    """Minimal raw GETs, then blind SOAP, when SSDP produced no LOCATION."""
    for port in (7679, 5000):
        if not tcp_open(gw, port, timeout=15):
            out("TCP", f"{port}=closed")
            continue
        out("TCP", f"{port}=open")
        for path in BLIND_DESC_PATHS:
            for proto, tail in (
                ("1.0", "\r\n\r\n\r\n\r\n"),
                ("1.1", f"\r\nHost: {gw}\r\n\r\n"),
            ):
                reply = raw_http(gw, port, f"GET {path} HTTP/{proto}{tail}".encode(), timeout=15)
                text = reply.decode("utf-8", "replace")
                status = text.split("\r\n", 1)[0] if text else "no_reply"
                out("RAWGET", f"{port} {path} {proto} -> {status}")
                if "<root" in text and "MediaServer" in text:
                    body = text.split("\r\n\r\n", 1)[-1]
                    out("DESC_FALLBACK_URL", f"http://{gw}:{port}{path}")
                    return f"http://{gw}:{port}{path}", body
    for path in BLIND_CONTROL_PATHS:
        url = f"http://{gw}:7679{path}"
        args = ("<ObjectID>0</ObjectID><BrowseFlag>BrowseDirectChildren</BrowseFlag>"
                "<Filter>*</Filter><StartingIndex>0</StartingIndex>"
                "<RequestedCount>10</RequestedCount><SortCriteria></SortCriteria>")
        rc, status, _h, body, err = soap_call(url, CD_SERVICE, "Browse", args, timeout=20)
        out("BLINDSOAP", f"{path} -> {status or err.strip()}")
        if rc == 0 and " 200 " in status and "NumberReturned" in body:
            print(body[:4000], flush=True)
            return None, None
    return None, None


# ---------------------------------------------------------------- mode flows

def get_description(location, mac, prefix="SEC_DSC_"):
    uas = [prefix + mac] + [p + mac for p in DESC_FALLBACK_UA]
    headers_extra = {"Access-Method": "manual"}
    for ua in uas:
        rc, status, hdrs, body, err = curl_http(
            location, headers=dict(headers_extra, **{"User-Agent": ua}), timeout=60)
        out("DESC_FETCH", f"ua={ua} status={status or err.strip()}")
        if rc == 0 and body and "<root" in body:
            return body
    return None


def extract_location(texts):
    for text in texts:
        m = re.search(r"(?im)^LOCATION:\s*(\S+)", text)
        if m:
            return m.group(1)
    return None


def resolve(base, path):
    return urllib.parse.urljoin(base, path)


def run_ml(args):
    host_ip, gw = ensure_link("ml", args.wait, args.reconnect, args.expect_octet)
    if not host_ip:
        return 2
    keep = Keepalive(gw)
    keep.start()
    srv = None
    mac = wlan_mac()
    auth = {"User-Agent": UA_PREFIX["ml"] + mac, "Access-Method": "manual"}
    try:
        out("MODE", "ml")
        out("HOST_IP", host_ip)
        out("GATEWAY", gw)
        out("WLAN_MAC", mac)
        for port in (7676, 7679):
            out(f"TCP_{port}", "open" if tcp_open(gw, port, timeout=20) else "closed")

        location = discover(gw, args.ssdp_wait)
        if not location:
            out("ML_BLOCKED", "reason=no_notify_location")
            return 3
        out("LOCATION", location)
        body = get_description(location, mac, UA_PREFIX["ml"])
        if not body:
            out("ML_BLOCKED", "reason=no_description")
            return 3
        desc = parse_description(body)
        if not desc:
            out("ML_BLOCKED", "reason=no_mediaserver_contentdirectory")
            return 3
        out("DEVICETYPE", desc["device_type"])
        out("SERVICE", desc["service_type"])
        control = resolve(location, desc["control"])
        eventsub = resolve(location, desc["eventsub"])
        scpd = resolve(location, desc["scpd"])
        out("CONTROLURL", control)
        out("EVENTSUBURL", eventsub)
        out("SCPDURL", scpd)
        if desc["scpd"]:
            rc, status, _h, scpd_body, err = curl_http(scpd, headers=auth, timeout=60)
            out("SCPD_FETCH", status or err.strip())
            print("SCPD_BEGIN", flush=True)
            print(scpd_body[:20000], flush=True)
            print("SCPD_END", flush=True)

        # GENA is not usable on this firmware: an unanswered SUBSCRIBE blocks
        # the camera's single connection handler, so it is deliberately skipped.
        srv = start_event_server()

        items, containers = browse_all(control, desc["service_type"], auth=auth)
        out("BROWSE_TOTAL_ITEMS", len(items))
        if not items:
            out("BROWSE_EMPTY", "no_items")
            out("ML_PARTIAL", "reason=no_items")
            return 4
        ok = download_proof(items)
        time.sleep(3)
        out("ML_PASS" if ok else "ML_BLOCKED",
            "download_200" if ok else "reason=download_not_200")
        return 0 if ok else 3
    finally:
        keep.stop()
        if srv:
            srv.shutdown()


def get_information(control, service_type, auth=None):
    """The firmware SCPD names this action 'GetInfomation' (sic)."""
    args = ""
    for action in ("GetInfomation", "GetInformation"):
        rc, status, _h, body, err = soap_call(control, service_type, action, args,
                                              headers=auth)
        out("GETINFO", f"action={action} -> {status or err.strip()}")
        if rc == 0 and " 200 " in status:
            return body
        print(body[:2000], flush=True)
    return None


def parse_stream_urls(body):
    """Return dict of StreamUrl children plus StreamQuality children."""
    result = {"stream": {}, "quality": {}, "ip_result": None}
    try:
        root = ET.fromstring(body)
    except ET.ParseError:
        return result
    for elem in root.iter():
        name = local(elem.tag)
        if name == "StreamUrl":
            for c in list(elem):
                result["stream"][local(c.tag)] = (c.text or "").strip()
        elif name == "StreamQuality":
            for c in list(elem):
                result["quality"][local(c.tag)] = (c.text or "").strip()
        elif name == "GETIPRESULT":
            result["ip_result"] = (elem.text or "").strip()
    return result


def run_rvf(args):
    host_ip, gw = ensure_link("rvf", args.wait, args.reconnect, args.expect_octet)
    if not host_ip:
        return 2
    keep = Keepalive(gw)
    keep.start()
    srv = None
    mac = wlan_mac()
    auth = {"User-Agent": UA_PREFIX["rvf"] + mac, "Access-Method": "manual"}
    try:
        out("MODE", "rvf")
        out("GATEWAY", gw)
        out("WLAN_MAC", mac)
        for port in (7676, 7679):
            out(f"TCP_{port}", "open" if tcp_open(gw, port, timeout=20) else "closed")

        location = discover(gw, args.ssdp_wait)
        if not location:
            out("RVF_BLOCKED", "reason=no_notify_location")
            return 3
        out("LOCATION", location)
        body = get_description(location, mac, UA_PREFIX["rvf"])
        if not body:
            out("RVF_BLOCKED", "reason=no_description")
            return 3
        desc = parse_description(body)
        if not desc:
            out("RVF_BLOCKED", "reason=no_mediaserver_contentdirectory")
            return 3
        control = resolve(location, desc["control"])
        eventsub = resolve(location, desc["eventsub"])
        out("DEVICETYPE", desc["device_type"])
        out("SERVICE", desc["service_type"])
        out("CONTROLURL", control)
        out("EVENTSUBURL", eventsub)
        out("SCPDURL", resolve(location, desc["scpd"]))
        if desc["scpd"]:
            rc, status, _h, scpd_body, err = curl_http(
                resolve(location, desc["scpd"]), headers=auth, timeout=60)
            out("SCPD_FETCH", status or err.strip())
            try:
                names = [e.text or "" for e in ET.fromstring(scpd_body).iter()
                         if local(e.tag) == "name"]
                out("SCPD_ACTIONS", " ".join(names))
            except ET.ParseError:
                pass
            print("SCPD_BEGIN", flush=True)
            print(scpd_body[:20000], flush=True)
            print("SCPD_END", flush=True)

        # GENA is not usable on this firmware: an unanswered SUBSCRIBE blocks
        # the camera's single connection handler, so it is deliberately skipped.
        srv = start_event_server()

        info = get_information(control, desc["service_type"], auth)
        if not info:
            out("RVF_BLOCKED", "reason=no_getinfomation")
            return 3
        print("GETINFO_BODY_BEGIN", flush=True)
        print(info, flush=True)
        print("GETINFO_BODY_END", flush=True)
        parsed = parse_stream_urls(info)
        for name, value in parsed["stream"].items():
            out("STREAMURL_CHILD", f"{name} {value}")
        for name, value in parsed["quality"].items():
            out("STREAMQUALITY_CHILD", f"{name} {value}")
        high = parsed["stream"].get("QualityHighUrl") or parsed["stream"].get("QualityLowUrl")
        if not high and parsed["ip_result"]:
            high = parsed["ip_result"]
            out("STREAMURL_FROM_GETIPRESULT", high)
        if not high:
            out("RVF_BLOCKED", "reason=no_stream_url")
            return 3
        out("STREAMURL_HIGH", high)

        rc, status, hdrs, _b, err = curl_http(high, method="HEAD", timeout=60)
        out("STREAM_HEAD", f"{status or err.strip()} type={hdrs.get('content-type', '')}")

        rc, stdout, stderr = sh([
            "timeout", "30", "ffprobe", "-v", "error",
            "-show_entries", "stream=codec_name,codec_type,width,height",
            "-show_entries", "format=format_name",
            "-of", "default=noprint_wrappers=1", high,
        ], timeout=60)
        print("FFPROBE_BEGIN", flush=True)
        print(stdout, flush=True)
        print(stderr, flush=True)
        print("FFPROBE_END", flush=True)
        ok = ("codec_name=" in stdout) and ("format_name=" in stdout)

        env = dict(os.environ, SDL_VIDEODRIVER="dummy", SDL_AUDIODRIVER="dummy")
        try:
            p = subprocess.run(
                ["timeout", "20", "ffplay", "-an", "-autoexit", "-t", "3",
                 "-loglevel", "error", high],
                capture_output=True, text=True, env=env, timeout=40)
            out("FFPLAY", f"rc={p.returncode}")
            if p.stderr.strip():
                print(p.stderr[:2000], flush=True)
        except (subprocess.TimeoutExpired, OSError) as exc:
            out("FFPLAY", f"failed {exc}")

        rc, status, _h, low_body, err = soap_call(
            control, desc["service_type"], "SetStreamQuality",
            "<Quality>low</Quality>", headers=auth)
        out("SETSTREAMQUALITY", status or err.strip())
        info2 = get_information(control, desc["service_type"], auth)
        if info2:
            parsed2 = parse_stream_urls(info2)
            low = parsed2["stream"].get("QualityLowUrl")
            out("STREAMURL_LOW", low or "none")
            out("STREAMURL_CHANGED", str(low != high).lower())

        time.sleep(3)
        out("RVF_PASS" if ok else "RVF_BLOCKED",
            "ffprobe_ok" if ok else "reason=ffprobe_failed")
        return 0 if ok else 3
    finally:
        keep.stop()
        if srv:
            srv.shutdown()


def run_sp(args):
    host_ip, gw = ensure_link("sp", args.wait, args.reconnect, args.expect_octet)
    if not host_ip:
        return 2
    keep = Keepalive(gw)
    keep.start()
    try:
        out("MODE", "sp")
        out("GATEWAY", gw)
        if not tcp_open(gw, 8100, timeout=30):
            out("TCP", "8100=closed")
        else:
            out("TCP", "8100=open")
        payload = (
            "HEAD /sp/control HTTP/1.1\r\n"
            f"Host: {gw}:8100\r\n"
            f"User-Agent: SEC_SP_{wlan_mac()}\r\n"
            f"Data-Server : {host_ip}:18100\r\n"
            "Data-Port : 18100\r\n"
            "NTS : alive\r\n"
            "HOST-PNumber : none\r\n"
            "Access-Method : manual\r\n"
            "\r\n"
        ).encode()
        reply = raw_http(gw, 8100, payload, timeout=30, read_all=True)
        text = reply.decode("utf-8", "replace")
        print("SP_BEGIN", flush=True)
        print(text[:8000], flush=True)
        print("SP_END", flush=True)
        status = text.split("\r\n", 1)[0] if text else ""
        if status.startswith("HTTP/"):
            out("SP_CONTROL", status)
            return 0
        out("SP_CONTROL", "no_reply")
        return 3
    finally:
        keep.stop()


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("mode", choices=("ml", "rvf", "sp"))
    ap.add_argument("--ssdp-wait", type=int, default=600,
                    help="seconds to keep listening for SSDP after the mode "
                         "gate passes (default 600)")
    ap.add_argument("--wait", type=int, default=900,
                    help="seconds to wait for the camera AP (default 900)")
    ap.add_argument("--expect-octet", type=lambda v: [int(x) for x in v.split(",")],
                    default=None,
                    help="override accepted subnet octets, e.g. 101,104")
    ap.add_argument("--no-reconnect", dest="reconnect", action="store_false",
                    help="do not cycle the saved NetworkManager profile")
    ap.set_defaults(reconnect=True)
    args = ap.parse_args()
    if args.mode == "ml":
        return run_ml(args)
    if args.mode == "rvf":
        return run_rvf(args)
    return run_sp(args)


if __name__ == "__main__":
    sys.exit(main())

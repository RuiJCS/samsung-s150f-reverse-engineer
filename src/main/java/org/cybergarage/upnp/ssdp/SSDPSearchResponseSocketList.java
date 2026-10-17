package org.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.SocketAddressException;
import org.cybergarage.util.IPAddressUtil;

/* JADX INFO: loaded from: classes.dex */
public class SSDPSearchResponseSocketList extends Vector {
    private InetAddress[] binds;

    public SSDPSearchResponseSocketList() {
        this.binds = null;
    }

    public SSDPSearchResponseSocketList(InetAddress[] binds) {
        this.binds = null;
        this.binds = binds;
    }

    public void setControlPoint(ControlPoint ctrlPoint) {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.setControlPoint(ctrlPoint);
        }
    }

    public SSDPSearchResponseSocket getSSDPSearchResponseSocket(int n) {
        return (SSDPSearchResponseSocket) get(n);
    }

    public boolean open(int port) {
        String[] bindAddresses;
        InetAddress[] binds = this.binds;
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }
        int j = 0;
        while (true) {
            try {
                int port2 = port;
                if (j < bindAddresses.length) {
                    if (IPAddressUtil.isIPv4Address(bindAddresses[j])) {
                        port = port2 + 1;
                        try {
                            SSDPSearchResponseSocket socket = new SSDPSearchResponseSocket(bindAddresses[j], port2);
                            add(socket);
                        } catch (Exception e) {
                        }
                    } else {
                        port = port2;
                    }
                    j++;
                } else {
                    return true;
                }
            } catch (Exception e2) {
            }
            stop();
            close();
            clear();
            return false;
        }
    }

    public boolean open() {
        return open(SSDP.PORT);
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.close();
        }
        clear();
    }

    public void start() throws SocketAddressException {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.stop();
        }
    }

    public boolean post(SSDPSearchRequest req) {
        boolean ret = true;
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            if (sock != null) {
                String bindAddr = sock.getLocalAddress();
                req.setLocalAddress(bindAddr);
                String ssdpAddr = SSDP.ADDRESS;
                if (HostInterface.isIPv6Address(bindAddr)) {
                    ssdpAddr = SSDP.getIPv6Address();
                }
                if (!sock.post(ssdpAddr, SSDP.PORT, req)) {
                    ret = false;
                }
            }
        }
        return ret;
    }
}

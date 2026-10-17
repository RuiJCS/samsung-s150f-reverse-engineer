package org.cybergarage.upnp.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Enumeration;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class HTTPMUSocket {
    private InetSocketAddress ssdpMultiGroup = null;
    private MulticastSocket ssdpMultiSock = null;
    private NetworkInterface ssdpMultiIf = null;

    public HTTPMUSocket() {
    }

    public HTTPMUSocket(String addr, int port, String bindAddr) {
        open(addr, port, bindAddr);
    }

    protected void finalize() {
        close();
    }

    public String getLocalAddress() {
        if (this.ssdpMultiGroup == null || this.ssdpMultiIf == null) {
            return "";
        }
        InetAddress mcastAddr = this.ssdpMultiGroup.getAddress();
        Enumeration<InetAddress> inetAddresses = this.ssdpMultiIf.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
            InetAddress addr = inetAddresses.nextElement();
            if ((mcastAddr instanceof Inet6Address) && (addr instanceof Inet6Address)) {
                return addr.getHostAddress();
            }
            if ((mcastAddr instanceof Inet4Address) && (addr instanceof Inet4Address)) {
                return addr.getHostAddress();
            }
        }
        return "";
    }

    public int getMulticastPort() {
        return this.ssdpMultiGroup.getPort();
    }

    public int getLocalPort() {
        return this.ssdpMultiSock.getLocalPort();
    }

    public MulticastSocket getSocket() {
        return this.ssdpMultiSock;
    }

    public InetAddress getMulticastInetAddress() {
        return this.ssdpMultiGroup.getAddress();
    }

    public String getMulticastAddress() {
        return getMulticastInetAddress().getHostAddress();
    }

    public boolean open(String addr, int port, InetAddress bindAddr) {
        try {
            this.ssdpMultiSock = new MulticastSocket((SocketAddress) null);
            this.ssdpMultiSock.setReuseAddress(true);
            InetSocketAddress bindSockAddr = new InetSocketAddress(port);
            this.ssdpMultiSock.bind(bindSockAddr);
            this.ssdpMultiGroup = new InetSocketAddress(InetAddress.getByName(addr), port);
            this.ssdpMultiIf = NetworkInterface.getByInetAddress(bindAddr);
            this.ssdpMultiSock.joinGroup(this.ssdpMultiGroup, this.ssdpMultiIf);
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean open(String addr, int port, String bindAddr) {
        try {
            return open(addr, port, InetAddress.getByName(bindAddr));
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean close() {
        if (this.ssdpMultiSock == null) {
            return true;
        }
        try {
            this.ssdpMultiSock.leaveGroup(this.ssdpMultiGroup, this.ssdpMultiIf);
            this.ssdpMultiSock.close();
            this.ssdpMultiSock = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean send(String msg, String bindAddr, int bindPort) {
        MulticastSocket msock;
        try {
            if (bindAddr != null && bindPort > 0) {
                msock = new MulticastSocket((SocketAddress) null);
                msock.bind(new InetSocketAddress(bindAddr, bindPort));
            } else {
                msock = new MulticastSocket();
            }
            DatagramPacket dgmPacket = new DatagramPacket(msg.getBytes(), msg.length(), this.ssdpMultiGroup);
            msock.setTimeToLive(UPnP.getTimeToLive());
            msock.send(dgmPacket);
            msock.close();
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean send(String msg) {
        return send(msg, null, -1);
    }

    public boolean post(HTTPRequest req, String bindAddr, int bindPort) {
        return send(req.toString(), bindAddr, bindPort);
    }

    public boolean post(HTTPRequest req) {
        return send(req.toString(), null, -1);
    }

    public SSDPPacket receive() throws IOException {
        byte[] ssdvRecvBuf = new byte[1024];
        SSDPPacket recvPacket = new SSDPPacket(ssdvRecvBuf, ssdvRecvBuf.length);
        recvPacket.setLocalAddress(getLocalAddress());
        if (this.ssdpMultiSock == null) {
            return null;
        }
        this.ssdpMultiSock.receive(recvPacket.getDatagramPacket());
        recvPacket.setTimeStamp(System.currentTimeMillis());
        return recvPacket;
    }
}

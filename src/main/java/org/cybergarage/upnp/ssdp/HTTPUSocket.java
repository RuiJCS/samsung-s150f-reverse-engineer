package org.cybergarage.upnp.ssdp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class HTTPUSocket {
    private DatagramSocket ssdpUniSock = null;
    private String localAddr = "";

    public DatagramSocket getDatagramSocket() {
        return this.ssdpUniSock;
    }

    public HTTPUSocket() {
        open();
    }

    public HTTPUSocket(String bindAddr, int bindPort) {
        open(bindAddr, bindPort);
    }

    public HTTPUSocket(int bindPort) {
        open(bindPort);
    }

    protected void finalize() {
        close();
    }

    public void setLocalAddress(String addr) {
        this.localAddr = addr;
    }

    public DatagramSocket getUDPSocket() {
        return this.ssdpUniSock;
    }

    public String getLocalAddress() {
        return this.localAddr.length() > 0 ? this.localAddr : this.ssdpUniSock.getLocalAddress().getHostAddress();
    }

    public boolean open() {
        close();
        try {
            this.ssdpUniSock = new DatagramSocket();
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean open(String bindAddr, int bindPort) {
        close();
        try {
            InetSocketAddress bindInetAddr = new InetSocketAddress(bindPort);
            this.ssdpUniSock = new DatagramSocket(bindInetAddr);
            this.ssdpUniSock.setReuseAddress(true);
            setLocalAddress(bindAddr);
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean open(int bindPort) {
        close();
        try {
            InetSocketAddress bindSock = new InetSocketAddress(bindPort);
            this.ssdpUniSock = new DatagramSocket((SocketAddress) null);
            this.ssdpUniSock.setReuseAddress(true);
            this.ssdpUniSock.bind(bindSock);
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean close() {
        if (this.ssdpUniSock == null) {
            return true;
        }
        try {
            this.ssdpUniSock.close();
            this.ssdpUniSock = null;
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean post(String addr, int port, String msg) {
        try {
            InetAddress inetAddr = InetAddress.getByName(addr);
            DatagramPacket dgmPacket = new DatagramPacket(msg.getBytes(), msg.length(), inetAddr, port);
            this.ssdpUniSock.send(dgmPacket);
            this.ssdpUniSock.send(dgmPacket);
            this.ssdpUniSock.send(dgmPacket);
            return true;
        } catch (Exception e) {
            Debug.warning("addr = " + this.ssdpUniSock.getLocalAddress().getHostName());
            Debug.warning("port = " + this.ssdpUniSock.getLocalPort());
            Debug.warning(e);
            return false;
        }
    }

    public SSDPPacket receive() {
        byte[] ssdvRecvBuf = new byte[1024];
        SSDPPacket recvPacket = new SSDPPacket(ssdvRecvBuf, ssdvRecvBuf.length);
        recvPacket.setLocalAddress(getLocalAddress());
        try {
            this.ssdpUniSock.receive(recvPacket.getDatagramPacket());
            recvPacket.setTimeStamp(System.currentTimeMillis());
            return recvPacket;
        } catch (Exception e) {
            Debug.warning(e);
            return null;
        }
    }
}

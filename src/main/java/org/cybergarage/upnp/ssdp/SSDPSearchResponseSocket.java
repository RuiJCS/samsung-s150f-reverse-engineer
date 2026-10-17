package org.cybergarage.upnp.ssdp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.SocketAddressException;

/* JADX INFO: loaded from: classes.dex */
public class SSDPSearchResponseSocket extends HTTPUSocket implements Runnable {
    private ControlPoint controlPoint;
    private Thread deviceSearchResponseThread;

    public SSDPSearchResponseSocket() {
        this.controlPoint = null;
        this.deviceSearchResponseThread = null;
        setControlPoint(null);
    }

    public SSDPSearchResponseSocket(String bindAddr, int port) {
        super(bindAddr, port);
        this.controlPoint = null;
        this.deviceSearchResponseThread = null;
        setControlPoint(null);
    }

    public void setControlPoint(ControlPoint ctrlp) {
        this.controlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    @Override // java.lang.Runnable
    public void run() {
        Thread thisThread = Thread.currentThread();
        ControlPoint ctrlPoint = getControlPoint();
        while (this.deviceSearchResponseThread == thisThread) {
            Thread.yield();
            SSDPPacket packet = receive();
            if (packet != null) {
                if (ctrlPoint != null) {
                    ctrlPoint.searchResponseReceived(packet);
                }
            } else {
                return;
            }
        }
    }

    public void start() throws SocketAddressException {
        StringBuffer name = new StringBuffer("Cyber.SSDPSearchResponseSocket/");
        DatagramSocket s = getDatagramSocket();
        if (s != null) {
            InetAddress localAddr = s.getLocalAddress();
            if (localAddr != null) {
                name.append(s.getLocalAddress()).append(':');
                name.append(s.getLocalPort());
            }
            this.deviceSearchResponseThread = new Thread(this, name.toString());
            this.deviceSearchResponseThread.start();
            return;
        }
        throw new SocketAddressException("");
    }

    public void stop() {
        this.deviceSearchResponseThread = null;
    }

    public boolean post(String addr, int port, SSDPSearchResponse res) {
        return post(addr, port, res.getHeader());
    }

    public boolean post(String addr, int port, SSDPSearchRequest req) {
        return post(addr, port, req.toString());
    }
}

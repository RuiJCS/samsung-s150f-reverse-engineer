package org.cybergarage.upnp.ssdp;

import java.io.IOException;
import java.net.InetAddress;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class SSDPNotifySocket extends HTTPMUSocket implements Runnable {
    private ControlPoint controlPoint = null;
    private Thread deviceNotifyThread = null;
    private boolean useIPv6Address;

    public SSDPNotifySocket(String bindAddr) {
        String addr = SSDP.ADDRESS;
        this.useIPv6Address = false;
        if (HostInterface.isIPv6Address(bindAddr)) {
            addr = SSDP.getIPv6Address();
            this.useIPv6Address = true;
        }
        open(addr, SSDP.PORT, bindAddr);
        setControlPoint(null);
    }

    public void setControlPoint(ControlPoint ctrlp) {
        this.controlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    public boolean post(SSDPNotifyRequest req) {
        String ssdpAddr = SSDP.ADDRESS;
        if (this.useIPv6Address) {
            ssdpAddr = SSDP.getIPv6Address();
        }
        req.setHost(ssdpAddr, SSDP.PORT);
        return post((HTTPRequest) req);
    }

    @Override // java.lang.Runnable
    public void run() {
        Thread thisThread = Thread.currentThread();
        ControlPoint ctrlPoint = getControlPoint();
        while (this.deviceNotifyThread == thisThread) {
            Thread.yield();
            try {
                SSDPPacket packet = receive();
                if (packet != null) {
                    InetAddress maddr = getMulticastInetAddress();
                    InetAddress pmaddr = packet.getHostInetAddress();
                    if (!maddr.equals(pmaddr)) {
                        Debug.warning("Invalidate Multicast Recieved from IP " + maddr + " on " + pmaddr);
                    } else if (ctrlPoint != null) {
                        ctrlPoint.notifyReceived(packet);
                    }
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    public void start() {
        StringBuffer name = new StringBuffer("Cyber.SSDPNotifySocket/");
        String localAddr = getLocalAddress();
        if (localAddr != null && localAddr.length() > 0) {
            name.append(getLocalAddress()).append(':');
            name.append(getLocalPort()).append(" -> ");
            name.append(getMulticastAddress()).append(':');
            name.append(getMulticastPort());
        }
        this.deviceNotifyThread = new Thread(this, name.toString());
        this.deviceNotifyThread.start();
    }

    public void stop() {
        close();
        this.deviceNotifyThread = null;
    }
}

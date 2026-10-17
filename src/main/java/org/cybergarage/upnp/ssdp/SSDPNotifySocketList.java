package org.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;

/* JADX INFO: loaded from: classes.dex */
public class SSDPNotifySocketList extends Vector {
    private InetAddress[] binds;

    public SSDPNotifySocketList() {
        this.binds = null;
    }

    public SSDPNotifySocketList(InetAddress[] binds) {
        this.binds = null;
        this.binds = binds;
    }

    public SSDPNotifySocket getSSDPNotifySocket(int n) {
        return (SSDPNotifySocket) get(n);
    }

    public void setControlPoint(ControlPoint ctrlPoint) {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPNotifySocket sock = getSSDPNotifySocket(n);
            sock.setControlPoint(ctrlPoint);
        }
    }

    public boolean open() {
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
        for (int i2 = 0; i2 < bindAddresses.length; i2++) {
            if (bindAddresses[i2] != null) {
                SSDPNotifySocket ssdpNotifySocket = new SSDPNotifySocket(bindAddresses[i2]);
                add(ssdpNotifySocket);
            }
        }
        return true;
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPNotifySocket sock = getSSDPNotifySocket(n);
            sock.close();
        }
        clear();
    }

    public void start() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPNotifySocket sock = getSSDPNotifySocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPNotifySocket sock = getSSDPNotifySocket(n);
            sock.stop();
        }
    }
}

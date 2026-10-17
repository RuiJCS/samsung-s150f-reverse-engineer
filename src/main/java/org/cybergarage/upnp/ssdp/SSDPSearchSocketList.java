package org.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.device.SearchListener;

/* JADX INFO: loaded from: classes.dex */
public class SSDPSearchSocketList extends Vector {
    private InetAddress[] binds;
    private String multicastIPv4;
    private String multicastIPv6;
    private int port;

    public SSDPSearchSocketList() {
        this.binds = null;
        this.multicastIPv4 = SSDP.ADDRESS;
        this.multicastIPv6 = SSDP.getIPv6Address();
        this.port = SSDP.PORT;
    }

    public SSDPSearchSocketList(InetAddress[] binds) {
        this.binds = null;
        this.multicastIPv4 = SSDP.ADDRESS;
        this.multicastIPv6 = SSDP.getIPv6Address();
        this.port = SSDP.PORT;
        this.binds = binds;
    }

    public SSDPSearchSocketList(InetAddress[] binds, int port, String multicastIPv4, String multicastIPv6) {
        this.binds = null;
        this.multicastIPv4 = SSDP.ADDRESS;
        this.multicastIPv6 = SSDP.getIPv6Address();
        this.port = SSDP.PORT;
        this.binds = binds;
        this.port = port;
        this.multicastIPv4 = multicastIPv4;
        this.multicastIPv6 = multicastIPv6;
    }

    public SSDPSearchSocket getSSDPSearchSocket(int n) {
        return (SSDPSearchSocket) get(n);
    }

    public void addSearchListener(SearchListener listener) {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.addSearchListener(listener);
        }
    }

    public boolean open() {
        String[] bindAddresses;
        SSDPSearchSocket ssdpSearchSocket;
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
                if (HostInterface.isIPv6Address(bindAddresses[i2])) {
                    ssdpSearchSocket = new SSDPSearchSocket(bindAddresses[i2], this.port, this.multicastIPv6);
                } else {
                    ssdpSearchSocket = new SSDPSearchSocket(bindAddresses[i2], this.port, this.multicastIPv4);
                }
                add(ssdpSearchSocket);
            }
        }
        return true;
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.close();
        }
        clear();
    }

    public void start() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.stop();
        }
    }
}

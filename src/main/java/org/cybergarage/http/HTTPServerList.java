package org.cybergarage.http;

import java.net.InetAddress;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.Device;

/* JADX INFO: loaded from: classes.dex */
public class HTTPServerList extends Vector {
    private InetAddress[] binds;
    private int port;

    public HTTPServerList() {
        this.binds = null;
        this.port = Device.HTTP_DEFAULT_PORT;
    }

    public HTTPServerList(InetAddress[] list, int port) {
        this.binds = null;
        this.port = Device.HTTP_DEFAULT_PORT;
        this.binds = list;
        this.port = port;
    }

    public void addRequestListener(HTTPRequestListener listener) {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.addRequestListener(listener);
        }
    }

    public void removeRequestListener(HTTPRequestListener listener) {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.removeRequestListener(listener);
        }
    }

    public HTTPServer getHTTPServer(int n) {
        return (HTTPServer) get(n);
    }

    public void close() {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.close();
        }
    }

    public int open() {
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
        for (int i2 = 0; i2 < bindAddresses.length; i2++) {
            HTTPServer httpServer = new HTTPServer();
            if (bindAddresses[i2] == null || !httpServer.open(bindAddresses[i2], this.port)) {
                close();
                clear();
            } else {
                add(httpServer);
                j++;
            }
        }
        return j;
    }

    public boolean open(int port) {
        this.port = port;
        return open() != 0;
    }

    public void start() {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.start();
        }
    }

    public void stop() {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.stop();
        }
    }
}

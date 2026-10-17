package org.cybergarage.http;

import java.net.Socket;

/* JADX INFO: loaded from: classes.dex */
public class HTTPServerThread extends Thread {
    private HTTPServer httpServer;
    private Socket sock;

    public HTTPServerThread(HTTPServer httpServer, Socket sock) {
        super("Cyber.HTTPServerThread");
        this.httpServer = httpServer;
        this.sock = sock;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        HTTPSocket httpSock = new HTTPSocket(this.sock);
        if (httpSock.open()) {
            HTTPRequest httpReq = new HTTPRequest();
            httpReq.setSocket(httpSock);
            while (httpReq.read()) {
                this.httpServer.performRequestListener(httpReq);
                if (!httpReq.isKeepAlive()) {
                    break;
                }
            }
            httpSock.close();
        }
    }
}

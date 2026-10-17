package com.samsungimaging.filesharing;

import com.samsungimaging.filesharing.util.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class HumbleHttpClient {
    public Map<String, String> headers = null;
    public int contentLength = -1;
    public String contentType = null;
    protected Socket sock = null;
    protected InputStream is = null;
    protected BufferedReader br = null;
    protected PrintWriter pw = null;

    public InputStream request(String strUrl) {
        int ii = 0;
        InputStream responseStream = null;
        try {
            URL url = new URL(strUrl);
            try {
                this.sock = new Socket(url.getHost(), url.getPort());
                this.pw = new PrintWriter(this.sock.getOutputStream(), false);
                this.is = this.sock.getInputStream();
                Logger.GG("\nGET " + url.getPath() + " HTTP/1.0\r\n\r\n\r\n");
                this.pw.print("GET " + url.getPath() + " HTTP/1.0\r\n\r\n\r\n\r\n");
                this.pw.flush();
                this.headers = Collections.synchronizedMap(new HashMap());
                this.br = new BufferedReader(new InputStreamReader(this.is));
                while (true) {
                    String line = this.br.readLine();
                    if (line == null || line.length() <= 0) {
                        break;
                    }
                    Logger.GG(line);
                    if (ii > 0) {
                        String[] nv = line.split(SOAP.DELIM, 2);
                        if (nv.length == 2) {
                            String attrName = nv[0].trim();
                            String attrVal = nv[1].trim();
                            this.headers.put(attrName, attrVal);
                            if (attrName.equalsIgnoreCase(HTTP.CONTENT_TYPE)) {
                                this.contentType = attrVal;
                            } else if (attrName.equalsIgnoreCase(HTTP.CONTENT_LENGTH)) {
                                try {
                                    this.contentLength = Integer.parseInt(attrVal);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    ii++;
                }
                Logger.GG("Returning remained data...");
                responseStream = this.sock.getInputStream();
                return responseStream;
            } catch (MalformedURLException e2) {
                e = e2;
                e.printStackTrace();
                close();
                return responseStream;
            } catch (IOException e3) {
                e = e3;
                e.printStackTrace();
                close();
                return responseStream;
            }
        } catch (MalformedURLException e4) {
            e = e4;
        } catch (IOException e5) {
            e = e5;
        }
    }

    private void close() {
        if (this.is != null) {
            try {
                this.is.close();
            } catch (Throwable th) {
            }
        }
        if (this.pw != null) {
            try {
                this.pw.close();
            } catch (Throwable th2) {
            }
        }
        if (this.br != null) {
            try {
                this.br.close();
            } catch (Throwable th3) {
            }
        }
        if (this.sock != null) {
            try {
                this.sock.close();
            } catch (Throwable th4) {
            }
        }
        this.is = null;
        this.pw = null;
        this.br = null;
        this.sock = null;
        this.contentLength = -1;
        this.contentType = null;
    }
}

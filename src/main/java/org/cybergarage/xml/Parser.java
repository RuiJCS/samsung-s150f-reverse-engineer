package org.cybergarage.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.upnp.UPnPStatus;

/* JADX INFO: loaded from: classes.dex */
public abstract class Parser {
    public abstract Node parse(InputStream inputStream) throws ParserException;

    public Node parse(URL locationURL, String userAgent, String accessMethod) throws ParserException {
        String host = locationURL.getHost();
        int port = locationURL.getPort();
        String uri = locationURL.getPath();
        try {
            HTTPRequest httpReq = new HTTPRequest();
            httpReq.setMethod(HTTP.GET);
            httpReq.setURI(uri);
            if (userAgent != null) {
                httpReq.addHeader(HTTP.USER_AGENT, userAgent);
            }
            if (accessMethod != null) {
                httpReq.addHeader(HTTP.ACCESS_METHOD, accessMethod);
            }
            HTTPResponse httpRes = httpReq.post(host, port);
            if (accessMethod != null) {
                switch (httpRes.getStatusCode()) {
                    case UPnPStatus.INVALID_ACTION /* 401 */:
                        throw new ParserException("HTTP_UNAUTHORIZED");
                    case 500:
                        throw new ParserException("HTTP_INTERNAL_ERROR");
                    case UPnPStatus.ACTION_FAILED /* 501 */:
                        throw new ParserException("HTTP_NOT_IMPLEMENTED");
                    case 502:
                        throw new ParserException("HTTP_BAD_GATEWAY");
                    case 503:
                        throw new ParserException("HTTP_UNAVAILABLE");
                    case 504:
                        throw new ParserException("HTTP_GATEWAY_TIMEOUT");
                    case 505:
                        throw new ParserException("HTTP_VERSION");
                }
            }
            if (!httpRes.isSuccessful()) {
                throw new ParserException("HTTP comunication failed: no answer from peer.Unable to retrive resoure -> " + locationURL.toString());
            }
            String content = new String(httpRes.getContent());
            ByteArrayInputStream strBuf = new ByteArrayInputStream(content.getBytes());
            Node rootElem = parse(strBuf);
            return rootElem;
        } catch (ParserException pe) {
            throw new ParserException(pe);
        } catch (Exception e) {
            try {
                HttpURLConnection urlCon = (HttpURLConnection) locationURL.openConnection();
                urlCon.setRequestMethod(HTTP.GET);
                urlCon.setRequestProperty(HTTP.CONTENT_LENGTH, "0");
                if (host != null) {
                    urlCon.setRequestProperty(HTTP.HOST, host);
                }
                if (userAgent != null) {
                    urlCon.setRequestProperty(HTTP.USER_AGENT, userAgent);
                }
                InputStream urlIn = urlCon.getInputStream();
                Node rootElem2 = parse(urlIn);
                urlIn.close();
                urlCon.disconnect();
                return rootElem2;
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public Node parse(File descriptionFile) throws ParserException {
        try {
            InputStream fileIn = new FileInputStream(descriptionFile);
            Node root = parse(fileIn);
            fileIn.close();
            return root;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public Node parse(String descr) throws ParserException {
        try {
            InputStream decrIn = new ByteArrayInputStream(descr.getBytes());
            Node root = parse(decrIn);
            return root;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}

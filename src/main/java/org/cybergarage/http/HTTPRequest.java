package org.cybergarage.http;

import com.samsungimaging.connectionmanager.util.Trace;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.StringTokenizer;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class HTTPRequest extends HTTPPacket {
    private Trace.Tag TAG;
    private HTTPSocket httpSocket;
    private String method;
    private Socket postSocket;
    private String requestHost;
    private int requestPort;
    private String uri;

    public HTTPRequest() {
        this.TAG = Trace.Tag.CYBERGARAGE;
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
        this.postSocket = null;
        setVersion("1.0");
    }

    public HTTPRequest(InputStream in) {
        super(in);
        this.TAG = Trace.Tag.CYBERGARAGE;
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
        this.postSocket = null;
    }

    public HTTPRequest(HTTPSocket httpSock) {
        this(httpSock.getInputStream());
        setSocket(httpSock);
    }

    public void setMethod(String value) {
        this.method = value;
    }

    public String getMethod() {
        return this.method != null ? this.method : getFirstLineToken(0);
    }

    public boolean isMethod(String method) {
        String headerMethod = getMethod();
        if (headerMethod == null) {
            return false;
        }
        return headerMethod.equalsIgnoreCase(method);
    }

    public boolean isGetRequest() {
        return isMethod(HTTP.GET);
    }

    public boolean isPostRequest() {
        return isMethod(HTTP.POST);
    }

    public boolean isHeadRequest() {
        return isMethod(HTTP.HEAD);
    }

    public boolean isSubscribeRequest() {
        return isMethod("SUBSCRIBE");
    }

    public boolean isUnsubscribeRequest() {
        return isMethod("UNSUBSCRIBE");
    }

    public boolean isNotifyRequest() {
        return isMethod(HTTP.NOTIFY);
    }

    public void setURI(String value, boolean isCheckRelativeURL) {
        this.uri = value;
        if (isCheckRelativeURL) {
            this.uri = HTTP.toRelativeURL(this.uri);
        }
    }

    public void setURI(String value) {
        setURI(value, false);
    }

    public String getURI() {
        return this.uri != null ? this.uri : getFirstLineToken(1);
    }

    public ParameterList getParameterList() {
        int paramIdx;
        ParameterList paramList = new ParameterList();
        String uri = getURI();
        if (uri != null && (paramIdx = uri.indexOf(63)) >= 0) {
            while (paramIdx > 0) {
                int eqIdx = uri.indexOf(61, paramIdx + 1);
                String name = uri.substring(paramIdx + 1, eqIdx);
                int nextParamIdx = uri.indexOf(38, eqIdx + 1);
                String value = uri.substring(eqIdx + 1, nextParamIdx > 0 ? nextParamIdx : uri.length());
                Parameter param = new Parameter(name, value);
                paramList.add(param);
                paramIdx = nextParamIdx;
            }
        }
        return paramList;
    }

    public String getParameterValue(String name) {
        ParameterList paramList = getParameterList();
        return paramList.getValue(name);
    }

    public boolean isSOAPAction() {
        return hasHeader(HTTP.SOAP_ACTION);
    }

    public void setRequestHost(String host) {
        this.requestHost = host;
    }

    public String getRequestHost() {
        return this.requestHost;
    }

    public void setRequestPort(int host) {
        this.requestPort = host;
    }

    public int getRequestPort() {
        return this.requestPort;
    }

    public void setSocket(HTTPSocket value) {
        this.httpSocket = value;
    }

    public HTTPSocket getSocket() {
        return this.httpSocket;
    }

    public String getLocalAddress() {
        return getSocket().getLocalAddress();
    }

    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    public boolean parseRequestLine(String lineStr) {
        StringTokenizer st = new StringTokenizer(lineStr, " ");
        if (!st.hasMoreTokens()) {
            return false;
        }
        setMethod(st.nextToken());
        if (!st.hasMoreTokens()) {
            return false;
        }
        setURI(st.nextToken());
        if (!st.hasMoreTokens()) {
            return false;
        }
        setVersion(st.nextToken());
        return true;
    }

    public String getHTTPVersion() {
        return hasFirstLine() ? getFirstLineToken(2) : "HTTP/" + super.getVersion();
    }

    public String getFirstLineString() {
        String ret = null;
        try {
            URL url = new URL(getURI());
            if (url != null) {
                try {
                    ret = String.valueOf(getMethod()) + " " + url.getPath() + " " + getHTTPVersion() + HTTP.CRLF;
                } catch (MalformedURLException e) {
                    e = e;
                    e.printStackTrace();
                    return String.valueOf(getMethod()) + " " + getURI() + " " + getHTTPVersion() + HTTP.CRLF;
                }
            }
            return ret;
        } catch (MalformedURLException e2) {
            e = e2;
        }
    }

    public String getHeader() {
        StringBuffer str = new StringBuffer();
        str.append(getFirstLineString());
        String headerString = getHeaderString();
        str.append(headerString);
        return str.toString();
    }

    public boolean isKeepAlive() {
        if (isCloseConnection()) {
            return false;
        }
        if (isKeepAliveConnection()) {
            return true;
        }
        String httpVer = getHTTPVersion();
        boolean isHTTP10 = httpVer.indexOf("1.0") > 0;
        return !isHTTP10;
    }

    public boolean read() {
        return super.read(getSocket());
    }

    public boolean post(HTTPResponse httpRes) {
        HTTPSocket httpSock = getSocket();
        long offset = 0;
        long length = httpRes.getContentLength();
        if (hasContentRange()) {
            long firstPos = getContentRangeFirstPosition();
            long lastPos = getContentRangeLastPosition();
            if (lastPos <= 0) {
                lastPos = length - 1;
            }
            if (firstPos > length || lastPos > length) {
                return returnResponse(HTTPStatus.INVALID_RANGE);
            }
            httpRes.setContentRange(firstPos, lastPos, length);
            httpRes.setStatusCode(HTTPStatus.PARTIAL_CONTENT);
            offset = firstPos;
            length = (lastPos - firstPos) + 1;
        }
        return httpSock.post(httpRes, offset, length, isHeadRequest());
    }

    public HTTPResponse post(String host, int port, boolean isKeepAlive, int timeOut) {
        Trace.d(this.TAG, "start post() host : " + host + " port : " + port);
        Trace.d(this.TAG, "Res 1");
        HTTPResponse httpRes = new HTTPResponse();
        Trace.d(this.TAG, "Res 2");
        setHost(host);
        Trace.d(this.TAG, "Res 3");
        setConnection(isKeepAlive ? HTTP.KEEP_ALIVE : HTTP.CLOSE);
        Trace.d(this.TAG, "Res 4");
        boolean isHeaderRequest = isHeadRequest();
        Trace.d(this.TAG, "Res 5");
        OutputStream out = null;
        InputStream in = null;
        try {
            Trace.d(this.TAG, "Try 1");
            if (this.postSocket == null) {
                this.postSocket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(host, port);
                this.postSocket.connect(socketAddress, timeOut);
            }
            Trace.d(this.TAG, "Try 2");
            out = this.postSocket.getOutputStream();
            Trace.d(this.TAG, "Try 3");
            PrintStream pout = new PrintStream(out);
            pout.print(getHeader());
            Trace.d(this.TAG, "Try 4");
            pout.print(HTTP.CRLF);
            boolean isChunkedRequest = isChunked();
            Trace.d(this.TAG, "Try 5");
            String content = getContentString();
            Trace.d(this.TAG, "Try 6");
            int contentLength = content != null ? content.length() : 0;
            Trace.d(this.TAG, "Try 7");
            if (contentLength > 0) {
                if (isChunkedRequest) {
                    String chunSizeBuf = Long.toHexString(contentLength);
                    pout.print(chunSizeBuf);
                    pout.print(HTTP.CRLF);
                }
                pout.print(content);
                if (isChunkedRequest) {
                    pout.print(HTTP.CRLF);
                }
            }
            if (isChunkedRequest) {
                pout.print("0");
                pout.print(HTTP.CRLF);
            }
            pout.flush();
            Trace.d(this.TAG, "Try 8");
            in = this.postSocket.getInputStream();
            Trace.d(this.TAG, "Try 9");
            httpRes.set(in, isHeaderRequest);
            Trace.d(this.TAG, "Try 10");
        } catch (SocketException e) {
            Trace.d(this.TAG, "Res SocketException");
            httpRes.setStatusCode(500);
            Debug.warning(e);
        } catch (IOException e2) {
            Trace.d(this.TAG, "Res IOException");
            httpRes.setStatusCode(500);
            Debug.warning(e2);
        } catch (Exception e3) {
            Trace.d(this.TAG, "Res Exception");
            Debug.warning(e3);
        } finally {
            if (!isKeepAlive) {
                try {
                    in.close();
                } catch (Exception e4) {
                }
                if (in != null) {
                    try {
                        out.close();
                    } catch (Exception e5) {
                    }
                }
                if (out != null) {
                    try {
                        this.postSocket.close();
                    } catch (Exception e6) {
                    }
                }
                this.postSocket = null;
            }
        }
        return httpRes;
    }

    public HTTPResponse post(String host, int port) {
        return post(host, port, false, HTTPServer.DEFAULT_TIMEOUT);
    }

    public HTTPResponse post(String host, int port, int timeOut) {
        return post(host, port, false, timeOut);
    }

    public void set(HTTPRequest httpReq) {
        set((HTTPPacket) httpReq);
        setSocket(httpReq.getSocket());
    }

    public boolean returnResponse(int statusCode) {
        HTTPResponse httpRes = new HTTPResponse();
        httpRes.setStatusCode(statusCode);
        httpRes.setContentLength(0L);
        return post(httpRes);
    }

    public boolean returnOK() {
        return returnResponse(200);
    }

    public boolean returnBadRequest() {
        return returnResponse(HTTPStatus.BAD_REQUEST);
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getHeader());
        str.append(HTTP.CRLF);
        str.append(getContentString());
        return str.toString();
    }

    public void print() {
        System.out.println(toString());
    }
}

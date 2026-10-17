package org.cybergarage.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class HTTPSocket {
    private Socket socket = null;
    private InputStream sockIn = null;
    private OutputStream sockOut = null;

    public HTTPSocket(Socket socket) {
        setSocket(socket);
        open();
    }

    public HTTPSocket(HTTPSocket socket) {
        setSocket(socket.getSocket());
        setInputStream(socket.getInputStream());
        setOutputStream(socket.getOutputStream());
    }

    public void finalize() {
        close();
    }

    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getLocalAddress() {
        return getSocket().getLocalAddress().getHostAddress();
    }

    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    private void setInputStream(InputStream in) {
        this.sockIn = in;
    }

    public InputStream getInputStream() {
        return this.sockIn;
    }

    private void setOutputStream(OutputStream out) {
        this.sockOut = out;
    }

    private OutputStream getOutputStream() {
        return this.sockOut;
    }

    public boolean open() {
        Socket sock = getSocket();
        try {
            this.sockIn = sock.getInputStream();
            this.sockOut = sock.getOutputStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean close() {
        try {
            if (this.sockIn != null) {
                this.sockIn.close();
            }
            if (this.sockOut != null) {
                this.sockOut.close();
            }
            getSocket().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean post(HTTPResponse httpRes, byte[] content, long contentOffset, long contentLength, boolean isOnlyHeader) {
        httpRes.setDate(Calendar.getInstance());
        OutputStream out = getOutputStream();
        try {
            httpRes.setContentLength(contentLength);
            out.write(httpRes.getHeader().getBytes());
            out.write(HTTP.CRLF.getBytes());
            if (isOnlyHeader) {
                out.flush();
                return true;
            }
            boolean isChunkedResponse = httpRes.isChunked();
            if (isChunkedResponse) {
                String chunSizeBuf = Long.toHexString(contentLength);
                out.write(chunSizeBuf.getBytes());
                out.write(HTTP.CRLF.getBytes());
            }
            out.write(content, (int) contentOffset, (int) contentLength);
            if (isChunkedResponse) {
                out.write(HTTP.CRLF.getBytes());
                out.write("0".getBytes());
                out.write(HTTP.CRLF.getBytes());
            }
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean post(HTTPResponse httpRes, InputStream in, long contentOffset, long contentLength, boolean isOnlyHeader) {
        httpRes.setDate(Calendar.getInstance());
        OutputStream out = getOutputStream();
        try {
            httpRes.setContentLength(contentLength);
            out.write(httpRes.getHeader().getBytes());
            out.write(HTTP.CRLF.getBytes());
            if (isOnlyHeader) {
                out.flush();
                return true;
            }
            boolean isChunkedResponse = httpRes.isChunked();
            if (0 < contentOffset) {
                in.skip(contentOffset);
            }
            int chunkSize = HTTP.getChunkSize();
            byte[] readBuf = new byte[chunkSize];
            long readCnt = 0;
            long readSize = ((long) chunkSize) < contentLength ? chunkSize : contentLength;
            int readLen = in.read(readBuf, 0, (int) readSize);
            while (readLen > 0 && readCnt < contentLength) {
                if (isChunkedResponse) {
                    String chunSizeBuf = Long.toHexString(readLen);
                    out.write(chunSizeBuf.getBytes());
                    out.write(HTTP.CRLF.getBytes());
                }
                out.write(readBuf, 0, readLen);
                if (isChunkedResponse) {
                    out.write(HTTP.CRLF.getBytes());
                }
                readCnt += (long) readLen;
                long readSize2 = ((long) chunkSize) < contentLength - readCnt ? chunkSize : contentLength - readCnt;
                readLen = in.read(readBuf, 0, (int) readSize2);
            }
            if (isChunkedResponse) {
                out.write("0".getBytes());
                out.write(HTTP.CRLF.getBytes());
            }
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean post(HTTPResponse httpRes, long contentOffset, long contentLength, boolean isOnlyHeader) {
        return httpRes.hasContentInputStream() ? post(httpRes, httpRes.getContentInputStream(), contentOffset, contentLength, isOnlyHeader) : post(httpRes, httpRes.getContent(), contentOffset, contentLength, isOnlyHeader);
    }
}

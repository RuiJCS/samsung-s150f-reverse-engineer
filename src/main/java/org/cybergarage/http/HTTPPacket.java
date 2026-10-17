package org.cybergarage.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.soap.SOAP;
import org.cybergarage.util.Debug;
import org.cybergarage.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class HTTPPacket {
    private byte[] content;
    private InputStream contentInput;
    private String firstLine;
    private Vector httpHeaderList;
    private String version;

    public HTTPPacket() {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.1");
        setContentInputStream(null);
    }

    public HTTPPacket(HTTPPacket httpPacket) {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.1");
        set(httpPacket);
        setContentInputStream(null);
    }

    public HTTPPacket(InputStream in) {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.1");
        set(in);
        setContentInputStream(null);
    }

    public void init() {
        setFirstLine("");
        clearHeaders();
        setContent(new byte[0], false);
        setContentInputStream(null);
    }

    public void setVersion(String ver) {
        this.version = ver;
    }

    public String getVersion() {
        return this.version;
    }

    private String readLine(BufferedInputStream in) {
        ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();
        byte[] readBuf = new byte[1];
        try {
            int readLen = in.read(readBuf);
            while (readLen > 0 && readBuf[0] != 10) {
                if (readBuf[0] != 13) {
                    lineBuf.write(readBuf[0]);
                }
                readLen = in.read(readBuf);
            }
        } catch (InterruptedIOException e) {
        } catch (IOException e2) {
            Debug.warning(e2);
        }
        return lineBuf.toString();
    }

    protected boolean set(InputStream in, boolean onlyHeaders) {
        try {
            BufferedInputStream reader = new BufferedInputStream(in);
            String firstLine = readLine(reader);
            if (firstLine == null || firstLine.length() <= 0) {
                return false;
            }
            setFirstLine(firstLine);
            HTTPStatus httpStatus = new HTTPStatus(firstLine);
            int statCode = httpStatus.getStatusCode();
            if (statCode == 100) {
                String headerLine = readLine(reader);
                while (headerLine != null && headerLine.length() > 0) {
                    HTTPHeader header = new HTTPHeader(headerLine);
                    if (header.hasName()) {
                        setHeader(header);
                    }
                    headerLine = readLine(reader);
                }
                String actualFirstLine = readLine(reader);
                if (actualFirstLine != null && actualFirstLine.length() > 0) {
                    setFirstLine(actualFirstLine);
                } else {
                    return true;
                }
            }
            String headerLine2 = readLine(reader);
            while (headerLine2 != null && headerLine2.length() > 0) {
                HTTPHeader header2 = new HTTPHeader(headerLine2);
                if (header2.hasName()) {
                    setHeader(header2);
                }
                headerLine2 = readLine(reader);
            }
            if (onlyHeaders) {
                setContent("", false);
                return true;
            }
            boolean isChunkedRequest = isChunked();
            long contentLen = 0;
            if (isChunkedRequest) {
                try {
                    String chunkSizeLine = readLine(reader);
                    contentLen = chunkSizeLine != null ? Long.parseLong(chunkSizeLine.trim(), 16) : 0L;
                } catch (Exception e) {
                }
            } else {
                contentLen = getContentLength();
            }
            ByteArrayOutputStream contentBuf = new ByteArrayOutputStream();
            while (0 < contentLen) {
                int chunkSize = HTTP.getChunkSize();
                byte[] readBuf = new byte[(int) (contentLen > ((long) chunkSize) ? chunkSize : contentLen)];
                long readCnt = 0;
                while (readCnt < contentLen) {
                    long bufReadLen = contentLen - readCnt;
                    if (chunkSize < bufReadLen) {
                        bufReadLen = chunkSize;
                    }
                    try {
                        int readLen = reader.read(readBuf, 0, (int) bufReadLen);
                        if (readLen < 0) {
                            break;
                        }
                        contentBuf.write(readBuf, 0, readLen);
                        readCnt += (long) readLen;
                    } catch (Exception e2) {
                        Debug.warning(e2);
                    }
                }
                if (isChunkedRequest) {
                    long skipLen = 0;
                    do {
                        long skipCnt = reader.skip(((long) HTTP.CRLF.length()) - skipLen);
                        if (skipCnt < 0) {
                            break;
                        }
                        skipLen += skipCnt;
                    } while (skipLen < HTTP.CRLF.length());
                    try {
                        String chunkSizeLine2 = readLine(reader);
                        contentLen = Long.parseLong(new String(chunkSizeLine2.getBytes(), 0, chunkSizeLine2.length() - 2), 16);
                    } catch (Exception e3) {
                        contentLen = 0;
                    }
                } else {
                    contentLen = 0;
                }
            }
            setContent(contentBuf.toByteArray(), false);
            return true;
        } catch (Exception e4) {
            Debug.warning(e4);
            return false;
        }
    }

    protected boolean set(InputStream in) {
        return set(in, false);
    }

    protected boolean set(HTTPSocket httpSock) {
        return set(httpSock.getInputStream());
    }

    protected void set(HTTPPacket httpPacket) {
        setFirstLine(httpPacket.getFirstLine());
        clearHeaders();
        int nHeaders = httpPacket.getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = httpPacket.getHeader(n);
            addHeader(header);
        }
        setContent(httpPacket.getContent());
    }

    public boolean read(HTTPSocket httpSock) {
        init();
        return set(httpSock);
    }

    private void setFirstLine(String value) {
        this.firstLine = value;
    }

    protected String getFirstLine() {
        return this.firstLine;
    }

    protected String getFirstLineToken(int num) {
        StringTokenizer st = new StringTokenizer(this.firstLine, " ");
        String lastToken = "";
        for (int n = 0; n <= num; n++) {
            if (!st.hasMoreTokens()) {
                return "";
            }
            lastToken = st.nextToken();
        }
        return lastToken;
    }

    public boolean hasFirstLine() {
        return this.firstLine.length() > 0;
    }

    public int getNHeaders() {
        return this.httpHeaderList.size();
    }

    public void addHeader(HTTPHeader header) {
        this.httpHeaderList.add(header);
    }

    public void addHeader(String name, String value) {
        HTTPHeader header = new HTTPHeader(name, value);
        this.httpHeaderList.add(header);
    }

    public HTTPHeader getHeader(int n) {
        return (HTTPHeader) this.httpHeaderList.get(n);
    }

    public HTTPHeader getHeader(String name) {
        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            String headerName = header.getName();
            if (headerName.equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public void clearHeaders() {
        this.httpHeaderList.clear();
        this.httpHeaderList = new Vector();
    }

    public boolean hasHeader(String name) {
        return getHeader(name) != null;
    }

    public void setHeader(String name, String value) {
        HTTPHeader header = getHeader(name);
        if (header != null) {
            header.setValue(value);
        } else {
            addHeader(name, value);
        }
    }

    public void setHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public void setHeader(HTTPHeader header) {
        setHeader(header.getName(), header.getValue());
    }

    public String getHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        return header == null ? "" : header.getValue();
    }

    public void setStringHeader(String name, String value, String startWidth, String endWidth) {
        String headerValue = value;
        if (!headerValue.startsWith(startWidth)) {
            headerValue = String.valueOf(startWidth) + headerValue;
        }
        if (!headerValue.endsWith(endWidth)) {
            headerValue = String.valueOf(headerValue) + endWidth;
        }
        setHeader(name, headerValue);
    }

    public void setStringHeader(String name, String value) {
        setStringHeader(name, value, "\"", "\"");
    }

    public String getStringHeaderValue(String name, String startWidth, String endWidth) {
        String headerValue = getHeaderValue(name);
        if (headerValue.startsWith(startWidth)) {
            headerValue = headerValue.substring(1, headerValue.length());
        }
        if (headerValue.endsWith(endWidth)) {
            return headerValue.substring(0, headerValue.length() - 1);
        }
        return headerValue;
    }

    public String getStringHeaderValue(String name) {
        return getStringHeaderValue(name, "\"", "\"");
    }

    public void setIntegerHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setLongHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public int getIntegerHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null) {
            return 0;
        }
        return StringUtil.toInteger(header.getValue());
    }

    public long getLongHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null) {
            return 0L;
        }
        return StringUtil.toLong(header.getValue());
    }

    public String getHeaderString() {
        StringBuffer str = new StringBuffer();
        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            str.append(String.valueOf(header.getName()) + ": " + header.getValue() + HTTP.CRLF);
        }
        return str.toString();
    }

    public void setContent(byte[] data, boolean updateWithContentLength) {
        this.content = data;
        if (updateWithContentLength) {
            setContentLength(data.length);
        }
    }

    public void setContent(byte[] data) {
        setContent(data, true);
    }

    public void setContent(String data, boolean updateWithContentLength) {
        setContent(data.getBytes(), updateWithContentLength);
    }

    public void setContent(String data) {
        setContent(data, true);
    }

    public byte[] getContent() {
        return this.content;
    }

    public String getContentString() {
        String charSet = getCharSet();
        if (charSet == null || charSet.length() <= 0) {
            return new String(this.content);
        }
        try {
            return new String(this.content, charSet);
        } catch (Exception e) {
            Debug.warning(e);
            return new String(this.content);
        }
    }

    public boolean hasContent() {
        return this.content.length > 0;
    }

    public void setContentInputStream(InputStream in) {
        this.contentInput = in;
    }

    public InputStream getContentInputStream() {
        return this.contentInput;
    }

    public boolean hasContentInputStream() {
        return this.contentInput != null;
    }

    public void setContentType(String type) {
        setHeader(HTTP.CONTENT_TYPE, type);
    }

    public String getContentType() {
        return getHeaderValue(HTTP.CONTENT_TYPE);
    }

    public String getCharSet() {
        String contentType;
        int charSetIdx;
        String contentType2 = getContentType();
        if (contentType2 == null || (charSetIdx = (contentType = contentType2.toLowerCase()).indexOf(HTTP.CHARSET)) < 0) {
            return "";
        }
        int charSetEndIdx = HTTP.CHARSET.length() + charSetIdx + 1;
        String charSet = new String(contentType.getBytes(), charSetEndIdx, contentType.length() - charSetEndIdx);
        if (charSet.length() < 0) {
            return "";
        }
        if (charSet.charAt(0) == '\"') {
            charSet = charSet.substring(1, charSet.length() - 1);
        }
        if (charSet.length() < 0) {
            return "";
        }
        if (charSet.charAt(charSet.length() - 1) == '\"') {
            return charSet.substring(0, charSet.length() - 1);
        }
        return charSet;
    }

    public void setContentLength(long len) {
        setLongHeader(HTTP.CONTENT_LENGTH, len);
    }

    public long getContentLength() {
        return getLongHeaderValue(HTTP.CONTENT_LENGTH);
    }

    public boolean hasConnection() {
        return hasHeader(HTTP.CONNECTION);
    }

    public void setConnection(String value) {
        setHeader(HTTP.CONNECTION, value);
    }

    public String getConnection() {
        return getHeaderValue(HTTP.CONNECTION);
    }

    public boolean isCloseConnection() {
        String connection;
        if (hasConnection() && (connection = getConnection()) != null) {
            return connection.equalsIgnoreCase(HTTP.CLOSE);
        }
        return false;
    }

    public boolean isKeepAliveConnection() {
        String connection;
        if (hasConnection() && (connection = getConnection()) != null) {
            return connection.equalsIgnoreCase(HTTP.KEEP_ALIVE);
        }
        return false;
    }

    public boolean hasContentRange() {
        return hasHeader(HTTP.CONTENT_RANGE) || hasHeader(HTTP.RANGE);
    }

    public void setContentRange(long firstPos, long lastPos, long length) {
        String rangeStr = String.valueOf("") + "bytes ";
        setHeader(HTTP.CONTENT_RANGE, String.valueOf(String.valueOf(String.valueOf(rangeStr) + Long.toString(firstPos) + "-") + Long.toString(lastPos) + "/") + (0 < length ? Long.toString(length) : "*"));
    }

    public long[] getContentRange() {
        long[] range = {0, 0, 0};
        if (hasContentRange()) {
            String rangeLine = getHeaderValue(HTTP.CONTENT_RANGE);
            if (rangeLine.length() <= 0) {
                rangeLine = getHeaderValue(HTTP.RANGE);
            }
            if (rangeLine.length() > 0) {
                StringTokenizer strToken = new StringTokenizer(rangeLine, " =");
                if (strToken.hasMoreTokens()) {
                    strToken.nextToken(" ");
                    if (strToken.hasMoreTokens()) {
                        String firstPosStr = strToken.nextToken(" -");
                        try {
                            range[0] = Long.parseLong(firstPosStr);
                        } catch (NumberFormatException e) {
                        }
                        if (strToken.hasMoreTokens()) {
                            String lastPosStr = strToken.nextToken("-/");
                            try {
                                range[1] = Long.parseLong(lastPosStr);
                            } catch (NumberFormatException e2) {
                            }
                            if (strToken.hasMoreTokens()) {
                                String lengthStr = strToken.nextToken("/");
                                try {
                                    range[2] = Long.parseLong(lengthStr);
                                } catch (NumberFormatException e3) {
                                }
                            }
                        }
                    }
                }
            }
        }
        return range;
    }

    public long getContentRangeFirstPosition() {
        long[] range = getContentRange();
        return range[0];
    }

    public long getContentRangeLastPosition() {
        long[] range = getContentRange();
        return range[1];
    }

    public long getContentRangeInstanceLength() {
        long[] range = getContentRange();
        return range[2];
    }

    public void setCacheControl(String directive) {
        setHeader(HTTP.CACHE_CONTROL, directive);
    }

    public void setCacheControl(String directive, int value) {
        String strVal = String.valueOf(directive) + "=" + Integer.toString(value);
        setHeader(HTTP.CACHE_CONTROL, strVal);
    }

    public void setCacheControl(int value) {
        setCacheControl(HTTP.MAX_AGE, value);
    }

    public String getCacheControl() {
        return getHeaderValue(HTTP.CACHE_CONTROL);
    }

    public void setServer(String name) {
        setHeader(HTTP.SERVER, name);
    }

    public String getServer() {
        return getHeaderValue(HTTP.SERVER);
    }

    public void setHost(String host, int port) {
        String hostAddr = host;
        if (HostInterface.isIPv6Address(host)) {
            hostAddr = "[" + host + "]";
        }
        setHeader(HTTP.HOST, String.valueOf(hostAddr) + SOAP.DELIM + Integer.toString(port));
    }

    public void setHost(String host) {
        String hostAddr = host;
        if (HostInterface.isIPv6Address(host)) {
            hostAddr = "[" + host + "]";
        }
        setHeader(HTTP.HOST, hostAddr);
    }

    public String getHost() {
        return getHeaderValue(HTTP.HOST);
    }

    public void setDate(Calendar cal) {
        Date date = new Date(cal);
        setHeader(HTTP.DATE, date.getDateString());
    }

    public String getDate() {
        return getHeaderValue(HTTP.DATE);
    }

    public boolean hasTransferEncoding() {
        return hasHeader(HTTP.TRANSFER_ENCODING);
    }

    public void setTransferEncoding(String value) {
        setHeader(HTTP.TRANSFER_ENCODING, value);
    }

    public String getTransferEncoding() {
        return getHeaderValue(HTTP.TRANSFER_ENCODING);
    }

    public boolean isChunked() {
        String transEnc;
        if (hasTransferEncoding() && (transEnc = getTransferEncoding()) != null) {
            return transEnc.equalsIgnoreCase(HTTP.CHUNKED);
        }
        return false;
    }
}

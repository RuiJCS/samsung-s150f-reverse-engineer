package org.cybergarage.upnp.ssdp;

import java.io.InputStream;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;

/* JADX INFO: loaded from: classes.dex */
public class SSDPRequest extends HTTPRequest {
    public SSDPRequest() {
        setVersion("1.1");
    }

    public SSDPRequest(InputStream in) {
        super(in);
    }

    public void setNT(String value) {
        setHeader(HTTP.NT, value);
    }

    public String getNT() {
        return getHeaderValue(HTTP.NT);
    }

    public void setNTS(String value) {
        setHeader(HTTP.NTS, value);
    }

    public String getNTS() {
        return getHeaderValue(HTTP.NTS);
    }

    public void setLocation(String value) {
        setHeader(HTTP.LOCATION, value);
    }

    public String getLocation() {
        return getHeaderValue(HTTP.LOCATION);
    }

    public void setUSN(String value) {
        setHeader(HTTP.USN, value);
    }

    public String getUSN() {
        return getHeaderValue(HTTP.USN);
    }

    public void setLeaseTime(int len) {
        setHeader(HTTP.CACHE_CONTROL, "max-age=" + Integer.toString(len));
    }

    public int getLeaseTime() {
        String cacheCtrl = getHeaderValue(HTTP.CACHE_CONTROL);
        return SSDP.getLeaseTime(cacheCtrl);
    }
}

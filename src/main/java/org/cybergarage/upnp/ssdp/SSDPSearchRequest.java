package org.cybergarage.upnp.ssdp;

import org.cybergarage.http.HTTP;
import org.cybergarage.net.HostInterface;

/* JADX INFO: loaded from: classes.dex */
public class SSDPSearchRequest extends SSDPRequest {
    public SSDPSearchRequest(String serachTarget, int mx) {
        setMethod(HTTP.M_SEARCH);
        setURI("*");
        setHeader(HTTP.ST, serachTarget);
        setHeader(HTTP.MX, Integer.toString(mx));
        setHeader(HTTP.MAN, "\"ssdp:discover\"");
    }

    public SSDPSearchRequest(String serachTarget) {
        this(serachTarget, 3);
    }

    public SSDPSearchRequest() {
        this("upnp:rootdevice");
    }

    public void setLocalAddress(String bindAddr) {
        String ssdpAddr = SSDP.ADDRESS;
        if (HostInterface.isIPv6Address(bindAddr)) {
            ssdpAddr = SSDP.getIPv6Address();
        }
        setHost(ssdpAddr, SSDP.PORT);
    }
}

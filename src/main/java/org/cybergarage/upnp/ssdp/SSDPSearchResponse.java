package org.cybergarage.upnp.ssdp;

import org.cybergarage.http.HTTP;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnP;

/* JADX INFO: loaded from: classes.dex */
public class SSDPSearchResponse extends SSDPResponse {
    public SSDPSearchResponse() {
        setStatusCode(200);
        setCacheControl(Device.DEFAULT_LEASE_TIME);
        setHeader(HTTP.SERVER, UPnP.getServerName());
        setHeader(HTTP.EXT, "");
    }
}

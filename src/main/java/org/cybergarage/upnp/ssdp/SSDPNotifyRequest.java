package org.cybergarage.upnp.ssdp;

import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
public class SSDPNotifyRequest extends SSDPRequest {
    public SSDPNotifyRequest() {
        setMethod(HTTP.NOTIFY);
        setURI("*");
    }
}

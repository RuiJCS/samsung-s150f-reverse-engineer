package org.cybergarage.upnp.event;

import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.device.NT;

/* JADX INFO: loaded from: classes.dex */
public class SubscriptionRequest extends HTTPRequest {
    private static final String CALLBACK_END_WITH = ">";
    private static final String CALLBACK_START_WITH = "<";

    public SubscriptionRequest() {
        setContentLength(0L);
    }

    public SubscriptionRequest(HTTPRequest httpReq) {
        this();
        set(httpReq);
    }

    private void setService(Service service) {
        Device rootDev;
        Device rootDev2;
        String eventSubURL = service.getEventSubURL();
        setURI(eventSubURL, true);
        String urlBaseStr = "";
        Device dev = service.getDevice();
        if (dev != null) {
            urlBaseStr = dev.getURLBase();
        }
        if ((urlBaseStr == null || urlBaseStr.length() <= 0) && (rootDev = service.getRootDevice()) != null) {
            urlBaseStr = rootDev.getURLBase();
        }
        if ((urlBaseStr == null || urlBaseStr.length() <= 0) && (rootDev2 = service.getRootDevice()) != null) {
            urlBaseStr = rootDev2.getLocation();
        }
        if ((urlBaseStr == null || urlBaseStr.length() <= 0) && HTTP.isAbsoluteURL(eventSubURL)) {
            urlBaseStr = eventSubURL;
        }
        String reqHost = HTTP.getHost(urlBaseStr);
        int reqPort = HTTP.getPort(urlBaseStr);
        setHost(reqHost, reqPort);
        setRequestHost(reqHost);
        setRequestPort(reqPort);
    }

    public void setSubscribeRequest(Service service, String callback, long timeout) {
        setMethod("SUBSCRIBE");
        setService(service);
        setCallback(callback);
        setNT(NT.EVENT);
        setTimeout(timeout);
    }

    public void setRenewRequest(Service service, String uuid, long timeout) {
        setMethod("SUBSCRIBE");
        setService(service);
        setSID(uuid);
        setTimeout(timeout);
    }

    public void setUnsubscribeRequest(Service service) {
        setMethod("UNSUBSCRIBE");
        setService(service);
        setSID(service.getSID());
    }

    public void setNT(String value) {
        setHeader(HTTP.NT, value);
    }

    public String getNT() {
        return getHeaderValue(HTTP.NT);
    }

    public boolean hasNT() {
        String nt = getNT();
        return nt != null && nt.length() > 0;
    }

    public void setCallback(String value) {
        setStringHeader(HTTP.CALLBACK, value, CALLBACK_START_WITH, CALLBACK_END_WITH);
    }

    public String getCallback() {
        return getStringHeaderValue(HTTP.CALLBACK, CALLBACK_START_WITH, CALLBACK_END_WITH);
    }

    public boolean hasCallback() {
        String callback = getCallback();
        return callback != null && callback.length() > 0;
    }

    public void setSID(String id) {
        setHeader(HTTP.SID, Subscription.toSIDHeaderString(id));
    }

    public String getSID() {
        String sid = Subscription.getSID(getHeaderValue(HTTP.SID));
        if (sid == null) {
            return "";
        }
        return sid;
    }

    public boolean hasSID() {
        String sid = getSID();
        return sid != null && sid.length() > 0;
    }

    public final void setTimeout(long value) {
        setHeader(HTTP.TIMEOUT, Subscription.toTimeoutHeaderString(value));
    }

    public long getTimeout() {
        return Subscription.getTimeout(getHeaderValue(HTTP.TIMEOUT));
    }

    public void post(SubscriptionResponse subRes) {
        super.post((HTTPResponse) subRes);
    }

    public SubscriptionResponse post() {
        HTTPResponse httpRes = post(getRequestHost(), getRequestPort());
        return new SubscriptionResponse(httpRes);
    }

    public SubscriptionResponse post(int timeOut) {
        HTTPResponse httpRes = post(getRequestHost(), getRequestPort(), timeOut);
        return new SubscriptionResponse(httpRes);
    }
}

package org.cybergarage.upnp.event;

import org.cybergarage.upnp.UPnP;

/* JADX INFO: loaded from: classes.dex */
public class Subscription {
    public static final String INFINITE_STRING = "infinite";
    public static final int INFINITE_VALUE = -1;
    public static final String SUBSCRIBE_METHOD = "SUBSCRIBE";
    public static final String TIMEOUT_HEADER = "Second-";
    public static final String UNSUBSCRIBE_METHOD = "UNSUBSCRIBE";
    public static final String UUID = "uuid:";
    public static final String XMLNS = "urn:schemas-upnp-org:event-1-0";

    public static final String toTimeoutHeaderString(long time) {
        return time == -1 ? INFINITE_STRING : TIMEOUT_HEADER + Long.toString(time);
    }

    public static final long getTimeout(String headerValue) {
        int minusIdx = headerValue.indexOf(45);
        try {
            String timeoutStr = headerValue.substring(minusIdx + 1, headerValue.length());
            long timeout = Long.parseLong(timeoutStr);
            return timeout;
        } catch (Exception e) {
            return -1L;
        }
    }

    public static final String createSID() {
        return UPnP.createUUID();
    }

    public static final String toSIDHeaderString(String id) {
        return UUID + id;
    }

    public static final String getSID(String headerValue) {
        if (headerValue == null) {
            return "";
        }
        return headerValue.startsWith(UUID) ? headerValue.substring(UUID.length(), headerValue.length()) : headerValue;
    }
}

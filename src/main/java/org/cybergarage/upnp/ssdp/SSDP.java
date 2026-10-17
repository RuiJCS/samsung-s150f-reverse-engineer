package org.cybergarage.upnp.ssdp;

import org.cybergarage.http.HTTP;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class SSDP {
    public static final String ADDRESS = "239.255.255.250";
    public static final int DEFAULT_MSEARCH_MX = 3;
    private static String IPV6_ADDRESS = null;
    public static final String IPV6_ADMINISTRATIVE_ADDRESS = "FF04::C";
    public static final String IPV6_GLOBAL_ADDRESS = "FF0E::C";
    public static final String IPV6_LINK_LOCAL_ADDRESS = "FF02::C";
    public static final String IPV6_SITE_LOCAL_ADDRESS = "FF05::C";
    public static final String IPV6_SUBNET_ADDRESS = "FF03::C";
    public static int PORT = 1900;
    public static final int RECV_MESSAGE_BUFSIZE = 1024;

    static {
        setIPv6Address(IPV6_LINK_LOCAL_ADDRESS);
    }

    public static final void setIPv6Address(String addr) {
        IPV6_ADDRESS = addr;
    }

    public static final String getIPv6Address() {
        return IPV6_ADDRESS;
    }

    public static final int getLeaseTime(String cacheCont) {
        int maxAgeIdx = cacheCont.indexOf(HTTP.MAX_AGE);
        if (maxAgeIdx < 0) {
            return 0;
        }
        int endIdx = cacheCont.indexOf(44, maxAgeIdx);
        if (endIdx < 0) {
            endIdx = cacheCont.length();
        }
        try {
            String mxStr = cacheCont.substring(cacheCont.indexOf("=", maxAgeIdx) + 1, endIdx).trim();
            int mx = Integer.parseInt(mxStr);
            return mx;
        } catch (Exception e) {
            Debug.warning(e);
            return 0;
        }
    }

    public static final void setSSDPPort(int port) {
        PORT = port;
    }

    public static final int getSSDPPort() {
        return PORT;
    }
}

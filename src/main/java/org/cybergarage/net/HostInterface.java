package org.cybergarage.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import org.cybergarage.soap.SOAP;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class HostInterface {
    public static final int IPV4_BITMASK = 1;
    public static final int IPV6_BITMASK = 16;
    public static final int LOCAL_BITMASK = 256;
    public static boolean USE_LOOPBACK_ADDR = false;
    public static boolean USE_ONLY_IPV4_ADDR = false;
    public static boolean USE_ONLY_IPV6_ADDR = false;
    public static boolean USE_MULTICAST_ADDR = false;
    private static String ifAddress = "";

    public static final void setInterface(String ifaddr) {
        ifAddress = ifaddr;
    }

    public static final String getInterface() {
        return ifAddress;
    }

    private static final boolean hasAssignedInterface() {
        return ifAddress.length() > 0;
    }

    private static final boolean isUsableAddress(InetAddress addr) {
        if (!USE_LOOPBACK_ADDR && addr.isLoopbackAddress()) {
            return false;
        }
        if (USE_ONLY_IPV4_ADDR && (addr instanceof Inet6Address)) {
            return false;
        }
        if (USE_ONLY_IPV6_ADDR && (addr instanceof Inet4Address)) {
            return false;
        }
        return USE_MULTICAST_ADDR || !addr.isMulticastAddress();
    }

    public static final int getNHostAddresses() {
        if (hasAssignedInterface()) {
            return 1;
        }
        int nHostAddrs = 0;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress addr = inetAddresses.nextElement();
                    if (addr.toString().contains("/192.") && isUsableAddress(addr)) {
                        nHostAddrs++;
                    }
                }
            }
            return nHostAddrs;
        } catch (Exception e) {
            Debug.warning(e);
            return nHostAddrs;
        }
    }

    public static final InetAddress[] getInetAddress(int ipfilter, String[] interfaces) {
        Enumeration<NetworkInterface> networkInterfaces;
        if (interfaces != null) {
            Vector iflist = new Vector();
            for (String str : interfaces) {
                try {
                    NetworkInterface ni = NetworkInterface.getByName(str);
                    if (ni != null) {
                        iflist.add(ni);
                    }
                } catch (SocketException e) {
                }
            }
            networkInterfaces = iflist.elements();
        } else {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e2) {
                return null;
            }
        }
        ArrayList addresses = new ArrayList();
        while (networkInterfaces.hasMoreElements()) {
            Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress addr = inetAddresses.nextElement();
                if ((ipfilter & 256) != 0 || !addr.isLoopbackAddress()) {
                    if ((ipfilter & 1) != 0 && (addr instanceof Inet4Address)) {
                        addresses.add(addr);
                    } else if ((ipfilter & 16) != 0 && (addr instanceof InetAddress)) {
                        addresses.add(addr);
                    }
                }
            }
        }
        return (InetAddress[]) addresses.toArray(new InetAddress[0]);
    }

    public static final String getHostAddress(int n) {
        if (hasAssignedInterface()) {
            return getInterface();
        }
        int hostAddrCnt = 0;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress addr = inetAddresses.nextElement();
                    if (addr.toString().contains("/192.") && isUsableAddress(addr)) {
                        if (hostAddrCnt < n) {
                            hostAddrCnt++;
                        } else {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static final boolean isIPv6Address(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet6Address;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean isIPv4Address(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet4Address;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean hasIPv4Addresses() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasIPv6Addresses() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr)) {
                return true;
            }
        }
        return false;
    }

    public static final String getIPv4Address() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr)) {
                return addr;
            }
        }
        return "";
    }

    public static final String getIPv6Address() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr)) {
                return addr;
            }
        }
        return "";
    }

    public static final String getHostURL(String host, int port, String uri) {
        String hostAddr = host;
        if (isIPv6Address(host)) {
            hostAddr = "[" + host + "]";
        }
        return "http://" + hostAddr + SOAP.DELIM + Integer.toString(port) + uri;
    }
}

package org.cybergarage.upnp.device;

/* JADX INFO: loaded from: classes.dex */
public class ST {
    public static final String ALL_DEVICE = "ssdp:all";
    public static final String ROOT_DEVICE = "upnp:rootdevice";
    public static final String URN_DEVICE = "urn:schemas-upnp-org:device:";
    public static final String URN_SERVICE = "urn:schemas-upnp-org:service:";
    public static final String UUID_DEVICE = "uuid";

    public static final boolean isAllDevice(String value) {
        if (value == null) {
            return false;
        }
        if (value.equals(ALL_DEVICE)) {
            return true;
        }
        return value.equals("\"ssdp:all\"");
    }

    public static final boolean isRootDevice(String value) {
        if (value == null) {
            return false;
        }
        if (value.equals("upnp:rootdevice")) {
            return true;
        }
        return value.equals("\"upnp:rootdevice\"");
    }

    public static final boolean isUUIDDevice(String value) {
        if (value == null) {
            return false;
        }
        if (value.startsWith(UUID_DEVICE)) {
            return true;
        }
        return value.startsWith("\"uuid");
    }

    public static final boolean isURNDevice(String value) {
        if (value == null) {
            return false;
        }
        if (value.startsWith(URN_DEVICE)) {
            return true;
        }
        return value.startsWith("\"urn:schemas-upnp-org:device:");
    }

    public static final boolean isURNService(String value) {
        if (value == null) {
            return false;
        }
        if (value.startsWith(URN_SERVICE)) {
            return true;
        }
        return value.startsWith("\"urn:schemas-upnp-org:service:");
    }
}

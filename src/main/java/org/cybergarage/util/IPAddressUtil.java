package org.cybergarage.util;

/* JADX INFO: loaded from: classes.dex */
public class IPAddressUtil {
    public static boolean isIPv4Address(String src) {
        return src.matches("([0-9]{1,3}\\.){3}([0-9]{1,3})");
    }
}

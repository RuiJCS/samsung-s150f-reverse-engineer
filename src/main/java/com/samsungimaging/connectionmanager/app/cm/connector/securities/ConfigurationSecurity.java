package com.samsungimaging.connectionmanager.app.cm.connector.securities;

import android.net.wifi.ScanResult;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class ConfigurationSecurity {
    public static final String EAP = "EAP";
    public static final String OPEN = "OPEN";
    public static final String PSK = "PSK";
    public static final int SECURITY_EAP = 2;
    public static final int SECURITY_OPEN = 3;
    public static final int SECURITY_PSK = 1;
    public static final int SECURITY_WEP = 0;
    public static final String WEP = "WEP";

    public static int getSecurity(ScanResult result) {
        Log.d("c_m", "ConfigurationSecurities, getSecurity, result.capabilities = " + result.capabilities);
        if (result.capabilities.contains(WEP)) {
            return 0;
        }
        if (result.capabilities.contains(PSK)) {
            return 1;
        }
        if (result.capabilities.contains(EAP)) {
            return 2;
        }
        return 3;
    }

    public static String getSecurity2(ScanResult result) {
        Log.d("c_m", "ConfigurationSecurities, getSecurity2, result.capabilities = " + result.capabilities);
        if (result.capabilities.contains(WEP)) {
            return WEP;
        }
        if (result.capabilities.contains(PSK)) {
            return PSK;
        }
        if (result.capabilities.contains(EAP)) {
            return EAP;
        }
        return OPEN;
    }

    public static boolean isOpenNetwork(ScanResult result) {
        int security_type = getSecurity(result);
        return String.valueOf(3).equals(String.valueOf(security_type));
    }

    public static boolean isOpenNetwork(String security) {
        return String.valueOf(3).equals(security);
    }
}

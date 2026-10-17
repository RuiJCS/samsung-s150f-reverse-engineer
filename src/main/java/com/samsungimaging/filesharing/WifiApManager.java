package com.samsungimaging.filesharing;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.samsungimaging.filesharing.util.Logger;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class WifiApManager {
    private static final String TAG = "myLog";
    public static final int WIFI_AP_STATE_DISABLED = 1;
    public static final int WIFI_AP_STATE_DISABLING = 0;
    public static final int WIFI_AP_STATE_ENABLED = 3;
    public static final int WIFI_AP_STATE_ENABLING = 2;
    public static final int WIFI_AP_STATE_FAILED = 4;
    private final WifiManager mWifiManager;

    public WifiApManager(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) {
            this.mWifiManager.setWifiEnabled(false);
        }
        try {
            Method method = this.mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return ((Boolean) method.invoke(this.mWifiManager, null, Boolean.valueOf(enabled))).booleanValue();
        } catch (Exception e) {
            Logger.e(TAG, "", e);
            return false;
        }
    }

    public boolean setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean enabled) {
        try {
            Method method = this.mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return ((Boolean) method.invoke(this.mWifiManager, wifiConfiguration, Boolean.valueOf(enabled))).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public int getWifiApState() {
        try {
            Method method = this.mWifiManager.getClass().getMethod("getWifiApState", new Class[0]);
            return ((Integer) method.invoke(this.mWifiManager, new Object[0])).intValue();
        } catch (Exception e) {
            Logger.e(TAG, "", e);
            return 4;
        }
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = this.mWifiManager.getClass().getMethod("getWifiApState", new Class[0]);
            int state = ((Integer) method.invoke(this.mWifiManager, new Object[0])).intValue();
            if (state == 3) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Logger.e(TAG, "", e);
            return false;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = this.mWifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]);
            return (WifiConfiguration) method.invoke(this.mWifiManager, new Object[0]);
        } catch (Exception e) {
            Logger.e(TAG, "", e);
            return null;
        }
    }
}

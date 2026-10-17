package com.samsungimaging.connectionmanager.app.cm.connector;

import android.net.wifi.ScanResult;

/* JADX INFO: loaded from: classes.dex */
public class LastRequestedCameraInfo {
    private static LastRequestedCameraInfo instance_LastRequestedCameraInfo = null;
    private ScanResult mSr = null;
    private String mSSID = null;

    private LastRequestedCameraInfo() {
    }

    public static synchronized LastRequestedCameraInfo getInstance() {
        if (instance_LastRequestedCameraInfo == null) {
            instance_LastRequestedCameraInfo = new LastRequestedCameraInfo();
        }
        return instance_LastRequestedCameraInfo;
    }

    public void setLastRequestedCameraInfo(ScanResult sr) {
        this.mSr = sr;
    }

    public ScanResult getLastRequestedCameraInfo() {
        return this.mSr;
    }

    public void setLastRequestedCameraSSID(String ssid) {
        this.mSSID = ssid;
    }

    public String getLastRequestedCameraSSID() {
        return this.mSSID;
    }
}

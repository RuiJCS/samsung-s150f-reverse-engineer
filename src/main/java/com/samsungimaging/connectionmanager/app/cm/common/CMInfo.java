package com.samsungimaging.connectionmanager.app.cm.common;

import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class CMInfo {
    public static final int NUM_OF_NFC_SCAN_RESULT_CHECK_MAX = 3;
    private static CMInfo instance_CMInfo = null;
    private int mNFCScanResultCheckCount = 0;
    private String mConnectedSSID = "";
    private String mNFCTagSSID = null;
    private boolean mIsNFCLaunch = false;
    private String mDescriptionURLForRVFML = null;
    private boolean mOldCamera = true;

    private CMInfo() {
    }

    public static synchronized CMInfo getInstance() {
        if (instance_CMInfo == null) {
            instance_CMInfo = new CMInfo();
        }
        return instance_CMInfo;
    }

    public void setConnectedSSID(String connectedssid) {
        this.mConnectedSSID = CMUtil.convertToNOTQuotedString(connectedssid);
        Trace.d(CMConstants.TAG_NAME, "CMInfo.setConnectedSSID, mConnectedSSID = " + this.mConnectedSSID);
    }

    public String getConnectedSSID() {
        Trace.d(CMConstants.TAG_NAME, "CMInfo.getConnectedSSID, mConnectedSSID = " + this.mConnectedSSID);
        return this.mConnectedSSID;
    }

    public void setIsNFCLaunch(boolean isNFCLaunch, String nfctagssid) {
        this.mIsNFCLaunch = isNFCLaunch;
        String ssid_temp = CMUtil.convertToNOTQuotedString(nfctagssid);
        this.mNFCTagSSID = ssid_temp;
        Trace.d(CMConstants.TAG_NAME, "CMInfo.setIsNFCLaunch, mIsNFCLaunch = " + this.mIsNFCLaunch + ", mNFCTagSSID = " + this.mNFCTagSSID);
        if (!this.mIsNFCLaunch) {
            this.mNFCScanResultCheckCount = 0;
        }
    }

    public boolean getIsNFCLaunch() {
        Trace.d(CMConstants.TAG_NAME, "CMInfo.getIsNFCLaunch, mIsNFCLaunch = " + this.mIsNFCLaunch + ", mNFCTagSSID = " + this.mNFCTagSSID);
        if (this.mConnectedSSID.equals(this.mNFCTagSSID) && this.mIsNFCLaunch) {
            Trace.d(CMConstants.TAG_NAME, "CMInfo.getIsNFCLaunch is true!");
            return true;
        }
        Trace.d(CMConstants.TAG_NAME, "CMInfo.getIsNFCLaunch is false!");
        return false;
    }

    public boolean getIsNFCLaunchFlag() {
        Trace.d(CMConstants.TAG_NAME, "CMInfo.getIsNFCLaunchFlag, mIsNFCLaunch = " + this.mIsNFCLaunch);
        return this.mIsNFCLaunch;
    }

    public String getNFCTagSSID() {
        return this.mNFCTagSSID;
    }

    public void setNFCScanResultCheck(int count) {
        this.mNFCScanResultCheckCount = count;
    }

    public int getNFCScanResultCheckCount() {
        return this.mNFCScanResultCheckCount;
    }

    public void setDescriptionURL(String url) {
        this.mDescriptionURLForRVFML = url;
    }

    public String getDescriptionURL() {
        return this.mDescriptionURLForRVFML;
    }

    public void setIsOldCamera(boolean old) {
        this.mOldCamera = old;
    }

    public boolean isOldCamera() {
        return this.mOldCamera;
    }
}

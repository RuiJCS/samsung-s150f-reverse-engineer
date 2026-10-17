package com.samsungimaging.connectionmanager.app.cm.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class CMNetworkStateChangeReceiver extends BroadcastReceiver {
    private static /* synthetic */ int[] $SWITCH_TABLE$android$net$wifi$SupplicantState;
    private String mOldstate_network_state_changed = "";
    private String mNewstate_network_state_changed = "";
    private String mOldssid_network_state_changed = "";
    private String mNewssid_network_state_changed = "";
    private int mRetryPasswordInputCount = 0;
    private String mOldstate_supplicant_state_changed = "";
    private String mNewstate_supplicant_state_changed = "";
    private String mOldssid_supplicant_state_changed = "";
    private String mNewssid_supplicant_state_changed = "";
    private String mOldbssid_supplicant_state_changed = "";
    private String mNewbssid_supplicant_state_changed = "";

    static /* synthetic */ int[] $SWITCH_TABLE$android$net$wifi$SupplicantState() {
        int[] iArr = $SWITCH_TABLE$android$net$wifi$SupplicantState;
        if (iArr == null) {
            iArr = new int[SupplicantState.values().length];
            try {
                iArr[SupplicantState.ASSOCIATED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[SupplicantState.ASSOCIATING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[SupplicantState.AUTHENTICATING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[SupplicantState.COMPLETED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[SupplicantState.DISCONNECTED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[SupplicantState.DORMANT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[SupplicantState.FOUR_WAY_HANDSHAKE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[SupplicantState.GROUP_HANDSHAKE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[SupplicantState.INACTIVE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[SupplicantState.INTERFACE_DISABLED.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[SupplicantState.INVALID.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[SupplicantState.SCANNING.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[SupplicantState.UNINITIALIZED.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            $SWITCH_TABLE$android$net$wifi$SupplicantState = iArr;
        }
        return iArr;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, action = " + action);
        if (action.equals("android.intent.action.LOCALE_CHANGED")) {
            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, LOCALE_CHANGED!!!");
            WifiManager wifi = (WifiManager) context.getSystemService("wifi");
            wifi.disconnect();
            CMService.getInstance().goToHome();
            return;
        }
        boolean isReceiverRunnig = "true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.SP_KEY_IS_RECEIVER_RUNNING, "true"));
        if (!isReceiverRunnig) {
            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, return04");
            return;
        }
        if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(action)) {
            boolean connected = intent.getBooleanExtra("connected", false);
            if (!connected) {
                Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver : finighing by wifi disable on low version android phone");
                CMUtil.sendBroadCastToMain(context, CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED);
                return;
            }
            return;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        this.mNewssid_network_state_changed = wifiInfo.getSSID();
        this.mNewssid_supplicant_state_changed = this.mNewssid_network_state_changed;
        this.mNewbssid_supplicant_state_changed = wifiInfo.getBSSID();
        NetworkInfo.DetailedState state = null;
        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        if (info != null) {
            state = info.getDetailedState();
            this.mNewstate_network_state_changed = state.toString();
        }
        int apiLevel = Integer.parseInt(Build.VERSION.SDK);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            String typeName = info.getTypeName();
            info.getSubtypeName();
            boolean available = info.isAvailable();
            if ("WIFI".equals(typeName) && !available) {
                if (CMUtil.supportDSCPrefix(this.mNewssid_network_state_changed) && CMUtil.isManagedSSID(context, this.mNewssid_network_state_changed)) {
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver----------------1");
                } else {
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver----------------2");
                }
            } else if ("WIFI".equals(typeName) && available) {
                Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver----------------3");
            }
        }
        if ("android.net.wifi.STATE_CHANGE".equals(action)) {
            int wifiOnOffState = wifiManager.getWifiState();
            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, wifiOnOffState = " + wifiOnOffState);
            if (wifiOnOffState == 3) {
                Trace.d(CMConstants.TAG_NAME, "Performance Check Point : Detailed State = " + state);
                if (state == NetworkInfo.DetailedState.CONNECTED) {
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, mNewssid = " + this.mNewssid_network_state_changed);
                    if (CMUtil.supportDSCPrefix(this.mNewssid_network_state_changed) && CMUtil.isManagedSSID(context, this.mNewssid_network_state_changed)) {
                        if (CMService.IS_WIFI_CONNECTED || CMService.getInstance().isSubAppAlive() || CMService.getInstance().isWifiScanStop()) {
                            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, return, IS_WIFI_CONNECTED01 = " + CMService.IS_WIFI_CONNECTED + ", ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", isWifiScanStop = " + CMService.getInstance().isWifiScanStop());
                            return;
                        } else {
                            CMService.IS_WIFI_CONNECTED = true;
                            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, Connected to CamearAP....ok");
                            CMService.getInstance().checkSubApplicationType();
                        }
                    } else {
                        Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, IS_WIFI_CONNECTED02 = " + CMService.IS_WIFI_CONNECTED);
                        CMService.IS_WIFI_CONNECTED = false;
                    }
                } else {
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, apiLevel = " + apiLevel);
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, mOldstate_network_state_changed = " + this.mOldstate_network_state_changed + ", mNewstate_network_state_changed = " + this.mNewstate_network_state_changed);
                    Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, mOldssid_network_state_changed = " + this.mOldssid_network_state_changed + ", mNewssid_network_state_changed = " + this.mNewssid_network_state_changed);
                    if (this.mOldstate_network_state_changed.equals("VERIFYING_POOR_LINK") && this.mNewstate_network_state_changed.equals("DISCONNECTED")) {
                        String brand = Build.BRAND;
                        String manufacturer = Build.MANUFACTURER;
                        Trace.d(CMConstants.TAG_NAME, "brand = " + brand + ", manufacturer = " + manufacturer);
                        if (!brand.toLowerCase().contains("samsung") && !manufacturer.toLowerCase().contains("samsung") && (((CMUtil.supportDSCPrefix(this.mNewssid_network_state_changed) && CMUtil.isManagedSSID(context, this.mNewssid_network_state_changed)) || (CMUtil.supportDSCPrefix(this.mOldssid_network_state_changed) && CMUtil.isManagedSSID(context, this.mOldssid_network_state_changed))) && CMUtil.isCMTopActivity(context))) {
                            CMUtil.sendBroadCastToMain(context, CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_INTERNET_SERVICE_MENU_IN_SETTINGS);
                        }
                    } else if ((this.mOldstate_network_state_changed.equals("CONNECTED") && this.mNewstate_network_state_changed.equals("DISCONNECTED")) || ((this.mOldstate_network_state_changed.equals("CONNECTED") && this.mNewstate_network_state_changed.equals("DISCONNECTING")) || ((this.mOldstate_network_state_changed.equals("CONNECTED") && this.mNewstate_network_state_changed.equals("SCANNING")) || (this.mOldstate_network_state_changed.equals("CAPTIVE_PORTAL_CHECK") && this.mNewstate_network_state_changed.equals("DISCONNECTED"))))) {
                        Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, check disconnected... IS_WIFI_CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", mOldssid = " + this.mOldssid_network_state_changed);
                        if (CMUtil.supportDSCPrefix(this.mOldssid_network_state_changed) && CMUtil.isManagedSSID(context, this.mOldssid_network_state_changed)) {
                            CMService.IS_WIFI_CONNECTED = false;
                            CMUtil.sendBroadCastToMain(context, CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED);
                        }
                    } else {
                        Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, check disconnected... IS_WIFI_CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", mNewstate = " + this.mNewstate_network_state_changed);
                        if (CMService.IS_WIFI_CONNECTED && this.mNewstate_network_state_changed.equals("DISCONNECTED")) {
                            CMService.IS_WIFI_CONNECTED = false;
                            CMUtil.sendBroadCastToMain(context, CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED);
                        }
                        CMService.IS_WIFI_CONNECTED = false;
                    }
                }
                this.mOldssid_network_state_changed = this.mNewssid_network_state_changed;
                this.mOldstate_network_state_changed = state.toString();
            } else {
                if (wifiOnOffState == 0 || wifiOnOffState == 1) {
                    CMService.IS_WIFI_CONNECTED = false;
                }
                Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, finished by wifi disable");
                CMUtil.sendBroadCastToMain(context, CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED);
            }
        }
        if (action.equals("android.net.wifi.supplicant.STATE_CHANGE")) {
            SupplicantState supl_state = (SupplicantState) intent.getParcelableExtra("newState");
            this.mNewstate_supplicant_state_changed = supl_state.toString();
            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, SUPPLICANT_STATE_CHANGED_ACTION, mOldssid_supplicant_state_changed = " + this.mOldssid_supplicant_state_changed + ", mNewssid_supplicant_state_changed = " + this.mNewssid_supplicant_state_changed);
            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, SUPPLICANT_STATE_CHANGED_ACTION, mOldstate_supplicant_state_changed = " + this.mOldstate_supplicant_state_changed + ", mNewstate_supplicant_state_changed = " + this.mNewstate_supplicant_state_changed);
            switch ($SWITCH_TABLE$android$net$wifi$SupplicantState()[supl_state.ordinal()]) {
                case 5:
                    if (CMUtil.supportDSCPrefix(this.mOldssid_supplicant_state_changed) && CMUtil.isManagedSSID(context, this.mOldssid_supplicant_state_changed)) {
                        if (this.mOldstate_supplicant_state_changed.equals(SupplicantState.FOUR_WAY_HANDSHAKE.toString())) {
                            this.mRetryPasswordInputCount++;
                            Trace.d(CMConstants.TAG_NAME, "CMNetworkStateChangeReceiver, SUPPLICANT_STATE_CHANGED_ACTION, password error!!!, mRetryPasswordInputCount = " + this.mRetryPasswordInputCount);
                            if (this.mRetryPasswordInputCount >= 1) {
                                CMUtil.removeNetworkConfig(context, wifiManager, this.mOldssid_supplicant_state_changed, this.mOldbssid_supplicant_state_changed);
                                this.mRetryPasswordInputCount = 0;
                            }
                        } else {
                            this.mRetryPasswordInputCount = 0;
                        }
                    }
                    break;
            }
            this.mOldssid_supplicant_state_changed = this.mNewssid_supplicant_state_changed;
            this.mOldbssid_supplicant_state_changed = this.mNewbssid_supplicant_state_changed;
            this.mOldstate_supplicant_state_changed = this.mNewstate_supplicant_state_changed;
        }
    }
}

package com.samsungimaging.filesharing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import com.samsungimaging.filesharing.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
@SuppressLint({"NewApi"})
public class WifiConnector {
    private static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = null;
    public static final int CONNECT_TIMEOUT = 20000;
    public static final int SCAN_TIMEOUT = 6000;
    public static final String TAG = "INRY";
    public static WifiConnector instance = null;
    protected WifiManager wifiManager;
    private ConnectorThread mConnectorThread = null;
    private boolean mIsStop = false;
    protected List<ScanResult> srs = null;
    private Context mContext = null;
    private Handler mHandler = null;

    static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState() {
        int[] iArr = $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;
        if (iArr == null) {
            iArr = new int[NetworkInfo.DetailedState.values().length];
            try {
                iArr[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[NetworkInfo.DetailedState.BLOCKED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[NetworkInfo.DetailedState.DISCONNECTING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[NetworkInfo.DetailedState.FAILED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[NetworkInfo.DetailedState.IDLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[NetworkInfo.DetailedState.SUSPENDED.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = iArr;
        }
        return iArr;
    }

    public static WifiConnector getInstance() {
        if (instance != null) {
            return instance;
        }
        WifiConnector wifiConnector = new WifiConnector();
        instance = wifiConnector;
        return wifiConnector;
    }

    private WifiConnector() {
        this.wifiManager = null;
        this.wifiManager = null;
    }

    public WifiConnector init(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        return this;
    }

    public boolean start() {
        Logger.d("INRY", "WifiConnector.start");
        stop();
        this.mIsStop = false;
        this.mConnectorThread = new ConnectorThread();
        this.mConnectorThread.start();
        return true;
    }

    public boolean stop() {
        Logger.d("INRY", "WifiConnector.stop");
        this.mIsStop = true;
        if (this.mConnectorThread != null) {
            this.mConnectorThread.interrupt();
            try {
                this.mConnectorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mConnectorThread = null;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sendEvent(int event, Object obj) {
        if (this.mHandler != null) {
            Logger.d("INRY", "WifiConnector.sendEvent event=" + event);
            Message msg = new Message();
            msg.what = event;
            msg.obj = obj;
            this.mHandler.sendMessage(msg);
        }
        return false;
    }

    public class ConnectorThread extends Thread {
        public ConnectorThread() {
            setName(getClass().getSimpleName());
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ((FileSharing) WifiConnector.this.mContext).mWifiLock = WifiConnector.this.wifiManager.createWifiLock(3, "LOCK");
            ((FileSharing) WifiConnector.this.mContext).mWifiLock.setReferenceCounted(true);
            ((FileSharing) WifiConnector.this.mContext).mWifiLock.acquire();
            WifiConnector.this.sendEvent(1, null);
            if (WifiConnector.this.isConnectedSoftAp(false)) {
                WifiConnector.this.sendEvent(3, null);
            }
            Logger.GG("Waiting for turning Wifi on... \n\t" + (WifiConnector.this.enableWifi(true) ? "OK" : "Failed"));
            if (WifiConnector.this.disconnectNetwork()) {
                WifiConnector.this.sendEvent(2, WifiConnector.this.wi().getSSID());
            }
            if (WifiConnector.this.srs != null) {
                WifiConnector.this.srs.clear();
            }
            long startTimeInMillis = System.currentTimeMillis();
            do {
                WifiConnector.this.srs = WifiConnector.this.scanSoftAP(3);
                long passedTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
                if (passedTimeInMillis >= 20000) {
                    break;
                }
            } while (WifiConnector.this.srs.size() == 0);
            Logger.d("INRY", "srs.size() : " + WifiConnector.this.srs.size());
            switch (WifiConnector.this.srs.size()) {
                case WifiApManager.WIFI_AP_STATE_DISABLING /* 0 */:
                    Logger.GG("Soft-AP를 찾지못했습니다. 일단 기존연결 해제...\n\t" + (WifiConnector.this.disconnectNetwork(WifiConnector.this.wi()) ? "OK" : "Failed."));
                    Logger.GG("*. After disconnecting current networks/");
                    WifiConnector.this.dumpNetworkInfo();
                    WifiConnector.this.sendEvent(41, null);
                    break;
                case 1:
                    if (Utils.unsupportDSCPrefix(WifiConnector.this.srs.get(0).SSID)) {
                        WifiConnector.this.sendEvent(43, null);
                    } else {
                        Logger.GG("1개의 Soft-AP 발견. 자동으로 연결합니다.");
                        if (WifiConnector.this.connectSingleApAuto(WifiConnector.this.srs.get(0), 3)) {
                            ((FileSharing) WifiConnector.this.mContext).mSelectedSr = WifiConnector.this.srs.get(0);
                            FileSharing.mPreconnectedSsid = WifiConnector.this.srs.get(0).SSID;
                            WifiConnector.this.sendEvent(3, null);
                        } else {
                            WifiConnector.this.sendEvent(41, null);
                        }
                        Logger.GG("*. After connecting target networks/");
                        WifiConnector.this.dumpNetworkInfo();
                    }
                    break;
                default:
                    Logger.GG("2개이상의 Soft-AP 발견.");
                    Collections.sort(WifiConnector.this.srs, new Comparator<ScanResult>() { // from class: com.samsungimaging.filesharing.WifiConnector.ConnectorThread.1
                        @Override // java.util.Comparator
                        public int compare(ScanResult sr1, ScanResult sr2) {
                            return Integer.signum(sr2.level - sr1.level);
                        }
                    });
                    WifiConnector.this.sendEvent(40, null);
                    break;
            }
        }
    }

    public WifiInfo wi() {
        return this.wifiManager.getConnectionInfo();
    }

    public NetworkInfo ni() {
        return ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getNetworkInfo(1);
    }

    public String getCurrentSSID() {
        return wi().getSSID();
    }

    public String getCurrentIpAddr() {
        return WifiUtils.intToIp(wi().getIpAddress());
    }

    public void dumpNetworkInfo() {
        if (ni() != null) {
            Logger.GG(ni().toString());
        }
    }

    public boolean enableWifi(boolean bEnable) {
        long estimated;
        this.wifiManager.setWifiEnabled(bEnable);
        Logger.GG("Waiting for " + (bEnable ? "Enabling" : "Disabling") + " WiFi...");
        if (bEnable) {
            estimated = Utils.waitFor(5000L, new Utils.IFunc() { // from class: com.samsungimaging.filesharing.WifiConnector.1
                @Override // com.samsungimaging.filesharing.Utils.IFunc
                public boolean func() {
                    return WifiConnector.this.wifiManager.isWifiEnabled();
                }
            });
        } else {
            estimated = Utils.waitFor(5000L, new Utils.IFunc() { // from class: com.samsungimaging.filesharing.WifiConnector.2
                @Override // com.samsungimaging.filesharing.Utils.IFunc
                public boolean func() {
                    return !WifiConnector.this.wifiManager.isWifiEnabled();
                }
            });
        }
        StringBuilder sb = new StringBuilder(HTTP.TAB);
        boolean result = estimated < 5000;
        Logger.GG(sb.append(result ? "OK" : "Failed").append(" during ").append(estimated / 1000.0d).append(" secs").toString());
        return result;
    }

    public boolean connectSingleApAuto(ScanResult sr, int retryCount) {
        boolean bConn;
        int ii = 0;
        if (retryCount <= 0) {
            Logger.GG("Why are you calling on me?");
            return false;
        }
        Logger.GG("Connecting [" + sr.SSID + "]");
        while (true) {
            bConn = connectAp(sr);
            if (bConn) {
                break;
            }
            int ii2 = ii + 1;
            if (ii > retryCount) {
                break;
            }
            ii = ii2;
        }
        return bConn;
    }

    public boolean disconnectNetwork(WifiInfo wi) {
        Logger.GG("\t\t\tDisconnecting current AP (" + wi.getSSID() + ")");
        StringBuilder sb = new StringBuilder("\t\t\t\t");
        boolean result = this.wifiManager.disconnect();
        Logger.GG(sb.append(result ? "OK" : "Failed.").toString());
        if (result) {
            Logger.GG("\t\t\tWaiting for disconnected...");
            long estimated = Utils.waitFor(5000L, new Utils.IFunc() { // from class: com.samsungimaging.filesharing.WifiConnector.3
                @Override // com.samsungimaging.filesharing.Utils.IFunc
                public boolean func() {
                    return WifiConnector.this.ni().getState() == NetworkInfo.State.DISCONNECTED;
                }
            });
            StringBuilder sb2 = new StringBuilder("\t\t\t\t");
            result = estimated < 5000;
            Logger.GG(sb2.append(result ? "OK" : "Failed").append(" during ").append(estimated / 1000.0d).append(" secs").toString());
        }
        if (result) {
            ((FileSharing) this.mContext).mSelectedSr = null;
            FileSharing.mPreconnectedSsid = null;
        }
        return result;
    }

    public boolean disconnectNetwork() {
        return disconnectNetwork(wi());
    }

    public void removeConfiguredNetworks() {
        for (WifiConfiguration wc : this.wifiManager.getConfiguredNetworks()) {
            Logger.GG("Disabling Configured Network [" + wc.SSID + "/" + wc.BSSID + "](" + wc.networkId + ")...\n\t" + (this.wifiManager.disableNetwork(wc.networkId) ? "OK" : "Failed."));
            Logger.GG("Removing Configured Network [" + wc.SSID + "/" + wc.BSSID + "](" + wc.networkId + ")...\n\t" + (this.wifiManager.removeNetwork(wc.networkId) ? "OK" : "Failed."));
        }
    }

    public boolean isConnectedSoftAp(boolean bIncludeConnecting) {
        boolean bRet = false;
        String ssid = null;
        switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[ni().getDetailedState().ordinal()]) {
            case 4:
            case 5:
                long estimated = Utils.waitFor(5000L, new Utils.IFunc() { // from class: com.samsungimaging.filesharing.WifiConnector.4
                    @Override // com.samsungimaging.filesharing.Utils.IFunc
                    public boolean func() {
                        return WifiConnector.this.ni().getDetailedState() == NetworkInfo.DetailedState.CONNECTED;
                    }
                });
                Logger.GG("Checking Soft-AP connection...");
                Logger.GG("\t\t\tWaiting for CONNECTED state...");
                StringBuilder sb = new StringBuilder("\t\t\t\t");
                bRet = estimated < 5000;
                Logger.GG(sb.append(bRet ? "OK" : "Failed").append(" during ").append(estimated / 1000.0d).append(" secs").toString());
                break;
        }
        if (this.wifiManager.isWifiEnabled() && ni().getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            Logger.GG("\tAlready Wifi Enabled.");
            ssid = getCurrentSSID();
            bRet = Utils.supportDSCPrefix(ssid);
            if (!bRet) {
                Logger.GG("\tOops. It seems that SSID[" + ssid + "] is not Soft-AP.");
            }
        } else {
            Logger.GG("\tOops. Wifi Disabled or network not connected.");
        }
        Logger.GG("\t..." + (bRet ? "Already" : "Not") + " connected to Soft-AP" + (bRet ? "[" + ssid + "]." : "."));
        if (bRet) {
            FileSharing.mPreconnectedSsid = getCurrentSSID();
        }
        return bRet;
    }

    public List<ScanResult> scanSoftAP(int retryCount) {
        long passedTimeInMillis;
        int ii = 0;
        long startTimeInMillis = System.currentTimeMillis();
        List<ScanResult> srs = Collections.synchronizedList(new ArrayList());
        Logger.GG("Scanning All APs...");
        if (!this.wifiManager.startScan()) {
            Logger.GG("\tFailed. startScan() false. (Permission? Hardware?)");
            return null;
        }
        do {
            srs.clear();
            ii++;
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Logger.d("INRY", "CM?");
            }
            passedTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
            Logger.GG("\tAttempt." + ii + " while " + (passedTimeInMillis / 1000.0d) + "secs.");
            int jj = 0;
            List<ScanResult> srsall = this.wifiManager.getScanResults();
            if (srsall == null) {
                Logger.GG("\t\tScanResults NULL. (ABNORMAL!!)");
                Logger.GG("Waiting for turning Wifi on... \n\t" + (enableWifi(true) ? "OK" : "Failed"));
            } else {
                Logger.GG("\t\tTotal " + srsall.size() + " APs (Soft AP + Others) scanned.");
                for (ScanResult sr : srsall) {
                    if (Utils.supportDSCPrefix(sr.SSID) || Utils.unsupportDSCPrefix(sr.SSID)) {
                        jj++;
                        Logger.GG("\t\tAP #" + jj + " : " + sr.SSID + " [" + sr.BSSID + "] " + sr.level + " dBm");
                        boolean exists = false;
                        for (ScanResult s : srs) {
                            if (s.SSID.equals(sr.SSID) || s.BSSID.equals(sr.BSSID)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            srs.add(sr);
                        }
                    }
                }
            }
            if (this.mIsStop || passedTimeInMillis >= 6000 || ii >= retryCount) {
                break;
            }
        } while (srs.size() == 0);
        Logger.GG("\tCompleted in " + (passedTimeInMillis / 1000.0d) + " secs.");
        return srs;
    }

    protected boolean connectAp(ScanResult sr) {
        boolean bRet = false;
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"".concat(sr.SSID).concat("\"");
        configuration.status = 1;
        configuration.priority = 40;
        if (sr.capabilities.contains("WEP")) {
            Logger.d("INRY", "APInfo ::: " + sr.capabilities + "WEP");
            configuration.allowedKeyManagement.set(0);
            configuration.allowedAuthAlgorithms.set(0);
            configuration.allowedPairwiseCiphers.set(2);
            configuration.allowedGroupCiphers.set(0);
            configuration.wepKeys[0] = "\"".concat("qwertyui").concat("\"");
            configuration.wepTxKeyIndex = 0;
        } else if (sr.capabilities.contains("WPA")) {
            Logger.d("INRY", "APInfo ::: " + sr.capabilities + "WPA");
            configuration.allowedAuthAlgorithms.set(0);
            configuration.allowedProtocols.set(1);
            configuration.allowedKeyManagement.set(1);
            configuration.allowedPairwiseCiphers.set(2);
            configuration.allowedPairwiseCiphers.set(1);
            configuration.allowedGroupCiphers.set(3);
            configuration.allowedGroupCiphers.set(2);
            configuration.preSharedKey = "\"".concat("qwertyui").concat("\"");
        } else {
            Logger.d("INRY", "APInfo ::: " + sr.capabilities + "OPEN");
            configuration.allowedKeyManagement.set(0);
            configuration.allowedProtocols.set(1);
            configuration.allowedProtocols.set(0);
            configuration.allowedAuthAlgorithms.clear();
            configuration.allowedPairwiseCiphers.set(2);
            configuration.allowedGroupCiphers.set(0);
            configuration.allowedGroupCiphers.set(1);
            configuration.allowedGroupCiphers.set(3);
            configuration.allowedGroupCiphers.set(2);
        }
        Logger.GG("Adding Network[" + sr.SSID + "]...");
        int targetNetworkId = this.wifiManager.addNetwork(configuration);
        if (targetNetworkId != -1) {
            Logger.GG("\tOK (NetworkID:" + targetNetworkId + ")");
            Logger.GG("\tEnabling Network[" + sr.SSID + "]...");
            bRet = this.wifiManager.enableNetwork(targetNetworkId, true);
            if (bRet) {
                Logger.GG(HTTP.TAB + (bRet ? "OK" : "Failed"));
                dumpNetworkInfo();
                Logger.GG("\t\t\tWaiting for connection...");
                long estimated = Utils.waitFor(5000L, new Utils.IFunc() { // from class: com.samsungimaging.filesharing.WifiConnector.5
                    @Override // com.samsungimaging.filesharing.Utils.IFunc
                    public boolean func() {
                        return WifiConnector.this.ni().getState() == NetworkInfo.State.CONNECTED;
                    }
                });
                StringBuilder sb = new StringBuilder("\t\t\t\t");
                bRet = estimated < 5000;
                Logger.GG(sb.append(bRet ? "OK" : "Failed").append(" during ").append(estimated / 1000.0d).append(" secs").toString());
                dumpNetworkInfo();
            }
        } else {
            Logger.GG("\tFailed.");
        }
        return bRet;
    }
}

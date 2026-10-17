package com.samsungimaging.connectionmanager.app.cm.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeServer;
import com.samsungimaging.connectionmanager.app.cm.Main;
import com.samsungimaging.connectionmanager.app.cm.broadcastreceiver.CMNetworkStateChangeReceiver;
import com.samsungimaging.connectionmanager.app.cm.broadcastreceiver.CMScanResultReceiver;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.connector.CMCameraAPConnector;
import com.samsungimaging.connectionmanager.app.cm.connector.CMEnableAllApsAfterConnecting;
import com.samsungimaging.connectionmanager.app.cm.modemanager.CMModeManager;
import com.samsungimaging.connectionmanager.app.cm.notimanager.CMNotificationManager;
import com.samsungimaging.connectionmanager.app.localgallery.LocalGallery;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Const;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter;
import com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare;
import com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush;
import com.samsungimaging.connectionmanager.util.MyLocalGallery;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CMService extends Service {
    public static Context mContext = null;
    public static WifiManager mWifiManager = null;
    public static ConnectivityManager mConnectivityManager = null;
    public static boolean IS_WIFI_CONNECTED = false;
    public static boolean IS_MODE_CONNECTED = false;
    public static boolean AUTOSHARE_AUTOSEARCH = false;
    public static int ACTIVE_SERVICE = -1;
    private static CMService s_obj = null;
    public static IModeClient mIModeClientDummy = new IModeClient() { // from class: com.samsungimaging.connectionmanager.app.cm.service.CMService.1
        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
        public void runSubApplication(int msg) {
        }

        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
        public void runByebye() {
        }
    };
    private WifiManager.WifiLock mWifiLock = null;
    private CMNetworkStateChangeReceiver mCMNetworkStateChangeReceiver = null;
    private CMScanResultReceiver mCMScanResultReceiver = null;
    private boolean mIsCMScanResultReceiverRegistered = false;
    private boolean mIsCMNetworkStateChangeReceiverRegistered = false;
    private boolean mWifiScanStop = true;
    public int mConnectionTryCount = 0;
    private ActivityManager mActivityManager = null;
    private IModeServer mIModeServer = new IModeServer() { // from class: com.samsungimaging.connectionmanager.app.cm.service.CMService.2
        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeServer
        public void runSubApplication(int mode) {
            Trace.d(CMConstants.TAG_NAME, "CMService, IModeServer, runSubApplication() => " + mode);
            if (mode == 20) {
                Trace.d(CMConstants.TAG_NAME, "CMService, IModeServer, runSubApplication(), ClientÃªÂ°â‚¬ Ã¬â€¢Å’ Ã¬Ë†Ëœ Ã¬â€”â€ Ã«Å â€� Ã¬Å¡â€�Ã¬Â²Â\u00adÃ¬ï¿½â€ž Ã\u00adâ€¢ËœÃ¬Ëœâ‚¬Ã¬Å ÂµÃ«â€¹Ë†Ã«â€¹Â¤.");
            } else if (mode == 7) {
                Trace.d(CMConstants.TAG_NAME, "CMService, IModeServer, runSubApplication(), ClientÃªÂ°â‚¬ IDLEÃ¬ï¿½â€ž Ã¬â€¹Â¤Ã\u00adâ€“â€°Ã\u00adâ€¢ËœÃ«ï¿½Â¼ÃªÂ³Â  Ã¬Å¡â€�Ã¬Â²Â\u00adÃ\u00adâ€“Ë†Ã¬Å ÂµÃ«â€¹Ë†Ã«â€¹Â¤.");
            } else {
                Trace.d(CMConstants.TAG_NAME, "CMService, IModeServer, runSubApplication(), ClientÃªÂ°â‚¬ " + mode + "Ã«Â¥Â¼ Ã¬â€¹Â¤Ã\u00adâ€“â€°Ã\u00adâ€¢ËœÃ«ï¿½Â¼ÃªÂ³Â  Ã¬Å¡â€�Ã¬Â²Â\u00adÃ\u00adâ€“Ë†Ã¬Å ÂµÃ«â€¹Ë†Ã«â€¹Â¤.");
                CMService.this.startSubApplication(mode);
            }
        }

        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeServer
        public void runByebye() {
            Trace.d(CMConstants.TAG_NAME, "CMService, IModeServer, runByebye()");
            CMUtil.sendBroadCastToMain(CMService.mContext, CMConstants.EXTRA_VALUE_SHOW_STATUS_BAR);
            CMNotificationManager.getInstance().notifyStatusChange(false, null, CMService.this.getResources().getString(R.string.cm_noti_msg_is_disconnected).toString(), R.string.cm_noti_ongoing);
            CMService.this.finishSafe();
        }
    };
    private Handler mWifiScanHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.cm.service.CMService.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Trace.d(CMConstants.TAG_NAME, "WifiScanHandler start, IS_CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", mWifiScanStop = " + CMService.this.mWifiScanStop + ", ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", IsNFCLaunchFlag() = " + CMInfo.getInstance().getIsNFCLaunchFlag());
            if (CMService.mWifiManager == null) {
                CMService.mWifiManager = (WifiManager) CMService.this.getApplicationContext().getSystemService("wifi");
            }
            if (!CMService.IS_WIFI_CONNECTED && !CMService.this.mWifiScanStop && CMUtil.isAvailableScan(CMService.mContext, CMService.mWifiManager)) {
                Trace.d(CMConstants.TAG_NAME, "startScan");
                CMService.mWifiManager.startScan();
            }
            CMService.this.mWifiScanHandler.sendEmptyMessageDelayed(0, 4000L);
        }
    };
    private boolean beforeFinishFlag = false;
    private List<ScanResult> mCameraSrs = new ArrayList();

    public static synchronized CMService getInstance() {
        if (s_obj == null) {
            Trace.d(CMConstants.TAG_NAME, "s_obj is null!");
            s_obj = new CMService();
        }
        return s_obj;
    }

    public WifiManager.WifiLock getCMWifiLock() {
        return this.mWifiLock;
    }

    public void forceClosed() {
        CMModeManager.runModeClient(mIModeClientDummy, true);
        getInstance().appclose();
    }

    public boolean isSubAppAlive() {
        Trace.d(CMConstants.TAG_NAME, "CMService, isSubAppAlive, ACTIVE_SERVICE = " + ACTIVE_SERVICE);
        switch (ACTIVE_SERVICE) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public void wifiScanStop() {
        this.mConnectionTryCount = 0;
        this.mWifiScanStop = true;
        CMCameraAPConnector.getInstance().cancel();
    }

    public void wifiScanStart() {
        this.mWifiScanStop = false;
    }

    public boolean isWifiScanStop() {
        return this.mWifiScanStop;
    }

    public void runModeServer() {
        CMModeManager.runModeServer(this.mIModeServer);
    }

    public void stopModeServer() {
        CMModeManager.removeModeServerListener(this.mIModeServer);
        CMModeManager.stopModeServer();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Trace.d(CMConstants.TAG_NAME, "CMService : onCreate");
        s_obj = this;
        mContext = getApplicationContext();
        this.mActivityManager = (ActivityManager) getSystemService("activity");
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext.getSystemService("wifi");
        }
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) mContext.getSystemService("connectivity");
        }
        String atFirstWifiEnabled = CMSharedPreferenceUtil.getString(mContext, CMConstants.ATFIRST_WIFIENABLED, "");
        if (atFirstWifiEnabled.equals("")) {
            if (mWifiManager.getWifiState() == 3 || mWifiManager.getWifiState() == 2) {
                CMSharedPreferenceUtil.put(mContext, CMConstants.ATFIRST_WIFIENABLED, "true");
            } else {
                CMSharedPreferenceUtil.put(mContext, CMConstants.ATFIRST_WIFIENABLED, "false");
            }
        }
        try {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        } catch (Throwable th) {
        }
        if (!isSubAppAlive()) {
            handleCommand();
            Trace.d(CMConstants.TAG_NAME, "CMService : registerBroadcastReceiver");
            registerBroadcastReceiver();
            this.mWifiScanHandler.sendEmptyMessage(1);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return 2;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleCommand() {
        Trace.d(CMConstants.TAG_NAME, "CMService : handlecommand");
        CMSharedPreferenceUtil.put(getApplicationContext(), CMConstants.SP_KEY_IS_RECEIVER_RUNNING, "true");
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService("connectivity");
        connectivityManager.setNetworkPreference(1);
        this.mWifiLock = mWifiManager.createWifiLock(1, "CM_LOCK");
        this.mWifiLock.setReferenceCounted(true);
        this.mWifiLock.acquire();
        Trace.d(CMConstants.TAG_NAME, "CMService : WIFI_MODE_FULL");
        CMEnableAllApsAfterConnecting.enalbleAllAps(mContext);
        Trace.d(CMConstants.TAG_NAME, "CMService : is restareted.......................");
    }

    private void stopService(Context context) {
        context.stopService(new Intent(context, (Class<?>) CMService.class));
    }

    public void NFCConnectionTryCancel() {
        if (CMInfo.getInstance().getIsNFCLaunchFlag()) {
            CMSharedPreferenceUtil.remove(mContext, CMConstants.PREF_NFC_SSID);
            CMInfo.getInstance().setIsNFCLaunch(false, "");
        }
    }

    public void beforefinish(int type) {
        if (!this.beforeFinishFlag) {
            this.beforeFinishFlag = true;
            String connected_ssid = mWifiManager.getConnectionInfo().getSSID();
            boolean old = CMUtil.checkOldVersionSmartCameraApp(connected_ssid);
            boolean isConnected = isWifiConneced();
            Trace.d(CMConstants.TAG_NAME, "CMService, beforefinish, isConnected = " + isConnected + ", type = " + type);
            CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_SHOW_STATUS_BAR);
            if (isConnected) {
                if (old || ACTIVE_SERVICE == 5) {
                    CMUtil.disableConnectedCamera(mWifiManager);
                    CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
                    IS_WIFI_CONNECTED = false;
                    IS_MODE_CONNECTED = false;
                } else if (type == 1) {
                    CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
                    CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES, CMConstants.WIFI_SCAN_TIMER_FOR_AUTOSHARE_AUTOSEARCH);
                } else if (type == 2) {
                    Main.getInstance().sendByebyeForLowBattery();
                } else {
                    CMNotificationManager.getInstance().notifyStatusChange(true, connected_ssid, getResources().getString(R.string.cm_noti_msg_is_connected).toString(), R.string.cm_noti_ongoing);
                }
            } else if (type == 1) {
                CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
                CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES, CMConstants.WIFI_SCAN_TIMER_FOR_AUTOSHARE_AUTOSEARCH);
            } else {
                IS_WIFI_CONNECTED = false;
                IS_MODE_CONNECTED = false;
                CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED);
                CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
            }
            ACTIVE_SERVICE = 0;
            CMSharedPreferenceUtil.remove(mContext, CMConstants.PREF_NFC_SSID);
            CMInfo.getInstance().setIsNFCLaunch(false, "");
            String top = CMUtil.whatIsTopActivity(mContext);
            boolean istop = CMUtil.isCMTopActivity(mContext);
            Trace.d(CMConstants.TAG_NAME, "CMService, beforefinish, top = " + top + ", istop = " + istop);
            Intent intent = new Intent(mContext, (Class<?>) Main.class);
            intent.addFlags(872415232);
            startActivity(intent);
        }
    }

    public void finishSafe() {
        CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_APP_CLOSE_SENDING_BYEBYE);
    }

    public void appclose() {
        CMModeManager.stopModeServer();
        CMModeManager.stopModeClient();
        if (this.mWifiScanHandler != null && this.mWifiScanHandler.hasMessages(0)) {
            this.mWifiScanHandler.removeMessages(0);
        }
        String atFirstWifiEnabled = CMSharedPreferenceUtil.getString(mContext, CMConstants.ATFIRST_WIFIENABLED, "");
        if (!atFirstWifiEnabled.equals("")) {
            if (atFirstWifiEnabled.equals("true")) {
                mWifiManager.setWifiEnabled(true);
            } else if (atFirstWifiEnabled.equals("false")) {
                mWifiManager.setWifiEnabled(false);
            }
            CMSharedPreferenceUtil.put(mContext, CMConstants.ATFIRST_WIFIENABLED, "");
        }
        CMCameraAPConnector.getInstance().cancel();
        unRegisterBroadcastReceiver();
        CMEnableAllApsAfterConnecting.enalbleAllAps(mContext);
        CMUtil.downPriority(mWifiManager);
        CMUtil.disalbleCameraAps(mContext, null);
        CMSharedPreferenceUtil.remove(mContext, CMConstants.PREF_NFC_SSID);
        CMInfo.getInstance().setIsNFCLaunch(false, "");
        if (this.mWifiLock != null && this.mWifiLock.isHeld()) {
            this.mWifiLock.release();
        }
        CMNotificationManager.getInstance().cancelAllNoti();
        stopService(mContext);
        Trace.d(CMConstants.TAG_NAME, "CMService.stopService!");
        Trace.d(CMConstants.TAG_NAME, "CMService : is finished.....");
        if (Main.getInstance() != null) {
            Main.getInstance().finish();
        }
        IS_WIFI_CONNECTED = false;
        IS_MODE_CONNECTED = false;
        ACTIVE_SERVICE = -1;
        s_obj = null;
    }

    public void goToHome() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        startActivity(intent);
    }

    private void registerBroadcastReceiver() {
        if (this.mCMScanResultReceiver == null) {
            this.mCMScanResultReceiver = new CMScanResultReceiver();
            IntentFilter filter_for_CMScanResultReceiver = new IntentFilter();
            filter_for_CMScanResultReceiver.addAction("android.net.wifi.SCAN_RESULTS");
            registerReceiver(this.mCMScanResultReceiver, filter_for_CMScanResultReceiver);
            this.mIsCMScanResultReceiverRegistered = true;
        }
        if (this.mCMNetworkStateChangeReceiver == null) {
            this.mCMNetworkStateChangeReceiver = new CMNetworkStateChangeReceiver();
            IntentFilter filter_for_CMNetworkStateChangeReceiver = new IntentFilter();
            filter_for_CMNetworkStateChangeReceiver.addAction("android.net.wifi.STATE_CHANGE");
            filter_for_CMNetworkStateChangeReceiver.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter_for_CMNetworkStateChangeReceiver.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
            filter_for_CMNetworkStateChangeReceiver.addAction("android.intent.action.LOCALE_CHANGED");
            filter_for_CMNetworkStateChangeReceiver.addAction("android.net.wifi.supplicant.STATE_CHANGE");
            registerReceiver(this.mCMNetworkStateChangeReceiver, filter_for_CMNetworkStateChangeReceiver);
            this.mIsCMNetworkStateChangeReceiverRegistered = true;
        }
    }

    private void unRegisterBroadcastReceiver() {
        CMSharedPreferenceUtil.put(mContext, CMConstants.SP_KEY_IS_RECEIVER_RUNNING, "false");
        int apiLevel = Integer.parseInt(Build.VERSION.SDK);
        Trace.d(CMConstants.TAG_NAME, "unRegisterBroadcastReceiver, apiLevel = " + apiLevel);
        if (apiLevel >= 7) {
            try {
                if (this.mCMScanResultReceiver != null && this.mIsCMScanResultReceiverRegistered) {
                    mContext.unregisterReceiver(this.mCMScanResultReceiver);
                    this.mIsCMScanResultReceiverRegistered = false;
                }
                if (this.mCMNetworkStateChangeReceiver != null && this.mIsCMNetworkStateChangeReceiverRegistered) {
                    mContext.unregisterReceiver(this.mCMNetworkStateChangeReceiver);
                    this.mIsCMNetworkStateChangeReceiverRegistered = false;
                    return;
                }
                return;
            } catch (IllegalArgumentException e) {
                Trace.d(CMConstants.TAG_NAME, "CMService.appclose, unregisterReceiver, IllegalArgumentException!!!");
                return;
            }
        }
        if (this.mCMScanResultReceiver != null && this.mIsCMScanResultReceiverRegistered) {
            unregisterReceiver(this.mCMScanResultReceiver);
            this.mIsCMScanResultReceiverRegistered = false;
        }
        if (this.mCMNetworkStateChangeReceiver != null && this.mIsCMNetworkStateChangeReceiverRegistered) {
            unregisterReceiver(this.mCMNetworkStateChangeReceiver);
            this.mIsCMNetworkStateChangeReceiverRegistered = false;
        }
    }

    public void setScannedList(List<ScanResult> srs) {
        if (srs != null) {
            this.mCameraSrs = srs;
        }
    }

    public List<ScanResult> getScannedList() {
        return this.mCameraSrs;
    }

    public void checkCurrentWifiConnection() {
        boolean z = false;
        WifiInfo info = mWifiManager.getConnectionInfo();
        boolean isConnected = isWifiConneced();
        if (isConnected) {
            if (CMUtil.supportDSCPrefix(info.getSSID()) && CMUtil.isManagedSSID(mContext, info.getSSID())) {
                z = true;
            }
            IS_WIFI_CONNECTED = z;
            if (IS_WIFI_CONNECTED) {
                checkSubApplicationType();
                return;
            }
            return;
        }
        IS_WIFI_CONNECTED = false;
    }

    private boolean isWifiConneced() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        Trace.d(CMConstants.TAG_NAME, "isWifiConneced, state : " + state + ", ssid : " + info.getSSID() + ", networkInfo = " + networkInfo);
        if (networkInfo != null) {
            NetworkInfo.DetailedState state_connectivitymanager = networkInfo.getDetailedState();
            String typeName = networkInfo.getTypeName();
            boolean available = networkInfo.isAvailable();
            if (state_connectivitymanager != NetworkInfo.DetailedState.CONNECTED || !"WIFI".equals(typeName) || !available) {
                return false;
            }
            return true;
        }
        if (state != NetworkInfo.DetailedState.CONNECTED) {
            return false;
        }
        return true;
    }

    public void checkSubApplicationType() {
        String connected_ssid = mWifiManager.getConnectionInfo().getSSID();
        CMInfo.getInstance().setConnectedSSID(connected_ssid);
        if (0 != 0) {
            if (!isSubAppAlive()) {
                CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_RUN_MODEMANAGER);
                return;
            }
            return;
        }
        if (!isSubAppAlive()) {
            int appType = CMUtil.getAppTypeFromCamera(mWifiManager);
            if (appType == 8) {
                CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_RUN_MODEMANAGER);
                return;
            }
            Trace.d(CMConstants.TAG_NAME, "checkSubApplicationType, Build.MODEL = " + Build.MODEL);
            if (CMUtil.lateDHCPRefresh(Build.MODEL)) {
                Trace.d(CMConstants.TAG_NAME, "Performance Check Point : Start sleep for updating DHCP info");
                if (Build.MODEL.contains("LG-F240")) {
                    DhcpInfo di = mWifiManager.getDhcpInfo();
                    if (di.leaseDuration <= 60) {
                        Trace.d(CMConstants.TAG_NAME, "DHCP Lease time is " + di.leaseDuration);
                        mWifiManager.reassociate();
                    }
                }
                SystemClock.sleep(8000L);
                Trace.d(CMConstants.TAG_NAME, "Performance Check Point : End sleep for updating DHCP info");
                appType = CMUtil.getAppTypeFromCamera(mWifiManager);
            }
            if (CMUtil.isGalaxySeriesPrefix(connected_ssid)) {
                if (1 <= appType && appType <= 5) {
                    startSubApplication(appType);
                    return;
                } else {
                    startSubApplication(2);
                    return;
                }
            }
            startSubApplication(appType);
            return;
        }
        startSubApplication(ACTIVE_SERVICE);
    }

    public void startSubApplication(int appType) {
        String app;
        String connected_ssid = mWifiManager.getConnectionInfo().getSSID();
        boolean old = CMUtil.checkOldVersionSmartCameraApp(connected_ssid);
        Trace.d(CMConstants.TAG_NAME, "startSubApplication, connected ssid = " + connected_ssid + ", old = " + old);
        Intent intent = null;
        if (appType == 1) {
            ACTIVE_SERVICE = 1;
            if (MyLocalGallery.isActivityCreated()) {
                Log.d("local_exit", "local existed");
                intent = new Intent(mContext, (Class<?>) LocalGallery.class);
            } else if (old) {
                intent = new Intent(mContext, (Class<?>) FileSharing.class);
            } else {
                intent = new Intent(mContext, (Class<?>) FileSharing.class);
                intent.putExtra(CMConstants.INTENT_EXTRA_NAME_DEFAULT_URL_FOR_ML_RVF, CMInfo.getInstance().getDescriptionURL());
            }
            app = Const.Config.FUNCTION_NAME;
        } else if (appType == 2) {
            ACTIVE_SERVICE = 2;
            if (MyLocalGallery.isActivityCreated()) {
                Log.d("local_exit", "local existed");
                intent = new Intent(mContext, (Class<?>) LocalGallery.class);
            } else if (old) {
                intent = new Intent(mContext, (Class<?>) LiveShutter.class);
            } else {
                intent = new Intent(mContext, (Class<?>) LiveShutter.class);
                intent.putExtra(CMConstants.INTENT_EXTRA_NAME_DEFAULT_URL_FOR_ML_RVF, CMInfo.getInstance().getDescriptionURL());
            }
            app = "RVF";
        } else if (appType == 3) {
            ACTIVE_SERVICE = 3;
            if (old) {
                intent = new Intent(mContext, (Class<?>) AutoShare.class);
            } else {
                intent = new Intent(mContext, (Class<?>) AutoShare.class);
            }
            app = "S2L";
        } else if (appType == 4) {
            ACTIVE_SERVICE = 4;
            if (old) {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            } else {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            }
            intent.putExtra(CMConstants.INTENT_EXTRA_NAME_SELECTIVEPUSH_SERVICE_TYPE, CMConstants.MsgString.SELECTIVEPUSH_STRING);
            app = "SP";
        } else if (appType == 5) {
            ACTIVE_SERVICE = 5;
            if (old) {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            } else {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            }
            intent.putExtra(CMConstants.INTENT_EXTRA_NAME_SELECTIVEPUSH_SERVICE_TYPE, CMConstants.MsgString.GROUPSHARE_STRING);
            app = "GS";
        } else if (appType == 6) {
            ACTIVE_SERVICE = 6;
            if (old) {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            } else {
                intent = new Intent(mContext, (Class<?>) SelectivePush.class);
            }
            intent.putExtra(CMConstants.INTENT_EXTRA_NAME_SELECTIVEPUSH_SERVICE_TYPE, CMConstants.MsgString.MOBILEBACKUP_STRING);
            app = "AUTOBACKUP";
        } else if (appType == 7) {
            app = "IDLE";
        } else {
            Trace.d(CMConstants.TAG_NAME, "startSubApplication error, Ã\u00adËœÂ¸Ã¬Â¶Å“Ã\u00adâ€¢Â  Ã\u00adâ€¢ËœÃ¬Å“â€žÃ¬â€¢Â±Ã¬ï¿½â€ž Ã\u00adÅ’ï¿½Ã«â€¹Â¨Ã\u00adâ€¢Â  Ã¬Ë†Ëœ Ã¬â€”â€ Ã¬Å ÂµÃ«â€¹Ë†Ã«â€¹Â¤.");
            app = "error";
            CMUtil.disableConnectedCamera(mWifiManager);
            IS_WIFI_CONNECTED = false;
        }
        Trace.d(CMConstants.TAG_NAME, "Ã\u00adâ€¢ËœÃ¬Å“â€žÃ¬â€¢Â± Ã\u00adâ„¢â€¢Ã¬ï¿½Â¸ ÃªÂ²Â°ÃªÂ³Â¼, " + app + "Ã¬ï¿½Â´(ÃªÂ°â‚¬) Ã¬â€¹Â¤Ã\u00adâ€“â€°Ã«ï¿½Â©Ã«â€¹Ë†Ã«â€¹Â¤.");
        CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_STOP_NFC_TAGGING_TIMER);
        if (intent != null && mWifiManager.isWifiEnabled()) {
            intent.addFlags(872415232);
            Trace.d(CMConstants.TAG_NAME, "Performance Check Point : Start App : " + appType);
            if ((appType == 3 || appType == 4 || appType == 5 || appType == 6) && getInstance() != null) {
                WifiManager.WifiLock mWifiLock = getInstance().getCMWifiLock();
                if (mWifiLock != null && mWifiLock.isHeld()) {
                    mWifiLock.release();
                }
                WifiManager.WifiLock mWifiLock2 = mWifiManager.createWifiLock(3, "CM_LOCK");
                mWifiLock2.setReferenceCounted(true);
                mWifiLock2.acquire();
                Trace.d(CMConstants.TAG_NAME, "startAppAfterConnectingAp : WIFI_MODE_FULL_HIGH_PERF");
            }
            CMUtil.sendBroadCastToMain(mContext, CMConstants.EXTRA_VALUE_SHOW_STATUS_BAR);
            CMNotificationManager.getInstance().notifyStatusChange(true, connected_ssid, mContext.getResources().getString(R.string.cm_noti_msg_is_connected).toString(), R.string.cm_noti_ongoing);
            this.beforeFinishFlag = false;
            CMCameraAPConnector.getInstance().cancel();
            mContext.startActivity(intent);
            if (Main.getInstance() != null) {
                Main.getInstance().dismissAllDialog();
            }
        }
    }
}

package com.samsungimaging.connectionmanager.app.cm.common;

import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public interface CMConstants {
    public static final String ATFIRST_WIFIENABLED = "com.samsungimaging.connectionmanager.ATFIRST_WIFIENABLED";
    public static final String AUTO_SEARCH_BUTTON_ON_OFF = "com.samsungimaging.connectionmanager.AUTO_SEARCH_BUTTON_ON_OFF";
    public static final String CLASS_NAME_CM_ACTIVITY = "com.samsungimaging.connectionmanager.app.cm.Main";
    public static final String CLASS_NAME_ML_ACTIVITY = "com.samsungimaging.connectionmanager.app.pullservice.MobileLink";
    public static final String CLASS_NAME_RVF_ACTIVITY = "com.samsungimaging.connectionmanager.app.pullservice.RemoteViewFinder";
    public static final String CLASS_NAME_S2L_ACTIVITY = "com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare";
    public static final String CLASS_NAME_SP_ACTIVITY = "com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush";
    public static final String EXTRA_KEY2_FROM_CM = "EXTRA_KEY2_FROM_CM";
    public static final String EXTRA_KEY3_FROM_CM = "EXTRA_KEY3_FROM_CM";
    public static final String EXTRA_KEY_FROM_CM = "EXTRA_KEY_FROM_CM";
    public static final String EXTRA_VALUE_APP_CLOSE_SENDING_BYEBYE = "APP_CLOSE_SENDING_BYEBYE";
    public static final String EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR = "CHECK_FOR_401_ERROR";
    public static final String EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR = "CHECK_FOR_503_ERROR";
    public static final String EXTRA_VALUE_CHECKED_THE_CHECK_FOR_INTERNET_SERVICE_MENU_IN_SETTINGS = "CHECK_FOR_INTERNET_SERVICE_IN_SETTINGS";
    public static final String EXTRA_VALUE_CHECKED_THE_CHECK_FOR_MODECLIENT_NOTRESPONSE_OR_CANCELED = "CHECK_FOR_MODECLIENT_NOTRESPONSE_OR_CANCELED";
    public static final String EXTRA_VALUE_CHECKED_THE_CHECK_FOR_MODECLIENT_UNKNOWN_ERROR = "CHECK_FOR_MODECLIENT_UNKNOWN_ERROR";
    public static final String EXTRA_VALUE_NEED_PASSWORD = "NEED_PASSWORD";
    public static final String EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED = "WIFI_DISCONNECTED";
    public static final String EXTRA_VALUE_RUN_MODEMANAGER = "RUN_MODEMANAGER";
    public static final String EXTRA_VALUE_SHOW_HELPDIALOG = "SHOW_HELPDIALOG";
    public static final String EXTRA_VALUE_SHOW_NOT_SUPPORTED_DIALOG = "SHOW_NOT_SUPPORTED_DIALOG";
    public static final String EXTRA_VALUE_SHOW_SEARCHAPDIALOG = "SHOW_SEARCHAPDIALOG";
    public static final String EXTRA_VALUE_SHOW_STATUS_BAR = "SHOW_STATUS_BAR";
    public static final String EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES = "START_WIFI_SCAN_TIMER_FOR_MIMUTES";
    public static final String EXTRA_VALUE_STOP_NFC_TAGGING_TIMER = "STOP_NFC_TAGGING_TIMER";
    public static final String EXTRA_VALUE_STOP_WIFI_SCAN_TIMER_FOR_MIMUTES = "STOP_WIFI_SCAN_TIMER_FOR_MIMUTES";
    public static final int INIT_GUIDE_DIALOG_TIMER = 500;
    public static final String INTENT_EXTRA_NAME_DEFAULT_URL_FOR_ML_RVF = "default_url_for_ml_rvf";
    public static final String INTENT_EXTRA_NAME_SELECTIVEPUSH_SERVICE_TYPE = "selectivepush_service_type";
    public static final String INTENT_FROM_CM = "com.samsungimaging.connectionmanager.intent.FROM_CM";
    public static final String MANAGEDLIST_MODE = "managedlistmode";
    public static final int MAX_COUNT_CAMERA_AP_SEARCH = 5;
    public static final int MAX_COUNT_FOR_AP_SEARCH_PER_CAMERA_AP = 5;
    public static final int MAX_COUNT_FOR_SHOW_CONN_FAIL_TOAST = 3;
    public static final int MIN_FINISH_APP_INTERVAL_SECONDS = 5;
    public static final double MIN_WIFI_SCAN_INTERVAL_SECONDS = 1.0d;
    public static final int MODECLIENT_MAX_TRY_COUNT = 16;
    public static final int MODECLIENT_SOCKET_TIMEOUT = 3000;
    public static final String MODESERVER_PORT = "com.samsungimaging.connectionmanager.MODESERVER_PORT";
    public static final int NFC_TAGGING_TIMER = 20000;
    public static final String PACKAGE_FULL_NAME = "com.samsungimaging.connectionmanager";
    public static final String PREF_NFC_SSID = "pref_nfc_ssid";
    public static final String SCANNEDLIST_MODE = "scannedlistmode";
    public static final String SELECTED_MANAGED_CAMERA_FOR_REMOVEPAIRED_DIALOG_SSID = "com.samsungimaging.connectionmanager.SELECTED_MANAGED_CAMERA_FOR_REMOVEPAIRED_DIALOG_SSID";
    public static final String SP_KEY_COUNT_FOR_AP_SEARCH = "com.samsungimaging.connectionmanager.SP_KEY_COUNT_FOR_AP_SEARCH";
    public static final String SP_KEY_IS_RECEIVER_RUNNING = "com.samsungimaging.connectionmanager.SP_KEY_IS_RECEIVER_RUNNING";
    public static final String SP_KEY_SHOW_INIT_GUIDE_DAILOG = "com.samsungimaging.connectionmanager.SP_KEY_SHOW_INIT_GUIDE_DAILOG";
    public static final String SP_KEY_SHOW_INIT_NFC_GUIDE_DAILOG = "com.samsungimaging.connectionmanager.SP_KEY_SHOW_INIT_NFC_GUIDE_DAILOG";
    public static final Trace.Tag TAG_NAME = Trace.Tag.CM;
    public static final String TESTMODE = "com.samsungimaging.connectionmanager.TESTMODE";
    public static final String TESTMODE_SSID = "com.samsungimaging.connectionmanager.TESTMODE_SSID";
    public static final String TESTMODE_SSID2 = "com.samsungimaging.connectionmanager.TESTMODE_SSID2";
    public static final String TESTMODE_SSID3 = "com.samsungimaging.connectionmanager.TESTMODE_SSID3";
    public static final int WIFI_SCAN_TIMER = 120000;
    public static final int WIFI_SCAN_TIMER_FOR_AUTOSHARE_AUTOSEARCH = 600000;

    public static class BeforeFinishType {
        public static final int AUTOSEARCH = 1;
        public static final int LOWBATTERY = 2;
        public static final int NORMAL = 0;
    }

    public static class MsgId {
        public static final int MSG_200OK = 24;
        public static final int MSG_BYEBYE = 23;
        public static final int MSG_MODECLIENT_401_ERROR = 21;
        public static final int MSG_MODECLIENT_503_ERROR = 22;
        public static final int MSG_MODECLIENT_NOTRESPONSE_OR_CANCELED = 0;
        public static final int MSG_MODESERVER_UNKNOWN_ERROR = 20;
        public static final int MSG_NEED_PASSWORD = 100;
        public static final int MSG_RUN_GS = 5;
        public static final int MSG_RUN_IDLE = 7;
        public static final int MSG_RUN_ML = 1;
        public static final int MSG_RUN_MOBILEBACKUP = 6;
        public static final int MSG_RUN_RVF = 2;
        public static final int MSG_RUN_S2L = 3;
        public static final int MSG_RUN_SP = 4;
    }

    public static class MsgString {
        public static final String AUTOSHARE = "<currentmode>autoshare</currentmode>";
        public static final String AUTOSHARE_STRING = "autoshare";
        public static final String BYEBYE = "byebye";
        public static final String ERROR401 = "HTTP/1.1 401";
        public static final String ERROR503 = "HTTP/1.1 503";
        public static final String GROUPSHARE = "<currentmode>groupshare</currentmode>";
        public static final String GROUPSHARE_STRING = "groupshare";
        public static final String IDLE = "<currentmode>idle</currentmode>";
        public static final String IDLE_STRING = "idle";
        public static final String MOBILEBACKUP = "<currentmode>mobilebackup</currentmode>";
        public static final String MOBILEBACKUP_STRING = "mobilebackup";
        public static final String MOBILELINK = "<currentmode>mobilelink</currentmode>";
        public static final String MOBILELINK_STRING = "mobilelink";
        public static final String REMOTEVIEWFINDER = "<currentmode>rvf</currentmode>";
        public static final String REMOTEVIEWFINDER_STRING = "rvf";
        public static final String SAMSUNGMODESERVER = "samsung mode server";
        public static final String SELECTIVEPUSH = "<currentmode>selectivepush</currentmode>";
        public static final String SELECTIVEPUSH_STRING = "selectivepush";
    }

    public static class SubAppType {
        public static final int APP_CM = 0;
        public static final int APP_GS = 5;
        public static final int APP_IDLE = 7;
        public static final int APP_ML = 1;
        public static final int APP_MOBILEBACKUP = 6;
        public static final int APP_RVF = 2;
        public static final int APP_S2L = 3;
        public static final int APP_SP = 4;
        public static final int MODE_CLIENT = 8;
    }
}

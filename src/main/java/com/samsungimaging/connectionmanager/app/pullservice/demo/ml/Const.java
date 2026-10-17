package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

/* JADX INFO: loaded from: classes.dex */
public class Const {

    public static class ActivityResultId {
        public static final int ID_WIFI_SETTING = 101;
        public static final int REQUEST_GALLERY = 100;
    }

    public static class BoroadcastAction {
        public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    }

    public static class Config {
        public static final long CRITICAL_SD_MEMORY = 10000000;
        public static int DUMMY_WIFI_CONNECT_TYPE = 1;
        public static final String FUNCTION_NAME = "ML";
        public static final int MAX_SELECT_CHECKBOX = 1000;
        public static final int NUM_OF_ITEMS_AT_FIRST = 100;
        public static final int NUM_OF_REQUEST_ITEM = 100;
        public static final int NUM_OF_TOTALITEMS = 1000;
        public static final int TIMEOUT_OF_CONNECT = 180000;
        public static final String USER_AGENT_PREFIX = "SEC_RVF_ML_";
        public static final boolean USE_SELECT_WIFI_POPUP = false;
        public static final boolean USE_WIFI_CAM_CHECKER = false;
        public static final int WIFI_CAM_CHECKER_RETRY_COUNT = 3;
        public static final boolean WIFI_DISABLE_ACTION = false;
    }

    public static class Extras {
        public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    }

    public static class GridItem {
        public static final int STATE_CHECKED = 2;
        public static final int STATE_DOWNLOADED = 3;
        public static final int STATE_NOT_CHECKED = 1;
        public static final int STATE_NOT_SUPPORTED_MEDIA = 4;
    }

    public static class MsgBoxId {
        public static final int DIALOG_ID_GUIDE_INIT = 1013;
        public static final int MSGBOX_AP_FAIL = 1004;
        public static final int MSGBOX_CONNGUIDE_DETAIL_VIEW = 1011;
        public static final int MSGBOX_COPY_COMPLETE = 1008;
        public static final int MSGBOX_DSC_DOWN = 1002;
        public static final int MSGBOX_GUIDE_DETAIL_VIEW = 1009;
        public static final int MSGBOX_MEMORYFULL = 1006;
        public static final int MSGBOX_MEMORYFULL_NOT_EXIT = 1016;
        public static final int MSGBOX_NEED_CAM_WIFI = 1010;
        public static final int MSGBOX_NETWORK_DISCONNECT = 1000;
        public static final int MSGBOX_NO_RESPONSE_FROM_CAMERA = 1015;
        public static final int MSGBOX_REMOVED_SDCARD = 1005;
        public static final int MSGBOX_TRANSMISSION_FAIL = 1014;
        public static final int MSGBOX_WAIT_CONNECTION = 1012;
        public static final int MSGBOX_WIFI_SETTING = 1007;
        public static final int PROGRESS_BAR_DISPLAY_EXIT = 1003;
    }

    public static class MsgId {
        public static final int APP_CLOSE_FOR_DSC_ERROR = 23;
        public static final int APP_CLOSE_FOR_MEMORY_FULL = 21;
        public static final int APP_CLOSE_FOR_REMOVED_SD = 22;
        public static final int APP_FINISH = 32;
        public static final int AP_CHECK_COMPLETED = 9;
        public static final int AP_CHECK_FAILED = 10;
        public static final int AP_CONNECTED_FAILED = 39;
        public static final int AP_CONNECTING = 1;
        public static final int AP_DIALOG_OPEN = 40;
        public static final int AP_DISCONNECTED_OTHER_CONNECTION = 2;
        public static final int CANCEL_SAVING = 36;
        public static final int CHANGED_SELECTED_FILE_COUNT = 37;
        public static final int CONNGUIDE_DIALOG_OPEN = 41;
        public static final int CP_BROWSING_COMPLETED = 6;
        public static final int CP_DEVICE_FOUND = 8;
        public static final int DEVICE_CONFIGURATION_COMPLETED = 5;
        public static final int EXITING = 32;
        public static final int FINISH_SAVING_FILE = 38;
        public static final int MEMORY_FULL = 11;
        public static final int MSG_ID_DEVICE_DETECTED = 44;
        public static final int MSG_ID_TRANSMISSION_FAIL = 45;
        public static final int NO_RESPONSE_FROM_CAMERA = 47;
        public static final int OCCURED_PARSER_EXCEPTION = 46;
        public static final int PREPARE_SAVING_FILE = 34;
        public static final int RUN_EXIT_BYEBYE = 33;
        public static final int SHOW_DETAIL_IMAGE = 42;
        public static final int SHOW_GALLERY_DIALOG = 4;
        public static final int STARTING_SAVING_MESSAGE = 43;
        public static final int UPDATE_SAVING_MESSAGE = 35;
    }

    public static class ToastId {
        public static final int CHANGED_SYSTEM_SETTING = 9;
        public static final int MEMORY_FULL = 6;
        public static final int NO_THUMBNAIL = 7;
        public static final int OVER_LIMITATION_COUNT = 4;
        public static final int OVER_LIMITATION_SIZE = 5;
        public static final int TOAST_ID_MEMORY_FULL = 8;
        public static final int WIFI_CONNECTED = 1;
        public static final int WIFI_CONNECTING = 2;
        public static final int WIFI_SCAN_FAILED = 3;
    }
}

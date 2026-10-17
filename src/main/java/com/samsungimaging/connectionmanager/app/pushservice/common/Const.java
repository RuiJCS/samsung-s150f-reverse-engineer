package com.samsungimaging.connectionmanager.app.pushservice.common;

/* JADX INFO: loaded from: classes.dex */
public class Const {

    public static class AccessMethod {
        public static final String ACCESS_METHOD_MANUAL = "manual";
        public static final String ACCESS_METHOD_NFC = "nfc";
    }

    public static class ActivityResultId {
        public static final int ID_WIFI_SETTING = 101;
        public static final int REPLY_GALLERY_EXIT = 100;
    }

    public static class BoroadcastAction {
        public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    }

    public static class Config {
        public static final int BACK_PRESS_TIME = 3000;
        public static final long CRITICAL_SD_MEMORY = 10485760;
        public static int DUMMY_WIFI_CONNECT_TYPE = 1;
        public static final int LOW_BATTERY_LEVEL = 4;
        public static final int MAX_SELECT_CHECKBOX = 100;
        public static final int NUM_OF_ITEMS_AT_FIRST = 100;
        public static final int NUM_OF_REQUEST_ITEM = 20;
        public static final int NUM_OF_TOTALITEMS = 1000;
        public static final int TIMEOUT_OF_CONNECT = 180000;
        public static final String USER_AGENT_PREFIX = "SEC_DSC_";
        public static final boolean USE_SELECT_WIFI_POPUP = false;
        public static final boolean USE_WIFI_CAM_CHECKER = false;
        public static final int WIFI_CAM_CHECKER_RETRY_COUNT = 3;
        public static final boolean WIFI_DISABLE_ACTION = false;
    }

    public static class ErrCode {
        public static final String SSC_ERROR = "2000";
        public static final String SSC_ERROR_FILE_OPEN_FAIL = "2500";
        public static final String SSC_ERROR_FILE_SAVE_FAIL = "2502";
        public static final String SSC_ERROR_HEADER_OMISSION = "2100";
        public static final String SSC_ERROR_RECEIVE_NONE = "2200";
        public static final String SSC_ERROR_SD_NONE = "2300";
        public static final String SSC_ERROR_SOCKET_CREATE_FAIL = "2400";
        public static final String SSC_ERROR_UNKNOWN = "2999";
        public static final String SSC_NO_ERROR = "0";
    }

    public static class ExitReason {
        public static final String BACKEY_PRESSED = "BackKey_Pressed";
        public static final String BYEBYE_STANDBY = "ByeBye_StandBy";
        public static final String CANCELED = "Canceled";
        public static final String EXIT_PRESSED = "Exit_Pressed";
        public static final String LOW_BATTERY = "Low_Battery";
        public static final String MEMORY_FULL = "Memory_Full";
        public static final String REMOVED_SDCARD = "Removed_SDCard";
    }

    public static class Extras {
        public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    }

    public static class GpsCardinalPoints {
        public static final String EAST = "E";
        public static final String NORTH = "N";
        public static final String SOUTH = "S";
        public static final String WEST = "W";
    }

    public static class HeaderParsed {
        public static final int COMMAND = 3;
        public static final int FAIL = 2;
        public static final int SUCCESS = 1;
    }

    public static class MsgBoxId {
        public static final int MSGBOX_AP_DISCONNECTED = 1006;
        public static final int MSGBOX_CONNECTION_FAIL = 1007;
        public static final int MSGBOX_GUIDE_INIT = 1002;
        public static final int MSGBOX_LOW_BATTERY = 1008;
        public static final int MSGBOX_MEMORYFULL = 1004;
        public static final int MSGBOX_PROCESSING = 1000;
        public static final int MSGBOX_REMOVED_SDCARD = 1003;
        public static final int MSGBOX_SPINNER = 1001;
        public static final int MSGBOX_WAIT_CONNECTION = 1005;
    }

    public static class MsgId {
        public static final int APP_CLOSE = 24;
        public static final int APP_CLOSE_FOR_DSC_ERROR = 23;
        public static final int APP_CLOSE_FOR_EXITQUERY = 20;
        public static final int APP_CLOSE_FOR_MEMORY_FULL = 21;
        public static final int APP_CLOSE_FOR_REMOVED_SD = 22;
        public static final int APP_FINISH = 32;
        public static final int AP_CHECK_COMPLETED = 9;
        public static final int AP_CHECK_FAILED = 10;
        public static final int AP_CONNECTED = 3;
        public static final int AP_CONNECTED_FAILED = 39;
        public static final int AP_CONNECTING = 1;
        public static final int AP_DIALOG_OPEN = 40;
        public static final int AP_DISCONNECTED = 52;
        public static final int AP_DISCONNECTED_OTHER_CONNECTION = 2;
        public static final int AP_NOT_CONNECTED = 55;
        public static final int AP_RESPONSE_FAIL = 70;
        public static final int AP_WPA_DETECTED = 11;
        public static final int BACK_BUTTON_INVALID = 71;
        public static final int CANCEL_SAVING = 36;
        public static final int CHANGED_SELECTED_FILE_COUNT = 37;
        public static final int CONNECTION_FAIL = 73;
        public static final int CONNECTION_FAIL_UNAVAILABILITY = 74;
        public static final int CONNGUIDE_DIALOG_OPEN = 41;
        public static final int CP_BROWSING_COMPLETED = 6;
        public static final int CP_DEVICE_FOUND = 8;
        public static final int DEVICE_FIND_TIMEOUT = 7;
        public static final int EXITING = 32;
        public static final int FINISH_SAVING_FILE = 38;
        public static final int GOTO_GALLERY = 5;
        public static final int IMAGE_RCV_DONE = 51;
        public static final int IMAGE_RCV_START = 50;
        public static final int IMAGE_RCV_TIMEOUT = 54;
        public static final int IMAGE_RCV_WAIT = 53;
        public static final int PREPARE_SAVING_FILE = 34;
        public static final int PROGRESS_COMPLETED = 64;
        public static final int PROGRESS_THREAD = 62;
        public static final int RUN_EXIT_BYEBYE = 33;
        public static final int SEND_FILENAME = 63;
        public static final int SHOW_GALLERY_DIALOG = 4;
        public static final int START_THREAD = 60;
        public static final int STOP_THREAD = 61;
        public static final int UPDATE_SAVING_MESSAGE = 35;
        public static final int WIFI_CONNECTION_IS_BAD = 72;
    }

    public static class OSVersion {
        public static final String OS_VERSION_4_0_4 = "4.0.4";
        public static final String OS_VERSION_4_1_1 = "4.1.1";
        public static final String OS_VERSION_4_1_2 = "4.1.2";
    }

    public static class Resolution {
        public static final int GALAXY_S3 = 4;
        public static final int LARGE = 2;
        public static final int NORMAL = 1;
        public static final int NOTE10DOT1 = 5;
        public static final int XLARGE = 3;
    }
}

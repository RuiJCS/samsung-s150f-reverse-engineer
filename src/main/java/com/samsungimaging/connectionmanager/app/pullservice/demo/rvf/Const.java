package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf;

/* JADX INFO: loaded from: classes.dex */
public class Const {
    public static final String PREF_CHECKED_STATUS_OF_NOTICE = "PREF_CHECKED_STATUS_OF_NOTICE";
    public static final String PREF_LAST_SAVE_FILE_NAME = "PREF_LAST_SAVE_FILE_NAME";
    public static final String SRVF_PREFERENCE = "SRVF_PREFERENCE";

    public static class ActivityResultId {
        public static final int REQUEST_GALLERY = 0;
    }

    public static class Config {
        public static final int CONNECT_TIME = 180;
        public static final int EXIT_TIME = 180;
        public static final int NEXUSTIME = 4000;
        public static final int SAMSUNGTIME = 2000;
        public static final int TIMEOUT_OF_SAVING = 20000;
        public static final String USER_AGENT_PREFIX = "SEC_RVF_ML_";
        public static final boolean USE_WIFI_CAM_CHECKER = false;
    }

    public static class GpsCardinalPoints {
        public static final String EAST = "E";
        public static final String NORTH = "N";
        public static final String SOUTH = "S";
        public static final String WEST = "W";
    }

    public static class MsgBoxId {
        public static final int MSGBOX_BATTERY_EMPTY = 1001;
        public static final int MSGBOX_CAMERA_RECORD_ERROR_CODEC_ERROR = 1018;
        public static final int MSGBOX_CUSTOM = 1012;
        public static final int MSGBOX_DCF_FULL_ERROR = 1009;
        public static final int MSGBOX_DSC_DOWN = 1007;
        public static final int MSGBOX_DSC_NOTSEARCH = 1003;
        public static final int MSGBOX_FLASH_ON = 1013;
        public static final int MSGBOX_MEMORYFULL = 1006;
        public static final int MSGBOX_MEMORYFULLERROR = 1008;
        public static final int MSGBOX_NOTSERVICEVERSION = 1005;
        public static final int MSGBOX_REMOVED_SDCARD = 1014;
        public static final int MSGBOX_SMARTPHONE_SAVE_RESOLUTION_GUIDE = 1017;
        public static final int MSGBOX_STORAGE_ERROR = 1002;
        public static final int MSGBOX_WAIT_STREAM_QUALITY = 1016;
        public static final int PROGRESS_BAR_DISPLAY = 1010;
        public static final int PROGRESS_BAR_DISPLAY_EXIT = 1011;
        public static final int SHOT_FAILED = 1015;
    }

    public static class MsgId {
        public static final int CHANGED_SYSTEM_SETTING = 124;
        public static final int DISMISS_CUSTOM_PROGRESS_DIALOG = 141;
        public static final int DISPLAY_AF = 6;
        public static final int DISPLAY_AFDONE = 9;
        public static final int DISPLAY_BUTTONTRUE = 28;
        public static final int DISPLAY_BUTTON_ACTIVE = 110;
        public static final int DISPLAY_BUTTON_SHOW = 34;
        public static final int DISPLAY_CLOSEMSG = 7;
        public static final int DISPLAY_DONE = 13;
        public static final int DISPLAY_DSCDOWNMSG = 19;
        public static final int DISPLAY_EXITMSG = 11;
        public static final int DISPLAY_MSGCONNECTED = 16;
        public static final int DISPLAY_MSGCONNECTING = 15;
        public static final int DISPLAY_MSGMEMORYERROR = 29;
        public static final int DISPLAY_MSGMEMORYFULL = 18;
        public static final int DISPLAY_NETERRORMSG = 10;
        public static final int DISPLAY_OPEN_FLASH = 121;
        public static final int DISPLAY_SAVE = 20;
        public static final int DISPLAY_SHUTTER_ACTIVE = 111;
        public static final int DISPLAY_SHUTTING = 24;
        public static final int DISPLAY_STARTPRO = 22;
        public static final int DISPLAY_STORAGEMSG = 5;
        public static final int DISPLAY_THUMBNAILIMAGE = 2;
        public static final int DISPLAY_TIMER = 1;
        public static final int DISPLAY_WAIT_STREAMING = 32;
        public static final int DISPLAY_ZOOMBAR = 30;
        public static final int DISPLAY_ZOOMINBUTTON = 26;
        public static final int DISPLAY_ZOOMOUTBUTTON = 25;
        public static final int DISPLAY_ZOOM_REGION = 108;
        public static final int FLASH_RESOURCE_CHANGE = 123;
        public static final int LAYOUT_CHANGE = 122;
        public static final int OCCURED_PARSER_EXCEPTION = 142;
        public static final int ON_CHANGED_APERTURE_LIST = 135;
        public static final int ON_CHANGED_APERTURE_SETUP_VALUE = 130;
        public static final int ON_CHANGED_CURRENT_ZOOM_VALUE = 134;
        public static final int ON_CHANGED_EV_SETUP_VALUE = 131;
        public static final int ON_CHANGED_FLASH_STATUS = 127;
        public static final int ON_CHANGED_FOCUS_STATUS = 128;
        public static final int ON_CHANGED_LAST_PHOTO_URL_VALUE = 133;
        public static final int ON_CHANGED_MOVIE_RECORD_TIME = 137;
        public static final int ON_CHANGED_REMAIN_REC_TIME_VALUE = 138;
        public static final int ON_CHANGED_REMAIN_SHOT_COUNT_VALUE = 132;
        public static final int ON_CHANGED_ROTATION_STATUS = 126;
        public static final int ON_CHANGED_SHUTTER_SPEED_LIST = 136;
        public static final int ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE = 129;
        public static final int ON_CODEC_RESTARTED = 145;
        public static final int ON_DOWNLOAD_SUCCESS = 140;
        public static final int ON_UPDATE_MOVIE_RECORD_TIME = 139;
        public static final int RUN_CODEC_INIT = 48;
        public static final int RUN_DISPLAY_SHOT = 47;
        public static final int RUN_EXIT = 44;
        public static final int RUN_EXIT_BYEBYE = 45;
        public static final int RUN_MULTIAF_DSC = 49;
        public static final int RUN_SHOT = 40;
        public static final int RUN_SHUTTER_UP = 50;
        public static final int RUN_THUMBNAILDOWN = 43;
        public static final int RUN_TOAST = 41;
        public static final int SET_ENABLE_SURFACEVIEW = 144;
        public static final int SHOT_FAILED = 125;
        public static final int TIMEOUT_OF_SAVING = 35;
    }

    public static class OptionMenuID {
        public static final int CAMERA_MORE = 5;
        public static final int CAMERA_SAVE = 4;
        public static final int FLASH = 1;
        public static final int NONE = 0;
        public static final int PHOTO_SIZE = 3;
        public static final int STREAMING_QUALITY = 6;
        public static final int TIMER = 2;
    }

    public static class ScreenType {
        public static final int PHONELANDCAMLAND = 3;
        public static final int PHONELANDCAMPORT = 4;
        public static final int PHONEPORTCAMLAND = 1;
        public static final int PHONEPORTCAMPORT = 2;
    }

    public static class ToastId {
        public static final int CHANGED_SYSTEM_SETTING = 8;
        public static final int EXIT_TIME_EXIT = 6;
        public static final int MEMORY_FULL = 7;
    }
}

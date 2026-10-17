package com.samsungimaging.connectionmanager.app.pullservice;

/* JADX INFO: loaded from: classes.dex */
public class Const {

    public static class AFFrameMetricsType {
        public static final int METRICS_1x1 = 0;
        public static final int METRICS_3x3 = 1;
        public static final int METRICS_3x5 = 2;
        public static final int METRICS_3x7 = 3;
    }

    public static class AFFrameType {
        public static final int EXTEND = 2;
        public static final int MATRIX = 1;
        public static final int NONE = 0;
    }

    public static class AFMode {
        public static final int MULTI = 1;
        public static final int SINGLE = 0;
    }

    public static class AccessMethod {
        public static final String ACCESS_METHOD_MANUAL = "manual";
        public static final String ACCESS_METHOD_NFC = "nfc";
    }

    public static class ActivityResultId {
        public static final int REQUEST_GALLERY = 0;
    }

    public static class Config {
        public static final int DELAY_CONNECTION_TIME_OUT = 180000;
        public static final int DELAY_DISPLAY_EXIT = 500;
        public static final int LOW_BATTERY_LEVEL = 4;
    }

    public static class MsgBoxId {
        public static final int MSGBOX_BATTERY_EMPTY = 2003;
        public static final int MSGBOX_CONFIRM_MESSAGE = 2000;
        public static final int MSGBOX_CONNECTION_FAIL = 2004;
        public static final int MSGBOX_GOTO_SETTINGS = 1113;
        public static final int MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY = 2007;
        public static final int MSGBOX_LENS_ERROR = 2011;
        public static final int MSGBOX_NETWORK_DISCONNECT = 2006;
        public static final int MSGBOX_NO_CARD = 2008;
        public static final int MSGBOX_QUESTION_EXIT = 2005;
        public static final int MSGBOX_SHUTDOWN_BY_NOT_RESPONSE = 2002;
        public static final int MSGBOX_WAIT_CONNECTION = 2001;
        public static final int PROGRESS_BAR_DISPLAY_EXIT = 3000;
        public static final int PROGRESS_BAR_SERVICE_CHANGE = 3001;
    }

    public static class MsgID {
        public static final int CONNECTOR_ON_ALIVE = 100;
        public static final int CONNECTOR_ON_BYEBYE = 101;
        public static final int CONNECTOR_ON_CAMERA_CHANGE = 102;
        public static final int CONNECTOR_ON_CHANGED_APERTURE_LIST = 112;
        public static final int CONNECTOR_ON_CHANGED_APERTURE_SETUP_VALUE = 107;
        public static final int CONNECTOR_ON_CHANGED_CURRENT_ZOOM_VALUE = 111;
        public static final int CONNECTOR_ON_CHANGED_EV_SETUP_VALUE = 108;
        public static final int CONNECTOR_ON_CHANGED_FLASH_STATUS = 104;
        public static final int CONNECTOR_ON_CHANGED_FLASH_STROBE_STATUS = 116;
        public static final int CONNECTOR_ON_CHANGED_FOCUS_STATUS = 105;
        public static final int CONNECTOR_ON_CHANGED_LAST_PHOTO_URL_VALUE = 110;
        public static final int CONNECTOR_ON_CHANGED_MOVIE_RECORD_TIME = 114;
        public static final int CONNECTOR_ON_CHANGED_OPERATION_STATE = 117;
        public static final int CONNECTOR_ON_CHANGED_REMAIN_REC_TIME_VALUE = 115;
        public static final int CONNECTOR_ON_CHANGED_REMAIN_SHOT_COUNT_VALUE = 109;
        public static final int CONNECTOR_ON_CHANGED_ROTATION_STATUS = 103;
        public static final int CONNECTOR_ON_CHANGED_SHUTTER_SPEED_LIST = 113;
        public static final int CONNECTOR_ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE = 106;
        public static final int CONNECTOR_ON_OCCURED_PARSER_EXCEPTION = 118;
        public static final int DOWNLOADER_ON_DOWNLOAD_SUCCESS = 19;
        public static final int ON_UNKNOWN = 20;
        public static final int TIMER_HIDE_AF_FRAME = 18;
        public static final int TIMER_ON_CONNECTION_TIME_OUT = 200;
        public static final int TIMER_ON_DISPLAY_EXIT = 201;
        public static final int TIMER_ON_EXIT_BY_NETWORK_ERROR = 202;
    }

    public static class MsgKey {
        public static final String DIALOG_MESSAGE_KEY = "DIALOG_MESSAGE_KEY";
    }

    public static class PopupWindowId {
        public static final int POPUP_WINDOW_MEMORY_FULL = 1;
        public static final int POPUP_WINDOW_NONE = 0;
        public static final int POPUP_WINDOW_SCREEN_OFF_WHILE_RECORDING = 2;
    }

    public static class RatioType {
        public static final int RATIO_16X9 = 1;
        public static final int RATIO_1X1 = 4;
        public static final int RATIO_3X2 = 3;
        public static final int RATIO_4X3 = 2;
        public static final int RATIO_NONE = 0;
    }

    public static class ScreenType {
        public static final int PHONE_LAND_CAM_LAND = 2;
        public static final int PHONE_LAND_CAM_PORT = 3;
        public static final int PHONE_PORT_CAM_LAND = 0;
        public static final int PHONE_PORT_CAM_PORT = 1;
    }

    public static class ToastID {
    }
}

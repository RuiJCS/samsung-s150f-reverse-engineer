package com.samsungimaging.connectionmanager.app.pullservice.controller;

import android.os.Handler;
import android.os.Message;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCCamera;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCCommand;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCMenuItem;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCMovieResolution;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCResolution;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.RVFFunctionManager;
import com.samsungimaging.connectionmanager.app.pullservice.util.CommandManager;
import com.samsungimaging.connectionmanager.app.pullservice.util.CommandRequestListener;
import com.samsungimaging.connectionmanager.app.pullservice.util.Utils;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.Locale;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.control.ControlResponse;
import org.cybergarage.upnp.xml.ActionData;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class DeviceController extends DSCCamera implements CommandRequestListener {
    private Action AFAct;
    private Action BrowseAct;
    private Action CancelTouchAFMovieAct;
    private Action GetDeviceConfigurationAct;
    private Action GetInformationAct;
    private Action GetZoomAct;
    private Action MULTIAFAct;
    private Action ReleaseSelfTimerAct;
    private Action SetAFAreaAct;
    private Action SetAFModeAct;
    private Action SetApertureAct;
    private Action SetDialModeAct;
    private Action SetDriveAct;
    private Action SetEVAct;
    private Action SetFileSaveAct;
    private Action SetFlashAct;
    private Action SetISOAct;
    private Action SetLEDAct;
    private Action SetMeteringAct;
    private Action SetMovieResolutionAct;
    private Action SetMovieStreamQualityAct;
    private Action SetOperationStateAct;
    private Action SetQualityAct;
    private Action SetResolutionAct;
    private Action SetShutterSpeedAct;
    private Action SetStreamQualityAct;
    private Action SetTotalCopyItemsAct;
    private Action SetTouchAFOptionAct;
    private Action SetWBAct;
    private Action SetZoomAct;
    private Action ShotAct;
    private Action ShotWithGPSAct;
    private Action ShutterUpAct;
    private Action StartRecordAct;
    private Action StartShotAct;
    private Action StartZoomAct;
    private Action StopRecordAct;
    private Action StopShotAct;
    private Action StopZoomAct;
    private Action TouchAFAct;
    private Action TouchAFMovieAct;
    private Action TouchAFPointAct;
    private Action ZoomINAct;
    private Action ZoomOUTAct;
    private Handler handler;
    private Trace.Tag TAG = Trace.Tag.CYBERGARAGE;
    private CommandManager commandManager = new CommandManager();

    public static class ActionID {
        public static final int AUTO_FOCUS_ACTION_ID = 9;
        public static final int BROWSE_ACTION_ID = 41;
        public static final int CANCEL_TOUCH_AF_MOVIE_ACTION_ID = 39;
        public static final int GET_DEVICE_CONFIGURATION_ACTION_ID = 42;
        public static final int GET_INFORMATION_ACTION_ID = 1;
        public static final int GET_ZOOM_ACTION_ID = 5;
        public static final int MULTI_AUTO_FOCUS_ACTION_ID = 10;
        public static final int NONE_ACTION_ID = 0;
        public static final int RELEASE_SELF_TIMER_ACTION_ID = 13;
        public static final int SET_AF_AREA_ACTION_ID = 28;
        public static final int SET_AF_MODE_ACTION_ID = 27;
        public static final int SET_APERTURE_ACTION_ID = 22;
        public static final int SET_DIAL_MODE_ACTION_ID = 32;
        public static final int SET_DRIVE_ACTION_ID = 29;
        public static final int SET_EV_ACTION_ID = 23;
        public static final int SET_FILE_SAVE_ACTION_ID = 37;
        public static final int SET_FLASH_ACTION_ID = 17;
        public static final int SET_ISO_ACTION_ID = 24;
        public static final int SET_LED_ACTION_ID = 16;
        public static final int SET_METERING_ACTION_ID = 26;
        public static final int SET_MOVIE_RESOLUTION_ACTION_ID = 34;
        public static final int SET_MOVIE_STREAM_QUALITY_ACTION_ID = 19;
        public static final int SET_OPERATION_STATE_ACTION_ID = 43;
        public static final int SET_QUALITY_ACTION_ID = 33;
        public static final int SET_RESOLUTION_ACTION_ID = 2;
        public static final int SET_SHUTTER_SPEED_ACTION_ID = 21;
        public static final int SET_STREAM_QUALITY_ACTION_ID = 18;
        public static final int SET_TOTAL_COPY_ITEMS_ACTION_ID = 44;
        public static final int SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_ID = 11;
        public static final int SET_WB_ACTION_ID = 25;
        public static final int SET_ZOOM_ACTION_ID = 6;
        public static final int SHOT_ACTION_ID = 14;
        public static final int SHOT_WITH_GPS_ACTION_ID = 15;
        public static final int SHUTTER_UP_ACTION_ID = 20;
        public static final int START_RECORD_ACTION_ID = 35;
        public static final int START_SHOT_ACTION_ID = 30;
        public static final int START_ZOOM_ACTION_ID = 7;
        public static final int STOP_RECORD_ACTION_ID = 36;
        public static final int STOP_SHOT_ACTION_ID = 31;
        public static final int STOP_ZOOM_ACTION_ID = 8;
        public static final int TOUCH_AF_MOVIE_ACTION_ID = 40;
        public static final int TOUCH_AF_POINT_ACTION_ID = 38;
        public static final int TOUCH_AUTO_FOCUS_ACTION_ID = 12;
        public static final int ZOOM_IN_ACTION_ID = 3;
        public static final int ZOOM_OUT_ACTION_ID = 4;
    }

    public static class ActionName {
        public static final String AUTO_FOCUS_ACTION_NAME = "AF";
        public static final String BROWSE_ACTION_NAME = "Browse";
        public static final String CANCEL_TOUCH_AF_MOVIE_ACTION_NAME = "CancelTouchAFMovie";
        public static final String GET_DEVICE_CONFIGURATION_ACTION_NAME = "GetDeviceConfiguration";
        public static final String GET_INFOMATION_ACTION_NAME = "GetInfomation";
        public static final String GET_ZOOM_ACTION_NAME = "GetZoom";
        public static final String MULTI_AUTO_FOCUS_ACTION_NAME = "MULTIAF";
        public static final String RELEASE_SELF_TIMER_ACTION_NAME = "ReleaseSelfTimer";
        public static final String SET_AF_AREA_ACTION_NAME = "SetAFArea";
        public static final String SET_AF_MODE_ACTION_NAME = "SetAFMode";
        public static final String SET_APERTURE_ACTION_NAME = "SetAperture";
        public static final String SET_DIAL_MODE_ACTION_NAME = "SetDialMode";
        public static final String SET_DRIVE_ACTION_NAME = "SetDrive";
        public static final String SET_EV_ACTION_NAME = "SetEV";
        public static final String SET_FILE_SAVE_ACTION_NAME = "SetFileSave";
        public static final String SET_FLASH_ACTION_NAME = "SetFlash";
        public static final String SET_ISO_ACTION_NAME = "SetISO";
        public static final String SET_LED_ACTION_NAME = "SetLED";
        public static final String SET_METERING_ACTION_NAME = "SetMetering";
        public static final String SET_MOVIE_RESOLUTION_ACTION_NAME = "SetMovieResolution";
        public static final String SET_MOVIE_STREAM_QUALITY_ACTION_NAME = "SetStreamQuality";
        public static final String SET_OPERATION_STATE_ACTION_NAME = "SetOperationState";
        public static final String SET_QUALITY_ACTION_NAME = "SetQuality";
        public static final String SET_RESOLUTION_ACTION_NAME = "SetResolution";
        public static final String SET_SHUTTER_SPEED_ACTION_NAME = "SetShutterSpeed";
        public static final String SET_STREAM_QUALITY_ACTION_NAME = "SetStreamQuality";
        public static final String SET_TOTAL_COPY_ITEMS_ACTION_NAME = "SetTotalCopyItems";
        public static final String SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_NAME = "setTouchAFOption";
        public static final String SET_WB_ACTION_NAME = "SetWB";
        public static final String SET_ZOOM_ACTION_NAME = "SetZoom";
        public static final String SHOT_ACTION_NAME = "Shot";
        public static final String SHOT_WITH_GPS_ACTION_NAME = "ShotWithGPS";
        public static final String SHUTTER_UP_ACTION_NAME = "ShutterUp";
        public static final String START_RECORD_ACTION_NAME = "StartRecord";
        public static final String START_SHOT_ACTION_NAME = "StartShot";
        public static final String START_ZOOM_ACTION_NAME = "StartZoom";
        public static final String STOP_RECORD_ACTION_NAME = "StopRecord";
        public static final String STOP_SHOT_ACTION_NAME = "StopShot";
        public static final String STOP_ZOOM_ACTION_NAME = "StopZoom";
        public static final String TOUCH_AF_MOVIE_ACTION_NAME = "touchAFMovie";
        public static final String TOUCH_AF_POINT_ACTION_NAME = "touchAFPoint";
        public static final String TOUCH_AUTO_FOCUS_ACTION_NAME = "touchAF";
        public static final String ZOOM_IN_ACTION_NAME = "ZoomIN";
        public static final String ZOOM_OUT_ACTION_NAME = "ZoomOUT";
    }

    public static class ActionNode {
        public static final String AF_AREA_VALUE = "AFAreaValue";
        public static final String AF_MODE_VALUE = "AFModeValue";
        public static final String AF_POSITION = "AFPOSITION";
        public static final String APERTURE_VALUE = "ApertureValue";
        public static final String AUTO_FOCUS = "AutoFocus";
        public static final String AUTO_FOCUS_PRIORITY = "AFPriority";
        public static final String AVAILSHOTS = "AVAILSHOTS";
        public static final String CARD_STATUS_VALUE = "CardStatusValue";
        public static final String CURRENT_FLASH_DISPLAY = "CurrentFlashDisplay";
        public static final String DEFAULT_FLASH = "Defaultflash";
        public static final String DEFAULT_FOCUS_STATE = "DefaultFocusState";
        public static final String DEFAULT_ZOOM = "DefaultZoom";
        public static final String DIAL_MODE_VALUE = "DialModeValue";
        public static final String DRIVE_VALUE = "DriveValue";
        public static final String EV_VALUE = "EVValue";
        public static final String FILE_SAVE_VALUE = "FileSaveValue";
        public static final String FLASH = "Flash";
        public static final String FLASH_DISPLAY = "FlashDisplay";
        public static final String FLASH_STROBE_STATUS = "FlashStrobeStatus";
        public static final String GET_INFORMATION_RESULT = "GETINFORMATIONRESULT";
        public static final String GET_IP_RESULT = "GETIPRESULT";
        public static final String ISO_VALUE = "ISOValue";
        public static final String MAX_ZOOM = "MaxZoom";
        public static final String METERING_VALUE = "MeteringValue";
        public static final String MIN_ZOOM = "MinZoom";
        public static final String MOVIE_RESOLUTION_VALUE = "MovieResolutionValue";
        public static final String MOVIE_TOUCH_AF_STATUS = "MovieTouchAFStatus";
        public static final String MULTI_AF_MATRIX_SIZE = "MultiAFMatrixSize";
        public static final String QUALITY_HIGH_URL = "QualityHighUrl";
        public static final String QUALITY_LOW_URL = "QualityLowUrl";
        public static final String QUALITY_VALUE = "QualityValue";
        public static final String RATIO_OFFSET = "RatioOffset";
        public static final String REMAIN_REC_TIME = "RemainRecTime";
        public static final String RESOLUTIONS = "Resolutions";
        public static final String RESOLUTION_RATIO = "ResolutionRatio";
        public static final String ROTATION = "ROTATION";
        public static final String SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
        public static final String STREAMING_URLS = "StreamUrl";
        public static final String STREAM_QUALITY = "StreamQuality";
        public static final String TOUCH_AUTO_FOCUS = "TouchAF";
        public static final String VERSION = "Version";
        public static final String VIDEO_OUT_VALUE = "VideoOutValue";
        public static final String WB_VALUE = "WBValue";
        public static final String ZOOM_INFO = "ZoomInfo";
    }

    public static class ActionResult {
        public static final int FAIL = 0;
        public static final int SUCCESS = 1;
    }

    public static class ActionStatus {
        public static final int ACTION_STATUS_NONE = 0;
        public static final int ACTION_STATUS_READY = 1;
        public static final int AUTO_FOCUS_ACTION_END = 19;
        public static final int AUTO_FOCUS_ACTION_START = 18;
        public static final int BROWSE_ACTION_END = 83;
        public static final int BROWSE_ACTION_START = 82;
        public static final int CANCEL_TOUCH_AF_MOVIE_ACTION_END = 79;
        public static final int CANCEL_TOUCH_AF_MOVIE_ACTION_START = 78;
        public static final int GET_DEVICE_CONFIGURATION_ACTION_END = 85;
        public static final int GET_DEVICE_CONFIGURATION_ACTION_START = 84;
        public static final int GET_INFORMATION_ACTION_END = 3;
        public static final int GET_INFORMATION_ACTION_START = 2;
        public static final int GET_ZOOM_ACTION_END = 11;
        public static final int GET_ZOOM_ACTION_START = 10;
        public static final int MULTI_AUTO_FOCUS_ACTION_END = 21;
        public static final int MULTI_AUTO_FOCUS_ACTION_START = 20;
        public static final int RELEASE_SELF_TIMER_ACTION_END = 27;
        public static final int RELEASE_SELF_TIMER_ACTION_START = 26;
        public static final int SET_AF_AREA_ACTION_END = 57;
        public static final int SET_AF_AREA_ACTION_START = 56;
        public static final int SET_AF_MODE_ACTION_END = 55;
        public static final int SET_AF_MODE_ACTION_START = 54;
        public static final int SET_APERTURE_ACTION_END = 45;
        public static final int SET_APERTURE_ACTION_START = 44;
        public static final int SET_DIAL_MODE_ACTION_END = 65;
        public static final int SET_DIAL_MODE_ACTION_START = 64;
        public static final int SET_DRIVE_ACTION_END = 59;
        public static final int SET_DRIVE_ACTION_START = 58;
        public static final int SET_EV_ACTION_END = 47;
        public static final int SET_EV_ACTION_START = 46;
        public static final int SET_FILE_SAVE_ACTION_END = 75;
        public static final int SET_FILE_SAVE_ACTION_START = 74;
        public static final int SET_FLASH_ACTION_END = 35;
        public static final int SET_FLASH_ACTION_START = 34;
        public static final int SET_ISO_ACTION_END = 49;
        public static final int SET_ISO_ACTION_START = 48;
        public static final int SET_LED_ACTION_END = 33;
        public static final int SET_LED_ACTION_START = 32;
        public static final int SET_METERING_ACTION_END = 53;
        public static final int SET_METERING_ACTION_START = 52;
        public static final int SET_MOVIE_RESOLUTION_ACTION_END = 69;
        public static final int SET_MOVIE_RESOLUTION_ACTION_START = 68;
        public static final int SET_MOVIE_STREAM_QUALITY_ACTION_END = 39;
        public static final int SET_MOVIE_STREAM_QUALITY_ACTION_START = 38;
        public static final int SET_OPERATION_STATE_ACTION_END = 87;
        public static final int SET_OPERATION_STATE_ACTION_START = 86;
        public static final int SET_QUALITY_ACTION_END = 67;
        public static final int SET_QUALITY_ACTION_START = 66;
        public static final int SET_RESOLUTION_ACTION_END = 5;
        public static final int SET_RESOLUTION_ACTION_START = 4;
        public static final int SET_SHUTTER_SPEED_ACTION_END = 43;
        public static final int SET_SHUTTER_SPEED_ACTION_START = 42;
        public static final int SET_STREAM_QUALITY_ACTION_END = 37;
        public static final int SET_STREAM_QUALITY_ACTION_START = 36;
        public static final int SET_TOTAL_COPY_ITEMS_ACTION_END = 89;
        public static final int SET_TOTAL_COPY_ITEMS_ACTION_START = 88;
        public static final int SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_END = 23;
        public static final int SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_START = 22;
        public static final int SET_WB_ACTION_END = 51;
        public static final int SET_WB_ACTION_START = 50;
        public static final int SET_ZOOM_ACTION_END = 13;
        public static final int SET_ZOOM_ACTION_START = 12;
        public static final int SHOT_ACTION_END = 29;
        public static final int SHOT_ACTION_START = 28;
        public static final int SHOT_WITH_GPS_ACTION_END = 31;
        public static final int SHOT_WITH_GPS_ACTION_START = 30;
        public static final int SHUTTER_UP_ACTION_END = 41;
        public static final int SHUTTER_UP_ACTION_START = 40;
        public static final int START_RECORD_ACTION_END = 71;
        public static final int START_RECORD_ACTION_START = 70;
        public static final int START_SHOT_ACTION_END = 61;
        public static final int START_SHOT_ACTION_START = 60;
        public static final int START_ZOOM_ACTION_END = 15;
        public static final int START_ZOOM_ACTION_START = 14;
        public static final int STOP_RECORD_ACTION_END = 73;
        public static final int STOP_RECORD_ACTION_START = 72;
        public static final int STOP_SHOT_ACTION_END = 63;
        public static final int STOP_SHOT_ACTION_START = 62;
        public static final int STOP_ZOOM_ACTION_END = 17;
        public static final int STOP_ZOOM_ACTION_START = 16;
        public static final int TOUCH_AF_MOVIE_ACTION_END = 81;
        public static final int TOUCH_AF_MOVIE_ACTION_START = 80;
        public static final int TOUCH_AF_POINT_ACTION_END = 77;
        public static final int TOUCH_AF_POINT_ACTION_START = 76;
        public static final int TOUCH_AUTO_FOCUS_ACTION_END = 25;
        public static final int TOUCH_AUTO_FOCUS_ACTION_START = 24;
        public static final int ZOOM_IN_ACTION_END = 7;
        public static final int ZOOM_IN_ACTION_START = 6;
        public static final int ZOOM_OUT_ACTION_END = 9;
        public static final int ZOOM_OUT_ACTION_START = 8;
    }

    public static class FlashMode {
        public static final String AUTO = "AUTO";
        public static final String OFF = "OFF";
    }

    public DeviceController() {
        this.commandManager.addCommandRequestListener(this);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendEvent(int event, Object obj) {
        Trace.d(this.TAG, "start sendEvent() name : " + actionStatusToString(event));
        if (this.handler != null) {
            Message message = new Message();
            message.what = event;
            message.obj = obj;
            this.handler.sendMessage(message);
        }
    }

    public void setAction(Device device) {
        if (device != null) {
            ServiceList serviceList = device.getServiceList();
            for (int i = 0; i < serviceList.size(); i++) {
                Service service = serviceList.getService(i);
                if (service.getServiceType().contains("service:ContentDirectory")) {
                    this.GetInformationAct = service.getAction(ActionName.GET_INFOMATION_ACTION_NAME);
                    this.SetResolutionAct = service.getAction(ActionName.SET_RESOLUTION_ACTION_NAME);
                    this.GetZoomAct = service.getAction(ActionName.GET_ZOOM_ACTION_NAME);
                    this.SetZoomAct = service.getAction(ActionName.SET_ZOOM_ACTION_NAME);
                    this.ZoomINAct = service.getAction(ActionName.ZOOM_IN_ACTION_NAME);
                    this.ZoomOUTAct = service.getAction(ActionName.ZOOM_OUT_ACTION_NAME);
                    this.StartZoomAct = service.getAction(ActionName.START_ZOOM_ACTION_NAME);
                    this.StopZoomAct = service.getAction(ActionName.STOP_ZOOM_ACTION_NAME);
                    this.AFAct = service.getAction(ActionName.AUTO_FOCUS_ACTION_NAME);
                    this.MULTIAFAct = service.getAction(ActionName.MULTI_AUTO_FOCUS_ACTION_NAME);
                    this.SetTouchAFOptionAct = service.getAction(ActionName.SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_NAME);
                    this.TouchAFAct = service.getAction(ActionName.TOUCH_AUTO_FOCUS_ACTION_NAME);
                    this.ReleaseSelfTimerAct = service.getAction(ActionName.RELEASE_SELF_TIMER_ACTION_NAME);
                    this.ShotAct = service.getAction(ActionName.SHOT_ACTION_NAME);
                    this.ShotWithGPSAct = service.getAction(ActionName.SHOT_WITH_GPS_ACTION_NAME);
                    this.SetLEDAct = service.getAction(ActionName.SET_LED_ACTION_NAME);
                    this.SetFlashAct = service.getAction(ActionName.SET_FLASH_ACTION_NAME);
                    this.SetStreamQualityAct = service.getAction("SetStreamQuality");
                    this.SetMovieStreamQualityAct = service.getAction("SetStreamQuality");
                    this.ShutterUpAct = service.getAction(ActionName.SHUTTER_UP_ACTION_NAME);
                    this.SetShutterSpeedAct = service.getAction(ActionName.SET_SHUTTER_SPEED_ACTION_NAME);
                    this.SetApertureAct = service.getAction(ActionName.SET_APERTURE_ACTION_NAME);
                    this.SetEVAct = service.getAction(ActionName.SET_EV_ACTION_NAME);
                    this.SetISOAct = service.getAction(ActionName.SET_ISO_ACTION_NAME);
                    this.SetWBAct = service.getAction(ActionName.SET_WB_ACTION_NAME);
                    this.SetMeteringAct = service.getAction(ActionName.SET_METERING_ACTION_NAME);
                    this.SetAFModeAct = service.getAction(ActionName.SET_AF_MODE_ACTION_NAME);
                    this.SetAFAreaAct = service.getAction(ActionName.SET_AF_AREA_ACTION_NAME);
                    this.SetDriveAct = service.getAction(ActionName.SET_DRIVE_ACTION_NAME);
                    this.StartShotAct = service.getAction(ActionName.START_SHOT_ACTION_NAME);
                    this.StopShotAct = service.getAction(ActionName.STOP_SHOT_ACTION_NAME);
                    this.SetDialModeAct = service.getAction(ActionName.SET_DIAL_MODE_ACTION_NAME);
                    this.SetQualityAct = service.getAction(ActionName.SET_QUALITY_ACTION_NAME);
                    this.SetMovieResolutionAct = service.getAction(ActionName.SET_MOVIE_RESOLUTION_ACTION_NAME);
                    this.StartRecordAct = service.getAction(ActionName.START_RECORD_ACTION_NAME);
                    this.StopRecordAct = service.getAction(ActionName.STOP_RECORD_ACTION_NAME);
                    this.SetFileSaveAct = service.getAction(ActionName.SET_FILE_SAVE_ACTION_NAME);
                    this.TouchAFPointAct = service.getAction(ActionName.TOUCH_AF_POINT_ACTION_NAME);
                    this.CancelTouchAFMovieAct = service.getAction(ActionName.CANCEL_TOUCH_AF_MOVIE_ACTION_NAME);
                    this.TouchAFMovieAct = service.getAction(ActionName.TOUCH_AF_MOVIE_ACTION_NAME);
                    this.BrowseAct = service.getAction(ActionName.BROWSE_ACTION_NAME);
                    this.GetDeviceConfigurationAct = service.getAction(ActionName.GET_DEVICE_CONFIGURATION_ACTION_NAME);
                    this.SetOperationStateAct = service.getAction(ActionName.SET_OPERATION_STATE_ACTION_NAME);
                    this.SetTotalCopyItemsAct = service.getAction(ActionName.SET_TOTAL_COPY_ITEMS_ACTION_NAME);
                    return;
                }
            }
        }
    }

    public Action getAction(int actionID) {
        switch (actionID) {
            case 1:
                Action action = this.GetInformationAct;
                return action;
            case 2:
                Action action2 = this.SetResolutionAct;
                return action2;
            case 3:
                Action action3 = this.ZoomINAct;
                return action3;
            case 4:
                Action action4 = this.ZoomOUTAct;
                return action4;
            case 5:
                Action action5 = this.GetZoomAct;
                return action5;
            case 6:
                Action action6 = this.SetZoomAct;
                return action6;
            case 7:
                Action action7 = this.StartZoomAct;
                return action7;
            case 8:
                Action action8 = this.StopZoomAct;
                return action8;
            case 9:
                Action action9 = this.AFAct;
                return action9;
            case 10:
                Action action10 = this.MULTIAFAct;
                return action10;
            case 11:
                Action action11 = this.SetTouchAFOptionAct;
                return action11;
            case 12:
                Action action12 = this.TouchAFAct;
                return action12;
            case 13:
                Action action13 = this.ReleaseSelfTimerAct;
                return action13;
            case 14:
                Action action14 = this.ShotAct;
                return action14;
            case 15:
                Action action15 = this.ShotWithGPSAct;
                return action15;
            case 16:
                Action action16 = this.SetLEDAct;
                return action16;
            case 17:
                Action action17 = this.SetFlashAct;
                return action17;
            case 18:
                Action action18 = this.SetStreamQualityAct;
                return action18;
            case 19:
                Action action19 = this.SetMovieStreamQualityAct;
                return action19;
            case 20:
                Action action20 = this.ShutterUpAct;
                return action20;
            case 21:
                Action action21 = this.SetShutterSpeedAct;
                return action21;
            case 22:
                Action action22 = this.SetApertureAct;
                return action22;
            case 23:
                Action action23 = this.SetEVAct;
                return action23;
            case 24:
                Action action24 = this.SetISOAct;
                return action24;
            case 25:
                Action action25 = this.SetWBAct;
                return action25;
            case 26:
                Action action26 = this.SetMeteringAct;
                return action26;
            case 27:
                Action action27 = this.SetAFModeAct;
                return action27;
            case 28:
                Action action28 = this.SetAFAreaAct;
                return action28;
            case 29:
                Action action29 = this.SetDriveAct;
                return action29;
            case 30:
                Action action30 = this.StartShotAct;
                return action30;
            case 31:
                Action action31 = this.StopShotAct;
                return action31;
            case 32:
                Action action32 = this.SetDialModeAct;
                return action32;
            case 33:
                Action action33 = this.SetQualityAct;
                return action33;
            case 34:
                Action action34 = this.SetMovieResolutionAct;
                return action34;
            case 35:
                Action action35 = this.StartRecordAct;
                return action35;
            case 36:
                Action action36 = this.StopRecordAct;
                return action36;
            case 37:
                Action action37 = this.SetFileSaveAct;
                return action37;
            case 38:
                Action action38 = this.TouchAFPointAct;
                return action38;
            case 39:
                Action action39 = this.CancelTouchAFMovieAct;
                return action39;
            case 40:
                Action action40 = this.TouchAFMovieAct;
                return action40;
            case 41:
                Action action41 = this.BrowseAct;
                return action41;
            case 42:
                Action action42 = this.GetDeviceConfigurationAct;
                return action42;
            case 43:
                Action action43 = this.SetOperationStateAct;
                return action43;
            case 44:
                Action action44 = this.SetTotalCopyItemsAct;
                return action44;
            default:
                return null;
        }
    }

    public String actionStatusToString(int actionStatus) {
        switch (actionStatus) {
            case 0:
                return "ACTION_STATUS_NONE";
            case 1:
            default:
                return "";
            case 2:
                return "GET_INFORMATION_ACTION_START";
            case 3:
                return "GET_INFORMATION_ACTION_END";
            case 4:
                return "SET_RESOLUTION_ACTION_START";
            case 5:
                return "SET_RESOLUTION_ACTION_END";
            case 6:
                return "ZOOM_IN_ACTION_START";
            case 7:
                return "ZOOM_IN_ACTION_END";
            case 8:
                return "ZOOM_OUT_ACTION_START";
            case 9:
                return "ZOOM_OUT_ACTION_END";
            case 10:
                return "GET_ZOOM_ACTION_START";
            case 11:
                return "GET_ZOOM_ACTION_END";
            case 12:
                return "SET_ZOOM_ACTION_START";
            case 13:
                return "SET_ZOOM_ACTION_END";
            case 14:
                return "START_ZOOM_ACTION_START";
            case 15:
                return "START_ZOOM_ACTION_END";
            case 16:
                return "STOP_ZOOM_ACTION_START";
            case 17:
                return "STOP_ZOOM_ACTION_END";
            case 18:
                return "AUTO_FOCUS_ACTION_START";
            case 19:
                return "AUTO_FOCUS_ACTION_END";
            case 20:
                return "MULTI_AUTO_FOCUS_ACTION_START";
            case 21:
                return "MULTI_AUTO_FOCUS_ACTION_END";
            case 22:
                return "SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_START";
            case 23:
                return "SET_TOUCH_AUTO_FOCUS_OPTION_ACTION_END";
            case 24:
                return "TOUCH_AUTO_FOCUS_ACTION_START";
            case 25:
                return "TOUCH_AUTO_FOCUS_ACTION_END";
            case 26:
                return "RELEASE_SELF_TIMER_ACTION_START";
            case 27:
                return "RELEASE_SELF_TIMER_ACTION_END";
            case 28:
                return "SHOT_ACTION_START";
            case 29:
                return "SHOT_ACTION_END";
            case 30:
                return "SHOT_WITH_GPS_ACTION_START";
            case 31:
                return "SHOT_WITH_GPS_ACTION_END";
            case 32:
                return "SET_LED_ACTION_START";
            case 33:
                return "SET_LED_ACTION_END";
            case 34:
                return "SET_FLASH_ACTION_START";
            case 35:
                return "SET_FLASH_ACTION_END";
            case 36:
                return "SET_STREAM_QUALITY_ACTION_START";
            case 37:
                return "SET_STREAM_QUALITY_ACTION_END";
            case 38:
                return "SET_MOVIE_STREAM_QUALITY_ACTION_START";
            case 39:
                return "SET_MOVIE_STREAM_QUALITY_ACTION_END";
            case 40:
                return "SHUTTER_UP_ACTION_START";
            case 41:
                return "SHUTTER_UP_ACTION_END";
            case 42:
                return "SET_SHUTTER_SPEED_ACTION_START";
            case 43:
                return "SET_SHUTTER_SPEED_ACTION_END";
            case 44:
                return "SET_APERTURE_ACTION_START";
            case 45:
                return "SET_APERTURE_ACTION_END";
            case 46:
                return "SET_EV_ACTION_START";
            case 47:
                return "SET_EV_ACTION_END";
            case 48:
                return "SET_ISO_ACTION_START";
            case 49:
                return "SET_ISO_ACTION_END";
            case 50:
                return "SET_WB_ACTION_START";
            case 51:
                return "SET_WB_ACTION_END";
            case 52:
                return "SET_METERING_ACTION_START";
            case 53:
                return "SET_METERING_ACTION_END";
            case 54:
                return "SET_AF_MODE_ACTION_START";
            case 55:
                return "SET_AF_MODE_ACTION_END";
            case ActionStatus.SET_AF_AREA_ACTION_START /* 56 */:
                return "SET_AF_AREA_ACTION_START";
            case ActionStatus.SET_AF_AREA_ACTION_END /* 57 */:
                return "SET_AF_AREA_ACTION_END";
            case ActionStatus.SET_DRIVE_ACTION_START /* 58 */:
                return "SET_DRIVE_ACTION_START";
            case ActionStatus.SET_DRIVE_ACTION_END /* 59 */:
                return "SET_DRIVE_ACTION_END";
            case 60:
                return "START_SHOT_ACTION_START";
            case 61:
                return "START_SHOT_ACTION_END";
            case 62:
                return "STOP_SHOT_ACTION_START";
            case 63:
                return "STOP_SHOT_ACTION_END";
            case 64:
                return "SET_DIAL_MODE_ACTION_START";
            case ActionStatus.SET_DIAL_MODE_ACTION_END /* 65 */:
                return "SET_DIAL_MODE_ACTION_END";
            case ActionStatus.SET_QUALITY_ACTION_START /* 66 */:
                return "SET_QUALITY_ACTION_START";
            case ActionStatus.SET_QUALITY_ACTION_END /* 67 */:
                return "SET_QUALITY_ACTION_END";
            case ActionStatus.SET_MOVIE_RESOLUTION_ACTION_START /* 68 */:
                return "SET_MOVIE_RESOLUTION_ACTION_START";
            case ActionStatus.SET_MOVIE_RESOLUTION_ACTION_END /* 69 */:
                return "SET_MOVIE_RESOLUTION_ACTION_END";
            case 70:
                return "START_RECORD_ACTION_START";
            case 71:
                return "START_RECORD_ACTION_END";
            case 72:
                return "STOP_RECORD_ACTION_START";
            case 73:
                return "STOP_RECORD_ACTION_END";
            case 74:
                return "SET_FILE_SAVE_ACTION_START";
            case ActionStatus.SET_FILE_SAVE_ACTION_END /* 75 */:
                return "SET_FILE_SAVE_ACTION_END";
            case ActionStatus.TOUCH_AF_POINT_ACTION_START /* 76 */:
                return "TOUCH_AF_POINT_ACTION_START";
            case ActionStatus.TOUCH_AF_POINT_ACTION_END /* 77 */:
                return "TOUCH_AF_POINT_ACTION_END";
            case ActionStatus.CANCEL_TOUCH_AF_MOVIE_ACTION_START /* 78 */:
                return "CANCEL_TOUCH_AF_MOVIE_ACTION_START";
            case ActionStatus.CANCEL_TOUCH_AF_MOVIE_ACTION_END /* 79 */:
                return "CANCEL_TOUCH_AF_MOVIE_ACTION_END";
            case 80:
                return "TOUCH_AF_MOVIE_ACTION_START";
            case ActionStatus.TOUCH_AF_MOVIE_ACTION_END /* 81 */:
                return "TOUCH_AF_MOVIE_ACTION_END";
            case ActionStatus.BROWSE_ACTION_START /* 82 */:
                return "BROWSE_ACTION_START";
            case ActionStatus.BROWSE_ACTION_END /* 83 */:
                return "BROWSE_ACTION_END";
            case ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                return "GET_DEVICE_CONFIGURATION_ACTION_START";
            case ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_END /* 85 */:
                return "GET_DEVICE_CONFIGURATION_ACTION_END";
            case ActionStatus.SET_OPERATION_STATE_ACTION_START /* 86 */:
                return "SET_OPERATION_STATE_ACTION_START";
            case ActionStatus.SET_OPERATION_STATE_ACTION_END /* 87 */:
                return "SET_OPERATION_STATE_ACTION_END";
            case ActionStatus.SET_TOTAL_COPY_ITEMS_ACTION_START /* 88 */:
                return "SET_TOTAL_COPY_ITEMS_ACTION_START";
            case ActionStatus.SET_TOTAL_COPY_ITEMS_ACTION_END /* 89 */:
                return "SET_TOTAL_COPY_ITEMS_ACTION_END";
        }
    }

    public boolean doAction(int actionID, String param) {
        Trace.d(this.TAG, "start doAction() action : " + actionID + " param : " + param);
        boolean result = false;
        if (getAction(actionID) != null) {
            DSCCommand command = new DSCCommand(actionID, param);
            this.commandManager.queueCommand(command);
            result = true;
        }
        Trace.d(this.TAG, "end doAction() action : " + actionID + " result : " + result);
        return result;
    }

    private void requestAction(int actionID, String param) {
        switch (actionID) {
            case 1:
                getInformateionAct(param);
                break;
            case 2:
                setResolutionAct(param);
                break;
            case 3:
                zoomINAct();
                break;
            case 4:
                zoomOUTAct();
                break;
            case 5:
                getZoomAct();
                break;
            case 6:
                setZoomAct(param);
                break;
            case 7:
                startZoomAct(param);
                break;
            case 8:
                stopZoomAct(param);
                break;
            case 9:
                afAct();
                break;
            case 10:
                multiAFAct();
                break;
            case 11:
                setTouchAFOptionAct(param);
                break;
            case 12:
                touchAFAct(param);
                break;
            case 13:
                releaseSelfTimerAct();
                break;
            case 14:
                shotAct(param);
                break;
            case 15:
                shotWithGPSAct();
                break;
            case 16:
                setLEDAct(param);
                break;
            case 17:
                setFlashAct(param);
                break;
            case 18:
                setStreamQualityAct(param);
                break;
            case 19:
                setMovieStreamQualityAct(param);
                break;
            case 20:
                shutterUpAct();
                break;
            case 21:
                setShutterSpeedAct(param);
                break;
            case 22:
                setApertureAct(param);
                break;
            case 23:
                setEVAct(param);
                break;
            case 24:
                setISOAct(param);
                break;
            case 25:
                setWBAct(param);
                break;
            case 26:
                setMeteringAct(param);
                break;
            case 27:
                setAFModeAct(param);
                break;
            case 28:
                setAFAreaAct(param);
                break;
            case 29:
                setDriveAct(param);
                break;
            case 30:
                startShotAct(param);
                break;
            case 31:
                stopShotAct(param);
                break;
            case 32:
                setDialModeAct(param);
                break;
            case 33:
                setQualityAct(param);
                break;
            case 34:
                setMovieResolutionAct(param);
                break;
            case 35:
                startRecord();
                break;
            case 36:
                stopRecord();
                break;
            case 37:
                setFileSave(param);
                break;
            case 39:
                cancelTouchAFMovie(param);
                break;
            case 40:
                touchAFMovie(param);
                break;
            case 41:
                browse(param);
                break;
            case 42:
                getDeviceConfiguration(param);
                break;
            case 43:
                setOperationState(param);
                break;
            case 44:
                setTotalCopyItems(param);
                break;
        }
    }

    public void getInformateionAct(String param) {
        this.GetInformationAct.setArgumentValue("GPSINFO", param);
        sendEvent(2, true);
        boolean result = this.GetInformationAct.postControlAction();
        if (result) {
            resetCamera(this.GetInformationAct.getActionNode());
            setConnected(true);
        }
        sendEvent(3, Boolean.valueOf(result));
    }

    public void setResolutionAct(String param) {
        this.SetResolutionAct.setArgumentValue("RESOLUTION", param);
        sendEvent(4, true);
        boolean result = this.SetResolutionAct.postControlAction();
        if (result) {
            Node node = this.SetResolutionAct.getActionNode();
            ActionData userData = (ActionData) node.getUserData();
            ControlResponse control = userData.getControlResponse();
            Node bodyNode = control.getBodyNode();
            for (int i = 0; i < bodyNode.getNNodes(); i++) {
                Node cnode = bodyNode.getNode(i);
                for (int j = 0; j < cnode.getNNodes(); j++) {
                    Node ccnode = cnode.getNode(j);
                    if (ccnode.getName().equals(ActionNode.AVAILSHOTS)) {
                        if (!ccnode.getValue().equals("")) {
                            setAvailShots(ccnode.getValue());
                        }
                    } else if (ccnode.getName().equals(ActionNode.RESOLUTION_RATIO)) {
                        setRatioValue(ccnode.getValue());
                        for (int k = 0; k < getRatioOffsetMenuItems().size(); k++) {
                            if (getRatioOffsetMenuItems().get(k).getRatio().equals(ccnode.getValue())) {
                                setRatioOffsetValue(getRatioOffsetMenuItems().get(k));
                                break;
                            }
                        }
                    }
                }
            }
        }
        sendEvent(5, Boolean.valueOf(result));
    }

    public void zoomINAct() {
        sendEvent(6, true);
        boolean result = this.ZoomINAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.ZoomINAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (name.equals("CURRENTZOOM")) {
                    setDefaultZoom(value);
                }
            }
        }
        sendEvent(7, Boolean.valueOf(result));
    }

    public void zoomOUTAct() {
        sendEvent(8, true);
        boolean result = this.ZoomOUTAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.ZoomOUTAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (name.equals("CURRENTZOOM")) {
                    setDefaultZoom(value);
                }
            }
        }
        sendEvent(9, Boolean.valueOf(result));
    }

    public void getZoomAct() {
        sendEvent(10, true);
        boolean result = this.GetZoomAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.GetZoomAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (name.equals("CURRENTZOOM")) {
                    setDefaultZoom(value);
                }
            }
        }
        sendEvent(11, Boolean.valueOf(result));
    }

    public void setZoomAct(String param) {
        this.SetZoomAct.setArgumentValue("ZOOMLEVEL", param);
        sendEvent(12, true);
        boolean result = this.SetZoomAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.SetZoomAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (name.equals("CURRENTZOOM")) {
                    setDefaultZoom(value);
                }
            }
        }
        sendEvent(13, Boolean.valueOf(result));
    }

    public void startZoomAct(String param) {
        this.StartZoomAct.setArgumentValue("ZoomDirection", param);
        sendEvent(14, true);
        boolean result = this.StartZoomAct.postControlAction();
        sendEvent(15, Boolean.valueOf(result));
    }

    public void stopZoomAct(String param) {
        this.StopZoomAct.setArgumentValue("ZoomDirection", param);
        sendEvent(16, true);
        boolean result = this.StopZoomAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.StopZoomAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (name.equals("CURRENTZOOM") && !value.equals("")) {
                    setDefaultZoom(value);
                }
            }
        }
        sendEvent(17, Boolean.valueOf(result));
    }

    public void afAct() {
        sendEvent(18, true);
        boolean result = this.AFAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.AFAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (!name.equals("AF_MF") && !value.equals("3") && name.equals("AFSTATUS")) {
                    value.equals("0");
                }
            }
        }
        sendEvent(19, Boolean.valueOf(result));
    }

    public void multiAFAct() {
        sendEvent(20, true);
        boolean result = this.MULTIAFAct.postControlAction();
        if (result) {
            ArgumentList outArgList = this.MULTIAFAct.getOutputArgumentList();
            int nOutArgs = outArgList.size();
            for (int n = 0; n < nOutArgs; n++) {
                Argument outArg = outArgList.getArgument(n);
                String name = outArg.getName();
                String value = outArg.getValue();
                if (!name.equals("AF_MF") && name.equals("AFSTATUS")) {
                    value.equals("AFFAIL");
                }
            }
        }
        sendEvent(21, Boolean.valueOf(result));
    }

    public void setTouchAFOptionAct(String param) {
        this.SetTouchAFOptionAct.setArgumentValue("TOUCH_AF_OPTION", param);
        sendEvent(22, true);
        boolean result = this.SetTouchAFOptionAct.postControlAction();
        if (result) {
            setTouchAFValue(this.SetTouchAFOptionAct.getArgumentValue("TOUCH_AF_OPTION"));
        }
        sendEvent(23, Boolean.valueOf(result));
    }

    public void touchAFAct(String param) {
        this.TouchAFAct.setArgumentValue(ActionNode.AF_POSITION, param);
        sendEvent(24, true);
        boolean result = this.TouchAFAct.postControlAction();
        sendEvent(25, Boolean.valueOf(result));
    }

    public void releaseSelfTimerAct() {
        this.ReleaseSelfTimerAct.setArgumentValue("RELEASETIMER", "0");
        sendEvent(26, true);
        boolean result = this.ReleaseSelfTimerAct.postControlAction();
        sendEvent(27, Boolean.valueOf(result));
    }

    public void shotAct(String param) {
        this.ShotAct.setArgumentValue("REQUESTIMAGE", param);
        sendEvent(28, true);
        setAFShotResult("");
        boolean result = this.ShotAct.postControlAction();
        if (result) {
            Node node = this.ShotAct.getActionNode();
            ActionData userData = (ActionData) node.getUserData();
            ControlResponse control = userData.getControlResponse();
            Node bodyNode = control.getBodyNode();
            for (int i = 0; i < bodyNode.getNNodes(); i++) {
                Node cnode = bodyNode.getNode(i);
                for (int j = 0; j < cnode.getNNodes(); j++) {
                    Node ccnode = cnode.getNode(j);
                    if (ccnode.getName().equals("AFSHOTRESULT")) {
                        setAFShotResult(ccnode.getValue());
                    } else if (ccnode.getName().equals(ActionNode.AVAILSHOTS)) {
                        if (!ccnode.getValue().equals("")) {
                            setAvailShots(ccnode.getValue());
                        }
                    } else if (ccnode.getName().equals(ActionNode.CURRENT_FLASH_DISPLAY)) {
                        setCurrentFlashDisplay(ccnode.getValue());
                    } else if (ccnode.getName().equals(ActionNode.REMAIN_REC_TIME)) {
                        setRemainRecTimeValue(ccnode.getValue());
                    }
                }
            }
        }
        sendEvent(29, Boolean.valueOf(result));
    }

    public void shotWithGPSAct() {
        sendEvent(30, true);
        this.ShotWithGPSAct.postControlAction();
        sendEvent(31, null);
    }

    public void setLEDAct(String param) {
        this.SetLEDAct.setArgumentValue("LEDTIME", param);
        sendEvent(32, true);
        boolean result = this.SetLEDAct.postControlAction();
        if (result) {
            String menu = this.SetLEDAct.getArgumentValue("LEDTIME");
            if (menu.equals("2")) {
                setCurrentLEDTimeMenuItem("2sec");
                setDriveValue("Timer (2sec)");
            } else if (menu.equals("5")) {
                setCurrentLEDTimeMenuItem("5sec");
                setDriveValue("Timer (5sec)");
            } else if (menu.equals("10")) {
                setCurrentLEDTimeMenuItem("10sec");
                setDriveValue("Timer (10sec)");
            } else if (menu.equals("Double")) {
                setCurrentLEDTimeMenuItem("Double");
                setDriveValue("Timer (Double)");
            } else {
                setCurrentLEDTimeMenuItem("Off");
                setDriveValue("SINGLE");
            }
        }
        sendEvent(33, Boolean.valueOf(result));
    }

    public void setFlashAct(String param) {
        this.SetFlashAct.setArgumentValue("FLASHMODE", param);
        sendEvent(34, true);
        boolean result = this.SetFlashAct.postControlAction();
        if (result) {
            setDefaultFlash(this.SetFlashAct.getArgumentValue("FLASHMODE"));
            setCurrentFlashDisplay(this.SetFlashAct.getArgumentValue("FLASHMODE"));
            Node node = this.SetFlashAct.getActionNode();
            ActionData userData = (ActionData) node.getUserData();
            ControlResponse control = userData.getControlResponse();
            Node rootNode = control.getBodyNode();
            for (int i = 0; i < rootNode.getNNodes(); i++) {
                Node cnode = rootNode.getNode(i);
                for (int j = 0; j < cnode.getNNodes(); j++) {
                    Node ccnode = cnode.getNode(j);
                    if (ccnode.getName().equals(ActionNode.CURRENT_FLASH_DISPLAY)) {
                        if (ccnode.getValue().equals(Service.MAJOR_VALUE)) {
                            setCurrentFlashDisplay("auto");
                            break;
                        }
                        if (ccnode.getValue().equals("0")) {
                            setCurrentFlashDisplay("off");
                            break;
                        } else if (ccnode.getValue().equals("On")) {
                            setCurrentFlashDisplay(getDefaultFlash());
                            break;
                        } else {
                            setCurrentFlashDisplay(ccnode.getValue());
                            break;
                        }
                    }
                }
            }
        }
        sendEvent(35, Boolean.valueOf(result));
    }

    public void setStreamQualityAct(String param) {
        String menuName;
        this.SetStreamQualityAct.setArgumentValue("Quality", param);
        sendEvent(36, true);
        boolean result = this.SetStreamQualityAct.postControlAction();
        if (result) {
            setCurrentStreamQuality(this.SetStreamQualityAct.getArgumentValue("Quality"));
            if (getCurrentStreamQuality().equals("low")) {
                menuName = ActionNode.QUALITY_LOW_URL;
            } else {
                menuName = ActionNode.QUALITY_HIGH_URL;
            }
            for (int i = 0; i < getStreamUrlMenuItems().size(); i++) {
                DSCMenuItem dscMenuItem = getStreamUrlMenuItems().get(i);
                if (dscMenuItem.getName().equals(menuName)) {
                    setCurrentStreamUrl(dscMenuItem.getValue());
                    break;
                }
            }
        }
        sendEvent(37, Boolean.valueOf(result));
    }

    public void setMovieStreamQualityAct(String param) {
        String menuName;
        this.SetMovieStreamQualityAct.setArgumentValue("Quality", param);
        sendEvent(38, true);
        boolean result = this.SetMovieStreamQualityAct.postControlAction();
        if (result) {
            setCurrentStreamQuality(this.SetMovieStreamQualityAct.getArgumentValue("Quality"));
            if (getCurrentStreamQuality().equals("low")) {
                menuName = "QualityLowMjpgUrl";
            } else {
                menuName = "QualityHighMjpgUrl";
            }
            for (int i = 0; i < getStreamUrlMenuItems().size(); i++) {
                DSCMenuItem dscMenuItem = getStreamUrlMenuItems().get(i);
                if (dscMenuItem.getName().equals(menuName)) {
                    setCurrentStreamUrl(dscMenuItem.getValue());
                    break;
                }
            }
        }
        sendEvent(39, Boolean.valueOf(result));
    }

    public void shutterUpAct() {
        sendEvent(40, true);
        boolean result = this.ShutterUpAct.postControlAction();
        sendEvent(41, Boolean.valueOf(result));
    }

    public void setShutterSpeedAct(String param) {
        this.SetShutterSpeedAct.setArgumentValue(ActionNode.SHUTTER_SPEED_VALUE, param);
        sendEvent(42, true);
        boolean result = this.SetShutterSpeedAct.postControlAction();
        if (result) {
            setShutterSpeedValue(this.SetShutterSpeedAct.getArgumentValue(ActionNode.SHUTTER_SPEED_VALUE));
            resetCamera(this.SetShutterSpeedAct.getActionNode());
        }
        sendEvent(43, Boolean.valueOf(result));
    }

    public void setApertureAct(String param) {
        this.SetApertureAct.setArgumentValue(ActionNode.APERTURE_VALUE, param);
        sendEvent(44, true);
        boolean result = this.SetApertureAct.postControlAction();
        if (result) {
            setApertureValue(this.SetApertureAct.getArgumentValue(ActionNode.APERTURE_VALUE));
            resetCamera(this.SetApertureAct.getActionNode());
        }
        sendEvent(45, Boolean.valueOf(result));
    }

    public void setEVAct(String param) {
        this.SetEVAct.setArgumentValue(ActionNode.EV_VALUE, param);
        sendEvent(46, true);
        boolean result = this.SetEVAct.postControlAction();
        if (result) {
            setEVValue(this.SetEVAct.getArgumentValue(ActionNode.EV_VALUE));
            resetCamera(this.SetEVAct.getActionNode());
        }
        sendEvent(47, Boolean.valueOf(result));
    }

    public void setISOAct(String param) {
        this.SetISOAct.setArgumentValue(ActionNode.ISO_VALUE, param);
        sendEvent(48, true);
        boolean result = this.SetISOAct.postControlAction();
        if (result) {
            setISOValue(this.SetISOAct.getArgumentValue(ActionNode.ISO_VALUE));
            resetCamera(this.SetISOAct.getActionNode());
        }
        sendEvent(49, Boolean.valueOf(result));
    }

    public void setWBAct(String param) {
        this.SetWBAct.setArgumentValue(ActionNode.WB_VALUE, param);
        sendEvent(50, true);
        boolean result = this.SetWBAct.postControlAction();
        if (result) {
            setWBValue(this.SetWBAct.getArgumentValue(ActionNode.WB_VALUE));
        }
        sendEvent(51, Boolean.valueOf(result));
    }

    public void setMeteringAct(String param) {
        this.SetMeteringAct.setArgumentValue(ActionNode.METERING_VALUE, param);
        sendEvent(52, true);
        boolean result = this.SetMeteringAct.postControlAction();
        if (result) {
            setMeteringValue(this.SetMeteringAct.getArgumentValue(ActionNode.METERING_VALUE));
        }
        sendEvent(53, Boolean.valueOf(result));
    }

    public void setAFModeAct(String param) {
        this.SetAFModeAct.setArgumentValue(ActionNode.AF_MODE_VALUE, param);
        sendEvent(54, true);
        boolean result = this.SetAFModeAct.postControlAction();
        if (result) {
            setAFModeValue(this.SetAFModeAct.getArgumentValue(ActionNode.AF_MODE_VALUE));
        }
        sendEvent(55, Boolean.valueOf(result));
    }

    public void setAFAreaAct(String param) {
        this.SetAFAreaAct.setArgumentValue(ActionNode.AF_AREA_VALUE, param);
        sendEvent(56, true);
        boolean result = this.SetAFAreaAct.postControlAction();
        if (result) {
            setAFAreaValue(this.SetAFAreaAct.getArgumentValue(ActionNode.AF_AREA_VALUE));
        }
        sendEvent(57, Boolean.valueOf(result));
    }

    public void setDriveAct(String param) {
        this.SetDriveAct.setArgumentValue(ActionNode.DRIVE_VALUE, param);
        sendEvent(58, true);
        boolean result = this.SetDriveAct.postControlAction();
        if (result) {
            if (this.SetDriveAct.getArgumentValue(ActionNode.DRIVE_VALUE).toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)")) {
                setCurrentLEDTimeMenuItem("Timer (2sec)");
            } else if (this.SetDriveAct.getArgumentValue(ActionNode.DRIVE_VALUE).toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)")) {
                setCurrentLEDTimeMenuItem("Timer (5sec)");
            } else if (this.SetDriveAct.getArgumentValue(ActionNode.DRIVE_VALUE).toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)")) {
                setCurrentLEDTimeMenuItem("Timer (10sec)");
            } else {
                setCurrentLEDTimeMenuItem("Single");
            }
            setDriveValue(this.SetDriveAct.getArgumentValue(ActionNode.DRIVE_VALUE));
        }
        sendEvent(59, Boolean.valueOf(result));
    }

    public void startShotAct(String param) {
        this.StartShotAct.setArgumentValue(ActionNode.DRIVE_VALUE, param);
        sendEvent(60, true);
        boolean result = this.StartShotAct.postControlAction();
        sendEvent(61, Boolean.valueOf(result));
    }

    public void stopShotAct(String param) {
        this.StopShotAct.setArgumentValue(ActionNode.DRIVE_VALUE, param);
        sendEvent(62, true);
        boolean result = this.StopShotAct.postControlAction();
        sendEvent(63, Boolean.valueOf(result));
    }

    public void setDialModeAct(String param) {
        this.SetDialModeAct.setArgumentValue(ActionNode.DIAL_MODE_VALUE, param);
        sendEvent(64, true);
        boolean result = this.SetDialModeAct.postControlAction();
        if (result) {
            resetCamera(this.SetDialModeAct.getActionNode());
        }
        sendEvent(65, Boolean.valueOf(result));
    }

    public void setQualityAct(String param) {
        this.SetQualityAct.setArgumentValue(ActionNode.QUALITY_VALUE, param);
        sendEvent(66, true);
        boolean result = this.SetQualityAct.postControlAction();
        if (result) {
            setQualityValue(this.SetQualityAct.getArgumentValue(ActionNode.QUALITY_VALUE));
            setAvailShots(this.SetQualityAct.getArgumentValue(ActionNode.AVAILSHOTS));
            if (getRatioOffsetMenuItems().size() > 0) {
                if (getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW")) {
                    setRatioValue("3:2");
                    setRatioOffsetValue(getRatioOffsetMenuItems().get(0));
                } else {
                    DSCResolution resolution = getResolutionMenuItems().get(Integer.parseInt(getDefaultResolutionIndex()));
                    for (int i = 0; i < getRatioOffsetMenuItems().size(); i++) {
                        DSCResolution ratioOffset = getRatioOffsetMenuItems().get(i);
                        if (resolution.getRatio().equals(ratioOffset.getRatio())) {
                            setRatioValue(ratioOffset.getRatio());
                            setRatioOffsetValue(ratioOffset);
                            break;
                        }
                    }
                }
            }
        }
        sendEvent(67, Boolean.valueOf(result));
    }

    public void setMovieResolutionAct(String param) {
        this.SetMovieResolutionAct.setArgumentValue(ActionNode.MOVIE_RESOLUTION_VALUE, param);
        sendEvent(68, true);
        boolean result = this.SetMovieResolutionAct.postControlAction();
        if (result) {
            setRemainRecTimeValue(this.SetMovieResolutionAct.getArgumentValue(ActionNode.REMAIN_REC_TIME));
            setMovieResolutionRatio(this.SetMovieResolutionAct.getArgumentValue("MovieResolutionRatio"));
            setMovieResolutionValue(this.SetMovieResolutionAct.getArgumentValue(ActionNode.MOVIE_RESOLUTION_VALUE));
        }
        sendEvent(69, Boolean.valueOf(result));
    }

    public void startRecord() {
        sendEvent(70, true);
        boolean result = this.StartRecordAct.postControlAction();
        if (result) {
            setRemainRecTimeValue(this.StartRecordAct.getArgumentValue(ActionNode.REMAIN_REC_TIME));
        }
        sendEvent(71, Boolean.valueOf(result));
    }

    public void stopRecord() {
        String menuName;
        sendEvent(72, true);
        boolean result = this.StopRecordAct.postControlAction();
        if (result) {
            setRemainRecTimeValue(this.StopRecordAct.getArgumentValue(ActionNode.REMAIN_REC_TIME));
            if (!this.StopRecordAct.getArgumentValue(ActionNode.AVAILSHOTS).equals("")) {
                setAvailShots(this.StopRecordAct.getArgumentValue(ActionNode.AVAILSHOTS));
            }
            if (getCurrentStreamQuality().equals("low")) {
                menuName = ActionNode.QUALITY_LOW_URL;
            } else {
                menuName = ActionNode.QUALITY_HIGH_URL;
            }
            for (int i = 0; i < getStreamUrlMenuItems().size(); i++) {
                DSCMenuItem dscMenuItem = getStreamUrlMenuItems().get(i);
                if (dscMenuItem.getName().equals(menuName)) {
                    setCurrentStreamUrl(dscMenuItem.getValue());
                    break;
                }
            }
        }
        sendEvent(73, Boolean.valueOf(result));
    }

    public void setFileSave(String param) {
        this.SetFileSaveAct.setArgumentValue(ActionNode.FILE_SAVE_VALUE, param);
        sendEvent(74, true);
        boolean result = this.SetFileSaveAct.postControlAction();
        if (result) {
            setFileSaveValue(this.SetFileSaveAct.getArgumentValue(ActionNode.FILE_SAVE_VALUE));
        }
        sendEvent(75, Boolean.valueOf(result));
    }

    public void touchAFPoint(String param) {
        this.TouchAFPointAct.setArgumentValue(ActionNode.AF_POSITION, param);
        sendEvent(76, true);
        boolean result = this.TouchAFPointAct.postControlAction();
        sendEvent(77, Boolean.valueOf(result));
    }

    public void cancelTouchAFMovie(String param) {
        Trace.d(this.TAG, "start cancelTouchAFMovie() param : " + param);
        this.CancelTouchAFMovieAct.setArgumentValue(ActionNode.MOVIE_TOUCH_AF_STATUS, param);
        sendEvent(78, true);
        boolean result = this.CancelTouchAFMovieAct.postControlAction();
        sendEvent(79, Boolean.valueOf(result));
    }

    public void touchAFMovie(String param) {
        this.TouchAFMovieAct.setArgumentValue(ActionNode.AF_POSITION, param);
        sendEvent(80, true);
        boolean result = this.TouchAFMovieAct.postControlAction();
        sendEvent(81, Boolean.valueOf(result));
    }

    public void browse(String param) {
        sendEvent(82, true);
        this.BrowseAct.postControlAction();
        sendEvent(83, null);
    }

    public void getDeviceConfiguration(String param) {
        sendEvent(84, true);
        boolean result = this.GetDeviceConfigurationAct.postControlAction();
        if (result) {
            if (this.GetDeviceConfigurationAct.getArgumentValue("TranferOption").equals("0")) {
                setResizeMode(true);
            } else {
                setResizeMode(false);
            }
        }
        sendEvent(85, null);
    }

    public void setOperationState(String param) {
        this.SetOperationStateAct.setArgumentValue("StateEvent", param);
        sendEvent(86, true);
        this.SetOperationStateAct.postControlAction();
        sendEvent(87, null);
    }

    public void setTotalCopyItems(String param) {
        this.SetTotalCopyItemsAct.setArgumentValue("TotalNumber", param);
        sendEvent(88, true);
        boolean result = this.SetTotalCopyItemsAct.postControlAction();
        sendEvent(89, Boolean.valueOf(result));
    }

    public void resetCamera(Node node) {
        ActionData userData = (ActionData) node.getUserData();
        ControlResponse control = userData.getControlResponse();
        Node rootNode = control.getBodyNode();
        rootNode.print();
        for (int i = 0; i < rootNode.getNNodes(); i++) {
            Node cnode = rootNode.getNode(i);
            for (int j = 0; j < cnode.getNNodes(); j++) {
                Node ccnode = cnode.getNode(j);
                if (ccnode.getName().equals(ActionNode.GET_INFORMATION_RESULT)) {
                    for (int k = 0; k < ccnode.getNNodes(); k++) {
                        Node infoNode = ccnode.getNode(k);
                        if (infoNode.getName().equals(ActionNode.VERSION)) {
                            String versionInfo = infoNode.getValue();
                            if (versionInfo.contains("_")) {
                                setVersionPrefix(versionInfo.split("_", 3)[0]);
                                setVersion(versionInfo.split("_", 3)[1]);
                            } else {
                                setVersion(versionInfo);
                            }
                        } else if (infoNode.getName().equals(ActionNode.DIAL_MODE_VALUE)) {
                            for (int l = 0; l < infoNode.getNNodes(); l++) {
                                Node dialModeNode = infoNode.getNode(l);
                                if (dialModeNode.getName().equals("Supports")) {
                                    if (dialModeNode.getNNodes() > 0) {
                                        getDialModeMenuItems().clear();
                                        for (int m = 0; m < dialModeNode.getNNodes(); m++) {
                                            Node dialModeSupportNode = dialModeNode.getNode(m);
                                            getDialModeMenuItems().add(dialModeSupportNode.getValue());
                                        }
                                    }
                                } else if (dialModeNode.getName().equals("Default")) {
                                    setDialModeValue(dialModeNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.RESOLUTIONS)) {
                            getResolutionMenuItems().clear();
                            getResolutionMenuItemsDim().clear();
                            for (int l2 = 0; l2 < infoNode.getNNodes(); l2++) {
                                Node resolutionsNode = infoNode.getNode(l2);
                                if (resolutionsNode.getName().equals("Resolution")) {
                                    DSCResolution dscResolution = new DSCResolution();
                                    for (int m2 = 0; m2 < resolutionsNode.getNNodes(); m2++) {
                                        Node resolutionNode = resolutionsNode.getNode(m2);
                                        if (resolutionNode.getName().equals("Width")) {
                                            int value = Integer.parseInt(resolutionNode.getValue());
                                            dscResolution.setWidth(value);
                                        } else if (resolutionNode.getName().equals("Height")) {
                                            int value2 = Integer.parseInt(resolutionNode.getValue());
                                            dscResolution.setHeight(value2);
                                        } else if (resolutionNode.getName().equals("Ratio")) {
                                            String value3 = resolutionNode.getValue();
                                            dscResolution.setRatio(value3);
                                        }
                                    }
                                    getResolutionMenuItems().add(dscResolution);
                                } else if (resolutionsNode.getName().equals("Default")) {
                                    if (Utils.isNumeric(resolutionsNode.getValue())) {
                                        setDefaultResolutionIndex(resolutionsNode.getValue());
                                    } else {
                                        for (int m3 = 0; m3 < getResolutionMenuItems().size(); m3++) {
                                            DSCResolution dscResolution2 = getResolutionMenuItems().get(m3);
                                            String title = String.format("%dx%d", Integer.valueOf(dscResolution2.getWidth()), Integer.valueOf(dscResolution2.getHeight()));
                                            if (resolutionsNode.getValue().equals(title)) {
                                                setDefaultResolutionIndex(String.valueOf(m3));
                                                setRatioValue(getResolutionMenuItems().get(m3).getRatio());
                                            }
                                        }
                                    }
                                } else if (resolutionsNode.getName().equals("Disable")) {
                                    for (int m4 = 0; m4 < resolutionsNode.getNNodes(); m4++) {
                                        Node disableNode = resolutionsNode.getNode(m4);
                                        DSCMenuItem dscMenuItem = new DSCMenuItem();
                                        dscMenuItem.setName(disableNode.getName());
                                        dscMenuItem.setValue(disableNode.getValue());
                                        getResolutionMenuItemsDim().add(dscMenuItem);
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.FLASH)) {
                            getFlashMenuItems().clear();
                            for (int l3 = 0; l3 < infoNode.getNNodes(); l3++) {
                                Node flashNode = infoNode.getNode(l3);
                                if (flashNode.getName().equals("Supports")) {
                                    if (flashNode.getNNodes() > 0) {
                                        for (int m5 = 0; m5 < flashNode.getNNodes(); m5++) {
                                            getFlashMenuItems().add(flashNode.getNode(m5).getValue());
                                        }
                                    }
                                } else if (flashNode.getName().equals(ActionNode.DEFAULT_FLASH)) {
                                    setDefaultFlash(flashNode.getValue());
                                    setCurrentFlashDisplay(flashNode.getValue());
                                } else if (flashNode.getName().equals("Support")) {
                                    getFlashMenuItems().add(flashNode.getValue());
                                } else if (flashNode.getName().equals("Default")) {
                                    if (Utils.isNumeric(flashNode.getValue())) {
                                        setDefaultFlash(getFlashMenuItems().get(Integer.parseInt(flashNode.getValue())));
                                        setCurrentFlashDisplay(getFlashMenuItems().get(Integer.parseInt(flashNode.getValue())));
                                    } else {
                                        setDefaultFlash(flashNode.getValue());
                                        setCurrentFlashDisplay(flashNode.getValue());
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.FLASH_DISPLAY)) {
                            for (int l4 = 0; l4 < infoNode.getNNodes(); l4++) {
                                Node flashDisplayNode = infoNode.getNode(l4);
                                if (flashDisplayNode.getName().equals("Supports")) {
                                    if (flashDisplayNode.getNNodes() > 0) {
                                        getFlashDisplayMenuItems().clear();
                                        for (int m6 = 0; m6 < flashDisplayNode.getNNodes(); m6++) {
                                            Node flashDisplaySupportNode = flashDisplayNode.getNode(m6);
                                            getFlashDisplayMenuItems().add(flashDisplaySupportNode.getValue());
                                        }
                                    }
                                } else if (flashDisplayNode.getName().equals(ActionNode.CURRENT_FLASH_DISPLAY)) {
                                    if (Utils.isNumeric(flashDisplayNode.getValue())) {
                                        setCurrentFlashDisplay(getFlashDisplayMenuItems().get(Integer.parseInt(flashDisplayNode.getValue())));
                                    } else if (flashDisplayNode.getValue().equals("On")) {
                                        setCurrentFlashDisplay(getDefaultFlash());
                                    } else {
                                        setCurrentFlashDisplay(flashDisplayNode.getValue());
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.DEFAULT_FOCUS_STATE)) {
                            setDefaultFocusState(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.ZOOM_INFO)) {
                            for (int l5 = 0; l5 < infoNode.getNNodes(); l5++) {
                                Node zoomNode = infoNode.getNode(l5);
                                if (zoomNode.getName().equals(ActionNode.DEFAULT_ZOOM)) {
                                    setDefaultZoom(zoomNode.getValue());
                                } else if (zoomNode.getName().equals(ActionNode.MAX_ZOOM)) {
                                    if (RVFFunctionManager.isBigZoomMaxValue(CMInfo.getInstance().getConnectedSSID())) {
                                        setMaxZoom(String.valueOf(Integer.parseInt(zoomNode.getValue()) - 1));
                                    } else {
                                        setMaxZoom(zoomNode.getValue());
                                    }
                                } else if (zoomNode.getName().equals(ActionNode.MIN_ZOOM)) {
                                    setMinZoom(zoomNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.AUTO_FOCUS)) {
                            for (int l6 = 0; l6 < infoNode.getNNodes(); l6++) {
                                Node afNode = infoNode.getNode(l6);
                                if (afNode.getName().equals(ActionNode.AUTO_FOCUS_PRIORITY)) {
                                    setAFPriority(afNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.AVAILSHOTS)) {
                            if (!infoNode.getValue().equals("")) {
                                setAvailShots(infoNode.getValue());
                            }
                        } else if (infoNode.getName().equals(ActionNode.REMAIN_REC_TIME)) {
                            setRemainRecTimeValue(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.STREAM_QUALITY)) {
                            getStreamQualityMenuItems().clear();
                            for (int l7 = 0; l7 < infoNode.getNNodes(); l7++) {
                                Node streamQualityNode = infoNode.getNode(l7);
                                if (streamQualityNode.getName().equals("Quality")) {
                                    for (int m7 = 0; m7 < streamQualityNode.getNNodes(); m7++) {
                                        Node streamQualityOptionNode = streamQualityNode.getNode(m7);
                                        getStreamQualityMenuItems().add(streamQualityOptionNode.getValue());
                                    }
                                } else if (streamQualityNode.getName().equals("Default")) {
                                    setCurrentStreamQuality(streamQualityNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.ROTATION)) {
                            setRotation(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.EV_VALUE)) {
                            for (int l8 = 0; l8 < infoNode.getNNodes(); l8++) {
                                Node evNode = infoNode.getNode(l8);
                                if (evNode.getName().equals("Supports")) {
                                    if (evNode.getNNodes() > 0) {
                                        getEVMenuItems().clear();
                                        for (int m8 = 0; m8 < evNode.getNNodes(); m8++) {
                                            Node evSupportNode = evNode.getNode(m8);
                                            getEVMenuItems().add(evSupportNode.getValue());
                                        }
                                    }
                                } else if (evNode.getName().equals("Default")) {
                                    setEVValue(evNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.ISO_VALUE)) {
                            for (int l9 = 0; l9 < infoNode.getNNodes(); l9++) {
                                Node isoNode = infoNode.getNode(l9);
                                if (isoNode.getName().equals("Supports")) {
                                    if (isoNode.getNNodes() > 0) {
                                        getISOMenuItems().clear();
                                        for (int m9 = 0; m9 < isoNode.getNNodes(); m9++) {
                                            Node isoSupportNode = isoNode.getNode(m9);
                                            getISOMenuItems().add(isoSupportNode.getValue());
                                        }
                                    }
                                } else if (isoNode.getName().equals("Default")) {
                                    setISOValue(isoNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.WB_VALUE)) {
                            for (int l10 = 0; l10 < infoNode.getNNodes(); l10++) {
                                Node wbNode = infoNode.getNode(l10);
                                if (!wbNode.getName().equals("Supports")) {
                                    if (wbNode.getName().equals("Default")) {
                                        setWBValue(wbNode.getValue());
                                    }
                                } else if (wbNode.getNNodes() > 0) {
                                    getWBMenuItems().clear();
                                    for (int m10 = 0; m10 < wbNode.getNNodes(); m10++) {
                                        Node wbSupportNode = wbNode.getNode(m10);
                                        getWBMenuItems().add(wbSupportNode.getValue());
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.METERING_VALUE)) {
                            for (int l11 = 0; l11 < infoNode.getNNodes(); l11++) {
                                Node meteringNode = infoNode.getNode(l11);
                                if (meteringNode.getName().equals("Supports")) {
                                    if (meteringNode.getNNodes() > 0) {
                                        getMeteringMenuItems().clear();
                                        for (int m11 = 0; m11 < meteringNode.getNNodes(); m11++) {
                                            Node meteringSupportNode = meteringNode.getNode(m11);
                                            getMeteringMenuItems().add(meteringSupportNode.getValue());
                                        }
                                    }
                                } else if (meteringNode.getName().equals("Default")) {
                                    setMeteringValue(meteringNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.AF_MODE_VALUE)) {
                            for (int l12 = 0; l12 < infoNode.getNNodes(); l12++) {
                                Node afModeNode = infoNode.getNode(l12);
                                if (afModeNode.getName().equals("Supports")) {
                                    if (afModeNode.getNNodes() > 0) {
                                        getAFModeMenuItems().clear();
                                        for (int m12 = 0; m12 < afModeNode.getNNodes(); m12++) {
                                            Node afModeSupportNode = afModeNode.getNode(m12);
                                            getAFModeMenuItems().add(afModeSupportNode.getValue());
                                        }
                                    }
                                } else if (afModeNode.getName().equals("Default")) {
                                    setAFModeValue(afModeNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.AF_AREA_VALUE)) {
                            for (int l13 = 0; l13 < infoNode.getNNodes(); l13++) {
                                Node afAreaNode = infoNode.getNode(l13);
                                if (afAreaNode.getName().equals("Supports")) {
                                    getAFAreaMenuItems().clear();
                                    if (afAreaNode.getNNodes() > 0) {
                                        for (int m13 = 0; m13 < afAreaNode.getNNodes(); m13++) {
                                            getAFAreaMenuItems().add(afAreaNode.getNode(m13).getValue());
                                        }
                                    }
                                } else if (afAreaNode.getName().equals("Default")) {
                                    setAFAreaValue(afAreaNode.getValue());
                                } else if (afAreaNode.getName().equals("Disable")) {
                                    getAFAreaMenuItemsDim().clear();
                                    if (afAreaNode.getNNodes() > 0) {
                                        for (int m14 = 0; m14 < afAreaNode.getNNodes(); m14++) {
                                            DSCMenuItem item = new DSCMenuItem();
                                            Node afAreaSupportNode = afAreaNode.getNode(m14);
                                            item.setName(afAreaSupportNode.getName());
                                            item.setValue(afAreaSupportNode.getValue());
                                            getAFAreaMenuItemsDim().add(item);
                                        }
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.DRIVE_VALUE)) {
                            for (int l14 = 0; l14 < infoNode.getNNodes(); l14++) {
                                Node driveNode = infoNode.getNode(l14);
                                if (driveNode.getName().equals("Supports")) {
                                    if (driveNode.getNNodes() > 0) {
                                        getDriveMenuItems().clear();
                                        for (int m15 = 0; m15 < driveNode.getNNodes(); m15++) {
                                            getDriveMenuItems().add(driveNode.getNode(m15).getValue());
                                        }
                                    }
                                } else if (driveNode.getName().equals("Default")) {
                                    if (driveNode.getValue().toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)")) {
                                        setCurrentLEDTimeMenuItem("Timer (2sec)");
                                    } else if (driveNode.getValue().toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)")) {
                                        setCurrentLEDTimeMenuItem("Timer (5sec)");
                                    } else if (driveNode.getValue().toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)")) {
                                        setCurrentLEDTimeMenuItem("Timer (10sec)");
                                    } else {
                                        setCurrentLEDTimeMenuItem("Single");
                                    }
                                    setDriveValue(driveNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.SHUTTER_SPEED_VALUE)) {
                            for (int l15 = 0; l15 < infoNode.getNNodes(); l15++) {
                                Node shutterSpeedNode = infoNode.getNode(l15);
                                if (shutterSpeedNode.getName().equals("Supports")) {
                                    if (shutterSpeedNode.getNNodes() > 0) {
                                        getShutterSpeedMenuItems().clear();
                                        for (int m16 = 0; m16 < shutterSpeedNode.getNNodes(); m16++) {
                                            Node shutterSpeedSupportNode = shutterSpeedNode.getNode(m16);
                                            getShutterSpeedMenuItems().add(shutterSpeedSupportNode.getValue());
                                        }
                                    }
                                } else if (shutterSpeedNode.getName().equals("Default")) {
                                    setShutterSpeedValue(shutterSpeedNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.APERTURE_VALUE)) {
                            for (int l16 = 0; l16 < infoNode.getNNodes(); l16++) {
                                Node apertureNode = infoNode.getNode(l16);
                                if (apertureNode.getName().equals("Supports")) {
                                    if (apertureNode.getNNodes() > 0) {
                                        getApertureMenuItems().clear();
                                        for (int m17 = 0; m17 < apertureNode.getNNodes(); m17++) {
                                            Node apertureSupportNode = apertureNode.getNode(m17);
                                            getApertureMenuItems().add(apertureSupportNode.getValue());
                                        }
                                    }
                                } else if (apertureNode.getName().equals("Default")) {
                                    setApertureValue(apertureNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.QUALITY_VALUE)) {
                            for (int l17 = 0; l17 < infoNode.getNNodes(); l17++) {
                                Node qualityNode = infoNode.getNode(l17);
                                if (qualityNode.getName().equals("Supports")) {
                                    getQualityMenuItems().clear();
                                    setQualityValue("");
                                    if (qualityNode.getNNodes() > 0) {
                                        for (int m18 = 0; m18 < qualityNode.getNNodes(); m18++) {
                                            Node qualitySupportNode = qualityNode.getNode(m18);
                                            if (!qualitySupportNode.getValue().isEmpty()) {
                                                getQualityMenuItems().add(qualitySupportNode.getValue());
                                            }
                                        }
                                    }
                                } else if (qualityNode.getName().equals("Default")) {
                                    setQualityValue(qualityNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.MOVIE_RESOLUTION_VALUE)) {
                            for (int l18 = 0; l18 < infoNode.getNNodes(); l18++) {
                                Node movieResolutionNode = infoNode.getNode(l18);
                                if (movieResolutionNode.getName().equals("Supports")) {
                                    if (movieResolutionNode.getNNodes() > 0) {
                                        getMovieResolutionMenuItems().clear();
                                        for (int m19 = 0; m19 < movieResolutionNode.getNNodes(); m19++) {
                                            Node movieResolutionSupportNode = movieResolutionNode.getNode(m19);
                                            if (!movieResolutionSupportNode.getValue().isEmpty()) {
                                                if (movieResolutionSupportNode.getName().equals("S")) {
                                                    DSCMovieResolution dscMovieResolution = new DSCMovieResolution();
                                                    dscMovieResolution.setResolution(movieResolutionSupportNode.getValue());
                                                    getMovieResolutionMenuItems().add(dscMovieResolution);
                                                } else if (movieResolutionSupportNode.getName().equals("Ratio")) {
                                                    getMovieResolutionMenuItems().get(getMovieResolutionMenuItems().size() - 1).setRatio(movieResolutionSupportNode.getValue());
                                                }
                                            }
                                        }
                                    }
                                } else if (movieResolutionNode.getName().equals("Default")) {
                                    setMovieResolutionValue(movieResolutionNode.getValue());
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.MULTI_AF_MATRIX_SIZE)) {
                            setMultiAFMatrixSizeValue(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.FILE_SAVE_VALUE)) {
                            for (int l19 = 0; l19 < infoNode.getNNodes(); l19++) {
                                Node fileSaveNode = infoNode.getNode(l19);
                                if (fileSaveNode.getName().equals("Supports")) {
                                    getFileSaveMenuItems().clear();
                                    getFileSaveMenuItemsDim().clear();
                                    if (fileSaveNode.getNNodes() > 0) {
                                        for (int m20 = 0; m20 < fileSaveNode.getNNodes(); m20++) {
                                            Node fileSaveSupportNode = fileSaveNode.getNode(m20);
                                            if (!fileSaveSupportNode.getValue().isEmpty()) {
                                                getFileSaveMenuItems().add(fileSaveSupportNode.getValue());
                                            }
                                        }
                                    }
                                } else if (fileSaveNode.getName().equals("Default")) {
                                    setFileSaveValue(fileSaveNode.getValue());
                                } else if (fileSaveNode.getName().equals("Disable") && fileSaveNode.getNNodes() > 0) {
                                    getFileSaveMenuItemsDim().clear();
                                    for (int m21 = 0; m21 < fileSaveNode.getNNodes(); m21++) {
                                        Node fileSaveSupportNodeDim = fileSaveNode.getNode(m21);
                                        if (!fileSaveSupportNodeDim.getValue().isEmpty()) {
                                            DSCMenuItem item2 = new DSCMenuItem();
                                            item2.setName(fileSaveSupportNodeDim.getName());
                                            item2.setValue(fileSaveSupportNodeDim.getValue());
                                            getFileSaveMenuItemsDim().add(item2);
                                        }
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.RATIO_OFFSET)) {
                            if (infoNode.getNNodes() > 0) {
                                getRatioOffsetMenuItems().clear();
                                for (int l20 = 0; l20 < infoNode.getNNodes(); l20++) {
                                    Node ratioOffsetNode = infoNode.getNode(l20);
                                    if (ratioOffsetNode.getName().equals("Supports")) {
                                        if (ratioOffsetNode.getNNodes() > 0) {
                                            DSCResolution dscResolution3 = new DSCResolution();
                                            for (int m22 = 0; m22 < ratioOffsetNode.getNNodes(); m22++) {
                                                Node ratioOffsetSupportNode = ratioOffsetNode.getNode(m22);
                                                if (ratioOffsetSupportNode.getName().equals("Width")) {
                                                    dscResolution3.setWidth(Integer.parseInt(ratioOffsetSupportNode.getValue()));
                                                } else if (ratioOffsetSupportNode.getName().equals("Height")) {
                                                    dscResolution3.setHeight(Integer.parseInt(ratioOffsetSupportNode.getValue()));
                                                } else if (ratioOffsetSupportNode.getName().equals("Ratio")) {
                                                    dscResolution3.setRatio(ratioOffsetSupportNode.getValue());
                                                }
                                            }
                                            getRatioOffsetMenuItems().add(dscResolution3);
                                        }
                                    } else if (ratioOffsetNode.getName().equals("Default") && ratioOffsetNode.getNNodes() > 0) {
                                        DSCResolution dscResolution4 = new DSCResolution();
                                        for (int m23 = 0; m23 < ratioOffsetNode.getNNodes(); m23++) {
                                            Node ratioOffsetSupportNode2 = ratioOffsetNode.getNode(m23);
                                            if (ratioOffsetSupportNode2.getName().equals("Width")) {
                                                dscResolution4.setWidth(Integer.parseInt(ratioOffsetSupportNode2.getValue()));
                                            } else if (ratioOffsetSupportNode2.getName().equals("Height")) {
                                                dscResolution4.setHeight(Integer.parseInt(ratioOffsetSupportNode2.getValue()));
                                            } else if (ratioOffsetSupportNode2.getName().equals("Ratio")) {
                                                dscResolution4.setRatio(ratioOffsetSupportNode2.getValue());
                                            }
                                        }
                                        setRatioOffsetValue(dscResolution4);
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.TOUCH_AUTO_FOCUS)) {
                            for (int l21 = 0; l21 < infoNode.getNNodes(); l21++) {
                                Node touchAFNode = infoNode.getNode(l21);
                                if (touchAFNode.getName().equals("Supports")) {
                                    if (touchAFNode.getNNodes() > 0) {
                                        getTouchAFMenuItems().clear();
                                        for (int m24 = 0; m24 < touchAFNode.getNNodes(); m24++) {
                                            getTouchAFMenuItems().add(touchAFNode.getNode(m24).getValue());
                                        }
                                    }
                                } else if (touchAFNode.getName().equals("Default")) {
                                    setTouchAFValue(touchAFNode.getValue());
                                } else if (touchAFNode.getName().equals("Disable") && touchAFNode.getNNodes() > 0) {
                                    getTouchAFMenuItemsDim().clear();
                                    for (int m25 = 0; m25 < touchAFNode.getNNodes(); m25++) {
                                        DSCMenuItem item3 = new DSCMenuItem();
                                        Node touchAFSupportNode = touchAFNode.getNode(m25);
                                        item3.setName(touchAFSupportNode.getName());
                                        item3.setValue(touchAFSupportNode.getValue());
                                        getTouchAFMenuItemsDim().add(item3);
                                    }
                                }
                            }
                        } else if (infoNode.getName().equals(ActionNode.VIDEO_OUT_VALUE)) {
                            setVideoOutValue(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.FLASH_STROBE_STATUS)) {
                            setFlashStrobeStatus(infoNode.getValue());
                        } else if (infoNode.getName().equals(ActionNode.CARD_STATUS_VALUE)) {
                            setCardStatusValue(infoNode.getValue());
                        }
                    }
                } else if (ccnode.getName().equals(ActionNode.STREAMING_URLS)) {
                    getStreamUrlMenuItems().clear();
                    for (int k2 = 0; k2 < ccnode.getNNodes(); k2++) {
                        Node infoNode2 = ccnode.getNode(k2);
                        DSCMenuItem dscMenuItem2 = new DSCMenuItem();
                        dscMenuItem2.setName(infoNode2.getName());
                        dscMenuItem2.setValue(infoNode2.getValue());
                        getStreamUrlMenuItems().add(dscMenuItem2);
                    }
                    if (getCurrentStreamQuality().equals("high")) {
                        for (int k3 = 0; k3 < getStreamUrlMenuItems().size(); k3++) {
                            DSCMenuItem dscMenuItem3 = getStreamUrlMenuItems().get(k3);
                            if (dscMenuItem3.getName().equals(ActionNode.QUALITY_HIGH_URL)) {
                                setCurrentStreamUrl(dscMenuItem3.getValue());
                                break;
                            }
                        }
                    } else if (getCurrentStreamQuality().equals("low")) {
                        for (int k4 = 0; k4 < getStreamUrlMenuItems().size(); k4++) {
                            DSCMenuItem dscMenuItem4 = getStreamUrlMenuItems().get(k4);
                            if (dscMenuItem4.getName().equals(ActionNode.QUALITY_LOW_URL)) {
                                setCurrentStreamUrl(dscMenuItem4.getValue());
                                break;
                            }
                        }
                    }
                } else if (ccnode.getName().equals(ActionNode.GET_IP_RESULT)) {
                    getStreamUrlMenuItems().clear();
                    DSCMenuItem dscMenuItem5 = new DSCMenuItem();
                    dscMenuItem5.setName(ccnode.getName());
                    dscMenuItem5.setValue(ccnode.getValue());
                    getStreamUrlMenuItems().add(dscMenuItem5);
                    setCurrentStreamUrl(getStreamUrlMenuItems().get(0).getValue());
                } else if (ccnode.getName().equals(ActionNode.SHUTTER_SPEED_VALUE)) {
                    setShutterSpeedValue(ccnode.getValue());
                } else if (ccnode.getName().equals(ActionNode.APERTURE_VALUE)) {
                    setApertureValue(ccnode.getValue());
                } else if (ccnode.getName().equals(ActionNode.EV_VALUE)) {
                    setEVValue(ccnode.getValue());
                }
            }
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.util.CommandRequestListener
    public void onCommand(Object command) {
        DSCCommand action = (DSCCommand) command;
        requestAction(action.getcommandID(), action.getparam());
    }
}

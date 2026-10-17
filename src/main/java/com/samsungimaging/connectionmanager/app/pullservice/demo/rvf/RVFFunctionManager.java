package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf;

import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class RVFFunctionManager {
    private static Trace.Tag TAG = Trace.Tag.RVF;

    public static class AutoFocusFrameMetricsType {
        public static final int METRICS_1x1 = 0;
        public static final int METRICS_3x3 = 1;
        public static final int METRICS_3x5 = 2;
        public static final int METRICS_3x7 = 3;
    }

    public static class AutoFocusMode {
        public static final int MULTI = 1;
        public static final int SINGLE = 0;
    }

    public static class CameraSaveType {
        public static final int CAMERA_AND_PHONE = 0;
        public static final int CAMERA_ONLY = 1;
    }

    public static class LiveViewResolutionType {
        public static final int RESOLUTION_16x9 = 1;
        public static final int RESOLUTION_1x1 = 4;
        public static final int RESOLUTION_3x2 = 3;
        public static final int RESOLUTION_4x3 = 2;
        public static final int RESOLUTION_NONE = 0;
    }

    public static class MountType {
        public static final int DETACHABLE = 0;
        public static final int UNDETACHABLE = 1;
    }

    public static class ProtocolValueType {
        public static final int FIRST_ONLY_UPPER = 3;
        public static final int LOWER = 1;
        public static final int NUMERIC = 0;
        public static final int UPPER = 2;
    }

    public static class SaveGuideDialogType {
        public static final int CSC = 2;
        public static final int DSC = 3;
        public static final int NONE = 0;
        public static final int NOT_SUPPORTED_MOVIE_AND_RAW = 1;
    }

    private static boolean isNXAP(String ssid) {
        if (ssid != null) {
            return ssid.contains("NX300") || ssid.contains("NX30") || ssid.contains("NX3000") || ssid.contains("NX2000") || ssid.contains("NX20") || ssid.contains("NX210") || ssid.contains("NX1000") || ssid.contains("NX1100") || ssid.contains("F1") || ssid.contains("NX MINI") || ssid.contains("GN120");
        }
        return false;
    }

    private static boolean isGalaxyAP(String ssid) {
        if (ssid != null) {
            return ssid.startsWith("EK_") || ssid.startsWith("SM_") || ssid.startsWith("GC_") || ssid.startsWith("AP_SSC_GC");
        }
        return false;
    }

    private static boolean isODMAP(String ssid) {
        if (ssid != null) {
            return isGalaxyAP(ssid) || ssid.contains("WB35") || ssid.contains("WB50") || ssid.contains("WB350") || ssid.contains("WB1100") || ssid.contains("WB2200");
        }
        return false;
    }

    private static boolean isGalaxySAP(String ssid) {
        if (ssid != null) {
            return ssid.startsWith("GC_") || ssid.startsWith("AP_SSC_GC");
        }
        return false;
    }

    private static int getLensMountType(String ssid) {
        if (isNXAP(ssid)) {
            return 0;
        }
        return 1;
    }

    public static boolean isTouchableZoomButton(String ssid) {
        boolean isTouchable = false;
        if (ssid != null && !ssid.isEmpty() && !ssid.startsWith("GC_") && !ssid.startsWith("AP_SSC_GC") && !ssid.startsWith("SM_")) {
            isTouchable = true;
        }
        if (ssid != null) {
            Trace.d(TAG, "isSupportZoomButton() mPreconnectedSsid : " + ssid + " isSupport : " + isTouchable);
        }
        return isTouchable;
    }

    public static boolean isTouchableZoomSeekBar(String ssid) {
        if (ssid == null || ssid.isEmpty() || !isGalaxyAP(ssid)) {
            return false;
        }
        return true;
    }

    public static int getSaveGuideDialogType(String prefix, String version, String ssid) {
        if (isGalaxyAP(ssid)) {
            if (isNXAP(ssid)) {
                return 2;
            }
            return 3;
        }
        if (version.equals("") || Double.parseDouble(version) < 1.0d || isODMAP(ssid)) {
            return 0;
        }
        return 1;
    }

    public static boolean isSupportedDisplayZoomBar(String ssid) {
        if (getLensMountType(ssid) == 0) {
            return false;
        }
        return true;
    }

    public static boolean isUnsupportedZoomableLens(String ssid) {
        if (ssid != null) {
            return ssid.contains("NX1000") || ssid.contains("NX1100");
        }
        return false;
    }

    public static boolean isSupportedMoreButton(String ssid) {
        if (ssid != null) {
            return ssid.contains("NX2000") || ssid.contains("NX300M") || ssid.contains("NX300") || ssid.contains("NX30");
        }
        return false;
    }

    public static boolean isCancellableShotByAFFail(String prefix, String version, String ssid) {
        if (prefix.equals("CSC") && !version.equals("")) {
            return Double.parseDouble(version) >= 1.0d;
        }
        if (isNXAP(ssid)) {
            return true;
        }
        return false;
    }

    public static boolean isCancellableTimerShotByTouchLiveView(String ssid) {
        return !isGalaxyAP(ssid);
    }

    public static int getAFFrameMetricsType(String multiAFMatrixSize, String ssid) {
        if (ssid.startsWith("EK_") || ssid.startsWith("GC_") || ssid.startsWith("AP_SSC_GC") || ssid.startsWith("SM_")) {
            return 0;
        }
        if (multiAFMatrixSize.equals("7x3") || multiAFMatrixSize.equals("7X3") || ssid.contains("NX2000") || ssid.contains("NX300M") || ssid.contains("NX300") || ssid.contains("NX30")) {
            return 3;
        }
        if (ssid.contains("NX210") || ssid.contains("NX20") || ssid.contains("NX1000") || ssid.contains("NX1100")) {
            return 2;
        }
        return 1;
    }

    public static int getFlashMountType(String ssid) {
        if (isNXAP(ssid) && !isGalaxyAP(ssid)) {
            return 0;
        }
        return 1;
    }

    public static String getDefaultFlash(String ssid) {
        if (isGalaxyAP(ssid)) {
            if (isGalaxySAP(ssid)) {
                return DeviceController.FlashMode.AUTO;
            }
            return DeviceController.FlashMode.OFF;
        }
        if (ssid.contains("ST200") || ssid.contains("WB150")) {
            return DeviceController.FlashMode.AUTO;
        }
        return "off";
    }

    public static int getDefaultCameraSaveType(String ssid) {
        if (isGalaxyAP(ssid) && !isGalaxySAP(ssid)) {
            return 1;
        }
        return 0;
    }

    public static boolean isSupported5SecondsTimerShot(String ssid) {
        return isGalaxyAP(ssid);
    }

    public static boolean isSupportedDoubleTimerShot(String ssid) {
        if (ssid != null) {
            return ssid.contains("GC200");
        }
        return false;
    }

    public static boolean isBigZoomMaxValue(String ssid) {
        if (ssid != null) {
            return ssid.contains("ST200") || ssid.contains("WB150");
        }
        return false;
    }

    public static boolean isSupportedDownloadBYOriginalImageURL(String version) {
        return !version.equals("") && Double.parseDouble(version) >= 1.0d;
    }

    public static int getFlashProtocolValueType(String ssid) {
        return 1;
    }

    public static int getLiveViewResolutionType(String ssid) {
        if (isGalaxyAP(ssid) || ssid.contains("EX2F")) {
            return 1;
        }
        return 2;
    }

    public static int getAFMode(String ssid) {
        if (isGalaxyAP(ssid)) {
            return 0;
        }
        return 1;
    }

    public static boolean isSupportedZoomLongKey(String ssid, String prefix, String version) {
        if (!version.equals("")) {
            return Double.parseDouble(version) >= 1.0d;
        }
        if (!ssid.contains("NX300M") && !ssid.contains("NX300") && !ssid.contains("NX2000")) {
            return false;
        }
        return true;
    }

    public static boolean isSupportedShutterUpAct(String ssid) {
        return isGalaxyAP(ssid);
    }

    public static boolean isSupportedSmartPanel(String prefix, String version) {
        return prefix.equals("CSC") && !version.equals("") && Double.parseDouble(version) >= 1.0d;
    }
}

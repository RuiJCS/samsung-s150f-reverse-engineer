package com.samsungimaging.connectionmanager.app.pullservice;

/* JADX INFO: loaded from: classes.dex */
public class FunctionManager {

    public static class PhotoSizePresentationType {
        public static final int GALAXY = 0;
        public static final int OTHERS = 1;
    }

    private static boolean isGalaxyAP(String ssid) {
        if (ssid != null) {
            return ssid.startsWith("EK_") || ssid.startsWith("SM_") || ssid.startsWith("GC_") || ssid.startsWith("AP_SSC_GC");
        }
        return false;
    }

    public static boolean isSupportedSmartPanel(String prefix, String version) {
        return prefix.equals("CSC") && !version.equals("") && Double.parseDouble(version) >= 1.0d;
    }

    public static int getAFMode(String ssid) {
        if (isGalaxyAP(ssid)) {
            return 0;
        }
        return 1;
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

    public static boolean isSupportedDownloadBYOriginalImageURL(String version) {
        return !version.equals("") && Double.parseDouble(version) >= 1.0d;
    }

    public static int getPhotoSizePresentationType(String ssid) {
        if (!isGalaxyAP(ssid)) {
            return 1;
        }
        return 0;
    }

    public static boolean isNotSupportedRawQualityAndContinuousShotAtTheSameTime(String ssid) {
        if (ssid != null) {
            return ssid.contains("NX3000") || ssid.contains("NX3300") || ssid.contains("NX MINI");
        }
        return false;
    }
}

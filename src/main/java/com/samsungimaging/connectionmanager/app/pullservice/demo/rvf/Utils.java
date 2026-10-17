package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    public static Trace.Tag TAG = Trace.Tag.RVF;
    private static String userAgent = null;
    private static String ACCESS_METHOD_NFC = "nfc";
    private static String ACCESS_METHOD_MANUAL = "manual";
    static String[] noTelephonyDeviceList = {"GT-P7500", "SCH-I905", "SHW-M300W", "SHW-M380K", "SHW-M380S", "SHW-M380W", "P3", "SGH-T859", "SHW-M180K", "SMT-i9100", "GT-P7300", "GT-P7310", "SGH-I957", "YP-G1", "YP-G70", "YP-GB1", "YP-GB70", "YP-GS1", "GT-B7510", "GT-B7510B", "GT-B7510L", "GT-B5510", "GT-B5510B", "GT-B5510L", "GT-B5512", "SGH-T939", "GT-I5500", "GT-I5500B", "GT-I5500L", "GT-I5503", "GT-B5512B", "GT-I5500M", "GT-I5503T", "GT-I5508", "GT-I5700L", "GT-I5800L", "GT-P1010", "GT-P1013", "GT-P6210", "GT-P6211", "GT-P6810", "GT-P7300B", "GT-P7320", "GT-P7500D", "GT-P7500M", "GT-P7500R", "GT-P7501", "GT-P7503", "GT-P7511", "GT-S5360", "GT-S5360B", "GT-S5360L", "GT-S5360T", "GT-S5363", "GT-S5368", "GT-S5369", "GT-S5570B", "GT-S5570I", "GT-S5570L", "GT-S5578", "GT-S5670B", "GT-S5670L", "GT-S6102", "GT-I7500", "GT-S5670", "GT-S5570", "SPH-M900", "SPH-M580", "SC-01D", "SCH-I559", "SCH-I815", "SCH-P739", "SCH-i509", "SCH-i559", "SGH-I957D", "SGH-I957M", "SGH-T499", "SGH-T499V", "SGH-T499Y", "SGH-T869", "SHV-E140K", "SHV-E140L", "SHV-E140S", "SHW-M180W", "SHW-M305W", "SHW-M430W", "SPH-M580BST", "YP-G50", "GT-P7510"};
    static String[] existStatusBarDeviceForTablet = {"SHW-M480", "GT-N8000", "SHW-M380", "GT-P7510", "GT-P7100", "SCH-I925U"};

    public interface IFunc {
        boolean func();
    }

    public static String getDefaultStorage() {
        if (isNexusOne()) {
            String sPath = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/sdcard/Camera/Samsung Smart Camera Application/RemoteViewfinder";
            return sPath;
        }
        String sPath2 = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/DCIM/Camera/Samsung Smart Camera Application/RemoteViewfinder";
        return sPath2;
    }

    public static boolean isNexusOne() {
        return Build.MODEL.equals("Nexus One");
    }

    public static boolean isSCHi909() {
        return Build.MODEL.equals("SCH-i909");
    }

    public static String getUserAgent() {
        Trace.d(TAG, "getUserAgent - userAgent : " + userAgent);
        return userAgent;
    }

    public static String getAccessMethod() {
        String str = ACCESS_METHOD_MANUAL;
        if (CMInfo.getInstance().getIsNFCLaunch()) {
            String accessMethod = ACCESS_METHOD_NFC;
            return accessMethod;
        }
        String accessMethod2 = ACCESS_METHOD_MANUAL;
        return accessMethod2;
    }

    public static void setUseragent(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SEC_RVF_ML_");
        String addPostfix = null;
        if (context != null) {
            if (!LiveShutter.bNoTelephonyDevice) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (telephonyManager.getSimState() != 1) {
                    addPostfix = telephonyManager.getLine1Number();
                }
            }
            if (addPostfix == null || addPostfix.length() == 0) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    addPostfix = wifiInfo.getMacAddress().toLowerCase();
                    Trace.d(TAG, "addPostfix : " + addPostfix);
                }
            }
        }
        if (addPostfix == null || addPostfix.length() == 0) {
            addPostfix = "UNKNOWN";
        }
        stringBuffer.append(addPostfix);
        userAgent = stringBuffer.toString();
    }

    public static String getMacAddress(Context context) {
        String orgAddress;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null || (orgAddress = wifiInfo.getMacAddress()) == null) {
            return "";
        }
        return orgAddress;
    }

    public static String unitChange(LiveShutter obj, double nWidth, double nHeight, int type) {
        if ((nWidth == 5472.0d && nHeight == 3648.0d) || (nWidth == 3648.0d && nHeight == 5472.0d)) {
            return "20M";
        }
        if ((nWidth == 5472.0d && nHeight == 3080.0d) || (nWidth == 3080.0d && nHeight == 5472.0d)) {
            if (type == 0) {
                return "W16M";
            }
            return "16.9M";
        }
        if ((nWidth == 4608.0d && nHeight == 3456.0d) || (nWidth == 3456.0d && nHeight == 4608.0d)) {
            return "16M";
        }
        if ((nWidth == 4608.0d && nHeight == 3072.0d) || (nWidth == 3072.0d && nHeight == 4608.0d)) {
            return "P14M";
        }
        if ((nWidth == 3648.0d && nHeight == 3648.0d) || (nWidth == 3648.0d && nHeight == 3648.0d)) {
            if (type == 0) {
                return "13M";
            }
            return "13.3M";
        }
        if ((nWidth == 4608.0d && nHeight == 2592.0d) || (nWidth == 2592.0d && nHeight == 4608.0d)) {
            return "W12M";
        }
        if ((nWidth == 4320.0d && nHeight == 2432.0d) || (nWidth == 2432.0d && nHeight == 4320.0d)) {
            return "W10M";
        }
        if ((nWidth == 3648.0d && nHeight == 2736.0d) || (nWidth == 2736.0d && nHeight == 3648.0d)) {
            return "10M";
        }
        if ((nWidth == 3888.0d && nHeight == 2592.0d) || (nWidth == 2592.0d && nHeight == 3888.0d)) {
            if (type == 0) {
                return "10M";
            }
            return "10.1M";
        }
        if ((nWidth == 4000.0d && nHeight == 2248.0d) || (nWidth == 2248.0d && nHeight == 4000.0d)) {
            return "9M";
        }
        if ((nWidth == 2832.0d && nHeight == 2832.0d) || (nWidth == 2832.0d && nHeight == 2832.0d)) {
            return "8M";
        }
        if ((nWidth == 3712.0d && nHeight == 2088.0d) || (nWidth == 2088.0d && nHeight == 3712.0d)) {
            if (type == 0) {
                return "W7M";
            }
            return "7.8M";
        }
        if ((nWidth == 2640.0d && nHeight == 2640.0d) || (nWidth == 2640.0d && nHeight == 2640.0d)) {
            return "7M";
        }
        if ((nWidth == 2976.0d && nHeight == 1984.0d) || (nWidth == 1984.0d && nHeight == 2976.0d)) {
            if (type == 0) {
                return "5M";
            }
            return "5.9M";
        }
        if ((nWidth == 2592.0d && nHeight == 1944.0d) || (nWidth == 1944.0d && nHeight == 2592.0d)) {
            return "5M";
        }
        if ((nWidth == 2944.0d && nHeight == 1656.0d) || (nWidth == 1656.0d && nHeight == 2944.0d)) {
            if (type == 0) {
                return "W4M";
            }
            return "4.9M";
        }
        if ((nWidth == 2000.0d && nHeight == 2000.0d) || (nWidth == 2000.0d && nHeight == 2000.0d)) {
            return "4M";
        }
        if ((nWidth == 1984.0d && nHeight == 1488.0d) || (nWidth == 1488.0d && nHeight == 1984.0d)) {
            return "3M";
        }
        if ((nWidth == 1920.0d && nHeight == 1080.0d) || (nWidth == 1080.0d && nHeight == 1920.0d)) {
            if (type == 0) {
                return "W2M";
            }
            return "2.1M";
        }
        if ((nWidth == 1728.0d && nHeight == 1152.0d) || (nWidth == 1152.0d && nHeight == 1728.0d)) {
            return "2M";
        }
        if ((nWidth == 1024.0d && nHeight == 1024.0d) || (nWidth == 1024.0d && nHeight == 1024.0d)) {
            if (type == 0) {
                return "1M";
            }
            return "1.1M";
        }
        if ((nWidth == 1024.0d && nHeight == 768.0d) || (nWidth == 768.0d && nHeight == 1024.0d)) {
            return "1M";
        }
        return "10M";
    }

    public static int stringToRatioType(String ratio) {
        if (ratio.equals("16:9")) {
            return 1;
        }
        if (ratio.equals("4:3")) {
            return 2;
        }
        if (ratio.equals("3:2")) {
            return 3;
        }
        if (!ratio.equals("1:1")) {
            return 2;
        }
        return 4;
    }

    public static void setDisplayConnGuide(Context context, boolean showNotice) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor profileEditor = sharedPref.edit();
        profileEditor.putBoolean("connguide", showNotice);
        profileEditor.commit();
    }

    public static boolean getDisplayConnGuide(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bRet = sharedPref.getBoolean("connguide", true);
        return bRet;
    }

    public static long waitFor(long msec, IFunc p) {
        long startTimeMillis = System.currentTimeMillis();
        long estimated = 0;
        while (!p.func()) {
            try {
                Thread.sleep(10L);
            } catch (Throwable th) {
                long j = estimated * (-1);
            }
            Thread.yield();
            estimated = System.currentTimeMillis() - startTimeMillis;
            if (estimated > msec) {
                break;
            }
        }
        return estimated;
    }

    public static void noop(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
            Thread.yield();
        } catch (Throwable th) {
        }
    }

    public static boolean CheckTelephonyDevice(String model) {
        if (model == null) {
            return false;
        }
        for (String s : noTelephonyDeviceList) {
            if (model.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean CheckexistStatusBarDeviceForTablet(String model) {
        if (model == null) {
            return false;
        }
        for (String s : existStatusBarDeviceForTablet) {
            if (model.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static int MaxValue(int x, int y) {
        return x > y ? x : y;
    }

    public static int MinValue(int x, int y) {
        return x < y ? x : y;
    }

    public static boolean CheckOptimusView(int x, int y) {
        return (x == 1024 && y == 768) || (x == 768 && y == 1024);
    }

    public static boolean isNumeric(String s) {
        Pattern pattern = Pattern.compile("[+-]?\\d+");
        return pattern.matcher(s).matches();
    }

    public static boolean CheckLayoutLarge(int x, int y, int dpi) {
        Trace.d(TAG, "CheckLayoutLarge x : " + x + " y : " + y + " dpi : " + dpi);
        return (x == 600 && y == 1024) || (x == 1024 && y == 600 && dpi == 160);
    }
}

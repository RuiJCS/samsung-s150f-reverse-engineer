package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    private static String ACCESS_METHOD_MANUAL;
    private static String ACCESS_METHOD_NFC;
    private static ArrayList<String> dscPrefixList;
    static String[] noTelephonyDeviceList;
    private static String userAgent;

    public interface IFunc {
        boolean func();
    }

    static {
        dscPrefixList = null;
        dscPrefixList = new ArrayList<>();
        dscPrefixList.add("WB150");
        dscPrefixList.add("DV300");
        dscPrefixList.add("DV500");
        dscPrefixList.add("WB300");
        dscPrefixList.add("ST200");
        dscPrefixList.add("WB850");
        dscPrefixList.add("NX20");
        dscPrefixList.add("NX210");
        dscPrefixList.add("NX1000");
        dscPrefixList.add("EX2");
        dscPrefixList.add("MV900");
        dscPrefixList.add("QF30");
        userAgent = null;
        ACCESS_METHOD_NFC = "nfc";
        ACCESS_METHOD_MANUAL = "manual";
        noTelephonyDeviceList = new String[]{"GT-P7500", "SCH-I905", "SHW-M300W", "SHW-M380K", "SHW-M380S", "SHW-M380W", "P3", "SGH-T859", "SHW-M180K", "SMT-i9100", "GT-P7300", "GT-P7310", "SGH-I957", "YP-G1", "YP-G70", "YP-GB1", "YP-GB70", "YP-GS1", "GT-B7510", "GT-B7510B", "GT-B7510L", "GT-B5510", "GT-B5510B", "GT-B5510L", "GT-B5512", "SGH-T939", "GT-I5500", "GT-I5500B", "GT-I5500L", "GT-I5503", "GT-B5512B", "GT-I5500M", "GT-I5503T", "GT-I5508", "GT-I5700L", "GT-I5800L", "GT-P1010", "GT-P1013", "GT-P6210", "GT-P6211", "GT-P6810", "GT-P7300B", "GT-P7320", "GT-P7500D", "GT-P7500M", "GT-P7500R", "GT-P7501", "GT-P7503", "GT-P7511", "GT-S5360", "GT-S5360B", "GT-S5360L", "GT-S5360T", "GT-S5363", "GT-S5368", "GT-S5369", "GT-S5570B", "GT-S5570I", "GT-S5570L", "GT-S5578", "GT-S5670B", "GT-S5670L", "GT-S6102", "GT-I7500", "GT-S5670", "GT-S5570", "SPH-M900", "SPH-M580", "SC-01D", "SCH-I559", "SCH-I815", "SCH-P739", "SCH-i509", "SCH-i559", "SGH-I957D", "SGH-I957M", "SGH-T499", "SGH-T499V", "SGH-T499Y", "SGH-T869", "SHV-E140K", "SHV-E140L", "SHV-E140S", "SHW-M180W", "SHW-M305W", "SHW-M430W", "SPH-M580BST", "YP-G50", "GT-P7510"};
    }

    public static boolean supportDSCPrefix(String ssid) {
        if (ssid == null) {
            return false;
        }
        for (String s : dscPrefixList) {
            if (ssid.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static String getDefaultStorage() {
        String sPath = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/DCIM/Camera/Samsung Smart Camera Application/MobileLink";
        return sPath;
    }

    public static String getThumbStorage() {
        String sPath = String.valueOf(getDefaultStorage()) + "/LazyList";
        return sPath;
    }

    public static boolean isTopActivity(Context context, String value) {
        if (context == null || value == null) {
            return true;
        }
        ActivityManager activityMan = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> info = activityMan.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runtaskinfo = null;
        for (ActivityManager.RunningTaskInfo runtaskinfo2 : info) {
            runtaskinfo = runtaskinfo2;
        }
        if (runtaskinfo.topActivity.getClassName().equals(value)) {
            return true;
        }
        return false;
    }

    public static boolean isMemoryFull() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            if (availableSize >= Const.Config.CRITICAL_SD_MEMORY) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMountedExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (!state.equals("removed") && !state.equals("shared") && !state.equals("bad_removal") && !state.equals("unmounted") && !state.equals("checking") && !state.equals("unmountable")) {
            return true;
        }
        return false;
    }

    public static String getExtention(String urlPath) {
        if (urlPath == null || urlPath.lastIndexOf(".") == -1) {
            return "";
        }
        String sRet = urlPath.substring(urlPath.lastIndexOf(".") + 1, urlPath.length());
        return sRet;
    }

    public static String renameFile(String dir, String filename) {
        File file = new File(dir, filename);
        while (file.exists()) {
            filename = rename(filename);
            file = new File(dir, filename);
        }
        return filename;
    }

    private static String rename(String filename) {
        Pattern p = Pattern.compile("(\\-)(\\d*)+[.]");
        Matcher m = p.matcher(filename);
        if (m.find()) {
            String str = m.group();
            int count = Integer.parseInt(str.replace("-", "").replace(".", ""));
            String rename = filename.replaceAll("(\\-)(\\d*)+[.]", "-" + (count + 1) + ".");
            return rename;
        }
        String rename2 = String.valueOf(filename.substring(0, filename.lastIndexOf("."))) + "-0" + filename.substring(filename.lastIndexOf("."), filename.length());
        return rename2;
    }

    public static boolean isTab101() {
        return Build.MODEL.contains("SHW-M380");
    }

    public static boolean isICS() {
        return Build.MODEL.contains("Galaxy Nexus");
    }

    public static void setDisplayNotice(Context context, boolean showNotice) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor profileEditor = sharedPref.edit();
        profileEditor.putBoolean("notice", showNotice);
        profileEditor.commit();
    }

    public static boolean getDisplayNotice(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bRet = sharedPref.getBoolean("notice", true);
        return bRet;
    }

    public static void setDisplayDetailGuide(Context context, boolean showNotice) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor profileEditor = sharedPref.edit();
        profileEditor.putBoolean("guide", showNotice);
        profileEditor.commit();
    }

    public static boolean getDisplayDetailGuide(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bRet = sharedPref.getBoolean("guide", true);
        return bRet;
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

    public static String getUserAgent() {
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
            if (!FileSharing.bNoTelephonyDevice) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (telephonyManager.getSimState() != 1) {
                    addPostfix = telephonyManager.getLine1Number();
                }
            }
            if (addPostfix == null || addPostfix.length() == 0) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                addPostfix = wifiManager.getConnectionInfo().getMacAddress().toLowerCase();
            }
        }
        if (addPostfix == null || addPostfix.length() == 0) {
            addPostfix = "UNKNOWN";
        }
        stringBuffer.append(addPostfix);
        userAgent = stringBuffer.toString();
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "";
        }
        String orgAddress = wifiInfo.getMacAddress();
        if (orgAddress != null) {
            return orgAddress;
        }
        return "UNKNOWN";
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

    public static Map<String, PackageInfo> getPackageMap(Context ctx) {
        Map<String, PackageInfo> packages = Collections.synchronizedSortedMap(new TreeMap());
        PackageManager pm = ctx.getPackageManager();
        for (ApplicationInfo ai : pm.getInstalledApplications(128)) {
            try {
                packages.put(ai.packageName, pm.getPackageInfo(ai.packageName, 0));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return packages;
    }

    public static PackageInfo getPackageSmartWiFiCM(Context ctx) {
        for (PackageInfo pi : getPackageMap(ctx).values()) {
            if (pi.packageName.startsWith("com.skt.network.wificm.")) {
                return pi;
            }
        }
        return null;
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

    private static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (!isExternalMemoryAvailable()) {
            return -1L;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static String formatSize(long size) {
        double doubleSize = (size / 1024.0d) / 1024.0d;
        return String.valueOf(String.format("%.2f", Double.valueOf(doubleSize))) + "MB";
    }
}

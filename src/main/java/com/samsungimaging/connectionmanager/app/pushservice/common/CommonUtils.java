package com.samsungimaging.connectionmanager.app.pushservice.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cybergarage.soap.SOAP;
import org.cybergarage.upnp.Device;

/* JADX INFO: loaded from: classes.dex */
public class CommonUtils {
    static final int ERROR = -1;
    private static final int thumbHeight = 80;
    private static final int thumbWidth = 112;
    private static final int zoomHeight = 240;
    private static final int zoomWidth = 336;
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    static String[] configString = new String[128];
    private static boolean isXlargeTablet = false;
    private static String userAgent = null;

    public interface IFunc {
        boolean func();
    }

    public static String getDefaultStorage() {
        String path = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/DCIM/Camera/Samsung Smart Camera Application/";
        switch (CMService.ACTIVE_SERVICE) {
            case 1:
                return String.valueOf(path) + "MobileLink/";
            case 2:
                return String.valueOf(path) + "RemoteViewfinder/";
            case 3:
                return String.valueOf(path) + "AutoShare/";
            case 4:
            case 5:
                return String.valueOf(path) + "MobileLink/";
            default:
                return path;
        }
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

    public static void setUseragent(Context context) {
        String orgAddress;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Const.Config.USER_AGENT_PREFIX);
        String addPostfix = null;
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager.getSimState() != 1) {
                addPostfix = telephonyManager.getLine1Number();
            }
            if (addPostfix == null || addPostfix.length() == 0) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null && (orgAddress = wifiInfo.getMacAddress()) != null) {
                    addPostfix = orgAddress.replace(SOAP.DELIM, "");
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

    public static String ByteToHex(byte[] buf) {
        String res = "";
        for (byte b : buf) {
            String token = Integer.toHexString(b);
            if (token.length() >= 2) {
                token = token.substring(token.length() - 2);
            } else {
                for (int jx = 0; jx < 2 - token.length(); jx++) {
                    token = "0" + token;
                }
            }
            res = String.valueOf(res) + " " + token;
        }
        return res.toUpperCase();
    }

    public static String intToIp(int i) {
        return String.valueOf(i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        if (height <= reqHeight && width <= reqWidth) {
            return 1;
        }
        if (width > height) {
            int inSampleSize = Math.round(height / reqHeight);
            return inSampleSize;
        }
        int inSampleSize2 = Math.round(width / reqWidth);
        return inSampleSize2;
    }

    public static int getExifOrientation(String path) {
        int orientation;
        ExifInterface exif = null;
        try {
            ExifInterface exif2 = new ExifInterface(path);
            exif = exif2;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif == null || (orientation = exif.getAttributeInt("Orientation", 1)) == 1) {
            return 0;
        }
        switch (orientation) {
            case 2:
                return 280;
            case 3:
                return 180;
            case 4:
                return 290;
            case 5:
                return Device.DEFAULT_DISCOVERY_WAIT_TIME;
            case 6:
                return 90;
            case 7:
                return 310;
            case 8:
                return 270;
            default:
                return 0;
        }
    }

    public static Bitmap getRotateBitmap(Bitmap bitmap, int degree) {
        if (degree < 280 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            try {
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != rotateBitmap) {
                    bitmap.recycle();
                    return rotateBitmap;
                }
                return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        }
        if (degree >= 280 && bitmap != null) {
            Matrix matrix2 = new Matrix();
            if (degree == 280) {
                matrix2.setScale(-1.0f, 1.0f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            } else if (degree == 290) {
                matrix2.setScale(1.0f, -1.0f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            } else if (degree == 300) {
                matrix2.setRotate(270.0f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
                matrix2.postTranslate((-(bitmap.getWidth() - bitmap.getHeight())) / 2.0f, (-(bitmap.getHeight() - bitmap.getWidth())) / 2.0f);
                matrix2.preScale(1.0f, -1.0f);
            } else if (degree == 310) {
                matrix2.setRotate(90.0f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
                matrix2.postTranslate((-(bitmap.getWidth() - bitmap.getHeight())) / 2.0f, (-(bitmap.getHeight() - bitmap.getWidth())) / 2.0f);
                matrix2.preScale(1.0f, -1.0f);
            }
            try {
                Bitmap rotateBitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix2, false);
                if (bitmap != rotateBitmap2) {
                    bitmap.recycle();
                    return rotateBitmap2;
                }
                return bitmap;
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                return bitmap;
            }
        }
        return bitmap;
    }

    public static Bitmap decodeThumb(String path) {
        if (path.toLowerCase().endsWith(".jpg")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, 112, 80);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(path, options);
            Trace.d(TAG, "FREE MEMORY : " + (Runtime.getRuntime().freeMemory() / 1048576) + "MB");
            int degree = getExifOrientation(path);
            return getRotateBitmap(src, degree);
        }
        Bitmap src2 = ThumbnailUtils.createVideoThumbnail(path, 3);
        return src2;
    }

    public static Bitmap decodeForZoom(String path) {
        if (path.toLowerCase().endsWith(".jpg")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, zoomWidth, zoomHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(path, options);
            int degree = getExifOrientation(path);
            return getRotateBitmap(src, degree);
        }
        Bitmap src2 = ThumbnailUtils.createVideoThumbnail(path, 1);
        return src2;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1L;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1L;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static void setXlargeTablet() {
        isXlargeTablet = true;
    }

    public static boolean isXlargeTablet() {
        return isXlargeTablet;
    }

    public static String getLineNumber(Context context) {
        String lineNumber = null;
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager.getSimState() != 1) {
                lineNumber = telephonyManager.getLine1Number();
            }
        }
        if (lineNumber == null || lineNumber.length() == 0) {
            return "none";
        }
        return lineNumber;
    }

    public static String getUseragent(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        String addPostfix = null;
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager.getSimState() != 1) {
                addPostfix = telephonyManager.getLine1Number();
            }
            if (addPostfix == null || addPostfix.length() == 0) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    addPostfix = wifiInfo.getMacAddress().toLowerCase();
                }
            }
        }
        if (addPostfix == null || addPostfix.length() == 0) {
            addPostfix = "UNKNOWN";
        }
        stringBuffer.append(addPostfix);
        return stringBuffer.toString();
    }

    public static void setSystemConfigurationChanged(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (config.orientation == 1) {
            configString[0] = config.toString();
            Trace.d(TAG, "setSystemConfigurationChanged configString[0] : " + configString[0]);
        } else if (config.orientation == 2) {
            configString[1] = config.toString();
            Trace.d(TAG, "setSystemConfigurationChanged configString[1] : " + configString[1]);
        }
    }

    public static boolean getSystemConfigurationChanged(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Trace.d(TAG, "getSystemConfigurationChanged configString[0] : " + configString[0]);
        Trace.d(TAG, "getSystemConfigurationChanged configString[1] : " + configString[1]);
        Trace.d(TAG, "getSystemConfigurationChanged config : " + config.toString());
        if (configString[0] != null && config.toString().equals(configString[0])) {
            Trace.d(TAG, "getSystemConfigurationChanged   config value is Not changed");
            return false;
        }
        if (configString[1] == null || !config.toString().equals(configString[1])) {
            return true;
        }
        Trace.d(TAG, "getSystemConfigurationChanged   config value is Not changed");
        return false;
    }

    public static boolean getActivityRunning(Context context, String name) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(100);
        return taskList.get(0).baseActivity.getPackageName().contains(name);
    }
}

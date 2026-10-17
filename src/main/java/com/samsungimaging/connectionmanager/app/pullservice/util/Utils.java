package com.samsungimaging.connectionmanager.app.pullservice.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.IOException;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    private static Trace.Tag TAG = Trace.Tag.RVF;

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[+-]?\\d+");
        return pattern.matcher(str).matches();
    }

    public static String getAgent(Context ctx, String prefix) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(prefix);
        String addPostfix = null;
        if (ctx != null) {
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService("phone");
            if (telephonyManager.getSimState() != 1) {
                addPostfix = telephonyManager.getLine1Number();
            }
            if (addPostfix == null || addPostfix.length() == 0) {
                WifiManager wifiManager = (WifiManager) ctx.getSystemService("wifi");
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

    public static int toRatioType(String str) {
        if (str.equals("16:9")) {
            return 1;
        }
        if (str.equals("4:3")) {
            return 2;
        }
        if (str.equals("3:2")) {
            return 3;
        }
        if (!str.equals("1:1")) {
            return 2;
        }
        return 4;
    }

    public static String unitChange(double width, double height) {
        if ((width == 5472.0d && height == 3648.0d) || (width == 3648.0d && height == 5472.0d)) {
            return "20M";
        }
        if ((width == 5472.0d && height == 3080.0d) || (width == 3080.0d && height == 5472.0d)) {
            return "W16M";
        }
        if ((width == 4608.0d && height == 3456.0d) || (width == 3456.0d && height == 4608.0d)) {
            return "16M";
        }
        if ((width == 4608.0d && height == 3072.0d) || (width == 3072.0d && height == 4608.0d)) {
            return "P14M";
        }
        if ((width == 3648.0d && height == 3648.0d) || (width == 3648.0d && height == 3648.0d)) {
            return "13M";
        }
        if ((width == 4608.0d && height == 2592.0d) || (width == 2592.0d && height == 4608.0d)) {
            return "W12M";
        }
        if ((width == 4320.0d && height == 2432.0d) || (width == 2432.0d && height == 4320.0d)) {
            return "W10M";
        }
        if ((width == 3648.0d && height == 2736.0d) || (width == 2736.0d && height == 3648.0d)) {
            return "10M";
        }
        if ((width == 3888.0d && height == 2592.0d) || (width == 2592.0d && height == 3888.0d)) {
            return "10M";
        }
        if ((width == 4000.0d && height == 2248.0d) || (width == 2248.0d && height == 4000.0d)) {
            return "W9M";
        }
        if ((width == 2832.0d && height == 2832.0d) || (width == 2832.0d && height == 2832.0d)) {
            return "8M";
        }
        if ((width == 3712.0d && height == 2088.0d) || (width == 2088.0d && height == 3712.0d)) {
            return "W7M";
        }
        if ((width == 2640.0d && height == 2640.0d) || (width == 2640.0d && height == 2640.0d)) {
            return "7M";
        }
        if ((width == 2976.0d && height == 1984.0d) || (width == 1984.0d && height == 2976.0d)) {
            return "5M";
        }
        if ((width == 2592.0d && height == 1944.0d) || (width == 1944.0d && height == 2592.0d)) {
            return "5M";
        }
        if ((width == 2944.0d && height == 1656.0d) || (width == 1656.0d && height == 2944.0d)) {
            return "W4M";
        }
        if ((width == 2000.0d && height == 2000.0d) || (width == 2000.0d && height == 2000.0d)) {
            return "4M";
        }
        if ((width == 1984.0d && height == 1488.0d) || (width == 1488.0d && height == 1984.0d)) {
            return "3M";
        }
        if ((width == 1920.0d && height == 1080.0d) || (width == 1080.0d && height == 1920.0d)) {
            return "W2M";
        }
        if ((width == 1728.0d && height == 1152.0d) || (width == 1152.0d && height == 1728.0d)) {
            return "2M";
        }
        if ((width == 1024.0d && height == 1024.0d) || (width == 1024.0d && height == 1024.0d)) {
            return "1M";
        }
        if ((width == 1024.0d && height == 768.0d) || (width == 768.0d && height == 1024.0d)) {
            return "1M";
        }
        return "10M";
    }

    public static String getDefaultStorage() {
        String sPath = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/DCIM/Camera/Samsung Smart Camera Application/RemoteViewfinder";
        return sPath;
    }

    public static boolean isInstalledPackage(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, 128);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getLocation(LocationManager locationManager) {
        Location gpsLocation = locationManager.getLastKnownLocation("gps");
        Location networkLocation = locationManager.getLastKnownLocation("network");
        if (gpsLocation != null) {
            String locationValue = getGPSValue(gpsLocation);
            return locationValue;
        }
        if (networkLocation != null) {
            String locationValue2 = getGPSValue(networkLocation);
            return locationValue2;
        }
        return "UNKNOWN";
    }

    private static String getGPSValue(Location location) {
        String latitude;
        String longitude;
        int latitudeValue = (int) (location.getLatitude() * 3600.0d);
        int longitudeValue = (int) (location.getLongitude() * 3600.0d);
        if (latitudeValue >= 0) {
            latitude = "N";
        } else {
            latitude = "S";
            latitudeValue *= -1;
        }
        if (longitudeValue >= 0) {
            longitude = "E";
        } else {
            longitude = "W";
            longitudeValue *= -1;
        }
        String gpsValue = String.valueOf(latitude) + latitudeValue + "X" + longitude + longitudeValue;
        return gpsValue;
    }

    public static void setGPSInfoOfExif(Location location, String filename) {
        String latitude;
        String longitude;
        Trace.d(TAG, "start setGPSInfoOfExif()");
        int latitudeValue = (int) (location.getLatitude() * 3600.0d);
        int longitudeValue = (int) (location.getLongitude() * 3600.0d);
        if (latitudeValue >= 0) {
            latitude = "N";
        } else {
            latitude = "S";
            int i = latitudeValue * (-1);
        }
        if (longitudeValue >= 0) {
            longitude = "E";
        } else {
            longitude = "W";
            int i2 = longitudeValue * (-1);
        }
        try {
            ExifInterface exif = new ExifInterface(filename);
            try {
                String gpsInfo = exif.getAttribute("GPSLatitude");
                if (gpsInfo == null) {
                    exif.setAttribute("GPSLatitudeRef", latitude);
                    exif.setAttribute("GPSLongitudeRef", longitude);
                    exif.setAttribute("GPSLatitude", deg_to_dms(location.getLatitude()));
                    exif.setAttribute("GPSLongitude", deg_to_dms(location.getLongitude()));
                    Trace.d(TAG, "exif.saveAttributes()");
                    exif.saveAttributes();
                }
            } catch (IOException e) {
                e = e;
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e = e2;
        }
    }

    private static double roundDouble(double d, int places) {
        return Math.round(Math.pow(10.0d, places) * d) / Math.pow(10.0d, places);
    }

    private static String deg_to_dms(double degfloat) {
        if (degfloat < 0.0d) {
            return "";
        }
        int deg = (int) degfloat;
        double minfloat = (degfloat - ((double) deg)) * 60.0d;
        int min = (int) minfloat;
        double secfloat = roundDouble((minfloat - ((double) min)) * 60.0d, 2);
        if (secfloat == 60.0d) {
            min++;
        }
        if (min == 60) {
            deg++;
            min = 0;
        }
        return String.format("%d/1,%d/1,%.02f/1", Integer.valueOf(deg), Integer.valueOf(min), Double.valueOf(secfloat));
    }
}

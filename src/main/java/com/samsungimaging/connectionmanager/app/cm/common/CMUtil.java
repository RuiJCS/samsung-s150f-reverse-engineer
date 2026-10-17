package com.samsungimaging.connectionmanager.app.cm.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.ActivityChooserView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.app.cm.connector.ConnectionHandler;
import com.samsungimaging.connectionmanager.app.cm.connector.securities.ConfigurationSecurity;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;
import com.samsungimaging.connectionmanager.util.Trace;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class CMUtil {
    private static HashMap<String, Integer> CONNECT_TRYING_COUNT = new HashMap<>();
    private static ArrayList<String> NOTSupported_dscPrefixList;
    private static ArrayList<String> dscPrefixList;
    private static ArrayList<String> dscPrefixListForRenewal;
    private static ArrayList<String> galaxySeriesPrefixList;
    private static ArrayList<String> lateDHCPRefreshList;
    private static ArrayList<String> olddscPrefixList;
    private static ArrayList<String> useAllChooseDialogList;

    static {
        dscPrefixList = null;
        NOTSupported_dscPrefixList = null;
        useAllChooseDialogList = null;
        lateDHCPRefreshList = null;
        olddscPrefixList = null;
        galaxySeriesPrefixList = null;
        dscPrefixListForRenewal = null;
        dscPrefixListForRenewal = new ArrayList<>();
        dscPrefixListForRenewal.add("NX MINI");
        dscPrefixListForRenewal.add("NX3000");
        dscPrefixListForRenewal.add("NX3300");
        dscPrefixList = new ArrayList<>();
        dscPrefixList.add("AP_SSC_");
        dscPrefixList.add("GC_");
        dscPrefixList.add("EK_");
        dscPrefixList.add("SM_");
        NOTSupported_dscPrefixList = new ArrayList<>();
        NOTSupported_dscPrefixList.add("WB150");
        NOTSupported_dscPrefixList.add("DV300");
        NOTSupported_dscPrefixList.add("DV500");
        NOTSupported_dscPrefixList.add("WB300");
        NOTSupported_dscPrefixList.add("ST200");
        NOTSupported_dscPrefixList.add("WB850");
        NOTSupported_dscPrefixList.add("NX20");
        NOTSupported_dscPrefixList.add("NX210");
        NOTSupported_dscPrefixList.add("NX1000");
        NOTSupported_dscPrefixList.add("EX2");
        NOTSupported_dscPrefixList.add("MV900");
        NOTSupported_dscPrefixList.add("QF30");
        useAllChooseDialogList = new ArrayList<>();
        useAllChooseDialogList.add("LG-F180");
        lateDHCPRefreshList = new ArrayList<>();
        lateDHCPRefreshList.add("LG-F100");
        lateDHCPRefreshList.add("LG-F200");
        lateDHCPRefreshList.add("LG-F320");
        lateDHCPRefreshList.add("LG-F240");
        olddscPrefixList = new ArrayList<>();
        olddscPrefixList.add("WB150");
        olddscPrefixList.add("DV300");
        olddscPrefixList.add("DV500");
        olddscPrefixList.add("WB300");
        olddscPrefixList.add("ST200");
        olddscPrefixList.add("WB850");
        olddscPrefixList.add("NX20");
        olddscPrefixList.add("NX210");
        olddscPrefixList.add("NX1000");
        olddscPrefixList.add("EX2");
        olddscPrefixList.add("MV900");
        olddscPrefixList.add("QF30");
        olddscPrefixList.add("GC_");
        galaxySeriesPrefixList = new ArrayList<>();
        galaxySeriesPrefixList.add("EK_");
        galaxySeriesPrefixList.add("SM_");
        galaxySeriesPrefixList.add("GC_");
        galaxySeriesPrefixList.add("AP_SSC_GC");
    }

    public static boolean checkOldVersionSmartCameraApp(String connectedssid) {
        boolean bRet = true;
        String ssid_temp = convertToNOTQuotedString(connectedssid);
        if (ssid_temp != null && ssid_temp.contains("AP_SSC_")) {
            String[] ssid_array = ssid_temp.split("_");
            String model = ssid_array[2];
            for (String s : dscPrefixListForRenewal) {
                if (model.contains(s)) {
                    bRet = false;
                    break;
                }
            }
            if (!CMInfo.getInstance().isOldCamera()) {
                bRet = false;
            }
        }
        Trace.d(CMConstants.TAG_NAME, "checkOldVersionSmartCameraApp, old camera is " + bRet);
        return bRet;
    }

    public static void setdscPrefix_fortest(Context context, String ssid) {
        Trace.d(CMConstants.TAG_NAME, "CMUtil.setdscPrefix, ssid = " + ssid);
        if ("true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.TESTMODE, "false"))) {
            dscPrefixList.clear();
            if (ssid.equals("")) {
                dscPrefixList.add("AP_SSC_");
                dscPrefixList.add("EK_");
                dscPrefixList.add("SM_");
                dscPrefixList.add("GC_");
                return;
            }
            dscPrefixList.add(ssid);
        }
    }

    public static void setdscPrefix_fortest(Context context, List<String> ssidlist) {
        if ("true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.TESTMODE, "false"))) {
            dscPrefixList.clear();
            for (int i = 0; i < ssidlist.size(); i++) {
                String ssid_temp = ssidlist.get(i);
                if (!ssid_temp.equals("")) {
                    dscPrefixList.add(ssid_temp);
                }
            }
            int size = dscPrefixList.size();
            if (size == 0) {
                dscPrefixList.add("AP_SSC_");
                dscPrefixList.add("EK_");
                dscPrefixList.add("SM_");
                dscPrefixList.add("GC_");
            }
        }
    }

    public static boolean supportDSCPrefix(String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        if (ssid_temp == null) {
            return false;
        }
        for (String s : dscPrefixList) {
            if (ssid_temp.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean NOTsupportDSCPrefix(String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        if (ssid_temp == null) {
            return false;
        }
        for (String s : NOTSupported_dscPrefixList) {
            if (ssid_temp.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportOldDSCPrefix(String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        if (ssid_temp == null) {
            return false;
        }
        for (String s : olddscPrefixList) {
            if (ssid_temp.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGalaxySeriesPrefix(String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        if (ssid_temp == null) {
            return false;
        }
        for (String s : galaxySeriesPrefixList) {
            if (ssid_temp.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lateDHCPRefresh(String model) {
        if (model == null) {
            return false;
        }
        for (String s : lateDHCPRefreshList) {
            if (model.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportAllChooseDialogModel(String model) {
        if (model == null) {
            return false;
        }
        for (String s : useAllChooseDialogList) {
            if (model.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCMTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        ComponentName top = list.get(0).topActivity;
        if (!CMConstants.PACKAGE_FULL_NAME.equals(top.getPackageName())) {
            return false;
        }
        return true;
    }

    public static String whatIsTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        ComponentName top = list.get(0).topActivity;
        String topClassName = top.getClassName();
        Trace.d(CMConstants.TAG_NAME, "whatIsTopActivity, topClassName : " + topClassName);
        return topClassName;
    }

    public static List<ScanResult> selectCameraSoftAP(Context context, List<ScanResult> srs) {
        List<ScanResult> filteredList = new ArrayList<>();
        if ("true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.TESTMODE, "false"))) {
            String ssid_temp = CMSharedPreferenceUtil.getString(context, CMConstants.TESTMODE_SSID, "");
            Trace.d(CMConstants.TAG_NAME, "selectCameraSoftAP, ssid_temp = " + ssid_temp);
            if (ssid_temp.equals("")) {
                if (srs != null && srs.size() > 0) {
                    for (ScanResult sr : srs) {
                        if (supportDSCPrefix(sr.SSID) || NOTsupportDSCPrefix(sr.SSID)) {
                            filteredList.add(sr);
                        }
                    }
                }
            } else if (srs != null && srs.size() > 0) {
                for (ScanResult sr2 : srs) {
                    if (supportDSCPrefix(sr2.SSID)) {
                        filteredList.add(sr2);
                    }
                }
            }
        } else if (srs != null && srs.size() > 0) {
            for (ScanResult sr3 : srs) {
                if (supportDSCPrefix(sr3.SSID) || NOTsupportDSCPrefix(sr3.SSID)) {
                    filteredList.add(sr3);
                }
            }
        }
        return filteredList;
    }

    public static List<ScanResult> sort(List<ScanResult> srs) {
        Collections.sort(srs, new Comparator<ScanResult>() { // from class: com.samsungimaging.connectionmanager.app.cm.common.CMUtil.1
            @Override // java.util.Comparator
            public int compare(ScanResult sr1, ScanResult sr2) {
                return Integer.signum(sr2.level - sr1.level);
            }
        });
        return srs;
    }

    private static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int lastPos = string.length() - 1;
        return (lastPos > 0 && string.charAt(0) == '\"' && string.charAt(lastPos) == '\"') ? string : "\"" + string + "\"";
    }

    public static String convertToNOTQuotedString(String ssid) {
        if (ssid != null && !ssid.equals("")) {
            Pattern compiled_pattern = Pattern.compile("\".*\"");
            Matcher matcher = compiled_pattern.matcher(ssid);
            if (matcher.find()) {
                String patterned_ssid = matcher.group();
                return patterned_ssid.replace("\"", "");
            }
            return ssid;
        }
        return ssid;
    }

    public static WifiConfiguration getWifiConfigurationWithScanResult(WifiManager wifiManager, ScanResult sr) {
        String ssid = convertToQuotedString(sr.SSID);
        String bssid = sr.BSSID;
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (ssid == null || bssid == null || configurations == null) {
            return null;
        }
        for (WifiConfiguration config : configurations) {
            if (config.SSID != null && ssid.equals(config.SSID) && config.SSID != null && ssid.equals(config.SSID)) {
                if (config.BSSID == null) {
                    Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithScanResult, config.BSSID is null");
                    return config;
                }
                if (bssid.equals(config.BSSID)) {
                    Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithScanResult, config.BSSID = " + config.BSSID);
                    return config;
                }
            }
        }
        return null;
    }

    private static WifiConfiguration getWifiConfigurationWithWifiConfiguration(WifiManager wifiManager, WifiConfiguration wifiConfig) {
        String ssid = wifiConfig.SSID;
        String bssid = wifiConfig.BSSID;
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (ssid == null || configurations == null) {
            Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithWifiConfiguration, return null01");
            return null;
        }
        for (WifiConfiguration wifiConfiguration : configurations) {
            if (wifiConfiguration.SSID != null && ssid.equals(wifiConfiguration.SSID)) {
                if (wifiConfiguration.BSSID == null) {
                    Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithWifiConfiguration, wifiConfiguration.BSSID is null");
                    return wifiConfiguration;
                }
                if (bssid != null && bssid.equals(wifiConfiguration.BSSID)) {
                    Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithWifiConfiguration, wifiConfiguration.BSSID = " + wifiConfiguration.BSSID);
                    return wifiConfiguration;
                }
            }
        }
        Trace.d(CMConstants.TAG_NAME, "getWifiConfigurationWithWifiConfiguration, return null02");
        return null;
    }

    public static WifiConfiguration getWifiConfigurationWithScanResult(WifiManager wifiManager, String ssid_temp, String bssid_temp) {
        String ssid = convertToQuotedString(ssid_temp);
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configurations) {
            if (config.SSID != null && ssid.equals(config.SSID)) {
                return config;
            }
        }
        return null;
    }

    private static int changePriorityAndSave(WifiManager wifiManager) {
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        sortByPriority(configurations);
        for (int i = 0; i < configurations.size(); i++) {
            WifiConfiguration config = configurations.get(i);
            config.priority = i;
            wifiManager.updateNetwork(config);
        }
        wifiManager.saveConfiguration();
        return configurations.size();
    }

    private static void sortByPriority(List<WifiConfiguration> configurations) {
        Collections.sort(configurations, new Comparator<WifiConfiguration>() { // from class: com.samsungimaging.connectionmanager.app.cm.common.CMUtil.2
            @Override // java.util.Comparator
            public int compare(WifiConfiguration lhs, WifiConfiguration rhs) {
                return lhs.priority - rhs.priority;
            }
        });
    }

    private static int getMaxPriority(WifiManager wifiManager) {
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        int pri = 0;
        for (WifiConfiguration config : configurations) {
            if (config.priority > pri) {
                pri = config.priority;
            }
        }
        return pri;
    }

    public static boolean isAvailableScan(Context context, WifiManager wifiManager) {
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid_wifiinfo = info.getSSID();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
        Trace.d(CMConstants.TAG_NAME, "isAvailableScan, ssid_wifiinfo = " + ssid_wifiinfo);
        Trace.d(CMConstants.TAG_NAME, "isAvailableScan, state = " + state);
        return ((NetworkInfo.DetailedState.CONNECTED == state || NetworkInfo.DetailedState.CONNECTING == state || NetworkInfo.DetailedState.IDLE == state || NetworkInfo.DetailedState.OBTAINING_IPADDR == state || NetworkInfo.DetailedState.AUTHENTICATING == state || NetworkInfo.DetailedState.SCANNING == state) && supportDSCPrefix(ssid_wifiinfo) && isManagedSSID(context, ssid_wifiinfo)) ? false : true;
    }

    public static boolean isAvailableConnect(Context context, WifiManager wifiManager) {
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid_wifiinfo = info.getSSID();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
        Trace.d(CMConstants.TAG_NAME, "isAvailableConnect01, ssid_wifiinfo = " + ssid_wifiinfo);
        Trace.d(CMConstants.TAG_NAME, "isAvailableConnect01, state = " + state);
        return ((NetworkInfo.DetailedState.CONNECTED == state || NetworkInfo.DetailedState.CONNECTING == state || NetworkInfo.DetailedState.IDLE == state || NetworkInfo.DetailedState.OBTAINING_IPADDR == state || NetworkInfo.DetailedState.AUTHENTICATING == state) && supportDSCPrefix(ssid_wifiinfo) && isManagedSSID(context, ssid_wifiinfo)) ? false : true;
    }

    public static boolean isAvailableConnect(WifiManager wifiManager, String ssid) {
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid_wifiinfo = info.getSSID();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
        Trace.d(CMConstants.TAG_NAME, "isAvailableConnect02, ssid_wifiinfo = " + ssid_wifiinfo);
        Trace.d(CMConstants.TAG_NAME, "isAvailableConnect02, state = " + state);
        Trace.d(CMConstants.TAG_NAME, "isAvailableConnect02, ssid = " + ssid);
        if (ssid.contains("NX1000") && ssid_wifiinfo != null && convertToNOTQuotedString(ssid_wifiinfo).equals("0x")) {
            Trace.d(CMConstants.TAG_NAME, "because of, SSID is " + ssid_wifiinfo + ", then return false.");
            return false;
        }
        if (NetworkInfo.DetailedState.CONNECTED != state && NetworkInfo.DetailedState.CONNECTING != state && NetworkInfo.DetailedState.IDLE != state && NetworkInfo.DetailedState.OBTAINING_IPADDR != state && NetworkInfo.DetailedState.AUTHENTICATING != state) {
            return true;
        }
        if (ssid_wifiinfo == null || !convertToNOTQuotedString(ssid_wifiinfo).equals(convertToNOTQuotedString(ssid))) {
            return true;
        }
        Trace.d(CMConstants.TAG_NAME, "because of, already connecting, then return false.");
        return false;
    }

    public static synchronized boolean connectToNewCameraSoftAP(Context context, WifiManager wifiManager, ScanResult sr, ConnectionHandler handler) {
        boolean request;
        if (!isManagedSSID(context, sr.SSID)) {
            Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, return false01!!");
            request = false;
        } else {
            Trace.d(CMConstants.TAG_NAME, "Performance Check Point : Connecting to Camera Soft AP");
            if (!isAvailableConnect(wifiManager, sr.SSID)) {
                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, return false02!!, aleady connecting state.");
                request = false;
            } else {
                request = false;
                WifiConfiguration config = getWifiConfigurationWithScanResult(wifiManager, sr);
                boolean open_network = ConfigurationSecurity.isOpenNetwork(sr);
                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, open_network = " + open_network);
                if (!open_network) {
                    if (config != null && (config.preSharedKey == null || (config.preSharedKey != null && config.preSharedKey.isEmpty()))) {
                        Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSof4tAP, scan result is security, BUT config is open!");
                        removeNetworkConfig(context, wifiManager, sr.SSID, sr.BSSID);
                        config = null;
                    }
                } else if (config != null && config.preSharedKey != null && !config.preSharedKey.isEmpty()) {
                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, scan result is open, BUT config is security!");
                    removeNetworkConfig(context, wifiManager, sr.SSID, sr.BSSID);
                    config = null;
                }
                if (config == null) {
                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config not exist, open_network = " + open_network);
                    if (open_network) {
                        request = createConfigAndRequestConnection(context, wifiManager, sr, true, null, null);
                    } else {
                        sendMessageForPassword(sr, handler);
                    }
                } else {
                    String brand = Build.BRAND;
                    String manufacturer = Build.MANUFACTURER;
                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, brand = " + brand + ", manufacturer = " + manufacturer);
                    if (brand.toLowerCase().contains("samsung") || manufacturer.toLowerCase().contains("samsung")) {
                        boolean aleadySetSamsungSpecificFlag = true;
                        try {
                            try {
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, check SpecificFlags check!!!");
                                Field invokeField = WifiConfiguration.class.getField("samsungSpecificFlags");
                                BitSet samsungSpecificFlag = (BitSet) invokeField.get(config);
                                String samsungSpecificFlagToString = samsungSpecificFlag.toString();
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, samsungSpecificFlag check!!! = " + samsungSpecificFlagToString);
                                if (samsungSpecificFlagToString.contains("0")) {
                                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, samsungSpecificFlag check!!!, aleady set!!!");
                                    aleadySetSamsungSpecificFlag = true;
                                } else {
                                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, samsungSpecificFlag check!!!, not set!!!");
                                    aleadySetSamsungSpecificFlag = false;
                                }
                            } catch (IllegalAccessException e) {
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, check SpecificFlags check!!!, IllegalAccessException");
                                e.printStackTrace();
                            } catch (IllegalArgumentException e2) {
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, check SpecificFlags check!!!, IllegalArgumentException");
                                e2.printStackTrace();
                            }
                        } catch (NoSuchFieldException e3) {
                            Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, check SpecificFlags check!!!, NoSuchFieldException");
                            e3.printStackTrace();
                        }
                        if (aleadySetSamsungSpecificFlag) {
                            request = createConfigAndRequestConnection(context, wifiManager, sr, false, config, null);
                        } else {
                            Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config aleady exist, try remove config");
                            if (wifiManager.removeNetwork(config.networkId)) {
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config aleady exist, try remove config, success.");
                                if (!wifiManager.saveConfiguration()) {
                                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config aleady exist, try remove config, saveConfig failed.");
                                    request = false;
                                } else {
                                    Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config aleady exist, try remove config, saveConfig success, open_network = " + open_network);
                                    if (open_network) {
                                        request = createConfigAndRequestConnection(context, wifiManager, sr, true, null, null);
                                    } else {
                                        sendMessageForPassword(sr, handler);
                                    }
                                }
                            } else {
                                Trace.d(CMConstants.TAG_NAME, "connectToNewCameraSoftAP, config aleady exist, try remove config, failed.");
                            }
                        }
                    } else {
                        request = createConfigAndRequestConnection(context, wifiManager, sr, false, config, null);
                    }
                }
            }
        }
        return request;
    }

    /* JADX WARN: Code duplicated, block: B:101:0x04e8 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:103:0x04ee A[Catch: all -> 0x024c, TRY_LEAVE, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:105:0x04fd  */
    /* JADX WARN: Code duplicated, block: B:106:0x0501  */
    /* JADX WARN: Code duplicated, block: B:87:0x0453 A[Catch: all -> 0x024c, PHI: r15 r28
      0x0453: PHI (r15v1 'newPri' int) = (r15v0 'newPri' int), (r15v2 'newPri' int) binds: [B:83:0x0434, B:85:0x0442] A[DONT_GENERATE, DONT_INLINE]
      0x0453: PHI (r28v1 'requestconfig' android.net.wifi.WifiConfiguration) = (r28v0 'requestconfig' android.net.wifi.WifiConfiguration), (r28v3 'requestconfig' android.net.wifi.WifiConfiguration) binds: [B:83:0x0434, B:85:0x0442] A[DONT_GENERATE, DONT_INLINE], TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:89:0x0465 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:90:0x0474 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:92:0x0480 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:93:0x0495 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:95:0x049b A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:96:0x04b0 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:98:0x04ba A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    /* JADX WARN: Code duplicated, block: B:99:0x04c9 A[Catch: all -> 0x024c, TryCatch #3 {all -> 0x024c, blocks: (B:6:0x0009, B:8:0x0055, B:9:0x0077, B:47:0x0228, B:11:0x0099, B:13:0x00c0, B:14:0x00c4, B:16:0x00cc, B:17:0x00fc, B:21:0x0127, B:23:0x0131, B:54:0x029d, B:31:0x0172, B:33:0x017c, B:34:0x0184, B:36:0x01be, B:40:0x020d, B:43:0x0219, B:69:0x03b6, B:71:0x03bc, B:72:0x03cb, B:74:0x03d3, B:75:0x03e2, B:77:0x03ee, B:79:0x03f4, B:38:0x01ce, B:64:0x0384, B:66:0x0395, B:68:0x03a6, B:55:0x02c2, B:57:0x02cc, B:58:0x033f, B:60:0x0349, B:61:0x0365, B:62:0x0374, B:53:0x0297, B:52:0x024f, B:82:0x0409, B:84:0x0436, B:86:0x0444, B:87:0x0453, B:89:0x0465, B:90:0x0474, B:92:0x0480, B:93:0x0495, B:95:0x049b, B:96:0x04b0, B:98:0x04ba, B:99:0x04c9, B:101:0x04e8, B:103:0x04ee), top: B:111:0x0009, inners: #4, #5 }] */
    public static synchronized boolean createConfigAndRequestConnection(Context context, WifiManager wifiManager, ScanResult sr, boolean makeconfig, WifiConfiguration requestconfig, CharSequence[] securityconnectioninfo) {
        int networkId;
        WifiConfiguration requestconfig2;
        boolean connect;
        boolean z;
        String cs_ssid = null;
        String cs_bssid = null;
        String cs_security = null;
        String cs_password = null;
        if (securityconnectioninfo != null) {
            try {
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, makeconfig = " + makeconfig + ", SSID = " + ((Object) securityconnectioninfo[0]) + ", BSSID = " + ((Object) securityconnectioninfo[1]) + ", security = " + ((Object) securityconnectioninfo[2]));
                if (securityconnectioninfo[3] != null) {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, makeconfig = " + makeconfig + ", password = *");
                } else {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, makeconfig = " + makeconfig + ", password = null");
                }
                cs_ssid = securityconnectioninfo[0].toString();
                cs_bssid = securityconnectioninfo[1].toString();
                cs_security = securityconnectioninfo[2].toString();
                cs_password = securityconnectioninfo[3].toString();
            } catch (Throwable th) {
                throw th;
            }
        }
        if (makeconfig) {
            int newPri = getMaxPriority(wifiManager) + 1;
            Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, newPri01 = " + newPri);
            if (newPri > 99999) {
                newPri = changePriorityAndSave(wifiManager);
            }
            WifiConfiguration config = new WifiConfiguration();
            if (securityconnectioninfo != null) {
                config.SSID = convertToQuotedString(cs_ssid);
                config.BSSID = cs_bssid;
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, config.SSID = " + cs_ssid + ", config.BSSID = " + cs_bssid);
            } else {
                config.SSID = convertToQuotedString(sr.SSID);
                config.BSSID = sr.BSSID;
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, config.SSID = " + sr.SSID + ", config.BSSID = " + sr.BSSID);
            }
            config.priority = newPri;
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            int passwordLen = cs_password == null ? 0 : cs_password.length();
            if (cs_security != null) {
                if (cs_security.contains(ConfigurationSecurity.WEP)) {
                    config.allowedKeyManagement.set(0);
                    config.allowedAuthAlgorithms.set(0);
                    config.allowedAuthAlgorithms.set(1);
                    if (passwordLen != 0) {
                        if ((passwordLen == 10 || passwordLen == 26 || passwordLen == 58) && cs_password.matches("[0-9A-Fa-f]*")) {
                            config.wepKeys[0] = cs_password;
                        } else {
                            config.wepKeys[0] = String.valueOf('\"') + cs_password + '\"';
                        }
                    }
                } else if (cs_security.contains(ConfigurationSecurity.PSK)) {
                    config.allowedAuthAlgorithms.set(0);
                    config.allowedProtocols.set(1);
                    config.allowedKeyManagement.set(1);
                    config.allowedPairwiseCiphers.set(2);
                    config.allowedPairwiseCiphers.set(1);
                    config.allowedGroupCiphers.set(3);
                    config.allowedGroupCiphers.set(2);
                    config.preSharedKey = "\"".concat(cs_password).concat("\"");
                } else if (cs_security.contains(ConfigurationSecurity.EAP)) {
                    config.allowedKeyManagement.set(2);
                    config.allowedKeyManagement.set(3);
                } else {
                    config.allowedKeyManagement.set(0);
                }
            } else {
                config.allowedKeyManagement.set(0);
            }
            String brand = Build.BRAND;
            String manufacturer = Build.MANUFACTURER;
            Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, brand = " + brand + ", manufacturer = " + manufacturer);
            if (brand.toLowerCase().contains("samsung") || manufacturer.toLowerCase().contains("samsung")) {
                try {
                    try {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, check SpecificFlags");
                        Field invokeField = WifiConfiguration.class.getField("samsungSpecificFlags");
                        BitSet samsungSpecificFlag = (BitSet) invokeField.get(config);
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, samsungSpecificFlag = " + samsungSpecificFlag);
                        samsungSpecificFlag.set(0);
                    } catch (IllegalAccessException e) {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, check SpecificFlags IllegalAccessException");
                        e.printStackTrace();
                    } catch (IllegalArgumentException e2) {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, check SpecificFlags IllegalArgumentException");
                        e2.printStackTrace();
                    }
                } catch (NoSuchFieldException e3) {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, check SpecificFlags NoSuchFieldException");
                    e3.printStackTrace();
                }
            }
            int id = -1;
            try {
                id = wifiManager.addNetwork(config);
            } catch (NullPointerException e4) {
            }
            if (id == -1) {
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false01");
                z = false;
            } else if (!wifiManager.saveConfiguration()) {
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false02");
                z = false;
            } else if (getWifiConfigurationWithWifiConfiguration(wifiManager, config) == null) {
                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false03");
                z = false;
            } else if (wifiManager.enableNetwork(id, true)) {
                boolean connect2 = wifiManager.reconnect();
                if (!connect2) {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false04");
                    z = false;
                } else {
                    z = true;
                }
            } else {
                z = false;
            }
        } else if (requestconfig != null) {
            int oldPri = requestconfig.priority;
            int newPri2 = getMaxPriority(wifiManager) + 1;
            Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, newPri02 = " + newPri2);
            if (newPri2 > 99999) {
                newPri2 = changePriorityAndSave(wifiManager);
                requestconfig = getWifiConfigurationWithWifiConfiguration(wifiManager, requestconfig);
                if (requestconfig == null) {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail00");
                    z = false;
                } else {
                    requestconfig.priority = newPri2;
                    networkId = wifiManager.updateNetwork(requestconfig);
                    if (networkId == -1) {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail01");
                        z = false;
                    } else if (!wifiManager.enableNetwork(networkId, false)) {
                        requestconfig.priority = oldPri;
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail02");
                        z = false;
                    } else if (!wifiManager.saveConfiguration()) {
                        requestconfig.priority = oldPri;
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail03");
                        z = false;
                    } else {
                        requestconfig2 = getWifiConfigurationWithWifiConfiguration(wifiManager, requestconfig);
                        if (requestconfig2 == null) {
                            Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail04");
                            z = false;
                        } else {
                            Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, not SupplicantState.COMPLETED!!!");
                            if (wifiManager.enableNetwork(requestconfig2.networkId, true)) {
                                connect = wifiManager.reconnect();
                                if (!connect) {
                                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false02");
                                    z = false;
                                } else {
                                    z = true;
                                }
                            } else {
                                z = false;
                            }
                        }
                    }
                }
            } else {
                requestconfig.priority = newPri2;
                networkId = wifiManager.updateNetwork(requestconfig);
                if (networkId == -1) {
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail01");
                    z = false;
                } else if (!wifiManager.enableNetwork(networkId, false)) {
                    requestconfig.priority = oldPri;
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail02");
                    z = false;
                } else if (!wifiManager.saveConfiguration()) {
                    requestconfig.priority = oldPri;
                    Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail03");
                    z = false;
                } else {
                    requestconfig2 = getWifiConfigurationWithWifiConfiguration(wifiManager, requestconfig);
                    if (requestconfig2 == null) {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, priority setting, fail04");
                        z = false;
                    } else {
                        Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, not SupplicantState.COMPLETED!!!");
                        if (wifiManager.enableNetwork(requestconfig2.networkId, true)) {
                            connect = wifiManager.reconnect();
                            if (!connect) {
                                Trace.d(CMConstants.TAG_NAME, "createConfigAndRequestConnection, return false02");
                                z = false;
                            } else {
                                z = true;
                            }
                        } else {
                            z = false;
                        }
                    }
                }
            }
        } else {
            z = false;
        }
        return z;
    }

    private static void sendMessageForPassword(ScanResult sr, ConnectionHandler handler) {
        if (handler != null) {
            String security_type = ConfigurationSecurity.getSecurity2(sr);
            String[] security_info = {sr.SSID, sr.BSSID, security_type};
            Trace.d(CMConstants.TAG_NAME, "sendMessageForPassword, scanResult.SSID = " + sr.SSID);
            Trace.d(CMConstants.TAG_NAME, "sendMessageForPassword, scanResult.BSSID = " + sr.BSSID);
            Trace.d(CMConstants.TAG_NAME, "sendMessageForPassword, scanResult.capabilities = " + sr.capabilities);
            Trace.d(CMConstants.TAG_NAME, "sendMessageForPassword, security_type = " + security_type);
            Bundle bundle = new Bundle();
            bundle.putCharSequenceArray(BaseGalleryActivity.BUNDLE_KEY_DATA, security_info);
            Message ms = new Message();
            ms.what = 100;
            ms.obj = bundle;
            handler.sendMessage(ms);
        }
    }

    public static void removeNetworkConfig(Context context, WifiManager wifiManager, String ssid, String bssid) {
        Trace.d(CMConstants.TAG_NAME, "removeNetworkConfig, ssid = " + ssid);
        Trace.d(CMConstants.TAG_NAME, "removeNetworkConfig, bssid = " + bssid);
        WifiConfiguration config = getWifiConfigurationWithScanResult(wifiManager, ssid, bssid);
        if (config != null) {
            if (wifiManager.removeNetwork(config.networkId)) {
                if (!wifiManager.saveConfiguration()) {
                    Trace.d(CMConstants.TAG_NAME, "removeNetworkConfig, remove config failed01.");
                    return;
                }
                return;
            }
            Trace.d(CMConstants.TAG_NAME, "removeNetworkConfig, remove config failed02.");
            return;
        }
        Trace.d(CMConstants.TAG_NAME, "removeNetworkConfig, not exist config.");
    }

    public static void disalbleCameraAps(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        String ssid_temp = convertToNOTQuotedString(ssid);
        Trace.d(CMConstants.TAG_NAME, "disalbleCameraAps, ssid_temp = " + ssid_temp);
        if (ssid_temp == null) {
            if (configurations != null) {
                for (WifiConfiguration wifiConfiguration : configurations) {
                    if (supportDSCPrefix(wifiConfiguration.SSID)) {
                        wifiManager.disableNetwork(wifiConfiguration.networkId);
                    } else {
                        wifiManager.enableNetwork(wifiConfiguration.networkId, false);
                    }
                }
                return;
            }
            return;
        }
        if (configurations != null) {
            for (WifiConfiguration wifiConfiguration2 : configurations) {
                if (ssid_temp.equals(convertToNOTQuotedString(wifiConfiguration2.SSID))) {
                    Trace.d(CMConstants.TAG_NAME, "disalbleCameraAps, wifiConfiguration.SSID = " + wifiConfiguration2.SSID + ", wifiConfiguration.status = " + wifiConfiguration2.status);
                    wifiManager.disableNetwork(wifiConfiguration2.networkId);
                }
            }
        }
    }

    private static boolean isOpenSecurity(WifiConfiguration config) {
        return config.allowedKeyManagement.get(0);
    }

    public static void downPriority(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        Trace.d(CMConstants.TAG_NAME, "downPriority");
        if (configurations != null && wifiInfo != null) {
            Trace.d(CMConstants.TAG_NAME, "downPriority,  configurations.size() = " + configurations.size());
            for (int i = 0; i < configurations.size(); i++) {
                if (configurations.get(i).SSID.equals(wifiInfo.getSSID()) || configurations.get(i).SSID.replace("\"", "").equals(wifiInfo.getSSID())) {
                    Trace.d(CMConstants.TAG_NAME, "downPriority,  configurations.get(i).SSID = " + configurations.get(i).SSID + ", wifiInfo.getSSID() = " + wifiInfo.getSSID());
                    configurations.get(i).priority = 0;
                    wifiManager.updateNetwork(configurations.get(i));
                    wifiManager.saveConfiguration();
                    return;
                }
            }
        }
    }

    public static String intToIp(int i) {
        return String.valueOf(i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static synchronized int getAppTypeFromCamera(WifiManager wifiManager) {
        int appType;
        Trace.d(CMConstants.TAG_NAME, "getAppTypeFromCamera()");
        appType = -1;
        try {
            String c_class = checkCClass(wifiManager);
            if ("101".equals(c_class)) {
                appType = 1;
            } else if ("102".equals(c_class)) {
                appType = 2;
            } else if ("103".equals(c_class)) {
                appType = 3;
            } else if ("104".equals(c_class)) {
                appType = 4;
            } else if ("107".equals(c_class)) {
                appType = 8;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appType;
    }

    public static String checkCClass(WifiManager wifiManager) {
        DhcpInfo di = wifiManager.getDhcpInfo();
        String cameraIp = Formatter.formatIpAddress(di.serverAddress);
        String connected_ssid = wifiManager.getConnectionInfo().getSSID();
        Trace.d(CMConstants.TAG_NAME, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! connected_ssid : " + connected_ssid);
        Trace.d(CMConstants.TAG_NAME, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! camera_ip : " + cameraIp);
        String c_class = cameraIp.substring(0, cameraIp.lastIndexOf("."));
        return c_class.substring(c_class.lastIndexOf(".") + 1);
    }

    public static void goToHomeScreen(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        context.startActivity(intent);
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

    public static void sendBroadCastToMain(Context context, String extraInfo) {
        Trace.d(CMConstants.TAG_NAME, "sendBroadCastFromCM~~~ extraInfo = " + extraInfo);
        Intent i = new Intent();
        if (extraInfo != null) {
            i.putExtra(CMConstants.EXTRA_KEY_FROM_CM, extraInfo);
        }
        i.setAction(CMConstants.INTENT_FROM_CM);
        context.sendBroadcast(i);
    }

    public static void sendBroadCastToMain(Context context, String extraInfo, int extraInfo2) {
        Trace.d(CMConstants.TAG_NAME, "sendBroadCastFromCM~~~");
        Intent i = new Intent();
        if (extraInfo != null) {
            i.putExtra(CMConstants.EXTRA_KEY_FROM_CM, extraInfo);
        }
        i.putExtra(CMConstants.EXTRA_KEY2_FROM_CM, extraInfo2);
        i.setAction(CMConstants.INTENT_FROM_CM);
        context.sendBroadcast(i);
    }

    public static void sendBroadCastToMain(Context context, String extraInfo, Bundle bundle) {
        Trace.d(CMConstants.TAG_NAME, "sendBroadCastFromCM~~~");
        Intent i = new Intent();
        if (extraInfo != null) {
            i.putExtra(CMConstants.EXTRA_KEY_FROM_CM, extraInfo);
            i.putExtra(CMConstants.EXTRA_KEY3_FROM_CM, bundle);
        }
        i.setAction(CMConstants.INTENT_FROM_CM);
        context.sendBroadcast(i);
    }

    public static boolean isEqualSSID(String ssid1, String ssid2) {
        String ssid1_temp = convertToNOTQuotedString(ssid1);
        String ssid2_temp = convertToNOTQuotedString(ssid2);
        return ssid1_temp.equalsIgnoreCase(ssid2_temp);
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

    public static void disableConnectedCamera(WifiManager wifimanager) {
        WifiInfo connectedInfo;
        String connectedSSID;
        if (wifimanager.isWifiEnabled() && wifimanager.getWifiState() != 0 && (connectedSSID = (connectedInfo = wifimanager.getConnectionInfo()).getSSID()) != null && !connectedSSID.equals("") && supportDSCPrefix(connectedSSID)) {
            downPriority(wifimanager);
            wifimanager.disableNetwork(connectedInfo.getNetworkId());
        }
    }

    public static void finishSubServicew(Context context) {
        String top_activity = whatIsTopActivity(context);
        if (!top_activity.equals(CMConstants.CLASS_NAME_ML_ACTIVITY) && !top_activity.equals(CMConstants.CLASS_NAME_RVF_ACTIVITY) && !top_activity.equals(CMConstants.CLASS_NAME_S2L_ACTIVITY)) {
            top_activity.equals(CMConstants.CLASS_NAME_SP_ACTIVITY);
        }
    }

    public static String parseDescriptionURLForRVFML(String input) {
        String[] input_array = input.split("\n");
        for (int i = 0; i < input_array.length; i++) {
            Trace.d(CMConstants.TAG_NAME, "ModeClient, checkResponse, parseDescriptionURLForRVFML, input_array[i] = " + input_array[i]);
            if (input_array[i].contains("Default-URL:")) {
                String[] default_url_node = input_array[i].split("Default-URL:");
                for (int j = 0; j < default_url_node.length; j++) {
                    Trace.d(CMConstants.TAG_NAME, "ModeClient, checkResponse, parseDescriptionURLForRVFML, default_url_node[i] = " + default_url_node[j]);
                    if (default_url_node[j].contains("http")) {
                        String url = default_url_node[j];
                        return url;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static boolean isForceClose(ActivityManager am) {
        List<ActivityManager.RecentTaskInfo> taskInfo;
        if (am == null || (taskInfo = am.getRecentTasks(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 1)) == null) {
            return false;
        }
        boolean recent_info = false;
        for (int i = 0; i < taskInfo.size(); i++) {
            ActivityManager.RecentTaskInfo recent = taskInfo.get(i);
            Intent intent2 = recent.baseIntent;
            ComponentName cn = intent2.getComponent();
            if (cn.getPackageName().equals(CMConstants.PACKAGE_FULL_NAME)) {
                recent_info = true;
                break;
            }
        }
        if (!recent_info) {
            Trace.d(CMConstants.TAG_NAME, "isForceClose, disappeared in recent task info...finish app.");
            CMService.getInstance().forceClosed();
            return true;
        }
        Trace.d(CMConstants.TAG_NAME, "isForceClose, alive in recent task info.");
        return false;
    }

    public static boolean isManagedSSID(Context context, String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        DatabaseAP ap = DatabaseManager.fetchForAP(context, ssid_temp);
        return ap != null;
    }

    public static void addToManagedAPList(Context context, String ssid) {
        String ssid_temp = convertToNOTQuotedString(ssid);
        DatabaseManager.putForAP(context, ssid_temp);
    }

    public static boolean isTestMode(Context context) {
        if (!"true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.TESTMODE, "false"))) {
            return false;
        }
        return true;
    }
}

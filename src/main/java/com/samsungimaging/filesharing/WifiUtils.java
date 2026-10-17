package com.samsungimaging.filesharing;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.samsungimaging.filesharing.util.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.cybergarage.upnp.ssdp.SSDP;

/* JADX INFO: loaded from: classes.dex */
public class WifiUtils {
    private static final String TAG = "INRY";
    private static SavedWifiInfo savedWifiInfo = null;

    public static class SavedWifiInfo {
        boolean enabledHotspot;
        boolean enabledWifi;
    }

    public static void removeCameraConfigration(Context context, boolean forceall) {
        Logger.d("INRY", " removeCameraConfigration " + context);
        if (context != null) {
            Logger.d("INRY", " dddddddd ");
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
            WifiInfo curWifiInfo = wifiManager.getConnectionInfo();
            Logger.d("INRY", " wifiConfigs : " + wifiConfigs);
            if (wifiConfigs != null) {
                for (WifiConfiguration wc : wifiConfigs) {
                    Logger.d("INRY", " wc SSID=" + wc.SSID + ", wc.status=" + wc.status + ", wc.networkId=" + wc.networkId);
                    if (Utils.supportDSCPrefix(wc.SSID) && (curWifiInfo.getNetworkId() != wc.networkId || forceall)) {
                        boolean b = wifiManager.removeNetwork(wc.networkId);
                        Logger.d("INRY", "remove config>> wc SSID=" + wc.SSID + ", wc.status=" + wc.status + ", wc.networkId=" + wc.networkId + "result=" + b);
                    }
                }
            }
        }
    }

    public static boolean backupWifiConnection(Context context) {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            WifiApManager wifiApManager = new WifiApManager(context);
            savedWifiInfo = new SavedWifiInfo();
            savedWifiInfo.enabledHotspot = wifiApManager.isWifiApEnabled();
            savedWifiInfo.enabledWifi = wifiManager.isWifiEnabled();
            return true;
        }
        savedWifiInfo = null;
        return false;
    }

    public static boolean restoreWifiConection(Context context) {
        boolean bRet = false;
        if (context != null && savedWifiInfo != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            WifiApManager wifiApManager = new WifiApManager(context);
            if (savedWifiInfo.enabledHotspot) {
                wifiApManager.setWifiApEnabled(true);
            } else if (savedWifiInfo.enabledWifi) {
                wifiManager.setWifiEnabled(true);
            } else {
                wifiApManager.setWifiApEnabled(false);
            }
            bRet = true;
        }
        savedWifiInfo = null;
        return bRet;
    }

    public static String intToIp(int i) {
        return String.valueOf(i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static boolean doDhcpCommandLine() {
        byte[] bb = new byte[SSDP.RECV_MESSAGE_BUFSIZE];
        try {
            Process pp = Runtime.getRuntime().exec("/system/bin/dhcpcd -d -t 5 eth0");
            String stdOut = "";
            String stdErr = "";
            InputStream isOut = pp.getInputStream();
            InputStream isErr = pp.getErrorStream();
            while (true) {
                int red = isOut.read(bb);
                if (red == -1) {
                    break;
                }
                stdOut = String.valueOf(stdOut) + new String(bb, 0, red);
            }
            while (true) {
                int red2 = isErr.read(bb);
                if (red2 == -1) {
                    break;
                }
                stdErr = String.valueOf(stdErr) + new String(bb, 0, red2);
            }
            int exitCode = pp.waitFor();
            while (true) {
                int red3 = isOut.read(bb);
                if (red3 == -1) {
                    break;
                }
                stdOut = String.valueOf(stdOut) + new String(bb, 0, red3);
            }
            while (true) {
                int red4 = isErr.read(bb);
                if (red4 == -1) {
                    break;
                }
                stdErr = String.valueOf(stdErr) + new String(bb, 0, red4);
            }
            StringBuilder sbAppend = new StringBuilder("DHCP command exit code : (").append(exitCode).append(") (");
            int exitCode2 = pp.exitValue();
            Logger.GG(sbAppend.append(exitCode2).append(")").toString());
            Logger.GG("/stdout :");
            Logger.GG(stdOut);
            Logger.GG("/stderr :");
            Logger.GG(stdErr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        return false;
    }
}

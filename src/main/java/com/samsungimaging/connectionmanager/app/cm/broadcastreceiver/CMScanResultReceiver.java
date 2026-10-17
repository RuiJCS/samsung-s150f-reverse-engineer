package com.samsungimaging.connectionmanager.app.cm.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.connector.CMCameraAPConnector;
import com.samsungimaging.connectionmanager.app.cm.connector.CMEnableAllApsAfterConnecting;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CMScanResultReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
            Trace.d(CMConstants.TAG_NAME, "WifiManager.SCAN_RESULTS_AVAILABLE_ACTION!!!");
            if (CMService.getInstance() != null && CMService.getInstance().isWifiScanStop()) {
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, return01");
                return;
            }
            if (CMService.getInstance() == null) {
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, return02");
                return;
            }
            if (CMService.getInstance().isSubAppAlive()) {
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, return03");
                return;
            }
            boolean isReceiverRunnig = "true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(context, CMConstants.SP_KEY_IS_RECEIVER_RUNNING, "true"));
            if (!isReceiverRunnig) {
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, return04");
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService("connectivity");
            List<ScanResult> srs = wifiManager.getScanResults();
            List<ScanResult> cameraSrs = new ArrayList<>();
            if (srs != null && srs.size() > 0) {
                cameraSrs = CMUtil.selectCameraSoftAP(context, srs);
            }
            Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ scan count : " + cameraSrs.size());
            CMService.getInstance().setScannedList(cameraSrs);
            List<ScanResult> cameraSrs2 = CMService.getInstance().getScannedList();
            if (cameraSrs2 != null && cameraSrs2.size() > 0) {
                ArrayList<DatabaseAP> managedAPs = DatabaseManager.fetchAllForAP(context);
                List<ScanResult> cameraSrs3 = CMUtil.sort(cameraSrs2);
                WifiInfo info = wifiManager.getConnectionInfo();
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ state : " + state + ", ssid : " + info.getSSID());
                NetworkInfo networkInfo = connectivitymanager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    NetworkInfo.DetailedState state_connectivitymanager = networkInfo.getDetailedState();
                    Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ state_connectivitymanager : " + state_connectivitymanager);
                    Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ state_wifimanager : " + state);
                    if (state_connectivitymanager == NetworkInfo.DetailedState.CONNECTED && state == NetworkInfo.DetailedState.CONNECTED) {
                        CMService.IS_WIFI_CONNECTED = CMUtil.supportDSCPrefix(info.getSSID()) && CMUtil.isManagedSSID(context, info.getSSID());
                        if (CMService.IS_WIFI_CONNECTED) {
                            Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : CMMainActivity.IS_CONNECTED!!!");
                            if (CMService.getInstance() != null) {
                                CMService.getInstance().checkSubApplicationType();
                                return;
                            }
                            return;
                        }
                    }
                }
                if (CMUtil.isAvailableConnect(context, wifiManager)) {
                    List<ScanResult> tempList = new ArrayList<>();
                    if (managedAPs != null && managedAPs.size() > 0) {
                        for (int i = 0; i < managedAPs.size(); i++) {
                            for (int j = 0; j < cameraSrs3.size(); j++) {
                                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, managedAPs.get(" + i + ").getCameraSSID() = " + managedAPs.get(i).getSSID() + ", cameraSrs.get(" + j + ").SSID = " + cameraSrs3.get(j).SSID);
                                if (managedAPs.get(i).getSSID().equals(cameraSrs3.get(j).SSID)) {
                                    tempList.add(cameraSrs3.get(j));
                                }
                            }
                        }
                    }
                    Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver, ConnectionTryCount = " + CMService.getInstance().mConnectionTryCount);
                    if (CMInfo.getInstance().getIsNFCLaunchFlag()) {
                        CMService.getInstance().mConnectionTryCount = 0;
                    }
                    if (CMService.getInstance().mConnectionTryCount == 3) {
                        Toast.makeText(context, context.getString(R.string.not_connect_automatically_manual_connection), 0).show();
                    }
                    if (tempList.size() > 0) {
                        Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ call connector ");
                        CMCameraAPConnector.getInstance().setSr(tempList, context, wifiManager);
                        CMService.getInstance().mConnectionTryCount++;
                        return;
                    }
                    return;
                }
                return;
            }
            if (cameraSrs2 != null && cameraSrs2.size() == 0) {
                Trace.d(CMConstants.TAG_NAME, "CMScanResultReceiver : ~~~~~~~~~~~~~~~~~~~~~~~~~ cameraSrs.size() is 0,  enalbleAllAps.");
                CMEnableAllApsAfterConnecting.enalbleNOTCameraAps(context);
            }
        }
    }
}

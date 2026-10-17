package com.samsungimaging.connectionmanager.app.cm.connector;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CMEnableAllApsAfterConnecting {
    public static void start(Context context) {
        context.startService(new Intent(context, (Class<?>) BackgroundService.class));
    }

    public static void enalbleNOTCameraAps(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configurations != null) {
            for (WifiConfiguration wifiConfiguration : configurations) {
                if (!CMUtil.supportDSCPrefix(wifiConfiguration.SSID)) {
                    wifiManager.enableNetwork(wifiConfiguration.networkId, false);
                }
            }
        }
    }

    public static void enalbleAllAps(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configurations != null) {
            for (WifiConfiguration wifiConfiguration : configurations) {
                wifiManager.enableNetwork(wifiConfiguration.networkId, false);
            }
        }
    }

    public static class BackgroundService extends Service {
        private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.samsungimaging.connectionmanager.app.cm.connector.CMEnableAllApsAfterConnecting.BackgroundService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    NetworkInfo.DetailedState detailState = networkInfo.getDetailedState();
                    if (detailState == NetworkInfo.DetailedState.CONNECTED && CMService.IS_WIFI_CONNECTED && !BackgroundService.this.mRedo) {
                        BackgroundService.this.mRedo = true;
                        CMEnableAllApsAfterConnecting.enalbleAllAps(context);
                        BackgroundService.this.stopSelf();
                    }
                }
            }
        };
        private boolean mRedo;

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            this.mRedo = false;
            IntentFilter filter = new IntentFilter("android.net.wifi.STATE_CHANGE");
            registerReceiver(this.mReceiver, filter);
        }

        @Override // android.app.Service
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override // android.app.Service
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(this.mReceiver);
        }
    }
}

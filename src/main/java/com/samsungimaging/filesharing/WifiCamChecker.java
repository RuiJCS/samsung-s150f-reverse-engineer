package com.samsungimaging.filesharing;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import com.samsungimaging.filesharing.util.Logger;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WifiCamChecker {
    public static final String TAG = "INRY";
    private final Context mContext;
    private final Handler mHandler;
    private APChecker mAPCheckerThread = null;
    private boolean mIsStop = false;

    public WifiCamChecker(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public boolean start() {
        Logger.d("INRY", "WifiCamChecker.start");
        stop();
        this.mIsStop = false;
        this.mAPCheckerThread = new APChecker();
        this.mAPCheckerThread.start();
        return true;
    }

    public boolean stop() {
        Logger.d("INRY", "WifiCamChecker.stop");
        this.mIsStop = true;
        if (this.mAPCheckerThread != null) {
            this.mAPCheckerThread.interrupt();
            try {
                this.mAPCheckerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mAPCheckerThread = null;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sendEvent(int event, Object obj) {
        if (this.mHandler != null) {
            Logger.d("INRY", "WifiCamChecker.sendEvent event=" + event);
            Message msg = new Message();
            msg.what = event;
            msg.obj = obj;
            this.mHandler.sendMessage(msg);
        }
        return false;
    }

    private class APChecker extends Thread {
        List<ScanResult> scanResult = new ArrayList();
        WifiManager wifiManager;

        public APChecker() {
            this.wifiManager = (WifiManager) WifiCamChecker.this.mContext.getSystemService("wifi");
            setName(getClass().getSimpleName());
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Logger.d("INRY", "WifiCamChecker.thread start");
            try {
                int wifistate = this.wifiManager.getWifiState();
                if (wifistate == 1 || wifistate == 0) {
                    Logger.d("INRY", "WifiCamChecker.thread.wifiEnable(true)");
                    this.wifiManager.setWifiEnabled(true);
                }
                Logger.d("INRY", "WifiCamChecker.thread.waitForWifiEnabled >");
                waitForWifiEnabled();
                Logger.d("INRY", "WifiCamChecker.thread.waitForWifiEnabled <");
                Logger.d("INRY", "WifiCamChecker.thread.scanCamAP >");
                int nDetectedAPCount = scanCamAP();
                Logger.d("INRY", "WifiCamChecker.thread.scanCamAP < Count=" + nDetectedAPCount);
                WifiCamChecker.this.sendEvent(9, Integer.valueOf(nDetectedAPCount));
            } catch (InterruptedException e) {
                WifiCamChecker.this.sendEvent(10, null);
            }
            Logger.d("INRY", "WifiCamChecker.thread end");
        }

        private void waitForWifiEnabled() throws InterruptedException {
            while (!this.wifiManager.isWifiEnabled() && !WifiCamChecker.this.mIsStop) {
                sleep(500L);
            }
        }

        private int scanCamAP() throws InterruptedException {
            int nTryCnt = 3;
            boolean scanedAp = false;
            sleep(1000L);
            this.scanResult.clear();
            List<String> scanResultUniq = new ArrayList<>();
            while (!WifiCamChecker.this.mIsStop && !scanedAp) {
                Logger.d("INRY", "WifiCamChecker.thread scanCamAP 1");
                this.wifiManager.startScan();
                sleep(1000L);
                Logger.d("INRY", "WifiCamChecker.thread scanCamAP 2");
                if (WifiCamChecker.this.mIsStop) {
                    break;
                }
                Logger.d("INRY", "WifiCamChecker.thread scanCamAP 3");
                List<ScanResult> scanResultTmp = this.wifiManager.getScanResults();
                if (scanResultTmp == null) {
                    Logger.d("INRY", "WifiCamChecker.thread scanCamAP 4");
                } else {
                    debugResult(scanResultTmp);
                    Logger.d("INRY", "WifiCamChecker.thread scanCamAP 5");
                    for (ScanResult sr : scanResultTmp) {
                        if (!scanResultUniq.contains(sr.SSID) && Utils.supportDSCPrefix(sr.SSID)) {
                            scanResultUniq.add(sr.SSID);
                            this.scanResult.add(sr);
                        }
                    }
                    nTryCnt--;
                    if (nTryCnt <= 0) {
                        scanedAp = true;
                    }
                }
            }
            return this.scanResult.size();
        }

        private void debugResult(List<ScanResult> list) {
            Logger.d("INRY", "WifiCamChecker.thread debug scan listCount=" + list.size());
            for (int i = 0; i < list.size(); i++) {
                ScanResult sr = list.get(i);
                Logger.d("INRY", "WifiCamChecker.thread debug i=" + i + ", ssid=" + sr.SSID);
            }
        }
    }
}

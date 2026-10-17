package com.samsungimaging.filesharing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.samsungimaging.filesharing.util.ExToast;
import com.samsungimaging.filesharing.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WifiDialog extends Dialog {
    public static final String TAG = "INRY";
    AccessPointAdapter accessPointAdapter;
    Button btnClose;
    Button btnRefresh;
    Comparator<ScanResult> compare;
    boolean isConnected;
    ListView listView;
    AlertDialog.Builder mAdialog;
    Button mConnectBtn;
    private Context mContext;
    ExToast mExToast;
    private Handler mHandler;
    WifiManager mWifiManager;
    ProgressBar progressBar;
    List<ScanResult> scanResult;
    public String selectedSSID;
    public ScanResult selectedSr;
    List<WifiConfiguration> wifiConfigurationList;
    WifiStateChangedReceiver wifiStateChangedReceiver;

    public WifiDialog(Context context, Handler handler) {
        super(context, R.style.ml_Theme_Dialog);
        this.mContext = null;
        this.scanResult = Collections.synchronizedList(new ArrayList());
        this.wifiStateChangedReceiver = new WifiStateChangedReceiver(this, null);
        this.mAdialog = null;
        this.mExToast = ExToast.getInstance();
        this.isConnected = false;
        this.compare = new Comparator<ScanResult>() { // from class: com.samsungimaging.filesharing.WifiDialog.1
            @Override // java.util.Comparator
            public int compare(ScanResult object1, ScanResult object2) {
                int p1 = object1.level;
                int p2 = object2.level;
                if (p1 > p2) {
                    return -1;
                }
                return p1 == p2 ? 0 : 1;
            }
        };
        this.mContext = context;
        this.mHandler = handler;
        this.scanResult.clear();
        this.scanResult.addAll(WifiConnector.getInstance().srs);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ml_wifi);
        this.mExToast.register(this.mContext, 1, R.string.ml_ap_connected, 1);
        this.mExToast.register(this.mContext, 2, R.string.ml_toast_connect_to_ap, 1);
        this.mExToast.register(this.mContext, 3, R.string.ml_wifi_fail_scan, 1);
        getWindow().addFlags(128);
        this.accessPointAdapter = new AccessPointAdapter(this.mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mContext.registerReceiver(this.wifiStateChangedReceiver, filter);
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(true);
        }
        this.btnRefresh = (Button) findViewById(R.id.ml_wifi_refresh);
        this.btnRefresh.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Logger.d("INRY", "accessPointScan 1");
                WifiDialog.this.accessPointScan();
                WifiDialog.this.accessPointAdapter.notifyDataSetChanged();
            }
        });
        this.btnClose = (Button) findViewById(R.id.ml_close_wifi_setting);
        this.btnClose.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WifiDialog.this.mExToast.unregister(1);
                WifiDialog.this.mExToast.unregister(2);
                WifiDialog.this.mExToast.unregister(3);
                WifiDialog.this.mExToast.hideAll();
                if (WifiDialog.this.btnClose.getText().equals(WifiDialog.this.btnClose.getResources().getText(R.string.ml_wifi_cancel))) {
                    ((FileSharing) WifiDialog.this.mContext).systemExit();
                } else {
                    WifiDialog.this.dismiss();
                }
            }
        });
        this.progressBar = (ProgressBar) findViewById(R.id.progress_small);
        this.listView = (ListView) findViewById(R.id.ml_ac_list);
        this.wifiConfigurationList = this.mWifiManager.getConfiguredNetworks();
        stateWifi();
        Logger.d("INRY", "accessPointScan 2");
        this.listView.setAdapter((ListAdapter) this.accessPointAdapter);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        ((FileSharing) this.mContext).systemExit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void accessPointScan() {
        List<ScanResult> currentScanResult = WifiConnector.getInstance().scanSoftAP(3);
        if (this.scanResult == null) {
            this.mExToast.show(3);
        }
        this.progressBar.setVisibility(0);
        this.scanResult.clear();
        this.scanResult = currentScanResult;
        switch (this.scanResult.size()) {
            case WifiApManager.WIFI_AP_STATE_DISABLING /* 0 */:
                ((TextView) findViewById(R.id.ml_ac_no)).setText(R.string.ml_wifi_no_ap);
                ((TextView) findViewById(R.id.ml_ac_no)).setVisibility(0);
                break;
            default:
                ((TextView) findViewById(R.id.ml_ac_no)).setVisibility(8);
                Collections.sort(this.scanResult, new Comparator<ScanResult>() { // from class: com.samsungimaging.filesharing.WifiDialog.4
                    @Override // java.util.Comparator
                    public int compare(ScanResult sr1, ScanResult sr2) {
                        return Integer.signum(sr2.level - sr1.level);
                    }
                });
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSignalResId(int signal, boolean key) {
        switch (WifiManager.calculateSignalLevel(signal, 4)) {
            case WifiApManager.WIFI_AP_STATE_DISABLING /* 0 */:
                return key ? R.drawable.ml_ap_lock_connection_wifi_01 : R.drawable.ml_ap_connection_wifi_01;
            case 1:
                return key ? R.drawable.ml_ap_lock_connection_wifi_02 : R.drawable.ml_ap_connection_wifi_02;
            case 2:
                return key ? R.drawable.ml_ap_lock_connection_wifi_03 : R.drawable.ml_ap_connection_wifi_03;
            case 3:
                return key ? R.drawable.ml_ap_lock_connection_wifi_04 : R.drawable.ml_ap_connection_wifi_04;
            default:
                return key ? R.drawable.ml_ap_lock_connection_wifi_00 : R.drawable.ml_ap_connection_wifi_00;
        }
    }

    public class AccessPointAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public AccessPointAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return WifiDialog.this.scanResult.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("INRY", "getView Start!!!");
            ScanResult sr = WifiDialog.this.scanResult.get(position);
            Logger.d("INRY", String.valueOf(sr.SSID) + "   :" + sr.capabilities);
            String capa = sr.capabilities;
            String nowSSID = WifiDialog.this.mWifiManager.getConnectionInfo().getSSID();
            TextView checkedTextView = (TextView) this.mInflater.inflate(R.layout.ml_simple_list_item_1, (ViewGroup) null);
            Logger.d("INRY", String.valueOf(capa) + "  " + WifiDialog.this.WPSChecker(capa));
            if (!capa.equals("") && !WifiDialog.this.WPSChecker(capa)) {
                Logger.d("INRY", String.valueOf(capa) + " locked ");
                checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, WifiDialog.this.getSignalResId(sr.level, true), 0);
            } else {
                Logger.d("INRY", String.valueOf(capa) + " open ");
                checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, WifiDialog.this.getSignalResId(sr.level, false), 0);
            }
            checkedTextView.setText(sr.SSID);
            Logger.d("INRY", capa);
            checkedTextView.setTag(sr);
            if (sr.SSID.equals(nowSSID) && WifiDialog.this.isConnected) {
                checkedTextView.setTextColor(-7554461);
                checkedTextView.setText(Html.fromHtml("<b>" + sr.SSID + "</b>"));
            }
            checkedTextView.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    final ScanResult sr2 = (ScanResult) v.getTag();
                    if (!Utils.unsupportDSCPrefix(sr2.SSID)) {
                        WifiDialog.this.mExToast.show(2);
                        WifiDialog.this.selectedSSID = sr2.SSID;
                        WifiDialog.this.selectedSr = sr2;
                        int networkID = -1;
                        for (WifiConfiguration w : WifiDialog.this.wifiConfigurationList) {
                            if (w.SSID.equals("\"" + WifiDialog.this.selectedSSID + "\"")) {
                                networkID = w.networkId;
                            }
                        }
                        String nowSSID2 = WifiDialog.this.mWifiManager.getConnectionInfo().getSSID();
                        if (sr2.SSID.equals(nowSSID2) && WifiDialog.this.isConnected) {
                            Logger.d("INRY", "ALREADY CONNECTED!!");
                            return;
                        }
                        Logger.d("INRY", "Capa : " + sr2.capabilities + ", networkId=" + networkID);
                        if (networkID != -1) {
                            WifiDialog.this.progressBar.setVisibility(0);
                            boolean bEnableNetwork = WifiDialog.this.mWifiManager.enableNetwork(networkID, true);
                            if (bEnableNetwork) {
                                Logger.d("INRY", "Connected!");
                                return;
                            } else {
                                Logger.d("INRY", "Disconnected!");
                                return;
                            }
                        }
                        final WifiConfiguration wfc = new WifiConfiguration();
                        wfc.SSID = "\"".concat(WifiDialog.this.selectedSSID).concat("\"");
                        wfc.status = 1;
                        if (sr2.capabilities.equals("") || WifiDialog.this.WPSChecker(sr2.capabilities)) {
                            Logger.d("INRY", "openopenopen!!");
                            wfc.allowedKeyManagement.set(0);
                            wfc.allowedProtocols.set(1);
                            wfc.allowedProtocols.set(0);
                            wfc.allowedAuthAlgorithms.clear();
                            wfc.allowedPairwiseCiphers.set(2);
                            wfc.allowedGroupCiphers.set(0);
                            wfc.allowedGroupCiphers.set(1);
                            wfc.allowedGroupCiphers.set(3);
                            wfc.allowedGroupCiphers.set(2);
                            WifiDialog.this.connect(sr2);
                            return;
                        }
                        if (sr2.capabilities.contains("WEP")) {
                            Logger.d("INRY", "wepwepwep!!");
                            LayoutInflater factory = LayoutInflater.from(AccessPointAdapter.this.mContext);
                            final View textEntryView = factory.inflate(R.layout.ml_alert_dialog_text_entry, (ViewGroup) null);
                            if (WifiDialog.this.mAdialog == null) {
                                WifiDialog.this.mAdialog = new AlertDialog.Builder(AccessPointAdapter.this.mContext);
                                WifiDialog.this.mAdialog.setTitle(R.string.ml_wifi_need_password);
                                WifiDialog.this.mAdialog.setView(textEntryView);
                                WifiDialog.this.mAdialog.setCancelable(false);
                                WifiDialog.this.mAdialog.setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.1
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String password = ((EditText) textEntryView.findViewById(R.id.ml_password)).getText().toString();
                                        wfc.allowedKeyManagement.set(0);
                                        wfc.allowedAuthAlgorithms.set(0);
                                        wfc.allowedAuthAlgorithms.set(1);
                                        int length = password.length();
                                        if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                                            wfc.wepKeys[0] = password;
                                        } else {
                                            wfc.wepKeys[0] = String.valueOf('\"') + password + '\"';
                                        }
                                        WifiDialog.this.connect(sr2);
                                        WifiDialog.this.mAdialog = null;
                                    }
                                });
                                WifiDialog.this.mAdialog.setNegativeButton(R.string.ml_alert_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.2
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        WifiDialog.this.mAdialog = null;
                                    }
                                });
                                AlertDialog d = WifiDialog.this.mAdialog.show();
                                WifiDialog.this.mConnectBtn = d.getButton(-1);
                                WifiDialog.this.mConnectBtn.setEnabled(false);
                                EditText et = (EditText) textEntryView.findViewById(R.id.ml_password);
                                et.addTextChangedListener(new TextWatcher() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.3
                                    @Override // android.text.TextWatcher
                                    public void afterTextChanged(Editable s) {
                                    }

                                    @Override // android.text.TextWatcher
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override // android.text.TextWatcher
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        int nLen = s.toString().length();
                                        if (1 > nLen || nLen > 26) {
                                            if (WifiDialog.this.mConnectBtn != null) {
                                                WifiDialog.this.mConnectBtn.setEnabled(false);
                                            }
                                        } else if (WifiDialog.this.mConnectBtn != null) {
                                            WifiDialog.this.mConnectBtn.setEnabled(true);
                                        }
                                    }
                                });
                                return;
                            }
                            return;
                        }
                        Logger.d("INRY", "wpawpawpa!!");
                        LayoutInflater factory2 = LayoutInflater.from(AccessPointAdapter.this.mContext);
                        final View textEntryView2 = factory2.inflate(R.layout.ml_alert_dialog_text_entry, (ViewGroup) null);
                        if (WifiDialog.this.mAdialog == null) {
                            WifiDialog.this.mAdialog = new AlertDialog.Builder(AccessPointAdapter.this.mContext);
                            WifiDialog.this.mAdialog.setTitle(R.string.ml_wifi_need_password);
                            WifiDialog.this.mAdialog.setView(textEntryView2);
                            WifiDialog.this.mAdialog.setCancelable(false);
                            WifiDialog.this.mAdialog.setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.4
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String password = ((EditText) textEntryView2.findViewById(R.id.ml_password)).getText().toString();
                                    wfc.allowedAuthAlgorithms.set(0);
                                    wfc.allowedProtocols.set(1);
                                    wfc.allowedKeyManagement.set(1);
                                    wfc.allowedPairwiseCiphers.set(2);
                                    wfc.allowedPairwiseCiphers.set(1);
                                    wfc.allowedGroupCiphers.set(3);
                                    wfc.allowedGroupCiphers.set(2);
                                    wfc.preSharedKey = "\"".concat(password).concat("\"");
                                    WifiDialog.this.connect(sr2);
                                    WifiDialog.this.mAdialog = null;
                                }
                            });
                            WifiDialog.this.mAdialog.setNegativeButton(R.string.ml_alert_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.5
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    WifiDialog.this.mAdialog = null;
                                }
                            });
                            AlertDialog d2 = WifiDialog.this.mAdialog.show();
                            WifiDialog.this.mConnectBtn = d2.getButton(-1);
                            WifiDialog.this.mConnectBtn.setEnabled(false);
                            EditText et2 = (EditText) textEntryView2.findViewById(R.id.ml_password);
                            et2.addTextChangedListener(new TextWatcher() { // from class: com.samsungimaging.filesharing.WifiDialog.AccessPointAdapter.1.6
                                @Override // android.text.TextWatcher
                                public void afterTextChanged(Editable s) {
                                }

                                @Override // android.text.TextWatcher
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override // android.text.TextWatcher
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    int nLen = s.toString().length();
                                    if (8 > nLen || nLen > 63) {
                                        if (WifiDialog.this.mConnectBtn != null) {
                                            WifiDialog.this.mConnectBtn.setEnabled(false);
                                        }
                                    } else if (WifiDialog.this.mConnectBtn != null) {
                                        WifiDialog.this.mConnectBtn.setEnabled(true);
                                    }
                                }
                            });
                            return;
                        }
                        return;
                    }
                    Message msg = new Message();
                    msg.what = 43;
                    WifiDialog.this.mHandler.sendMessage(msg);
                }
            });
            return checkedTextView;
        }
    }

    private class WifiStateChangedReceiver extends BroadcastReceiver {
        private WifiStateChangedReceiver() {
        }

        /* synthetic */ WifiStateChangedReceiver(WifiDialog wifiDialog, WifiStateChangedReceiver wifiStateChangedReceiver) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                WifiDialog.this.progressBar.setVisibility(4);
                return;
            }
            if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTING || info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    if (WifiConnector.getInstance().isConnectedSoftAp(true)) {
                        WifiDialog.this.mExToast.show(1);
                        WifiDialog.this.isConnected = true;
                        WifiDialog.this.btnClose.setText(R.string.ml_wifi_close);
                    } else {
                        WifiConnector.getInstance().disconnectNetwork();
                        WifiDialog.this.btnClose.setText(R.string.ml_wifi_cancel);
                    }
                } else {
                    WifiDialog.this.isConnected = false;
                    WifiDialog.this.btnClose.setText(R.string.ml_wifi_cancel);
                }
            }
            switch (WifiDialog.this.mWifiManager.getWifiState()) {
                case WifiApManager.WIFI_AP_STATE_DISABLING /* 0 */:
                case 1:
                    WifiDialog.this.scanResult.clear();
                    break;
                case 2:
                    WifiDialog.this.accessPointScan();
                    break;
            }
            WifiDialog.this.stateWifi();
            WifiDialog.this.accessPointAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stateWifi() {
        switch (this.mWifiManager.getWifiState()) {
            case 1:
                this.progressBar.setVisibility(4);
                break;
        }
    }

    public boolean connect(ScanResult sr) {
        boolean bRet = WifiConnector.getInstance().connectSingleApAuto(sr, 3);
        if (!bRet) {
            WifiConnector.getInstance().disconnectNetwork();
            this.btnClose.setText(R.string.ml_wifi_cancel);
            accessPointScan();
            this.accessPointAdapter.notifyDataSetChanged();
        }
        return bRet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean WPSChecker(String ap) {
        Logger.d("INRY", "ap.length() is " + ap.length());
        if (ap.length() == 5) {
            if (ap.equals("[WPS]") || ap.equals("[ESS]")) {
                return true;
            }
        } else if (ap.length() == 10 && ap.equals("[WPS][ESS]")) {
            return true;
        }
        return false;
    }
}

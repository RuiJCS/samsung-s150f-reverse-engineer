package com.samsungimaging.connectionmanager.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.connector.CMCameraAPConnector;
import com.samsungimaging.connectionmanager.app.cm.connector.LastRequestedCameraInfo;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SearchAPDialog extends CustomDialog implements AdapterView.OnItemClickListener {
    private Activity mActivity;
    private AnimationDrawable mAnimation;
    private List<ScanResult> mCameraSrs;
    private ConnectivityManager mConnectivityManager;
    private ListView mListView;
    private BaseAdapter mScannedListAdapter;
    private Handler mScannedListRefreshHandler;
    private WifiManager mWifiManager;

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkRequested(String ssid) {
        ScanResult sr = LastRequestedCameraInfo.getInstance().getLastRequestedCameraInfo();
        String last_ssid = LastRequestedCameraInfo.getInstance().getLastRequestedCameraSSID();
        if (sr != null) {
            String last_scanresult_ssid = LastRequestedCameraInfo.getInstance().getLastRequestedCameraInfo().SSID;
            if (last_scanresult_ssid == null || last_scanresult_ssid.isEmpty() || !last_scanresult_ssid.contains(ssid)) {
                return false;
            }
            return true;
        }
        if (last_ssid == null || !last_ssid.contains(ssid)) {
            return false;
        }
        return true;
    }

    public SearchAPDialog(Activity activity) {
        super(activity);
        this.mWifiManager = null;
        this.mConnectivityManager = null;
        this.mCameraSrs = new ArrayList();
        this.mListView = null;
        this.mAnimation = null;
        this.mScannedListRefreshHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.dialog.SearchAPDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                SearchAPDialog.this.refreshScannedListView();
                SearchAPDialog.this.mScannedListRefreshHandler.sendEmptyMessageDelayed(0, 2000L);
            }
        };
        this.mScannedListAdapter = new BaseAdapter() { // from class: com.samsungimaging.connectionmanager.dialog.SearchAPDialog.2
            @Override // android.widget.Adapter
            public int getCount() {
                if (SearchAPDialog.this.mCameraSrs != null) {
                    return SearchAPDialog.this.mCameraSrs.size();
                }
                return 0;
            }

            @Override // android.widget.Adapter
            public ScanResult getItem(int position) {
                return (ScanResult) SearchAPDialog.this.mCameraSrs.get(position);
            }

            @Override // android.widget.Adapter
            public long getItemId(int position) {
                return position;
            }

            @Override // android.widget.Adapter
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) SearchAPDialog.this.getContext().getSystemService("layout_inflater");
                    view = inflater.inflate(R.layout.ap_list_item, (ViewGroup) null);
                }
                final ScanResult scanResult = getItem(position);
                final boolean isSupportDSC = !CMUtil.NOTsupportDSCPrefix(scanResult.SSID);
                boolean isConnectionRequested = SearchAPDialog.this.checkRequested(scanResult.SSID);
                TextView title = (TextView) view.findViewById(R.id.title);
                title.setSelected(true);
                title.setText(scanResult.SSID);
                title.setEnabled(isSupportDSC);
                ImageView button = (ImageView) view.findViewById(R.id.button);
                button.setImageResource(R.drawable.sca_popup_icon_connect);
                button.setVisibility(isSupportDSC ? 0 : 8);
                button.setSelected(isConnectionRequested);
                button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.SearchAPDialog.2.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        SearchAPDialog.this.connect(isSupportDSC, scanResult);
                    }
                });
                view.setTag(Boolean.valueOf(isSupportDSC));
                return view;
            }
        };
        this.mActivity = activity;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, onCreate");
        this.mWifiManager = (WifiManager) getContext().getSystemService("wifi");
        this.mConnectivityManager = (ConnectivityManager) getContext().getSystemService("connectivity");
        initContent();
        super.onCreate(savedInstanceState);
        initCustomContent();
        if (CMUtil.isTestMode(getContext())) {
            LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
            buttonLayout.setVisibility(4);
        }
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, onStart");
        this.mAnimation.start();
        if (this.mCameraSrs.size() > 0) {
            this.mCameraSrs.clear();
        }
        this.mScannedListRefreshHandler.sendEmptyMessage(1);
        initCustomContent();
        super.onStart();
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES, CMConstants.WIFI_SCAN_TIMER);
        int count = DatabaseManager.getCountForAP(getContext());
        if (count > 0 && !CMInfo.getInstance().getIsNFCLaunchFlag()) {
            Toast.makeText(getContext(), getContext().getString(R.string.cm_search_ap_dialog_toast01), 0).show();
        }
    }

    @Override // android.app.Dialog
    protected void onStop() {
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, onStop");
        LastRequestedCameraInfo.getInstance().setLastRequestedCameraInfo(null);
        LastRequestedCameraInfo.getInstance().setLastRequestedCameraSSID(null);
        this.mAnimation.stop();
        if (this.mScannedListRefreshHandler != null && this.mScannedListRefreshHandler.hasMessages(0)) {
            this.mScannedListRefreshHandler.removeMessages(0);
        }
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_STOP_WIFI_SCAN_TIMER_FOR_MIMUTES);
        super.onStop();
    }

    public void refreshScannedListView() {
        new ArrayList();
        List<ScanResult> cameraSrs_temp = CMService.getInstance().getScannedList();
        boolean refresh = false;
        int same_ap_count = 0;
        if (this.mCameraSrs != null && cameraSrs_temp != null) {
            if (this.mCameraSrs.size() != cameraSrs_temp.size()) {
                refresh = true;
            } else {
                for (int i = 0; i < cameraSrs_temp.size(); i++) {
                    for (int j = 0; j < this.mCameraSrs.size(); j++) {
                        String scan_result_ap = cameraSrs_temp.get(i).SSID;
                        String showing_ap = this.mCameraSrs.get(j).SSID;
                        if (scan_result_ap.equals(showing_ap)) {
                            same_ap_count++;
                        }
                    }
                }
                refresh = same_ap_count != cameraSrs_temp.size();
            }
        }
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, refreshScannedListView, refresh = " + refresh + ", mCameraSrs.size() = " + this.mCameraSrs.size() + ", cameraSrs_temp.size() = " + cameraSrs_temp.size());
        if (refresh) {
            this.mCameraSrs = cameraSrs_temp;
            this.mScannedListAdapter.notifyDataSetChanged();
        }
    }

    private void initContent() {
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, initContent");
        setView(R.layout.dialog_search_ap);
        setNeutralButton(R.string.cancel_search, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.SearchAPDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SearchAPDialog.this.beforeFinish();
                SearchAPDialog.this.dismiss();
            }
        });
        ImageView imageView = (ImageView) this.mView.findViewById(R.id.animation_view);
        this.mAnimation = (AnimationDrawable) imageView.getBackground();
    }

    private void initCustomContent() {
        this.mListView = (ListView) this.mView.findViewById(R.id.list_view);
        this.mListView.setEmptyView(this.mView.findViewById(R.id.empty_view));
        this.mListView.setAdapter((ListAdapter) this.mScannedListAdapter);
        this.mListView.setOnItemClickListener(this);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean isSupportDSC = ((Boolean) view.getTag()).booleanValue();
        ScanResult scanResult = (ScanResult) this.mScannedListAdapter.getItem(position);
        connect(isSupportDSC, scanResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connect(boolean isSupportDSC, ScanResult scanResult) {
        if (isSupportDSC) {
            LastRequestedCameraInfo.getInstance().setLastRequestedCameraInfo(scanResult);
            LastRequestedCameraInfo.getInstance().setLastRequestedCameraSSID(scanResult.SSID);
            this.mScannedListAdapter.notifyDataSetChanged();
            CMUtil.addToManagedAPList(getContext(), scanResult.SSID);
            WifiInfo info = this.mWifiManager.getConnectionInfo();
            NetworkInfo networkInfo = this.mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                NetworkInfo.DetailedState state_connectivitymanager = networkInfo.getDetailedState();
                String typeName = networkInfo.getTypeName();
                boolean available = networkInfo.isAvailable();
                Trace.d(CMConstants.TAG_NAME, "onClick : ~~~~~~~~~~~~~~~~~~~~~~~~~ state_connectivitymanager : " + state_connectivitymanager);
                Trace.d(CMConstants.TAG_NAME, "onClick : ~~~~~~~~~~~~~~~~~~~~~~~~~ typeName : " + typeName + ", available : " + available);
                if (state_connectivitymanager == NetworkInfo.DetailedState.CONNECTED) {
                    if ("WIFI".equals(typeName) && available) {
                        CMService.IS_WIFI_CONNECTED = CMUtil.supportDSCPrefix(info.getSSID()) && CMUtil.isManagedSSID(getContext(), info.getSSID()) && CMUtil.isEqualSSID(scanResult.SSID, info.getSSID());
                        Trace.d(CMConstants.TAG_NAME, "onClick, IS_CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                        if (CMService.IS_WIFI_CONNECTED) {
                            Trace.d(CMConstants.TAG_NAME, "already connected by wifi-manager!");
                            if (CMService.getInstance() != null) {
                                CMService.getInstance().checkSubApplicationType();
                                return;
                            }
                            return;
                        }
                        Trace.d(CMConstants.TAG_NAME, "test06");
                        CMCameraAPConnector.getInstance().setSr(scanResult, getContext(), this.mWifiManager);
                        return;
                    }
                    Trace.d(CMConstants.TAG_NAME, "test06-1");
                    CMCameraAPConnector.getInstance().setSr(scanResult, getContext(), this.mWifiManager);
                    return;
                }
                return;
            }
            Trace.d(CMConstants.TAG_NAME, "test07");
            CMCameraAPConnector.getInstance().setSr(scanResult, getContext(), this.mWifiManager);
            return;
        }
        Trace.d(CMConstants.TAG_NAME, "NOTsupportDSCPrefix, onClick!!!");
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_SHOW_NOT_SUPPORTED_DIALOG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beforeFinish() {
        Trace.d(CMConstants.TAG_NAME, "SearchAPDialog, beforeFinish");
        CMService.getInstance().NFCConnectionTryCancel();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        super.onBackPressed();
        beforeFinish();
        dismiss();
    }
}

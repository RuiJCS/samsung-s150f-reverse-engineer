package com.samsungimaging.connectionmanager.app.pullservice;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseActivity;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.app.pullservice.controller.UPNPController;
import com.samsungimaging.connectionmanager.app.pullservice.util.Utils;
import com.samsungimaging.connectionmanager.dialog.ConnectionWaitDialog;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.util.ImageCache;
import com.samsungimaging.connectionmanager.util.Trace;
import org.cybergarage.upnp.Device;

/* JADX INFO: loaded from: classes.dex */
public abstract class PullService extends BaseActivity implements View.OnTouchListener {
    private Handler handler;
    protected UPNPController upnpController;
    private Trace.Tag TAG = Trace.Tag.COMMON;
    protected String connectedSsid = null;
    private String prefixAgent = "SEC_RVF_ML_";
    private boolean bCloseByFinishSafe = false;
    private WifiManager wifiManager = null;
    protected DeviceController mDeviceController = null;
    protected CustomDialog mConfirmDialog = null;
    private ImageCache mImageCacheThumbnail = null;
    private ImageCache mImageCacheViewer = null;
    private BroadcastReceiver BroadcastInfoReceiver = new BroadcastReceiver() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.1
        private static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;

        static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState() {
            int[] iArr = $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;
            if (iArr == null) {
                iArr = new int[NetworkInfo.DetailedState.values().length];
                try {
                    iArr[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.BLOCKED.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTING.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.FAILED.ordinal()] = 8;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.IDLE.ordinal()] = 9;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 11;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SUSPENDED.ordinal()] = 12;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.VERIFYING_POOR_LINK.ordinal()] = 13;
                } catch (NoSuchFieldError e13) {
                }
                $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = iArr;
            }
            return iArr;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Trace.d(PullService.this.TAG, "start onReceive() action : " + intent.getAction());
            String action = intent.getAction();
            if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int level = intent.getIntExtra("level", -1);
                if (level != -1 && level <= 4) {
                    Trace.d(PullService.this.TAG, "Battery level : " + level);
                    if (PullService.this.handler.hasMessages(Const.MsgBoxId.MSGBOX_WAIT_CONNECTION)) {
                        PullService.this.dismissDialog(Const.MsgBoxId.MSGBOX_WAIT_CONNECTION);
                    }
                    PullService.this.upnpController.disconnect();
                    PullService.this.showDialog(Const.MsgBoxId.MSGBOX_BATTERY_EMPTY);
                    return;
                }
                return;
            }
            if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo.DetailedState state = info.getDetailedState();
                switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[state.ordinal()]) {
                    case 4:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTED");
                        break;
                    case 5:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTING");
                        break;
                    case 6:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTED");
                        PullService.this.showDialog(Const.MsgBoxId.MSGBOX_NETWORK_DISCONNECT);
                        PullService.this.handler.sendEmptyMessageDelayed(Const.MsgID.TIMER_ON_EXIT_BY_NETWORK_ERROR, 500L);
                        break;
                    case 7:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTING");
                        break;
                    case 8:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - FAILED");
                        break;
                    case 9:
                    default:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - Unknown");
                        break;
                    case 10:
                        Trace.d(PullService.this.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - OBTAINING_IPADDR");
                        break;
                }
                return;
            }
            if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(action)) {
                boolean connected = intent.getBooleanExtra("connected", false);
                if (!connected) {
                    PullService.this.showDialog(Const.MsgBoxId.MSGBOX_NETWORK_DISCONNECT);
                    PullService.this.handler.sendEmptyMessageDelayed(Const.MsgID.TIMER_ON_EXIT_BY_NETWORK_ERROR, 500L);
                }
            }
        }
    };

    public abstract void closeService();

    public abstract void onAlive();

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        String accessMethod;
        Trace.d(this.TAG, "start PullService.onCreate()");
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
        init();
        initDiskCache();
        Intent bat = registerReceiver(this.BroadcastInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int batLevel = bat.getIntExtra("level", -1);
        if (batLevel != -1 && batLevel <= 4) {
            showDialog(Const.MsgBoxId.MSGBOX_BATTERY_EMPTY);
            return;
        }
        if (!this.upnpController.isConnected()) {
            this.handler.sendEmptyMessageDelayed(200, 180000L);
            showDialog(Const.MsgBoxId.MSGBOX_WAIT_CONNECTION);
            boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(this.connectedSsid);
            int ssdpPort = 1900;
            String prefixAgent = "SEC_RVF_ML_";
            if (isOldCamera) {
                if (CMInfo.getInstance().getIsNFCLaunchFlag()) {
                    accessMethod = "nfc";
                } else {
                    accessMethod = "manual";
                }
                if (CMService.ACTIVE_SERVICE == 2) {
                    ssdpPort = 1900;
                    prefixAgent = "SEC_RVF_";
                } else if (CMService.ACTIVE_SERVICE == 1) {
                    ssdpPort = 1901;
                    prefixAgent = com.samsungimaging.connectionmanager.app.pushservice.common.Const.Config.USER_AGENT_PREFIX;
                }
            } else {
                accessMethod = "manual";
                ssdpPort = 1900;
                prefixAgent = "SEC_RVF_ML_";
            }
            this.upnpController.connect(Utils.getAgent(getApplicationContext(), prefixAgent), accessMethod, ssdpPort);
        }
    }

    private void initDiskCache() {
        this.mImageCacheThumbnail = new ImageCache(getApplicationContext(), GalleryFragment.DISK_CACHE_DIR_THUMBNAIL);
        this.mImageCacheViewer = new ImageCache(getApplicationContext(), GalleryFragment.DISK_CACHE_DIR_VIEWER);
        this.mImageCacheThumbnail.initDiskCache();
        this.mImageCacheViewer.initDiskCache();
    }

    private void closeDiskCache() {
        if (this.mImageCacheThumbnail != null) {
            this.mImageCacheThumbnail.closeCache();
            this.mImageCacheThumbnail = null;
        }
        if (this.mImageCacheViewer != null) {
            this.mImageCacheViewer.closeCache();
            this.mImageCacheViewer = null;
        }
    }

    public void CheckDirtyCache(String filePath) {
        if (this.mImageCacheThumbnail != null && this.mImageCacheThumbnail.hasSnapshotInDiskCache(filePath)) {
            Trace.i(this.TAG, "CheckDirtyCache remove Thumbnail Cache : " + filePath);
            this.mImageCacheThumbnail.remove(filePath);
        }
        if (this.mImageCacheViewer != null && this.mImageCacheViewer.hasSnapshotInDiskCache(filePath)) {
            Trace.i(this.TAG, "CheckDirtyCache remove Viewer Cache : " + filePath);
            this.mImageCacheViewer.remove(filePath);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        Trace.d(this.TAG, "start PullService.onStart()");
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        Trace.d(this.TAG, "start PullService.onResume()");
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onPause() {
        Trace.d(this.TAG, "start PullService.onPause()");
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onStop() {
        Trace.d(this.TAG, "start PullService.onStop()");
        super.onStop();
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.d(this.TAG, "start PullService.onDestroy()");
        super.onDestroy();
        closeDiskCache();
        unregisterBroadcastReceiver();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Trace.d(this.TAG, "start PullService.onBackPressed()");
        if (isBackPressAvailable()) {
            unregisterBroadcastReceiver();
            onUnsubscribe();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        Trace.d(this.TAG, "start PullService.onTouch()");
        return false;
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle args) {
        Trace.d(this.TAG, "start onCreateDialog() id : " + id);
        switch (id) {
            case 2000:
                this.mConfirmDialog = new CustomDialog(this);
                this.mConfirmDialog.setIcon(R.drawable.rvf_popup_icon);
                this.mConfirmDialog.setTitle(R.string.rvf_msg_title);
                this.mConfirmDialog.setMessage(args.getString(Const.MsgKey.DIALOG_MESSAGE_KEY));
                this.mConfirmDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        PullService.this.dismissDialog(2000);
                    }
                });
                return this.mConfirmDialog;
            case Const.MsgBoxId.MSGBOX_WAIT_CONNECTION /* 2001 */:
                ConnectionWaitDialog connectionWaitDialog = new ConnectionWaitDialog(this);
                connectionWaitDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.3
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                PullService.this.handler.removeMessages(200);
                                PullService.this.onUnsubscribe();
                                PullService.this.wifiManager.disconnect();
                                break;
                        }
                        return false;
                    }
                });
                connectionWaitDialog.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        PullService.this.handler.removeMessages(200);
                        PullService.this.onUnsubscribe();
                        PullService.this.wifiManager.disconnect();
                    }
                });
                return connectionWaitDialog;
            case Const.MsgBoxId.MSGBOX_SHUTDOWN_BY_NOT_RESPONSE /* 2002 */:
                CustomDialog dialog = new CustomDialog(this);
                dialog.setIcon(R.drawable.rvf_popup_icon);
                dialog.setTitle(R.string.end);
                dialog.setMessage(R.string.rvf_wait_msg);
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        PullService.this.onUnsubscribe();
                    }
                });
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.6
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog2, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                            case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return dialog;
            case Const.MsgBoxId.MSGBOX_BATTERY_EMPTY /* 2003 */:
                CustomDialog dialog2 = new CustomDialog(this);
                dialog2.setIcon(R.drawable.rvf_popup_icon);
                dialog2.setTitle(R.string.end);
                dialog2.setMessage(R.string.rvf_battery_empty);
                dialog2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.7
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog3, int which) {
                        PullService.this.bCloseByFinishSafe = true;
                        PullService.this.onUnsubscribe();
                    }
                });
                dialog2.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.8
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog3, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                PullService.this.bCloseByFinishSafe = true;
                                PullService.this.onUnsubscribe();
                                break;
                        }
                        return false;
                    }
                });
                return dialog2;
            case Const.MsgBoxId.MSGBOX_CONNECTION_FAIL /* 2004 */:
                View view = getLayoutInflater().inflate(R.layout.ssc_connection_fail, (ViewGroup) null);
                Button button = (Button) view.findViewById(R.id.ssc_confirm_btn);
                TextView ssc_connection_fail = (TextView) view.findViewById(R.id.ssc_connection_fail);
                ssc_connection_fail.setText(args.getString(Const.MsgKey.DIALOG_MESSAGE_KEY));
                TextView ssc_rejected_camera = (TextView) view.findViewById(R.id.ssc_rejected_camera);
                ssc_rejected_camera.setText(((Object) ssc_rejected_camera.getText()) + CMInfo.getInstance().getConnectedSSID());
                CustomDialog dialog3 = new CustomDialog(this);
                dialog3.setView(view);
                dialog3.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.9
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog4, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                CMService.getInstance().beforefinish(0);
                            default:
                                return false;
                        }
                    }
                });
                button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.10
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        CMService.getInstance().beforefinish(0);
                    }
                });
                return dialog3;
            case Const.MsgBoxId.MSGBOX_QUESTION_EXIT /* 2005 */:
                CustomDialog dialog4 = new CustomDialog(this);
                dialog4.setIcon(R.drawable.ml_question);
                dialog4.setTitle(R.string.end);
                dialog4.setMessage(R.string.ml_exit_question);
                dialog4.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.11
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog5, int which) {
                        PullService.this.showDialog(3000);
                        PullService.this.onUnsubscribe();
                    }
                });
                dialog4.setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null);
                dialog4.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.12
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog5, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return dialog4;
            case Const.MsgBoxId.MSGBOX_NETWORK_DISCONNECT /* 2006 */:
                CustomDialog dialog5 = new CustomDialog(this);
                dialog5.setIcon(R.drawable.rvf_popup_icon);
                dialog5.setTitle(R.string.end);
                dialog5.setMessage(R.string.network_disconnected);
                dialog5.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.13
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog6, int which) {
                        PullService.this.onClose();
                    }
                });
                dialog5.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.14
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog6, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return dialog5;
            case Const.MsgBoxId.MSGBOX_LENS_ERROR /* 2011 */:
                CustomDialog customDialog = new CustomDialog(this);
                customDialog.setMessage(R.string.lens_fail);
                customDialog.setCancelable(false);
                customDialog.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.15
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog6, int which) {
                        PullService.this.dismissDialog(Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
                        PullService.this.dismissDialog(Const.MsgBoxId.MSGBOX_LENS_ERROR);
                    }
                });
                return customDialog;
            case 3000:
                CustomProgressDialog customProgressDialog = new CustomProgressDialog(this);
                customProgressDialog.setCancelable(false);
                customProgressDialog.setProgressStyle(0);
                customProgressDialog.setMessage(getString(R.string.rvf_appclose));
                return customProgressDialog;
            case Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE /* 3001 */:
                CustomProgressDialog customProgressDialog2 = new CustomProgressDialog(this);
                customProgressDialog2.setCancelable(false);
                customProgressDialog2.setProgressStyle(0);
                customProgressDialog2.setMessage(getString(R.string.rvf_service_changing));
                return customProgressDialog2;
            default:
                return super.onCreateDialog(id, args);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        registerReceiver(this.BroadcastInfoReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        try {
            unregisterReceiver(this.BroadcastInfoReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        this.handler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.PullService.16
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Trace.d(PullService.this.TAG, "start handleMessage() msg.what : " + msg.what);
                switch (msg.what) {
                    case 100:
                        PullService.this.handler.removeMessages(200);
                        PullService.this.dismissDialog(Const.MsgBoxId.MSGBOX_WAIT_CONNECTION);
                        PullService.this.onAlive();
                        break;
                    case 101:
                        PullService.this.onUnsubscribe();
                        break;
                    case Const.MsgID.CONNECTOR_ON_OCCURED_PARSER_EXCEPTION /* 118 */:
                        PullService.this.handler.removeMessages(200);
                        PullService.this.dismissDialog(Const.MsgBoxId.MSGBOX_WAIT_CONNECTION);
                        String message = (String) msg.obj;
                        Trace.d(PullService.this.TAG, "rvf message : " + message);
                        Bundle args = new Bundle();
                        if (message.contains("HTTP_UNAUTHORIZED")) {
                            args.putString(Const.MsgKey.DIALOG_MESSAGE_KEY, PullService.this.getString(R.string.rejected_connection));
                        } else if (message.contains("HTTP_UNAVAILABLE")) {
                            args.putString(Const.MsgKey.DIALOG_MESSAGE_KEY, PullService.this.getString(R.string.already_connected));
                        }
                        PullService.this.showDialog(Const.MsgBoxId.MSGBOX_CONNECTION_FAIL, args);
                        break;
                    case 200:
                        PullService.this.showDialog(3000);
                        PullService.this.handler.sendEmptyMessageDelayed(Const.MsgID.TIMER_ON_DISPLAY_EXIT, 500L);
                        break;
                    case Const.MsgID.TIMER_ON_DISPLAY_EXIT /* 201 */:
                        PullService.this.dismissDialog(3000);
                        PullService.this.onUnsubscribe();
                        break;
                    case Const.MsgID.TIMER_ON_EXIT_BY_NETWORK_ERROR /* 202 */:
                        PullService.this.onClose();
                        break;
                }
            }
        };
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService("wifi");
        this.connectedSsid = CMInfo.getInstance().getConnectedSSID();
        this.upnpController = UPNPController.getInstance();
        this.upnpController.setDeviceNotifyEventHandler(this.handler);
        this.mDeviceController = this.upnpController.getDeviceController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getConnectedSSID() {
        Trace.d(this.TAG, "start getConnectedSSID()");
        return this.connectedSsid;
    }

    protected void onUnsubscribe() {
        Trace.d(this.TAG, "start onUnsubscribe()");
        this.upnpController.setDeviceNotifyEventHandler(null);
        new UnsubscribeTask(this, null).execute(this.upnpController.getConnectedDevice());
    }

    protected void onClose() {
        Trace.d(this.TAG, "start onClose()");
        this.upnpController.disconnect();
        closeService();
        if (this.bCloseByFinishSafe) {
            CMService.getInstance().beforefinish(2);
            CMService.getInstance().finishSafe();
        }
    }

    private class UnsubscribeTask extends AsyncTask<Device, Void, Void> {
        private UnsubscribeTask() {
        }

        /* synthetic */ UnsubscribeTask(PullService pullService, UnsubscribeTask unsubscribeTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Device... params) {
            Trace.d(PullService.this.TAG, "start UnsubscribeTask.doInBackground() isConnected : " + PullService.this.upnpController.isConnected());
            if (PullService.this.prefixAgent.equals("SEC_RVF_") || PullService.this.upnpController == null || !PullService.this.upnpController.isConnected()) {
                PullService.this.handler.removeMessages(200);
                return null;
            }
            PullService.this.upnpController.unsubscribe(params[0]);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            Trace.d(PullService.this.TAG, "start UnsubscribeTask.onPostExecute()");
            PullService.this.onClose();
            super.onPostExecute(result);
        }
    }
}

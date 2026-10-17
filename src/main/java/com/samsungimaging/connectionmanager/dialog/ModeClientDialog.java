package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.modemanager.CMModeManager;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class ModeClientDialog extends CustomDialog {
    private String mConnected_SSID;
    private IModeClient mIModeClient;
    private boolean mOldCameraDesc;
    private DialogInterface.OnClickListener mOnClickListener;
    private WifiManager mWifiManager;

    public ModeClientDialog(Context context) {
        super(context);
        this.mWifiManager = null;
        this.mConnected_SSID = null;
        this.mOldCameraDesc = false;
        this.mIModeClient = new IModeClient() { // from class: com.samsungimaging.connectionmanager.dialog.ModeClientDialog.1
            @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
            public void runSubApplication(int msg) {
                Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, IModeClient, runSubApplication() => " + msg);
                switch (msg) {
                    case 0:
                        ModeClientDialog.this.beforeFinish();
                        ModeClientDialog.this.dismiss();
                        CMUtil.sendBroadCastToMain(ModeClientDialog.this.getContext(), CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_MODECLIENT_NOTRESPONSE_OR_CANCELED);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(ModeClientDialog.this.mConnected_SSID);
                        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, IModeClient, ONLY old Camera, service execution supported by ModeClient. is OldCamera? = " + isOldCamera);
                        if (isOldCamera) {
                            CMService.getInstance().startSubApplication(msg);
                        } else {
                            CMService.IS_MODE_CONNECTED = true;
                        }
                        break;
                    case 5:
                        CMService.getInstance().startSubApplication(msg);
                        break;
                    case 21:
                        ModeClientDialog.this.beforeFinish();
                        ModeClientDialog.this.dismiss();
                        CMUtil.sendBroadCastToMain(ModeClientDialog.this.getContext(), CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR);
                        break;
                    case 22:
                        ModeClientDialog.this.beforeFinish();
                        ModeClientDialog.this.dismiss();
                        CMUtil.sendBroadCastToMain(ModeClientDialog.this.getContext(), CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR);
                        break;
                }
            }

            @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
            public void runByebye() {
            }
        };
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        super.onStart();
        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, onStart");
        this.mConnected_SSID = this.mWifiManager.getConnectionInfo().getSSID();
        if (!"107".equals(CMUtil.checkCClass(this.mWifiManager)) || this.mConnected_SSID.contains("WB350")) {
            this.mOldCameraDesc = true;
        }
        if (this.mOldCameraDesc) {
            if (CMInfo.getInstance().getIsNFCLaunch()) {
                TextView description = (TextView) findViewById(R.id.tv_cm_waitconnect_desc);
                description.setText(R.string.cm_waitconnect_desc02);
            } else {
                TextView description2 = (TextView) findViewById(R.id.tv_cm_waitconnect_desc);
                description2.setText(R.string.cm_waitconnect_desc);
            }
        } else {
            TextView description3 = (TextView) findViewById(R.id.tv_cm_waitconnect_desc);
            description3.setText(R.string.cm_waitconnect_desc);
        }
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_STOP_WIFI_SCAN_TIMER_FOR_MIMUTES);
        CMModeManager.runModeClient(this.mIModeClient, false);
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, onStop");
        CMModeManager.stopModeClient();
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, onCreate");
        this.mWifiManager = (WifiManager) getContext().getSystemService("wifi");
        initContent();
        super.onCreate(savedInstanceState);
        TextView tvPhoneMacAddress = (TextView) findViewById(R.id.tv_cm_waitconnect_phone_mac);
        tvPhoneMacAddress.setText(((Object) tvPhoneMacAddress.getText()) + CMUtil.getUseragent(getContext()));
        if (CMUtil.isTestMode(getContext())) {
            LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
            buttonLayout.setVisibility(4);
        }
    }

    private void initContent() {
        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, initContent");
        setView(R.layout.dialog_modeclient);
        this.mOnClickListener = new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.ModeClientDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                ModeClientDialog.this.beforeFinish();
                ModeClientDialog.this.dismiss();
            }
        };
        setNeutralButton(R.string.close, this.mOnClickListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beforeFinish() {
        Trace.d(CMConstants.TAG_NAME, "ModeClientDialog, beforeFinish");
        CMModeManager.stopModeClient();
        CMUtil.disableConnectedCamera(this.mWifiManager);
        CMService.IS_WIFI_CONNECTED = false;
        CMService.IS_MODE_CONNECTED = false;
        CMService.getInstance().NFCConnectionTryCancel();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        beforeFinish();
        dismiss();
        super.onBackPressed();
    }
}

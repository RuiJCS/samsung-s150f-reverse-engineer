package com.samsungimaging.connectionmanager.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.connector.CMCameraAPConnector;
import com.samsungimaging.connectionmanager.app.cm.connector.LastRequestedCameraInfo;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.Const;

/* JADX INFO: loaded from: classes.dex */
public class PasswordDialog extends CustomDialog {
    private Activity mActivity;
    private TextView mDesc;
    private EditText mInputSpace;
    private CheckBox mPinDisplayCheckBox;
    private TextView mSSID;
    private TextView mSecurity;
    private CharSequence[] mSecurityType;

    public PasswordDialog(Activity activity) {
        super(activity);
        this.mSecurityType = new String[3];
        this.mPinDisplayCheckBox = null;
        this.mActivity = activity;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
        initCustomContent();
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        super.onStart();
        getWindow().clearFlags(131080);
        this.mDesc.setText(getContext().getResources().getString(R.string.pin));
        this.mInputSpace.setInputType(Const.MsgId.ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE);
        setTitle(this.mSecurityType[0]);
        this.mSSID.setVisibility(8);
        this.mSecurity.setVisibility(8);
        this.mDesc.setVisibility(8);
        this.mPinDisplayCheckBox.setChecked(false);
        if (this.mSecurityType[0].length() > 0) {
            this.mSSID.setText(this.mSecurityType[0]);
        }
        if (this.mSecurityType[2].length() > 0) {
            this.mSecurity.setText(this.mSecurityType[2]);
        }
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        this.mInputSpace.setText("");
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG);
    }

    private void initContent() {
        setView(R.layout.dialog_password);
        setTitle(getContext().getResources().getString(R.string.pin));
        setPositiveButton(R.string.close, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.PasswordDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                LastRequestedCameraInfo.getInstance().setLastRequestedCameraSSID(null);
                PasswordDialog.this.dismiss();
            }
        });
        setNeutralButton(R.string.cm_confirm, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.PasswordDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String input = PasswordDialog.this.mInputSpace.getText().toString();
                if (PasswordDialog.this.mInputSpace.length() > 0) {
                    CharSequence[] connection_info = {PasswordDialog.this.mSecurityType[0], PasswordDialog.this.mSecurityType[1], PasswordDialog.this.mSecurityType[2], input};
                    LastRequestedCameraInfo.getInstance().setLastRequestedCameraSSID(connection_info[0].toString());
                    CMCameraAPConnector.getInstance().setSr(connection_info, PasswordDialog.this.getContext(), CMService.mWifiManager);
                } else {
                    LastRequestedCameraInfo.getInstance().setLastRequestedCameraSSID(null);
                }
                PasswordDialog.this.dismiss();
            }
        });
    }

    private void initCustomContent() {
        this.mInputSpace = (EditText) this.mView.findViewById(R.id.input_password);
        this.mSSID = (TextView) this.mView.findViewById(R.id.security_ssid);
        this.mSecurity = (TextView) this.mView.findViewById(R.id.security_type);
        this.mDesc = (TextView) this.mView.findViewById(R.id.security_desc);
        this.mPinDisplayCheckBox = (CheckBox) findViewById(R.id.check_box);
        this.mPinDisplayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.connectionmanager.dialog.PasswordDialog.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PasswordDialog.this.mInputSpace.setInputType(1);
                } else {
                    PasswordDialog.this.mInputSpace.setInputType(Const.MsgId.ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE);
                }
                PasswordDialog.this.mInputSpace.setSelection(PasswordDialog.this.mInputSpace.length());
            }
        });
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog
    public void setTag(Object tag) {
        this.mSecurityType = (CharSequence[]) tag;
    }
}

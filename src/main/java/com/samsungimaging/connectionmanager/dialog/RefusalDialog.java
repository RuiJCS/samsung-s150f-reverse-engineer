package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;

/* JADX INFO: loaded from: classes.dex */
public class RefusalDialog extends CustomDialog {
    public RefusalDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        TextView camera_ssid = (TextView) findViewById(R.id.tv_cm_refusal_camera_ssid);
        String connected_ssid = CMInfo.getInstance().getConnectedSSID();
        camera_ssid.setText(connected_ssid);
        TextView description = (TextView) findViewById(R.id.tv_cm_refusal_desc);
        String msg = (String) getTag();
        if (msg.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR)) {
            description.setText(R.string.rejected_connection);
        } else if (msg.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR)) {
            description.setText(R.string.already_connected);
        }
        super.onStart();
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setView(R.layout.dialog_refusal);
    }
}

package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionWaitDialog extends CustomDialog {
    public ConnectionWaitDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
        if (CMUtil.isTestMode(getContext())) {
            LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
            buttonLayout.setVisibility(4);
        }
    }

    private void initContent() {
        setView(R.layout.dialog_modeclient);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        TextView description = (TextView) findViewById(R.id.tv_cm_waitconnect_desc);
        description.setText(R.string.cm_waitconnect_desc);
        TextView tvPhoneMacAddress = (TextView) findViewById(R.id.tv_cm_waitconnect_phone_mac);
        tvPhoneMacAddress.setText(((Object) tvPhoneMacAddress.getText()) + CMUtil.getUseragent(getContext()));
    }
}

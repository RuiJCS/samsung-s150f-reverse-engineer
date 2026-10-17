package com.samsungimaging.connectionmanager.app.pullservice.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.pullservice.demo.util.CommonUtils;

/* JADX INFO: loaded from: classes.dex */
public class CMWaitConnectDialog extends Dialog {
    public CMWaitConnectDialog(Context context) {
        super(context);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.ml_dialog_wait_connect);
        TextView tvExplanation = (TextView) findViewById(R.id.tv_cm_wait_dialog_text);
        if (CMInfo.getInstance().getIsNFCLaunch()) {
            tvExplanation.setText(R.string.cm_waitconnect_desc02);
        } else {
            tvExplanation.setText(R.string.cm_waitconnect_desc);
        }
        TextView tvPhoneMacAddress = (TextView) findViewById(R.id.tv_cm_waitconnect_phone_mac);
        tvPhoneMacAddress.setText(((Object) tvPhoneMacAddress.getText()) + CommonUtils.getUseragent(getContext()));
    }
}

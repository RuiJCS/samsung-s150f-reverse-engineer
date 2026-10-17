package com.samsungimaging.connectionmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;

/* JADX INFO: loaded from: classes.dex */
public class CMTestModePassWordDialog extends Dialog {
    private Activity mActivity;
    Context mContext;
    EditText met_password;

    public CMTestModePassWordDialog(Context context, Activity activity) {
        super(context);
        this.mContext = null;
        this.mActivity = null;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.cm_dialog_testmode_password);
        this.met_password = (EditText) findViewById(R.id.et_cm_testmode_password);
        Button btnConfirm = (Button) findViewById(R.id.btn_cm_testmode_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModePassWordDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String password_temp = CMTestModePassWordDialog.this.met_password.getText().toString();
                if ("true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(CMTestModePassWordDialog.this.mContext, CMConstants.TESTMODE, "false")) && password_temp.equals("6440")) {
                    Toast.makeText(CMTestModePassWordDialog.this.mContext, "TEST MODEë¡œ ì§„ìž…í•©ë‹ˆë‹¤.", 0).show();
                    CMTestModePassWordDialog.this.met_password.setText("");
                    CMTestModePassWordDialog.this.mActivity.showDialog(1011);
                } else {
                    Toast.makeText(CMTestModePassWordDialog.this.mContext, "ìž…ë ¥ë�œ Passwordê°€ í‹€ë¦½ë‹ˆë‹¤.", 0).show();
                    CMTestModePassWordDialog.this.met_password.setText("");
                }
                CMTestModePassWordDialog.this.dismiss();
            }
        });
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        this.met_password.setText("");
    }
}

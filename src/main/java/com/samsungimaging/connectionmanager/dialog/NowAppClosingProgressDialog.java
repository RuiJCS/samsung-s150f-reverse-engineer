package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class NowAppClosingProgressDialog extends CustomProgressDialog {
    public NowAppClosingProgressDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomProgressDialog, com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setCancelable(false);
        setMessage(R.string.ml_appclose);
    }
}

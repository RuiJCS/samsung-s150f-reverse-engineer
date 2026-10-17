package com.samsungimaging.connectionmanager.dialog;

import android.R;
import android.content.Context;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class IntroDialog extends CustomDialog {
    public IntroDialog(Context context) {
        super(context, R.style.Theme.Black.NoTitleBar.Fullscreen);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
    }

    private void initContent() {
        setContentView(com.samsungimaging.connectionmanager.R.layout.dialog_intro);
    }
}

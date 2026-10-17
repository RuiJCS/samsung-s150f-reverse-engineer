package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class HelpDialog extends CustomDialog {
    public HelpDialog(Context context) {
        super(context, R.style.Theme_Default_Alert_Fullscreen);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setTitle(R.string.cm_main_help);
        setView(R.layout.dialog_help);
        setNeutralButton(R.string.close, (DialogInterface.OnClickListener) null);
    }
}

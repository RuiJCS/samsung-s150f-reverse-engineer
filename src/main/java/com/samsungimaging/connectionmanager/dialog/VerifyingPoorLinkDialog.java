package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class VerifyingPoorLinkDialog extends CustomDialog {
    public VerifyingPoorLinkDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setView(R.layout.dialog_verifying_poor_link);
        setNeutralButton(R.string.cm_confirm, (DialogInterface.OnClickListener) null);
    }
}

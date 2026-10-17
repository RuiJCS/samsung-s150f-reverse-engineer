package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;

/* JADX INFO: loaded from: classes.dex */
public class InitHelpDialog extends HelpDialog {
    public InitHelpDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.HelpDialog, com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG);
    }
}

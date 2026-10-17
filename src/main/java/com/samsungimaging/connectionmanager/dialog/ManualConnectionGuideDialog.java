package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class ManualConnectionGuideDialog extends CustomDialog implements DialogInterface.OnClickListener {
    public ManualConnectionGuideDialog(Context context) {
        super(context, R.style.Theme_Default_Alert_Fullscreen);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setTitle(R.string.cm_main_guide);
        setMessage(R.string.cm_manualconnect_desc);
        setPositiveButton(R.string.close, (DialogInterface.OnClickListener) null);
        setNegativeButton(R.string.cm_nativewifi_setting, this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
        getContext().startActivity(intent);
    }
}

package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class NotSupportedDialog extends CustomDialog {
    public NotSupportedDialog(Context context) {
        super(context);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
        initCustomContent();
    }

    private void initContent() {
        setView(R.layout.dialog_not_supported);
        setNeutralButton(R.string.close, (DialogInterface.OnClickListener) null);
    }

    private void initCustomContent() {
        LinearLayout btnLinkML = (LinearLayout) findViewById(R.id.mobilelink_link);
        btnLinkML.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.NotSupportedDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=com.samsungimaging.filesharing");
                Intent i = new Intent("android.intent.action.VIEW", uri);
                NotSupportedDialog.this.getContext().startActivity(i);
            }
        });
        LinearLayout btnLinkRVF = (LinearLayout) findViewById(R.id.rvf_link);
        btnLinkRVF.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.NotSupportedDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=com.samsung.app");
                Intent i = new Intent("android.intent.action.VIEW", uri);
                NotSupportedDialog.this.getContext().startActivity(i);
            }
        });
    }
}

package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;

/* JADX INFO: loaded from: classes.dex */
public class PairedCameraRemoveDialog extends CustomDialog implements DialogInterface.OnClickListener {
    private DatabaseAP mAp;

    public PairedCameraRemoveDialog(Context context) {
        super(context);
        this.mAp = null;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        TextView title = (TextView) this.mView.findViewById(R.id.title);
        title.setText(R.string.cm_remove_pairedap_ssid);
        title.append("\n" + this.mAp.getSSID());
        super.onStart();
    }

    private void initContent() {
        setView(R.layout.dialog_paired_camera_remove);
        setPositiveButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
        setNegativeButton(R.string.delete, this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        DatabaseManager.deleteForAP(getContext(), this.mAp.getUri());
        dismiss();
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog
    public void setTag(Object tag) {
        this.mAp = (DatabaseAP) tag;
    }
}

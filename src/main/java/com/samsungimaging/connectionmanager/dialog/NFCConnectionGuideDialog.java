package com.samsungimaging.connectionmanager.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;

/* JADX INFO: loaded from: classes.dex */
public class NFCConnectionGuideDialog extends CustomDialog implements CompoundButton.OnCheckedChangeListener {
    private BaseGalleryActivity mActivity;
    private AnimationDrawable mAnimation;
    private boolean mIsChecked;

    public NFCConnectionGuideDialog(BaseGalleryActivity activity) {
        super(activity, R.style.Theme_Default_Alert_Fullscreen);
        this.mActivity = null;
        this.mAnimation = null;
        this.mIsChecked = false;
        this.mActivity = activity;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setTitle(R.string.cm_main_nfc);
        setView(R.layout.dialog_nfc_connection_guide);
        setNeutralButton(R.string.close, (DialogInterface.OnClickListener) null);
        setOnDismissListener(this);
        ImageView imageView = (ImageView) this.mView.findViewById(R.id.animation_view);
        this.mAnimation = (AnimationDrawable) imageView.getBackground();
        CheckBox checkBox = (CheckBox) this.mView.findViewById(R.id.check_box);
        checkBox.setChecked(this.mIsChecked);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.Dialog
    protected void onStart() {
        this.mAnimation.start();
        CheckBox checkBox = (CheckBox) this.mView.findViewById(R.id.check_box);
        TextView textView = (TextView) this.mView.findViewById(R.id.text_view);
        if (((Boolean) getTag()).booleanValue()) {
            checkBox.setVisibility(0);
            textView.setVisibility(0);
        } else {
            checkBox.setVisibility(8);
            textView.setVisibility(8);
        }
        super.onStart();
    }

    @Override // android.app.Dialog
    protected void onStop() {
        this.mAnimation.stop();
        super.onStop();
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog
    public void onOrientationChanged() {
        initContent();
        initialize();
        onStart();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.playSoundEffect(0);
        this.mIsChecked = isChecked;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        if (((Boolean) getTag()).booleanValue()) {
            this.mActivity.getSettings().setNFCConnectionGuide(this.mIsChecked);
            CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG);
        }
    }
}

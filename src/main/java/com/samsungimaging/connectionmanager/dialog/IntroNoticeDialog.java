package com.samsungimaging.connectionmanager.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseActivity;

/* JADX INFO: loaded from: classes.dex */
public class IntroNoticeDialog extends CustomDialog implements CompoundButton.OnCheckedChangeListener {
    private BaseActivity mActivity;
    private boolean mIsChecked;

    public IntroNoticeDialog(BaseActivity activity) {
        super(activity);
        this.mActivity = null;
        this.mIsChecked = false;
        this.mActivity = activity;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        setTitle(R.string.ml_title_notice);
        setMessage(R.string.intro_guide_message);
        setView(R.layout.dialog_intro_guide);
        CheckBox checkBox = (CheckBox) this.mView.findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.playSoundEffect(0);
        this.mIsChecked = isChecked;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this.mActivity.getSettings().setIntroNoticeGuide(this.mIsChecked);
    }
}

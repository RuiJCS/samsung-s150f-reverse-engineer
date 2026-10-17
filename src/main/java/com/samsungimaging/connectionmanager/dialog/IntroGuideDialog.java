package com.samsungimaging.connectionmanager.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseActivity;
import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare;
import com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush;

/* JADX INFO: loaded from: classes.dex */
public class IntroGuideDialog extends CustomDialog implements CompoundButton.OnCheckedChangeListener {
    private BaseActivity mActivity;
    private boolean mIsChecked;

    public IntroGuideDialog(BaseActivity activity) {
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
        if (this.mActivity instanceof AutoShare) {
            setTitle(R.string.autoshare);
            setMessage(R.string.ssc_s2l_notice);
        } else if (this.mActivity instanceof SelectivePush) {
            setTitle(R.string.mobile_link);
            setMessage(R.string.ssc_sp_notice);
        }
        setView(R.layout.dialog_intro_guide);
        setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.IntroGuideDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(IntroGuideDialog.this.getContext(), R.string.home_screen_continue_receive_files, 0).show();
            }
        });
        setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.dialog.IntroGuideDialog.2
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case 4:
                        dialog.dismiss();
                        Toast.makeText(IntroGuideDialog.this.getContext(), R.string.home_screen_continue_receive_files, 0).show();
                        return false;
                    case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                        return true;
                    default:
                        return false;
                }
            }
        });
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
        this.mActivity.getSettings().setIntroGuide(this.mIsChecked);
    }
}

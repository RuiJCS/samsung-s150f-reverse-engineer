package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class InitGuideDialog extends CustomDialog {
    private Button mHelpButton;
    private CheckBox mInitCheckBox;
    private DialogInterface.OnClickListener mOnClickListener;

    public InitGuideDialog(Context context) {
        super(context);
        this.mInitCheckBox = null;
        this.mHelpButton = null;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        Trace.d(CMConstants.TAG_NAME, "InitGuideDialog, onCreate");
        initContent();
        super.onCreate(savedInstanceState);
        initCustomView();
    }

    private void initContent() {
        Trace.d(CMConstants.TAG_NAME, "InitGuideDialog, initContent");
        setView(R.layout.dialog_init_guide);
        setNeutralButton(R.string.cm_confirm, (DialogInterface.OnClickListener) null);
    }

    private void initCustomView() {
        this.mHelpButton = (Button) findViewById(R.id.btn_cm_initguide_help);
        this.mHelpButton.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.InitGuideDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CMUtil.sendBroadCastToMain(InitGuideDialog.this.getContext(), CMConstants.EXTRA_VALUE_SHOW_HELPDIALOG);
                InitGuideDialog.this.dismiss();
            }
        });
        this.mInitCheckBox = (CheckBox) findViewById(R.id.check_box);
        this.mInitCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.connectionmanager.dialog.InitGuideDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.playSoundEffect(0);
                if (isChecked) {
                    CMSharedPreferenceUtil.put(InitGuideDialog.this.getContext(), CMConstants.SP_KEY_SHOW_INIT_GUIDE_DAILOG, "false");
                } else {
                    CMSharedPreferenceUtil.put(InitGuideDialog.this.getContext(), CMConstants.SP_KEY_SHOW_INIT_GUIDE_DAILOG, "true");
                }
            }
        });
        this.mOnClickListener = new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.InitGuideDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                CMUtil.sendBroadCastToMain(InitGuideDialog.this.getContext(), CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG);
                InitGuideDialog.this.dismiss();
            }
        };
        setNeutralButton(R.string.cm_confirm, this.mOnClickListener);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        CMUtil.sendBroadCastToMain(getContext(), CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG);
        super.onBackPressed();
    }
}

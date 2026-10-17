package com.samsungimaging.connectionmanager.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseArray;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.util.HandlerMessage;
import com.samsungimaging.connectionmanager.util.Settings;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;

/* JADX INFO: loaded from: classes.dex */
public class BaseActivity extends Activity {
    private static final int BACK_PRESSED_TIME_DELAY = 2000;
    private static final int HANDLER_STATUS_BAR_HIDE = 0;
    private static final int STATUS_BAR_HIDE_DELAY = 2000;
    protected Settings mSettings = null;
    protected ActionBar mActionBar = null;
    protected SparseArray<CustomDialog> mDialogList = new SparseArray<>();
    private boolean mStatusBarVisible = false;
    private boolean mIsBackPressed = false;
    private long mBackPressedTimeMillis = 0;
    private HandlerMessage mHandler = new HandlerMessage(this) { // from class: com.samsungimaging.connectionmanager.app.BaseActivity.1
        @Override // com.samsungimaging.connectionmanager.util.HandlerMessage
        public void onHandleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    BaseActivity.this.hideStatusBar();
                    break;
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Default);
        super.onCreate(savedInstanceState);
        this.mSettings = new Settings(this);
        this.mActionBar = getActionBar();
        this.mActionBar.hide();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.mActionBar != null) {
            this.mActionBar = null;
        }
        if (this.mDialogList != null) {
            this.mDialogList.clear();
            this.mDialogList = null;
        }
        Utils.unbindView(getWindow().getDecorView());
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        if (this.mDialogList != null && dialog != null && (dialog instanceof CustomDialog)) {
            this.mDialogList.put(id, (CustomDialog) dialog);
            super.onPrepareDialog(id, dialog, bundle);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Trace.d(Trace.Tag.COMMON, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        if (this.mDialogList != null) {
            for (int i = 0; i < this.mDialogList.size(); i++) {
                int key = this.mDialogList.keyAt(i);
                this.mDialogList.get(key).onOrientationChanged();
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideStatusBar();
        } else {
            removeMessage(0);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public void showStatusBar() {
        if (!this.mStatusBarVisible) {
            getWindow().addFlags(2048);
            this.mStatusBarVisible = true;
        }
        if (this.mHandler != null) {
            removeMessage(0);
            this.mHandler.sendEmptyMessageDelayed(0, 2000L);
        }
    }

    public void hideStatusBar() {
        if (this.mStatusBarVisible) {
            getWindow().clearFlags(2048);
            this.mStatusBarVisible = false;
        }
        removeMessage(0);
    }

    protected void removeMessage(int what) {
        if (this.mHandler != null && this.mHandler.hasMessages(what)) {
            this.mHandler.removeMessages(what);
        }
    }

    public Settings getSettings() {
        return this.mSettings;
    }

    protected boolean isBackPressAvailable() {
        if (SystemClock.elapsedRealtime() - this.mBackPressedTimeMillis > 2000) {
            this.mBackPressedTimeMillis = SystemClock.elapsedRealtime();
            Toast.makeText(this, R.string.back_button_again_exit, 0).show();
            return false;
        }
        if (isFinishing() || this.mIsBackPressed) {
            return false;
        }
        this.mIsBackPressed = true;
        return true;
    }
}

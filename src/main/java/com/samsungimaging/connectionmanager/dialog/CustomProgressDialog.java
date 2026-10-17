package com.samsungimaging.connectionmanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import java.text.NumberFormat;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CustomProgressDialog extends CustomDialog {
    public static final int STYLE_HORIZONTAL = 1;
    public static final int STYLE_SPINNER = 0;
    private int mMax;
    private CharSequence mMessage;
    private OnProcessNumberListener mOnProcessNumberListener;
    private OnProcessPercentListener mOnProcessPercentListener;
    private ProgressBar mProgressBar;
    private int mProgressStyle;
    private int mProgressVal;
    private Handler mViewUpdateHandler;

    public interface OnProcessNumberListener {
        String onProcessNumber(CustomProgressDialog customProgressDialog);
    }

    public interface OnProcessPercentListener {
        String onProcessPercent(CustomProgressDialog customProgressDialog);
    }

    public CustomProgressDialog(Context context) {
        super(context);
        this.mProgressStyle = 0;
        this.mMessage = null;
        this.mProgressBar = null;
        this.mProgressVal = 0;
        this.mMax = 0;
        this.mOnProcessPercentListener = null;
        this.mOnProcessNumberListener = null;
        this.mViewUpdateHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.dialog.CustomProgressDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                TextView processPercent = (TextView) CustomProgressDialog.this.mView.findViewById(R.id.process_percent);
                TextView processNumber = (TextView) CustomProgressDialog.this.mView.findViewById(R.id.process_number);
                if (CustomProgressDialog.this.mOnProcessPercentListener != null) {
                    processPercent.setText(CustomProgressDialog.this.mOnProcessPercentListener.onProcessPercent(CustomProgressDialog.this));
                } else {
                    NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.ENGLISH);
                    processPercent.setText(percentFormat.format(((double) CustomProgressDialog.this.mProgressVal) / ((double) CustomProgressDialog.this.mMax)));
                }
                if (CustomProgressDialog.this.mOnProcessNumberListener != null) {
                    processNumber.setText(CustomProgressDialog.this.mOnProcessNumberListener.onProcessNumber(CustomProgressDialog.this));
                } else {
                    processNumber.setText(String.format("%,d/%,d", Integer.valueOf(CustomProgressDialog.this.mProgressVal), Integer.valueOf(CustomProgressDialog.this.mMax)));
                }
            }
        };
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        LinearLayout styleLayout;
        setView(R.layout.dialog_custom_progress);
        if (this.mProgressStyle == 0) {
            styleLayout = (LinearLayout) this.mView.findViewById(R.id.style_spenner_layout);
            TextView messageView = (TextView) styleLayout.findViewById(R.id.message);
            messageView.setText(this.mMessage);
            this.mProgressBar = (ProgressBar) styleLayout.findViewById(R.id.progressbar);
        } else {
            styleLayout = (LinearLayout) this.mView.findViewById(R.id.style_horizontal_layout);
            super.mMessage = this.mMessage;
            this.mProgressBar = (ProgressBar) styleLayout.findViewById(R.id.progressbar);
        }
        if (this.mMax > 0) {
            setMax(this.mMax);
        }
        if (this.mProgressVal > 0) {
            setProgress(this.mProgressVal);
        }
        styleLayout.setVisibility(0);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog
    public void setMessage(int messageId) {
        this.mMessage = getContext().getText(messageId);
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog
    public void setMessage(CharSequence message) {
        this.mMessage = message;
    }

    public void setProgressStyle(int style) {
        this.mProgressStyle = style;
    }

    public void setProgress(int value) {
        this.mProgressVal = value;
        if (this.mProgressBar != null) {
            this.mProgressBar.setProgress(value);
            onProgressChanged();
        }
    }

    public int getProgress() {
        return this.mProgressVal;
    }

    public void setMax(int max) {
        this.mMax = max;
        if (this.mProgressBar != null) {
            this.mProgressBar.setMax(max);
            onProgressChanged();
        }
    }

    public int getMax() {
        return this.mMax;
    }

    private void onProgressChanged() {
        if (this.mProgressStyle == 1 && this.mViewUpdateHandler != null && !this.mViewUpdateHandler.hasMessages(0)) {
            this.mViewUpdateHandler.sendEmptyMessage(0);
        }
    }

    public void setOnProcessPercentListener(OnProcessPercentListener l) {
        this.mOnProcessPercentListener = l;
    }

    public void setOnProcessNumberListener(OnProcessNumberListener l) {
        this.mOnProcessNumberListener = l;
    }
}

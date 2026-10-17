package com.samsungimaging.connectionmanager.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class HandlerMessage extends Handler {
    private final WeakReference<Activity> mActivity;

    public abstract void onHandleMessage(Message message);

    public HandlerMessage(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Activity activity = this.mActivity.get();
        if (activity != null) {
            onHandleMessage(msg);
        }
    }
}

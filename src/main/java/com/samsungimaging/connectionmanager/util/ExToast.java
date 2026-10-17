package com.samsungimaging.connectionmanager.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.Const;
import java.util.HashMap;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class ExToast {
    private static final int MSG_TOAST_TIMEOUT = 1;
    private static ExToast mExToast = new ExToast();
    private HashMap<String, MyToast> mToastMap = new HashMap<>();
    private LinkedList<String> mQueue = new LinkedList<>();
    private Handler mToastHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.util.ExToast.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    synchronized (ExToast.this.mToastMap) {
                        String sKey = (String) msg.obj;
                        ExToast.this.hide(Integer.parseInt(sKey));
                        ExToast.this.mQueue.remove(sKey);
                        if (!ExToast.this.mQueue.isEmpty()) {
                            String newKey = (String) ExToast.this.mQueue.getFirst();
                            ExToast.this.doShow(newKey);
                        }
                        break;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    private ExToast() {
    }

    public static synchronized ExToast getInstance() {
        return mExToast;
    }

    public synchronized void register(Context context, int toastId, int resId, int term) {
        MyToast myToast = new MyToast(this, null);
        myToast.mToast = makeToast(context, makeView(context, 12, resId), term);
        myToast.mTerm = term;
        myToast.mRunning = false;
        this.mToastMap.put(new StringBuilder().append(toastId).toString(), myToast);
    }

    public synchronized void unregister(int toastKey) {
        hide(toastKey);
        this.mToastMap.remove(new StringBuilder().append(toastKey).toString());
    }

    public synchronized void unregisterAll() {
        hideAll();
        this.mToastMap.clear();
    }

    public synchronized void show(int toastKey) {
        MyToast myToast = this.mToastMap.get(new StringBuilder().append(toastKey).toString());
        if (myToast != null) {
            boolean isReady = false;
            for (String sKey : this.mQueue) {
                if (sKey.equalsIgnoreCase(new StringBuilder().append(toastKey).toString())) {
                    isReady = true;
                }
            }
            if (!myToast.mRunning && !isReady) {
                this.mQueue.add(new StringBuilder().append(toastKey).toString());
                if (this.mQueue.size() == 1) {
                    doShow(new StringBuilder().append(toastKey).toString());
                }
            }
        }
    }

    public synchronized void hide(int toastKey) {
        this.mQueue.remove(new StringBuilder().append(toastKey).toString());
        MyToast myToast = this.mToastMap.get(new StringBuilder().append(toastKey).toString());
        if (myToast != null && myToast.mToast != null) {
            myToast.mToast.cancel();
            myToast.mRunning = false;
        }
    }

    public synchronized void hideAll() {
        MyToast myToast;
        this.mQueue.clear();
        for (String key : this.mToastMap.keySet()) {
            if (key != null && (myToast = this.mToastMap.get(key)) != null && myToast.mToast != null) {
                myToast.mToast.cancel();
                myToast.mRunning = false;
            }
        }
    }

    public synchronized boolean isShowing(int key) {
        boolean bRet;
        bRet = false;
        MyToast myToast = this.mToastMap.get(new StringBuilder().append(key).toString());
        if (myToast != null && myToast.mRunning) {
            bRet = true;
        }
        return bRet;
    }

    public synchronized boolean isShowing() {
        boolean bRet;
        MyToast myToast;
        bRet = false;
        for (String key : this.mToastMap.keySet()) {
            if (key != null && (myToast = this.mToastMap.get(key)) != null && myToast.mRunning) {
                bRet = true;
            }
        }
        return bRet;
    }

    private View makeView(Context context, int size, int resId) {
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setBackgroundResource(R.drawable.ssc_toast_bg);
        textView.setText(Html.fromHtml("<b>" + context.getString(resId) + "</b>"));
        textView.setTextSize(size);
        return textView;
    }

    private Toast makeToast(Context context, View view, int term) {
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(term);
        return toast;
    }

    private void setTimeout(int term, String sKey) {
        int delayMillis;
        if (term == 0) {
            delayMillis = 2500;
        } else {
            delayMillis = Const.Config.NEXUSTIME;
        }
        Message msg = new Message();
        msg.what = 1;
        msg.obj = sKey;
        this.mToastHandler.sendMessageDelayed(msg, delayMillis);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow(String sKey) {
        MyToast myToast = this.mToastMap.get(sKey);
        if (myToast != null) {
            myToast.mRunning = true;
            myToast.mToast.show();
            setTimeout(myToast.mTerm, sKey);
        }
    }

    private class MyToast {
        boolean mRunning;
        int mTerm;
        Toast mToast;

        private MyToast() {
        }

        /* synthetic */ MyToast(ExToast exToast, MyToast myToast) {
            this();
        }
    }
}

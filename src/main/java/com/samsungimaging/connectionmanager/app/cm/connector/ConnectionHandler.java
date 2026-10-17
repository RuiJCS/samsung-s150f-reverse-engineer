package com.samsungimaging.connectionmanager.app.cm.connector;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionHandler extends Handler {
    private static ConnectionHandler instance_ConnectionHandler = null;
    private Context mContext;

    private ConnectionHandler(Context context) {
        this.mContext = context;
    }

    public static synchronized ConnectionHandler getInstance(Context context) {
        if (instance_ConnectionHandler == null) {
            instance_ConnectionHandler = new ConnectionHandler(context);
        }
        return instance_ConnectionHandler;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Trace.d(CMConstants.TAG_NAME, "ConnectionHandler, msg : " + msg);
        Trace.d(CMConstants.TAG_NAME, "ConnectionHandler, msg.obj : " + msg.obj);
        switch (msg.what) {
            case 100:
                Trace.d(CMConstants.TAG_NAME, "ConnectionHandler, MSG_NEED_PASSWORD");
                Bundle bundle = (Bundle) msg.obj;
                CMUtil.sendBroadCastToMain(this.mContext, CMConstants.EXTRA_VALUE_NEED_PASSWORD, bundle);
                break;
        }
        super.handleMessage(msg);
    }
}

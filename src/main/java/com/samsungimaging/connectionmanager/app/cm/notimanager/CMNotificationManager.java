package com.samsungimaging.connectionmanager.app.cm.notimanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.Main;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;

/* JADX INFO: loaded from: classes.dex */
public class CMNotificationManager {
    private static CMNotificationManager s_obj = new CMNotificationManager();
    Context mContext = null;
    NotificationManager mNotificationManager = null;
    private boolean mIsConnected = false;
    private String mSsid = null;

    public boolean getCurrentNoti_mIsConnected() {
        return this.mIsConnected;
    }

    public String getCurrentNoti_mSsid() {
        return this.mSsid;
    }

    private CMNotificationManager() {
    }

    public static CMNotificationManager getInstance() {
        return s_obj;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
    }

    public void notifyStatusChange(boolean isConnected, String ssid, String msg, int notiId) {
        this.mIsConnected = isConnected;
        if (this.mContext != null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.mContext);
            BitmapDrawable drawable = (BitmapDrawable) this.mContext.getResources().getDrawable(R.drawable.notification_large_icon);
            Bitmap bitmap = drawable.getBitmap();
            mBuilder.setLargeIcon(bitmap);
            mBuilder.setContentTitle(this.mContext.getResources().getString(R.string.app_name));
            if (isConnected) {
                mBuilder.setSmallIcon(R.drawable.notification_small_icon_connected);
            } else {
                mBuilder.setSmallIcon(R.drawable.notification_small_icon_disconnected);
            }
            mBuilder.setContentText(msg);
            mBuilder.setTicker(msg);
            mBuilder.setOngoing(true);
            Intent activity = new Intent(this.mContext, (Class<?>) Main.class);
            activity.addFlags(872415232);
            PendingIntent contentIntent = PendingIntent.getActivity(this.mContext, notiId, activity, 134217728);
            mBuilder.setContentIntent(contentIntent);
            if (ssid != null) {
                this.mSsid = CMUtil.convertToNOTQuotedString(ssid);
                mBuilder.setSubText(this.mSsid);
            }
            this.mNotificationManager.notify(notiId, mBuilder.build());
        }
    }

    public void cancelNoti(int id) {
        if (this.mNotificationManager != null) {
            this.mNotificationManager.cancel(id);
        }
    }

    public void cancelAllNoti() {
        cancelNoti(R.string.cm_noti_ongoing);
        if (this.mNotificationManager != null) {
            this.mNotificationManager.cancelAll();
        }
    }
}

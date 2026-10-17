package com.samsungimaging.connectionmanager.util;

import android.app.Application;

/* JADX INFO: loaded from: classes.dex */
public class MyLocalGallery extends Application {
    private static boolean activityCreated;

    public static boolean isActivityCreated() {
        return activityCreated;
    }

    public static void activityCreated() {
        activityCreated = true;
    }

    public static void activityDestroyed() {
        activityCreated = false;
    }
}

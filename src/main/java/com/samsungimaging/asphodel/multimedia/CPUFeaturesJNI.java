package com.samsungimaging.asphodel.multimedia;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class CPUFeaturesJNI {
    private static final String LOG_TAG = "[CPUFeaturesJNI]";
    private static int m_argc = 0;
    private static String[] m_argv = null;
    private static int m_IsSupportNEON = 0;

    private static native int cpufeaturesMain(int i, String[] strArr);

    public static int construct() {
        System.loadLibrary("cpufeatures-jni");
        m_IsSupportNEON = cpufeaturesMain(m_argc, m_argv);
        Log.i(LOG_TAG, "Is Support NEON? : " + String.valueOf(m_IsSupportNEON));
        return m_IsSupportNEON;
    }

    public static int destruct() {
        return 0;
    }
}

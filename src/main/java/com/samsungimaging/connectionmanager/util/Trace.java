package com.samsungimaging.connectionmanager.util;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class Trace {
    private static final boolean DEBUG_MODE = true;
    private static final String TAG = "SSCA_";

    public enum Tag {
        COMMON,
        CM,
        AS,
        SP,
        ML,
        RVF,
        FFMPEG,
        CYBERGARAGE;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Tag[] valuesCustom() {
            Tag[] tagArrValuesCustom = values();
            int length = tagArrValuesCustom.length;
            Tag[] tagArr = new Tag[length];
            System.arraycopy(tagArrValuesCustom, 0, tagArr, 0, length);
            return tagArr;
        }
    }

    public static void e(Tag tag, String str) {
        Exception e = new Exception();
        StackTraceElement element = e.getStackTrace()[1];
        Log.e(TAG + tag, String.format("[%s : %s] %s", element.getFileName(), Integer.valueOf(element.getLineNumber()), str));
    }

    public static void w(Tag tag, String str) {
        Exception e = new Exception();
        StackTraceElement element = e.getStackTrace()[1];
        Log.w(TAG + tag, String.format("[%s : %s] %s", element.getFileName(), Integer.valueOf(element.getLineNumber()), str));
    }

    public static void i(Tag tag, String str) {
        Exception e = new Exception();
        StackTraceElement element = e.getStackTrace()[1];
        Log.i(TAG + tag, String.format("[%s : %s] %s", element.getFileName(), Integer.valueOf(element.getLineNumber()), str));
    }

    public static void d(Tag tag, String str) {
        Exception e = new Exception();
        StackTraceElement element = e.getStackTrace()[1];
        Log.d(TAG + tag, String.format("[%s : %s] %s", element.getFileName(), Integer.valueOf(element.getLineNumber()), str));
    }

    public static void v(Tag tag, String str) {
        Exception e = new Exception();
        StackTraceElement element = e.getStackTrace()[1];
        Log.v(TAG + tag, String.format("[%s : %s] %s", element.getFileName(), Integer.valueOf(element.getLineNumber()), str));
    }
}

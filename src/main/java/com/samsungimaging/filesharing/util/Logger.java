package com.samsungimaging.filesharing.util;

import android.util.Log;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class Logger {
    private static boolean mOnOff = true;

    public static void enable(boolean onoff) {
        mOnOff = onoff;
    }

    public static boolean isEnable() {
        return mOnOff;
    }

    public static void e(String tag, String msg, Exception e) {
        if (mOnOff) {
            if (msg == null) {
                msg = "null";
            }
            Log.e(tag, msg, e);
        }
    }

    public static void e(String tag, String msg) {
        if (mOnOff) {
            if (msg == null) {
                msg = "null";
            }
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mOnOff) {
            if (msg == null) {
                msg = "null";
            }
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mOnOff) {
            if (msg == null) {
                msg = "null";
            }
            Log.i(tag, msg);
        }
    }

    public static void GG(String msg) {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        Log.i("GG", "(" + ste.getFileName() + SOAP.DELIM + ste.getLineNumber() + ") " + msg.replaceAll(HTTP.TAB, "    "));
    }
}

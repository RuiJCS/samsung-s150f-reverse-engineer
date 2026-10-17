package com.samsungimaging.connectionmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/* JADX INFO: loaded from: classes.dex */
public class Settings {
    private static final String KEY_INTRO_GUIDE = "pref_intro_guide_";
    private static final String KEY_INTRO_NOTICE_GUIDE = "pref_intro_notice_guide_";
    private static final String KEY_NFC_CONNECTION_GUIDE = "pref_nfc_connection_guide";
    private Context mContext;
    private SharedPreferences mPreferences;

    public Settings(Context context) {
        this.mContext = null;
        this.mPreferences = null;
        this.mContext = context;
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setNFCConnectionGuide(boolean value) {
        boolean oldValue = getNFCConnectionGuide();
        if (oldValue != value) {
            SharedPreferences.Editor editor = this.mPreferences.edit();
            editor.putBoolean(KEY_NFC_CONNECTION_GUIDE, value);
            editor.commit();
        }
    }

    public boolean getNFCConnectionGuide() {
        return this.mPreferences.getBoolean(KEY_NFC_CONNECTION_GUIDE, false);
    }

    public void setIntroGuide(boolean value) {
        boolean oldValue = getIntroGuide();
        if (oldValue != value) {
            SharedPreferences.Editor editor = this.mPreferences.edit();
            editor.putBoolean(KEY_INTRO_GUIDE + this.mContext.toString(), value);
            editor.commit();
        }
    }

    public boolean getIntroGuide() {
        return this.mPreferences.getBoolean(KEY_INTRO_GUIDE + this.mContext.toString(), false);
    }

    public void setIntroNoticeGuide(boolean value) {
        boolean oldValue = getIntroGuide();
        if (oldValue != value) {
            SharedPreferences.Editor editor = this.mPreferences.edit();
            editor.putBoolean(KEY_INTRO_NOTICE_GUIDE, value);
            editor.commit();
        }
    }

    public boolean getIntroNoticeGuide() {
        return this.mPreferences.getBoolean(KEY_INTRO_NOTICE_GUIDE, false);
    }
}

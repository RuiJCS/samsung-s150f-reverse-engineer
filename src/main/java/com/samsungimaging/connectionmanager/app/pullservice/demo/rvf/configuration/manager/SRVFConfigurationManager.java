package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.configuration.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.Const;

/* JADX INFO: loaded from: classes.dex */
public class SRVFConfigurationManager {
    private static SRVFConfigurationManager instanceOfSRVFConfigurationManager;
    private static Context mContext;
    private static SharedPreferences mPref;

    private SRVFConfigurationManager() {
    }

    public static SRVFConfigurationManager getInstance(Context ctx) {
        mContext = ctx;
        mPref = mContext.getSharedPreferences(Const.SRVF_PREFERENCE, 0);
        if (instanceOfSRVFConfigurationManager == null) {
            instanceOfSRVFConfigurationManager = new SRVFConfigurationManager();
        }
        return instanceOfSRVFConfigurationManager;
    }

    public boolean isCheckedStatusOfNotice() {
        SharedPreferences pref = mContext.getSharedPreferences(Const.SRVF_PREFERENCE, 0);
        return pref.getBoolean(Const.PREF_CHECKED_STATUS_OF_NOTICE, false);
    }

    public void setCheckedStatusOfNotice(boolean status) {
        SharedPreferences pref = mContext.getSharedPreferences(Const.SRVF_PREFERENCE, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Const.PREF_CHECKED_STATUS_OF_NOTICE, status);
        editor.commit();
    }

    public String getLastSaveFileName() {
        SharedPreferences pref = mContext.getSharedPreferences(Const.SRVF_PREFERENCE, 0);
        return pref.getString(Const.PREF_LAST_SAVE_FILE_NAME, "no_file");
    }

    public void setLastSaveFileName(String lastFileName) {
        SharedPreferences pref = mContext.getSharedPreferences(Const.SRVF_PREFERENCE, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Const.PREF_LAST_SAVE_FILE_NAME, lastFileName);
        editor.commit();
    }
}

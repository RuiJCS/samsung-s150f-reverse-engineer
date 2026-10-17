package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.common;

import android.app.Activity;
import android.os.Bundle;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class DSCBaseActivity extends Activity {
    private Trace.Tag TAG = Trace.Tag.RVF;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        Trace.d(this.TAG, "start onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        Trace.d(this.TAG, "start onDestroy()");
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        Trace.d(this.TAG, "start onPause()");
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        Trace.d(this.TAG, "start onResume()");
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        Trace.d(this.TAG, "start onRestart()");
        super.onRestart();
    }

    @Override // android.app.Activity
    protected void onStart() {
        Trace.d(this.TAG, "start onStart()");
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        Trace.d(this.TAG, "start onStop()");
        super.onStop();
    }
}

package com.samsungimaging.connectionmanager.dialog;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class LicenseDialog extends Activity {
    WebView web;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_license);
        this.web = (WebView) findViewById(R.id.webView1);
        this.web.setBackgroundColor(0);
        this.web.loadUrl("file:///android_asset/open_source_license.html");
        this.web.setBackgroundColor(0);
        this.web.getSettings().setJavaScriptEnabled(true);
        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.LicenseDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LicenseDialog.this.onBackPressed();
            }
        });
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

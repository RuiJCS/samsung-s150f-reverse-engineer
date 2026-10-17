package com.samsungimaging.connectionmanager.app.localgallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.util.MyLocalGallery;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;

/* JADX INFO: loaded from: classes.dex */
public class LocalGallery extends BaseGalleryActivity implements View.OnClickListener {
    public static final String EXTRA_BACK_BUTTON_STRING_RES_ID = "extra_back_button_string_res_id";
    public static final String EXTRA_DESCRIPTION_RES_ID = "extra_description_res_id";
    public static final String EXTRA_IS_SUPPORT_BACK_BUTTON = "EXTRA_IS_SUPPORT_BACK_BUTTON";
    private TextView mDescription = null;
    private TextView mBackButton = null;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.samsungimaging.connectionmanager.app.localgallery.LocalGallery.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Utils.ACTION_DISCONNECT.equals(action)) {
                LocalGallery.this.finish();
            }
        }
    };

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLocalGallery.activityCreated();
        setContentView(R.layout.activity_status_type_button);
        this.mGallery = (GalleryFragment) getFragmentManager().findFragmentById(R.id.fragment_gallery);
        this.mGallery.setDiskCacheDir(GalleryFragment.DISK_CACHE_DIR_THUMBNAIL, GalleryFragment.DISK_CACHE_DIR_VIEWER);
        this.mGallery.setOnItemClickListener(this);
        this.mGallery.setOnItemLongClickListener(this);
        this.mGallery.setQuery(getQuery());
        this.mActionBar.setTitle(R.string.back);
        this.mActionBar.show();
        this.mDescription = (TextView) findViewById(android.R.id.text1);
        this.mDescription.setText(getIntent().getIntExtra(EXTRA_DESCRIPTION_RES_ID, 0));
        this.mBackButton = (TextView) findViewById(android.R.id.button1);
        this.mBackButton.setText(getIntent().getIntExtra(EXTRA_BACK_BUTTON_STRING_RES_ID, 0));
        this.mBackButton.setOnClickListener(this);
        if (getIntent().getBooleanExtra(EXTRA_IS_SUPPORT_BACK_BUTTON, true)) {
            this.mBackButton.setVisibility(0);
        } else {
            this.mBackButton.setVisibility(4);
        }
        initIntentFilter();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("LG", "LG_NEW INTENT");
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        unregisterReceiver(this.mBroadcastReceiver);
        MyLocalGallery.activityDestroyed();
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if ((this.mActionBar.getDisplayOptions() & 4) == 0) {
            this.mActionBar.setDisplayHomeAsUpEnabled(true);
            this.mActionBar.setTitle(R.string.back);
        }
        if (!CMUtil.checkOldVersionSmartCameraApp(CMInfo.getInstance().getConnectedSSID())) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (item.getItemId() == R.id.menu_camera && !item.isVisible()) {
                    item.setVisible(true);
                }
            }
        }
        return true;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_camera) {
            Trace.d(Trace.Tag.COMMON, "R.id.menu_camera");
            Intent data = new Intent();
            data.putExtra("request_code", "goto_rvf");
            setResult(-1, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (((TextView) v).getText().equals(getString(R.string.mobile_link))) {
            Intent data = new Intent();
            data.putExtra("request_code", "goto_ml");
            setResult(-1, data);
        }
        finish();
    }

    private void initIntentFilter() {
        IntentFilter filter = new IntentFilter(Utils.ACTION_DISCONNECT);
        registerReceiver(this.mBroadcastReceiver, filter);
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity
    protected void exit() {
        setResult(-1);
        finish();
    }
}

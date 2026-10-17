package com.samsungimaging.connectionmanager.app.pushservice.common;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.dialog.IntroGuideDialog;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public abstract class CommonPushService extends BaseGalleryActivity implements MediaScannerConnection.OnScanCompletedListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$app$pushservice$common$CommonPushService$Service;
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    private Service mService = Service.NONE;
    private TextView mMainDescription = null;
    private TextView mSubDescription = null;
    private BroadcastReceiver mBatteryChangedReceiver = new BroadcastReceiver() { // from class: com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int level;
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) && (level = intent.getIntExtra("level", -1)) != -1 && level <= 4) {
                Trace.d(CommonPushService.TAG, "Broadcasted Low Battery level : " + level);
                CommonPushService.this.showDialog(1008);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    public enum Service {
        NONE,
        AUTOSHARE,
        SELECTIVEPUSH,
        GROUPSHARE,
        MOBILEBACKUP;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Service[] valuesCustom() {
            Service[] serviceArrValuesCustom = values();
            int length = serviceArrValuesCustom.length;
            Service[] serviceArr = new Service[length];
            System.arraycopy(serviceArrValuesCustom, 0, serviceArr, 0, length);
            return serviceArr;
        }
    }

    protected abstract void exitAfterSendByeBye(String str);

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$app$pushservice$common$CommonPushService$Service() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$app$pushservice$common$CommonPushService$Service;
        if (iArr == null) {
            iArr = new int[Service.valuesCustom().length];
            try {
                iArr[Service.AUTOSHARE.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Service.GROUPSHARE.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Service.MOBILEBACKUP.ordinal()] = 5;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Service.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Service.SELECTIVEPUSH.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            $SWITCH_TABLE$com$samsungimaging$connectionmanager$app$pushservice$common$CommonPushService$Service = iArr;
        }
        return iArr;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_type_text);
        this.mGallery = (GalleryFragment) getFragmentManager().findFragmentById(R.id.fragment_gallery);
        this.mGallery.setDiskCacheDir(GalleryFragment.DISK_CACHE_DIR_THUMBNAIL, GalleryFragment.DISK_CACHE_DIR_VIEWER);
        this.mGallery.setOnItemClickListener(this);
        this.mGallery.setOnItemLongClickListener(this);
        this.mGallery.setQuery(getQuery());
        this.mActionBar.setTitle(R.string.download_files);
        this.mActionBar.show();
        this.mMainDescription = (TextView) findViewById(android.R.id.text1);
        this.mSubDescription = (TextView) findViewById(android.R.id.text2);
        registerReceiver(this.mBatteryChangedReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        unregisterReceiver(this.mBatteryChangedReceiver);
        super.onDestroy();
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.mService == Service.MOBILEBACKUP) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if ((item.getItemId() == R.id.menu_multi_select || item.getItemId() == R.id.menu_delete) && item.isVisible()) {
                    item.setVisible(false);
                }
            }
            return true;
        }
        return true;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case 1000:
                CustomProgressDialog progress = new CustomProgressDialog(this);
                progress.setCancelable(false);
                progress.setProgressStyle(1);
                progress.setTitle(R.string.ml_saving);
                progress.setOnProcessNumberListener(new CustomProgressDialog.OnProcessNumberListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService.2
                    @Override // com.samsungimaging.connectionmanager.dialog.CustomProgressDialog.OnProcessNumberListener
                    public String onProcessNumber(CustomProgressDialog dialog) {
                        return String.format(Locale.getDefault(), "%,.01f/%,.01fMB", Float.valueOf(dialog.getProgress() / 1000.0f), Float.valueOf(dialog.getMax() / 1000.0f));
                    }
                });
                Trace.d(TAG, "create dialog DIALOG_ID_PROCESSING");
                return progress;
            case 1001:
                CustomProgressDialog progress2 = new CustomProgressDialog(this);
                progress2.setCancelable(false);
                progress2.setProgressStyle(0);
                progress2.setMessage(R.string.ml_saving);
                Trace.d(TAG, "create dialog DIALOG_ID_SPINNER");
                return progress2;
            case 1002:
                Dialog dialog = new IntroGuideDialog(this);
                return dialog;
            case 1003:
                CustomDialog custom = new CustomDialog(this);
                custom.setMessage(R.string.insert_external_memory_card);
                custom.setCancelable(false);
                custom.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        CommonPushService.this.exitAfterSendByeBye(Const.ExitReason.REMOVED_SDCARD);
                    }
                });
                return custom;
            case 1004:
                CustomDialog custom2 = new CustomDialog(this);
                custom2.setMessage(R.string.insufficient_memory);
                custom2.setCancelable(false);
                custom2.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        CommonPushService.this.exitAfterSendByeBye(Const.ExitReason.MEMORY_FULL);
                    }
                });
                return custom2;
            case 1005:
            case 1007:
            default:
                Dialog dialog2 = super.onCreateDialog(id, bundle);
                return dialog2;
            case 1006:
                CustomDialog custom3 = new CustomDialog(this);
                custom3.setMessage(R.string.network_disconnected);
                return custom3;
            case 1008:
                CustomDialog custom4 = new CustomDialog(this);
                custom4.setMessage(R.string.rvf_battery_empty);
                custom4.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog3, int which) {
                        CommonPushService.this.exitAfterSendByeBye(Const.ExitReason.LOW_BATTERY);
                        CommonPushService.this.exit();
                    }
                });
                return custom4;
        }
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

    public void updateMediaContent(String filePath) {
        mediaScannerConnectionScanFile(filePath);
        this.mGallery.CheckDirtyCache(filePath);
    }

    public void mediaScannerConnectionScanFile(String filePath) {
        String[] paths = {filePath};
        String[] mimeTypes = {Utils.getMimeType(filePath)};
        MediaScannerConnection.scanFile(this, paths, mimeTypes, this);
    }

    @Override // android.media.MediaScannerConnection.OnScanCompletedListener
    public void onScanCompleted(String path, Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.mGallery.setReceivedMedia(DatabaseMedia.builder(cursor));
            cursor.close();
        }
    }

    protected void setService(Service service) {
        this.mService = service;
        switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$app$pushservice$common$CommonPushService$Service()[this.mService.ordinal()]) {
            case 2:
                this.mMainDescription.setText(R.string.connected_autoshare);
                this.mSubDescription.setText(R.string.connected_autoshare_description);
                break;
            case 3:
            case 4:
                this.mMainDescription.setText(R.string.connected_mobilelink);
                this.mSubDescription.setText(R.string.connected_selective_push_description);
                break;
            case 5:
                this.mMainDescription.setText(R.string.connected_mobilebackup);
                this.mSubDescription.setText(R.string.connected_mobilebackup_description);
                this.mGallery.setOnItemLongClickListener(null);
                break;
        }
    }

    protected Service getService() {
        return this.mService;
    }
}

package com.samsungimaging.connectionmanager.app.pullservice;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.localgallery.LocalGallery;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;
import com.samsungimaging.connectionmanager.util.Utils;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class MobileLink extends BaseGalleryActivity implements View.OnClickListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State;
    private static final int REQUEST_GALLERY = 0;
    private TextView mMainDescription = null;
    private TextView mSubDescription = null;

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State;
        if (iArr == null) {
            iArr = new int[GalleryFragment.State.valuesCustom().length];
            try {
                iArr[GalleryFragment.State.IMAGE_VIEWER.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[GalleryFragment.State.MULTI_SELECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[GalleryFragment.State.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State = iArr;
        }
        return iArr;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilelink);
        this.mGallery = (GalleryFragment) getFragmentManager().findFragmentById(R.id.fragment_gallery);
        this.mGallery.setDiskCacheDir("camera_thumbnail", "camera_viewer");
        this.mGallery.setOnItemClickListener(this);
        this.mGallery.setOnItemLongClickListener(this);
        testDBInsert();
        this.mActionBar.setTitle(R.string.mobile_link);
        this.mActionBar.show();
        setGravityForActionBarCustomView(5);
        this.mMainDescription = (TextView) findViewById(android.R.id.text1);
        this.mMainDescription.setText(R.string.connected_mobilelink);
        this.mSubDescription = (TextView) findViewById(android.R.id.text2);
        this.mSubDescription.setText(R.string.connected_mobilelink_description);
        this.mGallery.setState(GalleryFragment.State.MULTI_SELECT, new Object[0]);
        this.mGallery.setQuery(getQuery());
    }

    private void testDBInsert() {
        DatabaseManager.deleteForCameraMedia(this, null);
        this.mGallery.clearCache();
        int type = 1;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            if (type == 1) {
                type = 3;
            } else {
                type = 1;
            }
            cal.set(5, cal.get(5) - 1);
            DatabaseMedia media = new DatabaseMedia("http://192.168.101.1:7679/smp000001129121.jpg", "http://192.168.101.1:7679/smpthumb000002129121.jpg", "http://192.168.101.1:7679/smpscreen000003129121.jpg", type, cal.getTimeInMillis(), 0);
            DatabaseManager.putForCameraMedia(this, media);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        CMService.getInstance().beforefinish(0);
        Intent intent = new Intent(Utils.ACTION_DISCONNECT);
        sendBroadcast(intent);
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
        if (keyCode != 4 || this.mGallery.getState() == GalleryFragment.State.MULTI_SELECT) {
            return super.onKeyUp(keyCode, event);
        }
        this.mGallery.setState(GalleryFragment.State.MULTI_SELECT, new Object[0]);
        return true;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible()) {
                item.setVisible(false);
            }
        }
        switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State()[this.mGallery.getState().ordinal()]) {
            case 2:
                if ((this.mActionBar.getDisplayOptions() & 4) == 4) {
                    this.mActionBar.setDisplayHomeAsUpEnabled(false);
                    this.mActionBar.setTitle(R.string.mobile_link);
                }
                Button copy = (Button) findViewById(R.id.copy);
                if (this.mGallery.getCheckedCount() > 0) {
                    copy.setEnabled(true);
                    break;
                } else {
                    copy.setEnabled(false);
                    break;
                }
            default:
                return true;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery /* 2131558401 */:
                Intent intent = new Intent(this, (Class<?>) LocalGallery.class);
                intent.putExtra(LocalGallery.EXTRA_DESCRIPTION_RES_ID, R.string.connected_mobilelink);
                intent.putExtra(LocalGallery.EXTRA_BACK_BUTTON_STRING_RES_ID, R.string.camera_file_view);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity
    protected GalleryFragment.Query getQuery() {
        return new GalleryFragment.Query() { // from class: com.samsungimaging.connectionmanager.app.pullservice.MobileLink.1
            @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.Query
            public GalleryFragment.QueryInfo getGroupQueryInfo() {
                Uri uri = DatabaseMedia.CONTENT_URI;
                String[] projection = Utils.getMediaGroupQueryProjection();
                return new GalleryFragment.QueryInfo(uri, projection, Utils.getCameraMediaQuerySelection() + ") GROUP BY (datetaken_string", null, "datetaken_string DESC");
            }

            @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.Query
            public GalleryFragment.QueryInfo getChildQueryInfo(Cursor groupCursor) {
                String dateTakenString = groupCursor.getString(groupCursor.getColumnIndex(GalleryColumns.KEY_DATE_TAKEN_STRING));
                Uri uri = DatabaseMedia.CONTENT_URI;
                String[] projection = {GalleryColumns.KEY_ID, GalleryColumns.KEY_ORIGINAL_PATH, GalleryColumns.KEY_THUMBNAIL_PATH, GalleryColumns.KEY_VIEWER_PATH, GalleryColumns.KEY_DATE_TAKEN, "date((datetaken / 1000), 'unixepoch', 'localtime') as datetaken_string", GalleryColumns.KEY_MEDIA_TYPE, GalleryColumns.KEY_ORIENTATION};
                String[] selectionArgs = {dateTakenString};
                return new GalleryFragment.QueryInfo(uri, projection, Utils.getCameraMediaQuerySelection() + " AND datetaken_string=?", selectionArgs, "datetaken DESC");
            }
        };
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.gallery.GalleryFragment.OnItemLongClickListener
    public boolean onItemLongClick(View view, int groupPosition, int childPosition) {
        this.mGallery.setState(GalleryFragment.State.IMAGE_VIEWER, Integer.valueOf(groupPosition), Integer.valueOf(childPosition));
        return true;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0000. Please report as an issue. */
    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        }
    }
}

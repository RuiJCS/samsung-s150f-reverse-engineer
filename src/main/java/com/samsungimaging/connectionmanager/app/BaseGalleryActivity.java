package com.samsungimaging.connectionmanager.app;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.HelpDialog;
import com.samsungimaging.connectionmanager.dialog.LicenseDialog;
import com.samsungimaging.connectionmanager.dialog.ManualConnectionGuideDialog;
import com.samsungimaging.connectionmanager.dialog.NFCConnectionGuideDialog;
import com.samsungimaging.connectionmanager.dialog.PairedCameraDialog;
import com.samsungimaging.connectionmanager.dialog.PairedCameraRemoveDialog;
import com.samsungimaging.connectionmanager.dialog.PasswordDialog;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;
import com.samsungimaging.connectionmanager.widget.ListPopupWindow;

/* JADX INFO: loaded from: classes.dex */
public class BaseGalleryActivity extends BaseActivity implements GalleryFragment.OnItemClickListener, GalleryFragment.OnItemLongClickListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State = null;
    public static final String BUNDLE_KEY_DATA = "data";
    private static final int DIALOG_ID_HELP = 4;
    private static final int DIALOG_ID_LICENSE = 5;
    private static final int DIALOG_ID_MANUAL_CONNECTION_GUIDE = 2;
    public static final int DIALOG_ID_NFC_CONNECTION_GUIDE = 3;
    private static final int DIALOG_ID_PAIRED_CAMERA = 0;
    public static final int DIALOG_ID_PAIRED_CAMERA_REMOVE = 1;
    public static final int DIALOG_ID_PASSWORD = 6;
    private static final String LOCAL_MEDIA_FOLDER = "%DCIM/Camera/Samsung Smart Camera Application%";
    protected NfcAdapter mNFCAdapter = null;
    private Button mActionBarCustomView = null;
    private ListPopupWindow mSelectPopupWindow = null;
    protected GalleryFragment mGallery = null;
    private View.OnClickListener mActionBarCustomClickListener = new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.BaseGalleryActivity.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            String[] items;
            if (BaseGalleryActivity.this.mSelectPopupWindow == null) {
                BaseGalleryActivity.this.mSelectPopupWindow = new ListPopupWindow(BaseGalleryActivity.this);
                BaseGalleryActivity.this.mSelectPopupWindow.setSize(BaseGalleryActivity.this.getResources().getDimensionPixelSize(R.dimen.gallery_select_popup_window_width), -2);
                BaseGalleryActivity.this.mSelectPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.app.BaseGalleryActivity.1.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        boolean isCheckAll = false;
                        int checkedCount = BaseGalleryActivity.this.mGallery.getCheckedCount();
                        int childTotalCount = BaseGalleryActivity.this.mGallery.getChildTotalCount();
                        if (checkedCount == 0) {
                            isCheckAll = true;
                        } else if (checkedCount != childTotalCount) {
                            isCheckAll = position == 0;
                        }
                        BaseGalleryActivity.this.mGallery.setCheckAll(isCheckAll);
                        BaseGalleryActivity.this.mSelectPopupWindow.dismiss();
                    }
                });
            }
            int checkedCount = BaseGalleryActivity.this.mGallery.getCheckedCount();
            int childTotalCount = BaseGalleryActivity.this.mGallery.getChildTotalCount();
            if (checkedCount == 0) {
                items = BaseGalleryActivity.this.getResources().getStringArray(R.array.select_popup_zero);
            } else if (checkedCount == childTotalCount) {
                items = BaseGalleryActivity.this.getResources().getStringArray(R.array.select_popup_all);
            } else {
                items = BaseGalleryActivity.this.getResources().getStringArray(R.array.select_popup_more);
            }
            BaseGalleryActivity.this.mSelectPopupWindow.clearItem();
            BaseGalleryActivity.this.mSelectPopupWindow.addItem(items);
            BaseGalleryActivity.this.mSelectPopupWindow.showAsDropDown(v);
        }
    };

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

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mNFCAdapter = NfcAdapter.getDefaultAdapter(this);
        this.mActionBarCustomView = (Button) getLayoutInflater().inflate(R.layout.actionbar_custom, (ViewGroup) null);
        this.mActionBarCustomView.setOnClickListener(this.mActionBarCustomClickListener);
        this.mActionBar.setCustomView(this.mActionBarCustomView, new ActionBar.LayoutParams(3));
        this.mActionBar.setDisplayShowTitleEnabled(true);
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        if (this.mSelectPopupWindow != null) {
            this.mSelectPopupWindow.destroy();
        }
        super.onDestroy();
    }

    protected final boolean keyDown(int keyCode, KeyEvent event) {
        return this.mGallery.isVisible() && this.mGallery.onKeyDown(keyCode, event);
    }

    protected final boolean keyUp(int keyCode, KeyEvent event) {
        return this.mGallery.isVisible() && this.mGallery.onKeyUp(keyCode, event);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        Trace.d(Trace.Tag.COMMON, "start onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu, menu);
        if (this.mNFCAdapter == null) {
            menu.findItem(R.id.menu_nfc_connection_guide).setVisible(false);
            return true;
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        Trace.d(Trace.Tag.COMMON, "start onPrepareOptionsMenu()");
        if (this.mGallery != null) {
            switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State()[this.mGallery.getState().ordinal()]) {
                case 1:
                    for (int i = 0; i < menu.size(); i++) {
                        MenuItem item = menu.getItem(i);
                        if (item.getItemId() == R.id.menu_menu) {
                            if (!item.isVisible()) {
                                item.setVisible(true);
                            }
                        } else if (item.isVisible()) {
                            item.setVisible(false);
                        }
                    }
                    MenuItem item2 = menu.findItem(R.id.menu_multi_select);
                    if (this.mGallery.isEmpty()) {
                        if (item2.isVisible()) {
                            item2.setVisible(false);
                        }
                    } else if (!item2.isVisible()) {
                        item2.setVisible(true);
                    }
                    if ((this.mActionBar.getDisplayOptions() & 4) == 4) {
                        this.mActionBar.setDisplayHomeAsUpEnabled(false);
                        this.mActionBar.setTitle(R.string.download_files);
                    }
                    if ((this.mActionBar.getDisplayOptions() & 16) == 16) {
                        this.mActionBar.setDisplayShowCustomEnabled(false);
                    }
                    break;
                case 2:
                    int checkedCount = this.mGallery.getCheckedCount();
                    for (int i2 = 0; i2 < menu.size(); i2++) {
                        MenuItem item3 = menu.getItem(i2);
                        if (item3.getItemId() == R.id.menu_menu) {
                            if (!item3.isVisible()) {
                                item3.setVisible(true);
                            }
                        } else if (item3.getItemId() == R.id.menu_delete && checkedCount > 0) {
                            if (!item3.isVisible()) {
                                item3.setVisible(true);
                            }
                        } else if (item3.isVisible()) {
                            item3.setVisible(false);
                        }
                    }
                    if ((this.mActionBar.getDisplayOptions() & 4) == 0) {
                        this.mActionBar.setDisplayHomeAsUpEnabled(true);
                        this.mActionBar.setTitle(R.string.back);
                    }
                    if ((this.mActionBar.getDisplayOptions() & 16) == 0) {
                        this.mActionBar.setDisplayShowCustomEnabled(true);
                    }
                    this.mActionBarCustomView.setText(getString(R.string.selected_count, new Object[]{Integer.valueOf(checkedCount)}));
                    break;
                case 3:
                    for (int i3 = 0; i3 < menu.size(); i3++) {
                        MenuItem item4 = menu.getItem(i3);
                        if (item4.getItemId() == R.id.menu_delete || item4.getItemId() == R.id.menu_menu) {
                            if (!item4.isVisible()) {
                                item4.setVisible(true);
                            }
                        } else if (item4.isVisible()) {
                            item4.setVisible(false);
                        }
                    }
                    if ((this.mActionBar.getDisplayOptions() & 4) == 0) {
                        this.mActionBar.setDisplayHomeAsUpEnabled(true);
                        this.mActionBar.setTitle(R.string.back);
                    }
                    if ((this.mActionBar.getDisplayOptions() & 16) == 16) {
                        this.mActionBar.setDisplayShowCustomEnabled(false);
                    }
                    break;
            }
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        Trace.d(Trace.Tag.COMMON, "start onOptionsItemSelected()");
        switch (item.getItemId()) {
            case android.R.id.home:
                if ((this.mActionBar.getDisplayOptions() & 4) == 4) {
                    dispatchKeyEvent(new KeyEvent(0, 4));
                    dispatchKeyEvent(new KeyEvent(1, 4));
                }
                return true;
            case R.id.menu_delete /* 2131558723 */:
                this.mGallery.delete();
                return true;
            case R.id.menu_multi_select /* 2131558726 */:
                this.mGallery.setState(GalleryFragment.State.MULTI_SELECT, new Object[0]);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_paired_camera /* 2131558727 */:
                showDialog(0);
                return true;
            case R.id.menu_manual_connection_guide /* 2131558728 */:
                showDialog(2);
                return true;
            case R.id.menu_nfc_connection_guide /* 2131558729 */:
                Bundle bundle = new Bundle();
                bundle.putBoolean(BUNDLE_KEY_DATA, false);
                showDialog(3, bundle);
                return true;
            case R.id.menu_help /* 2131558730 */:
                showDialog(4);
                return true;
            case R.id.menu_license /* 2131558731 */:
                startActivity(new Intent(this, (Class<?>) LicenseDialog.class));
                return true;
            default:
                return true;
        }
    }

    protected void exit() {
        CMService.getInstance().finishSafe();
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case 0:
                Dialog dialog = new PairedCameraDialog(this);
                return dialog;
            case 1:
                Dialog dialog2 = new PairedCameraRemoveDialog(this);
                return dialog2;
            case 2:
                Dialog dialog3 = new ManualConnectionGuideDialog(this);
                return dialog3;
            case 3:
                Dialog dialog4 = new NFCConnectionGuideDialog(this);
                ((CustomDialog) dialog4).setTag(Boolean.valueOf(bundle.getBoolean(BUNDLE_KEY_DATA)));
                return dialog4;
            case 4:
                Dialog dialog5 = new HelpDialog(this);
                return dialog5;
            case 5:
            default:
                Dialog dialog6 = super.onCreateDialog(id, bundle);
                return dialog6;
            case 6:
                Dialog dialog7 = new PasswordDialog(this);
                return dialog7;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        switch (id) {
            case 1:
                DatabaseAP ap = (DatabaseAP) bundle.getSerializable(BUNDLE_KEY_DATA);
                ((CustomDialog) dialog).setTag(ap);
                break;
            case 3:
                ((CustomDialog) dialog).setTag(Boolean.valueOf(bundle.getBoolean(BUNDLE_KEY_DATA)));
                break;
            case 6:
                ((CustomDialog) dialog).setTag(bundle.getCharSequenceArray(BUNDLE_KEY_DATA));
                break;
        }
        super.onPrepareDialog(id, dialog, bundle);
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Trace.d(Trace.Tag.COMMON, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        invalidateActionBarCustomView();
    }

    protected GalleryFragment.Query getQuery() {
        return new GalleryFragment.Query() { // from class: com.samsungimaging.connectionmanager.app.BaseGalleryActivity.2
            @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.Query
            public GalleryFragment.QueryInfo getGroupQueryInfo() {
                Uri uri = MediaStore.Files.getContentUri("external");
                String[] projection = Utils.getMediaGroupQueryProjection();
                String[] selectionArgs = {BaseGalleryActivity.LOCAL_MEDIA_FOLDER};
                return new GalleryFragment.QueryInfo(uri, projection, Utils.getLocalMediaQuerySelection() + ") GROUP BY (datetaken_string", selectionArgs, "datetaken_string DESC");
            }

            @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.Query
            public GalleryFragment.QueryInfo getChildQueryInfo(Cursor groupCursor) {
                String dateTakenString = groupCursor.getString(groupCursor.getColumnIndex(GalleryColumns.KEY_DATE_TAKEN_STRING));
                Uri uri = MediaStore.Files.getContentUri("external");
                String[] projection = {GalleryColumns.KEY_ID, GalleryColumns.KEY_ORIGINAL_PATH, GalleryColumns.KEY_DATE_TAKEN, "date((datetaken / 1000), 'unixepoch', 'localtime') as datetaken_string", GalleryColumns.KEY_MEDIA_TYPE, GalleryColumns.KEY_ORIENTATION};
                String[] selectionArgs = {BaseGalleryActivity.LOCAL_MEDIA_FOLDER, dateTakenString};
                return new GalleryFragment.QueryInfo(uri, projection, Utils.getLocalMediaQuerySelection() + " AND datetaken_string=?", selectionArgs, "datetaken DESC");
            }
        };
    }

    protected void setGravityForActionBarCustomView(int gravity) {
        ActionBar.LayoutParams params = (ActionBar.LayoutParams) this.mActionBarCustomView.getLayoutParams();
        params.gravity = gravity;
        this.mActionBarCustomView.setLayoutParams(params);
    }

    @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.OnItemClickListener
    public void onItemClick(View view, int groupPosition, int childPosition) {
        if (this.mGallery.getState() == GalleryFragment.State.MULTI_SELECT) {
            this.mGallery.setChecked(groupPosition, childPosition);
        } else {
            this.mGallery.setState(GalleryFragment.State.IMAGE_VIEWER, Integer.valueOf(groupPosition), Integer.valueOf(childPosition));
        }
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), 3, 0.0f, 0.0f, 0);
        view.dispatchTouchEvent(event);
        event.recycle();
    }

    @Override // com.samsungimaging.connectionmanager.gallery.GalleryFragment.OnItemLongClickListener
    public boolean onItemLongClick(View view, int groupPosition, int childPosition) {
        if (this.mGallery.getState() != GalleryFragment.State.MULTI_SELECT) {
            this.mGallery.setState(GalleryFragment.State.MULTI_SELECT, Integer.valueOf(groupPosition), Integer.valueOf(childPosition));
        } else {
            this.mGallery.setChecked(groupPosition, childPosition);
        }
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), 3, 0.0f, 0.0f, 0);
        view.dispatchTouchEvent(event);
        event.recycle();
        return true;
    }

    private void invalidateActionBarCustomView() {
        int gravity = ((ActionBar.LayoutParams) this.mActionBarCustomView.getLayoutParams()).gravity;
        CharSequence text = this.mActionBarCustomView.getText();
        Utils.unbindView(this.mActionBarCustomView);
        this.mActionBarCustomView = (Button) getLayoutInflater().inflate(R.layout.actionbar_custom, (ViewGroup) null);
        this.mActionBarCustomView.setText(text);
        this.mActionBarCustomView.setOnClickListener(this.mActionBarCustomClickListener);
        this.mActionBar.setCustomView(this.mActionBarCustomView, new ActionBar.LayoutParams(gravity));
        if (this.mSelectPopupWindow != null && this.mSelectPopupWindow.isShowing()) {
            this.mSelectPopupWindow.dismiss();
            this.mSelectPopupWindow.showAsDropDown(this.mActionBarCustomView);
        }
    }
}

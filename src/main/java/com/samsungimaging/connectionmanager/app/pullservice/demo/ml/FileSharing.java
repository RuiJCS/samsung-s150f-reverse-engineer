package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.localgallery.LocalGallery;
import com.samsungimaging.connectionmanager.app.pullservice.PullService;
import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ContentArrayAdapter;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.entity.GroupEntity;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.entity.GroupEntity.GroupItemEntity;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter;
import com.samsungimaging.connectionmanager.app.pullservice.demo.util.CommonUtils;
import com.samsungimaging.connectionmanager.app.pullservice.dialog.CustomDialogFileSave;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.util.ExToast;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;

/* JADX INFO: loaded from: classes.dex */
public class FileSharing extends PullService {
    private static Button mCopyButton;
    private static Button mGallaryButton;
    private static Button mSelectButton;
    private LinearLayout action_bar;
    TextView bodyText;
    Button btnOk;
    private CheckBox checkBox;
    Dialog dialog;
    CustomDialog dialog1;
    LinearLayout layout;
    private ContentArrayAdapter mContentArrayAdapter;
    private ExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private GridView mGridView;
    private TextView mTextView;
    private ImageButton ml_goto_rvf;
    TextView title;
    private static ProgressDialog mConnectAPProgDialog = null;
    private static CustomProgressDialog mInitDataInfoProgDialog = null;
    private static ProgressDialog mCloseAppProgDialog = null;
    public static WifiManager mWifimanager = null;
    public static List<Item> mContainerList = new ArrayList();
    public static List<GroupEntity> mGroupCollection = new ArrayList();
    public static boolean mCancel = false;
    private static boolean bConnect = false;
    public static boolean bClosing = false;
    public static boolean bNoTelephonyDevice = false;
    private static boolean bFileReceiveDone = false;
    private int mSortingMode = 2;
    private Trace.Tag TAG = Trace.Tag.ML;
    private AlertDialog mProgressSave = null;
    private boolean bpopupflag = false;
    private boolean isChecked = false;
    ExToast mExToast = ExToast.getInstance();
    public WifiManager.WifiLock mWifiLock = null;
    public ScanResult mSelectedSr = null;
    public boolean bConnectedMobileData = true;
    ImageLoader mImageLoader = null;
    public boolean CopyModeState = false;
    private CustomDialogFileSave mCustomDialogFileSave = null;
    private Handler mEventHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Trace.d(FileSharing.this.TAG, "start handleMessage() : " + msg.what);
            switch (msg.what) {
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_OPERATION_STATE /* 117 */:
                    if (((String) msg.obj).equals("TransitionToRVF")) {
                        Intent intent = new Intent(FileSharing.this, (Class<?>) LiveShutter.class);
                        FileSharing.this.startActivity(intent);
                        FileSharing.this.finish();
                    } else if (((String) msg.obj).equals("TransitionToML")) {
                        String requestMessage = FileSharing.this.getIntent().getStringExtra("requestServiceChange");
                        if (requestMessage != null && requestMessage.equals("changeToML")) {
                            FileSharing.this.dismissDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
                        }
                        FileSharing.this.doDeviceConfiguration();
                    }
                    break;
            }
        }
    };
    private Handler mDeviceControlHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_END /* 85 */:
                    FileSharing.this.doSendEmptyMessage(5);
                    break;
                case DeviceController.ActionStatus.SET_TOTAL_COPY_ITEMS_ACTION_END /* 89 */:
                    boolean result = ((Boolean) msg.obj).booleanValue();
                    if (result) {
                        String totalNumber = FileSharing.this.mDeviceController.getAction(44).getArgumentValue("TotalNumber");
                        Trace.d(FileSharing.this.TAG, "TotalNumber : " + totalNumber);
                        if (!totalNumber.equals("0")) {
                            FileSharing.this.saveFiles();
                        }
                    }
                    break;
            }
        }
    };
    Dialog detail_view_dialog = null;
    Handler mHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            String imageUrl;
            Trace.d(FileSharing.this.TAG, "****MSGWHAT : " + String.valueOf(msg.what));
            switch (msg.what) {
                case 1:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.AP_CONNECTING");
                    FileSharing.this.dismissProgressDialog();
                    CharSequence str = String.format("%s\n\n[%s] %s", FileSharing.this.getString(R.string.ml_waitconnect), FileSharing.this.getString(R.string.ml_alert_smartphone), Utils.getMacAddress(FileSharing.this.getApplicationContext()));
                    FileSharing.mConnectAPProgDialog = ProgressDialog.show(FileSharing.this, null, str, true, true, null);
                    FileSharing.mConnectAPProgDialog.setCancelable(false);
                    FileSharing.mConnectAPProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.3.1
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                            if (event.getKeyCode() == 84) {
                                return true;
                            }
                            if (event.getKeyCode() == 4) {
                                FileSharing.this.showDialog(1003);
                                FileSharing.this.doSendEmptyMessageDelayed(32, 1000L);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                case 4:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.SHOW_GALLERY_DIALOG");
                    if (FileSharing.this.mProgressSave != null && FileSharing.this.mProgressSave.isShowing()) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                    }
                    if (!CommonUtils.getActivityRunning(FileSharing.this.getApplicationContext(), "samsungimaging")) {
                        FileSharing.bFileReceiveDone = true;
                    } else {
                        FileSharing.this.showDialog(1008);
                    }
                    break;
                case 5:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.DEVICE_CONFIGURATION_COMPLETED");
                    if (!Utils.getDisplayNotice(FileSharing.this.getApplicationContext())) {
                        FileSharing.this.doBrowsing();
                    } else {
                        FileSharing.this.doSendEmptyMessage(44);
                    }
                    break;
                case 6:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.CP_BROWSING_COMPLETED");
                    if (FileSharing.mInitDataInfoProgDialog != null) {
                        FileSharing.mInitDataInfoProgDialog.dismiss();
                        FileSharing.mSelectButton.setTextColor(Color.parseColor("#ebebeb"));
                        FileSharing.mInitDataInfoProgDialog = null;
                    }
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.DISPLAY_BROWSE_RESULT");
                    if (FileSharing.this.mSortingMode != 1) {
                        if (FileSharing.this.mSortingMode == 2) {
                            FileSharing.this.setGroupCollection(FileSharing.this.getApplicationContext().getResources().getInteger(R.integer.column_num));
                            FileSharing.this.mExpandableListAdapter = new ExpandableListAdapter(FileSharing.this.getApplicationContext(), FileSharing.this, FileSharing.this.mExpandableListView, FileSharing.mGroupCollection, FileSharing.this.getApplicationContext().getResources().getInteger(R.integer.column_num), true, FileSharing.this.mHandler, FileSharing.this.mExToast);
                            FileSharing.this.mExpandableListView.setAdapter(FileSharing.this.mExpandableListAdapter);
                            FileSharing.this.mExpandableListAdapter.notifyDataSetChanged();
                            for (int i = 0; i < FileSharing.this.mExpandableListAdapter.getGroupCount(); i++) {
                                if (FileSharing.mGroupCollection.get(i).groupExpandedStatus == 1) {
                                    FileSharing.this.mExpandableListView.expandGroup(i);
                                }
                            }
                        }
                    } else {
                        FileSharing.this.mGridView.setAdapter((ListAdapter) FileSharing.this.mContentArrayAdapter);
                        FileSharing.this.mContentArrayAdapter.notifyDataSetChanged();
                    }
                    FileSharing.this.onChangedSelectItemCount();
                    Trace.i(FileSharing.this.TAG, String.valueOf(Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext())) + "    Utils.getDisplayDetailGuide(getApplicationContext())    " + Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext()));
                    if (Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext())) {
                        FileSharing.this.showDialog(1009);
                    } else {
                        Toast.makeText(FileSharing.this, R.string.ml_connected, 1).show();
                    }
                    Log.d("Tag", "Select button enabled");
                    FileSharing.mSelectButton.setEnabled(true);
                    break;
                case 8:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.CP_DEVICE_FOUND");
                    if (FileSharing.mConnectAPProgDialog != null) {
                        FileSharing.mConnectAPProgDialog.dismiss();
                        FileSharing.mConnectAPProgDialog = null;
                    }
                    FileSharing.mInitDataInfoProgDialog = new CustomProgressDialog(FileSharing.this);
                    FileSharing.mInitDataInfoProgDialog.setCancelable(false);
                    FileSharing.mInitDataInfoProgDialog.setProgressStyle(0);
                    FileSharing.mInitDataInfoProgDialog.setMessage(FileSharing.this.getString(R.string.ml_init_camera_info));
                    FileSharing.mInitDataInfoProgDialog.show();
                    FileSharing.mInitDataInfoProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.3.2
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialogInterface, int i2, KeyEvent event) {
                            Trace.d(FileSharing.this.TAG, "onKey()");
                            if (event.getKeyCode() == 84) {
                                return true;
                            }
                            if (event.getKeyCode() == 4) {
                                FileSharing.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_QUESTION_EXIT);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                case 9:
                    int nDectectCount = ((Integer) msg.obj).intValue();
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.AP_CHECK_COMPLETED nDectectCount=" + nDectectCount);
                    if (nDectectCount < 2) {
                        Const.Config.DUMMY_WIFI_CONNECT_TYPE = 1;
                    } else {
                        Const.Config.DUMMY_WIFI_CONNECT_TYPE = 3;
                    }
                    Trace.d(FileSharing.this.TAG, "AP Running Mode = " + Const.Config.DUMMY_WIFI_CONNECT_TYPE);
                    break;
                case 10:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.AP_CHECK_FAILED");
                    FileSharing.this.doSendEmptyMessage(32);
                    break;
                case 21:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.APP_CLOSE_FOR_MEMORY_FULL");
                    FileSharing.this.showDialog(1006);
                    break;
                case 22:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.APP_CLOSE_FOR_REMOVED_SD");
                    FileSharing.this.showDialog(1005);
                    break;
                case 23:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.APP_CLOSE_FOR_DSC_ERROR");
                    if (!FileSharing.this.bpopupflag) {
                        FileSharing.this.bpopupflag = true;
                        FileSharing.this.showDialog(1002);
                        FileSharing.this.doSendEmptyMessageDelayed(32, 1000L);
                    }
                    break;
                case 32:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.EXITING");
                    FileSharing.this.appClose();
                    break;
                case 33:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.RUN_EXIT_BYEBYE");
                    FileSharing.this.showDialog(1003);
                    FileSharing.this.doSendEmptyMessageDelayed(32, 1000L);
                    break;
                case 36:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.CANCEL_SAVING");
                    if (FileSharing.this.mProgressSave != null && FileSharing.this.mProgressSave.isShowing()) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                    }
                    FileSharing.mCancel = true;
                    FileSharing.this.CopyModeState = false;
                    FileSharing.this.updateSelectedState();
                    break;
                case 37:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.CHANGED_SELECTED_FILE_COUNT");
                    FileSharing.this.onChangedSelectItemCount();
                    break;
                case 38:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.FINISH_SAVING_FILE");
                    if (FileSharing.this.mProgressSave != null && FileSharing.this.mProgressSave.isShowing()) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                        break;
                    }
                    break;
                case 39:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.AP_CONNECTED_FAILED");
                    FileSharing.this.showDialog(1010);
                    break;
                case 41:
                    Trace.d(FileSharing.this.TAG, "Const.MsgId.CONNGUIDE_DIALOG_OPEN");
                    if (FileSharing.mInitDataInfoProgDialog != null) {
                        FileSharing.mInitDataInfoProgDialog.dismiss();
                        FileSharing.mInitDataInfoProgDialog = null;
                    }
                    if (FileSharing.this.mSortingMode != 1) {
                        if (FileSharing.this.mSortingMode == 2) {
                            FileSharing.this.mExpandableListView.setAdapter(FileSharing.this.mExpandableListAdapter);
                            FileSharing.this.mExpandableListAdapter.notifyDataSetChanged();
                            for (int i2 = 0; i2 < FileSharing.this.mExpandableListAdapter.getGroupCount(); i2++) {
                                if (FileSharing.mGroupCollection.get(i2).groupExpandedStatus == 1) {
                                    FileSharing.this.mExpandableListView.expandGroup(i2);
                                }
                            }
                        }
                    } else {
                        FileSharing.this.mGridView.setAdapter((ListAdapter) FileSharing.this.mContentArrayAdapter);
                        FileSharing.this.mContentArrayAdapter.notifyDataSetChanged();
                    }
                    if (Utils.getDisplayConnGuide(FileSharing.this.getApplicationContext())) {
                        FileSharing.this.showDialog(1011);
                    } else {
                        FileSharing.this.appClose();
                    }
                    break;
                case 42:
                    Item item = (Item) msg.obj;
                    if (item != null) {
                        if (item.getScreenRes() == null || item.getScreenRes() == "") {
                            imageUrl = item.getThumbRes();
                        } else {
                            imageUrl = item.getScreenRes();
                        }
                        if (imageUrl != null) {
                            String filename = String.valueOf(imageUrl.hashCode());
                            File file = new File(Utils.getThumbStorage(), filename);
                            if (file.exists()) {
                                String fullPath = String.valueOf(Utils.getThumbStorage()) + "/" + filename;
                                boolean bRet = FileSharing.this.showDetailDialog(fullPath);
                                if (!bRet) {
                                    FileSharing.this.mExToast.show(7);
                                }
                            } else {
                                FileSharing.this.showDetailDialogForURL(imageUrl);
                            }
                        }
                    }
                    break;
                case 44:
                    FileSharing.this.showDialog(1013);
                    break;
                case 45:
                    if (!FileSharing.this.isFinishing()) {
                        FileSharing.this.showDialog(1014);
                    }
                    break;
                case 46:
                    String message = (String) msg.obj;
                    Trace.d(FileSharing.this.TAG, "ml message : " + message);
                    if (!message.contains("HTTP_UNAUTHORIZED")) {
                        if (message.contains("HTTP_UNAVAILABLE")) {
                            FileSharing.this.connectionFailDialog(FileSharing.this.getString(R.string.already_connected));
                        }
                    } else {
                        FileSharing.this.connectionFailDialog(FileSharing.this.getString(R.string.rejected_connection));
                    }
                    break;
                case 47:
                    FileSharing.this.showDialog(1015);
                    break;
            }
        }
    };
    private boolean bCloseByFinishSafe = false;

    public class SortingMode {
        public static final int DATE = 2;
        public static final int FILE_NAME = 3;
        public static final int FILE_SIZE = 4;
        public static final int NORMAL = 1;

        public SortingMode() {
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Trace.i(this.TAG, "Performance Check Point : start onCreate()");
        super.onCreate(savedInstanceState);
        if (!Utils.isMountedExternalStorage()) {
            showDialog(1005);
            return;
        }
        if (Utils.isMemoryFull()) {
            showDialog(1006);
            return;
        }
        CMService.ACTIVE_SERVICE = 1;
        init();
        if (this.upnpController.isConnected()) {
            onAlive();
        }
        if (this.connectedSsid.contains("NX1000") || this.connectedSsid.contains("NX1100")) {
            this.mSortingMode = 1;
        } else {
            this.mSortingMode = 2;
        }
        getWindow().addFlags(128);
        mWifimanager = (WifiManager) getSystemService("wifi");
        bNoTelephonyDevice = Utils.CheckTelephonyDevice(Build.MODEL);
        Trace.d(this.TAG, "model name : " + Build.MODEL + ",is No telephony device: " + bNoTelephonyDevice);
        this.mExToast.register(this, 4, R.string.ml_alert_check_full, 0);
        this.mExToast.register(this, 5, R.string.ml_alert_over_limitation_size, 0);
        this.mExToast.register(this, 6, R.string.insufficient_memory, 0);
        this.mExToast.register(this, 7, R.string.ml_alert_no_thumbnail, 0);
        this.mExToast.register(this, 9, R.string.rvf_changed_system_setting, 0);
        setContentView();
        System.gc();
        removeTempFiles(Utils.getThumbStorage());
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onStart() {
        super.onStart();
        Trace.i(this.TAG, "onStart()-111");
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.d("Test00", "**** filesharing onresume");
        Trace.i(this.TAG, "onResume()-111");
        getWindow().addFlags(128);
        if (bFileReceiveDone) {
            showDialog(1008);
            bFileReceiveDone = false;
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        Trace.i(this.TAG, "onRestart()-111");
        getWindow().addFlags(128);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onPause() {
        super.onPause();
        Trace.i(this.TAG, "onPause()-111");
        getWindow().addFlags(128);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onStop() {
        super.onStop();
        Trace.i(this.TAG, "onStop()-111");
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.d(this.TAG, "start onDestroy()");
        Intent intent = new Intent(com.samsungimaging.connectionmanager.util.Utils.ACTION_DISCONNECT);
        sendBroadcast(intent);
        super.onDestroy();
        if (this.mCustomDialogFileSave != null) {
            this.mCustomDialogFileSave.cancel(true);
        }
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        if (CMUtil.isForceClose(activityManager)) {
            SystemClock.sleep(3000L);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("goto RVF")) {
            doAction(43, "changeToRVF");
            showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        Trace.i(this.TAG, "onCreateDialog()-111 id : " + id);
        switch (id) {
            case 1000:
                break;
            case 1001:
            case 1012:
            default:
                return super.onCreateDialog(id);
            case 1002:
                Trace.d(this.TAG, "MSGBOX_DSC_DOWN");
                break;
            case 1003:
                Trace.i(this.TAG, "PROGRESS_BAR_DISPLAY_EXIT");
                dismissProgressDialog();
                mCloseAppProgDialog = new ProgressDialog(this);
                mCloseAppProgDialog.setMessage(getString(R.string.ml_appclose));
                mCloseAppProgDialog.setIndeterminate(true);
                mCloseAppProgDialog.setProgressStyle(0);
                mCloseAppProgDialog.setCancelable(false);
                mCloseAppProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.15
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        Trace.i(FileSharing.this.TAG, "onKey-3()");
                        return event.getKeyCode() == 84 || event.getKeyCode() == 4;
                    }
                });
                return mCloseAppProgDialog;
            case 1004:
                Trace.i(this.TAG, "MSGBOX_AP_FAIL");
                if (bClosing) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.end).setMessage(R.string.ml_apsettingfail).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.11
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Trace.d(FileSharing.this.TAG, "exit 66");
                        FileSharing.this.showDialog(1003);
                        FileSharing.this.doSendEmptyMessageDelayed(32, 1000L);
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.12
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Trace.i(FileSharing.this.TAG, "onKey-2()");
                        if (keyCode == 84 && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return keyCode == 4 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1005:
                dismissProgressDialog();
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.end).setMessage(R.string.insert_external_memory_card).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.18
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.showDialog(1003);
                        FileSharing.this.doSendEmptyMessageDelayed(32, 1000L);
                    }
                }).create();
            case 1006:
                dismissProgressDialog();
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.end).setMessage(R.string.ml_alert_sdcard_out_of_sd_space).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.17
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
            case 1007:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_alert_info).setMessage(R.string.ml_alert_needs_wifi_setting_file).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.19
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
            case 1008:
                this.dialog = new Dialog(this);
                this.dialog.requestWindowFeature(1);
                this.dialog.setContentView(R.layout.custom);
                this.dialog.setCancelable(false);
                this.title = (TextView) this.dialog.findViewById(R.id.title);
                this.title.setText(R.string.ml_file_copy);
                this.bodyText = (TextView) this.dialog.findViewById(R.id.msgBody);
                this.bodyText.setText(R.string.ml_copycomplete);
                this.btnOk = (Button) this.dialog.findViewById(R.id.btnOk);
                this.btnOk.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.16
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        FileSharing.this.dialog.dismiss();
                        FileSharing.this.updateSelectedState();
                        FileSharing.this.CopyModeState = false;
                    }
                });
                return this.dialog;
            case 1009:
                this.detail_view_dialog = new Dialog(this);
                this.detail_view_dialog.requestWindowFeature(1);
                this.detail_view_dialog.setContentView(R.layout.ml_custom_dialog_detail_view);
                this.detail_view_dialog.setCanceledOnTouchOutside(false);
                final CheckBox checkBox = (CheckBox) this.detail_view_dialog.findViewById(R.id.content_3);
                checkBox.setButtonDrawable(R.drawable.selector_checkbox);
                checkBox.setChecked(this.isChecked);
                float scale = getResources().getDisplayMetrics().density;
                checkBox.setPadding(checkBox.getPaddingLeft() + ((int) ((10.0f * scale) + 0.5f)), checkBox.getPaddingTop(), checkBox.getPaddingRight(), checkBox.getPaddingBottom());
                Button button = (Button) this.detail_view_dialog.findViewById(R.id.button_ok);
                button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.10
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            Utils.setDisplayDetailGuide(FileSharing.this.getApplicationContext(), false);
                        }
                        FileSharing.this.dismissDialog(1009);
                        Toast.makeText(FileSharing.this, R.string.ml_connected, 1).show();
                    }
                });
                return this.detail_view_dialog;
            case 1010:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_alert_info).setMessage(R.string.ml_alert_needs_wifi_setting_file).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.20
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.finish();
                    }
                }).create();
            case 1011:
                LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
                View layout = inflater.inflate(R.layout.ml_custom_dialog3, (ViewGroup) findViewById(R.id.ml_layout_root));
                final CheckBox checkbox = (CheckBox) layout.findViewById(R.id.ml_noticecheckbox);
                checkbox.setVisibility(8);
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.7
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkbox.setChecked(isChecked);
                    }
                });
                TextView tv = (TextView) layout.findViewById(R.id.ml_infotext2);
                tv.setText(R.string.ml_alert_conn_guide);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("");
                builder.setView(layout);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.8
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FileSharing.this.appClose();
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.9
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                FileSharing.this.finish();
                                return false;
                            case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return builder.create();
            case 1013:
                this.dialog = new Dialog(this);
                this.dialog.requestWindowFeature(1);
                this.dialog.setContentView(R.layout.ml_custom_dialog3);
                this.dialog.setCanceledOnTouchOutside(false);
                final CheckBox checkBox2 = (CheckBox) this.dialog.findViewById(R.id.ml_noticecheckbox);
                checkBox2.setButtonDrawable(R.drawable.selector_checkbox);
                checkBox2.setChecked(this.isChecked);
                float scale2 = getResources().getDisplayMetrics().density;
                checkBox2.setPadding(checkBox2.getPaddingLeft() + ((int) ((10.0f * scale2) + 0.5f)), checkBox2.getPaddingTop(), checkBox2.getPaddingRight(), checkBox2.getPaddingBottom());
                this.title = (TextView) this.dialog.findViewById(R.id.title);
                this.bodyText = (TextView) this.dialog.findViewById(R.id.ml_infotext2);
                this.layout = (LinearLayout) this.dialog.findViewById(R.id.titleLayout);
                this.layout.setVisibility(0);
                this.title.setText(R.string.rvf_title_notice);
                this.bodyText.setText(R.string.ml_notice);
                this.btnOk = (Button) this.dialog.findViewById(R.id.btnOk);
                checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.4
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkBox2.setChecked(isChecked);
                    }
                });
                this.btnOk.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        FileSharing.this.dialog.dismiss();
                        if (checkBox2.isChecked()) {
                            Trace.e(FileSharing.this.TAG, "notice is checked");
                            Utils.setDisplayNotice(FileSharing.this.getApplicationContext(), false);
                        }
                        FileSharing.this.doBrowsing();
                    }
                });
                this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.6
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                System.exit(1);
                                return false;
                            case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return this.dialog;
            case 1014:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_alert_info).setMessage(R.string.ml_transmission_fail).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.21
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
            case 1015:
                CustomDialog dialog = new CustomDialog(this);
                dialog.setIcon(R.drawable.ml_info);
                dialog.setTitle(R.string.ml_alert_info);
                dialog.setMessage(R.string.no_response_from_camera);
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.22
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        FileSharing.this.dismissDialog(1015);
                    }
                });
                return dialog;
        }
        return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.end).setMessage(R.string.network_disconnected).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int whichButton) {
                FileSharing.this.showDialog(1003);
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.14
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog2, int keyCode, KeyEvent event) {
                if (keyCode == 84 && event.getRepeatCount() == 0) {
                    return true;
                }
                return keyCode == 4 && event.getRepeatCount() == 0;
            }
        }).create();
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.checkBox != null) {
            this.isChecked = this.checkBox.isChecked();
        }
        switch (newConfig.orientation) {
            case 1:
            case 2:
                setContentView();
                if (this.mSortingMode == 1) {
                    this.mGridView.setAdapter((ListAdapter) this.mContentArrayAdapter);
                    this.mContentArrayAdapter.notifyDataSetChanged();
                } else if (this.mSortingMode == 2) {
                    setGroupCollection(getApplicationContext().getResources().getInteger(R.integer.column_num));
                    this.mExpandableListAdapter = new ExpandableListAdapter(getApplicationContext(), this, this.mExpandableListView, mGroupCollection, getApplicationContext().getResources().getInteger(R.integer.column_num), true, this.mHandler, this.mExToast);
                    this.mExpandableListView.setAdapter(this.mExpandableListAdapter);
                    this.mExpandableListAdapter.notifyDataSetChanged();
                    for (int i = 0; i < this.mExpandableListAdapter.getGroupCount(); i++) {
                        if (((GroupEntity) this.mExpandableListAdapter.getGroup(i)).groupExpandedStatus == 1) {
                            this.mExpandableListView.expandGroup(i);
                        }
                    }
                }
                onChangedSelectItemCount();
                break;
        }
        CommonUtils.setSystemConfigurationChanged(getApplicationContext());
        refreshDialog();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Trace.d(this.TAG, "onActivityResult() " + requestCode + ",    " + resultCode);
        switch (requestCode) {
            case 100:
                if (resultCode == -1) {
                    if (data.getStringExtra("request_code").equals("goto_rvf")) {
                        doAction(43, "changeToRVF");
                        showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
                    } else {
                        this.bCloseByFinishSafe = true;
                        new UnsubscribeTask(this, null).execute(this.upnpController.getConnectedDevice());
                    }
                }
                break;
            case 101:
                doAfterUserWifiSetting();
                break;
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case DeviceController.ActionStatus.GET_DEVICE_CONFIGURATION_ACTION_START /* 84 */:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void removeTempFiles(String mPath) {
        String[] children;
        Trace.e(this.TAG, "removeTempFiles start");
        Trace.e(this.TAG, "mPath is ::: " + mPath);
        File dir = new File(mPath);
        File[] childFileList = dir.listFiles();
        if (dir.isDirectory()) {
            Trace.e(this.TAG, "dir.listFiles() is :: " + childFileList.length + "     11111");
        }
        if (dir.exists() && childFileList.length != 0) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    removeTempFiles(childFile.getAbsolutePath());
                } else {
                    childFile.delete();
                }
            }
        }
        if (dir.length() != 0 && (children = dir.list()) != null) {
            for (String filename : children) {
                File f = new File(String.valueOf(mPath) + "/" + filename);
                if (f.exists()) {
                    Trace.e(this.TAG, "children size   :::   " + children.length);
                    f.delete();
                    Trace.e(this.TAG, "children size   :::   " + children.length);
                }
            }
        }
    }

    public void byebye() {
        Trace.d(this.TAG, "------- Bye Bye ------------");
        if (!isFinishing()) {
            Message msg = new Message();
            msg.what = 33;
            doSendMessageDelayed(msg, 10L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDeviceConfiguration() {
        Trace.d(this.TAG, "doDeviceConfiguration start");
        Action mDeviceConfiguration = this.mDeviceController.getAction(42);
        if (mDeviceConfiguration != null) {
            doAction(42, null);
        } else {
            doSendEmptyMessage(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doBrowsing() {
        Message msg1 = new Message();
        msg1.what = 8;
        doSendMessageDelayed(msg1, 1L);
        new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.23
            @Override // java.lang.Runnable
            public void run() {
                Trace.d(FileSharing.this.TAG, "DISPLAY_BROWSE_RESULT in r");
                if (FileSharing.this.mDeviceController.getAction(41) != null) {
                    Trace.d(FileSharing.this.TAG, "==============================");
                    Trace.d(FileSharing.this.TAG, "ddddddfffffffffgggggggggg       zzzzzzz eeee");
                    new BrowseManager(FileSharing.this.mDeviceController.getAction(41)).doBrowse();
                    Trace.d(FileSharing.this.TAG, "==============================");
                } else {
                    Trace.d(FileSharing.this.TAG, "d f q w e r t y u i o p a s d f g h j k l 0 0 0 0 0 0 0 0 0 0");
                }
                FileSharing.this.doSendEmptyMessage(6);
            }
        }).start();
    }

    public void closedata() {
        if (!bClosing) {
            bClosing = true;
            bConnect = false;
        }
    }

    public void appClose() {
        Trace.d(this.TAG, "start appClose()");
        Trace.d(this.TAG, "newlo onDestroy() bConnect : " + bConnect);
        dismissProgressDialog();
        this.upnpController.disconnect();
        this.upnpController.stop();
        CMService.getInstance().beforefinish(0);
        finish();
    }

    private void setContentView() {
        if (this.mSortingMode == 1) {
            setContentView(R.layout.ml_grid_view_sorting_none);
            this.mContentArrayAdapter = new ContentArrayAdapter(this, this, R.layout.ml_gridframe_sorting_none, mContainerList, true, this.mHandler, this.mExToast);
            this.mGridView = (GridView) findViewById(R.id.ml_gridview);
        } else if (this.mSortingMode == 2) {
            setContentView(R.layout.ml_grid_view);
            this.mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        }
        this.mTextView = (TextView) findViewById(R.id.ml_texttitle);
        this.action_bar = (LinearLayout) findViewById(R.id.action_bar);
        this.ml_goto_rvf = (ImageButton) findViewById(R.id.ml_goto_rvf);
        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID());
        if (isOldCamera) {
            this.action_bar.setVisibility(8);
        } else {
            this.ml_goto_rvf.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.24
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    FileSharing.this.doAction(43, "changeToRVF");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.24.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (CMService.ACTIVE_SERVICE == 1) {
                                FileSharing.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_LENS_ERROR);
                                handler.postDelayed(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.24.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        WifiManager wifi = (WifiManager) CMService.mContext.getSystemService("wifi");
                                        wifi.disconnect();
                                    }
                                }, 2000L);
                            }
                        }
                    }, 3000L);
                    FileSharing.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
                }
            });
        }
        mSelectButton = (Button) findViewById(R.id.ml_select_all_btn);
        mSelectButton.setEnabled(false);
        mSelectButton.setTextColor(-7829368);
        mSelectButton.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.25
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Trace.d(FileSharing.this.TAG, "mSelectButton text : " + FileSharing.mSelectButton.getText().toString() + " string resource : " + FileSharing.this.getString(R.string.ml_select_all));
                if (FileSharing.mSelectButton.getText().toString().equals(FileSharing.this.getString(R.string.ml_select_all))) {
                    FileSharing.this.selectAllItem();
                } else {
                    FileSharing.this.unSelectAllItem();
                }
            }
        });
        mCopyButton = (Button) findViewById(R.id.ml_copy_btn);
        setEnabledCopyButton(false);
        mCopyButton.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.26
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Integer totalCopyItems;
                Trace.d(FileSharing.this.TAG, "start mCopyButton.onClick() CopyModeState : " + FileSharing.this.CopyModeState);
                boolean isOldCamera2 = CMUtil.checkOldVersionSmartCameraApp(FileSharing.this.getConnectedSSID());
                if (!FileSharing.this.CopyModeState) {
                    FileSharing.this.CopyModeState = true;
                    if (!isOldCamera2) {
                        Integer.valueOf(0);
                        if (FileSharing.this.mSortingMode == 1) {
                            totalCopyItems = Integer.valueOf(FileSharing.this.mContentArrayAdapter.getCheckedItemCount());
                        } else {
                            totalCopyItems = Integer.valueOf(FileSharing.this.mExpandableListAdapter.getCheckedItemCount());
                        }
                        FileSharing.this.doAction(44, totalCopyItems.toString());
                        return;
                    }
                    FileSharing.this.saveFiles();
                }
            }
        });
        mGallaryButton = (Button) findViewById(R.id.ml_gallery_btn);
        mGallaryButton.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.27
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent(FileSharing.this, (Class<?>) LocalGallery.class);
                intent.putExtra(LocalGallery.EXTRA_DESCRIPTION_RES_ID, R.string.connected_mobilelink);
                intent.putExtra(LocalGallery.EXTRA_BACK_BUTTON_STRING_RES_ID, R.string.camera_file_view);
                FileSharing.this.startActivityForResult(intent, 100);
            }
        });
    }

    public static void setEnabledCopyButton(boolean bEnabled) {
        if (mCopyButton != null) {
            if (bEnabled) {
                mCopyButton.setEnabled(true);
                mCopyButton.setTextColor(Color.parseColor("#ebebeb"));
            } else {
                mCopyButton.setEnabled(false);
                mCopyButton.setTextColor(-7829368);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissProgressDialog() {
        if (mConnectAPProgDialog != null) {
            mConnectAPProgDialog.dismiss();
            mConnectAPProgDialog = null;
        }
        if (mInitDataInfoProgDialog != null) {
            mInitDataInfoProgDialog.dismiss();
            mInitDataInfoProgDialog = null;
        }
        if (mCloseAppProgDialog != null) {
            mCloseAppProgDialog.dismiss();
            mCloseAppProgDialog = null;
        }
        if (this.mProgressSave != null) {
            this.mProgressSave.dismiss();
            this.mProgressSave = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showDetailDialog(String fullPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(fullPath);
        if (bitmap == null) {
            return false;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.ml_custom_detail_view, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        final Dialog dialog = builder.create();
        ImageView iv = (ImageView) layout.findViewById(R.id.ml_iv_detail);
        iv.setImageBitmap(bitmap);
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.28
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDetailDialogForURL(String url) {
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.ml_custom_detail_view, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        final Dialog dialog = builder.create();
        ImageView iv = (ImageView) layout.findViewById(R.id.ml_iv_detail);
        this.mImageLoader.setImage(url, this, iv, null);
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.29
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void doAfterUserWifiSetting() {
        if (mWifimanager.isWifiEnabled()) {
            String ssid = this.connectedSsid;
            if (Utils.supportDSCPrefix(ssid)) {
                Trace.d(this.TAG, "1!!!!!!!!!!!!!");
                new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.30
                    @Override // java.lang.Runnable
                    public void run() {
                        FileSharing.this.doSendMessage(FileSharing.this.mHandler.obtainMessage(1));
                    }
                }).start();
                return;
            } else {
                Trace.d(this.TAG, "2!!!!!!!!!!!!!");
                showDialog(1003);
                new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.31
                    @Override // java.lang.Runnable
                    public void run() {
                        FileSharing.this.appClose();
                    }
                }).start();
                return;
            }
        }
        Trace.d(this.TAG, "3!!!!!!!!!!!!!");
        showDialog(1003);
        new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.32
            @Override // java.lang.Runnable
            public void run() {
                FileSharing.this.appClose();
            }
        }).start();
    }

    private class SdcardStateChangedReceiver extends BroadcastReceiver {
        private SdcardStateChangedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Trace.e(FileSharing.this.TAG, "Sdcard onReceive() -- " + intent.getAction());
            if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED") || intent.getAction().equals("android.intent.action.MEDIA_SHARED") || intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTABLE")) {
                Message newMsg = new Message();
                newMsg.what = 22;
                FileSharing.this.doSendMessageDelayed(newMsg, 1000L);
            }
        }
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        public MyPhoneStateListener() {
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0000. Please report as an issue. */
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onChangedSelectItemCount() {
        if (this.mSortingMode == 1) {
            this.mTextView.setText(String.valueOf(this.mContentArrayAdapter.getCheckedItemCount()) + "   " + getString(R.string.ml_item_selected));
            if (this.mContentArrayAdapter.getCheckedItemCount() > 0) {
                setEnabledCopyButton(true);
            } else {
                setEnabledCopyButton(false);
            }
            if (this.mContentArrayAdapter.getCheckedItemCount() >= 1000 || (this.mContentArrayAdapter.getCheckedItemCount() == this.mContentArrayAdapter.getCount() && this.mContentArrayAdapter.getCount() != 0)) {
                mSelectButton.setText(R.string.ml_unselect_all);
                return;
            } else {
                mSelectButton.setText(R.string.ml_select_all);
                return;
            }
        }
        if (this.mSortingMode == 2) {
            if (this.mExpandableListAdapter.getCheckedItemCount() > 0) {
                this.mTextView.setText(String.valueOf(this.mExpandableListAdapter.getCheckedItemCount()) + "   " + getString(R.string.ml_item_selected));
                setEnabledCopyButton(true);
            } else {
                this.mTextView.setText("");
                setEnabledCopyButton(false);
            }
            if (this.mExpandableListAdapter.getCheckedItemCount() >= 1000 || (this.mExpandableListAdapter.getCheckedItemCount() == this.mExpandableListAdapter.getItemCount() && this.mExpandableListAdapter.getItemCount() != 0)) {
                mSelectButton.setEnabled(true);
                mSelectButton.setText(R.string.ml_unselect_all);
                mSelectButton.setTextColor(-1);
            } else {
                mSelectButton.setEnabled(true);
                mSelectButton.setText(R.string.ml_select_all);
                mSelectButton.setTextColor(-1);
            }
        }
    }

    private class UnsubscribeTask extends AsyncTask<Device, Void, Void> {
        private UnsubscribeTask() {
        }

        /* synthetic */ UnsubscribeTask(FileSharing fileSharing, UnsubscribeTask unsubscribeTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            if (!FileSharing.this.isFinishing()) {
                FileSharing.this.showDialog(1003);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Device... params) {
            Trace.d(FileSharing.this.TAG, "start UnsubscribeTask.doInBackground()");
            if (FileSharing.this.upnpController != null && FileSharing.this.connectedSsid != null && params[0] != null) {
                FileSharing.this.upnpController.unsubscribe(params[0]);
                return null;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            Trace.d(FileSharing.this.TAG, "start UnsubscribeTask.onPostExecute()");
            FileSharing.this.appClose();
            if (FileSharing.this.bCloseByFinishSafe) {
                CMService.getInstance().finishSafe();
            }
        }
    }

    public void finishSafe() {
        Trace.d(this.TAG, "start finishSafe");
        this.upnpController.stop();
        CMService.getInstance().beforefinish(0);
    }

    public void selectAllItem() {
        Trace.d(this.TAG, "start selectAllItem()");
        if (this.mSortingMode == 1) {
            int checkableItemCount = 1000 - this.mContentArrayAdapter.getCheckedItemCount();
            for (int i = 0; i < this.mContentArrayAdapter.getCount(); i++) {
                if (this.mContentArrayAdapter.getItem(i).getItemState() == 1) {
                    if (Utils.getAvailableExternalMemorySize() <= this.mContentArrayAdapter.getCheckedItemSize() + Long.parseLong(this.mContentArrayAdapter.getItem(i).getSize())) {
                        Trace.d(this.TAG, "Warning memory full 00");
                    }
                    if (checkableItemCount > 0) {
                        checkableItemCount--;
                        this.mContentArrayAdapter.getItem(i).setItemState(2);
                    }
                }
            }
            this.mContentArrayAdapter.notifyDataSetChanged();
        } else if (this.mSortingMode == 2) {
            int checkableItemCount2 = 1000 - this.mExpandableListAdapter.getCheckedItemCount();
            for (int i2 = 0; i2 < mGroupCollection.size() && checkableItemCount2 > 0; i2++) {
                Trace.d(this.TAG, "i : " + i2);
                GroupEntity ge = mGroupCollection.get(i2);
                for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                    Trace.d(this.TAG, "j : " + j);
                    if (checkableItemCount2 <= 0) {
                        break;
                    }
                    GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                    for (int k = 0; k < gi.ItemList.size(); k++) {
                        Trace.d(this.TAG, "k : " + k);
                        Item item = gi.ItemList.get(k);
                        if (item.getItemState() == 1) {
                            Trace.d(this.TAG, "selectAllItem step 1");
                            Trace.d(this.TAG, "selectAllItem step 2");
                            if (checkableItemCount2 <= 0) {
                                break;
                            }
                            Trace.d(this.TAG, "selectAllItem step 3");
                            checkableItemCount2--;
                            item.setItemState(2);
                            if (this.mExpandableListAdapter.mChildMap.get(item) != null && item.equals(this.mExpandableListAdapter.mChildMap.get(item).getTag())) {
                                this.mExpandableListAdapter.mChildMap.get(item).setChecked(true);
                            }
                        }
                    }
                }
                if (this.mExpandableListAdapter.getGroupUncheckedItemCount(i2) == 0) {
                    ge.groupCheckedAllStatus = true;
                    if (this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)) != null) {
                        this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)).title.setChecked(true);
                    }
                }
            }
        }
        onChangedSelectItemCount();
        if (this.mSortingMode == 1) {
            if (this.mContentArrayAdapter.getCount() > 1000) {
                this.mExToast.show(4);
            }
        } else if (this.mSortingMode == 2 && this.mExpandableListAdapter.getItemCount() > 1000) {
            this.mExToast.show(4);
        }
    }

    public void unSelectAllItem() {
        Trace.d(this.TAG, "start unSelectAllItem()");
        if (this.mSortingMode == 1) {
            for (int i = 0; i < this.mContentArrayAdapter.getCount(); i++) {
                if (this.mContentArrayAdapter.getItem(i).getItemState() == 2) {
                    this.mContentArrayAdapter.getItem(i).setItemState(1);
                }
            }
            this.mContentArrayAdapter.notifyDataSetChanged();
        } else if (this.mSortingMode == 2) {
            for (int i2 = 0; i2 < mGroupCollection.size(); i2++) {
                GroupEntity ge = mGroupCollection.get(i2);
                for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                    GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                    for (int k = 0; k < gi.ItemList.size(); k++) {
                        Item item = gi.ItemList.get(k);
                        if (item.getItemState() == 2) {
                            item.setItemState(1);
                            if (this.mExpandableListAdapter.mChildMap.get(item) != null) {
                                this.mExpandableListAdapter.mChildMap.get(item).setChecked(false);
                            }
                        }
                    }
                }
                ge.groupCheckedAllStatus = false;
                if (this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)) != null) {
                    this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)).title.setChecked(false);
                }
            }
        }
        onChangedSelectItemCount();
    }

    public void updateSelectedState() {
        if (this.mSortingMode == 1) {
            for (int i = 0; i < this.mContentArrayAdapter.getCount(); i++) {
                if (this.mContentArrayAdapter.getItem(i).getItemState() == 3) {
                    this.mContentArrayAdapter.getItem(i).setItemState(1);
                }
            }
            this.mContentArrayAdapter.notifyDataSetChanged();
        } else if (this.mSortingMode == 2) {
            for (int i2 = 0; i2 < mGroupCollection.size(); i2++) {
                GroupEntity ge = mGroupCollection.get(i2);
                for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                    GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                    for (int k = 0; k < gi.ItemList.size(); k++) {
                        Item item = gi.ItemList.get(k);
                        if (item.getItemState() == 3) {
                            item.setItemState(1);
                            if (this.mExpandableListAdapter.mChildMap.get(item) != null) {
                                this.mExpandableListAdapter.mChildMap.get(item).setChecked(false);
                                ge.groupCheckedAllStatus = false;
                                if (this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)) != null) {
                                    this.mExpandableListAdapter.mGroupMap.get(Integer.valueOf(i2)).title.setChecked(false);
                                }
                            }
                        }
                    }
                }
            }
        }
        onChangedSelectItemCount();
    }

    public void refreshDialog() {
        if (this.detail_view_dialog != null && this.detail_view_dialog.isShowing()) {
            this.detail_view_dialog.dismiss();
            this.detail_view_dialog = new Dialog(this);
            this.detail_view_dialog.requestWindowFeature(1);
            this.detail_view_dialog.setContentView(R.layout.ml_custom_dialog_detail_view);
            this.detail_view_dialog.setCanceledOnTouchOutside(false);
            this.checkBox = (CheckBox) this.detail_view_dialog.findViewById(R.id.content_3);
            this.detail_view_dialog.setCanceledOnTouchOutside(false);
            this.checkBox.setButtonDrawable(R.drawable.selector_checkbox);
            this.checkBox.setChecked(this.isChecked);
            float scale = getResources().getDisplayMetrics().density;
            this.checkBox.setPadding(this.checkBox.getPaddingLeft() + ((int) ((10.0f * scale) + 0.5f)), this.checkBox.getPaddingTop(), this.checkBox.getPaddingRight(), this.checkBox.getPaddingBottom());
            Button button = (Button) this.detail_view_dialog.findViewById(R.id.button_ok);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.33
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (FileSharing.this.checkBox.isChecked()) {
                        FileSharing.this.isChecked = true;
                        Utils.setDisplayDetailGuide(FileSharing.this.getApplicationContext(), false);
                    }
                    FileSharing.this.detail_view_dialog.dismiss();
                    FileSharing.this.detail_view_dialog = null;
                    Toast.makeText(FileSharing.this, R.string.ml_connected, 1).show();
                }
            });
            this.detail_view_dialog.show();
        }
    }

    public void setGroupCollection(int columnNum) {
        Trace.d(this.TAG, "start setGroupCollection() columnNum : " + columnNum);
        for (int i = 0; i < mGroupCollection.size(); i++) {
            GroupEntity ge = mGroupCollection.get(i);
            ge.GroupItemCollection.clear();
            ge.getClass();
            GroupEntity.GroupItemEntity gi = ge.new GroupItemEntity();
            gi.ItemList = new ArrayList<>();
            ge.GroupItemCollection.add(gi);
        }
        for (int i2 = 0; i2 < mContainerList.size(); i2++) {
            Item item = mContainerList.get(i2);
            int j = 0;
            while (j < mGroupCollection.size()) {
                GroupEntity ge2 = mGroupCollection.get(j);
                if (!ge2.Name.equals(item.getDate())) {
                    j++;
                } else {
                    if (ge2.GroupItemCollection.get(ge2.GroupItemCollection.size() - 1).ItemList.size() < columnNum) {
                        ge2.GroupItemCollection.get(ge2.GroupItemCollection.size() - 1).ItemList.add(item);
                        break;
                    }
                    ge2.getClass();
                    GroupEntity.GroupItemEntity gi2 = ge2.new GroupItemEntity();
                    gi2.ItemList = new ArrayList<>();
                    gi2.ItemList.add(item);
                    ge2.GroupItemCollection.add(gi2);
                    break;
                }
            }
            if (j == mGroupCollection.size()) {
                GroupEntity ge3 = new GroupEntity();
                ge3.Name = item.getDate();
                ge3.getClass();
                GroupEntity.GroupItemEntity gi3 = ge3.new GroupItemEntity();
                gi3.ItemList = new ArrayList<>();
                gi3.ItemList.add(item);
                ge3.GroupItemCollection.add(gi3);
                mGroupCollection.add(ge3);
            }
        }
    }

    public void getParserException() {
        doSendEmptyMessage(46);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectionFailDialog(String message) {
        View view = getLayoutInflater().inflate(R.layout.ssc_connection_fail, (ViewGroup) null);
        Button button = (Button) view.findViewById(R.id.ssc_confirm_btn);
        TextView ssc_connection_fail = (TextView) view.findViewById(R.id.ssc_connection_fail);
        ssc_connection_fail.setText(message);
        TextView ssc_rejected_camera = (TextView) view.findViewById(R.id.ssc_rejected_camera);
        ssc_rejected_camera.setText(((Object) ssc_rejected_camera.getText()) + CMInfo.getInstance().getConnectedSSID());
        new AlertDialog.Builder(this).setView(view).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.34
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                if (event.getKeyCode() == 84) {
                    return true;
                }
                if (event.getKeyCode() != 4) {
                    return false;
                }
                CMService.getInstance().beforefinish(0);
                return true;
            }
        }).show();
        button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.35
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CMService.getInstance().beforefinish(0);
            }
        });
    }

    public void doAction(int id, String param) {
        Trace.d(this.TAG, "start doAction() id : " + id + " param : " + param);
        this.mDeviceController.doAction(id, param);
    }

    private void init() {
        this.mImageLoader = new ImageLoader(getApplicationContext(), this, R.drawable.ml_loading, Utils.getThumbStorage());
        Utils.setUseragent(getApplicationContext());
        mContainerList.clear();
        mGroupCollection.clear();
        this.upnpController.setEventHandler(this.mEventHandler);
        this.mDeviceController.setHandler(this.mDeviceControlHandler);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void closeService() {
        Trace.d(this.TAG, "start closeService()");
        this.mHandler = null;
        dismissProgressDialog();
        CMService.getInstance().beforefinish(0);
        finish();
        if (this.bCloseByFinishSafe) {
            CMService.getInstance().finishSafe();
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void onAlive() {
        bConnect = true;
        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID());
        if (isOldCamera) {
            doDeviceConfiguration();
            return;
        }
        Trace.d(this.TAG, "intent : " + getIntent().getStringExtra("requestServiceChange"));
        String requestMessage = getIntent().getStringExtra("requestServiceChange");
        if (requestMessage != null && requestMessage.equals("changeToML")) {
            showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
            doAction(43, "changeToML");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveFiles() {
        ArrayList<Item> items = new ArrayList<>();
        int itemCount = mContainerList.size();
        for (int i = 0; i < itemCount; i++) {
            Item item = mContainerList.get(i);
            if (item.getItemState() == 2) {
                items.add(item);
            }
        }
        this.mCustomDialogFileSave = new CustomDialogFileSave(this, this.mHandler, this.mDeviceController.isResizeMode(), items, this.mImageLoader);
        this.mCustomDialogFileSave.setNegativeButton(new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing.36
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                FileSharing.this.doAction(44, "0");
                FileSharing.this.mCustomDialogFileSave.cancel(true);
            }
        });
        this.mCustomDialogFileSave.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSendEmptyMessage(int what) {
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(what);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSendEmptyMessageDelayed(int what, long delayMillis) {
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessageDelayed(what, delayMillis);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSendMessage(Message msg) {
        if (this.mHandler != null) {
            this.mHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSendMessageDelayed(Message msg, long delayMillis) {
        if (this.mHandler != null) {
            this.mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }
}

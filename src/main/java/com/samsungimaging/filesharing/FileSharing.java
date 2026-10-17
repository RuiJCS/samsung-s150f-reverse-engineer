package com.samsungimaging.filesharing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.filesharing.adapter.ContentArrayAdapter;
import com.samsungimaging.filesharing.util.ExToast;
import com.samsungimaging.filesharing.util.Logger;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

/* JADX INFO: loaded from: classes.dex */
@SuppressLint({"NewApi", "NewApi"})
public class FileSharing extends Activity {
    private static LinearLayout mCopyButton;
    ProgressBar currentFileSizeProgressBar;
    TextView fileName;
    TextView fileSize;
    private ContentArrayAdapter mContainerAdapter;
    private ContentListManager mContentListManager;
    private int mCurrentFileIndex;
    private DeviceList mDeviceList;
    private GridView mGridView;
    private int mMaxTotalFileCount;
    private long mMaxTotalSize;
    private long mProgressTotalsize;
    private TextView mTextView;
    private WifiCamChecker mWifiCamChecker;
    private WifiConnector mWifiConnector;
    private WifiDialog mWifiDialog;
    TextView totalCount;
    ProgressBar totalFilesSizeProgressBar;
    TextView totalSize;
    private static final String TAG = FileSharing.class.getSimpleName();
    private static ProgressDialog mConnectAPProgDialog = null;
    private static ProgressDialog mInitDataInfoProgDialog = null;
    private static ProgressDialog mCloseAppProgDialog = null;
    public static WifiManager mWifimanager = null;
    private static Device mConnectedDevice = null;
    private static MyControlPoint mControlPoint = null;
    public static List<Item> mContainerList = new ArrayList();
    public static boolean mCancel = false;
    private static boolean bConnect = false;
    public static boolean bClosing = false;
    public static String mPreconnectedSsid = null;
    public static boolean bNoTelephonyDevice = false;
    public static boolean bPreWifiStatusOn = false;
    WifiStateChangedReceiverForAppClose mWifiStateChangedReceiverForAppClose = null;
    WifiStateChangedReceiverForSettingClose mWifiStateChangedReceiverForSettingClose = null;
    SdcardStateChangedReceiver sdcardStateChangedReceiver = new SdcardStateChangedReceiver(this, null);
    private AlertDialog mProgressSave = null;
    private Action mBrowseAction = null;
    private boolean bpopupflag = false;
    private boolean mcurrentactivity = false;
    ExToast mExToast = ExToast.getInstance();
    public WifiManager.WifiLock mWifiLock = null;
    public ScanResult mSelectedSr = null;
    public boolean bConnectedMobileData = true;
    public boolean CopyModeState = false;
    Handler mHandler = new Handler() { // from class: com.samsungimaging.filesharing.FileSharing.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            String thumbUrl;
            Logger.GG("****MSGWHAT : " + String.valueOf(msg.what));
            switch (msg.what) {
                case 1:
                    Logger.d(FileSharing.TAG, "Const.MsgId.AP_CONNECTING");
                    FileSharing.this.dismissProgressDialog();
                    CharSequence str = String.format("%s\n\n[%s] %s", FileSharing.this.getString(R.string.ml_waitconnect), FileSharing.this.getString(R.string.ml_alert_smartphone), Utils.getMacAddress(FileSharing.this.getApplicationContext()));
                    FileSharing.mConnectAPProgDialog = ProgressDialog.show(FileSharing.this, null, str, true, true, null);
                    FileSharing.mConnectAPProgDialog.setCancelable(false);
                    FileSharing.mConnectAPProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.1.2
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                            if (event.getKeyCode() == 84) {
                                return true;
                            }
                            if (event.getKeyCode() == 4) {
                                FileSharing.this.showDialog(12);
                                FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                case 3:
                    Logger.d(FileSharing.TAG, "Const.MsgId.AP_CONNECTED");
                    FileSharing.this.stopWifiConnector();
                    FileSharing.this.startControlPoint();
                    FileSharing.this.mHandler.sendEmptyMessageDelayed(7, 180000L);
                    FileSharing.this.registerDisconnectReceiver();
                    break;
                case 4:
                    Logger.d(FileSharing.TAG, "Const.MsgId.SHOW_GALLERY_DIALOG");
                    if (FileSharing.this.mProgressSave != null) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                    }
                    FileSharing.this.showDialog(24);
                    break;
                case 6:
                    Logger.d(FileSharing.TAG, "Const.MsgId.CP_BROWSING_COMPLETED");
                    if (FileSharing.mInitDataInfoProgDialog != null) {
                        FileSharing.mInitDataInfoProgDialog.dismiss();
                        FileSharing.mInitDataInfoProgDialog = null;
                    }
                    Logger.d(FileSharing.TAG, "Const.MsgId.DISPLAY_BROWSE_RESULT");
                    FileSharing.this.mGridView.setAdapter((ListAdapter) FileSharing.this.mContainerAdapter);
                    FileSharing.this.mContainerAdapter.notifyDataSetChanged();
                    Logger.i(FileSharing.TAG, String.valueOf(Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext())) + "    Utils.getDisplayDetailGuide(getApplicationContext())    " + Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext()));
                    if (Utils.getDisplayDetailGuide(FileSharing.this.getApplicationContext())) {
                        FileSharing.this.showDialog(25);
                    }
                    break;
                case 7:
                    Logger.d(FileSharing.TAG, "Const.MsgId.APP_CLOSE");
                    FileSharing.this.byebye();
                    break;
                case 8:
                    Logger.d(FileSharing.TAG, "Const.MsgId.CP_DEVICE_FOUND");
                    if (FileSharing.mConnectAPProgDialog != null) {
                        FileSharing.mConnectAPProgDialog.dismiss();
                        FileSharing.mConnectAPProgDialog = null;
                    }
                    Toast.makeText(FileSharing.this, R.string.ml_connected, 1).show();
                    FileSharing.mInitDataInfoProgDialog = ProgressDialog.show(FileSharing.this, null, FileSharing.this.getString(R.string.ml_init_camera_info), true, true, null);
                    FileSharing.mInitDataInfoProgDialog.setCancelable(false);
                    FileSharing.mInitDataInfoProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.1.3
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                            Logger.d(FileSharing.TAG, "onKey()");
                            if (event.getKeyCode() == 84) {
                                return true;
                            }
                            if (event.getKeyCode() == 4) {
                                Message msg2 = FileSharing.this.mHandler.obtainMessage(20);
                                FileSharing.this.mHandler.sendMessageDelayed(msg2, 10L);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                case 9:
                    int nDectectCount = ((Integer) msg.obj).intValue();
                    Logger.d(FileSharing.TAG, "Const.MsgId.AP_CHECK_COMPLETED nDectectCount=" + nDectectCount);
                    if (nDectectCount < 2) {
                        Const.Config.DUMMY_WIFI_CONNECT_TYPE = 1;
                    } else {
                        Const.Config.DUMMY_WIFI_CONNECT_TYPE = 3;
                    }
                    Logger.d(FileSharing.TAG, "AP Running Mode = " + Const.Config.DUMMY_WIFI_CONNECT_TYPE);
                    FileSharing.this.stopWifiCamChecker();
                    FileSharing.this.startWifiEnabler();
                    break;
                case 10:
                    Logger.d(FileSharing.TAG, "Const.MsgId.AP_CHECK_FAILED");
                    FileSharing.this.stopWifiCamChecker();
                    FileSharing.this.mHandler.sendEmptyMessage(32);
                    break;
                case 20:
                    Logger.d(FileSharing.TAG, "Const.MsgId.APP_CLOSE_FOR_EXITQUERY");
                    FileSharing.this.showDialog(10);
                    break;
                case 21:
                    Logger.d(FileSharing.TAG, "Const.MsgId.APP_CLOSE_FOR_MEMORY_FULL");
                    FileSharing.this.showDialog(22);
                    break;
                case 22:
                    Logger.d(FileSharing.TAG, "Const.MsgId.APP_CLOSE_FOR_REMOVED_SD");
                    FileSharing.this.showDialog(21);
                    break;
                case 23:
                    Logger.d(FileSharing.TAG, "Const.MsgId.APP_CLOSE_FOR_DSC_ERROR");
                    if (!FileSharing.this.bpopupflag) {
                        FileSharing.this.bpopupflag = true;
                        FileSharing.this.showDialog(11);
                        FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    }
                    break;
                case 32:
                    Logger.d(FileSharing.TAG, "Const.MsgId.EXITING");
                    new UnsubscribeTask(FileSharing.this, null).execute(FileSharing.mConnectedDevice);
                    break;
                case Const.MsgId.RUN_EXIT_BYEBYE /* 33 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.RUN_EXIT_BYEBYE");
                    FileSharing.this.showDialog(1);
                    FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    break;
                case Const.MsgId.PREPARE_SAVING_FILE /* 34 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.PREPARE_SAVING_FILE");
                    FileSharing.this.CopyModeState = true;
                    if (FileSharing.this.mProgressSave != null) {
                        FileSharing.this.mProgressSave.dismiss();
                    }
                    FileSharing.this.mProgressSave = null;
                    View view = FileSharing.this.getLayoutInflater().inflate(R.layout.ml_custom_progress_dialog, (ViewGroup) null);
                    FileSharing.this.currentFileSizeProgressBar = (ProgressBar) view.findViewById(R.id.ml_currentFileSizeProgressBar);
                    FileSharing.this.fileName = (TextView) view.findViewById(R.id.ml_fileName);
                    FileSharing.this.fileSize = (TextView) view.findViewById(R.id.ml_fileSize);
                    FileSharing.this.totalFilesSizeProgressBar = (ProgressBar) view.findViewById(R.id.ml_totalFilesSizeProgressBar);
                    FileSharing.this.totalCount = (TextView) view.findViewById(R.id.ml_totalCount);
                    FileSharing.this.totalSize = (TextView) view.findViewById(R.id.ml_totalSize);
                    FileSharing.this.setMaxTotalCount(FileSharing.this.mContainerAdapter.getCheckedItemCount());
                    FileSharing.this.setMaxTotalSize(FileSharing.this.mContainerAdapter.getCheckedItemSize());
                    FileSharing.this.setCurrentFileIndex(0);
                    FileSharing.this.mProgressSave = new AlertDialog.Builder(FileSharing.this).setIcon(R.drawable.ml_tw_popup_title_info_icon).setTitle(R.string.ml_saving).setView(view).setNegativeButton(R.string.ml_alert_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.1.4
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            FileSharing.this.mHandler.sendEmptyMessage(36);
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.1.5
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                            if (event.getKeyCode() == 84) {
                                return true;
                            }
                            if (event.getKeyCode() == 4) {
                                Logger.d(FileSharing.TAG, "onCancel()");
                                FileSharing.this.mHandler.sendEmptyMessage(36);
                                return true;
                            }
                            return false;
                        }
                    }).show();
                    break;
                case Const.MsgId.UPDATE_SAVING_MESSAGE /* 35 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.UPDATE_SAVING_MESSAGE");
                    if (FileSharing.this.mProgressSave != null) {
                        FileSharing.this.setCurrentFileName(FileSharing.mContainerList.get(FileSharing.this.mContentListManager.getCurrentSavingFileIndex()).getTitle());
                        FileSharing.this.setMaxCurrentFileSize(Long.parseLong(FileSharing.mContainerList.get(FileSharing.this.mContentListManager.getCurrentSavingFileIndex()).getSize()));
                        FileSharing.this.setProgressCurrentFileSize(FileSharing.this.mContentListManager.getDownloadedImageSize());
                        FileSharing.this.setCurrentFileIndex(FileSharing.this.mContentListManager.getTotalDownloadImageCount());
                        FileSharing.this.setProgressTotalSize((int) FileSharing.this.mContentListManager.getTotalDownloadImageSize());
                    }
                    break;
                case Const.MsgId.CANCEL_SAVING /* 36 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.CANCEL_SAVING");
                    if (FileSharing.this.mProgressSave != null) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                    }
                    FileSharing.mCancel = true;
                    FileSharing.this.mContentListManager.cancelSaving();
                    FileSharing.this.getChangeText();
                    break;
                case Const.MsgId.CHANGED_SELECTED_FILE_COUNT /* 37 */:
                    Logger.d("test02", "Const.MsgId.CHANGED_SELECTED_FILE_COUNT");
                    FileSharing.this.onChangedSelectItemCount();
                    break;
                case Const.MsgId.FINISH_SAVING_FILE /* 38 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.FINISH_SAVING_FILE");
                    if (FileSharing.this.mProgressSave != null) {
                        FileSharing.this.mProgressSave.dismiss();
                        FileSharing.this.mProgressSave = null;
                    }
                    FileSharing.this.CopyModeState = false;
                    break;
                case Const.MsgId.AP_CONNECTED_FAILED /* 39 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.AP_CONNECTED_FAILED");
                    FileSharing.this.showDialog(26);
                    break;
                case Const.MsgId.AP_DIALOG_OPEN /* 40 */:
                    FileSharing.this.mWifiDialog = new WifiDialog(FileSharing.this, FileSharing.this.mHandler);
                    FileSharing.this.mWifiDialog.scanResult.clear();
                    FileSharing.this.mWifiDialog.scanResult.addAll(WifiConnector.getInstance().srs);
                    FileSharing.this.mWifiDialog.setCancelable(false);
                    FileSharing.this.mWifiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.samsungimaging.filesharing.FileSharing.1.1
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog) {
                            if (FileSharing.mPreconnectedSsid == null) {
                                FileSharing.mPreconnectedSsid = FileSharing.this.mWifiDialog.selectedSr.SSID;
                                WifiDialog wifiDialog = FileSharing.this.mWifiDialog;
                                FileSharing fileSharing = FileSharing.this;
                                ScanResult scanResult = FileSharing.this.mWifiDialog.selectedSr;
                                fileSharing.mSelectedSr = scanResult;
                                wifiDialog.connect(scanResult);
                            }
                            FileSharing.this.doAfterUserWifiSetting();
                        }
                    });
                    FileSharing.this.mWifiDialog.show();
                    break;
                case Const.MsgId.CONNGUIDE_DIALOG_OPEN /* 41 */:
                    Logger.d(FileSharing.TAG, "Const.MsgId.CONNGUIDE_DIALOG_OPEN");
                    if (FileSharing.mInitDataInfoProgDialog != null) {
                        FileSharing.mInitDataInfoProgDialog.dismiss();
                        FileSharing.mInitDataInfoProgDialog = null;
                    }
                    FileSharing.this.mGridView.setAdapter((ListAdapter) FileSharing.this.mContainerAdapter);
                    FileSharing.this.mContainerAdapter.notifyDataSetChanged();
                    if (Utils.getDisplayConnGuide(FileSharing.this.getApplicationContext())) {
                        FileSharing.this.showDialog(27);
                    } else {
                        FileSharing.this.appClose();
                    }
                    break;
                case Const.MsgId.SHOW_DETAIL_IMAGE /* 42 */:
                    Item item = FileSharing.this.mContainerAdapter.getItem(((Integer) msg.obj).intValue());
                    if (item != null && (thumbUrl = item.getThumbRes()) != null) {
                        String filename = String.valueOf(thumbUrl.hashCode());
                        File file = new File(Utils.getThumbStorage(), filename);
                        if (file.exists()) {
                            String fullPath = String.valueOf(Utils.getThumbStorage()) + "/" + filename;
                            boolean bRet = FileSharing.this.showDetailDialog(fullPath);
                            if (!bRet) {
                                FileSharing.this.mExToast.show(7);
                            }
                        } else {
                            FileSharing.this.mExToast.show(7);
                        }
                        break;
                    }
                    break;
                case Const.MsgId.MSG_UNSUPPORTED_CAMERA /* 43 */:
                    FileSharing.this.dismissProgressDialog();
                    FileSharing.this.showDialog(29);
                    break;
            }
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Logger.i(TAG, "onCreate()-111");
        super.onCreate(savedInstanceState);
        if (!Utils.isMountedExternalStorage()) {
            showDialog(21);
            return;
        }
        if (Utils.isMemoryFull()) {
            showDialog(22);
            return;
        }
        if (Utils.detectAnotherSdicApp(getApplicationContext())) {
            showDialog(28);
            return;
        }
        getWindow().addFlags(128);
        mWifimanager = (WifiManager) getSystemService("wifi");
        if (mWifimanager.isWifiEnabled()) {
            Logger.d("HERE", "wifi enabled");
            bPreWifiStatusOn = true;
        } else {
            Logger.d("HERE", "wifi disabled");
            bPreWifiStatusOn = false;
        }
        bNoTelephonyDevice = Utils.CheckTelephonyDevice(Build.MODEL);
        Logger.d(TAG, "model name : " + Build.MODEL + ",is No telephony device: " + bNoTelephonyDevice);
        Utils.setUseragent(getApplicationContext());
        this.mExToast.register(this, 4, R.string.ml_alert_check_full, 0);
        this.mExToast.register(this, 5, R.string.ml_alert_over_limitation_size, 0);
        this.mExToast.register(this, 6, R.string.ml_alert_over_available_size, 0);
        this.mExToast.register(this, 7, R.string.ml_alert_no_thumbnail, 0);
        this.mContentListManager = new ContentListManager(this.mHandler);
        setContentView();
        System.gc();
        registerReceiver();
        removeTempFiles(Utils.getThumbStorage());
        getChangeText();
        if (Utils.getDisplayNotice(this)) {
            showDialog(20);
        } else {
            startWifiEnabler();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart()-111");
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume()-111");
        getWindow().addFlags(128);
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        Logger.i(TAG, "onRestart()-111");
        getWindow().addFlags(128);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause()-111");
        getWindow().addFlags(128);
        if (mWifimanager != null) {
            if (bPreWifiStatusOn) {
                mWifimanager.setWifiEnabled(true);
                Logger.d("HERE", "ON_DESTORY WIFI ON");
            } else {
                mWifimanager.setWifiEnabled(false);
                Logger.d("HERE", "ON_DESTORY WIFI OFF");
            }
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        Logger.i(TAG, "onStop()-111");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy()-111");
        removeTempFiles(Utils.getThumbStorage());
        if (mWifimanager != null) {
            mWifimanager.disconnect();
        }
        unregisterDisconnectReceiver();
        unregisterReceiver();
        this.mExToast.unregister(4);
        this.mExToast.unregister(5);
        this.mExToast.unregister(6);
        this.mExToast.unregister(7);
        Logger.i(TAG, "System.exit()");
        appClose();
        System.exit(1);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:16:0x0187  */
    /* JADX WARN: Code duplicated, block: B:17:0x01bc  */
    /* JADX WARN: Code duplicated, block: B:19:0x01c0  */
    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        Logger.i(TAG, "onCreateDialog()-111");
        switch (id) {
            case 1:
                if (!this.CopyModeState) {
                    return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_network_disconnect).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.13
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FileSharing.this.showDialog(12);
                            FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.14
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 84 && event.getRepeatCount() == 0) {
                                return true;
                            }
                            return keyCode == 4 && event.getRepeatCount() == 0;
                        }
                    }).create();
                }
                if (this.CopyModeState) {
                    return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_ap_disconnected).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.15
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FileSharing.this.showDialog(12);
                            FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.16
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 84 && event.getRepeatCount() == 0) {
                                return true;
                            }
                            return keyCode == 4 && event.getRepeatCount() == 0;
                        }
                    }).create();
                }
                Logger.i(TAG, "PROGRESS_BAR_DISPLAY_EXIT");
                dismissProgressDialog();
                mCloseAppProgDialog = new ProgressDialog(this);
                mCloseAppProgDialog.setMessage(getString(R.string.ml_appclose));
                mCloseAppProgDialog.setIndeterminate(true);
                mCloseAppProgDialog.setProgressStyle(0);
                mCloseAppProgDialog.setCancelable(false);
                mCloseAppProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.17
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        Logger.i(FileSharing.TAG, "onKey-3()");
                        return event.getKeyCode() == 84 || event.getKeyCode() == 4;
                    }
                });
                return mCloseAppProgDialog;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 14:
            case 15:
            case HostInterface.IPV6_BITMASK /* 16 */:
            case 17:
            case 18:
            case 19:
            default:
                return super.onCreateDialog(id);
            case 10:
                if (bClosing) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_question).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_exit_question).setPositiveButton(R.string.ml_alert_dialog_yes, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.18
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FileSharing.this.showDialog(12);
                        FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    }
                }).setNegativeButton(R.string.ml_alert_dialog_no, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.19
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FileSharing.this.mcurrentactivity = true;
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.20
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return event.getKeyCode() == 4;
                    }
                }).create();
            case Const.MsgBoxId.MSGBOX_DSC_DOWN /* 11 */:
                Logger.d(TAG, "MSGBOX_DSC_DOWN");
                if (!this.CopyModeState) {
                    return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_network_disconnect).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.13
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FileSharing.this.showDialog(12);
                            FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.14
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 84 && event.getRepeatCount() == 0) {
                                return true;
                            }
                            return keyCode == 4 && event.getRepeatCount() == 0;
                        }
                    }).create();
                }
                if (this.CopyModeState) {
                    return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_ap_disconnected).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.15
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FileSharing.this.showDialog(12);
                            FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.16
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 84 && event.getRepeatCount() == 0) {
                                return true;
                            }
                            return keyCode == 4 && event.getRepeatCount() == 0;
                        }
                    }).create();
                }
                Logger.i(TAG, "PROGRESS_BAR_DISPLAY_EXIT");
                dismissProgressDialog();
                mCloseAppProgDialog = new ProgressDialog(this);
                mCloseAppProgDialog.setMessage(getString(R.string.ml_appclose));
                mCloseAppProgDialog.setIndeterminate(true);
                mCloseAppProgDialog.setProgressStyle(0);
                mCloseAppProgDialog.setCancelable(false);
                mCloseAppProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.17
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        Logger.i(FileSharing.TAG, "onKey-3()");
                        return event.getKeyCode() == 84 || event.getKeyCode() == 4;
                    }
                });
                return mCloseAppProgDialog;
            case Const.MsgBoxId.PROGRESS_BAR_DISPLAY_EXIT /* 12 */:
                Logger.i(TAG, "PROGRESS_BAR_DISPLAY_EXIT");
                dismissProgressDialog();
                mCloseAppProgDialog = new ProgressDialog(this);
                mCloseAppProgDialog.setMessage(getString(R.string.ml_appclose));
                mCloseAppProgDialog.setIndeterminate(true);
                mCloseAppProgDialog.setProgressStyle(0);
                mCloseAppProgDialog.setCancelable(false);
                mCloseAppProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.17
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        Logger.i(FileSharing.TAG, "onKey-3()");
                        return event.getKeyCode() == 84 || event.getKeyCode() == 4;
                    }
                });
                return mCloseAppProgDialog;
            case Const.MsgBoxId.MSGBOX_AP_FAIL /* 13 */:
                Logger.i(TAG, "MSGBOX_AP_FAIL");
                if (bClosing) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_apsettingfail).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.11
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Logger.d(FileSharing.TAG, "exit 66");
                        FileSharing.this.showDialog(12);
                        FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.12
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Logger.i(FileSharing.TAG, "onKey-2()");
                        if (keyCode == 84 && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return keyCode == 4 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 20:
                LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
                View layout = inflater.inflate(R.layout.ml_custom_dialog3, (ViewGroup) findViewById(R.id.ml_layout_root));
                final CheckBox checkbox = (CheckBox) layout.findViewById(R.id.ml_noticecheckbox);
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.filesharing.FileSharing.5
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkbox.setChecked(isChecked);
                    }
                });
                TextView tv = (TextView) layout.findViewById(R.id.ml_infotext2);
                tv.setText(R.string.ml_notice);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.ml_title_notice);
                builder.setView(layout);
                builder.setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.6
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (checkbox.isChecked()) {
                            Logger.e(FileSharing.TAG, "notice is checked");
                            Utils.setDisplayNotice(FileSharing.this.getApplicationContext(), false);
                        }
                        FileSharing.this.startWifiEnabler();
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.7
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                System.exit(1);
                                return false;
                            case 84:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return builder.create();
            case 21:
                dismissProgressDialog();
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_alert_sdcard_message_removed).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.23
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.showDialog(12);
                        FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    }
                }).create();
            case 22:
                dismissProgressDialog();
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_exit_title).setMessage(R.string.ml_alert_sdcard_out_of_sd_space).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.22
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.showDialog(12);
                        FileSharing.this.mHandler.sendEmptyMessageDelayed(32, 1000L);
                    }
                }).create();
            case 23:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_alert_info).setMessage(R.string.ml_alert_needs_wifi_setting_file).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.24
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.startWifiConnector();
                    }
                }).create();
            case Const.MsgBoxId.MSGBOX_COPY_COMPLETE /* 24 */:
                return new AlertDialog.Builder(this).setTitle(R.string.ml_file_copy).setMessage(R.string.ml_copycomplete).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.21
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.mContentListManager.cancelSaving();
                        FileSharing.this.getChangeText();
                    }
                }).create();
            case Const.MsgBoxId.MSGBOX_GUIDE_DETAIL_VIEW /* 25 */:
                Logger.i(TAG, "MSGBOX_GUIDE_DETAIL_VIEW is start!!!!");
                LayoutInflater inflater2 = (LayoutInflater) getSystemService("layout_inflater");
                View layout2 = inflater2.inflate(R.layout.ml_custom_dialog3, (ViewGroup) findViewById(R.id.ml_layout_root));
                final CheckBox checkbox2 = (CheckBox) layout2.findViewById(R.id.ml_noticecheckbox);
                checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.filesharing.FileSharing.8
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkbox2.setChecked(isChecked);
                    }
                });
                TextView tv2 = (TextView) layout2.findViewById(R.id.ml_infotext2);
                tv2.setText(R.string.ml_alert_guide_detail_content);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(R.string.ml_alert_guide_detail_title);
                builder2.setView(layout2);
                builder2.setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.9
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (checkbox2.isChecked()) {
                            Logger.e(FileSharing.TAG, "notice is checked");
                            Utils.setDisplayDetailGuide(FileSharing.this.getApplicationContext(), false);
                        }
                    }
                });
                builder2.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.10
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                FileSharing.this.finish();
                                return false;
                            case 84:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return builder2.create();
            case Const.MsgBoxId.MSGBOX_NEED_CAM_WIFI /* 26 */:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ml_info).setTitle(R.string.ml_alert_info).setMessage(R.string.ml_alert_needs_wifi_setting_file).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.25
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.finish();
                    }
                }).create();
            case Const.MsgBoxId.MSGBOX_CONNGUIDE_DETAIL_VIEW /* 27 */:
                LayoutInflater inflater3 = (LayoutInflater) getSystemService("layout_inflater");
                View layout3 = inflater3.inflate(R.layout.ml_custom_dialog3, (ViewGroup) findViewById(R.id.ml_layout_root));
                final CheckBox checkbox3 = (CheckBox) layout3.findViewById(R.id.ml_noticecheckbox);
                checkbox3.setVisibility(8);
                checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.filesharing.FileSharing.2
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkbox3.setChecked(isChecked);
                    }
                });
                TextView tv3 = (TextView) layout3.findViewById(R.id.ml_infotext2);
                tv3.setText(R.string.ml_alert_conn_guide);
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("");
                builder3.setView(layout3);
                builder3.setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FileSharing.this.appClose();
                    }
                });
                builder3.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.4
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                FileSharing.this.finish();
                                return false;
                            case 84:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return builder3.create();
            case Const.MsgBoxId.MSGBOX_DETECTED_SAMSUNG_SMART_CAMERA /* 28 */:
                return new AlertDialog.Builder(this).setMessage(R.string.ml_msg_detected_samsung_smart_camera).setCancelable(false).setPositiveButton(R.string.ml_alert_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.26
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.finish();
                    }
                }).create();
            case Const.MsgBoxId.MSGBOX_UNSUPPORTED_CAMERA /* 29 */:
                Logger.d(TAG, "Const.MsgBoxId.MSGBOX_UNSUPPORTED_CAMERA");
                LayoutInflater inflater4 = (LayoutInflater) getSystemService("layout_inflater");
                View layout4 = inflater4.inflate(R.layout.ml_dialog_unsupported_camera, (ViewGroup) findViewById(R.id.ml_layout_root));
                Button link = (Button) layout4.findViewById(R.id.ml_link_playstore_samsung_smart_camera);
                link.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.27
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        Uri uri = Uri.parse("market://details?id=com.samsungimaging.connectionmanager");
                        Intent i = new Intent("android.intent.action.VIEW", uri);
                        this.startActivity(i);
                    }
                });
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setView(layout4);
                builder4.setPositiveButton(R.string.ml_close, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.28
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        FileSharing.this.appClose();
                    }
                });
                builder4.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.filesharing.FileSharing.29
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case 4:
                                System.exit(1);
                                return false;
                            case 84:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return builder4.create();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case 1:
            case 2:
                setContentView();
                this.mGridView.setAdapter((ListAdapter) this.mContainerAdapter);
                this.mContainerAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                if (bConnect) {
                    Logger.d(TAG, "------------ BackKey Cancel ----------");
                    break;
                } else {
                    dismissProgressDialog();
                    if (!this.mcurrentactivity) {
                        Message msg = this.mHandler.obtainMessage(20);
                        this.mHandler.sendMessageDelayed(msg, 10L);
                    }
                    break;
                }
            case 84:
            default:
                return true;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                if (this.mcurrentactivity) {
                    Logger.i(TAG, "----------KEYCODE_BACK----------::::FileSharing.java");
                    Message msg = this.mHandler.obtainMessage(20);
                    this.mHandler.sendMessageDelayed(msg, 10L);
                } else {
                    this.mcurrentactivity = true;
                }
                return false;
            case 84:
                return true;
            default:
                return false;
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult() " + requestCode);
        if (requestCode == 100) {
            Logger.d(TAG, "gallery End");
            if (resultCode == -1) {
                Logger.d(TAG, "RESULT_OK");
                setContentView();
                this.mGridView.setAdapter((ListAdapter) this.mContainerAdapter);
                this.mContainerAdapter.notifyDataSetChanged();
                this.mContentListManager.cancelSaving();
                getChangeText();
                if (this.mProgressSave != null) {
                    this.mProgressSave.dismiss();
                }
                this.mProgressSave = null;
                return;
            }
            return;
        }
        if (requestCode == 101) {
            Logger.d(TAG, "wifisetting End");
            doAfterUserWifiSetting();
            unregisterSettingCloseReceiver();
        }
    }

    private void removeTempFiles(String mPath) {
        String[] children;
        Logger.e(TAG, "removeTempFiles start");
        Logger.e(TAG, "mPath is ::: " + mPath);
        File dir = new File(mPath);
        File[] childFileList = dir.listFiles();
        if (dir.isDirectory()) {
            Logger.e(TAG, "dir.listFiles() is :: " + childFileList.length + "     11111");
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
                    Logger.e(TAG, "children size   :::   " + children.length);
                    f.delete();
                    Logger.e(TAG, "children size   :::   " + children.length);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiEnabler() {
        saveInitialState();
        if (Const.Config.DUMMY_WIFI_CONNECT_TYPE == 2) {
            if (!isConnectCamAP()) {
                showDialog(23);
                return;
            } else {
                doAfterUserWifiSetting();
                return;
            }
        }
        startWifiConnector();
    }

    public void startControlPoint() {
        mControlPoint = MyControlPoint.getInstance();
        mControlPoint.SetMain(this);
    }

    public void byebye() {
        Logger.d(TAG, "------- Bye Bye ------------");
        Message msg = new Message();
        msg.what = 33;
        this.mHandler.sendMessageDelayed(msg, 10L);
    }

    private void checkDevice(Device dev) {
        this.mDeviceList = mControlPoint.getDeviceList();
        boolean bSearchFlag = false;
        for (int i = 0; i < this.mDeviceList.size(); i++) {
            String devicetype = new String(this.mDeviceList.getDevice(i).getDeviceType());
            if (devicetype.contains("MediaServer")) {
                mConnectedDevice = dev;
                if (this.mBrowseAction != null) {
                    this.mBrowseAction.print();
                }
                if (!bConnect) {
                    Message msg1 = new Message();
                    msg1.what = 8;
                    this.mHandler.sendMessageDelayed(msg1, 1L);
                    Logger.d(TAG, "Device Detected.");
                    bSearchFlag = true;
                    MyControlPoint.selectedDevice = this.mDeviceList.getDevice(i);
                    mConnectedDevice = this.mDeviceList.getDevice(i);
                    this.mBrowseAction = mConnectedDevice.getAction("Browse");
                    new Thread(new Runnable() { // from class: com.samsungimaging.filesharing.FileSharing.30
                        @Override // java.lang.Runnable
                        public void run() {
                            Logger.d(FileSharing.TAG, "DISPLAY_BROWSE_RESULT in r");
                            if (FileSharing.this.mBrowseAction != null) {
                                Logger.d(FileSharing.TAG, "==============================");
                                Logger.d(FileSharing.TAG, "ddddddfffffffffgggggggggg       zzzzzzz eeee");
                                BrowseManager.getInstance(FileSharing.this.mBrowseAction).doBrowse();
                                Logger.d(FileSharing.TAG, "==============================");
                            } else {
                                Logger.d(FileSharing.TAG, "d f q w e r t y u i o p a s d f g h j k l 0 0 0 0 0 0 0 0 0 0");
                            }
                            FileSharing.this.mHandler.sendEmptyMessage(6);
                        }
                    }).start();
                    this.mHandler.removeMessages(7);
                    bConnect = true;
                    break;
                }
            }
        }
        if (!bSearchFlag) {
            mConnectedDevice = null;
        }
    }

    public void DeviceChange(int nChange, Device dev) {
        Logger.d(TAG, "DeviceChange");
        if (nChange == 256) {
            if (!bConnect && mConnectedDevice == null) {
                Logger.d(TAG, "To checkDevice was started!");
                checkDevice(dev);
                return;
            }
            return;
        }
        if (nChange == 257) {
            DeviceList mDList = mControlPoint.getDeviceList();
            boolean bFind = false;
            for (int i = 0; i < mDList.size(); i++) {
                String devicetype = new String(mDList.getDevice(i).getDeviceType());
                if (devicetype.contains("MediaServer")) {
                    Device device = mDList.getDevice(i);
                    if (device == mConnectedDevice) {
                        bFind = true;
                        break;
                    }
                }
            }
            if (!bFind && dev == mConnectedDevice) {
                closedata();
                Message msg = new Message();
                msg.what = 23;
                this.mHandler.sendMessageDelayed(msg, 0L);
            }
        }
    }

    public void closedata() {
        if (!bClosing) {
            bClosing = true;
            if (mControlPoint != null) {
                if (mConnectedDevice != null) {
                    mControlPoint.unsubscribe(mConnectedDevice);
                }
                mControlPoint.finalize();
            }
            bConnect = false;
        }
    }

    public void appClose() {
        Logger.d(TAG, "start appClose");
        if (this.mContentListManager != null) {
            this.mContentListManager.stopImageLoader();
        }
        bClosing = true;
        bConnect = false;
        if (mControlPoint != null) {
            Logger.d(TAG, "finalize");
            if (mConnectedDevice != null) {
                mControlPoint.unsubscribe(mConnectedDevice);
            }
            mControlPoint.finalize();
        }
        WifiUtils.removeCameraConfigration(this, true);
        restoreInitialState(true);
        if (mWifimanager != null) {
            mWifimanager.disconnect();
        }
        System.exit(1);
    }

    private void setContentView() {
        setContentView(R.layout.ml_grid_view);
        if (this.mProgressSave != null) {
            this.mProgressSave.dismiss();
        }
        this.mProgressSave = null;
        this.mContainerAdapter = new ContentArrayAdapter(this, R.layout.ml_gridframe, mContainerList, true, this.mHandler, this.mExToast);
        this.mcurrentactivity = true;
        this.mContentListManager.mAdapter = this.mContainerAdapter;
        this.mGridView = (GridView) findViewById(R.id.ml_gridview);
        this.mTextView = (TextView) findViewById(R.id.ml_texttitle);
        mCopyButton = (LinearLayout) findViewById(R.id.ml_copybtn1);
        mCopyButton.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.31
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                FileSharing.this.mContentListManager.SaveAllFileToPhone(FileSharing.this.getApplicationContext());
            }
        });
        getChangeText();
    }

    public static void setEnabledCopyButton(boolean bEnabled) {
        if (mCopyButton != null) {
            if (bEnabled) {
                mCopyButton.setEnabled(true);
            } else {
                mCopyButton.setEnabled(false);
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
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.FileSharing.32
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return true;
    }

    private void startWifiChecker() {
        this.mWifiCamChecker = new WifiCamChecker(this, this.mHandler);
        this.mWifiCamChecker.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWifiCamChecker() {
        if (this.mWifiCamChecker != null) {
            this.mWifiCamChecker.stop();
            this.mWifiCamChecker = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiConnector() {
        Logger.d(TAG, "WifiConnector!!!!!!!!!!!! start");
        if (Const.Config.DUMMY_WIFI_CONNECT_TYPE == 1) {
            this.mWifiConnector = WifiConnector.getInstance();
            this.mWifiConnector.init(this, this.mHandler).start();
            return;
        }
        if (Const.Config.DUMMY_WIFI_CONNECT_TYPE == 2) {
            Intent i = new Intent();
            if (Build.VERSION.SDK_INT >= 11) {
                i.setClassName("com.android.settings", "com.android.settings.wifi.WifiPickerActivity");
                Logger.d(TAG, "is Tab101 " + Utils.isTab101());
            } else {
                i.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                Logger.d(TAG, "is Tab101 " + Utils.isTab101());
            }
            startActivityForResult(i, Const.ActivityResultId.ID_WIFI_SETTING);
            return;
        }
        this.mWifiDialog = new WifiDialog(this, this.mHandler);
        this.mWifiDialog.setCancelable(false);
        this.mWifiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.samsungimaging.filesharing.FileSharing.33
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                if (FileSharing.mPreconnectedSsid == null) {
                    FileSharing.mPreconnectedSsid = FileSharing.this.mWifiDialog.selectedSr.SSID;
                    WifiDialog wifiDialog = FileSharing.this.mWifiDialog;
                    FileSharing fileSharing = FileSharing.this;
                    ScanResult scanResult = FileSharing.this.mWifiDialog.selectedSr;
                    fileSharing.mSelectedSr = scanResult;
                    wifiDialog.connect(scanResult);
                }
                FileSharing.this.doAfterUserWifiSetting();
            }
        });
        this.mWifiDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWifiConnector() {
        Logger.d(TAG, "WifiConnector!!!!!!!!!!!! stop");
        if (Const.Config.DUMMY_WIFI_CONNECT_TYPE == 1) {
            if (this.mWifiConnector != null) {
                this.mWifiConnector.stop();
                return;
            }
            return;
        }
        int i = Const.Config.DUMMY_WIFI_CONNECT_TYPE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAfterUserWifiSetting() {
        if (mWifimanager.isWifiEnabled()) {
            String ssid = mPreconnectedSsid;
            if (Utils.supportDSCPrefix(ssid)) {
                Logger.d(TAG, "1!!!!!!!!!!!!!");
                new Thread(new Runnable() { // from class: com.samsungimaging.filesharing.FileSharing.34
                    @Override // java.lang.Runnable
                    public void run() {
                        Utils.noop(1000L);
                        FileSharing.this.mHandler.sendMessage(FileSharing.this.mHandler.obtainMessage(1));
                        Utils.noop(100L);
                        FileSharing.this.mHandler.sendMessage(FileSharing.this.mHandler.obtainMessage(3));
                    }
                }).start();
                return;
            } else {
                Logger.d(TAG, "2!!!!!!!!!!!!!");
                showDialog(12);
                new Thread(new Runnable() { // from class: com.samsungimaging.filesharing.FileSharing.35
                    @Override // java.lang.Runnable
                    public void run() {
                        Utils.noop(1000L);
                        FileSharing.this.appClose();
                    }
                }).start();
                return;
            }
        }
        Logger.d(TAG, "3!!!!!!!!!!!!!");
        showDialog(12);
        new Thread(new Runnable() { // from class: com.samsungimaging.filesharing.FileSharing.36
            @Override // java.lang.Runnable
            public void run() {
                Utils.noop(1000L);
                FileSharing.this.appClose();
            }
        }).start();
    }

    private boolean isConnectCamAP() {
        if (!mWifimanager.isWifiEnabled()) {
            return false;
        }
        WifiInfo wifiInfo = mWifimanager.getConnectionInfo();
        String bssid = wifiInfo.getSSID();
        if (bssid == null) {
            return false;
        }
        boolean bRet = Utils.supportDSCPrefix(bssid);
        return bRet;
    }

    private void registerReceiver() {
        IntentFilter sdcardFilter = new IntentFilter("android.intent.action.MEDIA_REMOVED");
        sdcardFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        sdcardFilter.addDataScheme("file");
        registerReceiver(this.sdcardStateChangedReceiver, sdcardFilter);
        if (!bNoTelephonyDevice) {
            MyPhoneStateListener phoneListener = new MyPhoneStateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
            telephonyManager.listen(phoneListener, 32);
        }
    }

    private void unregisterReceiver() {
        try {
            unregisterReceiver(this.sdcardStateChangedReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDisconnectReceiver() {
        this.mWifiStateChangedReceiverForAppClose = new WifiStateChangedReceiverForAppClose(this, null);
        IntentFilter filter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        registerReceiver(this.mWifiStateChangedReceiverForAppClose, filter);
    }

    private void unregisterDisconnectReceiver() {
        if (this.mWifiStateChangedReceiverForAppClose != null) {
            unregisterReceiver(this.mWifiStateChangedReceiverForAppClose);
        }
    }

    private void registerSettingCloseReceiver() {
        this.mWifiStateChangedReceiverForSettingClose = new WifiStateChangedReceiverForSettingClose(this, null);
        IntentFilter filter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        registerReceiver(this.mWifiStateChangedReceiverForSettingClose, filter);
    }

    private void unregisterSettingCloseReceiver() {
        if (this.mWifiStateChangedReceiverForSettingClose != null) {
            unregisterReceiver(this.mWifiStateChangedReceiverForSettingClose);
        }
    }

    private class SdcardStateChangedReceiver extends BroadcastReceiver {
        private SdcardStateChangedReceiver() {
        }

        /* synthetic */ SdcardStateChangedReceiver(FileSharing fileSharing, SdcardStateChangedReceiver sdcardStateChangedReceiver) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Logger.e(FileSharing.TAG, "Sdcard onReceive() -- " + intent.getAction());
            if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED") || intent.getAction().equals("android.intent.action.MEDIA_SHARED") || intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTABLE")) {
                Message newMsg = new Message();
                newMsg.what = 22;
                FileSharing.this.mHandler.sendMessageDelayed(newMsg, 1000L);
            }
        }
    }

    private class WifiStateChangedReceiverForAppClose extends BroadcastReceiver {
        private static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;

        static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState() {
            int[] iArr = $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;
            if (iArr == null) {
                iArr = new int[NetworkInfo.DetailedState.values().length];
                try {
                    iArr[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.BLOCKED.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTING.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.FAILED.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.IDLE.ordinal()] = 8;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 9;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SUSPENDED.ordinal()] = 11;
                } catch (NoSuchFieldError e11) {
                }
                $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = iArr;
            }
            return iArr;
        }

        private WifiStateChangedReceiverForAppClose() {
        }

        /* synthetic */ WifiStateChangedReceiverForAppClose(FileSharing fileSharing, WifiStateChangedReceiverForAppClose wifiStateChangedReceiverForAppClose) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra("connected", false);
                if (!connected) {
                    FileSharing.this.mHandler.sendEmptyMessage(33);
                }
                return;
            }
            if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo.DetailedState state = info.getDetailedState();
                Logger.GG("NetworkInfo.DetailedState = " + state.name());
                switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[state.ordinal()]) {
                    case 5:
                        FileSharing.this.mHandler.sendEmptyMessage(33);
                        break;
                }
            }
        }
    }

    public void systemExit() {
        Logger.e("LiveShutter", "systemExit is start!!!! exit is start");
        restoreInitialState(true);
        if (mWifimanager != null) {
            mWifimanager.disconnect();
        }
        System.exit(1);
    }

    public void saveInitialState() {
        try {
            this.bConnectedMobileData = getMobileDataEnabled(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (this.bConnectedMobileData) {
        }
    }

    public void restoreInitialState(boolean bDisconn) {
        if (this.mWifiLock != null) {
            try {
                this.mWifiLock.release();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        Logger.GG("Restoring initial connection state...");
        if (this.bConnectedMobileData) {
            if (this.mWifiConnector != null && bDisconn) {
                this.mWifiConnector.disconnectNetwork();
                return;
            }
            return;
        }
        mWifimanager.setWifiEnabled(true);
    }

    public boolean getMobileDataEnabled(Context context) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        if (!bNoTelephonyDevice) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
            if (tm.getDataState() == 2) {
                return true;
            }
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        Class<?> cmClass = Class.forName(cm.getClass().getName());
        Field iConnectivityManagerField = cmClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        Object iConnectivityManager = iConnectivityManagerField.get(cm);
        Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        Method getMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled", Boolean.TYPE);
        getMobileDataEnabledMethod.setAccessible(true);
        return ((Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager, null)).booleanValue();
    }

    public void setMobileDataEnabled_2(Context context, boolean enabled) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        Class<?> tmClass = Class.forName(tm.getClass().getName());
        Method getITelephonyMethod = tmClass.getDeclaredMethod("getITelephony", new Class[0]);
        getITelephonyMethod.setAccessible(true);
        Object ITelephonyStub = getITelephonyMethod.invoke(tm, new Object[0]);
        Class<?> ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
        Method method = ITelephonyClass.getDeclaredMethod(enabled ? "enableDataConnectivity" : "disableDataConnectivity", new Class[0]);
        method.setAccessible(true);
        method.invoke(ITelephonyStub, new Object[0]);
    }

    private class WifiStateChangedReceiverForSettingClose extends BroadcastReceiver {
        private static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;

        static /* synthetic */ int[] $SWITCH_TABLE$android$net$NetworkInfo$DetailedState() {
            int[] iArr = $SWITCH_TABLE$android$net$NetworkInfo$DetailedState;
            if (iArr == null) {
                iArr = new int[NetworkInfo.DetailedState.values().length];
                try {
                    iArr[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.BLOCKED.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTING.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.FAILED.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.IDLE.ordinal()] = 8;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 9;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SUSPENDED.ordinal()] = 11;
                } catch (NoSuchFieldError e11) {
                }
                $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = iArr;
            }
            return iArr;
        }

        private WifiStateChangedReceiverForSettingClose() {
        }

        /* synthetic */ WifiStateChangedReceiverForSettingClose(FileSharing fileSharing, WifiStateChangedReceiverForSettingClose wifiStateChangedReceiverForSettingClose) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo.DetailedState state = info.getDetailedState();
                switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[state.ordinal()]) {
                    case 3:
                        if (Utils.supportDSCPrefix(FileSharing.mPreconnectedSsid) || (FileSharing.this.mSelectedSr != null && Utils.supportDSCPrefix(FileSharing.this.mSelectedSr.SSID))) {
                            FileSharing.this.finishActivity(Const.ActivityResultId.ID_WIFI_SETTING);
                        }
                        break;
                }
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
        this.mTextView.setText(String.valueOf(this.mContainerAdapter.getCheckedItemCount()) + "   " + getString(R.string.ml_item_selected));
        if (this.mContainerAdapter.getCheckedItemCount() > 0) {
            setEnabledCopyButton(true);
        } else {
            setEnabledCopyButton(false);
        }
    }

    public void setMaxCurrentFileSize(long max) {
        if (this.currentFileSizeProgressBar != null) {
            this.currentFileSizeProgressBar.setMax(1000);
            if (this.fileSize != null) {
                this.fileSize.setText(String.valueOf(Utils.formatSize(this.mContentListManager.getDownloadedImageSize())) + "/" + Utils.formatSize(Long.parseLong(mContainerList.get(this.mContentListManager.getCurrentSavingFileIndex()).getSize())));
            }
        }
    }

    public int getMaxFileSize() {
        return this.currentFileSizeProgressBar.getMax();
    }

    public void setProgressCurrentFileSize(long progress) {
        if (this.currentFileSizeProgressBar != null) {
            this.currentFileSizeProgressBar.setProgress((int) ((1000 * progress) / Long.parseLong(mContainerList.get(this.mContentListManager.getCurrentSavingFileIndex()).getSize())));
            if (this.fileSize != null) {
                this.fileSize.setText(String.valueOf(Utils.formatSize(this.mContentListManager.getDownloadedImageSize())) + "/" + Utils.formatSize(Long.parseLong(mContainerList.get(this.mContentListManager.getCurrentSavingFileIndex()).getSize())));
            }
        }
    }

    public int getProgressFileSize() {
        return this.currentFileSizeProgressBar.getProgress();
    }

    public void setCurrentFileName(String name) {
        this.fileName.setText(name);
    }

    public String getCurrentFileName() {
        return (String) this.fileName.getText();
    }

    public void setMaxTotalSize(long max) {
        this.mMaxTotalSize = max;
        if (this.totalFilesSizeProgressBar != null) {
            this.totalFilesSizeProgressBar.setMax(1000);
        }
    }

    public long getMaxTotalSize() {
        return this.mMaxTotalSize;
    }

    public void setMaxTotalCount(int max) {
        this.mMaxTotalFileCount = max;
        if (this.totalCount != null) {
            this.totalCount.setText("Total[" + getCurrentFileIndex() + "/" + getMaxTotalFileCount() + "]");
        }
    }

    public int getMaxTotalFileCount() {
        return this.mMaxTotalFileCount;
    }

    public void setProgressTotalSize(long progress) {
        this.mProgressTotalsize = progress;
        if (this.totalFilesSizeProgressBar != null) {
            this.totalFilesSizeProgressBar.setProgress((int) ((1000 * progress) / getMaxTotalSize()));
            if (this.totalSize != null) {
                this.totalSize.setText(String.valueOf(Utils.formatSize(this.mProgressTotalsize)) + "/" + Utils.formatSize(getMaxTotalSize()));
            }
        }
    }

    public long getProgressTotalSize() {
        return this.mProgressTotalsize;
    }

    public int getCurrentFileIndex() {
        return this.mCurrentFileIndex;
    }

    public void setCurrentFileIndex(int currentFileIndex) {
        if (currentFileIndex <= getMaxTotalFileCount()) {
            this.mCurrentFileIndex = currentFileIndex;
        } else {
            this.mCurrentFileIndex = getMaxTotalFileCount();
        }
        if (this.totalCount != null) {
            this.totalCount.setText("Total[" + getCurrentFileIndex() + "/" + getMaxTotalFileCount() + "]");
        }
    }

    public void getChangeText() {
        Logger.d(TAG, "postDelayed rr " + this.mContainerAdapter.getCheckedItemCount());
        if (this.mContainerAdapter.getCheckedItemCount() > 0) {
            setEnabledCopyButton(true);
        } else {
            setEnabledCopyButton(false);
        }
        this.mTextView.setText(String.valueOf(this.mContainerAdapter.getCheckedItemCount()) + "   " + getString(R.string.ml_item_selected));
    }

    private class UnsubscribeTask extends AsyncTask<Device, Void, Void> {
        private UnsubscribeTask() {
        }

        /* synthetic */ UnsubscribeTask(FileSharing fileSharing, UnsubscribeTask unsubscribeTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            FileSharing.this.showDialog(12);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Device... params) {
            if (FileSharing.mControlPoint != null && FileSharing.mPreconnectedSsid != null && FileSharing.mPreconnectedSsid.contains("QF30")) {
                if (params[0] != null) {
                    FileSharing.mControlPoint.unsubscribe(params[0]);
                }
                FileSharing.mControlPoint.finalize();
                return null;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            FileSharing.this.dismissProgressDialog();
            if (FileSharing.mControlPoint != null) {
                FileSharing.mControlPoint.bDisconnect = true;
                Utils.noop(1100L);
            }
            FileSharing.this.restoreInitialState(true);
            FileSharing.this.finish();
        }
    }

    public void selectAllItem() {
        for (int i = 0; i < this.mContainerAdapter.getCount(); i++) {
            if (this.mContainerAdapter.getItem(i).getItemState() == 1) {
                this.mContainerAdapter.getItem(i).setItemState(2);
            }
        }
        this.mContainerAdapter.notifyDataSetChanged();
        onChangedSelectItemCount();
    }
}

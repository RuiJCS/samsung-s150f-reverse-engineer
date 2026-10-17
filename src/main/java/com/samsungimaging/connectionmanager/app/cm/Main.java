package com.samsungimaging.connectionmanager.app.cm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.connector.LastRequestedCameraInfo;
import com.samsungimaging.connectionmanager.app.cm.modemanager.CMModeManager;
import com.samsungimaging.connectionmanager.app.cm.modemanager.ModeServer;
import com.samsungimaging.connectionmanager.app.cm.notimanager.CMNotificationManager;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.pullservice.Const;
import com.samsungimaging.connectionmanager.dialog.CMTestModeDialog;
import com.samsungimaging.connectionmanager.dialog.CMTestModePassWordDialog;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.dialog.InitGuideDialog;
import com.samsungimaging.connectionmanager.dialog.InitHelpDialog;
import com.samsungimaging.connectionmanager.dialog.IntroDialog;
import com.samsungimaging.connectionmanager.dialog.IntroNoticeDialog;
import com.samsungimaging.connectionmanager.dialog.ModeClientDialog;
import com.samsungimaging.connectionmanager.dialog.NotSupportedDialog;
import com.samsungimaging.connectionmanager.dialog.NowAppClosingProgressDialog;
import com.samsungimaging.connectionmanager.dialog.RefusalDialog;
import com.samsungimaging.connectionmanager.dialog.SearchAPDialog;
import com.samsungimaging.connectionmanager.dialog.VerifyingPoorLinkDialog;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class Main extends BaseGalleryActivity implements View.OnClickListener {
    private static final int DIALOG_ID_INIT_GUIDE = 1003;
    private static final int DIALOG_ID_INIT_HELP = 1004;
    private static final int DIALOG_ID_INTRO_GUIDE = 1002;
    private static final int DIALOG_ID_INTRO_NOTICE = 1010;
    private static final int DIALOG_ID_MODE_CLIENT = 1001;
    private static final int DIALOG_ID_NOT_SUPPORTED_CAMERA = 1007;
    private static final int DIALOG_ID_NOW_APP_CLOSING = 1008;
    private static final int DIALOG_ID_REFUSAL = 1006;
    private static final int DIALOG_ID_SEARCH_AP = 1000;
    public static final int DIALOG_ID_TEST_MODE = 1011;
    private static final int DIALOG_ID_TEST_MODE_PASSWORD = 1009;
    private static final int DIALOG_ID_VERIFYING_POOR_LINK = 1005;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String packageNameACM = "com.samsungimaging.connectionmanager";
    private static Main s_obj = null;
    private TextView mDescription = null;
    private Button mConnectButton = null;
    private boolean mSSID_Fixed_Mode_For_Test = false;
    final ArrayList<String> deniedPermissions = new ArrayList<>();
    private Handler mTimerHandler = null;
    private Handler mTimerHandlerForInitGuideDialog = null;
    private Handler mNFCTaggingTimerHandler = null;
    private Thread mFinishThread = null;
    private BroadcastReceiver mNetworkReciever = new BroadcastReceiver() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (CMConstants.INTENT_FROM_CM.equals(intent.getAction())) {
                Trace.d(CMConstants.TAG_NAME, "INTENT_FROM_CM~");
                String extraInfo = null;
                Bundle bundle = null;
                int extraInfo2 = -1;
                if (intent != null && intent.getExtras() != null) {
                    extraInfo = intent.getExtras().getString(CMConstants.EXTRA_KEY_FROM_CM);
                    extraInfo2 = intent.getExtras().getInt(CMConstants.EXTRA_KEY2_FROM_CM, -1);
                    bundle = intent.getExtras().getBundle(CMConstants.EXTRA_KEY3_FROM_CM);
                    Trace.d(CMConstants.TAG_NAME, "extraInfo = " + extraInfo + ", extraInfo2 = " + extraInfo2 + ", bundle = " + bundle);
                }
                if (extraInfo != null) {
                    if (!extraInfo.equals(CMConstants.EXTRA_VALUE_NETWORKCHANGE_WIFIDISCONNECTED)) {
                        if (!extraInfo.equals(CMConstants.EXTRA_VALUE_RUN_MODEMANAGER)) {
                            if (!extraInfo.equals(CMConstants.EXTRA_VALUE_SHOW_SEARCHAPDIALOG)) {
                                if (!extraInfo.equals(CMConstants.EXTRA_VALUE_SHOW_HELPDIALOG)) {
                                    if (!extraInfo.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_INTERNET_SERVICE_MENU_IN_SETTINGS)) {
                                        if (!extraInfo.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR) && !extraInfo.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR)) {
                                            if (!extraInfo.equals(CMConstants.EXTRA_VALUE_SHOW_NOT_SUPPORTED_DIALOG)) {
                                                if (!extraInfo.equals(CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_MODECLIENT_NOTRESPONSE_OR_CANCELED)) {
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES)) {
                                                        Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_START_WIFI_SCAN_TIMER_FOR_MIMUTES, extraInfo2 = " + extraInfo2);
                                                        if (extraInfo2 > 0) {
                                                            Main.this.startWifiScanTimer(extraInfo2);
                                                            return;
                                                        } else {
                                                            Main.this.startWifiScanTimer(CMConstants.WIFI_SCAN_TIMER);
                                                            return;
                                                        }
                                                    }
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_STOP_WIFI_SCAN_TIMER_FOR_MIMUTES)) {
                                                        Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_STOP_WIFI_SCAN_TIMER_FOR_MIMUTES");
                                                        Main.this.stopWifiScanTimer();
                                                        return;
                                                    }
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_SHOW_STATUS_BAR)) {
                                                        Main.this.showStatusBar();
                                                        return;
                                                    }
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_STOP_NFC_TAGGING_TIMER)) {
                                                        Main.this.stopNFCTaggingTimer();
                                                        return;
                                                    }
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_APP_CLOSE_SENDING_BYEBYE)) {
                                                        Main.this.appcloseSendingByeBye();
                                                        return;
                                                    }
                                                    if (extraInfo.equals(CMConstants.EXTRA_VALUE_NEED_PASSWORD)) {
                                                        Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, CMService.IS_WIFI_CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                                                        if (!CMService.IS_WIFI_CONNECTED) {
                                                            CustomDialog search_dialog = (CustomDialog) Main.this.mDialogList.get(1000);
                                                            if (search_dialog != null && search_dialog.isShowing()) {
                                                                search_dialog.dismiss();
                                                            }
                                                            if (bundle != null) {
                                                                String[] security_info = (String[]) bundle.getCharSequenceArray(BaseGalleryActivity.BUNDLE_KEY_DATA);
                                                                Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, security_info[0] = " + security_info[0]);
                                                                Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, security_info[1] = " + security_info[1]);
                                                                Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, security_info[2] = " + security_info[2]);
                                                                ScanResult last_sr = LastRequestedCameraInfo.getInstance().getLastRequestedCameraInfo();
                                                                if (last_sr == null) {
                                                                    Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, last_sr = " + last_sr);
                                                                } else {
                                                                    Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, last_sr.SSID = " + last_sr.SSID);
                                                                }
                                                                if (last_sr == null || (last_sr != null && last_sr.SSID.contains(security_info[0]))) {
                                                                    CustomDialog password = (CustomDialog) Main.this.mDialogList.get(6);
                                                                    if (password == null || !password.isShowing()) {
                                                                        Main.this.showDialog(6, bundle);
                                                                        return;
                                                                    } else {
                                                                        Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, password already showing!");
                                                                        return;
                                                                    }
                                                                }
                                                                Trace.d(CMConstants.TAG_NAME, "EXTRA_VALUE_NEED_PASSWORD, other SSID selected.");
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    return;
                                                }
                                                Main.this.wifiDisconnected();
                                                Toast.makeText(Main.this, "There is no response from the Camera. Please retry again", 0).show();
                                                return;
                                            }
                                            Main.this.showNOTSupportedDialog();
                                            return;
                                        }
                                        Main.this.wifiDisconnected();
                                        Main.this.showRefusalDialog(extraInfo);
                                        return;
                                    }
                                    Main.this.showVerifyingPoorLinkDialog();
                                    return;
                                }
                                Main.this.showHelpDialog();
                                return;
                            }
                            Main.this.showWifiManagerDialog();
                            return;
                        }
                        Main.this.showModeManagerDialog();
                        return;
                    }
                    Main.this.wifiDisconnected();
                }
            }
        }
    };
    private IModeClient mIModeClientForSendingByebye = new IModeClient() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.2
        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
        public void runSubApplication(int msg) {
            Trace.d(CMConstants.TAG_NAME, "CMService, ModeClient Sending byebye...response runSubApplication, msg = " + msg);
            CMUtil.disableConnectedCamera(CMService.mWifiManager);
            CMService.IS_WIFI_CONNECTED = false;
        }

        @Override // com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient
        public void runByebye() {
            Trace.d(CMConstants.TAG_NAME, "CMService, ModeClient Sending byebye...response 200OK byebye.");
            CMUtil.disableConnectedCamera(CMService.mWifiManager);
            CMService.IS_WIFI_CONNECTED = false;
        }
    };

    public static synchronized Main getInstance() {
        return s_obj;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        CMSharedPreferenceUtil.put(getApplicationContext(), CMConstants.TESTMODE, String.valueOf(this.mSSID_Fixed_Mode_For_Test));
        checkModeServerPort();
        super.onCreate(savedInstanceState);
        Trace.d(CMConstants.TAG_NAME, "onCreate");
        s_obj = this;
        if (Build.VERSION.SDK_INT >= 23) {
            checkRunTimePermission();
        } else {
            callMethod();
        }
    }

    public void callMethod() {
        CMService.mWifiManager = (WifiManager) getSystemService("wifi");
        CMService.mConnectivityManager = (ConnectivityManager) getSystemService("connectivity");
        CMService.mContext = getApplicationContext();
        Intent parsedIntent = getIntent();
        if (parsedIntent != null) {
            String action = parsedIntent.getAction();
            Trace.d(CMConstants.TAG_NAME, "onCreate, getAction : " + action);
            if ("android.nfc.action.NDEF_DISCOVERED".equals(action)) {
                Trace.d(CMConstants.TAG_NAME, "onCreate, NDEF_DISCOVERED");
                processTag(parsedIntent);
                createContent();
                showWifiManagerDialog();
                return;
            }
            showInitDialog();
        }
    }

    private void checkRunTimePermission() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.add("android.permission.ACCESS_FINE_LOCATION");
        listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        listPermissionsNeeded.add("android.permission.READ_PHONE_STATE");
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            this.deniedPermissions.add("android.permission.ACCESS_FINE_LOCATION");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            this.deniedPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
            this.deniedPermissions.add("android.permission.READ_PHONE_STATE");
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            callMethod();
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0099  */
    /* JADX WARN: Code duplicated, block: B:28:0x00b7  */
    @Override // android.app.Activity
    @SuppressLint({"Override"})
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                SharedPreferences mPreferences = getSharedPreferences("configuration", 0);
                boolean firstTimeAccount = mPreferences.getBoolean("firstTimeAccount", true);
                Map<String, Integer> perms = new HashMap<>();
                perms.put("android.permission.ACCESS_FINE_LOCATION", 0);
                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", 0);
                perms.put("android.permission.READ_PHONE_STATE", 0);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                    }
                    if (perms.get("android.permission.ACCESS_FINE_LOCATION").intValue() == 0 && perms.get("android.permission.WRITE_EXTERNAL_STORAGE").intValue() == 0 && perms.get("android.permission.READ_PHONE_STATE").intValue() == 0) {
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putBoolean("firstTimeAccount", true);
                        editor.commit();
                        callMethod();
                    } else if (!firstTimeAccount) {
                        showDialog(Const.MsgBoxId.MSGBOX_GOTO_SETTINGS);
                    } else {
                        boolean showRationale = false;
                        for (String permission : this.deniedPermissions) {
                            showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (showRationale) {
                                if (!showRationale) {
                                    SharedPreferences.Editor editor2 = mPreferences.edit();
                                    editor2.putBoolean("firstTimeAccount", false);
                                    editor2.commit();
                                    finish();
                                } else {
                                    finish();
                                }
                                break;
                            }
                        }
                        if (!showRationale) {
                            SharedPreferences.Editor editor3 = mPreferences.edit();
                            editor3.putBoolean("firstTimeAccount", false);
                            editor3.commit();
                            finish();
                        } else {
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @SuppressLint({"NewApi"})
    public static Drawable getPermissionGroupDrawable(Context context, String requestedPermission) {
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return null;
            }
            PermissionInfo pi = pm.getPermissionInfo(requestedPermission, 4096);
            PermissionGroupInfo pgi = pm.getPermissionGroupInfo(pi.group, 4096);
            return context.getResources().getDrawable(pgi.icon, null);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getPermissionGroupString(Context context, String requestedPermission) {
        String label = null;
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return null;
            }
            PermissionInfo pi = pm.getPermissionInfo(requestedPermission, 4096);
            PermissionGroupInfo pgi = pm.getPermissionGroupInfo(pi.group, 4096);
            label = context.getResources().getString(pgi.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return label;
    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        List<Drawable> imageId;
        private LayoutInflater inflater;
        List<String> result;
        View rowView;

        public CustomAdapter(Main mainActivity, List<String> listPermission, List<Drawable> permissionDrawableList) {
            this.inflater = null;
            this.result = listPermission;
            this.context = mainActivity;
            this.imageId = permissionDrawableList;
            this.inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.result.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            ImageView img;
            TextView tv;

            public ViewHolder() {
            }
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.simple_list_item_permission, (ViewGroup) null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.text_permission_needed);
                holder.img = (ImageView) convertView.findViewById(R.id.image_permission_needed);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(this.result.get(position));
            holder.img.setImageDrawable(this.imageId.get(position));
            return convertView;
        }
    }

    private void checkModeServerPort() {
        ModeServer.mServerPort = CMSharedPreferenceUtil.getInteger(this, CMConstants.MODESERVER_PORT, 7789);
        Trace.d(CMConstants.TAG_NAME, "checkModeServerPort, ModeServer.mServerPort : " + ModeServer.mServerPort);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createContent() {
        setContentView(R.layout.activity_status_type_button);
        this.mGallery = (GalleryFragment) getFragmentManager().findFragmentById(R.id.fragment_gallery);
        this.mGallery.setDiskCacheDir(GalleryFragment.DISK_CACHE_DIR_THUMBNAIL, GalleryFragment.DISK_CACHE_DIR_VIEWER);
        this.mGallery.setOnItemClickListener(this);
        this.mGallery.setOnItemLongClickListener(this);
        this.mGallery.setQuery(getQuery());
        this.mActionBar.setTitle(R.string.download_files);
        this.mActionBar.show();
        this.mDescription = (TextView) findViewById(android.R.id.text1);
        this.mDescription.setText(R.string.not_connected);
        this.mConnectButton = (Button) findViewById(android.R.id.button1);
        this.mConnectButton.setText(R.string.connect_camera);
        this.mConnectButton.setOnClickListener(this);
        registerLocalBroadcastReceiver();
        cmStart();
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
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        Dialog dialog;
        switch (id) {
            case 1000:
                dialog = new SearchAPDialog(this);
                break;
            case 1001:
                dialog = new ModeClientDialog(this);
                break;
            case 1002:
                dialog = new IntroDialog(this);
                break;
            case 1003:
                dialog = new InitGuideDialog(this);
                break;
            case 1004:
                dialog = new InitHelpDialog(this);
                break;
            case 1005:
                dialog = new VerifyingPoorLinkDialog(this);
                break;
            case 1006:
                dialog = new RefusalDialog(this);
                ((CustomDialog) dialog).setTag(bundle.getString(BaseGalleryActivity.BUNDLE_KEY_DATA));
                ((CustomDialog) dialog).setNeutralButton(R.string.cm_confirm, (DialogInterface.OnClickListener) null);
                break;
            case 1007:
                dialog = new NotSupportedDialog(this);
                break;
            case 1008:
                dialog = new NowAppClosingProgressDialog(this);
                break;
            case 1009:
                dialog = new CMTestModePassWordDialog(this, this);
                break;
            case 1010:
                dialog = new IntroNoticeDialog(this);
                ((CustomDialog) dialog).setNeutralButton(R.string.close, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        dialog2.dismiss();
                        Main.this.showInitNFCDialog();
                    }
                });
                break;
            case 1011:
                dialog = new CMTestModeDialog(this, android.R.style.Theme.Light.NoTitleBar);
                break;
            case Const.MsgBoxId.MSGBOX_GOTO_SETTINGS /* 1113 */:
                CustomDialog customDialog = new CustomDialog(this);
                customDialog.setMessage(getString(R.string.MSS_UNABLE_TO_OPEN_PS_TAP_SETTINGS_AND_GO_TO_APP_INFO_MSG, new Object[]{getString(R.string.app_name)}));
                List<String> permissionStringList = new ArrayList<>();
                List<Drawable> permissionDrawableList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                    permissionStringList.add(getPermissionGroupString(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION"));
                    permissionDrawableList.add(getPermissionGroupDrawable(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION"));
                }
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
                    permissionStringList.add(getPermissionGroupString(getApplicationContext(), "android.permission.READ_PHONE_STATE"));
                    permissionDrawableList.add(getPermissionGroupDrawable(getApplicationContext(), "android.permission.READ_PHONE_STATE"));
                }
                if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    permissionStringList.add(getPermissionGroupString(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE"));
                    permissionDrawableList.add(getPermissionGroupDrawable(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE"));
                }
                customDialog.setAdapter(new CustomAdapter(this, permissionStringList, permissionDrawableList));
                customDialog.setPositiveButton(getResources().getString(R.string.menu_txt_settings), new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:com.samsungimaging.connectionmanager"));
                            Main.this.startActivity(intent);
                            Main.this.dismissDialog(Const.MsgBoxId.MSGBOX_GOTO_SETTINGS);
                            Main.this.finish();
                        } catch (ActivityNotFoundException e) {
                            Main.this.startActivity(new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS"));
                            Main.this.dismissDialog(Const.MsgBoxId.MSGBOX_GOTO_SETTINGS);
                        }
                    }
                });
                customDialog.setNegativeButton(R.string.psm_popup_button_cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        Main.this.dismissDialog(Const.MsgBoxId.MSGBOX_GOTO_SETTINGS);
                        Main.this.finish();
                    }
                });
                customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.6
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        if (event.getKeyCode() == 84) {
                            return true;
                        }
                        if (event.getKeyCode() == 4) {
                            Main.this.dismissDialog(Const.MsgBoxId.MSGBOX_GOTO_SETTINGS);
                            Main.this.finish();
                            return true;
                        }
                        return false;
                    }
                });
                return customDialog;
            default:
                dialog = super.onCreateDialog(id, bundle);
                break;
        }
        return dialog;
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Trace.d(CMConstants.TAG_NAME, "onResume");
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.d(CMConstants.TAG_NAME, "onDestroy");
        super.onDestroy();
        CMNotificationManager.getInstance().cancelAllNoti();
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        if (CMUtil.isForceClose(activityManager)) {
            SystemClock.sleep(3000L);
        }
        CMService.mWifiManager = (WifiManager) getSystemService("wifi");
        CMService.mWifiManager.disconnect();
        killProcess();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (isBackPressAvailable()) {
            Trace.d(CMConstants.TAG_NAME, "onBackPressed(), ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE);
            if (!CMService.getInstance().isSubAppAlive()) {
                CMService.getInstance().finishSafe();
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        showWifiManagerDialog();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        Trace.d(CMConstants.TAG_NAME, "onNewIntent, intent.getAction() = " + intent.getAction());
        if ("android.nfc.action.NDEF_DISCOVERED".equals(intent.getAction())) {
            processTag(intent);
            showWifiManagerDialog();
            return;
        }
        Trace.d(CMConstants.TAG_NAME, "onNewIntent, CMService.IS_WIFI_CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", CMService.ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE);
        if (CMService.IS_WIFI_CONNECTED && CMService.getInstance().isSubAppAlive()) {
            showWifiManagerDialog();
        }
    }

    private void processTag(Intent parsedIntent) {
        String act = parsedIntent.getAction();
        Trace.d(CMConstants.TAG_NAME, "processTag(), act = " + act);
        if ("android.nfc.action.NDEF_DISCOVERED".equals(act)) {
            Trace.d(CMConstants.TAG_NAME, "processTag(), ACTION_NDEF_DISCOVERED");
            NdefMessage[] messages = getNdefMessages(parsedIntent);
            Trace.d(CMConstants.TAG_NAME, "processTag(), messages.length = " + messages.length);
            int outerCnt = messages.length;
            String[][] ndefMsgArray = new String[outerCnt][];
            for (int i = 0; i < outerCnt; i++) {
                int innerCnt = messages[i].getRecords().length;
                ndefMsgArray[i] = new String[innerCnt];
                for (int j = 0; j < innerCnt; j++) {
                    byte[] payload_record_byteArr = messages[i].getRecords()[j].getPayload();
                    ndefMsgArray[i][j] = new String(payload_record_byteArr);
                    Trace.d(CMConstants.TAG_NAME, "processTag(), payload_record_str[" + j + "] : " + ndefMsgArray[i][j]);
                }
            }
            String nfcTagSSID = ndefMsgArray[0][0].trim().toUpperCase();
            DatabaseManager.putMaxCountForAP(CMService.mContext, nfcTagSSID);
            CMInfo.getInstance().setIsNFCLaunch(true, nfcTagSSID);
            WifiInfo info = CMService.mWifiManager.getConnectionInfo();
            if (CMUtil.supportDSCPrefix(info.getSSID()) && info.getSSID().indexOf(nfcTagSSID) < 0 && CMService.getInstance().isSubAppAlive()) {
                Trace.d(CMConstants.TAG_NAME, "processTag(), current SSID : " + info.getSSID() + ", new SSID : " + nfcTagSSID);
                CMSharedPreferenceUtil.put(this, CMConstants.PREF_NFC_SSID, nfcTagSSID);
                CMUtil.finishSubServicew(CMService.mContext);
                CMUtil.disableConnectedCamera(CMService.mWifiManager);
                CMService.mWifiManager.disconnect();
                CMService.IS_WIFI_CONNECTED = false;
                CMService.ACTIVE_SERVICE = 0;
            }
        }
    }

    private void startNFCTaggingTimer() {
        if (this.mNFCTaggingTimerHandler == null) {
            this.mNFCTaggingTimerHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.7
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    CMService.getInstance().NFCConnectionTryCancel();
                }
            };
            this.mNFCTaggingTimerHandler.sendEmptyMessageDelayed(0, 20000L);
        } else {
            if (this.mNFCTaggingTimerHandler.hasMessages(0)) {
                this.mNFCTaggingTimerHandler.removeMessages(0);
            }
            this.mNFCTaggingTimerHandler.sendEmptyMessageDelayed(0, 20000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopNFCTaggingTimer() {
        if (this.mNFCTaggingTimerHandler != null) {
            if (this.mNFCTaggingTimerHandler.hasMessages(0)) {
                this.mNFCTaggingTimerHandler.removeMessages(0);
            }
            this.mNFCTaggingTimerHandler = null;
        }
    }

    private NdefMessage[] getNdefMessages(Intent intent) {
        Trace.d(CMConstants.TAG_NAME, "getNdefMessages()");
        String action = intent.getAction();
        if (!"android.nfc.action.TAG_DISCOVERED".equals(action) && !"android.nfc.action.NDEF_DISCOVERED".equals(action)) {
            return null;
        }
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
        if (rawMsgs != null) {
            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
            return msgs;
        }
        byte[] empty = new byte[0];
        NdefRecord record = new NdefRecord((short) 5, empty, empty, empty);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
        return new NdefMessage[]{msg};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiScanTimer(int time) {
        CMService.getInstance().wifiScanStart();
        Trace.d(CMConstants.TAG_NAME, "startWifiScanTimer, WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", time = " + time);
        if (time == 600000) {
            CMService.AUTOSHARE_AUTOSEARCH = true;
        } else {
            CMService.AUTOSHARE_AUTOSEARCH = false;
        }
        changeMainDescription();
        if (this.mTimerHandler == null) {
            this.mTimerHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.8
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    CMService.getInstance().wifiScanStop();
                    CMService.AUTOSHARE_AUTOSEARCH = false;
                    Main.this.changeMainDescription();
                    CustomDialog searchDialog = (CustomDialog) Main.this.mDialogList.get(1000);
                    if (searchDialog != null && searchDialog.isShowing()) {
                        Trace.d(CMConstants.TAG_NAME, "Main, startWifiScanTimer, background WIFI scan stop and Search dialog dismiss.");
                        searchDialog.dismiss();
                    }
                }
            };
            this.mTimerHandler.sendEmptyMessageDelayed(0, time);
        } else {
            if (this.mTimerHandler.hasMessages(0)) {
                this.mTimerHandler.removeMessages(0);
            }
            this.mTimerHandler.sendEmptyMessageDelayed(0, time);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWifiScanTimer() {
        CMService.getInstance().wifiScanStop();
        Trace.d(CMConstants.TAG_NAME, "stopWifiScanTimer, WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED);
        CMService.AUTOSHARE_AUTOSEARCH = false;
        changeMainDescription();
        if (this.mTimerHandler != null) {
            if (this.mTimerHandler.hasMessages(0)) {
                this.mTimerHandler.removeMessages(0);
            }
            this.mTimerHandler = null;
        }
    }

    private void showInitDialog() {
        showDialog(1002);
        if (this.mTimerHandlerForInitGuideDialog == null) {
            this.mTimerHandlerForInitGuideDialog = new Handler() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.9
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    CustomDialog initGuideDialog01 = (CustomDialog) Main.this.mDialogList.get(1002);
                    if (initGuideDialog01 != null && initGuideDialog01.isShowing()) {
                        initGuideDialog01.dismiss();
                    }
                    Main.this.createContent();
                    Main.this.showInitIntroDialog();
                }
            };
        }
        this.mTimerHandlerForInitGuideDialog.sendEmptyMessageDelayed(0, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInitIntroDialog() {
        if (!this.mSettings.getIntroNoticeGuide()) {
            showDialog(1010);
        } else {
            showInitNFCDialog();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInitNFCDialog() {
        if (this.mNFCAdapter != null) {
            if (!this.mSettings.getNFCConnectionGuide()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(BaseGalleryActivity.BUNDLE_KEY_DATA, true);
                showDialog(3, bundle);
                return;
            }
            showWifiManagerDialog();
            return;
        }
        boolean showInitDailog = "true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(getApplicationContext(), CMConstants.SP_KEY_SHOW_INIT_GUIDE_DAILOG, "true"));
        Trace.d(CMConstants.TAG_NAME, "showInitDialog = " + showInitDailog);
        if (showInitDailog) {
            showDialog(1003);
        } else {
            showWifiManagerDialog();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void wifiDisconnected() {
        CMService.IS_MODE_CONNECTED = false;
        changeMainDescription();
        CMService.getInstance().stopModeServer();
        CustomDialog modeClientDialog = this.mDialogList.get(1001);
        if (modeClientDialog != null && modeClientDialog.isShowing()) {
            Trace.d(CMConstants.TAG_NAME, "wifiDisconnected(), wifi disconnected...BUT ModeClientDialog is showing, dismiss.");
            CMModeManager.stopModeClient();
            CMService.IS_WIFI_CONNECTED = false;
            CMService.getInstance().NFCConnectionTryCancel();
            modeClientDialog.dismiss();
        }
        showStatusBar();
        CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
    }

    private void registerLocalBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CMConstants.INTENT_FROM_CM);
        registerReceiver(this.mNetworkReciever, filter);
    }

    private void cmStart() {
        CMNotificationManager.getInstance().init(getApplicationContext());
        showStatusBar();
        CMNotificationManager.getInstance().notifyStatusChange(false, null, getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
        startForegroundService();
    }

    private void startForegroundService() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) CMService.class);
        startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWifiManagerDialog() {
        Trace.d(CMConstants.TAG_NAME, "showWifiManagerDialog()");
        CMService.getInstance().checkCurrentWifiConnection();
        if (!CMService.IS_WIFI_CONNECTED) {
            if (!CMService.mWifiManager.isWifiEnabled()) {
                CMService.mWifiManager.setWifiEnabled(true);
            }
            showDialog(1000);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showModeManagerDialog() {
        Trace.d(CMConstants.TAG_NAME, "showModeManagerDialog(), WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", MODE CONNECTED = " + CMService.IS_MODE_CONNECTED);
        if (CMService.IS_WIFI_CONNECTED) {
            if (CMService.IS_MODE_CONNECTED) {
                Trace.d(CMConstants.TAG_NAME, "showModeManagerDialog(), wifi conneced and mode connected. NOT show ModeManager Dialog.");
                return;
            }
            CustomDialog nfcInitGuide = this.mDialogList.get(3);
            CustomDialog normalInitGuide = this.mDialogList.get(1003);
            CustomDialog normalInitGuideHelp = this.mDialogList.get(1004);
            if ((nfcInitGuide != null && nfcInitGuide.isShowing()) || ((normalInitGuide != null && normalInitGuide.isShowing()) || (normalInitGuideHelp != null && normalInitGuideHelp.isShowing()))) {
                Trace.d(CMConstants.TAG_NAME, "showModeManagerDialog, Init Guide Dialog is showing...wait");
                return;
            }
            stopWifiScanTimer();
            CMService.getInstance().runModeServer();
            CustomDialog searchDialog = this.mDialogList.get(1000);
            if (searchDialog != null && searchDialog.isShowing()) {
                searchDialog.dismiss();
            }
            CustomDialog modeClientDialog = this.mDialogList.get(1001);
            if (modeClientDialog == null || !modeClientDialog.isShowing()) {
                showDialog(1001);
            } else if (modeClientDialog != null) {
                modeClientDialog.isShowing();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHelpDialog() {
        showDialog(1004);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showVerifyingPoorLinkDialog() {
        showDialog(1005);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRefusalDialog(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseGalleryActivity.BUNDLE_KEY_DATA, msg);
        showDialog(1006, bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNOTSupportedDialog() {
        showDialog(1007);
    }

    public void dismissAllDialog() {
        if (this.mDialogList != null) {
            CustomDialog searchDialog = this.mDialogList.get(1000);
            if (searchDialog != null && searchDialog.isShowing()) {
                searchDialog.dismiss();
            }
            CustomDialog modeClientDialog = this.mDialogList.get(1001);
            if (modeClientDialog != null && modeClientDialog.isShowing()) {
                modeClientDialog.dismiss();
            }
            CustomDialog initGuideDialog = this.mDialogList.get(1003);
            if (initGuideDialog != null && initGuideDialog.isShowing()) {
                initGuideDialog.dismiss();
            }
            CustomProgressDialog nowAppClosingDialog = (CustomProgressDialog) this.mDialogList.get(1008);
            if (nowAppClosingDialog != null && nowAppClosingDialog.isShowing()) {
                nowAppClosingDialog.dismiss();
            }
        }
    }

    public void appcloseSendingByeBye() {
        Trace.d(CMConstants.TAG_NAME, "appcloseSendingByeBye()");
        showDialog(1008);
        if (this.mFinishThread == null) {
            this.mFinishThread = new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.10
                @Override // java.lang.Runnable
                public void run() {
                    boolean old = CMUtil.checkOldVersionSmartCameraApp(CMInfo.getInstance().getConnectedSSID());
                    Trace.d(CMConstants.TAG_NAME, "appcloseSendingByeBye, old camera= " + old + ", WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                    if (CMService.IS_WIFI_CONNECTED && !old) {
                        CMModeManager.runModeClient(Main.this.mIModeClientForSendingByebye, true);
                        int count = 0;
                        while (true) {
                            Trace.d(CMConstants.TAG_NAME, "appcloseSendingByeBye, run... count = " + count);
                            if (!CMService.IS_WIFI_CONNECTED) {
                                Trace.d(CMConstants.TAG_NAME, "appcloseSendingByeBye, run...break01");
                                break;
                            } else if (count == 12) {
                                Trace.d(CMConstants.TAG_NAME, "appcloseSendingByeBye, run...break02");
                                break;
                            } else {
                                count++;
                                SystemClock.sleep(500L);
                            }
                        }
                    }
                    Main.this.dismissAllDialog();
                    CMModeManager.stopModeClient();
                    CMService.getInstance().appclose();
                }
            });
            this.mFinishThread.start();
        }
    }

    public void sendByebyeForLowBattery() {
        Thread thread = new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.11
            @Override // java.lang.Runnable
            public void run() {
                boolean old = CMUtil.checkOldVersionSmartCameraApp(CMInfo.getInstance().getConnectedSSID());
                Trace.d(CMConstants.TAG_NAME, "sendByebyeForLowBattery, old camera= " + old + ", WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                if (CMService.IS_WIFI_CONNECTED && !old) {
                    CMModeManager.runModeClient(Main.this.mIModeClientForSendingByebye, true);
                    int count = 0;
                    while (true) {
                        Trace.d(CMConstants.TAG_NAME, "sendByebyeForLowBattery, run... count = " + count);
                        if (!CMService.IS_WIFI_CONNECTED) {
                            Trace.d(CMConstants.TAG_NAME, "sendByebyeForLowBattery, run...break01");
                            break;
                        } else if (count == 12) {
                            Trace.d(CMConstants.TAG_NAME, "sendByebyeForLowBattery, run...break02");
                            break;
                        } else {
                            count++;
                            SystemClock.sleep(500L);
                        }
                    }
                }
                CMModeManager.stopModeClient();
                CMUtil.disableConnectedCamera(CMService.mWifiManager);
                CMNotificationManager.getInstance().notifyStatusChange(false, null, Main.this.getResources().getString(R.string.cm_noti_msg_is_waiting2).toString(), R.string.cm_noti_ongoing);
                CMService.IS_WIFI_CONNECTED = false;
                CMService.IS_MODE_CONNECTED = false;
            }
        });
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeMainDescription() {
        Trace.d(CMConstants.TAG_NAME, "changeMainDescription, WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED + ", AUTOSHARE_AUTOSEARCH = " + CMService.AUTOSHARE_AUTOSEARCH);
        if (CMService.IS_WIFI_CONNECTED) {
            this.mDescription.setText(R.string.connected);
            this.mConnectButton.setEnabled(false);
        } else if (CMService.AUTOSHARE_AUTOSEARCH) {
            this.mDescription.setText(R.string.not_connected_autoshare_autosearch_description);
            this.mConnectButton.setEnabled(true);
        } else {
            this.mDescription.setText(R.string.not_connected);
            this.mConnectButton.setEnabled(true);
        }
        checkTestMode();
    }

    public void killProcess() {
        try {
            final ActivityManager am = (ActivityManager) getSystemService("activity");
            new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.cm.Main.12
                @Override // java.lang.Runnable
                public void run() {
                    while (true) {
                        for (ActivityManager.RunningAppProcessInfo rai : am.getRunningAppProcesses()) {
                            if (rai.processName.equals(Main.this.getApplicationInfo().processName)) {
                                rai.importance = 500;
                                Trace.d(CMConstants.TAG_NAME, "killBackgroundProcesses");
                                am.killBackgroundProcesses(Main.this.getPackageName());
                            } else {
                                Thread.yield();
                            }
                        }
                    }
                }
            }, "CM").start();
            Trace.d(CMConstants.TAG_NAME, "killProcess");
            Process.killProcess(Process.myPid());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void checkTestMode() {
        if (CMUtil.isTestMode(getApplicationContext())) {
            String ssid_temp = CMSharedPreferenceUtil.getString(this, CMConstants.TESTMODE_SSID, "");
            Trace.d(CMConstants.TAG_NAME, "checkTestMode(), ssid_temp = " + ssid_temp);
            if (ssid_temp.length() != 0 && ssid_temp.equals("NONE")) {
                ssid_temp = "";
            }
            String ssid_temp_2 = CMSharedPreferenceUtil.getString(this, CMConstants.TESTMODE_SSID2, "");
            Trace.d(CMConstants.TAG_NAME, "checkTestMode(), ssid_temp_2 = " + ssid_temp_2);
            if (ssid_temp_2.length() != 0 && ssid_temp_2.equals("NONE")) {
                ssid_temp_2 = "";
            }
            String ssid_temp_3 = CMSharedPreferenceUtil.getString(this, CMConstants.TESTMODE_SSID3, "");
            Trace.d(CMConstants.TAG_NAME, "checkTestMode(), ssid_temp_3 = " + ssid_temp_3);
            if (ssid_temp_3.length() != 0 && ssid_temp_3.equals("NONE")) {
                ssid_temp_3 = "";
            }
            List<String> list = new ArrayList<>();
            list.add(ssid_temp);
            list.add(ssid_temp_2);
            list.add(ssid_temp_3);
            String title = "";
            for (int i = 0; i < list.size(); i++) {
                String temp = list.get(i);
                if (temp.length() != 0 && !temp.equals("NONE")) {
                    title = String.valueOf(title) + "(" + temp + ")";
                }
            }
            Trace.d(CMConstants.TAG_NAME, "checkTestMode(), title = " + title);
            CMUtil.setdscPrefix_fortest(this, list);
            this.mDescription.setText(((Object) this.mDescription.getText()) + " " + title);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (CMUtil.isTestMode(getApplicationContext())) {
            menu.findItem(R.id.menu_test_mode).setVisible(true);
        }
        return true;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_test_mode /* 2131558732 */:
                showDialog(1009);
                break;
        }
        return true;
    }
}

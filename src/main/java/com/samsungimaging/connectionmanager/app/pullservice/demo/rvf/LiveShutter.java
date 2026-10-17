package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.samsungimaging.asphodel.multimedia.FFmpegJNI;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.localgallery.LocalGallery;
import com.samsungimaging.connectionmanager.app.pullservice.FunctionManager;
import com.samsungimaging.connectionmanager.app.pullservice.PullService;
import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCMenuItem;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCMovieResolution;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCResolution;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.FileSharing;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.common.Timer;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.configuration.manager.SRVFConfigurationManager;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.dialog.CustomDialogListMenu;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.dialog.CustomDialogWheelMenu;
import com.samsungimaging.connectionmanager.app.pullservice.demo.util.CommonUtils;
import com.samsungimaging.connectionmanager.app.pullservice.util.Graph;
import com.samsungimaging.connectionmanager.app.pullservice.util.ImageDownloader;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.util.ExToast;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.widget.VerticalSeekBar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.soap.SOAP;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnPStatus;
import org.kankan.wheel.widget.OnWheelClickedListener;
import org.kankan.wheel.widget.OnWheelScrollListener;
import org.kankan.wheel.widget.WheelView;
import org.kankan.wheel.widget.adapters.ArrayWheelAdapter;

/* JADX INFO: loaded from: classes.dex */
public final class LiveShutter extends PullService implements View.OnClickListener, View.OnTouchListener, SurfaceHolder.Callback, OnWheelScrollListener, OnWheelClickedListener {
    private static Context Appcontext = null;
    public static final int LENGTH_ENDLESS = 1;
    public static final int LENGTH_SHORT = 0;
    private static String cam_rotated_type;
    static Handler msgHandler;
    private static int orientation;
    private static RelativeLayout rvf_QuickSettingMenu;
    private static ImageView rvf_af3x3_00;
    private static ImageView rvf_af3x3_01;
    private static ImageView rvf_af3x3_02;
    private static ImageView rvf_af3x3_10;
    private static ImageView rvf_af3x3_11;
    private static ImageView rvf_af3x3_12;
    private static ImageView rvf_af3x3_20;
    private static ImageView rvf_af3x3_21;
    private static ImageView rvf_af3x3_22;
    private static ImageView rvf_af3x5_00;
    private static ImageView rvf_af3x5_01;
    private static ImageView rvf_af3x5_02;
    private static ImageView rvf_af3x5_03;
    private static ImageView rvf_af3x5_04;
    private static ImageView rvf_af3x5_10;
    private static ImageView rvf_af3x5_11;
    private static ImageView rvf_af3x5_12;
    private static ImageView rvf_af3x5_13;
    private static ImageView rvf_af3x5_14;
    private static ImageView rvf_af3x5_20;
    private static ImageView rvf_af3x5_21;
    private static ImageView rvf_af3x5_22;
    private static ImageView rvf_af3x5_23;
    private static ImageView rvf_af3x5_24;
    private static ImageView rvf_af3x7_00;
    private static ImageView rvf_af3x7_01;
    private static ImageView rvf_af3x7_02;
    private static ImageView rvf_af3x7_03;
    private static ImageView rvf_af3x7_04;
    private static ImageView rvf_af3x7_05;
    private static ImageView rvf_af3x7_06;
    private static ImageView rvf_af3x7_10;
    private static ImageView rvf_af3x7_11;
    private static ImageView rvf_af3x7_12;
    private static ImageView rvf_af3x7_13;
    private static ImageView rvf_af3x7_14;
    private static ImageView rvf_af3x7_15;
    private static ImageView rvf_af3x7_16;
    private static ImageView rvf_af3x7_20;
    private static ImageView rvf_af3x7_21;
    private static ImageView rvf_af3x7_22;
    private static ImageView rvf_af3x7_23;
    private static ImageView rvf_af3x7_24;
    private static ImageView rvf_af3x7_25;
    private static ImageView rvf_af3x7_26;
    private static RelativeLayout rvf_af_extend;
    private static ImageButton rvf_cameramore_button;
    private static ImageButton rvf_camerasave_button;
    private static ImageButton rvf_close_button;
    private static ImageButton rvf_flash_button;
    private static ImageView rvf_metering_spot_frame;
    private static ImageButton rvf_open_button;
    private static ImageButton rvf_photosize_button;
    private static ImageButton rvf_timer_button;
    private static TextView rvf_title;
    private static ImageView rvf_touchAutoFocus;
    private static RelativeLayout rvf_zoomMain;
    private static SeekBar rvf_zoomSeekBar;
    private static ImageButton rvf_zoomin_button;
    private static ImageButton rvf_zoomout_button;
    private String[] AFData;
    Button closenotice;
    private FrameCounterTask frameCounter;
    LinearLayout layout;
    private LocationManager locationManager;
    private SRVFConfigurationManager mConfigurationManager;
    private DeviceList mDeviceList;
    private MainOptionMenuListAdapter mMainOptionMenuListAdapter;
    private CustomDialogListMenu mSmartPanelCustomDialogList;
    private int mSmartPanelCustomDialogListMenuId;
    private ArrayList<Menu> mSmartPanelCustomDialogListMenuItems;
    private CustomDialogWheelMenu mSmartPanelCustomDialogWheel;
    private ArrayList<String> mSmartPanelCustomDialogWheelMenuItems;
    private SubOptionMenuListAdapter mSubOptionMenuListAdapter;
    private CheckBox noticecheck;
    WindowManager.LayoutParams params;
    private PopupWindow popupWindow;
    private ImageView rvf_af_extend_leftdown;
    private ImageView rvf_af_extend_leftup;
    private ImageView rvf_af_extend_rightdown;
    private ImageView rvf_af_extend_rightup;
    private ImageButton rvf_camcorder_button;
    private ImageView rvf_camera_btn_af;
    private RelativeLayout rvf_gallery;
    private ImageButton rvf_indicator_af_area;
    private LinearLayout rvf_indicator_bar;
    private ImageButton rvf_indicator_drive;
    private ImageButton rvf_indicator_flash;
    private ImageButton rvf_indicator_metering;
    private ImageButton rvf_indicator_movie_size;
    private ImageButton rvf_indicator_photo_size;
    private ImageButton rvf_indicator_quality;
    private ImageButton rvf_indicator_storage;
    private ImageButton rvf_indicator_touch_af;
    private ImageButton rvf_indicator_wb;
    private ListView rvf_list;
    private RelativeLayout rvf_live_shutter;
    private ImageView rvf_memory_full;
    private TextView rvf_menuTitle;
    private ImageView rvf_menuTop1;
    private ImageView rvf_menuTop2;
    private ImageView rvf_menuTop3;
    private ImageView rvf_menuTop4;
    private RelativeLayout rvf_menu_button;
    private Button rvf_mode_button;
    private ImageView rvf_mode_icon;
    private ImageView rvf_no_item;
    private LinearLayout rvf_option;
    private ProgressBar rvf_progress;
    private ImageView rvf_rec_icon;
    private ImageButton rvf_rec_stop_button;
    private LinearLayout rvf_rec_time;
    private LinearLayout rvf_rec_title;
    private TextView rvf_recording_time;
    private TextView rvf_remain_time;
    private LinearLayout rvf_setting_sub_menu;
    private ListView rvf_setting_sub_menu_list;
    private TextView rvf_setting_sub_menu_title;
    private ImageButton rvf_shutter_button;
    private LinearLayout rvf_smart_panel;
    private ImageButton rvf_smart_panel_afArea;
    private ImageButton rvf_smart_panel_afMode;
    private WheelView rvf_smart_panel_aperture;
    private ImageButton rvf_smart_panel_drive;
    private WheelView rvf_smart_panel_ev;
    private ImageButton rvf_smart_panel_flash;
    private ImageButton rvf_smart_panel_icon;
    private WheelView rvf_smart_panel_iso;
    private ImageButton rvf_smart_panel_metering;
    private WheelView rvf_smart_panel_mode;
    private ImageButton rvf_smart_panel_movieSize;
    private ImageButton rvf_smart_panel_photoSize;
    private ImageButton rvf_smart_panel_quality;
    private ImageButton rvf_smart_panel_save;
    private WheelView rvf_smart_panel_shutterSpeed;
    private ImageButton rvf_smart_panel_streaming_quality;
    private ImageButton rvf_smart_panel_touchAF;
    private ImageButton rvf_smart_panel_wb;
    private SurfaceView rvf_surface;
    private ImageView rvf_thumbnail;
    private ImageView rvf_timecount;
    private RelativeLayout rvf_timerCountMain;
    private ImageView rvf_timerHat;
    private TextView rvf_toast_message;
    private SoundPool soundPool;
    private int soundTimerBeep1;
    private int soundTimerBeep2;
    private TimerTaskRun timetask;
    TextView txtView;
    TextView txtView2;
    private Vibrator vibe;
    public static long connectionTimeout = 0;
    public static Device DSCdevice = null;
    private static Bitmap bmImg = null;
    private static char cBmpRotation = 'D';
    private static Bitmap bgbitmap = null;
    private static Bitmap bgbitmap1 = null;
    private static Bitmap thumbnail_bmp = null;
    private static SurfaceHolder holder = null;
    private static boolean isButtonShow = false;
    private static int nDisplayFlag = 0;
    static Handler dHandler = new Handler();
    static Handler mHandler = new Handler();
    static boolean bappclose = false;
    static boolean bsaveflag = false;
    static boolean bshutter = false;
    private static boolean codec_init = false;
    private static boolean bClosing = false;
    private static boolean bVersionFail = false;
    private static int nShutterDnUp = -1;
    private static int nHolderLongWidth = 0;
    private static int nHolderShortWidth = 0;
    private static int nHolderShortHeight = 0;
    private static int nCanvasY = 0;
    private static int nCanvasX = 0;
    private static double mLatitude = 0.0d;
    private static double mLongitude = 0.0d;
    private static boolean isShotProcessing = false;
    public static boolean bNoTelephonyDevice = false;
    private static boolean mExistStatusBarDeviceForTablet = false;
    private static boolean configChanged = false;
    private Trace.Tag TAG = Trace.Tag.RVF;
    private ExToast mExToast = ExToast.getInstance();
    private int mCurPopupWindowId = 0;
    private int mCurrentMainOptionMenuId = 0;
    private int mCurrentSubOptionMenuId = 0;
    private int mPhoneCamType = 1;
    private ArrayList<Menu> mMainOptionMenuList = new ArrayList<>();
    private ArrayList<Menu> mSubOptionMenuList = new ArrayList<>();
    private Handler mHandleTimer = null;
    private int nCameraSaveSelect = 1;
    private int nTimerCount = 0;
    private int REQUESTIMAGE = 1;
    private Timer recordTimer = null;
    private ImageDownloader mImageDownloader = null;
    private int AfFrameType = 0;
    Dialog notice = null;
    private boolean mDestroyed = false;
    private AdapterView.OnItemClickListener mSmartPanelCustomDialogListMenuItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            LiveShutter.this.mSmartPanelCustomDialogList.setEnabled(false);
            if (((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).isSelected()) {
                LiveShutter.this.setTimer(9);
                LiveShutter.this.actionEnd();
            }
            int i = 0;
            while (i < LiveShutter.this.mSmartPanelCustomDialogListMenuItems.size()) {
                ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(i)).setSelected(i == position);
                i++;
            }
            LiveShutter.this.mSmartPanelCustomDialogList.notifyDataSetChanged();
            switch (LiveShutter.this.getSmartPanelCustomDialogListMenuId()) {
                case 2:
                    LiveShutter.this.nPhotoSizeSelect = position;
                    if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultResolutionIndex()) != position) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 11:
                    if (!LiveShutter.this.mDeviceController.getTouchAFValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 17:
                    if (!LiveShutter.this.mDeviceController.getFlashStrobeStatus().equals("DETACHED")) {
                        if (!LiveShutter.this.mDeviceController.getDefaultFlash().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                            LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                        }
                    } else {
                        LiveShutter.this.mSmartPanelCustomDialogList.setEnabled(true);
                        LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                        LiveShutter.this.mSmartPanelCustomDialogList.hide();
                        Bundle bundle = new Bundle();
                        bundle.putString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY, LiveShutter.this.getString(R.string.rvf_ensure_flash_on_the_camera));
                        LiveShutter.this.showDialog(1013, bundle);
                    }
                    break;
                case 18:
                    if (!LiveShutter.this.mDeviceController.getCurrentStreamQuality().equals(LiveShutter.this.mDeviceController.getStreamQualityMenuItems().get(position))) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), LiveShutter.this.mDeviceController.getStreamQualityMenuItems().get(position));
                    }
                    break;
                case 25:
                    if (!LiveShutter.this.mDeviceController.getWBValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 26:
                    if (!LiveShutter.this.mDeviceController.getMeteringValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 27:
                    if (!LiveShutter.this.mDeviceController.getAFModeValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 28:
                    if (!LiveShutter.this.mDeviceController.getAFAreaValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 29:
                    if (!LiveShutter.this.mDeviceController.getDriveValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS") && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW")) {
                            if (LiveShutter.this.mConfirmDialog == null) {
                                Trace.d(LiveShutter.this.TAG, "mConfirmDialog is null");
                                Bundle args = new Bundle();
                                args.putString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY, LiveShutter.this.getString(R.string.not_supported_continuous_shot_in_raw_quality));
                                LiveShutter.this.showDialog(2000, args);
                            } else {
                                Trace.d(LiveShutter.this.TAG, "mConfirmDialog is not null");
                                LiveShutter.this.mConfirmDialog.setMessage(LiveShutter.this.getString(R.string.not_supported_continuous_shot_in_raw_quality));
                                LiveShutter.this.mConfirmDialog.show();
                            }
                            LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                            LiveShutter.this.mSmartPanelCustomDialogList.hide();
                        } else {
                            LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                        }
                    }
                    break;
                case 33:
                    if (!LiveShutter.this.mDeviceController.getQualityValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                            if (LiveShutter.this.mConfirmDialog == null) {
                                Trace.d(LiveShutter.this.TAG, "mConfirmDialog is null");
                                Bundle args2 = new Bundle();
                                args2.putString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY, LiveShutter.this.getString(R.string.not_supported_raw_quality_in_continuous_shot));
                                LiveShutter.this.showDialog(2000, args2);
                            } else {
                                Trace.d(LiveShutter.this.TAG, "mConfirmDialog is not null");
                                LiveShutter.this.mConfirmDialog.setMessage(LiveShutter.this.getString(R.string.not_supported_raw_quality_in_continuous_shot));
                                LiveShutter.this.mConfirmDialog.show();
                            }
                            LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                            LiveShutter.this.mSmartPanelCustomDialogList.hide();
                        } else {
                            LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                        }
                    }
                    break;
                case 34:
                    if (!LiveShutter.this.mDeviceController.getMovieResolutionValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                    }
                    break;
                case 37:
                    if (!LiveShutter.this.mDeviceController.getFileSaveValue().equals(((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName())) {
                        LiveShutter.this.doAction(LiveShutter.this.getSmartPanelCustomDialogListMenuId(), ((Menu) LiveShutter.this.mSmartPanelCustomDialogListMenuItems.get(position)).getName());
                        LiveShutter.this.nCameraSaveSelect = position;
                    }
                    break;
            }
        }
    };
    private AdapterView.OnItemClickListener mMainOptionMenuItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            LiveShutter.this.setTimer(7);
            switch (LiveShutter.this.getCurrentMainOptionMenuId()) {
                case 1:
                    LiveShutter.this.doAction(17, LiveShutter.this.mDeviceController.getFlashMenuItems().get(position));
                    LiveShutter.this.buttondisplay(true);
                    break;
                case 2:
                    if (!LiveShutter.this.mDeviceController.getLedTimeMenuItems().get(position).equals(LiveShutter.this.getString(R.string.rvf_timer_off))) {
                        if (!LiveShutter.this.mDeviceController.getLedTimeMenuItems().get(position).equals(LiveShutter.this.getString(R.string.rvf_timer_2sec))) {
                            if (!LiveShutter.this.mDeviceController.getLedTimeMenuItems().get(position).equals(LiveShutter.this.getString(R.string.rvf_timer_5sec))) {
                                if (!LiveShutter.this.mDeviceController.getLedTimeMenuItems().get(position).equals(LiveShutter.this.getString(R.string.rvf_timer_10sec))) {
                                    if (LiveShutter.this.mDeviceController.getLedTimeMenuItems().get(position).equals(LiveShutter.this.getString(R.string.rvf_timer_double))) {
                                        LiveShutter.this.doAction(16, "Double");
                                    }
                                } else {
                                    LiveShutter.this.doAction(16, "10");
                                }
                            } else {
                                LiveShutter.this.doAction(16, "5");
                            }
                        } else {
                            LiveShutter.this.doAction(16, "2");
                        }
                    } else {
                        LiveShutter.this.doAction(16, "0");
                    }
                    LiveShutter.this.setImageResource(LiveShutter.rvf_timer_button);
                    LiveShutter.this.buttondisplay(true);
                    break;
                case 3:
                    LiveShutter.this.nPhotoSizeSelect = position;
                    DSCResolution dscResolution = LiveShutter.this.mDeviceController.getResolutionMenuItems().get(LiveShutter.this.nPhotoSizeSelect);
                    String argument = String.format("%dx%d", Integer.valueOf(dscResolution.getWidth()), Integer.valueOf(dscResolution.getHeight()));
                    LiveShutter.this.doAction(2, argument);
                    LiveShutter.this.setImageResource(LiveShutter.rvf_photosize_button);
                    LiveShutter.this.buttondisplay(true);
                    break;
                case 4:
                    LiveShutter.this.nCameraSaveSelect = position;
                    if (LiveShutter.this.nCameraSaveSelect == 0) {
                        if (RVFFunctionManager.getSaveGuideDialogType(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid) != 0) {
                            LiveShutter.this.showDialog(Const.MsgBoxId.MSGBOX_SMARTPHONE_SAVE_RESOLUTION_GUIDE);
                        }
                        if (CommonUtils.isMemoryFull()) {
                            LiveShutter.this.mExToast.show(7);
                            LiveShutter.this.setShutterButtonEnabled(false);
                        }
                        LiveShutter.this.mDeviceController.setFileSaveValue("");
                    } else {
                        LiveShutter.this.setShutterButtonEnabled(true);
                    }
                    LiveShutter.this.mDeviceController.setFileSaveValue(LiveShutter.this.mDeviceController.getFileSaveMenuItems().get(LiveShutter.this.nCameraSaveSelect));
                    LiveShutter.this.setImageResource(LiveShutter.this.rvf_indicator_storage);
                    LiveShutter.this.setImageResource(LiveShutter.rvf_camerasave_button);
                    LiveShutter.this.updateImageOfGallaryButton();
                    LiveShutter.this.setTimer(2);
                    LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    break;
                case 5:
                    switch (position) {
                        case 0:
                            LiveShutter.this.showSubOptionMenu(4);
                            break;
                        case 1:
                            LiveShutter.this.showSubOptionMenu(6);
                            break;
                    }
                    break;
            }
        }
    };
    private AdapterView.OnItemClickListener mSubOptionMenuItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.3
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            LiveShutter.this.setTimer(7);
            switch (LiveShutter.this.getCurrentSubOptionMenuId()) {
                case 4:
                    LiveShutter.this.nCameraSaveSelect = position;
                    if (LiveShutter.this.nCameraSaveSelect == 0 && RVFFunctionManager.getSaveGuideDialogType(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid) != 0) {
                        LiveShutter.this.showDialog(Const.MsgBoxId.MSGBOX_SMARTPHONE_SAVE_RESOLUTION_GUIDE);
                    }
                    LiveShutter.this.mDeviceController.setFileSaveValue(LiveShutter.this.mDeviceController.getFileSaveMenuItems().get(LiveShutter.this.nCameraSaveSelect));
                    LiveShutter.this.setImageResource(LiveShutter.this.rvf_indicator_storage);
                    LiveShutter.this.setImageResource(LiveShutter.rvf_camerasave_button);
                    LiveShutter.this.updateImageOfGallaryButton();
                    LiveShutter.this.setTimer(3);
                    LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    LiveShutter.this.mSubOptionMenuListAdapter.notifyDataSetChanged();
                    break;
                case 6:
                    switch (position) {
                        case 0:
                            if (LiveShutter.this.mDeviceController.getCurrentStreamQuality().equals("high")) {
                                LiveShutter.this.setTimer(3);
                            } else {
                                LiveShutter.this.doAction(18, "high");
                            }
                            break;
                        case 1:
                            if (LiveShutter.this.mDeviceController.getCurrentStreamQuality().equals("low")) {
                                LiveShutter.this.setTimer(3);
                            } else {
                                LiveShutter.this.doAction(18, "low");
                            }
                            break;
                    }
                    break;
            }
        }
    };
    private String cameratype = "ssdp:rotationD";
    private boolean bNetworkGPS = false;
    private boolean bConnect = false;
    private boolean isInitializedCamera = false;
    private boolean bexitflag = false;
    private Location currentLocation_network = null;
    private Location currentLocation_gps = null;
    private int mSmartPanelCustomDialogWheelMenuId = 0;
    private int mSmartPanelVisibility = 8;
    private Toast toast = null;
    private ProgressDialog proap = null;
    private ProgressDialog pro = null;
    private ProgressDialog streampro = null;
    private ProgressDialog customProgressBar = null;
    private AlertDialog mConnectionFail = null;
    private boolean isLongZoom = false;
    private int nPhotoSizeSelect = 0;
    private boolean isOptimusVu = false;
    private boolean isOperationStateChange = false;
    private int mShotStateEx = 0;
    private int mShotState = 0;
    private int mZoomState = 0;
    private int mDoubleTimerShotState = 0;
    private Handler mEventHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.4
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Trace.d(LiveShutter.this.TAG, "start handleMessage() : " + msg.what);
            switch (msg.what) {
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CAMERA_CHANGE /* 102 */:
                    String strNTS = (String) msg.obj;
                    if (strNTS.contains("rotation") && !strNTS.equals(LiveShutter.this.cameratype)) {
                        if (strNTS.equals("ssdp:rotationD") || strNTS.equals("ssdp:rotationU") || strNTS.equals("ssdp:rotationL") || strNTS.equals("ssdp:rotationR")) {
                            LiveShutter.this.cameratype = strNTS;
                            LiveShutter.this.checkphonecamtype();
                            if (LiveShutter.this.getPhoneCamType() == 2 || LiveShutter.this.getPhoneCamType() == 1) {
                                LiveShutter.this.CheckLayout();
                            }
                        }
                        break;
                    } else if (strNTS.contains("flash") && !strNTS.substring(10).toLowerCase().equals(LiveShutter.this.mDeviceController.getCurrentFlashDisplay().toLowerCase())) {
                        for (int i = 0; i < LiveShutter.this.mDeviceController.getFlashDisplayMenuItems().size(); i++) {
                            if (strNTS.substring(10).toLowerCase().equals(LiveShutter.this.mDeviceController.getFlashDisplayMenuItems().get(i).toLowerCase())) {
                                LiveShutter.this.mDeviceController.setCurrentFlashDisplay(LiveShutter.this.mDeviceController.getFlashDisplayMenuItems().get(i));
                                LiveShutter.nDisplayFlag = Const.MsgId.FLASH_RESOURCE_CHANGE;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            }
                            break;
                        }
                        break;
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_ROTATION_STATUS /* 103 */:
                    String value = (String) msg.obj;
                    if (!value.equals("NULL")) {
                        String strDirection = "";
                        if (((String) msg.obj).equals("D")) {
                            strDirection = "ssdp:rotationD";
                        } else if (((String) msg.obj).equals("L")) {
                            strDirection = "ssdp:rotationL";
                        } else if (((String) msg.obj).equals("U")) {
                            strDirection = "ssdp:rotationU";
                        } else if (((String) msg.obj).equals("R")) {
                            strDirection = "ssdp:rotationR";
                        }
                        if (!strDirection.equals(LiveShutter.this.cameratype)) {
                            LiveShutter.this.cameratype = strDirection;
                            LiveShutter.this.checkphonecamtype();
                            if (LiveShutter.this.getPhoneCamType() == 4 || LiveShutter.this.getPhoneCamType() == 3 || LiveShutter.this.getPhoneCamType() == 2 || LiveShutter.this.getPhoneCamType() == 1) {
                                LiveShutter.this.CheckLayout();
                            }
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_FLASH_STATUS /* 104 */:
                    String value2 = (String) msg.obj;
                    if (!value2.equals("NULL")) {
                        if (!Utils.isNumeric(value2)) {
                            LiveShutter.this.mDeviceController.setCurrentFlashDisplay(value2);
                            if (!LiveShutter.this.mDeviceController.getFlashStrobeStatus().isEmpty()) {
                                if (value2.toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                                    LiveShutter.this.mDeviceController.setFlashStrobeStatus("DETACHED");
                                } else {
                                    LiveShutter.this.mDeviceController.setFlashStrobeStatus("ATTACHED");
                                }
                            }
                        } else if (Integer.parseInt(value2) == 0) {
                            switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                case 2:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.OFF);
                                    break;
                                default:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay("off");
                                    break;
                            }
                            if (!LiveShutter.this.mDeviceController.getFlashStrobeStatus().isEmpty()) {
                                LiveShutter.this.mDeviceController.setFlashStrobeStatus("DETACHED");
                            }
                        } else {
                            switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                case 2:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.AUTO);
                                    break;
                                default:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay("auto");
                                    break;
                            }
                            if (!LiveShutter.this.mDeviceController.getFlashStrobeStatus().isEmpty()) {
                                LiveShutter.this.mDeviceController.setFlashStrobeStatus("ATTACHED");
                            }
                        }
                        if (LiveShutter.this.mDeviceController.isConnected()) {
                            if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                                LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_flash);
                            } else {
                                LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                            }
                            LiveShutter.this.setIndicator(R.id.rvf_indicator_flash);
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_FOCUS_STATUS /* 105 */:
                    String value3 = (String) msg.obj;
                    Trace.d(LiveShutter.this.TAG, "msg.obj : " + msg.obj);
                    if (value3.equals("0")) {
                        LiveShutter.this.mDeviceController.setDefaultFocusState("af");
                        if (LiveShutter.this.mDeviceController.isConnected()) {
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afArea);
                            LiveShutter.this.rvf_smart_panel_afArea.setEnabled(true);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_touchAF);
                            LiveShutter.this.rvf_smart_panel_touchAF.setEnabled(true);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_indicator_touch_af);
                        }
                    } else {
                        LiveShutter.this.mDeviceController.setDefaultFocusState("mf");
                        if (LiveShutter.this.mDeviceController.isConnected()) {
                            LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afArea);
                            LiveShutter.this.rvf_smart_panel_afArea.setEnabled(false);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_touchAF);
                            LiveShutter.this.rvf_smart_panel_touchAF.setEnabled(false);
                            if (LiveShutter.this.getSmartPanelCustomDialogListMenuId() == 11 || LiveShutter.this.getSmartPanelCustomDialogListMenuId() == 28) {
                                LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                                LiveShutter.this.mSmartPanelCustomDialogList.dismiss();
                            }
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_indicator_touch_af);
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE /* 106 */:
                    LiveShutter.this.mDeviceController.setShutterSpeedValue((String) msg.obj);
                    if (LiveShutter.this.mDeviceController.isConnected()) {
                        LiveShutter.this.setSmartPanel();
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_APERTURE_SETUP_VALUE /* 107 */:
                    LiveShutter.this.mDeviceController.setApertureValue((String) msg.obj);
                    if (LiveShutter.this.mDeviceController.isConnected()) {
                        LiveShutter.this.setSmartPanel();
                    }
                    break;
                case 108:
                    LiveShutter.this.mDeviceController.setEVValue((String) msg.obj);
                    if (LiveShutter.this.mDeviceController.isConnected()) {
                        LiveShutter.this.setSmartPanel();
                    }
                    break;
                case 109:
                    if (!((String) msg.obj).equals("NULL")) {
                        if (!((String) msg.obj).equals("")) {
                            LiveShutter.this.mDeviceController.setAvailShots((String) msg.obj);
                        }
                        if (LiveShutter.this.mShotStateEx != 5 || !LiveShutter.this.isAvailShot() || LiveShutter.this.isDCFFull()) {
                            if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS") && LiveShutter.this.mShotStateEx == 5) {
                                LiveShutter.this.mShotStateEx = 6;
                                LiveShutter.this.doAction(31, "");
                            }
                            LiveShutter.this.bexitflag = true;
                            LiveShutter.nDisplayFlag = 111;
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            LiveShutter.isShotProcessing = false;
                            LiveShutter.this.bshotrunflag = false;
                            LiveShutter.this.mShotStateEx = 0;
                            LiveShutter.this.actionEnd();
                        }
                        if (LiveShutter.this.isDCFFull()) {
                            LiveShutter.this.showDialog(1009);
                        } else if (!LiveShutter.this.isAvailShot()) {
                            LiveShutter.this.showMemoryFullDialog(1);
                        }
                    }
                    break;
                case 110:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.setAFShotResult((String) msg.obj);
                        if (LiveShutter.this.nCameraSaveSelect == 0) {
                            LiveShutter.this.mImageDownloader.downloadImage(LiveShutter.this.mDeviceController.getAFShotResult());
                        } else {
                            new ShotPrepareTask(LiveShutter.this, null).execute(new String[0]);
                        }
                    }
                    break;
                case 111:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.setDefaultZoom((String) msg.obj);
                        if (LiveShutter.this.mDeviceController.isConnected()) {
                            LiveShutter.this.showZoomBar();
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_APERTURE_LIST /* 112 */:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.getApertureMenuItems().clear();
                        for (String str : ((String) msg.obj).split(",")) {
                            LiveShutter.this.mDeviceController.getApertureMenuItems().add(str);
                        }
                        LiveShutter.this.setSmartPanel();
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_SHUTTER_SPEED_LIST /* 113 */:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.getShutterSpeedMenuItems().clear();
                        String[] items = ((String) msg.obj).split(",");
                        for (String str2 : items) {
                            LiveShutter.this.mDeviceController.getShutterSpeedMenuItems().add(str2);
                        }
                        LiveShutter.this.setSmartPanel();
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_MOVIE_RECORD_TIME /* 114 */:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.setMovieRecordTime((String) msg.obj);
                        LiveShutter.this.rvf_recording_time.setText(LiveShutter.this.getTimeFormat((String) msg.obj));
                        if (!((String) msg.obj).equals(LiveShutter.this.mDeviceController.getRemainRecTimeValue())) {
                            if (LiveShutter.this.recordTimer == null) {
                                LiveShutter.this.recordTimer = new Timer(LiveShutter.msgHandler, Const.MsgId.ON_UPDATE_MOVIE_RECORD_TIME, 100);
                                LiveShutter.this.recordTimer.start();
                            }
                        } else {
                            LiveShutter.this.doAction(36, "");
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_REMAIN_REC_TIME_VALUE /* 115 */:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.setRemainRecTimeValue((String) msg.obj);
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_FLASH_STROBE_STATUS /* 116 */:
                    if (!((String) msg.obj).equals("NULL")) {
                        LiveShutter.this.mDeviceController.setFlashStrobeStatus((String) msg.obj);
                        if (LiveShutter.this.mDeviceController.isConnected()) {
                            if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                                LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_flash);
                            } else {
                                LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                            }
                            LiveShutter.this.setIndicator(R.id.rvf_indicator_flash);
                        }
                    }
                    break;
                case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgID.CONNECTOR_ON_CHANGED_OPERATION_STATE /* 117 */:
                    if (((String) msg.obj).equals("TransitionToML") && LiveShutter.this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("EXTERNAL")) {
                        LiveShutter.this.mHandleTimer.removeMessages(5);
                        Intent intent = new Intent(LiveShutter.this, (Class<?>) FileSharing.class);
                        LiveShutter.this.startActivity(intent);
                        LiveShutter.this.finish();
                        break;
                    }
                    break;
            }
        }
    };
    private Handler mDeviceControlHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.5
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Trace.d(LiveShutter.this.TAG, "apk version : " + LiveShutter.this.versionName);
            switch (msg.what) {
                case 3:
                    boolean result = ((Boolean) msg.obj).booleanValue();
                    if (result) {
                        if (!LiveShutter.this.mDeviceController.getFileSaveValue().equals("") && LiveShutter.this.mDeviceController.getFileSaveMenuItems().size() > 0) {
                            for (int i = 0; i < LiveShutter.this.mDeviceController.getFileSaveMenuItems().size(); i++) {
                                if (LiveShutter.this.mDeviceController.getFileSaveMenuItems().get(i).equals(LiveShutter.this.mDeviceController.getFileSaveValue())) {
                                    LiveShutter.this.nCameraSaveSelect = i;
                                }
                            }
                        }
                        if (LiveShutter.this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                            LiveShutter.this.screenConfigChange(2);
                        } else {
                            LiveShutter.this.screenConfigChange(Utils.stringToRatioType(LiveShutter.this.mDeviceController.getRatioValue()));
                        }
                        LiveShutter.this.LayoutChange(LiveShutter.this.getPhoneCamType());
                        if (LiveShutter.this.mDeviceController.getAFAreaMenuItems().size() <= 0 || !LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                            if (LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                                LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            }
                        } else {
                            LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                            LiveShutter.rvf_touchAutoFocus.setVisibility(0);
                        }
                        LiveShutter.bshutter = false;
                        Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_BUTTON_SHOW");
                        LiveShutter.nDisplayFlag = 34;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                        LiveShutter.rvf_zoomSeekBar.setMax(Integer.parseInt(LiveShutter.this.mDeviceController.getMaxZoom()) - Integer.parseInt(LiveShutter.this.mDeviceController.getMinZoom()));
                        LiveShutter.this.zoombardisplay();
                        LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_flash);
                        LiveShutter.this.setIndicator();
                    } else {
                        Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_NETERRORMSG");
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 4:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Set Resolution Action Start");
                    LiveShutter.this.actionStart();
                    break;
                case 5:
                    boolean result2 = ((Boolean) msg.obj).booleanValue();
                    if (!result2) {
                        Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_NETERRORMSG");
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    } else {
                        LiveShutter.this.mDeviceController.setDefaultResolutionIndex(String.valueOf(LiveShutter.this.nPhotoSizeSelect));
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            Action action = LiveShutter.this.mDeviceController.getAction(2);
                            ArgumentList outArgList = action.getOutputArgumentList();
                            int nOutArgs = outArgList.size();
                            for (int n = 0; n < nOutArgs; n++) {
                                Argument outArg = outArgList.getArgument(n);
                                String name = outArg.getName();
                                String value = outArg.getValue();
                                Trace.d(LiveShutter.this.TAG, "name : " + name + " value : " + value);
                                if (name.equals("RatioChangeStatus") && value.equals(Service.MAJOR_VALUE)) {
                                    new RestartCodecTask(LiveShutter.this, null).execute(new Integer[0]);
                                }
                            }
                            LiveShutter.this.setTimer(9);
                        }
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_photoSize);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_photo_size);
                    }
                    if (!RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                        LiveShutter.this.setTimer(2);
                        LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Set Resolution Action End");
                    break;
                case 6:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Zoom In Action Start");
                    LiveShutter.this.setButtonEnabled(false);
                    LiveShutter.this.setShutterButtonEnabled(false);
                    break;
                case 7:
                    boolean result3 = ((Boolean) msg.obj).booleanValue();
                    if (result3) {
                        LiveShutter.this.zoombardisplay();
                        LiveShutter.nDisplayFlag = 30;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                        if (!LiveShutter.this.isLongZoom || Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) >= Integer.parseInt(LiveShutter.this.mDeviceController.getMaxZoom())) {
                            LiveShutter.this.rvf_progress.setVisibility(8);
                            LiveShutter.this.actionEnd();
                            LiveShutter.this.setShutterButtonEnabled(true);
                        } else {
                            LiveShutter.this.doAction(3, "");
                        }
                    }
                    LiveShutter.this.setTimer(1);
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Zoom in Action End");
                    break;
                case 8:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Zoom Out Action Start");
                    LiveShutter.this.setButtonEnabled(false);
                    LiveShutter.this.setShutterButtonEnabled(false);
                    break;
                case 9:
                    boolean result4 = ((Boolean) msg.obj).booleanValue();
                    if (result4) {
                        LiveShutter.this.zoombardisplay();
                        LiveShutter.nDisplayFlag = 30;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                        if (!LiveShutter.this.isLongZoom || Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) <= 0) {
                            LiveShutter.this.rvf_progress.setVisibility(8);
                            LiveShutter.this.actionEnd();
                            LiveShutter.this.setShutterButtonEnabled(true);
                        } else {
                            LiveShutter.this.doAction(4, "");
                        }
                    }
                    LiveShutter.this.setTimer(1);
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Zoom Out Action End");
                    break;
                case 10:
                    LiveShutter.this.actionStart();
                    break;
                case 11:
                    boolean result5 = ((Boolean) msg.obj).booleanValue();
                    if (!result5) {
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 12:
                    LiveShutter.this.actionStart();
                    break;
                case 13:
                    boolean result6 = ((Boolean) msg.obj).booleanValue();
                    LiveShutter.this.hideProgressBar();
                    if (!result6) {
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 15:
                    boolean result7 = ((Boolean) msg.obj).booleanValue();
                    if (!result7) {
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    }
                    break;
                case 17:
                    boolean result8 = ((Boolean) msg.obj).booleanValue();
                    if (!result8) {
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 10L);
                    } else {
                        LiveShutter.this.zoombardisplay();
                        LiveShutter.nDisplayFlag = 30;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                        LiveShutter.this.hideProgressBar();
                    }
                    LiveShutter.this.setShutterButtonEnabled(true);
                    LiveShutter.this.updateZoomButtonEnabled();
                    LiveShutter.this.rvf_progress.setVisibility(8);
                    LiveShutter.this.actionEnd();
                    break;
                case 18:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : AF Action Start");
                    if (LiveShutter.this.isInitializedCamera) {
                        LiveShutter.this.actionStart();
                    }
                    break;
                case 19:
                    if (LiveShutter.this.isInitializedCamera) {
                        boolean result9 = ((Boolean) msg.obj).booleanValue();
                        Action action2 = LiveShutter.this.mDeviceController.getAction(9);
                        Trace.d(LiveShutter.this.TAG, "result : " + result9);
                        if (result9) {
                            ArgumentList outArgList2 = action2.getOutputArgumentList();
                            int nOutArgs2 = outArgList2.size();
                            for (int n2 = 0; n2 < nOutArgs2; n2++) {
                                Argument outArg2 = outArgList2.getArgument(n2);
                                String name2 = outArg2.getName();
                                String value2 = outArg2.getValue();
                                Trace.d(LiveShutter.this.TAG, "name : " + name2 + " value : " + value2);
                                if (!name2.equals("AF_MF") && name2.equals("AFSTATUS")) {
                                    if (!value2.equals("0")) {
                                        if (value2.equals("3")) {
                                            LiveShutter.this.bAFFAIL = false;
                                        } else {
                                            LiveShutter.this.bAFFAIL = false;
                                            LiveShutter.this.AFData = value2.split(",");
                                            try {
                                                Thread.sleep(500L);
                                                break;
                                            } catch (Throwable th) {
                                            }
                                            Thread.yield();
                                            if (LiveShutter.nShutterDnUp == 0) {
                                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                            }
                                        }
                                    } else {
                                        LiveShutter.this.bAFFAIL = true;
                                        try {
                                            Thread.sleep(500L);
                                            break;
                                        } catch (Throwable th2) {
                                        }
                                        Thread.yield();
                                        if (LiveShutter.nShutterDnUp == 0) {
                                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                        }
                                    }
                                    LiveShutter.this.setTimer(10);
                                }
                            }
                        } else {
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(10));
                        }
                        if (!LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") && LiveShutter.nShutterDnUp == 1) {
                            LiveShutter.this.timerdisplay();
                        }
                    } else {
                        LiveShutter.this.doAction(13, "");
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : AF Action End");
                    break;
                case 20:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : AF Action Start");
                    if (LiveShutter.this.isInitializedCamera) {
                        LiveShutter.this.actionStart();
                    }
                    break;
                case 21:
                    if (LiveShutter.this.isInitializedCamera) {
                        boolean result10 = ((Boolean) msg.obj).booleanValue();
                        Action action3 = LiveShutter.this.mDeviceController.getAction(10);
                        Trace.d(LiveShutter.this.TAG, "result : " + result10);
                        if (result10) {
                            ArgumentList outArgList3 = action3.getOutputArgumentList();
                            int nOutArgs3 = outArgList3.size();
                            for (int n3 = 0; n3 < nOutArgs3; n3++) {
                                Argument outArg3 = outArgList3.getArgument(n3);
                                String name3 = outArg3.getName();
                                String value3 = outArg3.getValue();
                                if (LiveShutter.nShutterDnUp != -1) {
                                    Trace.d(LiveShutter.this.TAG, "name : " + name3 + " value : " + value3);
                                    if (name3.equals("AF_MF")) {
                                        LiveShutter.this.AfFrameType = 0;
                                    } else if (value3.equals("3")) {
                                        LiveShutter.this.AfFrameType = 0;
                                        Trace.d(LiveShutter.this.TAG, "Manual Focus");
                                    } else if (name3.equals("AFSTATUS") && value3.equals("AFEXTEND_OK")) {
                                        LiveShutter.this.AfFrameType = 4;
                                        LiveShutter.this.bAFFAIL = false;
                                        if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") || LiveShutter.nShutterDnUp == 0) {
                                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                        }
                                    } else if (name3.equals("AFSTATUS") && value3.equals("AFEXTEND_FAIL")) {
                                        LiveShutter.this.AfFrameType = 4;
                                        LiveShutter.this.bAFFAIL = true;
                                        if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") || LiveShutter.nShutterDnUp == 0) {
                                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                        }
                                    } else if (!name3.equals("AFSTATUS") || value3.equals("AFFAIL") || value3.equals("0")) {
                                        LiveShutter.this.AfFrameType = 1;
                                        LiveShutter.this.bAFFAIL = true;
                                        if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") || LiveShutter.nShutterDnUp == 0) {
                                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                        }
                                    } else {
                                        LiveShutter.this.AfFrameType = 1;
                                        LiveShutter.this.bAFFAIL = false;
                                        LiveShutter.this.AFData = value3.split(",");
                                        if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") || LiveShutter.nShutterDnUp == 0) {
                                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(6));
                                        }
                                    }
                                    LiveShutter.this.setTimer(10);
                                }
                            }
                        } else {
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(10));
                        }
                        if (!LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
                            if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                                if (LiveShutter.nShutterDnUp != 0 || (LiveShutter.this.bAFFAIL && RVFFunctionManager.isCancellableShotByAFFail(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid))) {
                                    LiveShutter.this.mShotStateEx = 0;
                                    Trace.d(LiveShutter.this.TAG, "do action RELEASE_SELF_TIMER 08");
                                    LiveShutter.this.mShotState = 0;
                                    LiveShutter.this.resetVisibilityTouchAFFrame();
                                    LiveShutter.this.doAction(13, "");
                                } else {
                                    LiveShutter.this.mShotStateEx = 5;
                                    LiveShutter.this.mShotState = 3;
                                    LiveShutter.this.doAction(30, "");
                                }
                            } else if (LiveShutter.nShutterDnUp != 0) {
                                if (LiveShutter.nShutterDnUp != 1 || (LiveShutter.this.bAFFAIL && RVFFunctionManager.isCancellableShotByAFFail(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid))) {
                                    LiveShutter.this.mShotStateEx = 0;
                                    Trace.d(LiveShutter.this.TAG, "do action RELEASE_SELF_TIMER 11");
                                    LiveShutter.this.mShotState = 0;
                                    LiveShutter.this.resetVisibilityTouchAFFrame();
                                    LiveShutter.this.doAction(13, "");
                                } else {
                                    LiveShutter.this.timerdisplay();
                                }
                            }
                        }
                    } else {
                        LiveShutter.this.doAction(13, "");
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : AF Action End");
                    break;
                case 22:
                    LiveShutter.this.actionStart();
                    break;
                case 23:
                    boolean result11 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result11);
                    if (result11) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_touchAF);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_touch_af);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 24:
                    if (LiveShutter.this.isInitializedCamera) {
                        LiveShutter.this.actionStart();
                    }
                    break;
                case 25:
                    if (LiveShutter.this.isInitializedCamera) {
                        boolean result12 = ((Boolean) msg.obj).booleanValue();
                        Action action4 = LiveShutter.this.mDeviceController.getAction(12);
                        Trace.d(LiveShutter.this.TAG, "result : " + result12);
                        if (result12) {
                            if (action4.getArgumentValue("TOUCHAF_RESULT").equals("AFOK")) {
                                LiveShutter.this.bAFFAIL = false;
                                LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_g);
                            } else {
                                LiveShutter.this.bAFFAIL = true;
                                LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_r);
                            }
                        }
                        if (LiveShutter.nShutterDnUp == 1 && !LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") && !LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS") && LiveShutter.isShotProcessing) {
                            LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            LiveShutter.this.timerdisplay();
                        }
                        LiveShutter.this.setShutterButtonEnabled(true);
                        LiveShutter.this.actionEnd();
                        if (!LiveShutter.this.bAFFAIL && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                            if (LiveShutter.nShutterDnUp == 0) {
                                LiveShutter.this.mHandleTimer.removeMessages(4);
                                LiveShutter.this.mShotStateEx = 5;
                                LiveShutter.this.mShotState = 3;
                                LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                                LiveShutter.this.doAction(30, "");
                            } else if (LiveShutter.nShutterDnUp == 1) {
                                LiveShutter.this.mShotState = 0;
                                LiveShutter.this.resetVisibilityTouchAFFrame();
                                LiveShutter.this.doAction(13, "");
                            }
                            break;
                        }
                    } else {
                        LiveShutter.this.actionEnd();
                        LiveShutter.this.doAction(13, "");
                        break;
                    }
                    break;
                case 26:
                    if (LiveShutter.this.isInitializedCamera) {
                        LiveShutter.this.nTimerCount = 0;
                        if (LiveShutter.this.rvf_timerCountMain != null) {
                            LiveShutter.this.rvf_timerCountMain.setVisibility(8);
                            LiveShutter.bshutter = false;
                        }
                    }
                    break;
                case 27:
                    if (LiveShutter.this.isOperationStateChange) {
                        if (LiveShutter.this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("EXTERNAL")) {
                            LiveShutter.this.mHandleTimer.removeMessages(5);
                            LiveShutter.this.codec_stop();
                            FFmpegJNI.destruct();
                            Intent intent = new Intent(LiveShutter.this, (Class<?>) FileSharing.class);
                            intent.putExtra("requestServiceChange", "changeToML");
                            LiveShutter.this.startActivity(intent);
                            LiveShutter.this.finish();
                        }
                    } else {
                        if (!LiveShutter.this.isInitializedCamera) {
                            LiveShutter.nDisplayFlag = 16;
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                        } else {
                            LiveShutter.isShotProcessing = false;
                            LiveShutter.this.resetVisibilityTouchAFFrame();
                        }
                        LiveShutter.this.actionEnd();
                    }
                    break;
                case 28:
                case 30:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Shot Action Start");
                    String shutterSpeedValue = LiveShutter.this.mDeviceController.getShutterSpeedValue().replaceAll("\"", "");
                    if (Utils.isNumeric(shutterSpeedValue) && Integer.parseInt(shutterSpeedValue) >= 5) {
                        LiveShutter.this.mHandleTimer.removeMessages(5);
                        LiveShutter.this.setTimer(6);
                    }
                    LiveShutter.this.actionStart();
                    break;
                case 29:
                case 31:
                    boolean result13 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result13);
                    if (!result13) {
                        LiveShutter.this.mShotState = 0;
                        LiveShutter.isShotProcessing = false;
                        if (!LiveShutter.bClosing) {
                            int err = LiveShutter.this.mDeviceController.getAction(14).getControlResponse().getUPnPErrorCode();
                            if (err == 803) {
                                LiveShutter.nDisplayFlag = Const.MsgId.SHOT_FAILED;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            } else {
                                LiveShutter.nDisplayFlag = 10;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            }
                        }
                    } else if (LiveShutter.this.mDoubleTimerShotState != 2 || !LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("DOUBLE")) {
                        LiveShutter.isShotProcessing = false;
                        LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                        if (!LiveShutter.bClosing) {
                            if (!LiveShutter.this.mDeviceController.getAFShotResult().equals("")) {
                                boolean bSupportedSave = true;
                                if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                                    ArrayList<DSCMenuItem> dimMenuItems = LiveShutter.this.mDeviceController.getFileSaveMenuItemsDim();
                                    for (int i2 = 0; i2 < dimMenuItems.size(); i2++) {
                                        DSCMenuItem item = dimMenuItems.get(i2);
                                        if (item.getName().toUpperCase(Locale.ENGLISH).equals("DRIVE") && item.getValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                                            bSupportedSave = false;
                                        }
                                    }
                                }
                                Trace.d(LiveShutter.this.TAG, "bSupportedSave : " + bSupportedSave + " nCameraSaveSelect : " + LiveShutter.this.nCameraSaveSelect);
                                if (!bSupportedSave || LiveShutter.this.nCameraSaveSelect != 0 || LiveShutter.this.mDeviceController.getQualityValue().equals("RAW")) {
                                    LiveShutter.this.mShotState = 0;
                                    new ShotPrepareTask(LiveShutter.this, null).execute(new String[0]);
                                } else {
                                    LiveShutter.this.mShotState = 4;
                                    new ImageDownloadTask(LiveShutter.this, null).execute(LiveShutter.this.mDeviceController.getAFShotResult());
                                }
                            }
                            LiveShutter.this.bshotrunflag = false;
                        }
                    } else {
                        LiveShutter.isShotProcessing = false;
                        LiveShutter.this.shutterbutton(true);
                        if (LiveShutter.this.nCameraSaveSelect == 0) {
                            LiveShutter.this.mImageDownloader.downloadImage(LiveShutter.this.mDeviceController.getAFShotResult());
                        }
                        if (LiveShutter.this.streampro != null && LiveShutter.this.streampro.isShowing()) {
                            LiveShutter.this.streampro.dismiss();
                            LiveShutter.this.removeDialog(1010);
                        }
                        LiveShutter.this.mDoubleTimerShotState = 1;
                        LiveShutter.this.startLEDTimeDisplay(2);
                    }
                    LiveShutter.this.mHandleTimer.removeMessages(6);
                    if (LiveShutter.this.isDCFFull()) {
                        LiveShutter.this.showDialog(1009);
                    } else if (!LiveShutter.this.isAvailShot()) {
                        LiveShutter.this.showMemoryFullDialog(1);
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Shot Action End");
                    break;
                case 32:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Set Led Action Start");
                    LiveShutter.this.actionStart();
                    break;
                case 33:
                    boolean result14 = ((Boolean) msg.obj).booleanValue();
                    if (!result14) {
                        Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_NETERRORMSG");
                        LiveShutter.nDisplayFlag = 10;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                    } else {
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_drive);
                        LiveShutter.this.setImageResource(LiveShutter.rvf_timer_button);
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_drive);
                            LiveShutter.this.setTimer(9);
                        }
                    }
                    if (!RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                        LiveShutter.this.setTimer(2);
                        LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Set Led Action End");
                    break;
                case 34:
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Flash set Action Start");
                    LiveShutter.this.actionStart();
                    break;
                case 35:
                    boolean result15 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result15);
                    Action action5 = LiveShutter.this.mDeviceController.getAction(17);
                    Trace.d(LiveShutter.this.TAG, "action.getOutputArgumentList().size() : " + action5.getOutputArgumentList().size());
                    if (result15) {
                        LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_flash);
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            LiveShutter.this.setTimer(9);
                        }
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_flash);
                    } else {
                        UPnPStatus err1 = action5.getStatus();
                        String err2 = action5.getControlStatus().getDescription();
                        int err3 = action5.getControlStatus().getCode();
                        Trace.d(LiveShutter.this.TAG, "err.getCode() : " + err1.getCode() + HTTP.TAB + " err.getDescription() : " + err1.getDescription());
                        Trace.d(LiveShutter.this.TAG, "err2 : " + err2);
                        Trace.d(LiveShutter.this.TAG, "err3 : " + err3);
                        if (err3 == 800) {
                            if (!action5.getArgumentValue("FLASHMODE").equals(DeviceController.FlashMode.OFF)) {
                                LiveShutter.nDisplayFlag = Const.MsgId.DISPLAY_OPEN_FLASH;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            }
                        } else if (err3 == 802) {
                            LiveShutter.nDisplayFlag = Const.MsgId.DISPLAY_OPEN_FLASH;
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                case 2:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.OFF);
                                    break;
                                default:
                                    LiveShutter.this.mDeviceController.setCurrentFlashDisplay("off");
                                    break;
                            }
                            LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                        } else if (err1.getCode() != 0) {
                            if (err3 == 501) {
                                switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                    case 2:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.OFF);
                                        break;
                                    default:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay("off");
                                        break;
                                }
                                LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                            } else {
                                Trace.e(LiveShutter.this.TAG, "DISPLAY_NETERRORMSG 11" + err1.toString());
                                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_NETERRORMSG");
                                LiveShutter.nDisplayFlag = 10;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                            }
                        }
                    }
                    if (!RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                        LiveShutter.this.setTimer(2);
                        LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    }
                    LiveShutter.this.actionEnd();
                    Trace.d(LiveShutter.this.TAG, "Performance Check Point : Flash set Action End");
                    break;
                case 36:
                    LiveShutter.this.actionStart();
                    break;
                case 37:
                    boolean result16 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result16);
                    if (result16) {
                        new RestartCodecTask(LiveShutter.this, null).execute(new Integer[0]);
                    }
                    if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                        if (LiveShutter.this.mSmartPanelCustomDialogList != null) {
                            LiveShutter.this.setTimer(9);
                        }
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_streaming_quality);
                    } else {
                        LiveShutter.this.setTimer(3);
                        LiveShutter.this.mSubOptionMenuListAdapter.notifyDataSetChanged();
                        LiveShutter.this.mMainOptionMenuListAdapter.notifyDataSetChanged();
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 38:
                    LiveShutter.this.actionStart();
                    LiveShutter.this.showDialog(1016);
                    break;
                case 39:
                    boolean result17 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result17);
                    if (result17) {
                        new StartRecordTask(LiveShutter.this, null).execute(new Void[0]);
                    } else {
                        LiveShutter.this.dismissDialog(1016);
                    }
                    break;
                case 42:
                    LiveShutter.this.actionStart();
                    break;
                case 43:
                    boolean result18 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result18);
                    if (result18) {
                        LiveShutter.this.setSmartPanel();
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 44:
                    LiveShutter.this.actionStart();
                    break;
                case 45:
                    boolean result19 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result19);
                    if (result19) {
                        LiveShutter.this.setSmartPanel();
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 46:
                    LiveShutter.this.actionStart();
                    break;
                case 47:
                    boolean result20 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result20);
                    if (result20) {
                        LiveShutter.this.setSmartPanel();
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 48:
                    LiveShutter.this.actionStart();
                    break;
                case 49:
                    boolean result21 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result21);
                    if (result21) {
                        LiveShutter.this.setSmartPanel();
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 50:
                    LiveShutter.this.actionStart();
                    break;
                case 51:
                    boolean result22 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result22);
                    if (result22) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_wb);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_wb);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 52:
                    LiveShutter.this.actionStart();
                    break;
                case 53:
                    boolean result23 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result23);
                    if (result23) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_metering);
                        if (!LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF") || !LiveShutter.this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                            LiveShutter.rvf_metering_spot_frame.setVisibility(4);
                        } else {
                            LiveShutter.rvf_metering_spot_frame.setVisibility(0);
                        }
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_metering);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 54:
                    LiveShutter.this.actionStart();
                    break;
                case 55:
                    boolean result24 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result24);
                    if (result24) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afMode);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.SET_AF_AREA_ACTION_START /* 56 */:
                    LiveShutter.this.actionStart();
                    break;
                case DeviceController.ActionStatus.SET_AF_AREA_ACTION_END /* 57 */:
                    boolean result25 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result25);
                    if (result25) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afArea);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_af_area);
                        if (!LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF") || LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO) || !LiveShutter.this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                            LiveShutter.rvf_metering_spot_frame.setVisibility(4);
                        } else {
                            LiveShutter.rvf_metering_spot_frame.setVisibility(0);
                        }
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.SET_DRIVE_ACTION_START /* 58 */:
                    LiveShutter.this.actionStart();
                    break;
                case DeviceController.ActionStatus.SET_DRIVE_ACTION_END /* 59 */:
                    boolean result26 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result26);
                    if (result26) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_drive);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_drive);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_storage);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 60:
                    LiveShutter.this.showDialog(1010);
                    break;
                case 61:
                    boolean result27 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result27);
                    LiveShutter.this.mHandleTimer.removeMessages(5);
                    break;
                case 63:
                    boolean result28 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result28);
                    if (result28) {
                        LiveShutter.this.mShotState = 0;
                        new ShotPrepareTask(LiveShutter.this, null).execute(new String[0]);
                    }
                    LiveShutter.this.mShotState = 0;
                    LiveShutter.isShotProcessing = false;
                    LiveShutter.this.actionEnd();
                    break;
                case 64:
                    LiveShutter.this.actionStart();
                    break;
                case DeviceController.ActionStatus.SET_DIAL_MODE_ACTION_END /* 65 */:
                    boolean result29 = ((Boolean) msg.obj).booleanValue();
                    Trace.d(LiveShutter.this.TAG, "result : " + result29);
                    if (result29) {
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            Action action6 = LiveShutter.this.mDeviceController.getAction(32);
                            if (action6.getArgumentValue("RatioChangeStatus").equals(Service.MAJOR_VALUE)) {
                                if (LiveShutter.this.mDeviceController.getQualityValue().equals("RAW")) {
                                    new RestartCodecTask(LiveShutter.this, null).execute(3);
                                } else {
                                    new RestartCodecTask(LiveShutter.this, null).execute(new Integer[0]);
                                }
                            } else if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).contains("CONTINUOUS")) {
                                LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
                            }
                        }
                        if (!LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                            if (!LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("P")) {
                                if (!LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("A")) {
                                    if (!LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("S")) {
                                        if (LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("M")) {
                                            LiveShutter.rvf_title.setText("Manual");
                                        }
                                    } else {
                                        LiveShutter.rvf_title.setText("Shutter Priority");
                                    }
                                } else {
                                    LiveShutter.rvf_title.setText("Aperture Priority");
                                }
                            } else {
                                LiveShutter.rvf_title.setText("Program");
                            }
                        } else {
                            LiveShutter.rvf_title.setText("Auto");
                        }
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            LiveShutter.this.setSmartPanel();
                            if (!LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF") || LiveShutter.this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO) || !LiveShutter.this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                                LiveShutter.rvf_metering_spot_frame.setVisibility(4);
                            } else {
                                LiveShutter.rvf_metering_spot_frame.setVisibility(0);
                            }
                        }
                        if (LiveShutter.this.mDeviceController.getAFAreaMenuItems().size() <= 0 || !LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                            if (LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                                LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            }
                        } else {
                            LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                            LiveShutter.rvf_touchAutoFocus.setVisibility(0);
                        }
                    }
                    LiveShutter.this.mSmartPanelCustomDialogWheel.setCancelable(true);
                    LiveShutter.this.setIndicator();
                    LiveShutter.this.updateImageOfGallaryButton();
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.SET_QUALITY_ACTION_END /* 67 */:
                    boolean result30 = ((Boolean) msg.obj).booleanValue();
                    if (result30) {
                        if (LiveShutter.this.getSmartPanelCustomDialogListMenuId() != 0) {
                            LiveShutter.this.setTimer(9);
                        }
                        if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                            Action action7 = LiveShutter.this.mDeviceController.getAction(33);
                            if (action7.getArgumentValue("RatioChangeStatus").equals(Service.MAJOR_VALUE)) {
                                if (LiveShutter.this.mDeviceController.getQualityValue().equals("RAW")) {
                                    new RestartCodecTask(LiveShutter.this, null).execute(3);
                                } else {
                                    new RestartCodecTask(LiveShutter.this, null).execute(new Integer[0]);
                                }
                            }
                        }
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_quality);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_quality);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_photo_size);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_storage);
                    }
                    LiveShutter.this.setSmartPanel();
                    if (!LiveShutter.this.isAvailShot() && LiveShutter.this.getSmartPanelVisibility() != 0) {
                        LiveShutter.this.showMemoryFullDialog(1);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.SET_MOVIE_RESOLUTION_ACTION_END /* 69 */:
                    boolean result31 = ((Boolean) msg.obj).booleanValue();
                    if (result31) {
                        LiveShutter.this.setTimer(9);
                        LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_movieSize);
                        LiveShutter.this.setIndicator(R.id.rvf_indicator_movie_size);
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 71:
                    boolean result32 = ((Boolean) msg.obj).booleanValue();
                    if (result32) {
                        if (LiveShutter.this.mDeviceController.getStreamUrlMenuItems().size() <= 2) {
                            LiveShutter.this.showPopupWindow(2);
                        }
                        int err4 = LiveShutter.this.mDeviceController.getAction(35).getControlResponse().getUPnPErrorCode();
                        if (err4 != 804) {
                            LiveShutter.this.mHandleTimer.removeMessages(7);
                            LiveShutter.this.dismissDialog(1016);
                            LiveShutter.this.startRecord();
                        } else {
                            LiveShutter.this.dismissDialog(1016);
                            LiveShutter.this.showDialog(Const.MsgBoxId.MSGBOX_CAMERA_RECORD_ERROR_CODEC_ERROR);
                            LiveShutter.this.doAction(36, "");
                        }
                    } else {
                        int err5 = LiveShutter.this.mDeviceController.getAction(35).getControlResponse().getUPnPErrorCode();
                        if (err5 != 804) {
                            LiveShutter.this.mHandleTimer.removeMessages(7);
                            LiveShutter.this.dismissDialog(1016);
                            LiveShutter.this.startRecord();
                        } else {
                            LiveShutter.this.dismissDialog(1016);
                            LiveShutter.this.showDialog(Const.MsgBoxId.MSGBOX_CAMERA_RECORD_ERROR_CODEC_ERROR);
                            LiveShutter.this.doAction(36, "");
                        }
                    }
                    LiveShutter.this.actionEnd();
                    break;
                case 72:
                    LiveShutter.this.rvf_camera_btn_af.setVisibility(8);
                    LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                    LiveShutter.this.mDeviceController.setTouchAFMovie(false);
                    if (LiveShutter.this.recordTimer != null) {
                        LiveShutter.this.recordTimer.interrupt();
                        LiveShutter.this.recordTimer = null;
                    }
                    break;
                case 73:
                    if (LiveShutter.this.mDeviceController.getStreamUrlMenuItems().size() <= 2) {
                        LiveShutter.this.hidePopupWindow(2);
                    }
                    LiveShutter.this.setTimer(7);
                    LiveShutter.this.doAction(18, LiveShutter.this.mDeviceController.getCurrentStreamQuality());
                    if (LiveShutter.this.isAvailShot()) {
                        if (!LiveShutter.this.isAvailMovieRecording()) {
                            LiveShutter.this.showMemoryFullDialog(0);
                        }
                    } else {
                        LiveShutter.this.showMemoryFullDialog(1);
                    }
                    break;
                case 74:
                    LiveShutter.this.actionStart();
                    break;
                case DeviceController.ActionStatus.SET_FILE_SAVE_ACTION_END /* 75 */:
                    if (LiveShutter.this.nCameraSaveSelect == 0) {
                        if (RVFFunctionManager.getSaveGuideDialogType(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid) != 0) {
                            LiveShutter.this.showDialog(Const.MsgBoxId.MSGBOX_SMARTPHONE_SAVE_RESOLUTION_GUIDE);
                        }
                        if (CommonUtils.isMemoryFull()) {
                            LiveShutter.this.mExToast.show(7);
                            LiveShutter.this.setShutterButtonEnabled(false);
                        }
                    } else {
                        LiveShutter.this.setShutterButtonEnabled(true);
                    }
                    LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_save);
                    LiveShutter.this.setIndicator(R.id.rvf_indicator_storage);
                    LiveShutter.this.updateImageOfGallaryButton();
                    LiveShutter.this.setTimer(9);
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.CANCEL_TOUCH_AF_MOVIE_ACTION_END /* 79 */:
                    LiveShutter.this.rvf_camera_btn_af.setVisibility(8);
                    LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                    LiveShutter.this.mDeviceController.setTouchAFMovie(false);
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.TOUCH_AF_MOVIE_ACTION_END /* 81 */:
                    LiveShutter.rvf_touchAutoFocus.setVisibility(0);
                    LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                    LiveShutter.this.rvf_camera_btn_af.setVisibility(0);
                    LiveShutter.this.mDeviceController.setTouchAFMovie(true);
                    LiveShutter.this.actionEnd();
                    break;
                case DeviceController.ActionStatus.SET_OPERATION_STATE_ACTION_END /* 87 */:
                    LiveShutter.this.actionEnd();
                    break;
            }
        }
    };
    private int nConnectCount = 0;
    private int nSaveCount = 0;
    private int nZoomGap = 0;
    private int nAFGap = 0;
    private long zoomtime = 0;
    private long shotcanceltime = 0;
    private java.util.Timer mTimer = null;
    private boolean bStart = false;
    private int mActionState = 0;
    private boolean bAFFAIL = false;
    private boolean btimercount = false;
    private boolean btoast = false;
    private boolean bpopupflag = false;
    private boolean bshotrunflag = false;
    private boolean btoastflag = false;
    private boolean bOnZoomProcess = false;
    int nCurShowDlg = -1;
    private float gDensity = 0.0f;
    private int gDeviceWidth = 0;
    private int gDeviceHeight = 0;
    private int gWidthDips = 0;
    private int gHeightDips = 0;
    private boolean isExitByBackPressed = false;
    private String versionName = "";
    private DialogInterface.OnCancelListener progDiaCancelListener = new DialogInterface.OnCancelListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.6
        private DialogInterface.OnCancelListener progDiaCancelListener = new DialogInterface.OnCancelListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.6.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                Trace.d(LiveShutter.this.TAG, "progDiaCancelListener1");
                LiveShutter.this.showDialog(1011);
                LiveShutter.this.holderremove();
                LiveShutter.this.appclose();
            }
        };

        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            Trace.d(LiveShutter.this.TAG, "progDiaCancelListener2");
            LiveShutter.this.showDialog(1011);
            LiveShutter.this.holderremove();
            LiveShutter.this.appclose();
        }
    };
    private int positionX = 50;
    private int positionY = 50;
    private int mFrameWidth = 0;
    private int mFrameHeight = 0;
    private int mOrientationWhenCheckMargin = 1;
    private int mMarginLeft = -1;
    private int mMarginTop = -1;
    private int[][] screenPositoin = new int[4][];
    private int[][] screenSize = new int[4][];
    private boolean isSettedLiveStream = false;
    private boolean isByeBye = false;
    private OnWheelScrollListener mSmartPanelCustomDialogWheelMenuWheelScrollListener = new OnWheelScrollListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.7
        @Override // org.kankan.wheel.widget.OnWheelScrollListener
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override // org.kankan.wheel.widget.OnWheelScrollListener
        public void onScrollingFinished(WheelView wheel) {
            if (LiveShutter.this.mSmartPanelCustomDialogWheelMenuId == 32) {
                LiveShutter.this.mHandleTimer.removeMessages(4);
                LiveShutter.rvf_touchAutoFocus.setVisibility(4);
            }
            switch (LiveShutter.this.mSmartPanelCustomDialogWheelMenuId) {
                case 21:
                    if (!LiveShutter.this.mDeviceController.getShutterSpeedValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
                    }
                    break;
                case 22:
                    if (!LiveShutter.this.mDeviceController.getApertureValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
                    }
                    break;
                case 23:
                    if (!LiveShutter.this.mDeviceController.getEVValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
                    }
                    break;
                case 24:
                    if (!LiveShutter.this.mDeviceController.getISOValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
                    }
                    break;
                case 32:
                    if (!LiveShutter.this.mDeviceController.getDialModeValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()))) {
                        LiveShutter.this.mSmartPanelCustomDialogWheel.setCancelable(false);
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
                    }
                    break;
            }
            LiveShutter.this.mSmartPanelCustomDialogWheel.setValue((String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
        }
    };
    private OnWheelClickedListener mSmartPanelCustomDialogWheelMenuWheelClickedListener = new OnWheelClickedListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.8
        @Override // org.kankan.wheel.widget.OnWheelClickedListener
        public void onItemClicked(WheelView wheel, int itemIndex) {
            wheel.setCurrentItem(itemIndex);
            switch (LiveShutter.this.mSmartPanelCustomDialogWheelMenuId) {
                case 21:
                    if (!LiveShutter.this.mDeviceController.getShutterSpeedValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex));
                    }
                    break;
                case 22:
                    if (!LiveShutter.this.mDeviceController.getApertureValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex));
                    }
                    break;
                case 23:
                    if (!LiveShutter.this.mDeviceController.getEVValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex));
                    }
                    break;
                case 24:
                    if (!LiveShutter.this.mDeviceController.getISOValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex))) {
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex));
                    }
                    break;
                case 32:
                    if (!LiveShutter.this.mDeviceController.getDialModeValue().equals(LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex))) {
                        LiveShutter.this.mSmartPanelCustomDialogWheel.setCancelable(false);
                        LiveShutter.this.doAction(LiveShutter.this.mSmartPanelCustomDialogWheelMenuId, (String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(itemIndex));
                    }
                    break;
            }
            LiveShutter.this.mSmartPanelCustomDialogWheel.setValue((String) LiveShutter.this.mSmartPanelCustomDialogWheelMenuItems.get(wheel.getCurrentItem()));
        }
    };
    private int surfaceViewWidth = 0;
    private int surfaceViewHeight = 0;
    private boolean bCloseByFinishSafe = false;

    private static class TimerMessageID {
        private static final int ACTION_WAIT_TIME = 5;
        private static final int ACTION_WAIT_TIME_LONG = 6;
        private static final int APP_CLOSE = 8;
        private static final int DISPLAY_APP_CLOSE_MESSAGE = 7;
        private static final int HIDE_AUTO_FOCUS_FRAME = 10;
        private static final int HIDE_CUSTOM_DIALOG = 9;
        private static final int HIDE_TOAST_MESSAGE = 12;
        private static final int OPTION_MENU_CLOSE = 2;
        private static final int OSD_CLOSE = 1;
        private static final int SUB_MENU_CLOSE = 3;
        private static final int TOUCH_AUTO_FOCUS_FRAME_CLOSE = 4;
        private static final int UPDATE_LED_TIME_DISPLAY = 11;

        private TimerMessageID() {
        }
    }

    private static class AutoFocusFrameType {
        private static final int EXTEND = 4;
        private static final int MATRIX = 1;
        private static final int METERING_SPOT = 3;
        private static final int NONE = 0;
        private static final int TOUCH = 2;

        private AutoFocusFrameType() {
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Trace.i(this.TAG, "Performance Check Point : start onCreate()");
        Trace.d(this.TAG, "**** liveshutter oncreate");
        super.onCreate(savedInstanceState);
        this.mDestroyed = false;
        CMService.ACTIVE_SERVICE = 2;
        init();
        setTimer(7);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        bNoTelephonyDevice = Utils.CheckTelephonyDevice(Build.MODEL);
        mExistStatusBarDeviceForTablet = Utils.CheckexistStatusBarDeviceForTablet(Build.MODEL);
        Trace.d(this.TAG, "model name : " + Build.MODEL + ",is No telephony device: " + bNoTelephonyDevice);
        this.vibe = (Vibrator) getSystemService("vibrator");
        Utils.setUseragent(getApplicationContext());
        Trace.d(this.TAG, "language : " + getResources().getConfiguration().locale.getLanguage());
        Appcontext = getApplicationContext();
        msgHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.9
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Trace.d(LiveShutter.this.TAG, "msg : " + msg);
                switch (msg.what) {
                    case 126:
                        if (!((String) msg.obj).equals("NULL")) {
                            String strDirection = "";
                            if (((String) msg.obj).equals("D")) {
                                strDirection = "ssdp:rotationD";
                            } else if (((String) msg.obj).equals("L")) {
                                strDirection = "ssdp:rotationL";
                            } else if (((String) msg.obj).equals("U")) {
                                strDirection = "ssdp:rotationU";
                            } else if (((String) msg.obj).equals("R")) {
                                strDirection = "ssdp:rotationR";
                            }
                            if (!strDirection.equals(LiveShutter.this.cameratype)) {
                                LiveShutter.this.cameratype = strDirection;
                                LiveShutter.this.checkphonecamtype();
                                if (LiveShutter.this.getPhoneCamType() == 2 || LiveShutter.this.getPhoneCamType() == 1) {
                                    LiveShutter.this.CheckLayout();
                                }
                            }
                        }
                        break;
                    case 127:
                        String value = (String) msg.obj;
                        if (!value.equals("NULL")) {
                            if (!Utils.isNumeric(value)) {
                                LiveShutter.this.mDeviceController.setCurrentFlashDisplay(value);
                            } else if (Integer.parseInt(value) == 0) {
                                switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                    case 2:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.OFF);
                                        break;
                                    default:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay("off");
                                        break;
                                }
                            } else {
                                switch (RVFFunctionManager.getFlashProtocolValueType(LiveShutter.this.connectedSsid)) {
                                    case 2:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay(DeviceController.FlashMode.AUTO);
                                        break;
                                    default:
                                        LiveShutter.this.mDeviceController.setCurrentFlashDisplay("auto");
                                        break;
                                }
                            }
                            if (RVFFunctionManager.isSupportedSmartPanel(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion())) {
                                LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_flash);
                            } else {
                                LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                            }
                            LiveShutter.this.setIndicator(R.id.rvf_indicator_flash);
                        }
                        break;
                    case 128:
                        if (((Integer) msg.obj).intValue() == 0) {
                            LiveShutter.this.mDeviceController.setDefaultFocusState("af");
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afArea);
                            LiveShutter.this.rvf_smart_panel_afArea.setEnabled(true);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_touchAF);
                            LiveShutter.this.rvf_smart_panel_touchAF.setEnabled(true);
                            LiveShutter.this.resetVisibilityTouchAFFrame();
                        } else {
                            LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            LiveShutter.this.mDeviceController.setDefaultFocusState("mf");
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_afArea);
                            LiveShutter.this.rvf_smart_panel_afArea.setEnabled(false);
                            LiveShutter.this.setImageResource(LiveShutter.this.rvf_smart_panel_touchAF);
                            LiveShutter.this.rvf_smart_panel_touchAF.setEnabled(false);
                            if (LiveShutter.this.getSmartPanelCustomDialogListMenuId() == 11 || LiveShutter.this.getSmartPanelCustomDialogListMenuId() == 28) {
                                LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                                LiveShutter.this.mSmartPanelCustomDialogList.dismiss();
                            }
                            LiveShutter.this.resetVisibilityTouchAFFrame();
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE /* 129 */:
                        LiveShutter.this.mDeviceController.setShutterSpeedValue((String) msg.obj);
                        LiveShutter.this.setSmartPanel();
                        break;
                    case 130:
                        LiveShutter.this.mDeviceController.setApertureValue((String) msg.obj);
                        LiveShutter.this.setSmartPanel();
                        break;
                    case Const.MsgId.ON_CHANGED_EV_SETUP_VALUE /* 131 */:
                        LiveShutter.this.mDeviceController.setEVValue((String) msg.obj);
                        LiveShutter.this.setSmartPanel();
                        break;
                    case Const.MsgId.ON_CHANGED_REMAIN_SHOT_COUNT_VALUE /* 132 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            if (!((String) msg.obj).equals("")) {
                                LiveShutter.this.mDeviceController.setAvailShots((String) msg.obj);
                            }
                            if (LiveShutter.this.mShotStateEx != 5 || !LiveShutter.this.isAvailShot() || LiveShutter.this.isDCFFull()) {
                                if (LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS") && LiveShutter.this.mShotStateEx == 5) {
                                    LiveShutter.this.mShotStateEx = 6;
                                    LiveShutter.this.doAction(31, "");
                                }
                                LiveShutter.this.bexitflag = true;
                                LiveShutter.nDisplayFlag = 111;
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                                LiveShutter.isShotProcessing = false;
                                LiveShutter.this.bshotrunflag = false;
                                LiveShutter.this.mShotStateEx = 0;
                                LiveShutter.this.actionEnd();
                            }
                            if (LiveShutter.this.isDCFFull()) {
                                LiveShutter.this.showDialog(1009);
                            } else if (!LiveShutter.this.isAvailShot()) {
                                LiveShutter.this.showMemoryFullDialog(1);
                            }
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_LAST_PHOTO_URL_VALUE /* 133 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.setAFShotResult((String) msg.obj);
                            if (LiveShutter.this.nCameraSaveSelect == 0) {
                                LiveShutter.this.mImageDownloader.downloadImage(LiveShutter.this.mDeviceController.getAFShotResult());
                            } else {
                                new ShotPrepareTask(LiveShutter.this, null).execute(new String[0]);
                            }
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_CURRENT_ZOOM_VALUE /* 134 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.setDefaultZoom((String) msg.obj);
                            LiveShutter.this.showZoomBar();
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_APERTURE_LIST /* 135 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.getApertureMenuItems().clear();
                            for (String str : ((String) msg.obj).split(",")) {
                                LiveShutter.this.mDeviceController.getApertureMenuItems().add(str);
                            }
                            LiveShutter.this.setSmartPanel();
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_SHUTTER_SPEED_LIST /* 136 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.getShutterSpeedMenuItems().clear();
                            String[] items = ((String) msg.obj).split(",");
                            for (String str2 : items) {
                                LiveShutter.this.mDeviceController.getShutterSpeedMenuItems().add(str2);
                            }
                            LiveShutter.this.setSmartPanel();
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_MOVIE_RECORD_TIME /* 137 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.setMovieRecordTime((String) msg.obj);
                            LiveShutter.this.rvf_recording_time.setText(LiveShutter.this.getTimeFormat((String) msg.obj));
                            if (!((String) msg.obj).equals(LiveShutter.this.mDeviceController.getRemainRecTimeValue())) {
                                if (LiveShutter.this.recordTimer == null) {
                                    LiveShutter.this.recordTimer = new Timer(LiveShutter.msgHandler, Const.MsgId.ON_UPDATE_MOVIE_RECORD_TIME, 100);
                                    LiveShutter.this.recordTimer.start();
                                }
                            } else {
                                LiveShutter.this.doAction(36, "");
                            }
                        }
                        break;
                    case Const.MsgId.ON_CHANGED_REMAIN_REC_TIME_VALUE /* 138 */:
                        if (!((String) msg.obj).equals("NULL")) {
                            LiveShutter.this.mDeviceController.setRemainRecTimeValue((String) msg.obj);
                        }
                        break;
                    case Const.MsgId.ON_UPDATE_MOVIE_RECORD_TIME /* 139 */:
                        Trace.d(LiveShutter.this.TAG, "movie record time : " + msg.obj);
                        Long movieRecordTime = Long.valueOf(((Long) msg.obj).longValue() / 1000);
                        if (movieRecordTime.longValue() < Long.parseLong(LiveShutter.this.mDeviceController.getRemainRecTimeValue())) {
                            LiveShutter.this.mDeviceController.setMovieRecordTime(String.valueOf(movieRecordTime.longValue() + 1));
                            LiveShutter.this.rvf_recording_time.setText(LiveShutter.this.getTimeFormat(LiveShutter.this.mDeviceController.getMovieRecordTime()));
                            if (movieRecordTime.longValue() % 2 == 0) {
                                LiveShutter.this.rvf_rec_icon.setVisibility(0);
                            } else {
                                LiveShutter.this.rvf_rec_icon.setVisibility(4);
                            }
                        } else {
                            LiveShutter.this.recordTimer.interrupt();
                        }
                        break;
                    case Const.MsgId.ON_DOWNLOAD_SUCCESS /* 140 */:
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.ON_DOWNLOAD_SUCCESS");
                        LiveShutter.thumbnail_bmp = Graph.getThumbnail((String) msg.obj);
                        Trace.d(LiveShutter.this.TAG, "scan file path 01 : " + ((String) msg.obj));
                        String mimetype = com.samsungimaging.connectionmanager.util.Utils.getMimeType((String) msg.obj);
                        MediaScannerConnection.scanFile(LiveShutter.Appcontext, new String[]{(String) msg.obj}, new String[]{mimetype}, null);
                        LiveShutter.this.mConfigurationManager.setLastSaveFileName((String) msg.obj);
                        LiveShutter.this.updateImageOfGallaryButton();
                        break;
                    case Const.MsgId.OCCURED_PARSER_EXCEPTION /* 142 */:
                        String message = (String) msg.obj;
                        Trace.d(LiveShutter.this.TAG, "rvf message : " + message);
                        if (!message.contains("HTTP_UNAUTHORIZED")) {
                            if (message.contains("HTTP_UNAVAILABLE")) {
                                LiveShutter.this.connectionFailDialog(LiveShutter.this.getString(R.string.already_connected));
                            }
                        } else {
                            LiveShutter.this.connectionFailDialog(LiveShutter.this.getString(R.string.rejected_connection));
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };
        this.mImageDownloader = new ImageDownloader(msgHandler, Const.MsgId.ON_DOWNLOAD_SUCCESS);
        if (Utils.isNexusOne()) {
            this.nZoomGap = Const.Config.NEXUSTIME;
            this.nAFGap = Const.Config.NEXUSTIME;
            this.btoastflag = false;
        } else {
            this.nZoomGap = 900;
            this.nAFGap = 2000;
            this.btoastflag = true;
        }
        if (configChanged) {
            this.mExToast.show(8);
            Trace.d(this.TAG, "onCreate  configChanged is true");
            configChanged = false;
            mHandler.postDelayed(new XxxThread(Const.MsgId.CHANGED_SYSTEM_SETTING), 3000L);
            return;
        }
        Process.setThreadPriority(-8);
        System.gc();
        getWindow().addFlags(128);
        this.locationManager = (LocationManager) getSystemService("location");
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(1);
        for (int i = 0; i < 4; i++) {
            this.screenPositoin[i] = new int[2];
            this.screenSize[i] = new int[2];
        }
        switch (RVFFunctionManager.getLiveViewResolutionType(this.connectedSsid)) {
            case 1:
                this.mDeviceController.setRatioValue("16:9");
                break;
            case 2:
                this.mDeviceController.setRatioValue("4:3");
                break;
            case 3:
                this.mDeviceController.setRatioValue("3:2");
                break;
            case 4:
                this.mDeviceController.setRatioValue("1:1");
                break;
        }
        if (this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
            screenConfigChange(2);
        } else {
            screenConfigChange(Utils.stringToRatioType(this.mDeviceController.getRatioValue()));
        }
        if (this.mDeviceController.isConnected()) {
            setContentView();
            initLayout();
            holderremove();
            holder = this.rvf_surface.getHolder();
            Trace.d(this.TAG, "Add callback of surface holder");
            holder.addCallback(this);
        }
        bgbitmap1 = Bitmap.createBitmap(this.surfaceViewHeight, this.surfaceViewWidth, Bitmap.Config.RGB_565);
        bgbitmap = Bitmap.createBitmap(this.surfaceViewWidth + 18, this.surfaceViewHeight, Bitmap.Config.RGB_565);
        FFmpegJNI.construct(bgbitmap1, bgbitmap, this.screenPositoin, this.screenSize, getPhoneCamType() - 1, 0);
        bClosing = false;
        if (!isLowBattery()) {
            if (this.upnpController.isConnected()) {
                showDialog(1016);
                this.upnpController.setEventHandler(this.mEventHandler);
                doAction(1, GpsValue());
                isButtonShow = true;
                return;
            }
            startAPSearch();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDestroyActivity() {
        return this.mDestroyed;
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.d(Trace.Tag.RVF, "start onDestroy()");
        this.mDestroyed = true;
        if (this.timetask != null) {
            this.timetask.cancel();
        }
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        Intent intent = new Intent(com.samsungimaging.connectionmanager.util.Utils.ACTION_DISCONNECT);
        sendBroadcast(intent);
        super.onDestroy();
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        if (CMUtil.isForceClose(activityManager)) {
            SystemClock.sleep(3000L);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        setTimer(7);
        orientation = newConfig.orientation;
        switch (newConfig.orientation) {
            case 1:
            case 2:
                Trace.d(this.TAG, "-=> onConfigurationChanged checkphonecamtype() orientation=" + orientation);
                checkphonecamtype();
                Createchanged();
                if (getPhoneCamType() == 2 || getCurrentMainOptionMenuId() != 0) {
                    CheckLayout();
                }
                Trace.d(this.TAG, "onConfigurationChanged start!!!!!");
                break;
        }
        CommonUtils.setSystemConfigurationChanged(getApplicationContext());
        super.onConfigurationChanged(newConfig);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:4:0x0023 A[FALL_THROUGH, ORIG_RETURN, RETURN] */
    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        int screenPositionX;
        int screenPositionY;
        int screenSizeWidth;
        int screenSizeHeight;
        Trace.d(this.TAG, "start onTouch() action : " + event.getAction());
        switch (event.getAction()) {
            case 0:
                setTimer(7);
                switch (v.getId()) {
                    case R.id.rvf_surface /* 2131558403 */:
                    case R.id.rvf_rec_stop_button /* 2131558484 */:
                    default:
                        return false;
                    case R.id.rvf_zoomMain /* 2131558477 */:
                        return true;
                    case R.id.rvf_zoomout_button /* 2131558478 */:
                        if (!isShotProcessing) {
                            if (RVFFunctionManager.isSupportedZoomLongKey(this.connectedSsid, this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                                if (Integer.parseInt(this.mDeviceController.getDefaultZoom()) > Integer.parseInt(this.mDeviceController.getMinZoom())) {
                                    setShutterButtonEnabled(false);
                                    this.mZoomState = 2;
                                    this.rvf_progress.setVisibility(0);
                                    if (rvf_touchAutoFocus.getVisibility() == 0) {
                                        this.mHandleTimer.removeMessages(4);
                                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 13");
                                        this.mShotState = 0;
                                        resetVisibilityTouchAFFrame();
                                        doAction(13, "");
                                    }
                                    doAction(7, "Wide");
                                }
                            } else {
                                this.isLongZoom = true;
                                ZoomOut();
                            }
                        }
                        return true;
                    case R.id.rvf_zoomin_button /* 2131558479 */:
                        if (!isShotProcessing) {
                            if (RVFFunctionManager.isSupportedZoomLongKey(this.connectedSsid, this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                                if (Integer.parseInt(this.mDeviceController.getDefaultZoom()) < Integer.parseInt(this.mDeviceController.getMaxZoom())) {
                                    setShutterButtonEnabled(false);
                                    this.mZoomState = 1;
                                    this.rvf_progress.setVisibility(0);
                                    if (rvf_touchAutoFocus.getVisibility() == 0) {
                                        this.mHandleTimer.removeMessages(4);
                                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 13");
                                        this.mShotState = 0;
                                        resetVisibilityTouchAFFrame();
                                        doAction(13, "");
                                    }
                                    doAction(7, "Tele");
                                }
                            } else {
                                this.isLongZoom = true;
                                ZoomIn();
                            }
                        }
                        return true;
                    case R.id.rvf_mode_button /* 2131558481 */:
                        Trace.d(this.TAG, "camcorder button isPressed : " + this.rvf_camcorder_button.isPressed());
                        return this.rvf_camcorder_button.isPressed();
                    case R.id.rvf_shutter_button /* 2131558482 */:
                        Trace.d(this.TAG, "camcorder button isPressed : " + this.rvf_camcorder_button.isPressed());
                        if (this.rvf_camcorder_button.isPressed()) {
                            return true;
                        }
                        nShutterDnUp = 0;
                        MenuClose(true);
                        if (isAvailShot()) {
                            isShotProcessing = true;
                            long ctime = System.currentTimeMillis();
                            Trace.d(this.TAG, "-=> rvf_shutter_button ctime=" + ctime + ", shotcanceltime=" + this.shotcanceltime + ", ctime-shotcanceltime=" + (ctime - this.shotcanceltime) + ", nAFGap=" + this.nAFGap);
                            Trace.d(this.TAG, "-=> rvf_shutter_button bshotrunflag=" + this.bshotrunflag);
                            if (ctime - this.shotcanceltime > this.nAFGap && getActionState() != 1) {
                                buttondisplay(true);
                                if (this.mShotState != 3 && this.mShotState != 4 && this.mShotState != 2) {
                                    this.bshotrunflag = true;
                                    this.shotcanceltime = System.currentTimeMillis();
                                    this.mHandleTimer.removeMessages(11);
                                    if (rvf_touchAutoFocus.getVisibility() == 0 && this.mDeviceController.getDefaultFocusState().equals("af")) {
                                        if (getActionState() != 1) {
                                            this.mHandleTimer.removeMessages(4);
                                            this.mShotState = 1;
                                            doAction(12, String.valueOf(String.valueOf(this.positionX)) + "x" + String.valueOf(this.positionY));
                                        }
                                    } else {
                                        this.mShotStateEx = 2;
                                        if (RVFFunctionManager.getAFMode(this.connectedSsid) == 0) {
                                            this.mShotState = 1;
                                            doAction(9, "");
                                        } else if (RVFFunctionManager.getAFMode(this.connectedSsid) == 1) {
                                            this.mShotState = 1;
                                            doAction(10, "");
                                        }
                                    }
                                }
                            } else {
                                this.bshotrunflag = false;
                            }
                        }
                        return false;
                    case R.id.rvf_camcorder_button /* 2131558483 */:
                        Trace.d(this.TAG, "shutter button isPressed : " + this.rvf_shutter_button.isPressed());
                        return this.rvf_mode_button.isPressed() | this.rvf_shutter_button.isPressed();
                }
            case 1:
                setTimer(7);
                switch (v.getId()) {
                    case R.id.rvf_surface /* 2131558403 */:
                        if ((this.mShotStateEx != 7 || (this.mDeviceController.getAction(40) != null && this.mDeviceController.getAction(39) != null)) && isAvailShot() && getSmartPanelVisibility() != 0) {
                            Trace.d(this.TAG, "mShotStateEx : " + this.mShotStateEx + " isShotProcessing : " + isShotProcessing + " focus state : " + this.mDeviceController.getDefaultFocusState() + " nTimerCount : " + this.nTimerCount + " action State : " + getActionState() + " touch AF value : " + this.mDeviceController.getTouchAFValue());
                            if (!isShotProcessing && this.mDeviceController.getDefaultFocusState().equals("af") && this.nTimerCount == 0 && getActionState() != 1 && !this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                                this.mFrameWidth = rvf_touchAutoFocus.getWidth();
                                this.mFrameHeight = rvf_touchAutoFocus.getHeight();
                                int x = (int) event.getX();
                                int y = (int) event.getY();
                                Trace.d(this.TAG, "<<<<< getRatioOffsetMenuItems().size() : " + this.mDeviceController.getRatioOffsetMenuItems().size());
                                if (this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                                    screenPositionX = ((this.mDeviceController.getRatioOffsetValue().getWidth() * this.screenSize[getPhoneCamType() - 1][0]) / 640) + this.screenPositoin[getPhoneCamType() - 1][0];
                                    screenPositionY = (this.mDeviceController.getRatioOffsetValue().getHeight() * this.screenSize[getPhoneCamType() - 1][1]) / 480;
                                    screenSizeWidth = this.screenSize[getPhoneCamType() - 1][0] - (((this.mDeviceController.getRatioOffsetValue().getWidth() * 2) * this.screenSize[getPhoneCamType() - 1][0]) / 640);
                                    screenSizeHeight = this.screenSize[getPhoneCamType() - 1][1] - (screenPositionY * 2);
                                } else {
                                    screenPositionX = this.screenPositoin[getPhoneCamType() - 1][0];
                                    screenPositionY = this.screenPositoin[getPhoneCamType() - 1][1];
                                    screenSizeWidth = this.screenSize[getPhoneCamType() - 1][0];
                                    screenSizeHeight = this.screenSize[getPhoneCamType() - 1][1];
                                }
                                Trace.d(this.TAG, "<<<<< screenSize W : " + screenSizeWidth + " H : " + screenSizeHeight + " frame W : " + this.mFrameWidth + " H : " + this.mFrameHeight);
                                Trace.d(this.TAG, "x : " + x + " screenPositionX : " + screenPositionX + " y : " + y + " screenPositionY : " + screenPositionY);
                                if (x > screenPositionX && x < screenPositionX + screenSizeWidth && y > screenPositionY && y < screenPositionY + screenSizeHeight) {
                                    this.mOrientationWhenCheckMargin = orientation;
                                    this.mMarginLeft = x - (this.mFrameWidth / 2);
                                    this.mMarginTop = y - (this.mFrameHeight / 2);
                                    if (this.mMarginLeft < screenPositionX) {
                                        this.mMarginLeft = screenPositionX;
                                    } else if (this.mMarginLeft + this.mFrameWidth > screenPositionX + screenSizeWidth) {
                                        this.mMarginLeft = (screenPositionX + screenSizeWidth) - this.mFrameWidth;
                                    }
                                    if (this.mMarginTop < screenPositionY) {
                                        this.mMarginTop = screenPositionY;
                                    } else if (this.mMarginTop + this.mFrameHeight > screenPositionY + screenSizeHeight) {
                                        this.mMarginTop = (screenPositionY + screenSizeHeight) - this.mFrameHeight;
                                    }
                                    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(rvf_touchAutoFocus.getLayoutParams());
                                    margin.setMargins(this.mMarginLeft, this.mMarginTop, 0, 0);
                                    rvf_touchAutoFocus.setLayoutParams(new RelativeLayout.LayoutParams(margin));
                                    nShutterDnUp = -1;
                                    this.positionX = this.mMarginLeft + (this.mFrameWidth / 2);
                                    this.positionY = this.mMarginTop + (this.mFrameHeight / 2);
                                    this.positionX -= screenPositionX;
                                    this.positionY -= screenPositionY;
                                    this.positionX = (this.positionX * 100) / screenSizeWidth;
                                    this.positionY = (this.positionY * 100) / screenSizeHeight;
                                    if (this.positionX < 8) {
                                        this.positionX = 8;
                                    }
                                    if (this.positionY > 88) {
                                        this.positionY = 88;
                                    }
                                    Trace.d(this.TAG, "mMarginLeft : " + this.mMarginLeft + " mMarginTop : " + this.mMarginTop + " positionX : " + this.positionX + " Y : " + this.positionY);
                                    if (this.mShotStateEx == 7) {
                                        doAction(40, String.valueOf(String.valueOf(this.positionX)) + "x" + String.valueOf(this.positionY));
                                    } else {
                                        rvf_touchAutoFocus.setVisibility(0);
                                        rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                                        if (this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals("TOUCH AF")) {
                                            this.mShotState = 1;
                                            doAction(12, String.valueOf(String.valueOf(this.positionX)) + "x" + String.valueOf(this.positionY));
                                            setShutterButtonEnabled(false);
                                        }
                                        if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF") || this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals("TOUCH AF")) {
                                            setTimer(4);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    case R.id.rvf_zoomMain /* 2131558477 */:
                    case R.id.rvf_mode_button /* 2131558481 */:
                    case R.id.rvf_camcorder_button /* 2131558483 */:
                    case R.id.rvf_rec_stop_button /* 2131558484 */:
                    default:
                        return false;
                    case R.id.rvf_zoomout_button /* 2131558478 */:
                        if (RVFFunctionManager.isSupportedZoomLongKey(this.connectedSsid, this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                            if (this.mZoomState == 2) {
                                rvf_zoomin_button.setEnabled(false);
                                rvf_zoomout_button.setEnabled(false);
                                doAction(8, "Wide");
                            }
                        } else {
                            this.isLongZoom = false;
                        }
                        return true;
                    case R.id.rvf_zoomin_button /* 2131558479 */:
                        if (RVFFunctionManager.isSupportedZoomLongKey(this.connectedSsid, this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                            if (this.mZoomState == 1) {
                                rvf_zoomin_button.setEnabled(false);
                                rvf_zoomout_button.setEnabled(false);
                                doAction(8, "Tele");
                            }
                        } else {
                            this.isLongZoom = false;
                        }
                        return true;
                    case R.id.rvf_shutter_button /* 2131558482 */:
                        if (isAvailShot()) {
                            int touchX = (int) event.getX();
                            int touchY = (int) event.getY();
                            int shutter_button_width = this.rvf_shutter_button.getWidth();
                            int shutter_button_height = this.rvf_shutter_button.getHeight();
                            if (touchX < 0 || touchY < 0 || touchX > shutter_button_width || touchY > shutter_button_height) {
                                nShutterDnUp = -1;
                                if (this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                                    if (this.mShotStateEx == 5) {
                                        this.mShotStateEx = 6;
                                        doAction(31, "");
                                    }
                                } else if (this.bshotrunflag) {
                                    stopLEDTimeDisplay();
                                    this.mHandleTimer.removeMessages(4);
                                    Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 00");
                                    this.mShotState = 0;
                                    resetVisibilityTouchAFFrame();
                                    doAction(13, "");
                                }
                            } else {
                                nShutterDnUp = 1;
                                if (this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                                    if (this.bAFFAIL && RVFFunctionManager.isCancellableShotByAFFail(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion(), this.connectedSsid)) {
                                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 02");
                                        this.mShotState = 0;
                                        resetVisibilityTouchAFFrame();
                                        doAction(13, "");
                                        Trace.e(this.TAG, "-=> shutter_button10 ReleaseSelfTimerAct_DSC DISPLAY_BUTTON_ACTIVE");
                                        Trace.d(this.TAG, "MESSAGE : DISPLAY_BUTTON_ACTIVE");
                                        nDisplayFlag = 110;
                                        mHandler.post(new XxxThread(nDisplayFlag));
                                    } else if (this.mShotStateEx == 5) {
                                        this.mShotStateEx = 6;
                                        doAction(31, "");
                                    }
                                } else {
                                    isShotProcessing = true;
                                    Trace.d(this.TAG, "-=> shutter_button2 ACTION_UP");
                                    buttondisplay(true);
                                    Trace.d(this.TAG, "-=> shutter_button3 ACTION_UP bshutter=" + bshutter + ", bshotrunflag=" + this.bshotrunflag);
                                    if (!bshutter && this.bshotrunflag) {
                                        Trace.d(this.TAG, "MESSAGE : DISPLAY_AFDONE");
                                        nDisplayFlag = 9;
                                        mHandler.post(new XxxThread(nDisplayFlag));
                                    }
                                    Trace.d(this.TAG, "-=> shutter_button4 ACTION_UP touchX=" + touchX + ", touchY=" + touchY);
                                    Trace.d(this.TAG, "-=> shutterbutton(true)-2 mShotState : " + this.mShotState);
                                    if (!this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE") && this.mShotState == 2) {
                                        stopLEDTimeDisplay();
                                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 01");
                                        this.mShotState = 0;
                                        resetVisibilityTouchAFFrame();
                                        doAction(13, "");
                                        return false;
                                    }
                                    Trace.d(this.TAG, "-=> shutterbutton(false)-5 bshotrunflag=" + this.bshotrunflag);
                                    if (this.bshotrunflag) {
                                        bshutter = true;
                                        shutterbutton(false);
                                        if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("SINGLE") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                                            this.mHandleTimer.removeMessages(4);
                                            setShutterButtonEnabled(false);
                                            if (getActionState() == 2) {
                                                if (this.AfFrameType == 4) {
                                                    hideAFExtend();
                                                } else {
                                                    hideAutoFocusFrame();
                                                }
                                            }
                                            Trace.d(this.TAG, "-=> shutter_button8 ACTION_UP");
                                            new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.10
                                                @Override // java.lang.Runnable
                                                public void run() {
                                                    while (LiveShutter.this.getActionState() != 2) {
                                                        try {
                                                            Thread.sleep(500L);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    LiveShutter.this.bshotrunflag = false;
                                                    Trace.d(LiveShutter.this.TAG, "-=> shutter_button10 ACTION_UP bAFFAIL=" + LiveShutter.this.bAFFAIL);
                                                    if (!LiveShutter.this.bAFFAIL || !RVFFunctionManager.isCancellableShotByAFFail(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid)) {
                                                        Trace.d(LiveShutter.this.TAG, "-=> shutter_button11 ACTION_UP");
                                                        Trace.d(LiveShutter.this.TAG, "MESSAGE : RUN_DISPLAY_SHOT");
                                                        LiveShutter.nDisplayFlag = 47;
                                                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                                                        return;
                                                    }
                                                    Trace.d(LiveShutter.this.TAG, "do action RELEASE_SELF_TIMER 02");
                                                    LiveShutter.this.mShotState = 0;
                                                    LiveShutter.this.doAction(13, "");
                                                    Trace.e(LiveShutter.this.TAG, "-=> shutter_button10 ReleaseSelfTimerAct_DSC DISPLAY_BUTTON_ACTIVE");
                                                    Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_BUTTON_ACTIVE");
                                                    LiveShutter.nDisplayFlag = 110;
                                                    LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
                                                }
                                            }).start();
                                        } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("2SEC") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("5SEC") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("10SEC") || this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)") || this.mDeviceController.getCurrentLEDTimeMenuItem().equals("Double")) {
                                            if (getActionState() == 2) {
                                                if (this.AfFrameType == 4) {
                                                    hideAFExtend();
                                                } else {
                                                    hideAutoFocusFrame();
                                                }
                                                rvf_touchAutoFocus.setVisibility(4);
                                                timerdisplay();
                                            }
                                            if (RVFFunctionManager.isSupportedShutterUpAct(this.connectedSsid)) {
                                                nDisplayFlag = 50;
                                                mHandler.post(new XxxThread(nDisplayFlag));
                                            }
                                        }
                                    } else {
                                        isShotProcessing = false;
                                    }
                                }
                            }
                        }
                        return false;
                }
                break;
            default:
                return false;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Trace.d(this.TAG, "start onClick()");
        setTimer(7);
        if (bshutter && this.nTimerCount > 0) {
            setVisibilityOSD(0);
            stopLEDTimeDisplay();
            Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 03");
            this.mShotState = 0;
            resetVisibilityTouchAFFrame();
            doAction(13, "");
        }
        switch (v.getId()) {
            case R.id.rvf_surface /* 2131558403 */:
                Trace.d(this.TAG, "click smart panel");
                if (this.rvf_setting_sub_menu.getVisibility() == 0) {
                    hideSubOptionMenu();
                    break;
                } else if (getCurrentMainOptionMenuId() != 0) {
                    hideMainOptionMenu();
                    if (getPhoneCamType() == 1 && getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_flash_button);
                    setImageResource(rvf_timer_button);
                    setImageResource(rvf_photosize_button);
                    setImageResource(rvf_camerasave_button);
                    setImageResource(rvf_cameramore_button);
                    break;
                } else if (this.rvf_smart_panel.getVisibility() == 0) {
                    setSmartPanelVisibility(8);
                    this.rvf_smart_panel_icon.setVisibility(0);
                    rvf_title.setVisibility(0);
                    showIndicatorBar();
                    this.rvf_gallery.setVisibility(0);
                    this.rvf_mode_button.setVisibility(0);
                    this.rvf_shutter_button.setVisibility(0);
                    if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
                        this.rvf_camcorder_button.setVisibility(0);
                    }
                    showZoomBar();
                    break;
                } else if (getPhoneCamType() != 1 && rvf_QuickSettingMenu.getVisibility() == 0) {
                    hideQuickSettingMenu();
                    showOSD();
                    break;
                } else if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                    if (rvf_zoomMain.getVisibility() == 0) {
                        if (getPhoneCamType() != 1) {
                            hideZoomBar();
                        }
                    } else {
                        showZoomBar();
                    }
                    break;
                }
                break;
            case R.id.rvf_smart_panel_icon /* 2131558462 */:
                if (this.mShotState == 1) {
                    this.mHandleTimer.removeMessages(4);
                    rvf_touchAutoFocus.setVisibility(4);
                    Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 06");
                    this.mShotState = 0;
                    resetVisibilityTouchAFFrame();
                    doAction(13, "");
                }
                setSmartPanelVisibility(0);
                this.rvf_smart_panel_icon.setVisibility(8);
                rvf_title.setVisibility(4);
                hideIndicatorBar();
                this.rvf_gallery.setVisibility(8);
                this.rvf_mode_button.setVisibility(8);
                this.rvf_shutter_button.setVisibility(8);
                this.rvf_camcorder_button.setVisibility(8);
                hideZoomBar();
                break;
            case R.id.rvf_open_button /* 2131558464 */:
                showQuickSettingMenu();
                hideOSD();
                break;
            case R.id.rvf_flash_button /* 2131558466 */:
                if (getCurrentMainOptionMenuId() == 1) {
                    hideMainOptionMenu();
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_flash_button);
                } else {
                    showMainOptionMenu(1);
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    }
                    setImageResource(rvf_timer_button);
                    setImageResource(rvf_photosize_button);
                    setImageResource(rvf_camerasave_button);
                    setImageResource(rvf_cameramore_button);
                    setImageResourceButtonFocus(rvf_flash_button);
                }
                break;
            case R.id.rvf_timer_button /* 2131558467 */:
                if (getCurrentMainOptionMenuId() == 2) {
                    hideMainOptionMenu();
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_timer_button);
                } else {
                    showMainOptionMenu(2);
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    }
                    setImageResource(rvf_flash_button);
                    setImageResource(rvf_photosize_button);
                    setImageResource(rvf_camerasave_button);
                    setImageResource(rvf_cameramore_button);
                    setImageResourceButtonFocus(rvf_timer_button);
                }
                break;
            case R.id.rvf_photosize_button /* 2131558468 */:
                if (getCurrentMainOptionMenuId() == 3) {
                    hideMainOptionMenu();
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_photosize_button);
                } else {
                    showMainOptionMenu(3);
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    }
                    setImageResource(rvf_flash_button);
                    setImageResource(rvf_timer_button);
                    setImageResource(rvf_camerasave_button);
                    setImageResource(rvf_cameramore_button);
                    setImageResourceButtonFocus(rvf_photosize_button);
                }
                break;
            case R.id.rvf_camerasave_button /* 2131558469 */:
                if (getCurrentMainOptionMenuId() == 4) {
                    hideMainOptionMenu();
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_camerasave_button);
                } else {
                    showMainOptionMenu(4);
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    }
                    setImageResource(rvf_flash_button);
                    setImageResource(rvf_timer_button);
                    setImageResource(rvf_photosize_button);
                    setImageResourceButtonFocus(rvf_camerasave_button);
                }
                break;
            case R.id.rvf_cameramore_button /* 2131558470 */:
                if (getCurrentMainOptionMenuId() == 5) {
                    hideMainOptionMenu();
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    setImageResource(rvf_cameramore_button);
                } else {
                    showMainOptionMenu(5);
                    hideSubOptionMenu();
                    if (getPhoneCamType() == 1) {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    }
                    setImageResource(rvf_flash_button);
                    setImageResource(rvf_timer_button);
                    setImageResource(rvf_photosize_button);
                    setImageResourceButtonFocus(rvf_cameramore_button);
                }
                break;
            case R.id.rvf_close_button /* 2131558475 */:
                hideQuickSettingMenu();
                hideMainOptionMenu();
                showOSD();
                setImageResource(rvf_flash_button);
                setImageResource(rvf_timer_button);
                setImageResource(rvf_photosize_button);
                setImageResource(rvf_camerasave_button);
                setImageResource(rvf_cameramore_button);
                break;
            case R.id.rvf_mode_button /* 2131558481 */:
                Trace.d(this.TAG, "nShutterDnUp : " + nShutterDnUp + " mShotState : " + this.mShotState + " isShotProcessing : " + isShotProcessing);
                if (nShutterDnUp != 0 && !isShotProcessing) {
                    if (this.mShotState == 1) {
                        this.mHandleTimer.removeMessages(4);
                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 04");
                        this.mShotState = 0;
                        resetVisibilityTouchAFFrame();
                        doAction(13, "");
                    }
                    rvf_touchAutoFocus.setVisibility(4);
                    Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
                    setVisibilityOSD(8);
                    showSmartPanelCustomDialogWheelMenu(32, "Mode", 5, this.mDeviceController.getDialModeMenuItems().indexOf(this.mDeviceController.getDialModeValue()), this.mDeviceController.getDialModeMenuItems(), display.getWidth() / 5);
                    this.mSmartPanelCustomDialogWheel.setCurrentItem(this.mDeviceController.getDialModeMenuItems().indexOf(this.mDeviceController.getDialModeValue()));
                    break;
                }
                break;
            case R.id.rvf_camcorder_button /* 2131558483 */:
                if (isAvailShot()) {
                    if (isAvailMovieRecording()) {
                        this.rvf_camcorder_button.setEnabled(false);
                        if (rvf_touchAutoFocus.getVisibility() == 0) {
                            this.mHandleTimer.removeMessages(4);
                            rvf_touchAutoFocus.setVisibility(4);
                            Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 05");
                            this.mShotState = 0;
                            doAction(13, "");
                        }
                        this.mShotState = 5;
                        doAction(19, this.mDeviceController.getCurrentStreamQuality());
                    } else {
                        showMemoryFullDialog(0);
                    }
                }
                break;
            case R.id.rvf_rec_stop_button /* 2131558484 */:
                this.rvf_rec_stop_button.setEnabled(false);
                this.mShotState = 0;
                doAction(36, "");
                break;
            case R.id.rvf_thumbnail /* 2131558492 */:
                boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID());
                if (isOldCamera || this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(0))) {
                    MenuClose(true);
                    if (codec_init) {
                        setButtonEnabled(false);
                        setShutterButtonEnabled(false);
                        loadingdisplay();
                    }
                } else if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(1))) {
                    this.mHandleTimer.removeMessages(7);
                    Trace.d(this.TAG, "hasMessages : " + this.mHandleTimer.hasMessages(4));
                    if (this.mHandleTimer.hasMessages(4)) {
                        this.mHandleTimer.removeMessages(4);
                        rvf_touchAutoFocus.setVisibility(4);
                        Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 12");
                        this.mShotState = 0;
                        resetVisibilityTouchAFFrame();
                        this.isOperationStateChange = true;
                        doAction(13, "");
                    } else if (this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("EXTERNAL")) {
                        setButtonEnabled(false);
                        this.mHandleTimer.removeMessages(5);
                        codec_stop();
                        FFmpegJNI.destruct();
                        Intent intent = new Intent(this, (Class<?>) FileSharing.class);
                        intent.putExtra("requestServiceChange", "changeToML");
                        startActivity(intent);
                        finish();
                    }
                }
                break;
            case R.id.rvf_camera_btn_af /* 2131558687 */:
                doAction(39, "off");
                break;
            case R.id.rvf_smart_panel /* 2131558694 */:
                Trace.d(this.TAG, "click smart panel clicked");
                break;
            case R.id.rvf_smart_panel_wb /* 2131558700 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(25, getString(R.string.rvf_white_balance), this.mDeviceController.getWBMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_metering /* 2131558701 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(26, getString(R.string.rvf_metering), this.mDeviceController.getMeteringMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_drive /* 2131558702 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(29, getString(R.string.rvf_drive), this.mDeviceController.getDriveMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_flash /* 2131558703 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(17, getString(R.string.rvf_flash), this.mDeviceController.getFlashMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_afMode /* 2131558704 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(27, "AF Mode", this.mDeviceController.getAFModeMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_photoSize /* 2131558705 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(2, getString(R.string.rvf_photo_size), null);
                }
                break;
            case R.id.rvf_smart_panel_quality /* 2131558706 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(33, getString(R.string.rvf_quality), this.mDeviceController.getQualityMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_movieSize /* 2131558707 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(34, getString(R.string.rvf_movie_size), null);
                }
                break;
            case R.id.rvf_smart_panel_streaming_quality /* 2131558708 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(18, getString(R.string.rvf_streaming_quality), this.mDeviceController.getStreamQualityMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_afArea /* 2131558709 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(28, getString(R.string.rvf_af_area), this.mDeviceController.getAFAreaMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_touchAF /* 2131558710 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(11, getString(R.string.rvf_touch_af), this.mDeviceController.getTouchAFMenuItems());
                }
                break;
            case R.id.rvf_smart_panel_save /* 2131558711 */:
                if (getSmartPanelCustomDialogListMenuId() == 0) {
                    showSmartPanelCustomDialogListMenu(37, getString(R.string.rvf_photo_storage), this.mDeviceController.getFileSaveMenuItems());
                }
                break;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    public void onBackPressed() {
        setTimer(7);
        if (this.rvf_setting_sub_menu.getVisibility() == 0) {
            hideSubOptionMenu();
            return;
        }
        if (getCurrentMainOptionMenuId() != 0) {
            hideMainOptionMenu();
            if (getPhoneCamType() == 1 && getPhoneCamType() == 1) {
                if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                    showZoomBar();
                }
                this.rvf_shutter_button.setVisibility(0);
                return;
            }
            return;
        }
        if (this.rvf_smart_panel.getVisibility() == 0) {
            setSmartPanelVisibility(8);
            this.rvf_smart_panel_icon.setVisibility(0);
            rvf_title.setVisibility(0);
            showIndicatorBar();
            this.rvf_gallery.setVisibility(0);
            this.rvf_mode_button.setVisibility(0);
            this.rvf_shutter_button.setVisibility(0);
            if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
                this.rvf_camcorder_button.setVisibility(0);
            }
            showZoomBar();
            return;
        }
        if (getPhoneCamType() != 1 && rvf_QuickSettingMenu.getVisibility() == 0) {
            hideQuickSettingMenu();
            showOSD();
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder2, int format, int width, int height) {
        Trace.d(this.TAG, "start surfaceChanged() isSettedLiveStream : " + this.isSettedLiveStream);
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_CHANGED, holder2);
        if (this.mShotStateEx == 7 && this.mDeviceController.getStreamUrlMenuItems().size() <= 2) {
            FFmpegJNI.onFreezeDisplay();
        }
        if (!this.isSettedLiveStream) {
            GetLiveStream();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder2) {
        Trace.d(this.TAG, "start surfaceCreated()");
        if (!this.bStart) {
            this.bStart = true;
        }
        buttondisplay(true);
        setImageResource(rvf_flash_button);
        setImageResource(rvf_timer_button);
        setImageResource(rvf_photosize_button);
        setImageResource(rvf_camerasave_button);
        setImageResource(rvf_cameramore_button);
        Trace.d(this.TAG, "-=> shutterbutton(false)-3");
        shutterbutton(false);
        Trace.d(this.TAG, "-=> shutterbutton(false)-4 mShotState : " + this.mShotState);
        if (this.mShotState != 0 && this.mShotState != 1) {
            shutterbutton(false);
            if (this.nTimerCount > 0) {
                setShutterButtonEnabled(true);
            } else {
                setShutterButtonEnabled(false);
            }
        } else {
            shutterbutton(true);
        }
        mHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.11
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Trace.d(LiveShutter.this.TAG, "mHandler msg.what : " + msg.what);
                switch (msg.what) {
                    case 1:
                        long ctime = System.currentTimeMillis();
                        LiveShutter.this.buttondisplay(true);
                        if (ctime - LiveShutter.this.zoomtime <= LiveShutter.this.nZoomGap) {
                            if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) == 0 || !LiveShutter.codec_init) {
                                if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) == 0) {
                                    LiveShutter.rvf_zoomin_button.setImageResource(R.drawable.rvf_zoom_minus_focus);
                                }
                            } else {
                                LiveShutter.rvf_zoomin_button.setImageResource(R.drawable.rvf_zoom_minus_normal);
                            }
                            LiveShutter.this.buttondisplay(true);
                            if (LiveShutter.this.btoastflag) {
                                if (LiveShutter.this.toast == null) {
                                    CharSequence str = LiveShutter.this.getString(R.string.rvf_zoom);
                                    LiveShutter.this.toast = Toast.makeText(LiveShutter.Appcontext, str, 0);
                                }
                                if (!LiveShutter.this.btoast) {
                                    if (LiveShutter.orientation == 2) {
                                        LiveShutter.this.toast.setGravity(81, 0, LiveShutter.convertDipToPx(LiveShutter.Appcontext, 43));
                                    } else {
                                        LiveShutter.this.toast.setGravity(81, 0, LiveShutter.convertDipToPx(LiveShutter.Appcontext, 150));
                                    }
                                    LiveShutter.this.btoast = true;
                                    LiveShutter.this.toast.show();
                                }
                            }
                            break;
                        } else {
                            LiveShutter.rvf_zoomin_button.setImageResource(R.drawable.rvf_zoom_minus_focus);
                            if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) != Integer.parseInt(LiveShutter.this.mDeviceController.getMaxZoom()) && LiveShutter.codec_init) {
                                LiveShutter.this.btoast = false;
                                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_ZOOMINBUTTON");
                                LiveShutter.nDisplayFlag = 26;
                                LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), LiveShutter.this.nZoomGap - 2);
                                LiveShutter.this.zoomtime = ctime;
                                LiveShutter.this.doAction(3, "");
                                break;
                            }
                        }
                        break;
                    case 2:
                        LiveShutter.this.buttondisplay(true);
                        long ctime2 = System.currentTimeMillis();
                        if (ctime2 - LiveShutter.this.zoomtime <= LiveShutter.this.nZoomGap) {
                            if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) == 0 || !LiveShutter.codec_init) {
                                if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) == 0) {
                                    LiveShutter.rvf_zoomout_button.setImageResource(R.drawable.rvf_zoom_plus_focus);
                                }
                            } else {
                                LiveShutter.rvf_zoomout_button.setImageResource(R.drawable.rvf_zoom_plus_normal);
                            }
                            LiveShutter.this.buttondisplay(true);
                            if (LiveShutter.this.btoastflag) {
                                if (LiveShutter.this.toast == null) {
                                    CharSequence str2 = LiveShutter.this.getString(R.string.rvf_zoom);
                                    LiveShutter.this.toast = Toast.makeText(LiveShutter.Appcontext, str2, 0);
                                }
                                if (!LiveShutter.this.btoast) {
                                    if (LiveShutter.orientation == 2) {
                                        LiveShutter.this.toast.setGravity(81, 0, LiveShutter.convertDipToPx(LiveShutter.Appcontext, 43));
                                    } else {
                                        LiveShutter.this.toast.setGravity(81, 0, LiveShutter.convertDipToPx(LiveShutter.Appcontext, 150));
                                    }
                                    LiveShutter.this.btoast = true;
                                    LiveShutter.this.toast.show();
                                }
                            }
                            break;
                        } else {
                            LiveShutter.rvf_zoomout_button.setImageResource(R.drawable.rvf_zoom_plus_focus);
                            if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultZoom()) != 0 && LiveShutter.codec_init) {
                                LiveShutter.this.btoast = false;
                                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_ZOOMOUTBUTTON");
                                LiveShutter.nDisplayFlag = 25;
                                LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), LiveShutter.this.nZoomGap - 2);
                                LiveShutter.this.zoomtime = ctime2;
                                LiveShutter.this.doAction(4, "");
                                break;
                            }
                        }
                        break;
                }
            }
        };
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_CREATED, holder2);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder2) {
        Trace.d(this.TAG, "start surfaceDestroyed()");
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_DESTROYED, holder2);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0008. Please report as an issue. */
    @Override // org.kankan.wheel.widget.OnWheelScrollListener
    public void onScrollingStarted(WheelView wheel) {
        setTimer(7);
        switch (wheel.getId()) {
        }
    }

    @Override // org.kankan.wheel.widget.OnWheelScrollListener
    public void onScrollingFinished(WheelView wheel) {
        setTimer(7);
        switch (wheel.getId()) {
            case R.id.rvf_smart_panel_mode /* 2131558695 */:
                if (!this.mDeviceController.getDialModeValue().equals(this.mDeviceController.getDialModeMenuItems().get(this.rvf_smart_panel_mode.getCurrentItem()))) {
                    this.mHandleTimer.removeMessages(4);
                    rvf_touchAutoFocus.setVisibility(4);
                    this.mDeviceController.setDialModeValue(this.mDeviceController.getDialModeMenuItems().get(this.rvf_smart_panel_mode.getCurrentItem()));
                    doAction(32, this.mDeviceController.getDialModeMenuItems().get(this.rvf_smart_panel_mode.getCurrentItem()));
                }
                break;
            case R.id.rvf_smart_panel_shutterSpeed /* 2131558696 */:
                if (!this.mDeviceController.getShutterSpeedValue().equals(this.mDeviceController.getShutterSpeedMenuItems().get(this.rvf_smart_panel_shutterSpeed.getCurrentItem()))) {
                    this.mDeviceController.setShutterSpeedValue(this.mDeviceController.getShutterSpeedMenuItems().get(this.rvf_smart_panel_shutterSpeed.getCurrentItem()));
                    doAction(21, this.mDeviceController.getShutterSpeedMenuItems().get(this.rvf_smart_panel_shutterSpeed.getCurrentItem()));
                }
                break;
            case R.id.rvf_smart_panel_aperture /* 2131558697 */:
                if (!this.mDeviceController.getApertureValue().equals(this.mDeviceController.getApertureMenuItems().get(this.rvf_smart_panel_aperture.getCurrentItem()))) {
                    this.mDeviceController.setApertureValue(this.mDeviceController.getApertureMenuItems().get(this.rvf_smart_panel_aperture.getCurrentItem()));
                    doAction(22, this.mDeviceController.getApertureMenuItems().get(this.rvf_smart_panel_aperture.getCurrentItem()));
                }
                break;
            case R.id.rvf_smart_panel_ev /* 2131558698 */:
                if (!this.mDeviceController.getEVValue().equals(this.mDeviceController.getEVMenuItems().get(this.rvf_smart_panel_ev.getCurrentItem()))) {
                    this.mDeviceController.setEVValue(this.mDeviceController.getEVMenuItems().get(this.rvf_smart_panel_ev.getCurrentItem()));
                    doAction(23, this.mDeviceController.getEVMenuItems().get(this.rvf_smart_panel_ev.getCurrentItem()));
                }
                break;
            case R.id.rvf_smart_panel_iso /* 2131558699 */:
                if (!this.mDeviceController.getISOValue().equals(this.mDeviceController.getISOMenuItems().get(this.rvf_smart_panel_iso.getCurrentItem()))) {
                    this.mDeviceController.setISOValue(this.mDeviceController.getISOMenuItems().get(this.rvf_smart_panel_iso.getCurrentItem()));
                    doAction(24, this.mDeviceController.getISOMenuItems().get(this.rvf_smart_panel_iso.getCurrentItem()));
                }
                break;
        }
    }

    @Override // org.kankan.wheel.widget.OnWheelClickedListener
    public void onItemClicked(WheelView wheel, int itemIndex) {
        setTimer(7);
        Trace.d(this.TAG, "rvf_smart_panel : " + this.rvf_smart_panel.getMeasuredWidth() + " rvf_smart_panel_shutterSpeed : " + this.rvf_smart_panel_shutterSpeed.getMeasuredWidth());
        switch (wheel.getId()) {
            case R.id.rvf_smart_panel_mode /* 2131558695 */:
                showSmartPanelCustomDialogWheelMenu(32, "Mode", 5, this.mDeviceController.getDialModeMenuItems().indexOf(this.mDeviceController.getDialModeValue()), this.mDeviceController.getDialModeMenuItems(), 84);
                break;
            case R.id.rvf_smart_panel_shutterSpeed /* 2131558696 */:
                showSmartPanelCustomDialogWheelMenu(21, "Shutter Speed", 3, this.mDeviceController.getShutterSpeedMenuItems().indexOf(this.mDeviceController.getShutterSpeedValue()), this.mDeviceController.getShutterSpeedMenuItems(), this.rvf_smart_panel.getMeasuredWidth() / 3);
                break;
            case R.id.rvf_smart_panel_aperture /* 2131558697 */:
                showSmartPanelCustomDialogWheelMenu(22, "Aperture", 5, this.mDeviceController.getApertureMenuItems().indexOf(this.mDeviceController.getApertureValue()), this.mDeviceController.getApertureMenuItems(), this.rvf_smart_panel.getMeasuredWidth() / 5);
                break;
            case R.id.rvf_smart_panel_ev /* 2131558698 */:
                showSmartPanelCustomDialogWheelMenu(23, "EV", 5, this.mDeviceController.getEVMenuItems().indexOf(this.mDeviceController.getEVValue()), this.mDeviceController.getEVMenuItems(), this.rvf_smart_panel.getMeasuredWidth() / 5);
                break;
            case R.id.rvf_smart_panel_iso /* 2131558699 */:
                showSmartPanelCustomDialogWheelMenu(24, "ISO", 5, this.mDeviceController.getISOMenuItems().indexOf(this.mDeviceController.getISOValue()), this.mDeviceController.getISOMenuItems(), this.rvf_smart_panel.getMeasuredWidth() / 3);
                break;
        }
    }

    public int getCurrentMainOptionMenuId() {
        return this.mCurrentMainOptionMenuId;
    }

    public void setCurrentMainOptionMenuId(int menuId) {
        this.mCurrentMainOptionMenuId = menuId;
    }

    public int getCurrentSubOptionMenuId() {
        return this.mCurrentSubOptionMenuId;
    }

    public void setCurrentSubOptionMenuId(int menuId) {
        this.mCurrentSubOptionMenuId = menuId;
    }

    public int getSmartPanelCustomDialogListMenuId() {
        Trace.d(this.TAG, "start getSmartPanelCustomDialogListMenuId() mSmartPanelCustomDialogListMenuId : " + this.mSmartPanelCustomDialogListMenuId);
        return this.mSmartPanelCustomDialogListMenuId;
    }

    public void setSmartPanelCustomDialogListMenuId(int menuId) {
        this.mSmartPanelCustomDialogListMenuId = menuId;
    }

    public int getPhoneCamType() {
        return this.mPhoneCamType;
    }

    public void setPhoneCamType(int phoneCamType) {
        this.mPhoneCamType = phoneCamType;
    }

    private void showOSD() {
        rvf_title.setVisibility(0);
        this.rvf_gallery.setVisibility(0);
        this.rvf_shutter_button.setVisibility(0);
        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
            showZoomBar();
        }
    }

    private void hideOSD() {
        rvf_title.setVisibility(4);
        this.rvf_gallery.setVisibility(8);
        this.rvf_shutter_button.setVisibility(8);
        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
            hideZoomBar();
        }
    }

    private void showAutoFocusFrame(boolean isAutoFocusSuccess) {
        switch (RVFFunctionManager.getAFFrameMetricsType(this.mDeviceController.getMultiAFMatrixSizeValue(), this.connectedSsid)) {
            case 0:
                if (isAutoFocusSuccess) {
                    rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_g);
                } else {
                    rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
                }
                rvf_af3x3_11.setVisibility(0);
                break;
            case 1:
                if (!isAutoFocusSuccess) {
                    rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
                    rvf_af3x3_11.setVisibility(0);
                }
                break;
            case 2:
                if (!isAutoFocusSuccess) {
                    rvf_af3x5_12.setImageResource(R.drawable.rvf_camera_af_face_r);
                    rvf_af3x5_12.setVisibility(0);
                }
                break;
            case 3:
                if (!isAutoFocusSuccess) {
                    rvf_af3x7_13.setImageResource(R.drawable.rvf_camera_af_face_r);
                    rvf_af3x7_13.setVisibility(0);
                }
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideAutoFocusFrame() {
        Trace.d(this.TAG, "start hideAutoFocusFrame()");
        rvf_af3x3_00.setVisibility(8);
        rvf_af3x3_01.setVisibility(8);
        rvf_af3x3_02.setVisibility(8);
        rvf_af3x3_10.setVisibility(8);
        rvf_af3x3_11.setVisibility(8);
        rvf_af3x3_12.setVisibility(8);
        rvf_af3x3_20.setVisibility(8);
        rvf_af3x3_21.setVisibility(8);
        rvf_af3x3_22.setVisibility(8);
        rvf_af3x5_00.setVisibility(8);
        rvf_af3x5_01.setVisibility(8);
        rvf_af3x5_02.setVisibility(8);
        rvf_af3x5_03.setVisibility(8);
        rvf_af3x5_04.setVisibility(8);
        rvf_af3x5_10.setVisibility(8);
        rvf_af3x5_11.setVisibility(8);
        rvf_af3x5_12.setVisibility(8);
        rvf_af3x5_13.setVisibility(8);
        rvf_af3x5_14.setVisibility(8);
        rvf_af3x5_20.setVisibility(8);
        rvf_af3x5_21.setVisibility(8);
        rvf_af3x5_22.setVisibility(8);
        rvf_af3x5_23.setVisibility(8);
        rvf_af3x5_24.setVisibility(8);
        rvf_af3x7_00.setVisibility(8);
        rvf_af3x7_01.setVisibility(8);
        rvf_af3x7_02.setVisibility(8);
        rvf_af3x7_03.setVisibility(8);
        rvf_af3x7_04.setVisibility(8);
        rvf_af3x7_05.setVisibility(8);
        rvf_af3x7_06.setVisibility(8);
        rvf_af3x7_10.setVisibility(8);
        rvf_af3x7_11.setVisibility(8);
        rvf_af3x7_12.setVisibility(8);
        rvf_af3x7_13.setVisibility(8);
        rvf_af3x7_14.setVisibility(8);
        rvf_af3x7_15.setVisibility(8);
        rvf_af3x7_16.setVisibility(8);
        rvf_af3x7_20.setVisibility(8);
        rvf_af3x7_21.setVisibility(8);
        rvf_af3x7_22.setVisibility(8);
        rvf_af3x7_23.setVisibility(8);
        rvf_af3x7_24.setVisibility(8);
        rvf_af3x7_25.setVisibility(8);
        rvf_af3x7_26.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAFExtend() {
        Trace.d(this.TAG, "start showAFExtend() bAFFAIL : " + this.bAFFAIL);
        if (this.bAFFAIL) {
            this.rvf_af_extend_leftup.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_leftup);
            this.rvf_af_extend_leftdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_leftdown);
            this.rvf_af_extend_rightup.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_rightup);
            this.rvf_af_extend_rightdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_rightdown);
        } else {
            this.rvf_af_extend_leftup.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_leftup);
            this.rvf_af_extend_leftdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_leftdown);
            this.rvf_af_extend_rightup.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_rightup);
            this.rvf_af_extend_rightdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_rightdown);
        }
        rvf_af_extend.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideAFExtend() {
        Trace.d(this.TAG, "start hideAFExtend()");
        rvf_af_extend.setVisibility(8);
    }

    private void setContentView() {
        Trace.d(this.TAG, "start setContentView()");
        if (this.isOptimusVu) {
            switch (getPhoneCamType()) {
                case 1:
                    setContentView(R.layout.rvf_main_layout_view);
                    break;
                case 2:
                    setContentView(R.layout.rvf_main_layout_view_full);
                    break;
                case 3:
                    setContentView(R.layout.rvf_main_layout);
                    break;
                case 4:
                    setContentView(R.layout.rvf_main_layout_full);
                    break;
            }
        } else {
            switch (getPhoneCamType()) {
                case 1:
                case 3:
                    setContentView(R.layout.rvf_main_layout);
                    break;
                case 2:
                case 4:
                    setContentView(R.layout.rvf_main_layout_full);
                    break;
            }
        }
        this.rvf_camera_btn_af = (ImageView) findViewById(R.id.rvf_camera_btn_af);
        this.rvf_camera_btn_af.setOnClickListener(this);
        this.rvf_surface = (SurfaceView) findViewById(R.id.rvf_surface);
        this.rvf_live_shutter = (RelativeLayout) findViewById(R.id.liveViewLayout);
        if (Build.MODEL.contains("N920")) {
            this.rvf_live_shutter.getLayoutParams().height = 1245;
            this.rvf_live_shutter.getLayoutParams().width = -1;
        }
        this.rvf_surface.setOnTouchListener(this);
        this.rvf_surface.setOnClickListener(this);
        rvf_touchAutoFocus = (ImageView) findViewById(R.id.rvf_touchAutoFocus);
        rvf_metering_spot_frame = (ImageView) findViewById(R.id.rvf_metering_spot_frame);
        rvf_af_extend = (RelativeLayout) findViewById(R.id.rvf_af_extend);
        this.rvf_af_extend_leftup = (ImageView) findViewById(R.id.rvf_af_extend_leftup);
        this.rvf_af_extend_leftdown = (ImageView) findViewById(R.id.rvf_af_extend_leftdown);
        this.rvf_af_extend_rightup = (ImageView) findViewById(R.id.rvf_af_extend_rightup);
        this.rvf_af_extend_rightdown = (ImageView) findViewById(R.id.rvf_af_extend_rightdown);
        rvf_af3x3_00 = (ImageView) findViewById(R.id.rvf_af3x3_00);
        rvf_af3x3_01 = (ImageView) findViewById(R.id.rvf_af3x3_01);
        rvf_af3x3_02 = (ImageView) findViewById(R.id.rvf_af3x3_02);
        rvf_af3x3_10 = (ImageView) findViewById(R.id.rvf_af3x3_10);
        rvf_af3x3_11 = (ImageView) findViewById(R.id.rvf_af3x3_11);
        rvf_af3x3_12 = (ImageView) findViewById(R.id.rvf_af3x3_12);
        rvf_af3x3_20 = (ImageView) findViewById(R.id.rvf_af3x3_20);
        rvf_af3x3_21 = (ImageView) findViewById(R.id.rvf_af3x3_21);
        rvf_af3x3_22 = (ImageView) findViewById(R.id.rvf_af3x3_22);
        rvf_af3x5_00 = (ImageView) findViewById(R.id.rvf_af3x5_00);
        rvf_af3x5_01 = (ImageView) findViewById(R.id.rvf_af3x5_01);
        rvf_af3x5_02 = (ImageView) findViewById(R.id.rvf_af3x5_02);
        rvf_af3x5_03 = (ImageView) findViewById(R.id.rvf_af3x5_03);
        rvf_af3x5_04 = (ImageView) findViewById(R.id.rvf_af3x5_04);
        rvf_af3x5_10 = (ImageView) findViewById(R.id.rvf_af3x5_10);
        rvf_af3x5_11 = (ImageView) findViewById(R.id.rvf_af3x5_11);
        rvf_af3x5_12 = (ImageView) findViewById(R.id.rvf_af3x5_12);
        rvf_af3x5_13 = (ImageView) findViewById(R.id.rvf_af3x5_13);
        rvf_af3x5_14 = (ImageView) findViewById(R.id.rvf_af3x5_14);
        rvf_af3x5_20 = (ImageView) findViewById(R.id.rvf_af3x5_20);
        rvf_af3x5_21 = (ImageView) findViewById(R.id.rvf_af3x5_21);
        rvf_af3x5_22 = (ImageView) findViewById(R.id.rvf_af3x5_22);
        rvf_af3x5_23 = (ImageView) findViewById(R.id.rvf_af3x5_23);
        rvf_af3x5_24 = (ImageView) findViewById(R.id.rvf_af3x5_24);
        rvf_af3x7_00 = (ImageView) findViewById(R.id.rvf_af3x7_00);
        rvf_af3x7_01 = (ImageView) findViewById(R.id.rvf_af3x7_01);
        rvf_af3x7_02 = (ImageView) findViewById(R.id.rvf_af3x7_02);
        rvf_af3x7_03 = (ImageView) findViewById(R.id.rvf_af3x7_03);
        rvf_af3x7_04 = (ImageView) findViewById(R.id.rvf_af3x7_04);
        rvf_af3x7_05 = (ImageView) findViewById(R.id.rvf_af3x7_05);
        rvf_af3x7_06 = (ImageView) findViewById(R.id.rvf_af3x7_06);
        rvf_af3x7_10 = (ImageView) findViewById(R.id.rvf_af3x7_10);
        rvf_af3x7_11 = (ImageView) findViewById(R.id.rvf_af3x7_11);
        rvf_af3x7_12 = (ImageView) findViewById(R.id.rvf_af3x7_12);
        rvf_af3x7_13 = (ImageView) findViewById(R.id.rvf_af3x7_13);
        rvf_af3x7_14 = (ImageView) findViewById(R.id.rvf_af3x7_14);
        rvf_af3x7_15 = (ImageView) findViewById(R.id.rvf_af3x7_15);
        rvf_af3x7_16 = (ImageView) findViewById(R.id.rvf_af3x7_16);
        rvf_af3x7_20 = (ImageView) findViewById(R.id.rvf_af3x7_20);
        rvf_af3x7_21 = (ImageView) findViewById(R.id.rvf_af3x7_21);
        rvf_af3x7_22 = (ImageView) findViewById(R.id.rvf_af3x7_22);
        rvf_af3x7_23 = (ImageView) findViewById(R.id.rvf_af3x7_23);
        rvf_af3x7_24 = (ImageView) findViewById(R.id.rvf_af3x7_24);
        rvf_af3x7_25 = (ImageView) findViewById(R.id.rvf_af3x7_25);
        rvf_af3x7_26 = (ImageView) findViewById(R.id.rvf_af3x7_26);
        this.rvf_timerCountMain = (RelativeLayout) findViewById(R.id.rvf_timerCountMain);
        this.rvf_timerHat = (ImageView) findViewById(R.id.rvf_timerHat);
        this.rvf_timecount = (ImageView) findViewById(R.id.rvf_timecount);
        rvf_title = (TextView) findViewById(R.id.rvf_title);
        this.rvf_memory_full = (ImageView) findViewById(R.id.rvf_memory_full);
        this.rvf_mode_icon = (ImageView) findViewById(R.id.rvf_mode_icon);
        this.rvf_smart_panel_icon = (ImageButton) findViewById(R.id.rvf_smart_panel_icon);
        this.rvf_smart_panel_icon.setOnClickListener(this);
        this.rvf_menu_button = (RelativeLayout) findViewById(R.id.rvf_menu_button);
        rvf_open_button = (ImageButton) findViewById(R.id.rvf_open_button);
        rvf_open_button.setOnClickListener(this);
        rvf_QuickSettingMenu = (RelativeLayout) findViewById(R.id.rvf_QuickSettingMenu);
        rvf_flash_button = (ImageButton) findViewById(R.id.rvf_flash_button);
        rvf_flash_button.setOnClickListener(this);
        rvf_timer_button = (ImageButton) findViewById(R.id.rvf_timer_button);
        rvf_timer_button.setOnClickListener(this);
        rvf_photosize_button = (ImageButton) findViewById(R.id.rvf_photosize_button);
        rvf_photosize_button.setOnClickListener(this);
        rvf_camerasave_button = (ImageButton) findViewById(R.id.rvf_camerasave_button);
        rvf_camerasave_button.setOnClickListener(this);
        rvf_cameramore_button = (ImageButton) findViewById(R.id.rvf_cameramore_button);
        rvf_cameramore_button.setOnClickListener(this);
        this.rvf_menuTop1 = (ImageView) findViewById(R.id.rvf_menuTop1);
        this.rvf_menuTop2 = (ImageView) findViewById(R.id.rvf_menuTop2);
        this.rvf_menuTop3 = (ImageView) findViewById(R.id.rvf_menuTop3);
        this.rvf_menuTop4 = (ImageView) findViewById(R.id.rvf_menuTop4);
        rvf_close_button = (ImageButton) findViewById(R.id.rvf_close_button);
        rvf_close_button.setOnClickListener(this);
        this.rvf_indicator_bar = (LinearLayout) findViewById(R.id.rvf_indicator_bar);
        this.rvf_indicator_wb = (ImageButton) findViewById(R.id.rvf_indicator_wb);
        this.rvf_indicator_metering = (ImageButton) findViewById(R.id.rvf_indicator_metering);
        this.rvf_indicator_drive = (ImageButton) findViewById(R.id.rvf_indicator_drive);
        this.rvf_indicator_flash = (ImageButton) findViewById(R.id.rvf_indicator_flash);
        this.rvf_indicator_photo_size = (ImageButton) findViewById(R.id.rvf_indicator_photo_size);
        this.rvf_indicator_quality = (ImageButton) findViewById(R.id.rvf_indicator_quality);
        this.rvf_indicator_movie_size = (ImageButton) findViewById(R.id.rvf_indicator_movie_size);
        this.rvf_indicator_af_area = (ImageButton) findViewById(R.id.rvf_indicator_af_area);
        this.rvf_indicator_touch_af = (ImageButton) findViewById(R.id.rvf_indicator_touch_af);
        this.rvf_indicator_storage = (ImageButton) findViewById(R.id.rvf_indicator_storage);
        rvf_zoomMain = (RelativeLayout) findViewById(R.id.rvf_zoomMain);
        rvf_zoomMain.setOnTouchListener(this);
        rvf_zoomout_button = (ImageButton) findViewById(R.id.rvf_zoomout_button);
        rvf_zoomout_button.setOnTouchListener(this);
        rvf_zoomin_button = (ImageButton) findViewById(R.id.rvf_zoomin_button);
        rvf_zoomin_button.setOnTouchListener(this);
        if (orientation == 1) {
            rvf_zoomSeekBar = (SeekBar) findViewById(R.id.rvf_zoomSeekBar);
        } else {
            rvf_zoomSeekBar = (VerticalSeekBar) findViewById(R.id.rvf_zoomSeekBar);
        }
        this.rvf_mode_button = (Button) findViewById(R.id.rvf_mode_button);
        this.rvf_mode_button.setOnTouchListener(this);
        this.rvf_mode_button.setOnClickListener(this);
        this.rvf_shutter_button = (ImageButton) findViewById(R.id.rvf_shutter_button);
        this.rvf_shutter_button.setOnTouchListener(this);
        this.rvf_camcorder_button = (ImageButton) findViewById(R.id.rvf_camcorder_button);
        this.rvf_camcorder_button.setOnTouchListener(this);
        this.rvf_camcorder_button.setOnClickListener(this);
        this.rvf_rec_stop_button = (ImageButton) findViewById(R.id.rvf_rec_stop_button);
        this.rvf_rec_stop_button.setOnClickListener(this);
        this.rvf_rec_title = (LinearLayout) findViewById(R.id.rvf_rec_title);
        this.rvf_rec_icon = (ImageView) findViewById(R.id.rvf_rec_icon);
        this.rvf_rec_time = (LinearLayout) findViewById(R.id.rvf_rec_time);
        this.rvf_recording_time = (TextView) findViewById(R.id.rvf_recording_time);
        this.rvf_remain_time = (TextView) findViewById(R.id.rvf_remain_time);
        this.rvf_list = (ListView) findViewById(R.id.rvf_list);
        this.rvf_option = (LinearLayout) findViewById(R.id.rvf_option);
        this.rvf_menuTitle = (TextView) findViewById(R.id.rvf_menuTitle);
        this.rvf_setting_sub_menu = (LinearLayout) findViewById(R.id.rvf_setting_sub_menu);
        this.rvf_setting_sub_menu_title = (TextView) findViewById(R.id.rvf_setting_sub_menu_title);
        this.rvf_setting_sub_menu_list = (ListView) findViewById(R.id.rvf_setting_sub_menu_list);
        this.rvf_gallery = (RelativeLayout) findViewById(R.id.rvf_gallery);
        this.rvf_thumbnail = (ImageView) findViewById(R.id.rvf_thumbnail);
        this.rvf_thumbnail.setOnClickListener(this);
        this.rvf_no_item = (ImageView) findViewById(R.id.rvf_no_item);
        this.rvf_smart_panel = (LinearLayout) findViewById(R.id.rvf_smart_panel);
        this.rvf_smart_panel.setOnClickListener(this);
        this.rvf_smart_panel_mode = (WheelView) findViewById(R.id.rvf_smart_panel_mode);
        this.rvf_smart_panel_mode.addScrollingListener(this);
        this.rvf_smart_panel_mode.addClickingListener(this);
        this.rvf_smart_panel_shutterSpeed = (WheelView) findViewById(R.id.rvf_smart_panel_shutterSpeed);
        this.rvf_smart_panel_shutterSpeed.addScrollingListener(this);
        this.rvf_smart_panel_shutterSpeed.addClickingListener(this);
        this.rvf_smart_panel_aperture = (WheelView) findViewById(R.id.rvf_smart_panel_aperture);
        this.rvf_smart_panel_aperture.addScrollingListener(this);
        this.rvf_smart_panel_aperture.addClickingListener(this);
        this.rvf_smart_panel_ev = (WheelView) findViewById(R.id.rvf_smart_panel_ev);
        this.rvf_smart_panel_ev.addScrollingListener(this);
        this.rvf_smart_panel_ev.addClickingListener(this);
        this.rvf_smart_panel_iso = (WheelView) findViewById(R.id.rvf_smart_panel_iso);
        this.rvf_smart_panel_iso.addScrollingListener(this);
        this.rvf_smart_panel_iso.addClickingListener(this);
        this.rvf_smart_panel_wb = (ImageButton) findViewById(R.id.rvf_smart_panel_wb);
        this.rvf_smart_panel_wb.setOnClickListener(this);
        this.rvf_smart_panel_metering = (ImageButton) findViewById(R.id.rvf_smart_panel_metering);
        this.rvf_smart_panel_metering.setOnClickListener(this);
        this.rvf_smart_panel_photoSize = (ImageButton) findViewById(R.id.rvf_smart_panel_photoSize);
        this.rvf_smart_panel_photoSize.setOnClickListener(this);
        this.rvf_smart_panel_quality = (ImageButton) findViewById(R.id.rvf_smart_panel_quality);
        this.rvf_smart_panel_quality.setOnClickListener(this);
        this.rvf_smart_panel_movieSize = (ImageButton) findViewById(R.id.rvf_smart_panel_movieSize);
        this.rvf_smart_panel_movieSize.setOnClickListener(this);
        this.rvf_smart_panel_afMode = (ImageButton) findViewById(R.id.rvf_smart_panel_afMode);
        this.rvf_smart_panel_afMode.setOnClickListener(this);
        this.rvf_smart_panel_drive = (ImageButton) findViewById(R.id.rvf_smart_panel_drive);
        this.rvf_smart_panel_drive.setOnClickListener(this);
        this.rvf_smart_panel_flash = (ImageButton) findViewById(R.id.rvf_smart_panel_flash);
        this.rvf_smart_panel_flash.setOnClickListener(this);
        this.rvf_smart_panel_afArea = (ImageButton) findViewById(R.id.rvf_smart_panel_afArea);
        this.rvf_smart_panel_afArea.setOnClickListener(this);
        this.rvf_smart_panel_touchAF = (ImageButton) findViewById(R.id.rvf_smart_panel_touchAF);
        this.rvf_smart_panel_touchAF.setOnClickListener(this);
        this.rvf_smart_panel_save = (ImageButton) findViewById(R.id.rvf_smart_panel_save);
        this.rvf_smart_panel_save.setOnClickListener(this);
        this.rvf_smart_panel_streaming_quality = (ImageButton) findViewById(R.id.rvf_smart_panel_streaming_quality);
        this.rvf_smart_panel_streaming_quality.setOnClickListener(this);
        this.rvf_toast_message = (TextView) findViewById(R.id.rvf_toast_message);
        this.rvf_progress = (ProgressBar) findViewById(R.id.rvf_progress);
    }

    private void init() {
        TimerTaskRun timerTaskRun = null;
        byte b = 0;
        Trace.d(this.TAG, "start init()");
        this.mExToast.register(this, 6, R.string.rvf_exit_msg, 0);
        this.mExToast.register(this, 7, R.string.insufficient_memory, 0);
        this.mExToast.register(this, 8, R.string.rvf_changed_system_setting, 0);
        this.soundPool = new SoundPool(2, 3, 0);
        this.soundTimerBeep1 = this.soundPool.load(this, R.raw.cameratimer1, 1);
        this.soundTimerBeep2 = this.soundPool.load(this, R.raw.cameratimer2, 1);
        if (!this.bConnect && this.timetask == null) {
            this.timetask = new TimerTaskRun(this, timerTaskRun);
            this.mTimer = new java.util.Timer();
            this.mTimer.schedule(this.timetask, 0L, 1000L);
        }
        if (this.frameCounter == null) {
            this.frameCounter = new FrameCounterTask(this, b == true ? 1 : 0);
            if (this.mTimer == null) {
                this.mTimer = new java.util.Timer();
            }
            this.mTimer.schedule(this.frameCounter, 0L, 1000L);
        }
        try {
            this.versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        this.upnpController.setEventHandler(this.mEventHandler);
        this.mDeviceController.setHandler(this.mDeviceControlHandler);
        this.mDeviceController.setDefaultFlash(RVFFunctionManager.getDefaultFlash(this.connectedSsid));
        this.mDeviceController.getLedTimeMenuItems().clear();
        this.mDeviceController.getLedTimeMenuItems().add(getString(R.string.rvf_timer_off));
        this.mDeviceController.getLedTimeMenuItems().add(getString(R.string.rvf_timer_2sec));
        if (RVFFunctionManager.isSupported5SecondsTimerShot(this.connectedSsid)) {
            this.mDeviceController.getLedTimeMenuItems().add(getString(R.string.rvf_timer_5sec));
        }
        this.mDeviceController.getLedTimeMenuItems().add(getString(R.string.rvf_timer_10sec));
        if (RVFFunctionManager.isSupportedDoubleTimerShot(this.connectedSsid)) {
            this.mDeviceController.getLedTimeMenuItems().add(getString(R.string.rvf_timer_double));
        }
        this.mDeviceController.getFileSaveMenuItems().clear();
        this.mDeviceController.getFileSaveMenuItems().add("Smartphone + Camera");
        this.mDeviceController.getFileSaveMenuItems().add("Camera");
        this.mConfigurationManager = SRVFConfigurationManager.getInstance(getApplicationContext());
        this.mHandleTimer = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.12
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Trace.d(LiveShutter.this.TAG, "mHandleTimer msg.what : " + msg.what);
                switch (msg.what) {
                    case 1:
                        if (LiveShutter.this.getPhoneCamType() != 1) {
                            LiveShutter.this.hideZoomBar();
                        }
                        break;
                    case 2:
                        LiveShutter.this.hideMainOptionMenu();
                        LiveShutter.this.hideSubOptionMenu();
                        if (LiveShutter.this.getPhoneCamType() == 1) {
                            if (!RVFFunctionManager.isUnsupportedZoomableLens(LiveShutter.this.connectedSsid) && Integer.parseInt(LiveShutter.this.mDeviceController.getMaxZoom()) > 0) {
                                LiveShutter.this.showZoomBar();
                            }
                            LiveShutter.this.rvf_shutter_button.setVisibility(0);
                        }
                        break;
                    case 3:
                        LiveShutter.this.hideSubOptionMenu();
                        break;
                    case 4:
                        String afArea = LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH);
                        if (afArea.equals("SELECTION AF")) {
                            LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                        } else if (afArea.equals("MULTI AF")) {
                            LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                        }
                        LiveShutter.this.mShotState = 0;
                        Trace.d(LiveShutter.this.TAG, "do action RELEASE_SELF_TIMER 07");
                        LiveShutter.this.resetVisibilityTouchAFFrame();
                        LiveShutter.this.doAction(13, "");
                        break;
                    case 5:
                    case 6:
                        if (LiveShutter.this.streampro != null) {
                            LiveShutter.this.streampro.dismiss();
                            LiveShutter.this.streampro = null;
                            LiveShutter.this.removeDialog(1010);
                        }
                        LiveShutter.this.closedata();
                        if (LiveShutter.this.isDestroyActivity() || LiveShutter.this.isFinishing()) {
                            Trace.d(LiveShutter.this.TAG, "LiveShatter Activity is finished or destroyed");
                            return;
                        }
                        LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_SHUTDOWN_BY_NOT_RESPONSE);
                        break;
                    case 7:
                        if (!CMUtil.isTestMode(LiveShutter.Appcontext) && !LiveShutter.this.bpopupflag) {
                            LiveShutter.this.bpopupflag = true;
                            LiveShutter.this.mExToast.show(6);
                            LiveShutter.this.setTimer(8);
                        }
                        break;
                    case 8:
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.RUN_EXIT");
                        if (!LiveShutter.this.isFinishing()) {
                            LiveShutter.this.showDialog(1011);
                        }
                        LiveShutter.this.onUnsubscribe();
                        break;
                    case 9:
                        LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
                        LiveShutter.this.mSmartPanelCustomDialogList.hide();
                        break;
                    case 10:
                        if (LiveShutter.this.AfFrameType != 4) {
                            if (LiveShutter.this.AfFrameType == 2) {
                                LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                            } else {
                                LiveShutter.this.hideAutoFocusFrame();
                            }
                        } else {
                            LiveShutter.this.hideAFExtend();
                        }
                        break;
                    case 11:
                        LiveShutter.this.nTimerCount--;
                        LiveShutter.this.updateLEDTimeDisplay(LiveShutter.this.nTimerCount);
                        if (LiveShutter.this.nTimerCount == 2) {
                            LiveShutter.this.soundPool.play(LiveShutter.this.soundTimerBeep2, 1.0f, 1.0f, 0, 0, 1.0f);
                            LiveShutter.this.setTimer(11);
                        } else if (LiveShutter.this.nTimerCount <= 0) {
                            LiveShutter.this.rvf_timerCountMain.setVisibility(8);
                            if (LiveShutter.bshutter) {
                                LiveShutter.this.shutterbutton(false);
                                LiveShutter.this.setShutterButtonEnabled(false);
                                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_STARTPRO");
                                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(22));
                            }
                        } else {
                            LiveShutter.this.soundPool.play(LiveShutter.this.soundTimerBeep1, 1.0f, 1.0f, 0, 0, 1.0f);
                            LiveShutter.this.setTimer(11);
                        }
                        break;
                    case 12:
                        LiveShutter.this.hideMemoryFullDialog();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        this.isOptimusVu = Utils.CheckOptimusView(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        orientation = getResources().getConfiguration().orientation;
        if (orientation == 2) {
            setPhoneCamType(3);
        } else {
            setPhoneCamType(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLayout() {
        Trace.d(this.TAG, "start initLayout()");
        if (this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
            rvf_title.setText("Auto");
        } else if (this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("P")) {
            rvf_title.setText("Program");
        } else if (this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("A")) {
            rvf_title.setText("Aperture Priority");
        } else if (this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("S")) {
            rvf_title.setText("Shutter Priority");
        } else if (this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals("M")) {
            rvf_title.setText("Manual");
        }
        if (RVFFunctionManager.isSupportedSmartPanel(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
            this.rvf_menu_button.setVisibility(8);
            if (this.mShotStateEx == 7) {
                this.rvf_smart_panel_icon.setVisibility(8);
                rvf_title.setVisibility(4);
                hideIndicatorBar();
                this.rvf_gallery.setVisibility(8);
                this.rvf_mode_button.setVisibility(8);
                this.rvf_shutter_button.setVisibility(8);
                this.rvf_camcorder_button.setVisibility(8);
                showZoomBar();
            } else if (getSmartPanelVisibility() == 0 || this.mSmartPanelCustomDialogWheelMenuId != 0) {
                this.rvf_smart_panel_icon.setVisibility(8);
                rvf_title.setVisibility(4);
                hideIndicatorBar();
                this.rvf_gallery.setVisibility(8);
                this.rvf_mode_button.setVisibility(8);
                this.rvf_shutter_button.setVisibility(8);
                this.rvf_camcorder_button.setVisibility(8);
                hideZoomBar();
            } else {
                this.rvf_smart_panel_icon.setVisibility(0);
                rvf_title.setVisibility(0);
                showIndicatorBar();
                this.rvf_gallery.setVisibility(0);
                this.rvf_mode_button.setVisibility(0);
                this.rvf_shutter_button.setVisibility(0);
                if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
                    this.rvf_camcorder_button.setVisibility(0);
                }
                showZoomBar();
            }
            if (this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF")) {
                setImageResource(this.rvf_smart_panel_afArea);
                Trace.d(this.TAG, ">>>>>>> 001");
                this.rvf_smart_panel_afArea.setEnabled(false);
                setImageResource(this.rvf_smart_panel_touchAF);
                this.rvf_smart_panel_touchAF.setEnabled(false);
            }
            if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF") && !this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO) && this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                rvf_metering_spot_frame.setVisibility(0);
            }
        } else {
            this.rvf_menu_button.setVisibility(0);
            this.rvf_smart_panel_icon.setVisibility(8);
            rvf_title.setVisibility(0);
            showIndicatorBar();
            this.rvf_gallery.setVisibility(0);
            this.rvf_mode_button.setVisibility(8);
            this.rvf_shutter_button.setVisibility(0);
            this.rvf_camcorder_button.setVisibility(8);
            showZoomBar();
        }
        if (!RVFFunctionManager.isTouchableZoomButton(this.connectedSsid)) {
            rvf_zoomin_button.setEnabled(false);
            rvf_zoomout_button.setEnabled(false);
        }
        rvf_zoomSeekBar.setMax(Integer.parseInt(this.mDeviceController.getMaxZoom()) - Integer.parseInt(this.mDeviceController.getMinZoom()));
        zoombardisplay();
        if (RVFFunctionManager.isTouchableZoomSeekBar(this.connectedSsid)) {
            rvf_zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.13
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    LiveShutter.this.setTimer(1);
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Trace.d(LiveShutter.this.TAG, "onStopTrackingTouch() current Zoom : " + LiveShutter.this.mDeviceController.getDefaultZoom() + " tracked Zoom : " + LiveShutter.rvf_zoomSeekBar.getProgress());
                    if (!LiveShutter.this.mDeviceController.getDefaultZoom().equals(String.valueOf(LiveShutter.rvf_zoomSeekBar.getProgress()))) {
                        LiveShutter.this.mDeviceController.setDefaultZoom(String.valueOf(LiveShutter.rvf_zoomSeekBar.getProgress()));
                        LiveShutter.this.doAction(6, String.valueOf(LiveShutter.rvf_zoomSeekBar.getProgress()));
                    }
                }
            });
        } else {
            rvf_zoomSeekBar.setEnabled(false);
        }
        if (RVFFunctionManager.isSupportedSmartPanel(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
            setSmartPanel();
            if (this.mShotStateEx == 7) {
                if (this.mDeviceController.isTouchAFMovie()) {
                    this.rvf_camera_btn_af.setVisibility(0);
                }
                this.rvf_rec_stop_button.setVisibility(0);
                if (this.mShotState == 0) {
                    this.rvf_rec_stop_button.setEnabled(false);
                }
                this.rvf_rec_title.setVisibility(0);
                this.rvf_rec_time.setVisibility(0);
            } else if (getSmartPanelCustomDialogListMenuId() != 0) {
                setSmartPanelVisibility(0);
                if (getPhoneCamType() == 1) {
                    this.rvf_rec_stop_button.setVisibility(8);
                    hideZoomBar();
                } else {
                    hideOSD();
                }
            } else {
                this.rvf_surface.setEnabled(true);
                this.rvf_rec_stop_button.setVisibility(8);
                this.rvf_rec_title.setVisibility(8);
                this.rvf_rec_time.setVisibility(8);
            }
            this.rvf_recording_time.setText(getTimeFormat(this.mDeviceController.getMovieRecordTime()));
            this.rvf_remain_time.setText(getTimeFormat(this.mDeviceController.getRemainRecTimeValue()));
            setSmartPanel();
            setSmartPanelVisibility(getSmartPanelVisibility());
        } else {
            if (RVFFunctionManager.isSupportedMoreButton(this.connectedSsid)) {
                rvf_camerasave_button.setVisibility(8);
            } else {
                rvf_cameramore_button.setVisibility(8);
            }
            this.mMainOptionMenuListAdapter = new MainOptionMenuListAdapter(this, R.layout.rvf_row, this.mMainOptionMenuList);
            this.rvf_list.setOnItemClickListener(this.mMainOptionMenuItemClickListener);
            this.rvf_list.setAdapter((ListAdapter) this.mMainOptionMenuListAdapter);
            this.mSubOptionMenuListAdapter = new SubOptionMenuListAdapter(this, R.layout.rvf_row, this.mSubOptionMenuList);
            this.rvf_setting_sub_menu_list.setOnItemClickListener(this.mSubOptionMenuItemClickListener);
            this.rvf_setting_sub_menu_list.setAdapter((ListAdapter) this.mSubOptionMenuListAdapter);
            switch (getPhoneCamType()) {
                case 1:
                    if (getCurrentMainOptionMenuId() != 0) {
                        showQuickSettingMenu();
                        showMainOptionMenu(getCurrentMainOptionMenuId());
                        if (getCurrentSubOptionMenuId() != 0) {
                            showSubOptionMenu(getCurrentSubOptionMenuId());
                        }
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            hideZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(8);
                    } else {
                        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
                            showZoomBar();
                        }
                        this.rvf_shutter_button.setVisibility(0);
                    }
                    break;
                case 2:
                case 3:
                case 4:
                    if (getCurrentMainOptionMenuId() != 0) {
                        showQuickSettingMenu();
                        showMainOptionMenu(getCurrentMainOptionMenuId());
                        if (getCurrentSubOptionMenuId() != 0) {
                            showSubOptionMenu(getCurrentSubOptionMenuId());
                        }
                        hideOSD();
                    } else {
                        showOSD();
                    }
                    break;
            }
            setSmartPanelVisibility(8);
        }
        if (this.nTimerCount > 0) {
            this.rvf_timerCountMain.setVisibility(0);
            if (this.nTimerCount > 2) {
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer);
            } else {
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer_b);
            }
            Trace.d(this.TAG, "nTimerCount : " + this.nTimerCount);
            updateLEDTimeDisplay(this.nTimerCount);
            updateZoomButtonEnabled();
            if (getPhoneCamType() != 1) {
                setVisibilityOSD(8);
            }
            this.rvf_timerCountMain.setVisibility(0);
            updateLEDTimeDisplay(this.nTimerCount);
        }
        this.rvf_list.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.14
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LiveShutter.this.setTimer(1);
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
        this.mMainOptionMenuListAdapter = new MainOptionMenuListAdapter(this, R.layout.rvf_row, this.mMainOptionMenuList);
        this.rvf_list.setAdapter((ListAdapter) this.mMainOptionMenuListAdapter);
        this.rvf_setting_sub_menu_list.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.15
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LiveShutter.this.setTimer(1);
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
        if (RVFFunctionManager.isSupportedMoreButton(this.connectedSsid)) {
            rvf_camerasave_button.setVisibility(8);
        } else {
            rvf_cameramore_button.setVisibility(8);
        }
        updateImageOfGallaryButton();
        if (this.bConnect) {
            setShutterButtonEnabled(true);
            setButtonEnabled(true);
        } else {
            setShutterButtonEnabled(false);
            setButtonEnabled(false);
        }
        setIndicator();
    }

    private void showQuickSettingMenu() {
        Trace.d(this.TAG, "show QuickSettingMenu");
        hideIndicatorBar();
        rvf_QuickSettingMenu.setVisibility(0);
        rvf_open_button.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideQuickSettingMenu() {
        Trace.d(this.TAG, "hide QuickSettingMenu");
        showIndicatorBar();
        rvf_QuickSettingMenu.setVisibility(8);
        rvf_open_button.setVisibility(0);
    }

    private void showMainOptionMenu(int menuId) {
        if (!isAvailShot()) {
            hideMemoryFullDialog();
        }
        setCurrentMainOptionMenuId(menuId);
        this.rvf_option.setVisibility(0);
        this.rvf_menuTop1.setVisibility(8);
        this.rvf_menuTop2.setVisibility(8);
        this.rvf_menuTop3.setVisibility(8);
        this.rvf_menuTop4.setVisibility(8);
        this.mMainOptionMenuList.clear();
        switch (menuId) {
            case 1:
                this.rvf_menuTop1.setVisibility(0);
                this.rvf_menuTitle.setText(R.string.rvf_flash);
                if (getPhoneCamType() == 2) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
                    params.topMargin = getApplicationContext().getResources().getInteger(R.integer.rvf_top_margin_of_1st_option_menu);
                }
                Trace.d(this.TAG, "mDeviceController.getFlashMenuItems().size() : " + this.mDeviceController.getFlashMenuItems().size());
                for (int i = 0; i < this.mDeviceController.getFlashMenuItems().size(); i++) {
                    String menuItem = this.mDeviceController.getFlashMenuItems().get(i).toUpperCase(Locale.ENGLISH);
                    if (menuItem.equals(DeviceController.FlashMode.AUTO)) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_auto)));
                    } else if (menuItem.equals("REDEYE")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_red_eye)));
                    } else if (menuItem.equals("FILLIN") || menuItem.equals("FILL-IN") || menuItem.equals("FILL IN")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_fill_in)));
                    } else if (menuItem.equals("SLOWSYNC")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_slow_sync)));
                    } else if (menuItem.equals("REDEYEFIX")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_red_eye_fix)));
                    } else if (menuItem.equals("FILLINREDEYE") || menuItem.equals("FILL-IN_RED") || menuItem.equals("FILL-IN RED")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_fill_in_red_eye)));
                    } else if (menuItem.equals("1STCURTAIN") || menuItem.equals("1ST_CURTAIN") || menuItem.equals("1ST CURTAIN")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_1st_curtain)));
                    } else if (menuItem.equals("2NDCURTAIN") || menuItem.equals("2ND_CURTAIN") || menuItem.equals("2ND CURTAIN")) {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_2nd_curtain)));
                    } else {
                        this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_flash_off)));
                    }
                }
                break;
            case 2:
                this.rvf_menuTop2.setVisibility(0);
                this.rvf_menuTitle.setText(R.string.rvf_timer);
                if (getPhoneCamType() == 2) {
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
                    params2.topMargin = getApplicationContext().getResources().getInteger(R.integer.rvf_top_margin_of_2nd_option_menu);
                }
                for (int i2 = 0; i2 < this.mDeviceController.getLedTimeMenuItems().size(); i2++) {
                    this.mMainOptionMenuList.add(new Menu(this.mDeviceController.getLedTimeMenuItems().get(i2)));
                }
                break;
            case 3:
                this.rvf_menuTop3.setVisibility(0);
                this.rvf_menuTitle.setText(R.string.rvf_photo_size);
                if (getPhoneCamType() == 2) {
                    RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
                    params3.topMargin = getApplicationContext().getResources().getInteger(R.integer.rvf_top_margin_of_3rd_option_menu);
                }
                for (int i3 = 0; i3 < this.mDeviceController.getResolutionMenuItems().size(); i3++) {
                    DSCResolution dscResolution = this.mDeviceController.getResolutionMenuItems().get(i3);
                    String prefix = Utils.unitChange(this, dscResolution.getHeight(), dscResolution.getWidth(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                    String title = String.format("%s (%dx%d)", prefix, Integer.valueOf(dscResolution.getWidth()), Integer.valueOf(dscResolution.getHeight()));
                    this.mMainOptionMenuList.add(new Menu(title));
                }
                break;
            case 4:
                this.rvf_menuTop4.setVisibility(0);
                this.rvf_menuTitle.setText(R.string.rvf_photo_storage);
                if (getPhoneCamType() == 2) {
                    RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
                    params4.topMargin = getApplicationContext().getResources().getInteger(R.integer.rvf_top_margin_of_4th_option_menu);
                }
                this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_smart_camera)));
                this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_camera)));
                break;
            case 5:
                this.rvf_menuTop4.setVisibility(0);
                this.rvf_menuTitle.setText(R.string.rvf_setting);
                if (getPhoneCamType() == 2) {
                    RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
                    params5.topMargin = getApplicationContext().getResources().getInteger(R.integer.rvf_top_margin_of_4th_option_menu);
                }
                this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_photo_storage)));
                this.mMainOptionMenuList.add(new Menu(getString(R.string.rvf_streaming_quality)));
                break;
        }
        this.mMainOptionMenuListAdapter.notifyDataSetChanged();
        setTimer(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideMainOptionMenu() {
        setCurrentMainOptionMenuId(0);
        this.rvf_option.setVisibility(8);
        this.rvf_menuTop1.setVisibility(8);
        this.rvf_menuTop2.setVisibility(8);
        this.rvf_menuTop3.setVisibility(8);
        this.rvf_menuTop4.setVisibility(8);
        setImageResource(rvf_flash_button);
        setImageResource(rvf_timer_button);
        setImageResource(rvf_photosize_button);
        setImageResource(rvf_camerasave_button);
        setImageResource(rvf_cameramore_button);
        setTimer(1);
        if (!isAvailShot()) {
            showMemoryFullDialog(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSubOptionMenu(int menuId) {
        setCurrentSubOptionMenuId(menuId);
        this.rvf_setting_sub_menu.setVisibility(0);
        this.mSubOptionMenuList.clear();
        if (getPhoneCamType() == 2) {
            RelativeLayout.LayoutParams mainParams = (RelativeLayout.LayoutParams) this.rvf_option.getLayoutParams();
            RelativeLayout.LayoutParams subParams = (RelativeLayout.LayoutParams) this.rvf_setting_sub_menu.getLayoutParams();
            subParams.topMargin = mainParams.topMargin + 56;
        }
        switch (menuId) {
            case 4:
                this.rvf_setting_sub_menu_title.setText(R.string.rvf_photo_storage);
                this.mSubOptionMenuList.add(new Menu(getString(R.string.rvf_smart_camera)));
                this.mSubOptionMenuList.add(new Menu(getString(R.string.rvf_camera)));
                break;
            case 6:
                this.rvf_setting_sub_menu_title.setText(R.string.rvf_streaming_quality);
                for (int i = 0; i < this.mDeviceController.getStreamQualityMenuItems().size(); i++) {
                    if (this.mDeviceController.getStreamQualityMenuItems().get(i).equals("high")) {
                        this.mSubOptionMenuList.add(new Menu("VGA"));
                    } else if (this.mDeviceController.getStreamQualityMenuItems().get(i).equals("low")) {
                        this.mSubOptionMenuList.add(new Menu("QVGA"));
                    }
                }
                break;
        }
        this.mSubOptionMenuListAdapter.notifyDataSetChanged();
        setTimer(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSubOptionMenu() {
        setCurrentSubOptionMenuId(0);
        this.rvf_setting_sub_menu.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unexpected iteration count in SwitchBreakVisitor. Please report as an issue */
    public void setImageResource(ImageButton button) {
        DSCResolution dscResolution;
        Trace.d(this.TAG, "start setImageResource()");
        switch (button.getId()) {
            case R.id.rvf_flash_button /* 2131558466 */:
                if (this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF) || this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals("") || this.mDeviceController.getFlashStrobeStatus().toUpperCase(Locale.ENGLISH).equals("DETACHED")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                } else {
                    String defaultFlash = this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH);
                    Trace.d(this.TAG, "defaultFlash : " + defaultFlash);
                    if (defaultFlash.equals(DeviceController.FlashMode.OFF)) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    } else if (defaultFlash.equals(DeviceController.FlashMode.AUTO)) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (defaultFlash.equals("REDEYE")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_redeye);
                    } else if (defaultFlash.equals("FILLIN") || defaultFlash.equals("FILL-IN") || defaultFlash.equals("FILL IN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (defaultFlash.equals("SLOWSYNC")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_slow_sync);
                    } else if (defaultFlash.equals("REDEYEFIX")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_redeye_fix);
                    } else if (defaultFlash.equals("FILLINREDEYE") || defaultFlash.equals("FILL-IN_RED") || defaultFlash.equals("FILL-IN RED")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (defaultFlash.equals("1STCURTAIN") || defaultFlash.equals("1ST_CURTAIN") || defaultFlash.equals("1ST CURTAIN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (defaultFlash.equals("2NDCURTAIN") || defaultFlash.equals("2ND_CURTAIN") || defaultFlash.equals("2ND CURTAIN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    }
                }
                break;
            case R.id.rvf_timer_button /* 2131558467 */:
                if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_off1);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("2SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_2sec);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("5SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_5sec);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("10SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_10sec);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("DOUBLE")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_double);
                }
                break;
            case R.id.rvf_photosize_button /* 2131558468 */:
                if (this.mDeviceController.getResolutionMenuItems().size() > 0) {
                    DSCResolution dscResolution2 = this.mDeviceController.getResolutionMenuItems().get(Integer.parseInt(this.mDeviceController.getDefaultResolutionIndex()));
                    if (dscResolution2 != null) {
                        String title = Utils.unitChange(this, dscResolution2.getWidth(), dscResolution2.getHeight(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                        if (title.equals("1M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_1m);
                            break;
                        } else if (title.equals("1.1M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_1_1m);
                            break;
                        } else if (title.equals("2M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_2m);
                            break;
                        } else if (title.equals("2.1M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_2_1m);
                            break;
                        } else if (title.equals("W2M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_2wm);
                            break;
                        } else {
                            if (title.equals("3M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_3m);
                            } else if (title.equals("4M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_4m);
                            } else if (title.equals("4.9M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_4_9m);
                            } else if (title.equals("W4M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_4wm);
                            } else if (title.equals("5M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_5m);
                            } else if (title.equals("5.9M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_5_9m);
                            } else if (title.equals("7M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_7m);
                            } else if (title.equals("7.8M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_7_8m);
                            } else if (title.equals("W7M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_7wm);
                            } else if (title.equals("8M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_8m);
                            } else if (title.equals("9M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_9m);
                            } else if (title.equals("10M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                            } else if (title.equals("10.1M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10_1m);
                            } else if (title.equals("W10M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10wm);
                            } else if (title.equals("W12M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_12wm);
                            } else if (title.equals("13M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_13m);
                            } else if (title.equals("13.3M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_13_3m);
                            } else if (title.equals("P14M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_14pm);
                            } else if (title.equals("16M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_16m);
                            } else if (title.equals("16.9M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_16_9m);
                            } else if (title.equals("W16M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_16wm);
                            } else if (title.equals("20M")) {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_20m);
                            } else {
                                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                            }
                            break;
                        }
                    }
                } else {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                    break;
                }
                break;
            case R.id.rvf_camerasave_button /* 2131558469 */:
                if (this.nCameraSaveSelect == 0) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                } else if (this.nCameraSaveSelect == 1) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_storage_camera);
                }
                break;
            case R.id.rvf_cameramore_button /* 2131558470 */:
                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_drive_camera_setting);
                break;
            case R.id.rvf_indicator_wb /* 2131558677 */:
                if (!this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                    if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("DAYLIGHT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_daylight);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("CLOUDY")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_cloudy);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_W")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_w);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_N") || this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_NW")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_nw);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_D")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_d);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("TUNGSTEN")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_tungsten);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLASHWB")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_flash_wb);
                    } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("COLORTEMP")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_colortemp);
                    }
                }
                break;
            case R.id.rvf_indicator_metering /* 2131558678 */:
                if (!this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("MULTI")) {
                    if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("CENTERWEIGHTED") || this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("CENTER-WEIGHTED")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_metering_center_weighted);
                    } else if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_metering_spot);
                    }
                }
                break;
            case R.id.rvf_indicator_drive /* 2131558679 */:
                String drive = this.mDeviceController.getDriveValue();
                if (!drive.toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
                    if (drive.toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_drive_continuous);
                    } else if (drive.toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_2sec);
                    } else if (drive.toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_5sec);
                    } else if (drive.toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_10sec);
                    } else if (drive.toUpperCase(Locale.ENGLISH).equals("TIMER (30SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_30sec);
                    } else if (drive.toUpperCase(Locale.ENGLISH).equals("TIMER (DOUBLE)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_double);
                    }
                }
                break;
            case R.id.rvf_indicator_flash /* 2131558680 */:
                if (!this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF) && !this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals("")) {
                    String defaultFlash2 = this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH);
                    Trace.d(this.TAG, "defaultFlash : " + defaultFlash2);
                    if (!defaultFlash2.equals(DeviceController.FlashMode.OFF)) {
                        if (defaultFlash2.equals(DeviceController.FlashMode.AUTO)) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_auto);
                        } else if (defaultFlash2.equals("REDEYE")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye);
                        } else if (defaultFlash2.equals("FILLIN") || defaultFlash2.equals("FILL-IN") || defaultFlash2.equals("FILL IN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_fillin);
                        } else if (defaultFlash2.equals("SLOWSYNC")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_slow_sync);
                        } else if (defaultFlash2.equals("REDEYEFIX")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye_fix);
                        } else if (defaultFlash2.equals("FILLINREDEYE") || defaultFlash2.equals("FILL-IN_RED") || defaultFlash2.equals("FILL-IN RED")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye);
                        } else if (defaultFlash2.equals("1STCURTAIN") || defaultFlash2.equals("1ST_CURTAIN") || defaultFlash2.equals("1ST CURTAIN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_1st_curtain);
                        } else if (defaultFlash2.equals("2NDCURTAIN") || defaultFlash2.equals("2ND_CURTAIN") || defaultFlash2.equals("2ND CURTAIN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_2nd_curtain);
                        }
                    }
                    break;
                }
                break;
            case R.id.rvf_indicator_photo_size /* 2131558681 */:
                if (isDimMenu(this.rvf_indicator_photo_size) || (!this.mDeviceController.getDialModeValue().equals("Auto") && this.mDeviceController.getQualityValue().equals("RAW"))) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_size_20m);
                    break;
                } else if (this.mDeviceController.getResolutionMenuItems().size() > 0 && (dscResolution = this.mDeviceController.getResolutionMenuItems().get(Integer.parseInt(this.mDeviceController.getDefaultResolutionIndex()))) != null) {
                    String title2 = Utils.unitChange(this, dscResolution.getWidth(), dscResolution.getHeight(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                    if (title2.equals("1M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_1m);
                        break;
                    } else if (title2.equals("1.1M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_1_1m);
                        break;
                    } else if (title2.equals("2M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_2m);
                        break;
                    } else if (title2.equals("2.1M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_2_1m);
                        break;
                    } else {
                        if (title2.equals("W2M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_2wm);
                        } else if (title2.equals("3M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_3m);
                        } else if (title2.equals("4M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_4m);
                        } else if (title2.equals("W4M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_4wm);
                        } else if (title2.equals("4.9M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_4_9m);
                        } else if (title2.equals("5M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_5m);
                        } else if (title2.equals("5.9M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_5_9m);
                        } else if (title2.equals("7M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_7m);
                        } else if (title2.equals("7.8M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_7_8m);
                        } else if (title2.equals("W7M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_7wm);
                        } else if (title2.equals("8M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_8m);
                        } else if (title2.equals("9M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_9m);
                        } else if (title2.equals("10M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_10m);
                        } else if (title2.equals("10.1M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_10_1m);
                        } else if (title2.equals("W10M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_10wm);
                        } else if (title2.equals("W12M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_12wm);
                        } else if (title2.equals("13M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_13m);
                        } else if (title2.equals("13.3M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_13_3m);
                        } else if (title2.equals("P14M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_14pm);
                        } else if (title2.equals("16M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_16m);
                        } else if (title2.equals("16.9M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_16_9m);
                        } else if (title2.equals("W16M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_16wm);
                        } else if (title2.equals("20M")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_size_20m);
                        }
                        break;
                    }
                }
                break;
            case R.id.rvf_indicator_quality /* 2131558682 */:
                if (!this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("SUPER FINE") && !this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("FINE") && !this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("NORMAL")) {
                    if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("RAW")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_quality_raw);
                    } else if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("RAW+JPEG")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_quality_raw_jpeg);
                    }
                    break;
                }
                break;
            case R.id.rvf_indicator_movie_size /* 2131558683 */:
                String movieResolutionValue = this.mDeviceController.getMovieResolutionValue();
                if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("NTSC")) {
                    if (movieResolutionValue.equals("1920x1080 (15p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_15p);
                    } else if (movieResolutionValue.equals("1920x1080 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_30p);
                    } else if (movieResolutionValue.equals("1920x1080 (60p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_60p);
                    } else if (movieResolutionValue.equals("1920x810 (24p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_810_24p);
                    } else if (movieResolutionValue.equals("1280x720 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_30p);
                    } else if (movieResolutionValue.equals("1280x720 (60p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_60p);
                    } else if (movieResolutionValue.equals("640x480 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_480_30p);
                    } else if (movieResolutionValue.equals("320x240 (30p)") || movieResolutionValue.equals("For Sharing")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_240_web);
                    }
                } else if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("PAL")) {
                    if (movieResolutionValue.equals("1920x1080 (25p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_25p);
                    } else if (movieResolutionValue.equals("1280x720 (25p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_25p);
                    } else if (movieResolutionValue.equals("640x480 (25p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_480_25p);
                    } else if (movieResolutionValue.equals("For Sharing")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_240_web);
                    }
                }
                break;
            case R.id.rvf_indicator_af_area /* 2131558684 */:
                String afArea = this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH);
                if (afArea.equals("SELECTION AF")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_afarea_selection);
                } else if (afArea.equals("MULTI AF")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_afarea_multi);
                }
                break;
            case R.id.rvf_indicator_touch_af /* 2131558685 */:
                if (isDimMenu(this.rvf_indicator_touch_af)) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_off);
                } else {
                    String touchAF = this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH);
                    if (touchAF.equals(DeviceController.FlashMode.OFF)) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_off);
                    } else if (touchAF.equals("TOUCH AF")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_touchaf);
                    } else if (touchAF.equals("AF POINT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_afpoint);
                    } else if (touchAF.equals("ONE TOUCH SHOT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_onetouchshot);
                    }
                }
                break;
            case R.id.rvf_indicator_storage /* 2131558686 */:
                if (this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("INTERNAL")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_storage_phone);
                } else if (isDimMenu(this.rvf_indicator_storage)) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_storage_camera);
                } else if (this.mDeviceController.getFileSaveMenuItems().size() > 0) {
                    if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(0))) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_storage_phone_camera);
                    } else if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(1))) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_storage_camera);
                    }
                }
                break;
            case R.id.rvf_smart_panel_wb /* 2131558700 */:
                if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_acamera_wb);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("DAYLIGHT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_daylight);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("CLOUDY")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_cloudy);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_W")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_w);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_N") || this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_NW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_n);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_D")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_d);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("TUNGSTEN")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_tungsten);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("FLASHWB")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_flash_camera_wb);
                } else if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals("COLORTEMP")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_colortemp);
                }
                break;
            case R.id.rvf_smart_panel_metering /* 2131558701 */:
                if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("MULTI")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_multi);
                } else if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("CENTERWEIGHTED") || this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("CENTER-WEIGHTED")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_center_weighted);
                } else if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_spot);
                }
                break;
            case R.id.rvf_smart_panel_drive /* 2131558702 */:
                String drive2 = this.mDeviceController.getDriveValue();
                if (drive2.toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_drive_single);
                } else if (drive2.toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_drive_continuous);
                } else if (drive2.toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_timer_2sec);
                } else if (drive2.toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_timer_5sec);
                } else if (drive2.toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_timer_10sec);
                } else if (drive2.toUpperCase(Locale.ENGLISH).equals("TIMER (DOUBLE)")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_timer_double);
                }
                break;
            case R.id.rvf_smart_panel_flash /* 2131558703 */:
                if (this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF) || this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals("") || this.mDeviceController.getFlashStrobeStatus().toUpperCase(Locale.ENGLISH).equals("DETACHED")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                } else {
                    String defaultFlash3 = this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH);
                    Trace.d(this.TAG, "defaultFlash : " + defaultFlash3);
                    if (defaultFlash3.equals(DeviceController.FlashMode.OFF)) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    } else if (defaultFlash3.equals(DeviceController.FlashMode.AUTO)) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (defaultFlash3.equals("REDEYE")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye);
                    } else if (defaultFlash3.equals("FILLIN") || defaultFlash3.equals("FILL-IN") || defaultFlash3.equals("FILL IN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (defaultFlash3.equals("SLOWSYNC")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_slow_sync);
                    } else if (defaultFlash3.equals("REDEYEFIX")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye_fix);
                    } else if (defaultFlash3.equals("FILLINREDEYE") || defaultFlash3.equals("FILL-IN_RED") || defaultFlash3.equals("FILL-IN RED")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (defaultFlash3.equals("1STCURTAIN") || defaultFlash3.equals("1ST_CURTAIN") || defaultFlash3.equals("1ST CURTAIN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (defaultFlash3.equals("2NDCURTAIN") || defaultFlash3.equals("2ND_CURTAIN") || defaultFlash3.equals("2ND CURTAIN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    }
                }
                break;
            case R.id.rvf_smart_panel_photoSize /* 2131558705 */:
                if (!this.mDeviceController.getDialModeValue().equals("Auto") && this.mDeviceController.getQualityValue().equals("RAW")) {
                    this.rvf_smart_panel_photoSize.setImageResource(R.drawable.rvf_camera_size_20m_d);
                    break;
                } else if (this.mDeviceController.getResolutionMenuItems().size() > 0) {
                    DSCResolution dscResolution3 = this.mDeviceController.getResolutionMenuItems().get(Integer.parseInt(this.mDeviceController.getDefaultResolutionIndex()));
                    if (dscResolution3 != null) {
                        String title3 = Utils.unitChange(this, dscResolution3.getWidth(), dscResolution3.getHeight(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                        if (title3.equals("1M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_1m);
                            break;
                        } else if (title3.equals("1.1M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_1_1m);
                            break;
                        } else if (title3.equals("2M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_2m);
                            break;
                        } else if (title3.equals("2.1M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_2_1m);
                            break;
                        } else if (title3.equals("W2M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_2wm);
                            break;
                        } else {
                            if (title3.equals("3M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_3m);
                            } else if (title3.equals("4M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_4m);
                            } else if (title3.equals("4.9M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_4_9m);
                            } else if (title3.equals("W4M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_4wm);
                            } else if (title3.equals("5M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_5m);
                            } else if (title3.equals("5.9M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_5_9m);
                            } else if (title3.equals("7M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_7m);
                            } else if (title3.equals("7.8M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_7_8m);
                            } else if (title3.equals("W7M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_7wm);
                            } else if (title3.equals("8M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_8m);
                            } else if (title3.equals("9M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_9m);
                            } else if (title3.equals("10M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                            } else if (title3.equals("10.1M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_10_1m);
                            } else if (title3.equals("W10M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_10wm);
                            } else if (title3.equals("W12M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_12wm);
                            } else if (title3.equals("13M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_13m);
                            } else if (title3.equals("13.3M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_13_3m);
                            } else if (title3.equals("P14M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_14pm);
                            } else if (title3.equals("16M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_16m);
                            } else if (title3.equals("16.9M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_16_9m);
                            } else if (title3.equals("W16M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_16wm);
                            } else if (title3.equals("20M")) {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_20m);
                            } else {
                                button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                            }
                            break;
                        }
                    }
                } else {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                    break;
                }
                break;
            case R.id.rvf_smart_panel_quality /* 2131558706 */:
                if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("SUPER FINE")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_superfine);
                } else if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("FINE")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_fine);
                } else if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("NORMAL")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_normal);
                } else if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("RAW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_raw);
                } else if (this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("RAW+JPEG")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_raw_jpeg);
                }
                break;
            case R.id.rvf_smart_panel_movieSize /* 2131558707 */:
                String movieResolutionValue2 = this.mDeviceController.getMovieResolutionValue();
                if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("NTSC")) {
                    if (movieResolutionValue2.equals("1920x1080 (15p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_15p);
                    } else if (movieResolutionValue2.equals("1920x1080 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_30p);
                    } else if (movieResolutionValue2.equals("1920x1080 (60p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_60p);
                    } else if (movieResolutionValue2.equals("1920x810 (24p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_810_24p);
                    } else if (movieResolutionValue2.equals("1280x720 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_30p);
                    } else if (movieResolutionValue2.equals("1280x720 (60p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_60p);
                    } else if (movieResolutionValue2.equals("640x480 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_480_30p);
                    } else if (movieResolutionValue2.equals("320x240 (30p)") || movieResolutionValue2.equals("For Sharing")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_240_web);
                    }
                } else if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("PAL")) {
                    if (movieResolutionValue2.equals("1920x1080 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_25p);
                    } else if (movieResolutionValue2.equals("1280x720 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_25p);
                    } else if (movieResolutionValue2.equals("640x480 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_480_25p);
                    } else if (movieResolutionValue2.equals("For Sharing")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_240_web);
                    }
                }
                break;
            case R.id.rvf_smart_panel_streaming_quality /* 2131558708 */:
                if (this.mDeviceController.getCurrentStreamQuality().toUpperCase(Locale.ENGLISH).equals("HIGH")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_640);
                } else if (this.mDeviceController.getCurrentStreamQuality().toUpperCase(Locale.ENGLISH).equals("LOW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_320);
                }
                break;
            case R.id.rvf_smart_panel_afArea /* 2131558709 */:
                if (this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF")) {
                    if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                        button.setImageResource(R.drawable.rvf_camera_afarea_selection_d);
                    } else if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                        button.setImageResource(R.drawable.rvf_camera_afarea_multi_d);
                    }
                } else if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_afarea_selection);
                } else if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_afarea_multi);
                }
                break;
            case R.id.rvf_smart_panel_touchAF /* 2131558710 */:
                if (this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF")) {
                    button.setImageResource(R.drawable.rvf_camera_touchaf_off_d);
                } else if (this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_off);
                } else if (this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals("TOUCH AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_touchaf);
                } else if (this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals("AF POINT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_afpoint);
                }
                break;
            case R.id.rvf_smart_panel_save /* 2131558711 */:
                if (this.mDeviceController.getFileSaveMenuItems().size() > 0) {
                    if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(0))) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                    } else if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(1))) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                    }
                }
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimer(int timerID) {
        Trace.d(this.TAG, "start setTimer() timerID : " + timerID);
        long miliseconds = 0;
        Message msg = new Message();
        msg.what = timerID;
        this.mHandleTimer.removeMessages(msg.what);
        switch (timerID) {
            case 1:
                miliseconds = 5000;
                break;
            case 2:
            case 3:
                miliseconds = 300;
                break;
            case 4:
                miliseconds = 5000;
                break;
            case 5:
                miliseconds = 60000;
                break;
            case 6:
                miliseconds = 90000;
                break;
            case 7:
                miliseconds = 180000;
                break;
            case 8:
                miliseconds = 3000;
                break;
            case 9:
                miliseconds = 300;
                break;
            case 10:
                miliseconds = 1000;
                break;
            case 11:
                miliseconds = 1000;
                break;
            case 12:
                miliseconds = 3000;
                break;
        }
        this.mHandleTimer.sendMessageDelayed(msg, miliseconds);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLEDTimeDisplay(int time) {
        this.nTimerCount = time;
        switch (this.nTimerCount) {
            case 2:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_2);
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer_b);
                break;
            case 5:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_5);
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer);
                break;
            case 10:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_10);
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer);
                break;
        }
        this.rvf_timerCountMain.setVisibility(0);
        if (this.nTimerCount > 2) {
            this.soundPool.play(this.soundTimerBeep1, 1.0f, 1.0f, 0, 0, 1.0f);
        } else {
            this.soundPool.play(this.soundTimerBeep2, 1.0f, 1.0f, 0, 0, 1.0f);
        }
        setTimer(11);
    }

    private void stopLEDTimeDisplay() {
        this.nTimerCount = 0;
        this.rvf_timerCountMain.setVisibility(8);
        this.soundPool.autoPause();
        this.mHandleTimer.removeMessages(11);
        shutterbutton(true);
        bshutter = false;
        this.bshotrunflag = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLEDTimeDisplay(int time) {
        switch (time) {
            case 0:
                this.rvf_timerCountMain.setVisibility(8);
                break;
            case 1:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_1);
                break;
            case 2:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_2);
                this.rvf_timerHat.setImageResource(R.drawable.rvf_camera_timer_b);
                break;
            case 3:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_3);
                break;
            case 4:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_4);
                break;
            case 5:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_5);
                break;
            case 6:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_6);
                break;
            case 7:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_7);
                break;
            case 8:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_8);
                break;
            case 9:
                this.rvf_timecount.setBackgroundResource(R.drawable.rvf_timer_9);
                break;
        }
    }

    private void showSmartPanelCustomDialogListMenu(int menuId, String title, ArrayList<String> items) {
        ArrayList<Menu> menuItems = new ArrayList<>();
        switch (menuId) {
            case 2:
                int i = 0;
                while (i < this.mDeviceController.getResolutionMenuItems().size()) {
                    DSCResolution dscResolution = this.mDeviceController.getResolutionMenuItems().get(i);
                    String argument = String.format("%dx%d", Integer.valueOf(dscResolution.getWidth()), Integer.valueOf(dscResolution.getHeight()));
                    Menu menu = new Menu(argument);
                    String resolutionUnit = Utils.unitChange(this, dscResolution.getWidth(), dscResolution.getHeight(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                    if (resolutionUnit.equals("1M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_1m);
                    } else if (resolutionUnit.equals("1.1M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_1_1m);
                    } else if (resolutionUnit.equals("2M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_2m);
                    } else if (resolutionUnit.equals("2.1M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_2_1m);
                    } else if (resolutionUnit.equals("W2M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_2wm);
                    } else if (resolutionUnit.equals("3M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_3m);
                    } else if (resolutionUnit.equals("4M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_4m);
                    } else if (resolutionUnit.equals("4.9M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_4_9m);
                    } else if (resolutionUnit.equals("W4M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_4wm);
                    } else if (resolutionUnit.equals("5M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_5m);
                    } else if (resolutionUnit.equals("5.9M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_5_9m);
                    } else if (resolutionUnit.equals("7M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_7m);
                    } else if (resolutionUnit.equals("7.8M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_7_8m);
                    } else if (resolutionUnit.equals("W7M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_7wm);
                    } else if (resolutionUnit.equals("8M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_8m);
                    } else if (resolutionUnit.equals("9M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_9m);
                    } else if (resolutionUnit.equals("10M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_10m);
                    } else if (resolutionUnit.equals("10.1M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_10_1m);
                    } else if (resolutionUnit.equals("W10M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_10wm);
                    } else if (resolutionUnit.equals("W12M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_12wm);
                    } else if (resolutionUnit.equals("13M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_13m);
                    } else if (resolutionUnit.equals("13.3M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_13_3m);
                    } else if (resolutionUnit.equals("P14M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_14pm);
                    } else if (resolutionUnit.equals("16M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_16m);
                    } else if (resolutionUnit.equals("16.9M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_16_9m);
                    } else if (resolutionUnit.equals("W16M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_16wm);
                    } else if (resolutionUnit.equals("20M")) {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_20m);
                    } else {
                        menu.setIconResourceId(R.drawable.rvf_custom_button_camera_size_10m);
                    }
                    menu.setSelected(i == Integer.parseInt(this.mDeviceController.getDefaultResolutionIndex()));
                    menuItems.add(menu);
                    i++;
                }
                break;
            case 11:
                for (int i2 = 0; i2 < items.size(); i2++) {
                    Menu menu2 = new Menu(items.get(i2));
                    String item = items.get(i2).toUpperCase(Locale.ENGLISH);
                    if (item.equals(DeviceController.FlashMode.OFF)) {
                        menu2.setIconResourceId(R.drawable.rvf_custom_button_camera_touchaf_off);
                    } else if (item.equals("TOUCH AF")) {
                        menu2.setIconResourceId(R.drawable.rvf_custom_button_camera_touchaf_touchaf);
                    } else if (item.equals("AF POINT")) {
                        menu2.setIconResourceId(R.drawable.rvf_custom_button_camera_touchaf_afpoint);
                    }
                    menu2.setSelected(items.get(i2).equals(this.mDeviceController.getTouchAFValue()));
                    menuItems.add(menu2);
                }
                break;
            case 17:
                for (int i3 = 0; i3 < items.size(); i3++) {
                    Menu menu3 = new Menu(items.get(i3));
                    String item2 = items.get(i3).toUpperCase(Locale.ENGLISH);
                    if (item2.equals(DeviceController.FlashMode.AUTO)) {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (item2.equals("FILL-IN") || item2.equals("FILL IN")) {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (item2.equals("FILL-IN_RED") || item2.equals("FILL-IN RED")) {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (item2.equals("1ST_CURTAIN") || item2.equals("1ST CURTAIN")) {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (item2.equals("2ND_CURTAIN") || item2.equals("2ND CURTAIN")) {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    } else {
                        menu3.setIconResourceId(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    }
                    menu3.setSelected(items.get(i3).equals(this.mDeviceController.getDefaultFlash()));
                    menuItems.add(menu3);
                }
                break;
            case 18:
                for (int i4 = 0; i4 < items.size(); i4++) {
                    Menu menu4 = new Menu(items.get(i4));
                    if (items.get(i4).toUpperCase(Locale.ENGLISH).equals("HIGH")) {
                        menu4.setName("VGA");
                        menu4.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_640);
                    } else if (items.get(i4).toUpperCase(Locale.ENGLISH).equals("LOW")) {
                        menu4.setName("QVGA");
                        menu4.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_320);
                    }
                    menu4.setSelected(items.get(i4).equals(this.mDeviceController.getCurrentStreamQuality()));
                    menuItems.add(menu4);
                }
                break;
            case 25:
                for (int i5 = 0; i5 < items.size(); i5++) {
                    Menu menu5 = new Menu(items.get(i5));
                    if (items.get(i5).toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_acamera_wb);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("DAYLIGHT")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_daylight);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("CLOUDY")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_cloudy);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_W")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_fluorescent_w);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_N") || items.get(i5).toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_NW")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_fluorescent_n);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("FLUORESCENT_D")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_fluorescent_d);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("TUNGSTEN")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_tungsten);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("FLASHWB")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_flash_camera_wb);
                    } else if (items.get(i5).toUpperCase(Locale.ENGLISH).equals("COLORTEMP")) {
                        menu5.setIconResourceId(R.drawable.rvf_custom_button_camera_wb_colortemp);
                    }
                    menu5.setSelected(items.get(i5).equals(this.mDeviceController.getWBValue()));
                    menuItems.add(menu5);
                }
                break;
            case 26:
                for (int i6 = 0; i6 < items.size(); i6++) {
                    Menu menu6 = new Menu(items.get(i6));
                    if (items.get(i6).toUpperCase(Locale.ENGLISH).equals("MULTI")) {
                        menu6.setIconResourceId(R.drawable.rvf_custom_button_camera_metering_multi);
                    } else if (items.get(i6).toUpperCase(Locale.ENGLISH).equals("CENTERWEIGHTED") || items.get(i6).toUpperCase(Locale.ENGLISH).equals("CENTER-WEIGHTED")) {
                        menu6.setIconResourceId(R.drawable.rvf_custom_button_camera_metering_center_weighted);
                    } else if (items.get(i6).toUpperCase(Locale.ENGLISH).equals("SPOT")) {
                        menu6.setIconResourceId(R.drawable.rvf_custom_button_camera_metering_spot);
                    }
                    menu6.setSelected(items.get(i6).equals(this.mDeviceController.getMeteringValue()));
                    menuItems.add(menu6);
                }
                break;
            case 27:
                for (int i7 = 0; i7 < items.size(); i7++) {
                    menuItems.add(new Menu(items.get(i7)));
                }
                break;
            case 28:
                for (int i8 = 0; i8 < items.size(); i8++) {
                    Menu menu7 = new Menu(items.get(i8));
                    String item3 = items.get(i8).toUpperCase(Locale.ENGLISH);
                    if (item3.equals("SELECTION AF")) {
                        menu7.setIconResourceId(R.drawable.rvf_custom_button_camera_afarea_selection);
                    } else if (item3.equals("MULTI AF")) {
                        menu7.setIconResourceId(R.drawable.rvf_custom_button_camera_afarea_multi);
                    }
                    menu7.setSelected(items.get(i8).equals(this.mDeviceController.getAFAreaValue()));
                    menuItems.add(menu7);
                }
                break;
            case 29:
                for (int i9 = 0; i9 < items.size(); i9++) {
                    Menu menu8 = new Menu(items.get(i9));
                    if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_drive_single);
                    } else if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_drive_continuous);
                    } else if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("TIMER (2SEC)")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_timer_2sec);
                    } else if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("TIMER (5SEC)")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_timer_5sec);
                    } else if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("TIMER (10SEC)")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_timer_10sec);
                    } else if (items.get(i9).toUpperCase(Locale.ENGLISH).equals("TIMER (DOUBLE)")) {
                        menu8.setIconResourceId(R.drawable.rvf_custom_button_camera_timer_double);
                    }
                    menu8.setSelected(items.get(i9).equals(this.mDeviceController.getDriveValue()));
                    menuItems.add(menu8);
                }
                break;
            case 33:
                for (int i10 = 0; i10 < items.size(); i10++) {
                    Menu menu9 = new Menu(items.get(i10));
                    if (items.get(i10).toUpperCase(Locale.ENGLISH).equals("SUPER FINE")) {
                        menu9.setIconResourceId(R.drawable.rvf_custom_button_camera_quality_superfine);
                    } else if (items.get(i10).toUpperCase(Locale.ENGLISH).equals("FINE")) {
                        menu9.setIconResourceId(R.drawable.rvf_custom_button_camera_quality_fine);
                    } else if (items.get(i10).toUpperCase(Locale.ENGLISH).equals("NORMAL")) {
                        menu9.setIconResourceId(R.drawable.rvf_custom_button_camera_quality_normal);
                    } else if (items.get(i10).toUpperCase(Locale.ENGLISH).equals("RAW")) {
                        menu9.setIconResourceId(R.drawable.rvf_custom_button_camera_quality_raw);
                    } else if (items.get(i10).toUpperCase(Locale.ENGLISH).equals("RAW+JPEG")) {
                        menu9.setIconResourceId(R.drawable.rvf_custom_button_camera_quality_raw_jpeg);
                    }
                    menu9.setSelected(items.get(i10).equals(this.mDeviceController.getQualityValue()));
                    menuItems.add(menu9);
                }
                break;
            case 34:
                if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("NTSC")) {
                    for (int i11 = 0; i11 < this.mDeviceController.getMovieResolutionMenuItems().size(); i11++) {
                        DSCMovieResolution dscMovieResolution = this.mDeviceController.getMovieResolutionMenuItems().get(i11);
                        Menu menu10 = new Menu(dscMovieResolution.getResolution());
                        if (dscMovieResolution.getResolution().equals("1920x1080 (15p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_1080_15p);
                        } else if (dscMovieResolution.getResolution().equals("1920x1080 (30p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_1080_30p);
                        } else if (dscMovieResolution.getResolution().equals("1920x1080 (60p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_1080_60p);
                        } else if (dscMovieResolution.getResolution().equals("1920x810 (24p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_810_24p);
                        } else if (dscMovieResolution.getResolution().equals("1280x720 (30p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_720_30p);
                        } else if (dscMovieResolution.getResolution().equals("1280x720 (60p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_720_60p);
                        } else if (dscMovieResolution.getResolution().equals("640x480 (30p)")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_480_30p);
                        } else if (dscMovieResolution.getResolution().equals("320x240 (30p)") || dscMovieResolution.getResolution().equals("For Sharing")) {
                            menu10.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_240_web);
                        }
                        menu10.setSelected(dscMovieResolution.getResolution().equals(this.mDeviceController.getMovieResolutionValue()));
                        menuItems.add(menu10);
                    }
                } else if (this.mDeviceController.getVideoOutValue().toUpperCase(Locale.ENGLISH).equals("PAL")) {
                    for (int i12 = 0; i12 < this.mDeviceController.getMovieResolutionMenuItems().size(); i12++) {
                        DSCMovieResolution dscMovieResolution2 = this.mDeviceController.getMovieResolutionMenuItems().get(i12);
                        Menu menu11 = new Menu(dscMovieResolution2.getResolution());
                        if (dscMovieResolution2.getResolution().equals("1920x1080 (25p)")) {
                            menu11.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_1080_25p);
                        } else if (dscMovieResolution2.getResolution().equals("1280x720 (25p)")) {
                            menu11.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_720_25p);
                        } else if (dscMovieResolution2.getResolution().equals("640x480 (25p)")) {
                            menu11.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_480_25p);
                        } else if (dscMovieResolution2.getResolution().equals("For Sharing")) {
                            menu11.setIconResourceId(R.drawable.rvf_custom_button_camera_movie_size_240_web);
                        }
                        menu11.setSelected(dscMovieResolution2.getResolution().equals(this.mDeviceController.getMovieResolutionValue()));
                        menuItems.add(menu11);
                    }
                }
                break;
            case 37:
                for (int i13 = 0; i13 < items.size(); i13++) {
                    Menu menu12 = new Menu(items.get(i13));
                    if (i13 == 0) {
                        menu12.setIconResourceId(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                    } else if (i13 == 1) {
                        menu12.setIconResourceId(R.drawable.rvf_custom_button_camera_storage_camera);
                    }
                    menu12.setSelected(items.get(i13).equals(this.mDeviceController.getFileSaveValue()));
                    menuItems.add(menu12);
                }
                break;
        }
        setSmartPanelCustomDialogListMenuId(menuId);
        this.mSmartPanelCustomDialogListMenuItems = menuItems;
        this.mSmartPanelCustomDialogList = new CustomDialogListMenu(this, menuItems, this.mSmartPanelCustomDialogListMenuItemClickListener);
        this.mSmartPanelCustomDialogList.setTitle(title);
        this.mSmartPanelCustomDialogList.setCanceledOnTouchOutside(true);
        this.mSmartPanelCustomDialogList.show();
        this.mSmartPanelCustomDialogList.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.16
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                LiveShutter.this.setSmartPanelCustomDialogListMenuId(0);
            }
        });
    }

    private class MainOptionMenuListAdapter extends ArrayAdapter<Menu> {
        public MainOptionMenuListAdapter(Context context, int textViewResourceId, List<Menu> objects) {
            super(context, textViewResourceId, objects);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            DSCResolution dscResolution;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) LiveShutter.this.getSystemService("layout_inflater");
                convertView = layoutInflater.inflate(R.layout.rvf_row, (ViewGroup) null);
            }
            Menu item = getItem(position);
            ImageView menuIcon = (ImageView) convertView.findViewById(R.id.rvf_menu_icon);
            TextView menuName = (TextView) convertView.findViewById(R.id.rvf_menu_text);
            ImageView menuCheck = (ImageView) convertView.findViewById(R.id.rvf_menu_select);
            menuName.setText(item.getName());
            switch (LiveShutter.this.getCurrentMainOptionMenuId()) {
                case 1:
                    String flashItem = LiveShutter.this.mDeviceController.getFlashMenuItems().get(position).toUpperCase(Locale.ENGLISH);
                    if (flashItem.equals(DeviceController.FlashMode.OFF)) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    } else if (flashItem.equals(DeviceController.FlashMode.AUTO)) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (flashItem.equals("REDEYE")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye);
                    } else if (flashItem.equals("FILLIN") || flashItem.equals("FILL-IN") || flashItem.equals("FILL IN")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (flashItem.equals("SLOWSYNC")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_slow_sync);
                    } else if (flashItem.equals("REDEYEFIX")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye_fix);
                    } else if (flashItem.equals("FILLINREDEYE") || flashItem.equals("FILL-IN_RED") || flashItem.equals("FILL-IN RED")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (flashItem.equals("1STCURTAIN") || flashItem.equals("1ST_CURTAIN") || flashItem.equals("1ST CURTAIN")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (flashItem.equals("2NDCURTAIN") || flashItem.equals("2ND_CURTAIN") || flashItem.equals("2ND CURTAIN")) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    } else {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    }
                    if (LiveShutter.this.mDeviceController.getDefaultFlashIndex() == position) {
                        menuCheck.setImageResource(R.drawable.rvf_check_on);
                    } else {
                        menuCheck.setImageResource(R.drawable.rvf_check_off);
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
                case 2:
                    if (item.getName().equals(LiveShutter.this.getString(R.string.rvf_timer_off))) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_timer_off1);
                        if (LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                            menuCheck.setImageResource(R.drawable.rvf_check_on);
                        } else {
                            menuCheck.setImageResource(R.drawable.rvf_check_off);
                        }
                    } else if (item.getName().equals(LiveShutter.this.getString(R.string.rvf_timer_2sec))) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_timer_2sec);
                        if (LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("2SEC")) {
                            menuCheck.setImageResource(R.drawable.rvf_check_on);
                        } else {
                            menuCheck.setImageResource(R.drawable.rvf_check_off);
                        }
                    } else if (item.getName().equals(LiveShutter.this.getString(R.string.rvf_timer_5sec))) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_timer_5sec);
                        if (LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("5SEC")) {
                            menuCheck.setImageResource(R.drawable.rvf_check_on);
                        } else {
                            menuCheck.setImageResource(R.drawable.rvf_check_off);
                        }
                    } else if (item.getName().equals(LiveShutter.this.getString(R.string.rvf_timer_10sec))) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_timer_10sec);
                        if (LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("10SEC")) {
                            menuCheck.setImageResource(R.drawable.rvf_check_on);
                        } else {
                            menuCheck.setImageResource(R.drawable.rvf_check_off);
                        }
                    } else if (item.getName().equals(LiveShutter.this.getString(R.string.rvf_timer_double))) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_timer_double);
                        if (LiveShutter.this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("DOUBLE")) {
                            menuCheck.setImageResource(R.drawable.rvf_check_on);
                        } else {
                            menuCheck.setImageResource(R.drawable.rvf_check_off);
                        }
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
                case 3:
                    if (LiveShutter.this.mDeviceController.getResolutionMenuItems().size() > 0 && (dscResolution = LiveShutter.this.mDeviceController.getResolutionMenuItems().get(position)) != null) {
                        String title = Utils.unitChange(LiveShutter.this, dscResolution.getWidth(), dscResolution.getHeight(), FunctionManager.getPhotoSizePresentationType(LiveShutter.this.connectedSsid));
                        if (title.equals("1M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_1m);
                        } else if (title.equals("1.1M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_1_1m);
                        } else if (title.equals("2M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_2m);
                        } else if (title.equals("2.1M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_2_1m);
                        } else if (title.equals("W2M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_2wm);
                        } else if (title.equals("3M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_3m);
                        } else if (title.equals("4M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_4m);
                        } else if (title.equals("4.9M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_4_9m);
                        } else if (title.equals("W4M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_4wm);
                        } else if (title.equals("5M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_5m);
                        } else if (title.equals("5.9M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_5_9m);
                        } else if (title.equals("7M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_7m);
                        } else if (title.equals("7.8M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_7_8m);
                        } else if (title.equals("W7M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_7wm);
                        } else if (title.equals("8M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_8m);
                        } else if (title.equals("9M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_9m);
                        } else if (title.equals("10M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                        } else if (title.equals("10.1M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_10_1m);
                        } else if (title.equals("W10M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_10wm);
                        } else if (title.equals("W12M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_12wm);
                        } else if (title.equals("13M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_13m);
                        } else if (title.equals("13.3M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_13_3m);
                        } else if (title.equals("P14M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_14pm);
                        } else if (title.equals("16M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_16m);
                        } else if (title.equals("16.9M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_16_9m);
                        } else if (title.equals("W16M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_16wm);
                        } else if (title.equals("20M")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_20m);
                        } else {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                        }
                    }
                    if (Integer.parseInt(LiveShutter.this.mDeviceController.getDefaultResolutionIndex()) == position) {
                        menuCheck.setImageResource(R.drawable.rvf_check_on);
                    } else {
                        menuCheck.setImageResource(R.drawable.rvf_check_off);
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
                case 4:
                    if (position == 0) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                        LiveShutter.this.REQUESTIMAGE = 1;
                    } else if (position == 1) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                        LiveShutter.this.REQUESTIMAGE = 0;
                    }
                    if (LiveShutter.this.nCameraSaveSelect == position) {
                        menuCheck.setImageResource(R.drawable.rvf_check_on);
                    } else {
                        menuCheck.setImageResource(R.drawable.rvf_check_off);
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
                case 5:
                    if (position == 0) {
                        if (LiveShutter.this.nCameraSaveSelect != 0) {
                            if (LiveShutter.this.nCameraSaveSelect == 1) {
                                menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                            }
                        } else {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                        }
                    } else if (position == 1) {
                        if (LiveShutter.this.mDeviceController.getCurrentStreamQuality().equals("low")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_320);
                        } else {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_640);
                        }
                    }
                    menuCheck.setVisibility(4);
                    return convertView;
                default:
                    return convertView;
            }
        }
    }

    private class SubOptionMenuListAdapter extends ArrayAdapter<Menu> {
        public SubOptionMenuListAdapter(Context context, int textViewResourceId, List<Menu> objects) {
            super(context, textViewResourceId, objects);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) LiveShutter.this.getSystemService("layout_inflater");
                convertView = vi.inflate(R.layout.rvf_row, (ViewGroup) null);
            }
            Menu item = getItem(position);
            ImageView menuIcon = (ImageView) convertView.findViewById(R.id.rvf_menu_icon);
            TextView menuName = (TextView) convertView.findViewById(R.id.rvf_menu_text);
            ImageView menuCheck = (ImageView) convertView.findViewById(R.id.rvf_menu_select);
            menuName.setText(item.getName());
            switch (LiveShutter.this.getCurrentSubOptionMenuId()) {
                case 4:
                    if (position == 0) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                        LiveShutter.this.REQUESTIMAGE = 1;
                    } else if (position == 1) {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                        LiveShutter.this.REQUESTIMAGE = 0;
                    }
                    if (LiveShutter.this.nCameraSaveSelect == position) {
                        menuCheck.setBackgroundResource(R.drawable.rvf_check_on);
                    } else {
                        menuCheck.setBackgroundResource(R.drawable.rvf_check_off);
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
                case 5:
                default:
                    return convertView;
                case 6:
                    if (!LiveShutter.this.mDeviceController.getStreamQualityMenuItems().get(position).equals("high")) {
                        if (LiveShutter.this.mDeviceController.getStreamQualityMenuItems().get(position).equals("low")) {
                            menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_320);
                        }
                    } else {
                        menuIcon.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_640);
                    }
                    if (LiveShutter.this.mDeviceController.getStreamQualityMenuItems().get(position).equals(LiveShutter.this.mDeviceController.getCurrentStreamQuality())) {
                        menuCheck.setBackgroundResource(R.drawable.rvf_check_on);
                    } else {
                        menuCheck.setBackgroundResource(R.drawable.rvf_check_off);
                    }
                    menuCheck.setVisibility(0);
                    return convertView;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSmartPanel() {
        Trace.d(this.TAG, "start setSmartPanel()");
        String[] modeItems = (String[]) this.mDeviceController.getDialModeMenuItems().toArray(new String[this.mDeviceController.getDialModeMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_mode, 5, this.mDeviceController.getDialModeMenuItems().indexOf(this.mDeviceController.getDialModeValue()), modeItems, 84);
        this.rvf_smart_panel_shutterSpeed.measure(0, 0);
        Trace.d(this.TAG, "rvf_smart_panel_shutterSpeed W : " + this.rvf_smart_panel_shutterSpeed.getMeasuredWidth());
        String[] shutterSpeedItems = (String[]) this.mDeviceController.getShutterSpeedMenuItems().toArray(new String[this.mDeviceController.getShutterSpeedMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_shutterSpeed, 3, this.mDeviceController.getShutterSpeedMenuItems().indexOf(this.mDeviceController.getShutterSpeedValue()), shutterSpeedItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 3);
        String[] apertureItems = (String[]) this.mDeviceController.getApertureMenuItems().toArray(new String[this.mDeviceController.getApertureMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_aperture, 5, this.mDeviceController.getApertureMenuItems().indexOf(this.mDeviceController.getApertureValue()), apertureItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 5);
        String[] evItems = (String[]) this.mDeviceController.getEVMenuItems().toArray(new String[this.mDeviceController.getEVMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_ev, 5, this.mDeviceController.getEVMenuItems().indexOf(this.mDeviceController.getEVValue()), evItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 5);
        String[] isoItems = (String[]) this.mDeviceController.getISOMenuItems().toArray(new String[this.mDeviceController.getISOMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_iso, 3, this.mDeviceController.getISOMenuItems().indexOf(this.mDeviceController.getISOValue()), isoItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 3);
        setSmartPanelVisibility(this.mDeviceController.getDialModeValue());
        setSmartPanelEnable(this.mDeviceController.getDialModeValue());
    }

    private void setSmartPanelVisibility(String mode) {
        Trace.d(this.TAG, "start setSmartPanelVisibility()");
        this.rvf_smart_panel_shutterSpeed.setVisibility(0);
        this.rvf_smart_panel_aperture.setVisibility(0);
        if (mode.equals("Auto")) {
            this.rvf_smart_panel_ev.setVisibility(4);
            this.rvf_smart_panel_iso.setVisibility(4);
            this.rvf_smart_panel_wb.setVisibility(4);
            this.rvf_smart_panel_metering.setVisibility(4);
            this.rvf_smart_panel_photoSize.setVisibility(0);
            this.rvf_smart_panel_quality.setVisibility(4);
        } else {
            this.rvf_smart_panel_ev.setVisibility(0);
            this.rvf_smart_panel_iso.setVisibility(0);
            this.rvf_smart_panel_wb.setVisibility(0);
            this.rvf_smart_panel_metering.setVisibility(0);
            this.rvf_smart_panel_photoSize.setVisibility(0);
            this.rvf_smart_panel_quality.setVisibility(0);
        }
        if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
            this.rvf_smart_panel_movieSize.setVisibility(0);
        } else {
            this.rvf_smart_panel_movieSize.setVisibility(4);
        }
        this.rvf_smart_panel_drive.setVisibility(0);
        if (this.mDeviceController.getFlashMenuItems().size() > 0) {
            this.rvf_smart_panel_flash.setVisibility(0);
        } else {
            this.rvf_smart_panel_flash.setVisibility(4);
        }
        setImageResource(this.rvf_smart_panel_wb);
        setImageResource(this.rvf_smart_panel_metering);
        if (!mode.equals("Auto") && this.mDeviceController.getQualityValue().equals("RAW")) {
            this.rvf_smart_panel_photoSize.setImageResource(R.drawable.rvf_camera_size_20m_d);
        } else {
            setImageResource(this.rvf_smart_panel_photoSize);
        }
        if (this.mDeviceController.getAFAreaMenuItems().size() == 0) {
            this.rvf_smart_panel_afArea.setVisibility(4);
        } else {
            this.rvf_smart_panel_afArea.setVisibility(0);
        }
        if (this.mDeviceController.getTouchAFMenuItems().size() == 0) {
            this.rvf_smart_panel_touchAF.setVisibility(4);
        } else {
            this.rvf_smart_panel_touchAF.setVisibility(0);
        }
        setImageResource(this.rvf_smart_panel_quality);
        setImageResource(this.rvf_smart_panel_movieSize);
        setImageResource(this.rvf_smart_panel_afMode);
        setImageResource(this.rvf_smart_panel_drive);
        setImageResource(this.rvf_smart_panel_flash);
        setImageResource(this.rvf_smart_panel_afArea);
        setImageResource(this.rvf_smart_panel_touchAF);
        setImageResource(this.rvf_smart_panel_save);
        setImageResource(this.rvf_smart_panel_streaming_quality);
    }

    private void setSmartPanelEnable(String mode) {
        if (mode.equals("Auto")) {
            this.rvf_smart_panel_shutterSpeed.setEnabled(false);
            this.rvf_smart_panel_aperture.setEnabled(false);
            this.rvf_smart_panel_ev.setEnabled(true);
            if (isDimMenu(this.rvf_smart_panel_photoSize)) {
                this.rvf_smart_panel_photoSize.setEnabled(false);
                return;
            } else {
                this.rvf_smart_panel_photoSize.setEnabled(true);
                return;
            }
        }
        if (mode.equals("P")) {
            this.rvf_smart_panel_shutterSpeed.setEnabled(false);
            this.rvf_smart_panel_aperture.setEnabled(false);
            this.rvf_smart_panel_ev.setEnabled(true);
            if (this.mDeviceController.getQualityValue().equals("RAW") || isDimMenu(this.rvf_smart_panel_photoSize)) {
                this.rvf_smart_panel_photoSize.setEnabled(false);
                return;
            } else {
                this.rvf_smart_panel_photoSize.setEnabled(true);
                return;
            }
        }
        if (mode.equals("A")) {
            this.rvf_smart_panel_shutterSpeed.setEnabled(false);
            this.rvf_smart_panel_aperture.setEnabled(true);
            this.rvf_smart_panel_ev.setEnabled(true);
            if (this.mDeviceController.getQualityValue().equals("RAW") || isDimMenu(this.rvf_smart_panel_photoSize)) {
                this.rvf_smart_panel_photoSize.setEnabled(false);
                return;
            } else {
                this.rvf_smart_panel_photoSize.setEnabled(true);
                return;
            }
        }
        if (mode.equals("S")) {
            this.rvf_smart_panel_shutterSpeed.setEnabled(true);
            this.rvf_smart_panel_aperture.setEnabled(false);
            this.rvf_smart_panel_ev.setEnabled(true);
            if (this.mDeviceController.getQualityValue().equals("RAW") || isDimMenu(this.rvf_smart_panel_photoSize)) {
                this.rvf_smart_panel_photoSize.setEnabled(false);
                return;
            } else {
                this.rvf_smart_panel_photoSize.setEnabled(true);
                return;
            }
        }
        if (mode.equals("M")) {
            this.rvf_smart_panel_shutterSpeed.setEnabled(true);
            this.rvf_smart_panel_aperture.setEnabled(true);
            this.rvf_smart_panel_ev.setEnabled(false);
            if (this.mDeviceController.getQualityValue().equals("RAW") || isDimMenu(this.rvf_smart_panel_photoSize)) {
                this.rvf_smart_panel_photoSize.setEnabled(false);
            } else {
                this.rvf_smart_panel_photoSize.setEnabled(true);
            }
        }
    }

    private void setWheelView(WheelView view, int visibleItems, int focusIndex, String[] items, int textViewWidth) {
        Trace.d(this.TAG, "text size : " + getApplicationContext().getResources().getInteger(R.integer.rvf_smart_panel_text_view_text_size));
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(this, items);
        adapter.setTextSize(getApplicationContext().getResources().getInteger(R.integer.rvf_smart_panel_text_view_text_size));
        adapter.setTextViewWidth(textViewWidth);
        view.setVisibleItems(visibleItems);
        view.setViewAdapter(adapter);
        view.setCurrentItem(focusIndex);
        updateWheelView(view);
    }

    private void updateWheelView(WheelView view) {
        String dialMode = this.mDeviceController.getDialModeValue();
        switch (view.getId()) {
            case R.id.rvf_smart_panel_mode /* 2131558695 */:
                view.setCurrentItem(this.mDeviceController.getDialModeMenuItems().indexOf(dialMode));
                view.setEnabled(true);
                ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                break;
            case R.id.rvf_smart_panel_shutterSpeed /* 2131558696 */:
                view.setCurrentItem(this.mDeviceController.getShutterSpeedMenuItems().indexOf(this.mDeviceController.getShutterSpeedValue()));
                if (dialMode.equals("Auto") || dialMode.equals("P") || dialMode.equals("A")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("S") || dialMode.equals("M")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_aperture /* 2131558697 */:
                view.setCurrentItem(this.mDeviceController.getApertureMenuItems().indexOf(this.mDeviceController.getApertureValue()));
                if (dialMode.equals("Auto") || dialMode.equals("P") || dialMode.equals("S")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("A") || dialMode.equals("M")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_ev /* 2131558698 */:
                view.setCurrentItem(this.mDeviceController.getEVMenuItems().indexOf(this.mDeviceController.getEVValue()));
                if (dialMode.equals("Auto") || dialMode.equals("M")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("P") || dialMode.equals("A") || dialMode.equals("S")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_iso /* 2131558699 */:
                view.setCurrentItem(this.mDeviceController.getISOMenuItems().indexOf(this.mDeviceController.getISOValue()));
                if (dialMode.equals("Auto")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("P") || dialMode.equals("A") || dialMode.equals("S") || dialMode.equals("M")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
        }
        view.invalidateWheel(true);
    }

    private class ShotStateEx {
        private static final int AUTO_FOCUSING = 2;
        private static final int MOVIE_RECORDING = 7;
        private static final int NONE = 0;
        private static final int START_SHOT = 5;
        private static final int STOP_SHOT = 6;

        private ShotStateEx() {
        }
    }

    private class ShotState {
        private static final int AF = 1;
        private static final int MOVIE = 5;
        private static final int NONE = 0;
        private static final int SAVE = 4;
        private static final int SHOT = 3;
        private static final int TIMER = 2;

        private ShotState() {
        }
    }

    private class ZoomState {
        private static final int NONE = 0;
        private static final int ZOOM_IN = 1;
        private static final int ZOOM_OUT = 2;

        private ZoomState() {
        }
    }

    private class DoubleTimerShotState {
        private static final int NONE = 0;
        private static final int SECOND_10 = 2;
        private static final int SECOND_2 = 1;

        private DoubleTimerShotState() {
        }
    }

    public void doAction(int id, String param) {
        Trace.d(this.TAG, "start doAction() id : " + id + " param : " + param);
        setActionState(1);
        setTimer(5);
        if (!this.mDeviceController.doAction(id, param)) {
            if (id == 13) {
                isShotProcessing = false;
            }
            actionEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void actionStart() {
        if (this.pro == null && (this.streampro == null || !this.streampro.isShowing())) {
            this.customProgressBar = ProgressDialog.show(this, null, null);
            this.customProgressBar.setContentView(R.layout.rvf_custom_progressbar);
        }
        setButtonEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void actionEnd() {
        Trace.d(this.TAG, "start actionEnd() nTimerCount : " + this.nTimerCount + " isShotProcessing : " + isShotProcessing);
        this.mHandleTimer.removeMessages(5);
        if (!isShotProcessing) {
            setButtonEnabled(true);
            updateZoomButtonEnabled();
        }
        setActionState(2);
        if (this.customProgressBar != null) {
            this.customProgressBar.hide();
        }
    }

    private static class ActionState {
        public static final int END = 2;
        public static final int NONE = 0;
        public static final int START = 1;

        private ActionState() {
        }
    }

    private class FrameCounterTask extends TimerTask {
        private FrameCounterTask() {
        }

        /* synthetic */ FrameCounterTask(LiveShutter liveShutter, FrameCounterTask frameCounterTask) {
            this();
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            Trace.d(LiveShutter.this.TAG, "frame count : " + FFmpegJNI.request(FFmpegJNI.Command.FRAME_COUNT_GET, 0, 0, LiveShutter.this.screenPositoin, LiveShutter.this.screenSize));
        }
    }

    private class TimerTaskRun extends TimerTask {
        private TimerTaskRun() {
        }

        /* synthetic */ TimerTaskRun(LiveShutter liveShutter, TimerTaskRun timerTaskRun) {
            this();
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (!LiveShutter.this.bConnect && FFmpegJNI.request(FFmpegJNI.Command.FRAME_COUNT_GET, 0, 0, LiveShutter.this.screenPositoin, LiveShutter.this.screenSize) > 0) {
                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_MSGCONNECTED");
                LiveShutter.this.dismissDialog(1016);
                LiveShutter.this.bConnect = true;
                if (LiveShutter.rvf_touchAutoFocus.getVisibility() != 0 || !LiveShutter.this.mDeviceController.getDefaultFocusState().equals("af")) {
                    LiveShutter.this.mShotStateEx = 2;
                    if (RVFFunctionManager.getAFMode(LiveShutter.this.connectedSsid) != 0) {
                        if (RVFFunctionManager.getAFMode(LiveShutter.this.connectedSsid) == 1) {
                            LiveShutter.this.mShotState = 1;
                            LiveShutter.this.doAction(10, "");
                        }
                    } else {
                        LiveShutter.this.mShotState = 1;
                        LiveShutter.this.doAction(9, "");
                    }
                } else if (LiveShutter.this.getActionState() != 1) {
                    LiveShutter.this.mHandleTimer.removeMessages(4);
                    LiveShutter.this.mShotState = 1;
                    LiveShutter.this.doAction(12, String.valueOf(String.valueOf(LiveShutter.this.positionX)) + "x" + String.valueOf(LiveShutter.this.positionY));
                }
            }
            if (LiveShutter.this.bConnect) {
                LiveShutter.this.nConnectCount = 0;
            } else {
                LiveShutter.this.nConnectCount++;
            }
            if (LiveShutter.this.nConnectCount == 180 && !LiveShutter.this.bConnect) {
                Trace.e(LiveShutter.this.TAG, "nConnectCount == CONNECT_TIME && !bConnect");
                if (LiveShutter.this.timetask != null) {
                    LiveShutter.this.timetask.cancel();
                }
                LiveShutter.this.closedata();
                Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_CLOSEMSG");
                LiveShutter.nDisplayFlag = 7;
                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
            }
        }
    }

    private boolean isLowBattery() {
        Intent bat = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int batvalue = bat.getIntExtra("level", -1);
        if (Utils.isSCHi909()) {
            batvalue /= 10;
        }
        if (batvalue > 4) {
            return false;
        }
        return true;
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    public void onPause() {
        Trace.d(this.TAG, "start onPause()");
        super.onPause();
        if (this.mZoomState == 1) {
            this.mZoomState = 0;
            this.rvf_progress.setVisibility(8);
            doAction(8, "Tele");
        } else if (this.mZoomState == 2) {
            this.mZoomState = 0;
            this.rvf_progress.setVisibility(8);
            doAction(8, "Wide");
        } else {
            switch (this.mShotStateEx) {
                case 5:
                    this.mShotStateEx = 6;
                    doAction(31, "");
                    break;
            }
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onResume() {
        super.onResume();
        Trace.d(this.TAG, "**** liveshutter onresume");
        if (bClosing) {
            hideProgressBar();
            if (this.proap != null) {
                this.proap.dismiss();
                this.proap = null;
            }
            Trace.d(this.TAG, "onResume1");
            showDialog(1011);
            holderremove();
            appclose();
            return;
        }
        CreateDir();
        if (this.mDeviceController.isConnected()) {
            updateImageOfGallaryButton();
            setButtonEnabled(true);
            if (this.nCameraSaveSelect == 0 && CommonUtils.isMemoryFull()) {
                this.mExToast.show(7);
                setShutterButtonEnabled(false);
            } else if (this.bConnect) {
                setShutterButtonEnabled(true);
            }
        }
    }

    private void setImageBitmapOfGalleryButton() {
        String lastSaveFileName = this.mConfigurationManager.getLastSaveFileName();
        if (fileExist(lastSaveFileName)) {
            if (thumbnail_bmp == null) {
                thumbnail_bmp = getThumbnail(lastSaveFileName);
            }
        } else {
            thumbnail_bmp = null;
            ArrayList<String> fileArray = new ArrayList<>();
            File[] fileList = getFileList(Utils.getDefaultStorage());
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    fileArray.add(file.toString());
                }
                fileArray.add(lastSaveFileName);
                Object[] obj = fileArray.toArray();
                Arrays.sort(obj, new Comparator() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.17
                    @Override // java.util.Comparator
                    public int compare(Object lhs, Object rhs) {
                        String t1 = (String) lhs;
                        String t2 = (String) rhs;
                        return t1.compareTo(t2);
                    }
                });
                for (int i = 0; i < obj.length; i++) {
                    if (obj[i].toString().contains(lastSaveFileName) && i > 0) {
                        lastSaveFileName = obj[i - 1].toString();
                        thumbnail_bmp = getThumbnail(lastSaveFileName);
                        break;
                    }
                }
            }
        }
        if (thumbnail_bmp != null) {
            this.rvf_thumbnail.setImageBitmap(thumbnail_bmp);
        } else {
            lastSaveFileName = "no_file";
            this.rvf_thumbnail.setImageBitmap(null);
        }
        this.mConfigurationManager.setLastSaveFileName(lastSaveFileName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateImageOfGallaryButton() {
        Trace.d(this.TAG, "start updateImageOfGallaryButton()");
        if (CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID())) {
            setImageBitmapOfGalleryButton();
        }
        switch (getPhoneCamType()) {
            case 1:
                if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(0))) {
                    this.rvf_no_item.setBackgroundResource(R.drawable.rvf_camera_gallery_no_item02);
                    setImageBitmapOfGalleryButton();
                } else if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(1))) {
                    this.rvf_thumbnail.setImageBitmap(null);
                    this.rvf_no_item.setBackgroundResource(R.drawable.rvf_camera_gallery_no_item02_mobilelink);
                }
                break;
            case 2:
            case 3:
            case 4:
                if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(0))) {
                    setImageBitmapOfGalleryButton();
                    this.rvf_no_item.setBackgroundResource(R.drawable.rvf_camera_gallery_no_item);
                } else if (this.mDeviceController.getFileSaveValue().equals(this.mDeviceController.getFileSaveMenuItems().get(1))) {
                    this.rvf_thumbnail.setImageBitmap(null);
                    this.rvf_no_item.setBackgroundResource(R.drawable.rvf_camera_gallery_no_item_mobilelink);
                }
                break;
        }
    }

    private boolean fileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    private File[] getFileList(String dirName) {
        FilenameFilter fileFilter = new FilenameFilter() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.18
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".jpg");
            }
        };
        File dir = new File(dirName);
        return dir.listFiles(fileFilter);
    }

    private Bitmap getThumbnail(String fileName) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 16;
        Bitmap bmp = BitmapFactory.decodeFile(fileName, option);
        if (bmp != null) {
            float ratio = bmp.getWidth() / bmp.getHeight();
            return Bitmap.createScaledBitmap(bmp, (int) (50.0f * ratio), 50, true);
        }
        return bmp;
    }

    private boolean isExistImageFile(String dirName) {
        File dir = new File(dirName);
        if (dir.exists()) {
            File[] fileList = getFileList(dirName);
            if (fileList.length == 0) {
                return false;
            }
            return true;
        }
        return true;
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

    /* JADX INFO: Access modifiers changed from: private */
    public void holderremove() {
        if (holder != null) {
            holder.removeCallback(this);
            holder = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closedata() {
        Trace.d(this.TAG, "-=> closedata");
        if (!bClosing) {
            if (this.timetask != null) {
                this.timetask.cancel();
            }
            bClosing = true;
            holderremove();
            if (this.bConnect) {
                FFmpegJNI.request(FFmpegJNI.Command.VIDEO_STREAMING_QUIT, FFmpegJNI.ARG_VIDEO_STREAMING_QUIT);
            }
            this.bConnect = false;
            if (!bappclose) {
                bappclose = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void appClose() {
        Trace.d(this.TAG, "start appClose()");
        this.mHandleTimer.removeMessages(7);
        codec_stop();
        FFmpegJNI.destruct();
        CMService.getInstance().beforefinish(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void appclose() {
        Trace.d(this.TAG, "start appclose()");
        if (this.timetask != null) {
            this.timetask.cancel();
        }
        bClosing = true;
        if (this.bConnect) {
            codec_stop();
        }
        Trace.d(this.TAG, "-=> appclose DSCdevice=" + DSCdevice);
        Trace.e(this.TAG, "1 ----------- setWifiEnabled(false) -------------");
        if (!bappclose) {
            bappclose = true;
        }
        backap();
        Trace.d(this.TAG, "systemExit() before");
        Trace.d(this.TAG, "systemExit() after");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void backap() {
        if (!bVersionFail) {
            Trace.d(this.TAG, "-=> shutterbutton(false)-1 rvf_shutter_button=" + this.rvf_shutter_button);
            if (this.rvf_shutter_button != null) {
                try {
                    setShutterButtonEnabled(false);
                    shutterbutton(false);
                } catch (Exception e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shutterbutton(boolean flag) {
        if (!isButtonShow) {
            flag = false;
        }
        Trace.d(this.TAG, "-=> shutterbutton flag=" + flag + ", bConnect=" + this.bConnect);
        if (flag && this.bConnect) {
            boolean isMemoryFull = CommonUtils.isMemoryFull();
            if (this.mDeviceController != null && (!isMemoryFull || this.nCameraSaveSelect != 0)) {
                setShutterButtonEnabled(true);
            } else if (this.mDeviceController.getAvailShots().equals("-100") && (!isMemoryFull || this.nCameraSaveSelect != 0)) {
                setShutterButtonEnabled(true);
            } else {
                setShutterButtonEnabled(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonEnabled(boolean enabled) {
        Trace.d(this.TAG, "start setButtonEnabled() enabled : " + enabled);
        rvf_flash_button.setEnabled(enabled);
        rvf_timer_button.setEnabled(enabled);
        rvf_photosize_button.setEnabled(enabled);
        rvf_camerasave_button.setEnabled(enabled);
        rvf_cameramore_button.setEnabled(enabled);
        this.rvf_thumbnail.setEnabled(enabled);
        if (isAvailShot()) {
            if (RVFFunctionManager.isTouchableZoomSeekBar(this.connectedSsid)) {
                rvf_zoomSeekBar.setEnabled(enabled);
            }
            if (RVFFunctionManager.isTouchableZoomButton(this.connectedSsid)) {
                if (this.mZoomState == 1 || this.mZoomState == 2) {
                    rvf_zoomin_button.setEnabled(true);
                    rvf_zoomout_button.setEnabled(true);
                } else {
                    rvf_zoomin_button.setEnabled(enabled);
                    rvf_zoomout_button.setEnabled(enabled);
                }
            } else {
                rvf_zoomin_button.setEnabled(false);
                rvf_zoomout_button.setEnabled(false);
            }
        } else {
            rvf_zoomSeekBar.setEnabled(false);
            rvf_zoomin_button.setEnabled(false);
            rvf_zoomout_button.setEnabled(false);
        }
        this.rvf_smart_panel_icon.setEnabled(enabled);
        this.rvf_smart_panel_wb.setEnabled(enabled);
        this.rvf_smart_panel_metering.setEnabled(enabled);
        if ((!this.mDeviceController.getDialModeValue().equals("Auto") && this.mDeviceController.getQualityValue().equals("RAW")) || isDimMenu(this.rvf_smart_panel_photoSize)) {
            this.rvf_smart_panel_photoSize.setEnabled(false);
        } else {
            this.rvf_smart_panel_photoSize.setEnabled(enabled);
        }
        this.rvf_smart_panel_quality.setEnabled(enabled);
        this.rvf_smart_panel_movieSize.setEnabled(enabled);
        this.rvf_smart_panel_afMode.setEnabled(enabled);
        this.rvf_smart_panel_drive.setEnabled(enabled);
        this.rvf_smart_panel_flash.setEnabled(enabled);
        if (isDimMenu(this.rvf_smart_panel_touchAF)) {
            Trace.d(this.TAG, ">>>>>>> 002");
            this.rvf_smart_panel_afArea.setEnabled(false);
        } else {
            Trace.d(this.TAG, ">>>>>>> 003");
            this.rvf_smart_panel_afArea.setEnabled(enabled);
        }
        if (isDimMenu(this.rvf_smart_panel_touchAF)) {
            this.rvf_smart_panel_touchAF.setEnabled(false);
        } else {
            this.rvf_smart_panel_touchAF.setEnabled(enabled);
        }
        if (isDimMenu(this.rvf_smart_panel_save)) {
            this.rvf_smart_panel_save.setEnabled(false);
        } else {
            this.rvf_smart_panel_save.setEnabled(enabled);
        }
        this.rvf_smart_panel_streaming_quality.setEnabled(enabled);
        this.rvf_mode_button.setEnabled(enabled);
        this.rvf_camcorder_button.setEnabled(enabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateZoomButtonEnabled() {
        if (this.nTimerCount <= 0 && RVFFunctionManager.isTouchableZoomButton(this.connectedSsid)) {
            rvf_zoomin_button.setEnabled(true);
            rvf_zoomout_button.setEnabled(true);
        } else {
            rvf_zoomin_button.setEnabled(false);
            rvf_zoomout_button.setEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setShutterButtonEnabled(boolean state) {
        this.rvf_shutter_button.setEnabled(state);
        Trace.d(this.TAG, "start setShutterButtonEnabled() state : " + state);
    }

    private void Createchanged() {
        Trace.d(this.TAG, "start Createchanged()");
        setContentView();
        initLayout();
        if (this.mZoomState == 1) {
            this.mZoomState = 0;
            this.rvf_progress.setVisibility(8);
            doAction(8, "Tele");
        } else if (this.mZoomState == 2) {
            this.mZoomState = 0;
            this.rvf_progress.setVisibility(8);
            doAction(8, "Wide");
        } else if (this.mShotStateEx == 5) {
            this.mShotStateEx = 6;
            doAction(31, "");
        }
        if (isShotProcessing) {
            if (nShutterDnUp != 1 && this.rvf_timerCountMain.getVisibility() != 0) {
                resetVisibilityTouchAFFrame();
                new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.19
                    @Override // java.lang.Runnable
                    public void run() {
                        while (LiveShutter.this.getActionState() == 1) {
                        }
                        Trace.d(LiveShutter.this.TAG, "do action RELEASE_SELF_TIMER 09");
                        LiveShutter.this.mShotState = 0;
                        LiveShutter.this.doAction(13, "");
                    }
                }).start();
            }
            setButtonEnabled(false);
        }
        if (this.nCameraSaveSelect == 0 && CommonUtils.isMemoryFull()) {
            this.mExToast.show(7);
            setShutterButtonEnabled(false);
        }
        holderremove();
        holder = this.rvf_surface.getHolder();
        Trace.d(this.TAG, "Add callback of surface holder");
        holder.addCallback(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zoombardisplay() {
        rvf_zoomSeekBar.setProgress(Integer.parseInt(this.mDeviceController.getDefaultZoom()) - Integer.parseInt(this.mDeviceController.getMinZoom()));
        if (orientation == 2) {
            ((VerticalSeekBar) rvf_zoomSeekBar).updateThumb();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoFocus1x1() {
        Trace.d(this.TAG, "start autoFocus1x1() bAFFAIL : " + this.bAFFAIL);
        if (this.bAFFAIL) {
            rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
        } else {
            rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_g);
        }
        rvf_af3x3_11.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoFocus3x3() {
        Trace.d(this.TAG, "start autoFocus3x3() bAFFAIL : " + this.bAFFAIL);
        if (this.bAFFAIL) {
            rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
            rvf_af3x3_11.setVisibility(0);
            return;
        }
        int listCnt = this.AFData.length;
        if (listCnt != 0) {
            for (int i = 0; i < listCnt; i++) {
                if (this.AFData[i].equals("0x0")) {
                    rvf_af3x3_00.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_00.setVisibility(0);
                } else if (this.AFData[i].equals("0x1")) {
                    rvf_af3x3_01.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_01.setVisibility(0);
                } else if (this.AFData[i].equals("0x2")) {
                    rvf_af3x3_02.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_02.setVisibility(0);
                } else if (this.AFData[i].equals("1x0")) {
                    rvf_af3x3_10.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_10.setVisibility(0);
                } else if (this.AFData[i].equals("1x1")) {
                    rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_11.setVisibility(0);
                } else if (this.AFData[i].equals("1x2")) {
                    rvf_af3x3_12.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_12.setVisibility(0);
                } else if (this.AFData[i].equals("2x0")) {
                    rvf_af3x3_20.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_20.setVisibility(0);
                } else if (this.AFData[i].equals("2x1")) {
                    rvf_af3x3_21.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_21.setVisibility(0);
                } else if (this.AFData[i].equals("2x2")) {
                    rvf_af3x3_22.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x3_22.setVisibility(0);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoFocus3x5() {
        Trace.d(this.TAG, "start autoFocus3x5() bAFFAIL : " + this.bAFFAIL);
        if (this.bAFFAIL) {
            rvf_af3x5_12.setImageResource(R.drawable.rvf_camera_af_face_r);
            rvf_af3x5_12.setVisibility(0);
            return;
        }
        int listCnt = this.AFData.length;
        if (listCnt != 0) {
            for (int i = 0; i < listCnt; i++) {
                if (this.AFData[i].equals("0x0")) {
                    rvf_af3x5_00.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_00.setVisibility(0);
                } else if (this.AFData[i].equals("0x1")) {
                    rvf_af3x5_01.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_01.setVisibility(0);
                } else if (this.AFData[i].equals("0x2")) {
                    rvf_af3x5_02.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_02.setVisibility(0);
                } else if (this.AFData[i].equals("0x3")) {
                    rvf_af3x5_03.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_03.setVisibility(0);
                } else if (this.AFData[i].equals("0x4")) {
                    rvf_af3x5_04.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_04.setVisibility(0);
                } else if (this.AFData[i].equals("1x0")) {
                    rvf_af3x5_10.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_10.setVisibility(0);
                } else if (this.AFData[i].equals("1x1")) {
                    rvf_af3x5_11.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_11.setVisibility(0);
                } else if (this.AFData[i].equals("1x2")) {
                    rvf_af3x5_12.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_12.setVisibility(0);
                } else if (this.AFData[i].equals("1x3")) {
                    rvf_af3x5_13.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_13.setVisibility(0);
                } else if (this.AFData[i].equals("1x4")) {
                    rvf_af3x5_14.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_14.setVisibility(0);
                } else if (this.AFData[i].equals("2x0")) {
                    rvf_af3x5_20.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_20.setVisibility(0);
                } else if (this.AFData[i].equals("2x1")) {
                    rvf_af3x5_21.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_21.setVisibility(0);
                } else if (this.AFData[i].equals("2x2")) {
                    rvf_af3x5_22.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_22.setVisibility(0);
                } else if (this.AFData[i].equals("2x3")) {
                    rvf_af3x5_23.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_23.setVisibility(0);
                } else if (this.AFData[i].equals("2x4")) {
                    rvf_af3x5_24.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x5_24.setVisibility(0);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoFocus3x7() {
        Trace.d(this.TAG, "start autoFocus3x7() bAFFAIL : " + this.bAFFAIL);
        if (this.bAFFAIL) {
            rvf_af3x7_13.setImageResource(R.drawable.rvf_camera_af_face_r);
            rvf_af3x7_13.setVisibility(0);
            return;
        }
        int listCnt = this.AFData.length;
        if (listCnt != 0) {
            for (int i = 0; i < listCnt; i++) {
                if (this.AFData[i].equals("0x0")) {
                    rvf_af3x7_00.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_00.setVisibility(0);
                } else if (this.AFData[i].equals("0x1")) {
                    rvf_af3x7_01.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_01.setVisibility(0);
                } else if (this.AFData[i].equals("0x2")) {
                    rvf_af3x7_02.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_02.setVisibility(0);
                } else if (this.AFData[i].equals("0x3")) {
                    rvf_af3x7_03.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_03.setVisibility(0);
                } else if (this.AFData[i].equals("0x4")) {
                    rvf_af3x7_04.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_04.setVisibility(0);
                } else if (this.AFData[i].equals("0x5")) {
                    rvf_af3x7_05.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_05.setVisibility(0);
                } else if (this.AFData[i].equals("0x6")) {
                    rvf_af3x7_06.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_06.setVisibility(0);
                } else if (this.AFData[i].equals("1x0")) {
                    rvf_af3x7_10.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_10.setVisibility(0);
                } else if (this.AFData[i].equals("1x1")) {
                    rvf_af3x7_11.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_11.setVisibility(0);
                } else if (this.AFData[i].equals("1x2")) {
                    rvf_af3x7_12.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_12.setVisibility(0);
                } else if (this.AFData[i].equals("1x3")) {
                    rvf_af3x7_13.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_13.setVisibility(0);
                } else if (this.AFData[i].equals("1x4")) {
                    rvf_af3x7_14.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_14.setVisibility(0);
                } else if (this.AFData[i].equals("1x5")) {
                    rvf_af3x7_15.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_15.setVisibility(0);
                } else if (this.AFData[i].equals("1x6")) {
                    rvf_af3x7_16.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_16.setVisibility(0);
                } else if (this.AFData[i].equals("2x0")) {
                    rvf_af3x7_20.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_20.setVisibility(0);
                } else if (this.AFData[i].equals("2x1")) {
                    rvf_af3x7_21.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_21.setVisibility(0);
                } else if (this.AFData[i].equals("2x2")) {
                    rvf_af3x7_22.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_22.setVisibility(0);
                } else if (this.AFData[i].equals("2x3")) {
                    rvf_af3x7_23.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_23.setVisibility(0);
                } else if (this.AFData[i].equals("2x4")) {
                    rvf_af3x7_24.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_24.setVisibility(0);
                } else if (this.AFData[i].equals("2x5")) {
                    rvf_af3x7_25.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_25.setVisibility(0);
                } else if (this.AFData[i].equals("2x6")) {
                    rvf_af3x7_26.setImageResource(R.drawable.rvf_camera_af_face_g);
                    rvf_af3x7_26.setVisibility(0);
                }
            }
        }
    }

    private void loadingdisplay() {
        Intent intent = new Intent(this, (Class<?>) LocalGallery.class);
        intent.putExtra(LocalGallery.EXTRA_DESCRIPTION_RES_ID, R.string.connected_rvf);
        intent.putExtra(LocalGallery.EXTRA_IS_SUPPORT_BACK_BUTTON, this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("EXTERNAL"));
        if (CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID())) {
            intent.putExtra(LocalGallery.EXTRA_BACK_BUTTON_STRING_RES_ID, R.string.remote_viewfinder);
        } else {
            intent.putExtra(LocalGallery.EXTRA_BACK_BUTTON_STRING_RES_ID, R.string.mobile_link);
        }
        startActivityForResult(intent, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void buttondisplay(boolean bDisplay) {
        Trace.d(this.TAG, "buttondisplay == bDisplay:" + bDisplay + " isButtonShow : " + isButtonShow);
        if (!isButtonShow) {
            bDisplay = false;
        }
        if (bDisplay) {
            if (getPhoneCamType() == 1) {
                rvf_flash_button.setVisibility(0);
                rvf_timer_button.setVisibility(0);
                rvf_photosize_button.setVisibility(0);
                if (RVFFunctionManager.isSupportedMoreButton(this.connectedSsid)) {
                    rvf_cameramore_button.setVisibility(0);
                    return;
                } else {
                    rvf_camerasave_button.setVisibility(0);
                    return;
                }
            }
            return;
        }
        if (getPhoneCamType() == 1) {
            rvf_flash_button.setVisibility(8);
            rvf_timer_button.setVisibility(8);
            rvf_photosize_button.setVisibility(8);
            if (RVFFunctionManager.isSupportedMoreButton(this.connectedSsid)) {
                rvf_cameramore_button.setVisibility(8);
            } else {
                rvf_camerasave_button.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ZoomButtondisplay() {
        if (this.rvf_option == null || this.rvf_option.getVisibility() != 0) {
            if ((!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) || !this.bConnect) {
                showZoomBar();
            } else {
                hideZoomBar();
            }
            zoombardisplay();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void timerdisplay() {
        Trace.d(this.TAG, "-=> timerdisplay bAFFAIL=" + this.bAFFAIL + " connectedSsid : " + this.connectedSsid);
        this.mHandleTimer.removeMessages(4);
        if (this.bAFFAIL && RVFFunctionManager.isCancellableShotByAFFail(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion(), this.connectedSsid)) {
            this.mShotState = 0;
            Trace.d(this.TAG, "do action RELEASE_SELF_TIMER 10");
            resetVisibilityTouchAFFrame();
            doAction(13, "");
            Trace.e(this.TAG, "-=> timerdisplay ReleaseSelfTimerAct_DSC DISPLAY_BUTTON_ACTIVE");
            Trace.d(this.TAG, "MESSAGE : DISPLAY_BUTTON_ACTIVE");
            nDisplayFlag = 110;
            mHandler.post(new XxxThread(nDisplayFlag));
            return;
        }
        if (getPhoneCamType() != 1) {
            setVisibilityOSD(8);
        }
        this.mShotState = 2;
        setShutterButtonEnabled(true);
        if (this.mDeviceController.getDriveValue().equals("Timer (2sec)")) {
            startLEDTimeDisplay(2);
            return;
        }
        if (this.mDeviceController.getDriveValue().equals("Timer (5sec)")) {
            startLEDTimeDisplay(5);
            return;
        }
        if (this.mDeviceController.getDriveValue().equals("Timer (10sec)")) {
            startLEDTimeDisplay(10);
        } else if (this.mDeviceController.getDriveValue().equals("Timer (Double)")) {
            this.mDoubleTimerShotState = 2;
            startLEDTimeDisplay(10);
        }
    }

    private void CreateDir() {
        File baseDir = new File(Utils.getDefaultStorage());
        if (!baseDir.isDirectory()) {
            baseDir.mkdirs();
        }
    }

    public void CameraChange(String strNTS) {
        Trace.d(this.TAG, "CameraChange() strNTS : " + strNTS);
        if (strNTS.contains("rotation") && !strNTS.equals(this.cameratype)) {
            if (strNTS.equals("ssdp:rotationD") || strNTS.equals("ssdp:rotationU") || strNTS.equals("ssdp:rotationL") || strNTS.equals("ssdp:rotationR")) {
                this.cameratype = strNTS;
                new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.20
                    @Override // java.lang.Runnable
                    public void run() {
                        Trace.d(LiveShutter.this.TAG, "-=> CameraChange checkphonecamtype() orientation=" + LiveShutter.orientation);
                        LiveShutter.this.checkphonecamtype();
                        if (LiveShutter.this.getPhoneCamType() != 2 && LiveShutter.this.getPhoneCamType() != 1) {
                            return;
                        }
                        LiveShutter.this.CheckLayout();
                    }
                }).start();
                return;
            }
            return;
        }
        if (strNTS.contains("flash") && !strNTS.substring(10).toLowerCase().equals(this.mDeviceController.getCurrentFlashDisplay().toLowerCase())) {
            for (int i = 0; i < this.mDeviceController.getFlashDisplayMenuItems().size(); i++) {
                if (strNTS.substring(10).toLowerCase().equals(this.mDeviceController.getFlashDisplayMenuItems().get(i).toLowerCase())) {
                    this.mDeviceController.setCurrentFlashDisplay(this.mDeviceController.getFlashDisplayMenuItems().get(i));
                    nDisplayFlag = Const.MsgId.FLASH_RESOURCE_CHANGE;
                    mHandler.post(new XxxThread(nDisplayFlag));
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkphonecamtype() {
        Trace.d(this.TAG, "checkphonecamtype cameratype=" + this.cameratype + ", orientation=" + orientation);
        if (this.cameratype.equals("ssdp:rotationD") || this.cameratype.equals("ssdp:rotationU")) {
            if (orientation == 2) {
                setPhoneCamType(3);
            } else {
                setPhoneCamType(1);
            }
            this.cameratype.equals("ssdp:rotationD");
        } else if (this.cameratype.equals("ssdp:rotationL") || this.cameratype.equals("ssdp:rotationR")) {
            if (orientation == 2) {
                setPhoneCamType(4);
            } else {
                setPhoneCamType(2);
            }
            this.cameratype.equals("ssdp:rotationR");
        }
        Trace.d(this.TAG, "-=> phonecamtype=" + getPhoneCamType());
        if (!bClosing) {
            if (!this.mDeviceController.getRatioValue().isEmpty()) {
                if (this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                    screenConfigChange(2);
                } else if (this.mShotStateEx == 7) {
                    for (int i = 0; i < this.mDeviceController.getMovieResolutionMenuItems().size(); i++) {
                        if (this.mDeviceController.getMovieResolutionMenuItems().get(i).getResolution().equals(this.mDeviceController.getMovieResolutionValue())) {
                            screenConfigChange(Utils.stringToRatioType(this.mDeviceController.getMovieResolutionMenuItems().get(i).getRatio()));
                            break;
                        }
                    }
                } else {
                    screenConfigChange(Utils.stringToRatioType(this.mDeviceController.getRatioValue()));
                }
            }
            if (this.cameratype.equals("ssdp:rotationD")) {
                Trace.d(this.TAG, "FFmpegJNI.request arg1=" + (getPhoneCamType() - 1) + ", degree=0");
                FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, 0, this.screenPositoin, this.screenSize);
                return;
            }
            if (this.cameratype.equals("ssdp:rotationR")) {
                Trace.d(this.TAG, "FFmpegJNI.request arg1=" + (getPhoneCamType() - 1) + ", degree=270");
                FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, 270, this.screenPositoin, this.screenSize);
                Trace.d(this.TAG, "FFmpegJNI.request arg1=" + (getPhoneCamType() - 1) + ", degree=90");
                FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, 90, this.screenPositoin, this.screenSize);
                return;
            }
            if (this.cameratype.equals("ssdp:rotationU")) {
                Trace.d(this.TAG, "FFmpegJNI.request arg1=" + (getPhoneCamType() - 1) + ", degree=180");
                FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, 180, this.screenPositoin, this.screenSize);
            } else if (this.cameratype.equals("ssdp:rotationL")) {
                Trace.d(this.TAG, "FFmpegJNI.request arg1=" + (getPhoneCamType() - 1) + ", degree=270");
                FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, 270, this.screenPositoin, this.screenSize);
            }
        }
    }

    private class XxxThread extends Thread {
        public int nnDisplayFlag;

        public XxxThread(int ii) {
            this.nnDisplayFlag = ii;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Trace.e(LiveShutter.this.TAG, "-=> nnDisplayFlag=" + String.valueOf(this.nnDisplayFlag));
            long startTimeMillis = System.currentTimeMillis();
            long estimated = 0;
            boolean bWaitingMessage = false;
            do {
                switch (this.nnDisplayFlag) {
                    case 5:
                        LiveShutter.this.showDialog(1002);
                        break;
                    case 6:
                        if (LiveShutter.this.AfFrameType != 4) {
                            if (RVFFunctionManager.getAFFrameMetricsType(LiveShutter.this.mDeviceController.getMultiAFMatrixSizeValue(), LiveShutter.this.connectedSsid) != 0) {
                                if (RVFFunctionManager.getAFFrameMetricsType(LiveShutter.this.mDeviceController.getMultiAFMatrixSizeValue(), LiveShutter.this.connectedSsid) != 2) {
                                    if (RVFFunctionManager.getAFFrameMetricsType(LiveShutter.this.mDeviceController.getMultiAFMatrixSizeValue(), LiveShutter.this.connectedSsid) != 3) {
                                        LiveShutter.this.autoFocus3x3();
                                    } else {
                                        LiveShutter.this.autoFocus3x7();
                                    }
                                } else {
                                    LiveShutter.this.autoFocus3x5();
                                }
                            } else {
                                LiveShutter.this.autoFocus1x1();
                            }
                        } else {
                            LiveShutter.this.showAFExtend();
                        }
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.DISPLAY_AF type : " + RVFFunctionManager.getAFFrameMetricsType(LiveShutter.this.mDeviceController.getMultiAFMatrixSizeValue(), LiveShutter.this.connectedSsid));
                        if (!LiveShutter.this.bAFFAIL || !RVFFunctionManager.isCancellableShotByAFFail(LiveShutter.this.mDeviceController.getVersionPrefix(), LiveShutter.this.mDeviceController.getVersion(), LiveShutter.this.connectedSsid)) {
                            LiveShutter.this.vibe.vibrate(100L);
                        }
                        break;
                    case 7:
                        if (!LiveShutter.this.bpopupflag) {
                            LiveShutter.this.bpopupflag = true;
                            LiveShutter.this.showDialog(1003);
                        }
                        break;
                    case 9:
                        break;
                    case 10:
                        if (!LiveShutter.this.bpopupflag) {
                            if (LiveShutter.this.timetask != null) {
                                LiveShutter.this.timetask.cancel();
                            }
                            LiveShutter.this.bpopupflag = true;
                            LiveShutter.this.closedata();
                            LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_NETWORK_DISCONNECT);
                            this.nnDisplayFlag = 44;
                            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(this.nnDisplayFlag), 500L);
                        }
                        break;
                    case 11:
                        if (!LiveShutter.this.bpopupflag) {
                            LiveShutter.this.bpopupflag = true;
                            LiveShutter.this.mExToast.show(6);
                            LiveShutter.this.closedata();
                            this.nnDisplayFlag = 44;
                            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(this.nnDisplayFlag), 3000L);
                        }
                        break;
                    case 13:
                        LiveShutter.this.rvf_timerCountMain.setVisibility(8);
                        break;
                    case 15:
                        CharSequence str = LiveShutter.this.getString(R.string.rvf_connecting);
                        if (LiveShutter.this.pro != null) {
                            LiveShutter.this.pro.setMessage(str);
                        }
                        break;
                    case 16:
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.DISPLAY_MSGCONNECTED");
                        CharSequence str2 = LiveShutter.this.getString(R.string.rvf_connected);
                        if (LiveShutter.this.pro != null) {
                            LiveShutter.this.pro.setMessage(str2);
                        }
                        LiveShutter.isButtonShow = true;
                        LiveShutter.this.buttondisplay(true);
                        LiveShutter.this.setImageResource(LiveShutter.rvf_photosize_button);
                        if (LiveShutter.this.mConfigurationManager.isCheckedStatusOfNotice()) {
                            if (!LiveShutter.this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("INTERNAL")) {
                                if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).contains("CONTINUOUS")) {
                                    LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
                                }
                            } else {
                                LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_NO_CARD);
                            }
                        } else {
                            LiveShutter.this.showDialog(1012);
                        }
                        LiveShutter.this.isInitializedCamera = true;
                        LiveShutter.this.initLayout();
                        break;
                    case 18:
                        LiveShutter.this.showDialog(1006);
                        break;
                    case 19:
                        if (!LiveShutter.this.bpopupflag) {
                            LiveShutter.this.bpopupflag = true;
                            this.nnDisplayFlag = 44;
                            LiveShutter.this.showDialog(1007);
                            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(this.nnDisplayFlag), 500L);
                        }
                        break;
                    case 20:
                        Trace.e(LiveShutter.this.TAG, "-=> DISPLAY_SAVE nSaveCount=" + LiveShutter.this.nSaveCount);
                        if (LiveShutter.this.nSaveCount == 0) {
                            CharSequence str3 = LiveShutter.this.getString(R.string.rvf_saving);
                            LiveShutter.this.streampro.setMessage(str3);
                            Trace.e(LiveShutter.this.TAG, "-=> DISPLAY_SAVE str=" + ((Object) str3));
                            LiveShutter.bshutter = false;
                            this.nnDisplayFlag = 34;
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(this.nnDisplayFlag));
                        }
                        LiveShutter.this.nSaveCount++;
                        break;
                    case 22:
                        LiveShutter.this.showDialog(1010);
                        Trace.d(LiveShutter.this.TAG, "-=> timer clear");
                        Trace.d(LiveShutter.this.TAG, "MESSAGE : DISPLAY_SHUTTING");
                        this.nnDisplayFlag = 24;
                        LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(this.nnDisplayFlag));
                        break;
                    case 24:
                        if (!LiveShutter.bClosing) {
                            LiveShutter.this.streampro.setMessage(LiveShutter.this.getString(R.string.rvf_shutting));
                            this.nnDisplayFlag = 40;
                            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(this.nnDisplayFlag));
                        } else {
                            return;
                        }
                        break;
                    case 25:
                    case 26:
                        LiveShutter.this.btoast = false;
                        if (LiveShutter.this.toast != null) {
                            LiveShutter.this.toast.cancel();
                        }
                        break;
                    case 28:
                        Trace.d(LiveShutter.this.TAG, "-=> shutterbutton(true,false)-1 bshutter=" + LiveShutter.bshutter);
                        if (LiveShutter.bshutter) {
                            LiveShutter.this.shutterbutton(false);
                            if (LiveShutter.this.nTimerCount > 0) {
                                LiveShutter.this.setShutterButtonEnabled(true);
                            } else {
                                LiveShutter.this.setShutterButtonEnabled(false);
                            }
                        } else {
                            LiveShutter.this.shutterbutton(true);
                        }
                        break;
                    case 29:
                        LiveShutter.this.showDialog(1008);
                        break;
                    case 30:
                        LiveShutter.this.bOnZoomProcess = false;
                        LiveShutter.this.zoombardisplay();
                        break;
                    case 34:
                        LiveShutter.this.buttondisplay(true);
                        break;
                    case 40:
                        LiveShutter.this.btimercount = false;
                        LiveShutter.codec_init = false;
                        LiveShutter.cam_rotated_type = LiveShutter.this.cameratype;
                        LiveShutter.this.nSaveCount = 0;
                        if ("ssdp:rotationD".equals(LiveShutter.cam_rotated_type)) {
                            LiveShutter.cBmpRotation = 'D';
                        } else if ("ssdp:rotationL".equals(LiveShutter.cam_rotated_type)) {
                            LiveShutter.cBmpRotation = 'L';
                        } else if ("ssdp:rotationR".equals(LiveShutter.cam_rotated_type)) {
                            LiveShutter.cBmpRotation = 'R';
                        } else if ("ssdp:rotationU".equals(LiveShutter.cam_rotated_type)) {
                            LiveShutter.cBmpRotation = 'U';
                        }
                        LiveShutter.this.doAction(14, String.valueOf(LiveShutter.this.REQUESTIMAGE));
                        break;
                    case 44:
                        LiveShutter.this.appClose();
                        break;
                    case 45:
                        if (!LiveShutter.this.bpopupflag) {
                            Trace.d(LiveShutter.this.TAG, "RUN_EXIT_BYEBYE+++++++");
                            if (LiveShutter.this.timetask != null) {
                                LiveShutter.this.timetask.cancel();
                            }
                            LiveShutter.this.bpopupflag = true;
                            LiveShutter.this.closedata();
                            Trace.d(LiveShutter.this.TAG, "MESSAGE : RUN_EXIT");
                            LiveShutter.this.showDialog(1011);
                            this.nnDisplayFlag = 44;
                            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(this.nnDisplayFlag), 500L);
                        }
                        break;
                    case 47:
                        Trace.e(LiveShutter.this.TAG, "-=> Const.MsgId.RUN_DISPLAY_SHOT");
                        LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                        LiveShutter.this.showDialog(1010);
                        LiveShutter.this.setmHandler(24);
                        break;
                    case 48:
                        new InitCodecTask(LiveShutter.this, null).execute(new Void[0]);
                        LiveShutter.this.hideProgressBar();
                        break;
                    case 50:
                        Trace.e(LiveShutter.this.TAG, "-=> Const.MsgId.RUN_SHUTTER_UP");
                        LiveShutter.this.doAction(20, "");
                        break;
                    case 108:
                        LiveShutter.this.ZoomButtondisplay();
                        break;
                    case 110:
                        LiveShutter.this.resetVisibilityTouchAFFrame();
                        Trace.d(LiveShutter.this.TAG, "-=> Const.MsgId.DISPLAY_BUTTON_ACTIVE");
                        LiveShutter.bshutter = false;
                        LiveShutter.this.shutterbutton(true);
                        LiveShutter.this.setShutterButtonEnabled(true);
                        break;
                    case 111:
                        if (LiveShutter.this.streampro != null && LiveShutter.this.streampro.isShowing()) {
                            LiveShutter.this.streampro.dismiss();
                            LiveShutter.this.removeDialog(1010);
                        }
                        LiveShutter.this.shutterbutton(true);
                        break;
                    case Const.MsgId.DISPLAY_OPEN_FLASH /* 121 */:
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.FLASH_ON_DIALOG_OPEN");
                        Bundle bundle = new Bundle();
                        if (RVFFunctionManager.getFlashMountType(LiveShutter.this.connectedSsid) == 0) {
                            bundle.putString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY, LiveShutter.this.getString(R.string.rvf_external_flash_detached_msg));
                        } else {
                            bundle.putString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY, LiveShutter.this.getString(R.string.rvf_open_flash_msg));
                        }
                        LiveShutter.this.showDialog(1013, bundle);
                        break;
                    case Const.MsgId.LAYOUT_CHANGE /* 122 */:
                        LiveShutter.this.LayoutChange(LiveShutter.this.getPhoneCamType());
                        break;
                    case Const.MsgId.FLASH_RESOURCE_CHANGE /* 123 */:
                        LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                        break;
                    case Const.MsgId.CHANGED_SYSTEM_SETTING /* 124 */:
                        LiveShutter.this.appclose();
                        break;
                    case Const.MsgId.SHOT_FAILED /* 125 */:
                        LiveShutter.this.showDialog(1015);
                        LiveShutter.this.shutterbutton(true);
                        break;
                    case Const.MsgId.SET_ENABLE_SURFACEVIEW /* 144 */:
                        LiveShutter.this.rvf_surface.setEnabled(true);
                        break;
                    default:
                        Trace.d(LiveShutter.this.TAG, "\tUNKNOWN MESSAGE (DROPPED?) = " + this.nnDisplayFlag);
                        bWaitingMessage = true;
                        break;
                }
                try {
                    Thread.sleep(10L);
                } catch (Throwable th) {
                    long j = estimated * (-1);
                }
                Thread.yield();
                estimated = System.currentTimeMillis() - startTimeMillis;
                if (estimated > 1000) {
                    Trace.d(LiveShutter.this.TAG, "\tTIMEOUT (HANDLING MESSAGE)");
                    return;
                }
            } while (bWaitingMessage);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("capture")) {
            String strFilePath = String.valueOf(Utils.getDefaultStorage()) + "/test00.jpg";
            Trace.d(this.TAG, "strFilePath : " + strFilePath);
            Graph.saveBitmapToJPEGFile(FFmpegJNI.getCurrentFrame(), strFilePath);
        } else if (item.getTitle().equals("goto ML")) {
            showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.PROGRESS_BAR_SERVICE_CHANGE);
            this.mHandleTimer.removeMessages(7);
            codec_stop();
            FFmpegJNI.destruct();
            doAction(43, "changeToML");
        } else if (item.getGroupId() == 35) {
            doAction(19, this.mDeviceController.getCurrentStreamQuality());
        } else if (item.getGroupId() == 36 || item.getGroupId() > 1) {
            doAction(item.getGroupId(), item.getTitle().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog getCustomDialog() {
        this.notice = new Dialog(this);
        this.notice.requestWindowFeature(1);
        this.notice.setContentView(R.layout.rvf_custom_dialog3);
        this.notice.setCanceledOnTouchOutside(false);
        this.txtView = (TextView) this.notice.findViewById(R.id.title3);
        this.layout = (LinearLayout) this.notice.findViewById(R.id.titleLayout3);
        this.layout.setVisibility(0);
        this.txtView.setText(R.string.rvf_title_notice);
        this.closenotice = (Button) this.notice.findViewById(R.id.btnOk);
        this.notice.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.21
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case 4:
                        LiveShutter.this.onUnsubscribe();
                        return true;
                    default:
                        return false;
                }
            }
        });
        this.noticecheck = (CheckBox) this.notice.findViewById(R.id.rvf_noticecheckbox);
        this.noticecheck.setButtonDrawable(R.drawable.selector_checkbox);
        float scale = getResources().getDisplayMetrics().density;
        this.noticecheck.setPadding(this.noticecheck.getPaddingLeft() + ((int) ((10.0f * scale) + 0.5f)), this.noticecheck.getPaddingTop(), this.noticecheck.getPaddingRight(), this.noticecheck.getPaddingBottom());
        Trace.e(this.TAG, "notice is : " + this.noticecheck.isChecked());
        this.noticecheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.22
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LiveShutter.this.noticecheck.setChecked(isChecked);
                Trace.e(LiveShutter.this.TAG, "notice is : " + LiveShutter.this.noticecheck.isChecked());
            }
        });
        this.closenotice.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.23
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LiveShutter.this.notice.dismiss();
                if (LiveShutter.this.noticecheck.isChecked()) {
                    Trace.e(LiveShutter.this.TAG, "notice is checked");
                    LiveShutter.this.mConfigurationManager.setCheckedStatusOfNotice(true);
                }
                if (!LiveShutter.this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("INTERNAL")) {
                    if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).contains("CONTINUOUS")) {
                        LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
                        return;
                    }
                    return;
                }
                LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_NO_CARD);
            }
        });
        return this.notice;
    }

    private Dialog SmartPhoneSaveGuideDialog() {
        Trace.d(this.TAG, "Start SmartPhoneSaveGuideDialogForDSC");
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.rvf_save_guide_dialog, (ViewGroup) findViewById(R.id.rvf_save_resolution_guide_layout));
        boolean bSupportedSaveOnContinuousShot = true;
        ArrayList<DSCMenuItem> dimMenuItems = this.mDeviceController.getFileSaveMenuItemsDim();
        for (int i = 0; i < dimMenuItems.size(); i++) {
            DSCMenuItem item = dimMenuItems.get(i);
            if (item.getName().toUpperCase(Locale.ENGLISH).equals("DRIVE") && item.getValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                bSupportedSaveOnContinuousShot = false;
                break;
            }
        }
        Trace.d(this.TAG, "bSupportedSaveOnContinuousShot : " + bSupportedSaveOnContinuousShot);
        if (!bSupportedSaveOnContinuousShot) {
            TextView textView = (TextView) layout.findViewById(R.id.rvf_guide_text);
            textView.setText(R.string.rvf_will_not_be_saved_that_continuous_shot_and_video_and_raw);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.24
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    private Dialog SmartPhoneSaveGuideDialogForDSC() {
        Trace.d(this.TAG, "Start SmartPhoneSaveGuideDialogForDSC");
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.rvf_save_resolution_guide_dialog, (ViewGroup) findViewById(R.id.rvf_save_resolution_guide_layout));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.25
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    private Dialog SmartPhoneSaveGuideDialogForCSC() {
        Trace.d(this.TAG, "Start SmartPhoneSaveGuideDialogForCSC");
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.rvf_save_resolution_guide_dialog_u, (ViewGroup) findViewById(R.id.rvf_save_resolution_guide_layout));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.26
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setmHandler(int value) {
        Trace.e(this.TAG, "nDisplayFlag is  " + nDisplayFlag + " ==> " + value);
        nDisplayFlag = value;
        mHandler.post(new XxxThread(nDisplayFlag));
    }

    private void startAPSearch() {
        Trace.d(this.TAG, "!!!!SH100 is ready!!!!");
        CheckVersion();
        CreateDir();
        this.nConnectCount = 0;
        if (this.proap != null) {
            this.proap.dismiss();
            this.proap = null;
        }
        Trace.e(this.TAG, " this is runrun ---- 22");
        this.nConnectCount = 0;
    }

    private void CheckVersion() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.gDensity = displayMetrics.density;
        this.gDeviceWidth = displayMetrics.widthPixels;
        this.gDeviceHeight = displayMetrics.heightPixels;
        this.gWidthDips = (int) ((this.gDeviceWidth / this.gDensity) + 0.5f);
        this.gHeightDips = (int) ((this.gDeviceHeight / this.gDensity) + 0.5f);
        Trace.d(this.TAG, "density:" + displayMetrics.density + ",wdip:" + this.gWidthDips + ",hdip:" + this.gHeightDips);
        String strMaufacturer = Build.MANUFACTURER;
        strMaufacturer.toLowerCase();
        Configuration config = getResources().getConfiguration();
        if (2 == config.orientation) {
            if (this.gWidthDips < 480 || this.gHeightDips < 320) {
                bVersionFail = true;
                showDialog(1005);
                return;
            }
        } else if (this.gWidthDips < 320 || this.gHeightDips < 480) {
            bVersionFail = true;
            showDialog(1005);
            return;
        }
        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion >= 8) {
            bVersionFail = false;
        } else {
            bVersionFail = true;
            showDialog(1005);
        }
    }

    private void checkDevice(Device dev) {
        this.mDeviceList = this.upnpController.getDeviceList();
        int size = this.mDeviceList.size();
        boolean bSearchFlag = false;
        for (int i = 0; i < size; i++) {
            String devicetype = new String(this.mDeviceList.getDevice(i).getDeviceType());
            if (devicetype.contains("MediaServer")) {
                Trace.d(this.TAG, "MESSAGE : DISPLAY_MSGCONNECTING");
                nDisplayFlag = 15;
                mHandler.post(new XxxThread(nDisplayFlag));
                Device detectedDevice = this.mDeviceList.getDevice(i);
                if (detectedDevice != null) {
                    this.mDeviceController.setAction(detectedDevice);
                }
                if (!this.bConnect) {
                    bSearchFlag = true;
                    DSCdevice = detectedDevice;
                    break;
                }
            }
        }
        if (!bSearchFlag) {
            DSCdevice = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String CreatFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.ms");
        Date currentTime = new Date();
        String filename = formatter.format(currentTime);
        String sFullPath = String.valueOf(Utils.getDefaultStorage()) + "/" + filename + ".jpg";
        return sFullPath;
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Boolean> {
        private ImageDownloadTask() {
        }

        /* synthetic */ ImageDownloadTask(LiveShutter liveShutter, ImageDownloadTask imageDownloadTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            LiveShutter.nDisplayFlag = 20;
            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(String... params) throws Throwable {
            String strURL;
            Trace.d(LiveShutter.this.TAG, "start ImageDownloadTask.doInBackground()");
            String savePicName = LiveShutter.CreatFileName();
            if (RVFFunctionManager.isSupportedDownloadBYOriginalImageURL(LiveShutter.this.mDeviceController.getVersion())) {
                strURL = params[0];
            } else {
                strURL = String.valueOf(params[0]) + "_SM";
            }
            try {
                if (RVFFunctionManager.isSupportedDownloadBYOriginalImageURL(LiveShutter.this.mDeviceController.getVersion())) {
                    savePicName = LiveShutter.this.mImageDownloader.syncDownloadImage(strURL);
                    LiveShutter.thumbnail_bmp = Graph.getThumbnail(savePicName);
                    Trace.d(LiveShutter.this.TAG, "scan file path 00 : " + savePicName);
                    String mimetype = com.samsungimaging.connectionmanager.util.Utils.getMimeType(savePicName);
                    MediaScannerConnection.scanFile(LiveShutter.Appcontext, new String[]{savePicName}, new String[]{mimetype}, null);
                    LiveShutter.this.CheckDirtyCache(savePicName);
                } else {
                    URL url = new URL(strURL);
                    HTTPRequest hreq = new HTTPRequest();
                    hreq.setMethod(HTTP.GET);
                    hreq.setURI(strURL);
                    HTTPResponse hres = hreq.post(url.getHost(), url.getPort());
                    Trace.d(LiveShutter.this.TAG, "[Thumbnail] Contents Length : " + hres.getContentLength() + " bytes");
                    LiveShutter.bmImg = BitmapFactory.decodeByteArray(hres.getContent(), 0, (int) hres.getContentLength());
                    if (LiveShutter.cBmpRotation != 'D') {
                        if (LiveShutter.cBmpRotation == 'L') {
                            LiveShutter.bmImg = LiveShutter.rotate(LiveShutter.bmImg, -90);
                        } else if (LiveShutter.cBmpRotation == 'R') {
                            LiveShutter.bmImg = LiveShutter.rotate(LiveShutter.bmImg, 90);
                        } else if (LiveShutter.cBmpRotation == 'U') {
                            LiveShutter.bmImg = LiveShutter.rotate(LiveShutter.bmImg, 180);
                        }
                    }
                    if (LiveShutter.bmImg != null) {
                        float ratio = LiveShutter.bmImg.getWidth() / LiveShutter.bmImg.getHeight();
                        if (ratio > 0.0f) {
                            LiveShutter.thumbnail_bmp = Bitmap.createScaledBitmap(LiveShutter.bmImg, (int) (50.0f * ratio), 50, true);
                        }
                    }
                    if (!CommonUtils.isMemoryFull()) {
                        LiveShutter.this.SaveImageVGA(savePicName);
                        Trace.d(LiveShutter.this.TAG, "scan file path 02 : " + savePicName);
                        String mimetype2 = com.samsungimaging.connectionmanager.util.Utils.getMimeType(savePicName);
                        MediaScannerConnection.scanFile(LiveShutter.Appcontext, new String[]{savePicName}, new String[]{mimetype2}, null);
                        LiveShutter.this.CheckDirtyCache(savePicName);
                    }
                }
                if (LiveShutter.this.currentLocation_gps != null || LiveShutter.this.currentLocation_network != null) {
                    if (LiveShutter.this.currentLocation_gps == null) {
                        com.samsungimaging.connectionmanager.app.pullservice.util.Utils.setGPSInfoOfExif(LiveShutter.this.currentLocation_network, savePicName);
                    } else {
                        com.samsungimaging.connectionmanager.app.pullservice.util.Utils.setGPSInfoOfExif(LiveShutter.this.currentLocation_gps, savePicName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LiveShutter.this.mConfigurationManager.setLastSaveFileName(savePicName);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            Trace.d(LiveShutter.this.TAG, "start ImageDownloadTask.onPostExecute()");
            if (result.booleanValue()) {
                LiveShutter.this.updateImageOfGallaryButton();
            }
            if (!LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("CONTINUOUS")) {
                LiveShutter.nDisplayFlag = 48;
                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
            }
            if (LiveShutter.this.nCameraSaveSelect == 0 && CommonUtils.isMemoryFull()) {
                LiveShutter.this.mExToast.show(7);
                LiveShutter.this.setShutterButtonEnabled(false);
            }
            LiveShutter.this.mShotState = 0;
            super.onPostExecute(result);
        }
    }

    private class ShotPrepareTask extends AsyncTask<String, Void, Boolean> {
        private ShotPrepareTask() {
        }

        /* synthetic */ ShotPrepareTask(LiveShutter liveShutter, ShotPrepareTask shotPrepareTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(String... params) {
            Trace.d(LiveShutter.this.TAG, "start ShotPrepareTask.doInBackground()");
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            Trace.d(LiveShutter.this.TAG, "start ShotPrepareTask.onPostExecute()");
            LiveShutter.nDisplayFlag = 48;
            LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
            super.onPostExecute(result);
        }
    }

    private static final double roundDouble(double d, int places) {
        return Math.round(Math.pow(10.0d, places) * d) / Math.pow(10.0d, places);
    }

    private static String deg_to_dms(double degfloat) {
        if (degfloat < 0.0d) {
            return "";
        }
        int deg = (int) degfloat;
        double minfloat = (degfloat - ((double) deg)) * 60.0d;
        int min = (int) minfloat;
        double secfloat = roundDouble((minfloat - ((double) min)) * 60.0d, 2);
        if (secfloat == 60.0d) {
            min++;
        }
        if (min == 60) {
            deg++;
            min = 0;
        }
        return String.format("%d/1,%d/1,%.02f/1", Integer.valueOf(deg), Integer.valueOf(min), Double.valueOf(secfloat));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean SaveImageVGA(String savePicName) {
        Trace.d(this.TAG, "start SaveImageVGA");
        try {
            FileOutputStream out = new FileOutputStream(savePicName);
            try {
                try {
                    bmImg.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    bmImg.recycle();
                    if (!bClosing && this.nCameraSaveSelect == 0 && CommonUtils.isMemoryFull()) {
                        this.mExToast.show(7);
                        setShutterButtonEnabled(false);
                    }
                } catch (FileNotFoundException e) {
                    e = e;
                    e.printStackTrace();
                    Trace.d(this.TAG, "MESSAGE : DISPLAY_STORAGEMSG_1");
                    nDisplayFlag = 5;
                    mHandler.post(new XxxThread(nDisplayFlag));
                    return false;
                } catch (IOException e2) {
                    e = e2;
                    e.printStackTrace();
                    Trace.d(this.TAG, "MESSAGE : DISPLAY_STORAGEMSG_2");
                    nDisplayFlag = 5;
                    mHandler.post(new XxxThread(nDisplayFlag));
                    return false;
                }
            } catch (NullPointerException e3) {
                e3.printStackTrace();
                nDisplayFlag = 5;
                mHandler.post(new XxxThread(nDisplayFlag));
            }
            out.close();
            bsaveflag = true;
            return true;
        } catch (FileNotFoundException e4) {
            e = e4;
        } catch (IOException e5) {
            e = e5;
        }
    }

    private void GetLiveStream() {
        Trace.d(this.TAG, "start GetLiveStream()");
        this.isSettedLiveStream = true;
        new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.27
            @Override // java.lang.Runnable
            public void run() {
                LiveShutter.bshutter = false;
                Trace.d(LiveShutter.this.TAG, "MESSAGE : RUN_CODEC_INIT");
                LiveShutter.nDisplayFlag = 48;
                LiveShutter.mHandler.post(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag));
            }
        }).start();
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle args) {
        Trace.d(this.TAG, "start onCreateDialog() id : " + id);
        switch (id) {
            case 1002:
                this.nCurShowDlg = 1002;
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_msg_title).setMessage(R.string.rvf_storageerror_msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.30
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.31
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return keyCode == 84 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1003:
                this.nCurShowDlg = 1003;
                getWindow().clearFlags(128);
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.end).setMessage(R.string.rvf_dsc_notsearch).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.34
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.nCurShowDlg = -1;
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.MSGBOX_DSC_NOTSEARCH");
                        LiveShutter.this.showDialog(1011);
                        LiveShutter.this.backap();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.35
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 84 && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return keyCode == 4 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1005:
                this.nCurShowDlg = 1005;
                if (bClosing) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.end).setMessage(R.string.rvf_notservice_msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.36
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.nCurShowDlg = -1;
                        Trace.d(LiveShutter.this.TAG, "Const.MsgId.MSGBOX_NOTSERVICEVERSION");
                        LiveShutter.this.showDialog(1011);
                        LiveShutter.this.holderremove();
                        LiveShutter.this.appclose();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.37
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 84 && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return keyCode == 4 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1006:
                this.nCurShowDlg = 1006;
                if (bClosing || !this.bStart) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_msg_title).setMessage(R.string.rvf_memoryfull_msg).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.38
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!LiveShutter.bClosing) {
                            LiveShutter.this.nCurShowDlg = -1;
                            Trace.d(LiveShutter.this.TAG, "Const.MsgId.MSGBOX_MEMORYFULL");
                        }
                    }
                }).create();
            case 1007:
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.end).setMessage(R.string.network_disconnected).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.32
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.appClose();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.33
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 84 && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return keyCode == 4 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1008:
                this.nCurShowDlg = 1008;
                if (bClosing || !this.bStart) {
                    return null;
                }
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_msg_title).setMessage(R.string.rvf_memoryerror_msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.39
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.nCurShowDlg = -1;
                        if (LiveShutter.this.bexitflag) {
                            Trace.d(LiveShutter.this.TAG, "Const.MsgId.MSGBOX_MEMORYFULLERROR");
                            LiveShutter.this.showDialog(1011);
                            LiveShutter.this.holderremove();
                            LiveShutter.this.appclose();
                        }
                    }
                }).create();
            case 1009:
                this.nCurShowDlg = 1009;
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_msg_title).setMessage(R.string.rvf_memoryerror_msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.40
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.nCurShowDlg = -1;
                        Trace.d(LiveShutter.this.TAG, "onClick() Const.MsgId.MSGBOX_DCF_FULL_ERROR");
                        LiveShutter.this.showDialog(1011);
                        LiveShutter.this.holderremove();
                        LiveShutter.this.onUnsubscribe();
                    }
                }).create();
            case 1010:
                if (bClosing || !this.bStart) {
                    return null;
                }
                CharSequence str = getString(R.string.rvf_shutting);
                if (this.streampro == null) {
                    this.streampro = new ProgressDialog(this);
                }
                this.streampro.setMessage(str);
                this.streampro.setIndeterminate(true);
                this.streampro.setProgressStyle(0);
                this.streampro.setCancelable(false);
                this.streampro.getWindow().clearFlags(2);
                this.streampro.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.41
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        return event.getKeyCode() == 84;
                    }
                });
                return this.streampro;
            case 1011:
                Trace.e(this.TAG, "-=> Const.MsgBoxId.PROGRESS_BAR_DISPLAY_EXIT_1");
                if (this.proap != null) {
                    this.proap.dismiss();
                    this.proap = null;
                }
                hideProgressBar();
                CharSequence str2 = getString(R.string.rvf_appclose);
                if (this.streampro == null) {
                    this.streampro = new ProgressDialog(this);
                }
                this.streampro.setMessage(str2);
                this.streampro.setIndeterminate(true);
                this.streampro.setProgressStyle(0);
                this.streampro.setCancelable(false);
                this.streampro.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.42
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        return event.getKeyCode() == 84;
                    }
                });
                return this.streampro;
            case 1012:
                return getCustomDialog();
            case 1013:
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_alert_info).setMessage(args.getString(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgKey.DIALOG_MESSAGE_KEY)).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.43
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LiveShutter.this.setImageResource(LiveShutter.rvf_flash_button);
                    }
                }).create();
            case 1014:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ssc_info).setTitle(R.string.end).setMessage(R.string.insert_external_memory_card).setCancelable(false).setPositiveButton(R.string.cm_confirm, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.44
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        LiveShutter.this.showDialog(1011);
                        LiveShutter.mHandler.sendEmptyMessageDelayed(11, 1000L);
                        LiveShutter.this.holderremove();
                        LiveShutter.this.appclose();
                    }
                }).create();
            case 1015:
                return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setTitle(R.string.rvf_msg_title).setMessage(R.string.rvf_shot_failed_msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.45
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LiveShutter.this.streampro.dismiss();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.46
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return keyCode == 84 && event.getRepeatCount() == 0;
                    }
                }).create();
            case 1016:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setContentView(R.layout.custom_progress_dialog_rvf);
                TextView bodyText = (TextView) dialog.findViewById(R.id.textView1);
                LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.titleLayout3);
                layout.setVisibility(0);
                TextView title = (TextView) dialog.findViewById(R.id.title3);
                title.setText(R.string.rvf_wait);
                bodyText.setText(R.string.rvf_please_wait_while_streaming);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView1);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
                imageView.startAnimation(animation);
                return dialog;
            case Const.MsgBoxId.MSGBOX_SMARTPHONE_SAVE_RESOLUTION_GUIDE /* 1017 */:
                switch (RVFFunctionManager.getSaveGuideDialogType(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion(), this.connectedSsid)) {
                    case 1:
                        Dialog dialog2 = SmartPhoneSaveGuideDialog();
                        return dialog2;
                    case 2:
                        Dialog dialog3 = SmartPhoneSaveGuideDialogForCSC();
                        return dialog3;
                    case 3:
                        Dialog dialog4 = SmartPhoneSaveGuideDialogForDSC();
                        return dialog4;
                }
            case Const.MsgBoxId.MSGBOX_CAMERA_RECORD_ERROR_CODEC_ERROR /* 1018 */:
                break;
            case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY /* 2007 */:
                CustomDialog dialog5 = new CustomDialog(this);
                dialog5.setIcon(R.drawable.rvf_popup_icon);
                dialog5.setTitle(R.string.rvf_msg_title);
                dialog5.setMessage(getString(R.string.is_setted_continuous_shot_and_raw_quality));
                dialog5.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.29
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog6, int which) {
                        LiveShutter.this.doAction(33, "Normal");
                        LiveShutter.this.dismissDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
                    }
                });
                return dialog5;
            case com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_NO_CARD /* 2008 */:
                CustomDialog dialog6 = new CustomDialog(this);
                dialog6.setIcon(R.drawable.rvf_popup_icon);
                dialog6.setTitle(R.string.rvf_msg_title);
                dialog6.setMessage(getString(R.string.rvf_no_card));
                dialog6.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.28
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog7, int which) {
                        LiveShutter.this.dismissDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_NO_CARD);
                        if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).contains("CONTINUOUS")) {
                            LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
                        }
                    }
                });
                return dialog6;
            default:
                return super.onCreateDialog(id, args);
        }
        return new AlertDialog.Builder(this).setIcon(R.drawable.rvf_popup_icon).setMessage("RECORD ERROR: CODEC ERROR!").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.47
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog7, int whichButton) {
            }
        }).create();
    }

    public void byebye() {
        this.isByeBye = true;
        Trace.d(this.TAG, "MESSAGE : RUN_EXIT_BYEBYE");
        nDisplayFlag = 45;
        mHandler.post(new XxxThread(nDisplayFlag));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int convertDipToPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dip * scale) + 0.5f);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected void onStop() {
        super.onStop();
        Trace.d(this.TAG, "Application Received onSTOP...");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0 || b == null) {
            return b;
        }
        Matrix m = new Matrix();
        m.setRotate(degrees, b.getWidth() / 2.0f, b.getHeight() / 2.0f);
        try {
            Bitmap result = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            return result;
        } catch (OutOfMemoryError e) {
            return b;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LayoutChange(int screentype) {
        Createchanged();
        setImageResource(rvf_flash_button);
        setImageResource(rvf_timer_button);
        if (getCurrentMainOptionMenuId() != 0) {
            showMainOptionMenu(getCurrentMainOptionMenuId());
        }
        if (getCurrentSubOptionMenuId() != 0) {
            showSubOptionMenu(getCurrentSubOptionMenuId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CheckLayout() {
        nDisplayFlag = Const.MsgId.LAYOUT_CHANGE;
        mHandler.post(new XxxThread(nDisplayFlag));
    }

    private void MenuClose(boolean bClear) {
        if (bClear) {
            setCurrentMainOptionMenuId(0);
        }
        Trace.d(this.TAG, "MenuClose  ");
        this.rvf_option.setVisibility(4);
        this.rvf_menuTop1.setVisibility(4);
        this.rvf_menuTop2.setVisibility(4);
        this.rvf_menuTop3.setVisibility(4);
        this.rvf_menuTop4.setVisibility(4);
        this.rvf_shutter_button.setVisibility(0);
        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
            showZoomBar();
        }
        setImageResource(rvf_flash_button);
        setImageResource(rvf_timer_button);
        setImageResource(rvf_photosize_button);
        setImageResource(rvf_camerasave_button);
        setImageResource(rvf_cameramore_button);
    }

    /* JADX WARN: Unexpected iteration count in SwitchBreakVisitor. Please report as an issue */
    private void setImageResourceButtonFocus(ImageButton imageButton) {
        switch (imageButton.getId()) {
            case R.id.rvf_flash_button /* 2131558466 */:
                if (this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_flash_off_f);
                } else {
                    String defaultFlash = this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH);
                    Trace.d(this.TAG, "defaultFlash : " + defaultFlash);
                    if (defaultFlash.equals(DeviceController.FlashMode.OFF)) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_flash_off_f);
                    } else if (defaultFlash.equals(DeviceController.FlashMode.AUTO)) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_flash_auto_f);
                    } else if (defaultFlash.equals("REDEYE")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_redeye_f);
                    } else if (defaultFlash.equals("FILLIN") || defaultFlash.equals("FILL-IN") || defaultFlash.equals("FILL IN")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_fillin_f);
                    } else if (defaultFlash.equals("SLOWSYNC")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_slow_sync_f);
                    } else if (defaultFlash.equals("REDEYEFIX")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_redeye_fix_f);
                    } else if (defaultFlash.equals("FILLINREDEYE") || defaultFlash.equals("FILL-IN_RED") || defaultFlash.equals("FILL-IN RED")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_flash_redeye_f);
                    } else if (defaultFlash.equals("1STCURTAIN") || defaultFlash.equals("1ST_CURTAIN") || defaultFlash.equals("1ST CURTAIN")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_1st_curtain_f);
                    } else if (defaultFlash.equals("2NDCURTAIN") || defaultFlash.equals("2ND_CURTAIN") || defaultFlash.equals("2ND CURTAIN")) {
                        imageButton.setBackgroundResource(R.drawable.rvf_camera_flash_2nd_curtain_f);
                    }
                }
                break;
            case R.id.rvf_timer_button /* 2131558467 */:
                if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_off1_f);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("2SEC")) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_2sec_f);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("5SEC")) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_5sec_f);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("10SEC")) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_10sec_f);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("30SEC")) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_30sec_f);
                } else if (this.mDeviceController.getCurrentLEDTimeMenuItem().toUpperCase(Locale.ENGLISH).equals("DOUBLE")) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_timer_double_f);
                }
                break;
            case R.id.rvf_photosize_button /* 2131558468 */:
                if (this.mDeviceController.getResolutionMenuItems().size() > 0) {
                    DSCResolution dscResolution = this.mDeviceController.getResolutionMenuItems().get(Integer.parseInt(this.mDeviceController.getDefaultResolutionIndex()));
                    if (dscResolution != null) {
                        String title = Utils.unitChange(this, dscResolution.getWidth(), dscResolution.getHeight(), FunctionManager.getPhotoSizePresentationType(this.connectedSsid));
                        if (title.equals("1M")) {
                            imageButton.setBackgroundResource(R.drawable.rvf_camera_size_1m_f);
                            break;
                        } else if (title.equals("1.1M")) {
                            imageButton.setBackgroundResource(R.drawable.rvf_camera_size_1_1m_f);
                            break;
                        } else if (title.equals("2M")) {
                            imageButton.setBackgroundResource(R.drawable.rvf_camera_size_2m_f);
                            break;
                        } else if (title.equals("2.1M")) {
                            imageButton.setBackgroundResource(R.drawable.rvf_camera_size_2_1m_f);
                            break;
                        } else if (title.equals("W2M")) {
                            imageButton.setBackgroundResource(R.drawable.rvf_camera_size_2wm_f);
                            break;
                        } else {
                            if (title.equals("3M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_3m_f);
                            } else if (title.equals("4M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_4m_f);
                            } else if (title.equals("4.8M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_4_9m_f);
                            } else if (title.equals("W4M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_4wm_f);
                            } else if (title.equals("5M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_5m_f);
                            } else if (title.equals("5.9M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_5_9m_f);
                            } else if (title.equals("7M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_7m_f);
                            } else if (title.equals("7.8M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_7_8m_f);
                            } else if (title.equals("W7M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_7wm_f);
                            } else if (title.equals("8M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_8m_f);
                            } else if (title.equals("9M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_9m_f);
                            } else if (title.equals("10M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_10m_f);
                            } else if (title.equals("10.1M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_10_1m_f);
                            } else if (title.equals("W10M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_10wm_f);
                            } else if (title.equals("W12M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_12wm_f);
                            } else if (title.equals("13M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_13m_f);
                            } else if (title.equals("13.3M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_13_3m_f);
                            } else if (title.equals("P14M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_14pm_f);
                            } else if (title.equals("16M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_16m_f);
                            } else if (title.equals("16.9M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_16_9m_f);
                            } else if (title.equals("W16M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_16wm_f);
                            } else if (title.equals("20M")) {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_20m_f);
                            } else {
                                imageButton.setBackgroundResource(R.drawable.rvf_camera_size_10m_f);
                            }
                            break;
                        }
                    }
                } else {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_size_10m_f);
                    break;
                }
                break;
            case R.id.rvf_camerasave_button /* 2131558469 */:
                if (this.nCameraSaveSelect == 0) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_storage_phone_camera_f);
                } else if (this.nCameraSaveSelect == 1) {
                    imageButton.setBackgroundResource(R.drawable.rvf_camera_storage_camera_f);
                }
                break;
            case R.id.rvf_cameramore_button /* 2131558470 */:
                imageButton.setBackgroundResource(R.drawable.rvf_camera_drive_camera_setting_f);
                break;
        }
    }

    public DeviceController getDeviceController() {
        return this.mDeviceController;
    }

    private void ZoomOut() {
        long ctime = System.currentTimeMillis();
        setTimer(1);
        buttondisplay(true);
        if (ctime - this.zoomtime > this.nZoomGap) {
            if (Integer.parseInt(this.mDeviceController.getDefaultZoom()) != 0 && codec_init && !this.bOnZoomProcess) {
                this.btoast = false;
                this.bOnZoomProcess = true;
                Trace.d(this.TAG, "MESSAGE : DISPLAY_ZOOMOUTBUTTON");
                nDisplayFlag = 25;
                mHandler.postDelayed(new XxxThread(nDisplayFlag), this.nZoomGap - 2);
                this.zoomtime = ctime;
                this.mHandleTimer.removeMessages(1);
                this.mZoomState = 2;
                this.rvf_progress.setVisibility(0);
                doAction(4, "");
                return;
            }
            return;
        }
        buttondisplay(true);
    }

    private void ZoomIn() {
        long ctime = System.currentTimeMillis();
        setTimer(1);
        buttondisplay(true);
        if (ctime - this.zoomtime > this.nZoomGap) {
            if (Integer.parseInt(this.mDeviceController.getDefaultZoom()) != Integer.parseInt(this.mDeviceController.getMaxZoom()) && codec_init && !this.bOnZoomProcess) {
                this.btoast = false;
                this.bOnZoomProcess = true;
                Trace.d(this.TAG, "MESSAGE : DISPLAY_ZOOMINBUTTON");
                nDisplayFlag = 26;
                mHandler.postDelayed(new XxxThread(nDisplayFlag), this.nZoomGap - 2);
                this.zoomtime = ctime;
                this.mHandleTimer.removeMessages(1);
                this.mZoomState = 1;
                this.rvf_progress.setVisibility(0);
                doAction(3, "");
                return;
            }
            return;
        }
        buttondisplay(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getActionState() {
        return this.mActionState;
    }

    private void setActionState(int actionState) {
        this.mActionState = actionState;
    }

    private void showProgressBar(String title, String msg) {
        Trace.d(this.TAG, "start showProgressBar()");
        if (this.pro == null) {
            this.pro = ProgressDialog.show(this, title, msg, true, false, this.progDiaCancelListener);
            this.pro.getWindow().clearFlags(2);
            this.pro.setCancelable(false);
            return;
        }
        this.pro.setMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgressBar() {
        Trace.d(this.TAG, "start hideProgressBar()");
        if (this.pro != null) {
            this.pro.dismiss();
            this.pro = null;
        }
    }

    private void showSmartPanelCustomDialogWheelMenu(int id, String title, int visibleItems, int focusIndex, ArrayList<String> menuItems, int textViewWidth) {
        this.mSmartPanelCustomDialogWheelMenuId = id;
        this.mSmartPanelCustomDialogWheelMenuItems = menuItems;
        this.mSmartPanelCustomDialogWheel = new CustomDialogWheelMenu(this, String.valueOf(title) + " : ", visibleItems, focusIndex, (String[]) menuItems.toArray(new String[menuItems.size()]), textViewWidth, this.mSmartPanelCustomDialogWheelMenuWheelScrollListener, this.mSmartPanelCustomDialogWheelMenuWheelClickedListener);
        this.mSmartPanelCustomDialogWheel.setCanceledOnTouchOutside(true);
        this.mSmartPanelCustomDialogWheel.show();
        this.mSmartPanelCustomDialogWheel.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.48
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                if (LiveShutter.this.mSmartPanelCustomDialogWheelMenuId != 32) {
                    LiveShutter.this.setSmartPanelVisibility(0);
                } else {
                    LiveShutter.this.setVisibilityOSD(0);
                    if (!LiveShutter.this.isAvailShot()) {
                        LiveShutter.this.showMemoryFullDialog(1);
                    }
                }
                LiveShutter.this.setButtonEnabled(true);
                LiveShutter.this.mSmartPanelCustomDialogWheelMenuId = 0;
                if (LiveShutter.this.mDeviceController.getAFAreaMenuItems().size() <= 0 || !LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                    if (LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                        LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                    }
                } else {
                    LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                    LiveShutter.rvf_touchAutoFocus.setVisibility(0);
                }
            }
        });
        setSmartPanelVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSmartPanelVisibility(int visibility) {
        Trace.d(this.TAG, "start setSmartPanelVisibility() visibility : " + visibility);
        this.mSmartPanelVisibility = visibility;
        this.rvf_smart_panel.setVisibility(visibility);
        if ((visibility == 4 || visibility == 8) && this.mSmartPanelCustomDialogWheelMenuId == 0) {
            resetVisibilityTouchAFFrame();
        } else {
            rvf_touchAutoFocus.setVisibility(4);
        }
        if (isAvailShot()) {
            hideMemoryFullDialog();
            return;
        }
        if (visibility == 0 || this.mSmartPanelCustomDialogWheelMenuId != 0) {
            hideMemoryFullDialog();
        } else if (visibility == 8 || visibility == 4) {
            showMemoryFullDialog(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSmartPanelVisibility() {
        return this.mSmartPanelVisibility;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectionFailDialog(String message) {
        View view = getLayoutInflater().inflate(R.layout.ssc_connection_fail, (ViewGroup) null);
        Button button = (Button) view.findViewById(R.id.ssc_confirm_btn);
        TextView ssc_connection_fail = (TextView) view.findViewById(R.id.ssc_connection_fail);
        ssc_connection_fail.setText(message);
        TextView ssc_rejected_camera = (TextView) view.findViewById(R.id.ssc_rejected_camera);
        ssc_rejected_camera.setText(((Object) ssc_rejected_camera.getText()) + this.connectedSsid);
        this.mConnectionFail = new AlertDialog.Builder(this).setView(view).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.49
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
        button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.LiveShutter.50
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CMService.getInstance().beforefinish(0);
            }
        });
    }

    public String GpsValue() {
        String str;
        if (this.locationManager == null) {
            this.locationManager = (LocationManager) getSystemService("location");
        }
        this.currentLocation_gps = this.locationManager.getLastKnownLocation("gps");
        this.currentLocation_network = this.locationManager.getLastKnownLocation("network");
        if (this.currentLocation_gps != null || this.currentLocation_network != null) {
            if (this.currentLocation_gps != null) {
                str = setGpsValue(this.currentLocation_gps);
            } else {
                str = setGpsValue(this.currentLocation_network);
            }
        } else {
            str = "UNKNOWN";
        }
        Trace.d(this.TAG, "GPS Value : " + str);
        return str;
    }

    private String setGpsValue(Location location) {
        String latitude;
        String longitude;
        int latitudeValue = (int) (location.getLatitude() * 3600.0d);
        int longitudeValue = (int) (location.getLongitude() * 3600.0d);
        if (latitudeValue >= 0) {
            latitude = "N";
        } else {
            latitude = "S";
            latitudeValue *= -1;
        }
        if (longitudeValue >= 0) {
            longitude = "E";
        } else {
            longitude = "W";
            longitudeValue *= -1;
        }
        String gpsValue = String.valueOf(latitude) + latitudeValue + "X" + longitude + longitudeValue;
        return gpsValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void screenConfigChange(int ratioType) {
        int landImageWidth;
        int landImageHeight;
        int portImageWidth;
        int portImageHeight;
        int exImageHeight;
        int exImageWidth;
        Trace.d(this.TAG, "start screenConfigChange() ratioType : " + ratioType);
        int ratioW = 0;
        int ratioH = 0;
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        if (display.getWidth() > display.getHeight()) {
            this.surfaceViewWidth = display.getWidth();
            this.surfaceViewHeight = display.getHeight();
        } else {
            this.surfaceViewWidth = display.getHeight();
            this.surfaceViewHeight = display.getWidth();
        }
        switch (ratioType) {
            case 1:
                ratioW = 16;
                ratioH = 9;
                break;
            case 2:
                ratioW = 4;
                ratioH = 3;
                break;
            case 3:
                ratioW = 3;
                ratioH = 2;
                break;
            case 4:
                ratioW = 1;
                ratioH = 1;
                break;
        }
        Trace.d(this.TAG, ">>>>> surface ratio : " + (this.surfaceViewWidth / this.surfaceViewHeight));
        Trace.d(this.TAG, ">>>>> image ratio : " + (ratioW / ratioH));
        if (this.surfaceViewWidth / this.surfaceViewHeight < ratioW / ratioH) {
            landImageWidth = this.surfaceViewWidth;
            landImageHeight = (int) (this.surfaceViewWidth / (ratioW / ratioH));
        } else {
            landImageWidth = (int) (this.surfaceViewHeight / (ratioH / ratioW));
            landImageHeight = this.surfaceViewHeight;
        }
        if (this.surfaceViewWidth / this.surfaceViewHeight < ratioH / ratioW) {
            portImageWidth = this.surfaceViewWidth;
            portImageHeight = (int) (this.surfaceViewWidth / (ratioH / ratioW));
        } else {
            portImageWidth = (int) (this.surfaceViewHeight / (ratioW / ratioH));
            portImageHeight = this.surfaceViewHeight;
        }
        if (1.3333334f < ratioW / ratioH) {
            exImageWidth = this.surfaceViewHeight;
            exImageHeight = (int) (this.surfaceViewHeight / (ratioW / ratioH));
        } else {
            exImageHeight = (int) (this.surfaceViewHeight / 1.3333334f);
            exImageWidth = (int) (exImageHeight / (ratioH / ratioW));
        }
        int landMarginX = (this.surfaceViewWidth - landImageWidth) / 2;
        int landMarginY = (this.surfaceViewHeight - landImageHeight) / 2;
        int portMarginX = (this.surfaceViewWidth - portImageWidth) / 2;
        int portMarginY = (this.surfaceViewHeight - portImageHeight) / 2;
        int exMarginX = (this.surfaceViewHeight - exImageWidth) / 2;
        int exMarginY = (((int) (this.surfaceViewHeight / 1.3333334f)) - exImageHeight) / 2;
        Trace.d(this.TAG, ">>>>> surfaceView W : " + this.surfaceViewWidth + " H : " + this.surfaceViewHeight);
        Trace.d(this.TAG, ">>>>> ratio W : " + ratioW + " H : " + ratioH);
        Trace.d(this.TAG, ">>>>> landImage W : " + landImageWidth + " H : " + landImageHeight);
        Trace.d(this.TAG, ">>>>> portImage W : " + portImageWidth + " H : " + portImageHeight);
        Trace.d(this.TAG, ">>>>> exImage W : " + exImageWidth + " H : " + exImageHeight);
        Trace.d(this.TAG, ">>>>> landMargin X : " + landMarginX + " Y : " + landMarginY);
        Trace.d(this.TAG, ">>>>> portMargin X : " + portMarginX + " Y : " + portMarginY);
        Trace.d(this.TAG, ">>>>> exMargin X : " + exMarginX + " Y : " + exMarginY);
        if (Build.MODEL.contains("N920")) {
            this.screenPositoin[0][0] = exMarginX;
            this.screenPositoin[0][1] = exMarginY + 100;
        } else {
            this.screenPositoin[0][0] = exMarginX;
            this.screenPositoin[0][1] = exMarginY;
        }
        this.screenPositoin[1][0] = landMarginY;
        this.screenPositoin[1][1] = landMarginX;
        this.screenPositoin[2][0] = landMarginX;
        this.screenPositoin[2][1] = landMarginY;
        this.screenPositoin[3][0] = portMarginX;
        this.screenPositoin[3][1] = portMarginY;
        this.screenSize[0][0] = exImageWidth;
        this.screenSize[0][1] = exImageHeight;
        this.screenSize[1][0] = landImageHeight;
        this.screenSize[1][1] = landImageWidth;
        this.screenSize[2][0] = landImageWidth;
        this.screenSize[2][1] = landImageHeight;
        this.screenSize[3][0] = portImageWidth;
        this.screenSize[3][1] = portImageHeight;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                Trace.d(this.TAG, ">>>>> screenPositoin[" + i + "][" + j + "] : " + this.screenPositoin[i][j]);
            }
        }
        for (int i2 = 0; i2 < 4; i2++) {
            for (int j2 = 0; j2 < 2; j2++) {
                Trace.d(this.TAG, ">>>>> screenSize[" + i2 + "][" + j2 + "] : " + this.screenSize[i2][j2]);
            }
        }
        int degree = 0;
        if (this.cameratype.equals("ssdp:rotationD")) {
            degree = 0;
        } else if (this.cameratype.equals("ssdp:rotationR")) {
            degree = 90;
        } else if (this.cameratype.equals("ssdp:rotationU")) {
            degree = 180;
        } else if (this.cameratype.equals("ssdp:rotationL")) {
            degree = 270;
        }
        FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, getPhoneCamType() - 1, degree, this.screenPositoin, this.screenSize);
    }

    private class StartRecordTask extends AsyncTask<Void, Void, Boolean> {
        private StartRecordTask() {
        }

        /* synthetic */ StartRecordTask(LiveShutter liveShutter, StartRecordTask startRecordTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            LiveShutter.this.codec_stop();
            if (LiveShutter.this.mDeviceController.getStreamUrlMenuItems().size() > 2) {
                if (LiveShutter.this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                    LiveShutter.this.screenConfigChange(2);
                } else {
                    for (int i = 0; i < LiveShutter.this.mDeviceController.getMovieResolutionMenuItems().size(); i++) {
                        if (LiveShutter.this.mDeviceController.getMovieResolutionMenuItems().get(i).getResolution().equals(LiveShutter.this.mDeviceController.getMovieResolutionValue())) {
                            LiveShutter.this.screenConfigChange(Utils.stringToRatioType(LiveShutter.this.mDeviceController.getMovieResolutionMenuItems().get(i).getRatio()));
                            break;
                        }
                    }
                }
            }
            if (LiveShutter.this.codec_start()) {
                Trace.d(LiveShutter.this.TAG, " codec_start is True : START_RECORD_ACTION_ID");
                LiveShutter.this.doAction(35, "");
                return null;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRecord() {
        this.mShotStateEx = 7;
        this.rvf_smart_panel_icon.setVisibility(8);
        rvf_title.setVisibility(4);
        hideIndicatorBar();
        this.rvf_gallery.setVisibility(8);
        this.rvf_mode_button.setVisibility(8);
        this.rvf_shutter_button.setVisibility(8);
        this.rvf_camcorder_button.setVisibility(8);
        this.rvf_rec_stop_button.setVisibility(0);
        this.rvf_rec_title.setVisibility(0);
        this.rvf_rec_time.setVisibility(0);
        this.rvf_recording_time.setText("00:00");
        this.rvf_remain_time.setText(getTimeFormat(this.mDeviceController.getRemainRecTimeValue()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopRecord() {
        this.mShotStateEx = 0;
        this.rvf_smart_panel_icon.setVisibility(0);
        rvf_title.setVisibility(0);
        showIndicatorBar();
        this.rvf_gallery.setVisibility(0);
        this.rvf_mode_button.setVisibility(0);
        this.rvf_shutter_button.setVisibility(0);
        this.rvf_camcorder_button.setVisibility(0);
        this.rvf_rec_stop_button.setVisibility(8);
        this.rvf_rec_stop_button.setEnabled(true);
        this.rvf_rec_title.setVisibility(8);
        this.rvf_rec_time.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getTimeFormat(String strTime) {
        int intTime = Integer.parseInt(strTime);
        String formatedTime = String.format("%02d", Integer.valueOf(intTime / 60));
        return String.valueOf(String.valueOf(formatedTime) + SOAP.DELIM) + String.format("%02d", Integer.valueOf(intTime % 60));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDCFFull() {
        boolean isDCFFull = !this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("INTERNAL") && this.mDeviceController.getAvailShots().equals("-1");
        Trace.d(this.TAG, "end isDCFFull() isDCFFull : " + isDCFFull);
        return isDCFFull;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAvailShot() {
        boolean isAvailShot = this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("INTERNAL") || !this.mDeviceController.getAvailShots().equals("0");
        Trace.d(this.TAG, "end isAvailShot() isAvailShot : " + isAvailShot);
        return isAvailShot;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAvailMovieRecording() {
        boolean isAvailMovieRecording = !this.mDeviceController.getRemainRecTimeValue().equals("0");
        Trace.d(this.TAG, "end isAvailMovieRecording() isAvailMovieRecording : " + isAvailMovieRecording);
        return isAvailMovieRecording;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMemoryFullDialog(int delay) {
        Trace.d(this.TAG, "start showMemoryFullDialog() delay : " + delay);
        rvf_zoomSeekBar.setEnabled(false);
        switch (delay) {
            case 0:
                showPopupWindow(1);
                setTimer(12);
                break;
            case 1:
                showPopupWindow(1);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideMemoryFullDialog() {
        Trace.d(this.TAG, "start hideMemoryFullDialog");
        if (RVFFunctionManager.isTouchableZoomSeekBar(this.connectedSsid)) {
            rvf_zoomSeekBar.setEnabled(true);
        }
        hidePopupWindow(1);
    }

    private String getInitialUserInput() {
        int width = 0;
        int height = 0;
        switch (getPhoneCamType()) {
            case 1:
                width = this.screenSize[0][0];
                height = this.screenSize[0][1];
                break;
            case 2:
                width = this.screenSize[1][0];
                height = this.screenSize[1][1];
                break;
            case 3:
                width = this.screenSize[2][0];
                height = this.screenSize[2][1];
                break;
            case 4:
                width = this.screenSize[3][0];
                height = this.screenSize[3][1];
                break;
        }
        return new String("ffplay -livestreaming -pktqimmediateflush -pictqinitialemptysec 4 -demuxprobesize 512 -an -sync ext -framedrop -infbuf -pictqsize 15 -displayresolution " + width + "x" + height + " ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean codec_start() {
        String changedUserInput;
        Trace.d(this.TAG, "start codec_start() phonecamtype : " + getPhoneCamType());
        Trace.d(this.TAG, "Connection Timeout is : " + connectionTimeout);
        if (codec_init) {
            return false;
        }
        codec_init = true;
        String initialUserInput = getInitialUserInput();
        if (this.mDeviceController.getCurrentStreamUrl().contains(".avi") || this.mDeviceController.getCurrentStreamUrl().contains(".AVI")) {
            changedUserInput = initialUserInput.concat("-f avi ");
        } else if (this.mDeviceController.getCurrentStreamUrl().contains(".mp4") || this.mDeviceController.getCurrentStreamUrl().contains(".MP4")) {
            changedUserInput = initialUserInput.concat("-f mp4 ");
        } else {
            changedUserInput = initialUserInput.concat("-f avi ");
        }
        FFmpegJNI.request(FFmpegJNI.Command.VIDEO_STREAMING_START, changedUserInput.concat(this.mDeviceController.getCurrentStreamUrl()));
        while (true) {
            try {
                Trace.d(this.TAG, "==> Connection is : " + FFmpegJNI.mConnectResponse + " Timeout : " + connectionTimeout);
                if (FFmpegJNI.mConnectResponse) {
                    return true;
                }
                connectionTimeout += 100;
                if (connectionTimeout > 5000) {
                    onUnsubscribe();
                    appClose();
                    return false;
                }
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void codec_stop() {
        Trace.d(this.TAG, "start codec_stop()");
        if (codec_init) {
            codec_init = false;
            FFmpegJNI.request(FFmpegJNI.Command.VIDEO_STREAMING_QUIT, FFmpegJNI.ARG_VIDEO_STREAMING_QUIT);
        }
        FFmpegJNI.mConnectResponse = false;
        connectionTimeout = 0L;
    }

    private class RestartCodecTask extends AsyncTask<Integer, Void, Boolean> {
        private RestartCodecTask() {
        }

        /* synthetic */ RestartCodecTask(LiveShutter liveShutter, RestartCodecTask restartCodecTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            LiveShutter.this.showDialog(1016);
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Integer... params) {
            LiveShutter.this.codec_stop();
            if (params.length != 0) {
                Trace.d(LiveShutter.this.TAG, "params[0] is not null");
                LiveShutter.this.screenConfigChange(params[0].intValue());
            } else {
                Trace.d(LiveShutter.this.TAG, "params[0] is null");
                if (LiveShutter.this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                    LiveShutter.this.screenConfigChange(2);
                } else {
                    LiveShutter.this.screenConfigChange(Utils.stringToRatioType(LiveShutter.this.mDeviceController.getRatioValue()));
                }
            }
            LiveShutter.this.codec_start();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (FunctionManager.isNotSupportedRawQualityAndContinuousShotAtTheSameTime(LiveShutter.this.connectedSsid) && LiveShutter.this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).contains("RAW") && LiveShutter.this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).contains("CONTINUOUS")) {
                LiveShutter.this.showDialog(com.samsungimaging.connectionmanager.app.pullservice.Const.MsgBoxId.MSGBOX_IS_SETTED_CONTINUOUS_SHOT_AND_RAW_QUALITY);
            }
            LiveShutter.this.dismissDialog(1016);
            LiveShutter.nDisplayFlag = Const.MsgId.SET_ENABLE_SURFACEVIEW;
            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 300L);
            if (LiveShutter.this.mShotStateEx == 7) {
                LiveShutter.this.stopRecord();
            }
            LiveShutter.this.resetVisibilityTouchAFFrame();
            super.onPostExecute(result);
        }
    }

    private class InitCodecTask extends AsyncTask<Void, Void, Boolean> {
        private InitCodecTask() {
        }

        /* synthetic */ InitCodecTask(LiveShutter liveShutter, InitCodecTask initCodecTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Trace.d(LiveShutter.this.TAG, "start InitCodecTask.onPreExecute()");
            if (LiveShutter.this.streampro != null) {
                CharSequence str = LiveShutter.this.getString(R.string.rvf_streaming);
                LiveShutter.this.streampro.setMessage(str);
            }
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            LiveShutter.this.codec_start();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (LiveShutter.this.mDeviceController.getAFAreaMenuItems().size() <= 0 || !LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                if (LiveShutter.this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                    LiveShutter.rvf_touchAutoFocus.setVisibility(4);
                }
            } else {
                LiveShutter.rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                LiveShutter.rvf_touchAutoFocus.setVisibility(0);
            }
            LiveShutter.nDisplayFlag = Const.MsgId.SET_ENABLE_SURFACEVIEW;
            LiveShutter.mHandler.postDelayed(LiveShutter.this.new XxxThread(LiveShutter.nDisplayFlag), 300L);
            if (LiveShutter.this.mShotStateEx == 7) {
                LiveShutter.this.stopRecord();
            }
            if (LiveShutter.this.getPhoneCamType() != 1 && LiveShutter.rvf_QuickSettingMenu.getVisibility() == 0) {
                LiveShutter.this.hideQuickSettingMenu();
            }
            LiveShutter.this.setVisibilityOSD(0);
            LiveShutter.this.resetVisibilityTouchAFFrame();
            LiveShutter.this.shutterbutton(true);
            if (LiveShutter.this.streampro != null && LiveShutter.this.streampro.isShowing()) {
                LiveShutter.this.streampro.dismiss();
                LiveShutter.this.removeDialog(1010);
                LiveShutter.this.removeDialog(1011);
            }
            super.onPostExecute(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIndicator() {
        if (this.mDeviceController.getWBValue().equals("") || this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
            this.rvf_indicator_wb.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_wb);
            this.rvf_indicator_wb.setVisibility(0);
        }
        if (this.mDeviceController.getMeteringValue().equals("") || this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("MULTI")) {
            this.rvf_indicator_metering.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_metering);
            this.rvf_indicator_metering.setVisibility(0);
        }
        if (this.mDeviceController.getDriveValue().equals("") || this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
            this.rvf_indicator_drive.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_drive);
            this.rvf_indicator_drive.setVisibility(0);
        }
        if (this.mDeviceController.getCurrentFlashDisplay().equals("") || this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF) || this.mDeviceController.getFlashStrobeStatus().toUpperCase(Locale.ENGLISH).equals("DETACHED")) {
            this.rvf_indicator_flash.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_flash);
            this.rvf_indicator_flash.setVisibility(0);
        }
        setImageResource(this.rvf_indicator_photo_size);
        this.rvf_indicator_photo_size.setVisibility(0);
        if (this.mDeviceController.getQualityValue().equals("") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("SUPER FINE") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("FINE") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("NORMAL") || this.mDeviceController.getQualityMenuItems().size() == 0) {
            this.rvf_indicator_quality.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_quality);
            this.rvf_indicator_quality.setVisibility(0);
        }
        if (this.mDeviceController.getMovieResolutionValue().equals("")) {
            this.rvf_indicator_movie_size.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_movie_size);
            this.rvf_indicator_movie_size.setVisibility(0);
        }
        setImageResource(this.rvf_indicator_af_area);
        if (this.mDeviceController.getAFAreaMenuItems().size() > 0) {
            this.rvf_indicator_af_area.setVisibility(0);
        } else {
            this.rvf_indicator_af_area.setVisibility(8);
        }
        if (this.mDeviceController.getTouchAFMenuItems().size() == 0 || this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
            this.rvf_indicator_touch_af.setVisibility(8);
        } else {
            setImageResource(this.rvf_indicator_touch_af);
            this.rvf_indicator_touch_af.setVisibility(0);
        }
        setImageResource(this.rvf_indicator_storage);
        this.rvf_indicator_storage.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIndicator(int iconId) {
        switch (iconId) {
            case R.id.rvf_indicator_wb /* 2131558677 */:
                if (this.mDeviceController.getWBValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                    this.rvf_indicator_wb.setVisibility(8);
                } else {
                    setImageResource(this.rvf_indicator_wb);
                    this.rvf_indicator_wb.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_metering /* 2131558678 */:
                if (this.mDeviceController.getMeteringValue().toUpperCase(Locale.ENGLISH).equals("MULTI")) {
                    this.rvf_indicator_metering.setVisibility(8);
                } else {
                    setImageResource(this.rvf_indicator_metering);
                    this.rvf_indicator_metering.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_drive /* 2131558679 */:
                if (this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH).equals("SINGLE")) {
                    this.rvf_indicator_drive.setVisibility(8);
                } else {
                    setImageResource(this.rvf_indicator_drive);
                    this.rvf_indicator_drive.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_flash /* 2131558680 */:
                if (this.mDeviceController.getCurrentFlashDisplay().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF) || this.mDeviceController.getDefaultFlash().toUpperCase(Locale.ENGLISH).equals("") || this.mDeviceController.getFlashStrobeStatus().toUpperCase(Locale.ENGLISH).equals("DETACHED")) {
                    this.rvf_indicator_flash.setVisibility(8);
                } else {
                    setImageResource(this.rvf_indicator_flash);
                    this.rvf_indicator_flash.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_photo_size /* 2131558681 */:
                setImageResource(this.rvf_indicator_photo_size);
                this.rvf_indicator_photo_size.setVisibility(0);
                break;
            case R.id.rvf_indicator_quality /* 2131558682 */:
                if (this.mDeviceController.getQualityValue().equals("") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("SUPER FINE") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("FINE") || this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH).equals("NORMAL") || this.mDeviceController.getQualityMenuItems().size() == 0) {
                    this.rvf_indicator_quality.setVisibility(8);
                } else {
                    setImageResource(this.rvf_indicator_quality);
                    this.rvf_indicator_quality.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_movie_size /* 2131558683 */:
                setImageResource(this.rvf_indicator_movie_size);
                this.rvf_indicator_movie_size.setVisibility(0);
                break;
            case R.id.rvf_indicator_af_area /* 2131558684 */:
                setImageResource(this.rvf_indicator_af_area);
                if (this.mDeviceController.getAFAreaMenuItems().size() > 0) {
                    this.rvf_indicator_af_area.setVisibility(0);
                } else {
                    this.rvf_indicator_af_area.setVisibility(8);
                }
                break;
            case R.id.rvf_indicator_touch_af /* 2131558685 */:
                setImageResource(this.rvf_indicator_touch_af);
                if (this.mDeviceController.getTouchAFMenuItems().size() == 0 || this.mDeviceController.getTouchAFValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.OFF)) {
                    this.rvf_indicator_touch_af.setVisibility(8);
                } else {
                    this.rvf_indicator_touch_af.setVisibility(0);
                }
                break;
            case R.id.rvf_indicator_storage /* 2131558686 */:
                setImageResource(this.rvf_indicator_storage);
                this.rvf_indicator_storage.setVisibility(0);
                break;
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0061  */
    /* JADX WARN: Code duplicated, block: B:21:0x0076  */
    /* JADX WARN: Code duplicated, block: B:38:0x011a  */
    /* JADX WARN: Code duplicated, block: B:9:0x001f  */
    boolean isDimMenu(ImageButton button) {
        Trace.d(this.TAG, "start isDimMenu()");
        boolean isDim = false;
        switch (button.getId()) {
            case R.id.rvf_indicator_photo_size /* 2131558681 */:
            case R.id.rvf_smart_panel_photoSize /* 2131558705 */:
                ArrayList<DSCMenuItem> dimMenuItems = this.mDeviceController.getResolutionMenuItemsDim();
                for (int i = 0; i < dimMenuItems.size(); i++) {
                    DSCMenuItem item = dimMenuItems.get(i);
                    if (item.getName().toUpperCase(Locale.ENGLISH).equals("QUALITY") && item.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH))) {
                        isDim = true;
                        if (isDim) {
                            this.rvf_smart_panel_photoSize.setImageResource(R.drawable.rvf_custom_button_camera_size_20m);
                            return isDim;
                        }
                        setImageResource(this.rvf_smart_panel_photoSize);
                        return isDim;
                    }
                }
                if (isDim) {
                    this.rvf_smart_panel_photoSize.setImageResource(R.drawable.rvf_custom_button_camera_size_20m);
                    return isDim;
                }
                setImageResource(this.rvf_smart_panel_photoSize);
                return isDim;
            case R.id.rvf_indicator_af_area /* 2131558684 */:
            case R.id.rvf_smart_panel_afArea /* 2131558709 */:
                ArrayList<DSCMenuItem> dimMenuItems2 = this.mDeviceController.getAFAreaMenuItemsDim();
                if (this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF")) {
                    return true;
                }
                for (int i2 = 0; i2 < dimMenuItems2.size(); i2++) {
                    DSCMenuItem item2 = dimMenuItems2.get(i2);
                    if (item2.getName().toUpperCase(Locale.ENGLISH).equals("AFMode") && item2.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getAFModeValue().toUpperCase(Locale.ENGLISH))) {
                        return true;
                    }
                }
                return false;
            case R.id.rvf_indicator_touch_af /* 2131558685 */:
            case R.id.rvf_smart_panel_touchAF /* 2131558710 */:
                ArrayList<DSCMenuItem> dimMenuItems3 = this.mDeviceController.getTouchAFMenuItemsDim();
                if (this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF")) {
                    isDim = true;
                } else {
                    for (int i3 = 0; i3 < dimMenuItems3.size(); i3++) {
                        DSCMenuItem item3 = dimMenuItems3.get(i3);
                        if (item3.getName().toUpperCase(Locale.ENGLISH).equals("AFMode") && item3.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getAFModeValue().toUpperCase(Locale.ENGLISH))) {
                            isDim = true;
                        }
                    }
                }
                if (isDim) {
                    this.rvf_smart_panel_touchAF.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_off);
                    return isDim;
                }
                setImageResource(this.rvf_smart_panel_touchAF);
                return isDim;
            case R.id.rvf_indicator_storage /* 2131558686 */:
            case R.id.rvf_smart_panel_save /* 2131558711 */:
                ArrayList<DSCMenuItem> dimMenuItems4 = this.mDeviceController.getFileSaveMenuItemsDim();
                for (int i4 = 0; i4 < dimMenuItems4.size(); i4++) {
                    DSCMenuItem item4 = dimMenuItems4.get(i4);
                    if (item4.getName().toUpperCase(Locale.ENGLISH).equals("DRIVE") && item4.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getDriveValue().toUpperCase(Locale.ENGLISH))) {
                        isDim = true;
                    } else if (item4.getName().toUpperCase(Locale.ENGLISH).equals("QUALITY") && item4.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getQualityValue().toUpperCase(Locale.ENGLISH))) {
                        isDim = true;
                    } else if (item4.getName().toUpperCase(Locale.ENGLISH).equals("CARDSTATUS") && item4.getValue().toUpperCase(Locale.ENGLISH).equals(this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH))) {
                        isDim = true;
                    }
                    if (isDim) {
                        this.rvf_smart_panel_save.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                        return isDim;
                    }
                    setImageResource(this.rvf_smart_panel_save);
                    return isDim;
                }
                if (isDim) {
                    this.rvf_smart_panel_save.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                    return isDim;
                }
                setImageResource(this.rvf_smart_panel_save);
                return isDim;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetVisibilityTouchAFFrame() {
        Trace.d(this.TAG, "start resetVisibilityTouchAFFrame() isTouchAFMovie : " + this.mDeviceController.isTouchAFMovie());
        if (this.mDeviceController.isTouchAFMovie()) {
            rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
            rvf_touchAutoFocus.setVisibility(0);
            setTouchAFPosition();
        } else {
            if (getSmartPanelVisibility() == 0 || this.mSmartPanelCustomDialogWheelMenuId != 0 || this.mDeviceController.getDefaultFocusState().toUpperCase(Locale.ENGLISH).equals("MF") || this.mDeviceController.getDialModeValue().toUpperCase(Locale.ENGLISH).equals(DeviceController.FlashMode.AUTO)) {
                rvf_touchAutoFocus.setVisibility(4);
                return;
            }
            if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("SELECTION AF")) {
                rvf_touchAutoFocus.setImageResource(R.drawable.rvf_camera_af_face_w);
                rvf_touchAutoFocus.setVisibility(0);
                setTouchAFPosition();
            } else if (this.mDeviceController.getAFAreaValue().toUpperCase(Locale.ENGLISH).equals("MULTI AF")) {
                rvf_touchAutoFocus.setVisibility(4);
            }
        }
    }

    private class UnsubscribeTask extends AsyncTask<Device, Void, Void> {
        private UnsubscribeTask() {
        }

        /* synthetic */ UnsubscribeTask(LiveShutter liveShutter, UnsubscribeTask unsubscribeTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Device... params) {
            Trace.d(LiveShutter.this.TAG, "start UnsubscribeTask.doInBackground()");
            if (LiveShutter.this.upnpController != null) {
                LiveShutter.this.upnpController.unsubscribe(params[0]);
                return null;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            Trace.d(LiveShutter.this.TAG, "start UnsubscribeTask.onPostExecute()");
            LiveShutter.this.appClose();
            if (LiveShutter.this.bCloseByFinishSafe) {
                CMService.getInstance().finishSafe();
            }
            super.onPostExecute(result);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Trace.d(this.TAG, "start onActivityResult() requestCode : " + requestCode + " resultCode : " + resultCode);
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    if (data.getStringExtra("request_code").equals("goto_ml")) {
                        if (this.mDeviceController.getCardStatusValue().toUpperCase(Locale.ENGLISH).equals("EXTERNAL")) {
                            this.mHandleTimer.removeMessages(7);
                            this.mHandleTimer.removeMessages(5);
                            codec_stop();
                            FFmpegJNI.destruct();
                            Intent intent = new Intent(this, (Class<?>) FileSharing.class);
                            intent.putExtra("requestServiceChange", "changeToML");
                            startActivity(intent);
                            finish();
                        }
                    } else if (!data.getStringExtra("request_code").equals("goto_rvf")) {
                        this.bCloseByFinishSafe = true;
                        new UnsubscribeTask(this, null).execute(this.upnpController.getConnectedDevice());
                    }
                }
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVisibilityOSD(int visibility) {
        Trace.d(this.TAG, "start setVisibilityOSD() visibility : " + visibility);
        if (visibility == 0) {
            rvf_title.setVisibility(visibility);
            this.rvf_gallery.setVisibility(visibility);
            this.rvf_shutter_button.setVisibility(visibility);
            showZoomBar();
            showIndicatorBar();
            if (RVFFunctionManager.isSupportedSmartPanel(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                this.rvf_smart_panel_icon.setVisibility(visibility);
                this.rvf_mode_button.setVisibility(visibility);
                if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
                    this.rvf_camcorder_button.setVisibility(visibility);
                    return;
                }
                return;
            }
            this.rvf_menu_button.setVisibility(visibility);
            return;
        }
        rvf_title.setVisibility(4);
        this.rvf_gallery.setVisibility(visibility);
        this.rvf_shutter_button.setVisibility(visibility);
        hideZoomBar();
        hideIndicatorBar();
        if (RVFFunctionManager.isSupportedSmartPanel(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
            this.rvf_smart_panel_icon.setVisibility(visibility);
            this.rvf_mode_button.setVisibility(visibility);
            if (this.mDeviceController.getMovieResolutionMenuItems().size() > 0) {
                this.rvf_camcorder_button.setVisibility(visibility);
                return;
            }
            return;
        }
        this.rvf_menu_button.setVisibility(visibility);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showZoomBar() {
        Trace.d(this.TAG, "showZoomBar()");
        if (!RVFFunctionManager.isUnsupportedZoomableLens(this.connectedSsid) && Integer.parseInt(this.mDeviceController.getMaxZoom()) > 0) {
            rvf_zoomMain.setVisibility(0);
            zoombardisplay();
        } else {
            rvf_zoomMain.setVisibility(8);
        }
        setTimer(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideZoomBar() {
        Trace.d(this.TAG, "hideZoomBar()");
        rvf_zoomMain.setVisibility(8);
    }

    private void showIndicatorBar() {
        switch (this.mPhoneCamType) {
            case 1:
                if (RVFFunctionManager.isSupportedSmartPanel(this.mDeviceController.getVersionPrefix(), this.mDeviceController.getVersion())) {
                    this.rvf_indicator_bar.setVisibility(0);
                } else {
                    this.rvf_indicator_bar.setVisibility(8);
                }
                break;
            case 2:
            case 3:
            case 4:
                this.rvf_indicator_bar.setVisibility(0);
                break;
        }
    }

    private void hideIndicatorBar() {
        this.rvf_indicator_bar.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopupWindow(int id) {
        Trace.d(this.TAG, "start showPopupWindow() id : " + id);
        if (this.rvf_surface.getWidth() > 0) {
            this.mCurPopupWindowId = id;
            View popupView = getLayoutInflater().inflate(R.layout.rvf_toast_message_layout, (ViewGroup) null);
            TextView rvf_toast_title = (TextView) popupView.findViewById(R.id.rvf_toast_title);
            TextView rvf_toast_message = (TextView) popupView.findViewById(R.id.rvf_toast_message);
            switch (id) {
                case 1:
                    rvf_toast_message.setVisibility(0);
                    rvf_toast_message.setText(R.string.rvf_memoryfull_msg);
                    break;
                case 2:
                    rvf_toast_title.setVisibility(0);
                    rvf_toast_title.setText("Recording...");
                    rvf_toast_message.setVisibility(0);
                    rvf_toast_message.setText(R.string.rvf_screen_will_be_off_while_recording_a_video);
                    break;
            }
            if (this.popupWindow == null) {
                this.popupWindow = new PopupWindow(popupView, -2, -2);
            }
            this.popupWindow.showAtLocation(this.rvf_surface, 17, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hidePopupWindow(int id) {
        Trace.d(this.TAG, "start hidePopupWindow()");
        if (this.mCurPopupWindowId == id && this.popupWindow != null) {
            this.popupWindow.dismiss();
            this.popupWindow = null;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void closeService() {
        Trace.d(this.TAG, "start closeService()");
        this.mHandleTimer.removeMessages(7);
        if (this.timetask != null) {
            this.timetask.cancel();
        }
        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(getConnectedSSID());
        if (!isOldCamera) {
            CMService.getInstance().beforefinish(0);
            codec_stop();
            FFmpegJNI.destruct();
        } else {
            codec_stop();
            FFmpegJNI.destruct();
            CMService.getInstance().beforefinish(0);
        }
        finish();
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void onAlive() {
        Trace.d(this.TAG, "start onAlive()");
        if (!this.bConnect) {
            showDialog(1016);
        }
        doAction(1, GpsValue());
    }

    private void setTouchAFPosition() {
        int screenPositionX;
        int screenPositionY;
        int screenSizeWidth;
        int screenSizeHeight;
        int marginLeft;
        int marginTop;
        Trace.d(this.TAG, "start setTouchAFPosition()");
        if (this.mMarginLeft < 0 || this.mOrientationWhenCheckMargin != orientation) {
            if (this.mFrameWidth == 0) {
                this.mFrameWidth = rvf_touchAutoFocus.getWidth();
                this.mFrameHeight = rvf_touchAutoFocus.getHeight();
            }
            if (this.mDeviceController.getRatioOffsetMenuItems().size() > 0) {
                screenPositionX = ((this.mDeviceController.getRatioOffsetValue().getWidth() * this.screenSize[getPhoneCamType() - 1][0]) / 640) + this.screenPositoin[getPhoneCamType() - 1][0];
                screenPositionY = (this.mDeviceController.getRatioOffsetValue().getHeight() * this.screenSize[getPhoneCamType() - 1][1]) / 480;
                screenSizeWidth = this.screenSize[getPhoneCamType() - 1][0] - (((this.mDeviceController.getRatioOffsetValue().getWidth() * 2) * this.screenSize[getPhoneCamType() - 1][0]) / 640);
                screenSizeHeight = this.screenSize[getPhoneCamType() - 1][1] - (screenPositionY * 2);
            } else {
                screenPositionX = this.screenPositoin[getPhoneCamType() - 1][0];
                screenPositionY = this.screenPositoin[getPhoneCamType() - 1][1];
                screenSizeWidth = this.screenSize[getPhoneCamType() - 1][0];
                screenSizeHeight = this.screenSize[getPhoneCamType() - 1][1];
            }
            marginLeft = (((this.positionX * screenSizeWidth) / 100) + screenPositionX) - (this.mFrameWidth / 2);
            marginTop = (((this.positionY * screenSizeHeight) / 100) + screenPositionY) - (this.mFrameHeight / 2);
            if (marginLeft < screenPositionX) {
                marginLeft = screenPositionX;
            } else if (this.mFrameWidth + marginLeft > screenPositionX + screenSizeWidth) {
                marginLeft = (screenPositionX + screenSizeWidth) - this.mFrameWidth;
            }
            if (marginTop < screenPositionY) {
                marginTop = screenPositionY;
            } else if (this.mFrameHeight + marginTop > screenPositionY + screenSizeHeight) {
                marginTop = (screenPositionY + screenSizeHeight) - this.mFrameHeight;
            }
            Trace.d(this.TAG, "<<<<< screenSize W : " + screenSizeWidth + " H : " + screenSizeHeight + " frame W : " + this.mFrameWidth + " H : " + this.mFrameHeight);
            Trace.d(this.TAG, "screenPositionX : " + screenPositionX + " screenPositionY : " + screenPositionY);
            Trace.d(this.TAG, "marginLeft : " + marginLeft + " marginTop : " + marginTop + " positionX : " + this.positionX + " Y : " + this.positionY);
        } else {
            marginLeft = this.mMarginLeft;
            marginTop = this.mMarginTop;
        }
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(rvf_touchAutoFocus.getLayoutParams());
        margin.setMargins(marginLeft, marginTop, 0, 0);
        rvf_touchAutoFocus.setLayoutParams(new RelativeLayout.LayoutParams(margin));
    }
}

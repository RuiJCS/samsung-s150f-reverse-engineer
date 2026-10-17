package com.samsungimaging.connectionmanager.app.pullservice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.samsungimaging.asphodel.multimedia.FFmpegJNI;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.localgallery.LocalGallery;
import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.app.pullservice.controller.UPNPController;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCResolution;
import com.samsungimaging.connectionmanager.app.pullservice.dialog.CustomDialogWheelMenu;
import com.samsungimaging.connectionmanager.app.pullservice.util.Graph;
import com.samsungimaging.connectionmanager.app.pullservice.util.ImageDownloader;
import com.samsungimaging.connectionmanager.app.pullservice.util.Utils;
import com.samsungimaging.connectionmanager.util.ExToast;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.kankan.wheel.widget.OnWheelClickedListener;
import org.kankan.wheel.widget.OnWheelScrollListener;
import org.kankan.wheel.widget.WheelView;
import org.kankan.wheel.widget.adapters.ArrayWheelAdapter;

/* JADX INFO: loaded from: classes.dex */
public final class RemoteViewFinder extends PullService implements OnWheelScrollListener, OnWheelClickedListener, View.OnClickListener, View.OnTouchListener, SurfaceHolder.Callback {
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
    private String cameraType;
    private UPNPController controller;
    private int curScreenType;
    private Handler deviceControlHandler;
    private DeviceController deviceController;
    private Display display;
    private ExToast exToast;
    private SurfaceHolder holder;
    private ImageDownloader imageDownloader;
    private LocationManager locationManager;
    private Handler msgHandler;
    private int nShutterDnUp;
    private ProgressDialog progressDialog;
    private ImageView rvf_af_extend_leftdown;
    private ImageView rvf_af_extend_leftup;
    private ImageView rvf_af_extend_rightdown;
    private ImageView rvf_af_extend_rightup;
    private ImageButton rvf_camcorder_button;
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
    private int[][] screenPosition = new int[4][];
    private int[][] screenSize = new int[4][];
    private int smartPanelVisibility;
    private String ssid;
    private CustomDialogWheelMenu wheelMenuDialog;
    private int wheelMenuID;
    private ArrayList<String> wheelMenuItems;

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        Trace.d(Trace.Tag.RVF, "start onCreate()");
        super.onCreate(savedInstanceState);
        init();
        initCodec();
        this.deviceController.setHandler(this.deviceControlHandler);
        if (this.controller.isConnected()) {
            doAction(1, Utils.getLocation(this.locationManager));
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.d(Trace.Tag.RVF, "start onDestroy()");
        Intent intent = new Intent(com.samsungimaging.connectionmanager.util.Utils.ACTION_DISCONNECT);
        sendBroadcast(intent);
        super.onDestroy();
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Trace.d(Trace.Tag.RVF, "start onConfigurationChanged()");
        setContentView();
        super.onConfigurationChanged(newConfig);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:28:0x0082. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:31:0x008a. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0015. Please report as an issue. */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:419)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:31)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:399)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:31)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:21)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.view.View.OnTouchListener
    public boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
        /*
            r7 = this;
            r6 = 1
            com.samsungimaging.connectionmanager.util.Trace$Tag r4 = com.samsungimaging.connectionmanager.util.Trace.Tag.RVF
            java.lang.String r5 = "start onTouch()"
            com.samsungimaging.connectionmanager.util.Trace.d(r4, r5)
            int r4 = r8.getId()
            switch(r4) {
                case 2131558403: goto L11;
                case 2131558478: goto L86;
                case 2131558479: goto L7e;
                case 2131558482: goto L19;
                default: goto Lf;
            }
        Lf:
            r4 = 0
            return r4
        L11:
            int r4 = r9.getAction()
            switch(r4) {
                case 0: goto Lf;
                default: goto L18;
            }
        L18:
            goto Lf
        L19:
            int r4 = r9.getAction()
            switch(r4) {
                case 0: goto L21;
                case 1: goto L51;
                default: goto L20;
            }
        L20:
            goto Lf
        L21:
            r7.nShutterDnUp = r6
            com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController r4 = r7.deviceController
            java.lang.String r4 = r4.getDefaultFocusState()
            java.lang.String r5 = "af"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto Lf
            java.lang.String r4 = r7.ssid
            int r4 = com.samsungimaging.connectionmanager.app.pullservice.FunctionManager.getAFMode(r4)
            if (r4 != 0) goto L41
            r4 = 9
            java.lang.String r5 = ""
            r7.doAction(r4, r5)
            goto Lf
        L41:
            java.lang.String r4 = r7.ssid
            int r4 = com.samsungimaging.connectionmanager.app.pullservice.FunctionManager.getAFMode(r4)
            if (r4 != r6) goto Lf
            r4 = 10
            java.lang.String r5 = ""
            r7.doAction(r4, r5)
            goto Lf
        L51:
            float r4 = r9.getX()
            int r2 = (int) r4
            float r4 = r9.getY()
            int r3 = (int) r4
            android.widget.ImageButton r4 = r7.rvf_shutter_button
            int r1 = r4.getWidth()
            android.widget.ImageButton r4 = r7.rvf_shutter_button
            int r0 = r4.getHeight()
            if (r2 < 0) goto L6f
            if (r3 < 0) goto L6f
            if (r2 > r1) goto L6f
            if (r3 <= r0) goto L7a
        L6f:
            r4 = 3
            r7.nShutterDnUp = r4
            r4 = 13
            java.lang.String r5 = ""
            r7.doAction(r4, r5)
            goto Lf
        L7a:
            r4 = 2
            r7.nShutterDnUp = r4
            goto Lf
        L7e:
            int r4 = r9.getAction()
            switch(r4) {
                case 0: goto Lf;
                default: goto L85;
            }
        L85:
            goto Lf
        L86:
            int r4 = r9.getAction()
            switch(r4) {
                case 0: goto Lf;
                default: goto L8d;
            }
        L8d:
            goto Lf
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsungimaging.connectionmanager.app.pullservice.RemoteViewFinder.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Trace.d(Trace.Tag.RVF, "start onClick()");
        switch (v.getId()) {
            case R.id.rvf_surface /* 2131558403 */:
                if (this.smartPanelVisibility == 0) {
                    setSmartPanelVisibility(8);
                }
                break;
            case R.id.rvf_smart_panel_icon /* 2131558462 */:
                setSmartPanelVisibility(0);
                break;
            case R.id.rvf_mode_button /* 2131558481 */:
                showWheelMenu(32, "Mode", 5, this.deviceController.getDialModeMenuItems().indexOf(this.deviceController.getDialModeValue()), this.deviceController.getDialModeMenuItems(), this.display.getWidth() / 5);
                break;
            case R.id.rvf_thumbnail /* 2131558492 */:
                Intent intent = new Intent(this, (Class<?>) LocalGallery.class);
                intent.putExtra(LocalGallery.EXTRA_DESCRIPTION_RES_ID, R.string.connected_rvf);
                intent.putExtra(LocalGallery.EXTRA_BACK_BUTTON_STRING_RES_ID, R.string.remote_viewfinder);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle args) {
        return super.onCreateDialog(id, args);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService, android.app.Activity
    public void onBackPressed() {
        Trace.d(Trace.Tag.RVF, "start onBackPressed()");
        if (this.rvf_smart_panel.getVisibility() == 0) {
            setSmartPanelVisibility(8);
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.addSubMenu("goto ML");
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("goto ML")) {
            stopCodec();
            doAction(43, "changeToML");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // org.kankan.wheel.widget.OnWheelClickedListener
    public void onItemClicked(WheelView wheel, int itemIndex) {
    }

    @Override // org.kankan.wheel.widget.OnWheelScrollListener
    public void onScrollingStarted(WheelView wheel) {
    }

    @Override // org.kankan.wheel.widget.OnWheelScrollListener
    public void onScrollingFinished(WheelView wheel) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_CHANGED, holder);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_CREATED, holder);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        FFmpegJNI.request(FFmpegJNI.Command.SURFACE_DESTROYED, holder);
    }

    private void initCodec() {
        int surfaceViewWidth;
        int surfaceViewHeight;
        if (this.display.getWidth() > this.display.getHeight()) {
            surfaceViewWidth = this.display.getWidth();
            surfaceViewHeight = this.display.getHeight();
        } else {
            surfaceViewWidth = this.display.getHeight();
            surfaceViewHeight = this.display.getWidth();
        }
        Bitmap portraitBackground = Bitmap.createBitmap(surfaceViewHeight, surfaceViewWidth, Bitmap.Config.RGB_565);
        Bitmap landscapeBackground = Bitmap.createBitmap(surfaceViewWidth, surfaceViewHeight, Bitmap.Config.RGB_565);
        FFmpegJNI.construct(portraitBackground, landscapeBackground, this.screenPosition, this.screenSize, this.curScreenType, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCodec() {
        String streamURL = this.deviceController.getCurrentStreamUrl();
        String changedUserInput = (streamURL.contains(".mp4") || streamURL.contains(".MP4")) ? "ffplay -livestreaming -pktqperiodicflush -pictqinitialemptysec 4 -demuxprobesize 512 -an -sync ext -framedrop -infbuf ".concat("-f mp4 ") : "ffplay -livestreaming -pktqperiodicflush -pictqinitialemptysec 4 -demuxprobesize 512 -an -sync ext -framedrop -infbuf ".concat("-f avi ");
        FFmpegJNI.request(FFmpegJNI.Command.VIDEO_STREAMING_START, changedUserInput.concat(this.deviceController.getCurrentStreamUrl()));
    }

    private class StartCodecTask extends AsyncTask<Void, Void, Boolean> {
        private StartCodecTask() {
        }

        /* synthetic */ StartCodecTask(RemoteViewFinder remoteViewFinder, StartCodecTask startCodecTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Trace.d(Trace.Tag.RVF, "start StartCodecTask.onPreExecute()");
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            RemoteViewFinder.this.startCodec();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopCodec() {
        FFmpegJNI.request(FFmpegJNI.Command.VIDEO_STREAMING_QUIT, FFmpegJNI.ARG_VIDEO_STREAMING_QUIT);
    }

    private class StopCodecTask extends AsyncTask<Void, Void, Boolean> {
        private StopCodecTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            RemoteViewFinder.this.stopCodec();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAFFrame(int frameType, boolean isAFSuccess) {
        if (frameType == 2) {
            showAFExtend(isAFSuccess);
            return;
        }
        String multiAFMatrixSize = this.deviceController.getMultiAFMatrixSizeValue();
        if (FunctionManager.getAFFrameMetricsType(multiAFMatrixSize, this.ssid) == 0) {
            showAF1X1(isAFSuccess);
            return;
        }
        if (FunctionManager.getAFFrameMetricsType(multiAFMatrixSize, this.ssid) == 2) {
            showAF3X5(isAFSuccess);
        } else if (FunctionManager.getAFFrameMetricsType(multiAFMatrixSize, this.ssid) == 3) {
            showAF3X7(isAFSuccess);
        } else {
            showAF3X3(isAFSuccess);
        }
    }

    private void showAF1X1(boolean isAFSuccess) {
        if (isAFSuccess) {
            rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_g);
        } else {
            rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
        }
        rvf_af3x3_11.setVisibility(0);
    }

    private void showAF3X3(boolean isAFSuccess) {
        if (isAFSuccess) {
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
                return;
            }
            return;
        }
        rvf_af3x3_11.setImageResource(R.drawable.rvf_camera_af_face_r);
        rvf_af3x3_11.setVisibility(0);
    }

    private void showAF3X5(boolean isAFSuccess) {
        if (isAFSuccess) {
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
                return;
            }
            return;
        }
        rvf_af3x5_12.setImageResource(R.drawable.rvf_camera_af_face_r);
        rvf_af3x5_12.setVisibility(0);
    }

    private void showAF3X7(boolean isAFSuccess) {
        if (isAFSuccess) {
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
                    } else if (!this.AFData[i].equals("2x5")) {
                        if (this.AFData[i].equals("2x6")) {
                            rvf_af3x7_26.setImageResource(R.drawable.rvf_camera_af_face_g);
                            rvf_af3x7_26.setVisibility(0);
                        }
                    } else {
                        rvf_af3x7_25.setImageResource(R.drawable.rvf_camera_af_face_g);
                        rvf_af3x7_25.setVisibility(0);
                    }
                }
                return;
            }
            return;
        }
        rvf_af3x7_13.setImageResource(R.drawable.rvf_camera_af_face_r);
        rvf_af3x7_13.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideAFFrame() {
        rvf_af_extend.setVisibility(8);
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

    private void init() {
        this.deviceControlHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.RemoteViewFinder.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                boolean result = ((Boolean) msg.obj).booleanValue();
                if (!result) {
                }
                switch (msg.what) {
                    case 3:
                        RemoteViewFinder.this.setContentView();
                        new StartCodecTask(RemoteViewFinder.this, null).execute(new Void[0]);
                        break;
                    case 21:
                        Action action = RemoteViewFinder.this.deviceController.getAction(10);
                        ArgumentList argList = action.getOutputArgumentList();
                        int nArgs = argList.size();
                        for (int i = 0; i < nArgs; i++) {
                            Argument arg = argList.getArgument(i);
                            String name = arg.getName();
                            String value = arg.getValue();
                            if (!name.equals("AF_MF") && name.equals("AFSTATUS")) {
                                if (value.equals("AFEXTEND_OK")) {
                                    RemoteViewFinder.this.showAFFrame(2, true);
                                } else if (value.equals("AFEXTEND_FAIL")) {
                                    RemoteViewFinder.this.showAFFrame(2, false);
                                } else if (!value.equals("3")) {
                                    if (!value.equals("AFFAIL") && !value.equals("0")) {
                                        RemoteViewFinder.this.AFData = value.split(",");
                                        RemoteViewFinder.this.showAFFrame(1, true);
                                    } else {
                                        RemoteViewFinder.this.showAFFrame(1, false);
                                    }
                                }
                            }
                        }
                        if (RemoteViewFinder.this.nShutterDnUp != 2) {
                            if (RemoteViewFinder.this.nShutterDnUp == 3) {
                                RemoteViewFinder.this.setTimer(18);
                                RemoteViewFinder.this.doAction(13, "");
                            }
                        } else {
                            RemoteViewFinder.this.setTimer(18);
                            RemoteViewFinder.this.doAction(14, String.valueOf(1));
                        }
                        break;
                    case 29:
                        new ImageDownloadTask(RemoteViewFinder.this, null).execute(RemoteViewFinder.this.deviceController.getAFShotResult());
                        break;
                }
            }
        };
        this.msgHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pullservice.RemoteViewFinder.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 18:
                        RemoteViewFinder.this.hideAFFrame();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        this.smartPanelVisibility = 8;
        this.ssid = CMInfo.getInstance().getConnectedSSID();
        this.cameraType = "ssdp:rotationD";
        this.nShutterDnUp = 0;
        for (int i = 0; i < 4; i++) {
            this.screenPosition[i] = new int[2];
            this.screenSize[i] = new int[2];
        }
        this.exToast = ExToast.getInstance();
        this.display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        this.controller = UPNPController.getInstance();
        this.deviceController = this.controller.getDeviceController();
        this.imageDownloader = new ImageDownloader(this.msgHandler, 19);
        this.locationManager = (LocationManager) getSystemService("location");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentView() {
        Trace.d(Trace.Tag.RVF, "start setContentView()");
        switch (this.curScreenType) {
            case 0:
            case 2:
                setContentView(R.layout.activity_remoteviewfinder);
                break;
            case 1:
            case 3:
                setContentView(R.layout.activity_remoteviewfinder_full);
                break;
        }
        this.rvf_surface = (SurfaceView) findViewById(R.id.rvf_surface);
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
        rvf_zoomSeekBar = (SeekBar) findViewById(R.id.rvf_zoomSeekBar);
        this.rvf_mode_button = (Button) findViewById(R.id.rvf_mode_button);
        this.rvf_mode_button.setOnClickListener(this);
        this.rvf_shutter_button = (ImageButton) findViewById(R.id.rvf_shutter_button);
        this.rvf_shutter_button.setOnTouchListener(this);
        this.rvf_camcorder_button = (ImageButton) findViewById(R.id.rvf_camcorder_button);
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
        initLayout();
    }

    private void checkScreenType() {
        Configuration config = getResources().getConfiguration();
        switch (config.orientation) {
            case 1:
                if (this.cameraType.equals("ssdp:rotationD") || this.cameraType.equals("ssdp:rotationU")) {
                    this.curScreenType = 0;
                } else if (this.cameraType.equals("ssdp:rotationL") || this.cameraType.equals("ssdp:rotationR")) {
                    this.curScreenType = 1;
                }
                break;
            case 2:
                if (this.cameraType.equals("ssdp:rotationD") || this.cameraType.equals("ssdp:rotationU")) {
                    this.curScreenType = 2;
                } else if (this.cameraType.equals("ssdp:rotationL") || this.cameraType.equals("ssdp:rotationR")) {
                    this.curScreenType = 3;
                }
                break;
        }
    }

    private void initLayout() {
        if (this.holder != null) {
            this.holder.removeCallback(this);
            this.holder = null;
        }
        this.holder = this.rvf_surface.getHolder();
        this.holder.addCallback(this);
        checkScreenType();
        if (this.deviceController.getRatioOffsetMenuItems().size() > 0) {
            screenConfigChange(2);
        } else {
            screenConfigChange(Utils.toRatioType(this.deviceController.getRatioValue()));
        }
        setIndicator();
        if (FunctionManager.isSupportedSmartPanel(this.deviceController.getVersionPrefix(), this.deviceController.getVersion())) {
            if (this.smartPanelVisibility == 0) {
                setSmartPanelVisibility(0);
            } else {
                setSmartPanelVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAction(int id, String param) {
        this.deviceController.doAction(id, param);
    }

    private void screenConfigChange(int ratioType) {
        int surfaceViewWidth;
        int surfaceViewHeight;
        int landImageWidth;
        int landImageHeight;
        int portImageWidth;
        int portImageHeight;
        int exImageHeight;
        int exImageWidth;
        int ratioW = 0;
        int ratioH = 0;
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        if (display.getWidth() > display.getHeight()) {
            surfaceViewWidth = display.getWidth();
            surfaceViewHeight = display.getHeight();
        } else {
            surfaceViewWidth = display.getHeight();
            surfaceViewHeight = display.getWidth();
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
        if (surfaceViewWidth / surfaceViewHeight < ratioW / ratioH) {
            landImageWidth = surfaceViewWidth;
            landImageHeight = (int) (surfaceViewWidth / (ratioW / ratioH));
        } else {
            landImageWidth = (int) (surfaceViewHeight / (ratioH / ratioW));
            landImageHeight = surfaceViewHeight;
        }
        if (surfaceViewWidth / surfaceViewHeight < ratioH / ratioW) {
            portImageWidth = surfaceViewWidth;
            portImageHeight = (int) (surfaceViewWidth / (ratioH / ratioW));
        } else {
            portImageWidth = (int) (surfaceViewHeight / (ratioW / ratioH));
            portImageHeight = surfaceViewHeight;
        }
        if (1.3333334f < ratioW / ratioH) {
            exImageWidth = surfaceViewHeight;
            exImageHeight = (int) (surfaceViewHeight / (ratioW / ratioH));
        } else {
            exImageHeight = (int) (surfaceViewHeight / 1.3333334f);
            exImageWidth = (int) (exImageHeight / (ratioH / ratioW));
        }
        int landMarginX = (surfaceViewWidth - landImageWidth) / 2;
        int landMarginY = (surfaceViewHeight - landImageHeight) / 2;
        int portMarginX = (surfaceViewWidth - portImageWidth) / 2;
        int portMarginY = (surfaceViewHeight - portImageHeight) / 2;
        int exMarginX = (surfaceViewHeight - exImageWidth) / 2;
        int exMarginY = (((int) (surfaceViewHeight / 1.3333334f)) - exImageHeight) / 2;
        this.screenPosition[0][0] = exMarginX;
        this.screenPosition[0][1] = exMarginY;
        this.screenPosition[1][0] = landMarginY;
        this.screenPosition[1][1] = landMarginX;
        this.screenPosition[2][0] = landMarginX;
        this.screenPosition[2][1] = landMarginY;
        this.screenPosition[3][0] = portMarginX;
        this.screenPosition[3][1] = portMarginY;
        this.screenSize[0][0] = exImageWidth;
        this.screenSize[0][1] = exImageHeight;
        this.screenSize[1][0] = landImageHeight;
        this.screenSize[1][1] = landImageWidth;
        this.screenSize[2][0] = landImageWidth;
        this.screenSize[2][1] = landImageHeight;
        this.screenSize[3][0] = portImageWidth;
        this.screenSize[3][1] = portImageHeight;
        int degree = 0;
        if (this.cameraType.equals("ssdp:rotationD")) {
            degree = 0;
        } else if (this.cameraType.equals("ssdp:rotationR")) {
            degree = 90;
        } else if (this.cameraType.equals("ssdp:rotationU")) {
            degree = 180;
        } else if (this.cameraType.equals("ssdp:rotationL")) {
            degree = 270;
        }
        FFmpegJNI.request(FFmpegJNI.Command.SCREEN_CONFIG_CHANGE, this.curScreenType, degree, this.screenPosition, this.screenSize);
    }

    /* JADX WARN: Unexpected iteration count in SwitchBreakVisitor. Please report as an issue */
    private void setImageResource(ImageButton button) {
        DSCResolution dscResolution;
        switch (button.getId()) {
            case R.id.rvf_flash_button /* 2131558466 */:
                String defaultFlash = this.deviceController.getDefaultFlash().toUpperCase();
                if (defaultFlash.equals(DeviceController.FlashMode.OFF) || defaultFlash.equals("")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                } else {
                    String currentFlash = this.deviceController.getCurrentFlashDisplay().toUpperCase();
                    if (currentFlash.equals(DeviceController.FlashMode.OFF)) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    } else if (currentFlash.equals(DeviceController.FlashMode.AUTO)) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (currentFlash.equals("REDEYE")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_redeye);
                    } else if (currentFlash.equals("FILLIN") || currentFlash.equals("FILL-IN") || currentFlash.equals("FILL IN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (currentFlash.equals("SLOWSYNC")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_slow_sync);
                    } else if (currentFlash.equals("REDEYEFIX")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_redeye_fix);
                    } else if (currentFlash.equals("FILLINREDEYE") || currentFlash.equals("FILL-IN_RED") || currentFlash.equals("FILL-IN RED")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (currentFlash.equals("1STCURTAIN") || currentFlash.equals("1ST_CURTAIN") || currentFlash.equals("1ST CURTAIN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (currentFlash.equals("2NDCURTAIN") || currentFlash.equals("2ND_CURTAIN") || currentFlash.equals("2ND CURTAIN")) {
                        button.setBackgroundResource(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    }
                }
                break;
            case R.id.rvf_timer_button /* 2131558467 */:
                String currentTime = this.deviceController.getCurrentLEDTimeMenuItem().toUpperCase();
                if (currentTime.equals(DeviceController.FlashMode.OFF)) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_off1);
                } else if (currentTime.equals("2SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_2sec);
                } else if (currentTime.equals("5SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_5sec);
                } else if (currentTime.equals("10SEC")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_10sec);
                } else if (currentTime.equals("DOUBLE")) {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_timer_double);
                }
                break;
            case R.id.rvf_photosize_button /* 2131558468 */:
                if (this.deviceController.getResolutionMenuItems().size() > 0) {
                    DSCResolution dscResolution2 = this.deviceController.getResolutionMenuItems().get(Integer.parseInt(this.deviceController.getDefaultResolutionIndex()));
                    if (dscResolution2 != null) {
                        String title = Utils.unitChange(dscResolution2.getWidth(), dscResolution2.getHeight());
                        if (title.equals("1M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_1m);
                        } else if (title.equals("1.1 M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_1_1m);
                        } else if (title.equals("2.1 M") || title.equals("2M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_2m);
                        } else if (title.equals("W2M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_2wm);
                        } else if (title.equals("3M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_3m);
                        } else if (title.equals("4M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_4m);
                        } else if (title.equals("W4M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_4wm);
                        } else if (title.equals("5M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_5m);
                        } else if (title.equals("5.9M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_5_9m);
                        } else if (title.equals("7M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_7m);
                        } else if (title.equals("W7M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_7wm);
                        } else if (title.equals("8M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_8m);
                        } else if (title.equals("9M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_9m);
                        } else if (title.equals("10M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                        } else if (title.equals("W10M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10wm);
                        } else if (title.equals("W12M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_12wm);
                        } else if (title.equals("13M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_13m);
                        } else if (title.equals("P14M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_14pm);
                        } else if (title.equals("16M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_16m);
                        } else if (title.equals("W16M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_16wm);
                        } else if (title.equals("20M")) {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_20m);
                        } else {
                            button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                        }
                    }
                } else {
                    button.setBackgroundResource(R.drawable.rvf_custom_button_camera_size_10m);
                }
                break;
            case R.id.rvf_cameramore_button /* 2131558470 */:
                button.setBackgroundResource(R.drawable.rvf_custom_button_camera_drive_camera_setting);
                break;
            case R.id.rvf_indicator_wb /* 2131558677 */:
                String wb = this.deviceController.getWBValue().toUpperCase();
                if (!wb.equals(DeviceController.FlashMode.AUTO)) {
                    if (wb.equals("DAYLIGHT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_daylight);
                    } else if (wb.equals("CLOUDY")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_cloudy);
                    } else if (wb.equals("FLUORESCENT_W")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_w);
                    } else if (wb.equals("FLUORESCENT_N") || wb.equals("FLUORESCENT_NW")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_nw);
                    } else if (wb.equals("FLUORESCENT_D")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_fluorescent_d);
                    } else if (wb.equals("TUNGSTEN")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_tungsten);
                    } else if (wb.equals("FLASHWB")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_flash_wb);
                    } else if (wb.equals("COLORTEMP")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_wb_colortemp);
                    }
                }
                break;
            case R.id.rvf_indicator_metering /* 2131558678 */:
                String metering = this.deviceController.getMeteringValue().toUpperCase();
                if (!metering.equals("MULTI")) {
                    if (metering.equals("CENTERWEIGHTED") || metering.equals("CENTER-WEIGHTED")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_metering_center_weighted);
                    } else if (metering.equals("SPOT")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_metering_spot);
                    }
                }
                break;
            case R.id.rvf_indicator_drive /* 2131558679 */:
                String drive = this.deviceController.getDriveValue().toUpperCase();
                if (!drive.equals("SINGLE")) {
                    if (drive.equals("CONTINUOUS")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_drive_continuous);
                    } else if (drive.equals("TIMER (2SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_2sec);
                    } else if (drive.equals("TIMER (5SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_5sec);
                    } else if (drive.equals("TIMER (10SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_10sec);
                    } else if (drive.equals("TIMER (30SEC)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_30sec);
                    } else if (drive.equals("TIMER (DOUBLE)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_timer_double);
                    }
                }
                break;
            case R.id.rvf_indicator_flash /* 2131558680 */:
                String defaultFlash2 = this.deviceController.getDefaultFlash().toUpperCase();
                if (!defaultFlash2.equals(DeviceController.FlashMode.OFF) && !defaultFlash2.equals("")) {
                    String currentFlash2 = this.deviceController.getCurrentFlashDisplay().toUpperCase();
                    if (!currentFlash2.equals(DeviceController.FlashMode.OFF)) {
                        if (currentFlash2.equals(DeviceController.FlashMode.AUTO)) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_auto);
                        } else if (currentFlash2.equals("REDEYE")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye);
                        } else if (currentFlash2.equals("FILLIN") || currentFlash2.equals("FILL-IN") || currentFlash2.equals("FILL IN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_fillin);
                        } else if (currentFlash2.equals("SLOWSYNC")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_slow_sync);
                        } else if (currentFlash2.equals("REDEYEFIX")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye_fix);
                        } else if (currentFlash2.equals("FILLINREDEYE") || currentFlash2.equals("FILL-IN_RED") || currentFlash2.equals("FILL-IN RED")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_redeye);
                        } else if (currentFlash2.equals("1STCURTAIN") || currentFlash2.equals("1ST_CURTAIN") || currentFlash2.equals("1ST CURTAIN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_1st_curtain);
                        } else if (currentFlash2.equals("2NDCURTAIN") || currentFlash2.equals("2ND_CURTAIN") || currentFlash2.equals("2ND CURTAIN")) {
                            button.setBackgroundResource(R.drawable.rvf_indicator_flash_2nd_curtain);
                        }
                    }
                    break;
                }
                break;
            case R.id.rvf_indicator_photo_size /* 2131558681 */:
                if (this.deviceController.getResolutionMenuItems().size() > 0 && (dscResolution = this.deviceController.getResolutionMenuItems().get(Integer.parseInt(this.deviceController.getDefaultResolutionIndex()))) != null) {
                    String title2 = Utils.unitChange(dscResolution.getWidth(), dscResolution.getHeight());
                    if (title2.equals("1M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_1m);
                    } else if (title2.equals("1.1 M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_1_1m);
                    } else if (title2.equals("2.1 M") || title2.equals("2M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_2m);
                    } else if (title2.equals("W2M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_2wm);
                    } else if (title2.equals("3M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_3m);
                    } else if (title2.equals("4M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_4m);
                    } else if (title2.equals("W4M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_4wm);
                    } else if (title2.equals("5M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_5m);
                    } else if (title2.equals("5.9M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_5_9m);
                    } else if (title2.equals("7M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_7m);
                    } else if (title2.equals("W7M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_7wm);
                    } else if (title2.equals("8M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_8m);
                    } else if (title2.equals("9M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_9m);
                    } else if (title2.equals("10M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_10m);
                    } else if (title2.equals("W10M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_10wm);
                    } else if (title2.equals("W12M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_12wm);
                    } else if (title2.equals("13M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_13m);
                    } else if (title2.equals("P14M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_14pm);
                    } else if (title2.equals("16M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_16m);
                    } else if (title2.equals("W16M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_16wm);
                    } else if (title2.equals("20M")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_size_20m);
                    }
                    break;
                }
                break;
            case R.id.rvf_indicator_quality /* 2131558682 */:
                String quality = this.deviceController.getQualityValue().toUpperCase();
                if (!quality.equals("SUPER FINE") && !quality.equals("FINE") && !quality.equals("NORMAL")) {
                    if (quality.equals("RAW")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_quality_raw);
                    } else if (quality.equals("RAW+JPEG")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_quality_raw_jpeg);
                    }
                    break;
                }
                break;
            case R.id.rvf_indicator_movie_size /* 2131558683 */:
                String movieResolution = this.deviceController.getMovieResolutionValue();
                if (this.deviceController.getVideoOutValue().toUpperCase().equals("NTSC")) {
                    if (movieResolution.equals("1920x1080 (15p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_15p);
                    } else if (movieResolution.equals("1920x1080 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_30p);
                    } else if (movieResolution.equals("1920x1080 (60p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_60p);
                    } else if (movieResolution.equals("1920x810 (24p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_810_24p);
                    } else if (movieResolution.equals("1280x720 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_30p);
                    } else if (movieResolution.equals("1280x720 (60p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_60p);
                    } else if (movieResolution.equals("640x480 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_480_30p);
                    } else if (movieResolution.equals("320x240 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_240_web);
                    }
                } else if (this.deviceController.getVideoOutValue().toUpperCase().equals("PAL")) {
                    if (movieResolution.equals("1920x1080 (15p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_1080_25p);
                    } else if (movieResolution.equals("1280x720 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_720_25p);
                    } else if (movieResolution.equals("640x480 (30p)")) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_movie_size_480_25p);
                    }
                }
                break;
            case R.id.rvf_indicator_af_area /* 2131558684 */:
                String afArea = this.deviceController.getAFAreaValue().toUpperCase();
                if (afArea.equals("SELECTION AF")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_afarea_selection);
                } else if (afArea.equals("MULTI AF")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_afarea_multi);
                }
                break;
            case R.id.rvf_indicator_touch_af /* 2131558685 */:
                String touchAF = this.deviceController.getTouchAFValue().toUpperCase();
                if (touchAF.equals(DeviceController.FlashMode.OFF)) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_off);
                } else if (touchAF.equals("TOUCH AF")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_touchaf);
                } else if (touchAF.equals("AF POINT")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_afpoint);
                } else if (touchAF.equals("ONE TOUCH SHOT")) {
                    button.setBackgroundResource(R.drawable.rvf_indicator_touchaf_onetouchshot);
                }
                break;
            case R.id.rvf_indicator_storage /* 2131558686 */:
                if (this.deviceController.getFileSaveMenuItems().size() > 0) {
                    String storage = this.deviceController.getFileSaveValue();
                    if (storage.equals(this.deviceController.getFileSaveMenuItems().get(0))) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_storage_phone_camera);
                    } else if (storage.equals(this.deviceController.getFileSaveMenuItems().get(1))) {
                        button.setBackgroundResource(R.drawable.rvf_indicator_storage_camera);
                    }
                }
                break;
            case R.id.rvf_smart_panel_wb /* 2131558700 */:
                String wb2 = this.deviceController.getWBValue().toUpperCase();
                if (wb2.equals(DeviceController.FlashMode.AUTO)) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_acamera_wb);
                } else if (wb2.equals("DAYLIGHT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_daylight);
                } else if (wb2.equals("CLOUDY")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_cloudy);
                } else if (wb2.equals("FLUORESCENT_W")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_w);
                } else if (wb2.equals("FLUORESCENT_N") || wb2.equals("FLUORESCENT_NW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_n);
                } else if (wb2.equals("FLUORESCENT_D")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_fluorescent_d);
                } else if (wb2.equals("TUNGSTEN")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_tungsten);
                } else if (wb2.equals("FLASHWB")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_flash_camera_wb);
                } else if (wb2.equals("COLORTEMP")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_wb_colortemp);
                }
                break;
            case R.id.rvf_smart_panel_metering /* 2131558701 */:
                String metering2 = this.deviceController.getMeteringValue().toUpperCase();
                if (metering2.equals("MULTI")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_multi);
                } else if (metering2.equals("CENTERWEIGHTED") || metering2.equals("CENTER-WEIGHTED")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_center_weighted);
                } else if (metering2.equals("SPOT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_metering_spot);
                }
                break;
            case R.id.rvf_smart_panel_flash /* 2131558703 */:
                String defaultFlash3 = this.deviceController.getDefaultFlash().toUpperCase();
                if (defaultFlash3.equals(DeviceController.FlashMode.OFF) || defaultFlash3.equals("")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                } else {
                    String currentFlash3 = this.deviceController.getCurrentFlashDisplay().toUpperCase();
                    if (currentFlash3.equals(DeviceController.FlashMode.OFF)) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_off);
                    } else if (currentFlash3.equals(DeviceController.FlashMode.AUTO)) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_auto);
                    } else if (currentFlash3.equals("REDEYE")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye);
                    } else if (currentFlash3.equals("FILLIN") || currentFlash3.equals("FILL-IN") || currentFlash3.equals("FILL IN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_fillin);
                    } else if (currentFlash3.equals("SLOWSYNC")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_slow_sync);
                    } else if (currentFlash3.equals("REDEYEFIX")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_redeye_fix);
                    } else if (currentFlash3.equals("FILLINREDEYE") || currentFlash3.equals("FILL-IN_RED") || currentFlash3.equals("FILL-IN RED")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_flash_redeye);
                    } else if (currentFlash3.equals("1STCURTAIN") || currentFlash3.equals("1ST_CURTAIN") || currentFlash3.equals("1ST CURTAIN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_1st_curtain);
                    } else if (currentFlash3.equals("2NDCURTAIN") || currentFlash3.equals("2ND_CURTAIN") || currentFlash3.equals("2ND CURTAIN")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_flash_2nd_curtain);
                    }
                }
                break;
            case R.id.rvf_smart_panel_photoSize /* 2131558705 */:
                if (this.deviceController.getResolutionMenuItems().size() > 0) {
                    DSCResolution dscResolution3 = this.deviceController.getResolutionMenuItems().get(Integer.parseInt(this.deviceController.getDefaultResolutionIndex()));
                    if (dscResolution3 != null) {
                        String title3 = Utils.unitChange(dscResolution3.getWidth(), dscResolution3.getHeight());
                        if (title3.equals("1M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_1m);
                        } else if (title3.equals("1.1 M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_1_1m);
                        } else if (title3.equals("2.1 M") || title3.equals("2M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_2m);
                        } else if (title3.equals("W2M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_2wm);
                        } else if (title3.equals("3M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_3m);
                        } else if (title3.equals("4M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_4m);
                        } else if (title3.equals("W4M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_4wm);
                        } else if (title3.equals("5M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_5m);
                        } else if (title3.equals("5.9M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_5_9m);
                        } else if (title3.equals("7M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_7m);
                        } else if (title3.equals("W7M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_7wm);
                        } else if (title3.equals("8M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_8m);
                        } else if (title3.equals("9M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_9m);
                        } else if (title3.equals("10M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                        } else if (title3.equals("W10M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_10wm);
                        } else if (title3.equals("W12M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_12wm);
                        } else if (title3.equals("13M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_13m);
                        } else if (title3.equals("P14M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_14pm);
                        } else if (title3.equals("16M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_16m);
                        } else if (title3.equals("W16M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_16wm);
                        } else if (title3.equals("20M")) {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_20m);
                        } else {
                            button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                        }
                    }
                } else {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_size_10m);
                }
                break;
            case R.id.rvf_smart_panel_quality /* 2131558706 */:
                String quality2 = this.deviceController.getQualityValue().toUpperCase();
                if (quality2.equals("SUPER FINE")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_superfine);
                } else if (quality2.equals("FINE")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_fine);
                } else if (quality2.equals("NORMAL")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_normal);
                } else if (quality2.equals("RAW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_raw);
                } else if (quality2.equals("RAW+JPEG")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_quality_raw_jpeg);
                }
                break;
            case R.id.rvf_smart_panel_movieSize /* 2131558707 */:
                String movieResolution2 = this.deviceController.getMovieResolutionValue().toUpperCase();
                if (this.deviceController.getVideoOutValue().toUpperCase().equals("NTSC")) {
                    if (movieResolution2.equals("1920x1080 (15p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_15p);
                    } else if (movieResolution2.equals("1920x1080 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_30p);
                    } else if (movieResolution2.equals("1920x1080 (60p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_60p);
                    } else if (movieResolution2.equals("1920x810 (24p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_810_24p);
                    } else if (movieResolution2.equals("1280x720 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_30p);
                    } else if (movieResolution2.equals("1280x720 (60p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_60p);
                    } else if (movieResolution2.equals("640x480 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_480_30p);
                    } else if (movieResolution2.equals("320x240 (30p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_240_web);
                    }
                } else if (this.deviceController.getVideoOutValue().toUpperCase().equals("PAL")) {
                    if (movieResolution2.equals("1920x1080 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_1080_25p);
                    } else if (movieResolution2.equals("1280x720 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_720_25p);
                    } else if (movieResolution2.equals("640x480 (25p)")) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_480_25p);
                    }
                }
                break;
            case R.id.rvf_smart_panel_streaming_quality /* 2131558708 */:
                String streamQuality = this.deviceController.getCurrentStreamQuality().toUpperCase();
                if (streamQuality.equals("HIGH")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_640);
                } else if (streamQuality.equals("LOW")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_movie_size_320);
                }
                break;
            case R.id.rvf_smart_panel_afArea /* 2131558709 */:
                String defaultFocusState = this.deviceController.getDefaultFocusState().toUpperCase();
                String afArea2 = this.deviceController.getAFAreaValue().toUpperCase();
                if (defaultFocusState.equals("MF")) {
                    if (afArea2.equals("SELECTION AF")) {
                        button.setImageResource(R.drawable.rvf_camera_afarea_selection_d);
                    } else if (afArea2.equals("MULTI AF")) {
                        button.setImageResource(R.drawable.rvf_camera_afarea_multi_d);
                    }
                } else if (afArea2.equals("SELECTION AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_afarea_selection);
                } else if (afArea2.equals("MULTI AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_afarea_multi);
                }
                break;
            case R.id.rvf_smart_panel_touchAF /* 2131558710 */:
                String defaultFocusState2 = this.deviceController.getDefaultFocusState().toUpperCase();
                String touchAF2 = this.deviceController.getTouchAFValue().toUpperCase();
                if (defaultFocusState2.equals("MF")) {
                    button.setImageResource(R.drawable.rvf_camera_touchaf_off_d);
                } else if (touchAF2.equals(DeviceController.FlashMode.OFF)) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_off);
                } else if (touchAF2.equals("TOUCH AF")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_touchaf);
                } else if (touchAF2.equals("AF POINT")) {
                    button.setImageResource(R.drawable.rvf_custom_button_camera_touchaf_afpoint);
                }
                break;
            case R.id.rvf_smart_panel_save /* 2131558711 */:
                if (this.deviceController.getFileSaveMenuItems().size() > 0) {
                    String save = this.deviceController.getFileSaveValue();
                    if (save.equals(this.deviceController.getFileSaveMenuItems().get(0))) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_storage_phone_camera);
                    } else if (save.equals(this.deviceController.getFileSaveMenuItems().get(1))) {
                        button.setImageResource(R.drawable.rvf_custom_button_camera_storage_camera);
                    }
                }
                break;
        }
    }

    private void setIndicator() {
        setIndicator(R.id.rvf_indicator_wb);
        setIndicator(R.id.rvf_indicator_metering);
        setIndicator(R.id.rvf_indicator_drive);
        setIndicator(R.id.rvf_indicator_flash);
        setIndicator(R.id.rvf_indicator_photo_size);
        setIndicator(R.id.rvf_indicator_quality);
        setIndicator(R.id.rvf_indicator_movie_size);
        setIndicator(R.id.rvf_indicator_af_area);
        setIndicator(R.id.rvf_indicator_touch_af);
        setIndicator(R.id.rvf_indicator_storage);
    }

    private void setIndicator(int iconID) {
        switch (iconID) {
            case R.id.rvf_indicator_wb /* 2131558677 */:
                String wb = this.deviceController.getWBValue().toUpperCase();
                if (wb.equals("") || wb.equals(DeviceController.FlashMode.AUTO)) {
                    this.rvf_indicator_wb.setVisibility(8);
                } else {
                    this.rvf_indicator_wb.setVisibility(0);
                    setImageResource(this.rvf_indicator_wb);
                }
                break;
            case R.id.rvf_indicator_metering /* 2131558678 */:
                String metering = this.deviceController.getMeteringValue().toUpperCase();
                if (metering.equals("MULTI")) {
                    this.rvf_indicator_metering.setVisibility(8);
                } else {
                    this.rvf_indicator_metering.setVisibility(0);
                    setImageResource(this.rvf_indicator_metering);
                }
                break;
            case R.id.rvf_indicator_drive /* 2131558679 */:
                String drive = this.deviceController.getDriveValue().toUpperCase();
                if (drive.equals("SINGLE")) {
                    this.rvf_indicator_drive.setVisibility(8);
                } else {
                    this.rvf_indicator_drive.setVisibility(0);
                    setImageResource(this.rvf_indicator_drive);
                }
                break;
            case R.id.rvf_indicator_flash /* 2131558680 */:
                String flash = this.deviceController.getCurrentFlashDisplay().toUpperCase();
                if (flash.equals(DeviceController.FlashMode.OFF)) {
                    this.rvf_indicator_flash.setVisibility(8);
                } else {
                    this.rvf_indicator_flash.setVisibility(0);
                    setImageResource(this.rvf_indicator_flash);
                }
                break;
            case R.id.rvf_indicator_photo_size /* 2131558681 */:
                this.rvf_indicator_photo_size.setVisibility(0);
                setImageResource(this.rvf_indicator_photo_size);
                break;
            case R.id.rvf_indicator_quality /* 2131558682 */:
                String quality = this.deviceController.getQualityValue().toUpperCase();
                if (quality.equals("SUPER FINE") || quality.equals("FINE") || quality.equals("NORMAL")) {
                    this.rvf_indicator_quality.setVisibility(8);
                } else {
                    this.rvf_indicator_quality.setVisibility(0);
                    setImageResource(this.rvf_indicator_quality);
                }
                break;
            case R.id.rvf_indicator_movie_size /* 2131558683 */:
                this.rvf_indicator_movie_size.setVisibility(0);
                setImageResource(this.rvf_indicator_movie_size);
                break;
            case R.id.rvf_indicator_af_area /* 2131558684 */:
                if (this.deviceController.getAFAreaMenuItems().size() == 0) {
                    this.rvf_indicator_af_area.setVisibility(8);
                } else {
                    this.rvf_indicator_af_area.setVisibility(0);
                    setImageResource(this.rvf_indicator_af_area);
                }
                break;
            case R.id.rvf_indicator_touch_af /* 2131558685 */:
                String touchAF = this.deviceController.getDriveValue().toUpperCase();
                if (touchAF.equals(DeviceController.FlashMode.OFF) || this.deviceController.getTouchAFMenuItems().size() == 0) {
                    this.rvf_indicator_touch_af.setVisibility(8);
                } else {
                    this.rvf_indicator_touch_af.setVisibility(0);
                    setImageResource(this.rvf_indicator_touch_af);
                }
                break;
            case R.id.rvf_indicator_storage /* 2131558686 */:
                this.rvf_indicator_storage.setVisibility(0);
                setImageResource(this.rvf_indicator_storage);
                break;
        }
    }

    private void setSmartPanel() {
        String[] modeItems = (String[]) this.deviceController.getDialModeMenuItems().toArray(new String[this.deviceController.getDialModeMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_mode, 5, this.deviceController.getDialModeMenuItems().indexOf(this.deviceController.getDialModeValue()), modeItems, 84);
        this.rvf_smart_panel_shutterSpeed.measure(0, 0);
        String[] shutterSpeedItems = (String[]) this.deviceController.getShutterSpeedMenuItems().toArray(new String[this.deviceController.getShutterSpeedMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_shutterSpeed, 3, this.deviceController.getShutterSpeedMenuItems().indexOf(this.deviceController.getShutterSpeedValue()), shutterSpeedItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 3);
        String[] apertureItems = (String[]) this.deviceController.getApertureMenuItems().toArray(new String[this.deviceController.getApertureMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_aperture, 5, this.deviceController.getApertureMenuItems().indexOf(this.deviceController.getApertureValue()), apertureItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 5);
        String[] evItems = (String[]) this.deviceController.getEVMenuItems().toArray(new String[this.deviceController.getEVMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_ev, 5, this.deviceController.getEVMenuItems().indexOf(this.deviceController.getEVValue()), evItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 5);
        String[] isoItems = (String[]) this.deviceController.getISOMenuItems().toArray(new String[this.deviceController.getISOMenuItems().size()]);
        setWheelView(this.rvf_smart_panel_iso, 3, this.deviceController.getISOMenuItems().indexOf(this.deviceController.getISOValue()), isoItems, this.rvf_smart_panel_shutterSpeed.getMeasuredWidth() / 3);
    }

    private void setSmartPanelVisibility(int visibility) {
        this.rvf_smart_panel.setVisibility(visibility);
        if (visibility == 0) {
            setOSDVisibility(8);
        } else {
            setOSDVisibility(0);
        }
        this.smartPanelVisibility = visibility;
    }

    private void setWheelView(WheelView view, int visibleItems, int focusIndex, String[] items, int textViewWidth) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(this, items);
        adapter.setTextViewWidth(textViewWidth);
        view.setVisibleItems(visibleItems);
        view.setViewAdapter(adapter);
        view.setCurrentItem(focusIndex);
        String dialMode = this.deviceController.getDialModeValue();
        switch (view.getId()) {
            case R.id.rvf_smart_panel_mode /* 2131558695 */:
                view.setCurrentItem(this.deviceController.getDialModeMenuItems().indexOf(dialMode));
                view.setEnabled(true);
                ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                break;
            case R.id.rvf_smart_panel_shutterSpeed /* 2131558696 */:
                view.setCurrentItem(this.deviceController.getShutterSpeedMenuItems().indexOf(this.deviceController.getShutterSpeedValue()));
                if (dialMode.equals("Auto") || dialMode.equals("P") || dialMode.equals("A")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("S") || dialMode.equals("M")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_aperture /* 2131558697 */:
                view.setCurrentItem(this.deviceController.getApertureMenuItems().indexOf(this.deviceController.getApertureValue()));
                if (dialMode.equals("Auto") || dialMode.equals("P") || dialMode.equals("S")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("A") || dialMode.equals("M")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_ev /* 2131558698 */:
                view.setCurrentItem(this.deviceController.getEVMenuItems().indexOf(this.deviceController.getEVValue()));
                if (dialMode.equals("Auto") || dialMode.equals("M")) {
                    view.setEnabled(false);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-7829368);
                } else if (dialMode.equals("P") || dialMode.equals("A") || dialMode.equals("S")) {
                    view.setEnabled(true);
                    ((ArrayWheelAdapter) view.getViewAdapter()).setTextColor(-1);
                }
                break;
            case R.id.rvf_smart_panel_iso /* 2131558699 */:
                view.setCurrentItem(this.deviceController.getISOMenuItems().indexOf(this.deviceController.getISOValue()));
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

    private void showWheelMenu(int id, String title, int visibleItems, int focusIdx, ArrayList<String> menuItems, int textViewWidth) {
        this.wheelMenuID = id;
        this.wheelMenuItems = menuItems;
        this.wheelMenuDialog = new CustomDialogWheelMenu(this, String.valueOf(title) + " : ", visibleItems, focusIdx, (String[]) menuItems.toArray(new String[menuItems.size()]), textViewWidth, this, this);
        this.wheelMenuDialog.setCanceledOnTouchOutside(true);
        this.wheelMenuDialog.show();
        this.wheelMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.RemoteViewFinder.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                RemoteViewFinder.this.wheelMenuID = 0;
            }
        });
    }

    private void setOSDVisibility(int visibility) {
        rvf_title.setVisibility(visibility);
        if (FunctionManager.isSupportedSmartPanel(this.deviceController.getVersionPrefix(), this.deviceController.getVersion())) {
            this.rvf_smart_panel_icon.setVisibility(visibility);
            this.rvf_mode_button.setVisibility(visibility);
            this.rvf_camcorder_button.setVisibility(visibility);
        } else {
            this.rvf_menu_button.setVisibility(visibility);
        }
        this.rvf_indicator_bar.setVisibility(visibility);
        this.rvf_gallery.setVisibility(visibility);
        this.rvf_shutter_button.setVisibility(visibility);
        rvf_zoomMain.setVisibility(visibility);
    }

    private void showAFExtend(boolean isAFSuccess) {
        if (isAFSuccess) {
            this.rvf_af_extend_leftup.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_leftup);
            this.rvf_af_extend_leftdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_leftdown);
            this.rvf_af_extend_rightup.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_rightup);
            this.rvf_af_extend_rightdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_g_rightdown);
        } else {
            this.rvf_af_extend_leftup.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_leftup);
            this.rvf_af_extend_leftdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_leftdown);
            this.rvf_af_extend_rightup.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_rightup);
            this.rvf_af_extend_rightdown.setBackgroundResource(R.drawable.rvf_camera_af_extend_r_rightdown);
        }
        rvf_af_extend.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimer(int timerID) {
        long miliseconds = 0;
        Message msg = new Message();
        msg.what = timerID;
        this.msgHandler.removeMessages(timerID);
        switch (timerID) {
            case 18:
                miliseconds = 1000;
                break;
        }
        this.msgHandler.sendMessageDelayed(msg, miliseconds);
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageDownloadTask() {
        }

        /* synthetic */ ImageDownloadTask(RemoteViewFinder remoteViewFinder, ImageDownloadTask imageDownloadTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... params) throws Throwable {
            if (FunctionManager.isSupportedDownloadBYOriginalImageURL(RemoteViewFinder.this.deviceController.getVersion())) {
                String imageURL = params[0];
                String savePicName = RemoteViewFinder.this.imageDownloader.syncDownloadImage(imageURL);
                Bitmap thumb = Graph.getThumbnail(savePicName);
                String mimeType = com.samsungimaging.connectionmanager.util.Utils.getMimeType(savePicName);
                MediaScannerConnection.scanFile(RemoteViewFinder.this.getApplicationContext(), new String[]{savePicName}, new String[]{mimeType}, null);
                return thumb;
            }
            String str = String.valueOf(params[0]) + "_SM";
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap thumb) {
            RemoteViewFinder.this.rvf_thumbnail.setImageBitmap(thumb);
            new StartCodecTask(RemoteViewFinder.this, null).execute(new Void[0]);
            super.onPostExecute(thumb);
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0000. Please report as an issue. */
    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void closeService() {
        stopCodec();
        this.exToast.unregisterAll();
        CMService.getInstance().beforefinish(0);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.PullService
    public void onAlive() {
    }
}

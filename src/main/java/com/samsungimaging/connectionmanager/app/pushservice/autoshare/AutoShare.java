package com.samsungimaging.connectionmanager.app.pushservice.autoshare;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.Main;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService;
import com.samsungimaging.connectionmanager.app.pushservice.common.CommonUtils;
import com.samsungimaging.connectionmanager.app.pushservice.common.Const;
import com.samsungimaging.connectionmanager.dialog.ConnectionWaitDialog;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.dialog.RefusalDialog;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class AutoShare extends CommonPushService {
    private static final int NOTI_RECEIVE_DONE = 1;
    private static final int NOTI_RECEIVE_PROGRESS = 2;
    private static final int SERVER_PORT = 1801;
    private static int mResolutionSize;
    private RemoteViews contentView;
    private RemoteViews contentView_prog;
    private int mPortNumber;
    private WifiInfo mWifiInfo;
    private NotificationManager nm;
    private Notification noti;
    private Notification noti_prog;
    private PendingIntent pending;
    private PendingIntent pending_prog;
    private static final Trace.Tag TAG = Trace.Tag.AS;
    private static boolean configChanged = false;
    private static int updateInterval = 0;
    private static WifiManager mWifimanager = null;
    private static int save_cnt = 0;
    private ServerSocket svSock = null;
    private InetAddress serverAddr = null;
    private RcvThread mRcvThread = null;
    private String mCreateFilePath = null;
    private String mReceivedFilePath = null;
    private String mHostName = null;
    private String mContentLength = null;
    private String mAuthorization = null;
    private String mRequest = null;
    private int image_file_idx = 0;
    private boolean bAcceptRun = false;
    private boolean bRcvThreadRun = false;
    private InitTask sscTask = null;
    private DeInitTask sscTaskEx = null;
    private GpsTask sscGpsTask = null;
    private boolean[] rcv_on = new boolean[5];
    private boolean wifi_stat_conn = false;
    private int g_progress_percent = 0;
    private int pre_progress_percent = 0;
    private boolean isHeaderError = false;
    private boolean isBackGround = false;
    private boolean isFileSaveError = false;
    private boolean isHomeKeyPressed = false;
    private boolean dscConnected = false;
    private Context mContext = null;
    private Queue<String> fileQueue = null;
    private WifiStateChangedReceiverForAppClose mWifiStateChangedReceiverForAppClose = null;
    private ConnectionWaitDialog mDialogCMWaitConnect = null;
    private PowerManager mPowerManager = null;
    private PowerManager.WakeLock mWakeLock = null;
    private LocationManager locationManager = null;
    private Location currentLocation_network = null;
    private Location currentLocation_gps = null;
    private String Latitude = null;
    private String Longitude = null;
    private String mGpsInfo = null;
    Handler mHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Location location;
            switch (msg.what) {
                case 3:
                    Trace.d(AutoShare.TAG, "Const.MsgId.AP_CONNECTED");
                    AutoShare.this.registerDisconnectReceiver();
                    AutoShare.this.mWifiInfo = AutoShare.mWifimanager.getConnectionInfo();
                    int ipAddress = AutoShare.this.mWifiInfo.getIpAddress();
                    Trace.d(AutoShare.TAG, "ip:" + ipAddress + " -> " + CommonUtils.intToIp(ipAddress));
                    if (AutoShare.mWifimanager.isWifiEnabled() && !AutoShare.this.dscConnected) {
                        Trace.d(AutoShare.TAG, "wifi was already connected");
                        AutoShare.this.sscTask = new InitTask(AutoShare.this, null);
                        AutoShare.this.sscTask.execute(AutoShare.this.mWifiInfo);
                        Trace.d(AutoShare.TAG, "InitTask called  1");
                        break;
                    }
                    break;
                case 22:
                    Trace.d(AutoShare.TAG, "Const.MsgId.APP_CLOSE_FOR_REMOVED_SD");
                    AutoShare.this.showDialog(1003);
                    break;
                case 24:
                    String exitReason = (String) msg.obj;
                    Trace.d(AutoShare.TAG, "APP_CLOSED with  : " + exitReason);
                    AutoShare.this.finishSafe(exitReason);
                    break;
                case 36:
                    Trace.d(AutoShare.TAG, "Const.MsgId.CANCEL_SAVING");
                    CustomProgressDialog mProgressDialog = (CustomProgressDialog) AutoShare.this.mDialogList.get(1000);
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Trace.d(AutoShare.TAG, "file Queue remove" + ((String) AutoShare.this.fileQueue.poll()));
                    AutoShare.this.nm.cancel(1);
                    AutoShare.this.nm.cancel(2);
                    break;
                case 51:
                    Trace.d(AutoShare.TAG, "Const.MsgId.IMAGE_RCV_DONE");
                    if (!AutoShare.this.fileQueue.isEmpty()) {
                        AutoShare.this.mReceivedFilePath = (String) AutoShare.this.fileQueue.poll();
                        if (AutoShare.this.currentLocation_gps != null || AutoShare.this.currentLocation_network != null) {
                            try {
                                ExifInterface exif = new ExifInterface(AutoShare.this.mReceivedFilePath);
                                String gpsInfo = exif.getAttribute("GPSLatitude");
                                if (gpsInfo == null) {
                                    if (AutoShare.this.currentLocation_gps == null) {
                                        location = AutoShare.this.currentLocation_network;
                                    } else {
                                        location = AutoShare.this.currentLocation_gps;
                                    }
                                    String gpsValue = AutoShare.this.setGpsValue(location);
                                    exif.setAttribute("GPSLatitudeRef", AutoShare.this.Latitude);
                                    exif.setAttribute("GPSLongitudeRef", AutoShare.this.Longitude);
                                    exif.setAttribute("GPSLatitude", AutoShare.this.deg_to_dms(location.getLatitude()));
                                    exif.setAttribute("GPSLongitude", AutoShare.this.deg_to_dms(location.getLongitude()));
                                    Trace.d(AutoShare.TAG, "Location mGpsInfo : " + AutoShare.this.mGpsInfo + ", gpsValue : " + gpsValue);
                                    exif.saveAttributes();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Trace.d(AutoShare.TAG, "IMAGE_RCV_DONE [" + AutoShare.save_cnt + "] change. path:" + AutoShare.this.mReceivedFilePath);
                    }
                    AutoShare.save_cnt++;
                    if (AutoShare.this.isBackGround) {
                        AutoShare.this.noti.defaults |= 1;
                        String.format(AutoShare.this.getResources().getString(R.string.files_received), Integer.valueOf(AutoShare.save_cnt));
                        AutoShare.this.nm.notify(1, AutoShare.this.noti);
                        AutoShare.this.nm.cancel(2);
                    }
                    break;
                case 52:
                    AutoShare.this.bAcceptRun = false;
                    for (int i = 0; i < 5; i++) {
                        AutoShare.this.rcv_on[i] = false;
                    }
                    AutoShare.this.showDialog(1006);
                    Trace.d(AutoShare.TAG, "wifi disconnected!!!!!, send message app close after 1000ms");
                    AutoShare.this.mHandler.sendEmptyMessageDelayed(24, 1000L);
                    break;
                case 53:
                    Trace.d(AutoShare.TAG, "Const.MsgId.IMAGE_RCV_WAIT");
                    break;
                case 62:
                    CustomProgressDialog mProgressDialog2 = (CustomProgressDialog) AutoShare.this.mDialogList.get(1000);
                    if (mProgressDialog2 != null && mProgressDialog2.isShowing()) {
                        mProgressDialog2.setProgress(msg.arg2);
                    }
                    if (AutoShare.this.bDownloading()) {
                        AutoShare.this.contentView_prog.setProgressBar(R.id.push_status_progress, 100, AutoShare.this.g_progress_percent, false);
                        AutoShare.this.nm.notify(2, AutoShare.this.noti_prog);
                        AutoShare.this.pre_progress_percent = AutoShare.this.g_progress_percent;
                    }
                    break;
                case 63:
                    CustomProgressDialog mProgressDialog3 = (CustomProgressDialog) AutoShare.this.mDialogList.get(1000);
                    if (mProgressDialog3 == null || !mProgressDialog3.isShowing()) {
                        Trace.d(AutoShare.TAG, "Show mProgressDialog");
                        AutoShare.this.showDialog(1000);
                        ((CustomProgressDialog) AutoShare.this.mDialogList.get(1000)).setMax(msg.arg1);
                    }
                    AutoShare.this.fileQueue.offer(AutoShare.this.mCreateFilePath);
                    Trace.d(AutoShare.TAG, "SSC Thumbnail [" + AutoShare.save_cnt + "] add. path:" + AutoShare.this.mCreateFilePath);
                    break;
                case 64:
                    AutoShare.this.mGallery.setState(GalleryFragment.State.NORMAL, new Object[0]);
                    CustomProgressDialog mProgressDialog4 = (CustomProgressDialog) AutoShare.this.mDialogList.get(1000);
                    if (mProgressDialog4 != null) {
                        mProgressDialog4.setProgress(msg.arg1);
                        mProgressDialog4.dismiss();
                    }
                    if (AutoShare.this.bDownloading()) {
                        AutoShare.this.contentView_prog.setProgressBar(R.id.push_status_progress, 100, 100, false);
                        AutoShare.this.nm.notify(2, AutoShare.this.noti_prog);
                        AutoShare.this.pre_progress_percent = AutoShare.this.g_progress_percent;
                    }
                    break;
                case 71:
                    Trace.d(AutoShare.TAG, "Const.MsgId.BACK_BUTTON_INVALID");
                    break;
                case 72:
                    Toast.makeText(AutoShare.this.mContext, R.string.ml_apsettingfail, 0).show();
                    Trace.d(AutoShare.TAG, "Const.MsgId.WIFI_CONNECTION_IS_BAD");
                    AutoShare.this.mHandler.sendEmptyMessageDelayed(24, 3000L);
                    break;
                case 73:
                    Bundle args = new Bundle();
                    args.putString("error", CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR);
                    AutoShare.this.showDialog(1007, args);
                    break;
                case 74:
                    Bundle args2 = new Bundle();
                    args2.putString("error", CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR);
                    AutoShare.this.showDialog(1007, args2);
                    break;
            }
        }
    };
    int socket_idx = 0;
    int svSockReceiveBufferSize = 0;
    LocationListener locationListener_gps = new LocationListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.2
        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Trace.d(AutoShare.TAG, "locationListener_gps onStatusChanged provider : " + provider + ", status : " + status);
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
            if (AutoShare.this.locationManager != null) {
                AutoShare.this.currentLocation_gps = AutoShare.this.locationManager.getLastKnownLocation(provider);
            }
            Trace.d(AutoShare.TAG, "locationListener_gps onProviderEnabled provider : " + provider);
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
            AutoShare.this.currentLocation_gps = null;
            Trace.d(AutoShare.TAG, "locationListener_gps onProviderDisabled provider : " + provider);
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            AutoShare.this.currentLocation_gps = location;
            Trace.d(AutoShare.TAG, "locationListener_gps onLocationChanged currentLocation" + location.toString());
        }
    };
    LocationListener locationListener_network = new LocationListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.3
        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Trace.d(AutoShare.TAG, "locationListener_network_gps onStatusChanged provider : " + provider + ", status : " + status);
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
            if (AutoShare.this.locationManager != null) {
                AutoShare.this.currentLocation_network = AutoShare.this.locationManager.getLastKnownLocation(provider);
            }
            Trace.d(AutoShare.TAG, "locationListener_network_gps onProviderEnabled provider : " + provider);
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
            AutoShare.this.currentLocation_network = null;
            Trace.d(AutoShare.TAG, "locationListener_network_gps onProviderDisabled provider : " + provider);
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            AutoShare.this.currentLocation_network = location;
            Trace.d(AutoShare.TAG, "locationListener_network_gps onLocationChanged currentLocation" + location.toString());
        }
    };

    private double roundDouble(double d, int places) {
        return Math.round(Math.pow(10.0d, places) * d) / Math.pow(10.0d, places);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String deg_to_dms(double degfloat) {
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

    public class RcvThread extends Thread {
        public RcvThread() {
        }

        public void quit() {
            if (AutoShare.this.svSock != null) {
                try {
                    AutoShare.this.svSock.close();
                    Trace.d(AutoShare.TAG, "Server Socket close");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                try {
                    Trace.i(AutoShare.TAG, "SSC RcvThread Run...");
                    AutoShare.this.svSock = new ServerSocket(AutoShare.SERVER_PORT, 0, AutoShare.this.serverAddr);
                    Trace.i(AutoShare.TAG, "SSC RcvThread Server Socket Created.");
                    AutoShare.this.socket_idx = 0;
                    while (AutoShare.this.bAcceptRun) {
                        AutoShare.this.socket_idx = AutoShare.this.getAvailSock();
                        if (AutoShare.this.socket_idx != -1) {
                            Trace.i(AutoShare.TAG, "SSC RcvThread Server Socket Waiting...");
                            Socket client = AutoShare.this.svSock.accept();
                            client.setSoTimeout(5000);
                            Trace.i(AutoShare.TAG, "SSC RcvThread Server Socket Accept.");
                            Trace.i(AutoShare.TAG, "socket_idx1: " + AutoShare.this.socket_idx);
                            if (isInterrupted()) {
                                Trace.d(AutoShare.TAG, "RcvThread interrupted");
                                break;
                            }
                            AutoShare.this.rcv_on[AutoShare.this.socket_idx] = true;
                            AutoShare.this.svSockReceiveBufferSize = AutoShare.this.svSock.getReceiveBufferSize();
                            Trace.d(AutoShare.TAG, "svSock.getReceiveBufferSize() : " + AutoShare.this.svSock.getReceiveBufferSize());
                            if (AutoShare.this.mWakeLock != null) {
                                AutoShare.this.mWakeLock.acquire();
                            }
                            ImageDownloader imageDownloader = AutoShare.this.new ImageDownloader(client, AutoShare.this.socket_idx);
                            AutoShare.this.waitPreviousDownloading();
                            imageDownloader.start();
                            if (AutoShare.this.mWakeLock != null && AutoShare.this.mWakeLock.isHeld()) {
                                AutoShare.this.mWakeLock.release();
                            }
                        } else {
                            Trace.d(AutoShare.TAG, "No available socket");
                        }
                    }
                    AutoShare.this.svSock.close();
                    Trace.d(AutoShare.TAG, "Server Socket close");
                    try {
                        AutoShare.this.svSock.close();
                        Trace.d(AutoShare.TAG, "Server Socket close");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        AutoShare.this.svSock.close();
                        Trace.d(AutoShare.TAG, "Server Socket close");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                Trace.d(AutoShare.TAG, "RcvThread exit run");
            } catch (Throwable th) {
                try {
                    AutoShare.this.svSock.close();
                    Trace.d(AutoShare.TAG, "Server Socket close");
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitPreviousDownloading() {
        for (int roopCnt = 10; roopCnt > 0 && this.rcv_on[0]; roopCnt--) {
            SystemClock.sleep(100L);
        }
    }

    public int getAvailSock() {
        Trace.d(TAG, "getAvailSock");
        for (int i = 0; i < 5; i++) {
            if (this.rcv_on[i]) {
                Trace.d(TAG, "rcv_on[" + i + "] is true");
            } else {
                return i;
            }
        }
        return -1;
    }

    public boolean barray_reset(byte[] barray) {
        for (int i = 0; i < barray.length; i++) {
            barray[i] = 0;
        }
        return true;
    }

    public byte[] make_resp(String err_code) {
        String ret_str;
        if (err_code == "0") {
            ret_str = "S2L/1.0 Result_OK\r\nHost: " + this.mHostName + HTTP.CRLF + "Content-length: " + this.mContentLength + HTTP.CRLF + "Authorization: " + this.mAuthorization + HTTP.CRLF + "Sub-ErrorCode: " + err_code + "\r\n\r\n";
        } else {
            ret_str = "S2L/1.0 Result_Error\r\nHost: " + this.mHostName + HTTP.CRLF + "Content-length: " + this.mContentLength + HTTP.CRLF + "Authorization: " + this.mAuthorization + HTTP.CRLF + "Sub-ErrorCode: " + err_code + "\r\n\r\n";
        }
        Trace.d(TAG, "make_resp : " + err_code);
        return ret_str.getBytes();
    }

    public class ImageDownloader {
        FileOutputStream fos;
        InputStream inStream;
        String mFileName;
        private TimerTask mTimer;
        OutputStream outStream;
        int sock_idx;
        protected Socket socket;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        File mReceivingFile = null;
        private Timer timer = new Timer();

        public ImageDownloader(Socket socket, int sock_idx) {
            this.inStream = null;
            this.outStream = null;
            this.socket = socket;
            this.sock_idx = sock_idx;
            try {
                this.inStream = socket.getInputStream();
                this.outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setFileName(String fileName) {
            this.mFileName = fileName;
        }

        public String getFileName() {
            return this.mFileName;
        }

        private void startTimer() {
            Trace.d(AutoShare.TAG, "startTimer");
            this.mTimer = new TimerTask() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.ImageDownloader.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    Trace.d(AutoShare.TAG, "Blocking....");
                    AutoShare.this.rcv_on[ImageDownloader.this.sock_idx] = false;
                    try {
                        if (ImageDownloader.this.dis != null) {
                            ImageDownloader.this.dis.close();
                            ImageDownloader.this.dis = null;
                        }
                        if (ImageDownloader.this.dos != null) {
                            ImageDownloader.this.dos.close();
                            ImageDownloader.this.dos = null;
                        }
                    } catch (IOException e) {
                        Trace.d(AutoShare.TAG, "IOException 03");
                        e.printStackTrace();
                    }
                }
            };
            this.timer.schedule(this.mTimer, 1000L);
        }

        private void stopTimer() {
            Trace.d(AutoShare.TAG, "stopTimer");
            this.mTimer.cancel();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void start() {
            int length = 256;
            long receiving_size = 0;
            try {
                try {
                    byte[] buf = new byte[102400];
                    boolean bHeaderParseCompleted = false;
                    Trace.d(AutoShare.TAG, "ImageDownloader Start, bAcceptRun : " + AutoShare.this.bAcceptRun);
                    try {
                        if (!AutoShare.this.bAcceptRun) {
                            AutoShare.this.rcv_on[this.sock_idx] = false;
                            return;
                        }
                        try {
                            try {
                                this.dis = new DataInputStream(this.inStream);
                                this.dos = new DataOutputStream(this.outStream);
                                int idx = 0;
                                long file_size = 0;
                                int roop_Cnt = 0;
                                AutoShare.this.isHeaderError = false;
                                AutoShare.this.isFileSaveError = false;
                                Trace.d(AutoShare.TAG, "socket isConnected : " + this.socket.isConnected());
                                Trace.d(AutoShare.TAG, "Camera client timeout: " + this.socket.getSoTimeout());
                                while (true) {
                                    int num = this.dis.read(buf, 0, length);
                                    if (num == -1 || !AutoShare.this.bAcceptRun) {
                                        break;
                                    }
                                    if (!bHeaderParseCompleted) {
                                        while (idx <= num - 4) {
                                            if (buf[idx] == 13 && buf[idx + 1] == 10 && buf[idx + 2] == 13 && buf[idx + 3] == 10) {
                                                bHeaderParseCompleted = true;
                                                Trace.d(AutoShare.TAG, "Performance Check Point : Header Received byte : " + idx);
                                                String header = new String(buf, 0, idx);
                                                Trace.d(AutoShare.TAG, "header : " + header);
                                                HeaderParser headerParser = new HeaderParser(header);
                                                if (headerParser.getHeaderParsed() == 2) {
                                                    break;
                                                }
                                                AutoShare.this.mHostName = headerParser.getSpecificValue("Host");
                                                AutoShare.this.mContentLength = headerParser.getSpecificValue("Content-length");
                                                AutoShare.this.mAuthorization = headerParser.getSpecificValue("Authorization");
                                                if (AutoShare.this.mHostName != null && AutoShare.this.mContentLength != null && AutoShare.this.mAuthorization != null) {
                                                    AutoShare.this.makeDirectory();
                                                    AutoShare.this.mCreateFilePath = CommonUtils.renameFile(CommonUtils.getDefaultStorage(), headerParser.getFileName());
                                                    AutoShare.this.mCreateFilePath = String.valueOf(CommonUtils.getDefaultStorage()) + AutoShare.this.mCreateFilePath;
                                                    Trace.d(AutoShare.TAG, "Create File Path : " + AutoShare.this.mCreateFilePath);
                                                    this.mReceivingFile = new File(AutoShare.this.mCreateFilePath);
                                                    this.fos = new FileOutputStream(this.mReceivingFile);
                                                    file_size = Integer.parseInt(AutoShare.this.mContentLength);
                                                    Trace.d(AutoShare.TAG, "file_size : " + file_size);
                                                    if (file_size < 1048576) {
                                                        AutoShare.updateInterval = 10;
                                                        length = 200;
                                                    } else if (file_size < 5242880) {
                                                        AutoShare.updateInterval = 30;
                                                        length = 1024;
                                                    } else {
                                                        AutoShare.updateInterval = 50;
                                                        length = buf.length;
                                                    }
                                                    if (file_size > CommonUtils.getAvailableExternalMemorySize() - Const.Config.CRITICAL_SD_MEMORY) {
                                                        Trace.d(AutoShare.TAG, "##### header check error - not enough memory !!! #####");
                                                        Trace.d(AutoShare.TAG, "receving size : " + file_size + "      phone free size : " + CommonUtils.getAvailableExternalMemorySize());
                                                        AutoShare.this.isFileSaveError = true;
                                                        Toast.makeText(AutoShare.this.mContext, R.string.insufficient_memory, 0).show();
                                                    }
                                                    AutoShare.this.mHandler.sendMessage(AutoShare.this.mHandler.obtainMessage(63, (int) (file_size / 1000), 0));
                                                    AutoShare.this.mHandler.sendMessage(AutoShare.this.mHandler.obtainMessage(62, (int) (file_size / 1000), 0));
                                                    SystemClock.sleep(300L);
                                                    this.fos.write(buf, idx + 4, (num - idx) - 4);
                                                    receiving_size = (num - idx) - 4;
                                                    Trace.d(AutoShare.TAG, "file size first : " + receiving_size);
                                                    AutoShare.this.mHandler.sendMessage(AutoShare.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                                    break;
                                                }
                                                Trace.d(AutoShare.TAG, "ImageDownloader Header Parsing Error  ");
                                                break;
                                            }
                                            idx++;
                                        }
                                        if (!bHeaderParseCompleted) {
                                            Trace.d(AutoShare.TAG, "Can't find contents download header start");
                                            String header2 = new String(buf, 0, idx);
                                            Trace.d(AutoShare.TAG, "header : " + header2);
                                            AutoShare.this.mRequest = new HeaderParser(header2).getSpecificValue("Request");
                                            if (!AutoShare.this.mRequest.contains("ByeBye_Stand") && !AutoShare.this.mRequest.contains("byebye_stand")) {
                                                if (!AutoShare.this.mRequest.contains("Bye") && !AutoShare.this.mRequest.contains("bye")) {
                                                    AutoShare.this.isHeaderError = true;
                                                    break;
                                                }
                                                Trace.d(AutoShare.TAG, "ImageDownloader Header Parsing ByeBye, send message App close ");
                                                AutoShare.this.mHandler.sendEmptyMessage(24);
                                                this.socket.close();
                                                this.socket.close();
                                                AutoShare.this.rcv_on[this.sock_idx] = false;
                                                AutoShare.this.rcv_on[this.sock_idx] = false;
                                                return;
                                            }
                                            Trace.d(AutoShare.TAG, "ImageDownloader Header Parsing ByeBye_StandBy, send message App close with standby");
                                            Message msg = AutoShare.this.mHandler.obtainMessage(24, Const.ExitReason.BYEBYE_STANDBY);
                                            AutoShare.this.mHandler.sendMessage(msg);
                                            this.socket.close();
                                            this.socket.close();
                                            AutoShare.this.rcv_on[this.sock_idx] = false;
                                            AutoShare.this.rcv_on[this.sock_idx] = false;
                                            return;
                                        }
                                    } else {
                                        if (!AutoShare.this.isFileSaveError) {
                                            this.fos.write(buf, 0, num);
                                        }
                                        receiving_size += (long) num;
                                        AutoShare.this.g_progress_percent = (int) ((100 * receiving_size) / file_size);
                                        if (roop_Cnt % AutoShare.updateInterval == 0) {
                                            Message msg2 = AutoShare.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000));
                                            AutoShare.this.mHandler.sendMessage(msg2);
                                        }
                                        roop_Cnt++;
                                    }
                                    if (receiving_size >= file_size) {
                                        if (!AutoShare.this.isFileSaveError) {
                                            AutoShare.this.mHandler.sendMessage(AutoShare.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                            SystemClock.sleep(200L);
                                            AutoShare.this.mHandler.sendMessage(AutoShare.this.mHandler.obtainMessage(64, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                            Trace.d(AutoShare.TAG, "Performance Check Point : file receive complete !!!");
                                            AutoShare.this.image_file_idx++;
                                            Trace.d(AutoShare.TAG, "image file save num : " + AutoShare.this.image_file_idx);
                                            this.dos.write(AutoShare.this.make_resp("0"));
                                            break;
                                        }
                                        Trace.d(AutoShare.TAG, "file receive fail !!!");
                                        Trace.d(AutoShare.TAG, "not enough memory !!!");
                                        break;
                                    }
                                    Trace.d(AutoShare.TAG, "Starting read: ");
                                }
                                this.fos.close();
                                Trace.d(AutoShare.TAG, "rcv_on[" + this.sock_idx + "] is false");
                                AutoShare.this.rcv_on[this.sock_idx] = false;
                                if (AutoShare.this.isHeaderError) {
                                    this.dos.write(AutoShare.this.make_resp(Const.ErrCode.SSC_ERROR_HEADER_OMISSION));
                                    Trace.d(AutoShare.TAG, "Performance Check Point : Header Error");
                                    this.socket.close();
                                    AutoShare.this.mHandler.sendEmptyMessage(36);
                                    if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                        Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                        this.mReceivingFile.delete();
                                    }
                                    this.socket.close();
                                    AutoShare.this.rcv_on[this.sock_idx] = false;
                                    AutoShare.this.rcv_on[this.sock_idx] = false;
                                    return;
                                }
                                if (!AutoShare.this.isFileSaveError) {
                                    Trace.d(AutoShare.TAG, "receive complete!");
                                    AutoShare.this.updateMediaContent(AutoShare.this.mCreateFilePath);
                                    AutoShare.this.mHandler.sendEmptyMessage(51);
                                    this.socket.close();
                                    Trace.d(AutoShare.TAG, "file write complete");
                                    this.socket.close();
                                    AutoShare.this.rcv_on[this.sock_idx] = false;
                                    AutoShare.this.rcv_on[this.sock_idx] = false;
                                    return;
                                }
                                this.dos.write(AutoShare.this.make_resp(Const.ErrCode.SSC_ERROR_FILE_SAVE_FAIL));
                                Trace.d(AutoShare.TAG, "Performance Check Point : File Save Error");
                                this.socket.close();
                                AutoShare.this.mHandler.sendEmptyMessage(36);
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                this.socket.close();
                                AutoShare.this.rcv_on[this.sock_idx] = false;
                                AutoShare.this.rcv_on[this.sock_idx] = false;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Trace.d(AutoShare.TAG, "DownloadOnSDcard  FileNotFoundException: " + e.toString());
                                this.dos.write(AutoShare.this.make_resp(Const.ErrCode.SSC_ERROR_FILE_OPEN_FAIL));
                                Trace.d(AutoShare.TAG, "Performance Check Point : File Not Found");
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                AutoShare.this.mHandler.sendEmptyMessage(36);
                                this.socket.close();
                                AutoShare.this.rcv_on[this.sock_idx] = false;
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                this.dos.write(AutoShare.this.make_resp(Const.ErrCode.SSC_ERROR_UNKNOWN));
                                Trace.d(AutoShare.TAG, "Performance Check Point : Unknown Exception");
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                AutoShare.this.mHandler.sendEmptyMessage(36);
                                this.socket.close();
                                AutoShare.this.rcv_on[this.sock_idx] = false;
                            }
                        } catch (SocketException se) {
                            se.printStackTrace();
                            Trace.d(AutoShare.TAG, "DownloadOnSDcard  SocketException: " + se.toString());
                            try {
                                if (this.fos != null) {
                                    this.fos.close();
                                }
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                            if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                this.mReceivingFile.delete();
                            }
                            AutoShare.this.mHandler.sendEmptyMessage(36);
                            this.socket.close();
                            AutoShare.this.rcv_on[this.sock_idx] = false;
                        } catch (IOException e4) {
                            e4.printStackTrace();
                            Trace.d(AutoShare.TAG, "DownloadOnSDcard  IOException: " + e4.toString());
                            try {
                                if (this.fos != null) {
                                    this.fos.close();
                                }
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                            if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                this.mReceivingFile.delete();
                            }
                            AutoShare.this.mHandler.sendEmptyMessage(36);
                            this.socket.close();
                            AutoShare.this.rcv_on[this.sock_idx] = false;
                        }
                    } catch (Throwable th) {
                        this.socket.close();
                        AutoShare.this.rcv_on[this.sock_idx] = false;
                        throw th;
                    }
                } catch (Exception e6) {
                    e6.printStackTrace();
                    try {
                        if (this.fos != null) {
                            this.fos.close();
                        }
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                    if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                        Trace.d(AutoShare.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                        this.mReceivingFile.delete();
                    }
                    Trace.d(AutoShare.TAG, "ImageDownloader  Exception: " + e6.toString());
                    try {
                        this.dos.write(AutoShare.this.make_resp(Const.ErrCode.SSC_ERROR_SOCKET_CREATE_FAIL));
                        Trace.d(AutoShare.TAG, "Performance Check Point : Socket Create Fail");
                        this.socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    AutoShare.this.mHandler.sendEmptyMessage(36);
                    AutoShare.this.rcv_on[this.sock_idx] = false;
                }
            } catch (Throwable th2) {
                AutoShare.this.rcv_on[this.sock_idx] = false;
                throw th2;
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Trace.d(TAG, "**** onresume");
        this.noti.defaults = 0;
        this.isBackGround = false;
        this.nm.cancel(1);
        this.nm.cancel(2);
        Trace.d(TAG, "onResume() ended dscConnected : " + this.dscConnected);
        Trace.d(TAG, "isBackGround false");
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.isBackGround = true;
        Trace.d(TAG, "onPause isBackGround true");
        if (this.isHomeKeyPressed) {
            String.format(getResources().getString(R.string.files_received), Integer.valueOf(save_cnt));
            this.nm.notify(1, this.noti);
            this.isHomeKeyPressed = false;
        }
    }

    private void startGpsTracking() {
        this.locationManager = (LocationManager) getSystemService("location");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        this.locationManager.requestLocationUpdates("gps", 5000L, 10.0f, this.locationListener_gps);
        if (telephonyManager.getNetworkType() != 0) {
            this.locationManager.requestLocationUpdates("network", 5000L, 10.0f, this.locationListener_network);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService, com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setService(CommonPushService.Service.AUTOSHARE);
        Trace.d(TAG, "oncreate : AutoShare");
        mWifimanager = (WifiManager) getSystemService("wifi");
        setContentView();
        this.mContext = this;
        this.nm = (NotificationManager) getSystemService("notification");
        startGpsTracking();
        this.noti = new Notification(R.drawable.push_indicator_send_completion, "AutoShare", System.currentTimeMillis());
        this.noti.flags |= 16;
        Intent intent = new Intent(getApplicationContext(), (Class<?>) Main.class);
        intent.addFlags(872415232);
        this.contentView = new RemoteViews(getPackageName(), R.layout.push_noti_indicator);
        this.noti.contentView = this.contentView;
        this.pending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 134217728);
        this.noti.contentIntent = this.pending;
        this.noti_prog = new Notification(R.drawable.push_indicator_down, "AutoShare", System.currentTimeMillis());
        this.noti_prog.flags |= 16;
        this.noti_prog.flags |= 2;
        Intent intent_prog = new Intent(getApplicationContext(), (Class<?>) Main.class);
        intent_prog.addFlags(872415232);
        this.contentView_prog = new RemoteViews(getPackageName(), R.layout.push_noti_indicator_progress);
        this.contentView_prog.setProgressBar(R.id.push_status_progress, 100, 0, false);
        this.noti_prog.contentView = this.contentView_prog;
        this.pending_prog = PendingIntent.getActivity(getApplicationContext(), 0, intent_prog, 134217728);
        this.noti_prog.contentIntent = this.pending_prog;
        if (configChanged) {
            Trace.d(TAG, "onCreate  configChanged is true");
            Toast.makeText(this.mContext, R.string.rvf_changed_system_setting, 0).show();
            configChanged = false;
            this.mHandler.sendEmptyMessageDelayed(24, 3000L);
            return;
        }
        makeDirectory();
        for (int i = 0; i < 5; i++) {
            this.rcv_on[i] = false;
        }
        if (this.mRcvThread == null) {
            Trace.d(TAG, "create RcvThread");
            this.mRcvThread = new RcvThread();
        }
        if (!this.mSettings.getIntroGuide()) {
            showDialog(1002);
        } else {
            Toast.makeText(this.mContext, R.string.home_screen_continue_receive_files, 0).show();
        }
        this.fileQueue = new LinkedList();
        this.mHandler.sendEmptyMessage(3);
        if (mWifimanager.isWifiEnabled() && !this.dscConnected) {
            showDialog(1005);
        }
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(1, "SSC_WAKE_LOCK");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeDirectory() {
        File file_path = new File(CommonUtils.getDefaultStorage());
        if (!file_path.exists() && !file_path.isDirectory()) {
            file_path.mkdirs();
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService, com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    protected void onDestroy() {
        Trace.i(TAG, "onDestroy()");
        super.onDestroy();
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        if (CMUtil.isForceClose(activityManager)) {
            SystemClock.sleep(3000L);
        }
    }

    public static int getResolutionSize() {
        return mResolutionSize;
    }

    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Trace.d(TAG, "onConfigurationChanged() newConfig.orientation : " + newConfig.orientation);
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case 1:
            case 2:
                Trace.d(TAG, "-=> onConfigurationChanged  orientation [1:Port,2:Land] = " + newConfig.orientation);
                setContentView();
                CommonUtils.setSystemConfigurationChanged(getApplicationContext());
                break;
        }
    }

    private void setContentView() {
        if (!CommonUtils.isMountedExternalStorage()) {
            showDialog(1003);
        } else if (CommonUtils.isMemoryFull()) {
            showDialog(1004);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (isBackPressAvailable()) {
            exitAfterSendByeBye(Const.ExitReason.BACKEY_PRESSED);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService, com.samsungimaging.connectionmanager.app.BaseGalleryActivity, android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        Trace.i(TAG, "onCreateDialog() id " + id);
        switch (id) {
            case 1005:
                if (this.mDialogCMWaitConnect == null) {
                    this.mDialogCMWaitConnect = new ConnectionWaitDialog(this);
                    this.mDialogCMWaitConnect.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.4
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            switch (keyCode) {
                                case 4:
                                    Trace.d(AutoShare.TAG, "back key pressed, send app close message");
                                    AutoShare.this.dscConnected = true;
                                    AutoShare.this.exitAfterSendByeBye(Const.ExitReason.CANCELED);
                                    break;
                            }
                            return false;
                        }
                    });
                    this.mDialogCMWaitConnect.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.5
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Trace.d(AutoShare.TAG, "Connection Wait Dialog is close, dscConnected is " + AutoShare.this.dscConnected);
                            AutoShare.this.dscConnected = true;
                            AutoShare.this.exitAfterSendByeBye(Const.ExitReason.CANCELED);
                        }
                    });
                }
                return this.mDialogCMWaitConnect;
            case 1006:
            default:
                return super.onCreateDialog(id, bundle);
            case 1007:
                CustomDialog custom = new RefusalDialog(this);
                String message = bundle.getString("error");
                custom.setTag(message);
                custom.setNeutralButton(R.string.cm_confirm, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.6
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Trace.d(AutoShare.TAG, "Camera service is rejected, closing...");
                        AutoShare.this.mHandler.sendEmptyMessage(24);
                        dialog.dismiss();
                    }
                });
                custom.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.autoshare.AutoShare.7
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        if (event.getKeyCode() != 4) {
                            return false;
                        }
                        AutoShare.this.mHandler.sendEmptyMessage(24);
                        dialogInterface.dismiss();
                        return true;
                    }
                });
                return custom;
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
    public void registerDisconnectReceiver() {
        this.mWifiStateChangedReceiverForAppClose = new WifiStateChangedReceiverForAppClose(this, null);
        IntentFilter filter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        registerReceiver(this.mWifiStateChangedReceiverForAppClose, filter);
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
                    iArr[NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.DISCONNECTING.ordinal()] = 7;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.FAILED.ordinal()] = 8;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.IDLE.ordinal()] = 9;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 11;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.SUSPENDED.ordinal()] = 12;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[NetworkInfo.DetailedState.VERIFYING_POOR_LINK.ordinal()] = 13;
                } catch (NoSuchFieldError e13) {
                }
                $SWITCH_TABLE$android$net$NetworkInfo$DetailedState = iArr;
            }
            return iArr;
        }

        private WifiStateChangedReceiverForAppClose() {
        }

        /* synthetic */ WifiStateChangedReceiverForAppClose(AutoShare autoShare, WifiStateChangedReceiverForAppClose wifiStateChangedReceiverForAppClose) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra("connected", false);
                if (!connected && AutoShare.this.wifi_stat_conn) {
                    AutoShare.this.wifi_stat_conn = false;
                    AutoShare.this.dscConnected = false;
                    AutoShare.this.mHandler.sendEmptyMessage(52);
                }
                return;
            }
            if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo.DetailedState state = info.getDetailedState();
                Trace.d(AutoShare.TAG, "NetworkInfo.DetailedState = " + state.name());
                switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[state.ordinal()]) {
                    case 4:
                        AutoShare.this.wifi_stat_conn = true;
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTED");
                        break;
                    case 5:
                        AutoShare.this.wifi_stat_conn = true;
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTING");
                        break;
                    case 6:
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTED");
                        Trace.d(AutoShare.TAG, "wifi_stat_conn : " + AutoShare.this.wifi_stat_conn);
                        if (AutoShare.this.wifi_stat_conn) {
                            AutoShare.this.wifi_stat_conn = false;
                            AutoShare.this.dscConnected = false;
                            AutoShare.this.mHandler.sendEmptyMessage(52);
                        }
                        break;
                    case 7:
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTING");
                        break;
                    case 8:
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - FAILED");
                        break;
                    case 9:
                    default:
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - Unknown");
                        break;
                    case 10:
                        Trace.d(AutoShare.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - OBTAINING_IPADDR");
                        break;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean bDownloading() {
        boolean result = false;
        if (!this.isBackGround || this.pre_progress_percent == this.g_progress_percent) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            if (this.rcv_on[i]) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override // android.app.Activity
    protected void onUserLeaveHint() {
        if (!isFinishing()) {
            this.isHomeKeyPressed = true;
        }
        Trace.d(TAG, "onUserLeaveHint(), isHomeKeyPressed : " + this.isHomeKeyPressed);
        super.onUserLeaveHint();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean SSC_Init(int portNum) throws Throwable {
        boolean isInited = false;
        Trace.d(TAG, "SSC_Init()");
        WifiInfo wifiInfo = mWifimanager.getConnectionInfo();
        if (!mWifimanager.isWifiEnabled()) {
            return false;
        }
        int ip = 0;
        for (int cnt = 0; cnt < 5 && (ip = wifiInfo.getIpAddress()) == 0; cnt++) {
            SystemClock.sleep(1000L);
        }
        if (ip == 0) {
            this.mHandler.sendEmptyMessage(72);
            Trace.d(TAG, "SSC_Init Local Ip Address is 0");
            return true;
        }
        String PhoneIp = CommonUtils.intToIp(ip);
        Trace.d(TAG, "-----------------------------localIp : " + PhoneIp);
        DhcpInfo di = mWifimanager.getDhcpInfo();
        String CameraIp = CommonUtils.intToIp(di.serverAddress);
        Trace.d(TAG, "-----------------------------CameraIp : " + CameraIp);
        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(wifiInfo.getSSID());
        String accessMethod = (CMInfo.getInstance().getIsNFCLaunch() && isOldCamera) ? "nfc" : "manual";
        Trace.d(TAG, "[S2L]accessMethod : " + accessMethod);
        StringBuffer sb = new StringBuffer();
        sb.append("S2L/1.0 Request").append(HTTP.CRLF).append("Host: SAMSUNG-S2L").append(HTTP.CRLF).append("Content-Type: text/xml;charset=utf-8").append(HTTP.CRLF).append("User-Agent: APP-TYPE").append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append("HOST-Mac : ").append(wifiInfo.getMacAddress().toLowerCase()).append(HTTP.CRLF).append("HOST-Address : ").append(PhoneIp).append(HTTP.CRLF).append("HOST-port : ").append(SERVER_PORT).append(HTTP.CRLF).append("HOST-PNumber : ").append(CommonUtils.getLineNumber(getApplicationContext())).append(HTTP.CRLF).append("Host-Gps : ").append(getGpsInfo()).append(HTTP.CRLF).append("Access-Method : ").append(accessMethod).append("\r\n\r\n");
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            try {
                Trace.d(TAG, "Camera Connection Try... : [" + CameraIp + SOAP.DELIM + portNum + "]");
                Socket socket = new Socket();
                SocketAddress addr = new InetSocketAddress(CameraIp, portNum);
                socket.connect(addr, 2000);
                Trace.d(TAG, "Camera Connection Success... : [" + CameraIp + SOAP.DELIM + portNum + "]");
                this.mPortNumber = portNum;
                Trace.d(TAG, "Connected Port Num : " + this.mPortNumber);
                BufferedOutputStream bos2 = new BufferedOutputStream(socket.getOutputStream());
                try {
                    Trace.d(TAG, sb.toString());
                    bos2.write(sb.toString().getBytes());
                    bos2.flush();
                    Trace.d(TAG, "Performance Check Point : Discovery Request.");
                    BufferedInputStream bis2 = new BufferedInputStream(socket.getInputStream());
                    try {
                        String str = "";
                        byte[] buf = new byte[256];
                        Trace.d(TAG, "Camera Response Waiting...");
                        do {
                            int len = bis2.read(buf, 0, buf.length);
                            if (len == -1) {
                                break;
                            }
                            str = String.valueOf(str) + new String(buf, 0, len);
                        } while (str.indexOf("\n") == -1);
                        Trace.d(TAG, "Performance Check Point : Camera Response Received.");
                        Trace.d(TAG, str.toString());
                        if (bos2 != null) {
                            bos2.close();
                        }
                        if (bis2 != null) {
                            bis2.close();
                        }
                        if (!str.contains("Result_OK")) {
                            if (str.contains("Result_Error")) {
                                if (this.mDialogCMWaitConnect != null) {
                                    this.mDialogCMWaitConnect.dismiss();
                                }
                                this.mHandler.sendEmptyMessage(74);
                                Trace.d(TAG, "SSC_init Camera Not Accept - Result_Error");
                            } else if (str.contains("Result_Reject")) {
                                if (this.mDialogCMWaitConnect != null) {
                                    this.mDialogCMWaitConnect.dismiss();
                                }
                                this.mHandler.sendEmptyMessage(73);
                                Trace.d(TAG, "SSC_init Camera Not Accept - Result_Reject");
                            } else {
                                if (this.mDialogCMWaitConnect != null) {
                                    this.mDialogCMWaitConnect.dismiss();
                                }
                                this.mHandler.sendEmptyMessage(73);
                                Trace.d(TAG, "SSC_init Camera Invalid Response");
                            }
                            if (bos2 != null) {
                                bos2.close();
                            }
                            if (bis2 != null) {
                                bis2.close();
                            }
                            return true;
                        }
                        Trace.d(TAG, "SSC_init Camera Accept");
                        try {
                            this.serverAddr = InetAddress.getByName(PhoneIp);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        this.bAcceptRun = true;
                        if (!this.bRcvThreadRun) {
                            this.bRcvThreadRun = true;
                            Trace.d(TAG, "start RcvThread");
                            this.mRcvThread.start();
                        }
                        if (this.mDialogCMWaitConnect != null) {
                            this.mDialogCMWaitConnect.dismiss();
                        }
                        isInited = true;
                        this.dscConnected = true;
                        Trace.d(TAG, "SSC_Init() ended dscConnected : " + this.dscConnected);
                        if (bos2 != null) {
                            bos2.close();
                        }
                        if (bis2 != null) {
                            bis2.close();
                        }
                    } catch (Exception e2) {
                        e = e2;
                        bis = bis2;
                        bos = bos2;
                        e.printStackTrace();
                        Trace.d(TAG, "Camera Connection Fail : [" + CameraIp + SOAP.DELIM + portNum + "]");
                        if (bos != null) {
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        bis = bis2;
                        bos = bos2;
                        if (bos != null) {
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    bos = bos2;
                } catch (Throwable th2) {
                    th = th2;
                    bos = bos2;
                }
            } catch (Exception e4) {
                e = e4;
            }
            return isInited;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private class DeInitTask extends AsyncTask<WifiInfo, Void, Boolean> {
        private DeInitTask() {
        }

        /* synthetic */ DeInitTask(AutoShare autoShare, DeInitTask deInitTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(WifiInfo... params) throws Throwable {
            try {
                AutoShare.this.SSC_DeInit(AutoShare.this.mWifiInfo);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean SSC_DeInit(WifiInfo wifiInfo) throws Throwable {
        boolean isDeInited = false;
        Trace.d(TAG, "SSC_DeInit()");
        if (wifiInfo == null) {
            wifiInfo = mWifimanager.getConnectionInfo();
        }
        if (!mWifimanager.isWifiEnabled()) {
            return false;
        }
        int ip = wifiInfo.getIpAddress();
        String PhoneIp = CommonUtils.intToIp(ip);
        Trace.d(TAG, "-----------------------------localIp : " + PhoneIp);
        DhcpInfo di = mWifimanager.getDhcpInfo();
        String CameraIp = CommonUtils.intToIp(di.serverAddress);
        Trace.d(TAG, "-----------------------------CameraIp : " + CameraIp);
        StringBuffer sb = new StringBuffer();
        sb.append("S2L/1.0 ByeBye").append(HTTP.CRLF).append("Host: SAMSUNG-S2L").append(HTTP.CRLF).append("Content-Type: text/xml;charset=utf-8").append(HTTP.CRLF).append("User-Agent: APP-TYPE").append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append("Connection: Close").append("\r\n\r\n");
        BufferedOutputStream bos = null;
        try {
            try {
                Trace.d(TAG, "CameraIp:" + CameraIp + ", CameraPort: " + this.mPortNumber);
                Socket socket = new Socket();
                SocketAddress addr = new InetSocketAddress(CameraIp, this.mPortNumber);
                socket.connect(addr, 3000);
                BufferedOutputStream bos2 = new BufferedOutputStream(socket.getOutputStream());
                try {
                    bos2.write(sb.toString().getBytes());
                    bos2.flush();
                    Trace.d(TAG, sb.toString());
                    if (bos2 != null) {
                        bos2.close();
                    }
                    this.dscConnected = false;
                    isDeInited = true;
                    Trace.d(TAG, "SSC_DeInit() ended dscConnected : " + this.dscConnected);
                    if (bos2 != null) {
                        bos2.close();
                    }
                    bos = bos2;
                } catch (Exception e) {
                    e = e;
                    bos = bos2;
                    e.printStackTrace();
                    if (bos != null) {
                        bos.close();
                    }
                } catch (Throwable th) {
                    th = th;
                    bos = bos2;
                    if (bos != null) {
                        bos.close();
                    }
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
            }
            return isDeInited;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private class InitTask extends AsyncTask<WifiInfo, Void, Boolean> {
        private InitTask() {
        }

        /* synthetic */ InitTask(AutoShare autoShare, InitTask initTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Trace.d(AutoShare.TAG, "InitTask onPreExecute()");
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(WifiInfo... params) {
            Trace.d(AutoShare.TAG, "InitTask doInBackground()");
            for (int portNum = 801; portNum < 804; portNum++) {
                try {
                    if (AutoShare.this.SSC_Init(portNum)) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            Trace.d(AutoShare.TAG, "InitTask onPostExecute()");
            if (AutoShare.this.mDialogCMWaitConnect != null) {
                AutoShare.this.mDialogCMWaitConnect.dismiss();
                AutoShare.this.mDialogCMWaitConnect = null;
            }
            super.onPostExecute(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SSC_GpsSend() throws Throwable {
        DhcpInfo di = mWifimanager.getDhcpInfo();
        String CameraIp = CommonUtils.intToIp(di.serverAddress);
        Trace.d(TAG, "SSC_GpsSend()");
        StringBuffer sb = new StringBuffer();
        sb.append("S2L/1.0 Request").append(HTTP.CRLF).append("Host-Gps : ").append(getGpsInfo()).append("\r\n\r\n");
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            try {
                Trace.d(TAG, "Camera Connection Try... : [" + CameraIp + SOAP.DELIM + this.mPortNumber + "]");
                Socket socket = new Socket();
                SocketAddress addr = new InetSocketAddress(CameraIp, this.mPortNumber);
                socket.connect(addr, 2000);
                BufferedOutputStream bos2 = new BufferedOutputStream(socket.getOutputStream());
                try {
                    Trace.d(TAG, sb.toString());
                    bos2.write(sb.toString().getBytes());
                    bos2.flush();
                    Trace.d(TAG, "Performance Check Point : Discovery Request.");
                    BufferedInputStream bis2 = new BufferedInputStream(socket.getInputStream());
                    try {
                        String str = "";
                        byte[] buf = new byte[256];
                        Trace.d(TAG, "Camera Response Waiting...");
                        do {
                            int len = bis2.read(buf, 0, buf.length);
                            if (len == -1) {
                                break;
                            } else {
                                str = String.valueOf(str) + new String(buf, 0, len);
                            }
                        } while (str.indexOf("\n") == -1);
                        Trace.d(TAG, "Performance Check Point : SSC_GpsSend Camera Response Received.");
                        Trace.d(TAG, str.toString());
                        if (bos2 != null) {
                            bos2.close();
                        }
                        if (bis2 != null) {
                            bis2.close();
                        }
                        bis = bis2;
                        bos = bos2;
                    } catch (Exception e) {
                        e = e;
                        bis = bis2;
                        bos = bos2;
                        e.printStackTrace();
                        if (bos != null) {
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        bis = bis2;
                        bos = bos2;
                        if (bos != null) {
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    bos = bos2;
                } catch (Throwable th2) {
                    th = th2;
                    bos = bos2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private class GpsTask extends AsyncTask<Void, Void, Boolean> {
        private GpsTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Trace.d(AutoShare.TAG, "GpsTask onPreExecute()");
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) throws Throwable {
            Trace.d(AutoShare.TAG, "GpsTask doInBackground()");
            try {
                AutoShare.this.SSC_GpsSend();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            Trace.d(AutoShare.TAG, "GpsTask onPostExecute()");
            super.onPostExecute(result);
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService
    public void exitAfterSendByeBye(String reason) {
        if (this.dscConnected) {
            this.sscTaskEx = new DeInitTask(this, null);
            this.sscTaskEx.execute(this.mWifiInfo);
            Trace.d(TAG, " ================== SSC ByeBye Message Send Complete. ==================");
        }
        if (!this.fileQueue.isEmpty()) {
            this.mReceivedFilePath = this.fileQueue.poll();
            File file = new File(this.mReceivedFilePath);
            if (file.exists()) {
                file.delete();
            }
            Trace.d(TAG, "exitAfterSendByeBye  exit during downloaing image file");
        }
        Trace.d(TAG, "exitAfterSendByeBye, send message App close beacuseof : " + reason);
        Message msg = this.mHandler.obtainMessage(24, reason);
        this.mHandler.sendMessageDelayed(msg, 500L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.samsungimaging.connectionmanager.app.BaseGalleryActivity
    public void exit() {
        finishSafe(Const.ExitReason.EXIT_PRESSED);
        super.exit();
    }

    public void finishSafe(String reason) {
        setService(CommonPushService.Service.NONE);
        this.bAcceptRun = false;
        this.bRcvThreadRun = false;
        if (this.mDialogCMWaitConnect != null) {
            this.mDialogCMWaitConnect.dismiss();
            this.mDialogCMWaitConnect = null;
        }
        this.nm.cancel(1);
        this.nm.cancel(2);
        if (this.sscTask != null) {
            this.sscTask.cancel(true);
        }
        if (this.sscTaskEx != null) {
            this.sscTaskEx.cancel(true);
        }
        if (this.mRcvThread != null) {
            Trace.d(TAG, "finishSafe interrupt RcvThread");
            this.mRcvThread.quit();
            this.mRcvThread.interrupt();
            if (this.mRcvThread.isAlive()) {
                try {
                    Trace.d(TAG, "finishSafe RcvThread Alive wait ");
                    SystemClock.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.mRcvThread = null;
        }
        try {
            unregisterReceiver(this.mWifiStateChangedReceiverForAppClose);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Trace.d(TAG, "AutoShare is finishing...");
        if (Const.ExitReason.BYEBYE_STANDBY.equals(reason)) {
            Trace.i(TAG, "finishSafe set CMService restart search true");
            CMService.getInstance().beforefinish(1);
        } else if (Const.ExitReason.LOW_BATTERY.equals(reason)) {
            Trace.i(TAG, "finishSafe set CMService with Low Battery");
            CMService.getInstance().beforefinish(2);
        } else {
            Trace.i(TAG, "finishSafe set CMService restart search false");
            CMService.getInstance().beforefinish(0);
        }
        finish();
    }

    private String getGpsInfo() {
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
            this.mGpsInfo = str;
        } else {
            str = "none";
            this.mGpsInfo = null;
        }
        Trace.d(TAG, "getGpsInfo : " + str);
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String setGpsValue(Location location) {
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
        this.Latitude = latitude;
        this.Longitude = longitude;
        String gpsValue = String.valueOf(this.Latitude) + latitudeValue + "X" + this.Longitude + longitudeValue;
        return gpsValue;
    }

    public String toString() {
        return "AutoShare";
    }
}

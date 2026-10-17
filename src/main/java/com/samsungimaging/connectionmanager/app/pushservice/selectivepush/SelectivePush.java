package com.samsungimaging.connectionmanager.app.pushservice.selectivepush;

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
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
public class SelectivePush extends CommonPushService {
    private static final int NOTI_RECEIVE_DONE = 1;
    private static final int NOTI_RECEIVE_PROGRESS = 2;
    private static final int SERVER_PORT = 18100;
    private static int mResolutionSize;
    BufferedInputStream bis;
    BufferedOutputStream bos;
    private RemoteViews contentView;
    private RemoteViews contentView_prog;
    private WifiInfo mWifiInfo;
    private NotificationManager nm;
    private Notification noti;
    private Notification noti_prog;
    private PendingIntent pending;
    private PendingIntent pending_prog;
    private static final Trace.Tag TAG = Trace.Tag.SP;
    private static boolean configChanged = false;
    private static int updateInterval = 0;
    private static WifiManager mWifimanager = null;
    private static int save_cnt = 0;
    private ServerSocket svSock = null;
    private InetAddress serverAddr = null;
    private RcvThread mRcvThread = null;
    private String mCreateFilePath = null;
    private String mReceivedFilePath = null;
    private int mCameraPort = 8100;
    private int image_file_idx = 0;
    private boolean bAcceptRun = false;
    private boolean bRcvThreadRun = false;
    private InitTask sscTask = null;
    private DeInitTask sscTaskEx = null;
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
    private boolean bReject = false;
    private WifiStateChangedReceiverForAppClose mWifiStateChangedReceiverForAppClose = null;
    private SdcardStateChangedReceiver sdcardStateChangedReceiver = new SdcardStateChangedReceiver(this, null);
    private ConnectionWaitDialog mDialogCMWaitConnect = null;
    private PowerManager mPowerManager = null;
    private PowerManager.WakeLock mWakeLock = null;
    private boolean isGetCommand = false;
    Handler mHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.AP_CONNECTED");
                    SelectivePush.this.registerDisconnectReceiver();
                    SelectivePush.this.mWifiInfo = SelectivePush.mWifimanager.getConnectionInfo();
                    int ipAddress = SelectivePush.this.mWifiInfo.getIpAddress();
                    Trace.d(SelectivePush.TAG, "ip:" + ipAddress + " -> " + CommonUtils.intToIp(ipAddress));
                    if (SelectivePush.mWifimanager.isWifiEnabled() && !SelectivePush.this.dscConnected) {
                        Trace.d(SelectivePush.TAG, "wifi was already connected");
                        SelectivePush.this.sscTask = new InitTask(SelectivePush.this, null);
                        SelectivePush.this.sscTask.execute(SelectivePush.this.mWifiInfo);
                        Trace.d(SelectivePush.TAG, "InitTask called  1");
                        break;
                    }
                    break;
                case 22:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.APP_CLOSE_FOR_REMOVED_SD");
                    SelectivePush.this.showDialog(1003);
                    break;
                case 24:
                    String exitReason = (String) msg.obj;
                    Trace.d(SelectivePush.TAG, "APP_CLOSED with  : " + exitReason);
                    SelectivePush.this.finishSafe(exitReason);
                    break;
                case 36:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.CANCEL_SAVING");
                    CustomProgressDialog mProgressDialog = (CustomProgressDialog) SelectivePush.this.mDialogList.get(1000);
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    SelectivePush.this.nm.cancel(1);
                    SelectivePush.this.nm.cancel(2);
                    break;
                case 51:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.IMAGE_RCV_DONE");
                    if (!SelectivePush.this.fileQueue.isEmpty()) {
                        SelectivePush.this.mReceivedFilePath = (String) SelectivePush.this.fileQueue.poll();
                        Trace.d(SelectivePush.TAG, "SSC Thumbnail [" + SelectivePush.save_cnt + "] change. path:" + SelectivePush.this.mReceivedFilePath);
                    }
                    SelectivePush.save_cnt++;
                    Trace.d(SelectivePush.TAG, "IMAGE_RCV_DONE [" + SelectivePush.save_cnt + "] change. path:" + SelectivePush.this.mReceivedFilePath);
                    if (SelectivePush.this.isBackGround) {
                        SelectivePush.this.noti.defaults |= 1;
                        String.format(SelectivePush.this.getResources().getString(R.string.files_received), Integer.valueOf(SelectivePush.save_cnt));
                        SelectivePush.this.nm.notify(1, SelectivePush.this.noti);
                        SelectivePush.this.nm.cancel(2);
                    }
                    break;
                case 52:
                    SelectivePush.this.bAcceptRun = false;
                    for (int i = 0; i < 5; i++) {
                        SelectivePush.this.rcv_on[i] = false;
                    }
                    SelectivePush.this.showDialog(1006);
                    Trace.d(SelectivePush.TAG, "wifi disconnected!!!!!, send message app close after 1000ms");
                    SelectivePush.this.mHandler.sendEmptyMessageDelayed(24, 1000L);
                    break;
                case 53:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.IMAGE_RCV_WAIT");
                    break;
                case 62:
                    CustomProgressDialog mProgressDialog2 = (CustomProgressDialog) SelectivePush.this.mDialogList.get(1000);
                    if (mProgressDialog2 != null && mProgressDialog2.isShowing()) {
                        mProgressDialog2.setProgress(msg.arg2);
                    }
                    if (SelectivePush.this.bDownloading()) {
                        SelectivePush.this.contentView_prog.setProgressBar(R.id.push_status_progress, 100, SelectivePush.this.g_progress_percent, false);
                        SelectivePush.this.nm.notify(2, SelectivePush.this.noti_prog);
                        SelectivePush.this.pre_progress_percent = SelectivePush.this.g_progress_percent;
                    }
                    break;
                case 63:
                    CustomProgressDialog mProgressDialog3 = (CustomProgressDialog) SelectivePush.this.mDialogList.get(1000);
                    if ((mProgressDialog3 == null || !mProgressDialog3.isShowing()) && SelectivePush.this.getService() != CommonPushService.Service.MOBILEBACKUP) {
                        Trace.d(SelectivePush.TAG, "Show mProgressDialog");
                        SelectivePush.this.showDialog(1000);
                        ((CustomProgressDialog) SelectivePush.this.mDialogList.get(1000)).setMax(msg.arg1);
                    }
                    SelectivePush.this.fileQueue.offer(SelectivePush.this.mCreateFilePath);
                    Trace.d(SelectivePush.TAG, "SSC Thumbnail [" + SelectivePush.save_cnt + "] add. path:" + SelectivePush.this.mCreateFilePath);
                    break;
                case 64:
                    SelectivePush.this.mGallery.setState(GalleryFragment.State.NORMAL, new Object[0]);
                    CustomProgressDialog mProgressDialog4 = (CustomProgressDialog) SelectivePush.this.mDialogList.get(1000);
                    if (mProgressDialog4 != null) {
                        mProgressDialog4.setProgress(msg.arg1);
                        mProgressDialog4.dismiss();
                    }
                    if (SelectivePush.this.bDownloading()) {
                        SelectivePush.this.contentView_prog.setProgressBar(R.id.push_status_progress, 100, 100, false);
                        SelectivePush.this.nm.notify(2, SelectivePush.this.noti_prog);
                        SelectivePush.this.pre_progress_percent = SelectivePush.this.g_progress_percent;
                    }
                    break;
                case 71:
                    Trace.d(SelectivePush.TAG, "Const.MsgId.BACK_BUTTON_INVALID");
                    break;
                case 72:
                    Toast.makeText(SelectivePush.this.mContext, R.string.ml_apsettingfail, 0).show();
                    Trace.d(SelectivePush.TAG, "Const.MsgId.WIFI_CONNECTION_IS_BAD");
                    SelectivePush.this.mHandler.sendEmptyMessageDelayed(24, 3000L);
                    break;
                case 73:
                    Bundle args = new Bundle();
                    args.putString("error", CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_401_ERROR);
                    SelectivePush.this.showDialog(1007, args);
                    break;
                case 74:
                    Bundle args2 = new Bundle();
                    args2.putString("error", CMConstants.EXTRA_VALUE_CHECKED_THE_CHECK_FOR_503_ERROR);
                    SelectivePush.this.showDialog(1007, args2);
                    break;
            }
        }
    };
    int socket_idx = 0;
    int svSockReceiveBufferSize = 0;
    Socket clientSocket = null;

    public class RcvThread extends Thread {
        public RcvThread() {
        }

        public void quit() {
            if (SelectivePush.this.svSock != null) {
                try {
                    SelectivePush.this.svSock.close();
                    Trace.d(SelectivePush.TAG, "Server Socket close");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                try {
                    Trace.i(SelectivePush.TAG, "SSC RcvThread Run...");
                    SelectivePush.this.svSock = new ServerSocket(SelectivePush.SERVER_PORT, 0, SelectivePush.this.serverAddr);
                    Trace.i(SelectivePush.TAG, "SSC RcvThread Server Socket Created.");
                    SelectivePush.this.socket_idx = 0;
                    while (SelectivePush.this.bAcceptRun) {
                        SelectivePush.this.socket_idx = SelectivePush.this.getAvailSock();
                        if (SelectivePush.this.socket_idx != -1) {
                            Trace.i(SelectivePush.TAG, "SSC RcvThread Server Socket Waiting...");
                            Socket client = SelectivePush.this.svSock.accept();
                            Trace.i(SelectivePush.TAG, "SSC RcvThread Server Socket Acept.");
                            Trace.i(SelectivePush.TAG, "socket_idx1: " + SelectivePush.this.socket_idx);
                            if (isInterrupted()) {
                                Trace.d(SelectivePush.TAG, "RcvThread interrupted");
                                break;
                            }
                            SelectivePush.this.rcv_on[SelectivePush.this.socket_idx] = true;
                            SelectivePush.this.svSockReceiveBufferSize = SelectivePush.this.svSock.getReceiveBufferSize();
                            Trace.d(SelectivePush.TAG, "svSock.getReceiveBufferSize() : " + SelectivePush.this.svSock.getReceiveBufferSize());
                            if (SelectivePush.this.mWakeLock != null) {
                                SelectivePush.this.mWakeLock.acquire();
                            }
                            ImageDownloader imageDownloader = SelectivePush.this.new ImageDownloader(client, SelectivePush.this.socket_idx);
                            SelectivePush.this.waitPreviousDownloading();
                            imageDownloader.start();
                            if (SelectivePush.this.mWakeLock != null && SelectivePush.this.mWakeLock.isHeld()) {
                                SelectivePush.this.mWakeLock.release();
                            }
                        } else {
                            Trace.d(SelectivePush.TAG, "No available socket");
                        }
                    }
                    SelectivePush.this.svSock.close();
                    Trace.d(SelectivePush.TAG, "Server Socket close");
                    try {
                        SelectivePush.this.svSock.close();
                        Trace.d(SelectivePush.TAG, "Server Socket close");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        SelectivePush.this.svSock.close();
                        Trace.d(SelectivePush.TAG, "Server Socket close");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                Trace.d(SelectivePush.TAG, "RcvThread exit run");
            } catch (Throwable th) {
                try {
                    SelectivePush.this.svSock.close();
                    Trace.d(SelectivePush.TAG, "Server Socket close");
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
            ret_str = "HTTP/1.1 200 OK\r\nContent-Length: 0\r\nSub-ErrorCode: " + err_code + "\r\n\r\n";
        } else {
            ret_str = "HTTP/1.1 500 Internal Server Error\r\nSub-ErrorCode: " + err_code + "\r\n\r\n";
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
            Trace.d(SelectivePush.TAG, "startTimer");
            this.mTimer = new TimerTask() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.ImageDownloader.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    Trace.d(SelectivePush.TAG, "Blocking....");
                    SelectivePush.this.rcv_on[ImageDownloader.this.sock_idx] = false;
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
                        Trace.d(SelectivePush.TAG, "IOException 03");
                        e.printStackTrace();
                    }
                }
            };
            this.timer.schedule(this.mTimer, 1000L);
        }

        private void stopTimer() {
            Trace.d(SelectivePush.TAG, "stopTimer");
            this.mTimer.cancel();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void start() {
            int length = 512;
            long receiving_size = 0;
            try {
                try {
                    byte[] buf = new byte[102400];
                    boolean bHeaderParseCompleted = false;
                    Trace.d(SelectivePush.TAG, "ImageDownloader Start, bAcceptRun : " + SelectivePush.this.bAcceptRun);
                    try {
                        if (!SelectivePush.this.bAcceptRun) {
                            SelectivePush.this.rcv_on[this.sock_idx] = false;
                            return;
                        }
                        try {
                            try {
                                this.dis = new DataInputStream(this.inStream);
                                this.dos = new DataOutputStream(this.outStream);
                                int idx = 0;
                                long file_size = 0;
                                int roop_Cnt = 0;
                                SelectivePush.this.isHeaderError = false;
                                SelectivePush.this.isFileSaveError = false;
                                Trace.d(SelectivePush.TAG, "socket isConnected : " + this.socket.isConnected());
                                while (true) {
                                    int num = this.dis.read(buf, 0, length);
                                    if (num == -1 || !SelectivePush.this.bAcceptRun) {
                                        break;
                                    }
                                    if (!bHeaderParseCompleted) {
                                        while (idx <= num - 4) {
                                            if (buf[idx] == 13 && buf[idx + 1] == 10 && buf[idx + 2] == 13 && buf[idx + 3] == 10) {
                                                bHeaderParseCompleted = true;
                                                Trace.d(SelectivePush.TAG, "Performance Check Point : Header Received");
                                                String header = new String(buf, 0, idx);
                                                Trace.d(SelectivePush.TAG, "header : " + header);
                                                HeaderParser headerParser = new HeaderParser(header);
                                                if (headerParser.getHeaderParsed() == 2) {
                                                    break;
                                                }
                                                if (headerParser.getHeaderParsed() != 3) {
                                                    SelectivePush.this.mCreateFilePath = CommonUtils.renameFile(CommonUtils.getDefaultStorage(), headerParser.getFileName());
                                                    SelectivePush.this.mCreateFilePath = String.valueOf(CommonUtils.getDefaultStorage()) + SelectivePush.this.mCreateFilePath;
                                                    Trace.d(SelectivePush.TAG, "Create File Path : " + SelectivePush.this.mCreateFilePath);
                                                    this.mReceivingFile = new File(SelectivePush.this.mCreateFilePath);
                                                    this.fos = new FileOutputStream(this.mReceivingFile);
                                                    file_size = Long.parseLong(headerParser.getContentLength());
                                                    Trace.d(SelectivePush.TAG, "file_size : " + file_size);
                                                    if (file_size < 1048576) {
                                                        SelectivePush.updateInterval = 10;
                                                        length = 200;
                                                    } else if (file_size < 5242880) {
                                                        SelectivePush.updateInterval = 30;
                                                        length = 1024;
                                                    } else {
                                                        SelectivePush.updateInterval = 50;
                                                        length = buf.length;
                                                    }
                                                    StringBuffer sb = new StringBuffer();
                                                    if (file_size <= CommonUtils.getAvailableExternalMemorySize() - Const.Config.CRITICAL_SD_MEMORY) {
                                                        sb.append("HTTP/1.1 100 Continue").append("\r\n\r\n");
                                                        this.dos.write(sb.toString().getBytes());
                                                        this.dos.flush();
                                                        Trace.d(SelectivePush.TAG, "Performance Check Point : HTTP/1.1 100 Continue");
                                                        SelectivePush.this.mHandler.sendMessage(SelectivePush.this.mHandler.obtainMessage(63, (int) (file_size / 1000), 0));
                                                        SelectivePush.this.mHandler.sendMessage(SelectivePush.this.mHandler.obtainMessage(62, (int) (file_size / 1000), 0));
                                                        SystemClock.sleep(300L);
                                                        this.fos.write(buf, idx + 4, (num - idx) - 4);
                                                        receiving_size = (num - idx) - 4;
                                                        Trace.d(SelectivePush.TAG, "file size first : " + receiving_size);
                                                        SelectivePush.this.mHandler.sendMessage(SelectivePush.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                                        break;
                                                    }
                                                    Trace.d(SelectivePush.TAG, "##### header check error - not enough memory !!! #####");
                                                    Trace.d(SelectivePush.TAG, "receving size : " + file_size + "      phone free size : " + CommonUtils.getAvailableExternalMemorySize());
                                                    SelectivePush.this.isFileSaveError = true;
                                                    Toast.makeText(SelectivePush.this.mContext, R.string.insufficient_memory, 0).show();
                                                    sb.append("HTTP/1.1 500 Internal Server Error").append(HTTP.CRLF).append("Sub-ErrorCode: 2502").append("\r\n\r\n");
                                                    this.dos.write(sb.toString().getBytes());
                                                    SelectivePush.this.isFileSaveError = true;
                                                    Trace.d(SelectivePush.TAG, "Performance Check Point : HTTP/1.1 500 Internal Server Error");
                                                    Trace.d(SelectivePush.TAG, "Sub-ErrorCode: 2502");
                                                    break;
                                                }
                                                SelectivePush.this.isGetCommand = true;
                                                SelectivePush.this.bReject = true;
                                                break;
                                            }
                                            idx++;
                                        }
                                        if (!bHeaderParseCompleted) {
                                            Trace.d(SelectivePush.TAG, "Can't find contents download header start");
                                            String header2 = new String(buf, 0, idx);
                                            Trace.d(SelectivePush.TAG, "header : " + header2);
                                            String mRequest = new HeaderParser(header2).getSpecificValue("Request");
                                            if (!mRequest.contains("ByeBye_Stand") && !mRequest.contains("byebye_stand")) {
                                                if (!mRequest.contains("Bye") && !mRequest.contains("bye")) {
                                                    SelectivePush.this.isHeaderError = true;
                                                    break;
                                                }
                                                Trace.d(SelectivePush.TAG, "ImageDownloader Header Parsing ByeBye, send message App close ");
                                                SelectivePush.this.mHandler.sendEmptyMessage(24);
                                                this.socket.close();
                                                this.socket.close();
                                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                                                return;
                                            }
                                            Trace.d(SelectivePush.TAG, "ImageDownloader Header Parsing ByeBye_StandBy, send message App close with standby");
                                            Message msg = SelectivePush.this.mHandler.obtainMessage(24, Const.ExitReason.BYEBYE_STANDBY);
                                            SelectivePush.this.mHandler.sendMessage(msg);
                                            this.socket.close();
                                            this.socket.close();
                                            SelectivePush.this.rcv_on[this.sock_idx] = false;
                                            SelectivePush.this.rcv_on[this.sock_idx] = false;
                                            return;
                                        }
                                    } else {
                                        this.fos.write(buf, 0, num);
                                        receiving_size += (long) num;
                                        SelectivePush.this.g_progress_percent = (int) ((100 * receiving_size) / file_size);
                                        if (roop_Cnt % SelectivePush.updateInterval == 0) {
                                            Message msg2 = SelectivePush.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000));
                                            SelectivePush.this.mHandler.sendMessage(msg2);
                                        }
                                        roop_Cnt++;
                                    }
                                    if (SelectivePush.this.isGetCommand) {
                                        StringBuffer sb2 = new StringBuffer();
                                        try {
                                            sb2.append("HTTP/1.1 200 OK").append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append("Sub-ErrorCode: 0").append("\r\n\r\n");
                                            this.dos.write(sb2.toString().getBytes());
                                            this.dos.flush();
                                            SelectivePush.this.mHandler.sendEmptyMessage(24);
                                            break;
                                        } catch (IOException err) {
                                            err.printStackTrace();
                                            Trace.d(SelectivePush.TAG, "sNp command = Getout  IOException: " + err.toString());
                                            break;
                                        }
                                    }
                                    if (receiving_size >= file_size) {
                                        SelectivePush.this.mHandler.sendMessage(SelectivePush.this.mHandler.obtainMessage(62, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                        SystemClock.sleep(200L);
                                        SelectivePush.this.mHandler.sendMessage(SelectivePush.this.mHandler.obtainMessage(64, (int) (file_size / 1000), (int) (receiving_size / 1000)));
                                        Trace.d(SelectivePush.TAG, "Performance Check Point : file receive complete !!!");
                                        SelectivePush.this.image_file_idx++;
                                        Trace.d(SelectivePush.TAG, "image file save num : " + SelectivePush.this.image_file_idx);
                                        this.dos.write(SelectivePush.this.make_resp("0"));
                                        break;
                                    }
                                }
                                if (SelectivePush.this.isGetCommand) {
                                    this.socket.close();
                                    if (this.socket.isClosed()) {
                                        Trace.d(SelectivePush.TAG, "sNp : socket close");
                                    } else {
                                        this.socket.close();
                                    }
                                    this.socket.close();
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    return;
                                }
                                this.fos.close();
                                if (receiving_size < file_size) {
                                    SelectivePush.this.isFileSaveError = true;
                                }
                                Trace.d(SelectivePush.TAG, "rcv_on[" + this.sock_idx + "] is false");
                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                                if (SelectivePush.this.isHeaderError) {
                                    this.dos.write(SelectivePush.this.make_resp(Const.ErrCode.SSC_ERROR_HEADER_OMISSION));
                                    Trace.d(SelectivePush.TAG, "Performance Check Point : Header Error");
                                    this.socket.close();
                                    SelectivePush.this.mHandler.sendEmptyMessage(36);
                                    if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                        Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                        this.mReceivingFile.delete();
                                    }
                                    this.socket.close();
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    return;
                                }
                                if (!SelectivePush.this.isFileSaveError) {
                                    Trace.d(SelectivePush.TAG, "receive complete!");
                                    SelectivePush.this.updateMediaContent(SelectivePush.this.mCreateFilePath);
                                    SelectivePush.this.mHandler.sendEmptyMessage(51);
                                    this.socket.close();
                                    Trace.d(SelectivePush.TAG, "file write complete");
                                    this.socket.close();
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                                    return;
                                }
                                this.dos.write(SelectivePush.this.make_resp(Const.ErrCode.SSC_ERROR_FILE_SAVE_FAIL));
                                Trace.d(SelectivePush.TAG, "Performance Check Point : File Save Error");
                                this.socket.close();
                                SelectivePush.this.mHandler.sendEmptyMessage(36);
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                this.socket.close();
                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Trace.d(SelectivePush.TAG, "DownloadOnSDcard  FileNotFoundException: " + e.toString());
                                this.dos.write(SelectivePush.this.make_resp(Const.ErrCode.SSC_ERROR_FILE_OPEN_FAIL));
                                Trace.d(SelectivePush.TAG, "Performance Check Point : File Not Found");
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                SelectivePush.this.mHandler.sendEmptyMessage(36);
                                this.socket.close();
                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                this.dos.write(SelectivePush.this.make_resp(Const.ErrCode.SSC_ERROR_UNKNOWN));
                                Trace.d(SelectivePush.TAG, "Performance Check Point : Unknown Exception");
                                if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                    Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                    this.mReceivingFile.delete();
                                }
                                SelectivePush.this.mHandler.sendEmptyMessage(36);
                                this.socket.close();
                                SelectivePush.this.rcv_on[this.sock_idx] = false;
                            }
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            Trace.d(SelectivePush.TAG, "DownloadOnSDcard  IOException: " + e3.toString());
                            try {
                                if (this.fos != null) {
                                    this.fos.close();
                                }
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                            if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                                Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                                this.mReceivingFile.delete();
                            }
                            SelectivePush.this.mHandler.sendEmptyMessage(36);
                            this.socket.close();
                            SelectivePush.this.rcv_on[this.sock_idx] = false;
                        }
                    } catch (Throwable th) {
                        this.socket.close();
                        SelectivePush.this.rcv_on[this.sock_idx] = false;
                        throw th;
                    }
                } catch (Exception e5) {
                    e5.printStackTrace();
                    try {
                        if (this.fos != null) {
                            this.fos.close();
                        }
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                    if (this.mReceivingFile != null && this.mReceivingFile.exists()) {
                        Trace.d(SelectivePush.TAG, "ImageDownloader Cancel Saving File Delete : " + this.mReceivingFile.getName());
                        this.mReceivingFile.delete();
                    }
                    Trace.d(SelectivePush.TAG, "ImageDownloader  Exception: " + e5.toString());
                    try {
                        this.dos.write(SelectivePush.this.make_resp(Const.ErrCode.SSC_ERROR_SOCKET_CREATE_FAIL));
                        Trace.d(SelectivePush.TAG, "Performance Check Point : Socket Create Fail");
                        this.socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    SelectivePush.this.mHandler.sendEmptyMessage(36);
                    SelectivePush.this.rcv_on[this.sock_idx] = false;
                }
            } catch (Throwable th2) {
                SelectivePush.this.rcv_on[this.sock_idx] = false;
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
        Trace.d(TAG, "isBackGround true");
        if (this.isHomeKeyPressed) {
            String.format(getResources().getString(R.string.files_received), Integer.valueOf(save_cnt));
            this.nm.notify(1, this.noti);
            this.isHomeKeyPressed = false;
        }
    }

    @Override // com.samsungimaging.connectionmanager.app.pushservice.common.CommonPushService, com.samsungimaging.connectionmanager.app.BaseGalleryActivity, com.samsungimaging.connectionmanager.app.BaseActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String service_type = getIntent().getStringExtra(CMConstants.INTENT_EXTRA_NAME_SELECTIVEPUSH_SERVICE_TYPE);
        if (service_type.equalsIgnoreCase(CMConstants.MsgString.MOBILEBACKUP_STRING)) {
            setService(CommonPushService.Service.MOBILEBACKUP);
        } else {
            setService(CommonPushService.Service.SELECTIVEPUSH);
        }
        Trace.d(TAG, "oncreate : " + service_type);
        mWifimanager = (WifiManager) getSystemService("wifi");
        setContentView();
        this.mContext = this;
        this.nm = (NotificationManager) getSystemService("notification");
        this.noti = new Notification(R.drawable.push_indicator_send_completion, "Mobile Link", System.currentTimeMillis());
        this.noti.flags |= 16;
        Intent intent = new Intent(getApplicationContext(), (Class<?>) Main.class);
        intent.addFlags(872415232);
        this.contentView = new RemoteViews(getPackageName(), R.layout.push_noti_indicator);
        this.noti.contentView = this.contentView;
        this.pending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 134217728);
        this.noti.contentIntent = this.pending;
        this.noti_prog = new Notification(R.drawable.push_indicator_down, "Mobile Link", System.currentTimeMillis());
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
        File file_path = new File(CommonUtils.getDefaultStorage());
        if (!file_path.exists() && !file_path.isDirectory()) {
            file_path.mkdirs();
        }
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
        Trace.d(TAG, "onConfigurationChanged() newConfig.orientation : " + newConfig.orientation);
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
                    this.mDialogCMWaitConnect.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.2
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            switch (keyCode) {
                                case 4:
                                    Trace.d(SelectivePush.TAG, "back key pressed, send app close message");
                                    SelectivePush.this.mHandler.sendEmptyMessageDelayed(24, 500L);
                                    break;
                            }
                            return false;
                        }
                    });
                    this.mDialogCMWaitConnect.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            Trace.d(SelectivePush.TAG, "Connection Wait Dialog is close, dscConnected is " + SelectivePush.this.dscConnected);
                            SelectivePush.this.dscConnected = true;
                            SelectivePush.this.exitAfterSendByeBye(Const.ExitReason.CANCELED);
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
                custom.setNeutralButton(R.string.cm_confirm, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Trace.d(SelectivePush.TAG, "Camera service is rejected, closing...");
                        SelectivePush.this.mHandler.sendEmptyMessage(24);
                        dialog.dismiss();
                    }
                });
                custom.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pushservice.selectivepush.SelectivePush.5
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {
                        if (event.getKeyCode() != 4) {
                            return false;
                        }
                        SelectivePush.this.mHandler.sendEmptyMessage(24);
                        dialogInterface.dismiss();
                        return true;
                    }
                });
                return custom;
        }
    }

    private void registerReceiver() {
        IntentFilter sdcardFilter = new IntentFilter("android.intent.action.MEDIA_REMOVED");
        sdcardFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        sdcardFilter.addDataScheme("file");
        registerReceiver(this.sdcardStateChangedReceiver, sdcardFilter);
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        telephonyManager.listen(phoneListener, 32);
    }

    private class SdcardStateChangedReceiver extends BroadcastReceiver {
        private SdcardStateChangedReceiver() {
        }

        /* synthetic */ SdcardStateChangedReceiver(SelectivePush selectivePush, SdcardStateChangedReceiver sdcardStateChangedReceiver) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Trace.e(SelectivePush.TAG, "Sdcard onReceive() -- " + intent.getAction());
            if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED") || intent.getAction().equals("android.intent.action.MEDIA_SHARED") || intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL") || intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTABLE")) {
                Message newMsg = new Message();
                newMsg.what = 22;
                SelectivePush.this.mHandler.sendMessageDelayed(newMsg, 1000L);
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

        /* synthetic */ WifiStateChangedReceiverForAppClose(SelectivePush selectivePush, WifiStateChangedReceiverForAppClose wifiStateChangedReceiverForAppClose) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra("connected", false);
                if (!connected && SelectivePush.this.wifi_stat_conn) {
                    SelectivePush.this.wifi_stat_conn = false;
                    SelectivePush.this.dscConnected = false;
                    SelectivePush.this.mHandler.sendEmptyMessage(52);
                }
                return;
            }
            if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo.DetailedState state = info.getDetailedState();
                Trace.d(SelectivePush.TAG, "NetworkInfo.DetailedState = " + state.name());
                switch ($SWITCH_TABLE$android$net$NetworkInfo$DetailedState()[state.ordinal()]) {
                    case 4:
                        SelectivePush.this.wifi_stat_conn = true;
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTED");
                        break;
                    case 5:
                        SelectivePush.this.wifi_stat_conn = true;
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - CONNECTING");
                        break;
                    case 6:
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTED");
                        Trace.d(SelectivePush.TAG, "wifi_stat_conn : " + SelectivePush.this.wifi_stat_conn);
                        if (SelectivePush.this.wifi_stat_conn) {
                            SelectivePush.this.wifi_stat_conn = false;
                            SelectivePush.this.dscConnected = false;
                            SelectivePush.this.mHandler.sendEmptyMessage(52);
                        }
                        break;
                    case 7:
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - DISCONNECTING");
                        break;
                    case 8:
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - FAILED");
                        break;
                    case 9:
                    default:
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - Unknown");
                        break;
                    case 10:
                        Trace.d(SelectivePush.TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION - OBTAINING_IPADDR");
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
    public int SSC_Init(int portNum) throws IOException {
        String accessMethod;
        int initState = 0;
        Trace.d(TAG, "SSC_Init()");
        WifiInfo wifiInfo = mWifimanager.getConnectionInfo();
        if (!mWifimanager.isWifiEnabled()) {
            return 0;
        }
        int ip = 0;
        for (int cnt = 0; cnt < 5 && (ip = wifiInfo.getIpAddress()) == 0; cnt++) {
            SystemClock.sleep(1000L);
        }
        if (ip == 0) {
            this.mHandler.sendEmptyMessage(72);
            Trace.d(TAG, "SSC_Init Local Ip Address is 0");
            return 1;
        }
        String mPhoneIp = CommonUtils.intToIp(ip);
        Trace.d(TAG, "-----------------------------localIp : " + mPhoneIp);
        DhcpInfo di = mWifimanager.getDhcpInfo();
        String mCameraIp = CommonUtils.intToIp(di.serverAddress);
        Trace.d(TAG, "-----------------------------mCameraIp : " + mCameraIp);
        boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(wifiInfo.getSSID());
        if (CMInfo.getInstance().getIsNFCLaunch() && isOldCamera) {
            accessMethod = "nfc";
        } else {
            accessMethod = "manual";
        }
        Trace.d(TAG, "[SP]accessMethod : " + accessMethod);
        StringBuffer sb = new StringBuffer();
        sb.append("HEAD /sp/control HTTP/1.1").append(HTTP.CRLF).append("Host: 192.168.104.1:8100").append(HTTP.CRLF).append("User-Agent: SEC_SP_").append(wifiInfo.getMacAddress().toLowerCase()).append(HTTP.CRLF).append("Data-Server : ").append(mPhoneIp).append(":18100").append(HTTP.CRLF).append("Data-Port : ").append("18100").append(HTTP.CRLF).append("NTS : alive").append(HTTP.CRLF).append("HOST-PNumber : ").append(CommonUtils.getLineNumber(getApplicationContext())).append(HTTP.CRLF).append("Access-Method : ").append(accessMethod).append("\r\n\r\n");
        try {
            try {
                Trace.d(TAG, "mCameraIp:" + mCameraIp + ", mCameraPort : " + portNum);
                this.clientSocket = new Socket();
                SocketAddress addr = new InetSocketAddress(mCameraIp, portNum);
                this.clientSocket.connect(addr, 2000);
                Trace.d(TAG, "Connected Port Num : " + portNum);
                this.mCameraPort = portNum;
                this.bos = new BufferedOutputStream(this.clientSocket.getOutputStream());
                Trace.d(TAG, sb.toString());
                this.bos.write(sb.toString().getBytes());
                this.bos.flush();
                Trace.d(TAG, "Performance Check Point : Discovery Request.");
                this.bis = new BufferedInputStream(this.clientSocket.getInputStream());
                try {
                    this.serverAddr = InetAddress.getByName(mPhoneIp);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                this.bAcceptRun = true;
                if (!this.bRcvThreadRun) {
                    this.bRcvThreadRun = true;
                    Trace.d(TAG, "start RcvThread");
                    this.mRcvThread.start();
                }
                String str = "";
                byte[] buf = new byte[256];
                Trace.d(TAG, "Camera Response Waiting...");
                do {
                    int len = this.bis.read(buf, 0, buf.length);
                    if (len == -1) {
                        break;
                    }
                    str = String.valueOf(str) + new String(buf, 0, len);
                } while (str.indexOf("\n") == -1);
                Trace.d(TAG, "Performance Check Point : Camera Response Received.");
                Trace.d(TAG, str.toString());
                if (str.contains("200 OK")) {
                    Trace.d(TAG, "Camera has Accept");
                    initState = 1;
                    this.dscConnected = true;
                    if (this.mDialogCMWaitConnect != null) {
                        this.mDialogCMWaitConnect.dismiss();
                    }
                    Trace.d(TAG, "SSC_Init() ended dscConnected : " + this.dscConnected);
                } else if (str.contains("503 Service Unavailable")) {
                    Trace.d(TAG, "SSC_init Camera Not Accept - 503 Service Unavailable");
                    initState = -1;
                    this.mHandler.sendEmptyMessage(74);
                    this.bReject = true;
                    this.bAcceptRun = false;
                    this.bRcvThreadRun = false;
                } else if (str.contains("401 Unauthorized")) {
                    Trace.d(TAG, "SSC_init Camera Not Accept - 401 Unauthorized");
                    initState = -1;
                    this.mHandler.sendEmptyMessage(73);
                    this.bReject = true;
                    this.bAcceptRun = false;
                    this.bRcvThreadRun = false;
                } else {
                    Trace.d(TAG, "SSC_init Camera Invalid Response");
                    initState = -1;
                    this.mCameraPort = -1;
                    this.bReject = true;
                    this.bAcceptRun = false;
                    this.bRcvThreadRun = false;
                }
                try {
                    this.bos.close();
                    this.bis.close();
                    this.clientSocket.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                try {
                    this.bos.close();
                    this.bis.close();
                    this.clientSocket.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            return initState;
        } catch (Throwable th) {
            try {
                this.bos.close();
                this.bis.close();
                this.clientSocket.close();
            } catch (Exception e5) {
                e5.printStackTrace();
            }
            throw th;
        }
    }

    private class DeInitTask extends AsyncTask<WifiInfo, Void, Boolean> {
        private DeInitTask() {
        }

        /* synthetic */ DeInitTask(SelectivePush selectivePush, DeInitTask deInitTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(WifiInfo... params) {
            try {
                SelectivePush.this.SSC_DeInit(SelectivePush.this.mWifiInfo);
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
    public boolean SSC_DeInit(WifiInfo wifiInfo) throws IOException {
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
        sb.append("HEAD /sp/control HTTP/1.1").append(HTTP.CRLF).append("Host: 192.168.104.1:8100").append(HTTP.CRLF).append("User-Agent: SEC_SP_").append(wifiInfo.getMacAddress().toLowerCase()).append(HTTP.CRLF).append("Data-Server : ").append(PhoneIp).append(":18100").append(HTTP.CRLF).append("Data-Port : ").append("18100").append(HTTP.CRLF).append("NTS : byebye").append("\r\n\r\n");
        try {
            Trace.d(TAG, "CameraIp:" + CameraIp + ", CameraPort : " + this.mCameraPort);
            this.clientSocket = new Socket();
            SocketAddress addr = new InetSocketAddress(CameraIp, this.mCameraPort);
            this.clientSocket.connect(addr, 3000);
            this.bos = new BufferedOutputStream(this.clientSocket.getOutputStream());
            this.bos.write(sb.toString().getBytes());
            this.bos.flush();
            Trace.d(TAG, sb.toString());
            this.bis = new BufferedInputStream(this.clientSocket.getInputStream());
            String str = "";
            byte[] buf = new byte[256];
            do {
                int len = this.bis.read(buf, 0, buf.length);
                if (len == -1) {
                    break;
                }
                str = String.valueOf(str) + new String(buf, 0, len);
            } while (str.indexOf("\n") == -1);
            Trace.d(TAG, "Camera Response Received.");
            Trace.d(TAG, str.toString());
            if (str.contains("200 OK")) {
                Trace.d(TAG, "Camera has Accept");
                this.dscConnected = false;
                Trace.d(TAG, "SSC_DeInit() ended dscConnected : " + this.dscConnected);
            } else if (str.contains("500 Internal Server Error")) {
                Trace.d(TAG, "Camera has Not Accept");
            } else {
                Trace.d(TAG, "Camera No Responce");
            }
            this.dscConnected = false;
            isDeInited = true;
            Trace.d(TAG, "SSC_DeInit() ended dscConnected : " + this.dscConnected);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.bos.close();
                this.bis.close();
                this.clientSocket.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return isDeInited;
    }

    private class InitTask extends AsyncTask<WifiInfo, Void, Boolean> {
        private InitTask() {
        }

        /* synthetic */ InitTask(SelectivePush selectivePush, InitTask initTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Trace.d(SelectivePush.TAG, "InitTask onPreExecute()");
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(WifiInfo... params) {
            Trace.d(SelectivePush.TAG, "InitTask doInBackground()");
            for (int portNum = 8100; portNum < 8104; portNum++) {
                try {
                    if (SelectivePush.this.SSC_Init(portNum) != 0) {
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
            Trace.d(SelectivePush.TAG, "InitTask onPostExecute()");
            if (SelectivePush.this.mCameraPort != -1) {
                if (SelectivePush.this.mDialogCMWaitConnect != null) {
                    SelectivePush.this.mDialogCMWaitConnect.dismiss();
                    SelectivePush.this.mDialogCMWaitConnect = null;
                }
            } else {
                SelectivePush.this.mHandler.sendEmptyMessage(73);
            }
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
        this.isGetCommand = false;
        if (this.mRcvThread != null) {
            Trace.d(TAG, "finishSafe interrupt RcvThread");
            this.mRcvThread.quit();
            this.mRcvThread.interrupt();
            if (this.mRcvThread.isAlive()) {
                try {
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
        Trace.d(TAG, "Selective Push is finishing...");
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

    public String toString() {
        return "SelectivePush";
    }
}

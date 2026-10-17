package com.samsungimaging.connectionmanager.app.cm.modemanager;

import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.text.format.Formatter;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class ModeClient implements Runnable {
    private static ModeClient instance_ModeClient = null;
    private static ArrayList<IModeClient> mIModeClientList = new ArrayList<>();
    private String mNTS;
    private int mServerPort = 7788;
    private Thread mRunModeClient = null;
    private Socket mModeClientSocket = null;

    private ModeClient(boolean isbyebye) {
        this.mNTS = "alive";
        Trace.d(CMConstants.TAG_NAME, "ModeClient, isbyebye = " + isbyebye);
        if (isbyebye) {
            this.mNTS = CMConstants.MsgString.BYEBYE;
        } else {
            this.mNTS = "alive";
        }
    }

    public static synchronized ModeClient getInstance() {
        if (instance_ModeClient == null) {
            instance_ModeClient = new ModeClient(false);
        }
        return instance_ModeClient;
    }

    /* JADX WARN: Code duplicated, block: B:116:0x0460  */
    /* JADX WARN: Code duplicated, block: B:117:0x0469  */
    /* JADX WARN: Code duplicated, block: B:119:0x0473  */
    /* JADX WARN: Code duplicated, block: B:121:0x047d  */
    /* JADX WARN: Code duplicated, block: B:147:0x0380 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:153:0x0375 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:157:0x0370 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:70:0x038b  */
    /* JADX WARN: Code duplicated, block: B:72:0x039a  */
    @Override // java.lang.Runnable
    public void run() throws Throwable {
        int msg = 0;
        for (int try_count = 0; try_count < 16; try_count++) {
            if (CMService.mWifiManager != null) {
                WifiInfo wifiInfo = CMService.mWifiManager.getConnectionInfo();
                wifiInfo.getSSID();
                DhcpInfo di = CMService.mWifiManager.getDhcpInfo();
                int serverPort = this.mServerPort;
                String cameraIP = Formatter.formatIpAddress(di.serverAddress);
                String phoneIP = Formatter.formatIpAddress(di.ipAddress);
                Trace.d(CMConstants.TAG_NAME, "ModeClient, cameraIP = " + cameraIP);
                Trace.d(CMConstants.TAG_NAME, "ModeClient, phoneIP = " + phoneIP);
                Trace.d(CMConstants.TAG_NAME, "ModeClient, ModeServer.mServerPort = " + ModeServer.mServerPort);
                String phoneNumber = CMUtil.getLineNumber(CMService.mContext);
                String mac = wifiInfo.getMacAddress().toUpperCase();
                if (!phoneNumber.equals("none")) {
                    mac = phoneNumber;
                }
                StringBuffer sb = new StringBuffer();
                sb.append("HEAD /mode/control HTTP/1.1").append(HTTP.CRLF).append("User-Agent: SEC_MODE_").append(mac).append(HTTP.CRLF).append("Connection: Close").append(HTTP.CRLF).append("NTS : ").append(this.mNTS).append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append("HOST-Mac : ").append(wifiInfo.getMacAddress().toLowerCase()).append(HTTP.CRLF).append("HOST-Address : ").append(phoneIP).append(HTTP.CRLF).append("HOST-port : ").append(serverPort).append(HTTP.CRLF).append("HOST-PNumber : ").append(phoneNumber).append(HTTP.CRLF).append("Access-Method : ").append("manual").append(HTTP.CRLF).append("CALLBACK: <http://").append(phoneIP).append(SOAP.DELIM).append(ModeServer.mServerPort).append("/eventCallback>").append(HTTP.CRLF).append(HTTP.CRLF);
                BufferedOutputStream bos = null;
                BufferedInputStream bis = null;
                try {
                    try {
                        Trace.d(CMConstants.TAG_NAME, "ModeClient, Camera Connection Try... : [" + cameraIP + SOAP.DELIM + serverPort + "]");
                        this.mModeClientSocket = new Socket();
                        SocketAddress addr = new InetSocketAddress(cameraIP, serverPort);
                        this.mModeClientSocket.connect(addr, 3000);
                        Trace.d(CMConstants.TAG_NAME, "ModeClient, Camera Connection Success... : [" + cameraIP + SOAP.DELIM + serverPort + "]");
                        BufferedOutputStream bos2 = new BufferedOutputStream(this.mModeClientSocket.getOutputStream());
                        try {
                            Trace.d(CMConstants.TAG_NAME, "ModeClient, OutputStream = " + sb.toString());
                            bos2.write(sb.toString().getBytes());
                            bos2.flush();
                            Trace.d(CMConstants.TAG_NAME, "ModeClient, Discovery Request.");
                            BufferedInputStream bis2 = new BufferedInputStream(this.mModeClientSocket.getInputStream());
                            try {
                                String str = "";
                                byte[] buf = new byte[356];
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, Camera Response Waiting...");
                                do {
                                    int len = bis2.read(buf, 0, buf.length);
                                    if (len == -1) {
                                        break;
                                    } else {
                                        str = String.valueOf(str) + new String(buf, 0, len);
                                    }
                                } while (str.indexOf("\n") == -1);
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, Camera Response Received.");
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, InputStream = " + str.toString());
                                if (bos2 != null) {
                                    bos2.close();
                                }
                                if (bis2 != null) {
                                    bis2.close();
                                }
                                if (this.mModeClientSocket != null) {
                                    this.mModeClientSocket.close();
                                }
                                msg = checkResponse(str);
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, finally!");
                                if (bos2 != null) {
                                    try {
                                        bos2.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (bis2 != null) {
                                    try {
                                        bis2.close();
                                    } catch (IOException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                                if (this.mModeClientSocket != null) {
                                    try {
                                        this.mModeClientSocket.close();
                                    } catch (IOException e3) {
                                        e3.printStackTrace();
                                    }
                                }
                                if (msg > 0) {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, DONE.");
                                    if (msg == 23) {
                                        performRunByebye();
                                        return;
                                    } else {
                                        performRunSubApplication(msg);
                                        return;
                                    }
                                }
                                if (try_count + 1 == 16) {
                                    performRunSubApplication(0);
                                } else {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, retry!");
                                }
                            } catch (ConnectException e4) {
                                bis = bis2;
                                bos = bos2;
                                try {
                                    Trace.d(Trace.Tag.CM, "delay start");
                                    Thread.sleep(500L);
                                    Trace.d(Trace.Tag.CM, "delay end");
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, finally!");
                                if (bos != null) {
                                    try {
                                        bos.close();
                                    } catch (IOException e5) {
                                        e5.printStackTrace();
                                    }
                                }
                                if (bis != null) {
                                    try {
                                        bis.close();
                                    } catch (IOException e6) {
                                        e6.printStackTrace();
                                    }
                                }
                                if (this.mModeClientSocket != null) {
                                    try {
                                        this.mModeClientSocket.close();
                                    } catch (IOException e7) {
                                        e7.printStackTrace();
                                    }
                                }
                                if (msg > 0) {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, DONE.");
                                    if (msg == 23) {
                                        performRunByebye();
                                        return;
                                    } else {
                                        performRunSubApplication(msg);
                                        return;
                                    }
                                }
                                if (try_count + 1 == 16) {
                                    performRunSubApplication(0);
                                } else {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, retry!");
                                }
                            } catch (Exception e8) {
                                e = e8;
                                bis = bis2;
                                bos = bos2;
                                e.printStackTrace();
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, Exception!");
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, finally!");
                                if (bos != null) {
                                    try {
                                        bos.close();
                                    } catch (IOException e9) {
                                        e9.printStackTrace();
                                    }
                                }
                                if (bis != null) {
                                    try {
                                        bis.close();
                                    } catch (IOException e10) {
                                        e10.printStackTrace();
                                    }
                                }
                                if (this.mModeClientSocket != null) {
                                    try {
                                        this.mModeClientSocket.close();
                                    } catch (IOException e11) {
                                        e11.printStackTrace();
                                    }
                                }
                                if (msg > 0) {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, DONE.");
                                    if (msg == 23) {
                                        performRunByebye();
                                        return;
                                    } else {
                                        performRunSubApplication(msg);
                                        return;
                                    }
                                }
                                if (try_count + 1 == 16) {
                                    performRunSubApplication(0);
                                } else {
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, retry!");
                                }
                            } catch (Throwable th) {
                                th = th;
                                bis = bis2;
                                bos = bos2;
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, finally!");
                                if (bos != null) {
                                    try {
                                        bos.close();
                                    } catch (IOException e12) {
                                        e12.printStackTrace();
                                    }
                                }
                                if (bis != null) {
                                    try {
                                        bis.close();
                                    } catch (IOException e13) {
                                        e13.printStackTrace();
                                    }
                                }
                                if (this.mModeClientSocket != null) {
                                    try {
                                        this.mModeClientSocket.close();
                                    } catch (IOException e14) {
                                        e14.printStackTrace();
                                    }
                                }
                                if (msg > 0) {
                                    if (try_count + 1 == 16) {
                                        performRunSubApplication(0);
                                        throw th;
                                    }
                                    Trace.d(CMConstants.TAG_NAME, "ModeClient, retry!");
                                    throw th;
                                }
                                Trace.d(CMConstants.TAG_NAME, "ModeClient, DONE.");
                                if (msg == 23) {
                                    performRunByebye();
                                    return;
                                } else {
                                    performRunSubApplication(msg);
                                    return;
                                }
                            }
                        } catch (ConnectException e15) {
                            bos = bos2;
                        } catch (Exception e16) {
                            e = e16;
                            bos = bos2;
                        } catch (Throwable th2) {
                            th = th2;
                            bos = bos2;
                        }
                    } catch (ConnectException e17) {
                    } catch (Exception e18) {
                        e = e18;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    Trace.d(CMConstants.TAG_NAME, "ModeClient, finally!");
                    if (bos != null) {
                        bos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                    if (this.mModeClientSocket != null) {
                        this.mModeClientSocket.close();
                    }
                    if (msg > 0) {
                        if (try_count + 1 == 16) {
                            performRunSubApplication(0);
                            throw th;
                        }
                        Trace.d(CMConstants.TAG_NAME, "ModeClient, retry!");
                        throw th;
                    }
                    Trace.d(CMConstants.TAG_NAME, "ModeClient, DONE.");
                    if (msg == 23) {
                        performRunByebye();
                        return;
                    } else {
                        performRunSubApplication(msg);
                        return;
                    }
                }
            }
        }
    }

    public void runModeClient(boolean isbyebye) {
        Trace.d(CMConstants.TAG_NAME, "ModeClient, runModeClient(), start!!!, mRunModeClient = " + this.mRunModeClient);
        if (this.mRunModeClient == null) {
            Trace.d(CMConstants.TAG_NAME, "ModeClient, runModeClient(), start!!!");
            this.mRunModeClient = new Thread(new ModeClient(isbyebye));
            this.mRunModeClient.start();
            return;
        }
        Trace.d(CMConstants.TAG_NAME, "ModeClient, runModeClient(), already started!!!");
    }

    public void stopModeClient() {
        Trace.d(CMConstants.TAG_NAME, "ModeClient, stopModeClient()");
        clearModeClientListner();
        if (this.mModeClientSocket != null) {
            try {
                this.mModeClientSocket.close();
                this.mModeClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Trace.d(CMConstants.TAG_NAME, "ModeClient, stopModeClient(), mModeClientSocket exception!!!");
            }
        }
        Trace.d(CMConstants.TAG_NAME, "ModeClient, stopModeClient(), mRunModeClient = " + this.mRunModeClient);
        if (this.mRunModeClient != null) {
            Trace.d(CMConstants.TAG_NAME, "ModeClient, stopModeClient()");
            this.mRunModeClient.interrupt();
            this.mRunModeClient = null;
        }
    }

    private int checkResponse(String request) {
        int msg = 0;
        if (request.contains("HTTP/1.1 200 OK")) {
            Trace.d(CMConstants.TAG_NAME, "ModeClient, HTTP/1.1 200 OK");
            String default_url = CMUtil.parseDescriptionURLForRVFML(request);
            Trace.d(CMConstants.TAG_NAME, "ModeClient, checkResponse, default_url = " + default_url);
            CMInfo.getInstance().setDescriptionURL(default_url);
            if (request.toLowerCase().contains(CMConstants.MsgString.MOBILELINK_STRING)) {
                msg = 1;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.REMOTEVIEWFINDER_STRING)) {
                msg = 2;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.AUTOSHARE_STRING)) {
                msg = 3;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.SELECTIVEPUSH_STRING)) {
                msg = 4;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.GROUPSHARE_STRING)) {
                msg = 5;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.MOBILEBACKUP_STRING)) {
                msg = 6;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.IDLE_STRING)) {
                msg = 7;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.BYEBYE)) {
                msg = 23;
            }
            if (request.contains("MVERSION")) {
                CMInfo.getInstance().setIsOldCamera(false);
                return msg;
            }
            CMInfo.getInstance().setIsOldCamera(true);
            return msg;
        }
        if (request.contains(CMConstants.MsgString.ERROR401)) {
            return 21;
        }
        if (!request.contains(CMConstants.MsgString.ERROR503)) {
            return 0;
        }
        return 22;
    }

    private void clearModeClientListner() {
        mIModeClientList.clear();
    }

    public void addModeClientListener(IModeClient listener) {
        Trace.d(CMConstants.TAG_NAME, "ModeClient, addModeClientListener -> " + listener);
        int listner_size = mIModeClientList.size();
        for (int i = 0; i < listner_size; i++) {
            Trace.d(CMConstants.TAG_NAME, "ModeClient, addModeClientListener -> mIModeClientList.get(" + i + ") = " + mIModeClientList.get(i));
            if (listener.equals(mIModeClientList.get(i))) {
                Trace.d(CMConstants.TAG_NAME, "ModeClient, addModeClientListener, already added.");
                return;
            }
        }
        Trace.d(CMConstants.TAG_NAME, "ModeClient, addModeClientListener, add done.");
        mIModeClientList.add(listener);
    }

    private void performRunSubApplication(int mode) {
        int listenerSize = mIModeClientList.size();
        Trace.d(CMConstants.TAG_NAME, "ModeClient, performRunSubApplication, listenerSize = " + listenerSize);
        for (int n = 0; n < listenerSize; n++) {
            IModeClient listener = mIModeClientList.get(n);
            listener.runSubApplication(mode);
        }
    }

    private void performRunByebye() {
        int listenerSize = mIModeClientList.size();
        Trace.d(CMConstants.TAG_NAME, "ModeClient, performRunByebye, listenerSize = " + listenerSize);
        for (int n = 0; n < listenerSize; n++) {
            IModeClient listener = mIModeClientList.get(n);
            listener.runByebye();
        }
    }
}

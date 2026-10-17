package com.samsungimaging.connectionmanager.app.cm.modemanager;

import android.net.DhcpInfo;
import android.text.format.Formatter;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeServer;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ModeServer implements Runnable {
    public static int mServerPort = 7789;
    private static ModeServer instance_ModeServer = null;
    private static ArrayList<IModeServer> mIModeServerList = new ArrayList<>();
    private Thread mRunModeServer = null;
    private ServerSocket mServerSocket = null;
    private InetAddress callBackServerIP = null;

    private ModeServer() {
    }

    public static synchronized ModeServer getInstance() {
        if (instance_ModeServer == null) {
            instance_ModeServer = new ModeServer();
        }
        return instance_ModeServer;
    }

    /* JADX WARN: Code duplicated, block: B:35:0x00d3 A[Catch: IOException -> 0x00f3, FINALLY_INSNS, TryCatch #2 {IOException -> 0x00f3, blocks: (B:33:0x00cf, B:35:0x00d3, B:36:0x00db, B:38:0x00e5, B:39:0x00e9), top: B:49:0x00cf }] */
    /* JADX WARN: Code duplicated, block: B:38:0x00e5 A[Catch: IOException -> 0x00f3, FINALLY_INSNS, TryCatch #2 {IOException -> 0x00f3, blocks: (B:33:0x00cf, B:35:0x00d3, B:36:0x00db, B:38:0x00e5, B:39:0x00e9), top: B:49:0x00cf }] */
    @Override // java.lang.Runnable
    public void run() {
        try {
            if (this.mServerSocket == null) {
                Trace.d(CMConstants.TAG_NAME, "ModeServer, ServerSocket created. mServerPort = " + mServerPort);
                DhcpInfo di = CMService.mWifiManager.getDhcpInfo();
                String phoneIP = Formatter.formatIpAddress(di.ipAddress);
                this.callBackServerIP = InetAddress.getByName(phoneIP);
                this.mServerSocket = new ServerSocket(mServerPort, 0, this.callBackServerIP);
                Trace.d(CMConstants.TAG_NAME, "ModeServer, ServerSocket created.");
            }
            Trace.d(CMConstants.TAG_NAME, "ModeServer, Connecting...");
            while (true) {
                Trace.d(CMConstants.TAG_NAME, "ModeServer, Request Waiting...");
                Socket client = this.mServerSocket.accept();
                Trace.d(CMConstants.TAG_NAME, "ModeServer, Request Receiving.");
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                ModeServerResponse modeServerResponseThread = new ModeServerResponse(client);
                modeServerResponseThread.start();
                try {
                    if (this.mServerSocket != null) {
                        this.mServerSocket.close();
                        this.mServerSocket = null;
                    }
                    mServerPort++;
                    if (mServerPort == 7794) {
                        mServerPort = 7789;
                    }
                    CMSharedPreferenceUtil.put(CMService.mContext, CMConstants.MODESERVER_PORT, mServerPort);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Trace.d(CMConstants.TAG_NAME, "ModeServer is inTerrupted.");
        } catch (Exception e) {
            Trace.d(CMConstants.TAG_NAME, "ModeServer, Exception02");
            e.printStackTrace();
        } finally {
            if (this.mServerSocket != null) {
                this.mServerSocket.close();
                this.mServerSocket = null;
            }
            mServerPort++;
            if (mServerPort == 7794) {
                mServerPort = 7789;
            }
            CMSharedPreferenceUtil.put(CMService.mContext, CMConstants.MODESERVER_PORT, mServerPort);
        }
    }

    public void runModeServer() {
        if (this.mRunModeServer == null) {
            Trace.d(CMConstants.TAG_NAME, "ModeServer, runModeServer(), start!!!");
            if (instance_ModeServer == null) {
                instance_ModeServer = new ModeServer();
            }
            this.mRunModeServer = new Thread(instance_ModeServer);
            this.mRunModeServer.start();
            return;
        }
        Trace.d(CMConstants.TAG_NAME, "ModeServer, runModeServer(), already started!!!");
    }

    public void stopModeServer() {
        Trace.d(CMConstants.TAG_NAME, "ModeServer, stopModeServer()");
        clearModeServerListner();
        if (this.mServerSocket != null) {
            try {
                this.mServerSocket.close();
                this.mServerSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                Trace.d(CMConstants.TAG_NAME, "ModeServer, stopModeServer(), mServerSocket exception!!!");
            }
        }
        if (this.mRunModeServer != null) {
            this.mRunModeServer.interrupt();
            this.mRunModeServer = null;
        }
    }

    private void clearModeServerListner() {
        mIModeServerList.clear();
    }

    public void addModeServerListener(IModeServer listener) {
        Trace.d(CMConstants.TAG_NAME, "ModeServer, addModeServerListener -> " + listener);
        int listner_size = mIModeServerList.size();
        for (int i = 0; i < listner_size; i++) {
            Trace.d(CMConstants.TAG_NAME, "ModeServer, addModeServerListener -> mIModeServerList.get(" + i + ") = " + mIModeServerList.get(i));
            if (listener.equals(mIModeServerList.get(i))) {
                Trace.d(CMConstants.TAG_NAME, "ModeServer, addModeServerListener, already added.");
                return;
            }
        }
        Trace.d(CMConstants.TAG_NAME, "ModeServer, addModeServerListener, add done.");
        mIModeServerList.add(listener);
    }

    public void removeModeServerListener(IModeServer listener) {
        Trace.d(CMConstants.TAG_NAME, "ModeServer, removeModeServerListener -> " + listener);
        int listner_size = mIModeServerList.size();
        for (int i = 0; i < listner_size; i++) {
            Trace.d(CMConstants.TAG_NAME, "ModeServer, removeModeServerListener -> mIModeServerList.get(" + i + ") = " + mIModeServerList.get(i));
            if (listener.equals(mIModeServerList.get(i))) {
                mIModeServerList.remove(i);
                Trace.d(CMConstants.TAG_NAME, "ModeServer, removeModeServerListener, remove done.");
                return;
            }
        }
        Trace.d(CMConstants.TAG_NAME, "ModeServer, removeModeServerListener, There is no item to be removed");
    }

    public void performRunSubApplication(int mode) {
        int listenerSize = mIModeServerList.size();
        Trace.d(CMConstants.TAG_NAME, "ModeServer, performRunSubApplication, listenerSize = " + listenerSize);
        for (int n = 0; n < listenerSize; n++) {
            IModeServer listener = mIModeServerList.get(n);
            listener.runSubApplication(mode);
        }
    }

    public void performRunByebye() {
        int listenerSize = mIModeServerList.size();
        Trace.d(CMConstants.TAG_NAME, "ModeServer, performRunByebye, listenerSize = " + listenerSize);
        for (int n = 0; n < listenerSize; n++) {
            IModeServer listener = mIModeServerList.get(n);
            listener.runByebye();
        }
    }
}

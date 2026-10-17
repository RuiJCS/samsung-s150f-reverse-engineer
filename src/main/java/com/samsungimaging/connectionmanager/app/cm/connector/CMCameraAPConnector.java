package com.samsungimaging.connectionmanager.app.cm.connector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CMCameraAPConnector {
    private static CMCameraAPConnector s_obj = null;
    Context mContext = null;
    WifiManager mWifiManager = null;
    private Connector mConnector = null;
    private ConnectorSecurity mConnectorSecurity = null;
    private List<ScanResult> mSrs = null;
    private CharSequence[] mSecurityConnectionInfo = new String[4];
    private boolean mTryToConnect = false;
    private ConnectionHandler mConnectionHandler = null;

    private CMCameraAPConnector() {
    }

    public static synchronized CMCameraAPConnector getInstance() {
        if (s_obj == null) {
            s_obj = new CMCameraAPConnector();
        }
        return s_obj;
    }

    public void setSr(List<ScanResult> sr, Context context, WifiManager wifiManager) {
        if (this.mConnectionHandler == null) {
            this.mConnectionHandler = ConnectionHandler.getInstance(context);
        }
        if (this.mContext == null) {
            this.mContext = context;
        }
        if (this.mWifiManager == null) {
            this.mWifiManager = wifiManager;
        }
        if (!CMService.IS_WIFI_CONNECTED) {
            if (this.mConnector == null) {
                Trace.d(CMConstants.TAG_NAME, "mConnector is null, new Connector()01!!!");
                this.mSrs = sr;
                this.mConnector = new Connector();
                this.mConnector.execute(null, null, null);
                return;
            }
            Trace.d(CMConstants.TAG_NAME, "mConnector is running.");
        }
    }

    public void setSr(ScanResult sr, Context context, WifiManager wifiManager) {
        if (this.mConnectionHandler == null) {
            this.mConnectionHandler = ConnectionHandler.getInstance(context);
        }
        if (this.mContext == null) {
            this.mContext = context;
        }
        if (this.mWifiManager == null) {
            this.mWifiManager = wifiManager;
        }
        if (!CMService.IS_WIFI_CONNECTED) {
            if (this.mConnector == null) {
                Trace.d(CMConstants.TAG_NAME, "mConnector is null, new Connector()02!!!");
                if (this.mSrs == null) {
                    this.mSrs = new ArrayList();
                }
                this.mSrs.add(sr);
                this.mConnector = new Connector();
                this.mConnector.execute(null, null, null);
                return;
            }
            cancel();
            if (this.mConnector == null) {
                Trace.d(CMConstants.TAG_NAME, "mConnector is null, new Connector()03!!!");
                if (this.mSrs != null) {
                    this.mSrs.clear();
                }
                this.mSrs.add(sr);
                this.mConnector = new Connector();
                this.mConnector.execute(null, null, null);
            }
        }
    }

    class Connector extends AsyncTask<Void, Void, Void> {
        Connector() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... params) {
            List<ScanResult> tempSrs = CMCameraAPConnector.this.mSrs;
            if (CMCameraAPConnector.this.mConnectionHandler == null) {
                Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : handler is null, return!");
            } else if (tempSrs != null && tempSrs.size() > 0) {
                Trace.d(CMConstants.TAG_NAME, "Performance Check Point : Checking Scan Result List to Connect AP");
                Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : doInBackground---- 1");
                for (int i = 0; i < tempSrs.size(); i++) {
                    Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : doInBackground---- 2");
                    if (!CMService.IS_WIFI_CONNECTED) {
                        Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : doInBackground---- 3, try to connect to camear : " + tempSrs.get(i).SSID);
                        CMCameraAPConnector.this.mTryToConnect = CMUtil.connectToNewCameraSoftAP(CMCameraAPConnector.this.mContext, CMCameraAPConnector.this.mWifiManager, tempSrs.get(i), CMCameraAPConnector.this.mConnectionHandler);
                        Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : doInBackground---- 4, mTryToConnect = " + CMCameraAPConnector.this.mTryToConnect);
                        SystemClock.sleep(3000L);
                        if (isCancelled() || CMService.IS_WIFI_CONNECTED) {
                            Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector : Canceled!!!, WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                            break;
                        }
                    }
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, PostExecute(), mConnector set null.");
            CMCameraAPConnector.this.mSrs = null;
            CMCameraAPConnector.this.mConnector = null;
        }
    }

    public void cancel() {
        if (this.mConnector != null) {
            Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, cancel(), mConnector is not null.");
            if (!this.mConnector.isCancelled()) {
                this.mConnector.cancel(true);
            }
            this.mConnector = null;
        }
        if (this.mConnectorSecurity != null) {
            Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, cancel(), mConnectorSecurity is not null.");
            if (!this.mConnectorSecurity.isCancelled()) {
                this.mConnectorSecurity.cancel(true);
            }
            this.mConnectorSecurity = null;
        }
    }

    public void setSr(CharSequence[] connectioninfo, Context context, WifiManager wifiManager) {
        if (this.mContext == null) {
            this.mContext = context;
        }
        if (this.mWifiManager == null) {
            this.mWifiManager = wifiManager;
        }
        if (!CMService.IS_WIFI_CONNECTED) {
            if (this.mConnectorSecurity == null) {
                Trace.d(CMConstants.TAG_NAME, "mConnectorSecurity is null, new ConnectorSecurity()01!!!");
                this.mSecurityConnectionInfo = connectioninfo;
                this.mConnectorSecurity = new ConnectorSecurity();
                this.mConnectorSecurity.execute(null, null, null);
                return;
            }
            cancel();
            if (this.mConnectorSecurity == null) {
                Trace.d(CMConstants.TAG_NAME, "mConnectorSecurity is null, new ConnectorSecurity()02!!!");
                this.mSecurityConnectionInfo = connectioninfo;
                this.mConnectorSecurity = new ConnectorSecurity();
                this.mConnectorSecurity.execute(null, null, null);
            }
        }
    }

    class ConnectorSecurity extends AsyncTask<Void, Void, Void> {
        ConnectorSecurity() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... params) {
            if (CMCameraAPConnector.this.mConnectionHandler == null) {
                Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, ConnectorSecurity : handler is null, return!");
            } else {
                Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, ConnectorSecurity : doInBackground---- 1, WIFI CONNECTED = " + CMService.IS_WIFI_CONNECTED);
                if (!CMService.IS_WIFI_CONNECTED) {
                    Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, ConnectorSecurity : doInBackground---- 2");
                    CMCameraAPConnector.this.mTryToConnect = CMUtil.createConfigAndRequestConnection(CMCameraAPConnector.this.mContext, CMCameraAPConnector.this.mWifiManager, null, true, null, CMCameraAPConnector.this.mSecurityConnectionInfo);
                    Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, ConnectorSecurity : doInBackground---- 3, mTryToConnect = " + CMCameraAPConnector.this.mTryToConnect);
                    SystemClock.sleep(3000L);
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            Trace.d(CMConstants.TAG_NAME, "CMCameraAPConnector, ConnectorSecurity, PostExecute(), mConnectorSecurity set null.");
            CMCameraAPConnector.this.mConnectorSecurity = null;
        }
    }
}

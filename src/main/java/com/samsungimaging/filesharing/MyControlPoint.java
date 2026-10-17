package com.samsungimaging.filesharing;

import com.samsungimaging.filesharing.util.Logger;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.ST;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

/* JADX INFO: loaded from: classes.dex */
public class MyControlPoint extends ControlPoint implements DeviceChangeListener, SearchResponseListener, NotifyListener {
    public static final int DEVICEADDED = 256;
    public static final int DEVICEDELETED = 257;
    private static final String TAG = "MYCP";
    private static final String TAG_INRY = "INRY";
    private static FileSharing m_main;
    private static MyControlPoint mycp = null;
    public static Device selectedDevice;
    private boolean bCheckOnce = false;
    private boolean mdevicedAdded = false;
    protected boolean bDisconnect = false;

    private MyControlPoint() {
        Logger.d("INRY", "START CONTROL POINT!!");
        addDeviceChangeListener(this);
        addNotifyListener(this);
        addSearchResponseListener(this);
        MSearchStart();
    }

    public static synchronized MyControlPoint getInstance() {
        if (mycp == null) {
            mycp = new MyControlPoint();
        }
        return mycp;
    }

    public void SetMain(FileSharing filesharing) {
        m_main = filesharing;
    }

    private void MSearchStart() {
        new Thread(new Runnable() { // from class: com.samsungimaging.filesharing.MyControlPoint.1
            @Override // java.lang.Runnable
            public void run() {
                boolean bStrarted = false;
                while (!MyControlPoint.this.mdevicedAdded) {
                    if (!bStrarted) {
                        try {
                            String userAgent = Utils.getUserAgent();
                            bStrarted = MyControlPoint.this.start(ST.ALL_DEVICE, userAgent);
                            Logger.d("INRY", "ControlPoint start=" + bStrarted);
                            Thread.sleep(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MyControlPoint.this.search(ST.ALL_DEVICE);
                        Logger.d("INRY", "ControlPoint search.");
                        Thread.sleep(1000L);
                    }
                }
            }
        }).start();
    }

    @Override // org.cybergarage.upnp.device.NotifyListener
    public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
        Logger.i(TAG, "deviceNotifyReceived()");
        String subType = ssdpPacket.getNTS();
        String strNT = ssdpPacket.getNT();
        ssdpPacket.getUSN();
        Logger.e(TAG, "0   NTS    " + ssdpPacket.getNT() + ", NT=" + ssdpPacket.getNT() + ", USN=" + ssdpPacket.getUSN());
        if (strNT.equals("urn:schemas-upnp-org:service:ConnectionManager:1")) {
            Logger.d(TAG, "----- bCheckOnce -----     " + this.bCheckOnce);
            if (NTS.ALIVE.equals(subType) && !this.bCheckOnce) {
                Logger.d(TAG, "----------- ssdp:alive!!!! -------------");
                this.bCheckOnce = true;
            } else if (NTS.BYEBYE.equals(subType) && this.bCheckOnce) {
                Logger.d(TAG, "----------- ssdp:byebye!!!! -------------");
                m_main.byebye();
            }
        }
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceAdded(Device dev) {
        this.mdevicedAdded = true;
        Logger.i(TAG, "deviceAdded()");
        Logger.e(TAG, "dev : " + dev);
        Logger.e(TAG, "dev : getModelURL=" + dev.getModelURL() + ", getUDN=" + dev.getUDN());
        m_main.DeviceChange(256, dev);
        if (FileSharing.mPreconnectedSsid != null && FileSharing.mPreconnectedSsid.contains("QF30")) {
            ServiceList serviceList = dev.getServiceList();
            int serviceCnt = serviceList.size();
            for (int n = 0; n < serviceCnt; n++) {
                subscribe(serviceList.getService(n));
            }
        }
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceRemoved(Device dev) {
        Logger.i(TAG, "deviceRemoved()");
        m_main.DeviceChange(DEVICEDELETED, dev);
    }

    @Override // org.cybergarage.upnp.device.SearchResponseListener
    public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
        Logger.e(TAG, "deviceSearchResponseReceived()");
        String subType = ssdpPacket.getNTS();
        String strST = ssdpPacket.getST();
        ssdpPacket.getUSN();
        Logger.e(TAG, "2   NTS    " + subType + ", ST=" + ssdpPacket.getST() + ", USN=" + ssdpPacket.getUSN());
        this.bCheckOnce = true;
        if (strST.equals("urn:schemas-upnp-org:service:ConnectionManager:1")) {
            if (NTS.ALIVE.equals(subType) && !this.bCheckOnce) {
                Logger.d(TAG, "----------- ssdp:alive!!!! -------------");
                this.bCheckOnce = true;
            } else if (NTS.BYEBYE.equals(subType) && this.bCheckOnce) {
                Logger.d(TAG, "----------- ssdp:byebye!!!! -------------");
                m_main.byebye();
            }
        }
    }

    @Override // org.cybergarage.upnp.ControlPoint, org.cybergarage.http.HTTPRequestListener
    public void httpRequestRecieved(HTTPRequest httpReq) {
        if (!this.bDisconnect) {
            super.httpRequestRecieved(httpReq);
        }
    }
}

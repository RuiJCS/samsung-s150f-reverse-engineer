package com.samsungimaging.connectionmanager.app.pullservice.controller;

import android.os.Handler;
import android.os.Message;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.app.pullservice.Const;
import com.samsungimaging.connectionmanager.util.Trace;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.ST;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.ssdp.SSDP;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.xml.ParserException;
import org.cybergarage.xml.ParserExceptionListener;

/* JADX INFO: loaded from: classes.dex */
public class UPNPController extends ControlPoint implements DeviceChangeListener, NotifyListener, SearchResponseListener, EventListener, Controller, ParserExceptionListener {
    public static UPNPController controller = null;
    private DeviceController deviceController;
    public Trace.Tag TAG = Trace.Tag.CYBERGARAGE;
    private Device connectedDevice = null;
    private Handler eventHandler = null;
    private Handler deviceNotifyEventHandler = null;
    private boolean bCheckOnce = false;
    private String strUSN = null;
    private boolean isConnected = false;

    private UPNPController() {
        this.deviceController = null;
        this.deviceController = new DeviceController();
        addDeviceChangeListener(this);
        addNotifyListener(this);
        addSearchResponseListener(this);
        addEventListener(this);
        addParserExceptionListener(this);
    }

    public static synchronized UPNPController getInstance() {
        if (controller == null) {
            controller = new UPNPController();
        }
        return controller;
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.controller.Controller
    public void connect(String userAgent, String accessMethod, int ssdpPort) {
        Trace.d(this.TAG, "start connect()");
        MSearchStart(userAgent, accessMethod);
        SSDP.setSSDPPort(ssdpPort);
    }

    @Override // com.samsungimaging.connectionmanager.app.pullservice.controller.Controller
    public void disconnect() {
        Trace.d(this.TAG, "start disconnect()");
        removeDeviceChangeListener(this);
        removeNotifyListener(this);
        removeSearchResponseListener(this);
        removeEventListener(this);
        removeParserExceptionListener(this);
        stop();
        this.bCheckOnce = false;
        this.isConnected = false;
        controller = null;
        this.connectedDevice = null;
        this.deviceController = null;
    }

    @Override // org.cybergarage.upnp.event.EventListener
    public void eventNotifyReceived(String uuid, long seq, String varName, String value) {
        Trace.d(this.TAG, "start eventNotifyReceived() varName : " + varName + " value : " + value);
        if (!value.isEmpty()) {
            Message message = new Message();
            if (varName.equals("RVF_ROTATION_STATUS")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_ROTATION_STATUS;
                message.obj = value;
            } else if (varName.equals("RVF_FLASH_STATUS")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_FLASH_STATUS;
                message.obj = value;
            } else if (varName.equals("RVF_FOCUS_STATUS")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_FOCUS_STATUS;
                message.obj = value;
            } else if (varName.equals("RVF_SHUTTER_SPEED_SETUP_VALUE")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_SHUTTER_SPEED_SETUP_VALUE;
                message.obj = value;
            } else if (varName.equals("RVF_APERTURE_SETUP_VALUE")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_APERTURE_SETUP_VALUE;
                message.obj = value;
            } else if (varName.equals("RVF_EV_SETUP_VALUE")) {
                message.what = 108;
                message.obj = value;
            } else if (varName.equals("RVF_REMAIN_SHOT_COUNT_VALUE")) {
                message.what = 109;
                message.obj = value;
            } else if (varName.equals("RVF_LAST_PHOTO_URL_VALUE")) {
                message.what = 110;
                message.obj = value;
            } else if (varName.equals("RVF_CURRENT_ZOOM_VALUE")) {
                message.what = 111;
                message.obj = value;
            } else if (varName.equals("RVF_APERTURE_LIST")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_APERTURE_LIST;
                message.obj = value;
            } else if (varName.equals("RVF_SHUTTER_SPEED_LIST")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_SHUTTER_SPEED_LIST;
                message.obj = value;
            } else if (varName.equals("RVF_MOVIE_RECORD_TIME")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_MOVIE_RECORD_TIME;
                message.obj = value;
            } else if (varName.equals("RVF_REMAIN_REC_TIME_VALUE")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_REMAIN_REC_TIME_VALUE;
                message.obj = value;
            } else if (varName.equals("RVF_FLASH_STROBE_STATUS")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_FLASH_STROBE_STATUS;
                message.obj = value;
            } else if (varName.equals("OperationState")) {
                message.what = Const.MsgID.CONNECTOR_ON_CHANGED_OPERATION_STATE;
                message.obj = value;
            } else {
                message.what = 20;
                message.obj = value;
            }
            if (this.eventHandler != null) {
                this.eventHandler.sendMessage(message);
            }
        }
    }

    @Override // org.cybergarage.upnp.device.SearchResponseListener
    public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
        Trace.d(this.TAG, "start deviceSearchResponseReceived()");
        setupPeerCamDevice(ssdpPacket.getNTS(), ssdpPacket.getST(), ssdpPacket.getUSN());
    }

    @Override // org.cybergarage.upnp.device.NotifyListener
    public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
        Trace.d(this.TAG, "start deviceNotifyReceived()");
        setupPeerCamDevice(ssdpPacket.getNTS(), ssdpPacket.getNT(), ssdpPacket.getUSN());
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceAdded(Device dev) {
        Trace.d(this.TAG, "start deviceAdded()");
        if (this.connectedDevice == null) {
            DeviceList deviceList = getDeviceList();
            int size = deviceList.size();
            for (int i = 0; i < size; i++) {
                Device device = deviceList.getDevice(i);
                Trace.d(this.TAG, "device type : " + device.getDeviceType());
                if (device.getDeviceType().contains("MediaServer")) {
                    this.deviceController.setAction(device);
                    ServiceList serviceList = device.getServiceList();
                    int serviceCnt = serviceList.size();
                    for (int n = 0; n < serviceCnt; n++) {
                        Service service = serviceList.getService(n);
                        Trace.d(this.TAG, "service type : " + service.getServiceType());
                        if (service.getServiceType().contains("ContentDirectory")) {
                            Trace.d(this.TAG, "call subscribe()");
                            subscribe(service);
                        }
                    }
                    this.connectedDevice = device;
                    Trace.d(this.TAG, "set connected device");
                    this.isConnected = true;
                }
            }
        }
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceRemoved(Device dev) {
        Trace.d(this.TAG, "start deviceRemoved()");
    }

    @Override // org.cybergarage.xml.ParserExceptionListener
    public void occuredParserException(ParserException pe) {
        Trace.d(this.TAG, "start occuredParserException()");
        pe.printStackTrace();
        String msg = pe.getMessage();
        if (msg.contains("HTTP_UNAUTHORIZED") || msg.contains("HTTP_INTERNAL_ERROR") || msg.contains("HTTP_NOT_IMPLEMENTED") || msg.contains("HTTP_BAD_GATEWAY") || msg.contains("HTTP_UNAVAILABLE") || msg.contains("HTTP_GATEWAY_TIMEOUT") || msg.contains("HTTP_VERSION")) {
            Message message = new Message();
            message.what = Const.MsgID.CONNECTOR_ON_OCCURED_PARSER_EXCEPTION;
            message.obj = msg;
            this.deviceNotifyEventHandler.sendMessage(message);
        }
    }

    private void MSearchStart(final String userAgent, final String accessMethod) {
        new Thread(new Runnable() { // from class: com.samsungimaging.connectionmanager.app.pullservice.controller.UPNPController.1
            @Override // java.lang.Runnable
            public void run() {
                boolean bStarted = false;
                while (UPNPController.this.connectedDevice == null) {
                    try {
                        Trace.d(UPNPController.this.TAG, "bStarted : " + bStarted + " userAgent : " + userAgent + " accessMethod : " + accessMethod);
                        if (!bStarted) {
                            bStarted = UPNPController.this.start(ST.ALL_DEVICE, userAgent, accessMethod);
                            Thread.sleep(3000L);
                        } else {
                            UPNPController.this.search(ST.ALL_DEVICE);
                            Thread.sleep(1000L);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setupPeerCamDevice(String subType, String strNT, String strLocalUSN) {
        if (strNT.equals("urn:schemas-upnp-org:service:ConnectionManager:1") || strNT.equals("urn:schemas-upnp-org:service:ContentDirectory:1")) {
            Trace.d(this.TAG, "start setupPeerCamDevice() bCheckOnce : " + this.bCheckOnce);
            Trace.d(this.TAG, "subType : " + subType);
            Trace.d(this.TAG, "strNT : " + strNT);
            Trace.d(this.TAG, "strLocalUSN : " + strLocalUSN);
            if (!this.bCheckOnce) {
                boolean isOldCamera = CMUtil.checkOldVersionSmartCameraApp(CMInfo.getInstance().getConnectedSSID());
                if (!isOldCamera && subType.equals(NTS.BYEBYE)) {
                    Message message = new Message();
                    message.what = 101;
                    this.deviceNotifyEventHandler.sendMessage(message);
                    return;
                } else {
                    if (this.connectedDevice != null) {
                        this.strUSN = strLocalUSN;
                        this.bCheckOnce = true;
                        Message message2 = new Message();
                        message2.what = 100;
                        this.deviceNotifyEventHandler.sendMessage(message2);
                        return;
                    }
                    return;
                }
            }
            if (subType.equals(NTS.BYEBYE) && this.strUSN.equals(strLocalUSN)) {
                Message message3 = new Message();
                message3.what = 101;
                this.deviceNotifyEventHandler.sendMessage(message3);
            } else if (!subType.equals("")) {
                Message message4 = new Message();
                message4.what = Const.MsgID.CONNECTOR_ON_CAMERA_CHANGE;
                message4.obj = subType;
                this.eventHandler.sendMessage(message4);
            }
        }
    }

    public DeviceController getDeviceController() {
        return this.deviceController;
    }

    public boolean isConnected() {
        Trace.d(this.TAG, "start isConnected() : " + this.isConnected);
        return this.isConnected;
    }

    public void setEventHandler(Handler handler) {
        this.eventHandler = handler;
    }

    public void setDeviceNotifyEventHandler(Handler handler) {
        this.deviceNotifyEventHandler = handler;
    }

    public Device getConnectedDevice() {
        Trace.d(this.TAG, "start getConnectedDevice()");
        return this.connectedDevice;
    }
}

package org.cybergarage.upnp;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Calendar;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPRequestListener;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.http.HTTPStatus;
import org.cybergarage.net.HostInterface;
import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.ActionRequest;
import org.cybergarage.upnp.control.ActionResponse;
import org.cybergarage.upnp.control.ControlRequest;
import org.cybergarage.upnp.control.ControlResponse;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.control.QueryRequest;
import org.cybergarage.upnp.device.Advertiser;
import org.cybergarage.upnp.device.Description;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.ST;
import org.cybergarage.upnp.device.SearchListener;
import org.cybergarage.upnp.event.Subscriber;
import org.cybergarage.upnp.event.Subscription;
import org.cybergarage.upnp.event.SubscriptionRequest;
import org.cybergarage.upnp.event.SubscriptionResponse;
import org.cybergarage.upnp.ssdp.SSDPNotifyRequest;
import org.cybergarage.upnp.ssdp.SSDPNotifySocket;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.upnp.ssdp.SSDPSearchResponse;
import org.cybergarage.upnp.ssdp.SSDPSearchResponseSocket;
import org.cybergarage.upnp.ssdp.SSDPSearchSocketList;
import org.cybergarage.upnp.xml.DeviceData;
import org.cybergarage.util.Debug;
import org.cybergarage.util.FileUtil;
import org.cybergarage.util.Mutex;
import org.cybergarage.util.TimerUtil;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

/* JADX INFO: loaded from: classes.dex */
public class Device implements HTTPRequestListener, SearchListener {
    public static final String DEFAULT_DESCRIPTION_URI = "/description.xml";
    public static final int DEFAULT_DISCOVERY_WAIT_TIME = 300;
    public static final int DEFAULT_LEASE_TIME = 1800;
    public static final int DEFAULT_STARTUP_WAIT_TIME = 1000;
    private static final String DEVICE_TYPE = "deviceType";
    public static final String ELEM_NAME = "device";
    private static final String FRIENDLY_NAME = "friendlyName";
    public static final int HTTP_DEFAULT_PORT = 4004;
    private static final String MANUFACTURE = "manufacturer";
    private static final String MANUFACTURE_URL = "manufacturerURL";
    private static final String MODEL_DESCRIPTION = "modelDescription";
    private static final String MODEL_NAME = "modelName";
    private static final String MODEL_NUMBER = "modelNumber";
    private static final String MODEL_URL = "modelURL";
    private static final String SERIAL_NUMBER = "serialNumber";
    private static final String UDN = "UDN";
    private static final String UPC = "UPC";
    public static final String UPNP_ROOTDEVICE = "upnp:rootdevice";
    private static final String URLBASE_NAME = "URLBase";
    private static Calendar cal = null;
    private static final String presentationURL = "presentationURL";
    private String devUUID;
    private Node deviceNode;
    private Mutex mutex;
    private Node rootNode;
    private Object userData;
    private boolean wirelessMode;

    public Node getRootNode() {
        if (this.rootNode != null) {
            return this.rootNode;
        }
        if (this.deviceNode == null) {
            return null;
        }
        return this.deviceNode.getRootNode();
    }

    public Node getDeviceNode() {
        return this.deviceNode;
    }

    public void setRootNode(Node node) {
        this.rootNode = node;
    }

    public void setDeviceNode(Node node) {
        this.deviceNode = node;
    }

    static {
        UPnP.initialize();
        cal = Calendar.getInstance();
    }

    public Device(Node root, Node device) {
        this.mutex = new Mutex();
        this.userData = null;
        this.rootNode = root;
        this.deviceNode = device;
        setUUID(UPnP.createUUID());
        setWirelessMode(false);
    }

    public Device() {
        this(null, null);
    }

    public Device(Node device) {
        this(null, device);
    }

    public Device(File descriptionFile) throws InvalidDescriptionException {
        this(null, null);
        loadDescription(descriptionFile);
    }

    public Device(InputStream input) throws InvalidDescriptionException {
        this(null, null);
        loadDescription(input);
    }

    public Device(String descriptionFileName) throws InvalidDescriptionException {
        this(new File(descriptionFileName));
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }

    public String getAbsoluteURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.toString();
        } catch (Exception e) {
            Device rootDev = getRootDevice();
            String urlBaseStr = rootDev.getURLBase();
            if (urlBaseStr == null || urlBaseStr.length() <= 0) {
                String location = rootDev.getLocation();
                String locationHost = HTTP.getHost(location);
                int locationPort = HTTP.getPort(location);
                urlBaseStr = HTTP.getRequestHostURL(locationHost, locationPort);
            }
            String urlString2 = HTTP.toRelativeURL(urlString);
            String absUrl = String.valueOf(urlBaseStr) + urlString2;
            try {
                URL url2 = new URL(absUrl);
                return url2.toString();
            } catch (Exception e2) {
                String absUrl2 = HTTP.getAbsoluteURL(urlBaseStr, urlString2);
                try {
                    URL url3 = new URL(absUrl2);
                    return url3.toString();
                } catch (Exception e3) {
                    return "";
                }
            }
        }
    }

    public void setNMPRMode(boolean flag) {
        Node devNode = getDeviceNode();
        if (devNode != null) {
            if (flag) {
                devNode.setNode(UPnP.INMPR03, "1.0");
                devNode.removeNode(URLBASE_NAME);
            } else {
                devNode.removeNode(UPnP.INMPR03);
            }
        }
    }

    public boolean isNMPRMode() {
        Node devNode = getDeviceNode();
        return (devNode == null || devNode.getNode(UPnP.INMPR03) == null) ? false : true;
    }

    public void setWirelessMode(boolean flag) {
        this.wirelessMode = flag;
    }

    public boolean isWirelessMode() {
        return this.wirelessMode;
    }

    public int getSSDPAnnounceCount() {
        return (isNMPRMode() && isWirelessMode()) ? 4 : 1;
    }

    private void setUUID(String uuid) {
        this.devUUID = uuid;
    }

    private String getUUID() {
        return this.devUUID;
    }

    private void updateUDN() {
        setUDN(Subscription.UUID + getUUID());
    }

    public Device getRootDevice() {
        Node devNode;
        Node rootNode = getRootNode();
        if (rootNode == null || (devNode = rootNode.getNode(ELEM_NAME)) == null) {
            return null;
        }
        return new Device(rootNode, devNode);
    }

    public Device getParentDevice() {
        if (isRootDevice()) {
            return null;
        }
        Node devNode = getDeviceNode();
        Node aux = devNode.getParentNode().getParentNode();
        return new Device(aux);
    }

    public void addService(Service s) {
        Node serviceListNode = getDeviceNode().getNode("serviceList");
        if (serviceListNode == null) {
            serviceListNode = new Node("serviceList");
            getDeviceNode().addNode(serviceListNode);
        }
        serviceListNode.addNode(s.getServiceNode());
    }

    public void addDevice(Device d) {
        Node deviceListNode = getDeviceNode().getNode(DeviceList.ELEM_NAME);
        if (deviceListNode == null) {
            deviceListNode = new Node(DeviceList.ELEM_NAME);
            getDeviceNode().addNode(deviceListNode);
        }
        deviceListNode.addNode(d.getDeviceNode());
        d.setRootNode(null);
        if (getRootNode() == null) {
            Node root = new Node(RootDescription.ROOT_ELEMENT);
            root.setNameSpace("", RootDescription.ROOT_ELEMENT_NAMESPACE);
            Node spec = new Node("specVersion");
            Node maj = new Node("major");
            maj.setValue(Service.MAJOR_VALUE);
            Node min = new Node("minor");
            min.setValue("0");
            spec.addNode(maj);
            spec.addNode(min);
            root.addNode(spec);
            setRootNode(root);
        }
    }

    private DeviceData getDeviceData() {
        Node node = getDeviceNode();
        DeviceData userData = (DeviceData) node.getUserData();
        if (userData == null) {
            DeviceData userData2 = new DeviceData();
            node.setUserData(userData2);
            userData2.setNode(node);
            return userData2;
        }
        return userData;
    }

    private void setDescriptionFile(File file) {
        getDeviceData().setDescriptionFile(file);
    }

    public File getDescriptionFile() {
        return getDeviceData().getDescriptionFile();
    }

    private void setDescriptionURI(String uri) {
        getDeviceData().setDescriptionURI(uri);
    }

    private String getDescriptionURI() {
        return getDeviceData().getDescriptionURI();
    }

    private boolean isDescriptionURI(String uri) {
        String descriptionURI = getDescriptionURI();
        if (uri == null || descriptionURI == null) {
            return false;
        }
        return descriptionURI.equals(uri);
    }

    public String getDescriptionFilePath() {
        File descriptionFile = getDescriptionFile();
        return descriptionFile == null ? "" : descriptionFile.getAbsoluteFile().getParent();
    }

    public boolean loadDescription(InputStream input) throws InvalidDescriptionException {
        try {
            Parser parser = UPnP.getXMLParser();
            this.rootNode = parser.parse(input);
            if (this.rootNode == null) {
                throw new InvalidDescriptionException(Description.NOROOT_EXCEPTION);
            }
            this.deviceNode = this.rootNode.getNode(ELEM_NAME);
            if (this.deviceNode == null) {
                throw new InvalidDescriptionException(Description.NOROOTDEVICE_EXCEPTION);
            }
            if (!initializeLoadedDescription()) {
                return false;
            }
            setDescriptionFile(null);
            return true;
        } catch (ParserException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    public boolean loadDescription(String descString) throws InvalidDescriptionException {
        try {
            Parser parser = UPnP.getXMLParser();
            this.rootNode = parser.parse(descString);
            if (this.rootNode == null) {
                throw new InvalidDescriptionException(Description.NOROOT_EXCEPTION);
            }
            this.deviceNode = this.rootNode.getNode(ELEM_NAME);
            if (this.deviceNode == null) {
                throw new InvalidDescriptionException(Description.NOROOTDEVICE_EXCEPTION);
            }
            if (!initializeLoadedDescription()) {
                return false;
            }
            setDescriptionFile(null);
            return true;
        } catch (ParserException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    public boolean loadDescription(File file) throws InvalidDescriptionException {
        try {
            Parser parser = UPnP.getXMLParser();
            this.rootNode = parser.parse(file);
            if (this.rootNode == null) {
                throw new InvalidDescriptionException(Description.NOROOT_EXCEPTION, file);
            }
            this.deviceNode = this.rootNode.getNode(ELEM_NAME);
            if (this.deviceNode == null) {
                throw new InvalidDescriptionException(Description.NOROOTDEVICE_EXCEPTION, file);
            }
            if (!initializeLoadedDescription()) {
                return false;
            }
            setDescriptionFile(file);
            return true;
        } catch (ParserException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    private boolean initializeLoadedDescription() {
        setDescriptionURI(DEFAULT_DESCRIPTION_URI);
        setLeaseTime(DEFAULT_LEASE_TIME);
        setHTTPPort(HTTP_DEFAULT_PORT);
        if (!hasUDN()) {
            updateUDN();
            return true;
        }
        return true;
    }

    public static boolean isDeviceNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    public boolean isRootDevice() {
        return getRootNode().getNode(ELEM_NAME).getNodeValue(UDN).equals(getUDN());
    }

    public void setSSDPPacket(SSDPPacket packet) {
        getDeviceData().setSSDPPacket(packet);
    }

    public SSDPPacket getSSDPPacket() {
        if (isRootDevice()) {
            return getDeviceData().getSSDPPacket();
        }
        return null;
    }

    public void setLocation(String value) {
        getDeviceData().setLocation(value);
    }

    public String getLocation() {
        SSDPPacket packet = getSSDPPacket();
        return packet != null ? packet.getLocation() : getDeviceData().getLocation();
    }

    public void setLeaseTime(int value) {
        getDeviceData().setLeaseTime(value);
        Advertiser adv = getAdvertiser();
        if (adv != null) {
            announce();
            adv.restart();
        }
    }

    public int getLeaseTime() {
        SSDPPacket packet = getSSDPPacket();
        return packet != null ? packet.getLeaseTime() : getDeviceData().getLeaseTime();
    }

    public long getTimeStamp() {
        SSDPPacket packet = getSSDPPacket();
        if (packet != null) {
            return packet.getTimeStamp();
        }
        return 0L;
    }

    public long getElapsedTime() {
        return (System.currentTimeMillis() - getTimeStamp()) / 1000;
    }

    public boolean isExpired() {
        long elipsedTime = getElapsedTime();
        long leaseTime = getLeaseTime() + 60;
        return leaseTime < elipsedTime;
    }

    private void setURLBase(String value) {
        if (isRootDevice()) {
            Node node = getRootNode().getNode(URLBASE_NAME);
            if (node != null) {
                node.setValue(value);
                return;
            }
            Node node2 = new Node(URLBASE_NAME);
            node2.setValue(value);
            int index = 1;
            if (!getRootNode().hasNodes()) {
                index = 1;
            }
            getRootNode().insertNode(node2, index);
        }
    }

    private void updateURLBase(String host) {
        String urlBase = HostInterface.getHostURL(host, getHTTPPort(), "");
        setURLBase(urlBase);
    }

    public String getURLBase() {
        return isRootDevice() ? getRootNode().getNodeValue(URLBASE_NAME) : "";
    }

    public void setDeviceType(String value) {
        getDeviceNode().setNode(DEVICE_TYPE, value);
    }

    public String getDeviceType() {
        return getDeviceNode().getNodeValue(DEVICE_TYPE);
    }

    public boolean isDeviceType(String value) {
        if (value == null) {
            return false;
        }
        return value.equals(getDeviceType());
    }

    public void setFriendlyName(String value) {
        getDeviceNode().setNode(FRIENDLY_NAME, value);
    }

    public String getFriendlyName() {
        return getDeviceNode().getNodeValue(FRIENDLY_NAME);
    }

    public void setManufacture(String value) {
        getDeviceNode().setNode(MANUFACTURE, value);
    }

    public String getManufacture() {
        return getDeviceNode().getNodeValue(MANUFACTURE);
    }

    public void setManufactureURL(String value) {
        getDeviceNode().setNode(MANUFACTURE_URL, value);
    }

    public String getManufactureURL() {
        return getDeviceNode().getNodeValue(MANUFACTURE_URL);
    }

    public void setModelDescription(String value) {
        getDeviceNode().setNode(MODEL_DESCRIPTION, value);
    }

    public String getModelDescription() {
        return getDeviceNode().getNodeValue(MODEL_DESCRIPTION);
    }

    public void setModelName(String value) {
        getDeviceNode().setNode(MODEL_NAME, value);
    }

    public String getModelName() {
        return getDeviceNode().getNodeValue(MODEL_NAME);
    }

    public void setModelNumber(String value) {
        getDeviceNode().setNode(MODEL_NUMBER, value);
    }

    public String getModelNumber() {
        return getDeviceNode().getNodeValue(MODEL_NUMBER);
    }

    public void setModelURL(String value) {
        getDeviceNode().setNode(MODEL_URL, value);
    }

    public String getModelURL() {
        return getDeviceNode().getNodeValue(MODEL_URL);
    }

    public void setSerialNumber(String value) {
        getDeviceNode().setNode(SERIAL_NUMBER, value);
    }

    public String getSerialNumber() {
        return getDeviceNode().getNodeValue(SERIAL_NUMBER);
    }

    public void setUDN(String value) {
        getDeviceNode().setNode(UDN, value);
    }

    public String getUDN() {
        return getDeviceNode().getNodeValue(UDN);
    }

    public boolean hasUDN() {
        String udn = getUDN();
        return udn != null && udn.length() > 0;
    }

    public void setUPC(String value) {
        getDeviceNode().setNode(UPC, value);
    }

    public String getUPC() {
        return getDeviceNode().getNodeValue(UPC);
    }

    public void setPresentationURL(String value) {
        getDeviceNode().setNode(presentationURL, value);
    }

    public String getPresentationURL() {
        return getDeviceNode().getNodeValue(presentationURL);
    }

    public DeviceList getDeviceList() {
        DeviceList devList = new DeviceList();
        Node devListNode = getDeviceNode().getNode(DeviceList.ELEM_NAME);
        if (devListNode != null) {
            int nNode = devListNode.getNNodes();
            for (int n = 0; n < nNode; n++) {
                Node node = devListNode.getNode(n);
                if (isDeviceNode(node)) {
                    Device dev = new Device(node);
                    devList.add(dev);
                }
            }
        }
        return devList;
    }

    public boolean isDevice(String name) {
        if (name == null) {
            return false;
        }
        return name.endsWith(getUDN()) || name.equals(getFriendlyName()) || name.endsWith(getDeviceType());
    }

    public Device getDevice(String name) {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            if (dev.isDevice(name)) {
                return dev;
            }
            Device cdev = dev.getDevice(name);
            if (cdev != null) {
                return cdev;
            }
        }
        return null;
    }

    public Device getDeviceByDescriptionURI(String uri) {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            if (dev.isDescriptionURI(uri)) {
                return dev;
            }
            Device cdev = dev.getDeviceByDescriptionURI(uri);
            if (cdev != null) {
                return cdev;
            }
        }
        return null;
    }

    public ServiceList getServiceList() {
        ServiceList serviceList = new ServiceList();
        Node serviceListNode = getDeviceNode().getNode("serviceList");
        if (serviceListNode != null) {
            int nNode = serviceListNode.getNNodes();
            for (int n = 0; n < nNode; n++) {
                Node node = serviceListNode.getNode(n);
                if (Service.isServiceNode(node)) {
                    Service service = new Service(node);
                    serviceList.add(service);
                }
            }
        }
        return serviceList;
    }

    public Service getService(String name) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.isService(name)) {
                return service;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Service service2 = dev.getService(name);
            if (service2 != null) {
                return service2;
            }
        }
        return null;
    }

    public Service getServiceBySCPDURL(String searchUrl) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.isSCPDURL(searchUrl)) {
                return service;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Service service2 = dev.getServiceBySCPDURL(searchUrl);
            if (service2 != null) {
                return service2;
            }
        }
        return null;
    }

    public Service getServiceByControlURL(String searchUrl) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.isControlURL(searchUrl)) {
                return service;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Service service2 = dev.getServiceByControlURL(searchUrl);
            if (service2 != null) {
                return service2;
            }
        }
        return null;
    }

    public Service getServiceByEventSubURL(String searchUrl) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.isEventSubURL(searchUrl)) {
                return service;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Service service2 = dev.getServiceByEventSubURL(searchUrl);
            if (service2 != null) {
                return service2;
            }
        }
        return null;
    }

    public Service getSubscriberService(String uuid) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            String sid = service.getSID();
            if (uuid.equals(sid)) {
                return service;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Service service2 = dev.getSubscriberService(uuid);
            if (service2 != null) {
                return service2;
            }
        }
        return null;
    }

    public StateVariable getStateVariable(String serviceType, String name) {
        StateVariable stateVar;
        if (serviceType == null && name == null) {
            return null;
        }
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if ((serviceType == null || service.getServiceType().equals(serviceType)) && (stateVar = service.getStateVariable(name)) != null) {
                return stateVar;
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            StateVariable stateVar2 = dev.getStateVariable(serviceType, name);
            if (stateVar2 != null) {
                return stateVar2;
            }
        }
        return null;
    }

    public StateVariable getStateVariable(String name) {
        return getStateVariable(null, name);
    }

    public Action getAction(String name) {
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            ActionList actionList = service.getActionList();
            int actionCnt = actionList.size();
            for (int i = 0; i < actionCnt; i++) {
                Action action = actionList.getAction(i);
                String actionName = action.getName();
                if (actionName != null && actionName.equals(name)) {
                    return action;
                }
            }
        }
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n2 = 0; n2 < devCnt; n2++) {
            Device dev = devList.getDevice(n2);
            Action action2 = dev.getAction(name);
            if (action2 != null) {
                return action2;
            }
        }
        return null;
    }

    public IconList getIconList() {
        IconList iconList = new IconList();
        Node iconListNode = getDeviceNode().getNode(IconList.ELEM_NAME);
        if (iconListNode != null) {
            int nNode = iconListNode.getNNodes();
            for (int n = 0; n < nNode; n++) {
                Node node = iconListNode.getNode(n);
                if (Icon.isIconNode(node)) {
                    Icon icon = new Icon(node);
                    iconList.add(icon);
                }
            }
        }
        return iconList;
    }

    public Icon getIcon(int n) {
        IconList iconList = getIconList();
        if (n >= 0 || iconList.size() - 1 >= n) {
            return iconList.getIcon(n);
        }
        return null;
    }

    public Icon getSmallestIcon() {
        Icon smallestIcon = null;
        IconList iconList = getIconList();
        int iconCount = iconList.size();
        for (int n = 0; n < iconCount; n++) {
            Icon icon = iconList.getIcon(n);
            if (smallestIcon == null) {
                smallestIcon = icon;
            } else if (icon.getWidth() < smallestIcon.getWidth()) {
                smallestIcon = icon;
            }
        }
        return smallestIcon;
    }

    public String getLocationURL(String host) {
        return HostInterface.getHostURL(host, getHTTPPort(), getDescriptionURI());
    }

    private String getNotifyDeviceNT() {
        return !isRootDevice() ? getUDN() : "upnp:rootdevice";
    }

    private String getNotifyDeviceUSN() {
        return !isRootDevice() ? getUDN() : String.valueOf(getUDN()) + "::upnp:rootdevice";
    }

    private String getNotifyDeviceTypeNT() {
        return getDeviceType();
    }

    private String getNotifyDeviceTypeUSN() {
        return String.valueOf(getUDN()) + "::" + getDeviceType();
    }

    public static final void notifyWait() {
        TimerUtil.waitRandom(DEFAULT_DISCOVERY_WAIT_TIME);
    }

    public void announce(String bindAddr) {
        String devLocation = getLocationURL(bindAddr);
        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);
        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setServer(UPnP.getServerName());
        ssdpReq.setLeaseTime(getLeaseTime());
        ssdpReq.setLocation(devLocation);
        ssdpReq.setNTS(NTS.ALIVE);
        if (isRootDevice()) {
            String devNT = getNotifyDeviceNT();
            String devUSN = getNotifyDeviceUSN();
            ssdpReq.setNT(devNT);
            ssdpReq.setUSN(devUSN);
            ssdpSock.post(ssdpReq);
            String devUDN = getUDN();
            ssdpReq.setNT(devUDN);
            ssdpReq.setUSN(devUDN);
            ssdpSock.post(ssdpReq);
        }
        String devNT2 = getNotifyDeviceTypeNT();
        String devUSN2 = getNotifyDeviceTypeUSN();
        ssdpReq.setNT(devNT2);
        ssdpReq.setUSN(devUSN2);
        ssdpSock.post(ssdpReq);
        ssdpSock.close();
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            service.announce(bindAddr);
        }
        DeviceList childDeviceList = getDeviceList();
        int childDeviceCnt = childDeviceList.size();
        for (int n2 = 0; n2 < childDeviceCnt; n2++) {
            Device childDevice = childDeviceList.getDevice(n2);
            childDevice.announce(bindAddr);
        }
    }

    public void announce() {
        String[] bindAddresses;
        notifyWait();
        InetAddress[] binds = getDeviceData().getHTTPBindAddress();
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }
        for (int j = 0; j < bindAddresses.length; j++) {
            if (bindAddresses[j] != null && bindAddresses[j].length() != 0) {
                int ssdpCount = getSSDPAnnounceCount();
                for (int i2 = 0; i2 < ssdpCount; i2++) {
                    announce(bindAddresses[j]);
                }
            }
        }
    }

    public void byebye(String bindAddr) {
        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);
        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setNTS(NTS.BYEBYE);
        if (isRootDevice()) {
            String devNT = getNotifyDeviceNT();
            String devUSN = getNotifyDeviceUSN();
            ssdpReq.setNT(devNT);
            ssdpReq.setUSN(devUSN);
            ssdpSock.post(ssdpReq);
        }
        String devNT2 = getNotifyDeviceTypeNT();
        String devUSN2 = getNotifyDeviceTypeUSN();
        ssdpReq.setNT(devNT2);
        ssdpReq.setUSN(devUSN2);
        ssdpSock.post(ssdpReq);
        ssdpSock.close();
        ServiceList serviceList = getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            service.byebye(bindAddr);
        }
        DeviceList childDeviceList = getDeviceList();
        int childDeviceCnt = childDeviceList.size();
        for (int n2 = 0; n2 < childDeviceCnt; n2++) {
            Device childDevice = childDeviceList.getDevice(n2);
            childDevice.byebye(bindAddr);
        }
    }

    public void byebye() {
        String[] bindAddresses;
        InetAddress[] binds = getDeviceData().getHTTPBindAddress();
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }
        for (int j = 0; j < bindAddresses.length; j++) {
            if (bindAddresses[j] != null && bindAddresses[j].length() > 0) {
                int ssdpCount = getSSDPAnnounceCount();
                for (int i2 = 0; i2 < ssdpCount; i2++) {
                    byebye(bindAddresses[j]);
                }
            }
        }
    }

    public boolean postSearchResponse(SSDPPacket ssdpPacket, String st, String usn) {
        String localAddr = ssdpPacket.getLocalAddress();
        Device rootDev = getRootDevice();
        String rootDevLocation = rootDev.getLocationURL(localAddr);
        SSDPSearchResponse ssdpRes = new SSDPSearchResponse();
        ssdpRes.setLeaseTime(getLeaseTime());
        ssdpRes.setDate(cal);
        ssdpRes.setST(st);
        ssdpRes.setUSN(usn);
        ssdpRes.setLocation(rootDevLocation);
        ssdpRes.setMYNAME(getFriendlyName());
        int mx = ssdpPacket.getMX();
        TimerUtil.waitRandom(mx * 1000);
        String remoteAddr = ssdpPacket.getRemoteAddress();
        int remotePort = ssdpPacket.getRemotePort();
        SSDPSearchResponseSocket ssdpResSock = new SSDPSearchResponseSocket();
        if (Debug.isOn()) {
            ssdpRes.print();
        }
        int ssdpCount = getSSDPAnnounceCount();
        for (int i = 0; i < ssdpCount; i++) {
            ssdpResSock.post(remoteAddr, remotePort, ssdpRes);
        }
        return true;
    }

    public void deviceSearchResponse(SSDPPacket ssdpPacket) {
        String ssdpST = ssdpPacket.getST();
        if (ssdpST != null) {
            boolean isRootDevice = isRootDevice();
            String devUSN = getUDN();
            if (isRootDevice) {
                devUSN = String.valueOf(devUSN) + "::upnp:rootdevice";
            }
            if (ST.isAllDevice(ssdpST)) {
                String devNT = getNotifyDeviceNT();
                int repeatCnt = isRootDevice ? 3 : 2;
                for (int n = 0; n < repeatCnt; n++) {
                    postSearchResponse(ssdpPacket, devNT, devUSN);
                }
            } else if (ST.isRootDevice(ssdpST)) {
                if (isRootDevice) {
                    postSearchResponse(ssdpPacket, "upnp:rootdevice", devUSN);
                }
            } else if (ST.isUUIDDevice(ssdpST)) {
                String devUDN = getUDN();
                if (ssdpST.equals(devUDN)) {
                    postSearchResponse(ssdpPacket, devUDN, devUSN);
                }
            } else if (ST.isURNDevice(ssdpST)) {
                String devType = getDeviceType();
                if (ssdpST.equals(devType)) {
                    postSearchResponse(ssdpPacket, devType, String.valueOf(getUDN()) + "::" + devType);
                }
            }
            ServiceList serviceList = getServiceList();
            int serviceCnt = serviceList.size();
            for (int n2 = 0; n2 < serviceCnt; n2++) {
                Service service = serviceList.getService(n2);
                service.serviceSearchResponse(ssdpPacket);
            }
            DeviceList childDeviceList = getDeviceList();
            int childDeviceCnt = childDeviceList.size();
            for (int n3 = 0; n3 < childDeviceCnt; n3++) {
                Device childDevice = childDeviceList.getDevice(n3);
                childDevice.deviceSearchResponse(ssdpPacket);
            }
        }
    }

    @Override // org.cybergarage.upnp.device.SearchListener
    public void deviceSearchReceived(SSDPPacket ssdpPacket) {
        deviceSearchResponse(ssdpPacket);
    }

    public void setHTTPPort(int port) {
        getDeviceData().setHTTPPort(port);
    }

    public int getHTTPPort() {
        return getDeviceData().getHTTPPort();
    }

    public void setHTTPBindAddress(InetAddress[] inets) {
        getDeviceData().setHTTPBindAddress(inets);
    }

    public InetAddress[] getHTTPBindAddress() {
        return getDeviceData().getHTTPBindAddress();
    }

    public String getSSDPIPv4MulticastAddress() {
        return getDeviceData().getMulticastIPv4Address();
    }

    public void getSSDPIPv4MulticastAddress(String ip) {
        getDeviceData().setMulticastIPv4Address(ip);
    }

    public String getSSDPIPv6MulticastAddress() {
        return getDeviceData().getMulticastIPv6Address();
    }

    public void getSSDPIPv6MulticastAddress(String ip) {
        getDeviceData().setMulticastIPv6Address(ip);
    }

    @Override // org.cybergarage.http.HTTPRequestListener
    public void httpRequestRecieved(HTTPRequest httpReq) {
        if (Debug.isOn()) {
            httpReq.print();
        }
        if (httpReq.isGetRequest() || httpReq.isHeadRequest()) {
            httpGetRequestRecieved(httpReq);
            return;
        }
        if (httpReq.isPostRequest()) {
            httpPostRequestRecieved(httpReq);
        } else if (httpReq.isSubscribeRequest() || httpReq.isUnsubscribeRequest()) {
            SubscriptionRequest subReq = new SubscriptionRequest(httpReq);
            deviceEventSubscriptionRecieved(subReq);
        } else {
            httpReq.returnBadRequest();
        }
    }

    private synchronized byte[] getDescriptionData(String host) {
        byte[] bytes;
        if (!isNMPRMode()) {
            updateURLBase(host);
        }
        Node rootNode = getRootNode();
        if (rootNode == null) {
            bytes = new byte[0];
        } else {
            String desc = new String();
            bytes = (String.valueOf(String.valueOf(String.valueOf(desc) + "<?xml version=\"1.0\" encoding=\"utf-8\"?>") + "\n") + rootNode.toString()).getBytes();
        }
        return bytes;
    }

    private void httpGetRequestRecieved(HTTPRequest httpReq) {
        byte[] fileByte;
        String uri = httpReq.getURI();
        Debug.message("httpGetRequestRecieved = " + uri);
        if (uri == null) {
            httpReq.returnBadRequest();
            return;
        }
        byte[] bArr = new byte[0];
        if (isDescriptionURI(uri)) {
            httpReq.getLocalAddress();
            String localAddr = HostInterface.getInterface();
            fileByte = getDescriptionData(localAddr);
        } else {
            Device embDev = getDeviceByDescriptionURI(uri);
            if (embDev != null) {
                String localAddr2 = httpReq.getLocalAddress();
                fileByte = embDev.getDescriptionData(localAddr2);
            } else {
                Service embService = getServiceBySCPDURL(uri);
                if (embService != null) {
                    fileByte = embService.getSCPDData();
                } else {
                    httpReq.returnBadRequest();
                    return;
                }
            }
        }
        HTTPResponse httpRes = new HTTPResponse();
        if (FileUtil.isXMLFileName(uri)) {
            httpRes.setContentType("text/xml; charset=\"utf-8\"");
        }
        httpRes.setStatusCode(200);
        httpRes.setContent(fileByte);
        httpReq.post(httpRes);
    }

    private void httpPostRequestRecieved(HTTPRequest httpReq) {
        if (httpReq.isSOAPAction()) {
            soapActionRecieved(httpReq);
        } else {
            httpReq.returnBadRequest();
        }
    }

    private void soapBadActionRecieved(HTTPRequest soapReq) {
        SOAPResponse soapRes = new SOAPResponse();
        soapRes.setStatusCode(HTTPStatus.BAD_REQUEST);
        soapReq.post(soapRes);
    }

    private void soapActionRecieved(HTTPRequest soapReq) {
        String uri = soapReq.getURI();
        Service ctlService = getServiceByControlURL(uri);
        if (ctlService != null) {
            ActionRequest crlReq = new ActionRequest(soapReq);
            deviceControlRequestRecieved(crlReq, ctlService);
        } else {
            soapBadActionRecieved(soapReq);
        }
    }

    private void deviceControlRequestRecieved(ControlRequest ctlReq, Service service) {
        if (ctlReq.isQueryControl()) {
            deviceQueryControlRecieved(new QueryRequest(ctlReq), service);
        } else {
            deviceActionControlRecieved(new ActionRequest(ctlReq), service);
        }
    }

    private void invalidActionControlRecieved(ControlRequest ctlReq) {
        ControlResponse actRes = new ActionResponse();
        actRes.setFaultResponse(UPnPStatus.INVALID_ACTION);
        ctlReq.post(actRes);
    }

    private void invalidArgumentsControlRecieved(ControlRequest ctlReq) {
        ControlResponse actRes = new ActionResponse();
        actRes.setFaultResponse(UPnPStatus.INVALID_ARGS);
        ctlReq.post(actRes);
    }

    private void deviceActionControlRecieved(ActionRequest ctlReq, Service service) {
        if (Debug.isOn()) {
            ctlReq.print();
        }
        String actionName = ctlReq.getActionName();
        Action action = service.getAction(actionName);
        if (action == null) {
            invalidActionControlRecieved(ctlReq);
            return;
        }
        ArgumentList actionArgList = action.getArgumentList();
        ArgumentList reqArgList = ctlReq.getArgumentList();
        try {
            actionArgList.setReqArgs(reqArgList);
            if (!action.performActionListener(ctlReq)) {
                invalidActionControlRecieved(ctlReq);
            }
        } catch (IllegalArgumentException e) {
            invalidArgumentsControlRecieved(ctlReq);
        }
    }

    private void deviceQueryControlRecieved(QueryRequest ctlReq, Service service) {
        if (Debug.isOn()) {
            ctlReq.print();
        }
        String varName = ctlReq.getVarName();
        if (!service.hasStateVariable(varName)) {
            invalidActionControlRecieved(ctlReq);
            return;
        }
        StateVariable stateVar = getStateVariable(varName);
        if (!stateVar.performQueryListener(ctlReq)) {
            invalidActionControlRecieved(ctlReq);
        }
    }

    private void upnpBadSubscriptionRecieved(SubscriptionRequest subReq, int code) {
        SubscriptionResponse subRes = new SubscriptionResponse();
        subRes.setErrorResponse(code);
        subReq.post(subRes);
    }

    private void deviceEventSubscriptionRecieved(SubscriptionRequest subReq) {
        String uri = subReq.getURI();
        Service service = getServiceByEventSubURL(uri);
        if (service == null) {
            subReq.returnBadRequest();
            return;
        }
        if (!subReq.hasCallback() && !subReq.hasSID()) {
            upnpBadSubscriptionRecieved(subReq, 412);
            return;
        }
        if (subReq.isUnsubscribeRequest()) {
            deviceEventUnsubscriptionRecieved(service, subReq);
            return;
        }
        if (subReq.hasCallback()) {
            deviceEventNewSubscriptionRecieved(service, subReq);
        } else if (subReq.hasSID()) {
            deviceEventRenewSubscriptionRecieved(service, subReq);
        } else {
            upnpBadSubscriptionRecieved(subReq, 412);
        }
    }

    private void deviceEventNewSubscriptionRecieved(Service service, SubscriptionRequest subReq) {
        String callback = subReq.getCallback();
        try {
            new URL(callback);
            long timeOut = subReq.getTimeout();
            String sid = Subscription.createSID();
            Subscriber sub = new Subscriber();
            sub.setDeliveryURL(callback);
            sub.setTimeOut(timeOut);
            sub.setSID(sid);
            service.addSubscriber(sub);
            SubscriptionResponse subRes = new SubscriptionResponse();
            subRes.setStatusCode(200);
            subRes.setSID(sid);
            subRes.setTimeout(timeOut);
            if (Debug.isOn()) {
                subRes.print();
            }
            subReq.post(subRes);
            if (Debug.isOn()) {
                subRes.print();
            }
            service.notifyAllStateVariables();
        } catch (Exception e) {
            upnpBadSubscriptionRecieved(subReq, 412);
        }
    }

    private void deviceEventRenewSubscriptionRecieved(Service service, SubscriptionRequest subReq) {
        String sid = subReq.getSID();
        Subscriber sub = service.getSubscriber(sid);
        if (sub == null) {
            upnpBadSubscriptionRecieved(subReq, 412);
            return;
        }
        long timeOut = subReq.getTimeout();
        sub.setTimeOut(timeOut);
        sub.renew();
        SubscriptionResponse subRes = new SubscriptionResponse();
        subRes.setStatusCode(200);
        subRes.setSID(sid);
        subRes.setTimeout(timeOut);
        subReq.post(subRes);
        if (Debug.isOn()) {
            subRes.print();
        }
    }

    private void deviceEventUnsubscriptionRecieved(Service service, SubscriptionRequest subReq) {
        String sid = subReq.getSID();
        Subscriber sub = service.getSubscriber(sid);
        if (sub == null) {
            upnpBadSubscriptionRecieved(subReq, 412);
            return;
        }
        service.removeSubscriber(sub);
        SubscriptionResponse subRes = new SubscriptionResponse();
        subRes.setStatusCode(200);
        subReq.post(subRes);
        if (Debug.isOn()) {
            subRes.print();
        }
    }

    private HTTPServerList getHTTPServerList() {
        return getDeviceData().getHTTPServerList();
    }

    public void setSSDPPort(int port) {
        getDeviceData().setSSDPPort(port);
    }

    public int getSSDPPort() {
        return getDeviceData().getSSDPPort();
    }

    public void setSSDPBindAddress(InetAddress[] inets) {
        getDeviceData().setSSDPBindAddress(inets);
    }

    public InetAddress[] getSSDPBindAddress() {
        return getDeviceData().getSSDPBindAddress();
    }

    public void setMulticastIPv4Address(String ip) {
        getDeviceData().setMulticastIPv4Address(ip);
    }

    public String getMulticastIPv4Address() {
        return getDeviceData().getMulticastIPv4Address();
    }

    public void setMulticastIPv6Address(String ip) {
        getDeviceData().setMulticastIPv6Address(ip);
    }

    public String getMulticastIPv6Address() {
        return getDeviceData().getMulticastIPv6Address();
    }

    private SSDPSearchSocketList getSSDPSearchSocketList() {
        return getDeviceData().getSSDPSearchSocketList();
    }

    private void setAdvertiser(Advertiser adv) {
        getDeviceData().setAdvertiser(adv);
    }

    private Advertiser getAdvertiser() {
        return getDeviceData().getAdvertiser();
    }

    public boolean start() {
        stop(true);
        int retryCnt = 0;
        int bindPort = getHTTPPort();
        HTTPServerList httpServerList = getHTTPServerList();
        while (!httpServerList.open(bindPort)) {
            retryCnt++;
            if (100 < retryCnt) {
                return false;
            }
            setHTTPPort(bindPort + 1);
            bindPort = getHTTPPort();
        }
        httpServerList.addRequestListener(this);
        httpServerList.start();
        SSDPSearchSocketList ssdpSearchSockList = getSSDPSearchSocketList();
        if (!ssdpSearchSockList.open()) {
            return false;
        }
        ssdpSearchSockList.addSearchListener(this);
        ssdpSearchSockList.start();
        announce();
        Advertiser adv = new Advertiser(this);
        setAdvertiser(adv);
        adv.start();
        return true;
    }

    private boolean stop(boolean doByeBye) {
        if (doByeBye) {
            byebye();
        }
        HTTPServerList httpServerList = getHTTPServerList();
        httpServerList.stop();
        httpServerList.close();
        httpServerList.clear();
        SSDPSearchSocketList ssdpSearchSockList = getSSDPSearchSocketList();
        ssdpSearchSockList.stop();
        ssdpSearchSockList.close();
        ssdpSearchSockList.clear();
        Advertiser adv = getAdvertiser();
        if (adv != null) {
            adv.stop();
            setAdvertiser(null);
            return true;
        }
        return true;
    }

    public boolean stop() {
        return stop(true);
    }

    public boolean isRunning() {
        return getAdvertiser() != null;
    }

    public String getInterfaceAddress() {
        SSDPPacket ssdpPacket = getSSDPPacket();
        return ssdpPacket == null ? "" : ssdpPacket.getLocalAddress();
    }

    public void setActionListener(ActionListener listener) {
        ServiceList serviceList = getServiceList();
        int nServices = serviceList.size();
        for (int n = 0; n < nServices; n++) {
            Service service = serviceList.getService(n);
            service.setActionListener(listener);
        }
    }

    public void setQueryListener(QueryListener listener) {
        ServiceList serviceList = getServiceList();
        int nServices = serviceList.size();
        for (int n = 0; n < nServices; n++) {
            Service service = serviceList.getService(n);
            service.setQueryListener(listener);
        }
    }

    public void setActionListener(ActionListener listener, boolean includeSubDevices) {
        setActionListener(listener);
        if (includeSubDevices) {
            DeviceList devList = getDeviceList();
            int devCnt = devList.size();
            for (int n = 0; n < devCnt; n++) {
                Device dev = devList.getDevice(n);
                dev.setActionListener(listener, true);
            }
        }
    }

    public void setQueryListener(QueryListener listener, boolean includeSubDevices) {
        setQueryListener(listener);
        if (includeSubDevices) {
            DeviceList devList = getDeviceList();
            int devCnt = devList.size();
            for (int n = 0; n < devCnt; n++) {
                Device dev = devList.getDevice(n);
                dev.setQueryListener(listener, true);
            }
        }
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

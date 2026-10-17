package org.cybergarage.upnp;

import com.samsungimaging.connectionmanager.util.Trace;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.ST;
import org.cybergarage.upnp.event.NotifyRequest;
import org.cybergarage.upnp.event.Subscriber;
import org.cybergarage.upnp.event.SubscriberList;
import org.cybergarage.upnp.ssdp.SSDPNotifyRequest;
import org.cybergarage.upnp.ssdp.SSDPNotifySocket;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.upnp.xml.ServiceData;
import org.cybergarage.util.Debug;
import org.cybergarage.util.Mutex;
import org.cybergarage.util.StringUtil;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

/* JADX INFO: loaded from: classes.dex */
public class Service {
    private static final String CONTROL_URL = "controlURL";
    public static final String ELEM_NAME = "service";
    private static final String EVENT_SUB_URL = "eventSubURL";
    public static final String MAJOR = "major";
    public static final String MAJOR_VALUE = "1";
    public static final String MINOR = "minor";
    public static final String MINOR_VALUE = "0";
    private static final String SCPDURL = "SCPDURL";
    public static final String SCPD_ROOTNODE = "scpd";
    public static final String SCPD_ROOTNODE_NS = "urn:schemas-upnp-org:service-1-0";
    private static final String SERVICE_ID = "serviceId";
    private static final String SERVICE_TYPE = "serviceType";
    public static final String SPEC_VERSION = "specVersion";
    private Trace.Tag TAG;
    private Mutex mutex;
    private Node serviceNode;
    private Object userData;

    public Node getServiceNode() {
        return this.serviceNode;
    }

    public Service() {
        this(new Node("service"));
        Node sp = new Node("specVersion");
        Node M = new Node("major");
        M.setValue(MAJOR_VALUE);
        sp.addNode(M);
        Node m = new Node("minor");
        m.setValue("0");
        sp.addNode(m);
        Node scpd = new Node(SCPD_ROOTNODE);
        scpd.addAttribute("xmlns", SCPD_ROOTNODE_NS);
        scpd.addNode(sp);
        getServiceData().setSCPDNode(scpd);
    }

    public Service(Node node) {
        this.TAG = Trace.Tag.CYBERGARAGE;
        this.mutex = new Mutex();
        this.userData = null;
        this.serviceNode = node;
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }

    public static boolean isServiceNode(Node node) {
        return "service".equals(node.getName());
    }

    private Node getDeviceNode() {
        Node node = getServiceNode().getParentNode();
        if (node == null) {
            return null;
        }
        return node.getParentNode();
    }

    private Node getRootNode() {
        return getServiceNode().getRootNode();
    }

    public Device getDevice() {
        return new Device(getRootNode(), getDeviceNode());
    }

    public Device getRootDevice() {
        return getDevice().getRootDevice();
    }

    public void setServiceType(String value) {
        getServiceNode().setNode(SERVICE_TYPE, value);
    }

    public String getServiceType() {
        return getServiceNode().getNodeValue(SERVICE_TYPE);
    }

    public void setServiceID(String value) {
        getServiceNode().setNode(SERVICE_ID, value);
    }

    public String getServiceID() {
        return getServiceNode().getNodeValue(SERVICE_ID);
    }

    private boolean isURL(String referenceUrl, String url) {
        if (referenceUrl == null || url == null) {
            return false;
        }
        boolean ret = url.equals(referenceUrl);
        if (ret) {
            return true;
        }
        String relativeRefUrl = HTTP.toRelativeURL(referenceUrl, false);
        boolean ret2 = url.equals(relativeRefUrl);
        return ret2;
    }

    public void setSCPDURL(String value) {
        getServiceNode().setNode(SCPDURL, value);
    }

    public String getSCPDURL() {
        return getServiceNode().getNodeValue(SCPDURL);
    }

    public boolean isSCPDURL(String url) {
        return isURL(getSCPDURL(), url);
    }

    public void setControlURL(String value) {
        getServiceNode().setNode(CONTROL_URL, value);
    }

    public String getControlURL() {
        return getServiceNode().getNodeValue(CONTROL_URL);
    }

    public boolean isControlURL(String url) {
        return isURL(getControlURL(), url);
    }

    public void setEventSubURL(String value) {
        getServiceNode().setNode(EVENT_SUB_URL, value);
    }

    public String getEventSubURL() {
        return getServiceNode().getNodeValue(EVENT_SUB_URL);
    }

    public boolean isEventSubURL(String url) {
        return isURL(getEventSubURL(), url);
    }

    public boolean loadSCPD(String scpdStr) throws InvalidDescriptionException {
        try {
            Parser parser = UPnP.getXMLParser();
            Node scpdNode = parser.parse(scpdStr);
            if (scpdNode == null) {
                return false;
            }
            ServiceData data = getServiceData();
            data.setSCPDNode(scpdNode);
            return true;
        } catch (ParserException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    public boolean loadSCPD(File file) throws ParserException {
        Parser parser = UPnP.getXMLParser();
        Node scpdNode = parser.parse(file);
        if (scpdNode == null) {
            return false;
        }
        ServiceData data = getServiceData();
        data.setSCPDNode(scpdNode);
        return true;
    }

    public boolean loadSCPD(InputStream input) throws ParserException {
        Parser parser = UPnP.getXMLParser();
        Node scpdNode = parser.parse(input);
        if (scpdNode == null) {
            return false;
        }
        ServiceData data = getServiceData();
        data.setSCPDNode(scpdNode);
        return true;
    }

    public void setDescriptionURL(String value) {
        getServiceData().setDescriptionURL(value);
    }

    public String getDescriptionURL() {
        return getServiceData().getDescriptionURL();
    }

    private Node getSCPDNode(URL scpdUrl) throws ParserException {
        Parser parser = UPnP.getXMLParser();
        return parser.parse(scpdUrl, null, null);
    }

    private Node getSCPDNode(File scpdFile) throws ParserException {
        Parser parser = UPnP.getXMLParser();
        return parser.parse(scpdFile);
    }

    private Node getSCPDNode() {
        ServiceData data = getServiceData();
        Node scpdNode = data.getSCPDNode();
        if (scpdNode != null) {
            return scpdNode;
        }
        Device rootDev = getDevice();
        if (rootDev == null) {
            return null;
        }
        String scpdURLStr = getSCPDURL();
        try {
            URL scpdUrl = new URL(rootDev.getAbsoluteURL(scpdURLStr));
            Node scpdNode2 = getSCPDNode(scpdUrl);
            if (scpdNode2 != null) {
                data.setSCPDNode(scpdNode2);
                return scpdNode2;
            }
        } catch (Exception e) {
        }
        String newScpdURLStr = String.valueOf(rootDev.getDescriptionFilePath()) + HTTP.toRelativeURL(scpdURLStr);
        try {
            getSCPDNode(new File(newScpdURLStr));
            return null;
        } catch (Exception e2) {
            Debug.warning(e2);
            return null;
        }
    }

    public byte[] getSCPDData() {
        Node scpdNode = getSCPDNode();
        if (scpdNode == null) {
            return new byte[0];
        }
        String desc = new String();
        return (String.valueOf(String.valueOf(String.valueOf(desc) + "<?xml version=\"1.0\" encoding=\"utf-8\"?>") + "\n") + scpdNode.toString()).getBytes();
    }

    public ActionList getActionList() {
        Node actionListNode;
        ActionList actionList = new ActionList();
        Node scdpNode = getSCPDNode();
        if (scdpNode != null && (actionListNode = scdpNode.getNode(ActionList.ELEM_NAME)) != null) {
            int nNode = actionListNode.getNNodes();
            for (int n = 0; n < nNode; n++) {
                Node node = actionListNode.getNode(n);
                if (Action.isActionNode(node)) {
                    Action action = new Action(this.serviceNode, node);
                    Trace.d(this.TAG, "action Name : " + action.getName());
                    actionList.add(action);
                }
            }
        }
        return actionList;
    }

    public Action getAction(String actionName) {
        ActionList actionList = getActionList();
        int nActions = actionList.size();
        for (int n = 0; n < nActions; n++) {
            Action action = actionList.getAction(n);
            String name = action.getName();
            if (name != null && name.equals(actionName)) {
                return action;
            }
        }
        return null;
    }

    public void addAction(Action a) {
        for (Argument arg : a.getArgumentList()) {
            arg.setService(this);
        }
        Node scdpNode = getSCPDNode();
        Node actionListNode = scdpNode.getNode(ActionList.ELEM_NAME);
        if (actionListNode == null) {
            actionListNode = new Node(ActionList.ELEM_NAME);
            scdpNode.addNode(actionListNode);
        }
        actionListNode.addNode(a.getActionNode());
    }

    public ServiceStateTable getServiceStateTable() {
        ServiceStateTable stateTable = new ServiceStateTable();
        Node stateTableNode = getSCPDNode().getNode(ServiceStateTable.ELEM_NAME);
        if (stateTableNode != null) {
            Node serviceNode = getServiceNode();
            int nNode = stateTableNode.getNNodes();
            for (int n = 0; n < nNode; n++) {
                Node node = stateTableNode.getNode(n);
                if (StateVariable.isStateVariableNode(node)) {
                    StateVariable serviceVar = new StateVariable(serviceNode, node);
                    stateTable.add(serviceVar);
                }
            }
        }
        return stateTable;
    }

    public StateVariable getStateVariable(String name) {
        ServiceStateTable stateTable = getServiceStateTable();
        int tableSize = stateTable.size();
        for (int n = 0; n < tableSize; n++) {
            StateVariable var = stateTable.getStateVariable(n);
            String varName = var.getName();
            if (varName != null && varName.equals(name)) {
                return var;
            }
        }
        return null;
    }

    public boolean hasStateVariable(String name) {
        return getStateVariable(name) != null;
    }

    public boolean isService(String name) {
        if (name == null) {
            return false;
        }
        return name.endsWith(getServiceType()) || name.endsWith(getServiceID());
    }

    private ServiceData getServiceData() {
        Node node = getServiceNode();
        ServiceData userData = (ServiceData) node.getUserData();
        if (userData == null) {
            ServiceData userData2 = new ServiceData();
            node.setUserData(userData2);
            userData2.setNode(node);
            return userData2;
        }
        return userData;
    }

    private String getNotifyServiceTypeNT() {
        return getServiceType();
    }

    private String getNotifyServiceTypeUSN() {
        return String.valueOf(getDevice().getUDN()) + "::" + getServiceType();
    }

    public void announce(String bindAddr) {
        Device rootDev = getRootDevice();
        String devLocation = rootDev.getLocationURL(bindAddr);
        String serviceNT = getNotifyServiceTypeNT();
        String serviceUSN = getNotifyServiceTypeUSN();
        Device dev = getDevice();
        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setServer(UPnP.getServerName());
        ssdpReq.setLeaseTime(dev.getLeaseTime());
        ssdpReq.setLocation(devLocation);
        ssdpReq.setNTS(NTS.ALIVE);
        ssdpReq.setNT(serviceNT);
        ssdpReq.setUSN(serviceUSN);
        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);
        Device.notifyWait();
        ssdpSock.post(ssdpReq);
    }

    public void byebye(String bindAddr) {
        String devNT = getNotifyServiceTypeNT();
        String devUSN = getNotifyServiceTypeUSN();
        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setNTS(NTS.BYEBYE);
        ssdpReq.setNT(devNT);
        ssdpReq.setUSN(devUSN);
        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);
        Device.notifyWait();
        ssdpSock.post(ssdpReq);
    }

    public boolean serviceSearchResponse(SSDPPacket ssdpPacket) {
        String ssdpST = ssdpPacket.getST();
        if (ssdpST == null) {
            return false;
        }
        Device dev = getDevice();
        String serviceNT = getNotifyServiceTypeNT();
        String serviceUSN = getNotifyServiceTypeUSN();
        if (ST.isAllDevice(ssdpST)) {
            dev.postSearchResponse(ssdpPacket, serviceNT, serviceUSN);
        } else if (ST.isURNService(ssdpST)) {
            String serviceType = getServiceType();
            if (ssdpST.equals(serviceType)) {
                dev.postSearchResponse(ssdpPacket, serviceType, serviceUSN);
            }
        }
        return true;
    }

    public void setQueryListener(QueryListener queryListener) {
        ServiceStateTable stateTable = getServiceStateTable();
        int tableSize = stateTable.size();
        for (int n = 0; n < tableSize; n++) {
            StateVariable var = stateTable.getStateVariable(n);
            var.setQueryListener(queryListener);
        }
    }

    public SubscriberList getSubscriberList() {
        return getServiceData().getSubscriberList();
    }

    public void addSubscriber(Subscriber sub) {
        getSubscriberList().add(sub);
    }

    public void removeSubscriber(Subscriber sub) {
        getSubscriberList().remove(sub);
    }

    public Subscriber getSubscriber(String name) {
        String sid;
        SubscriberList subList = getSubscriberList();
        int subListCnt = subList.size();
        for (int n = 0; n < subListCnt; n++) {
            Subscriber sub = subList.getSubscriber(n);
            if (sub != null && (sid = sub.getSID()) != null && sid.equals(name)) {
                return sub;
            }
        }
        return null;
    }

    private boolean notify(Subscriber sub, StateVariable stateVar) {
        String varName = stateVar.getName();
        String value = stateVar.getValue();
        String host = sub.getDeliveryHost();
        int port = sub.getDeliveryPort();
        NotifyRequest notifyReq = new NotifyRequest();
        notifyReq.setRequest(sub, varName, value);
        HTTPResponse res = notifyReq.post(host, port);
        if (!res.isSuccessful()) {
            return false;
        }
        sub.incrementNotifyCount();
        return true;
    }

    public void notify(StateVariable stateVar) {
        SubscriberList subList = getSubscriberList();
        int subListCnt = subList.size();
        Subscriber[] subs = new Subscriber[subListCnt];
        for (int n = 0; n < subListCnt; n++) {
            subs[n] = subList.getSubscriber(n);
        }
        for (int n2 = 0; n2 < subListCnt; n2++) {
            Subscriber sub = subs[n2];
            if (sub != null && sub.isExpired()) {
                removeSubscriber(sub);
            }
        }
        int subListCnt2 = subList.size();
        Subscriber[] subs2 = new Subscriber[subListCnt2];
        for (int n3 = 0; n3 < subListCnt2; n3++) {
            subs2[n3] = subList.getSubscriber(n3);
        }
        for (int n4 = 0; n4 < subListCnt2; n4++) {
            Subscriber sub2 = subs2[n4];
            if (sub2 != null) {
                notify(sub2, stateVar);
            }
        }
    }

    public void notifyAllStateVariables() {
        ServiceStateTable stateTable = getServiceStateTable();
        int tableSize = stateTable.size();
        for (int n = 0; n < tableSize; n++) {
            StateVariable var = stateTable.getStateVariable(n);
            if (var.isSendEvents()) {
                notify(var);
            }
        }
    }

    public String getSID() {
        return getServiceData().getSID();
    }

    public void setSID(String id) {
        getServiceData().setSID(id);
    }

    public void clearSID() {
        setSID("");
        setTimeout(0L);
    }

    public boolean hasSID() {
        return StringUtil.hasData(getSID());
    }

    public boolean isSubscribed() {
        return hasSID();
    }

    public long getTimeout() {
        return getServiceData().getTimeout();
    }

    public void setTimeout(long value) {
        getServiceData().setTimeout(value);
    }

    public void setActionListener(ActionListener listener) {
        ActionList actionList = getActionList();
        int nActions = actionList.size();
        for (int n = 0; n < nActions; n++) {
            Action action = actionList.getAction(n);
            action.setActionListener(listener);
        }
    }

    public void addStateVariable(StateVariable var) {
        Node stateTableNode = getSCPDNode().getNode(ServiceStateTable.ELEM_NAME);
        if (stateTableNode == null) {
            stateTableNode = new Node(ServiceStateTable.ELEM_NAME);
            getSCPDNode().addNode(stateTableNode);
        }
        var.setServiceNode(getServiceNode());
        stateTableNode.addNode(var.getStateVariableNode());
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

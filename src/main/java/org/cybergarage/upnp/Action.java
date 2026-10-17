package org.cybergarage.upnp;

import java.util.Iterator;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.ActionRequest;
import org.cybergarage.upnp.control.ActionResponse;
import org.cybergarage.upnp.control.ControlResponse;
import org.cybergarage.upnp.xml.ActionData;
import org.cybergarage.util.Debug;
import org.cybergarage.util.Mutex;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class Action {
    public static final String ELEM_NAME = "action";
    private static final String NAME = "name";
    private ArgumentList actionArgList;
    private Node actionNode;
    private boolean bSuccess;
    private ActionResponse ctrlRes;
    private Mutex mutex;
    private Node serviceNode;
    private UPnPStatus upnpStatus;
    private Object userData;

    private Node getServiceNode() {
        return this.serviceNode;
    }

    public Service getService() {
        return new Service(getServiceNode());
    }

    void setService(Service s) {
        this.serviceNode = s.getServiceNode();
        for (Argument arg : getArgumentList()) {
            arg.setService(s);
        }
    }

    public Node getActionNode() {
        return this.actionNode;
    }

    public Action(Node serviceNode) {
        this.ctrlRes = null;
        this.bSuccess = false;
        this.actionArgList = null;
        this.mutex = new Mutex();
        this.upnpStatus = new UPnPStatus();
        this.userData = null;
        this.serviceNode = serviceNode;
        this.actionNode = new Node(ELEM_NAME);
    }

    public Action(Node serviceNode, Node actionNode) {
        this.ctrlRes = null;
        this.bSuccess = false;
        this.actionArgList = null;
        this.mutex = new Mutex();
        this.upnpStatus = new UPnPStatus();
        this.userData = null;
        this.serviceNode = serviceNode;
        this.actionNode = actionNode;
    }

    public Action(Action action) {
        this.ctrlRes = null;
        this.bSuccess = false;
        this.actionArgList = null;
        this.mutex = new Mutex();
        this.upnpStatus = new UPnPStatus();
        this.userData = null;
        this.serviceNode = action.getServiceNode();
        this.actionNode = action.getActionNode();
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }

    public static boolean isActionNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    public void setName(String value) {
        getActionNode().setNode(NAME, value);
    }

    public String getName() {
        return getActionNode().getNodeValue(NAME);
    }

    public ArgumentList getArgumentList() {
        ArgumentList argumentList = new ArgumentList();
        Node argumentListNode = getActionNode().getNode(ArgumentList.ELEM_NAME);
        if (argumentListNode != null) {
            int nodeCnt = argumentListNode.getNNodes();
            for (int n = 0; n < nodeCnt; n++) {
                Node node = argumentListNode.getNode(n);
                if (Argument.isArgumentNode(node)) {
                    Argument argument = new Argument(getServiceNode(), node);
                    argumentList.add(argument);
                }
            }
        }
        return argumentList;
    }

    public void setArgumentList(ArgumentList al) {
        Node argumentListNode = getActionNode().getNode(ArgumentList.ELEM_NAME);
        if (argumentListNode == null) {
            argumentListNode = new Node(ArgumentList.ELEM_NAME);
            getActionNode().addNode(argumentListNode);
        } else {
            argumentListNode.removeAllNodes();
        }
        Iterator i = al.iterator();
        while (i.hasNext()) {
            Argument a = (Argument) i.next();
            a.setService(getService());
            argumentListNode.addNode(a.getArgumentNode());
        }
    }

    public ArgumentList getInputArgumentList() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        ArgumentList argList = new ArgumentList();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (arg.isInDirection()) {
                argList.add(arg);
            }
        }
        return argList;
    }

    public ArgumentList getOutputArgumentList() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        ArgumentList argList = new ArgumentList();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (arg.isOutDirection()) {
                argList.add(arg);
            }
        }
        return argList;
    }

    public Argument getArgument(String name) {
        ArgumentList argList = getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            String argName = arg.getName();
            if (argName != null && name.equals(argName)) {
                return arg;
            }
        }
        return null;
    }

    public void setArgumentValues(ArgumentList argList) {
        getArgumentList().set(argList);
    }

    public void setInArgumentValues(ArgumentList argList) {
        getArgumentList().setReqArgs(argList);
    }

    public void setOutArgumentValues(ArgumentList argList) {
        getArgumentList().setResArgs(argList);
    }

    public void setArgumentValue(String name, String value) {
        Argument arg = getArgument(name);
        if (arg != null) {
            arg.setValue(value);
        }
    }

    public void setArgumentValue(String name, int value) {
        setArgumentValue(name, Integer.toString(value));
    }

    private void clearOutputAgumentValues() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (arg.isOutDirection()) {
                arg.setValue("");
            }
        }
    }

    public String getArgumentValue(String name) {
        Argument arg = getArgument(name);
        return arg == null ? "" : arg.getValue();
    }

    public int getArgumentIntegerValue(String name) {
        Argument arg = getArgument(name);
        if (arg == null) {
            return 0;
        }
        return arg.getIntegerValue();
    }

    private ActionData getActionData() {
        Node node = getActionNode();
        ActionData userData = (ActionData) node.getUserData();
        if (userData == null) {
            ActionData userData2 = new ActionData();
            node.setUserData(userData2);
            userData2.setNode(node);
            return userData2;
        }
        return userData;
    }

    public ActionListener getActionListener() {
        return getActionData().getActionListener();
    }

    public void setActionListener(ActionListener listener) {
        getActionData().setActionListener(listener);
    }

    public boolean performActionListener(ActionRequest actionReq) {
        ActionListener listener = getActionListener();
        if (listener == null) {
            return false;
        }
        ActionResponse actionRes = new ActionResponse();
        setStatus(UPnPStatus.INVALID_ACTION);
        clearOutputAgumentValues();
        if (listener.actionControlReceived(this)) {
            actionRes.setResponse(this);
        } else {
            UPnPStatus upnpStatus = getStatus();
            actionRes.setFaultResponse(upnpStatus.getCode(), upnpStatus.getDescription());
        }
        if (Debug.isOn()) {
            actionRes.print();
        }
        actionReq.post(actionRes);
        return true;
    }

    public ControlResponse getControlResponse() {
        return getActionData().getControlResponse();
    }

    private void setControlResponse(ControlResponse res) {
        getActionData().setControlResponse(res);
    }

    public UPnPStatus getControlStatus() {
        return getControlResponse().getUPnPError();
    }

    public boolean postControlAction() {
        ArgumentList actionArgList = getArgumentList();
        ArgumentList actionInputArgList = getInputArgumentList();
        ActionRequest ctrlReq = new ActionRequest();
        ctrlReq.setRequest(this, actionInputArgList);
        if (Debug.isOn()) {
            ctrlReq.print();
        }
        ActionResponse ctrlRes = ctrlReq.post();
        if (Debug.isOn()) {
            ctrlRes.print();
        }
        setControlResponse(ctrlRes);
        int statCode = ctrlRes.getStatusCode();
        setStatus(statCode);
        if (!ctrlRes.isSuccessful()) {
            return false;
        }
        ArgumentList outArgList = ctrlRes.getResponse();
        try {
            actionArgList.setResArgs(outArgList);
            return true;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            setStatus(UPnPStatus.INVALID_ARGS, "Action succesfully delivered but invalid arguments returned.");
            return false;
        }
    }

    public void print() {
        System.out.println("Action : " + getName());
        ArgumentList argList = getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            String name = arg.getName();
            String value = arg.getValue();
            String dir = arg.getDirection();
            System.out.println(" [" + n + "] = " + dir + ", " + name + ", " + value);
        }
    }

    public void setStatus(int code, String descr) {
        this.upnpStatus.setCode(code);
        this.upnpStatus.setDescription(descr);
    }

    public void setStatus(int code) {
        setStatus(code, UPnPStatus.code2String(code));
    }

    public UPnPStatus getStatus() {
        return this.upnpStatus;
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

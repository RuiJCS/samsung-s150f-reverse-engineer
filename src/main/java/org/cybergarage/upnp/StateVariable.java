package org.cybergarage.upnp;

import java.util.Iterator;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.control.QueryRequest;
import org.cybergarage.upnp.control.QueryResponse;
import org.cybergarage.upnp.xml.NodeData;
import org.cybergarage.upnp.xml.StateVariableData;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class StateVariable extends NodeData {
    private static final String DATATYPE = "dataType";
    private static final String DEFAULT_VALUE = "defaultValue";
    public static final String ELEM_NAME = "stateVariable";
    private static final String NAME = "name";
    private static final String SENDEVENTS = "sendEvents";
    private static final String SENDEVENTS_NO = "no";
    private static final String SENDEVENTS_YES = "yes";
    private Node serviceNode;
    private Node stateVariableNode;
    private UPnPStatus upnpStatus;
    private Object userData;

    public Node getServiceNode() {
        return this.serviceNode;
    }

    void setServiceNode(Node n) {
        this.serviceNode = n;
    }

    public Service getService() {
        Node serviceNode = getServiceNode();
        if (serviceNode == null) {
            return null;
        }
        return new Service(serviceNode);
    }

    public Node getStateVariableNode() {
        return this.stateVariableNode;
    }

    public StateVariable() {
        this.upnpStatus = new UPnPStatus();
        this.userData = null;
        this.serviceNode = null;
        this.stateVariableNode = new Node(ELEM_NAME);
    }

    public StateVariable(Node serviceNode, Node stateVarNode) {
        this.upnpStatus = new UPnPStatus();
        this.userData = null;
        this.serviceNode = serviceNode;
        this.stateVariableNode = stateVarNode;
    }

    public static boolean isStateVariableNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    public void setName(String value) {
        getStateVariableNode().setNode(NAME, value);
    }

    public String getName() {
        return getStateVariableNode().getNodeValue(NAME);
    }

    public void setDataType(String value) {
        getStateVariableNode().setNode(DATATYPE, value);
    }

    public String getDataType() {
        return getStateVariableNode().getNodeValue(DATATYPE);
    }

    public void setSendEvents(boolean state) {
        getStateVariableNode().setAttribute(SENDEVENTS, state ? SENDEVENTS_YES : SENDEVENTS_NO);
    }

    public boolean isSendEvents() {
        String state = getStateVariableNode().getAttributeValue(SENDEVENTS);
        return state != null && state.equalsIgnoreCase(SENDEVENTS_YES);
    }

    public void set(StateVariable stateVar) {
        setName(stateVar.getName());
        setValue(stateVar.getValue());
        setDataType(stateVar.getDataType());
        setSendEvents(stateVar.isSendEvents());
    }

    public StateVariableData getStateVariableData() {
        Node node = getStateVariableNode();
        StateVariableData userData = (StateVariableData) node.getUserData();
        if (userData == null) {
            StateVariableData userData2 = new StateVariableData();
            node.setUserData(userData2);
            userData2.setNode(node);
            return userData2;
        }
        return userData;
    }

    public void setValue(String value) {
        String currValue = getStateVariableData().getValue();
        if (currValue == null || !currValue.equals(value)) {
            getStateVariableData().setValue(value);
            Service service = getService();
            if (service != null && isSendEvents()) {
                service.notify(this);
            }
        }
    }

    public void setValue(int value) {
        setValue(Integer.toString(value));
    }

    public void setValue(long value) {
        setValue(Long.toString(value));
    }

    public String getValue() {
        return getStateVariableData().getValue();
    }

    public AllowedValueList getAllowedValueList() {
        AllowedValueList valueList = new AllowedValueList();
        Node valueListNode = getStateVariableNode().getNode(AllowedValueList.ELEM_NAME);
        if (valueListNode == null) {
            return null;
        }
        int nNode = valueListNode.getNNodes();
        for (int n = 0; n < nNode; n++) {
            Node node = valueListNode.getNode(n);
            if (AllowedValue.isAllowedValueNode(node)) {
                AllowedValue allowedVal = new AllowedValue(node);
                valueList.add(allowedVal);
            }
        }
        return valueList;
    }

    public void setAllowedValueList(AllowedValueList avl) {
        getStateVariableNode().removeNode(AllowedValueList.ELEM_NAME);
        getStateVariableNode().removeNode(AllowedValueRange.ELEM_NAME);
        Node n = new Node(AllowedValueList.ELEM_NAME);
        Iterator i = avl.iterator();
        while (i.hasNext()) {
            AllowedValue av = (AllowedValue) i.next();
            n.addNode(av.getAllowedValueNode());
        }
        getStateVariableNode().addNode(n);
    }

    public boolean hasAllowedValueList() {
        AllowedValueList valueList = getAllowedValueList();
        return valueList != null;
    }

    public AllowedValueRange getAllowedValueRange() {
        Node valueRangeNode = getStateVariableNode().getNode(AllowedValueRange.ELEM_NAME);
        if (valueRangeNode == null) {
            return null;
        }
        return new AllowedValueRange(valueRangeNode);
    }

    public void setAllowedValueRange(AllowedValueRange avr) {
        getStateVariableNode().removeNode(AllowedValueList.ELEM_NAME);
        getStateVariableNode().removeNode(AllowedValueRange.ELEM_NAME);
        getStateVariableNode().addNode(avr.getAllowedValueRangeNode());
    }

    public boolean hasAllowedValueRange() {
        return getAllowedValueRange() != null;
    }

    public QueryListener getQueryListener() {
        return getStateVariableData().getQueryListener();
    }

    public void setQueryListener(QueryListener listener) {
        getStateVariableData().setQueryListener(listener);
    }

    public boolean performQueryListener(QueryRequest queryReq) {
        QueryListener listener = getQueryListener();
        if (listener == null) {
            return false;
        }
        QueryResponse queryRes = new QueryResponse();
        StateVariable retVar = new StateVariable();
        retVar.set(this);
        retVar.setValue("");
        retVar.setStatus(404);
        if (listener.queryControlReceived(retVar)) {
            queryRes.setResponse(retVar);
        } else {
            UPnPStatus upnpStatus = retVar.getStatus();
            queryRes.setFaultResponse(upnpStatus.getCode(), upnpStatus.getDescription());
        }
        queryReq.post(queryRes);
        return true;
    }

    public QueryResponse getQueryResponse() {
        return getStateVariableData().getQueryResponse();
    }

    private void setQueryResponse(QueryResponse res) {
        getStateVariableData().setQueryResponse(res);
    }

    public UPnPStatus getQueryStatus() {
        return getQueryResponse().getUPnPError();
    }

    public boolean postQuerylAction() {
        QueryRequest queryReq = new QueryRequest();
        queryReq.setRequest(this);
        if (Debug.isOn()) {
            queryReq.print();
        }
        QueryResponse queryRes = queryReq.post();
        if (Debug.isOn()) {
            queryRes.print();
        }
        setQueryResponse(queryRes);
        if (!queryRes.isSuccessful()) {
            setValue(queryRes.getReturnValue());
            return false;
        }
        setValue(queryRes.getReturnValue());
        return true;
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

    public String getDefaultValue() {
        return getStateVariableNode().getNodeValue(DEFAULT_VALUE);
    }

    public void setDefaultValue(String value) {
        getStateVariableNode().setNode(DEFAULT_VALUE, value);
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

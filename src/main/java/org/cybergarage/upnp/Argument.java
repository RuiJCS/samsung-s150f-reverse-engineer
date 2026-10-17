package org.cybergarage.upnp;

import org.cybergarage.upnp.xml.ArgumentData;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class Argument {
    private static final String DIRECTION = "direction";
    public static final String ELEM_NAME = "argument";
    public static final String IN = "in";
    private static final String NAME = "name";
    public static final String OUT = "out";
    private static final String RELATED_STATE_VARIABLE = "relatedStateVariable";
    private Node argumentNode;
    private Node serviceNode;
    private Object userData;

    public Node getArgumentNode() {
        return this.argumentNode;
    }

    private Node getServiceNode() {
        return this.serviceNode;
    }

    public Service getService() {
        return new Service(getServiceNode());
    }

    void setService(Service s) {
        s.getServiceNode();
    }

    public Node getActionNode() {
        Node actionNode;
        Node argumentLinstNode = getArgumentNode().getParentNode();
        if (argumentLinstNode == null || (actionNode = argumentLinstNode.getParentNode()) == null || !Action.isActionNode(actionNode)) {
            return null;
        }
        return actionNode;
    }

    public Action getAction() {
        return new Action(getServiceNode(), getActionNode());
    }

    public Argument() {
        this.userData = null;
        this.argumentNode = new Node(ELEM_NAME);
        this.serviceNode = null;
    }

    public Argument(Node servNode) {
        this.userData = null;
        this.argumentNode = new Node(ELEM_NAME);
        this.serviceNode = servNode;
    }

    public Argument(Node servNode, Node argNode) {
        this.userData = null;
        this.serviceNode = servNode;
        this.argumentNode = argNode;
    }

    public Argument(String name, String value) {
        this();
        setName(name);
        setValue(value);
    }

    public static boolean isArgumentNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    public void setName(String value) {
        getArgumentNode().setNode(NAME, value);
    }

    public String getName() {
        return getArgumentNode().getNodeValue(NAME);
    }

    public void setDirection(String value) {
        getArgumentNode().setNode(DIRECTION, value);
    }

    public String getDirection() {
        return getArgumentNode().getNodeValue(DIRECTION);
    }

    public boolean isInDirection() {
        String dir = getDirection();
        if (dir == null) {
            return false;
        }
        return dir.equalsIgnoreCase(IN);
    }

    public boolean isOutDirection() {
        return !isInDirection();
    }

    public void setRelatedStateVariableName(String value) {
        getArgumentNode().setNode(RELATED_STATE_VARIABLE, value);
    }

    public String getRelatedStateVariableName() {
        return getArgumentNode().getNodeValue(RELATED_STATE_VARIABLE);
    }

    public StateVariable getRelatedStateVariable() {
        Service service = getService();
        if (service == null) {
            return null;
        }
        String relatedStatVarName = getRelatedStateVariableName();
        return service.getStateVariable(relatedStatVarName);
    }

    private ArgumentData getArgumentData() {
        Node node = getArgumentNode();
        ArgumentData userData = (ArgumentData) node.getUserData();
        if (userData == null) {
            ArgumentData userData2 = new ArgumentData();
            node.setUserData(userData2);
            userData2.setNode(node);
            return userData2;
        }
        return userData;
    }

    public void setValue(String value) {
        getArgumentData().setValue(value);
    }

    public void setValue(int value) {
        setValue(Integer.toString(value));
    }

    public String getValue() {
        return getArgumentData().getValue();
    }

    public int getIntegerValue() {
        String value = getValue();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

package org.cybergarage.upnp.control;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.soap.SOAP;
import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Service;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class ActionRequest extends ControlRequest {
    public ActionRequest() {
    }

    public ActionRequest(HTTPRequest httpReq) {
        set(httpReq);
    }

    public Node getActionNode() {
        Node bodyNode = getBodyNode();
        if (bodyNode != null && bodyNode.hasNodes()) {
            return bodyNode.getNode(0);
        }
        return null;
    }

    public String getActionName() {
        String name;
        int idx;
        Node node = getActionNode();
        if (node == null || (name = node.getName()) == null || (idx = name.indexOf(SOAP.DELIM) + 1) < 0) {
            return "";
        }
        return name.substring(idx, name.length());
    }

    public ArgumentList getArgumentList() {
        Node actNode = getActionNode();
        int nArgNodes = actNode.getNNodes();
        ArgumentList argList = new ArgumentList();
        for (int n = 0; n < nArgNodes; n++) {
            Argument arg = new Argument();
            Node argNode = actNode.getNode(n);
            arg.setName(argNode.getName());
            arg.setValue(argNode.getValue());
            argList.add(arg);
        }
        return argList;
    }

    public void setRequest(Action action, ArgumentList argList) {
        Service service = action.getService();
        setRequestHost(service);
        setEnvelopeNode(SOAP.createEnvelopeBodyNode());
        Node envNode = getEnvelopeNode();
        Node bodyNode = getBodyNode();
        Node argNode = createContentNode(service, action, argList);
        bodyNode.addNode(argNode);
        setContent(envNode);
        String serviceType = service.getServiceType();
        String actionName = action.getName();
        String soapAction = "\"" + serviceType + "#" + actionName + "\"";
        setSOAPAction(soapAction);
    }

    private Node createContentNode(Service service, Action action, ArgumentList argList) {
        String actionName = action.getName();
        String serviceType = service.getServiceType();
        Node actionNode = new Node();
        actionNode.setName("u", actionName);
        actionNode.setNameSpace("u", serviceType);
        int argListCnt = argList.size();
        for (int n = 0; n < argListCnt; n++) {
            Argument arg = argList.getArgument(n);
            Node argNode = new Node();
            argNode.setName(arg.getName());
            argNode.setValue(arg.getValue());
            actionNode.addNode(argNode);
        }
        return actionNode;
    }

    public ActionResponse post() {
        SOAPResponse soapRes = postMessage(getRequestHost(), getRequestPort());
        return new ActionResponse(soapRes);
    }
}

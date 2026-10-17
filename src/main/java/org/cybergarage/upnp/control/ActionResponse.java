package org.cybergarage.upnp.control;

import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;
import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Service;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class ActionResponse extends ControlResponse {
    public ActionResponse() {
        setHeader(HTTP.EXT, "");
    }

    public ActionResponse(SOAPResponse soapRes) {
        super(soapRes);
        setHeader(HTTP.EXT, "");
    }

    public void setResponse(Action action) {
        setStatusCode(200);
        Node bodyNode = getBodyNode();
        Node resNode = createResponseNode(action);
        bodyNode.addNode(resNode);
        Node envNode = getEnvelopeNode();
        setContent(envNode);
    }

    private Node createResponseNode(Action action) {
        String actionName = action.getName();
        Node actionNameResNode = new Node("u:" + actionName + SOAP.RESPONSE);
        Service service = action.getService();
        if (service != null) {
            actionNameResNode.setAttribute("xmlns:u", service.getServiceType());
        }
        ArgumentList argList = action.getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            if (arg.isOutDirection()) {
                Node argNode = new Node();
                argNode.setName(arg.getName());
                argNode.setValue(arg.getValue());
                actionNameResNode.addNode(argNode);
            }
        }
        return actionNameResNode;
    }

    private Node getActionResponseNode() {
        Node bodyNode = getBodyNode();
        if (bodyNode == null || !bodyNode.hasNodes()) {
            return null;
        }
        return bodyNode.getNode(0);
    }

    public ArgumentList getResponse() {
        ArgumentList argList = new ArgumentList();
        Node resNode = getActionResponseNode();
        if (resNode != null) {
            int nArgs = resNode.getNNodes();
            for (int n = 0; n < nArgs; n++) {
                Node node = resNode.getNode(n);
                String name = node.getName();
                String value = node.getValue();
                Argument arg = new Argument(name, value);
                argList.add(arg);
            }
        }
        return argList;
    }
}

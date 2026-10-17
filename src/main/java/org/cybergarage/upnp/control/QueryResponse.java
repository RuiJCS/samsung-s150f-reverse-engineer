package org.cybergarage.upnp.control;

import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class QueryResponse extends ControlResponse {
    public QueryResponse() {
    }

    public QueryResponse(SOAPResponse soapRes) {
        super(soapRes);
    }

    private Node getReturnNode() {
        Node queryResNode;
        Node bodyNode = getBodyNode();
        if (bodyNode != null && bodyNode.hasNodes() && (queryResNode = bodyNode.getNode(0)) != null && queryResNode.hasNodes()) {
            return queryResNode.getNode(0);
        }
        return null;
    }

    public String getReturnValue() {
        Node node = getReturnNode();
        return node == null ? "" : node.getValue();
    }

    public void setResponse(StateVariable stateVar) {
        String var = stateVar.getValue();
        setStatusCode(200);
        Node bodyNode = getBodyNode();
        Node resNode = createResponseNode(var);
        bodyNode.addNode(resNode);
        Node envNodee = getEnvelopeNode();
        setContent(envNodee);
    }

    private Node createResponseNode(String var) {
        Node queryResNode = new Node();
        queryResNode.setName("u", Control.QUERY_STATE_VARIABLE_RESPONSE);
        queryResNode.setNameSpace("u", Control.XMLNS);
        Node returnNode = new Node();
        returnNode.setName(Control.RETURN);
        returnNode.setValue(var);
        queryResNode.addNode(returnNode);
        return queryResNode;
    }
}

package org.cybergarage.upnp.control;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.soap.SOAP;
import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class QueryRequest extends ControlRequest {
    public QueryRequest() {
    }

    public QueryRequest(HTTPRequest httpReq) {
        set(httpReq);
    }

    private Node getVarNameNode() {
        Node queryStateVarNode;
        Node bodyNode = getBodyNode();
        if (bodyNode != null && bodyNode.hasNodes() && (queryStateVarNode = bodyNode.getNode(0)) != null && queryStateVarNode.hasNodes()) {
            return queryStateVarNode.getNode(0);
        }
        return null;
    }

    public String getVarName() {
        Node node = getVarNameNode();
        return node == null ? "" : node.getValue();
    }

    public void setRequest(StateVariable stateVar) {
        Service service = stateVar.getService();
        service.getControlURL();
        setRequestHost(service);
        setEnvelopeNode(SOAP.createEnvelopeBodyNode());
        Node envNode = getEnvelopeNode();
        Node bodyNode = getBodyNode();
        Node qeuryNode = createContentNode(stateVar);
        bodyNode.addNode(qeuryNode);
        setContent(envNode);
        setSOAPAction(Control.QUERY_SOAPACTION);
    }

    private Node createContentNode(StateVariable stateVar) {
        Node queryVarNode = new Node();
        queryVarNode.setName("u", Control.QUERY_STATE_VARIABLE);
        queryVarNode.setNameSpace("u", Control.XMLNS);
        Node varNode = new Node();
        varNode.setName("u", Control.VAR_NAME);
        varNode.setValue(stateVar.getName());
        queryVarNode.addNode(varNode);
        return queryVarNode;
    }

    public QueryResponse post() {
        SOAPResponse soapRes = postMessage(getRequestHost(), getRequestPort());
        return new QueryResponse(soapRes);
    }
}

package org.cybergarage.soap;

import org.cybergarage.http.HTTPResponse;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class SOAPResponse extends HTTPResponse {
    private Node rootNode;

    public SOAPResponse() {
        setRootNode(SOAP.createEnvelopeBodyNode());
        setContentType("text/xml; charset=\"utf-8\"");
    }

    public SOAPResponse(HTTPResponse httpRes) {
        super(httpRes);
        setRootNode(SOAP.createEnvelopeBodyNode());
        setContentType("text/xml; charset=\"utf-8\"");
    }

    public SOAPResponse(SOAPResponse soapRes) {
        super(soapRes);
        setEnvelopeNode(soapRes.getEnvelopeNode());
        setContentType("text/xml; charset=\"utf-8\"");
    }

    private void setRootNode(Node node) {
        this.rootNode = node;
    }

    private Node getRootNode() {
        return this.rootNode;
    }

    public void setEnvelopeNode(Node node) {
        setRootNode(node);
    }

    public Node getEnvelopeNode() {
        return getRootNode();
    }

    public Node getBodyNode() {
        Node envNode = getEnvelopeNode();
        if (envNode == null) {
            return null;
        }
        return envNode.getNodeEndsWith(SOAP.BODY);
    }

    public Node getMethodResponseNode(String name) {
        Node bodyNode = getBodyNode();
        if (bodyNode == null) {
            return null;
        }
        String methodResName = String.valueOf(name) + SOAP.RESPONSE;
        return bodyNode.getNodeEndsWith(methodResName);
    }

    public Node getFaultNode() {
        Node bodyNode = getBodyNode();
        if (bodyNode == null) {
            return null;
        }
        return bodyNode.getNodeEndsWith(SOAP.FAULT);
    }

    public Node getFaultCodeNode() {
        Node faultNode = getFaultNode();
        if (faultNode == null) {
            return null;
        }
        return faultNode.getNodeEndsWith(SOAP.FAULT_CODE);
    }

    public Node getFaultStringNode() {
        Node faultNode = getFaultNode();
        if (faultNode == null) {
            return null;
        }
        return faultNode.getNodeEndsWith(SOAP.FAULT_STRING);
    }

    public Node getFaultActorNode() {
        Node faultNode = getFaultNode();
        if (faultNode == null) {
            return null;
        }
        return faultNode.getNodeEndsWith(SOAP.FAULTACTOR);
    }

    public Node getFaultDetailNode() {
        Node faultNode = getFaultNode();
        if (faultNode == null) {
            return null;
        }
        return faultNode.getNodeEndsWith(SOAP.DETAIL);
    }

    public String getFaultCode() {
        Node node = getFaultCodeNode();
        return node == null ? "" : node.getValue();
    }

    public String getFaultString() {
        Node node = getFaultStringNode();
        return node == null ? "" : node.getValue();
    }

    public String getFaultActor() {
        Node node = getFaultActorNode();
        return node == null ? "" : node.getValue();
    }

    public void setContent(Node node) {
        String conStr = String.valueOf("") + "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        setContent(String.valueOf(String.valueOf(conStr) + "\n") + node.toString());
    }

    @Override // org.cybergarage.http.HTTPResponse
    public void print() {
        Node rootElem;
        Debug.message(toString());
        if (!hasContent() && (rootElem = getRootNode()) != null) {
            Debug.message(rootElem.toString());
        }
    }
}

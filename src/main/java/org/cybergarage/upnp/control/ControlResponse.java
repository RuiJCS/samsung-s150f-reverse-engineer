package org.cybergarage.upnp.control;

import org.cybergarage.soap.SOAP;
import org.cybergarage.soap.SOAPResponse;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class ControlResponse extends SOAPResponse {
    public static final String FAULT_CODE = "Client";
    public static final String FAULT_STRING = "UPnPError";
    private UPnPStatus upnpErr;

    public ControlResponse() {
        this.upnpErr = new UPnPStatus();
        setServer(UPnP.getServerName());
    }

    public ControlResponse(SOAPResponse soapRes) {
        super(soapRes);
        this.upnpErr = new UPnPStatus();
    }

    public void setFaultResponse(int errCode, String errDescr) {
        setStatusCode(500);
        Node bodyNode = getBodyNode();
        Node faultNode = createFaultResponseNode(errCode, errDescr);
        bodyNode.addNode(faultNode);
        Node envNode = getEnvelopeNode();
        setContent(envNode);
    }

    public void setFaultResponse(int errCode) {
        setFaultResponse(errCode, UPnPStatus.code2String(errCode));
    }

    private Node createFaultResponseNode(int errCode, String errDescr) {
        Node faultNode = new Node("s:Fault");
        Node faultCodeNode = new Node(SOAP.FAULT_CODE);
        faultCodeNode.setValue("s:Client");
        faultNode.addNode(faultCodeNode);
        Node faultStringNode = new Node(SOAP.FAULT_STRING);
        faultStringNode.setValue("UPnPError");
        faultNode.addNode(faultStringNode);
        Node detailNode = new Node(SOAP.DETAIL);
        faultNode.addNode(detailNode);
        Node upnpErrorNode = new Node("UPnPError");
        upnpErrorNode.setAttribute("xmlns", Control.XMLNS);
        detailNode.addNode(upnpErrorNode);
        Node errorCodeNode = new Node(SOAP.ERROR_CODE);
        errorCodeNode.setValue(errCode);
        upnpErrorNode.addNode(errorCodeNode);
        Node errorDesctiprionNode = new Node(SOAP.ERROR_DESCRIPTION);
        errorDesctiprionNode.setValue(errDescr);
        upnpErrorNode.addNode(errorDesctiprionNode);
        return faultNode;
    }

    private Node createFaultResponseNode(int errCode) {
        return createFaultResponseNode(errCode, UPnPStatus.code2String(errCode));
    }

    private Node getUPnPErrorNode() {
        Node detailNode = getFaultDetailNode();
        if (detailNode == null) {
            return null;
        }
        return detailNode.getNodeEndsWith("UPnPError");
    }

    private Node getUPnPErrorCodeNode() {
        Node errorNode = getUPnPErrorNode();
        if (errorNode == null) {
            return null;
        }
        return errorNode.getNodeEndsWith(SOAP.ERROR_CODE);
    }

    private Node getUPnPErrorDescriptionNode() {
        Node errorNode = getUPnPErrorNode();
        if (errorNode == null) {
            return null;
        }
        return errorNode.getNodeEndsWith(SOAP.ERROR_DESCRIPTION);
    }

    public int getUPnPErrorCode() {
        Node errorCodeNode = getUPnPErrorCodeNode();
        if (errorCodeNode == null) {
            return -1;
        }
        String errorCodeStr = errorCodeNode.getValue();
        try {
            return Integer.parseInt(errorCodeStr);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getUPnPErrorDescription() {
        Node errorDescNode = getUPnPErrorDescriptionNode();
        return errorDescNode == null ? "" : errorDescNode.getValue();
    }

    public UPnPStatus getUPnPError() {
        int code = getUPnPErrorCode();
        String desc = getUPnPErrorDescription();
        this.upnpErr.setCode(code);
        this.upnpErr.setDescription(desc);
        return this.upnpErr;
    }
}

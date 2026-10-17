package org.cybergarage.soap;

import java.io.ByteArrayInputStream;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

/* JADX INFO: loaded from: classes.dex */
public class SOAPRequest extends HTTPRequest {
    private static final String SOAPACTION = "SOAPACTION";
    private Node rootNode;

    public SOAPRequest() {
        setContentType("text/xml; charset=\"utf-8\"");
        setMethod(HTTP.POST);
    }

    public SOAPRequest(HTTPRequest httpReq) {
        set(httpReq);
    }

    public void setSOAPAction(String action) {
        setStringHeader("SOAPACTION", action);
    }

    public String getSOAPAction() {
        return getStringHeaderValue("SOAPACTION");
    }

    public boolean isSOAPAction(String value) {
        String headerValue = getHeaderValue("SOAPACTION");
        if (headerValue == null) {
            return false;
        }
        if (headerValue.equals(value)) {
            return true;
        }
        String soapAction = getSOAPAction();
        if (soapAction != null) {
            return soapAction.equals(value);
        }
        return false;
    }

    public SOAPResponse postMessage(String host, int port) {
        HTTPResponse httpRes = post(host, port);
        httpRes.getStatusCode();
        SOAPResponse soapRes = new SOAPResponse(httpRes);
        byte[] content = soapRes.getContent();
        if (content.length > 0) {
            try {
                ByteArrayInputStream byteIn = new ByteArrayInputStream(content);
                Parser xmlParser = SOAP.getXMLParser();
                Node rootNode = xmlParser.parse(byteIn);
                soapRes.setEnvelopeNode(rootNode);
            } catch (Exception e) {
                Debug.warning(e);
            }
        }
        return soapRes;
    }

    private void setRootNode(Node node) {
        this.rootNode = node;
    }

    private synchronized Node getRootNode() {
        Node node;
        if (this.rootNode != null) {
            node = this.rootNode;
        } else {
            try {
                byte[] content = getContent();
                ByteArrayInputStream contentIn = new ByteArrayInputStream(content);
                Parser parser = SOAP.getXMLParser();
                this.rootNode = parser.parse(contentIn);
            } catch (ParserException e) {
                Debug.warning(e);
            }
            node = this.rootNode;
        }
        return node;
    }

    public void setEnvelopeNode(Node node) {
        setRootNode(node);
    }

    public Node getEnvelopeNode() {
        return getRootNode();
    }

    public Node getBodyNode() {
        Node envNode = getEnvelopeNode();
        if (envNode != null && envNode.hasNodes()) {
            return envNode.getNode(0);
        }
        return null;
    }

    public void setContent(Node node) {
        String conStr = String.valueOf("") + "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        setContent(String.valueOf(String.valueOf(conStr) + "\n") + node.toString());
    }

    @Override // org.cybergarage.http.HTTPRequest
    public void print() {
        Node rootElem;
        Debug.message(toString());
        if (!hasContent() && (rootElem = getRootNode()) != null) {
            Debug.message(rootElem.toString());
        }
    }
}

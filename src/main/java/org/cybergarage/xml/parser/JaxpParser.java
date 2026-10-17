package org.cybergarage.xml.parser;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: classes.dex */
public class JaxpParser extends Parser {
    public Node parse(Node parentNode, org.w3c.dom.Node domNode, int rank) {
        int domNodeType = domNode.getNodeType();
        String domNodeName = domNode.getNodeName();
        String domNodeValue = domNode.getNodeValue();
        NamedNodeMap attrs = domNode.getAttributes();
        if (attrs != null) {
            attrs.getLength();
        }
        if (domNodeType == 3) {
            parentNode.addValue(domNodeValue);
            return parentNode;
        }
        if (domNodeType == 1) {
            Node node = new Node();
            node.setName(domNodeName);
            node.setValue(domNodeValue);
            if (parentNode != null) {
                parentNode.addNode(node);
            }
            NamedNodeMap attrMap = domNode.getAttributes();
            if (attrMap != null) {
                int attrLen = attrMap.getLength();
                for (int n = 0; n < attrLen; n++) {
                    org.w3c.dom.Node attr = attrMap.item(n);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    node.setAttribute(attrName, attrValue);
                }
            }
            org.w3c.dom.Node child = domNode.getFirstChild();
            if (child == null) {
                node.setValue("");
                return node;
            }
            do {
                parse(node, child, rank + 1);
                child = child.getNextSibling();
            } while (child != null);
            return node;
        }
        return parentNode;
    }

    public Node parse(Node parentNode, org.w3c.dom.Node domNode) {
        return parse(parentNode, domNode, 0);
    }

    @Override // org.cybergarage.xml.Parser
    public Node parse(InputStream inStream) throws ParserException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inSrc = new InputSource(inStream);
            Document doc = builder.parse(inSrc);
            Element docElem = doc.getDocumentElement();
            if (docElem == null) {
                return null;
            }
            Node root = parse(null, docElem);
            return root;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}

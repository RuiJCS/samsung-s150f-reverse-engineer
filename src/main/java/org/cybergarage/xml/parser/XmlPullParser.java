package org.cybergarage.xml.parser;

import java.io.InputStream;
import org.cybergarage.soap.SOAP;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class XmlPullParser extends Parser {
    public Node parse(org.xmlpull.v1.XmlPullParser xpp, InputStream inStream) throws ParserException {
        Node rootNode = null;
        Node currNode = null;
        try {
            xpp.setInput(inStream, null);
            int eventType = xpp.getEventType();
            while (eventType != 1) {
                switch (eventType) {
                    case 2:
                        Node node = new Node();
                        String namePrefix = xpp.getPrefix();
                        String name = xpp.getName();
                        StringBuffer nodeName = new StringBuffer();
                        if (namePrefix != null && namePrefix.length() > 0) {
                            nodeName.append(namePrefix);
                            nodeName.append(SOAP.DELIM);
                        }
                        if (name != null && name.length() > 0) {
                            nodeName.append(name);
                        }
                        node.setName(nodeName.toString());
                        int attrsLen = xpp.getAttributeCount();
                        for (int n = 0; n < attrsLen; n++) {
                            String attrName = xpp.getAttributeName(n);
                            String attrValue = xpp.getAttributeValue(n);
                            node.setAttribute(attrName, attrValue);
                        }
                        if (currNode != null) {
                            currNode.addNode(node);
                        }
                        currNode = node;
                        if (rootNode == null) {
                            rootNode = node;
                        }
                        break;
                    case 3:
                        currNode = currNode.getParentNode();
                        break;
                    case 4:
                        String value = xpp.getText();
                        if (value != null && currNode != null) {
                            currNode.setValue(value);
                        }
                        break;
                }
                eventType = xpp.next();
            }
            return rootNode;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    @Override // org.cybergarage.xml.Parser
    public Node parse(InputStream inStream) throws ParserException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            org.xmlpull.v1.XmlPullParser xpp = factory.newPullParser();
            Node rootNode = parse(xpp, inStream);
            return rootNode;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }
}

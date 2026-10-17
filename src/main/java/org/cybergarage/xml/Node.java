package org.cybergarage.xml;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class Node {
    private AttributeList attrList;
    private String name;
    private NodeList nodeList;
    private Node parentNode;
    private Object userData;
    private String value;

    public Node() {
        this.parentNode = null;
        this.name = new String();
        this.value = new String();
        this.attrList = new AttributeList();
        this.nodeList = new NodeList();
        this.userData = null;
        setUserData(null);
        setParentNode(null);
    }

    public Node(String name) {
        this();
        setName(name);
    }

    public Node(String ns, String name) {
        this();
        setName(ns, name);
    }

    public void setParentNode(Node node) {
        this.parentNode = node;
    }

    public Node getParentNode() {
        return this.parentNode;
    }

    public Node getRootNode() {
        Node rootNode = null;
        Node parentNode = getParentNode();
        while (parentNode != null) {
            rootNode = parentNode;
            parentNode = rootNode.getParentNode();
        }
        return rootNode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String ns, String name) {
        this.name = String.valueOf(ns) + SOAP.DELIM + name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isName(String value) {
        return this.name.equals(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(int value) {
        setValue(Integer.toString(value));
    }

    public void addValue(String value) {
        if (this.value == null) {
            this.value = value;
        } else if (value != null) {
            this.value = String.valueOf(this.value) + value;
        }
    }

    public String getValue() {
        return this.value;
    }

    public int getNAttributes() {
        return this.attrList.size();
    }

    public Attribute getAttribute(int index) {
        return this.attrList.getAttribute(index);
    }

    public Attribute getAttribute(String name) {
        return this.attrList.getAttribute(name);
    }

    public void addAttribute(Attribute attr) {
        this.attrList.add(attr);
    }

    public void insertAttributeAt(Attribute attr, int index) {
        this.attrList.insertElementAt(attr, index);
    }

    public void addAttribute(String name, String value) {
        Attribute attr = new Attribute(name, value);
        addAttribute(attr);
    }

    public boolean removeAttribute(Attribute attr) {
        return this.attrList.remove(attr);
    }

    public boolean removeAttribute(String name) {
        return removeAttribute(getAttribute(name));
    }

    public boolean hasAttributes() {
        return getNAttributes() > 0;
    }

    public void setAttribute(String name, String value) {
        Attribute attr = getAttribute(name);
        if (attr != null) {
            attr.setValue(value);
        } else {
            addAttribute(new Attribute(name, value));
        }
    }

    public void setAttribute(String name, int value) {
        setAttribute(name, Integer.toString(value));
    }

    public String getAttributeValue(String name) {
        Attribute attr = getAttribute(name);
        return attr != null ? attr.getValue() : "";
    }

    public int getAttributeIntegerValue(String name) {
        String val = getAttributeValue(name);
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setNameSpace(String ns, String value) {
        setAttribute("xmlns:" + ns, value);
    }

    public int getNNodes() {
        return this.nodeList.size();
    }

    public Node getNode(int index) {
        return this.nodeList.getNode(index);
    }

    public Node getNode(String name) {
        return this.nodeList.getNode(name);
    }

    public Node getNodeEndsWith(String name) {
        return this.nodeList.getEndsWith(name);
    }

    public void addNode(Node node) {
        node.setParentNode(this);
        this.nodeList.add(node);
    }

    public void insertNode(Node node, int index) {
        node.setParentNode(this);
        this.nodeList.insertElementAt(node, index);
    }

    public int getIndex(String name) {
        int index = -1;
        for (Node n : this.nodeList) {
            index++;
            if (n.getName().equals(name)) {
                return index;
            }
        }
        return index;
    }

    public boolean removeNode(Node node) {
        node.setParentNode(null);
        return this.nodeList.remove(node);
    }

    public boolean removeNode(String name) {
        return this.nodeList.remove(getNode(name));
    }

    public void removeAllNodes() {
        this.nodeList.clear();
    }

    public boolean hasNodes() {
        return getNNodes() > 0;
    }

    public void setNode(String name, String value) {
        Node node = getNode(name);
        if (node != null) {
            node.setValue(value);
            return;
        }
        Node node2 = new Node(name);
        node2.setValue(value);
        addNode(node2);
    }

    public String getNodeValue(String name) {
        Node node = getNode(name);
        return node != null ? node.getValue() : "";
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }

    public String getIndentLevelString(int nIndentLevel) {
        return getIndentLevelString(nIndentLevel, "   ");
    }

    public String getIndentLevelString(int nIndentLevel, String space) {
        StringBuffer indentString = new StringBuffer(space.length() * nIndentLevel);
        for (int n = 0; n < nIndentLevel; n++) {
            indentString.append(space);
        }
        return indentString.toString();
    }

    public void outputAttributes(PrintWriter ps) {
        int nAttributes = getNAttributes();
        for (int n = 0; n < nAttributes; n++) {
            Attribute attr = getAttribute(n);
            ps.print(" " + attr.getName() + "=\"" + XML.escapeXMLChars(attr.getValue()) + "\"");
        }
    }

    public void output(PrintWriter ps, int indentLevel, boolean hasChildNode) {
        String indentString = getIndentLevelString(indentLevel);
        String name = getName();
        String value = getValue();
        if (!hasNodes() || !hasChildNode) {
            ps.print(String.valueOf(indentString) + "<" + name);
            outputAttributes(ps);
            if (value == null || value.length() == 0) {
                ps.println("></" + name + ">");
                return;
            } else {
                ps.println(">" + XML.escapeXMLChars(value) + "</" + name + ">");
                return;
            }
        }
        ps.print(String.valueOf(indentString) + "<" + name);
        outputAttributes(ps);
        ps.println(">");
        int nChildNodes = getNNodes();
        for (int n = 0; n < nChildNodes; n++) {
            Node cnode = getNode(n);
            cnode.output(ps, indentLevel + 1, true);
        }
        ps.println(String.valueOf(indentString) + "</" + name + ">");
    }

    public String toString(String enc, boolean hasChildNode) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintWriter pr = new PrintWriter(byteOut);
        output(pr, 0, hasChildNode);
        pr.flush();
        if (enc != null) {
            try {
                if (enc.length() > 0) {
                    return byteOut.toString(enc);
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return byteOut.toString();
    }

    public String toString() {
        return toString(XML.CHARSET_UTF8, true);
    }

    public String toXMLString(boolean hasChildNode) {
        String xmlStr = toString();
        return xmlStr.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
    }

    public String toXMLString() {
        return toXMLString(true);
    }

    public void print(boolean hasChildNode) {
        PrintWriter pr = new PrintWriter(System.out);
        output(pr, 0, hasChildNode);
        pr.flush();
    }

    public void print() {
        print(true);
    }
}

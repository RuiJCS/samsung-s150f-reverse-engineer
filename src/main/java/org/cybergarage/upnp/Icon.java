package org.cybergarage.upnp;

import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class Icon {
    private static final String DEPTH = "depth";
    public static final String ELEM_NAME = "icon";
    private static final String HEIGHT = "height";
    private static final String MIME_TYPE = "mimeType";
    private static final String URL = "url";
    private static final String WIDTH = "width";
    private Node iconNode;
    private Object userData = null;

    public Node getIconNode() {
        return this.iconNode;
    }

    public Icon(Node node) {
        this.iconNode = node;
    }

    public static boolean isIconNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    public void setMimeType(String value) {
        getIconNode().setNode(MIME_TYPE, value);
    }

    public String getMimeType() {
        return getIconNode().getNodeValue(MIME_TYPE);
    }

    public void setWidth(String value) {
        getIconNode().setNode(WIDTH, value);
    }

    public void setWidth(int value) {
        try {
            setWidth(Integer.toString(value));
        } catch (Exception e) {
        }
    }

    public int getWidth() {
        try {
            return Integer.parseInt(getIconNode().getNodeValue(WIDTH));
        } catch (Exception e) {
            return 0;
        }
    }

    public void setHeight(String value) {
        getIconNode().setNode(HEIGHT, value);
    }

    public void setHeight(int value) {
        try {
            setHeight(Integer.toString(value));
        } catch (Exception e) {
        }
    }

    public int getHeight() {
        try {
            return Integer.parseInt(getIconNode().getNodeValue(HEIGHT));
        } catch (Exception e) {
            return 0;
        }
    }

    public void setDepth(String value) {
        getIconNode().setNode(DEPTH, value);
    }

    public String getDepth() {
        return getIconNode().getNodeValue(DEPTH);
    }

    public void setURL(String value) {
        getIconNode().setNode(URL, value);
    }

    public String getURL() {
        return getIconNode().getNodeValue(URL);
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }
}

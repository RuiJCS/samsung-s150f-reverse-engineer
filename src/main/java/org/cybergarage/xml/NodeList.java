package org.cybergarage.xml;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class NodeList extends Vector {
    public Node getNode(int n) {
        return (Node) get(n);
    }

    public Node getNode(String name) {
        if (name == null) {
            return null;
        }
        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            String nodeName = node.getName();
            if (name.compareTo(nodeName) == 0) {
                return node;
            }
        }
        return null;
    }

    public Node getEndsWith(String name) {
        if (name == null) {
            return null;
        }
        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            String nodeName = node.getName();
            if (nodeName != null && nodeName.endsWith(name)) {
                return node;
            }
        }
        return null;
    }
}

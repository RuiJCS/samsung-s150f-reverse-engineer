package org.cybergarage.upnp;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class IconList extends Vector {
    public static final String ELEM_NAME = "iconList";

    public Icon getIcon(int n) {
        return (Icon) get(n);
    }
}

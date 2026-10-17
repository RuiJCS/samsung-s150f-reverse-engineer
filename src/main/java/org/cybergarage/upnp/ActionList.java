package org.cybergarage.upnp;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ActionList extends Vector {
    public static final String ELEM_NAME = "actionList";

    public Action getAction(int n) {
        return (Action) get(n);
    }
}

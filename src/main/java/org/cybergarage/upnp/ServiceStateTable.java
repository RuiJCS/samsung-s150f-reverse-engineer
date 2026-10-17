package org.cybergarage.upnp;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ServiceStateTable extends Vector {
    public static final String ELEM_NAME = "serviceStateTable";

    public StateVariable getStateVariable(int n) {
        return (StateVariable) get(n);
    }
}

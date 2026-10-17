package org.cybergarage.upnp;

import java.util.Iterator;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class AllowedValueList extends Vector {
    public static final String ELEM_NAME = "allowedValueList";

    public AllowedValueList() {
    }

    public AllowedValueList(String[] values) {
        for (String str : values) {
            add(new AllowedValue(str));
        }
    }

    public AllowedValue getAllowedValue(int n) {
        return (AllowedValue) get(n);
    }

    public boolean isAllowed(String v) {
        Iterator i = iterator();
        while (i.hasNext()) {
            AllowedValue av = (AllowedValue) i.next();
            if (av.getValue().equals(v)) {
                return true;
            }
        }
        return false;
    }
}

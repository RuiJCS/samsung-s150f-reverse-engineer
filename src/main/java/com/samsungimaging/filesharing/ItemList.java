package com.samsungimaging.filesharing;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ItemList extends Vector {
    public Item getItem(int n) {
        return (Item) get(n);
    }

    public Item getItem(String name) {
        int nArgs = size();
        for (int n = 0; n < nArgs; n++) {
            Item arg = getItem(n);
            String argName = arg.getTitle();
            if (argName != null && argName.equals(name)) {
                return arg;
            }
        }
        return null;
    }
}

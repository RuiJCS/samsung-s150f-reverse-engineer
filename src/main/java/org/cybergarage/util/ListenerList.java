package org.cybergarage.util;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ListenerList extends Vector {
    @Override // java.util.Vector, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(Object obj) {
        if (indexOf(obj) >= 0) {
            return false;
        }
        return super.add(obj);
    }
}

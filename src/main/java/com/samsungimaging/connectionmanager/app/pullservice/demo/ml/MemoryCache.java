package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import android.graphics.Bitmap;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class MemoryCache {
    private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<>();

    public Bitmap get(String id) {
        SoftReference<Bitmap> ref;
        if (this.cache.containsKey(id) && (ref = this.cache.get(id)) != null) {
            return ref.get();
        }
        return null;
    }

    public void put(String id, Bitmap bitmap) {
        this.cache.put(id, new SoftReference<>(bitmap));
    }

    public void clear() {
        this.cache.clear();
    }
}

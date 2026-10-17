package com.samsungimaging.connectionmanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import com.samsungimaging.connectionmanager.app.pushservice.common.Const;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;

/* JADX INFO: loaded from: classes.dex */
public class ImageCache {
    private static final int DISK_CACHE_SIZE = 10485760;
    private Context mContext;
    private DiskLruCache mDiskCache;
    private String mDiskCacheDir;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private LruCache<String, RecycleBitmapDrawable> mMemoryCache;

    public ImageCache(Context context, String diskCacheDir) {
        this.mContext = null;
        this.mDiskCacheDir = null;
        this.mContext = context;
        this.mDiskCacheDir = diskCacheDir;
        int memoryCacheSize = Math.round(Runtime.getRuntime().maxMemory() * 0.2f);
        Trace.d(Trace.Tag.COMMON, "[" + diskCacheDir + "] MemoryCacheSize : " + NumberFormat.getInstance().format(memoryCacheSize) + " byte");
        this.mMemoryCache = new LruCache<String, RecycleBitmapDrawable>(memoryCacheSize) { // from class: com.samsungimaging.connectionmanager.util.ImageCache.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.support.v4.util.LruCache
            public void entryRemoved(boolean evicted, String key, RecycleBitmapDrawable oldValue, RecycleBitmapDrawable newValue) {
                oldValue.setCached(false);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.support.v4.util.LruCache
            public int sizeOf(String key, RecycleBitmapDrawable value) {
                Bitmap bitmap = value.getBitmap();
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0071  */
    public void initDiskCache() {
        String path;
        synchronized (this.mDiskCacheLock) {
            if (this.mDiskCache == null || this.mDiskCache.isClosed()) {
                if (("mounted".equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) && this.mContext.getExternalCacheDir() != null) {
                    path = this.mContext.getExternalCacheDir().getPath();
                } else {
                    path = this.mContext.getCacheDir().getPath();
                }
                File diskCacheDir = new File(String.valueOf(path) + File.separator + this.mDiskCacheDir);
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }
                if (diskCacheDir.getUsableSpace() > Const.Config.CRITICAL_SD_MEMORY) {
                    try {
                        this.mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, Const.Config.CRITICAL_SD_MEMORY);
                    } catch (IOException e) {
                        Trace.e(Trace.Tag.COMMON, "initDiskCache() - " + e.getMessage());
                    }
                    this.mDiskCacheStarting = false;
                    this.mDiskCacheLock.notifyAll();
                } else {
                    this.mDiskCacheStarting = false;
                    this.mDiskCacheLock.notifyAll();
                }
            } else {
                this.mDiskCacheStarting = false;
                this.mDiskCacheLock.notifyAll();
            }
            throw th;
        }
    }

    public void clearMemoryCache() {
        if (this.mMemoryCache != null) {
            this.mMemoryCache.evictAll();
        }
    }

    public void clearCache() {
        clearMemoryCache();
        synchronized (this.mDiskCacheLock) {
            this.mDiskCacheStarting = true;
            if (this.mDiskCache != null && !this.mDiskCache.isClosed()) {
                try {
                    this.mDiskCache.delete();
                } catch (IOException e) {
                    Trace.e(Trace.Tag.COMMON, "clearCache() - " + e.getMessage());
                }
                this.mDiskCache = null;
                initDiskCache();
            }
        }
    }

    public void flushCache() {
        synchronized (this.mDiskCacheLock) {
            if (this.mDiskCache != null) {
                try {
                    this.mDiskCache.flush();
                } catch (IOException e) {
                    Trace.e(Trace.Tag.COMMON, "flush() - " + e.getMessage());
                }
            }
        }
    }

    public void closeCache() {
        synchronized (this.mDiskCacheLock) {
            if (this.mDiskCache != null) {
                try {
                    if (!this.mDiskCache.isClosed()) {
                        this.mDiskCache.close();
                        this.mDiskCache = null;
                    }
                } catch (IOException e) {
                    Trace.e(Trace.Tag.COMMON, "close() - " + e.getMessage());
                }
            }
        }
    }

    public void remove(String filePath) {
        RecycleBitmapDrawable drawable;
        if (this.mMemoryCache != null && this.mMemoryCache != null && (drawable = this.mMemoryCache.get(filePath)) != null) {
            Bitmap bitmap = drawable.getBitmap();
            this.mMemoryCache.remove(filePath);
            if (Build.VERSION.SDK_INT < 19 && !bitmap.isRecycled()) {
                Trace.i(Trace.Tag.COMMON, "bitmap recycle!!!");
                bitmap.recycle();
            }
        }
        synchronized (this.mDiskCacheLock) {
            if (this.mDiskCache != null) {
                try {
                    String key = String.valueOf(filePath.hashCode());
                    this.mDiskCache.remove(key);
                } catch (IOException e) {
                    Trace.e(Trace.Tag.COMMON, "remove() - " + e.getMessage());
                }
            }
        }
    }

    public void addBitmapToCache(String filePath, RecycleBitmapDrawable drawable) {
        if (filePath != null && drawable != null) {
            addBitmapToMemoryCache(filePath, drawable);
            synchronized (this.mDiskCacheLock) {
                if (this.mDiskCache != null) {
                    String key = String.valueOf(filePath.hashCode());
                    OutputStream out = null;
                    try {
                        try {
                            DiskLruCache.Snapshot snapshot = this.mDiskCache.get(key);
                            if (snapshot == null) {
                                DiskLruCache.Editor editor = this.mDiskCache.edit(key);
                                if (editor != null) {
                                    out = editor.newOutputStream(0);
                                    drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    editor.commit();
                                    out.close();
                                }
                            } else {
                                snapshot.getInputStream(0).close();
                            }
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                }
                            }
                        } catch (Exception e2) {
                            Trace.e(Trace.Tag.COMMON, "addBitmapToCache() - " + e2.getMessage());
                            if (0 != 0) {
                                try {
                                    out.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (Throwable th) {
                        if (0 != 0) {
                            try {
                                out.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                }
            }
        }
    }

    public void addBitmapToMemoryCache(String filePath, RecycleBitmapDrawable drawable) {
        if (filePath != null && drawable != null && this.mMemoryCache != null) {
            drawable.setCached(true);
            this.mMemoryCache.put(filePath, drawable);
        }
    }

    public RecycleBitmapDrawable getBitmapDrawableFromMemoryCache(String filePath) {
        if (this.mMemoryCache == null) {
            return null;
        }
        RecycleBitmapDrawable drawable = this.mMemoryCache.get(filePath);
        return drawable;
    }

    public Bitmap getBitmapFromDiskCache(String filePath) {
        String key = String.valueOf(filePath.hashCode());
        Bitmap bitmap = null;
        synchronized (this.mDiskCacheLock) {
            while (this.mDiskCacheStarting) {
                try {
                    this.mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                }
            }
            if (this.mDiskCache != null) {
                InputStream inputStream = null;
                try {
                    try {
                        DiskLruCache.Snapshot snapshot = this.mDiskCache.get(key);
                        if (snapshot != null && (inputStream = snapshot.getInputStream(0)) != null) {
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e2) {
                            }
                        }
                    } catch (Throwable th) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e3) {
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    Trace.e(Trace.Tag.COMMON, "getBitmapFromDiskCache() - " + e4.getMessage());
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e5) {
                        }
                    }
                    return bitmap;
                }
            }
        }
        return bitmap;
    }

    public boolean hasSnapshotInDiskCache(String filePath) {
        boolean hasSnapshot = false;
        String key = String.valueOf(filePath.hashCode());
        synchronized (this.mDiskCacheLock) {
            while (this.mDiskCacheStarting) {
                try {
                    this.mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                }
            }
            if (this.mDiskCache != null) {
                try {
                    DiskLruCache.Snapshot snapshot = this.mDiskCache.get(key);
                    if (snapshot != null) {
                        hasSnapshot = true;
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return hasSnapshot;
    }
}

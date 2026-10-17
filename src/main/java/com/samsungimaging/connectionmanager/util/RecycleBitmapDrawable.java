package com.samsungimaging.connectionmanager.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/* JADX INFO: loaded from: classes.dex */
public class RecycleBitmapDrawable extends BitmapDrawable {
    private int mCacheRefCount;
    private int mDisplayRefCount;
    private boolean mHasBeenDisplayed;
    private boolean mIsNoneImage;

    public RecycleBitmapDrawable(Bitmap bitmap) {
        super(bitmap);
        this.mCacheRefCount = 0;
        this.mDisplayRefCount = 0;
        this.mIsNoneImage = false;
    }

    public RecycleBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        this.mCacheRefCount = 0;
        this.mDisplayRefCount = 0;
        this.mIsNoneImage = false;
    }

    public void setCached(boolean isCached) {
        synchronized (this) {
            try {
                if (isCached) {
                    this.mCacheRefCount++;
                } else {
                    this.mCacheRefCount--;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        checkState();
    }

    public void setDisplayed(boolean isDisplayed) {
        synchronized (this) {
            try {
                if (isDisplayed) {
                    this.mDisplayRefCount++;
                    this.mHasBeenDisplayed = true;
                } else {
                    this.mDisplayRefCount--;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        checkState();
    }

    private synchronized void checkState() {
        if (this.mCacheRefCount <= 0 && this.mDisplayRefCount <= 0 && this.mHasBeenDisplayed && hasValidBitmap()) {
            Trace.i(Trace.Tag.COMMON, "bitmap recycle!!!");
            this.mHasBeenDisplayed = false;
            getBitmap().recycle();
        }
    }

    public synchronized boolean hasValidBitmap() {
        Bitmap bitmap;
        bitmap = getBitmap();
        return (bitmap == null || bitmap.isRecycled()) ? false : true;
    }

    public void setNoneImage(boolean isNoneImage) {
        this.mIsNoneImage = isNoneImage;
    }

    public boolean isNoneImage() {
        return this.mIsNoneImage;
    }
}

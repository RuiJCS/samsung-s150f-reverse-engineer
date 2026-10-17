package com.samsungimaging.asphodel.multimedia;

import android.graphics.Bitmap;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: compiled from: FFmpegJNI.java */
/* JADX INFO: loaded from: classes.dex */
class FFmpegPicture {
    private static final String LOG_TAG = "[FFmpegPicture]";
    private Bitmap m_bitmap;

    public FFmpegPicture(int width, int height, Bitmap.Config config) {
        this.m_bitmap = null;
        Trace.d(Trace.Tag.FFMPEG, "picture creation");
        this.m_bitmap = Bitmap.createBitmap(width, height, config);
    }

    public Bitmap getPicture() {
        return this.m_bitmap;
    }
}

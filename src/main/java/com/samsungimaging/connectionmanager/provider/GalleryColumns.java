package com.samsungimaging.connectionmanager.provider;

import android.provider.BaseColumns;

/* JADX INFO: loaded from: classes.dex */
public interface GalleryColumns extends BaseColumns {
    public static final String KEY_COUNT = "_count";
    public static final String KEY_DATE_TAKEN = "datetaken";
    public static final String KEY_DATE_TAKEN_STRING = "datetaken_string";
    public static final String KEY_ID = "_id";
    public static final String KEY_MEDIA_TYPE = "media_type";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_ORIGINAL_PATH = "_data";
    public static final String KEY_THUMBNAIL_PATH = "thumbnail_path";
    public static final String KEY_VIEWER_PATH = "screen_path";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_NONE = 0;
    public static final int MEDIA_TYPE_VIDEO = 3;
}

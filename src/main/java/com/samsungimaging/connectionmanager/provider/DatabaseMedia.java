package com.samsungimaging.connectionmanager.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.samsungimaging.connectionmanager.util.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DatabaseMedia implements GalleryColumns {
    public static final int COLUMN_DATE_TAKEN = 5;
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MEDIA_TYPE = 4;
    public static final int COLUMN_ORIENTATION = 6;
    public static final int COLUMN_ORIGINAL_PATH = 1;
    public static final int COLUMN_SCREEN_PATH = 3;
    public static final int COLUMN_THUMBNAIL_PATH = 2;
    protected static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.samsungimaging.connectionmanager.cameramedia";
    protected static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.samsungimaging.connectionmanager.cameramedia";
    protected static final String CREATE_TABLE = "CREATE TABLE CameraMedia (_id INTEGER PRIMARY KEY, _data TEXT, thumbnail_path TEXT, screen_path TEXT, media_type INTEGER, datetaken INTEGER, orientation INTEGER);";
    protected static final String DEFAULT_SORT_ORDER = "datetaken DESC";
    protected static final String TABLE_NAME = "CameraMedia";
    private long mDateTaken;
    private String mDateTakenString;
    private int mMediaType;
    private int mOrientation;
    private String mOriginalPath;
    private String mThumbnailPath;
    private long mUriID;
    private String mViewerPath;
    public static final Uri CONTENT_URI = Uri.parse("content://com.samsungimaging.connectionmanager/CameraMedia");
    protected static final String[] PROJECTION = {GalleryColumns.KEY_ID, GalleryColumns.KEY_ORIGINAL_PATH, GalleryColumns.KEY_THUMBNAIL_PATH, GalleryColumns.KEY_VIEWER_PATH, GalleryColumns.KEY_MEDIA_TYPE, GalleryColumns.KEY_DATE_TAKEN, GalleryColumns.KEY_ORIENTATION};
    protected static HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(GalleryColumns.KEY_ID, GalleryColumns.KEY_ID);
        PROJECTION_MAP.put(GalleryColumns.KEY_ORIGINAL_PATH, GalleryColumns.KEY_ORIGINAL_PATH);
        PROJECTION_MAP.put(GalleryColumns.KEY_THUMBNAIL_PATH, GalleryColumns.KEY_THUMBNAIL_PATH);
        PROJECTION_MAP.put(GalleryColumns.KEY_VIEWER_PATH, GalleryColumns.KEY_VIEWER_PATH);
        PROJECTION_MAP.put(GalleryColumns.KEY_MEDIA_TYPE, GalleryColumns.KEY_MEDIA_TYPE);
        PROJECTION_MAP.put(GalleryColumns.KEY_DATE_TAKEN, GalleryColumns.KEY_DATE_TAKEN);
        PROJECTION_MAP.put(GalleryColumns.KEY_ORIENTATION, GalleryColumns.KEY_ORIENTATION);
    }

    public DatabaseMedia() {
        this.mUriID = -1L;
        this.mOriginalPath = null;
        this.mThumbnailPath = null;
        this.mViewerPath = null;
        this.mMediaType = 0;
        this.mDateTaken = 0L;
        this.mDateTakenString = null;
        this.mOrientation = 0;
    }

    public DatabaseMedia(String originalPath, String thumbnailPath, String viewerPath, int mediaType, long dateTaken, int orientation) {
        this.mUriID = -1L;
        this.mOriginalPath = null;
        this.mThumbnailPath = null;
        this.mViewerPath = null;
        this.mMediaType = 0;
        this.mDateTaken = 0L;
        this.mDateTakenString = null;
        this.mOrientation = 0;
        this.mOriginalPath = originalPath;
        this.mThumbnailPath = thumbnailPath;
        this.mViewerPath = viewerPath;
        this.mMediaType = mediaType;
        this.mDateTaken = dateTaken;
        this.mDateTakenString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(this.mDateTaken));
        this.mOrientation = orientation;
    }

    public Uri getUri() {
        if (this.mUriID != -1) {
            return ContentUris.withAppendedId(CONTENT_URI, this.mUriID);
        }
        return null;
    }

    public long getID() {
        return this.mUriID;
    }

    public String getOriginalPath() {
        return this.mOriginalPath;
    }

    public String getThumbnailPath() {
        return this.mThumbnailPath;
    }

    public String getViewerPath() {
        return this.mViewerPath;
    }

    public long getDateTaken() {
        return this.mDateTaken;
    }

    public String getDateTakenString() {
        return this.mDateTakenString;
    }

    public int getMediaType() {
        return this.mMediaType;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(GalleryColumns.KEY_ORIGINAL_PATH, this.mOriginalPath);
        values.put(GalleryColumns.KEY_THUMBNAIL_PATH, this.mThumbnailPath);
        values.put(GalleryColumns.KEY_VIEWER_PATH, this.mViewerPath);
        values.put(GalleryColumns.KEY_MEDIA_TYPE, Integer.valueOf(this.mMediaType));
        values.put(GalleryColumns.KEY_DATE_TAKEN, Long.valueOf(this.mDateTaken));
        values.put(GalleryColumns.KEY_ORIENTATION, Integer.valueOf(this.mOrientation));
        return values;
    }

    public static DatabaseMedia builder(Cursor cursor) {
        DatabaseMedia media = new DatabaseMedia();
        media.mUriID = cursor.getLong(cursor.getColumnIndex(GalleryColumns.KEY_ID));
        media.mOriginalPath = cursor.getString(cursor.getColumnIndex(GalleryColumns.KEY_ORIGINAL_PATH));
        try {
            media.mThumbnailPath = cursor.getString(cursor.getColumnIndexOrThrow(GalleryColumns.KEY_THUMBNAIL_PATH));
            media.mViewerPath = cursor.getString(cursor.getColumnIndexOrThrow(GalleryColumns.KEY_VIEWER_PATH));
        } catch (Exception e) {
            media.mThumbnailPath = media.mOriginalPath;
            media.mViewerPath = media.mOriginalPath;
        }
        media.mDateTaken = cursor.getLong(cursor.getColumnIndex(GalleryColumns.KEY_DATE_TAKEN));
        media.mDateTakenString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(media.mDateTaken));
        try {
            media.mMediaType = cursor.getInt(cursor.getColumnIndexOrThrow(GalleryColumns.KEY_MEDIA_TYPE));
        } catch (Exception e2) {
            String mimeType = Utils.getMimeType(media.mOriginalPath);
            if (mimeType.startsWith("image")) {
                media.mMediaType = 1;
            } else {
                media.mMediaType = 3;
            }
        }
        try {
            media.mOrientation = cursor.getInt(cursor.getColumnIndexOrThrow(GalleryColumns.KEY_ORIENTATION));
        } catch (Exception e3) {
        }
        return media;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("Meida");
        str.append(" [");
        str.append("_data=" + this.mOriginalPath);
        str.append("thumbnail_path=" + this.mThumbnailPath);
        str.append("screen_path=" + this.mViewerPath);
        str.append(", media_type=" + this.mMediaType);
        str.append(", datetaken=" + this.mDateTaken);
        str.append(", datetaken_string=" + this.mDateTakenString);
        str.append(", orientation=" + this.mOrientation);
        str.append("]");
        return str.toString();
    }
}

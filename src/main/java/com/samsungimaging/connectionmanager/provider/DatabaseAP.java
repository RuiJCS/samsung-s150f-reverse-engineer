package com.samsungimaging.connectionmanager.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import java.io.Serializable;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class DatabaseAP implements BaseColumns, Serializable {
    public static final int COLUMN_COUNT = 2;
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_SSID = 1;
    protected static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.samsungimaging.connectionmanager.ap";
    protected static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.samsungimaging.connectionmanager.ap";
    protected static final String CREATE_TABLE = "CREATE TABLE APList (_id INTEGER PRIMARY KEY, ssid TEXT, count INTEGER);";
    protected static final String DEFAULT_SORT_ORDER = "count DESC";
    protected static final String TABLE_NAME = "APList";
    private static final long serialVersionUID = 1;
    private int mCount;
    private String mSSID;
    private long mUriId;
    public static final Uri CONTENT_URI = Uri.parse("content://com.samsungimaging.connectionmanager/APList");
    public static final String KEY_SSID = "ssid";
    public static final String KEY_COUNT = "count";
    protected static final String[] PROJECTION = {GalleryColumns.KEY_ID, KEY_SSID, KEY_COUNT};
    protected static HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(GalleryColumns.KEY_ID, GalleryColumns.KEY_ID);
        PROJECTION_MAP.put(KEY_SSID, KEY_SSID);
        PROJECTION_MAP.put(KEY_COUNT, KEY_COUNT);
    }

    public DatabaseAP() {
        this.mUriId = -1L;
        this.mSSID = null;
        this.mCount = 0;
    }

    public DatabaseAP(String ssid) {
        this.mUriId = -1L;
        this.mSSID = null;
        this.mCount = 0;
        this.mSSID = ssid;
    }

    public DatabaseAP(String ssid, int count) {
        this.mUriId = -1L;
        this.mSSID = null;
        this.mCount = 0;
        this.mSSID = ssid;
        this.mCount = count;
    }

    public Uri getUri() {
        if (this.mUriId != -1) {
            return ContentUris.withAppendedId(CONTENT_URI, this.mUriId);
        }
        return null;
    }

    public String getSSID() {
        return this.mSSID;
    }

    public int getConnectedCount() {
        return this.mCount;
    }

    public void setConnectedCount(int count) {
        this.mCount = count;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_SSID, this.mSSID);
        values.put(KEY_COUNT, Integer.valueOf(this.mCount));
        return values;
    }

    public static DatabaseAP builder(Cursor cursor) {
        DatabaseAP ap = new DatabaseAP();
        ap.mUriId = cursor.getLong(0);
        ap.mSSID = cursor.getString(1);
        ap.mCount = cursor.getInt(2);
        return ap;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("AP");
        str.append(" [");
        str.append("ssid=" + this.mSSID);
        str.append(", count=" + this.mCount);
        str.append("]");
        return str.toString();
    }
}

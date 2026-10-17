package com.samsungimaging.connectionmanager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class DatabaseProvider extends ContentProvider {
    private static final int AP = 0;
    private static final int AP_COUNT = 2;
    private static final int AP_ID = 1;
    private static final int AP_MAX = 3;
    protected static final String AUTHORITY = "com.samsungimaging.connectionmanager";
    private static final int CAMERA_MEDIA = 10;
    private static final int CAMERA_MEDIA_COUNT = 12;
    private static final int CAMERA_MEDIA_ID = 11;
    private static final String DATABASE_NAME = "SmartCameraApp.db";
    private static final int DATABASE_VERSION = 1;
    private DatabaseHelper mDatabaseHelper;
    private final UriMatcher mUriMatcher = new UriMatcher(-1);

    public DatabaseProvider() {
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "APList", 0);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "APList/#", 1);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "APList/count", 2);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "APList/max", 3);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "CameraMedia", 10);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "CameraMedia/#", 11);
        this.mUriMatcher.addURI("com.samsungimaging.connectionmanager", "CameraMedia/count", 12);
        this.mDatabaseHelper = null;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DatabaseProvider.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE APList (_id INTEGER PRIMARY KEY, ssid TEXT, count INTEGER);");
            db.execSQL("CREATE TABLE CameraMedia (_id INTEGER PRIMARY KEY, _data TEXT, thumbnail_path TEXT, screen_path TEXT, media_type INTEGER, datetaken INTEGER, orientation INTEGER);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS APList");
            db.execSQL("DROP TABLE IF EXISTS CameraMedia");
            onCreate(db);
        }
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (this.mUriMatcher.match(uri)) {
            case 0:
                return "vnd.android.cursor.dir/vnd.com.samsungimaging.connectionmanager.ap";
            case 1:
                return "vnd.android.cursor.item/vnd.com.samsungimaging.connectionmanager.ap";
            case 10:
                return "vnd.android.cursor.dir/vnd.com.samsungimaging.connectionmanager.cameramedia";
            case 11:
                return "vnd.android.cursor.item/vnd.com.samsungimaging.connectionmanager.cameramedia";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) {
            values = new ContentValues();
        }
        SQLiteDatabase db = this.mDatabaseHelper.getWritableDatabase();
        switch (this.mUriMatcher.match(uri)) {
            case 0:
                long rowId = db.insert("APList", null, values);
                if (rowId > 0) {
                    Uri newUri = ContentUris.withAppendedId(DatabaseAP.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case 10:
                long rowId2 = db.insert("CameraMedia", null, values);
                if (rowId2 > 0) {
                    Uri newUri2 = ContentUris.withAppendedId(DatabaseMedia.CONTENT_URI, rowId2);
                    getContext().getContentResolver().notifyChange(newUri2, null);
                    return newUri2;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = this.mDatabaseHelper.getWritableDatabase();
        switch (this.mUriMatcher.match(uri)) {
            case 0:
                count = db.update("APList", values, selection, selectionArgs);
                break;
            case 1:
                StringBuilder newSelection = new StringBuilder("_id=" + uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(selection)) {
                    newSelection.append(" AND (" + selection + ")");
                }
                count = db.update("APList", values, newSelection.toString(), selectionArgs);
                break;
            case 10:
                count = db.update("CameraMedia", values, selection, selectionArgs);
                break;
            case 11:
                StringBuilder newSelection2 = new StringBuilder("_id=" + uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(selection)) {
                    newSelection2.append(" AND (" + selection + ")");
                }
                count = db.update("CameraMedia", values, newSelection2.toString(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = this.mDatabaseHelper.getWritableDatabase();
        switch (this.mUriMatcher.match(uri)) {
            case 0:
                count = db.delete("APList", selection, selectionArgs);
                break;
            case 1:
                StringBuilder newSelection = new StringBuilder("_id=" + uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(selection)) {
                    newSelection.append(" AND (" + selection + ")");
                }
                count = db.delete("APList", newSelection.toString(), selectionArgs);
                break;
            case 10:
                count = db.delete("CameraMedia", selection, selectionArgs);
                break;
            case 11:
                StringBuilder newSelection2 = new StringBuilder("_id=" + uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(selection)) {
                    newSelection2.append(" AND (" + selection + ")");
                }
                count = db.delete("CameraMedia", newSelection2.toString(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (this.mUriMatcher.match(uri)) {
            case 0:
                builder.setTables("APList");
                builder.setProjectionMap(DatabaseAP.PROJECTION_MAP);
                if (projection == null) {
                    projection = DatabaseAP.PROJECTION;
                }
                if (sortOrder == null) {
                    sortOrder = "count DESC";
                }
                break;
            case 1:
                builder.setTables("APList");
                builder.setProjectionMap(DatabaseAP.PROJECTION_MAP);
                builder.appendWhere("_id=" + uri.getPathSegments().get(1));
                if (projection == null) {
                    projection = DatabaseAP.PROJECTION;
                }
                break;
            case 2:
                SQLiteDatabase db = this.mDatabaseHelper.getReadableDatabase();
                StringBuilder newSelection = new StringBuilder("SELECT count(*) FROM APList");
                if (!TextUtils.isEmpty(selection)) {
                    newSelection.append(" WHERE " + selection);
                }
                Cursor cursor = db.rawQuery(newSelection.toString(), selectionArgs);
                return cursor;
            case 3:
                SQLiteDatabase db2 = this.mDatabaseHelper.getReadableDatabase();
                StringBuilder newSelection2 = new StringBuilder("SELECT max(count) FROM APList");
                if (!TextUtils.isEmpty(selection)) {
                    newSelection2.append(" WHERE " + selection);
                }
                Cursor cursor2 = db2.rawQuery(newSelection2.toString(), selectionArgs);
                return cursor2;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            case 10:
                builder.setTables("CameraMedia");
                builder.setProjectionMap(DatabaseMedia.PROJECTION_MAP);
                if (projection == null) {
                    projection = DatabaseMedia.PROJECTION;
                }
                if (sortOrder == null) {
                    sortOrder = "datetaken DESC";
                }
                break;
            case 11:
                builder.setTables("CameraMedia");
                builder.setProjectionMap(DatabaseMedia.PROJECTION_MAP);
                builder.appendWhere("_id=" + uri.getPathSegments().get(1));
                if (projection == null) {
                    projection = DatabaseMedia.PROJECTION;
                }
                break;
            case 12:
                SQLiteDatabase db3 = this.mDatabaseHelper.getReadableDatabase();
                StringBuilder newSelection3 = new StringBuilder("SELECT count(*) FROM CameraMedia");
                if (!TextUtils.isEmpty(selection)) {
                    newSelection3.append(" WHERE " + selection);
                }
                Cursor cursor3 = db3.rawQuery(newSelection3.toString(), selectionArgs);
                return cursor3;
        }
        SQLiteDatabase db4 = this.mDatabaseHelper.getReadableDatabase();
        Cursor cursor4 = builder.query(db4, projection, selection, selectionArgs, null, null, sortOrder);
        cursor4.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor4;
    }
}

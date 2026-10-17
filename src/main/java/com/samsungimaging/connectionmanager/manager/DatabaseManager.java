package com.samsungimaging.connectionmanager.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DatabaseManager {
    public static void deleteForLocalMedia(Context context, DatabaseMedia media) {
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] selectionArgs = {media.getOriginalPath()};
        context.getContentResolver().delete(uri, "_data=?", selectionArgs);
    }

    public static void putForCameraMedia(Context context, DatabaseMedia cameraMedia) {
        Uri uri = cameraMedia.getUri();
        if (uri == null) {
            context.getContentResolver().insert(DatabaseMedia.CONTENT_URI, cameraMedia.getContentValues());
        } else {
            context.getContentResolver().update(uri, cameraMedia.getContentValues(), null, null);
        }
    }

    public static void deleteForCameraMedia(Context context, Uri uri) {
        if (uri == null) {
            uri = DatabaseMedia.CONTENT_URI;
        }
        context.getContentResolver().delete(uri, null, null);
    }

    private static void putForAP(Context context, DatabaseAP ap) {
        Uri uri = ap.getUri();
        if (uri == null) {
            context.getContentResolver().insert(DatabaseAP.CONTENT_URI, ap.getContentValues());
        } else {
            context.getContentResolver().update(uri, ap.getContentValues(), null, null);
        }
    }

    public static void putForAP(Context context, String ssid) {
        DatabaseAP ap = fetchForAP(context, ssid);
        if (ap == null) {
            ap = new DatabaseAP(ssid, 1);
        } else {
            ap.setConnectedCount(ap.getConnectedCount() + 1);
        }
        putForAP(context, ap);
    }

    public static int getCountForAP(Context context) {
        Uri uri = Uri.withAppendedPath(DatabaseAP.CONTENT_URI, DatabaseAP.KEY_COUNT);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return 0;
        }
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public static void putMaxCountForAP(Context context, String ssid) {
        Uri uri = Uri.withAppendedPath(DatabaseAP.CONTENT_URI, "max");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0) + 1;
            }
            cursor.close();
        }
        DatabaseAP ap = fetchForAP(context, ssid);
        if (ap == null) {
            ap = new DatabaseAP(ssid, count);
        } else {
            ap.setConnectedCount(count);
        }
        putForAP(context, ap);
    }

    public static DatabaseAP fetchForAP(Context context, String ssid) {
        String[] selectionArgs = {ssid};
        Cursor cursor = context.getContentResolver().query(DatabaseAP.CONTENT_URI, null, "ssid=?", selectionArgs, null);
        DatabaseAP ap = null;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ap = DatabaseAP.builder(cursor);
            }
            cursor.close();
        }
        return ap;
    }

    public static ArrayList<DatabaseAP> fetchAllForAP(Context context) {
        ArrayList<DatabaseAP> records = null;
        Cursor cursor = fetchAllToCursorForAP(context);
        if (cursor != null && cursor.getCount() > 0) {
            records = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                records.add(DatabaseAP.builder(cursor));
            }
            cursor.close();
        }
        return records;
    }

    public static Cursor fetchAllToCursorForAP(Context context) {
        return context.getContentResolver().query(DatabaseAP.CONTENT_URI, null, null, null, null);
    }

    public static void deleteForAP(Context context, Uri uri) {
        if (uri == null) {
            uri = DatabaseAP.CONTENT_URI;
        }
        context.getContentResolver().delete(uri, null, null);
    }
}

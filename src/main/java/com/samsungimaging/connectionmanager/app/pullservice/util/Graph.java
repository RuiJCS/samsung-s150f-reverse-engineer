package com.samsungimaging.connectionmanager.app.pullservice.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class Graph {
    public static void saveBitmapToJPEGFile(Bitmap bitmap, String strFilePath) {
        try {
            FileOutputStream filestream = new FileOutputStream(strFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, filestream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getThumbnail(String file) {
        Bitmap bmp = null;
        try {
            ExifInterface exif = new ExifInterface(file);
            if (exif.getThumbnail() == null) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 16;
                bmp = BitmapFactory.decodeFile(file, option);
            } else {
                bmp = BitmapFactory.decodeByteArray(exif.getThumbnail(), 0, exif.getThumbnail().length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}

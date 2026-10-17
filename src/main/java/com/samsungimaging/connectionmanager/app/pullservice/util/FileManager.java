package com.samsungimaging.connectionmanager.app.pullservice.util;

import android.os.Environment;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class FileManager {
    private static Trace.Tag TAG = Trace.Tag.RVF;

    private static String getDefaultStorage() {
        return String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/DCIM/Camera/Samsung Smart Camera Application/RemoteViewfinder";
    }

    public static String createFileName(String fileName) {
        return String.valueOf(getDefaultStorage()) + "/" + renameFile(getDefaultStorage(), fileName);
    }

    public static String renameFile(String dir, String fileName) {
        Trace.d(TAG, "start renameFile() dir : " + dir + " fileName : " + fileName);
        File file = new File(dir, fileName);
        while (file.exists()) {
            fileName = rename(fileName);
            file = new File(dir, fileName);
        }
        Trace.d(TAG, "end renameFile() fileName : " + fileName);
        return fileName;
    }

    private static String rename(String fileName) {
        Pattern p = Pattern.compile("(\\-)(\\d*)+[.]");
        Matcher m = p.matcher(fileName);
        if (m.find()) {
            String str = m.group();
            int count = Integer.parseInt(str.replace("-", "").replace(".", ""));
            String rename = fileName.replaceAll("(\\-)(\\d*)+[.]", "-" + (count + 1) + ".");
            return rename;
        }
        String rename2 = String.valueOf(fileName.substring(0, fileName.lastIndexOf("."))) + "-0" + fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return rename2;
    }
}

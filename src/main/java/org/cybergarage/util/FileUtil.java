package org.cybergarage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/* JADX INFO: loaded from: classes.dex */
public final class FileUtil {
    public static final byte[] load(String fileName) {
        try {
            FileInputStream fin = new FileInputStream(fileName);
            return load(fin);
        } catch (Exception e) {
            Debug.warning(e);
            return new byte[0];
        }
    }

    public static final byte[] load(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            return load(fin);
        } catch (Exception e) {
            Debug.warning(e);
            return new byte[0];
        }
    }

    public static final byte[] load(FileInputStream fin) {
        byte[] readBuf = new byte[524288];
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int readCnt = fin.read(readBuf);
            while (readCnt > 0) {
                bout.write(readBuf, 0, readCnt);
                readCnt = fin.read(readBuf);
            }
            fin.close();
            return bout.toByteArray();
        } catch (Exception e) {
            Debug.warning(e);
            return new byte[0];
        }
    }

    public static final boolean isXMLFileName(String name) {
        if (!StringUtil.hasData(name)) {
            return false;
        }
        String lowerName = name.toLowerCase();
        return lowerName.endsWith("xml");
    }
}

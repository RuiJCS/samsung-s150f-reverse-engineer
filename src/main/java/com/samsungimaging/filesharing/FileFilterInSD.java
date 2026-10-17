package com.samsungimaging.filesharing;

import java.io.File;
import java.io.FilenameFilter;

/* JADX INFO: compiled from: ContentListManager.java */
/* JADX INFO: loaded from: classes.dex */
class FileFilterInSD implements FilenameFilter {
    FileFilterInSD() {
    }

    @Override // java.io.FilenameFilter
    public boolean accept(File dir, String filename) {
        return filename.contains("JPG") || filename.contains("MP4");
    }
}

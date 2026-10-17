package com.samsungimaging.connectionmanager.app.pushservice.autoshare;

import com.samsungimaging.connectionmanager.util.Trace;
import java.lang.reflect.Array;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class HeaderParser {
    private static final Trace.Tag TAG = Trace.Tag.AS;
    String[][] headerArray;
    String mFileName = null;
    String mFileSize = null;
    int line_cnt = 0;
    int mHeaderParsed = 2;

    HeaderParser(String header) {
        parse(header);
    }

    public String getFileName() {
        Trace.d(TAG, "HeaderParser getHeaderParsed : " + this.mFileName);
        return this.mFileName.trim();
    }

    public String getContentLength() {
        Trace.d(TAG, "HeaderParser getHeaderParsed : " + this.mFileSize);
        return this.mFileSize.trim();
    }

    public int getHeaderParsed() {
        Trace.d(TAG, "HeaderParser getHeaderParsed : " + this.mHeaderParsed);
        return this.mHeaderParsed;
    }

    void parse(String header) {
        String[] header_line = header.split(HTTP.CRLF);
        int necessaryLine = 2;
        this.line_cnt = header_line.length;
        Trace.d(TAG, "HeaderParser Total Line : " + this.line_cnt);
        this.headerArray = (String[][]) Array.newInstance((Class<?>) String.class, header_line.length, 2);
        for (int j = 1; j < header_line.length; j++) {
            int colonIndex = header_line[j].indexOf(SOAP.DELIM);
            this.headerArray[j][0] = header_line[j].substring(0, colonIndex);
            this.headerArray[j][1] = header_line[j].substring(colonIndex + 1);
            Trace.d(TAG, "headerArray[" + j + "][pre] : " + this.headerArray[j][0]);
            Trace.d(TAG, "headerArray[" + j + "][post] : " + this.headerArray[j][1]);
        }
        int fileNameIndex = header_line[0].lastIndexOf("/") + 1;
        if (fileNameIndex > 0) {
            necessaryLine = 2 - 1;
            this.mFileName = header_line[0].substring(fileNameIndex);
            Trace.d(TAG, "parsed file name : " + this.mFileName);
        }
        for (int ii = 1; ii < this.line_cnt; ii++) {
            if (this.headerArray[ii][0].contains("Content-length")) {
                this.mFileSize = this.headerArray[ii][1];
                necessaryLine--;
                Trace.d(TAG, "parsed file size: " + this.mFileSize);
                break;
            }
        }
        if (necessaryLine == 0) {
            this.mHeaderParsed = 1;
        }
    }

    public String getSpecificValue(String keyword) {
        String result = null;
        for (int ii = 1; ii <= this.line_cnt; ii++) {
            if (this.headerArray[ii][0].contains(keyword)) {
                result = this.headerArray[ii][1];
                Trace.d(TAG, "getSpecificValue [" + keyword + "] : " + result);
                break;
            }
        }
        return result.trim();
    }
}

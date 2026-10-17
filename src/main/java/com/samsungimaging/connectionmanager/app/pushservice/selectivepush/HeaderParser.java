package com.samsungimaging.connectionmanager.app.pushservice.selectivepush;

import com.samsungimaging.connectionmanager.util.Trace;
import java.lang.reflect.Array;
import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class HeaderParser {
    private static final Trace.Tag TAG = Trace.Tag.SP;
    String[][] headerArray;
    String mFileName = null;
    String mFileSize = null;
    String mFileCommand = null;
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
        int necessaryLine = 3;
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
        String[] firstLine = header_line[0].split(" ");
        for (int i = 0; i < firstLine.length; i++) {
            if (firstLine[i].contains("DCIM")) {
                int fileNameIndex = firstLine[i].lastIndexOf("/") + 1;
                this.mFileName = firstLine[i].substring(fileNameIndex);
                Trace.d(TAG, "parsed file name : " + this.mFileName);
                necessaryLine = 3 - 1;
                break;
            }
        }
        for (int ii = 1; ii < this.line_cnt; ii++) {
            if (this.headerArray[ii][0].contains(HTTP.CONTENT_LENGTH)) {
                this.mFileSize = this.headerArray[ii][1];
                necessaryLine--;
                Trace.d(TAG, "parsed file size: " + this.mFileSize);
                break;
            }
        }
        for (int ii2 = 1; ii2 < this.line_cnt; ii2++) {
            if (this.headerArray[ii2][0].equals("Expect")) {
                necessaryLine--;
                break;
            }
        }
        if (necessaryLine == 0) {
            this.mHeaderParsed = 1;
        }
        for (int ii3 = 1; ii3 < this.line_cnt; ii3++) {
            if (this.headerArray[ii3][0].contains("Command")) {
                this.mFileCommand = this.headerArray[ii3][1].trim();
                if (this.mFileCommand.equals("Getout")) {
                    this.mHeaderParsed = 3;
                    Trace.d(TAG, "parsed Command: " + this.mHeaderParsed);
                    return;
                }
            }
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

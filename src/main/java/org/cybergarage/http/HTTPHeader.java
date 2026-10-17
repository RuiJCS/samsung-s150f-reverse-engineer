package org.cybergarage.http;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import org.cybergarage.util.Debug;

/* JADX INFO: loaded from: classes.dex */
public class HTTPHeader {
    private static int MAX_LENGTH = 1024;
    private String name;
    private String value;

    public HTTPHeader(String name, String value) {
        setName(name);
        setValue(value);
    }

    public HTTPHeader(String lineStr) {
        int colonIdx;
        setName("");
        setValue("");
        if (lineStr != null && (colonIdx = lineStr.indexOf(58)) >= 0) {
            String name = new String(lineStr.getBytes(), 0, colonIdx);
            String value = new String(lineStr.getBytes(), colonIdx + 1, (lineStr.length() - colonIdx) - 1);
            setName(name.trim());
            setValue(value.trim());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean hasName() {
        return this.name != null && this.name.length() > 0;
    }

    public static final String getValue(LineNumberReader reader, String name) {
        String bigName = name.toUpperCase();
        try {
            String lineStr = reader.readLine();
            while (lineStr != null && lineStr.length() > 0) {
                HTTPHeader header = new HTTPHeader(lineStr);
                if (!header.hasName()) {
                    lineStr = reader.readLine();
                } else {
                    String bigLineHeaderName = header.getName().toUpperCase();
                    if (!bigLineHeaderName.equals(bigName)) {
                        lineStr = reader.readLine();
                    } else {
                        return header.getValue();
                    }
                }
            }
            return "";
        } catch (IOException e) {
            Debug.warning(e);
            return "";
        }
    }

    public static final String getValue(String data, String name) {
        StringReader strReader = new StringReader(data);
        LineNumberReader lineReader = new LineNumberReader(strReader, Math.min(data.length(), MAX_LENGTH));
        return getValue(lineReader, name);
    }

    public static final String getValue(byte[] data, String name) {
        return getValue(new String(data), name);
    }

    public static final int getIntegerValue(String data, String name) {
        try {
            return Integer.parseInt(getValue(data, name));
        } catch (Exception e) {
            return 0;
        }
    }

    public static final int getIntegerValue(byte[] data, String name) {
        try {
            return Integer.parseInt(getValue(data, name));
        } catch (Exception e) {
            return 0;
        }
    }
}

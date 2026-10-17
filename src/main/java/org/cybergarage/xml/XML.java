package org.cybergarage.xml;

/* JADX INFO: loaded from: classes.dex */
public class XML {
    public static final String CHARSET_UTF8 = "utf-8";
    public static final String CONTENT_TYPE = "text/xml; charset=\"utf-8\"";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x0047 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:21:0x0049  */
    private static final String escapeXMLChars(String input, boolean quote) {
        if (input == null) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        int oldsize = input.length();
        char[] old = new char[oldsize];
        input.getChars(0, oldsize, old, 0);
        int selstart = 0;
        String entity = null;
        for (int i = 0; i < oldsize; i++) {
            switch (old[i]) {
                case '\"':
                    if (quote) {
                        entity = "&quot;";
                    }
                    break;
                case '&':
                    entity = "&amp;";
                    break;
                case '\'':
                    if (quote) {
                        entity = "&apos;";
                    } else if (quote) {
                        entity = "&quot;";
                    }
                    break;
                case '<':
                    entity = "&lt;";
                    break;
                case '>':
                    entity = "&gt;";
                    break;
            }
            if (entity != null) {
                out.append(old, selstart, i - selstart);
                out.append(entity);
                selstart = i + 1;
                entity = null;
            }
        }
        if (selstart != 0) {
            out.append(old, selstart, oldsize - selstart);
            return out.toString();
        }
        return input;
    }

    public static final String escapeXMLChars(String input) {
        return escapeXMLChars(input, true);
    }

    public static final String unescapeXMLChars(String input) {
        if (input == null) {
            return null;
        }
        String outStr = input.replace("&amp;", "&");
        return outStr.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'").replace("&quot;", "\"");
    }
}

package org.cybergarage.util;

/* JADX INFO: loaded from: classes.dex */
public final class StringUtil {
    public static final boolean hasData(String value) {
        return value != null && value.length() > 0;
    }

    public static final int toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0;
        }
    }

    public static final long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Debug.warning(e);
            return 0L;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x001c  */
    /* JADX WARN: Code duplicated, block: B:16:0x0022 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:18:0x0027 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:19:0x0029  */
    /* JADX WARN: Code duplicated, block: B:24:0x0004 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:25:0x0004 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:27:0x0024 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:28:0x0024 A[SYNTHETIC] */
    public static final int findOf(String str, String chars, int startIdx, int endIdx, int offset, boolean isEqual) {
        char strc;
        int noEqualCnt;
        int n;
        char charc;
        if (offset == 0) {
            return -1;
        }
        int charCnt = chars.length();
        int idx = startIdx;
        while (true) {
            if (offset > 0) {
                if (endIdx < idx) {
                    break;
                }
                strc = str.charAt(idx);
                noEqualCnt = 0;
                for (n = 0; n < charCnt; n++) {
                    charc = chars.charAt(n);
                    if (isEqual) {
                        if (strc != charc) {
                            return idx;
                        }
                    } else {
                        if (strc != charc) {
                            noEqualCnt++;
                        }
                        if (noEqualCnt == charCnt) {
                            return idx;
                        }
                    }
                }
                idx += offset;
            } else {
                if (idx < endIdx) {
                    break;
                }
                strc = str.charAt(idx);
                noEqualCnt = 0;
                while (n < charCnt) {
                    charc = chars.charAt(n);
                    if (isEqual) {
                        if (strc != charc) {
                            return idx;
                        }
                    } else {
                        if (strc != charc) {
                            noEqualCnt++;
                        }
                        if (noEqualCnt == charCnt) {
                            return idx;
                        }
                    }
                }
                idx += offset;
            }
        }
        return -1;
    }

    public static final int findFirstOf(String str, String chars) {
        return findOf(str, chars, 0, str.length() - 1, 1, true);
    }

    public static final int findFirstNotOf(String str, String chars) {
        return findOf(str, chars, 0, str.length() - 1, 1, false);
    }

    public static final int findLastOf(String str, String chars) {
        return findOf(str, chars, str.length() - 1, 0, -1, true);
    }

    public static final int findLastNotOf(String str, String chars) {
        return findOf(str, chars, str.length() - 1, 0, -1, false);
    }

    public static final String trim(String trimStr, String trimChars) {
        int spIdx = findFirstNotOf(trimStr, trimChars);
        if (spIdx < 0) {
            return trimStr;
        }
        String trimStr2 = trimStr.substring(spIdx, trimStr.length());
        int spIdx2 = findLastNotOf(trimStr2, trimChars);
        if (spIdx2 < 0) {
            return trimStr2;
        }
        String buf = trimStr2.substring(0, spIdx2 + 1);
        return buf;
    }
}

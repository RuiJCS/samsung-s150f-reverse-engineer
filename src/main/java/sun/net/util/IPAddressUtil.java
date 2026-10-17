package sun.net.util;

/* JADX INFO: loaded from: classes.dex */
public class IPAddressUtil {
    private static final int INADDR16SZ = 16;
    private static final int INADDR4SZ = 4;
    private static final int INT16SZ = 2;

    public static byte[] textToNumericFormatV4(String src) {
        if (src.length() == 0) {
            return null;
        }
        byte[] res = new byte[4];
        String[] s = src.split("\\.", -1);
        try {
            switch (s.length) {
                case 1:
                    long val = Long.parseLong(s[0]);
                    if (val < 0 || val > 4294967295L) {
                        return null;
                    }
                    res[0] = (byte) ((val >> 24) & 255);
                    res[1] = (byte) (((16777215 & val) >> 16) & 255);
                    res[2] = (byte) (((65535 & val) >> 8) & 255);
                    res[3] = (byte) (val & 255);
                    return res;
                case 2:
                    long val2 = Integer.parseInt(s[0]);
                    if (val2 < 0 || val2 > 255) {
                        return null;
                    }
                    res[0] = (byte) (val2 & 255);
                    long val3 = Integer.parseInt(s[1]);
                    if (val3 < 0 || val3 > 16777215) {
                        return null;
                    }
                    res[1] = (byte) ((val3 >> 16) & 255);
                    res[2] = (byte) (((65535 & val3) >> 8) & 255);
                    res[3] = (byte) (val3 & 255);
                    return res;
                case 3:
                    for (int i = 0; i < 2; i++) {
                        long val4 = Integer.parseInt(s[i]);
                        if (val4 < 0 || val4 > 255) {
                            return null;
                        }
                        res[i] = (byte) (val4 & 255);
                    }
                    long val5 = Integer.parseInt(s[2]);
                    if (val5 < 0 || val5 > 65535) {
                        return null;
                    }
                    res[2] = (byte) ((val5 >> 8) & 255);
                    res[3] = (byte) (val5 & 255);
                    return res;
                case 4:
                    for (int i2 = 0; i2 < 4; i2++) {
                        long val6 = Integer.parseInt(s[i2]);
                        if (val6 < 0 || val6 > 255) {
                            return null;
                        }
                        res[i2] = (byte) (val6 & 255);
                    }
                    return res;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static byte[] textToNumericFormatV6(String src) {
        byte[] v4addr;
        if (src.length() < 2) {
            return null;
        }
        char[] srcb = src.toCharArray();
        byte[] dst = new byte[16];
        int srcb_length = srcb.length;
        int pc = src.indexOf("%");
        if (pc == srcb_length - 1) {
            return null;
        }
        if (pc != -1) {
            srcb_length = pc;
        }
        int colonp = -1;
        int i = 0;
        if (srcb[0] == ':') {
            i = 0 + 1;
            if (srcb[i] != ':') {
                return null;
            }
        }
        int curtok = i;
        boolean saw_xdigit = false;
        int val = 0;
        int j = 0;
        while (true) {
            int i2 = i;
            if (i2 >= srcb_length) {
                break;
            }
            i = i2 + 1;
            char ch = srcb[i2];
            int chval = Character.digit(ch, 16);
            if (chval != -1) {
                val = (val << 4) | chval;
                if (val > 65535) {
                    return null;
                }
                saw_xdigit = true;
            } else if (ch == ':') {
                curtok = i;
                if (!saw_xdigit) {
                    if (colonp != -1) {
                        return null;
                    }
                    colonp = j;
                } else {
                    if (i == srcb_length || j + 2 > 16) {
                        return null;
                    }
                    int j2 = j + 1;
                    dst[j] = (byte) ((val >> 8) & 255);
                    j = j2 + 1;
                    dst[j2] = (byte) (val & 255);
                    saw_xdigit = false;
                    val = 0;
                }
            } else {
                if (ch == '.' && j + 4 <= 16) {
                    String ia4 = src.substring(curtok, srcb_length);
                    int dot_count = 0;
                    int index = 0;
                    while (true) {
                        int index2 = ia4.indexOf(46, index);
                        if (index2 == -1) {
                            break;
                        }
                        dot_count++;
                        index = index2 + 1;
                    }
                    if (dot_count != 3 || (v4addr = textToNumericFormatV4(ia4)) == null) {
                        return null;
                    }
                    int k = 0;
                    while (k < 4) {
                        dst[j] = v4addr[k];
                        k++;
                        j++;
                    }
                    saw_xdigit = false;
                    break;
                }
                return null;
            }
        }
        if (saw_xdigit) {
            if (j + 2 > 16) {
                return null;
            }
            int j3 = j + 1;
            dst[j] = (byte) ((val >> 8) & 255);
            j = j3 + 1;
            dst[j3] = (byte) (val & 255);
        }
        int j4 = j;
        if (colonp != -1) {
            int n = j4 - colonp;
            if (j4 == 16) {
                return null;
            }
            for (int i3 = 1; i3 <= n; i3++) {
                dst[16 - i3] = dst[(colonp + n) - i3];
                dst[(colonp + n) - i3] = 0;
            }
            j4 = 16;
        }
        if (j4 != 16) {
            return null;
        }
        byte[] newdst = convertFromIPv4MappedAddress(dst);
        return newdst == null ? dst : newdst;
    }

    public static boolean isIPv4LiteralAddress(String src) {
        return textToNumericFormatV4(src) != null;
    }

    public static boolean isIPv6LiteralAddress(String src) {
        return textToNumericFormatV6(src) != null;
    }

    public static byte[] convertFromIPv4MappedAddress(byte[] addr) {
        if (!isIPv4MappedAddress(addr)) {
            return null;
        }
        byte[] newAddr = new byte[4];
        System.arraycopy(addr, 12, newAddr, 0, 4);
        return newAddr;
    }

    private static boolean isIPv4MappedAddress(byte[] addr) {
        return addr.length >= 16 && addr[0] == 0 && addr[1] == 0 && addr[2] == 0 && addr[3] == 0 && addr[4] == 0 && addr[5] == 0 && addr[6] == 0 && addr[7] == 0 && addr[8] == 0 && addr[9] == 0 && addr[10] == -1 && addr[11] == -1;
    }
}

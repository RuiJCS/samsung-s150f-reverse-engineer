package org.cybergarage.upnp;

import org.cybergarage.http.HTTPStatus;

/* JADX INFO: loaded from: classes.dex */
public class UPnPStatus {
    public static final int ACTION_FAILED = 501;
    public static final int INVALID_ACTION = 401;
    public static final int INVALID_ARGS = 402;
    public static final int INVALID_VAR = 404;
    public static final int OUT_OF_SYNC = 403;
    public static final int PRECONDITION_FAILED = 412;
    private int code;
    private String description;

    public static final String code2String(int code) {
        switch (code) {
            case INVALID_ACTION /* 401 */:
                return "Invalid Action";
            case INVALID_ARGS /* 402 */:
                return "Invalid Args";
            case OUT_OF_SYNC /* 403 */:
                return "Out of Sync";
            case 404:
                return "Invalid Var";
            case 412:
                return "Precondition Failed";
            case ACTION_FAILED /* 501 */:
                return "Action Failed";
            default:
                return HTTPStatus.code2String(code);
        }
    }

    public UPnPStatus() {
        setCode(0);
        setDescription("");
    }

    public UPnPStatus(int code, String desc) {
        setCode(code);
        setDescription(desc);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

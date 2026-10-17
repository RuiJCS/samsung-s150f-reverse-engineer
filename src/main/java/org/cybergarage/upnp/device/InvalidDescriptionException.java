package org.cybergarage.upnp.device;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class InvalidDescriptionException extends Exception {
    public InvalidDescriptionException() {
    }

    public InvalidDescriptionException(String s) {
        super(s);
    }

    public InvalidDescriptionException(String s, File file) {
        super(String.valueOf(s) + " (" + file.toString() + ")");
    }

    public InvalidDescriptionException(Exception e) {
        super(e.getMessage());
    }
}

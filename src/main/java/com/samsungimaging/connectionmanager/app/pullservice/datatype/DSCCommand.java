package com.samsungimaging.connectionmanager.app.pullservice.datatype;

/* JADX INFO: loaded from: classes.dex */
public class DSCCommand {
    private int commandID;
    private String param;

    public DSCCommand(int commandID, String param) {
        this.commandID = commandID;
        this.param = param;
    }

    public int getcommandID() {
        return this.commandID;
    }

    public void setcommandID(int commandID) {
        this.commandID = commandID;
    }

    public String getparam() {
        return this.param;
    }

    public void setparam(String param) {
        this.param = param;
    }
}

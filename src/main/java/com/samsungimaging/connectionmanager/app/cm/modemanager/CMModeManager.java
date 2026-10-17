package com.samsungimaging.connectionmanager.app.cm.modemanager;

import com.samsungimaging.connectionmanager.app.cm.Interface.IModeClient;
import com.samsungimaging.connectionmanager.app.cm.Interface.IModeServer;

/* JADX INFO: loaded from: classes.dex */
public class CMModeManager {
    public static void removeModeServerListener(IModeServer listener) {
        if (listener != null) {
            ModeServer.getInstance().removeModeServerListener(listener);
        }
    }

    public static void runModeServer(IModeServer listener) {
        if (listener != null) {
            ModeServer.getInstance().addModeServerListener(listener);
            ModeServer.getInstance().runModeServer();
        }
    }

    public static void stopModeServer() {
        ModeServer.getInstance().stopModeServer();
    }

    public static void runModeClient(IModeClient listener, boolean isbyebye) {
        if (listener != null) {
            ModeClient.getInstance().addModeClientListener(listener);
            ModeClient.getInstance().runModeClient(isbyebye);
        }
    }

    public static void stopModeClient() {
        ModeClient.getInstance().stopModeClient();
    }
}

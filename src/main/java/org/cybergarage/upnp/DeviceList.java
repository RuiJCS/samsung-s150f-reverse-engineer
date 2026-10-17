package org.cybergarage.upnp;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class DeviceList extends Vector {
    public static final String ELEM_NAME = "deviceList";

    public Device getDevice(int n) {
        return (Device) get(n);
    }
}

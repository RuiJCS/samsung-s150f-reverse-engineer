package org.cybergarage.upnp.device;

import org.cybergarage.upnp.Device;

/* JADX INFO: loaded from: classes.dex */
public interface DeviceChangeListener {
    void deviceAdded(Device device);

    void deviceRemoved(Device device);
}

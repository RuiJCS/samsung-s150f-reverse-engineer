package org.cybergarage.upnp.device;

import org.cybergarage.upnp.ssdp.SSDPPacket;

/* JADX INFO: loaded from: classes.dex */
public interface SearchListener {
    void deviceSearchReceived(SSDPPacket sSDPPacket);
}

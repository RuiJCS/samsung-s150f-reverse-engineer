package org.cybergarage.upnp.device;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.ThreadCore;

/* JADX INFO: loaded from: classes.dex */
public class Advertiser extends ThreadCore {
    private Device device;

    public Advertiser(Device dev) {
        setDevice(dev);
    }

    public void setDevice(Device dev) {
        this.device = dev;
    }

    public Device getDevice() {
        return this.device;
    }

    @Override // org.cybergarage.util.ThreadCore, java.lang.Runnable
    public void run() {
        Device dev = getDevice();
        long leaseTime = dev.getLeaseTime();
        while (isRunnable()) {
            long notifyInterval = (leaseTime / 4) + ((long) (leaseTime * Math.random() * 0.25d));
            try {
                Thread.sleep(notifyInterval * 1000);
            } catch (InterruptedException e) {
            }
            dev.announce();
        }
    }
}

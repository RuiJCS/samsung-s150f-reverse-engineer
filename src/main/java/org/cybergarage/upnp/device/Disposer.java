package org.cybergarage.upnp.device;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.ThreadCore;

/* JADX INFO: loaded from: classes.dex */
public class Disposer extends ThreadCore {
    private ControlPoint ctrlPoint;

    public Disposer(ControlPoint ctrlp) {
        setControlPoint(ctrlp);
    }

    public void setControlPoint(ControlPoint ctrlp) {
        this.ctrlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return this.ctrlPoint;
    }

    @Override // org.cybergarage.util.ThreadCore, java.lang.Runnable
    public void run() {
        ControlPoint ctrlp = getControlPoint();
        long monitorInterval = ctrlp.getExpiredDeviceMonitoringInterval() * 1000;
        while (isRunnable()) {
            try {
                Thread.sleep(monitorInterval);
            } catch (InterruptedException e) {
            }
            ctrlp.removeExpiredDevices();
        }
    }
}

package org.cybergarage.upnp.control;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.ThreadCore;

/* JADX INFO: loaded from: classes.dex */
public class RenewSubscriber extends ThreadCore {
    public static final long INTERVAL = 900;
    private ControlPoint ctrlPoint;

    public RenewSubscriber(ControlPoint ctrlp) {
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
        while (isRunnable()) {
            try {
                Thread.sleep(900000L);
            } catch (InterruptedException e) {
            }
            ctrlp.renewSubscriberService();
        }
    }
}

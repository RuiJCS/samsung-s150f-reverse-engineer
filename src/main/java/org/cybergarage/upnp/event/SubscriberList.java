package org.cybergarage.upnp.event;

import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class SubscriberList extends Vector {
    public Subscriber getSubscriber(int n) {
        Object obj = null;
        try {
            obj = get(n);
        } catch (Exception e) {
        }
        return (Subscriber) obj;
    }
}

package org.cybergarage.upnp.event;

import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class Subscriber {
    private String SID = null;
    private String ifAddr = "";
    private String deliveryURL = "";
    private String deliveryHost = "";
    private String deliveryPath = "";
    private int deliveryPort = 0;
    private long timeOut = 0;
    private long subscriptionTime = 0;
    private long notifyCount = 0;

    public Subscriber() {
        renew();
    }

    public String getSID() {
        return this.SID;
    }

    public void setSID(String sid) {
        this.SID = sid;
    }

    public void setInterfaceAddress(String addr) {
        this.ifAddr = addr;
    }

    public String getInterfaceAddress() {
        return this.ifAddr;
    }

    public String getDeliveryURL() {
        return this.deliveryURL;
    }

    public void setDeliveryURL(String deliveryURL) {
        this.deliveryURL = deliveryURL;
        try {
            URL url = new URL(deliveryURL);
            this.deliveryHost = url.getHost();
            this.deliveryPath = url.getPath();
            this.deliveryPort = url.getPort();
        } catch (Exception e) {
        }
    }

    public String getDeliveryHost() {
        return this.deliveryHost;
    }

    public String getDeliveryPath() {
        return this.deliveryPath;
    }

    public int getDeliveryPort() {
        return this.deliveryPort;
    }

    public long getTimeOut() {
        return this.timeOut;
    }

    public void setTimeOut(long value) {
        this.timeOut = value;
    }

    public boolean isExpired() {
        long currTime = System.currentTimeMillis();
        if (this.timeOut == -1) {
            return false;
        }
        long expiredTime = getSubscriptionTime() + (getTimeOut() * 1000);
        return expiredTime < currTime;
    }

    public long getSubscriptionTime() {
        return this.subscriptionTime;
    }

    public void setSubscriptionTime(long time) {
        this.subscriptionTime = time;
    }

    public long getNotifyCount() {
        return this.notifyCount;
    }

    public void setNotifyCount(int cnt) {
        this.notifyCount = cnt;
    }

    public void incrementNotifyCount() {
        if (this.notifyCount == Long.MAX_VALUE) {
            this.notifyCount = 1L;
        } else {
            this.notifyCount++;
        }
    }

    public void renew() {
        setSubscriptionTime(System.currentTimeMillis());
        setNotifyCount(0);
    }
}

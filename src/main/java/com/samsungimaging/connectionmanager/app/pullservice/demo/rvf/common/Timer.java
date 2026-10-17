package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.common;

import android.os.Handler;
import android.os.Message;

/* JADX INFO: loaded from: classes.dex */
public class Timer extends Thread {
    int delay;
    Handler handler;
    int messageID;
    long startTime = 0;

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.startTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(this.delay);
                Message msg = new Message();
                msg.what = this.messageID;
                msg.obj = Long.valueOf(System.currentTimeMillis() - this.startTime);
                this.handler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public Timer(Handler handler, int messageID, int delay) {
        this.handler = handler;
        this.messageID = messageID;
        this.delay = delay;
    }
}

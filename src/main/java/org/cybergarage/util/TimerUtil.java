package org.cybergarage.util;

/* JADX INFO: loaded from: classes.dex */
public final class TimerUtil {
    public static final void wait(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (Exception e) {
        }
    }

    public static final void waitRandom(int time) {
        int waitTime = (int) (Math.random() * ((double) time));
        try {
            Thread.sleep(waitTime);
        } catch (Exception e) {
        }
    }
}

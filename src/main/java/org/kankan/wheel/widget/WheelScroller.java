package org.kankan.wheel.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ActivityChooserView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/* JADX INFO: loaded from: classes.dex */
public class WheelScroller {
    public static final int MIN_DELTA_FOR_SCROLLING = 1;
    private static final int SCROLLING_DURATION = 400;
    private Context context;
    private GestureDetector gestureDetector;
    private boolean isScrollingPerformed;
    private int lastScrollX;
    private float lastTouchedX;
    private ScrollingListener listener;
    private Scroller scroller;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: org.kankan.wheel.widget.WheelScroller.1
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            WheelScroller.this.lastScrollX = 0;
            WheelScroller.this.scroller.fling(WheelScroller.this.lastScrollX, 0, (int) (-velocityX), 0, -2147483647, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 0, 0);
            WheelScroller.this.setNextMessage(0);
            return true;
        }
    };
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;
    private Handler animationHandler = new Handler() { // from class: org.kankan.wheel.widget.WheelScroller.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            WheelScroller.this.scroller.computeScrollOffset();
            int currX = WheelScroller.this.scroller.getCurrX();
            int delta = WheelScroller.this.lastScrollX - currX;
            WheelScroller.this.lastScrollX = currX;
            if (delta != 0) {
                WheelScroller.this.listener.onScroll(delta);
            }
            if (Math.abs(currX - WheelScroller.this.scroller.getFinalX()) < 1) {
                WheelScroller.this.scroller.getFinalX();
                WheelScroller.this.scroller.forceFinished(true);
            }
            if (!WheelScroller.this.scroller.isFinished()) {
                WheelScroller.this.animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what != 0) {
                WheelScroller.this.finishScrolling();
            } else {
                WheelScroller.this.justify();
            }
        }
    };

    public interface ScrollingListener {
        void onFinished();

        void onJustify();

        void onScroll(int i);

        void onStarted();
    }

    public WheelScroller(Context context, ScrollingListener listener) {
        this.gestureDetector = new GestureDetector(context, this.gestureListener);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scroller = new Scroller(context);
        this.listener = listener;
        this.context = context;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.scroller.forceFinished(true);
        this.scroller = new Scroller(this.context, interpolator);
    }

    public void scroll(int distance, int time) {
        this.scroller.forceFinished(true);
        this.lastScrollX = 0;
        this.scroller.startScroll(0, 0, distance, 0, time != 0 ? time : 400);
        setNextMessage(0);
        startScrolling();
    }

    public void stopScrolling() {
        this.scroller.forceFinished(true);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.lastTouchedX = event.getX();
                this.scroller.forceFinished(true);
                clearMessages();
                break;
            case 2:
                int distanceX = (int) (event.getX() - this.lastTouchedX);
                if (distanceX != 0) {
                    startScrolling();
                    this.listener.onScroll(distanceX);
                    this.lastTouchedX = event.getX();
                }
                break;
        }
        if (!this.gestureDetector.onTouchEvent(event) && event.getAction() == 1) {
            justify();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextMessage(int message) {
        clearMessages();
        this.animationHandler.sendEmptyMessage(message);
    }

    private void clearMessages() {
        this.animationHandler.removeMessages(0);
        this.animationHandler.removeMessages(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void justify() {
        this.listener.onJustify();
        setNextMessage(1);
    }

    private void startScrolling() {
        if (!this.isScrollingPerformed) {
            this.isScrollingPerformed = true;
            this.listener.onStarted();
        }
    }

    void finishScrolling() {
        if (this.isScrollingPerformed) {
            this.listener.onFinished();
            this.isScrollingPerformed = false;
        }
    }
}

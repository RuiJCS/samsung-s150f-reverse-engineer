package com.samsungimaging.connectionmanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/* JADX INFO: loaded from: classes.dex */
public class ScaleAnimationRelativeLayout extends RelativeLayout {
    private ScaleAnimation mScaleInAnimation;
    private ScaleAnimation mScaleOutAnimation;

    public ScaleAnimationRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScaleInAnimation = null;
        this.mScaleOutAnimation = null;
        init();
    }

    public ScaleAnimationRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScaleInAnimation = null;
        this.mScaleOutAnimation = null;
        init();
    }

    public ScaleAnimationRelativeLayout(Context context) {
        super(context);
        this.mScaleInAnimation = null;
        this.mScaleOutAnimation = null;
        init();
    }

    private void init() {
        this.mScaleInAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, 1, 0.5f, 1, 0.5f);
        this.mScaleInAnimation.setDuration(100L);
        this.mScaleInAnimation.setFillAfter(true);
        this.mScaleOutAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, 1, 0.5f, 1, 0.5f);
        this.mScaleOutAnimation.setDuration(100L);
        this.mScaleOutAnimation.setFillAfter(true);
        this.mScaleOutAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.samsungimaging.connectionmanager.view.ScaleAnimationRelativeLayout.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                animation.setFillAfter(false);
            }
        });
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                startAnimation(this.mScaleInAnimation);
                break;
            case 1:
                startAnimation(this.mScaleOutAnimation);
                break;
            case 3:
                clearAnimation();
                break;
        }
        return super.onTouchEvent(event);
    }
}

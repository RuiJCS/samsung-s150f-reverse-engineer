package com.samsungimaging.connectionmanager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/* JADX INFO: loaded from: classes.dex */
public class VerticalSeekBar extends SeekBar {
    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onDraw(Canvas c) {
        c.rotate(-90.0f);
        c.translate(-getHeight(), 0.0f);
        super.onDraw(c);
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case 0:
                case 1:
                case 2:
                    int i = getMax() - ((int) ((getMax() * event.getY()) / getHeight()));
                    setProgress(i);
                    Log.i("Progress", new StringBuilder(String.valueOf(getProgress())).toString());
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                default:
                    return true;
            }
        }
        return true;
    }

    public void updateThumb() {
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}

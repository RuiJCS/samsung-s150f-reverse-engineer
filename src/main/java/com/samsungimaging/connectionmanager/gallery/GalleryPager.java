package com.samsungimaging.connectionmanager.gallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.samsungimaging.connectionmanager.widget.ImageViewer;

/* JADX INFO: loaded from: classes.dex */
public class GalleryPager extends ViewPager {
    private GestureDetector mGestureDetector;

    private class InternalGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final float NUM_OF_FLING_AVAILABLE = 2000.0f;

        private InternalGestureListener() {
        }

        /* synthetic */ InternalGestureListener(GalleryPager galleryPager, InternalGestureListener internalGestureListener) {
            this();
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) < Math.abs(velocityY) || Math.abs(velocityX) <= NUM_OF_FLING_AVAILABLE || GalleryPager.this.getCurrentImageViewer().isScaleMin()) {
                return false;
            }
            int currentPosition = GalleryPager.this.getCurrentItem();
            ImageViewer currentViewer = GalleryPager.this.getCurrentImageViewer();
            if (velocityX > 0.0f && currentViewer.isBoundaryLeft()) {
                if (currentPosition <= 0) {
                    return false;
                }
                GalleryPager.this.setCurrentItem(currentPosition - 1);
                currentViewer.setScaleMin();
                return true;
            }
            if (velocityX >= 0.0f || !currentViewer.isBoundaryRight() || currentPosition >= GalleryPager.this.getAdapter().getCount() - 1) {
                return false;
            }
            GalleryPager.this.setCurrentItem(currentPosition + 1);
            currentViewer.setScaleMin();
            return true;
        }
    }

    public GalleryPager(Context context) {
        super(context);
        init();
    }

    public GalleryPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.mGestureDetector = new GestureDetector(getContext(), new InternalGestureListener(this, null));
    }

    @Override // android.support.v4.view.ViewPager
    public GalleryPagerAdapter getAdapter() {
        return (GalleryPagerAdapter) super.getAdapter();
    }

    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (touchable(event)) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override // android.support.v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (touchable(event)) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    private boolean touchable(MotionEvent event) {
        if (this.mGestureDetector.onTouchEvent(event)) {
            return false;
        }
        switch (event.getAction()) {
            case 2:
                if (!getCurrentImageViewer().isScaleMin()) {
                    return false;
                }
            default:
                return true;
        }
    }

    public ImageViewer getCurrentImageViewer() {
        return getAdapter().getItem(getCurrentItem());
    }
}

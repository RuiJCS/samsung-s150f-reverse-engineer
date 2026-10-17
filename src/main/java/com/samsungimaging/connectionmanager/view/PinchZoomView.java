package com.samsungimaging.connectionmanager.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class PinchZoomView extends RecycleImageView {
    public static final float NUM_OF_SCALE_DOUBLE_TAB = 2.0f;
    public static final float NUM_OF_SCALE_MAX = 6.0f;
    public static final float NUM_OF_SCALE_MIN = 1.0f;
    private PointF mCenterRatio;
    private InternalDoubleTap mDoubleTap;
    private InternalFling mFling;
    private GestureDetector mGestureDetector;
    private float mImageHeight;
    private float mImageWidth;
    private boolean mInitializedLayout;
    private Matrix mMatrix;
    private float[] mMatrixCoordinate;
    private int mOrientation;
    private float mOriginalImageHeight;
    private float mOriginalImageWidth;
    private boolean mPinchZoomable;
    private float mScale;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleMax;
    private float mScaleMin;
    private State mState;

    private enum State {
        NONE,
        ZOOM;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static State[] valuesCustom() {
            State[] stateArrValuesCustom = values();
            int length = stateArrValuesCustom.length;
            State[] stateArr = new State[length];
            System.arraycopy(stateArrValuesCustom, 0, stateArr, 0, length);
            return stateArr;
        }
    }

    public PinchZoomView(Context context) {
        super(context);
        this.mState = State.NONE;
        this.mScaleMin = 0.0f;
        this.mScaleMax = 0.0f;
        this.mScale = 0.0f;
        this.mOriginalImageWidth = 0.0f;
        this.mOriginalImageHeight = 0.0f;
        this.mImageWidth = 0.0f;
        this.mImageHeight = 0.0f;
        this.mMatrix = new Matrix();
        this.mMatrixCoordinate = new float[9];
        this.mOrientation = 0;
        this.mInitializedLayout = false;
        this.mDoubleTap = null;
        this.mFling = null;
        this.mCenterRatio = new PointF();
        this.mPinchZoomable = true;
        init();
    }

    public PinchZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = State.NONE;
        this.mScaleMin = 0.0f;
        this.mScaleMax = 0.0f;
        this.mScale = 0.0f;
        this.mOriginalImageWidth = 0.0f;
        this.mOriginalImageHeight = 0.0f;
        this.mImageWidth = 0.0f;
        this.mImageHeight = 0.0f;
        this.mMatrix = new Matrix();
        this.mMatrixCoordinate = new float[9];
        this.mOrientation = 0;
        this.mInitializedLayout = false;
        this.mDoubleTap = null;
        this.mFling = null;
        this.mCenterRatio = new PointF();
        this.mPinchZoomable = true;
        init();
    }

    public PinchZoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mState = State.NONE;
        this.mScaleMin = 0.0f;
        this.mScaleMax = 0.0f;
        this.mScale = 0.0f;
        this.mOriginalImageWidth = 0.0f;
        this.mOriginalImageHeight = 0.0f;
        this.mImageWidth = 0.0f;
        this.mImageHeight = 0.0f;
        this.mMatrix = new Matrix();
        this.mMatrixCoordinate = new float[9];
        this.mOrientation = 0;
        this.mInitializedLayout = false;
        this.mDoubleTap = null;
        this.mFling = null;
        this.mCenterRatio = new PointF();
        this.mPinchZoomable = true;
        init();
    }

    private void init() {
        InternalGestureListener internalGestureListener = null;
        this.mInitializedLayout = false;
        this.mScaleDetector = new ScaleGestureDetector(getContext(), new InternalGestureListener(this, internalGestureListener));
        this.mGestureDetector = new GestureDetector(getContext(), new InternalGestureListener(this, internalGestureListener));
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public void setPinchZoomable(boolean pinchZoomable) {
        this.mPinchZoomable = pinchZoomable;
    }

    @Override // com.samsungimaging.connectionmanager.view.RecycleImageView, android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setImage();
    }

    private void setImage() {
        this.mInitializedLayout = false;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            this.mOriginalImageWidth = drawable.getIntrinsicWidth();
            this.mOriginalImageHeight = drawable.getIntrinsicHeight();
            if (this.mOriginalImageWidth != 0.0f && this.mOriginalImageHeight != 0.0f) {
                Trace.d(Trace.Tag.COMMON, "OriginalImageWidth : " + this.mOriginalImageWidth + ",    OriginalImageHeight : " + this.mOriginalImageHeight);
            }
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float scale;
        if (this.mOriginalImageWidth <= 0.0f || this.mOriginalImageHeight <= 0.0f) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        if (this.mDoubleTap != null) {
            this.mDoubleTap.cancel();
        }
        if (this.mFling != null) {
            this.mFling.cancel();
        }
        int orientation = getResources().getConfiguration().orientation;
        float scaleX = getWidth() / this.mOriginalImageWidth;
        float scaleY = getHeight() / this.mOriginalImageHeight;
        float scale2 = Math.min(scaleX, scaleY);
        this.mMatrix.getValues(this.mMatrixCoordinate);
        if (!this.mInitializedLayout || this.mScale == this.mScaleMin) {
            this.mInitializedLayout = true;
            if (scale2 >= 6.0f) {
                scale2 = 6.0f;
                this.mScaleMax = 6.0f;
                this.mScaleMin = 6.0f;
                this.mScale = 6.0f;
            } else {
                this.mScale = 1.0f;
                this.mScaleMin = 1.0f;
                this.mScaleMax = 6.0f / scale2;
            }
            this.mImageWidth = this.mOriginalImageWidth * scale2;
            this.mImageHeight = this.mOriginalImageHeight * scale2;
            this.mMatrixCoordinate[0] = scale2;
            this.mMatrixCoordinate[4] = scale2;
            this.mMatrixCoordinate[2] = (getWidth() - this.mImageWidth) / 2.0f;
            this.mMatrixCoordinate[5] = (getHeight() - this.mImageHeight) / 2.0f;
            this.mMatrix.setValues(this.mMatrixCoordinate);
        } else if (orientation != this.mOrientation) {
            if (scale2 >= 6.0f) {
                scale = 6.0f;
                this.mScaleMax = 6.0f;
                this.mScaleMin = 6.0f;
                this.mScale = 6.0f;
            } else {
                float newScaleMax = 6.0f / scale2;
                this.mScaleMin = 1.0f;
                this.mScale = (this.mScale / this.mScaleMax) * newScaleMax;
                if (this.mScale < 1.0f) {
                    this.mScale = 1.0f;
                }
                this.mScaleMax = newScaleMax;
                scale = scale2 * this.mScale;
            }
            this.mMatrixCoordinate[0] = scale;
            this.mMatrixCoordinate[4] = scale;
            this.mMatrixCoordinate[2] = -((this.mImageWidth * this.mCenterRatio.x) - (getWidth() / 2));
            this.mMatrixCoordinate[5] = -((this.mImageHeight * this.mCenterRatio.y) - (getHeight() / 2));
            this.mMatrix.setValues(this.mMatrixCoordinate);
            checkScaleBound();
        }
        setMatrix(this.mMatrix);
        this.mOrientation = orientation;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mScaleMin != this.mScaleMax && this.mPinchZoomable) {
            this.mScaleDetector.onTouchEvent(event);
            this.mGestureDetector.onTouchEvent(event);
            setMatrix(this.mMatrix);
        }
        return true;
    }

    private class InternalGestureListener extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private InternalGestureListener() {
        }

        /* synthetic */ InternalGestureListener(PinchZoomView pinchZoomView, InternalGestureListener internalGestureListener) {
            this();
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            PinchZoomView.this.setScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            PinchZoomView.this.mState = State.ZOOM;
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector detector) {
            PinchZoomView.this.mState = State.NONE;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            if (PinchZoomView.this.mFling != null) {
                PinchZoomView.this.mFling.cancel();
                return true;
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (PinchZoomView.this.mScale != PinchZoomView.this.mScaleMin && PinchZoomView.this.mState != State.ZOOM && e2.getPointerCount() == 1) {
                PinchZoomView.this.mMatrix.getValues(PinchZoomView.this.mMatrixCoordinate);
                float translateX = PinchZoomView.this.mMatrixCoordinate[2] - distanceX;
                float translateY = PinchZoomView.this.mMatrixCoordinate[5] - distanceY;
                if (PinchZoomView.this.getWidth() < PinchZoomView.this.mImageWidth) {
                    PinchZoomView.this.mMatrixCoordinate[2] = PinchZoomView.this.checkDragBound(translateX, PinchZoomView.this.getWidth(), PinchZoomView.this.mImageWidth);
                }
                if (PinchZoomView.this.getHeight() < PinchZoomView.this.mImageHeight) {
                    PinchZoomView.this.mMatrixCoordinate[5] = PinchZoomView.this.checkDragBound(translateY, PinchZoomView.this.getHeight(), PinchZoomView.this.mImageHeight);
                }
                PinchZoomView.this.mMatrix.setValues(PinchZoomView.this.mMatrixCoordinate);
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent e) {
            float scale;
            if (PinchZoomView.this.mState != State.ZOOM) {
                if (PinchZoomView.this.mScale != PinchZoomView.this.mScaleMin) {
                    scale = PinchZoomView.this.mScaleMin;
                } else {
                    scale = PinchZoomView.this.mOriginalImageWidth / PinchZoomView.this.mImageWidth;
                    if (scale < 1.0f) {
                        if (PinchZoomView.this.mScaleMax <= 2.0f) {
                            scale = PinchZoomView.this.mScaleMax;
                        } else {
                            scale = 2.0f;
                        }
                    }
                }
                if (PinchZoomView.this.mDoubleTap == null) {
                    PinchZoomView.this.mDoubleTap = new InternalDoubleTap(PinchZoomView.this, null);
                }
                PinchZoomView.this.mDoubleTap.execute(scale, e);
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (PinchZoomView.this.mScale != PinchZoomView.this.mScaleMin && PinchZoomView.this.mState != State.ZOOM) {
                if (PinchZoomView.this.mFling == null) {
                    PinchZoomView.this.mFling = PinchZoomView.this.new InternalFling();
                }
                PinchZoomView.this.mFling.execute(velocityX / 4.0f, velocityY / 4.0f);
            }
            return true;
        }
    }

    private class InternalDoubleTap implements Runnable {
        private static final float SCALE_TIME_DELAY = 300.0f;
        private float mFromScale;
        private DecelerateInterpolator mInterpolator;
        private long mStartTime;
        private float mToScale;
        private PointF mTouchPosition;

        private InternalDoubleTap() {
            this.mTouchPosition = null;
            this.mInterpolator = new DecelerateInterpolator();
        }

        /* synthetic */ InternalDoubleTap(PinchZoomView pinchZoomView, InternalDoubleTap internalDoubleTap) {
            this();
        }

        public void execute(float scale, MotionEvent e) {
            PinchZoomView.this.mState = State.ZOOM;
            this.mStartTime = SystemClock.elapsedRealtime();
            this.mFromScale = PinchZoomView.this.mScale;
            this.mToScale = scale;
            this.mTouchPosition = new PointF(e.getX(), e.getY());
            PinchZoomView.this.post(this);
        }

        public void cancel() {
            PinchZoomView.this.removeCallbacks(this);
            PinchZoomView.this.mState = State.NONE;
        }

        @Override // java.lang.Runnable
        public void run() {
            float elapsedTime = getElapsedTime();
            float scale = (this.mFromScale + ((this.mToScale - this.mFromScale) * elapsedTime)) / PinchZoomView.this.mScale;
            if (this.mToScale == PinchZoomView.this.mScaleMin) {
                PinchZoomView.this.setScale(scale, PinchZoomView.this.getWidth() / 2, PinchZoomView.this.getHeight() / 2);
            } else {
                PinchZoomView.this.setScale(scale, this.mTouchPosition.x, this.mTouchPosition.y);
                translateCenterForTouchPosition(elapsedTime);
            }
            PinchZoomView.this.setMatrix(PinchZoomView.this.mMatrix);
            if (elapsedTime < 1.0f) {
                PinchZoomView.this.post(this);
            } else {
                PinchZoomView.this.mState = State.NONE;
            }
        }

        private float getElapsedTime() {
            float elapsed = (SystemClock.elapsedRealtime() - this.mStartTime) / SCALE_TIME_DELAY;
            return this.mInterpolator.getInterpolation(Math.min(1.0f, elapsed));
        }

        private void translateCenterForTouchPosition(float elapsedTime) {
            float translateX = ((PinchZoomView.this.getWidth() / 2) - this.mTouchPosition.x) * elapsedTime;
            float translateY = ((PinchZoomView.this.getHeight() / 2) - this.mTouchPosition.y) * elapsedTime;
            PinchZoomView.this.mMatrix.getValues(PinchZoomView.this.mMatrixCoordinate);
            float[] fArr = PinchZoomView.this.mMatrixCoordinate;
            fArr[2] = fArr[2] + translateX;
            float[] fArr2 = PinchZoomView.this.mMatrixCoordinate;
            fArr2[5] = fArr2[5] + translateY;
            PinchZoomView.this.mMatrix.setValues(PinchZoomView.this.mMatrixCoordinate);
            this.mTouchPosition.x += translateX;
            this.mTouchPosition.y += translateY;
            PinchZoomView.this.checkScaleBound();
        }
    }

    private class InternalFling implements Runnable {
        private Scroller mScroller;

        InternalFling() {
            this.mScroller = new Scroller(PinchZoomView.this.getContext(), new AccelerateDecelerateInterpolator());
        }

        public void execute(float velocityX, float velocityY) {
            int maxX;
            int minX;
            int maxY;
            int minY;
            PinchZoomView.this.mMatrix.getValues(PinchZoomView.this.mMatrixCoordinate);
            int startX = (int) PinchZoomView.this.mMatrixCoordinate[2];
            int startY = (int) PinchZoomView.this.mMatrixCoordinate[5];
            if (PinchZoomView.this.mImageWidth > PinchZoomView.this.getWidth()) {
                minX = PinchZoomView.this.getWidth() - ((int) PinchZoomView.this.mImageWidth);
                maxX = 0;
            } else {
                maxX = startX;
                minX = startX;
            }
            if (PinchZoomView.this.mImageHeight > PinchZoomView.this.getHeight()) {
                minY = PinchZoomView.this.getHeight() - ((int) PinchZoomView.this.mImageHeight);
                maxY = 0;
            } else {
                maxY = startY;
                minY = startY;
            }
            this.mScroller.fling(startX, startY, (int) velocityX, (int) velocityY, minX, maxX, minY, maxY);
            PinchZoomView.this.post(this);
        }

        public void cancel() {
            PinchZoomView.this.removeCallbacks(this);
            if (this.mScroller != null) {
                this.mScroller.forceFinished(true);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
                if (PinchZoomView.this.mMatrixCoordinate[2] != this.mScroller.getCurrX() || PinchZoomView.this.mMatrixCoordinate[5] != this.mScroller.getCurrY()) {
                    PinchZoomView.this.mMatrix.getValues(PinchZoomView.this.mMatrixCoordinate);
                    PinchZoomView.this.mMatrixCoordinate[2] = this.mScroller.getCurrX();
                    PinchZoomView.this.mMatrixCoordinate[5] = this.mScroller.getCurrY();
                    PinchZoomView.this.mMatrix.setValues(PinchZoomView.this.mMatrixCoordinate);
                    PinchZoomView.this.setMatrix(PinchZoomView.this.mMatrix);
                }
                PinchZoomView.this.post(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float checkDragBound(float translate, float viewSize, float imageSize) {
        if (translate > 0.0f) {
            return 0.0f;
        }
        if (translate < (-imageSize) + viewSize) {
            return (-imageSize) + viewSize;
        }
        return translate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScaleBound() {
        this.mMatrix.getValues(this.mMatrixCoordinate);
        if (this.mImageWidth < getWidth()) {
            this.mMatrixCoordinate[2] = (getWidth() - this.mImageWidth) / 2.0f;
        } else if (this.mMatrixCoordinate[2] >= 0.0f) {
            this.mMatrixCoordinate[2] = 0.0f;
        } else if (this.mMatrixCoordinate[2] < getWidth() - this.mImageWidth) {
            this.mMatrixCoordinate[2] = getWidth() - this.mImageWidth;
        }
        if (this.mImageHeight < getHeight()) {
            this.mMatrixCoordinate[5] = (getHeight() - this.mImageHeight) / 2.0f;
        } else if (this.mMatrixCoordinate[5] >= 0.0f) {
            this.mMatrixCoordinate[5] = 0.0f;
        } else if (this.mMatrixCoordinate[5] < getHeight() - this.mImageHeight) {
            this.mMatrixCoordinate[5] = getHeight() - this.mImageHeight;
        }
        this.mMatrix.setValues(this.mMatrixCoordinate);
    }

    public void setScale(float scale, float touchX, float touchY) {
        float temp = this.mScale * scale;
        if (temp < this.mScaleMin) {
            scale = this.mScaleMin / this.mScale;
            this.mScale = this.mScaleMin;
        } else if (temp > this.mScaleMax) {
            scale = this.mScaleMax / this.mScale;
            this.mScale = this.mScaleMax;
        } else {
            this.mScale = temp;
        }
        if (scale != 1.0f) {
            this.mImageWidth *= scale;
            this.mImageHeight *= scale;
            this.mMatrix.postScale(scale, scale, touchX, touchY);
            checkScaleBound();
        }
        this.mMatrix.getValues(this.mMatrixCoordinate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMatrix(Matrix matrix) {
        this.mMatrix.getValues(this.mMatrixCoordinate);
        this.mCenterRatio.x = ((getWidth() / 2) - this.mMatrixCoordinate[2]) / this.mImageWidth;
        this.mCenterRatio.y = ((getHeight() / 2) - this.mMatrixCoordinate[5]) / this.mImageHeight;
        setImageMatrix(matrix);
    }

    public boolean isScaleMin() {
        return this.mScale == this.mScaleMin;
    }

    public void setScaleMin() {
        setScale(1.0f / this.mScale, getWidth() / 2.0f, getHeight() / 2.0f);
        setMatrix(this.mMatrix);
    }

    public boolean isBoundaryLeft() {
        this.mMatrix.getValues(this.mMatrixCoordinate);
        return ((float) getWidth()) > this.mImageWidth || this.mMatrixCoordinate[2] == 0.0f;
    }

    public boolean isBoundaryRight() {
        this.mMatrix.getValues(this.mMatrixCoordinate);
        return ((float) getWidth()) > this.mImageWidth || (-this.mMatrixCoordinate[2]) + ((float) getWidth()) == this.mImageWidth;
    }
}

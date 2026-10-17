package com.samsungimaging.asphodel.multimedia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: compiled from: FFmpegJNI.java */
/* JADX INFO: loaded from: classes.dex */
class FFmpegScreen {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command = null;
    private static final int SCREEN_TYPE_NUM = 4;
    private static final String TAG = "[FFmpegScreen]";
    private static SurfaceHolder m_surfaceHolder = null;
    private Bitmap[] m_background;
    private Matrix m_matrix;
    private int[][] m_screenPositoin;
    private int[][] m_screenSize;
    private int m_curScreenType = 0;
    private int m_curCameraRotate = 0;
    private Canvas m_canvas = null;
    private int m_canvasClearCount = 0;
    public int m_pictureQueueLen = 0;

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command;
        if (iArr == null) {
            iArr = new int[FFmpegJNI.Command.valuesCustom().length];
            try {
                iArr[FFmpegJNI.Command.FRAME_COUNT_GET.ordinal()] = 9;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[FFmpegJNI.Command.SCREEN_CONFIG_CHANGE.ordinal()] = 8;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[FFmpegJNI.Command.SURFACE_CHANGED.ordinal()] = 6;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[FFmpegJNI.Command.SURFACE_CREATED.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[FFmpegJNI.Command.SURFACE_DESTROYED.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[FFmpegJNI.Command.VIDEO_STREAMING_FLUSH.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[FFmpegJNI.Command.VIDEO_STREAMING_QUIT.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[FFmpegJNI.Command.VIDEO_STREAMING_START.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[FFmpegJNI.Command.VIDEO_STREAMING_TOGGLE_PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command = iArr;
        }
        return iArr;
    }

    public FFmpegScreen(Bitmap portraitBackground, Bitmap landscapeBackground, int[][] screenPositoin, int[][] screenSize, int curScreenType, int curCameraRotate) {
        this.m_background = null;
        this.m_screenPositoin = null;
        this.m_screenSize = null;
        this.m_matrix = null;
        this.m_matrix = new Matrix();
        this.m_background = new Bitmap[2];
        this.m_background[0] = portraitBackground;
        this.m_background[1] = landscapeBackground;
        this.m_screenPositoin = new int[4][];
        this.m_screenSize = new int[4][];
        changeScreenConfig(curScreenType, curCameraRotate, screenPositoin, screenSize);
    }

    public int drawScreen(Bitmap bitmap) {
        Bitmap transformedBitmap;
        int rotatedWidth = 0;
        int rotatedHeight = 0;
        if (!Thread.currentThread().isInterrupted()) {
            switch (this.m_curCameraRotate) {
                case 0:
                case 180:
                    rotatedWidth = bitmap.getWidth();
                    rotatedHeight = bitmap.getHeight();
                    break;
                case 90:
                case 270:
                    rotatedWidth = bitmap.getHeight();
                    rotatedHeight = bitmap.getWidth();
                    break;
            }
            this.m_matrix.reset();
            if (this.m_curCameraRotate != 0) {
                this.m_matrix.postRotate(this.m_curCameraRotate, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            }
            this.m_matrix.postScale(this.m_screenSize[this.m_curScreenType][0] / rotatedWidth, this.m_screenSize[this.m_curScreenType][1] / rotatedHeight);
            if (FFmpegJNI.getResolution() == 0) {
                Bitmap cropBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, cropBitmap.getWidth() * 2, cropBitmap.getHeight() * 2, true);
                transformedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), this.m_matrix, true);
            } else {
                transformedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.m_matrix, true);
            }
            synchronized (m_surfaceHolder) {
                this.m_canvas = m_surfaceHolder.lockCanvas();
                if (this.m_canvas == null) {
                    return -1;
                }
                if (this.m_canvasClearCount < this.m_pictureQueueLen) {
                    this.m_canvasClearCount++;
                    if (this.m_curScreenType == 0 || this.m_curScreenType == 1) {
                        this.m_canvas.drawBitmap(this.m_background[0], 0.0f, 0.0f, (Paint) null);
                    } else {
                        this.m_canvas.drawBitmap(this.m_background[1], 0.0f, 0.0f, (Paint) null);
                    }
                }
                this.m_canvas.drawBitmap(transformedBitmap, this.m_screenPositoin[this.m_curScreenType][0], this.m_screenPositoin[this.m_curScreenType][1], (Paint) null);
                m_surfaceHolder.unlockCanvasAndPost(this.m_canvas);
            }
        }
        return 0;
    }

    private void delay(String str, int tm) {
        if (tm < 0) {
            tm = 2000;
        }
        Trace.d(Trace.Tag.FFMPEG, "-=> delay1 " + str);
        try {
            Thread.sleep(tm);
        } catch (Exception e) {
        }
        Trace.d(Trace.Tag.FFMPEG, "-=> delay2 " + str);
    }

    public void configureSurface(FFmpegJNI.Command cmd, SurfaceHolder holder) {
        switch ($SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command()[cmd.ordinal()]) {
            case 5:
            case 7:
                break;
            case 6:
                m_surfaceHolder = holder;
                break;
            default:
                Trace.e(Trace.Tag.FFMPEG, "Unknown Command!");
                break;
        }
    }

    public int changeScreenConfig(int curScreenType, int curCameraRotate, int[][] screenPositoin, int[][] screenSize) {
        this.m_canvasClearCount = 0;
        this.m_curScreenType = curScreenType;
        this.m_curCameraRotate = curCameraRotate;
        for (int i = 0; i < 4; i++) {
            this.m_screenPositoin[i] = new int[2];
            if (screenPositoin != null) {
                this.m_screenPositoin[i][0] = screenPositoin[i][0];
                this.m_screenPositoin[i][1] = screenPositoin[i][1];
            }
            this.m_screenSize[i] = new int[2];
            if (screenSize != null) {
                this.m_screenSize[i][0] = screenSize[i][0];
                this.m_screenSize[i][1] = screenSize[i][1];
            }
        }
        return 0;
    }
}

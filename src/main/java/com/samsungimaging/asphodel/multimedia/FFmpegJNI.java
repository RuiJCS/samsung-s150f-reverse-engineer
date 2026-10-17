package com.samsungimaging.asphodel.multimedia;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import com.samsungimaging.connectionmanager.util.Trace;

/* JADX INFO: loaded from: classes.dex */
public class FFmpegJNI {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command = null;
    public static final String ARG_VIDEO_STREAMING_FLUSH = "ffplay flush";
    public static final String ARG_VIDEO_STREAMING_QUIT = "ffplay quit";
    public static final String ARG_VIDEO_STREAMING_TOGGLE_PAUSE = "ffplay toggle_pause";
    private static final String DELIMITER = " ";
    private static final int FFMPEG_ARGB = 27;
    private static final int FFMPEG_RGB565LE = 44;
    public static final int RESOLUTION_QVGA = 0;
    public static final int RESOLUTION_VGA = 1;
    public static final int SCREEN_TYPE_PHONE_LAND_CAMERA_LAND = 2;
    public static final int SCREEN_TYPE_PHONE_LAND_CAMERA_PORT = 3;
    public static final int SCREEN_TYPE_PHONE_PORT_CAMERA_LAND = 0;
    public static final int SCREEN_TYPE_PHONE_PORT_CAMERA_PORT = 1;
    private static final String TAG = "[FFmpegJNI]";
    private static boolean m_run = false;
    private static int m_argc = 0;
    private static String[] m_argv = null;
    private static Thread m_ffmpegMainThread = null;
    private static FFmpegScreen m_ffmpegScreen = null;
    private static int m_frameCount = 0;
    private static int m_currentFrameIdx = 0;
    private static int m_pictureQueueLen = 0;
    private static int m_pictureWidth = 0;
    private static int m_pictureHeigtht = 0;
    private static FFmpegPicture[] m_ffmpegPicture = null;
    private static int m_Resolution = 1;
    public static boolean mConnectResponse = false;

    public enum Command {
        VIDEO_STREAMING_START,
        VIDEO_STREAMING_TOGGLE_PAUSE,
        VIDEO_STREAMING_FLUSH,
        VIDEO_STREAMING_QUIT,
        SURFACE_CREATED,
        SURFACE_CHANGED,
        SURFACE_DESTROYED,
        SCREEN_CONFIG_CHANGE,
        FRAME_COUNT_GET;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Command[] valuesCustom() {
            Command[] commandArrValuesCustom = values();
            int length = commandArrValuesCustom.length;
            Command[] commandArr = new Command[length];
            System.arraycopy(commandArrValuesCustom, 0, commandArr, 0, length);
            return commandArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native int ffplayMain(int i, String[] strArr);

    private static native int ffplaySDL(int i, String[] strArr);

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command;
        if (iArr == null) {
            iArr = new int[Command.valuesCustom().length];
            try {
                iArr[Command.FRAME_COUNT_GET.ordinal()] = 9;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Command.SCREEN_CONFIG_CHANGE.ordinal()] = 8;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Command.SURFACE_CHANGED.ordinal()] = 6;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Command.SURFACE_CREATED.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Command.SURFACE_DESTROYED.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Command.VIDEO_STREAMING_FLUSH.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Command.VIDEO_STREAMING_QUIT.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[Command.VIDEO_STREAMING_START.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[Command.VIDEO_STREAMING_TOGGLE_PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            $SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command = iArr;
        }
        return iArr;
    }

    public static int construct(Bitmap portraitBackground, Bitmap landscapeBackground, int[][] screenPositoin, int[][] screenSize, int curScreenType, int curCameraRotate) {
        int IsEnableNEON = CPUFeaturesJNI.construct();
        if (IsEnableNEON == 0) {
            Trace.i(Trace.Tag.FFMPEG, "Load libffplay-jni.so");
            System.loadLibrary("ffplay-jni");
        } else {
            Trace.i(Trace.Tag.FFMPEG, "Load libffplay-neon-jni.so");
            System.loadLibrary("ffplay-neon-jni");
        }
        m_ffmpegScreen = new FFmpegScreen(portraitBackground, landscapeBackground, screenPositoin, screenSize, curScreenType, curCameraRotate);
        m_run = true;
        return IsEnableNEON;
    }

    public static int destruct() {
        m_run = false;
        m_frameCount = 0;
        return 0;
    }

    public static int request(Command cmd, String param) {
        if (!m_run) {
            Trace.e(Trace.Tag.FFMPEG, "You should call construct() function first!");
            return -1;
        }
        m_argv = param.split(" ");
        m_argc = m_argv.length;
        for (int i = 0; i < m_argc; i++) {
            Trace.i(Trace.Tag.FFMPEG, "m_argv[" + i + "] : " + m_argv[i]);
        }
        Trace.i(Trace.Tag.FFMPEG, "m_argc : " + m_argc);
        Trace.i(Trace.Tag.FFMPEG, "Command : " + cmd.toString());
        switch ($SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command()[cmd.ordinal()]) {
            case 1:
                if (m_ffmpegMainThread == null) {
                    m_ffmpegMainThread = new Thread(new Runnable() { // from class: com.samsungimaging.asphodel.multimedia.FFmpegJNI.1
                        @Override // java.lang.Runnable
                        public void run() {
                            int result = FFmpegJNI.ffplayMain(FFmpegJNI.m_argc, FFmpegJNI.m_argv);
                            Trace.d(Trace.Tag.FFMPEG, "ffplayMain result : " + result);
                            FFmpegJNI.m_ffmpegMainThread = null;
                        }
                    });
                    m_ffmpegMainThread.start();
                    return 0;
                }
                Trace.i(Trace.Tag.FFMPEG, "Video Streaming already started!");
                return -1;
            case 2:
            case 3:
            case 4:
                ffplaySDL(m_argc, m_argv);
                if (cmd == Command.VIDEO_STREAMING_QUIT) {
                    int loop_count = 0;
                    while (true) {
                        Trace.i(Trace.Tag.FFMPEG, "wait for termination,...");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (m_run) {
                            int loop_count2 = loop_count + 1;
                            if (loop_count <= 30 && m_ffmpegMainThread != null) {
                                loop_count = loop_count2;
                            }
                        }
                        break;
                    }
                }
                return 0;
            default:
                Trace.e(Trace.Tag.FFMPEG, "Unknown Command!");
                return -1;
        }
    }

    public static int request(Command cmd, SurfaceHolder holder) {
        if (!m_run) {
            Trace.e(Trace.Tag.FFMPEG, "You should call construct() function first!");
            return -1;
        }
        switch ($SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command()[cmd.ordinal()]) {
            case 5:
            case 6:
            case 7:
                Trace.i(Trace.Tag.FFMPEG, "Command : " + cmd.toString());
                m_ffmpegScreen.configureSurface(cmd, holder);
                return 0;
            default:
                Trace.e(Trace.Tag.FFMPEG, "Unknown Command!");
                return -1;
        }
    }

    public static int request(Command cmd, int param1, int param2, int[][] param3, int[][] param4) {
        if (!m_run) {
            Trace.e(Trace.Tag.FFMPEG, "You should call construct() function first!");
            return -1;
        }
        switch ($SWITCH_TABLE$com$samsungimaging$asphodel$multimedia$FFmpegJNI$Command()[cmd.ordinal()]) {
            case 8:
                Trace.i(Trace.Tag.FFMPEG, "Command : " + cmd.toString());
                m_ffmpegScreen.changeScreenConfig(param1, param2, param3, param4);
                return 0;
            case 9:
                return m_frameCount;
            default:
                Trace.e(Trace.Tag.FFMPEG, "Unknown Command!");
                return -1;
        }
    }

    private static int onInitPictureQueue(int len, int width, int height, int rgbType) {
        Bitmap.Config config;
        Trace.i(Trace.Tag.FFMPEG, "picture queue len : " + len);
        Trace.i(Trace.Tag.FFMPEG, "picture width : " + width);
        Trace.i(Trace.Tag.FFMPEG, "picture height : " + height);
        Trace.i(Trace.Tag.FFMPEG, "picture type : " + rgbType);
        m_pictureQueueLen = len;
        m_ffmpegScreen.m_pictureQueueLen = len;
        m_pictureWidth = width;
        m_pictureHeigtht = height;
        if (rgbType == 27) {
            Trace.i(Trace.Tag.FFMPEG, "RGB Type : ARGB8888");
            config = Bitmap.Config.ARGB_8888;
        } else if (rgbType == 44) {
            Trace.i(Trace.Tag.FFMPEG, "RGB Type : RGB565");
            config = Bitmap.Config.RGB_565;
        } else {
            Trace.e(Trace.Tag.FFMPEG, "Unknown RGB Type!");
            return -1;
        }
        m_ffmpegPicture = new FFmpegPicture[m_pictureQueueLen];
        for (int i = 0; i < m_pictureQueueLen; i++) {
            m_ffmpegPicture[i] = new FFmpegPicture(m_pictureWidth, m_pictureHeigtht, config);
        }
        return 0;
    }

    private static int getQueueSize() {
        return 15;
    }

    private static long getFreeHeapSize() {
        Trace.d(Trace.Tag.FFMPEG, "maxMemory : " + Runtime.getRuntime().maxMemory() + " totalMemory : " + Runtime.getRuntime().totalMemory() + " freeMemory : " + Runtime.getRuntime().freeMemory());
        return Runtime.getRuntime().maxMemory() - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    private static synchronized Bitmap onGetPicture(int index) {
        return index < m_pictureQueueLen ? m_ffmpegPicture[index].getPicture() : null;
    }

    private static int onDisplay(int index) {
        m_currentFrameIdx = index;
        m_ffmpegScreen.drawScreen(onGetPicture(index));
        m_frameCount++;
        return 0;
    }

    public static int onHttpConnectRequest(int status, int value) {
        return 0;
    }

    public static int onHttpConnetResponse(int status, int value) {
        mConnectResponse = true;
        return 0;
    }

    public static void onFreezeDisplay() {
        m_ffmpegScreen.drawScreen(onGetPicture(m_currentFrameIdx));
    }

    public static int getResolution() {
        return m_Resolution;
    }

    public static void setResolution(int resolution) {
        m_Resolution = resolution;
    }

    public static Bitmap getCurrentFrame() {
        return onGetPicture(m_currentFrameIdx);
    }
}

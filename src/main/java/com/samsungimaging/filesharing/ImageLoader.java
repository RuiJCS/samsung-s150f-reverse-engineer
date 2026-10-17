package com.samsungimaging.filesharing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.samsungimaging.filesharing.util.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes.dex */
public class ImageLoader {
    private static final String TAG = "INRY";
    private static File mCacheDir = null;
    private Map<ImageView, String> imageViews;
    Context mContext;
    private final int mDefaultDrawable;
    private final int mProgressDrawable;
    MemoryCache memoryCache;
    BitmapFactory.Options opts;
    PhotosLoader photoLoaderThread;
    PhotosQueue photosQueue;

    public ImageLoader(Context context, int defaultDrawable, String sCacheFolderPath) {
        this(context, -1, defaultDrawable, sCacheFolderPath);
    }

    public ImageLoader(Context context, int progressDrawable, int defaultDrawable, String sCacheFolderPath) {
        this.memoryCache = new MemoryCache();
        this.imageViews = Collections.synchronizedMap(new WeakHashMap());
        this.opts = new BitmapFactory.Options();
        this.photosQueue = new PhotosQueue();
        this.photoLoaderThread = new PhotosLoader();
        this.photoLoaderThread.setPriority(4);
        this.opts.inPreferredConfig = Bitmap.Config.RGB_565;
        this.mContext = context;
        this.mProgressDrawable = progressDrawable;
        this.mDefaultDrawable = defaultDrawable;
        if (mCacheDir == null) {
            mCacheDir = new File(sCacheFolderPath);
        }
        if (!mCacheDir.exists()) {
            Logger.d("INRY", "LazyList doesn't exist");
            mCacheDir.mkdirs();
        } else {
            Logger.d("INRY", "LazyList exist");
            mCacheDir.delete();
            mCacheDir.mkdirs();
        }
    }

    public void displayImage(String url, Activity activity, ImageView imageView) {
        this.imageViews.put(imageView, url);
        Bitmap bitmap = this.memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        String filename = String.valueOf(url.hashCode());
        File file = new File(mCacheDir, filename);
        if (file.exists()) {
            bitmap = decodeFile(file);
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, activity, imageView);
            imageView.setImageResource(this.mProgressDrawable != -1 ? this.mProgressDrawable : this.mDefaultDrawable);
        }
    }

    private void queuePhoto(String url, Activity activity, ImageView imageView) {
        this.photosQueue.Clean(imageView);
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        synchronized (this.photosQueue.photosToLoad) {
            this.photosQueue.photosToLoad.push(p);
            this.photosQueue.photosToLoad.notifyAll();
        }
        if (this.photoLoaderThread.getState() == Thread.State.NEW) {
            this.photoLoaderThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmap(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(mCacheDir, filename);
        Bitmap b = decodeFile(f);
        if (b == null) {
            try {
                Logger.d("INRY", "getBitmap open url=" + url);
                InputStream is = new URL(url).openStream();
                Logger.d("INRY", "getBitmap start url=" + url);
                OutputStream os = new FileOutputStream(f);
                copyStream(is, os);
                os.close();
                Logger.d("INRY", "getBitmap end url=" + url);
                Bitmap bitmap = decodeFile(f);
                return bitmap;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmapQF30(String url) {
        Logger.d("INRY", "getBitmap start url=" + url);
        File f = new File(mCacheDir, String.valueOf(url.hashCode()));
        Bitmap b = decodeFile(f);
        if (b == null) {
            long size = -1;
            while (size == -1) {
                try {
                    HumbleHttpClient hhc = new HumbleHttpClient();
                    InputStream is = hhc.request(url);
                    OutputStream os = new FileOutputStream(f);
                    size = copyStreamQF30(is, os);
                    if (size != hhc.contentLength) {
                        size = -1;
                    }
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return b;
                }
            }
            b = decodeFile(f);
            return b;
        }
        return b;
    }

    private Bitmap decodeFile(File f) {
        Bitmap resizedBitmap = null;
        if (f.exists() && f.isFile()) {
            try {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                if (b != null) {
                    int bHeight = b.getHeight();
                    int bWidth = b.getWidth();
                    if (bHeight > bWidth) {
                        resizedBitmap = Bitmap.createScaledBitmap(getNewHeight(bHeight, bWidth, b), 107, 107, true);
                    } else {
                        resizedBitmap = bWidth > bHeight ? Bitmap.createScaledBitmap(getNewWidth(bHeight, bWidth, b), 107, 107, true) : Bitmap.createScaledBitmap(b, 107, 107, true);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Logger.d("INRY", "File Not Found");
        }
        return resizedBitmap;
    }

    private class PhotoToLoad {
        public ImageView imageView;
        public String url;

        public PhotoToLoad(String u, ImageView i) {
            this.url = u;
            this.imageView = i;
        }
    }

    public void stopThread() {
        this.photoLoaderThread.interrupt();
    }

    class PhotosQueue {
        private Stack<PhotoToLoad> photosToLoad = new Stack<>();

        PhotosQueue() {
        }

        public void Clean(ImageView image) {
            int j = 0;
            while (j < this.photosToLoad.size()) {
                if (this.photosToLoad.get(j).imageView == image) {
                    this.photosToLoad.remove(j);
                } else {
                    j++;
                }
                j++;
            }
        }
    }

    class PhotosLoader extends Thread {
        PhotosLoader() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            PhotoToLoad photoToLoad;
            Bitmap bmp;
            do {
                try {
                    if (ImageLoader.this.photosQueue.photosToLoad.size() == 0) {
                        synchronized (ImageLoader.this.photosQueue.photosToLoad) {
                            ImageLoader.this.photosQueue.photosToLoad.wait();
                        }
                    }
                    if (ImageLoader.this.photosQueue.photosToLoad.size() != 0) {
                        synchronized (ImageLoader.this.photosQueue.photosToLoad) {
                            photoToLoad = (PhotoToLoad) ImageLoader.this.photosQueue.photosToLoad.remove(0);
                        }
                        if (FileSharing.mPreconnectedSsid == null || (!FileSharing.mPreconnectedSsid.contains("QF30") && !FileSharing.mPreconnectedSsid.contains("EX2"))) {
                            bmp = ImageLoader.this.getBitmap(photoToLoad.url);
                        } else {
                            bmp = ImageLoader.this.getBitmapQF30(photoToLoad.url);
                        }
                        if (bmp != null) {
                            ImageLoader.this.memoryCache.put(photoToLoad.url, bmp);
                            String tag = (String) ImageLoader.this.imageViews.get(photoToLoad.imageView);
                            if (tag != null && tag.equals(photoToLoad.url)) {
                                BitmapDisplayer bd = ImageLoader.this.new BitmapDisplayer(bmp, photoToLoad.imageView);
                                Activity a = (Activity) photoToLoad.imageView.getContext();
                                a.runOnUiThread(bd);
                                Logger.d("INRY", "Processed Thumbnail : " + photoToLoad.url);
                            } else {
                                Logger.d("INRY", "Processed(wrongr tag) Thumbnail : " + photoToLoad.url);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            } while (!Thread.interrupted());
            Logger.d("GG", "Interrupted : While Retrieving Thumbnails.");
        }
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;

        public BitmapDisplayer(Bitmap b, ImageView i) {
            this.bitmap = b;
            this.imageView = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.bitmap != null) {
                this.imageView.setImageBitmap(this.bitmap);
            } else {
                this.imageView.setImageBitmap(null);
            }
        }
    }

    public void clearCache() {
        this.memoryCache.clear();
        File[] files = mCacheDir.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[8192];
            while (true) {
                int count = is.read(bytes, 0, 8192);
                if (count != -1) {
                    os.write(bytes, 0, count);
                } else {
                    os.flush();
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private long copyStreamQF30(InputStream is, OutputStream os) {
        long size = 0;
        try {
            byte[] bytes = new byte[8192];
            while (true) {
                int count = is.read(bytes, 0, 8192);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
                size += (long) count;
            }
            os.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return size;
    }

    private Bitmap getNewHeight(int height, int width, Bitmap old) {
        int h = height - width;
        int newStart = h / 2;
        Bitmap newbitmap = Bitmap.createBitmap(old, 0, newStart, width, width);
        return newbitmap;
    }

    private Bitmap getNewWidth(int height, int width, Bitmap old) {
        int w = width - height;
        int newStart = w / 2;
        Bitmap newbitmap = Bitmap.createBitmap(old, newStart, 0, height, height);
        return newbitmap;
    }
}

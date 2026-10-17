package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
public class ImageLoader {
    private static File mCacheDir = null;
    private Trace.Tag TAG;
    ImageDownloadQueue imageDownloadQueue;
    ImageDownloader imageDownloader;
    private Map<ImageView, String> imageViews;
    Activity mActivity;
    Context mContext;
    private final int mDefaultDrawable;
    private final int mProgressDrawable;
    BitmapFactory.Options opts;

    public ImageLoader(Context context, Activity activity, int defaultDrawable, String sCacheFolderPath) {
        this(context, activity, -1, defaultDrawable, sCacheFolderPath);
    }

    public ImageLoader(Context context, Activity activity, int progressDrawable, int defaultDrawable, String sCacheFolderPath) {
        this.TAG = Trace.Tag.ML;
        this.imageViews = Collections.synchronizedMap(new WeakHashMap());
        this.opts = new BitmapFactory.Options();
        this.imageDownloadQueue = new ImageDownloadQueue();
        this.imageDownloader = new ImageDownloader();
        this.imageDownloader.setPriority(4);
        this.opts.inPreferredConfig = Bitmap.Config.RGB_565;
        this.mContext = context;
        this.mActivity = activity;
        this.mProgressDrawable = progressDrawable;
        this.mDefaultDrawable = defaultDrawable;
        if (mCacheDir == null) {
            mCacheDir = new File(sCacheFolderPath);
        }
        if (!mCacheDir.exists()) {
            Trace.d(this.TAG, "LazyList doesn't exist");
            mCacheDir.mkdirs();
        } else {
            Trace.d(this.TAG, "LazyList exist");
            mCacheDir.delete();
            mCacheDir.mkdirs();
        }
    }

    public void setImage(String url, Activity activity, ImageView imageView, Item item) {
        Trace.d(this.TAG, "start displayImage() url=" + url);
        if (!url.equals(imageView.getTag())) {
            BitmapDisplayer bd = new BitmapDisplayer(BitmapFactory.decodeResource(this.mActivity.getResources(), this.mProgressDrawable != -1 ? this.mProgressDrawable : this.mDefaultDrawable), imageView);
            this.mActivity.runOnUiThread(bd);
            imageView.setTag(null);
            this.imageViews.put(imageView, url);
            addQueueImage(url, activity, imageView, item);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmapQF30(String url) {
        Trace.d(this.TAG, "getBitmap start url=" + url);
        File f = new File(mCacheDir, String.valueOf(url.hashCode()));
        Bitmap image = decodeFile(f);
        if (image == null) {
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
                    return image;
                }
            }
            image = decodeFile(f);
            return image;
        }
        return image;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getImage(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(mCacheDir, filename);
        Bitmap b = decodeFile(f);
        if (b == null) {
            try {
                Trace.d(this.TAG, "Performance Check Point : start getBitmap url=" + url);
                URL urls = new URL(url);
                URLConnection con = urls.openConnection();
                if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                    con.setRequestProperty(HTTP.CONNECTION, HTTP.CLOSE);
                }
                InputStream is = new URL(url).openStream();
                Trace.d(this.TAG, "getBitmap start url=" + url);
                OutputStream os = new FileOutputStream(f);
                copyStream(is, os);
                os.close();
                Bitmap image = decodeFile(f);
                Trace.d(this.TAG, "Performance Check Point : end getBitmap url=" + url);
                return image;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap decodeFile(File f) {
        Bitmap b = null;
        if (f.exists() && f.isFile()) {
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Trace.d(this.TAG, "File Not Found");
        }
        Trace.d(this.TAG, "bWidth : 0 bHeight : 0");
        return b;
    }

    private void saveBitmap(Bitmap srcBitmap, String filePath, Bitmap.CompressFormat format) throws Throwable {
        if (format != Bitmap.CompressFormat.PNG) {
            Bitmap backupBitmap = Bitmap.createBitmap(srcBitmap);
            Canvas srcCanvas = new Canvas(srcBitmap);
            srcBitmap.eraseColor(-1);
            srcCanvas.drawBitmap(backupBitmap, 0.0f, 0.0f, (Paint) null);
            backupBitmap.recycle();
        }
        File fileCacheItem = new File(filePath);
        OutputStream out = null;
        try {
            fileCacheItem.createNewFile();
            OutputStream out2 = new FileOutputStream(fileCacheItem);
            try {
                srcBitmap.compress(format, 100, out2);
                try {
                    out2.flush();
                    out2.close();
                    out = out2;
                } catch (IOException e) {
                    e.printStackTrace();
                    out = out2;
                }
            } catch (Exception e2) {
                e = e2;
                out = out2;
                e.printStackTrace();
                try {
                    out.flush();
                    out.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (Throwable th) {
                th = th;
                out = out2;
                try {
                    out.flush();
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
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

    public void stopThread() {
        this.imageDownloader.interrupt();
    }

    private class ImageItem {
        public ImageView imageView;
        public Item item;
        public String url;

        public ImageItem(String url, ImageView imageView, Item item) {
            this.url = url;
            this.imageView = imageView;
            this.item = item;
        }
    }

    class ImageDownloadQueue {
        private Stack<ImageItem> imageItems = new Stack<>();

        ImageDownloadQueue() {
        }

        public void Clean(ImageView image) {
            int j = 0;
            while (j < this.imageItems.size()) {
                if (this.imageItems.get(j).imageView == image) {
                    this.imageItems.remove(j);
                } else {
                    j++;
                }
                j++;
            }
        }
    }

    private void addQueueImage(String url, Activity activity, ImageView imageView, Item item) {
        this.imageDownloadQueue.Clean(imageView);
        ImageItem p = new ImageItem(url, imageView, item);
        synchronized (this.imageDownloadQueue.imageItems) {
            this.imageDownloadQueue.imageItems.push(p);
            this.imageDownloadQueue.imageItems.notifyAll();
        }
        if (this.imageDownloader.getState() == Thread.State.NEW) {
            this.imageDownloader.start();
        }
    }

    class ImageDownloader extends Thread {
        ImageDownloader() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ImageItem imageItem;
            do {
                try {
                    if (ImageLoader.this.imageDownloadQueue.imageItems.size() == 0) {
                        synchronized (ImageLoader.this.imageDownloadQueue.imageItems) {
                            ImageLoader.this.imageDownloadQueue.imageItems.wait();
                        }
                    }
                    if (ImageLoader.this.imageDownloadQueue.imageItems.size() != 0) {
                        synchronized (ImageLoader.this.imageDownloadQueue.imageItems) {
                            imageItem = (ImageItem) ImageLoader.this.imageDownloadQueue.imageItems.remove(0);
                        }
                        Bitmap bmp = null;
                        int errCheck = 0;
                        String filename = String.valueOf(imageItem.url.hashCode());
                        File file = new File(ImageLoader.mCacheDir, filename);
                        Trace.d(ImageLoader.this.TAG, "file.exists() : " + file.exists());
                        if (file.exists()) {
                            bmp = ImageLoader.this.decodeFile(file);
                        }
                        Trace.d(ImageLoader.this.TAG, "bmp is null : " + (bmp == null));
                        if (bmp == null) {
                            if (CMInfo.getInstance().getConnectedSSID() != null && (CMInfo.getInstance().getConnectedSSID().contains("QF30") || CMInfo.getInstance().getConnectedSSID().contains("EX2"))) {
                                bmp = ImageLoader.this.getBitmapQF30(imageItem.url);
                            } else {
                                do {
                                    bmp = ImageLoader.this.getImage(imageItem.url);
                                    errCheck++;
                                    if (bmp != null) {
                                        break;
                                    }
                                } while (errCheck < 3);
                            }
                        }
                        Trace.d(ImageLoader.this.TAG, "bmp is null : " + (bmp == null));
                        if (bmp != null) {
                            String tag = (String) ImageLoader.this.imageViews.get(imageItem.imageView);
                            if (tag != null && tag.equals(imageItem.url)) {
                                BitmapDisplayer bd = ImageLoader.this.new BitmapDisplayer(bmp, imageItem.imageView);
                                ImageLoader.this.mActivity.runOnUiThread(bd);
                                imageItem.imageView.setTag(imageItem.url);
                            }
                        } else {
                            imageItem.item.setSupported(false);
                            BitmapDisplayer bd2 = ImageLoader.this.new BitmapDisplayer(BitmapFactory.decodeResource(ImageLoader.this.mContext.getResources(), R.drawable.sca_popup_img_none), imageItem.imageView);
                            ImageLoader.this.mActivity.runOnUiThread(bd2);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            } while (!Thread.interrupted());
            Trace.d(ImageLoader.this.TAG, "Interrupted : While Retrieving Thumbnails.");
        }
    }
}

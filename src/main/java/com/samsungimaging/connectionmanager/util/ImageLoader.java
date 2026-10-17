package com.samsungimaging.connectionmanager.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.webkit.URLUtil;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.view.RecycleImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class ImageLoader {
    private static final int IO_BUFFER_SIZE = 8192;
    private static final int MESSAGE_CLEAR_CACHE = 1;
    private static final int MESSAGE_CLOSE_CACHE = 3;
    private static final int MESSAGE_FLUSH_CACHE = 2;
    private static final int MESSAGE_INIT_DISK_CACHE = 0;
    private static final int MESSAGE_REMOVE_CACHE = 4;
    private static final int SMALL_SIZE = 100;
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    private Context mContext;
    ImageCache mImageCache;
    ImageSize mImageSize;
    private Bitmap mLoadingBitmap = null;
    private boolean mPauseLoad = false;
    private final Object mPauseLoadLock = new Object();

    public enum ImageSize {
        SMALL,
        LARGE;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static ImageSize[] valuesCustom() {
            ImageSize[] imageSizeArrValuesCustom = values();
            int length = imageSizeArrValuesCustom.length;
            ImageSize[] imageSizeArr = new ImageSize[length];
            System.arraycopy(imageSizeArrValuesCustom, 0, imageSizeArr, 0, length);
            return imageSizeArr;
        }
    }

    private static class AsyncBitmapDrawable extends BitmapDrawable {
        private final WeakReference<BitmapLoaderTask> mBitmapLoaderTask;

        public AsyncBitmapDrawable(Bitmap bitmap, BitmapLoaderTask bitmapLoaderTask) {
            super(bitmap);
            this.mBitmapLoaderTask = new WeakReference<>(bitmapLoaderTask);
        }

        public BitmapLoaderTask getBitmapLoaderTask() {
            return this.mBitmapLoaderTask.get();
        }
    }

    private class BitmapLoaderTask extends AsyncTask<DatabaseMedia, Void, RecycleBitmapDrawable> {
        private final WeakReference<RecycleImageView> mImageView;
        private DatabaseMedia mMedia;

        public BitmapLoaderTask(RecycleImageView imageView) {
            this.mImageView = new WeakReference<>(imageView);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public RecycleBitmapDrawable doInBackground(DatabaseMedia... params) throws Throwable {
            String filePath;
            synchronized (ImageLoader.this.mPauseLoadLock) {
                while (ImageLoader.this.mPauseLoad && !isCancelled()) {
                    try {
                        ImageLoader.this.mPauseLoadLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            this.mMedia = params[0];
            Bitmap bitmap = null;
            RecycleBitmapDrawable drawable = null;
            boolean isNoneImage = false;
            if (ImageLoader.this.mImageSize == ImageSize.SMALL) {
                filePath = this.mMedia.getThumbnailPath();
            } else {
                filePath = this.mMedia.getViewerPath();
            }
            if (ImageLoader.this.mImageCache != null && !isCancelled() && getAttachedImageView() != null) {
                bitmap = ImageLoader.this.mImageCache.getBitmapFromDiskCache(filePath);
            }
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null) {
                if (!URLUtil.isHttpUrl(filePath)) {
                    if (this.mMedia.getMediaType() != 1) {
                        int kind = ImageLoader.this.mImageSize != ImageSize.SMALL ? 2 : 1;
                        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
                    } else {
                        bitmap = ImageLoader.this.getBitmap(this.mMedia.getID(), filePath, this.mMedia.getOrientation());
                    }
                } else {
                    bitmap = ImageLoader.this.getBitmapFromStream(filePath);
                }
            }
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null) {
                bitmap = BitmapFactory.decodeResource(ImageLoader.this.mContext.getResources(), R.drawable.sca_popup_img_none);
                isNoneImage = true;
            }
            if (bitmap != null) {
                bitmap.setDensity(160);
                drawable = new RecycleBitmapDrawable(bitmap);
                drawable.setNoneImage(isNoneImage);
                if (ImageLoader.this.mImageCache != null) {
                    if (isNoneImage) {
                        ImageLoader.this.mImageCache.addBitmapToMemoryCache(filePath, drawable);
                    } else {
                        ImageLoader.this.mImageCache.addBitmapToCache(filePath, drawable);
                    }
                }
            }
            return drawable;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public void onPostExecute(RecycleBitmapDrawable result) {
            if (isCancelled()) {
                result = null;
            }
            RecycleImageView imageView = getAttachedImageView();
            if (result != null && imageView != null) {
                imageView.setImageDrawable(result);
            }
        }

        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        protected void onCancelled() {
            super.onCancelled();
            synchronized (ImageLoader.this.mPauseLoadLock) {
                ImageLoader.this.mPauseLoadLock.notifyAll();
            }
        }

        private RecycleImageView getAttachedImageView() {
            RecycleImageView imageView = this.mImageView.get();
            BitmapLoaderTask bitmapWorkerTask = ImageLoader.getBitmapLoaderTask(imageView);
            if (this == bitmapWorkerTask) {
                return imageView;
            }
            return null;
        }
    }

    private class CacheAsyncTask extends AsyncTask<Object, Void, Void> {
        private CacheAsyncTask() {
        }

        /* synthetic */ CacheAsyncTask(ImageLoader imageLoader, CacheAsyncTask cacheAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public Void doInBackground(Object... params) {
            switch (((Integer) params[0]).intValue()) {
                case 0:
                    if (ImageLoader.this.mImageCache != null) {
                        ImageLoader.this.mImageCache.initDiskCache();
                    }
                    return null;
                case 1:
                    if (ImageLoader.this.mImageCache != null) {
                        ImageLoader.this.mImageCache.clearCache();
                    }
                    return null;
                case 2:
                    if (ImageLoader.this.mImageCache != null) {
                        ImageLoader.this.mImageCache.flushCache();
                    }
                    return null;
                case 3:
                    if (ImageLoader.this.mImageCache != null) {
                        ImageLoader.this.mImageCache.closeCache();
                        ImageLoader.this.mImageCache = null;
                    }
                    return null;
                case 4:
                    if (ImageLoader.this.mImageCache != null) {
                        ImageLoader.this.mImageCache.remove((String) params[1]);
                    }
                    return null;
                default:
                    return null;
            }
        }
    }

    public ImageLoader(Context context, ImageSize imageSize, String diskCacheDir) {
        this.mImageSize = ImageSize.SMALL;
        this.mContext = null;
        this.mImageCache = null;
        this.mContext = context;
        this.mImageSize = imageSize;
        this.mImageCache = new ImageCache(context, diskCacheDir);
        initDiskCache();
    }

    public void loadImage(DatabaseMedia media, RecycleImageView imageView) {
        String filePath;
        if (media != null) {
            if (this.mImageSize == ImageSize.SMALL) {
                filePath = media.getThumbnailPath();
            } else {
                filePath = media.getViewerPath();
            }
            RecycleBitmapDrawable drawable = getBitmapDrawableFromMemoryCache(filePath);
            if (drawable != null) {
                if (drawable != imageView.getDrawable()) {
                    imageView.setImageDrawable(drawable);
                    return;
                }
                return;
            }
            BitmapLoaderTask bitmapLoaderTask = getBitmapLoaderTask(imageView);
            if (bitmapLoaderTask != null) {
                String previousFilePath = null;
                if (bitmapLoaderTask.mMedia != null) {
                    previousFilePath = this.mImageSize == ImageSize.SMALL ? bitmapLoaderTask.mMedia.getThumbnailPath() : bitmapLoaderTask.mMedia.getViewerPath();
                }
                if (previousFilePath == null || !previousFilePath.equals(filePath)) {
                    bitmapLoaderTask.cancel(true);
                } else {
                    return;
                }
            }
            BitmapLoaderTask bitmapLoaderTask2 = new BitmapLoaderTask(imageView);
            AsyncBitmapDrawable asyncBitmapDrawable = new AsyncBitmapDrawable(this.mLoadingBitmap, bitmapLoaderTask2);
            imageView.setImageDrawable(asyncBitmapDrawable);
            bitmapLoaderTask2.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, media);
        }
    }

    public RecycleBitmapDrawable loadImageSync(DatabaseMedia media) {
        String filePath;
        Bitmap bitmap = null;
        RecycleBitmapDrawable drawable = null;
        boolean isNoneImage = false;
        if (this.mImageSize == ImageSize.SMALL) {
            filePath = media.getThumbnailPath();
        } else {
            filePath = media.getViewerPath();
        }
        if (this.mImageCache != null) {
            bitmap = this.mImageCache.getBitmapFromDiskCache(filePath);
        }
        if (bitmap == null) {
            if (URLUtil.isHttpUrl(filePath)) {
                bitmap = getBitmapFromStream(filePath);
            } else if (media.getMediaType() == 1) {
                bitmap = getBitmap(media.getID(), filePath, media.getOrientation());
            } else {
                int kind = this.mImageSize != ImageSize.SMALL ? 2 : 1;
                bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
            }
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.sca_popup_img_none);
            isNoneImage = true;
        }
        if (bitmap != null) {
            bitmap.setDensity(160);
            drawable = new RecycleBitmapDrawable(bitmap);
            drawable.setNoneImage(isNoneImage);
            if (this.mImageCache != null) {
                if (isNoneImage) {
                    this.mImageCache.addBitmapToMemoryCache(filePath, drawable);
                } else {
                    this.mImageCache.addBitmapToCache(filePath, drawable);
                }
            }
        }
        return drawable;
    }

    public void cancelLoader(RecycleImageView imageView) {
        BitmapLoaderTask bitmapLoaderTask = getBitmapLoaderTask(imageView);
        if (bitmapLoaderTask != null) {
            bitmapLoaderTask.cancel(true);
        }
    }

    public void setPauseLoad(boolean pauseLoad) {
        synchronized (this.mPauseLoadLock) {
            this.mPauseLoad = pauseLoad;
            if (!this.mPauseLoad) {
                this.mPauseLoadLock.notifyAll();
            }
        }
    }

    public RecycleBitmapDrawable getBitmapDrawableFromMemoryCache(String filePath) {
        if (this.mImageCache != null) {
            return this.mImageCache.getBitmapDrawableFromMemoryCache(filePath);
        }
        return null;
    }

    public boolean hasSnapshotInDiskCache(String filePath) {
        if (this.mImageCache != null) {
            return this.mImageCache.hasSnapshotInDiskCache(filePath);
        }
        return false;
    }

    public void setLoadingImage(int resId) {
        this.mLoadingBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), resId);
    }

    public void setLoadingImage(Bitmap bitmap) {
        this.mLoadingBitmap = bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BitmapLoaderTask getBitmapLoaderTask(RecycleImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncBitmapDrawable) {
                AsyncBitmapDrawable asyncDrawable = (AsyncBitmapDrawable) drawable;
                return asyncDrawable.getBitmapLoaderTask();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmapFromStream(String filePath) throws Throwable {
        HttpURLConnection urlConnection = null;
        BufferedInputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            try {
                URL url = new URL(filePath);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream inputStream2 = new BufferedInputStream(urlConnection.getInputStream(), 8192);
                try {
                    bitmap = BitmapFactory.decodeStream(inputStream2);
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                            inputStream = inputStream2;
                        } catch (IOException e) {
                            inputStream = inputStream2;
                        }
                    } else {
                        inputStream = inputStream2;
                    }
                } catch (IOException e2) {
                    e = e2;
                    inputStream = inputStream2;
                    Trace.e(TAG, "Error in getBitmapFromStream() - " + e.getMessage());
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e3) {
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    inputStream = inputStream2;
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e = e5;
            }
            return bitmap;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBitmap(long id, String filePath, int orientation) {
        int widthRatio;
        int heightRatio;
        Bitmap bitmap;
        Bitmap bitmap2 = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (this.mImageSize == ImageSize.SMALL) {
                int width = 0;
                int height = 0;
                int sampleSize = 2;
                Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(this.mContext.getContentResolver(), id, 1, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    width = cursor.getInt(cursor.getColumnIndex("width"));
                    height = cursor.getInt(cursor.getColumnIndex("height"));
                    cursor.close();
                }
                if (width > 100 || height > 100) {
                    int widthRatio2 = Math.round(width / 100.0f);
                    int heightRatio2 = Math.round(height / 100.0f);
                    sampleSize = Math.min(widthRatio2, heightRatio2);
                }
                options.inSampleSize = sampleSize;
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.mContext.getContentResolver(), id, 1, options);
            } else {
                int sampleSize2 = 1;
                DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, options);
                if (options.outWidth > displayMetrics.widthPixels || options.outHeight > displayMetrics.heightPixels) {
                    if (options.outWidth > options.outHeight) {
                        widthRatio = Math.round(options.outWidth / Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels));
                        heightRatio = Math.round(options.outHeight / Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));
                    } else {
                        widthRatio = Math.round(options.outWidth / Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));
                        heightRatio = Math.round(options.outHeight / Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels));
                    }
                    sampleSize2 = Math.min(widthRatio, heightRatio);
                }
                options.inSampleSize = sampleSize2;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(filePath, options);
            }
            if (orientation != 0) {
                bitmap2 = rotateBitmap(bitmap, orientation);
                return bitmap2;
            }
            return bitmap;
        } catch (Exception e) {
            Trace.e(TAG, e.getMessage());
            return bitmap2;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.preRotate(orientation, 0.5f, 0.5f);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bitmap.recycle();
        return rotateBitmap;
    }

    public void clearMemoryCache() {
        if (this.mImageCache != null) {
            this.mImageCache.clearMemoryCache();
        }
    }

    public void initDiskCache() {
        new CacheAsyncTask(this, null).execute(0);
    }

    public void clearCache() {
        new CacheAsyncTask(this, null).execute(1);
    }

    public void flushCache() {
        new CacheAsyncTask(this, null).execute(2);
    }

    public void closeCache() {
        new CacheAsyncTask(this, null).execute(3);
    }

    public void removeCache(String filePath) {
        new CacheAsyncTask(this, null).execute(4, filePath);
    }
}

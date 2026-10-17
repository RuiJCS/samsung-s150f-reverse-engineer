package com.samsungimaging.connectionmanager.widget;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;
import com.samsungimaging.connectionmanager.util.ImageLoader;
import com.samsungimaging.connectionmanager.util.RecycleBitmapDrawable;
import com.samsungimaging.connectionmanager.view.PinchZoomView;
import com.samsungimaging.connectionmanager.view.RecycleImageView;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class ImageViewer extends FrameLayout implements View.OnClickListener {
    private ImageView mIconPlay;
    private ImageLoader mImageLoader;
    private RecycleImageView mLoadingView;
    private DatabaseMedia mMedia;
    private PinchZoomView mPinchZoom;

    public ImageViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mImageLoader = null;
        this.mMedia = null;
        this.mLoadingView = null;
        this.mPinchZoom = null;
        this.mIconPlay = null;
        init();
    }

    public ImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mImageLoader = null;
        this.mMedia = null;
        this.mLoadingView = null;
        this.mPinchZoom = null;
        this.mIconPlay = null;
        init();
    }

    public ImageViewer(Context context) {
        super(context);
        this.mImageLoader = null;
        this.mMedia = null;
        this.mLoadingView = null;
        this.mPinchZoom = null;
        this.mIconPlay = null;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        View view = inflater.inflate(R.layout.image_viewer, (ViewGroup) null);
        this.mLoadingView = (RecycleImageView) view.findViewById(R.id.loading_view);
        this.mPinchZoom = (PinchZoomView) view.findViewById(R.id.pinch_zoom);
        this.mIconPlay = (ImageView) view.findViewById(R.id.icon_play);
        this.mIconPlay.setOnClickListener(this);
        addView(view);
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
    }

    public void loadImage(DatabaseMedia media, RecycleBitmapDrawable loadingImage) {
        this.mMedia = media;
        if (this.mMedia.getMediaType() == 1) {
            this.mIconPlay.setVisibility(4);
            if (loadingImage.isNoneImage()) {
                this.mPinchZoom.setPinchZoomable(false);
            } else {
                this.mPinchZoom.setPinchZoomable(true);
            }
        } else {
            this.mIconPlay.setVisibility(0);
            this.mPinchZoom.setPinchZoomable(false);
        }
        if (URLUtil.isHttpUrl(this.mMedia.getViewerPath())) {
            this.mLoadingView.setVisibility(0);
            this.mLoadingView.setImageBitmap(loadingImage.getBitmap());
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(this.mMedia.getViewerPath(), options);
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            if (options.outWidth * 6.0f < displayMetrics.widthPixels && options.outHeight * 6.0f < displayMetrics.heightPixels && this.mMedia.getMediaType() == 1) {
                this.mLoadingView.setVisibility(4);
            } else {
                this.mLoadingView.setVisibility(0);
                this.mLoadingView.setImageBitmap(loadingImage.getBitmap());
            }
        }
        if (this.mImageLoader != null) {
            this.mImageLoader.loadImage(this.mMedia, this.mPinchZoom);
        }
    }

    public void clearImage() {
        if (this.mImageLoader != null) {
            this.mImageLoader.cancelLoader(this.mPinchZoom);
        }
        this.mPinchZoom.setImageDrawable(null);
        this.mLoadingView.setImageDrawable(null);
    }

    public static Uri fetchUriUsingContentProvider(Context mContext, String filePath) {
        File tempFile = new File(filePath);
        if (filePath.endsWith(".mp4") || filePath.endsWith(".MP4")) {
            Cursor mCursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{GalleryColumns.KEY_ID}, "_data=? ", new String[]{filePath}, null);
            if (mCursor == null) {
                return null;
            }
            if (mCursor.moveToFirst()) {
                int id = mCursor.getInt(mCursor.getColumnIndex(GalleryColumns.KEY_ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                mCursor.close();
                return Uri.withAppendedPath(baseUri, new StringBuilder().append(id).toString());
            }
            if (tempFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(GalleryColumns.KEY_ORIGINAL_PATH, filePath);
                mCursor.close();
                return mContext.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            }
            mCursor.close();
            return null;
        }
        Cursor mCursor2 = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{GalleryColumns.KEY_ID}, "_data=? ", new String[]{filePath}, null);
        if (mCursor2 == null) {
            return null;
        }
        if (mCursor2.moveToFirst()) {
            int id2 = mCursor2.getInt(mCursor2.getColumnIndex(GalleryColumns.KEY_ID));
            Uri baseUri2 = Uri.parse("content://media/external/images/media");
            mCursor2.close();
            return Uri.withAppendedPath(baseUri2, new StringBuilder().append(id2).toString());
        }
        if (tempFile.exists()) {
            ContentValues values2 = new ContentValues();
            values2.put(GalleryColumns.KEY_ORIGINAL_PATH, filePath);
            mCursor2.close();
            return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);
        }
        mCursor2.close();
        return null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        try {
            Uri mUri = fetchUriUsingContentProvider(getContext(), this.mMedia.getOriginalPath());
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(mUri, "video/*");
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseMedia getMedia() {
        return this.mMedia;
    }

    public boolean hasValidBitmap() {
        if (this.mPinchZoom.getDrawable() instanceof RecycleBitmapDrawable) {
            return ((RecycleBitmapDrawable) this.mPinchZoom.getDrawable()).hasValidBitmap();
        }
        return false;
    }

    public boolean isScaleMin() {
        return this.mPinchZoom.isScaleMin();
    }

    public void setScaleMin() {
        this.mPinchZoom.setScaleMin();
    }

    public boolean isBoundaryLeft() {
        return this.mPinchZoom.isBoundaryLeft();
    }

    public boolean isBoundaryRight() {
        return this.mPinchZoom.isBoundaryRight();
    }
}

package com.samsungimaging.connectionmanager.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.util.RecycleBitmapDrawable;

/* JADX INFO: loaded from: classes.dex */
public class RecycleImageView extends ImageView {
    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        setImageDrawable(null);
        super.onDetachedFromWindow();
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        super.setImageDrawable(drawable);
        notifyDrawable(drawable, true);
        notifyDrawable(previousDrawable, false);
    }

    private static void notifyDrawable(Drawable drawable, boolean isDisplayed) {
        if (drawable instanceof RecycleBitmapDrawable) {
            ((RecycleBitmapDrawable) drawable).setDisplayed(isDisplayed);
        }
    }
}

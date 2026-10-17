package com.samsungimaging.connectionmanager.gallery;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.samsungimaging.connectionmanager.widget.ImageViewer;

/* JADX INFO: loaded from: classes.dex */
public class GalleryPagerAdapter extends PagerAdapter {
    private GalleryFragment mGalleryFragment;
    private SparseArray<ImageViewer> mViewList = new SparseArray<>();
    private OnInstantiateItemListener mOnInstantiateItemListener = null;

    public interface OnInstantiateItemListener {
        ImageViewer onInstantiateItem(ViewGroup viewGroup, int i);
    }

    public GalleryPagerAdapter(GalleryFragment galleryFragment) {
        this.mGalleryFragment = null;
        this.mGalleryFragment = galleryFragment;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.mGalleryFragment.getChildTotalCount();
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getItemPosition(Object object) {
        return -2;
    }

    @Override // android.support.v4.view.PagerAdapter
    public ImageViewer instantiateItem(ViewGroup container, int position) {
        if (this.mOnInstantiateItemListener == null) {
            return null;
        }
        ImageViewer viewer = this.mOnInstantiateItemListener.onInstantiateItem(container, position);
        this.mViewList.put(position, viewer);
        return viewer;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageViewer viewer = (ImageViewer) object;
        viewer.clearImage();
        ((ViewPager) container).removeView(viewer);
        this.mViewList.remove(position);
    }

    public ImageViewer getItem(int position) {
        return this.mViewList.get(position);
    }

    public void setOnInstantiateItemListener(OnInstantiateItemListener l) {
        this.mOnInstantiateItemListener = l;
    }

    public void clear() {
        for (int i = 0; i < this.mViewList.size(); i++) {
            int key = this.mViewList.keyAt(i);
            this.mViewList.get(key).clearImage();
        }
        this.mViewList.clear();
        this.mViewList = null;
    }
}

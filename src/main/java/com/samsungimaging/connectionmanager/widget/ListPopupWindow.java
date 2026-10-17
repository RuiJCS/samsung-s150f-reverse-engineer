package com.samsungimaging.connectionmanager.widget;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.util.Utils;

/* JADX INFO: loaded from: classes.dex */
public class ListPopupWindow {
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private PopupWindow mPopupWindow;

    public ListPopupWindow(Activity activity) {
        this.mPopupWindow = null;
        this.mAdapter = null;
        this.mListView = null;
        this.mAdapter = new ArrayAdapter<>(activity, R.layout.simple_list_item);
        this.mListView = (ListView) activity.getLayoutInflater().inflate(R.layout.gallery_popup_window, (ViewGroup) null);
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        this.mPopupWindow = new PopupWindow(activity);
        this.mPopupWindow.setContentView(this.mListView);
        this.mPopupWindow.setTouchable(true);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.setOutsideTouchable(true);
    }

    public void addItem(String[] items) {
        if (this.mAdapter != null) {
            for (String item : items) {
                this.mAdapter.add(item);
            }
        }
    }

    public void clearItem() {
        if (this.mAdapter != null) {
            this.mAdapter.clear();
        }
    }

    public void setSize(int width, int height) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.setWidth(width);
            this.mPopupWindow.setHeight(height);
        }
    }

    public void showAsDropDown(View anchor) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.showAsDropDown(anchor);
        }
    }

    public boolean isShowing() {
        if (this.mPopupWindow != null) {
            return this.mPopupWindow.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.dismiss();
        }
    }

    public void destroy() {
        if (this.mAdapter != null) {
            this.mAdapter.clear();
            this.mAdapter = null;
        }
        Utils.unbindView(this.mListView);
        if (this.mPopupWindow != null && this.mPopupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
            this.mPopupWindow = null;
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        if (this.mListView != null) {
            this.mListView.setOnItemClickListener(l);
        }
    }

    public ListView getListView() {
        return this.mListView;
    }
}

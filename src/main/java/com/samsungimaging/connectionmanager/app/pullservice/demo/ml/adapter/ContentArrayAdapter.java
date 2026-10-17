package com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.ImageLoader;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Item;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Utils;
import com.samsungimaging.connectionmanager.util.ExToast;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ContentArrayAdapter extends ArrayAdapter<Item> {
    private ExToast mExToast;
    private Handler mHandler;
    public ImageLoader mImageLoader;

    public ContentArrayAdapter(Context context, Activity activity, int textViewResourceId, List<Item> objects, boolean enableImageLoader, Handler handler, ExToast exToast) {
        super(context, textViewResourceId, objects);
        this.mImageLoader = null;
        if (enableImageLoader) {
            this.mImageLoader = new ImageLoader(context, activity, R.drawable.ml_loading, Utils.getThumbStorage());
        }
        this.mHandler = handler;
        this.mExToast = exToast;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Item content = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            convertView = inflater.inflate(R.layout.ml_gridframe_sorting_none, parent, false);
            holder = new ViewHolder();
            holder.thumbImg = (ImageView) convertView.findViewById(R.id.ml_thumbnail);
            holder.movieImage = (ImageView) convertView.findViewById(R.id.ml_custom_movie);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox);
            holder.notSupportMask = (ImageView) convertView.findViewById(R.id.ml_not_support_mask);
            holder.checkBox.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ContentArrayAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Item item = (Item) v.getTag();
                    if (item.getItemState() != 3) {
                        if (((CheckBox) v).isChecked()) {
                            if (ContentArrayAdapter.this.getCheckedItemCount() >= 1000) {
                                ((CheckBox) v).setChecked(false);
                                item.setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(4);
                                return;
                            } else if (Utils.getAvailableExternalMemorySize() < ContentArrayAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize())) {
                                ((CheckBox) v).setChecked(false);
                                item.setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(6);
                                return;
                            } else if (CMInfo.getInstance().getConnectedSSID() != null && CMInfo.getInstance().getConnectedSSID().contains("QF30") && ContentArrayAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize()) > 2147483648L) {
                                ((CheckBox) v).setChecked(false);
                                item.setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(5);
                                return;
                            } else {
                                item.setItemState(2);
                                ContentArrayAdapter.this.mHandler.sendEmptyMessage(37);
                                return;
                            }
                        }
                        item.setItemState(1);
                        ContentArrayAdapter.this.mHandler.sendEmptyMessage(37);
                    }
                }
            });
            holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ContentArrayAdapter.2
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View v) {
                    if (((Item) v.getTag()).isSupported()) {
                        Message msg = new Message();
                        msg.what = 42;
                        msg.obj = v.getTag();
                        ContentArrayAdapter.this.mHandler.sendMessage(msg);
                        return true;
                    }
                    return true;
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setTag(content);
        this.mImageLoader.setImage(content.getThumbRes(), (Activity) getContext(), holder.thumbImg, content);
        if (Utils.getExtention(content.getRes()).equalsIgnoreCase("MP4")) {
            holder.movieImage.setVisibility(0);
            if (CMInfo.getInstance().getConnectedSSID() != null && CMInfo.getInstance().getConnectedSSID().contains("QF30") && content.getResolution().equals("1920x1080")) {
                content.setItemState(4);
            }
        } else {
            holder.movieImage.setVisibility(4);
        }
        switch (content.getItemState()) {
            case 1:
                holder.checkBox.setButtonDrawable(R.drawable.selector_checkbox);
                holder.checkBox.setChecked(false);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                break;
            case 2:
                holder.checkBox.setButtonDrawable(R.drawable.selector_checkbox);
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                break;
            case 3:
                holder.checkBox.setButtonDrawable(R.drawable.ml_downloaded);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                break;
            case 4:
                holder.checkBox.setVisibility(4);
                holder.notSupportMask.setVisibility(0);
                break;
        }
        if (!content.isSupported()) {
            holder.thumbImg.setBackgroundResource(R.drawable.sca_popup_img_none);
        }
        return convertView;
    }

    public int getCheckedItemCount() {
        int returnValue = 0;
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getItemState() == 2) {
                returnValue++;
            }
        }
        return returnValue;
    }

    public long getCheckedItemSize() {
        long returnValue = 0;
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getItemState() == 2) {
                returnValue += Long.parseLong(getItem(i).getSize());
            }
        }
        return returnValue;
    }

    public class ViewHolder {
        public CheckBox checkBox;
        public ImageView movieImage;
        public ImageView notSupportMask;
        public ImageView thumbImg;

        public ViewHolder() {
        }
    }
}

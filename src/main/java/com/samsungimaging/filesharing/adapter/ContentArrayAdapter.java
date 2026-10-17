package com.samsungimaging.filesharing.adapter;

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
import com.samsungimaging.filesharing.FileSharing;
import com.samsungimaging.filesharing.ImageLoader;
import com.samsungimaging.filesharing.Item;
import com.samsungimaging.filesharing.R;
import com.samsungimaging.filesharing.Utils;
import com.samsungimaging.filesharing.util.ExToast;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ContentArrayAdapter extends ArrayAdapter<Item> {
    private ExToast mExToast;
    private Handler mHandler;
    public ImageLoader mImageLoader;

    public ContentArrayAdapter(Context context, int textViewResourceId, List<Item> objects, boolean enableImageLoader, Handler handler, ExToast exToast) {
        super(context, textViewResourceId, objects);
        this.mImageLoader = null;
        if (enableImageLoader) {
            this.mImageLoader = new ImageLoader(context, R.drawable.ml_loading, Utils.getThumbStorage());
        }
        this.mHandler = handler;
        this.mExToast = exToast;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Item content = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            convertView = inflater.inflate(R.layout.ml_gridframe, parent, false);
            holder = new ViewHolder();
            holder.thumbImg = (ImageView) convertView.findViewById(R.id.ml_thumbnail);
            holder.movieImage = (ImageView) convertView.findViewById(R.id.ml_custom_movie);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox);
            holder.notSupportMask = (ImageView) convertView.findViewById(R.id.ml_not_support_mask);
            holder.checkBox.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.filesharing.adapter.ContentArrayAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).getItemState() != 3) {
                        if (((CheckBox) v).isChecked()) {
                            if (ContentArrayAdapter.this.getCheckedItemCount() >= 100) {
                                ((CheckBox) v).setChecked(false);
                                ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(4);
                                return;
                            } else if (Utils.getAvailableExternalMemorySize() < ContentArrayAdapter.this.getCheckedItemSize() + Long.parseLong(ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).getSize())) {
                                ((CheckBox) v).setChecked(false);
                                ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(6);
                                return;
                            } else if (FileSharing.mPreconnectedSsid != null && FileSharing.mPreconnectedSsid.contains("QF30") && ContentArrayAdapter.this.getCheckedItemSize() + Long.parseLong(ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).getSize()) > 2147483648L) {
                                ((CheckBox) v).setChecked(false);
                                ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).setItemState(1);
                                ContentArrayAdapter.this.mExToast.show(5);
                                return;
                            } else {
                                ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).setItemState(2);
                                ContentArrayAdapter.this.mHandler.sendEmptyMessage(37);
                                return;
                            }
                        }
                        ContentArrayAdapter.this.getItem(((Integer) v.getTag()).intValue()).setItemState(1);
                        ContentArrayAdapter.this.mHandler.sendEmptyMessage(37);
                    }
                }
            });
            holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.samsungimaging.filesharing.adapter.ContentArrayAdapter.2
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View v) {
                    Message msg = new Message();
                    msg.what = 42;
                    msg.obj = v.getTag();
                    ContentArrayAdapter.this.mHandler.sendMessage(msg);
                    return true;
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setTag(Integer.valueOf(position));
        this.mImageLoader.displayImage(content.getThumbRes(), (Activity) getContext(), holder.thumbImg);
        if (Utils.getExtention(content.getRes()).equalsIgnoreCase("MP4")) {
            holder.movieImage.setVisibility(0);
            if (FileSharing.mPreconnectedSsid != null && FileSharing.mPreconnectedSsid.contains("QF30") && content.getResolution().equals("1920x1080")) {
                content.setItemState(4);
            }
        } else {
            holder.movieImage.setVisibility(4);
        }
        switch (content.getItemState()) {
            case 1:
                holder.checkBox.setButtonDrawable(R.drawable.ml_custom_checkbox);
                holder.checkBox.setChecked(false);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                return convertView;
            case 2:
                holder.checkBox.setButtonDrawable(R.drawable.ml_custom_checkbox);
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                return convertView;
            case 3:
                holder.checkBox.setButtonDrawable(R.drawable.ml_downloaded);
                holder.checkBox.setVisibility(0);
                holder.notSupportMask.setVisibility(4);
                return convertView;
            case 4:
                holder.checkBox.setVisibility(4);
                holder.notSupportMask.setVisibility(0);
                return convertView;
            default:
                return convertView;
        }
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

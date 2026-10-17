package com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMInfo;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.ImageLoader;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Item;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Utils;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.entity.GroupEntity;
import com.samsungimaging.connectionmanager.util.ExToast;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Dialog EnlargeImagedialog;
    public Activity activity;
    ImageView img;
    public HashMap<Item, CheckBox> mChildMap;
    private int mColumnNum;
    private Context mContext;
    private ExToast mExToast;
    private ExpandableListView mExpandableListView;
    private List<GroupEntity> mGroupCollection;
    public HashMap<Integer, GroupHolder> mGroupMap;
    private Handler mHandler;
    public ImageLoader mImageLoader;
    private Trace.Tag TAG = Trace.Tag.ML;
    private String item_sreenRes = null;

    public ExpandableListAdapter(Context pContext, Activity activity, ExpandableListView pExpandableListView, List<GroupEntity> pGroupCollection, int columnNum, boolean enableImageLoader, Handler handler, ExToast exToast) {
        this.mGroupMap = new HashMap<>();
        this.mChildMap = new HashMap<>();
        Trace.d(this.TAG, "start ExpandableListAdapter() columnNum : " + columnNum);
        this.activity = activity;
        this.mHandler = handler;
        this.mExToast = exToast;
        this.mGroupMap = new HashMap<>();
        this.mChildMap = new HashMap<>();
        if (enableImageLoader) {
            this.mImageLoader = new ImageLoader(pContext, activity, R.drawable.ml_loading, Utils.getThumbStorage());
        }
        this.mContext = pContext;
        this.mGroupCollection = pGroupCollection;
        this.mColumnNum = columnNum;
        this.mExpandableListView = pExpandableListView;
        setListEvent();
    }

    private void setListEvent() {
        this.mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.1
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int arg0) {
                Trace.d(ExpandableListAdapter.this.TAG, "start onGroupExpand()");
                ((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(arg0)).groupExpandedStatus = 1;
            }
        });
        this.mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.2
            @Override // android.widget.ExpandableListView.OnGroupCollapseListener
            public void onGroupCollapse(int arg0) {
                Trace.d(ExpandableListAdapter.this.TAG, "start onGroupCollapse()");
                ((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(arg0)).groupExpandedStatus = 0;
            }
        });
    }

    @Override // android.widget.ExpandableListAdapter
    public ArrayList<Item> getChild(int arg0, int arg1) {
        return this.mGroupCollection.get(arg0).GroupItemCollection.get(arg1).ItemList;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int arg0, int arg1) {
        return 0L;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int groupPosition, int childPosition, boolean bLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        Trace.d(this.TAG, "start getChildView() groupPosition : " + groupPosition + " childPosition : " + childPosition);
        if (convertView == null) {
            Trace.d(this.TAG, "convertView is null");
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.ml_gridframe, (ViewGroup) null);
            childHolder = new ChildHolder();
            for (int i = 0; i < this.mColumnNum; i++) {
                switch (i) {
                    case 0:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame1);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail1);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie1);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox1);
                        View v = convertView.findViewById(R.id.ml_custom_checkbox1);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v));
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask1);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v));
                        break;
                    case 1:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame2);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail2);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie2);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox2);
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        View v2 = convertView.findViewById(R.id.ml_custom_checkbox2);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v2));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask2);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v2));
                        break;
                    case 2:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame3);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail3);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie3);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox3);
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        View v3 = convertView.findViewById(R.id.ml_custom_checkbox3);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v3));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask3);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v3));
                        break;
                    case 3:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame4);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail4);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie4);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox4);
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        View v4 = convertView.findViewById(R.id.ml_custom_checkbox4);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v4));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask4);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v4));
                        break;
                    case 4:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame5);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail5);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie5);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox5);
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        View v5 = convertView.findViewById(R.id.ml_custom_checkbox5);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v5));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask5);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v5));
                        break;
                    case 5:
                        childHolder.frame[i] = (FrameLayout) convertView.findViewById(R.id.ml_frame6);
                        childHolder.thumbImg[i] = (ImageView) convertView.findViewById(R.id.ml_thumbnail6);
                        childHolder.movieImage[i] = (ImageView) convertView.findViewById(R.id.ml_custom_movie6);
                        childHolder.checkBox[i] = (CheckBox) convertView.findViewById(R.id.ml_custom_checkbox6);
                        View v6 = convertView.findViewById(R.id.ml_custom_checkbox6);
                        childHolder.thumbImg[i].setOnClickListener(new thumbClickListener(v6));
                        childHolder.checkBox[i].setOnClickListener(new CheckBoxClickListener(this, null));
                        childHolder.checkBox[i].setOnLongClickListener(new CheckBoxLongClickListener(this, null));
                        childHolder.notSupportMask[i] = (ImageView) convertView.findViewById(R.id.ml_not_support_mask6);
                        childHolder.thumbImg[i].setOnLongClickListener(new imageLongclicklistener(v6));
                        break;
                }
            }
            convertView.setTag(childHolder);
        } else {
            Trace.d(this.TAG, "convertView is not null");
            childHolder = (ChildHolder) convertView.getTag();
        }
        ArrayList<Item> itemList = this.mGroupCollection.get(groupPosition).GroupItemCollection.get(childPosition).ItemList;
        for (int i2 = 0; i2 < this.mColumnNum; i2++) {
            if (i2 >= itemList.size()) {
                childHolder.frame[i2].setVisibility(4);
            } else {
                childHolder.frame[i2].setVisibility(0);
                Item item = itemList.get(i2);
                this.mImageLoader.setImage(item.getThumbRes(), (Activity) parent.getContext(), childHolder.thumbImg[i2], item);
                if (Utils.getExtention(item.getRes()).equalsIgnoreCase("MP4")) {
                    childHolder.movieImage[i2].setVisibility(0);
                    if (CMInfo.getInstance().getConnectedSSID() != null && CMInfo.getInstance().getConnectedSSID().contains("QF30") && item.getResolution().equals("1920x1080")) {
                        item.setItemState(4);
                    }
                } else {
                    childHolder.movieImage[i2].setVisibility(4);
                }
                Trace.d(this.TAG, "item state : " + item.getItemState());
                switch (item.getItemState()) {
                    case 1:
                    case 3:
                        childHolder.checkBox[i2].setButtonDrawable(R.drawable.selector_checkbox);
                        childHolder.checkBox[i2].setChecked(false);
                        childHolder.checkBox[i2].setVisibility(0);
                        childHolder.notSupportMask[i2].setVisibility(4);
                        break;
                    case 2:
                        childHolder.checkBox[i2].setButtonDrawable(R.drawable.selector_checkbox);
                        childHolder.checkBox[i2].setChecked(true);
                        childHolder.checkBox[i2].setVisibility(0);
                        childHolder.notSupportMask[i2].setVisibility(4);
                        break;
                    case 4:
                        childHolder.checkBox[i2].setVisibility(4);
                        childHolder.notSupportMask[i2].setVisibility(0);
                        break;
                }
                if (!item.isSupported()) {
                    childHolder.thumbImg[i2].setImageResource(R.drawable.sca_popup_img_none);
                }
                childHolder.checkBox[i2].setTag(item);
                this.mChildMap.put(item, childHolder.checkBox[i2]);
            }
        }
        Trace.d(this.TAG, "return convertView");
        return convertView;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int arg0) {
        return this.mGroupCollection.get(arg0).GroupItemCollection.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int arg0) {
        return this.mGroupCollection.get(arg0);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.mGroupCollection.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int arg0) {
        return arg0;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean arg1, View arg2, ViewGroup arg3) {
        GroupHolder groupHolder;
        if (arg2 == null) {
            arg2 = LayoutInflater.from(this.mContext).inflate(R.layout.ml_list_group, (ViewGroup) null);
            groupHolder = new GroupHolder();
            groupHolder.img = (ImageView) arg2.findViewById(R.id.tag_img);
            groupHolder.img.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    int groupPos = ((Integer) v.getTag()).intValue();
                    if (((GroupEntity) ExpandableListAdapter.this.getGroup(groupPos)).groupExpandedStatus == 0) {
                        ExpandableListAdapter.this.mExpandableListView.expandGroup(groupPos);
                    } else {
                        ExpandableListAdapter.this.mExpandableListView.collapseGroup(groupPos);
                    }
                }
            });
            groupHolder.title = (CheckBox) arg2.findViewById(R.id.group_title);
            groupHolder.title.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Trace.d(ExpandableListAdapter.this.TAG, "start onClick() title");
                    int groupPos = ((Integer) v.getTag()).intValue();
                    GroupEntity ge = (GroupEntity) ExpandableListAdapter.this.getGroup(groupPos);
                    if (ge.groupCheckedAllStatus) {
                        for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                            GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                            for (int k = 0; k < gi.ItemList.size(); k++) {
                                Item item = gi.ItemList.get(k);
                                if (item.getItemState() == 2) {
                                    item.setItemState(1);
                                    if (ExpandableListAdapter.this.mChildMap.get(item) != null && item.equals(ExpandableListAdapter.this.mChildMap.get(item).getTag())) {
                                        ExpandableListAdapter.this.mChildMap.get(item).setChecked(false);
                                    }
                                }
                            }
                        }
                        ge.groupCheckedAllStatus = false;
                        ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
                        return;
                    }
                    int checkableItemCount = 1000 - ExpandableListAdapter.this.getCheckedItemCount();
                    long checkedItemSize = ExpandableListAdapter.this.getCheckedItemSize();
                    if (checkableItemCount > 0 && ExpandableListAdapter.this.getGroupUncheckedItemCount(groupPos) <= checkableItemCount) {
                        ge.groupCheckedAllStatus = true;
                        for (int j2 = 0; j2 < ge.GroupItemCollection.size(); j2++) {
                            Trace.d(ExpandableListAdapter.this.TAG, "j : " + j2);
                            if (checkableItemCount <= 0) {
                                break;
                            }
                            GroupEntity.GroupItemEntity gi2 = ge.GroupItemCollection.get(j2);
                            for (int k2 = 0; k2 < gi2.ItemList.size(); k2++) {
                                Trace.d(ExpandableListAdapter.this.TAG, "k : " + k2);
                                Item item2 = gi2.ItemList.get(k2);
                                if (item2.getItemState() == 1) {
                                    Trace.d(ExpandableListAdapter.this.TAG, "selectAllItem step 1");
                                    if (Utils.getAvailableExternalMemorySize() > Long.parseLong(item2.getSize()) + checkedItemSize) {
                                        Trace.d(ExpandableListAdapter.this.TAG, "selectAllItem step 2");
                                        if (checkableItemCount <= 0) {
                                            break;
                                        }
                                        Trace.d(ExpandableListAdapter.this.TAG, "selectAllItem step 3");
                                        checkableItemCount--;
                                        checkedItemSize += Long.parseLong(item2.getSize());
                                        item2.setItemState(2);
                                        if (ExpandableListAdapter.this.mChildMap.get(item2) != null && item2.equals(ExpandableListAdapter.this.mChildMap.get(item2).getTag())) {
                                            ExpandableListAdapter.this.mChildMap.get(item2).setChecked(true);
                                        }
                                    } else {
                                        ge.groupCheckedAllStatus = false;
                                        ExpandableListAdapter.this.mExToast.show(6);
                                        break;
                                    }
                                }
                            }
                        }
                        ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
                        ((CheckBox) v).setChecked(ge.groupCheckedAllStatus);
                        return;
                    }
                    ((CheckBox) v).setChecked(false);
                }
            });
            arg2.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) arg2.getTag();
        }
        if (this.mGroupCollection.get(groupPosition).groupExpandedStatus == 0) {
            groupHolder.img.setImageResource(R.drawable.tw_expander_open_default_holo_dark);
        } else {
            groupHolder.img.setImageResource(R.drawable.tw_expander_close_default_holo_dark);
        }
        groupHolder.img.setTag(Integer.valueOf(groupPosition));
        groupHolder.title.setChecked(this.mGroupCollection.get(groupPosition).groupCheckedAllStatus);
        groupHolder.title.setTag(Integer.valueOf(groupPosition));
        int groupItemCount = 0;
        GroupEntity ge = this.mGroupCollection.get(groupPosition);
        for (int i = 0; i < ge.GroupItemCollection.size(); i++) {
            GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(i);
            groupItemCount += gi.ItemList.size();
        }
        groupHolder.title.setText(String.valueOf(this.mGroupCollection.get(groupPosition).Name) + "(" + groupItemCount + ")");
        this.mGroupMap.put(Integer.valueOf(groupPosition), groupHolder);
        return arg2;
    }

    public class GroupHolder {
        public ImageView img;
        public CheckBox title;

        public GroupHolder() {
        }
    }

    class ChildHolder {
        public FrameLayout[] frame = new FrameLayout[8];
        public ImageView[] thumbImg = new ImageView[8];
        public ImageView[] movieImage = new ImageView[8];
        public CheckBox[] checkBox = new CheckBox[8];
        public ImageView[] notSupportMask = new ImageView[8];

        ChildHolder() {
        }
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public int getCheckedItemCount() {
        int checkedItemCount = 0;
        for (int i = 0; i < this.mGroupCollection.size(); i++) {
            GroupEntity ge = this.mGroupCollection.get(i);
            for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                for (int k = 0; k < gi.ItemList.size(); k++) {
                    Item item = gi.ItemList.get(k);
                    if (item.getItemState() == 2) {
                        checkedItemCount++;
                    }
                }
            }
        }
        Trace.d(this.TAG, "end getCheckedItemCount() checkedItemCount : " + checkedItemCount);
        return checkedItemCount;
    }

    public long getCheckedItemSize() {
        long checkedItemSize = 0;
        for (int i = 0; i < this.mGroupCollection.size(); i++) {
            GroupEntity ge = this.mGroupCollection.get(i);
            for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(j);
                for (int k = 0; k < gi.ItemList.size(); k++) {
                    Item item = gi.ItemList.get(k);
                    if (item.getItemState() == 2) {
                        checkedItemSize += Long.parseLong(item.getSize());
                    }
                }
            }
        }
        Trace.d(this.TAG, "end getCheckedItemSize() checkedItemSize : " + checkedItemSize);
        return checkedItemSize;
    }

    public int getItemCount() {
        int itemCount = 0;
        for (int i = 0; i < this.mGroupCollection.size(); i++) {
            GroupEntity ge = this.mGroupCollection.get(i);
            for (int j = 0; j < ge.GroupItemCollection.size(); j++) {
                itemCount += ge.GroupItemCollection.get(j).ItemList.size();
            }
        }
        Trace.d(this.TAG, "end getItemCount() itemCount : " + itemCount);
        return itemCount;
    }

    public int getGroupItemCount(int groupPosition) {
        int itemCount = 0;
        GroupEntity ge = this.mGroupCollection.get(groupPosition);
        for (int i = 0; i < ge.GroupItemCollection.size(); i++) {
            itemCount += ge.GroupItemCollection.get(i).ItemList.size();
        }
        return itemCount;
    }

    public int getGroupUncheckedItemCount(int groupPosition) {
        int itemCount = 0;
        GroupEntity ge = this.mGroupCollection.get(groupPosition);
        for (int i = 0; i < ge.GroupItemCollection.size(); i++) {
            GroupEntity.GroupItemEntity gi = ge.GroupItemCollection.get(i);
            for (int j = 0; j < gi.ItemList.size(); j++) {
                if (gi.ItemList.get(j).getItemState() == 1) {
                    itemCount++;
                }
            }
        }
        return itemCount;
    }

    private class thumbClickListener implements View.OnClickListener {
        View v;

        public thumbClickListener(View v) {
            this.v = v;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View vv) {
            if (((CheckBox) this.v).isChecked()) {
                ((CheckBox) this.v).setChecked(false);
            } else {
                ((CheckBox) this.v).setChecked(true);
            }
            Trace.d(Trace.Tag.COMMON, "click thumbnail sending " + ((CheckBox) this.v).isChecked());
            Item item = (Item) this.v.getTag();
            if (item.getItemState() != 3) {
                if (((CheckBox) this.v).isChecked()) {
                    if (ExpandableListAdapter.this.getCheckedItemCount() >= 1000) {
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(4);
                    } else if (Utils.getAvailableExternalMemorySize() < ExpandableListAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize())) {
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(6);
                    } else if (CMInfo.getInstance().getConnectedSSID() != null && CMInfo.getInstance().getConnectedSSID().contains("QF30") && ExpandableListAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize()) > 2147483648L) {
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(5);
                    } else {
                        item.setItemState(2);
                        ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
                    }
                    for (int i = 0; i < ExpandableListAdapter.this.mGroupCollection.size(); i++) {
                        GroupEntity ge = (GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i);
                        if (ge.Name.equals(item.getDate())) {
                            if (ExpandableListAdapter.this.getGroupUncheckedItemCount(i) == 0) {
                                ge.groupCheckedAllStatus = true;
                                ExpandableListAdapter.this.mGroupMap.get(Integer.valueOf(i)).title.setChecked(true);
                                return;
                            }
                            return;
                        }
                    }
                    return;
                }
                for (int i2 = 0; i2 < ExpandableListAdapter.this.mGroupCollection.size(); i2++) {
                    if (((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i2)).Name.equals(item.getDate())) {
                        ((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i2)).groupCheckedAllStatus = false;
                        ExpandableListAdapter.this.mGroupMap.get(Integer.valueOf(i2)).title.setChecked(false);
                        break;
                    }
                }
                item.setItemState(1);
                ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
            }
        }
    }

    private class imageLongclicklistener implements View.OnLongClickListener {
        private View view;

        public imageLongclicklistener(View checkBox) {
            this.view = checkBox;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            Item item = (Item) this.view.getTag();
            if (item != null) {
                ExpandableListAdapter.this.EnlargeImagedialog = new Dialog(ExpandableListAdapter.this.activity);
                ExpandableListAdapter.this.EnlargeImagedialog.requestWindowFeature(1);
                ExpandableListAdapter.this.EnlargeImagedialog.setContentView(R.layout.thumb_zoom);
                ImageView mImgView = (ImageView) ExpandableListAdapter.this.EnlargeImagedialog.findViewById(R.id.zoom_image);
                ExpandableListAdapter.this.item_sreenRes = item.getScreenRes();
                if (ExpandableListAdapter.this.item_sreenRes != null) {
                    ExpandableListAdapter.this.mImageLoader.setImage(ExpandableListAdapter.this.item_sreenRes, ExpandableListAdapter.this.activity, mImgView, item);
                } else {
                    ExpandableListAdapter.this.mImageLoader.setImage(item.getThumbRes(), ExpandableListAdapter.this.activity, mImgView, item);
                }
                mImgView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.imageLongclicklistener.1
                    @Override // android.view.View.OnLongClickListener
                    public boolean onLongClick(View v2) {
                        ExpandableListAdapter.this.EnlargeImagedialog.dismiss();
                        return false;
                    }
                });
                ExpandableListAdapter.this.EnlargeImagedialog.show();
                mImgView.setOnTouchListener(new View.OnTouchListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.demo.ml.adapter.ExpandableListAdapter.imageLongclicklistener.2
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View v2, MotionEvent event) {
                        ExpandableListAdapter.this.EnlargeImagedialog.dismiss();
                        return false;
                    }
                });
                return false;
            }
            return false;
        }
    }

    private class CheckBoxClickListener implements View.OnClickListener {
        private CheckBoxClickListener() {
        }

        /* synthetic */ CheckBoxClickListener(ExpandableListAdapter expandableListAdapter, CheckBoxClickListener checkBoxClickListener) {
            this();
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Item item = (Item) v.getTag();
            if (item.getItemState() != 3) {
                if (((CheckBox) v).isChecked()) {
                    if (ExpandableListAdapter.this.getCheckedItemCount() >= 1000) {
                        ((CheckBox) v).setChecked(false);
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(4);
                    } else if (Utils.getAvailableExternalMemorySize() < ExpandableListAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize())) {
                        ((CheckBox) v).setChecked(false);
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(6);
                    } else if (CMInfo.getInstance().getConnectedSSID() != null && CMInfo.getInstance().getConnectedSSID().contains("QF30") && ExpandableListAdapter.this.getCheckedItemSize() + Long.parseLong(item.getSize()) > 2147483648L) {
                        ((CheckBox) v).setChecked(false);
                        item.setItemState(1);
                        ExpandableListAdapter.this.mExToast.show(5);
                    } else {
                        item.setItemState(2);
                        ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
                    }
                    for (int i = 0; i < ExpandableListAdapter.this.mGroupCollection.size(); i++) {
                        GroupEntity ge = (GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i);
                        if (ge.Name.equals(item.getDate())) {
                            if (ExpandableListAdapter.this.getGroupUncheckedItemCount(i) == 0) {
                                ge.groupCheckedAllStatus = true;
                                ExpandableListAdapter.this.mGroupMap.get(Integer.valueOf(i)).title.setChecked(true);
                                return;
                            }
                            return;
                        }
                    }
                    return;
                }
                for (int i2 = 0; i2 < ExpandableListAdapter.this.mGroupCollection.size(); i2++) {
                    if (((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i2)).Name.equals(item.getDate())) {
                        ((GroupEntity) ExpandableListAdapter.this.mGroupCollection.get(i2)).groupCheckedAllStatus = false;
                        ExpandableListAdapter.this.mGroupMap.get(Integer.valueOf(i2)).title.setChecked(false);
                        break;
                    }
                }
                item.setItemState(1);
                ExpandableListAdapter.this.mHandler.sendEmptyMessage(37);
            }
        }
    }

    private class CheckBoxLongClickListener implements View.OnLongClickListener {
        private CheckBoxLongClickListener() {
        }

        /* synthetic */ CheckBoxLongClickListener(ExpandableListAdapter expandableListAdapter, CheckBoxLongClickListener checkBoxLongClickListener) {
            this();
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (((Item) v.getTag()).isSupported()) {
                Message msg = new Message();
                msg.what = 42;
                msg.obj = v.getTag();
                ExpandableListAdapter.this.mHandler.sendMessage(msg);
                return true;
            }
            return true;
        }
    }
}

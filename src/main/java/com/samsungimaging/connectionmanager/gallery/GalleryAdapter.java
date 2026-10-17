package com.samsungimaging.connectionmanager.gallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;
import com.samsungimaging.connectionmanager.util.ImageLoader;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;
import com.samsungimaging.connectionmanager.view.RecycleImageView;

/* JADX INFO: loaded from: classes.dex */
public class GalleryAdapter extends CursorTreeAdapter implements View.OnClickListener, View.OnLongClickListener {
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    private int mCheckedCount;
    private SparseArray<CheckedInfo> mCheckedList;
    private int mChildTotalCount;
    private int mColumnHeight;
    private int mColumnNum;
    private int mColumnWidth;
    private GalleryFragment mGalleryFragment;
    private ImageLoader mImageLoader;
    private boolean mIsCheckAll;
    private boolean mIsCheckable;

    protected class CheckedInfo {
        private boolean mIsChecked = false;
        private SparseBooleanArray mChildCheckedArray = new SparseBooleanArray();

        protected CheckedInfo() {
        }

        public boolean isChecked() {
            return this.mIsChecked;
        }

        public SparseBooleanArray getChildCheckedArray() {
            return this.mChildCheckedArray;
        }
    }

    public GalleryAdapter(GalleryFragment galleryFragment, Cursor cursor, ImageLoader imageLoader) {
        super(cursor, galleryFragment.getActivity(), false);
        this.mGalleryFragment = null;
        this.mImageLoader = null;
        this.mColumnWidth = 0;
        this.mColumnHeight = 0;
        this.mColumnNum = 0;
        this.mIsCheckable = false;
        this.mIsCheckAll = false;
        this.mCheckedCount = 0;
        this.mChildTotalCount = 0;
        this.mCheckedList = new SparseArray<>();
        this.mGalleryFragment = galleryFragment;
        this.mImageLoader = imageLoader;
        this.mChildTotalCount = getChildrenCountFromCursor();
        initCoordinate();
    }

    private void initCoordinate() {
        int screenWidth = this.mGalleryFragment.getActivity().getWindowManager().getDefaultDisplay().getWidth();
        this.mColumnNum = this.mGalleryFragment.getActivity().getResources().getInteger(R.integer.gallery_thumbnail_column_num);
        this.mColumnWidth = screenWidth / this.mColumnNum;
        this.mColumnHeight = (int) (this.mColumnWidth * 0.75f);
    }

    private int getChildrenCountFromCursor() {
        int count = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            count += getChildrenCountFromCursor(i);
        }
        return count;
    }

    public int getChildrenCountFromCursor(int groupPosition) {
        Cursor cursor = getGroup(groupPosition);
        return cursor.getInt(cursor.getColumnIndex(GalleryColumns.KEY_COUNT));
    }

    @Override // android.widget.CursorTreeAdapter, android.widget.ExpandableListAdapter
    public int getChildrenCount(int groupPosition) {
        int count = getChildrenCountFromCursor(groupPosition);
        int addRowNum = count % this.mColumnNum > 0 ? 1 : 0;
        return (count / this.mColumnNum) + addRowNum;
    }

    @Override // android.widget.CursorTreeAdapter
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        GalleryFragment.QueryInfo queryInfo = this.mGalleryFragment.getQuery().getChildQueryInfo(groupCursor);
        return this.mGalleryFragment.getActivity().getContentResolver().query(queryInfo.mUri, queryInfo.mProjection, queryInfo.mSelection, queryInfo.mSelectionArgs, queryInfo.mOrderBy);
    }

    @Override // android.widget.CursorTreeAdapter, android.widget.ExpandableListAdapter
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newChildView(this.mGalleryFragment.getActivity(), getChild(groupPosition, childPosition), isLastChild, parent);
        } else {
            LinearLayout view = (LinearLayout) convertView;
            if (view.getChildCount() != this.mColumnNum) {
                Utils.unbindView(convertView);
                convertView = newChildView(this.mGalleryFragment.getActivity(), getChild(groupPosition, childPosition), isLastChild, parent);
            }
        }
        convertView.setTag(Integer.valueOf(groupPosition));
        return super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
    }

    @Override // android.widget.CursorTreeAdapter
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        int groupPosition = ((Integer) view.getTag()).intValue();
        int rowFirstPosition = cursor.getPosition() * this.mColumnNum;
        LinearLayout mainView = (LinearLayout) view;
        boolean isGroupChecked = isGroupChecked(groupPosition);
        for (int i = 0; i < this.mColumnNum; i++) {
            int childPosition = rowFirstPosition + i;
            View item = mainView.getChildAt(i);
            RecycleImageView imageView = (RecycleImageView) item.findViewById(R.id.image);
            CheckBox checkBox = (CheckBox) item.findViewById(R.id.check_box);
            ImageView playIcon = (ImageView) item.findViewById(R.id.icon_play);
            if (childPosition < cursor.getCount()) {
                cursor.moveToPosition(childPosition);
                DatabaseMedia media = DatabaseMedia.builder(cursor);
                if (this.mImageLoader != null) {
                    this.mImageLoader.loadImage(media, imageView);
                }
                if (this.mIsCheckable) {
                    if (this.mIsCheckAll || isGroupChecked) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(isChildChecked(groupPosition, childPosition));
                    }
                    checkBox.setVisibility(0);
                } else {
                    checkBox.setVisibility(4);
                }
                if (media.getMediaType() == 1) {
                    playIcon.setVisibility(4);
                } else {
                    playIcon.setVisibility(0);
                }
                int[] positions = {groupPosition, childPosition};
                item.setTag(positions);
                item.setVisibility(0);
            } else {
                item.setVisibility(4);
            }
        }
    }

    @Override // android.widget.CursorTreeAdapter
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        int position = cursor.getPosition();
        String dateTakenString = cursor.getString(cursor.getColumnIndex(GalleryColumns.KEY_DATE_TAKEN_STRING));
        String count = cursor.getString(cursor.getColumnIndex(GalleryColumns.KEY_COUNT));
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.group_list_check_box);
        TextView title = (TextView) view.findViewById(R.id.group_list_title);
        RecycleImageView indicator = (RecycleImageView) view.findViewById(R.id.group_list_indicator);
        if (this.mIsCheckable) {
            checkBox.setTag(Integer.valueOf(position));
            if (this.mIsCheckAll) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(isGroupChecked(position));
            }
            checkBox.setVisibility(0);
        } else {
            checkBox.setVisibility(8);
        }
        title.setText(String.valueOf(dateTakenString) + " (" + count + ")");
        indicator.setTag(Integer.valueOf(position));
        indicator.setSelected(isExpanded);
    }

    @Override // android.widget.CursorTreeAdapter
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        Trace.d(TAG, "newChildView()");
        int padding = this.mGalleryFragment.getActivity().getResources().getDimensionPixelSize(R.dimen.gallery_child_list_item_padding);
        LinearLayout view = new LinearLayout(this.mGalleryFragment.getActivity());
        view.setPadding(padding, 0, padding, 0);
        view.setBackgroundColor(Color.rgb(28, 36, 43));
        view.setOrientation(0);
        view.setGravity(1);
        for (int i = 0; i < this.mColumnNum; i++) {
            View item = this.mGalleryFragment.getActivity().getLayoutInflater().inflate(R.layout.gallery_child_list_item, (ViewGroup) null);
            item.setOnClickListener(this);
            item.setOnLongClickListener(this);
            view.addView(item, this.mColumnWidth, this.mColumnHeight);
        }
        return view;
    }

    @Override // android.widget.CursorTreeAdapter
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        Trace.d(TAG, "newGroupView()");
        View view = this.mGalleryFragment.getActivity().getLayoutInflater().inflate(R.layout.gallery_group_list_item, (ViewGroup) null);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.group_list_check_box);
        checkBox.setOnClickListener(this);
        RecycleImageView indicator = (RecycleImageView) view.findViewById(R.id.group_list_indicator);
        indicator.setOnClickListener(this);
        return view;
    }

    @Override // android.widget.CursorTreeAdapter
    public void notifyDataSetChanged(boolean releaseCursors) {
        super.notifyDataSetChanged(releaseCursors);
        if (releaseCursors) {
            this.mChildTotalCount = getChildrenCountFromCursor();
        }
    }

    public void onOrientationChanged() {
        initCoordinate();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery_child_list_item_layout /* 2131558566 */:
                GalleryFragment.OnItemClickListener onItemClickListener = this.mGalleryFragment.mOnItemClickListener;
                if (onItemClickListener != null) {
                    int[] positions = (int[]) view.getTag();
                    onItemClickListener.onItemClick(view, positions[0], positions[1]);
                }
                break;
            case R.id.group_list_check_box /* 2131558572 */:
                changeCheckedForGroup(((Integer) view.getTag()).intValue());
                break;
            case R.id.group_list_indicator /* 2131558574 */:
                int position = ((Integer) view.getTag()).intValue();
                if (view.isSelected()) {
                    this.mGalleryFragment.collapseGroup(position);
                } else {
                    this.mGalleryFragment.expandGroup(position);
                }
                break;
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        GalleryFragment.OnItemLongClickListener onItemLongClickListener = this.mGalleryFragment.mOnItemLongClickListener;
        if (onItemLongClickListener == null) {
            return false;
        }
        int[] positions = (int[]) view.getTag();
        return onItemLongClickListener.onItemLongClick(view, positions[0], positions[1]);
    }

    private boolean isGroupChecked(int position) {
        CheckedInfo checkInfo = this.mCheckedList.get(position);
        if (checkInfo == null) {
            return false;
        }
        return checkInfo.mIsChecked;
    }

    private boolean isChildChecked(int groupPosition, int childPosition) {
        Boolean childChecked;
        CheckedInfo checkInfo = this.mCheckedList.get(groupPosition);
        if (checkInfo == null || (childChecked = Boolean.valueOf(checkInfo.mChildCheckedArray.get(childPosition))) == null) {
            return false;
        }
        return childChecked.booleanValue();
    }

    private void changeCheckedForGroup(int position) {
        CheckedInfo checkInfo = this.mCheckedList.get(position);
        boolean isChecked = checkInfo == null ? false : checkInfo.mIsChecked;
        if (this.mIsCheckAll) {
            this.mIsCheckAll = false;
            for (int i = 0; i < getGroupCount(); i++) {
                if (i != position) {
                    CheckedInfo checkInfo2 = new CheckedInfo();
                    checkInfo2.mIsChecked = true;
                    this.mCheckedList.put(i, checkInfo2);
                }
            }
            setCheckedCount(this.mCheckedCount - getChildrenCountFromCursor(position));
        } else if (isChecked) {
            if (checkInfo.mChildCheckedArray.size() != 0) {
                setCheckedCount(this.mCheckedCount - checkInfo.mChildCheckedArray.size());
            } else {
                setCheckedCount(this.mCheckedCount - getChildrenCountFromCursor(position));
            }
            this.mCheckedList.remove(position);
        } else {
            if (checkInfo != null && checkInfo.mChildCheckedArray.size() > 0) {
                this.mCheckedCount -= checkInfo.mChildCheckedArray.size();
            }
            this.mCheckedCount += getChildrenCountFromCursor(position);
            setCheckedCount(this.mCheckedCount);
            if (this.mCheckedCount == this.mChildTotalCount) {
                this.mIsCheckAll = true;
                this.mCheckedList.clear();
            } else {
                if (checkInfo == null) {
                    checkInfo = new CheckedInfo();
                }
                checkInfo.mIsChecked = true;
                checkInfo.mChildCheckedArray.clear();
                this.mCheckedList.put(position, checkInfo);
            }
        }
        notifyDataSetChanged(false);
    }

    protected void changeCheckedForChild(int groupPosition, int childPosition) {
        CheckedInfo checkInfo = this.mCheckedList.get(groupPosition);
        boolean isGroupChecked = checkInfo == null ? false : checkInfo.mIsChecked;
        if (this.mIsCheckAll) {
            this.mIsCheckAll = false;
            for (int groupPos = 0; groupPos < getGroupCount(); groupPos++) {
                CheckedInfo checkInfo2 = new CheckedInfo();
                if (groupPos != groupPosition) {
                    checkInfo2.mIsChecked = true;
                } else {
                    for (int childPos = 0; childPos < getChildrenCountFromCursor(groupPosition); childPos++) {
                        if (childPos != childPosition) {
                            checkInfo2.mChildCheckedArray.put(childPos, true);
                        }
                    }
                }
                this.mCheckedList.put(groupPos, checkInfo2);
            }
            int i = this.mCheckedCount - 1;
            this.mCheckedCount = i;
            setCheckedCount(i);
            return;
        }
        if (isGroupChecked) {
            checkInfo.mIsChecked = false;
            if (checkInfo.mChildCheckedArray.size() != 0) {
                checkInfo.mChildCheckedArray.delete(childPosition);
            } else {
                for (int childPos2 = 0; childPos2 < getChildrenCountFromCursor(groupPosition); childPos2++) {
                    if (childPos2 != childPosition) {
                        checkInfo.mChildCheckedArray.put(childPos2, true);
                    }
                }
            }
            this.mCheckedList.put(groupPosition, checkInfo);
            int i2 = this.mCheckedCount - 1;
            this.mCheckedCount = i2;
            setCheckedCount(i2);
            return;
        }
        boolean isChildChecked = isChildChecked(groupPosition, childPosition);
        if (isChildChecked) {
            if (checkInfo.mChildCheckedArray.size() == 1) {
                this.mCheckedList.remove(groupPosition);
                int i3 = this.mCheckedCount - 1;
                this.mCheckedCount = i3;
                setCheckedCount(i3);
                return;
            }
        } else {
            if (this.mCheckedCount + 1 == this.mChildTotalCount) {
                this.mIsCheckAll = true;
                this.mCheckedList.clear();
                int i4 = this.mCheckedCount + 1;
                this.mCheckedCount = i4;
                setCheckedCount(i4);
                return;
            }
            if (checkInfo == null) {
                checkInfo = new CheckedInfo();
            }
            if (checkInfo.mChildCheckedArray.size() + 1 == getChildrenCountFromCursor(groupPosition)) {
                checkInfo.mIsChecked = true;
                checkInfo.mChildCheckedArray.clear();
                this.mCheckedList.put(groupPosition, checkInfo);
                int i5 = this.mCheckedCount + 1;
                this.mCheckedCount = i5;
                setCheckedCount(i5);
                return;
            }
        }
        if (isChildChecked) {
            checkInfo.mChildCheckedArray.delete(childPosition);
            int i6 = this.mCheckedCount - 1;
            this.mCheckedCount = i6;
            setCheckedCount(i6);
        } else {
            checkInfo.mChildCheckedArray.put(childPosition, true);
            int i7 = this.mCheckedCount + 1;
            this.mCheckedCount = i7;
            setCheckedCount(i7);
        }
        this.mCheckedList.put(groupPosition, checkInfo);
    }

    public void setCheckable(boolean isCheckable) {
        if (this.mIsCheckable != isCheckable) {
            this.mIsCheckable = isCheckable;
            if (!isCheckable) {
                this.mIsCheckAll = false;
                setCheckedCount(0);
                this.mCheckedList.clear();
            }
            notifyDataSetChanged(false);
        }
    }

    private void setCheckedCount(int count) {
        this.mCheckedCount = count;
        this.mGalleryFragment.getActivity().invalidateOptionsMenu();
    }

    public int getCheckedCount() {
        return this.mCheckedCount;
    }

    public int getChildTotalCount() {
        return this.mChildTotalCount;
    }

    public void setCheckAll(boolean isCheckAll) {
        if ((this.mCheckedCount != 0 && this.mCheckedCount != this.mChildTotalCount) || this.mIsCheckAll != isCheckAll) {
            this.mIsCheckAll = isCheckAll;
            if (isCheckAll) {
                setCheckedCount(this.mChildTotalCount);
            } else {
                setCheckedCount(0);
            }
            this.mCheckedList.clear();
            notifyDataSetChanged(false);
        }
    }

    public boolean getCheckAll() {
        return this.mIsCheckAll;
    }

    public SparseArray<CheckedInfo> getCheckedList() {
        return this.mCheckedList;
    }

    public int getColumnNum() {
        return this.mColumnNum;
    }
}

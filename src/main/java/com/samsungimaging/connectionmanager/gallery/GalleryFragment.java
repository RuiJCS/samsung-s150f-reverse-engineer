package com.samsungimaging.connectionmanager.gallery;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.dialog.CustomDialog;
import com.samsungimaging.connectionmanager.dialog.CustomProgressDialog;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseMedia;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;
import com.samsungimaging.connectionmanager.util.AsyncTask;
import com.samsungimaging.connectionmanager.util.ImageLoader;
import com.samsungimaging.connectionmanager.util.RecycleBitmapDrawable;
import com.samsungimaging.connectionmanager.util.Trace;
import com.samsungimaging.connectionmanager.util.Utils;
import com.samsungimaging.connectionmanager.widget.ImageViewer;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GalleryFragment extends Fragment implements AbsListView.OnScrollListener, ExpandableListView.OnGroupClickListener, GalleryPagerAdapter.OnInstantiateItemListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State = null;
    public static final String DISK_CACHE_DIR_THUMBNAIL = "thumbnail";
    public static final String DISK_CACHE_DIR_VIEWER = "viewer";
    private static final int HANDLER_RECEIVED_MEDIA = 0;
    private static final int QUERY_HANDLER_GROUP = 0;
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    private ArrayList<DatabaseMedia> delete_data_list;
    TextView emptyView;
    public List<String> groupList = new ArrayList();
    private State mState = State.NORMAL;
    private GalleryPager mGalleryPager = null;
    private GalleryPagerAdapter mGalleryPagerAdapter = null;
    private GalleryAdapter mAdapter = null;
    private ExpandableListView mListView = null;
    private ImageLoader mImageLoaderThumbnail = null;
    private ImageLoader mImageLoaderViewer = null;
    private Cursor mGroupCursor = null;
    private InternalContentObserver mContentObserver = new InternalContentObserver();
    private boolean mGroupCursorChangeable = true;
    private Query mQuery = null;
    protected OnItemClickListener mOnItemClickListener = null;
    protected OnItemLongClickListener mOnItemLongClickListener = null;
    private QueryHandler mAsyncQueryHandler = null;
    private Handler mHandler = new Handler() { // from class: com.samsungimaging.connectionmanager.gallery.GalleryFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    GalleryFragment.this.requeryForGroup();
                    GalleryFragment.this.setSelectionChild((DatabaseMedia) msg.obj);
                    break;
            }
        }
    };

    public interface OnItemClickListener {
        void onItemClick(View view, int i, int i2);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int i, int i2);
    }

    public interface Query {
        QueryInfo getChildQueryInfo(Cursor cursor);

        QueryInfo getGroupQueryInfo();
    }

    public enum State {
        NORMAL,
        MULTI_SELECT,
        IMAGE_VIEWER;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static State[] valuesCustom() {
            State[] stateArrValuesCustom = values();
            int length = stateArrValuesCustom.length;
            State[] stateArr = new State[length];
            System.arraycopy(stateArrValuesCustom, 0, stateArr, 0, length);
            return stateArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State;
        if (iArr == null) {
            iArr = new int[State.valuesCustom().length];
            try {
                iArr[State.IMAGE_VIEWER.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[State.MULTI_SELECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[State.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State = iArr;
        }
        return iArr;
    }

    public static final class QueryInfo {
        public String mOrderBy;
        public String[] mProjection;
        public String mSelection;
        public String[] mSelectionArgs;
        public Uri mUri;

        public QueryInfo(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
            this.mUri = null;
            this.mProjection = null;
            this.mSelection = null;
            this.mSelectionArgs = null;
            this.mOrderBy = null;
            this.mUri = uri;
            this.mProjection = projection;
            this.mSelection = selection;
            this.mSelectionArgs = selectionArgs;
            this.mOrderBy = orderBy;
        }
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Trace.d(GalleryFragment.TAG, "onQueryComplete() - " + token);
            if (GalleryFragment.this.mAsyncQueryHandler != null) {
                switch (token) {
                    case 0:
                        if (GalleryFragment.this.mListView != null) {
                            if (GalleryFragment.this.mAdapter != null) {
                                GalleryFragment.this.mAdapter.changeCursor(cursor);
                                GalleryFragment.this.expandGroupAll();
                            } else {
                                GalleryFragment.this.mAdapter = new GalleryAdapter(GalleryFragment.this, cursor, GalleryFragment.this.mImageLoaderThumbnail);
                                if (GalleryFragment.this.mState == State.MULTI_SELECT) {
                                    GalleryFragment.this.mAdapter.setCheckable(true);
                                }
                                GalleryFragment.this.mListView.setAdapter(GalleryFragment.this.mAdapter);
                                GalleryFragment.this.expandGroupAll();
                            }
                        }
                        if (cursor != null) {
                            GalleryFragment.this.mGroupCursor = cursor;
                            GalleryFragment.this.mGroupCursor.registerContentObserver(GalleryFragment.this.mContentObserver);
                        }
                        GalleryFragment.this.getActivity().invalidateOptionsMenu();
                        break;
                }
            }
        }
    }

    private class InternalContentObserver extends ContentObserver {
        public InternalContentObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            GalleryFragment.this.requeryForGroup();
        }
    }

    public void getdeletdata() {
        this.delete_data_list = new ArrayList<>();
        if (this.mState == State.MULTI_SELECT) {
            if (this.mAdapter.getCheckAll()) {
                for (int groupPosition = 0; groupPosition < this.mAdapter.getGroupCount(); groupPosition++) {
                    for (int childPosition = 0; childPosition < this.mAdapter.getChildrenCountFromCursor(groupPosition); childPosition++) {
                        DatabaseMedia media = DatabaseMedia.builder(this.mAdapter.getChild(groupPosition, childPosition));
                        this.delete_data_list.add(media);
                    }
                }
                return;
            }
            SparseArray<GalleryAdapter.CheckedInfo> checkedList = this.mAdapter.getCheckedList();
            for (int i = 0; i < checkedList.size(); i++) {
                GalleryAdapter.CheckedInfo checkedInfo = checkedList.valueAt(i);
                int groupPosition2 = checkedList.keyAt(i);
                if (checkedInfo.isChecked()) {
                    for (int childPosition2 = 0; childPosition2 < this.mAdapter.getChildrenCountFromCursor(groupPosition2); childPosition2++) {
                        DatabaseMedia media2 = DatabaseMedia.builder(this.mAdapter.getChild(groupPosition2, childPosition2));
                        this.delete_data_list.add(media2);
                    }
                } else {
                    SparseBooleanArray childCheckedArray = checkedInfo.getChildCheckedArray();
                    for (int j = 0; j < childCheckedArray.size(); j++) {
                        int childPosition3 = childCheckedArray.keyAt(j);
                        DatabaseMedia media3 = DatabaseMedia.builder(this.mAdapter.getChild(groupPosition2, childPosition3));
                        this.delete_data_list.add(media3);
                    }
                }
            }
            return;
        }
        if (this.mState == State.IMAGE_VIEWER) {
            this.delete_data_list.add(this.mGalleryPager.getCurrentImageViewer().getMedia());
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Void, Integer, Void> {
        private CustomProgressDialog mProgressDialog;

        private DeleteAsyncTask() {
            this.mProgressDialog = null;
        }

        /* synthetic */ DeleteAsyncTask(GalleryFragment galleryFragment, DeleteAsyncTask deleteAsyncTask) {
            this();
        }

        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        protected void onPreExecute() {
            GalleryFragment.this.mGroupCursorChangeable = false;
            int maxCount = GalleryFragment.this.getCheckedCount();
            this.mProgressDialog = new CustomProgressDialog(GalleryFragment.this.getActivity());
            this.mProgressDialog.setProgressStyle(1);
            this.mProgressDialog.setTitle(R.string.delete);
            this.mProgressDialog.setMax(maxCount);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.show();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public Void doInBackground(Void... params) {
            int count = 0;
            if (GalleryFragment.this.delete_data_list != null) {
                for (int i = 0; i < GalleryFragment.this.delete_data_list.size(); i++) {
                    count++;
                    delete((DatabaseMedia) GalleryFragment.this.delete_data_list.get(i), count);
                }
            }
            GalleryFragment.this.mGroupCursorChangeable = true;
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public void onProgressUpdate(Integer... values) {
            this.mProgressDialog.setProgress(values[0].intValue());
            super.onProgressUpdate((Object[]) values);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        public void onPostExecute(Void result) {
            finish();
            GalleryFragment.this.delete_data_list = null;
            super.onPostExecute(result);
        }

        @Override // com.samsungimaging.connectionmanager.util.AsyncTask
        protected void onCancelled() {
            finish();
            super.onCancelled();
        }

        private void finish() {
            GalleryFragment.this.setState(State.NORMAL, new Object[0]);
            if (this.mProgressDialog != null) {
                this.mProgressDialog.dismiss();
                this.mProgressDialog = null;
            }
            GalleryFragment.this.requeryForGroup();
        }

        private void delete(DatabaseMedia media, int progress) {
            DatabaseManager.deleteForLocalMedia(GalleryFragment.this.getActivity(), media);
            if (GalleryFragment.this.mImageLoaderThumbnail != null) {
                GalleryFragment.this.mImageLoaderThumbnail.removeCache(media.getThumbnailPath());
            }
            if (GalleryFragment.this.mImageLoaderViewer != null) {
                GalleryFragment.this.mImageLoaderViewer.removeCache(media.getViewerPath());
            }
            publishProgress(Integer.valueOf(progress));
        }
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Trace.d(TAG, "onCreate()");
        this.mAsyncQueryHandler = new QueryHandler(getActivity().getContentResolver());
        if (this.mQuery != null) {
            queryForGroup();
        }
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Trace.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.samsungimaging.connectionmanager.gallery.GalleryFragment.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.mListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);
        this.emptyView = (TextView) view.findViewById(R.id.empty_view);
        this.mListView.setEmptyView(this.emptyView);
        this.mListView.setOnGroupClickListener(this);
        this.mGalleryPager = (GalleryPager) view.findViewById(R.id.view_pager);
        return view;
    }

    @Override // android.app.Fragment
    public void onResume() {
        Trace.d(TAG, "onResume()");
        requeryForGroup();
        super.onResume();
    }

    @Override // android.app.Fragment
    public void onPause() {
        Trace.d(TAG, "onPause()");
        if (this.mGroupCursor != null) {
            this.mGroupCursor.deactivate();
        }
        if (this.mImageLoaderThumbnail != null) {
            this.mImageLoaderThumbnail.setPauseLoad(false);
            this.mImageLoaderThumbnail.flushCache();
        }
        if (this.mImageLoaderViewer != null) {
            this.mImageLoaderViewer.setPauseLoad(false);
            this.mImageLoaderViewer.flushCache();
        }
        this.emptyView.setVisibility(8);
        super.onPause();
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        Trace.d(TAG, "onDestroyView()");
        Utils.unbindView(getView());
        if (this.mGroupCursor != null) {
            this.mGroupCursor.unregisterContentObserver(this.mContentObserver);
            this.mGroupCursor.close();
            this.mGroupCursor = null;
        }
        if (this.mAdapter != null) {
            this.mAdapter.changeCursor(null);
            this.mAdapter = null;
        }
        if (this.mGalleryPagerAdapter != null) {
            this.mGalleryPagerAdapter.clear();
            this.mGalleryPagerAdapter = null;
        }
        if (this.mGalleryPager != null) {
            this.mGalleryPager.removeAllViews();
            this.mGalleryPager = null;
        }
        if (this.mImageLoaderThumbnail != null) {
            this.mImageLoaderThumbnail.clearMemoryCache();
            this.mImageLoaderThumbnail.closeCache();
            this.mImageLoaderThumbnail = null;
        }
        if (this.mImageLoaderViewer != null) {
            this.mImageLoaderViewer.clearMemoryCache();
            this.mImageLoaderViewer.closeCache();
            this.mImageLoaderViewer = null;
        }
        super.onDestroyView();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        Trace.d(TAG, "onDestroy()");
        this.mAsyncQueryHandler.removeMessages(0);
        this.mAsyncQueryHandler = null;
        super.onDestroy();
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == 2) {
            this.mImageLoaderThumbnail.setPauseLoad(true);
        } else {
            this.mImageLoaderThumbnail.setPauseLoad(false);
        }
    }

    @Override // android.widget.ExpandableListView.OnGroupClickListener
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mAdapter != null) {
            this.mAdapter.onOrientationChanged();
            this.mAdapter.notifyDataSetChanged(false);
        }
    }

    public void setDiskCacheDir(String diskCacheDirThumbnail, String diskCacheDirViewer) {
        this.mImageLoaderThumbnail = new ImageLoader(getActivity(), ImageLoader.ImageSize.SMALL, diskCacheDirThumbnail);
        this.mImageLoaderViewer = new ImageLoader(getActivity(), ImageLoader.ImageSize.LARGE, diskCacheDirViewer);
    }

    public void CheckDirtyCache(String filePath) {
        if (this.mImageLoaderThumbnail != null && this.mImageLoaderThumbnail.hasSnapshotInDiskCache(filePath)) {
            Trace.i(Trace.Tag.COMMON, "CheckDirtyCache remove Thumbnail Cache : " + filePath);
            this.mImageLoaderThumbnail.removeCache(filePath);
        }
        if (this.mImageLoaderViewer != null && this.mImageLoaderViewer.hasSnapshotInDiskCache(filePath)) {
            Trace.i(Trace.Tag.COMMON, "CheckDirtyCache remove Viewer Cache : " + filePath);
            this.mImageLoaderViewer.removeCache(filePath);
        }
    }

    public void setQuery(Query query) {
        if (query != null) {
            this.mQuery = query;
            if (this.mAsyncQueryHandler != null) {
                queryForGroup();
            }
        }
    }

    public Query getQuery() {
        return this.mQuery;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.mState == State.NORMAL) {
            return false;
        }
        setState(State.NORMAL, new Object[0]);
        return true;
    }

    public void expandGroupAll() {
        for (int i = 0; i < this.mAdapter.getGroupCount(); i++) {
            if (this.groupList.contains(new StringBuilder(String.valueOf(i)).toString())) {
                this.mListView.collapseGroup(i);
            } else {
                this.mListView.expandGroup(i);
            }
        }
    }

    private void queryForGroup() {
        QueryInfo queryInfo = this.mQuery.getGroupQueryInfo();
        this.mAsyncQueryHandler.startQuery(0, null, queryInfo.mUri, queryInfo.mProjection, queryInfo.mSelection, queryInfo.mSelectionArgs, queryInfo.mOrderBy);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requeryForGroup() {
        if (this.mGroupCursor != null && this.mGroupCursorChangeable) {
            Trace.d(TAG, "requeryForGroupCursor");
            this.mGroupCursor.requery();
            expandGroupAll();
            getActivity().invalidateOptionsMenu();
            if (this.mGalleryPagerAdapter != null) {
                this.mGalleryPagerAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setState(State state, Object... objects) {
        this.mState = state;
        switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$gallery$GalleryFragment$State()[state.ordinal()]) {
            case 1:
                if (this.mImageLoaderViewer != null) {
                    this.mImageLoaderViewer.setPauseLoad(false);
                    this.mImageLoaderViewer.flushCache();
                }
                if (this.mGalleryPager.getVisibility() == 0) {
                    ImageViewer viewer = this.mGalleryPager.getCurrentImageViewer();
                    if (viewer != null && !viewer.isScaleMin()) {
                        viewer.setScaleMin();
                    }
                    this.mGalleryPager.setVisibility(8);
                    this.mListView.setVisibility(0);
                }
                if (this.mAdapter != null) {
                    this.mAdapter.setCheckable(false);
                }
                break;
            case 2:
                if (this.mImageLoaderViewer != null) {
                    this.mImageLoaderViewer.setPauseLoad(false);
                    this.mImageLoaderViewer.flushCache();
                }
                if (this.mGalleryPager.getVisibility() == 0) {
                    ImageViewer viewer2 = this.mGalleryPager.getCurrentImageViewer();
                    if (viewer2 != null && !viewer2.isScaleMin()) {
                        viewer2.setScaleMin();
                    }
                    this.mGalleryPager.setVisibility(8);
                    this.mListView.setVisibility(0);
                }
                if (this.mAdapter != null) {
                    this.mAdapter.setCheckable(true);
                }
                if (objects.length > 0) {
                    setChecked(((Integer) objects[0]).intValue(), ((Integer) objects[1]).intValue());
                }
                break;
            case 3:
                if (this.mImageLoaderThumbnail != null) {
                    this.mImageLoaderThumbnail.setPauseLoad(false);
                    this.mImageLoaderThumbnail.flushCache();
                }
                if (this.mGalleryPagerAdapter == null) {
                    this.mGalleryPagerAdapter = new GalleryPagerAdapter(this);
                    this.mGalleryPagerAdapter.setOnInstantiateItemListener(this);
                    this.mGalleryPager.setAdapter(this.mGalleryPagerAdapter);
                }
                this.mGalleryPager.setCurrentItem(getPagerPosition(((Integer) objects[0]).intValue(), ((Integer) objects[1]).intValue()), false);
                this.mGalleryPager.setVisibility(0);
                this.mListView.setVisibility(8);
                break;
        }
        getActivity().invalidateOptionsMenu();
    }

    public State getState() {
        return this.mState;
    }

    public int getCheckedCount() {
        if (this.mState == State.MULTI_SELECT) {
            if (this.mAdapter == null) {
                return 0;
            }
            int count = this.mAdapter.getCheckedCount();
            return count;
        }
        if (this.mState != State.IMAGE_VIEWER) {
            return 0;
        }
        return 1;
    }

    public boolean isEmpty() {
        return this.mAdapter == null || this.mAdapter.getChildTotalCount() <= 0;
    }

    public void setCheckAll(boolean isCheckAll) {
        if (this.mAdapter != null) {
            this.mAdapter.setCheckAll(isCheckAll);
        }
    }

    public void collapseGroup(int position) {
        if (this.mListView != null) {
            this.mListView.collapseGroup(position);
            if (!this.groupList.contains(Integer.valueOf(position))) {
                String s = new StringBuilder(String.valueOf(position)).toString();
                this.groupList.add(s);
            }
        }
    }

    public void expandGroup(int position) {
        if (this.mListView != null) {
            this.mListView.expandGroup(position);
            if (this.groupList.contains(new StringBuilder(String.valueOf(position)).toString())) {
                this.groupList.remove(new StringBuilder(String.valueOf(position)).toString());
            }
        }
    }

    public int getChildTotalCount() {
        return this.mAdapter.getChildTotalCount();
    }

    public void delete() {
        int checkedCount = getCheckedCount();
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setTitle(R.string.delete);
        dialog.setMessage(getString(R.string.delete_count, Integer.valueOf(checkedCount)));
        dialog.setPositiveButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
        dialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.samsungimaging.connectionmanager.gallery.GalleryFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int which) {
                dialog2.dismiss();
                GalleryFragment.this.getdeletdata();
                new DeleteAsyncTask(GalleryFragment.this, null).execute(new Void[0]);
            }
        });
        dialog.show();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.mOnItemLongClickListener = l;
    }

    public void setChecked(int groupPosition, int childPosition) {
        if (this.mAdapter != null) {
            this.mAdapter.changeCheckedForChild(groupPosition, childPosition);
            this.mAdapter.notifyDataSetChanged(false);
        }
    }

    public void setReceivedMedia(DatabaseMedia media) {
        if (this.mHandler.hasMessages(0)) {
            this.mHandler.removeMessages(0);
        }
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = media;
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelectionChild(DatabaseMedia media) {
        Cursor groupCursor = this.mAdapter.getCursor();
        int groupPosition = 0;
        for (int i = 0; i < groupCursor.getCount(); i++) {
            groupCursor.moveToPosition(i);
            String dateTakenString = groupCursor.getString(groupCursor.getColumnIndex(GalleryColumns.KEY_DATE_TAKEN_STRING));
            if (media.getDateTakenString().equals(dateTakenString)) {
                groupPosition = i;
                break;
            }
        }
        Cursor childCursor = this.mAdapter.getChildrenCursor(groupCursor);
        int childPosition = 0;
        for (int i2 = 0; i2 < childCursor.getCount(); i2++) {
            childCursor.moveToPosition(i2);
            String filePath = childCursor.getString(childCursor.getColumnIndex(GalleryColumns.KEY_ORIGINAL_PATH));
            if (media.getOriginalPath().equals(filePath)) {
                childPosition = i2 / this.mAdapter.getColumnNum();
                break;
            }
        }
        this.mListView.setSelectedChild(groupPosition, childPosition, true);
    }

    public void clearCache() {
        this.mImageLoaderThumbnail.clearCache();
        this.mImageLoaderViewer.clearCache();
    }

    @Override // com.samsungimaging.connectionmanager.gallery.GalleryPagerAdapter.OnInstantiateItemListener
    public ImageViewer onInstantiateItem(ViewGroup container, int position) {
        int[] positions = getGalleryPositions(position);
        ImageViewer viewer = new ImageViewer(getActivity());
        viewer.setImageLoader(this.mImageLoaderViewer);
        Cursor cursor = this.mAdapter.getChild(positions[0], positions[1]);
        DatabaseMedia media = DatabaseMedia.builder(cursor);
        RecycleBitmapDrawable loadingImage = this.mImageLoaderThumbnail.getBitmapDrawableFromMemoryCache(media.getThumbnailPath());
        if (loadingImage == null) {
            loadingImage = this.mImageLoaderThumbnail.loadImageSync(media);
        }
        viewer.loadImage(media, loadingImage);
        ((ViewPager) container).addView(viewer);
        return viewer;
    }

    private int getPagerPosition(int groupPosition, int childPosition) {
        int position = childPosition;
        for (int i = 0; i < groupPosition; i++) {
            position += this.mAdapter.getChildrenCountFromCursor(i);
        }
        return position;
    }

    private int[] getGalleryPositions(int position) {
        int groupPosition = 0;
        int childPosition = position;
        for (int i = 0; i < this.mAdapter.getGroupCount(); i++) {
            groupPosition = i;
            int temp = this.mAdapter.getChildrenCountFromCursor(groupPosition);
            if (childPosition - temp < 0) {
                break;
            }
            childPosition -= temp;
        }
        return new int[]{groupPosition, childPosition};
    }
}

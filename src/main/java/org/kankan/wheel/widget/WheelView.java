package org.kankan.wheel.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.samsungimaging.connectionmanager.R;
import java.util.LinkedList;
import java.util.List;
import org.kankan.wheel.widget.adapters.WheelViewAdapter;

/* JADX INFO: loaded from: classes.dex */
public class WheelView extends View {
    private static final int DEF_VISIBLE_ITEMS = 5;
    private static final int ITEM_OFFSET_PERCENT = 10;
    private static final int PADDING = 10;
    private static final int[] SHADOWS_COLORS = {-15658735, 11184810, 11184810};
    private Drawable centerDrawable;
    private List<OnWheelChangedListener> changingListeners;
    private List<OnWheelClickedListener> clickingListeners;
    private int currentItem;
    private DataSetObserver dataObserver;
    private int firstItem;
    boolean isCyclic;
    private boolean isScrollingPerformed;
    private int itemWidth;
    private LinearLayout itemsLayout;
    private GradientDrawable leftShadow;
    private WheelRecycle recycle;
    private GradientDrawable rightShadow;
    private WheelScroller scroller;
    WheelScroller.ScrollingListener scrollingListener;
    private List<OnWheelScrollListener> scrollingListeners;
    private int scrollingOffset;
    private WheelViewAdapter viewAdapter;
    private int visibleItems;

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.currentItem = 0;
        this.visibleItems = 5;
        this.itemWidth = 0;
        this.isCyclic = false;
        this.recycle = new WheelRecycle(this);
        this.changingListeners = new LinkedList();
        this.scrollingListeners = new LinkedList();
        this.clickingListeners = new LinkedList();
        this.scrollingListener = new WheelScroller.ScrollingListener() { // from class: org.kankan.wheel.widget.WheelView.1
            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onStarted() {
                WheelView.this.isScrollingPerformed = true;
                WheelView.this.notifyScrollingListenersAboutStart();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onScroll(int distance) {
                WheelView.this.doScroll(distance);
                int width = WheelView.this.getWidth();
                if (WheelView.this.scrollingOffset > width) {
                    WheelView.this.scrollingOffset = width;
                    WheelView.this.scroller.stopScrolling();
                } else if (WheelView.this.scrollingOffset < (-width)) {
                    WheelView.this.scrollingOffset = -width;
                    WheelView.this.scroller.stopScrolling();
                }
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onFinished() {
                if (WheelView.this.isScrollingPerformed) {
                    WheelView.this.notifyScrollingListenersAboutEnd();
                    WheelView.this.isScrollingPerformed = false;
                }
                WheelView.this.scrollingOffset = 0;
                WheelView.this.invalidate();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onJustify() {
                if (Math.abs(WheelView.this.scrollingOffset) > 1) {
                    WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);
                }
            }
        };
        this.dataObserver = new DataSetObserver() { // from class: org.kankan.wheel.widget.WheelView.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                WheelView.this.invalidateWheel(false);
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                WheelView.this.invalidateWheel(true);
            }
        };
        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentItem = 0;
        this.visibleItems = 5;
        this.itemWidth = 0;
        this.isCyclic = false;
        this.recycle = new WheelRecycle(this);
        this.changingListeners = new LinkedList();
        this.scrollingListeners = new LinkedList();
        this.clickingListeners = new LinkedList();
        this.scrollingListener = new WheelScroller.ScrollingListener() { // from class: org.kankan.wheel.widget.WheelView.1
            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onStarted() {
                WheelView.this.isScrollingPerformed = true;
                WheelView.this.notifyScrollingListenersAboutStart();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onScroll(int distance) {
                WheelView.this.doScroll(distance);
                int width = WheelView.this.getWidth();
                if (WheelView.this.scrollingOffset > width) {
                    WheelView.this.scrollingOffset = width;
                    WheelView.this.scroller.stopScrolling();
                } else if (WheelView.this.scrollingOffset < (-width)) {
                    WheelView.this.scrollingOffset = -width;
                    WheelView.this.scroller.stopScrolling();
                }
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onFinished() {
                if (WheelView.this.isScrollingPerformed) {
                    WheelView.this.notifyScrollingListenersAboutEnd();
                    WheelView.this.isScrollingPerformed = false;
                }
                WheelView.this.scrollingOffset = 0;
                WheelView.this.invalidate();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onJustify() {
                if (Math.abs(WheelView.this.scrollingOffset) > 1) {
                    WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);
                }
            }
        };
        this.dataObserver = new DataSetObserver() { // from class: org.kankan.wheel.widget.WheelView.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                WheelView.this.invalidateWheel(false);
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                WheelView.this.invalidateWheel(true);
            }
        };
        initData(context);
    }

    public WheelView(Context context) {
        super(context);
        this.currentItem = 0;
        this.visibleItems = 5;
        this.itemWidth = 0;
        this.isCyclic = false;
        this.recycle = new WheelRecycle(this);
        this.changingListeners = new LinkedList();
        this.scrollingListeners = new LinkedList();
        this.clickingListeners = new LinkedList();
        this.scrollingListener = new WheelScroller.ScrollingListener() { // from class: org.kankan.wheel.widget.WheelView.1
            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onStarted() {
                WheelView.this.isScrollingPerformed = true;
                WheelView.this.notifyScrollingListenersAboutStart();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onScroll(int distance) {
                WheelView.this.doScroll(distance);
                int width = WheelView.this.getWidth();
                if (WheelView.this.scrollingOffset > width) {
                    WheelView.this.scrollingOffset = width;
                    WheelView.this.scroller.stopScrolling();
                } else if (WheelView.this.scrollingOffset < (-width)) {
                    WheelView.this.scrollingOffset = -width;
                    WheelView.this.scroller.stopScrolling();
                }
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onFinished() {
                if (WheelView.this.isScrollingPerformed) {
                    WheelView.this.notifyScrollingListenersAboutEnd();
                    WheelView.this.isScrollingPerformed = false;
                }
                WheelView.this.scrollingOffset = 0;
                WheelView.this.invalidate();
            }

            @Override // org.kankan.wheel.widget.WheelScroller.ScrollingListener
            public void onJustify() {
                if (Math.abs(WheelView.this.scrollingOffset) > 1) {
                    WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);
                }
            }
        };
        this.dataObserver = new DataSetObserver() { // from class: org.kankan.wheel.widget.WheelView.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                WheelView.this.invalidateWheel(false);
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                WheelView.this.invalidateWheel(true);
            }
        };
        initData(context);
    }

    private void initData(Context context) {
        this.scroller = new WheelScroller(getContext(), this.scrollingListener);
    }

    public void setInterpolator(Interpolator interpolator) {
        this.scroller.setInterpolator(interpolator);
    }

    public int getVisibleItems() {
        return this.visibleItems;
    }

    public void setVisibleItems(int count) {
        this.visibleItems = count;
    }

    public WheelViewAdapter getViewAdapter() {
        return this.viewAdapter;
    }

    public void setViewAdapter(WheelViewAdapter viewAdapter) {
        if (this.viewAdapter != null) {
            this.viewAdapter.unregisterDataSetObserver(this.dataObserver);
        }
        this.viewAdapter = viewAdapter;
        if (this.viewAdapter != null) {
            this.viewAdapter.registerDataSetObserver(this.dataObserver);
        }
        invalidateWheel(true);
    }

    public void addChangingListener(OnWheelChangedListener listener) {
        this.changingListeners.add(listener);
    }

    public void removeChangingListener(OnWheelChangedListener listener) {
        this.changingListeners.remove(listener);
    }

    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : this.changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    public void addScrollingListener(OnWheelScrollListener listener) {
        this.scrollingListeners.add(listener);
    }

    public void removeScrollingListener(OnWheelScrollListener listener) {
        this.scrollingListeners.remove(listener);
    }

    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : this.scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : this.scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    public void addClickingListener(OnWheelClickedListener listener) {
        this.clickingListeners.add(listener);
    }

    public void removeClickingListener(OnWheelClickedListener listener) {
        this.clickingListeners.remove(listener);
    }

    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelClickedListener listener : this.clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

    public int getCurrentItem() {
        return this.currentItem;
    }

    public void setCurrentItem(int index, boolean animated) {
        int scroll;
        if (this.viewAdapter != null && this.viewAdapter.getItemsCount() != 0) {
            int itemCount = this.viewAdapter.getItemsCount();
            if (index < 0 || index >= itemCount) {
                if (this.isCyclic) {
                    while (index < 0) {
                        index += itemCount;
                    }
                    index %= itemCount;
                } else {
                    return;
                }
            }
            if (index != this.currentItem) {
                if (animated) {
                    int itemsToScroll = index - this.currentItem;
                    if (this.isCyclic && (scroll = (Math.min(index, this.currentItem) + itemCount) - Math.max(index, this.currentItem)) < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                    scroll(itemsToScroll, 0);
                    return;
                }
                this.scrollingOffset = 0;
                int old = this.currentItem;
                this.currentItem = index;
                notifyChangingListeners(old, this.currentItem);
                invalidate();
            }
        }
    }

    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    public boolean isCyclic() {
        return this.isCyclic;
    }

    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidateWheel(false);
    }

    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            this.recycle.clearAll();
            if (this.itemsLayout != null) {
                this.itemsLayout.removeAllViews();
            }
            this.scrollingOffset = 0;
        } else if (this.itemsLayout != null) {
            this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
        }
        invalidate();
    }

    private void initResourcesIfNecessary() {
        if (this.centerDrawable == null) {
            this.centerDrawable = getContext().getResources().getDrawable(R.drawable.rvf_wheel_val);
        }
        if (this.leftShadow == null) {
            this.leftShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, SHADOWS_COLORS);
        }
        if (this.rightShadow == null) {
            this.rightShadow = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, SHADOWS_COLORS);
        }
    }

    private int getDesiredWidth(LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            this.itemWidth = layout.getChildAt(0).getMeasuredWidth();
        }
        int desired = (this.itemWidth * this.visibleItems) - ((this.itemWidth * 10) / 50);
        return Math.max(desired, getSuggestedMinimumWidth());
    }

    private int getItemWidth() {
        if (this.itemWidth != 0) {
            return this.itemWidth;
        }
        if (this.itemsLayout != null && this.itemsLayout.getChildAt(0) != null) {
            this.itemWidth = this.itemsLayout.getChildAt(0).getWidth();
            return this.itemWidth;
        }
        return getWidth() / this.visibleItems;
    }

    private int calculateLayoutHeight(int heightSize, int mode) {
        int height;
        initResourcesIfNecessary();
        this.itemsLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        this.itemsLayout.measure(View.MeasureSpec.makeMeasureSpec(heightSize, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        int height2 = this.itemsLayout.getMeasuredHeight();
        if (mode == 1073741824) {
            height = heightSize;
        } else {
            height = Math.max(height2 + 20, getSuggestedMinimumHeight());
            if (mode == Integer.MIN_VALUE && heightSize < height) {
                height = heightSize;
            }
        }
        this.itemsLayout.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(height - 20, 1073741824));
        return height;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        buildViewForMeasuring();
        int height = calculateLayoutHeight(heightSize, heightMode);
        if (widthMode == 1073741824) {
            width = widthSize;
        } else {
            width = getDesiredWidth(this.itemsLayout);
            if (widthMode == Integer.MIN_VALUE) {
                width = Math.min(width, widthSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout(r - l, b - t);
    }

    private void layout(int width, int height) {
        int itemsHeight = height - 20;
        this.itemsLayout.layout(0, 0, width, itemsHeight);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.viewAdapter != null && this.viewAdapter.getItemsCount() > 0) {
            updateView();
            drawItems(canvas);
        }
        drawShadows(canvas);
    }

    private void drawShadows(Canvas canvas) {
        int width = (int) (1.5d * ((double) getItemWidth()));
        this.leftShadow.setBounds(0, 0, width, getHeight());
        this.leftShadow.draw(canvas);
        this.rightShadow.setBounds(getWidth() - width, 0, getWidth(), getHeight());
        this.rightShadow.draw(canvas);
    }

    private void drawItems(Canvas canvas) {
        canvas.save();
        int left = ((this.currentItem - this.firstItem) * getItemWidth()) + ((getItemWidth() - getWidth()) / 2);
        canvas.translate((-left) + this.scrollingOffset, 10.0f);
        this.itemsLayout.draw(canvas);
        canvas.restore();
    }

    private void drawCenterRect(Canvas canvas) {
        int center = getWidth() / 2;
        int offset = (int) (((double) (getItemWidth() / 2)) * 1.2d);
        this.centerDrawable.setBounds(center - offset, 0, center + offset, getHeight());
        this.centerDrawable.draw(canvas);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int distance;
        if (!isEnabled() || getViewAdapter() == null) {
            return true;
        }
        switch (event.getAction()) {
            case 1:
                if (!this.isScrollingPerformed) {
                    int distance2 = ((int) event.getX()) - (getWidth() / 2);
                    if (distance2 > 0) {
                        distance = distance2 + (getItemWidth() / 2);
                    } else {
                        distance = distance2 - (getItemWidth() / 2);
                    }
                    int items = distance / getItemWidth();
                    if (isValidItemIndex(this.currentItem + items)) {
                        notifyClickListenersAboutClick(this.currentItem + items);
                    }
                }
                break;
            case 2:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return this.scroller.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScroll(int delta) {
        this.scrollingOffset += delta;
        int itemWidth = getItemWidth();
        int count = this.scrollingOffset / itemWidth;
        int pos = this.currentItem - count;
        int itemCount = this.viewAdapter.getItemsCount();
        int fixPos = this.scrollingOffset % itemWidth;
        if (Math.abs(fixPos) <= itemWidth / 2) {
            fixPos = 0;
        }
        if (this.isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--;
                count++;
            } else if (fixPos < 0) {
                pos++;
                count--;
            }
            while (pos < 0) {
                pos += itemCount;
            }
            pos %= itemCount;
        } else if (pos < 0) {
            count = this.currentItem;
            pos = 0;
        } else if (pos >= itemCount) {
            count = (this.currentItem - itemCount) + 1;
            pos = itemCount - 1;
        } else if (pos > 0 && fixPos > 0) {
            pos--;
            count++;
        } else if (pos < itemCount - 1 && fixPos < 0) {
            pos++;
            count--;
        }
        int offset = this.scrollingOffset;
        if (pos != this.currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }
        this.scrollingOffset = offset - (count * itemWidth);
        if (this.scrollingOffset > getWidth()) {
            this.scrollingOffset = (this.scrollingOffset % getWidth()) + getWidth();
        }
    }

    public void scroll(int itemsToScroll, int time) {
        int distance = (getItemWidth() * itemsToScroll) - this.scrollingOffset;
        this.scroller.scroll(distance, time);
    }

    private ItemsRange getItemsRange() {
        if (getItemWidth() == 0) {
            return null;
        }
        int first = this.currentItem;
        int count = 1;
        while (getItemWidth() * count < getWidth()) {
            first--;
            count += 2;
        }
        if (this.scrollingOffset != 0) {
            if (this.scrollingOffset > 0) {
                first--;
            }
            int emptyItems = this.scrollingOffset / getItemWidth();
            first -= emptyItems;
            count = (int) (((double) (count + 1)) + Math.asin(emptyItems));
        }
        return new ItemsRange(first, count);
    }

    private boolean rebuildItems() {
        boolean updated;
        ItemsRange range = getItemsRange();
        if (this.itemsLayout != null) {
            int first = this.recycle.recycleItems(this.itemsLayout, this.firstItem, range);
            updated = this.firstItem != first;
            this.firstItem = first;
        } else {
            createItemsLayout();
            updated = true;
        }
        if (!updated) {
            updated = (this.firstItem == range.getFirst() && this.itemsLayout.getChildCount() == range.getCount()) ? false : true;
        }
        if (this.firstItem > range.getFirst() && this.firstItem <= range.getLast()) {
            for (int i = this.firstItem - 1; i >= range.getFirst() && addViewItem(i, true); i--) {
                this.firstItem = i;
            }
        } else {
            this.firstItem = range.getFirst();
        }
        int first2 = this.firstItem;
        for (int i2 = this.itemsLayout.getChildCount(); i2 < range.getCount(); i2++) {
            if (!addViewItem(this.firstItem + i2, false) && this.itemsLayout.getChildCount() == 0) {
                first2++;
            }
        }
        this.firstItem = first2;
        return updated;
    }

    private void updateView() {
        if (rebuildItems()) {
            calculateLayoutHeight(getHeight(), 1073741824);
            layout(getWidth(), getHeight());
        }
    }

    private void createItemsLayout() {
        if (this.itemsLayout == null) {
            this.itemsLayout = new LinearLayout(getContext());
            this.itemsLayout.setOrientation(0);
        }
    }

    private void buildViewForMeasuring() {
        if (this.itemsLayout != null) {
            this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
        } else {
            createItemsLayout();
        }
        int addItems = this.visibleItems / 2;
        for (int i = this.currentItem + addItems; i >= this.currentItem - addItems; i--) {
            if (addViewItem(i, true)) {
                this.firstItem = i;
            }
        }
    }

    private boolean addViewItem(int index, boolean first) {
        View view = getItemView(index);
        if (view == null) {
            return false;
        }
        if (first) {
            this.itemsLayout.addView(view, 0);
        } else {
            this.itemsLayout.addView(view);
        }
        return true;
    }

    private boolean isValidItemIndex(int index) {
        return this.viewAdapter != null && this.viewAdapter.getItemsCount() > 0 && (this.isCyclic || (index >= 0 && index < this.viewAdapter.getItemsCount()));
    }

    private View getItemView(int index) {
        if (this.viewAdapter == null || this.viewAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = this.viewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return this.viewAdapter.getEmptyItem(this.recycle.getEmptyItem(), this.itemsLayout);
        }
        while (index < 0) {
            index += count;
        }
        return this.viewAdapter.getItem(index % count, this.recycle.getItem(), this.itemsLayout);
    }

    public void stopScrolling() {
        this.scroller.stopScrolling();
    }
}

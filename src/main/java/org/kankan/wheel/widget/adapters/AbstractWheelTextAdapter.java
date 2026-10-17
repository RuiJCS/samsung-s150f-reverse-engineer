package org.kankan.wheel.widget.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {
    public static final int DEFAULT_TEXT_COLOR = -15724528;
    public static final int DEFAULT_TEXT_SIZE = 24;
    public static final int DEFAULT_TEXT_VIEW_HEIGHT = 40;
    public static final int DEFAULT_TEXT_VIEW_WIDTH = 50;
    public static final int LABEL_COLOR = -9437072;
    protected static final int NO_RESOURCE = 0;
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
    protected Context context;
    protected int emptyItemResourceId;
    protected LayoutInflater inflater;
    protected int itemResourceId;
    protected int itemTextResourceId;
    private int textColor;
    private int textSize;
    private int textViewHeight;
    private int textViewTopPadding;
    private int textViewWidth;

    protected abstract CharSequence getItemText(int i);

    protected AbstractWheelTextAdapter(Context context) {
        this(context, -1);
    }

    protected AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, 0);
    }

    protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
        this.textColor = DEFAULT_TEXT_COLOR;
        this.textSize = 24;
        this.textViewWidth = 50;
        this.textViewHeight = 40;
        this.textViewTopPadding = 0;
        this.context = context;
        this.itemResourceId = itemResource;
        this.itemTextResourceId = itemTextResource;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextViewWidth() {
        return this.textViewWidth;
    }

    public void setTextViewWidth(int textViewWidth) {
        this.textViewWidth = textViewWidth;
    }

    public int getTextViewHeight() {
        return this.textViewHeight;
    }

    public void setTextViewHeight(int textViewHeight) {
        this.textViewHeight = textViewHeight;
    }

    public int getTextViewTopPadding() {
        return this.textViewTopPadding;
    }

    public void setTextViewTopPadding(int textViewTopPadding) {
        this.textViewTopPadding = textViewTopPadding;
    }

    public int getItemResource() {
        return this.itemResourceId;
    }

    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    public int getItemTextResource() {
        return this.itemTextResourceId;
    }

    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    public int getEmptyItemResource() {
        return this.emptyItemResourceId;
    }

    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    @Override // org.kankan.wheel.widget.adapters.WheelViewAdapter
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index < 0 || index >= getItemsCount()) {
            return null;
        }
        if (convertView == null) {
            convertView = getView(this.itemResourceId, parent);
        }
        TextView textView = getTextView(convertView, this.itemTextResourceId);
        if (textView != null) {
            CharSequence text = getItemText(index);
            if (text == null) {
                text = "";
            }
            textView.setText(text);
            if (this.itemResourceId == -1) {
                configureTextView(textView);
            }
        }
        return convertView;
    }

    @Override // org.kankan.wheel.widget.adapters.AbstractWheelAdapter, org.kankan.wheel.widget.adapters.WheelViewAdapter
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(this.emptyItemResourceId, parent);
        }
        if (this.emptyItemResourceId == -1 && (convertView instanceof TextView)) {
            configureTextView((TextView) convertView);
        }
        return convertView;
    }

    protected void configureTextView(TextView view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
        view.setTextColor(this.textColor);
        view.setGravity(17);
        view.setTextSize(this.textSize);
        view.setLines(1);
        view.setTypeface(Typeface.SANS_SERIF, 1);
        view.setWidth(this.textViewWidth);
        if (this.textViewHeight != 0) {
            view.setHeight(this.textViewHeight);
        }
        if (this.textViewTopPadding != 0) {
            view.setPadding(view.getPaddingLeft(), this.textViewTopPadding, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /* JADX WARN: Code duplicated, block: B:8:0x000c A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:9:0x000e A[Catch: ClassCastException -> 0x0015, TRY_LEAVE, TryCatch #0 {ClassCastException -> 0x0015, blocks: (B:4:0x0003, B:6:0x0007, B:9:0x000e), top: B:15:0x0003 }] */
    private TextView getTextView(View view, int textResource) {
        TextView text = null;
        if (textResource != 0) {
            if (textResource != 0) {
                text = (TextView) view.findViewById(textResource);
            }
        } else {
            try {
                if (view instanceof TextView) {
                    text = (TextView) view;
                } else if (textResource != 0) {
                    text = (TextView) view.findViewById(textResource);
                }
            } catch (ClassCastException e) {
                Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a TextView", e);
            }
        }
        return text;
    }

    private View getView(int resource, ViewGroup parent) {
        switch (resource) {
            case -1:
                return new TextView(this.context);
            case 0:
                return null;
            default:
                return this.inflater.inflate(resource, parent, false);
        }
    }
}

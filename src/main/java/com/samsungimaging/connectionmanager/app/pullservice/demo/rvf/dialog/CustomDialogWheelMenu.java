package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import org.kankan.wheel.widget.OnWheelClickedListener;
import org.kankan.wheel.widget.OnWheelScrollListener;
import org.kankan.wheel.widget.WheelView;
import org.kankan.wheel.widget.adapters.ArrayWheelAdapter;

/* JADX INFO: loaded from: classes.dex */
public class CustomDialogWheelMenu extends Dialog {
    int focusIndex;
    String[] items;
    int textViewHeight;
    int textViewTopPadding;
    int textViewWidth;
    String title;
    TextView titleView;
    TextView valueView;
    int visibleItems;
    OnWheelClickedListener wheelCliekedListener;
    OnWheelScrollListener wheelScrollListener;
    WheelView wheelView;

    public CustomDialogWheelMenu(Context context, String title, int visibleItems, int focusIndex, String[] items, int textViewWidth, OnWheelScrollListener wheelScrollListener, OnWheelClickedListener wheelCliekedListener) {
        super(context);
        this.textViewHeight = 0;
        this.textViewTopPadding = 0;
        this.items = items;
        this.wheelScrollListener = wheelScrollListener;
        this.wheelCliekedListener = wheelCliekedListener;
        this.title = title;
        this.visibleItems = visibleItems;
        this.focusIndex = focusIndex;
        this.textViewWidth = textViewWidth;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rvf_custom_dialog_wheel_menu);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.titleView = (TextView) findViewById(R.id.titleView);
        this.titleView.setText(this.title);
        this.valueView = (TextView) findViewById(R.id.valueView);
        this.valueView.setText(this.items[this.focusIndex]);
        this.wheelView = (WheelView) findViewById(R.id.rvf_wheelView);
        this.wheelView.setVisibleItems(5);
        updateWheelView(this.wheelView, this.items);
        this.wheelView.addScrollingListener(this.wheelScrollListener);
        this.wheelView.addClickingListener(this.wheelCliekedListener);
    }

    private void updateWheelView(WheelView wheelView, String[] items) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(getContext(), items);
        adapter.setTextSize(getContext().getResources().getInteger(R.integer.rvf_wheel_widget_text_view_text_size));
        adapter.setTextColor(-1);
        adapter.setTextViewWidth(this.textViewWidth);
        adapter.setTextViewHeight(this.textViewHeight);
        adapter.setTextViewTopPadding(this.textViewTopPadding);
        wheelView.setVisibleItems(this.visibleItems);
        wheelView.setViewAdapter(adapter);
        wheelView.setCurrentItem(this.focusIndex);
    }

    public void setCurrentItem(int index) {
        this.wheelView.setCurrentItem(index);
    }

    public void setValue(String value) {
        this.valueView.setText(value);
    }
}

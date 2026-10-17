package org.kankan.wheel.widget.adapters;

import android.content.Context;
import org.kankan.wheel.widget.WheelAdapter;

/* JADX INFO: loaded from: classes.dex */
public class AdapterWheel extends AbstractWheelTextAdapter {
    private WheelAdapter adapter;

    public AdapterWheel(Context context, WheelAdapter adapter) {
        super(context);
        this.adapter = adapter;
    }

    public WheelAdapter getAdapter() {
        return this.adapter;
    }

    @Override // org.kankan.wheel.widget.adapters.WheelViewAdapter
    public int getItemsCount() {
        return this.adapter.getItemsCount();
    }

    @Override // org.kankan.wheel.widget.adapters.AbstractWheelTextAdapter
    protected CharSequence getItemText(int index) {
        return this.adapter.getItem(index);
    }
}

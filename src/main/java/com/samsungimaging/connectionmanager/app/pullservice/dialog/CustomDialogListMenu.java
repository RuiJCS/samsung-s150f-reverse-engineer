package com.samsungimaging.connectionmanager.app.pullservice.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.pullservice.datatype.DSCMenuItem;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class CustomDialogListMenu extends Dialog {
    CustomDialogListMenuAdapter adapter;
    AdapterView.OnItemClickListener itemClickListener;
    ListView rvf_itemListView;

    public CustomDialogListMenu(Context context, ArrayList<DSCMenuItem> items, AdapterView.OnItemClickListener itemClickListener) {
        super(context);
        this.adapter = new CustomDialogListMenuAdapter(getContext(), R.layout.rvf_custom_dialog_list_menu_item, items);
        this.itemClickListener = itemClickListener;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rvf_custom_dialog_list_menu);
        this.rvf_itemListView = (ListView) findViewById(R.id.rvf_itemListView);
        this.rvf_itemListView.setAdapter((ListAdapter) this.adapter);
        this.rvf_itemListView.setOnItemClickListener(this.itemClickListener);
    }

    public void notifyDataSetChanged() {
        this.adapter.notifyDataSetChanged();
    }

    public void setEnabled(boolean enabled) {
        this.rvf_itemListView.setEnabled(enabled);
    }
}

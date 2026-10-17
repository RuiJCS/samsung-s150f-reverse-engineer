package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.pullservice.demo.rvf.Menu;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CustomDialogListMenuAdapter extends ArrayAdapter<Menu> {
    public CustomDialogListMenuAdapter(Context context, int resource, List<Menu> objects) {
        super(context, resource, objects);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rvf_custom_dialog_list_menu_item, (ViewGroup) null);
        }
        Menu item = getItem(position);
        ImageView rvf_menu_icon = (ImageView) convertView.findViewById(R.id.rvf_menu_icon);
        TextView rvf_menu_text = (TextView) convertView.findViewById(R.id.rvf_menu_text);
        ImageView rvf_menu_select = (ImageView) convertView.findViewById(R.id.rvf_menu_select);
        rvf_menu_icon.setImageResource(item.getIconResourceId());
        rvf_menu_text.setText(item.getName());
        if (item.isSelected()) {
            rvf_menu_select.setImageResource(R.drawable.rvf_check_on);
        } else {
            rvf_menu_select.setImageResource(R.drawable.rvf_check_off);
        }
        return convertView;
    }
}

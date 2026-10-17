package com.samsungimaging.connectionmanager.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.BaseGalleryActivity;
import com.samsungimaging.connectionmanager.manager.DatabaseManager;
import com.samsungimaging.connectionmanager.provider.DatabaseAP;

/* JADX INFO: loaded from: classes.dex */
public class PairedCameraDialog extends CustomDialog implements DialogInterface.OnClickListener {
    private Activity mActivity;

    private class InternalAdapter extends CursorAdapter {
        public InternalAdapter(Context context, Cursor cursor) {
            super(context, cursor, true);
        }

        @Override // android.support.v4.widget.CursorAdapter
        public void bindView(View view, Context context, Cursor cursor) {
            final DatabaseAP ap = DatabaseAP.builder(cursor);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setSelected(true);
            title.setText(ap.getSSID());
            ImageView button = (ImageView) view.findViewById(R.id.button);
            button.setImageResource(R.drawable.sca_popup_icon_delete);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.PairedCameraDialog.InternalAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PairedCameraDialog.this.delete(ap);
                }
            });
        }

        @Override // android.support.v4.widget.CursorAdapter
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return PairedCameraDialog.this.getLayoutInflater().inflate(R.layout.ap_list_item, (ViewGroup) null);
        }
    }

    public PairedCameraDialog(Activity activity) {
        super(activity);
        this.mActivity = null;
        this.mActivity = activity;
    }

    @Override // com.samsungimaging.connectionmanager.dialog.CustomDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        initContent();
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        ListAdapter adapter = new InternalAdapter(getContext(), DatabaseManager.fetchAllToCursorForAP(getContext()));
        TextView empty = (TextView) getLayoutInflater().inflate(R.layout.simple_list_empty_item, (ViewGroup) null);
        empty.setText(R.string.paired_camera_camera);
        setTitle(R.string.paired_cameras);
        setListEmptyView(empty);
        setAdapter(adapter, this);
        setNeutralButton(R.string.close, (DialogInterface.OnClickListener) null);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        ListView listView = ((CustomDialog) dialog).getListView();
        Cursor cursor = ((InternalAdapter) listView.getAdapter()).getCursor();
        cursor.moveToPosition(which);
        DatabaseAP ap = DatabaseAP.builder(cursor);
        delete(ap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delete(DatabaseAP ap) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseGalleryActivity.BUNDLE_KEY_DATA, ap);
        this.mActivity.showDialog(1, bundle);
    }
}

package com.samsungimaging.connectionmanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;

/* JADX INFO: loaded from: classes.dex */
public class CustomDialog extends AlertDialog implements DialogInterface.OnDismissListener {
    private ListAdapter mAdapter;
    private int mCheckedItem;
    private boolean[] mCheckedItems;
    private ChoiceMode mChoiceMode;
    private Cursor mCursor;
    private View mCustomTitleView;
    private Drawable mIcon;
    private View.OnClickListener mInternalButtonClickListener;
    private AdapterView.OnItemClickListener mInternalItemClickListener;
    private String mIsCheckedColumn;
    private CharSequence[] mItems;
    private String mLabelColumn;
    private DialogInterface.OnClickListener mListClickListener;
    private View mListEmptyView;
    private DialogInterface.OnMultiChoiceClickListener mListMultiChoiceClickListener;
    private ListView mListView;
    protected CharSequence mMessage;
    private TextView mMessageView;
    private DialogInterface.OnClickListener mNegativeButtonClickListener;
    private CharSequence mNegativeButtonText;
    private DialogInterface.OnClickListener mNeutralButtonClickListener;
    private CharSequence mNeutralButtonText;
    private DialogInterface.OnClickListener mPositiveButtonClickListener;
    private CharSequence mPositiveButtonText;
    private Object mTag;
    private CharSequence mTitle;
    private ImageView mTitleImageView;
    private LinearLayout mTitleLayout;
    private TextView mTitleTextView;
    protected View mView;

    private enum ChoiceMode {
        NONE,
        SINGLE,
        MULTI;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static ChoiceMode[] valuesCustom() {
            ChoiceMode[] choiceModeArrValuesCustom = values();
            int length = choiceModeArrValuesCustom.length;
            ChoiceMode[] choiceModeArr = new ChoiceMode[length];
            System.arraycopy(choiceModeArrValuesCustom, 0, choiceModeArr, 0, length);
            return choiceModeArr;
        }
    }

    private class InternalAdapter extends ArrayAdapter<CharSequence> {
        public InternalAdapter(Context context) {
            super(context, R.layout.simple_list_item_multi_choice, CustomDialog.this.mItems);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            boolean isChecked = CustomDialog.this.mCheckedItems[position];
            if (isChecked) {
                CustomDialog.this.mListView.setItemChecked(position, true);
            }
            return view;
        }
    }

    private class InternalCursorAdapter extends CursorAdapter {
        public InternalCursorAdapter(Context context) {
            super(context, CustomDialog.this.mCursor);
        }

        @Override // android.widget.CursorAdapter
        public void bindView(View view, Context context, Cursor cursor) {
            CheckedTextView text = (CheckedTextView) view;
            text.setText(cursor.getString(cursor.getColumnIndex(CustomDialog.this.mLabelColumn)));
            boolean isChecked = cursor.getInt(cursor.getColumnIndex(CustomDialog.this.mIsCheckedColumn)) == 1;
            CustomDialog.this.mListView.setItemChecked(cursor.getPosition(), isChecked);
        }

        @Override // android.widget.CursorAdapter
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return CustomDialog.this.getLayoutInflater().inflate(R.layout.simple_list_item_multi_choice, parent, false);
        }
    }

    public CustomDialog(Context context) {
        super(context);
        this.mChoiceMode = ChoiceMode.NONE;
        this.mIcon = null;
        this.mTitle = null;
        this.mCustomTitleView = null;
        this.mMessage = null;
        this.mView = null;
        this.mListView = null;
        this.mListEmptyView = null;
        this.mMessageView = null;
        this.mTitleLayout = null;
        this.mTitleImageView = null;
        this.mTitleTextView = null;
        this.mItems = null;
        this.mAdapter = null;
        this.mCursor = null;
        this.mIsCheckedColumn = null;
        this.mLabelColumn = null;
        this.mCheckedItem = 0;
        this.mCheckedItems = null;
        this.mListClickListener = null;
        this.mListMultiChoiceClickListener = null;
        this.mPositiveButtonText = null;
        this.mPositiveButtonClickListener = null;
        this.mNegativeButtonText = null;
        this.mNegativeButtonClickListener = null;
        this.mNeutralButtonText = null;
        this.mNeutralButtonClickListener = null;
        this.mTag = null;
        this.mInternalButtonClickListener = new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CustomDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int which = ((Integer) v.getTag()).intValue();
                switch (which) {
                    case -3:
                        if (CustomDialog.this.mNeutralButtonClickListener != null) {
                            CustomDialog.this.mNeutralButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    case -2:
                        if (CustomDialog.this.mNegativeButtonClickListener != null) {
                            CustomDialog.this.mNegativeButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    case -1:
                        if (CustomDialog.this.mPositiveButtonClickListener != null) {
                            CustomDialog.this.mPositiveButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    default:
                        throw new IllegalArgumentException("Unknown which " + which);
                }
            }
        };
        this.mInternalItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CustomDialog.2
            private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode;

            static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode() {
                int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode;
                if (iArr == null) {
                    iArr = new int[ChoiceMode.valuesCustom().length];
                    try {
                        iArr[ChoiceMode.MULTI.ordinal()] = 3;
                    } catch (NoSuchFieldError e) {
                    }
                    try {
                        iArr[ChoiceMode.NONE.ordinal()] = 1;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[ChoiceMode.SINGLE.ordinal()] = 2;
                    } catch (NoSuchFieldError e3) {
                    }
                    $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode = iArr;
                }
                return iArr;
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode()[CustomDialog.this.mChoiceMode.ordinal()]) {
                    case 1:
                    case 2:
                        if (CustomDialog.this.mListClickListener != null) {
                            CustomDialog.this.mListClickListener.onClick(CustomDialog.this, position);
                        }
                        break;
                    case 3:
                        if (CustomDialog.this.mListMultiChoiceClickListener != null) {
                            CustomDialog.this.mListMultiChoiceClickListener.onClick(CustomDialog.this, position, CustomDialog.this.mListView.isItemChecked(position));
                        }
                        break;
                }
            }
        };
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        this.mChoiceMode = ChoiceMode.NONE;
        this.mIcon = null;
        this.mTitle = null;
        this.mCustomTitleView = null;
        this.mMessage = null;
        this.mView = null;
        this.mListView = null;
        this.mListEmptyView = null;
        this.mMessageView = null;
        this.mTitleLayout = null;
        this.mTitleImageView = null;
        this.mTitleTextView = null;
        this.mItems = null;
        this.mAdapter = null;
        this.mCursor = null;
        this.mIsCheckedColumn = null;
        this.mLabelColumn = null;
        this.mCheckedItem = 0;
        this.mCheckedItems = null;
        this.mListClickListener = null;
        this.mListMultiChoiceClickListener = null;
        this.mPositiveButtonText = null;
        this.mPositiveButtonClickListener = null;
        this.mNegativeButtonText = null;
        this.mNegativeButtonClickListener = null;
        this.mNeutralButtonText = null;
        this.mNeutralButtonClickListener = null;
        this.mTag = null;
        this.mInternalButtonClickListener = new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CustomDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int which = ((Integer) v.getTag()).intValue();
                switch (which) {
                    case -3:
                        if (CustomDialog.this.mNeutralButtonClickListener != null) {
                            CustomDialog.this.mNeutralButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    case -2:
                        if (CustomDialog.this.mNegativeButtonClickListener != null) {
                            CustomDialog.this.mNegativeButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    case -1:
                        if (CustomDialog.this.mPositiveButtonClickListener != null) {
                            CustomDialog.this.mPositiveButtonClickListener.onClick(CustomDialog.this, which);
                            return;
                        } else {
                            CustomDialog.this.dismiss();
                            return;
                        }
                    default:
                        throw new IllegalArgumentException("Unknown which " + which);
                }
            }
        };
        this.mInternalItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CustomDialog.2
            private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode;

            static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode() {
                int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode;
                if (iArr == null) {
                    iArr = new int[ChoiceMode.valuesCustom().length];
                    try {
                        iArr[ChoiceMode.MULTI.ordinal()] = 3;
                    } catch (NoSuchFieldError e) {
                    }
                    try {
                        iArr[ChoiceMode.NONE.ordinal()] = 1;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[ChoiceMode.SINGLE.ordinal()] = 2;
                    } catch (NoSuchFieldError e3) {
                    }
                    $SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode = iArr;
                }
                return iArr;
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$dialog$CustomDialog$ChoiceMode()[CustomDialog.this.mChoiceMode.ordinal()]) {
                    case 1:
                    case 2:
                        if (CustomDialog.this.mListClickListener != null) {
                            CustomDialog.this.mListClickListener.onClick(CustomDialog.this, position);
                        }
                        break;
                    case 3:
                        if (CustomDialog.this.mListMultiChoiceClickListener != null) {
                            CustomDialog.this.mListMultiChoiceClickListener.onClick(CustomDialog.this, position, CustomDialog.this.mListView.isItemChecked(position));
                        }
                        break;
                }
            }
        };
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setOnDismissListener(this);
        initialize();
    }

    protected final void initialize() {
        setContentView(R.layout.dialog_custom);
        boolean titleLayoutVisible = initTitleLayout();
        boolean messageLayoutVisible = initMessageLayout();
        boolean buttonLayoutVisible = initButtonLayout();
        if (messageLayoutVisible) {
            ViewGroup messageBackgroundLayout = (ViewGroup) findViewById(R.id.message_background_layout);
            if (titleLayoutVisible && buttonLayoutVisible) {
                messageBackgroundLayout.setBackgroundResource(R.drawable.tw_dialog_middle_holo_dark);
                return;
            }
            if (!titleLayoutVisible && !buttonLayoutVisible) {
                messageBackgroundLayout.setBackgroundResource(R.drawable.tw_dialog_full_holo_dark);
            } else if (!titleLayoutVisible) {
                messageBackgroundLayout.setBackgroundResource(R.drawable.tw_dialog_top_holo_dark);
            } else {
                messageBackgroundLayout.setBackgroundResource(R.drawable.tw_dialog_bottom_holo_dark);
            }
        }
    }

    private boolean initTitleLayout() {
        boolean isVisible = false;
        this.mTitleLayout = (LinearLayout) findViewById(R.id.title_layout);
        this.mTitleImageView = (ImageView) findViewById(R.id.icon);
        this.mTitleTextView = (TextView) findViewById(R.id.title);
        if (this.mCustomTitleView != null) {
            this.mTitleLayout.addView(this.mCustomTitleView);
            isVisible = true;
        } else {
            if (this.mIcon != null) {
                this.mTitleImageView.setImageDrawable(this.mIcon);
                this.mTitleImageView.setVisibility(0);
                isVisible = true;
            }
            if (this.mTitle != null) {
                this.mTitleTextView.setText(this.mTitle);
                this.mTitleTextView.setVisibility(0);
                isVisible = true;
            }
        }
        if (isVisible) {
            this.mTitleLayout.setVisibility(0);
        }
        return isVisible;
    }

    private boolean initMessageLayout() {
        boolean isVisible = false;
        ViewGroup messageBackgroundLayout = (ViewGroup) findViewById(R.id.message_background_layout);
        LinearLayout messageLayout = (LinearLayout) findViewById(R.id.message_layout);
        if (this.mChoiceMode == ChoiceMode.MULTI) {
            if (this.mItems != null) {
                this.mAdapter = new InternalAdapter(getContext());
            } else if (this.mCursor != null) {
                this.mAdapter = new InternalCursorAdapter(getContext());
            }
        } else {
            int layout = this.mChoiceMode == ChoiceMode.SINGLE ? R.layout.simple_list_item_single_choice : R.layout.simple_list_item;
            if (this.mItems != null) {
                this.mAdapter = new ArrayAdapter(getContext(), layout, this.mItems);
            } else if (this.mCursor != null) {
                this.mAdapter = new SimpleCursorAdapter(getContext(), layout, this.mCursor, new String[]{this.mLabelColumn}, new int[]{android.R.id.text1});
            }
        }
        this.mListView = (ListView) findViewById(R.id.list_view);
        if (this.mAdapter == null) {
            this.mListView.setVisibility(8);
        } else {
            SharedPreferences mPreferences = getContext().getSharedPreferences("configuration", 0);
            boolean firstTimeAccount = mPreferences.getBoolean("firstTimeAccount", true);
            if (!firstTimeAccount) {
                this.mListView = (ListView) findViewById(R.id.list_view_for_permission);
                this.mListView.setAdapter(this.mAdapter);
            } else {
                this.mListView.setAdapter(this.mAdapter);
            }
            this.mListView.setOnItemClickListener(this.mInternalItemClickListener);
            if (this.mChoiceMode == ChoiceMode.SINGLE) {
                this.mListView.setChoiceMode(1);
                this.mListView.setItemChecked(this.mCheckedItem, true);
                this.mListView.setSelection(this.mCheckedItem);
            } else if (this.mChoiceMode == ChoiceMode.MULTI) {
                this.mListView.setChoiceMode(2);
            }
            if (this.mListEmptyView != null) {
                this.mListView.setEmptyView(this.mListEmptyView);
                messageBackgroundLayout.addView(this.mListEmptyView);
            }
            isVisible = true;
        }
        if (this.mMessage != null) {
            this.mMessageView = (TextView) findViewById(R.id.message);
            this.mMessageView.setText(this.mMessage);
            this.mMessageView.setVisibility(0);
            isVisible = true;
        }
        if (this.mView != null) {
            messageLayout.addView(this.mView);
            isVisible = true;
        }
        if (isVisible) {
            messageBackgroundLayout.setVisibility(0);
        }
        return isVisible;
    }

    private boolean initButtonLayout() {
        boolean isVisible = false;
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.custom_dialog_button_margin);
        if (this.mPositiveButtonText != null) {
            Button positiveButton = (Button) findViewById(R.id.positive_button);
            positiveButton.setTag(-1);
            positiveButton.setText(this.mPositiveButtonText);
            positiveButton.setOnClickListener(this.mInternalButtonClickListener);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
            params.setMargins(margin, margin, margin, margin);
            positiveButton.setLayoutParams(params);
            positiveButton.setVisibility(0);
            isVisible = true;
        }
        if (this.mNeutralButtonText != null) {
            Button neutralButton = (Button) findViewById(R.id.neutral_button);
            neutralButton.setTag(-3);
            neutralButton.setText(this.mNeutralButtonText);
            neutralButton.setOnClickListener(this.mInternalButtonClickListener);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) neutralButton.getLayoutParams();
            if (this.mPositiveButtonText == null) {
                params2.setMargins(margin, margin, margin, margin);
            } else {
                params2.setMargins(0, margin, margin, margin);
            }
            neutralButton.setLayoutParams(params2);
            neutralButton.setVisibility(0);
            isVisible = true;
        }
        if (this.mNegativeButtonText != null) {
            Button negativeButton = (Button) findViewById(R.id.negative_button);
            negativeButton.setTag(-2);
            negativeButton.setText(this.mNegativeButtonText);
            negativeButton.setOnClickListener(this.mInternalButtonClickListener);
            LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
            if (this.mPositiveButtonText == null && this.mNeutralButtonText == null) {
                params3.setMargins(margin, margin, margin, margin);
            } else {
                params3.setMargins(0, margin, margin, margin);
            }
            negativeButton.setLayoutParams(params3);
            negativeButton.setVisibility(0);
            isVisible = true;
        }
        if (isVisible) {
            buttonLayout.setVisibility(0);
        }
        return isVisible;
    }

    private void refreshTitleLayout() {
        if (this.mCustomTitleView == null && this.mTitleLayout != null) {
            boolean isVisible = false;
            if (this.mIcon != null && this.mTitleImageView != null) {
                this.mTitleImageView.setImageDrawable(this.mIcon);
                this.mTitleImageView.setVisibility(0);
                isVisible = true;
            }
            if (this.mTitle != null && this.mTitleTextView != null) {
                this.mTitleTextView.setText(this.mTitle);
                this.mTitleTextView.setVisibility(0);
                isVisible = true;
            }
            if (isVisible) {
                this.mTitleLayout.setVisibility(0);
            }
        }
    }

    @Override // android.app.Dialog
    protected void onStart() {
        if (this.mAdapter != null && this.mAdapter.getCount() > 0) {
            if (this.mListEmptyView != null && this.mListEmptyView.getVisibility() == 0) {
                this.mListView.setVisibility(0);
                this.mListEmptyView.setVisibility(8);
            }
        } else if (this.mListEmptyView != null && this.mListEmptyView.getVisibility() == 8) {
            this.mListView.setVisibility(8);
            this.mListEmptyView.setVisibility(0);
        }
        super.onStart();
    }

    @Override // android.app.AlertDialog
    public void setIcon(int resID) {
        this.mIcon = getContext().getResources().getDrawable(resID);
    }

    @Override // android.app.AlertDialog
    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    @Override // android.app.Dialog
    public void setTitle(int titleID) {
        this.mTitle = getContext().getText(titleID);
        refreshTitleLayout();
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    public void setTitle(CharSequence title) {
        this.mTitle = title;
        refreshTitleLayout();
    }

    public void setCustomTitle(int layoutResID) {
        this.mCustomTitleView = getLayoutInflater().inflate(layoutResID, (ViewGroup) null);
    }

    @Override // android.app.AlertDialog
    public void setCustomTitle(View view) {
        this.mCustomTitleView = view;
    }

    public void setMessage(int messageID) {
        this.mMessage = getContext().getText(messageID);
    }

    @Override // android.app.AlertDialog
    public void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public void setItems(int itemsId, DialogInterface.OnClickListener listener) {
        this.mItems = getContext().getResources().getTextArray(itemsId);
        this.mListClickListener = listener;
        this.mChoiceMode = ChoiceMode.NONE;
    }

    public void setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        this.mItems = items;
        this.mListClickListener = listener;
        this.mChoiceMode = ChoiceMode.NONE;
    }

    public void setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
        this.mAdapter = adapter;
        this.mListClickListener = listener;
    }

    public void setCursor(Cursor cursor, String labelColumn, DialogInterface.OnClickListener listener) {
        this.mCursor = cursor;
        this.mLabelColumn = labelColumn;
        this.mListClickListener = listener;
    }

    public void setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
        this.mItems = getContext().getResources().getTextArray(itemsId);
        this.mListClickListener = listener;
        this.mCheckedItem = checkedItem;
        this.mChoiceMode = ChoiceMode.SINGLE;
    }

    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
        this.mItems = items;
        this.mCheckedItem = checkedItem;
        this.mListClickListener = listener;
        this.mChoiceMode = ChoiceMode.SINGLE;
    }

    public void setSingleChoiceItems(ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) {
        this.mAdapter = adapter;
        this.mCheckedItem = checkedItem;
        this.mListClickListener = listener;
        this.mChoiceMode = ChoiceMode.SINGLE;
    }

    public void setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, DialogInterface.OnClickListener listener) {
        this.mCursor = cursor;
        this.mCheckedItem = checkedItem;
        this.mLabelColumn = labelColumn;
        this.mListClickListener = listener;
        this.mChoiceMode = ChoiceMode.SINGLE;
    }

    public void setMultiChoiceItems(int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
        this.mItems = getContext().getResources().getTextArray(itemsId);
        this.mCheckedItems = checkedItems;
        this.mListMultiChoiceClickListener = listener;
        this.mChoiceMode = ChoiceMode.MULTI;
    }

    public void setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
        this.mItems = items;
        this.mCheckedItems = checkedItems;
        this.mListMultiChoiceClickListener = listener;
        this.mChoiceMode = ChoiceMode.MULTI;
    }

    public void setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn, DialogInterface.OnMultiChoiceClickListener listener) {
        this.mCursor = cursor;
        this.mIsCheckedColumn = isCheckedColumn;
        this.mLabelColumn = labelColumn;
        this.mListMultiChoiceClickListener = listener;
        this.mChoiceMode = ChoiceMode.MULTI;
    }

    public void setListEmptyView(int layoutResID) {
        this.mListEmptyView = getLayoutInflater().inflate(layoutResID, (ViewGroup) null);
    }

    public void setListEmptyView(View view) {
        this.mListEmptyView = view;
    }

    @Override // android.app.AlertDialog
    public ListView getListView() {
        return this.mListView;
    }

    public void setView(int layoutResID) {
        this.mView = getLayoutInflater().inflate(layoutResID, (ViewGroup) null);
    }

    @Override // android.app.AlertDialog
    public void setView(View view) {
        this.mView = view;
    }

    public void setPositiveButton(int textID, DialogInterface.OnClickListener listener) {
        this.mPositiveButtonText = getContext().getText(textID);
        this.mPositiveButtonClickListener = listener;
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.mPositiveButtonText = text;
        this.mPositiveButtonClickListener = listener;
    }

    public void setNeutralButton(int textID, DialogInterface.OnClickListener listener) {
        this.mNeutralButtonText = getContext().getText(textID);
        this.mNeutralButtonClickListener = listener;
    }

    public void setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.mNeutralButtonText = text;
        this.mNeutralButtonClickListener = listener;
    }

    public void setNegativeButton(int textID, DialogInterface.OnClickListener listener) {
        this.mNegativeButtonText = getContext().getText(textID);
        this.mNegativeButtonClickListener = listener;
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.mNegativeButtonText = text;
        this.mNegativeButtonClickListener = listener;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return this.mTag;
    }

    public void onOrientationChanged() {
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }
}

package com.samsungimaging.connectionmanager.util;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.samsungimaging.connectionmanager.gallery.GalleryFragment;
import com.samsungimaging.connectionmanager.provider.GalleryColumns;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    public static final String ACTION_DISCONNECT = "action_disconnect";

    public static String[] getMediaGroupQueryProjection() {
        return new String[]{GalleryColumns.KEY_ID, "date((datetaken / 1000), 'unixepoch', 'localtime') as datetaken_string", GalleryColumns.KEY_MEDIA_TYPE, "count(*) as _count"};
    }

    public static String getLocalMediaQuerySelection() {
        return "_data LIKE ? AND (media_type=1 OR media_type=3)";
    }

    public static String getCameraMediaQuerySelection() {
        return "(media_type=1 OR media_type=3)";
    }

    public static void unbindView(View view) {
        ImageView imageView;
        Drawable d;
        if (view != null) {
            try {
                view.setOnClickListener(null);
            } catch (Exception e) {
            }
            try {
                view.setOnCreateContextMenuListener(null);
            } catch (Exception e2) {
            }
            try {
                view.setOnFocusChangeListener(null);
            } catch (Exception e3) {
            }
            try {
                view.setOnKeyListener(null);
            } catch (Exception e4) {
            }
            try {
                view.setOnLongClickListener(null);
            } catch (Exception e5) {
            }
            try {
                view.setOnTouchListener(null);
            } catch (Exception e6) {
            }
            Drawable d2 = view.getBackground();
            if (d2 != null) {
                d2.setCallback(null);
                view.setBackgroundDrawable(null);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                viewGroup.setOnHierarchyChangeListener(null);
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    unbindView(viewGroup.getChildAt(i));
                }
                if (view instanceof AdapterView) {
                    AdapterView<?> adapterView = (AdapterView) view;
                    adapterView.setOnItemClickListener(null);
                    adapterView.setOnItemLongClickListener(null);
                    adapterView.setOnItemSelectedListener(null);
                } else {
                    viewGroup.removeAllViews();
                }
            } else if ((view instanceof ImageView) && (d = (imageView = (ImageView) view).getDrawable()) != null) {
                d.setCallback(null);
                imageView.setImageDrawable(null);
            }
        }
    }

    public static Fragment findFragment(Activity activity, Class<?> cls) {
        FragmentManager manager = activity.getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(cls.getSimpleName());
        FragmentTransaction ft = manager.beginTransaction();
        if (fragment == null) {
            if (cls == GalleryFragment.class) {
                fragment = new GalleryFragment();
            }
            ft.add(R.id.content, fragment, cls.getSimpleName());
        } else if (fragment.isHidden()) {
            ft.show(fragment);
        }
        ft.commit();
        return fragment;
    }

    public static void showFragment(Activity activity, Fragment fragment) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        if (fragment.isAdded() || fragment.isHidden()) {
            ft.show(fragment);
        }
        ft.commit();
    }

    public static void hideFragment(Activity activity, Fragment fragment) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        if (fragment.isAdded() || fragment.isVisible()) {
            ft.hide(fragment);
        }
        ft.commit();
    }

    @SuppressLint({"DefaultLocale"})
    public static String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath.toLowerCase());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}

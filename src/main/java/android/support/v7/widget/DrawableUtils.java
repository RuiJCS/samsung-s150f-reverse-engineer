package android.support.v7.widget;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static final String TAG = "DrawableUtils";
    private static Class<?> sInsetsClazz;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                sInsetsClazz = Class.forName("android.graphics.Insets");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    private DrawableUtils() {
    }

    public static Rect getOpticalBounds(Drawable drawable) {
        if (sInsetsClazz != null) {
            try {
                Drawable drawable2 = DrawableCompat.unwrap(drawable);
                Method getOpticalInsetsMethod = drawable2.getClass().getMethod("getOpticalInsets", new Class[0]);
                Object insets = getOpticalInsetsMethod.invoke(drawable2, new Object[0]);
                if (insets != null) {
                    Rect result = new Rect();
                    Field[] arr$ = sInsetsClazz.getFields();
                    for (Field field : arr$) {
                        switch (field.getName()) {
                            case "left":
                                result.left = field.getInt(insets);
                                break;
                            case "top":
                                result.top = field.getInt(insets);
                                break;
                            case "right":
                                result.right = field.getInt(insets);
                                break;
                            case "bottom":
                                result.bottom = field.getInt(insets);
                                break;
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                Log.e(TAG, "Couldn't obtain the optical insets. Ignoring.");
            }
        }
        return INSETS_NONE;
    }
}

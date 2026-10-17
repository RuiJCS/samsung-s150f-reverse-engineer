package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.text.AllCapsTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
class AppCompatTextHelper {
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableTopTint;
    final TextView mView;
    private static final int[] VIEW_ATTRS = {R.attr.textAppearance, R.attr.drawableLeft, R.attr.drawableTop, R.attr.drawableRight, R.attr.drawableBottom};
    private static final int[] TEXT_APPEARANCE_ATTRS = {android.support.v7.appcompat.R.attr.textAllCaps};

    static AppCompatTextHelper create(TextView textView) {
        return Build.VERSION.SDK_INT >= 17 ? new AppCompatTextHelperV17(textView) : new AppCompatTextHelper(textView);
    }

    AppCompatTextHelper(TextView view) {
        this.mView = view;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        Context context = this.mView.getContext();
        TintManager tintManager = TintManager.get(context);
        TypedArray a = context.obtainStyledAttributes(attrs, VIEW_ATTRS, defStyleAttr, 0);
        int ap = a.getResourceId(0, -1);
        if (a.hasValue(1)) {
            this.mDrawableLeftTint = createTintInfo(context, tintManager, a.getResourceId(1, 0));
        }
        if (a.hasValue(2)) {
            this.mDrawableTopTint = createTintInfo(context, tintManager, a.getResourceId(2, 0));
        }
        if (a.hasValue(3)) {
            this.mDrawableRightTint = createTintInfo(context, tintManager, a.getResourceId(3, 0));
        }
        if (a.hasValue(4)) {
            this.mDrawableBottomTint = createTintInfo(context, tintManager, a.getResourceId(4, 0));
        }
        a.recycle();
        if (ap != -1) {
            TypedArray appearance = context.obtainStyledAttributes(ap, android.support.v7.appcompat.R.styleable.TextAppearance);
            if (appearance.hasValue(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(appearance.getBoolean(android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps, false));
            }
            appearance.recycle();
        }
        TypedArray a2 = context.obtainStyledAttributes(attrs, TEXT_APPEARANCE_ATTRS, defStyleAttr, 0);
        if (a2.getBoolean(0, false)) {
            setAllCaps(true);
        }
        a2.recycle();
    }

    void onSetTextAppearance(Context context, int resId) {
        TypedArray appearance = context.obtainStyledAttributes(resId, TEXT_APPEARANCE_ATTRS);
        if (appearance.hasValue(0)) {
            setAllCaps(appearance.getBoolean(0, false));
        }
        appearance.recycle();
    }

    void setAllCaps(boolean allCaps) {
        this.mView.setTransformationMethod(allCaps ? new AllCapsTransformationMethod(this.mView.getContext()) : null);
    }

    void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint != null || this.mDrawableTopTint != null || this.mDrawableRightTint != null || this.mDrawableBottomTint != null) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
    }

    final void applyCompoundDrawableTint(Drawable drawable, TintInfo info) {
        if (drawable != null && info != null) {
            TintManager.tintDrawable(drawable, info, this.mView.getDrawableState());
        }
    }

    protected static TintInfo createTintInfo(Context context, TintManager tintManager, int drawableId) {
        ColorStateList tintList = tintManager.getTintList(drawableId);
        if (tintList != null) {
            TintInfo tintInfo = new TintInfo();
            tintInfo.mHasTintList = true;
            tintInfo.mTintList = tintList;
            return null;
        }
        return null;
    }
}

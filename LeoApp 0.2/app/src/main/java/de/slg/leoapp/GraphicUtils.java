package de.slg.leoapp;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public abstract class GraphicUtils {

    public static float dpToPx(float dp) {
        Resources r = Utils.context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static double pxToDp(float px) {
        Resources r = Utils.context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, r.getDisplayMetrics());
    }

    public static int getDisplayHeight() {
        DisplayMetrics dm = Utils.context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getDisplayWidth() {
        DisplayMetrics dm = Utils.context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}

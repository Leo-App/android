package de.slg.leoapp;


import android.content.Context;
import android.content.res.Resources;
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

}

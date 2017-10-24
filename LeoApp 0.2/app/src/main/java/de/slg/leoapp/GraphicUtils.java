package de.slg.leoapp;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * GraphicUtils
 *
 * Diese abstrakte Klasse ist das Grafik-Gegenstück zu {@link Utils} und bietet eine Ressource für Grafikmethoden, die aus dem gesamten Projekt erreichbar ist.
 *
 * @author Gianni
 * @since 0.0.1
 * @version 2017.2310
 *
 */
public abstract class GraphicUtils {

    /**
     * Kovertiert auflösungsabhängige dp Werte zu absoluten px Werten für das aktuelle Endgerät.
     *
     * @param dp dp-Wert
     * @return Den entsprechenden px-Wert
     * @see #pxToDp(float)
     */
    public static float dpToPx(float dp) {
        Resources r = Utils.getContext().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    /**
     * Kovertiert absolute px Werte zu auflösungsabhängigen dp Werten für das aktuelle Endgerät.
     *
     * @param px px-Wert
     * @return Den entsprechenden dp-Wert
     * @see #dpToPx(float)
     */
    public static double pxToDp(float px) {
        Resources r = Utils.getContext().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, r.getDisplayMetrics());
    }

    /**
     * Gibt die Viewport-Höhe in Pixeln zurück
     *
     * @return Displayhöhe in px
     */
    public static int getDisplayHeight() {
        DisplayMetrics dm = Utils.getContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * Gibt die Viewport-Breite in Pixeln zurück
     *
     * @return Displaybreite in px
     */
    public static int getDisplayWidth() {
        DisplayMetrics dm = Utils.getContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}

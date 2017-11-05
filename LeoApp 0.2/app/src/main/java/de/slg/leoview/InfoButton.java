package de.slg.leoview;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.TextView;

import de.slg.leoapp.BottomSheetDialog;
import de.slg.leoapp.Utils;

/**
 * InfoButton
 * <p>
 * Decorator zu android.widget.Button, ergänzt entsprechende Buttons um eine Tooltip-Funktion mithilfe von {@link BottomSheetDialog BottomSheetDialogen}. Sollten benötigte
 * Button-Funktionen fehlen, bitte ergänzen.
 *
 * @author Gianni
 * @version 2017.2610
 * @since 0.5.7
 */
@SuppressWarnings("unused")
public class InfoButton implements Drawable.Callback, KeyEvent.Callback {

    private Button b;
    private boolean tooltipEnabled;

    public InfoButton(Button b) {
        this.b = b;
    }

    /**
     * Liefert eine String-Repräsentation des InfoButtons
     *
     * @return InfoButton im Stringformat
     */
    public String toString() {
        return b.toString();
    }

    /**
     * Fügt einen Tooltip zu einem Button hinzu.
     *
     * @param residTitle Titel des Tooltips als Stringressource
     * @param residContent Beschreibung des Tooltips als Stringressource
     */
    public void addTooltip(@StringRes final int residTitle, @StringRes final int residContent) {
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog();
                dialog.setTitle(Utils.getString(residTitle)).setContent(Utils.getString(residContent)).show(Utils.getController().getActiveActivity().getSupportFragmentManager(), dialog.getTag());
                return false;
            }
        });
    }

    /**
     * Fügt einen Tooltip zu einem Button hinzu.
     *
     * @param text Titel des Tooltips
     * @param content Beschreibung des Tooltips
     */
    public void addTooltip(final String text, final String content) {
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog();
                dialog.setTitle(text).setContent(content).show(Utils.getController().getActiveActivity().getSupportFragmentManager(), dialog.getTag());
                return false;
            }
        });
    }

    /**
     * Setzt, ob ein Tooltip angezeigt wird.
     *
     * @param enabled Tooltip anzeigen
     */
    public void setTooltipEnabled(boolean enabled) {
        tooltipEnabled = enabled;
    }

    /**
     * Gibt zurück, ob Tooltips zurzeit aktiviert sind.
     *
     * @return Tooltip aktiviert?
     */
    public boolean isTooltipEnabled() {
        return tooltipEnabled;
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param listener siehe {@link Button}
     */
    public void addOnLayoutChangeListener(View.OnLayoutChangeListener listener) {
        b.addOnLayoutChangeListener(listener);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param listener siehe {@link Button}
     */
    public void removeOnLayoutChangeListener(View.OnLayoutChangeListener listener) {
        b.removeOnLayoutChangeListener(listener);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param listener siehe {@link Button}
     */
    public void addOnAttachStateChangeListener(View.OnAttachStateChangeListener listener) {
        b.addOnAttachStateChangeListener(listener);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param listener siehe {@link Button}
     */
    public void removeOnAttachStateChangeListener(View.OnAttachStateChangeListener listener) {
        b.removeOnAttachStateChangeListener(listener);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return b.getOnFocusChangeListener();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnFocusChangeListener(View.OnFocusChangeListener l) {
        b.setOnFocusChangeListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnClickListener(@Nullable android.view.View.OnClickListener l) {
        b.setOnClickListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean hasOnClickListeners() {
        return b.hasOnClickListeners();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnLongClickListener(@Nullable View.OnLongClickListener l) {
        b.setOnLongClickListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean performClick() {
        return b.performClick();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean callOnClick() {
        return b.callOnClick();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean performLongClick() {
        return b.performLongClick();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean showContextMenu() {
        return b.showContextMenu();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnKeyListener(View.OnKeyListener l) {
        b.setOnKeyListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnTouchListener(View.OnTouchListener l) {
        b.setOnTouchListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnGenericMotionListener(View.OnGenericMotionListener l) {
        b.setOnGenericMotionListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnHoverListener(View.OnHoverListener l) {
        b.setOnHoverListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param l siehe {@link Button}
     */
    public void setOnDragListener(View.OnDragListener l) {
        b.setOnDragListener(l);
    }

    /**
     * Delegiert von android.widget.Button
     */
    public void clearFocus() {
        b.clearFocus();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean hasFocus() {
        return b.hasFocus();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean hasFocusable() {
        return b.hasFocusable();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param eventType siehe {@link Button}
     */
    public void sendAccessibilityEvent(int eventType) {
        b.sendAccessibilityEvent(eventType);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param event siehe {@link Button}
     */
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        b.sendAccessibilityEventUnchecked(event);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean isFocused() {
        return b.isFocused();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public View findFocus() {
        return b.findFocus();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public int getVisibility() {
        return b.getVisibility();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param visibility siehe {@link Button}
     */
    public void setVisibility(int visibility) {
        b.setVisibility(visibility);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean isEnabled() {
        return b.isEnabled();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param enabled siehe {@link Button}
     */
    public void setEnabled(boolean enabled) {
        b.setEnabled(enabled);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param clickable siehe {@link Button}
     */
    public void setClickable(boolean clickable) {
        b.setClickable(clickable);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean isLongClickable() {
        return b.isLongClickable();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param longClickable siehe {@link Button}
     */
    public void setLongClickable(boolean longClickable) {
        b.setLongClickable(longClickable);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean isPressed() {
        return b.isPressed();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param pressed siehe {@link Button}
     */
    public void setPressed(boolean pressed) {
        b.setPressed(pressed);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean isSaveEnabled() {
        return b.isSaveEnabled();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean requestFocus() {
        return b.requestFocus();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param direction siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean requestFocus(int direction) {
        return b.requestFocus(direction);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param direction             siehe {@link Button}
     * @param previouslyFocusedRect siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return b.requestFocus(direction, previouslyFocusedRect);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public boolean requestFocusFromTouch() {
        return b.requestFocusFromTouch();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param keyCode siehe {@link Button}
     * @param event   siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return b.onKeyLongPress(keyCode, event);
    }

    /**
     * Delegiert von android.widget.Button
     */
    public void cancelLongPress() {
        b.cancelLongPress();
    }

    /**
     * Delegiert von android.widget.Button
     */
    public void bringToFront() {
        b.bringToFront();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public ViewParent getParent() {
        return b.getParent();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public int getWidth() {
        return b.getWidth();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public int getHeight() {
        return b.getHeight();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param who  siehe {@link Button}
     * @param what siehe {@link Button}
     * @param when siehe {@link Button}
     */
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        b.scheduleDrawable(who, what, when);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param who  siehe {@link Button}
     * @param what siehe {@link Button}
     */
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        b.unscheduleDrawable(who, what);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public Object getTag() {
        return b.getTag();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param tag siehe {@link Button}
     */
    public void setTag(Object tag) {
        b.setTag(tag);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param key siehe {@link Button}
     * @return siehe {@link Button}
     */
    public Object getTag(int key) {
        return b.getTag(key);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param key siehe {@link Button}
     * @param tag siehe {@link Button}
     */
    public void setTag(int key, Object tag) {
        b.setTag(key, tag);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param text siehe {@link Button}
     */
    public void setText(CharSequence text) {
        b.setText(text);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param text siehe {@link Button}
     */
    public void setTextKeepState(CharSequence text) {
        b.setTextKeepState(text);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param text siehe {@link Button}
     * @param type siehe {@link Button}
     */
    public void setText(CharSequence text, TextView.BufferType type) {
        b.setText(text, type);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param text  siehe {@link Button}
     * @param start siehe {@link Button}
     * @param len   siehe {@link Button}
     */
    public void setText(char[] text, int start, int len) {
        b.setText(text, start, len);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param text siehe {@link Button}
     * @param type siehe {@link Button}
     */
    public void setTextKeepState(CharSequence text, TextView.BufferType type) {
        b.setTextKeepState(text, type);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param resid siehe {@link Button}
     */
    public void setText(@StringRes int resid) {
        b.setText(resid);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param resid siehe {@link Button}
     * @param type  siehe {@link Button}
     */
    public void setText(@StringRes int resid, TextView.BufferType type) {
        b.setText(resid, type);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param hint siehe {@link Button}
     */
    public void setHint(CharSequence hint) {
        b.setHint(hint);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @return siehe {@link Button}
     */
    public CharSequence getHint() {
        return b.getHint();
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param resid siehe {@link Button}
     */
    public void setHint(@StringRes int resid) {
        b.setHint(resid);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param drawable siehe {@link Button}
     */
    public void invalidateDrawable(@NonNull Drawable drawable) {
        b.invalidateDrawable(drawable);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param keyCode siehe {@link Button}
     * @param event   siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return b.onKeyDown(keyCode, event);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param keyCode     siehe {@link Button}
     * @param repeatCount siehe {@link Button}
     * @param event       siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return b.onKeyMultiple(keyCode, repeatCount, event);
    }

    /**
     * Delegiert von android.widget.Button
     *
     * @param keyCode siehe {@link Button}
     * @param event   siehe {@link Button}
     * @return siehe {@link Button}
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return b.onKeyUp(keyCode, event);
    }
}

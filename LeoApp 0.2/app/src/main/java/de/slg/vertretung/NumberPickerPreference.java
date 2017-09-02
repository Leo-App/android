package de.slg.vertretung;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import de.slg.leoapp.R;
import de.slg.leoapp.ReceiveService;
import de.slg.leoapp.Utils;

public class NumberPickerPreference extends DialogPreference {

    private static final int     MAX_VALUE           = 5;
    private static final int     MIN_VALUE           = 0;
    private static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int          value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);
        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setDisplayedValues(new String[]{"5 " + Utils.getString(R.string.minutes), "15 " + Utils.getString(R.string.minutes), "30 " + Utils.getString(R.string.minutes), "1 " + Utils.getString(R.string.hour), "1.5 " + Utils.getString(R.string.hours), "2 " + Utils.getString(R.string.hours)});
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    private int getValue() {
        this.value = getPersistedInt(1);
        return this.value;
    }

    private void setValue(int value) {
        this.value = value;
        persistInt(this.value);
        ReceiveService.setInterval(value);
    }
}
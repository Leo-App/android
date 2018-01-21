package de.slgdev.klausurplan;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

public class NumberPickerPreference extends DialogPreference {

    private static final int     MAX_VALUE           = 12;
    private static final int     MIN_VALUE           = 1;
    private static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;

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
        picker.setDisplayedValues(new String[]{"1 " + Utils.getString(R.string.month), "2 " + Utils.getString(R.string.months), "3 " + Utils.getString(R.string.months), "4 " + Utils.getString(R.string.months), "5 " + Utils.getString(R.string.months), "6 " + Utils.getString(R.string.months), "7 " + Utils.getString(R.string.months), "8 " + Utils.getString(R.string.months), "9 " + Utils.getString(R.string.months), "10 " + Utils.getString(R.string.months), "11 " + Utils.getString(R.string.months), "1 " + Utils.getString(R.string.year)});
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setValue(getSharedPreferences().getInt(getKey(), 12));
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                persistInt(newValue);
            }
        }
    }
}
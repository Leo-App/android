package de.slg.startseite;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

public class ClassPickerPref extends DialogPreference {

    private static final int MAX_VALUE = 12;
    private static final int MIN_VALUE = 5;
    private static final boolean WRAP_SELECTOR_WHEEL = false;
    private final String[] values = {"5", "6", "7", "8", "9", "EF", "Q1", "Q2"};
    private NumberPicker picker;

    public ClassPickerPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassPickerPref(Context context, AttributeSet attrs, int defStyleAttr) {
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
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(MIN_VALUE + getIndex());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue - MIN_VALUE);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    private void setValue(int index) {
        persistString(values[index]);
    }

    private int getIndex() {
        String grade;
        try {
            grade = getPersistedString("N/A");
        } catch (Exception e) {
            grade = "N/A";
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(grade))
                return i;
        }
        return 0;
    }
}
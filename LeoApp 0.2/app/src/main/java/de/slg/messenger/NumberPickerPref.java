package de.slg.messenger;

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
import de.slg.startseite.MainActivity;

public class NumberPickerPref extends DialogPreference {

    public static final int MAX_VALUE = 6;
    public static final int MIN_VALUE = 0;
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value;

    public NumberPickerPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPref(Context context, AttributeSet attrs, int defStyleAttr) {
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
        picker.setDisplayedValues(new String[] { "5 "+ MainActivity.ref.getString(R.string.seconds), "10 "+ MainActivity.ref.getString(R.string.seconds), "15 "+MainActivity.ref.getString(R.string.seconds), "30 "+MainActivity.ref.getString(R.string.seconds), "1 "+MainActivity.ref.getString(R.string.minute), "2 "+MainActivity.ref.getString(R.string.minutes), "5 "+MainActivity.ref.getString(R.string.minutes)});
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

    public int getValue() {
        this.value = getPersistedInt(1);
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
        ReceiveService.setIntervall(value);
    }
}

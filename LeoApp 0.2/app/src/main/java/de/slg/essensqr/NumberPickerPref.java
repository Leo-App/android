package de.slg.essensqr;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import de.slg.leoapp.R;
import de.slg.startseite.MainActivity;


public class NumberPickerPref extends DialogPreference {

    public static final int MAX_VALUE = 10;
    public static final int MIN_VALUE = 2;
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
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setDisplayedValues(new String[] { "2 "+ MainActivity.ref.getString(R.string.seconds), "3 "
                + MainActivity.ref.getString(R.string.seconds), "4 "+MainActivity.ref.getString(R.string.seconds), "5 "
                + MainActivity.ref.getString(R.string.seconds), "6 "+MainActivity.ref.getString(R.string.seconds), "7 "
                + MainActivity.ref.getString(R.string.seconds), "8 "+MainActivity.ref.getString(R.string.seconds), "9 "
                + MainActivity.ref.getString(R.string.seconds), "10 "+MainActivity.ref.getString(R.string.seconds)});
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

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state == null || !state.getClass().equals(SavedState.class))
            super.onRestoreInstanceState(state);
        else {
            SavedState myState = (SavedState) state;
            super.onRestoreInstanceState(myState);
        }
    }

    public int getValue() {
        this.value = getPersistedInt(2);
        return this.value;
    }

    private static class SavedState extends View.BaseSavedState {

        int number;

        public SavedState(Parcel source) {
            super(source);
            number = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            super.writeToParcel(dest, flags);
            dest.writeInt(number);

        }

        public SavedState(Parcelable state) {
            super(state);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }
}

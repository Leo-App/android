package de.slg.essensbons.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

public class FeedbackDialog extends AlertDialog {

    private boolean valid;
    private int     orderedMenu;

    public FeedbackDialog(@NonNull Context context, boolean valid, int orderedMenu) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.valid = valid;
        this.orderedMenu = orderedMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (valid) {
            setContentView(R.layout.dialog_valid);
            ((TextView) findViewById(R.id.textViewMenu)).setText(Utils.getContext().getString(R.string.dialog_desc_valid, orderedMenu));
        } else {
            setContentView(R.layout.dialog_invalid);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        dismiss();

        return super.onTouchEvent(event);
    }
}

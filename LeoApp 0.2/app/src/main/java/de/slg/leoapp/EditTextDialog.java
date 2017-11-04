package de.slg.leoapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class EditTextDialog extends AlertDialog {
    EditText editText;
    private String               hint;
    private View.OnClickListener action;

    protected EditTextDialog(@NonNull Context context, @NonNull String hint, @NonNull View.OnClickListener action) {
        super(context);
        this.hint = hint;
        this.action = action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_text);

        editText = (EditText) findViewById(R.id.editText);
        editText.setHint(hint);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.ok).setOnClickListener(action);
    }
}
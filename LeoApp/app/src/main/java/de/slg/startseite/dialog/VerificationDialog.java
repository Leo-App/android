package de.slg.startseite.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import de.slg.startseite.task.RegistrationTask;

import static android.view.View.VISIBLE;

public class VerificationDialog extends AlertDialog {
    private boolean enteredUser, enteredPass;

    public VerificationDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_verification);

        final EditText user = (EditText) findViewById(R.id.etName);
        final EditText pass = (EditText) findViewById(R.id.etPasswd);

        final View confirm = findViewById(R.id.buttonDialog2);
        final View cancel  = findViewById(R.id.buttonDialog1);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar1).setVisibility(VISIBLE);
                Utils.getController().getPreferences()
                        .edit()
                        .putString("pref_key_general_password", pass.getText().toString())
                        .putString("pref_key_general_defaultusername", user.getText().toString())
                        .apply();
                new RegistrationTask(VerificationDialog.this).execute();
            }
        });

        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredUser = s.length() > 0 && s.length() % 6 == 0;
                confirm.setEnabled(enteredUser && enteredPass);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredPass = s.length() > 0;
                confirm.setEnabled(enteredUser && enteredPass);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        user.setText(Utils.getUserDefaultName());
    }
}

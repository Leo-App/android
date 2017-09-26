package de.slg.startseite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

import static android.view.View.VISIBLE;

class VerificationDialog extends AlertDialog {

    VerificationDialog(@NonNull Context context) {
        super(context);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_verification);

        final EditText user = (EditText) findViewById(R.id.etName);
        final EditText pass = (EditText) findViewById(R.id.etPasswd);

        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar1).setVisibility(VISIBLE);
                Utils.getController().getPreferences().edit().putString("pref_key_password_general", pass.getText().toString()).apply();
                dismiss();
                new RegistrationTask(VerificationDialog.this).execute(user.getText().toString());
            }
        });

    }

}

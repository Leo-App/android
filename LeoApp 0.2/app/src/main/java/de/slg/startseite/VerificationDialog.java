package de.slg.startseite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import de.slg.leoapp.R;

class VerificationDialog extends AlertDialog {

    VerificationDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_verification);

        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new RegistrationTask().execute("Name", "Permission");
            }
        });

    }

}

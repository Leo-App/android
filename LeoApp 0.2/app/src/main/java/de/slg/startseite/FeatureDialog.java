package de.slg.startseite;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.slg.leoapp.R;

class FeatureDialog extends AlertDialog{
    FeatureDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.dialog_layout_featurerequest);

        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = ((EditText)findViewById(R.id.feature_request_desc)).getText().toString();
                new MailSendTask().execute(emailText);
                dismiss();
            }
        });


    }

}

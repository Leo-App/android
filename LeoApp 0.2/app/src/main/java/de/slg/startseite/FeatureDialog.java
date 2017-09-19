package de.slg.startseite;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

class FeatureDialog extends AlertDialog {
    FeatureDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_featurerequest);
        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = ((EditText) findViewById(R.id.feature_request_desc)).getText().toString();
                new MailSendTask().execute(emailText);
                dismiss();
                Toast.makeText(Utils.getController().getContext(), Utils.getString(R.string.thank_you_feature), Toast.LENGTH_SHORT).show();
            }
        });
        ((EditText) findViewById(R.id.feature_request_desc)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0)
                    findViewById(R.id.buttonDialog2).setEnabled(true);
                else
                    findViewById(R.id.buttonDialog2).setEnabled(false);
            }
        });
    }
}

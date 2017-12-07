package de.slg.leoapp.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

public class ChangelogDialog extends AppCompatDialog {
    public ChangelogDialog(Activity context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_changelog);

        findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utils.getController().getPreferences()
                        .edit()
                        .putString("previousVersion", Utils.getAppVersionName())
                        .apply();
            }
        });
    }
}
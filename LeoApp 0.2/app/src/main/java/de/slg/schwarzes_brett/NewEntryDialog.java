package de.slg.schwarzes_brett;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import de.slg.leoapp.R;

public class NewEntryDialog extends AlertDialog {
    protected NewEntryDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.dialog_create_entry);

    }
}

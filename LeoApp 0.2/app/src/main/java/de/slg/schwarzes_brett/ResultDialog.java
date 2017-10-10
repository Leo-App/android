package de.slg.schwarzes_brett;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import de.slg.leoapp.R;

class ResultDialog extends AlertDialog {

    private Context context;
    private int id;

    ResultDialog(@NonNull Context context, int id) {
        super(context);
        this.context = context;
        this.id = id;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_survey_result);
    }

}

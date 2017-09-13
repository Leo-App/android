package de.slg.schwarzes_brett;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import de.slg.leoapp.R;

class NewSurveyDialog extends AlertDialog {

    private int stage = 0;
    private int[] layouts = {R.layout.dialog_create_survey, R.layout.dialog_create_survey_content, R.layout.dialog_create_survey_answers};

    NewSurveyDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_create_survey);
    }

    private void next() {
        stage++;
        setContentView(layouts[stage]);
    }
}

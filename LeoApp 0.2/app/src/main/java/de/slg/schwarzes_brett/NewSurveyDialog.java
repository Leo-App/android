package de.slg.schwarzes_brett;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import de.slg.leoapp.R;

public class NewSurveyDialog extends AlertDialog {

    int stage = 0;
    int[] layouts = {R.layout.dialog_create_survey, R.layout.dialog_create_survey_content, R.layout.dialog_create_survey_answers};

    protected NewSurveyDialog(@NonNull Context context) {
        super(context);
        setContentView(layouts[stage]);
    }

    private void next() {
        stage++;
        setContentView(layouts[stage]);
    }
}

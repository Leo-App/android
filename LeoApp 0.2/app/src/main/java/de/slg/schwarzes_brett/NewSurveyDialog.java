package de.slg.schwarzes_brett;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import de.slg.leoapp.R;

class NewSurveyDialog extends AlertDialog {

    private int stage = 0;
    private int[] layouts = {R.layout.dialog_create_survey, R.layout.dialog_create_survey_content, R.layout.dialog_create_survey_answers};

    NewSurveyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_create_survey);
        initNextButton();
    }

    private void initNextButton() {
        findViewById(R.id.buttonExamSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void next() {
        stage++;
        setContentView(layouts[stage]);
        initNextButton();
    }
}

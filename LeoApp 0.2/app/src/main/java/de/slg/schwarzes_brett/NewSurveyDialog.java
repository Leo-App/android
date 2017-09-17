package de.slg.schwarzes_brett;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import de.slg.leoapp.R;

class NewSurveyDialog extends AlertDialog {

    private int stage = 0;
    private int[] layouts = {R.layout.dialog_create_survey, R.layout.dialog_create_survey_content, R.layout.dialog_create_survey_answers};
    private Context c;
    private View currentView;

    private String title;
    private String description;
    private String[] answers;

    NewSurveyDialog(@NonNull Context context) {
        super(context);
        c = context;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_create_survey);
        initNextButton();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void initNextButton() {
        findViewById(R.id.buttonExamSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (stage) {
                    case 0:
                        if(((EditText)findViewById(R.id.content)).getText().toString().length() == 0) {
                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), "Du musst einen Titel angeben", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            snackbar.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            break;
                        }
                        title = ((EditText)findViewById(R.id.content)).getText().toString();
                        next();
                        break;
                    case 1:
                        description = ((EditText)findViewById(R.id.content)).getText().toString();
                        next();
                        break;
                    case 2:
                        String s1 = ((EditText)findViewById(R.id.text1)).getText().toString(),
                                s2 = ((EditText)findViewById(R.id.text2)).getText().toString(),
                                s3 = ((EditText)findViewById(R.id.text3)).getText().toString(),
                                s4 = ((EditText)findViewById(R.id.text4)).getText().toString(),
                                s5 = ((EditText)findViewById(R.id.text5)).getText().toString();

                        if(s1.length()+s2.length()+s3.length()+s4.length()+s5.length() < 1) {
                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), "Du musst mindestens eine Antwortmöglichkeit angeben", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            snackbar.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            break;
                        }

                        answers = new String[]{s1, s2, s3, s4, s5};
                        next();
                        break;
                }
            }
        });
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void initAnswers() {
        if (stage == 2) {
            findViewById(R.id.new_answer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.new_answer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text1).setVisibility(View.VISIBLE);
                }
            });
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.button).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text2).setVisibility(View.VISIBLE);
                }
            });
            findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.button1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text3).setVisibility(View.VISIBLE);
                }
            });
            findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.button2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text4).setVisibility(View.VISIBLE);
                }
            });
            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.button3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text5).setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void next() {
        stage++;
        if (stage >= layouts.length) {
            new sendSurveyTask().execute();
            dismiss();
            return;
        }
        LayoutInflater inflater = getLayoutInflater();
        currentView = inflater.inflate(layouts[stage], null, false);
        currentView.startAnimation(AnimationUtils.loadAnimation(c, R.anim.text_view_slide_in));
        setContentView(currentView);
        initNextButton();
        initAnswers();
    }

    private void back() {
        stage--;
        if (stage < 0) {
            dismiss();
            return;
        }
        LayoutInflater inflater = getLayoutInflater();
        currentView = inflater.inflate(layouts[stage], null, false);
        currentView.startAnimation(AnimationUtils.loadAnimation(c, R.anim.text_view_slide_in));
        setContentView(currentView);
        initNextButton();
        initAnswers();
    }

    private class sendSurveyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

}

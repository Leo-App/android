package de.slgdev.umfragen.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.Utils;

public class NewSurveyDialog extends AlertDialog {
    private int   stage   = 0;
    private int[] layouts = {R.layout.dialog_create_survey, R.layout.dialog_create_survey_content, R.layout.dialog_create_survey_answers, R.layout.dialog_create_survey_to};
    private Context c;
    private View    currentView;

    private String   title;
    private String   description;
    private String[] answers;
    private boolean  multiple;
    private int      to;

    private boolean backPressed;

    public NewSurveyDialog(@NonNull Context context) {
        super(context);
        c = context;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_create_survey);
        initNextButton();
        setCanceledOnTouchOutside(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    public void onBackPressed() {
        if (backPressed) {
            dismiss();
        } else {
            GraphicUtils.sendToast("Zum Schließen zweimal zurück drücken");
            backPressed = true;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 1000);
        }
    }

    private void initEditText() {
        switch (stage) {
            case 0:
                ((EditText) findViewById(R.id.content)).setText(title);
                findViewById(R.id.content).requestFocus();
                break;
            case 1:
                ((EditText) findViewById(R.id.content)).setText(description);
                findViewById(R.id.content).requestFocus();
                break;
            case 2:
                if (answers == null)
                    break;
                int[] layouts = {R.id.text1, R.id.new_answer, R.id.text2, R.id.button, R.id.text3, R.id.button1, R.id.text4, R.id.button2, R.id.text5, R.id.button3};
                for (int i = 0; i < answers.length; i++) {
                    if (answers[i].length() > 0) {
                        findViewById(layouts[i * 2 + 1]).setVisibility(View.INVISIBLE);
                        findViewById(layouts[i * 2]).setVisibility(View.VISIBLE);
                        ((EditText) findViewById(layouts[i * 2])).setText(answers[i]);
                    }
                }
                break;
        }
    }

    private void initNextButton() {
        findViewById(R.id.buttonExamSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (stage) {
                    case 0:
                        if (((EditText) findViewById(R.id.content)).getText().toString().length() == 0) {
                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), R.string.need_title, Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            snackbar.setAction(getContext().getString(R.string.confirm), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            break;
                        }
                        title = ((EditText) findViewById(R.id.content)).getText().toString();
                        next();
                        break;
                    case 1:
                        description = ((EditText) findViewById(R.id.content)).getText().toString();
                        next();
                        break;
                    case 2:
                        String s1 = ((EditText) findViewById(R.id.text1)).getText().toString(),
                                s2 = ((EditText) findViewById(R.id.text2)).getText().toString(),
                                s3 = ((EditText) findViewById(R.id.text3)).getText().toString(),
                                s4 = ((EditText) findViewById(R.id.text4)).getText().toString(),
                                s5 = ((EditText) findViewById(R.id.text5)).getText().toString();

                        if (s1.length() + s2.length() + s3.length() + s4.length() + s5.length() < 1) {
                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), R.string.min_replies, Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            snackbar.setAction(getContext().getString(R.string.confirm), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            break;
                        }

                        multiple = ((CheckBox) findViewById(R.id.multiple)).isChecked();

                        saveAnswers();
                        next();
                        break;
                    case 3:
                        to = ((Spinner) findViewById(R.id.spinner2)).getSelectedItemPosition();
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

    private void saveAnswers() {

        if (stage != 2)
            return;

        String s1 = ((EditText) findViewById(R.id.text1)).getText().toString(),
                s2 = ((EditText) findViewById(R.id.text2)).getText().toString(),
                s3 = ((EditText) findViewById(R.id.text3)).getText().toString(),
                s4 = ((EditText) findViewById(R.id.text4)).getText().toString(),
                s5 = ((EditText) findViewById(R.id.text5)).getText().toString();

        answers = new String[]{s1, s2, s3, s4, s5};
    }

    private void initAnswers() {
        if (stage == 2) {
            findViewById(R.id.new_answer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.new_answer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.text1).setVisibility(View.VISIBLE);
                    InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(findViewById(R.id.text1), InputMethodManager.SHOW_IMPLICIT);
                    findViewById(R.id.text1).requestFocus();
                }
            });
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.text1).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.button).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text2).setVisibility(View.VISIBLE);
                        InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.showSoftInput(findViewById(R.id.text2), InputMethodManager.SHOW_IMPLICIT);
                        findViewById(R.id.text2).requestFocus();
                    } else {
                        findViewById(R.id.new_answer).callOnClick();
                    }
                }
            });
            findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.text2).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.button1).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text3).setVisibility(View.VISIBLE);
                        InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.showSoftInput(findViewById(R.id.text3), InputMethodManager.SHOW_IMPLICIT);
                        findViewById(R.id.text3).requestFocus();
                    } else {
                        findViewById(R.id.button).callOnClick();
                    }
                }
            });
            findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.text3).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.button2).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text4).setVisibility(View.VISIBLE);
                        InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.showSoftInput(findViewById(R.id.text4), InputMethodManager.SHOW_IMPLICIT);
                        findViewById(R.id.text4).requestFocus();
                    } else {
                        findViewById(R.id.button1).callOnClick();
                    }
                }
            });
            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.text4).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.button3).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text5).setVisibility(View.VISIBLE);
                        InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.showSoftInput(findViewById(R.id.text5), InputMethodManager.SHOW_IMPLICIT);
                        findViewById(R.id.text5).requestFocus();
                    } else {
                        findViewById(R.id.button2).callOnClick();
                    }
                }
            });
        }
    }

    private void next() {
        stage++;
        if (stage >= layouts.length) {
            new SendSurveyTask().execute();
            return;
        }
        if (stage == 1)
            saveAnswers();
        LayoutInflater inflater = getLayoutInflater();
        currentView = inflater.inflate(layouts[stage], null, false);
        currentView.startAnimation(AnimationUtils.loadAnimation(c, R.anim.text_view_slide_in));
        setContentView(currentView);
        initNextButton();
        initAnswers();
        initEditText();
        initSpinner();
    }

    private void back() {
        stage--;
        if (stage < 0) {
            dismiss();
            return;
        }
        if (stage == 1)
            saveAnswers();
        LayoutInflater inflater = getLayoutInflater();
        currentView = inflater.inflate(layouts[stage], null, false);
        currentView.startAnimation(AnimationUtils.loadAnimation(c, R.anim.text_view_slide_in));
        setContentView(currentView);
        initNextButton();
        initAnswers();
        initEditText();
        initSpinner();
    }

    private void initSpinner() {

        if (stage != layouts.length - 1)
            return;

        Spinner s = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Utils.getContext(),
                R.array.level, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        s.setAdapter(adapter);
    }

    private class SendSurveyTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            StringBuilder answerString = new StringBuilder(answers[0]);

            for (int i = 1; i < 5; i++) {
                if (answers[i].equals(""))
                    continue;
                answerString.append("_;_").append(answers[i]);
            }

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new URL(
                                        (Utils.BASE_URL_PHP + "survey/" +
                                                "addSurvey.php?" +
                                                "id=" + Utils.getUserID() + "&" +
                                                "to=" + to + "&" +
                                                "title=" + title + "&" +
                                                "desc=" + description + "&" +
                                                "mult=" + (multiple ? 1 : 0) + "&" +
                                                "answers=" + answerString)
                                                .replace(
                                                        " ",
                                                        "%20"
                                                )
                                )
                                        .openConnection()
                                        .getInputStream()
                        )
                );

                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();

                return !builder.toString().startsWith("-");
            } catch (IOException e) {
                Utils.logError(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                dismiss();
                Toast.makeText(Utils.getContext(), R.string.survey_sucessfully, Toast.LENGTH_LONG).show();
            } else {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), Utils.getString(R.string.error_later), Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                snackbar.setAction(getContext().getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
    }
}
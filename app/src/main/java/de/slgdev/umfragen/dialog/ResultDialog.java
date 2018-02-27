package de.slgdev.umfragen.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.umfragen.task.SyncResultTask;

import static android.view.View.GONE;

/**
 * Ergebnisdialog
 * <p>
 * Dieser Dialog wird zum Anzeigen der Umfrageergebnisse verwendet.
 *
 * @author Gianni
 * @version 2017.2110
 * @since 0.5.6
 */
public class ResultDialog extends AlertDialog implements TaskStatusListener {

    private int       id;
    private ObjectCallbackTask asyncTask;

    private TextView[]    answers;
    private TextView[]    percentages;
    private ProgressBar[] progressBars;
    private ProgressBar   load;
    private Button        b1;
    private TextView      t1;
    private TextView      t2;

    private String to;

    /**
     * Konstruktor.
     *
     * @param context Context-Objekt
     * @param id      Umfrage-ID, für die die Ergebnisse angezeigt werden sollen.
     */
    public ResultDialog(@NonNull Context context, int id, String to) {
        super(context);
        this.id = id;
        this.to = to;
    }

    /**
     * Hier werden View-Objekte instanziiert und der Synchronisationsvorgang gestartet.
     *
     * @param b Metadata
     */
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_survey_result);

        b1 = findViewById(R.id.buttonOK);

        t1 = findViewById(R.id.question);
        t2 = findViewById(R.id.votes);

        load = findViewById(R.id.progressBarLoading);

        ProgressBar p1 = findViewById(R.id.progressBar1);
        ProgressBar p2 = findViewById(R.id.progressBar2);
        ProgressBar p3 = findViewById(R.id.progressBar3);
        ProgressBar p4 = findViewById(R.id.progressBar4);
        ProgressBar p5 = findViewById(R.id.progressBar5);
        progressBars = new ProgressBar[]{p1, p2, p3, p4, p5};

        TextView op1 = findViewById(R.id.answer1);
        TextView op2 = findViewById(R.id.answer2);
        TextView op3 = findViewById(R.id.answer3);
        TextView op4 = findViewById(R.id.answer4);
        TextView op5 = findViewById(R.id.answer5);
        answers = new TextView[]{op1, op2, op3, op4, op5};

        TextView pe1 = findViewById(R.id.percent1);
        TextView pe2 = findViewById(R.id.percent2);
        TextView pe3 = findViewById(R.id.percent3);
        TextView pe4 = findViewById(R.id.percent4);
        TextView pe5 = findViewById(R.id.percent5);
        percentages = new TextView[]{pe1, pe2, pe3, pe4, pe5};

        for (TextView cur : answers)
            cur.setVisibility(View.GONE);
        for (ProgressBar cur : progressBars)
            cur.setVisibility(View.GONE);

        t1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);

        b1.setOnClickListener(v -> {
            dismiss();
            stopLoading();
        });

        asyncTask = new SyncResultTask();
        asyncTask.addListener(this).execute(id, to);
    }

    @SuppressWarnings("unchecked")
    private void animateChanges(int amount, HashMap<String, Integer> answerMap, int target, int votes) {

        Map.Entry<String, Integer>[] entries = answerMap.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < amount; i++) {
            answers[i].setText(entries[i].getKey());
            answers[i].setVisibility(View.VISIBLE);
            progressBars[i].setVisibility(View.VISIBLE);

            if (votes == 0)
                continue;

            ObjectAnimator animation = ObjectAnimator.ofInt(progressBars[i], "progress", entries[i].getValue() * 100 / votes);
            animation.setDuration(1250);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

            percentages[i].setText(String.valueOf(entries[i].getValue()));
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) b1.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, answers[amount - 1].getId());
        b1.setLayoutParams(params);

        for (int i = amount; i < answers.length; i++) {
            answers[i].setVisibility(GONE);
        }

        double        percentage = (double) votes * 100d / (double) target;
        DecimalFormat df         = new DecimalFormat("####0.00");

        t2.setText(Utils.getContext().getString(R.string.statistics_result, votes, target, df.format(percentage)));
    }

    private void stopLoading() {
        asyncTask.cancel(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void taskFinished(Object... params) {

        ResponseCode responseCode = (ResponseCode) params[0];

        int amountAnswers = (int) params[1];
        LinkedHashMap<String, Integer> answerResults = (LinkedHashMap<String, Integer>) params[2];
        int target = (int) params[3];
        int sumVotes = (int) params[4];
        String title = (String) params[5];

        load.setVisibility(View.GONE);

        switch (responseCode) {
            case NO_CONNECTION:
                findViewById(R.id.imageViewError).setVisibility(View.VISIBLE);
                final Snackbar snack = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.snackbar_no_connection_info), Snackbar.LENGTH_LONG);
                snack.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                snack.setAction(getContext().getString(R.string.confirm), v -> snack.dismiss());
                snack.show();
                break;
            case NOT_SENT:
            case SERVER_FAILED:
                findViewById(R.id.imageViewError).setVisibility(View.VISIBLE);
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.error_later), Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                snackbar.setAction(getContext().getString(R.string.confirm), v -> snackbar.dismiss());
                snackbar.show();
                break;
            case SUCCESS:
                t1.setText(title);
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
                animateChanges(amountAnswers, answerResults, target, sumVotes);
                break;
        }
    }
}

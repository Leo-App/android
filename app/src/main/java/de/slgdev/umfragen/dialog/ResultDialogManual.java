package de.slgdev.umfragen.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import de.slgdev.leoapp.R;

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
public class ResultDialogManual extends AlertDialog {

    private TextView[]    answers;
    private TextView[]    percentages;
    private ProgressBar[] progressBars;
    private Button        b1;
    private TextView      t1;

    private String description;
    private HashMap<String, Integer> answerMap;

    /**
     * Konstruktor.
     *
     * @param context Context-Objekt
     */
    public ResultDialogManual(@NonNull Context context, String description, HashMap<String, Integer> answers) {
        super(context);
        this.description = description;
        answerMap = answers;
    }

    /**
     * Hier werden View-Objekte instanziiert und der Synchronisationsvorgang gestartet.
     *
     * @param b Metadata
     */
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_survey_result_simplified);

        b1 = findViewById(R.id.buttonOK);
        t1 = findViewById(R.id.question);

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

        b1.setOnClickListener(v -> dismiss());

        initLayout();
    }

    private void initLayout() {
        t1.setVisibility(View.VISIBLE);
        t1.setText(description);
        animateChanges(answerMap.size(), answerMap, getSumVotes());
    }

    private int getSumVotes() {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : answerMap.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    @SuppressWarnings("unchecked")
    private void animateChanges(int amount, HashMap<String, Integer> answerMap, int votes) {

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
    }

}

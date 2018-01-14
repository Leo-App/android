package de.slg.umfragen.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import de.slg.schwarzes_brett.utility.ResponseCode;

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
public class ResultDialog extends AlertDialog {

    private int       id;
    private AsyncTask asyncTask;

    private TextView[]    answers;
    private TextView[]    percentages;
    private ProgressBar[] progressBars;
    private ProgressBar   load;
    private Button        b1;
    private TextView      t1;
    private TextView      t2;

    /**
     * Konstruktor.
     *
     * @param context Context-Objekt
     * @param id      Umfrage-ID, für die die Ergebnisse angezeigt werden sollen.
     */
    public ResultDialog(@NonNull Context context, int id) {
        super(context);
        this.id = id;
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

        b1 = (Button) findViewById(R.id.buttonOK);

        t1 = (TextView) findViewById(R.id.question);
        t2 = (TextView) findViewById(R.id.votes);

        load = (ProgressBar) findViewById(R.id.progressBarLoading);

        ProgressBar p1 = (ProgressBar) findViewById(R.id.progressBar1);
        ProgressBar p2 = (ProgressBar) findViewById(R.id.progressBar2);
        ProgressBar p3 = (ProgressBar) findViewById(R.id.progressBar3);
        ProgressBar p4 = (ProgressBar) findViewById(R.id.progressBar4);
        ProgressBar p5 = (ProgressBar) findViewById(R.id.progressBar5);
        progressBars = new ProgressBar[]{p1, p2, p3, p4, p5};

        TextView op1 = (TextView) findViewById(R.id.answer1);
        TextView op2 = (TextView) findViewById(R.id.answer2);
        TextView op3 = (TextView) findViewById(R.id.answer3);
        TextView op4 = (TextView) findViewById(R.id.answer4);
        TextView op5 = (TextView) findViewById(R.id.answer5);
        answers = new TextView[]{op1, op2, op3, op4, op5};

        TextView pe1 = (TextView) findViewById(R.id.percent1);
        TextView pe2 = (TextView) findViewById(R.id.percent2);
        TextView pe3 = (TextView) findViewById(R.id.percent3);
        TextView pe4 = (TextView) findViewById(R.id.percent4);
        TextView pe5 = (TextView) findViewById(R.id.percent5);
        percentages = new TextView[]{pe1, pe2, pe3, pe4, pe5};

        for (TextView cur : answers)
            cur.setVisibility(View.GONE);
        for (ProgressBar cur : progressBars)
            cur.setVisibility(View.GONE);

        t1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                stopLoading();
            }
        });

        asyncTask = new SyncResults().execute();
    }

    @SuppressWarnings("unchecked")
    private void animateChanges(int amount, HashMap<String, Integer> answerMap, int target, int votes) {

        Map.Entry<String, Integer>[] entries = answerMap.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < amount; i++) {
            answers[i].setText(entries[i].getKey());
            answers[i].setVisibility(View.VISIBLE);
            progressBars[i].setVisibility(View.VISIBLE);

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

    /**
     * Ergebnissynchronisations-Task
     * <p>
     * Diese private Klasse ruft die Ergebnisse der Umfrage mit der übergebenen ID ab. Dazu werden die Ergebnisse eines PHP Skripts abgerufen und ausgewertet.
     *
     * @author Gianni
     * @since 0.5.6
     * @version 2017.0512
     */
    private class SyncResults extends AsyncTask<Void, Void, ResponseCode> {

        private int                            amountAnswers;
        private int                            target;
        private int                            sumVotes;
        private String                         title;
        private LinkedHashMap<String, Integer> answerResults;

        @Override
        protected ResponseCode doInBackground(Void... params) {
            try {
                if (!Utils.checkNetwork()) {
                    return ResponseCode.NO_CONNECTION;
                }

                URL            updateURL = new URL(Utils.DOMAIN_DEV + "survey/getAllResults.php?ic_create_survey=" + id);
                BufferedReader reader    = new BufferedReader(new InputStreamReader(updateURL.openConnection().getInputStream()));

                Utils.logError(updateURL);

                String        cur;
                StringBuilder result = new StringBuilder();
                while ((cur = reader.readLine()) != null) {
                    result.append(cur);
                }

                String resString = result.toString();
                if (resString.contains("-ERR"))
                    return ResponseCode.SERVER_ERROR;

                String[] data = resString.split("_;;_");

                target = Integer.parseInt(data[0]);
                title = data[2];
                sumVotes = Integer.parseInt(data[3]);

                String[] answers = data[1].split("_next_");

                amountAnswers = answers.length;
                answerResults = new LinkedHashMap<>();

                for (String s : answers) {
                    answerResults.put(s.split("_;_")[0], Integer.parseInt(s.split("_;_")[1]));
                }
            } catch (IOException e) {
                Utils.logError(e);
            }

            return ResponseCode.SUCCESS;
        }

        @Override
        protected void onPostExecute(ResponseCode b) {
            load.setVisibility(GONE);
            switch (b) {
                case NO_CONNECTION:
                    findViewById(R.id.imageViewError).setVisibility(View.VISIBLE);
                    final Snackbar snack = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.snackbar_no_connection_info), Snackbar.LENGTH_LONG);
                    snack.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    snack.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                    break;
                case SERVER_ERROR:
                    findViewById(R.id.imageViewError).setVisibility(View.VISIBLE);
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.wrapper), "Es ist etwas schiefgelaufen, versuche es später erneut", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    snackbar.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
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
}

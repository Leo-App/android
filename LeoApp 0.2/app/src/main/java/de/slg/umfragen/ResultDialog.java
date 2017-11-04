package de.slg.umfragen;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.slg.leoapp.ItemAnimator;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.schwarzes_brett.ResponseCode;

/**
 * Ergebnisdialog
 * <p>
 * Dieser Dialog wird zum Anzeigen der Umfrageergebnisse verwendet
 *
 * @author Gianni
 * @version 2017.2110
 * @since 0.5.6
 */
class ResultDialog extends AlertDialog {

    private int       id;
    private AsyncTask asyncTask;

    private TextView[]    answers;
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
    ResultDialog(@NonNull Context context, int id) {
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

        for (TextView cur : answers)
            cur.setVisibility(View.INVISIBLE);
        for (ProgressBar cur : progressBars)
            cur.setVisibility(View.INVISIBLE);

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
            new ProgressBarAnimator(entries[i].getValue(), answers[i], progressBars[i]).setInterval(100).setIterations(entries[i].getValue()).execute();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) b1.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, answers[amount - 1].getId());
        b1.setLayoutParams(params);

        t2.setText(Utils.getContext().getString(R.string.statistics_result, votes, target, votes * 100 / target));
    }

    private void stopLoading() {
        asyncTask.cancel(true);
    }

    /**
     * ProgressBar-Animator
     * <p>
     * Diese private Klasse animiert die Balkenanzeige der Abstimmungen im Ergebnisdialog.
     *
     * @author Gianni
     * @see ItemAnimator
     * @since 0.5.6
     */
    private class ProgressBarAnimator extends ItemAnimator<ProgressBar> {

        private int      percentageValue;
        private int      addPerIteration;
        private TextView percentageText;

        ProgressBarAnimator(int percentageValue, TextView percentageText, ProgressBar progressBar) {
            super(progressBar);
            this.percentageValue = percentageValue;
            this.percentageText = percentageText;
            addPerIteration = 1;
        }

        @Override
        protected void doInIteration(ProgressBar view) {
            view.setProgress(view.getProgress() + addPerIteration);
        }

        @Override
        protected void doOnFinal(ProgressBar view) {
            percentageText.setText(String.valueOf(percentageValue));
            percentageText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Ergebnissynchronisations-Task
     * <p>
     * Diese private Klasse ruft die Ergebnisse der Umfrage mit der übergebenen ID ab. Dazu werden die Ergebnisse eines PHP Skripts abgerufen und ausgewertet.
     *
     * @author Gianni
     * @since 0.5.6
     */
    private class SyncResults extends AsyncTask<Void, Void, ResponseCode> {

        private int                      amountAnswers;
        private int                      target;
        private int                      sumVotes;
        private HashMap<String, Integer> answerResults;

        @Override
        protected ResponseCode doInBackground(Void... params) {
            try {
                if (!Utils.checkNetwork()) {
                    return ResponseCode.NO_CONNECTION;
                }
                URL            updateURL = new URL(Utils.BASE_URL_PHP + "survey/getAllResults.php?survey=" + id);
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

                target = Integer.parseInt(resString.split("_;;_")[0]);
                String[] answers = resString.split("_;;_")[1].split("_next_");

                amountAnswers = answers.length;
                answerResults = new HashMap<>();
                sumVotes = 0;

                for (String s : answers) {
                    answerResults.put(s.split("_;_")[0], Integer.parseInt(s.split("_;_")[1]));
                    sumVotes += Integer.parseInt(s.split("_;_")[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseCode.SUCCESS;
        }

        @Override
        protected void onPostExecute(ResponseCode b) {
            load.setVisibility(View.GONE);
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
                    for(Map.Entry<String, Integer> entry : answerResults.entrySet()) {
                        Utils.logError(entry.getKey());
                        Utils.logError(entry.getValue());
                    }
                    animateChanges(amountAnswers, answerResults, target, sumVotes);
                    break;
            }
        }
    }
}

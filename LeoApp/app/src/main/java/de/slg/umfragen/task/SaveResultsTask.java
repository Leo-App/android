package de.slg.umfragen.task;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import de.slg.leoapp.sqlite.SQLiteConnectorUmfragenSpeichern;
import de.slg.leoapp.utility.Utils;
import de.slg.schwarzes_brett.utility.ResponseCode;
import de.slg.umfragen.dialog.ResultDialog;

import static android.view.View.GONE;

/**
 * Created by Luzia on 19.01.2018.
 */

public class SaveResultsTask extends AsyncTask<Void, Void, ResponseCode> {

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

                URL            updateURL = new URL(Utils.BASE_URL_PHP + "survey/getAllResults.php?survey=" + de.slg.leoapp.utility.Utils.getUserID());
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
                    //answerResults.put(s.split("_;_")[0], Integer.parseInt(s.split("_;_")[1]));
                    int inti = 0;
                }
            } catch (IOException e) {
                Utils.logError(e);
            }

            return ResponseCode.SUCCESS;
        }

        @Override
        protected void onPostExecute(ResponseCode b) {
            switch (b) {
                case NO_CONNECTION:
                    break;
                case SERVER_ERROR:
                    break;
                case SUCCESS:
                    SQLiteConnectorUmfragenSpeichern db = new SQLiteConnectorUmfragenSpeichern(Utils.getContext());
                    SQLiteDatabase                   dbh = db.getWritableDatabase();

                    dbh.insert(SQLiteConnectorUmfragenSpeichern.TABLE_SAVED, null, db.getSurveyContentValues(title));
                    dbh.insert(SQLiteConnectorUmfragenSpeichern.TABLE_ANSWERS, null, db.getAnswerContentValues(
                            "test", //todo ich brauche den verdammten inhalt! (Das was oben rauskommentiert ist
                            de.slg.leoapp.utility.Utils.getUserID()
                    ));
                    break;
            }
        }
    }
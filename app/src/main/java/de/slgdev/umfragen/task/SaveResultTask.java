package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragenSpeichern;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.umfragen.utility.ResultListing;
import de.slgdev.umfragen.utility.Survey;

public class SaveResultTask extends VoidCallbackTask<ResponseCode> {

    private Survey survey;

    public SaveResultTask(Survey survey) {
        this.survey = survey;
    }

    //TODO: Check if empty description is "" or null

    @Override
    protected ResponseCode doInBackground(Void... voids) {

        try {
            if (NetworkUtils.isNetworkAvailable()) {

                URL updateURL = new URL(Utils.BASE_URL_PHP
                        + "survey/getAllResults.php?survey="
                        + survey.remoteId
                        + "&to="
                        + survey.to.replace(" ", "%20"));

                BufferedReader reader = new BufferedReader(new InputStreamReader(updateURL.openConnection().getInputStream()));

                String cur;
                StringBuilder result = new StringBuilder();
                while ((cur = reader.readLine()) != null)
                    result.append(cur);


                String resString = result.toString();

                String[] data = resString.split("_;;_");

                String[] answers = data[1].split("_next_");

                HashMap<String, Integer> answerResults = new HashMap<>();

                for (String s : answers) {
                    answerResults.put(s.split("_;_")[0], Integer.parseInt(s.split("_;_")[1]));
                }

                SQLiteConnectorUmfragenSpeichern connectorSpeichern = new SQLiteConnectorUmfragenSpeichern(Utils.getContext());
                connectorSpeichern.addSurvey(new ResultListing(survey.title, survey.description).setAnswerMap(answerResults));
                connectorSpeichern.close();

            } else {
                return ResponseCode.NO_CONNECTION;
            }
        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.NOT_SENT;
        }

        return ResponseCode.SUCCESS;

    }

    @Override
    protected void onPostExecute(ResponseCode responseCode) {
        switch (responseCode) {

            case NO_CONNECTION:
            case AUTH_FAILED:
            case SERVER_FAILED:
            case NOT_SENT:
                break;
            case SUCCESS:
                new DeleteSurveyTask()
                        .addListener(getListeners().toFirst().getContent())
                        .execute(survey.remoteId);
                break;

        }
    }
}

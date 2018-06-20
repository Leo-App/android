package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;

import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

/**
 * Ergebnissynchronisations-Task
 * <p>
 * Diese Klasse ruft die Ergebnisse der Umfrage mit der übergebenen ID ab. Dazu werden die Ergebnisse eines PHP Skripts abgerufen und ausgewertet.
 *
 * @author Gianni
 * @version 2018.2901
 * @since 0.7.5
 */
public class SyncResultTask extends ObjectCallbackTask<ResponseCode> {

    private int                            amountAnswers;
    private int                            target;
    private int                            sumVotes;
    private String                         title;
    private LinkedHashMap<String, Integer> answerResults;

    @Override
    protected ResponseCode doInBackground(Object... params) {

        int id    = (int) params[0];
        String to = (String) params[1];

        try {
            if (!Utils.isNetworkAvailable()) {
                return ResponseCode.NO_CONNECTION;
            }

            Utils.logDebug(Utils.BASE_URL_PHP + "survey/" +
                    "getAllResults.php?" +
                    "survey=" + id + "&" +
                    "to=" + to.replace(" ", "%20"));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "survey/" +
                                            "getAllResults.php?" +
                                            "survey=" + id + "&" +
                                            "to=" + to.replace(" ", "%20")

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

            String result = builder.toString();
            if (result.contains("-ERR"))
                return ResponseCode.SERVER_FAILED;

            String[] data = result.split("_;;_");

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
            return ResponseCode.NOT_SENT;
        }

        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode responseCode) {
        //TODO Maybe better architecture
        for (TaskStatusListener l : getListeners())
            l.taskFinished(responseCode, amountAnswers, answerResults, target, sumVotes, title);

    }
}

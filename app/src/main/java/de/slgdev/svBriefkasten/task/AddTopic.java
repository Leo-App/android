package de.slgdev.svBriefkasten.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 14.05.2018.
 */

public class AddTopic extends ObjectCallbackTask<ResponseCode> {


    /**
     * Die Methode fügt im Hintergrund ein Thema mit Vorschlag(oder ohne) in die Datenbank ein und "sagt" der zuhörenden Activity beim Beenden "Bescheid"
     */
    @Override
    protected ResponseCode doInBackground(Object... params) {
        if (!Utils.isNetworkAvailable())
            return ResponseCode.NO_CONNECTION;

        String topic = (String) params[0];
        String proposal = (String) params[1];
        String creator = Integer.toString(Utils.getUserID());
        Utils.logDebug(topic + "Test");
        Utils.logDebug(proposal + "Test");

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    "http://www.moritz.liegmanns.de/leoapp_php/svBriefkasten/addTopic.php?" +
                                            "topic=" + topic + "&" +
                                            "proposal=" + proposal + " &" +
                                            "creator=" + creator
                            )
                                    .openConnection()
                                    .getInputStream(),
                            "UTF-8"
                    )
            );

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

            if (builder.toString().startsWith("-"))
                return ResponseCode.SERVER_FAILED;

        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.SERVER_FAILED;
        }
        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode responseCode) {
        for (TaskStatusListener l : getListeners()) {
            l.taskFinished(responseCode);
        }
    }
}





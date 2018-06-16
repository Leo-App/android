package de.slgdev.svBriefkasten.task;

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
    @Override
    protected ResponseCode doInBackground(Object... params) {
        if (!Utils.isNetworkAvailable())
            return ResponseCode.NO_CONNECTION;

        String topic = (String) params[0];
        String proposal = (String) params[1];

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    ("https://www.moritz.liegmanns.de/leoapp_php/svBriefkasten/addTopix.php" +
                                            "addTopic.php?" +
                                            "topic= " + topic + "&" +
                                            "proposal1=" + proposal + "&" +
                                            "id=" + Utils.getUserID() + "&" )
                                            .replace("%", "%25")
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

            if (builder.toString().startsWith("-"))
                return ResponseCode.SERVER_FAILED;

            return ResponseCode.SUCCESS;

        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.NOT_SENT;
        }
    }

    @Override
    protected void onPostExecute(ResponseCode responseCode) {
        for (TaskStatusListener l : getListeners()) {
            l.taskFinished(responseCode);
        }
    }
}



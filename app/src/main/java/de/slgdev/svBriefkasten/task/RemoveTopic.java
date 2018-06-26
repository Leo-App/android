package de.slgdev.svBriefkasten.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 16.06.2018.
 */

public class RemoveTopic extends AsyncTask<Object, Void, ResponseCode> {

    /**
     * Ein Thema wird im Hintergrund, bei vorhandener Internetverbindung entfernt
     */
    @Override
    protected ResponseCode doInBackground(Object... params) {
        if (!Utils.isNetworkAvailable())
            return ResponseCode.NO_CONNECTION;

        String topic = (String) params[0];
        Utils.logDebug(topic + "TestRemove");

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    "http://www.moritz.liegmanns.de/leoapp_php/svBriefkasten/removeTopic.php?" +
                                            "topic=" + topic
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
}

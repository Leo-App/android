package de.slg.leoapp.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.Utils;

public class SyncQuestionTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            URLConnection connection = new URL(
                    Utils.BASE_URL_PHP + "stimmungsbarometer/getQuestion.php"
            )
                    .openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            "UTF-8"
                    )
            );

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            Utils.logDebug(builder);

            Utils.getController().getPreferences()
                    .edit()
                    .putString("stimmungsbarometer_frage", builder.toString())
                    .apply();
        } catch (IOException e) {
            Utils.logError(e);
        }
        return null;
    }
}
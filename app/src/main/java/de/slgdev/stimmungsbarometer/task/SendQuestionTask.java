package de.slgdev.stimmungsbarometer.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slgdev.leoapp.utility.Utils;

public class SendQuestionTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        try {
            URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/newQuestion.php?qtext=" + URLEncoder.encode(params[0], "UTF-8"))
                    .openConnection();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream(), "UTF-8"));

            String        line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

            if (builder.charAt(0) != '-') {
                Utils.getController().getPreferences()
                        .edit()
                        .putString("stimmungsbarometer_frage", params[0])
                        .apply();
            }
        } catch (IOException e) {
            Utils.logError(e);
        }
        return null;
    }
}

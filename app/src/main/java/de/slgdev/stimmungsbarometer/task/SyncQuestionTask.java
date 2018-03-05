package de.slgdev.stimmungsbarometer.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;

public class SyncQuestionTask extends VoidCallbackTask<Void> {

    @Override
    protected Void doInBackground(Void... params) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/getQuestion.php")
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
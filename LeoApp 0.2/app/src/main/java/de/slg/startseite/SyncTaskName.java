package de.slg.startseite;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.Utils;

class SyncTaskName extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        if (!Utils.checkNetwork())
            return null;

        StringBuilder builder = new StringBuilder();
        try {
            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL + "getName.php?key=5453&userid=" + Utils.getUserID())
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("<"))
                    builder.append(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (builder.charAt(0) == '-') {
            return null;
        }
        Utils.getPreferences()
                .edit()
                .putString("pref_key_username_general", builder.toString())
                .apply();

        return null;
    }
}
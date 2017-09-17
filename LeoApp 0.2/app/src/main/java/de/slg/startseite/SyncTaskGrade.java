package de.slg.startseite;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.Utils;

class SyncTaskGrade extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        if (!Utils.checkNetwork())
            return null;
        BufferedReader in     = null;
        String         result = "";
        try {
            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL + "getKlasse.php?key=5453&userid=" + Utils.getUserID())
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        if (result.startsWith("-"))
            return null;
        Utils.getPreferences()
                .edit()
                .putString("pref_key_level_general", result)
                .apply();
        return null;
    }
}
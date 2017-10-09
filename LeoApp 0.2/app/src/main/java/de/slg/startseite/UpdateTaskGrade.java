package de.slg.startseite;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.Utils;

public class UpdateTaskGrade extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        BufferedReader    in            = null;
        String            result        = "";
        boolean           hasConnection = hasActiveInternetConnection();
        if (!hasConnection)
            return false;
        try {
            int    id     = Utils.getUserID();
            String klasse = Utils.getUserStufe();
            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "user/updateKlasse.php?key=5453&userid=" + id + "&userklasse=" + klasse)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);

            in = null;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return !result.startsWith("-");
    }

    @Override
    protected void onPostExecute(Boolean b) {
        if (!b) {
            Utils.getController().getPreferences()
                    .edit()
                    .putBoolean("pref_key_level_has_to_be_synchronized", true)
                    .apply();
        }
    }

    private boolean hasActiveInternetConnection() {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    new URL("http://www.lunch.leo-ac.de")
                            .openConnection();
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return urlc.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }
}

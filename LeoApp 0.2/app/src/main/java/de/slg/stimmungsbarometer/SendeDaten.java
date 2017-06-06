package de.slg.stimmungsbarometer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class SendeDaten extends AsyncTask<AbstimmActivity.Wahl, Void, Void> {

    @Override
    protected Void doInBackground(AbstimmActivity.Wahl... wahls) {
        if (wahls[0] != null) {
            try {
                AbstimmActivity.Wahl w = wahls[0];
                URL url = new URL("http://moritz.liegmanns.de/stimmungsbarometer/vote.php?key=5453&voteid="+w.voteid+"&userid="+w.userid+"&grund="+w.grund.replace(' ', '+'));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader reader = new BufferedReader(inputStreamReader);
                while (reader.readLine() != null) {

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
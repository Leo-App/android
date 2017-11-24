package de.slg.stimmungsbarometer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class SendeDaten extends AsyncTask<Wahl, Void, Void> {
    @Override
    protected Void doInBackground(Wahl... wahls) {
        if (wahls[0] != null) {
            try {
                Wahl w = wahls[0];
                URLConnection connection = new URL(de.slg.leoapp.utility.Utils.BASE_URL_PHP + "stimmungsbarometer/vote.php?vid=" + w.voteid + "&uid=" + w.userid + "&grund=" + w.grund.replace(" ", "%20"))
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection
                                                .getInputStream(), "UTF-8"));
                while (reader.readLine() != null)
                    ;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

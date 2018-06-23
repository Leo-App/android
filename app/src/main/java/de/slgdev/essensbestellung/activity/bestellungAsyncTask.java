package de.slgdev.essensbestellung.activity;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by Florian on 21.06.2018.
 */

public class bestellungAsyncTask extends VoidCallbackTask<HashMap<String, String[]>> {

    private TaskStatusListener listener;
    @Override
    protected HashMap<String, String[]> doInBackground(Void... params) {
        if (Utils.isNetworkAvailable()) {
            try {
                //URLConnection connection = new URL(Utils.BASE_URL_PHP + "bestellung/bestellung_data.php")
                URLConnection connection = new URL("http://moritz.liegmanns.de/leoapp_php/bestellung/bestellung_data.php")
                        .openConnection();

                Utils.logError(connection);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line).append(System.getProperty("line.separator"));
                reader.close();

                String bestellungData = builder.toString();


                String[] bestellungLines = bestellungData.split("_next_");

                HashMap<String,String[]> gerichte = new HashMap<String,String[]>();
                int i = 0;
                for (i=0; i<bestellungLines.length; i++) {
                    String tag = bestellungLines[i];
                    String[] tagData = tag.split("_;_");
                    gerichte.put(tagData[0], tagData);

                    Log.d("AsyncTask GerichtData", i + ": " + tagData[0]);
                }
                if (gerichte == null)
                    Log.d("AsyncTask", "gerichte ist null");
                return gerichte;



            }
            catch (IOException e) {
                Utils.logError(e);
            }
        }


        return null;
    }

    protected void onPostExecute(HashMap<String, String[]> result) {listener.taskFinished(result);}
}

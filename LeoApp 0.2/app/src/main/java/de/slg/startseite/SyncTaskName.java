package de.slg.startseite;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class SyncTaskName extends AsyncTask<Void, Void, Boolean>{
    @Override
    protected Boolean doInBackground(Void... params) {

        if(!Utils.checkNetwork())
            return false;

        BufferedReader in = null;
        String result = "";

        try {

            URL interfaceDB = new URL("http://www.moritz.liegmanns.de/getName.php?key=5453&userid="+MainActivity.pref.getInt("pref_key_general_id", -1));


            in = null;
            in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
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
                    return false;
                }
        }

        if(result.startsWith("-"))
            return false;

        if(!result.equals(MainActivity.pref.getString("pref_key_username_general", "")))
            return false;

        SharedPreferences.Editor e = MainActivity.pref.edit();
        e.putString("pref_key_username_general", result);
        e.apply();

        return true;

    }

    @Override
    public void onPostExecute(Boolean b) {
        if(b) {
            Toast t = Toast.makeText(MainActivity.ref, Utils.getString(R.string.settings_name_changed), Toast.LENGTH_LONG);
            t.show();
        }
    }

}

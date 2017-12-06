package de.slg.leoapp.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.Utils;

public class SyncVoteTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/voted.php?uid=" + de.slg.leoapp.utility.Utils.getUserID())
                    .openConnection();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream(), "UTF-8"));
            de.slg.stimmungsbarometer.Utils.setLastVote(Integer.parseInt(reader.readLine()));
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (de.slg.stimmungsbarometer.Utils.showVoteOnStartup())
            Utils.getController().getMainActivity().notifyVote();
    }
}

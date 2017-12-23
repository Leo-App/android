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
        if (de.slg.stimmungsbarometer.utility.Utils.syncVote()) {
            try {
                URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/voted.php?uid=" + Utils.getUserID())
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                de.slg.stimmungsbarometer.utility.Utils.setLastVote(Integer.parseInt(reader.readLine()));
                reader.close();
            } catch (Exception e) {
                Utils.logError(e);
            }
        }
        if (!de.slg.stimmungsbarometer.utility.Utils.hasVoted()) {
            while (Utils.getController().getMainActivity() == null || Utils.getController().getActiveActivity() != Utils.getController().getMainActivity())
                ;
            Utils.getController().getMainActivity().notifyVote();
        }
        return null;
    }
}

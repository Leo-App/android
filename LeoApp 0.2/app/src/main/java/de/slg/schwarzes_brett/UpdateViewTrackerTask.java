package de.slg.schwarzes_brett;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.Utils;

public class UpdateViewTrackerTask extends AsyncTask<Integer, Void, Void> {
    private int remote;

    @Override
    protected Void doInBackground(Integer... params) {
        for (Integer cur : params) {
            remote = cur;
            try {
                URLConnection connection =
                        new URL(Utils.BASE_URL_PHP + "updateViewTracker.php?remote=" + remote)
                                .openConnection();

                connection.getInputStream();
                Utils.getController().getPreferences()
                        .edit()
                        .putString("pref_key_cache_vieweditems", getNewCacheString())
                        .apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getNewCacheString() {
        String        cache   = Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
        String[]      items   = cache.split("-");
        StringBuilder builder = new StringBuilder();
        for (String s : items) {
            if (s.matches(".+:" + remote))
                builder.append("-0:").append(remote);
            else
                builder.append("-").append(s);
        }
        return builder.toString().replaceFirst("-", "");
    }
}

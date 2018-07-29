package de.slgdev.schwarzes_brett.task;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.utility.Utils;

public class UpdateViewTrackerTask extends AsyncTask<Integer, Void, Void> {
    private int remote;

    @Override
    protected Void doInBackground(Integer... params) {
        for (Integer cur : params) {
            remote = cur;
            try {
                URLConnection connection =
                        new URL(Utils.BASE_URL_PHP + "schwarzesBrett/updateViewTracker.php?remote=" + remote)
                                .openConnection();

                connection.getInputStream();
                Utils.getController().getPreferences()
                        .edit()
                        .putString("pref_key_cache_vieweditems", getNewCacheString())
                        .apply();
            } catch (IOException e) {
                Utils.logError(e);
            }
        }
        return null;
    }

    private String getNewCacheString() {
        StringBuilder cache = new StringBuilder(Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", ""));

        String[] split = cache.toString().split("-");
        cache = new StringBuilder();
        for (String s : split) {
            if (s.equals(String.valueOf(remote)))
                continue;

            cache.append(s).append("-");
        }

        return cache.toString().endsWith("-") ? cache.toString().substring(0, cache.length() - 2) : cache.toString();
    }
}

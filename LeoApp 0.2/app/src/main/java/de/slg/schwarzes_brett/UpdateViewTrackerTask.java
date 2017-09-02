package de.slg.schwarzes_brett;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import de.slg.leoapp.Start;

public class UpdateViewTrackerTask extends AsyncTask<Integer, Void, Void> {

    private int remote;

    @Override
    protected Void doInBackground(Integer... params) {
        for (Integer cur : params) {
            Log.wtf("LeoApp", "syncing " + cur);
            remote = cur;
            try {
                URL updateURL = new URL("http://www.moritz.liegmanns.de/updateViewTracker.php?key=5453&remote=" + remote);
                updateURL.openConnection().getInputStream();
                Start.pref.edit().putString("pref_key_cache_vieweditems", getNewCacheString()).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getNewCacheString() {
        String        cache   = Start.pref.getString("pref_key_cache_vieweditems", "");
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

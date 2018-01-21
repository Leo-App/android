package de.slgdev.schwarzes_brett.utility;

import java.util.ArrayList;

public abstract class SchwarzesBrettUtils {

    public static long getLatestSchwarzesBrettDate() {
        return de.slgdev.leoapp.utility.Utils.getController().getPreferences().getLong("pref_key_general_last_notification_schwarzes_brett", 0);
    }

    public static void notifiedSchwarzesBrett(long date) {
        de.slgdev.leoapp.utility.Utils.getController().getPreferences()
                .edit()
                .putLong("pref_key_general_last_notification_schwarzes_brett", date)
                .apply();
    }

    public static boolean messageAlreadySeen(int id) {
        String   cache = de.slgdev.leoapp.utility.Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
        String[] items = cache.split("-");

        for (String s : items) {
            if (s.matches("[01]:" + id))
                return true;
        }
        return false;
    }

    public static ArrayList<Integer> getCachedIDs() {
        ArrayList<Integer> cachedValues = new ArrayList<>();
        String             cache        = de.slgdev.leoapp.utility.Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
        String[]           items        = cache.split("-");

        for (String s : items) {
            if (s.matches("1:.+"))
                cachedValues.add(Integer.parseInt(s.split(":")[1]));
        }
        return cachedValues;
    }
}
package de.slgdev.schwarzes_brett.utility;

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

    public static Integer[] getCachedIDs() {
        String cache = de.slgdev.leoapp.utility.Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");

        if (cache.equals(""))
            return new Integer[0];

        String[]  items = cache.split("-");
        Integer[] ids   = new Integer[items.length];

        for (int i = 0; i < items.length; i++) {
            ids[i] = Integer.parseInt(items[i]);
        }

        return ids;
    }
}
package de.slg.umfragen.utility;

/**
 * Utils.
 *
 * Erweitert {@link de.slg.leoapp.utility.Utils} lokal um allgemeine Methoden des Umfragefeatures.
 *
 * @author Gianni
 * @version 2017.1911
 * @since 0.6.2
 */

public class Utils {

    public static long getLatestSurveyDate() {
        return de.slg.leoapp.utility.Utils.getController().getPreferences().getLong("pref_key_general_last_notification_survey_students", 0);
    }

    public static void notifiedSurvey(long date) {
        de.slg.leoapp.utility.Utils.getController().getPreferences()
                .edit()
                .putLong("pref_key_general_last_notification_survey_students", date)
                .apply();
    }

}

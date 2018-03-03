package de.slgdev.stimmungsbarometer.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

public abstract class StimmungsbarometerUtils {

    public static boolean syncVote() {
        return !getLastVoteDate().equals(getCurrentDate());
    }

    public static boolean hasVoted() {
        return !syncVote() && getLastVote() != 0;
    }

    private static String getCurrentDate() {
        return new SimpleDateFormat("dd.MM", Locale.GERMANY).format(new Date());
    }

    public static int getCurrentMoodRessource() {
        switch (getLastVote()) {
            case 1:
                return R.drawable.ic_smiley_1;
            case 2:
                return R.drawable.ic_smiley_2;
            case 3:
                return R.drawable.ic_smiley_3;
            case 4:
                return R.drawable.ic_smiley_4;
            case 5:
                return R.drawable.ic_smiley_5;
            default:
                return R.drawable.ic_profile;
        }
    }

    private static String getLastVoteDate() {
        return Utils.getController().getPreferences().getString("pref_key_general_last_vote", "00.00");
    }

    public static int getLastVote() {
        return Utils.getController().getPreferences().getInt("pref_key_general_vote_id", -1);
    }

    public static void setLastVote(int vote) {
        Utils.getController().getPreferences().edit()
                .putString("pref_key_general_last_vote", getCurrentDate())
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }

    public static String getCurrentQuestion() {
        return Utils.getController()
                .getPreferences()
                .getString("stimmungsbarometer_frage","Wie geht es dir?");
    }

}

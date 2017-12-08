package de.slg.stimmungsbarometer.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.slg.leoapp.R;

public abstract class Utils {
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
                return R.drawable.ic_sentiment_very_satisfied_white_24px;
            case 2:
                return R.drawable.ic_sentiment_satisfied_white_24px;
            case 3:
                return R.drawable.ic_sentiment_neutral_white_24px;
            case 4:
                return R.drawable.ic_sentiment_dissatisfied_white_24px;
            case 5:
                return R.drawable.ic_sentiment_very_dissatisfied_white_24px;
            default:
                return R.drawable.ic_account_circle_black_24dp;
        }
    }

    private static String getLastVoteDate() {
        return de.slg.leoapp.utility.Utils.getController().getPreferences().getString("pref_key_general_last_vote", "00.00");
    }

    public static int getLastVote() {
        return de.slg.leoapp.utility.Utils.getController().getPreferences().getInt("pref_key_general_vote_id", -1);
    }

    public static void setLastVote(int vote) {
        de.slg.leoapp.utility.Utils.getController().getPreferences().edit()
                .putString("pref_key_general_last_vote", getCurrentDate())
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }
}

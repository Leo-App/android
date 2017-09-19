package de.slg.leoapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public abstract class Utils {
    public static final String BASE_URL      = "https://secureaccess.itac-school.de/slgweb/leoapp_php/";
    public static final String authorization = "Basic bGVvYXBwOmxlb2FwcA==";

    @SuppressLint("StaticFieldLeak")
    private static ActivityController controller;

    private static int currentlyDisplayedChatId = -1;

    public static boolean checkNetwork() {
        ConnectivityManager c = (ConnectivityManager) getController().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (c != null) {
            NetworkInfo n = c.getActiveNetworkInfo();
            if (n != null) {
                return n.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    public static String getAppVersionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getController().getContext().getPackageManager().getPackageInfo(getController().getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getController().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static ActivityController getController() {
        if (controller == null)
            controller = new ActivityController();
        return controller;
    }

    public static String getString(int id) {
        return getController().getContext().getString(id);
    }

    //Für Benachrichtigungen
    static long getLatestSchwarzesBrettDate() {
        return getController().getPreferences().getLong("pref_key_general_last_notification_schwarzes_brett", 0);
    }

    static void notifiedSchwarzesBrett(long date) {
        getController().getPreferences().edit()
                .putLong("pref_key_general_last_notification_schwarzes_brett", date)
                .apply();
    }

    public static int currentlyDisplayedChat() {
        return currentlyDisplayedChatId;
    }

    public static void setCurrentlyDisplayedChat(int cid) {
        currentlyDisplayedChatId = cid;
    }

    //Stimmungsbarometer
    static boolean showVoteOnStartup() {
        if (getLastVote().equals(getCurrentDate()))
            return false;
        boolean b = isVerified() && checkNetwork();
        if (b) {
            AsyncTask<Void, Void, Boolean> t = new AsyncTask<Void, Void, Boolean>() {
                private boolean b;

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        HttpsURLConnection connection = (HttpsURLConnection)
                                new URL(Utils.BASE_URL + "stimmungsbarometer/voted.php?key=5453&userid=" + getUserID())
                                        .openConnection();
                        connection.setRequestProperty("Authorization", Utils.authorization);
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                connection.getInputStream(), "UTF-8"));
                        b = !Boolean.parseBoolean(reader.readLine());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return b;
                }
            };
            t.execute();
            try {
                return t.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String getCurrentDate() {
        return new SimpleDateFormat("dd.MM").format(new Date());
    }

    public static int getCurrentMoodRessource() {
        int i = getController().getPreferences().getInt("pref_key_general_vote_id", -1);
        switch (i) {
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

    private static String getLastVote() {
        return getController().getPreferences().getString("pref_key_general_last_vote", "00.00");
    }

    public static void setLastVote(int vote) {
        getController().getPreferences().edit()
                .putString("pref_key_general_last_vote", getCurrentDate())
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }

    //User-Stuff
    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission(), "");
    }

    public static int getUserID() {
        return getController().getPreferences().getInt("pref_key_general_id", -1);
    }

    public static String getUserName() {
        return getController().getPreferences().getString("pref_key_username_general", "");
    }

    public static String getUserStufe() {
        return getController().getPreferences().getString("pref_key_level_general", "").replace("N/A", "");
    }

    public static int getUserPermission() {
        return getController().getPreferences().getInt("pref_key_general_permission", 0);
    }

    public static String getLehrerKuerzel() {
        return getController().getPreferences().getString("pref_key_kuerzel_general", "");
    }

    public static boolean isVerified() {
        return getUserID() > -1;
    }

    //Schwarzes Brett
    public static boolean messageAlreadySeen(int id) {
        String   cache = getController().getPreferences().getString("pref_key_cache_vieweditems", "");
        String[] items = cache.split("-");
        for (String s : items) {
            if (s.matches("[01]:" + id))
                return true;
        }
        return false;
    }

    public static ArrayList<Integer> getCachedIDs() {
        ArrayList<Integer> cachedValues = new ArrayList<>();
        String             cache        = getController().getPreferences().getString("pref_key_cache_vieweditems", "");
        String[]           items        = cache.split("-");
        for (String s : items) {
            if (s.matches("1:.+"))
                cachedValues.add(Integer.parseInt(s.split(":")[1]));
        }
        return cachedValues;
    }
}
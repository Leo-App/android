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

import de.slg.klausurplan.KlausurplanActivity;
import de.slg.messenger.ChatActivity;
import de.slg.messenger.DBConnection;
import de.slg.messenger.OverviewWrapper;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stundenplan.StundenplanDB;

@SuppressLint("StaticFieldLeak")
public abstract class Utils {
    public static Context context;

    private static MainActivity mainActivity;

    private static DBConnection dbConnection;
    private static OverviewWrapper overviewWrapper;
    private static ChatActivity chatActivity;
    private static int currentlyDisplayedChatId = -1;

    private static SchwarzesBrettActivity schwarzesBrettActivity;

    private static ReceiveService receiveService;

    private static KlausurplanActivity klausurplanActivity;

    private static StundenplanDB stundenplanDB;

    public static boolean checkNetwork() {
        ConnectivityManager c = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pInfo.versionName;

    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static String getString(int id) {
        return context.getString(id);
    }

    //Für Benachrichtigungen
    public static long getLatestMessageDate() {
        return Start.pref.getLong("pref_key_general_last_notification_messenger", 0);
    }

    static void notifiedMessenger() {
        Start.pref.edit()
                .putLong("pref_key_general_last_notification_messenger", getMDB().getLatestDateInDB())
                .apply();
    }

    static long getLatestSchwarzesBrettDate() {
        return Start.pref.getLong("pref_key_general_last_notification_schwarzes_brett", 0);
    }

    static void notifiedSchwarzesBrett(long date) {
        Start.pref.edit()
                .putLong("pref_key_general_last_notification_schwarzes_brett", date)
                .apply();
    }

    public static int currentlyDisplayedChat() {
        return currentlyDisplayedChatId;
    }

    public static void setCurrentlyDisplayedChat(int cid) {
        currentlyDisplayedChatId = cid;
    }

    //Datenbanken
    public static DBConnection getMDB() {
        if (dbConnection == null)
            dbConnection = new DBConnection(context);
        return dbConnection;
    }

    static void invalidateMDB() {
        dbConnection = null;
    }

    public static StundenplanDB getStundDB() {
        if (stundenplanDB == null)
            stundenplanDB = new StundenplanDB(context, 1);
        return stundenplanDB;
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
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL("http://moritz.liegmanns.de/stimmungsbarometer/voted.php?key=5453&userid=" + getUserID())
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
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
        int i = Start.pref.getInt("pref_key_general_vote_id", -1);
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
        return Start.pref.getString("pref_key_general_last_vote", "00.00");
    }

    public static void setLastVote(int vote) {
        Start.pref.edit()
                .putString("pref_key_general_last_vote", getCurrentDate())
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }

    //Registrierte Activities
    public static void registerOverviewWrapper(OverviewWrapper overviewWrapper) {
        Utils.overviewWrapper = overviewWrapper;
    }

    public static void registerChatActivity(ChatActivity chatActivity) {
        Utils.chatActivity = chatActivity;
    }

    public static OverviewWrapper getOverviewWrapper() {
        return overviewWrapper;
    }

    public static ChatActivity getChatActivity() {
        return chatActivity;
    }

    public static void registerKlausurplanActivity(KlausurplanActivity activity) {
        klausurplanActivity = activity;
    }

    public static KlausurplanActivity getKlausurplanActivity() {
        return klausurplanActivity;
    }

    public static void registerMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void registerSchwarzesBrettActivity(SchwarzesBrettActivity activity) {
        Utils.schwarzesBrettActivity = activity;
    }

    public static SchwarzesBrettActivity getSchwarzesBrettActivity() {
        return schwarzesBrettActivity;
    }

    //User-Stuff
    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission(), "");
    }

    public static int getUserID() {
        if (Start.pref == null)
            Start.initPref(context);
        return Start.pref.getInt("pref_key_general_id", -1);
    }

    public static String getUserName() {
        return Start.pref.getString("pref_key_username_general", "");
    }

    public static String getUserStufe() {
        try {
            return Start.pref.getString("pref_key_level_general", "").replace("N/A", "");
        } catch (ClassCastException e) {
            return "";
        }
    }

    public static int getUserPermission() {
        return Start.pref.getInt("pref_key_general_permission", 0);
    }

    public static String getLehrerKuerzel() {
        return Start.pref.getString("pref_key_kuerzel_general", "");
    }

    public static boolean isVerified() {
        return getUserID() > -1;
    }

    //Receive-Service
    static void registerReceiveService(ReceiveService receiveService) {
        Utils.receiveService = receiveService;
    }

    public static void receiveMessenger() {
        if (receiveService != null)
            receiveService.receiveMessages = true;
    }

    public static void receiveNews() {
        if (receiveService != null)
            receiveService.receiveNews = true;
    }

    //Schwarzes Brett
    public static boolean messageAlreadySeen(int id) {

        String cache = Start.pref.getString("pref_key_cache_vieweditems", "");
        String[] items = cache.split("-");

        for(String s : items) {

            if (s.matches("[01]:" + id))
                return true;

        }

        return false;

    }

    public static ArrayList<Integer> getCachedIDs() {

        ArrayList<Integer> cachedValues = new ArrayList<>();

        String cache = Start.pref.getString("pref_key_cache_vieweditems", "");
        String[] items = cache.split("-");

        for(String s : items) {

            if (s.matches("1:.+"))
                cachedValues.add(Integer.parseInt(s.split(":")[1]));

        }

        return cachedValues;

    }

}
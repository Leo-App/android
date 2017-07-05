package de.slg.leoapp;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import de.slg.messenger.ChatActivity;
import de.slg.messenger.DBConnection;
import de.slg.messenger.OverviewWrapper;

public abstract class Utils {
    public static Context context;
    private static DBConnection dbConnection;
    private static OverviewWrapper overviewWrapper;
    private static ChatActivity chatActivity;
    private static ReceiveService receiveService;

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

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission());
    }

    public static int getUserID() {
        return Start.pref.getInt("pref_key_general_id", -1);
    }

    public static String getUserName() {
        return Start.pref.getString("pref_key_username_general", context.getString(R.string.drawer_placeholder));
    }

    public static String getUserStufe() {
        int i = Start.pref.getInt("pref_key_level_general", -1);
        if (i >= 5)
            return new String[]{"5", "6", "7", "8", "9", "EF", "Q1", "Q2"}[i - 5];
        return "";
    }

    private static int getUserPermission() {
        return Start.pref.getInt("pref_key_general_permission", 0);
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
                .putString("pref_key_general_last_vote", getCurrentDate("dd.MM"))
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }

    public static String getString(int id) {
        return context.getString(id);
    }

    private static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static boolean isVerified() {
        return getUserID() > -1;
    }

    public static DBConnection getMessengerDBConnection() {
        if (dbConnection == null)
            dbConnection = new DBConnection(context);
        return dbConnection;
    }

    public static void registerOverviewWrapper(OverviewWrapper overviewWrapper) {
        Utils.overviewWrapper = overviewWrapper;
    }

    public static void registerChatActivity(ChatActivity chatActivity) {
        Utils.chatActivity = chatActivity;
    }

    static void registerReceiveService(ReceiveService receiveService) {
        Utils.receiveService = receiveService;
    }

    public static void receive() {
        if (receiveService != null)
            receiveService.receive();
    }

    public static OverviewWrapper getOverviewWrapper() {
        return overviewWrapper;
    }

    public static ChatActivity getChatActivity() {
        return chatActivity;
    }

    public static long getLatestMessageDate() {
        return Start.pref.getLong("pref_key_general_last_notification_messenger", 0);
    }

    static void notifiedMessenger() {
        Start.pref.edit()
                .putLong("pref_key_general_last_notification_messenger", getMessengerDBConnection().getLatestDateInDB())
                .apply();
    }

    static boolean showVoteOnStartup() {
        if (getLastVote().equals(getCurrentDate("dd.MM")))
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

    public static long getLatestSchwarzesBrettDate() {
        return Start.pref.getLong("pref_key_general_last_notification_schwarzes_brett", 0);
    }

    static void notifiedSchwarzesBrett() {
        Start.pref.edit()
                .putLong("pref_key_general_last_notification_schwarzes_brett", 0)
                .apply();
    }
}
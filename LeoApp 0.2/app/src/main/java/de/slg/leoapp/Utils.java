package de.slg.leoapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.messenger.AddGroupChatActivity;
import de.slg.messenger.ChatActivity;
import de.slg.messenger.ChatEditActivity;
import de.slg.messenger.DBConnection;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.AuswahlActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.stundenplan.StundenplanBildActivity;
import de.slg.stundenplan.StundenplanDB;

@SuppressLint("StaticFieldLeak")
public abstract class Utils {
    public static final String BASE_URL      = "https://secureaccess.itac-school.de/slgweb/leoapp_php/";
    public static final String BASE_DOMAIN      = "https://secureaccess.itac-school.de";
    public static final String authorization = "Basic bW9ybGllMDMxMDAwOnRyYWN5MzEw";
    public static final String authorizationPre = "Basic ";
    public static  Context           context;
    private static SharedPreferences preferences;

    //Activities
    private static MainActivity mainActivity;

    private static MessengerActivity    messengerActivity;
    private static ChatActivity         chatActivity;
    private static ChatEditActivity     chatEditActivity;
    private static AddGroupChatActivity addGroupChatActivity;

    private static SchwarzesBrettActivity schwarzesBrettActivity;

    private static StimmungsbarometerActivity stimmungsbarometerActivity;

    private static StundenplanActivity     stundenplanActivity;
    private static StundenplanBildActivity stundenplanBildActivity;
    private static AuswahlActivity         auswahlActivity;

    private static KlausurplanActivity klausurplanActivity;

    private static EssensQRActivity essensQRActivity;

    private static PreferenceActivity             preferenceActivity;
    private static NotificationPreferenceActivity notificationPreferenceActivity;

    //Datenbankverwaltungen
    private static DBConnection  dbConnection;
    private static StundenplanDB stundenplanDB;

    private static ReceiveService receiveService;

    private static int currentlyDisplayedChatId = -1;

    public static boolean checkNetwork() {
        ConnectivityManager c = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static String getString(int id) {
        return getContext().getString(id);
    }

    public static Context getContext() {
        if (context != null) {
            return context;
        } else if (mainActivity != null) {
            return mainActivity;
        } else if (messengerActivity != null) {
            return messengerActivity;
        } else if (chatActivity != null) {
            return chatActivity;
        } else if (chatEditActivity != null) {
            return chatEditActivity;
        } else if (addGroupChatActivity != null) {
            return addGroupChatActivity;
        } else if (schwarzesBrettActivity != null) {
            return schwarzesBrettActivity;
        } else if (stimmungsbarometerActivity != null) {
            return stimmungsbarometerActivity;
        } else if (stundenplanActivity != null) {
            return stundenplanActivity;
        } else if (stundenplanBildActivity != null) {
            return stundenplanBildActivity;
        } else if (auswahlActivity != null) {
            return auswahlActivity;
        } else if (klausurplanActivity != null) {
            return klausurplanActivity;
        } else if (essensQRActivity != null) {
            return essensQRActivity;
        } else if (preferenceActivity != null) {
            return preferenceActivity;
        } else if (notificationPreferenceActivity != null) {
            return notificationPreferenceActivity;
        }
        return null;
    }

    public static SharedPreferences getPreferences() {
        if (preferences == null && getContext() != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return preferences;
    }

    //Für Benachrichtigungen
    static long getLatestSchwarzesBrettDate() {
        return getPreferences().getLong("pref_key_general_last_notification_schwarzes_brett", 0);
    }

    static void notifiedSchwarzesBrett(long date) {
        getPreferences().edit()
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
            dbConnection = new DBConnection(getContext());
        return dbConnection;
    }

    static void invalidateMDB() {
        dbConnection = null;
    }

    public static StundenplanDB getStundDB() {
        if (stundenplanDB == null)
            stundenplanDB = new StundenplanDB(getContext());
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
        int i = getPreferences().getInt("pref_key_general_vote_id", -1);
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
        return getPreferences().getString("pref_key_general_last_vote", "00.00");
    }

    public static void setLastVote(int vote) {
        getPreferences().edit()
                .putString("pref_key_general_last_vote", getCurrentDate())
                .putInt("pref_key_general_vote_id", vote)
                .apply();
    }

    //Activities
    public static void registerMessengerActivity(MessengerActivity activity) {
        Utils.messengerActivity = activity;
    }

    public static void registerChatActivity(ChatActivity activity) {
        Utils.chatActivity = activity;
    }

    public static void registerChatEditActivity(ChatEditActivity activity) {
        chatEditActivity = activity;
    }

    public static void registerAddGroupChatActivity(AddGroupChatActivity activity) {
        addGroupChatActivity = activity;
    }

    public static void registerStimmungsbarometerActivity(StimmungsbarometerActivity activity) {
        stimmungsbarometerActivity = activity;
    }

    public static void registerStundenplanActivity(StundenplanActivity activity) {
        stundenplanActivity = activity;
    }

    public static void registerStundenplanBildActivity(StundenplanBildActivity activity) {
        stundenplanBildActivity = activity;
    }

    public static void registerAuswahlActivity(AuswahlActivity activity) {
        auswahlActivity = activity;
    }

    public static void registerMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static void registerSchwarzesBrettActivity(SchwarzesBrettActivity activity) {
        Utils.schwarzesBrettActivity = activity;
    }

    public static void registerKlausurplanActivity(KlausurplanActivity activity) {
        klausurplanActivity = activity;
    }

    public static void registerEssensQRActivity(EssensQRActivity activity) {
        Utils.essensQRActivity = activity;
    }

    static void registerPreferenceActivity(PreferenceActivity activity) {
        Utils.preferenceActivity = activity;
    }

    static void registerNotificationPreferenceActivity(NotificationPreferenceActivity activity) {
        Utils.notificationPreferenceActivity = activity;
    }

    public static MessengerActivity getMessengerActivity() {
        return messengerActivity;
    }

    public static ChatActivity getChatActivity() {
        return chatActivity;
    }

    public static KlausurplanActivity getKlausurplanActivity() {
        return klausurplanActivity;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    static SchwarzesBrettActivity getSchwarzesBrettActivity() {
        return schwarzesBrettActivity;
    }

    public static StundenplanActivity getStundenplanActivity() {
        return stundenplanActivity;
    }

    private static StundenplanBildActivity getStundenplanBildActivity() {
        return stundenplanBildActivity;
    }

    public static AuswahlActivity getAuswahlActivity() {
        return auswahlActivity;
    }

    private static StimmungsbarometerActivity getStimmungsbarometerActivity() {
        return stimmungsbarometerActivity;
    }

    private static ChatEditActivity getChatEditActivity() {
        return chatEditActivity;
    }

    private static AddGroupChatActivity getAddGroupChatActivity() {
        return addGroupChatActivity;
    }

    private static EssensQRActivity getEssensQRActivity() {
        return essensQRActivity;
    }

    private static PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    private static NotificationPreferenceActivity getNotificationPreferenceActivity() {
        return notificationPreferenceActivity;
    }

    public static void closeAll() {
        if (getChatEditActivity() != null)
            getChatEditActivity().finish();
        if (getChatActivity() != null)
            getChatActivity().finish();
        if (getAddGroupChatActivity() != null)
            getAddGroupChatActivity().finish();
        if (getMessengerActivity() != null)
            getMessengerActivity().finish();
        if (getAuswahlActivity() != null)
            getAuswahlActivity().finish();
        if (getStundenplanBildActivity() != null)
            getStundenplanBildActivity().finish();
        if (getStundenplanActivity() != null)
            getStundenplanActivity().finish();
        if (getSchwarzesBrettActivity() != null)
            getSchwarzesBrettActivity().finish();
        if (getStimmungsbarometerActivity() != null)
            getStimmungsbarometerActivity().finish();
        if (getEssensQRActivity() != null)
            getEssensQRActivity().finish();
        if (getKlausurplanActivity() != null)
            getKlausurplanActivity().finish();
        if (getNotificationPreferenceActivity() != null)
            getNotificationPreferenceActivity().finish();
        if (getPreferenceActivity() != null)
            getPreferenceActivity().finish();
        if (getMainActivity() != null)
            getMainActivity().finish();
    }

    //User-Stuff
    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission(), "");
    }

    public static int getUserID() {
        return getPreferences().getInt("pref_key_general_id", -1);
    }

    public static String getUserName() {
        return getPreferences().getString("pref_key_username_general", "");
    }

    public static String getUserStufe() {
        return getPreferences().getString("pref_key_level_general", "").replace("N/A", "");
    }

    public static int getUserPermission() {
        return getPreferences().getInt("pref_key_general_permission", 0);
    }

    public static String getLehrerKuerzel() {
        return getPreferences().getString("pref_key_kuerzel_general", "");
    }

    public static boolean isVerified() {
        return getUserID() > -1;
    }

    //Receive-Service
    static void registerReceiveService(ReceiveService receiveService) {
        Utils.receiveService = receiveService;
    }

    public static void receiveNews() {
        if (receiveService != null)
            receiveService.receiveNews = true;
    }

    //Schwarzes Brett
    public static boolean messageAlreadySeen(int id) {
        String   cache = getPreferences().getString("pref_key_cache_vieweditems", "");
        String[] items = cache.split("-");
        for (String s : items) {
            if (s.matches("[01]:" + id))
                return true;
        }
        return false;
    }

    public static ArrayList<Integer> getCachedIDs() {
        ArrayList<Integer> cachedValues = new ArrayList<>();
        String             cache        = getPreferences().getString("pref_key_cache_vieweditems", "");
        String[]           items        = cache.split("-");
        for (String s : items) {
            if (s.matches("1:.+"))
                cachedValues.add(Integer.parseInt(s.split(":")[1]));
        }
        return cachedValues;
    }

    //Authorization

    public static String toAuthFormat(String pPart1, String pPart2) {
        byte[] bytesEncoded = Base64.encode((pPart1+":"+pPart2).getBytes(), 0);
        return new String(bytesEncoded);
    }

}
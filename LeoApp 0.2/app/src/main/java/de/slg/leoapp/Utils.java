package de.slg.leoapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.lang.reflect.Array;

@SuppressLint("StaticFieldLeak")
public abstract class Utils {
    private static final String BASE_DOMAIN      = "https://secureaccess.itac-school.de/";
    public static final  String BASE_URL_PHP     = BASE_DOMAIN + "slgweb/leoapp_php/";
    static final         String URL_TOMCAT       = BASE_DOMAIN + "leoapp/";

    private static final String authorizationPre = "Basic ";
    public static final  String authorization    = authorizationPre + "bGVvYXBwOmxlb2FwcA==";

    @SuppressLint("StaticFieldLeak")
    private static ActivityController controller;

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

    public static Context getContext() {
        return getController().getContext();
    }

    public static String getString(int id) {
        return getController().getContext().getString(id);
    }

    //User-Stuff
    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission(), "");
    }

    public static int getUserID() {
        return getController().getPreferences().getInt("pref_key_general_id", -1);
    }

    public static String getUserName() {
        return getController().getPreferences().getString("pref_key_general_name", "");
    }

    public static String getUserDefaultName() {
        return getController().getPreferences().getString("pref_key_general_defaultusername", "");
    }

    public static String getUserStufe() {
        return getController().getPreferences().getString("pref_key_general_klasse", "").replace("N/A", "");
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

    public static String toAuthFormat(String user, String pass) {
        byte[] bytesEncoded = Base64.encode((user + ":" + pass).getBytes(), 0);
        return authorizationPre + new String(bytesEncoded);
    }

    public static <T> T[] concatArrays(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

}
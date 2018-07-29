package de.slgdev.essensbons.utility;

import de.slgdev.leoapp.utility.NetworkPerformance;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.Utils;

public abstract class EssensbonUtils {

    public static boolean mensaModeEnabled() {
        return Utils.getController().getPreferences().getBoolean("pref_key_mensa_mode", false);
    }

    public static boolean isLoggedIn() {
        return Utils.getController().getPreferences().getBoolean("pref_key_status_loggedin", false);
    }

    public static boolean isAutoSyncEnabled() {
        return Utils.getController().getPreferences().getBoolean("pref_key_qr_sync", false);
    }

    public static boolean isAutoFadeEnabled() {
        return Utils.getController().getPreferences().getBoolean("pref_key_qr_autofade", false);
    }

    public static int getFadeTime() {
        return Utils.getController().getPreferences().getInt("pref_key_qr_autofade_time", 3);
    }

    public static int getPreferredCamera() {
        return Utils.getController().getPreferences().getBoolean("pref_key_qr_camera", false) ? 1 : 0;
    }

    public static String getCustomerId() {
        return Utils.getController().getPreferences().getString("pref_key_qr_id", "00000");
    }

    public static String getPassword() {
        return Utils.getController().getPreferences().getString("pref_key_qr_pw", "");
    }

    public static void setCustomerId(String id) {
        Utils.getController().getPreferences().edit().putString("pref_key_qr_id", id).apply();
    }

    public static void setPassword(String pw) {
        Utils.getController().getPreferences().edit().putString("pref_key_qr_pw", pw).apply();
    }

    public static void setLoginStatus(boolean b) {
        Utils.getController().getPreferences().edit().putBoolean("pref_key_status_loggedin", b).apply();
    }

    public static boolean fastConnectionAvailable() {
        NetworkPerformance performance = NetworkUtils.getNetworkPerformance();
        return performance == NetworkPerformance.MEDIOCRE || performance == NetworkPerformance.EXCELLENT;
    }

}

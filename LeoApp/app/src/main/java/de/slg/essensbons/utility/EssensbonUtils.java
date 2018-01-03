package de.slg.essensbons.utility;

import de.slg.leoapp.utility.Utils;

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

}

package de.slg.leoapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.util.Base64;

/**
 * Utils
 *
 * Diese Klasse stellt allgemeine Methoden zur Verfügung, die von überall aufrufbar sind. Grafik- und Layoutmethoden werden durch {@link GraphicUtils} ergänzt.
 *
 * @version 2017.2610
 * @since 0.0.1
 * @author Moritz
 *
 */
@SuppressLint("StaticFieldLeak")
public abstract class Utils {
    /**
     * Basisdomain zum erreichen des LeoApp-Servers.
     */
    private static final String BASE_DOMAIN      = "https://secureaccess.itac-school.de/";
    /**
     * Pfad zu den PHP-Skripts auf dem Leo-Server.
     */
    public  static final String BASE_URL_PHP     = BASE_DOMAIN + "slgweb/leoapp_php/";
    /**
     * Pfad zum Application-Server.
     */
    public  static final String URL_TOMCAT       = BASE_DOMAIN + "leoapp/";

    private static final String authorizationPre = "Basic ";
    public  static final String authorization    = authorizationPre + "bGVvYXBwOmxlb2FwcA==";

    /* Allgemeines */

    /**
     * {@link UtilsController} Objekt.
     */
    private static UtilsController controller;

    /**
     * Prüft, ob das aktuelle Gerät mit dem Internet verbunden ist.
     *
     * @return true, falls eine aktive Netzwerkverbindung besteht; false, falls nicht
     */
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

    /**
     * Liefert den aktuellen Versionsnamen der App als String. Beispiel: "snapshot-0.5.6"
     *
     * @return Versionsnummer der App.
     */
    public static String getAppVersionName() {
        try {
            PackageInfo pInfo = getController().getContext().getPackageManager().getPackageInfo(getController().getContext().getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert das Objekt des laufenden {@link NotificationManager NotificationManagers}.
     *
     * @return aktueller NotificationManager
     */
    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getController().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Liefert den UtilsController, der alle aktiven Activities und Services verwaltet.
     *
     * @return Aktueller UtilsController
     * @see UtilsController
     */
    public static UtilsController getController() {
        if (controller == null)
            controller = new UtilsController();
        return controller;
    }

    /**
     * Liefert ein aktuelles Context-Objekt, basierend auf der aktuell laufenden Activity.
     *
     * @return Context-Objekt
     */
    public static Context getContext() {
        return getController().getContext();
    }

    /**
     * Liefert einen als String-Ressource hinterlegten String zurück.
     *
     * @param id String-ID des angefragten Strings.
     * @return String zu übergebener ID.
     */
    public static String getString(@StringRes int id) {
        return getController().getContext().getString(id);
    }

    /* User */

    /**
     * Liefert ein Objekt des aktuellen Users, mit allen wichtigen Informationen.
     *
     * @return {@link User}-Objekt
     * @see User
     */
    public static User getCurrentUser() {
        return new User(getUserID(), "Du", getUserStufe(), getUserPermission(), "");
    }

    /**
     * Liefert die einmalige User-ID.
     *
     * @return User-ID.
     */
    public static int getUserID() {
        return getController().getPreferences().getInt("pref_key_general_id", -1);
    }

    /**
     * Liefert den änderbaren Nutzernamen des Users.
     *
     * @return Aktueller Benutzername. Leerer String, wenn nicht verifiziert.
     */
    public static String getUserName() {
        return getController().getPreferences().getString("pref_key_general_name", "");
    }

    /**
     * Liefert den nicht änderbaren Benutzernamen des verbundenen Schullaccounts.
     *
     * @return Schulaccount-Benutzername. Leerer String, wenn nicht verifiziert.
     */
    public static String getUserDefaultName() {
        return getController().getPreferences().getString("pref_key_general_defaultusername", "");
    }

    /**
     * Liefert aktuelle Jahrgangsstufe des Users.
     *
     * @return Jahrgangsstufe des Users. Leerer String, wenn nicht verifiziert.
     */
    public static String getUserStufe() {
        return getController().getPreferences().getString("pref_key_general_klasse", "").replace("N/A", "");
    }

    /**
     * Liefert die Berechtigungsstufe des Users.
     *
     * @return 0: nicht verifiziert, 1: Schüler, 2: Lehrer, 3: Administrator
     */
    public static int getUserPermission() {
        return getController().getPreferences().getInt("pref_key_general_permission", 0);
    }

    /**
     * Liefert das Lehrerkürzel des Users, wenn der User kein Lehrer ist oder noch kein Kürzel angegeben wurde,
     * wird ein leerer String zurückgegeben.
     *
     * @return Lehrerkürzel.
     */
    public static String getLehrerKuerzel() {
        return getController().getPreferences().getString("pref_key_kuerzel_general", "");
    }

    /**
     * Prüft, ob das aktuelle Gerät verifiziert ist.
     *
     * @return true - Gerät ist verifiziert, false - Gerät ist nicht verifiziert.
     */
    public static boolean isVerified() {
        return getUserID() > -1;
    }

    /**
     * Konvertiert die Verifizierungsdaten des Users in ein Basic-Authentification Format.
     *
     * @param user Angegebener Nutzername des Schulaccounts.
     * @param pass Angegebenes Passwort.
     * @return Formatierte Verifizierungsdaten.
     */
    public static String toAuthFormat(String user, String pass) {
        byte[] bytesEncoded = Base64.encode((user + ":" + pass).getBytes(), 0);
        return authorizationPre + new String(bytesEncoded);
    }
}
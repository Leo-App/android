package de.slgdev.leoapp.utility;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.perf.FirebasePerformance;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.slgdev.leoapp.notification.NotificationTime;
import de.slgdev.leoapp.notification.NotificationType;

/**
 * Utils
 * <p>
 * Diese Klasse stellt allgemeine Methoden zur Verfügung, die von überall aufrufbar sind. Grafik- und Layoutmethoden werden durch {@link GraphicUtils} ergänzt.
 *
 * @author Moritz
 * @version 2017.2610
 * @since 0.0.1
 */
@SuppressLint("StaticFieldLeak")
@SuppressWarnings("all")
public abstract class Utils {

    /**
     * Domain zum Erreichen des Dev-Servers.
     */
    public static final String DOMAIN_DEV = "http://moritz.liegmanns.de/leoapp_php/";

    /**
     * Basisdomain zum Erreichen des LeoApp-Servers.
     */
    public static final String BASE_DOMAIN = "https://ucloud4schools.de/";

    /**
     * Basisdomain zum Erreichen des LeoApp-Userservers.
     */
    public static final String BASE_DOMAIN_SCHOOL = "https://secureaccess.itac-school.de/";

    /**
     * Pfad zum Application-Server.
     */
    public static final String URL_TOMCAT = "ws" + BASE_DOMAIN.substring(4) + "leoapp/";

    public static final String URL_TOMCAT_DEV = "ws://192.168.178.31:8080/";

    /**
     * Pfad zu den PHP-Skripts auf dem Leo-Server.
     */
    public static final String BASE_URL_PHP = BASE_DOMAIN + "ext/slg/leoapp_php/";

    /**
     * Pfad zum WebDAV-Verzeichnis
     */
    public static final String URL_WEBDAV = BASE_DOMAIN_SCHOOL + "slg/hcwebdav";

    /**
     * Pfad zum PHP-Ordner auf dem Schulserver
     */
    public static final String URL_PHP_SCHOOL = BASE_DOMAIN_SCHOOL + "slgweb/leoapp_php/";

    /**
     * Pfad zu den PHP Skripts der Essensbestellung
     */
    public static final String URL_LUNCH_LEO = "http://lunch.leo-ac.de/include/";

    /* Allgemeines */
    /**
     * {@link UtilsController} Objekt.
     */
    private static UtilsController controller;

    /**
     * Prüft, ob das aktuelle Gerät mit dem Internet verbunden ist.
     *
     * @return true, falls eine aktive Netzwerkverbindung besteht; false, falls nicht
     * @see Utils#getNetworkPerformance()
     */
    public static boolean isNetworkAvailable() {
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
     * Gibt die Geschwindigkeit der aktuellen Internetverbindung zurück.
     *
     * @return Aktuelle Netzwerkperformance, NOT_AVAILABLE wenn kein Internet verfügbar.
     */
    public static NetworkPerformance getNetworkPerformance() {
        ConnectivityManager c    = (ConnectivityManager) getController().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         info = c.getActiveNetworkInfo();

        if (info == null)
            return NetworkPerformance.NOT_AVAILABLE;

        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return NetworkPerformance.EXCELLENT;

        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkPerformance.INSUFFICIENT;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkPerformance.MEDIOCRE;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkPerformance.EXCELLENT;
                default:
                    return isNetworkAvailable() ? NetworkPerformance.INSUFFICIENT : NetworkPerformance.NOT_AVAILABLE;
            }
        }

        return isNetworkAvailable() ? NetworkPerformance.INSUFFICIENT : NetworkPerformance.NOT_AVAILABLE;
    }

    /**
     * Öffnet eine URL-Verbindung mit Authentifizierung.
     *
     * @param url - Gwünschte URL.
     * @param httpMethod - Zu nutzende HTTP Methode, siehe {@link RequestMethod}.
     * @return HTTP-Verbindung.
     * @throws IOException
     */
    public static HttpURLConnection openURLConnection(String url, RequestMethod httpMethod) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(httpMethod.name());
        connection.addRequestProperty("device", getCurrentDevice());
        connection.addRequestProperty("authentication", getAuthenticationToken());
        return connection;
    }

    /**
     * Öffnet eine URL-Verbindung mit Authentifizierung.
     *
     * @param url - Gwünschte URL.
     * @param httpMethod - Zu nutzende HTTP Methode, siehe {@link RequestMethod}.
     * @param contentType - Spezifizierter HTTP Content-Type für Requests mit Body (für Api-Aufrufe "application/json").
     * @return HTTP-Verbindung.
     * @throws IOException
     */
    public static HttpURLConnection openURLConnection(String url, RequestMethod httpMethod, String contentType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(httpMethod.name());
        connection.setRequestProperty("Content-type", contentType);
        connection.addRequestProperty("device", getCurrentDevice());
        connection.addRequestProperty("authentication", getAuthenticationToken());
        return connection;
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
            Utils.logError(e);
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

    /**
     * Gibt eine String Repräsentation des Parameters als Fehlermeldung in der Konsole aus.
     *
     * @param o Ausgabe im Android-Monitor
     */
    public static void logError(Object o) {
        if (o != null) {
            Log.wtf("LeoAppError", o.toString());
        } else {
            Log.wtf("LeoAppError", "null");
        }
    }

    /**
     * Gibt die Fehlermeldung des Throwables in der Konsole aus.
     *
     * @param t Fehlermeldung wird im Android-Monitor ausgegeben
     */
    public static void logError(Throwable t) {
        if (t != null) {
            Log.wtf("LeoAppError", Log.getStackTraceString(t));
        } else {
            Log.wtf("LeoAppError", "null");
        }
    }

    /**
     * Gibt eine String Repräsentation des Parameters als Debugmeldung in der Konsole aus.
     *
     * @param o Ausgabe im Android-Monitor
     */
    public static void logDebug(Object o) {
        if (o != null) {
            Log.d("LeoAppDebug", o.toString());
            if (o instanceof Iterable) {
                for (Object oI : (Iterable) o)
                    logDebug(oI);
            }
        } else {
            Log.d("LeoAppDebug", "null");
        }
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
     * Liefert den Identifier des aktuellen Geräts zurück.
     *
     * @return Deviceidentifier.
     */
    public static String getCurrentDevice() {
        return getController().getPreferences().getString("pref_key_cur_device", "");
    }

    /**
     * Setzt den DefaultUsername des Benutzers auf einen übergebenen Wert.
     *
     * @param defaultName Neuer DefaultName
     */
    public static void setUserDefaultName(String defaultName) {
        Utils.getController().getPreferences().edit()
                .putString("pref_key_general_defaultusername", defaultName)
                .apply();
    }

    /**
     * Setzt das Passwort des Users auf einen übergebenen Wert.
     *
     * @param password Neues Userpasswort
     */
    public static void setUserPassword(String password) {
        Utils.getController().getPreferences().edit()
                .putString("pref_key_general_password", password)
                .apply();
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
     * Konvertiert Anmeldedaten bestehend aus Nutzername und Passwort in ein Basic-Authentification Format.
     *
     * @param user Angegebener Nutzername des Schulaccounts.
     * @param pass Angegebenes Passwort.
     * @return Formatierte Verifizierungsdaten.
     */
    public static String toAuthFormat(String user, String pass) {
        return "Basic " + new String(Base64.encode((user + ":" + pass).getBytes(), 0));
    }

    /**
     * Liefert die Uhrzeit, zu der eine bestimmte LeoAppNotification gesendet wird.
     *
     * @param type Typ der LeoAppNotification
     * @return Uhrzeit zu spezifizierter LeoAppNotification
     */
    public static NotificationTime getNotificationTime(NotificationType type) {
        String   time;
        String[] parts;
        switch (type) {
            case FOODMARKS:
                time = Utils.getController().getPreferences().getString("pref_key_notification_time_foodmarks", "00:00");
                parts = time.split(":");
                return new NotificationTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            case TIMETABLE:
                time = Utils.getController().getPreferences().getString("pref_key_notification_time_schedule", "00:00");
                parts = time.split(":");
                return new NotificationTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            case KLAUSUR:
                time = Utils.getController().getPreferences().getString("pref_key_notification_time_test", "00:00");
                parts = time.split(":");
                return new NotificationTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            case MOOD:
                time = Utils.getController().getPreferences().getString("pref_key_notification_time_survey", "00:00");
                parts = time.split(":");
                return new NotificationTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            default:
                return new NotificationTime(0, 0);
        }
    }

    public static String getAuthenticationToken() {
        try {

            //TODO remove temp
            getController()
                    .getPreferences()
                    .edit()
                    .putString("auth_sum", "IkG62YuPC88TUD5L-0c7db8ade164b6b7822dfe84c09845420f2b5aeef8c80082fd3d3b978b7bb253")
                    .apply();
            getController()
                    .getPreferences()
                    .edit()
                    .putInt("pref_key_general_id", 1007)
                    .apply();
            //TODO remove temp

            String authsum = getController().getPreferences().getString("auth_sum", "null");

            SecureRandom random = new SecureRandom();
            byte salt[] = new byte[32];
            random.nextBytes(salt);
            String saltBase64 = Base64.encodeToString(salt, Base64.DEFAULT);

            long timestamp = System.currentTimeMillis()/10000;

            String baseString = authsum + saltBase64 + getUserID() + timestamp;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(baseString.getBytes(StandardCharsets.UTF_8));

            return getUserID() + "-" + bytesToHex(hash) + "-" + saltBase64 + "-" + timestamp%10;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
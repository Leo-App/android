package de.slgdev.leoapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@SuppressWarnings("all")
public abstract class NetworkUtils {

    /**
     * Prüft, ob das aktuelle Gerät mit dem Internet verbunden ist.
     *
     * @return true, falls eine aktive Netzwerkverbindung besteht; false, falls nicht
     * @see Utils#getNetworkPerformance()
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager c = (ConnectivityManager) Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        ConnectivityManager c = (ConnectivityManager) Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = c.getActiveNetworkInfo();

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
     * @param url        - Gewünschte URL.
     * @param httpMethod - Zu nutzende HTTP Methode, siehe {@link RequestMethod}.
     * @return HTTP-Verbindung.
     * @throws IOException -
     */
    public static HttpURLConnection openURLConnection(String url, RequestMethod httpMethod) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(httpMethod.name());
        connection.addRequestProperty("device", Utils.getDeviceIdentifier());
        connection.addRequestProperty("authentication", getAuthenticationToken());
        return connection;
    }

    /**
     * Öffnet eine URL-Verbindung mit Authentifizierung.
     *
     * @param url         - Gewünschte URL.
     * @param httpMethod  - Zu nutzende HTTP Methode, siehe {@link RequestMethod}.
     * @param contentType - Spezifizierter HTTP Content-Type für Requests mit Body (für Api-Aufrufe "application/json").
     * @param httpBody    - Inhalt des Request-Body als String
     * @return HTTP-Verbindung.
     * @throws IOException -
     */
    public static HttpURLConnection openURLConnection(String url, RequestMethod httpMethod, String contentType, String httpBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(httpMethod.name());
        connection.setRequestProperty("Content-type", contentType);
        connection.addRequestProperty("device", Utils.getDeviceIdentifier());
        connection.addRequestProperty("authentication", getAuthenticationToken());
        byte[] outputInBytes = httpBody.getBytes("ISO-8859-1");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();
        return connection;
    }

    public static JSONObject getJSONResponse(HttpURLConnection connection) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getResponseCode() == 200 ?
                            connection.getInputStream() : connection.getErrorStream()));

            String line;
            StringBuilder jsonResponse = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();
            //TODO remove
            Utils.logError("JSONResponse: "+jsonResponse);
            return new JSONObject(jsonResponse.toString());
        } catch (IOException | JSONException e) {
            Utils.logError(e);
            return null;
        }
    }

    public static String getAuthenticationToken() {
        try {
            String authsum = Utils.getDeviceChecksum();

            SecureRandom random = new SecureRandom();
            byte salt[] = new byte[32];
            random.nextBytes(salt);
            String saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP);

            long timestamp = System.currentTimeMillis() / 10000;

            String baseString = authsum + saltBase64 + Utils.getUserID() + timestamp;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(baseString.getBytes("UTF-8"));

            return Utils.getUserID() + "-" + StringUtils.bytesToHex(hash) + "-" + saltBase64 + "-" + timestamp % 10;

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}

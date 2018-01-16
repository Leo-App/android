package de.slg.essensbons.task;

import android.support.v4.app.Fragment;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.slg.essensbons.utility.Authenticator;
import de.slg.leoapp.R;
import de.slg.leoapp.task.general.CallbackTask;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.Utils;

public class EssensbonLoginTask extends CallbackTask<Void, Void, Authenticator> {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private Fragment target;

    public EssensbonLoginTask(Fragment target) {
        this.target = target;
    }

    @Override
    protected Authenticator doInBackground(Void... params) {
        if (fastConnectionAvailable()) {
            String pw = Utils.getController().getPreferences().getString("pref_key_qr_pw", "");
            String userId = null; //TODO
            try {
                byte[]         contents = pw.getBytes("UTF-8");
                MessageDigest  md       = MessageDigest.getInstance("MD5");
                byte[]         enc      = md.digest(contents);
                BufferedReader in;
                String         md5      = bytesToHex(enc);
                Utils.logDebug(md5);
                URL interfaceDB = new URL(Utils.URL_LUNCH_LEO + "qr_checkval.php?id=" + userId + "&auth=RW6SlQ&pw=" + md5);
                Utils.logDebug(interfaceDB.toString());
                in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("true")) {
                        return Authenticator.VALID;
                    }
                    if (inputLine.contains("false")) {
                        return Authenticator.NOT_VALID;
                    }
                }
                in.close();
            } catch (NoSuchAlgorithmException | IOException e) {
                Utils.logError(e);
            }
        } else
            return Authenticator.NO_CONNECTION;
        return Authenticator.NOT_VALID;
    }

    private boolean fastConnectionAvailable() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.lunch.leo-ac.de").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onPostExecute(Authenticator result) {
        target.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
        switch (result) {
            case VALID:
                break;
            case NOT_VALID:
                GraphicUtils.sendToast("Daten stimmen nicht überein");
                break;
            case NO_CONNECTION:
                GraphicUtils.sendToast("Keine Internetverbindung verfügbar");

        }

        super.onPostExecute(result);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void setLogin(boolean b) {
       Utils.getController().getPreferences()
                .edit()
                .putBoolean("pref_key_status_loggedin", b)
                .apply();
    }

}
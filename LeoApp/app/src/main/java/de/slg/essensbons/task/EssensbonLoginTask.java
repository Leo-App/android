package de.slg.essensbons.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.slg.essensbons.utility.Authenticator;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.leoapp.task.general.CallbackTask;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.utility.Utils;

public class EssensbonLoginTask extends CallbackTask<Void, Void, Authenticator> {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    @Override
    protected Authenticator doInBackground(Void... params) {
        if (EssensbonUtils.fastConnectionAvailable()) {
            String pw = EssensbonUtils.getPassword();
            String userId = EssensbonUtils.getCustomerId();
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
                        EssensbonUtils.setLoginStatus(true);
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

    @Override
    public void onPostExecute(Authenticator result) {
        for (TaskStatusListener listener : getListeners())
            listener.taskFinished(result);
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

}
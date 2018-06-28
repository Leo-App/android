package de.slgdev.essensbons.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.slgdev.essensbons.utility.Authenticator;
import de.slgdev.essensbons.utility.EssensbonUtils;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;

public class EssensbonLoginTask extends VoidCallbackTask<Authenticator> {

    @Override
    protected Authenticator doInBackground(Void... params) {

        if (EssensbonUtils.fastConnectionAvailable()) {

            String pw = EssensbonUtils.getPassword();
            String userId = EssensbonUtils.getCustomerId();
            try {
                byte[] contents = pw.getBytes("UTF-8");

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] enc = md.digest(contents);

                BufferedReader in;
                String md5 = Utils.bytesToHex(enc);

                URL interfaceDB = new URL(Utils.URL_LUNCH_LEO + "qr_checkval.php?id=" + userId + "&auth=RW6SlQ&pw=" + md5);
                Utils.logDebug(interfaceDB.toString());

                in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("true")) {
                        return Authenticator.VALID;
                    }
                    if (inputLine.contains("false")) {
                        EssensbonUtils.setLoginStatus(false);
                        return Authenticator.NOT_VALID;
                    }
                }
                in.close();
            } catch (NoSuchAlgorithmException | IOException e) {
                Utils.logError(e);
            }

        } else {
            return Authenticator.NO_CONNECTION;
        }

        return Authenticator.NOT_VALID;

    }

    @Override
    public void onPostExecute(Authenticator result) {
        for (TaskStatusListener listener : getListeners())
            listener.taskFinished(result);
    }

}
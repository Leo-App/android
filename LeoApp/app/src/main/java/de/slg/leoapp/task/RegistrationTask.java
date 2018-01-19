package de.slg.leoapp.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.ResponseCode;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.VerificationListener;
import de.slg.leoapp.utility.datastructure.List;

@SuppressLint("StaticFieldLeak")
public class RegistrationTask extends AsyncTask<Fragment, Void, ResponseCode> {

    private Fragment                   origin;
    private List<VerificationListener> listeners;

    public RegistrationTask() {
        listeners = new List<>();
    }

    @Override
    protected ResponseCode doInBackground(Fragment... params) {

        origin = params[0];

        String username = Utils.getUserDefaultName();
        String password = Utils.getController().getPreferences().getString("pref_key_general_password", "");

        StringBuilder checksum = new StringBuilder();
        boolean       teacher  = username.length() == 6;

        if (!Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {

            int permission = User.PERMISSION_SCHUELER;

            if (teacher) {
                permission = User.PERMISSION_LEHRER;
            }

            HttpURLConnection connection = (HttpURLConnection)
                    new URL(Utils.URL_PHP_SCHOOL + "verify.php")
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            int code = connection.getResponseCode();
            Utils.logDebug(code);

            if (code != 200) {
                if (code == 401) {
                    return ResponseCode.AUTH_FAILED;
                }
                return ResponseCode.SERVER_FAILED;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );

            String line;
            while ((line = reader.readLine()) != null) {
                checksum.append(line);
            }
            reader.close();

            Utils.logDebug(checksum);

            URLConnection connection2 =
                    new URL(Utils.BASE_URL_PHP + "user/addUser.php?name=" + Utils.getUserDefaultName() + "&permission=" + permission + "&checksum=" + checksum)
                            .openConnection();

            reader = new BufferedReader(
                    new InputStreamReader(
                            connection2.getInputStream(),
                            "UTF-8"
                    )
            );

            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            Utils.logDebug(result.toString());

            if (result.toString().startsWith("+")) {
                return ResponseCode.SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseCode.SERVER_FAILED;
    }

    public void registerListener(VerificationListener listener) {
        listeners.append(listener);
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        for (VerificationListener l : listeners) {
            l.onVerificationProcessed(code, origin);
        }
    }
}
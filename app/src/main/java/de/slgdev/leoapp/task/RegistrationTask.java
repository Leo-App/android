package de.slgdev.leoapp.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.VerificationListener;
import de.slgdev.leoapp.utility.datastructure.List;

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

        if (!Utils.isNetworkAvailable()) {
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

            reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "user/" +
                                            "addUser.php?" +
                                            "name=" + Utils.getUserDefaultName() + "&" +
                                            "permission=" + permission + "&" +
                                            "checksum=" + checksum
                            )
                                    .openConnection()
                                    .getInputStream(),
                            "UTF-8"
                    )
            );

            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            Utils.logDebug(builder.toString());

            if (builder.toString().startsWith("+")) {
                return ResponseCode.SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseCode.SERVER_FAILED;
    }

    public void addListener(VerificationListener listener) {
        listeners.append(listener);
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        for (VerificationListener l : listeners)
            l.onVerificationProcessed(code, origin);
    }
}
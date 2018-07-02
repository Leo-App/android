package de.slgdev.leoapp.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

public class VerificationTask extends VoidCallbackTask<ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Void... params) {
        String username = Utils.getUserDefaultName();
        String password = Utils.getController().getPreferences().getString("pref_key_general_password", "");
        StringBuilder checksum = new StringBuilder();

        if (!NetworkUtils.isNetworkAvailable()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            //TODO change dev url
            HttpURLConnection connection = (HttpURLConnection) new URL(Utils.DOMAIN_DEV + "verify.php")
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            int responseCode = connection.getResponseCode();

            if (responseCode == 401) {
                return ResponseCode.AUTH_FAILED;
            }

            if (responseCode != 200) {
                return ResponseCode.SERVER_FAILED;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                checksum.append(line);
            }
            reader.close();

            Utils.getController().getPreferences().edit()
                    .putString("auth_sum", checksum.toString())
                    .apply();

            return ResponseCode.SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode result) {
        for (TaskStatusListener l : getListeners ()) {
            l.taskFinished(result);
        }
    }
}
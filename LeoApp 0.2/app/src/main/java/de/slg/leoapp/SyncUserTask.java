package de.slg.leoapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class SyncUserTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        if (!Utils.checkNetwork()) {
            return null;
        }

        try {
            StringBuilder builder = new StringBuilder();

            String username = Utils.getUserDefaultName();
            String password = Utils.getController().getPreferences().getString("pref_key_password_general", "");

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "updateUser.php")
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            if (builder.charAt(0) != '-') {
                String result = builder.toString();

                int uid = Integer.parseInt(result.substring(0, result.indexOf(';')));
                result = result.substring(result.indexOf(';'));

                int permission = Integer.parseInt(result.substring(0, result.indexOf(';')));
                result = result.substring(result.indexOf(';'));

                String uname = result;

                Utils.getController().getPreferences()
                        .edit()
                        .putInt("pref_key_general_id", uid)
                        .putInt("pref_key_general_permission", permission)
                        .putString("pref_key_general_name", uname)
                        .apply();
            } else {
                Log.e("SyncUserTask", builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
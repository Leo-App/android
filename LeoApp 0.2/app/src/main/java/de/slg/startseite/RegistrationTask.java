package de.slg.startseite;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

import static android.view.View.GONE;

class RegistrationTask extends AsyncTask<String, Void, ResponseCode> {

    private AlertDialog dialog;

    RegistrationTask(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected ResponseCode doInBackground(String... params) {
        String result = "";
        boolean teacher = params[0].length() % 6 == 0;

        if (Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {

            String password = Utils.getPreferences().getString("pref_key_password_general", "");

            HttpsURLConnection checkConnection = (HttpsURLConnection)
                    new URL(Utils.BASE_DOMAIN + "/slg")
                            .openConnection();
            checkConnection.setRequestProperty("Authorization", Utils.authorizationPre + Utils.toAuthFormat(params[0], password));

            int code = checkConnection.getResponseCode();

            if (code != 200) {
                if (code == 401)
                    return ResponseCode.AUTH_FAILED;
                return ResponseCode.SERVER_FAILED;
            }

            String klasse = "N/A";
            if (teacher)
                klasse = "TEA";

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL + "addUser.php?key=5453&name=" + params[0] + "&permission=" + params[1] + "&klasse=" + klasse)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("<")) {
                    result += line;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseCode.SERVER_FAILED;
        }

        Log.d("RegistrationTask", result);

        if (result.startsWith("-")) {
            return ResponseCode.SERVER_FAILED;
        }

        if (result.startsWith("_new_")) {
            Utils.getPreferences()
                    .edit()
                    .putString("pref_key_username_general", params[0])
                    .putInt("pref_key_general_permission", teacher ? 2 : 1)
                    .putInt("pref_key_general_id", Integer.valueOf(result.replaceFirst("_new_", "")))
                    .apply();
            PreferenceActivity.setCurrentUsername(Utils.getUserName());
            return ResponseCode.SUCCESS;
        } else if (result.startsWith("_old_")) {
            String[] data = result.split("_");
            Utils.getPreferences()
                    .edit()
                    .putString("pref_key_level_general", data[data.length - 1])
                    .putString("pref_key_username_general", data[data.length - 3])
                    .putInt("pref_key_general_permission", teacher ? 2 : 1)
                    .putInt("pref_key_general_id", Integer.parseInt(data[data.length - 5]))
                    .apply();
            PreferenceActivity.setCurrentUsername(Utils.getUserName());
            return ResponseCode.SUCCESS;
        } else
            return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode b) {

        dialog.findViewById(R.id.progressBar1).setVisibility(GONE);

        switch (b) {

            case NO_CONNECTION:
                showSnackbarNoConnection();
                break;
            case AUTH_FAILED:
                showSnackbarAuthFailed();
                break;
            case SERVER_FAILED:
                showSnackbarServerFailed();
                break;
            case SUCCESS:
                Utils.getMainActivity().findViewById(R.id.card_view0).setVisibility(GONE);
                Calendar c = new GregorianCalendar();
                c.add(Calendar.YEAR, 1);
                c.set(Calendar.MONTH, Calendar.OCTOBER);
                c.set(Calendar.DAY_OF_MONTH, 1);
                String date = new SimpleDateFormat("dd.MM.yyyy").format(c.getTime());
                Utils.getPreferences()
                        .edit()
                        .putString("valid_until", date)
                        .apply();

                if (Utils.getUserPermission() == 2) {
                    Utils.getPreferences()
                            .edit()
                            .putBoolean("pref_key_notification_test", false)
                            .putBoolean("pref_key_notification_essensqr", false)
                            .putBoolean("pref_key_notification_news", false)
                            .putBoolean("pref_key_notification_schedule", false)
                            .apply();
                }
                dialog.dismiss();
                Toast.makeText(Utils.getContext(), "Erfolgreich verifiziert", Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void showSnackbarServerFailed() {
        final Snackbar cS = Snackbar.make(dialog.findViewById(R.id.snackbar), "Es ist etwas schiefgelaufen, versuche es zu einem späteren Zeitpunkt erneut", Snackbar.LENGTH_LONG);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void showSnackbarAuthFailed() {
        final Snackbar cS = Snackbar.make(dialog.findViewById(R.id.snackbar), "Benutzername und Passwort stimmen nicht überein", Snackbar.LENGTH_LONG);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void showSnackbarNoConnection() {

        final Snackbar cS = Snackbar.make(dialog.findViewById(R.id.snackbar), R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }
}

package de.slg.startseite;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
        String username = Utils.getUserDefaultName();
        String password = Utils.getController().getPreferences().getString("pref_key_password_general", "");

        String  result  = "";
        boolean teacher = username.length() == 6;

        if (!Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            String klasse = "N/A";
            if (teacher)
                klasse = "TEA";

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "addUser.php?key=5453&name=" + params[0] + "&permission=" + (teacher ? 2 : 1) + "&klasse=" + klasse)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            int code = connection.getResponseCode();

            if (code != 200) {
                if (code == 401)
                    return ResponseCode.AUTH_FAILED;
                return ResponseCode.SERVER_FAILED;
            }

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
            Utils.getController().getPreferences()
                    .edit()
                    .putString("pref_key_general_name", params[0])
                    .putInt("pref_key_general_permission", teacher ? 2 : 1)
                    .putInt("pref_key_general_id", Integer.valueOf(result.replaceFirst("_new_", "")))
                    .apply();

            Utils.getController().getPreferenceActivity().setCurrentUsername(Utils.getUserName());
            return ResponseCode.SUCCESS;
        } else if (result.startsWith("_old_")) {
            String[] data = result.split("_");
            Utils.getController().getPreferences()
                    .edit()
                    .putString("pref_key_general_klasse", data[data.length - 1])
                    .putString("pref_key_general_name", data[data.length - 3])
                    .putInt("pref_key_general_permission", teacher ? 2 : 1)
                    .putInt("pref_key_general_id", Integer.parseInt(data[data.length - 5]))
                    .apply();
            Utils.getController().getPreferenceActivity().setCurrentUsername(Utils.getUserName());
            return ResponseCode.SUCCESS;
        } else {
            return ResponseCode.SERVER_FAILED;
        }
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
                Utils.getController().getMainActivity().findViewById(R.id.card_view0).setVisibility(GONE);
                if (Utils.getUserPermission() == 2) {
                    Utils.getController().getPreferences()
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

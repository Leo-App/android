package de.slg.startseite;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.R;
import de.slg.leoapp.ResponseCode;
import de.slg.leoapp.SyncUserTask;
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
        String password = Utils.getController().getPreferences().getString("pref_key_general_password", "");

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
                    new URL(Utils.BASE_URL_PHP + "user/addUser.php?permission=" + (teacher ? 2 : 1) + "&klasse=" + klasse)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            int code = connection.getResponseCode();
            Log.d("code_register", String.valueOf(code));

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

            Log.d("RegistrationTask", result);

            if (result.startsWith("+")) {
                return ResponseCode.SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        switch (code) {
            case NO_CONNECTION:
                dialog.findViewById(R.id.progressBar1).setVisibility(GONE);
                showSnackbarNoConnection();
                break;
            case AUTH_FAILED:
                dialog.findViewById(R.id.progressBar1).setVisibility(GONE);
                showSnackbarAuthFailed();
                break;
            case SERVER_FAILED:
                dialog.findViewById(R.id.progressBar1).setVisibility(GONE);
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

                new SyncUserTask(dialog).execute();
                break;
        }
    }

    private void showSnackbarServerFailed() {
        final Snackbar snackbar = Snackbar.make(dialog.findViewById(R.id.snackbar), "Es ist etwas schiefgelaufen, versuche es zu einem späteren Zeitpunkt erneut", Snackbar.LENGTH_LONG);
        snackbar.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void showSnackbarAuthFailed() {
        final Snackbar snackbar = Snackbar.make(dialog.findViewById(R.id.snackbar), "Benutzername und Passwort stimmen nicht überein", Snackbar.LENGTH_LONG);
        snackbar.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void showSnackbarNoConnection() {
        final Snackbar snackbar = Snackbar.make(dialog.findViewById(R.id.snackbar), R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
        snackbar.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
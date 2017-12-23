package de.slg.startseite.task;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.R;
import de.slg.leoapp.task.SyncUserTask;
import de.slg.leoapp.utility.ResponseCode;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;

import static android.view.View.GONE;

public class RegistrationTask extends AsyncTask<String, Void, ResponseCode> {
    private AlertDialog dialog;

    public RegistrationTask(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected ResponseCode doInBackground(String... params) {
        String username = Utils.getUserDefaultName();
        String password = Utils.getController().getPreferences().getString("pref_key_general_password", "");

        String  checksum = "";
        boolean teacher  = username.length() == 6;

        if (!Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            int permission = 1;
            if (teacher) {
                permission = 2;
            }

            //TODO!!!
            HttpURLConnection connection = (HttpURLConnection)
                    new URL(Utils.URL_PHP_SCHOOL + "verify.php")
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            int code = connection.getResponseCode();
            Utils.logDebug(code);

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
                checksum += line;
            }
            reader.close();

            Utils.logDebug(checksum);

            URLConnection connection2 =
                    new URL(Utils.BASE_URL_PHP + "user/addUser.php?name=" + Utils.getUserDefaultName() + "&permission=" + permission + "&checksum=" + checksum)
                            .openConnection();

            reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection2
                                            .getInputStream(), "UTF-8"));
            String result = "";
            while ((line = reader.readLine()) != null)
                result += line;
            reader.close();

            Utils.logDebug(result);

            if (result.startsWith("+")) {
                return ResponseCode.SUCCESS;
            }
        } catch (IOException e) {
            Utils.logError(e);
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

                if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
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
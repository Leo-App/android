package de.slg.leoapp;

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

import de.slg.startseite.ResponseCode;

import static android.view.View.GONE;

public class SyncUserTask extends AsyncTask<Void, Void, ResponseCode> {
    private final AlertDialog dialog;

    SyncUserTask() {
        this.dialog = null;
    }

    public SyncUserTask(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected ResponseCode doInBackground(Void... params) {
        if (!Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            StringBuilder builder = new StringBuilder();

            String username = Utils.getUserDefaultName();
            String password = Utils.getController().getPreferences().getString("pref_key_password_general", "");

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "user/updateUser.php")
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

                return ResponseCode.SUCCESS;
            } else {
                Log.e("SyncUserTask", builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        if (dialog != null) {
            switch (code) {
                case NO_CONNECTION:
                    dialog.findViewById(R.id.progressBar1).setVisibility(GONE);
                    showSnackbarNoConnection();
                    break;
                case SERVER_FAILED:
                    dialog.findViewById(R.id.progressBar1).setVisibility(GONE);
                    showSnackbarServerFailed();
                    break;
                case SUCCESS:
                    dialog.dismiss();
                    Toast.makeText(Utils.getContext(), "Verifizierung abgeschlossen!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void showSnackbarServerFailed() {
        final Snackbar cS = Snackbar.make(dialog.findViewById(R.id.snackbar), "Es ist etwas schiefgelaufen, bitte starte die App erneut", Snackbar.LENGTH_LONG);
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
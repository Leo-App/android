package de.slg.leoapp;

import android.content.Intent;
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

public class SyncUserTask extends AsyncTask<Void, Void, ResponseCode> {
    private final AlertDialog dialog;
    private final boolean     refresh;

    SyncUserTask() {
        this.dialog = null;
        this.refresh = !Utils.isVerified();
    }

    public SyncUserTask(AlertDialog dialog) {
        this.dialog = dialog;
        this.refresh = !Utils.isVerified();
    }

    @Override
    protected ResponseCode doInBackground(Void... params) {
        if (!Utils.checkNetwork()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            StringBuilder builder = new StringBuilder();

            HttpURLConnection connection =
                    (HttpURLConnection) new URL(Utils.BASE_URL_PHP + "user/updateUser.php?name=" + Utils.getUserDefaultName())
                            .openConnection();

//            connection.setRequestProperty("Authorization", Utils.toAuthFormat(username, password));

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            String result = builder.toString();
            if (result.startsWith("-")) {
                throw new IOException(result);
            }

            int uid = Integer.parseInt(result.substring(0, result.indexOf(';')));
            result = result.substring(result.indexOf(';') + 1);

            int permission = Integer.parseInt(result.substring(0, result.indexOf(';')));
            result = result.substring(result.indexOf(';') + 1);

            String uname = result;

            Utils.getController().getPreferences()
                    .edit()
                    .putInt("pref_key_general_id", uid)
                    .putInt("pref_key_general_permission", permission)
                    .putString("pref_key_general_name", uname)
                    .apply();

            return ResponseCode.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        if (refresh) {
            switch (code) {
                case NO_CONNECTION:
                    dialog.findViewById(R.id.progressBar1).setVisibility(View.GONE);
                    showSnackbarNoConnection();
                    break;
                case SERVER_FAILED:
                    dialog.findViewById(R.id.progressBar1).setVisibility(View.GONE);
                    showSnackbarServerFailed();
                    break;
                case SUCCESS:
                    dialog.dismiss();
                    Toast.makeText(Utils.getContext(), "Verifizierung abgeschlossen!", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), Start.class)
                                    .putExtra("updateUser", false)
                                    .addCategory(Intent.CATEGORY_LAUNCHER)
                                    .setAction(Intent.ACTION_MAIN));
                        }
                    }, 200);
                    break;
            }
        }
    }

    private void showSnackbarServerFailed() {
        final Snackbar snackbar = Snackbar.make(dialog.findViewById(R.id.snackbar), R.string.error_snackbar, Snackbar.LENGTH_LONG);
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
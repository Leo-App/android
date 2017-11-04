package de.slg.leoapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

class UpdateTaskName extends AsyncTask<String, Void, ReturnValues> {
    private final String old;

    UpdateTaskName(String oldUsername) {
        old = oldUsername;
    }

    @Override
    protected ReturnValues doInBackground(String... params) {
        BufferedReader in     = null;
        String         result = "";
        if (!Utils.checkNetwork())
            return ReturnValues.NO_CONNECTION;
        try {
            int    id       = Utils.getUserID();
            String username = URLEncoder.encode(Utils.getUserName(), "UTF-8");
            URLConnection connection =
                    new URL(Utils.BASE_URL_PHP + "user/updateUsername.php?uid=" + id + "&uname=" + username)
                            .openConnection();

            in = null;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnValues.ERROR;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (result.startsWith("-")) {
            if (result.startsWith("-username")) {
                return ReturnValues.USERNAME_TAKEN;
            }
            return ReturnValues.ERROR;
        }
        return ReturnValues.SUCCESSFUL;
    }

    @Override
    protected void onPostExecute(ReturnValues b) {
        switch (b) {
            case USERNAME_TAKEN:
                resetName();
                showSnackbar2();
                break;
            case NO_CONNECTION:
                resetName();
                showSnackbar();
                break;
            case ERROR:
                resetName();
                showSnackbar3();
                break;
            case SUCCESSFUL:
                Toast t = Toast.makeText(Utils.getContext(), Utils.getString(R.string.settings_toast_username_success), Toast.LENGTH_LONG);
                t.show();
                Utils.getController().getProfileActivity().initProfil();
                Utils.getController().getProfileActivity().initNavigationView();
                break;
        }
    }

    private void resetName() {
        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_general_name", old)
                .apply();
    }

    private void showSnackbar() {
        final Snackbar cS = Snackbar.make(Utils.getController().getProfileActivity().getCoordinatorLayout(), R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void showSnackbar2() {
        final Snackbar cS = Snackbar.make(Utils.getController().getProfileActivity().getCoordinatorLayout(), R.string.settings_snackbar_username_taken, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void showSnackbar3() {
        final Snackbar cS = Snackbar.make(Utils.getController().getProfileActivity().getCoordinatorLayout(), R.string.error, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }
}
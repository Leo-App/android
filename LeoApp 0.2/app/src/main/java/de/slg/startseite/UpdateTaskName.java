package de.slg.startseite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;

public class UpdateTaskName extends AsyncTask<String, Void, ReturnValues> {
    private PreferenceActivity c;
    private boolean connection;
    private String old;

    public UpdateTaskName(PreferenceActivity c, String oldUsername) {

        this.c = c;
        old = oldUsername;

    }

    @Override
    protected ReturnValues doInBackground(String... params) {

        BufferedReader in = null;
        String result = "";

        connection = hasActiveInternetConnection();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);

        if (!connection)
            return ReturnValues.NO_CONNECTION;

        try {

            int id = pref.getInt("pref_key_general_id", -1);
            String username = pref.getString("pref_key_username_general", "").replace(' ', '+');

            URL interfaceDB = new URL("http://moritz.liegmanns.de/updateUsername.php?key=5453&userid=" + id + "&username=" + username);

            in = null;
            in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
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

            if (result.startsWith("-username"))
                return ReturnValues.USERNAME_TAKEN;

            return ReturnValues.ERROR;

        }

        return ReturnValues.SUCCESSFUL;
    }

    @Override
    protected void onPostExecute(ReturnValues b) {

        c.hideProgressBar();

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
                Toast t = Toast.makeText(c, c.getString(R.string.settings_toast_username_success), Toast.LENGTH_LONG);
                t.show();
                break;
        }

        c.findPreference("pref_key_username_general").setSummary(PreferenceManager.getDefaultSharedPreferences(c).getString("pref_key_username_general", ""));
        PreferenceActivity.setCurrentUsername(PreferenceManager.getDefaultSharedPreferences(c).getString("pref_key_username_general", ""));

    }

    private void resetName() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = pref.edit();
        e.putString("pref_key_username_general", old);
        e.apply();

    }

    private void showSnackbar() {

        final Snackbar cS = Snackbar.make(c.getCoordinatorLayout(), R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(c.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();

    }

    private void showSnackbar2() {

        final Snackbar cS = Snackbar.make(c.getCoordinatorLayout(), R.string.settings_snackbar_username_taken, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(c.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();

    }

    private void showSnackbar3() {

        final Snackbar cS = Snackbar.make(c.getCoordinatorLayout(), R.string.error, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(c.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();

    }


    private boolean hasActiveInternetConnection() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.lunch.leo-ac.de").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return urlc.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

}

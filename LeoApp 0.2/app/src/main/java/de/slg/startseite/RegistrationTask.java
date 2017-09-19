package de.slg.startseite;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
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

class RegistrationTask extends AsyncTask<String, Void, Boolean> {
    private boolean connection;
    int code;

    @Override
    protected Boolean doInBackground(String... params) {
        String result = "";
        connection = hasActiveInternetConnection();
        if (!connection) {
            return false;
        }

        try {

            String password = Utils.getPreferences().getString("pref_key_password_general", "");

            HttpsURLConnection checkConnection = (HttpsURLConnection)
                    new URL(Utils.BASE_DOMAIN + "/slg")
                            .openConnection();
            checkConnection.setRequestProperty("Authorization", Utils.authorizationPre + Utils.toAuthFormat(params[0], password));

            code = checkConnection.getResponseCode();

            if(checkConnection.getResponseCode() != 200)
                return false;
       /*     String klasse = "N/A";
            if (params[1].equals("2"))
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

            reader.close(); */
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
/*
        Log.d("RegistrationTask", result);

        if (result.startsWith("-")) {
            return false;
        }

        if (result.startsWith("_new_")) {
            Utils.getPreferences()
                    .edit()
                    .putString("pref_key_username_general", params[0])
                    .putInt("pref_key_general_permission", Integer.parseInt(params[1]))
                    .putInt("pref_key_general_id", Integer.valueOf(result.replaceFirst("_new_", "")))
                    .apply();
            PreferenceActivity.setCurrentUsername(Utils.getUserName());
            return true;
        } else if (result.startsWith("_old_")) {
            String[] data = result.split("_");
            Utils.getPreferences()
                    .edit()
                    .putString("pref_key_level_general", data[data.length - 1])
                    .putString("pref_key_username_general", data[data.length - 3])
                    .putInt("pref_key_general_permission", Integer.parseInt(params[1]))
                    .putInt("pref_key_general_id", Integer.parseInt(data[data.length - 5]))
                    .apply();
            PreferenceActivity.setCurrentUsername(Utils.getUserName());
            return true;
        } else
            return false; */
    return true;
    }

    @Override
    protected void onPostExecute(Boolean b) {

        Toast.makeText(Utils.getContext(), "WORKED "+code, Toast.LENGTH_LONG).show();

     /*   if (b) {
            Utils.getMainActivity().title.setTextColor(Color.GREEN);
            Utils.getMainActivity().title.setText(Utils.getString(R.string.title_info_auth));
            Utils.getMainActivity().info.setText(Utils.getString(R.string.summary_info_auth_success));
            Utils.getMainActivity().verify.setText(Utils.getString(R.string.button_info_noreminder));
            Utils.getMainActivity().progressBar.setVisibility(View.GONE);
            Utils.getMainActivity().title.setVisibility(View.VISIBLE);
            Utils.getMainActivity().info.setVisibility(View.VISIBLE);
            Utils.getMainActivity().verify.setVisibility(View.VISIBLE);
            Utils.getMainActivity().dismiss.setVisibility(View.GONE);
            Utils.getMainActivity().initFeatureCards();
            Utils.getMainActivity().initNavigationView();

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
        } else {
            if (!connection) {
                showSnackbar();
                Utils.getMainActivity().progressBar.setVisibility(View.GONE);
                Utils.getMainActivity().title.setVisibility(View.VISIBLE);
                Utils.getMainActivity().info.setVisibility(View.VISIBLE);
                Utils.getMainActivity().verify.setVisibility(View.VISIBLE);
            } else {
                Utils.getMainActivity().info.setText(Utils.getString(R.string.summary_info_auth_failed));
                Utils.getMainActivity().title.setText(Utils.getString(R.string.error));
                Utils.getMainActivity().progressBar.setVisibility(View.GONE);
                Utils.getMainActivity().title.setVisibility(View.VISIBLE);
                Utils.getMainActivity().info.setVisibility(View.VISIBLE);
                Utils.getMainActivity().verify.setVisibility(View.VISIBLE);
            }
        } */
    }

    private void showSnackbar() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final Snackbar cS = Snackbar.make(Utils.getMainActivity().cooredinatorLayout, R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
                cS.setActionTextColor(Color.WHITE);
                cS.setAction(Utils.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cS.dismiss();
                    }
                });
                cS.show();
            }
        };
        handler.postDelayed(r, 1000);
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

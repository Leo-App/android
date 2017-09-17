package de.slg.startseite;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

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

    private final MainActivity c;
    private       boolean      connection;

    RegistrationTask(MainActivity c) {
        this.c = c;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        BufferedReader in     = null;
        String         result = "";
        connection = hasActiveInternetConnection();
        Log.d("LeoApp", "test");
        if (!connection)
            return false;
        try {
            String klasse = "N/A";
            if (params[1].equals("2"))
                klasse = "TEA";
            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL + "addUser.php?key=5453&name=" + params[0] + "&permission=" + params[1] + "&klasse=" + klasse)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);
            Log.e("TAG", connection.getURL().toString());
            in = null;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }
        Log.d("LeoApp", "finished");
        Log.d("LeoApp", result);
        if (result.startsWith("-"))
            return false;
        if (result.startsWith("_new_")) {
            Utils.getPreferences()
                    .edit()
                    .putString("pref_key_username_general", params[0])
                    .putInt("pref_key_general_permission", Integer.parseInt(params[1]))
                    .putInt("pref_key_general_id", Integer.valueOf(result.replaceFirst("_new_", "")))
                    .apply();
            PreferenceActivity.setCurrentUsername(Utils.getPreferences().getString("pref_key_username_general", ""));
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
            PreferenceActivity.setCurrentUsername(Utils.getPreferences().getString("pref_key_username_general", ""));
            return true;
        } else
            return false;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            MainActivity.title.setTextColor(Color.GREEN);
            MainActivity.title.setText(c.getString(R.string.title_info_auth));
            MainActivity.info.setText(c.getString(R.string.summary_info_auth_success));
            MainActivity.verify.setText(c.getString(R.string.button_info_noreminder));
            MainActivity.progressBar.setVisibility(View.GONE);
            MainActivity.title.setVisibility(View.VISIBLE);
            MainActivity.info.setVisibility(View.VISIBLE);
            MainActivity.verify.setVisibility(View.VISIBLE);
            MainActivity.dismiss.setVisibility(View.GONE);
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

            if (Utils.getPreferences().getInt("pref_key_general_permission", -1) == 2) {
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
                MainActivity.progressBar.setVisibility(View.GONE);
                MainActivity.title.setVisibility(View.VISIBLE);
                MainActivity.info.setVisibility(View.VISIBLE);
                MainActivity.verify.setVisibility(View.VISIBLE);
            } else {
                MainActivity.info.setText(c.getString(R.string.summary_info_auth_failed));
                MainActivity.title.setText(c.getString(R.string.error));
                MainActivity.progressBar.setVisibility(View.GONE);
                MainActivity.title.setVisibility(View.VISIBLE);
                MainActivity.info.setVisibility(View.VISIBLE);
                MainActivity.verify.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showSnackbar() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final Snackbar cS = Snackbar.make(MainActivity.cooredinatorLayout, R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
                cS.setActionTextColor(Color.WHITE);
                cS.setAction(c.getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
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

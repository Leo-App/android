package de.slg.startseite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;

public class RegistrationTask extends AsyncTask<String, Void, Boolean> {

    private MainActivity c;
    private boolean connection;

    public RegistrationTask(MainActivity c) {

        this.c = c;

    }

    @Override
    protected Boolean doInBackground(String... params) {

        BufferedReader in = null;
        String result = "";

        connection = hasActiveInternetConnection();

        Log.d("LeoApp", "test");

        if(!connection)
            return false;

        try {

            int klasse = params[1].equals("2") ? 0 : -1;
            URL interfaceDB = new URL("http://www.moritz.liegmanns.de/addUser.php?key=5453&name="+params[0]+"&permission="+params[1]+"&klasse="+klasse);

            Log.d("LeoApp", "URL SET");

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

        if(result.startsWith("-"))
            return false;

        if(result.startsWith("_new_")) {

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor e = pref.edit();
            e.putString("pref_key_username_general", params[0]);
            e.putInt("pref_key_general_permission", Integer.parseInt(params[1]));
            e.putInt("pref_key_general_id", Integer.valueOf(result.replaceFirst("_new_", "")));
            e.apply();

            PreferenceActivity.setCurrentUsername(pref.getString("pref_key_username_general", ""));

            return true;

        } else if(result.startsWith("_old_")) {

            Log.d("LeoApp", "inRightPart");

            String[] data = result.split("_");

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor e = pref.edit();
            e.putInt("pref_key_general_permission", Integer.parseInt(params[1]));
            e.putString("pref_key_username_general", data[data.length-1]);
            e.putInt("pref_key_general_id", Integer.parseInt(data[data.length-3]));
            e.apply();

            PreferenceActivity.setCurrentUsername(pref.getString("pref_key_username_general", ""));

            return true;

        } else
            return false;

    }

    @Override
    protected void onPostExecute(Boolean b) {
        if(b) {

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);

            Log.d("LeoApp", pref.getString("pref_key_username_general", "Notset"));

            MainActivity.title.setTextColor(Color.GREEN);
            MainActivity.title.setText(c.getString(R.string.title_info_auth));
            MainActivity.info.setText(c.getString(R.string.summary_info_auth_success));
            MainActivity.verify.setText(c.getString(R.string.button_info_noreminder));

            MainActivity.pb.setVisibility(View.GONE);
            MainActivity.title.setVisibility(View.VISIBLE);
            MainActivity.info.setVisibility(View.VISIBLE);
            MainActivity.verify.setVisibility(View.VISIBLE);

            MainActivity.setVerified();

        } else {

            if(!connection) {
                showSnackbar();
                MainActivity.pb.setVisibility(View.GONE);
                MainActivity.title.setVisibility(View.VISIBLE);
                MainActivity.info.setVisibility(View.VISIBLE);
                MainActivity.verify.setVisibility(View.VISIBLE);
            } else {
                MainActivity.info.setText(c.getString(R.string.summary_info_auth_failed));
                MainActivity.title.setText(c.getString(R.string.error));
                MainActivity.pb.setVisibility(View.GONE);
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
                final Snackbar cS = Snackbar.make(MainActivity.v, R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
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

    public boolean hasActiveInternetConnection() {

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

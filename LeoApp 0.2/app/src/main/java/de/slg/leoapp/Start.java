package de.slg.leoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.slg.startseite.MainActivity;

public class Start extends Activity {
    public static SharedPreferences pref;

    public static void initPref(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.context = getApplicationContext();
        initPref(getApplicationContext());

        final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("show_dialog", Utils.showVoteOnStartup());

        startService(new Intent(getApplicationContext(), ReceiveService.class));
        startService(new Intent(getApplicationContext(), NotificationService.class));

        startActivity(main);

        finish();
    }
}
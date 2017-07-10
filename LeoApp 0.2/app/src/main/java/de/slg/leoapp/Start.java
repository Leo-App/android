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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initPref(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        Utils.context = getApplicationContext();

        final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("show_dialog", Utils.showVoteOnStartup());

        startService(new Intent(getApplicationContext(), ReceiveService.class));
        startService(new Intent(getApplicationContext(), NotificationService.class));

        startActivity(main);

        finish();
    }

    public static void initPref(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
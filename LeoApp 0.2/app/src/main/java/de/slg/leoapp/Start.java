package de.slg.leoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.AbstimmActivity;

public class Start extends Activity {

    public static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Utils.context = getApplicationContext();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        if (Utils.isVerified() && !Utils.getLastVote().equals(Utils.getCurrentDate("dd.MM"))) {
            startActivity(new Intent(getApplicationContext(), AbstimmActivity.class));
        }

        startService(new Intent(getApplicationContext(), ReceiveService.class));
        finish();
    }
}
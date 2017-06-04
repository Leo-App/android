package de.slg.leoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.AbstimmActivity;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.context = getApplicationContext();
        MainActivity.initPreference(getApplicationContext());
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        if (Utils.isVerified() && !Utils.getLastVote().equals(Utils.getCurrentDate("dd.MM"))) {
            startActivity(new Intent(getApplicationContext(), AbstimmActivity.class));
        }
        startService(new Intent(getApplicationContext(), ReceiveService.class));
        finish();
    }
}
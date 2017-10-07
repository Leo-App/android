package de.slg.leoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.slg.startseite.MainActivity;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().setContext(getApplicationContext());

        final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("show_dialog", Utils.showVoteOnStartup());

        startService(new Intent(getApplicationContext(), ReceiveService.class));
        startService(new Intent(getApplicationContext(), NotificationService.class));

        startActivity(main);
        finish();
    }
}
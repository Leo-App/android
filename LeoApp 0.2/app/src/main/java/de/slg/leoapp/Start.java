package de.slg.leoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

import de.slg.schwarzes_brett.UpdateViewTrackerTask;
import de.slg.startseite.MailSendTask;
import de.slg.startseite.MainActivity;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.getController().closeActivities();
                Utils.getController().closeServices();
                Utils.getController().closeDatabases();

                Utils.getController().setContext(getApplicationContext());

                runUpdateTasks();
                startServices();

                final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra("show_dialog", de.slg.stimmungsbarometer.Utils.showVoteOnStartup());

                startActivity(main);
                finish();
            }
        }, 1500);
    }

    private void runUpdateTasks() {
        ArrayList<Integer> cachedViews = de.slg.schwarzes_brett.Utils.getCachedIDs();
        new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

        if (getIntent().getBooleanExtra("updateUser", true) && Utils.isVerified())
            new SyncUserTask().execute();

        new SyncGradeTask().execute();

        if (!Utils.getController().getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
            new MailSendTask().execute(Utils.getController().getPreferences().getString("pref_key_request_cached", ""));
        }
    }

    private void startServices() {
        if (Utils.isVerified() && false) {
            startService(new Intent(getApplicationContext(), ReceiveService.class));
        }
        startService(new Intent(getApplicationContext(), NotificationService.class));
    }
}
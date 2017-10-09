package de.slg.leoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import de.slg.schwarzes_brett.UpdateViewTrackerTask;
import de.slg.startseite.MailSendTask;
import de.slg.startseite.MainActivity;
import de.slg.startseite.UpdateTaskGrade;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().setContext(getApplicationContext());

        runUpdateTasks();
        startServices();

        final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("show_dialog", de.slg.stimmungsbarometer.Utils.showVoteOnStartup());

        startActivity(main);
        finish();
    }

    private void runUpdateTasks() {
        ArrayList<Integer> cachedViews = de.slg.schwarzes_brett.Utils.getCachedIDs();
        new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

        new SyncUserTask().execute();
        new SyncTaskGrade().execute();

        if (!Utils.getController().getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
            new MailSendTask().execute(Utils.getController().getPreferences().getString("pref_key_request_cached", ""));
        }

        if (Utils.getController().getPreferences().getBoolean("pref_key_level_has_to_be_synchronized", false)) {
            new UpdateTaskGrade().execute();
        }
    }

    private void startServices() {
        startService(new Intent(getApplicationContext(), ReceiveService.class));
        startService(new Intent(getApplicationContext(), NotificationService.class));
    }
}
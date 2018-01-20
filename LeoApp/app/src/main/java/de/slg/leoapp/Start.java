package de.slg.leoapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import de.slg.leoapp.service.AlarmStartupService;
import de.slg.leoapp.service.ReceiveService;
import de.slg.leoapp.task.MailSendTask;
import de.slg.leoapp.task.SyncFilesTask;
import de.slg.leoapp.task.SyncGradeTask;
import de.slg.leoapp.task.SyncQuestionTask;
import de.slg.leoapp.task.SyncUserTask;
import de.slg.leoapp.task.SyncVoteTask;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.schwarzes_brett.task.UpdateViewTrackerTask;
import de.slg.schwarzes_brett.utility.SchwarzesBrettUtils;
import de.slg.startseite.activity.MainActivity;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().closeActivities();
        Utils.getController().closeServices();
        Utils.getController().closeDatabases();

        Utils.getController().setContext(getApplicationContext());

        runUpdateTasks();
        startServices();

        final Intent main = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(main);
        finish();
    }

    private void runUpdateTasks() {
        if (Utils.checkNetwork()) {
            ArrayList<Integer> cachedViews = SchwarzesBrettUtils.getCachedIDs();
            new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

            if (Utils.isVerified()) {
                new SyncVoteTask().execute();
            }

            if (Utils.isVerified() && Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                new SyncGradeTask().execute();
            }

            if (Utils.isVerified()) {
                new SyncUserTask().execute();
            }

            if (Utils.isVerified()) {
                new SyncFilesTask().execute();
            }

            if (!Utils.getController().getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
                new MailSendTask().execute(Utils.getController().getPreferences().getString("pref_key_request_cached", ""));
            }

            if (Utils.isVerified()) {
                new SyncQuestionTask().execute();
            }
        }
    }

    private void startServices() {
        if (Utils.isVerified()) {
            startReceiveService();
            startService(new Intent(getApplicationContext(), AlarmStartupService.class));
            initSyncAdapter();
        }
    }

    public static void startReceiveService() {
        if (Utils.checkNetwork())
            Utils.getContext().startService(new Intent(Utils.getContext(), ReceiveService.class));
    }

    private void initSyncAdapter() {
        ContentResolver.addPeriodicSync(
                createSyncAccount(),
                "de.slg.leoapp",
                Bundle.EMPTY,
                10*60);
    }

    private Account createSyncAccount() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts;

        try {
            accounts = am.getAccountsByType("de.slg.leoapp");
        } catch (SecurityException e) {
            accounts = new Account[0];
        }
        if (accounts.length > 0) {
            return accounts[0];
        }
        Account newAccount = new Account("default_account", "de.slg.leoapp");
        if (am.addAccountExplicitly(newAccount, "pass1", null)) {
            ContentResolver.setIsSyncable(newAccount, "de.slg.leoapp", 1);
            ContentResolver.setSyncAutomatically(newAccount, "de.slg.leoapp", true);
        } else {
            newAccount = null;
        }

        return newAccount;
    }
}
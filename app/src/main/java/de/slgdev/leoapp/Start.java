package de.slgdev.leoapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import de.slgdev.leoapp.service.AlarmStartupService;
import de.slgdev.leoapp.service.ReceiveService;
import de.slgdev.leoapp.task.MailSendTask;
import de.slgdev.leoapp.task.SyncFilesTask;
import de.slgdev.leoapp.task.SyncGradeTask;
import de.slgdev.leoapp.task.SyncUserTask;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.schwarzes_brett.task.UpdateViewTrackerTask;
import de.slgdev.schwarzes_brett.utility.SchwarzesBrettUtils;
import de.slgdev.startseite.activity.MainActivity;
import de.slgdev.stimmungsbarometer.task.SyncQuestionTask;
import de.slgdev.stimmungsbarometer.task.SyncVoteTask;

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().closeActivities();
        Utils.getController().closeServices();
        Utils.getController().closeDatabases();

        Utils.getController().setContext(getApplicationContext());

        if (Utils.isVerified()) {
            runUpdateTasks();
        }

        startServices();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public static void runUpdateTasks() {
        if (Utils.checkNetwork()) {
            new SyncUserTask().execute();

            if (Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                new SyncGradeTask().execute();
            }

            new SyncQuestionTask().execute();

            new SyncVoteTask().execute();

            new SyncFilesTask().execute();

            ArrayList<Integer> cachedViews = SchwarzesBrettUtils.getCachedIDs();
            new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

            if (!Utils.getController().getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
                new MailSendTask().execute(Utils.getController().getPreferences().getString("pref_key_request_cached", ""));
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
        if (Utils.checkNetwork()) {
            Utils.getContext().startService(new Intent(Utils.getContext(), ReceiveService.class));
        }
    }

    private void initSyncAdapter() {
        ContentResolver.addPeriodicSync(
                createSyncAccount(),
                "de.slgdev.leoapp",
                Bundle.EMPTY,
                10 * 60
        );
    }

    private Account createSyncAccount() {
        AccountManager am = AccountManager.get(this);
        Account[]      accounts;

        try {
            accounts = am.getAccountsByType("de.slgdev.leoapp");
        } catch (SecurityException e) {
            accounts = new Account[0];
        }
        if (accounts.length > 0) {
            return accounts[0];
        }
        Account newAccount = new Account("default_account", "de.slgdev.leoapp");
        if (am.addAccountExplicitly(newAccount, "pass1", null)) {
            ContentResolver.setIsSyncable(newAccount, "de.slgdev.leoapp", 1);
            ContentResolver.setSyncAutomatically(newAccount, "de.slgdev.leoapp", true);
        } else {
            newAccount = null;
        }

        return newAccount;
    }
}
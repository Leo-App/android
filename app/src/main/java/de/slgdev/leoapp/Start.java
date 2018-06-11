package de.slgdev.leoapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Locale;

import de.slgdev.leoapp.service.AlarmStartupService;
import de.slgdev.leoapp.service.SocketService;
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

        setLocaleIfNecessary();

        if (Utils.isVerified()) {
            runUpdateTasks();
        }

        startServices();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public static void runUpdateTasks() {
        if (Utils.isNetworkAvailable()) {
            new SyncUserTask()
                    .addListener(params -> {
                        if (Utils.getUserPermission() != User.PERMISSION_LEHRER)
                            new SyncGradeTask().execute();
                        else
                            Utils.getController().getPreferences()
                                    .edit()
                                    .putString("pref_key_general_klasse", "TEA")
                                    .apply();
                    })
                    .execute();

            new SyncQuestionTask().execute();
            new SyncVoteTask().execute();
            new SyncFilesTask().execute();

            new UpdateViewTrackerTask().execute(SchwarzesBrettUtils.getCachedIDs());

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
        if (Utils.isNetworkAvailable()) {
            Utils.getContext().startService(new Intent(Utils.getContext(), SocketService.class));
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

    private void setLocaleIfNecessary() {
        String locale = Utils.getController().getPreferences().getString("pref_key_locale", "");

        Locale loc = new Locale(locale);

        if (locale.equals("") || Locale.getDefault().equals(loc))
            return;

        Locale.setDefault(loc);
        Configuration config = new Configuration();
        config.locale = loc;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}
package de.slg.leoapp;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

import de.slg.leoapp.notification.NotificationTime;
import de.slg.leoapp.notification.NotificationType;
import de.slg.leoapp.service.NotificationServiceWrapper;
import de.slg.leoapp.service.ReceiveService;
import de.slg.leoapp.task.MailSendTask;
import de.slg.leoapp.task.SyncGradeTask;
import de.slg.leoapp.task.SyncUserTask;
import de.slg.leoapp.task.SyncVoteTask;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.schwarzes_brett.UpdateViewTrackerTask;
import de.slg.startseite.MainActivity;

public class Start extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().closeActivities();
        Utils.getController().closeServices();
        Utils.getController().closeDatabases();

        Utils.getController().setContext(getApplicationContext());

        //Vorübergehend
        SharedPreferences preferences = Utils.getController().getPreferences();
        if (!preferences.getBoolean("first", true) && preferences.getString("previousVersion", "").equals("")) {
            preferences.edit()
                    .putString("previousVersion", "beta-0.6.8")
                    .putBoolean("first", false)
                    .apply();
        }
        //TODO ab Version 0.7.0 entfernen!!!

        runUpdateTasks();
        startServices();

        final Intent main = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(main);
        finish();
    }

    private void runUpdateTasks() {
        if (!Utils.checkNetwork()) {
            return;
        }

        ArrayList<Integer> cachedViews = de.slg.schwarzes_brett.Utils.getCachedIDs();
        new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

        if (Utils.isVerified()) {
            new SyncVoteTask().execute();
        }

        if (Utils.isVerified() && getIntent().getBooleanExtra("updateUser", true)) {
            new SyncUserTask().execute();
        }

        if (Utils.isVerified() && Utils.getUserPermission() != User.PERMISSION_LEHRER) {
            new SyncGradeTask().execute();
        }

        if (!Utils.getController().getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
            new MailSendTask().execute(Utils.getController().getPreferences().getString("pref_key_request_cached", ""));
        }
    }

    private void startServices() {
        if (Utils.isVerified()) {
            startService(new Intent(getApplicationContext(), ReceiveService.class));
            initServiceIntents();
            initNotificationServices();
            initSyncAdapter();
        }
    }

    private void initSyncAdapter() {
        ContentResolver.addPeriodicSync(
                new Account("default_account", "default_account"),
                "de.slg.leoapp.provider",
                Bundle.EMPTY,
                60*20);
    }

    private void initServiceIntents() {
        PendingIntent piFoodmarks = PendingIntent.getService(
                Utils.getContext(),
                0,
                new Intent(Utils.getContext(), NotificationServiceWrapper.FoodmarkService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent piTimetable = PendingIntent.getService(
                Utils.getContext(),
                1,
                new Intent(Utils.getContext(), NotificationServiceWrapper.TimetableService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent piKlausurplan = PendingIntent.getService(
                Utils.getContext(),
                2,
                new Intent(Utils.getContext(), NotificationServiceWrapper.KlausurplanService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent piStimmungsbarometer = PendingIntent.getService(
                Utils.getContext(),
                3,
                new Intent(Utils.getContext(), NotificationServiceWrapper.StimmungsbarometerService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Utils.getController().registerFoodmarkNotificationReference(piFoodmarks);
        Utils.getController().registerTimetableNotificationReference(piTimetable);
        Utils.getController().registerKlausurplanNotificationReference(piKlausurplan);
        Utils.getController().registerStimmungsbarometerNotificationReference(piStimmungsbarometer);
    }

    public static void initNotificationServices() {

        Calendar calendar = Calendar.getInstance();

        NotificationTime time;

        AlarmManager am = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
        Utils.getController().registerAlarmManager(am);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time = Utils.getNotificationTime(NotificationType.FOODMARKS);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);

            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getFoodmarkReference());

            time = Utils.getNotificationTime(NotificationType.KLAUSUR);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getKlausurplanReference());

            time = Utils.getNotificationTime(NotificationType.MOOD);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getStimmungsbarometerReference());

            time = Utils.getNotificationTime(NotificationType.TIMETABLE);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getTimetableReference());
        } else {
            time = Utils.getNotificationTime(NotificationType.FOODMARKS);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getFoodmarkReference()
            );

            time = Utils.getNotificationTime(NotificationType.KLAUSUR);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getKlausurplanReference()
            );

            time = Utils.getNotificationTime(NotificationType.MOOD);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getStimmungsbarometerReference()
            );

            time = Utils.getNotificationTime(NotificationType.TIMETABLE);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getTimetableReference()
            );
        }
    }
}
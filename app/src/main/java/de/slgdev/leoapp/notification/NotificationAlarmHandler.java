package de.slgdev.leoapp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import de.slgdev.leoapp.service.NotificationServiceWrapper;
import de.slgdev.leoapp.utility.Utils;

public abstract class NotificationAlarmHandler {

    public static void updateTimetableAlarm() {
        NotificationTime time;
        Calendar         calendar = Calendar.getInstance();
        time = Utils.getNotificationTime(NotificationType.TIMETABLE);
        calendar.set(Calendar.HOUR_OF_DAY, time.hours);
        calendar.set(Calendar.MINUTE, time.minutes);

        getAlarmManager().cancel(Utils.getController().getTimetableReference());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getTimetableReference());
        } else {
            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getTimetableReference()
            );
        }
    }

    public static void updateFoodmarkAlarm() {
        NotificationTime time;
        Calendar         calendar = Calendar.getInstance();
        time = Utils.getNotificationTime(NotificationType.FOODMARKS);
        calendar.set(Calendar.HOUR_OF_DAY, time.hours);
        calendar.set(Calendar.MINUTE, time.minutes);

        getAlarmManager().cancel(Utils.getController().getFoodmarkReference());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getFoodmarkReference());
        } else {
            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getFoodmarkReference()
            );
        }
    }

    public static void updateKlausurAlarm() {
        NotificationTime time;
        Calendar         calendar = Calendar.getInstance();
        time = Utils.getNotificationTime(NotificationType.KLAUSUR);
        calendar.set(Calendar.HOUR_OF_DAY, time.hours);
        calendar.set(Calendar.MINUTE, time.minutes);

        getAlarmManager().cancel(Utils.getController().getKlausurplanReference());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getKlausurplanReference());
        } else {
            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getKlausurplanReference()
            );
        }
    }

    public static void updateMoodAlarm() {
        NotificationTime time;
        Calendar         calendar = Calendar.getInstance();
        time = Utils.getNotificationTime(NotificationType.MOOD);
        calendar.set(Calendar.HOUR_OF_DAY, time.hours);
        calendar.set(Calendar.MINUTE, time.minutes);

        getAlarmManager().cancel(Utils.getController().getStimmungsbarometerReference());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Utils.getController().getStimmungsbarometerReference());
        } else {
            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getStimmungsbarometerReference()
            );
        }
    }

    /**
     * Diese Methode initialisiert den AlarmManager, sodass Notifications zu festgelegten
     * Uhrzeiten gesendet werden können, sofern dieser noch keinen Notification-PendingIntent verwaltet.
     */
    public static void initAlarmManagerIfNotExists() {

 /*       if(getAlarmRunning())
            return; */ //TODO: FIXEN und wieder einfügen

        initServiceIntents();

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

    private static AlarmManager getAlarmManager() {
        if (Utils.getController().getAlarmManager() == null) {
            AlarmManager am = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
            Utils.getController().registerAlarmManager(am);
        }
        return Utils.getController().getAlarmManager();
    }

    private static boolean getAlarmRunning() {
        return PendingIntent.getService(
                Utils.getContext(),
                0,
                new Intent(Utils.getContext(), NotificationServiceWrapper.FoodmarkService.class),
                PendingIntent.FLAG_NO_CREATE
        ) != null;
    }

    private static void initServiceIntents() {
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

}

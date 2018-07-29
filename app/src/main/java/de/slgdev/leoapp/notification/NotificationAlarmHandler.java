package de.slgdev.leoapp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import de.slgdev.leoapp.utility.Utils;

public abstract class NotificationAlarmHandler {

    public static void updateTimetableAlarm() {

        getAlarmManager().cancel(Utils.getController().getTimetableReference());

        Utils.logError(NotificationHandler.StundenplanNotification.isEnabled());

        if (NotificationHandler.StundenplanNotification.isEnabled()) {

            NotificationTime time;
            Calendar calendar = Calendar.getInstance();
            time = Utils.getNotificationTime(NotificationType.TIMETABLE);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);

            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DATE, 1);

            Utils.logError(calendar.getTimeInMillis() + " - " + System.currentTimeMillis());
            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getTimetableReference()
            );

        }
    }

    public static void updateFoodmarkAlarm() {

        if (Utils.getController().getFoodmarkReference() == null) //TODO herausfinden wieso nötig
            initServiceIntents();

        getAlarmManager().cancel(Utils.getController().getFoodmarkReference());

        Utils.logError(NotificationHandler.EssensbonsNotification.isEnabled());

        if (NotificationHandler.EssensbonsNotification.isEnabled()) {

            NotificationTime time;
            Calendar calendar = Calendar.getInstance();
            time = Utils.getNotificationTime(NotificationType.FOODMARKS);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);

            Utils.logError(calendar.getTimeInMillis() + " - " + System.currentTimeMillis());
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DATE, 1);

            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getFoodmarkReference()
            );

        }

    }

    public static void updateKlausurAlarm() {

        getAlarmManager().cancel(Utils.getController().getKlausurplanReference());

        Utils.logError(NotificationHandler.KlausurplanNotification.isEnabled());

        if (NotificationHandler.KlausurplanNotification.isEnabled()) {

            NotificationTime time;
            Calendar calendar = Calendar.getInstance();
            time = Utils.getNotificationTime(NotificationType.KLAUSUR);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);

            Utils.logError(calendar.getTimeInMillis() + " - " + System.currentTimeMillis());
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DATE, 1);

            getAlarmManager().setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    Utils.getController().getKlausurplanReference()
            );

        }
    }

    public static void updateMoodAlarm() {

        getAlarmManager().cancel(Utils.getController().getStimmungsbarometerReference());

        Utils.logError(NotificationHandler.StimmungsbarometerNotification.isEnabled());

        if (NotificationHandler.StimmungsbarometerNotification.isEnabled()) {

            NotificationTime time;
            Calendar calendar = Calendar.getInstance();
            time = Utils.getNotificationTime(NotificationType.MOOD);
            calendar.set(Calendar.HOUR_OF_DAY, time.hours);
            calendar.set(Calendar.MINUTE, time.minutes);

            Utils.logError(calendar.getTimeInMillis() + " - " + System.currentTimeMillis());

            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DATE, 1);

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
     * Uhrzeiten gesendet werden können, sofern dieser noch keinen LeoAppNotification-PendingIntent verwaltet.
     */
    public static void initAlarmManagerIfNotExists() {

        initServiceIntents();

        AlarmManager am = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
        Utils.getController().registerAlarmManager(am);

        updateTimetableAlarm();
        updateMoodAlarm();
        updateKlausurAlarm();
        updateFoodmarkAlarm();

    }

    private static AlarmManager getAlarmManager() {
        if (Utils.getController().getAlarmManager() == null) {
            AlarmManager am = (AlarmManager) Utils.getContext().getSystemService(Context.ALARM_SERVICE);
            Utils.getController().registerAlarmManager(am);
        }
        return Utils.getController().getAlarmManager();
    }

    private static void initServiceIntents() {
        PendingIntent piFoodmarks = PendingIntent.getBroadcast(
                Utils.getContext(),
                0,
                new Intent(Utils.getContext(), NotificationBroadcastWrapper.FoodmarkReceiver.class),
                0
        );

        PendingIntent piTimetable = PendingIntent.getBroadcast(
                Utils.getContext(),
                1,
                new Intent(Utils.getContext(), NotificationBroadcastWrapper.TimetableReceiver.class),
                0
        );

        PendingIntent piKlausurplan = PendingIntent.getBroadcast(
                Utils.getContext(),
                2,
                new Intent(Utils.getContext(), NotificationBroadcastWrapper.KlausurplanReceiver.class),
                0
        );

        PendingIntent piStimmungsbarometer = PendingIntent.getBroadcast(
                Utils.getContext(),
                3,
                new Intent(Utils.getContext(), NotificationBroadcastWrapper.StimmungsbarometerReceiver.class),
                0
        );

        Utils.getController().registerFoodmarkNotificationReference(piFoodmarks);
        Utils.getController().registerTimetableNotificationReference(piTimetable);
        Utils.getController().registerKlausurplanNotificationReference(piKlausurplan);
        Utils.getController().registerStimmungsbarometerNotificationReference(piStimmungsbarometer);
    }

}

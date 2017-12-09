package de.slg.leoapp.service;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.notification.NotificationTime;
import de.slg.leoapp.notification.NotificationType;
import de.slg.leoapp.sqlite.SQLiteConnectorEssensbons;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.UtilsController;

/**
 * NotificationServiceWrapper.
 *
 * Container-Klasse für alle Services, die zu einer bestimmten Uhrzeit gestartet werden und Notifications senden.
 *
 * @author Gianni
 * @since 0.6.7
 * @version 2017.0312
 */
public abstract class NotificationServiceWrapper {

    public static class TimetableService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Utils.getController().setContext(getApplicationContext());
            reschedule();

            new NotificationHandler.TimetableNotification().send();

            Log.i("NotificationService", "Service (re)started!");
            return START_NOT_STICKY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i("NotificationService", "Service stopped!");
        }

        private void reschedule() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                UtilsController c = Utils.getController();
                c.getAlarmManager().cancel(c.getTimetableReference());
                Calendar calendar = Calendar.getInstance();

                NotificationTime time = Utils.getNotificationTime(NotificationType.TIMETABLE);
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, time.hours);
                calendar.set(Calendar.MINUTE, time.minutes);

                c.getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), c.getTimetableReference());
            }
        }

    }

    public static class StimmungsbarometerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Utils.getController().setContext(getApplicationContext());
            reschedule();

            new NotificationHandler.StimmungsbarometerNotification().send();

            Log.i("NotificationService", "Service (re)started!");
            return START_NOT_STICKY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i("NotificationService", "Service stopped!");
        }

        private void reschedule() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                UtilsController c = Utils.getController();
                c.getAlarmManager().cancel(c.getStimmungsbarometerReference());
                Calendar calendar = Calendar.getInstance();

                NotificationTime time = Utils.getNotificationTime(NotificationType.MOOD);
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, time.hours);
                calendar.set(Calendar.MINUTE, time.minutes);

                c.getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), c.getStimmungsbarometerReference());
            }
        }

    }

    public static class FoodmarkService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Utils.getController().setContext(getApplicationContext());
            reschedule();

            Date d = new Date();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);

            if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                sendNotificationIfNecessary();

            Log.i("NotificationService", "Service (re)started!");
            return START_NOT_STICKY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i("NotificationService", "Service stopped!");
        }

        private void reschedule() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                UtilsController c = Utils.getController();
                c.getAlarmManager().cancel(c.getFoodmarkReference());
                Calendar calendar = Calendar.getInstance();

                NotificationTime time = Utils.getNotificationTime(NotificationType.FOODMARKS);
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, time.hours);
                calendar.set(Calendar.MINUTE, time.minutes);

                c.getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), c.getFoodmarkReference());
            }
        }

        private void sendNotificationIfNecessary() {
            if (!Utils.getController().getPreferences().getBoolean("pref_key_status_loggedin", false))
                return;
            SQLiteConnectorEssensbons db     = new SQLiteConnectorEssensbons(this);
            SQLiteDatabase            dbw    = db.getReadableDatabase();
            Cursor                    cursor = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);
            if (cursor.getCount() == 0) {
                cursor.close();
                new NotificationHandler.FoodmarkNotification().send();
                return;
            }
            cursor.moveToFirst();
            int maxid = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            cursor = dbw.rawQuery("SELECT o.DATEU as date FROM USERORDERS o JOIN STATISTICS s ON s.LASTORDER = o.ID WHERE s.ID = " + maxid, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                new NotificationHandler.FoodmarkNotification().send();
                return;
            }
            cursor.moveToFirst();
            String date = cursor.getString(cursor.getColumnIndex("date"));
            cursor.close();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
            try {
                Date dateD = df.parse(date);
                if (dateD.before(new Date()))
                    new NotificationHandler.FoodmarkNotification().send();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public static class KlausurplanService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Utils.getController().setContext(getApplicationContext());
            reschedule();

            Date d = new Date();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);

            if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                sendNotificationIfNecessary();

            Log.i("NotificationService", "Service (re)started!");
            return START_NOT_STICKY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i("NotificationService", "Service stopped!");
        }

        private void reschedule() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                UtilsController c = Utils.getController();
                c.getAlarmManager().cancel(c.getKlausurplanReference());
                Calendar calendar = Calendar.getInstance();

                NotificationTime time = Utils.getNotificationTime(NotificationType.KLAUSUR);
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, time.hours);
                calendar.set(Calendar.MINUTE, time.minutes);

                c.getAlarmManager().setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), c.getKlausurplanReference());
            }
        }

        private void sendNotificationIfNecessary() {
            //TODO
        }

    }

}

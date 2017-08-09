package de.slg.leoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.essensqr.SQLiteHandler;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.messenger.Message;
import de.slg.messenger.OverviewWrapper;
import de.slg.schwarzes_brett.SQLiteConnector;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.AbstimmActivity;
import de.slg.stundenplan.Fach;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class NotificationService extends Service {
    private static short hoursQR, minutesQR;
    private static short hoursTT, minutesTT;
    private static short hoursTP, minutesTP;
    private static short hoursSB, minutesSB;
    private NotificationManager notificationManager;
    private Bitmap icon;
    private int userid;
    private boolean running;

    public static void getTimes() {
        String qr = Start.pref.getString("pref_key_notification_time", "00:00");
        hoursQR = Short.parseShort(qr.split(":")[0]);
        minutesQR = Short.parseShort(qr.split(":")[1]);

        String tt = Start.pref.getString("pref_key_notification_time_schedule", "00:00");
        hoursTT = Short.parseShort(tt.split(":")[0]);
        minutesTT = Short.parseShort(tt.split(":")[1]);

        String tp = Start.pref.getString("pref_key_notification_time_test", "00:00");
        hoursTP = Short.parseShort(tp.split(":")[0]);
        minutesTP = Short.parseShort(tp.split(":")[1]);

        String sb = Start.pref.getString("pref_key_notification_time_survey", "00:00");
        hoursSB = Short.parseShort(sb.split(":")[0]);
        minutesSB = Short.parseShort(sb.split(":")[1]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.context = getApplicationContext();
        Start.initPref(getApplicationContext());

        notificationManager = Utils.getNotificationManager();
        userid = Utils.getUserID();

        getTimes();
        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);

        new LoopThread().start();

        Log.i("NotificationService", "Service (re)started!");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        Log.i("NotificationService", "Service stopped!");
    }

    private void timeCheck() {
        Date d = new Date();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);

        if (c.get(Calendar.HOUR_OF_DAY) == hoursTT && c.get(Calendar.MINUTE) == minutesTT) {
            stundenplanNotification();
        }

        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c.get(Calendar.HOUR_OF_DAY) == hoursQR && c.get(Calendar.MINUTE) == minutesQR) {
            checkEssensqr();
        }

        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c.get(Calendar.HOUR_OF_DAY) == hoursTP && c.get(Calendar.MINUTE) == minutesTP) {
            klausurplanNotification();
        }

        if (c.get(Calendar.HOUR_OF_DAY) == hoursSB && c.get(Calendar.MINUTE) == minutesSB) {
            stimmungsbarometernotification();
        }
    }

    private void checkEssensqr() {
        de.slg.essensqr.SQLitePrinter.printDatabase(getApplicationContext());

        SQLiteHandler db = new SQLiteHandler(this);
        SQLiteDatabase dbw = db.getReadableDatabase();

        Cursor cursor = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);

        if (cursor.getCount() == 0) {
            cursor.close();
            essensqrNotification();
            return;
        }

        cursor.moveToFirst();
        int maxid = cursor.getInt(cursor.getColumnIndex("id"));

        cursor.close();

        cursor = dbw.rawQuery("SELECT o.DATEU as date FROM USERORDERS o JOIN STATISTICS s ON s.LASTORDER = o.ID WHERE s.ID = " + maxid, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            essensqrNotification();
            return;
        }

        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex("date"));
        cursor.close();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dateD = df.parse(date);
            if (dateD.before(new Date()))
                essensqrNotification();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void essensqrNotification() {
        if (Start.pref.getBoolean("pref_key_notification_essensqr", true)) {
            Intent resultIntent = new Intent(this, MainActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.qrcode)
                            .setVibrate(new long[]{200})
                            .setContentTitle("LeoApp")
                            .setContentText(getString(R.string.notification_summary_notif))
                            .setContentIntent(resultPendingIntent);

            notificationManager.notify(101, notificationBuilder.build());
        }
    }

    private void klausurplanNotification() {
        if (Start.pref.getBoolean("pref_key_notification_test", true)) {
            Intent resultIntent = new Intent(getApplicationContext(), KlausurplanActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.ic_content_paste_white_24dp)
                            .setVibrate(new long[]{200})
                            .setContentTitle("Klausurplan")
                            .setContentText("Hallo")
                            .setContentIntent(resultPendingIntent);

            notificationManager.notify(777, notificationBuilder.build());
        }
    }

    private void messengerNotification() {
        if (Start.pref.getBoolean("pref_key_notification_messenger", true) && Utils.getMDB().hasUnreadMessages()) {
            StringBuilder builder = new StringBuilder();
            for (Message m : Utils.getMDB().getUnreadMessages()) {
                builder.append(m.uname)
                        .append(": ")
                        .append(m.mtext)
                        .append(System.getProperty("line.separator"));
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), OverviewWrapper.class), 0);

            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setVibrate(new long[]{200, 100, 200})
                            .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                            .setContentTitle(getString(R.string.messenger_notification_title))
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(builder.toString()))
                            .build();
            notificationManager.notify(5453, notification);
            Utils.notifiedMessenger();
        }
    }

    private void nachhilfeNotification() {
//          TODO
    }

    private void schwarzesBrettNotification() {
        if (Start.pref.getBoolean("pref_key_notification_news", true)) {
            SQLiteConnector db = new SQLiteConnector(getApplicationContext());
            SQLiteDatabase dbh = db.getReadableDatabase();
            long latest = db.getLatestDate(dbh);
            dbh.close();
            db.close();

            if (latest > Utils.getLatestSchwarzesBrettDate()) {
                Utils.notifiedSchwarzesBrett(latest);
                Intent resultIntent = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                0
                        );

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setLargeIcon(icon)
                                .setSmallIcon(R.drawable.ic_event_note_white_24dp)
                                .setVibrate(new long[]{200})
                                .setContentTitle("Neue Einträge")
                                .setContentText("Es gibt Neuigkeiten am Schwarzen Brett")
                                .setContentIntent(resultPendingIntent);

                notificationManager.notify(287, notificationBuilder.build());
            }
        }
    }

    private void stimmungsbarometernotification() {
        if (Start.pref.getBoolean("pref_key_notification_survey", false) && Utils.showVoteOnStartup()) {
            Intent resultIntent = new Intent(getApplicationContext(), AbstimmActivity.class)
                    .putExtra("userid", userid);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            0
                    );

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.ic_insert_emoticon_white_24dp)
                            .setVibrate(new long[]{200})
                            .setContentTitle("Du hast noch nicht abgestimmt!")
                            .setContentText("Jetzt abstimmen")
                            .setContentIntent(resultPendingIntent);

            notificationManager.notify(234, notificationBuilder.build());
        }
    }

    private void stundenplanNotification() {
        if (Start.pref.getBoolean("pref_key_notification_schedule", true)) {
            StringBuilder builder = new StringBuilder();
            if (gibNaechstenWochentag() <= 5) {
                Fach[] faecher = Utils.getStundDB().gewaehlteFaecherAnTag(gibNaechstenWochentag());
                for (int i = 0; i < faecher.length; i++) {
                    builder.append(faecher[i].gibName());
                    if (i < faecher.length - 1)
                        builder.append(", ");
                }

                Intent resultIntent = new Intent(getApplicationContext(), WrapperStundenplanActivity.class);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setLargeIcon(icon)
                                .setSmallIcon(R.drawable.ic_event_white_24dp)
                                .setVibrate(new long[]{200})
                                .setContentTitle("Deine Stunden morgen:")
                                .setContentText(builder.toString())
                                .setContentIntent(resultPendingIntent);

                notificationManager.notify(101, notificationBuilder.build());
            }
        }
    }

    private void vertretungsplanNotification() {
        if (Start.pref.getBoolean("pref_key_notification_subst", true)) {
//          TODO
        }
    }

    private int gibNaechstenWochentag() {
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        int i = c.get(Calendar.DAY_OF_WEEK);
        if (i == Calendar.SUNDAY)
            return 1;
        if (i == Calendar.MONDAY)
            return 2;
        if (i == Calendar.TUESDAY)
            return 3;
        if (i == Calendar.WEDNESDAY)
            return 4;
        if (i == Calendar.THURSDAY)
            return 5;
        return 6;
    }

    private class LoopThread extends Thread {
        @Override
        public void run() {
            running = true;
            while (running) {
                messengerNotification();
                schwarzesBrettNotification();
                timeCheck();
            }
        }
    }
}
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.essensqr.SQLiteHandler;
import de.slg.messenger.Chat;
import de.slg.messenger.Message;
import de.slg.schwarzes_brett.SQLiteConnector;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.AbstimmActivity;
import de.slg.stundenplan.Fach;

public class NotificationService extends Service {
    public static final int ID_ESSENSQR    = 101;
    public static final int ID_KLAUSURPLAN = 777;
    public static final int ID_MESSENGER   = 5453;
    public static final int ID_NEWS        = 287;
    public static final int ID_BAROMETER   = 234;
    public static final int ID_STUNDENPLAN = 222;
    private static short hoursQR, minutesQR;
    private static short hoursTT, minutesTT;
    private static short hoursTP, minutesTP;
    private static short hoursSB, minutesSB;
    private NotificationManager notificationManager;
    private boolean             sentQR, sentTT, sentTP, sentSB;
    private Bitmap  icon;
    private boolean running;
    private int     unreadMessages;

    public static void getTimes() {
        String qr = Utils.getController().getPreferences().getString("pref_key_notification_time_foodmarks", "00:00");
        hoursQR = Short.parseShort(qr.split(":")[0]);
        minutesQR = Short.parseShort(qr.split(":")[1]);
        String tt = Utils.getController().getPreferences().getString("pref_key_notification_time_schedule", "00:00");
        hoursTT = Short.parseShort(tt.split(":")[0]);
        minutesTT = Short.parseShort(tt.split(":")[1]);
        String tp = Utils.getController().getPreferences().getString("pref_key_notification_time_test", "00:00");
        hoursTP = Short.parseShort(tp.split(":")[0]);
        minutesTP = Short.parseShort(tp.split(":")[1]);
        String sb = Utils.getController().getPreferences().getString("pref_key_notification_time_survey", "00:00");
        hoursSB = Short.parseShort(sb.split(":")[0]);
        minutesSB = Short.parseShort(sb.split(":")[1]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.getController().setContext(getApplicationContext());

        notificationManager = Utils.getNotificationManager();

        getTimes();

        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);

        new NotificationThread().start();

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
        Date              d = new Date();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);

        if (c.get(Calendar.HOUR_OF_DAY) == hoursTT && c.get(Calendar.MINUTE) == minutesTT) {
            if (!sentTT)
                stundenplanNotification();
            sentTT = true;
        } else {
            sentTT = false;
        }

        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c.get(Calendar.HOUR_OF_DAY) == hoursQR && c.get(Calendar.MINUTE) == minutesQR) {
            if (!sentQR)
                checkEssensqr();
            sentQR = true;
        } else {
            sentQR = false;
        }

        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c.get(Calendar.HOUR_OF_DAY) == hoursTP && c.get(Calendar.MINUTE) == minutesTP) {
            if (!sentTP)
                checkKlausurplan();
            sentTP = true;
        } else {
            sentTP = false;
        }

        if (c.get(Calendar.HOUR_OF_DAY) == hoursSB && c.get(Calendar.MINUTE) == minutesSB) {
            if (!sentSB)
                stimmungsbarometernotification();
            sentSB = true;
        } else {
            sentSB = false;
        }
    }

    private void checkEssensqr() {
        if (!Utils.getController().getPreferences().getBoolean("pref_key_status_loggedin", false))
            return;
        SQLiteHandler  db     = new SQLiteHandler(this);
        SQLiteDatabase dbw    = db.getReadableDatabase();
        Cursor         cursor = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);
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
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_essensqr", true)) {
            Intent resultIntent = new Intent(this, MainActivity.class)
                    .putExtra("start_intent", ID_ESSENSQR);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Notification notification =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.qrcode)
                            .setVibrate(new long[]{200})
                            .setContentTitle(getString(R.string.app_name))
                            .setAutoCancel(true)
                            .setContentText(getString(R.string.notification_summary_notif))
                            .setContentIntent(resultPendingIntent)
                            .build();

            notificationManager.notify(ID_ESSENSQR, notification);
        }
    }

    private void checkKlausurplan() {
        try {
            Calendar tomorrow = new GregorianCalendar();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput(getString(R.string.klausuren_filemane))));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] current = line.split(";");
                if (current.length == 4) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(new Date(Long.parseLong(current[1])));
                    if (c.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH) && c.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && c.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH)) {
                        klausurplanNotification();
                    }
                    if (c.getTime().after(tomorrow.getTime())) {
                        break;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void klausurplanNotification() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_test", true)) {
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class)
                    .putExtra("start_intent", ID_KLAUSURPLAN);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.ic_content_paste_white_24dp)
                            .setVibrate(new long[]{200})
                            .setContentTitle(getString(R.string.title_testplan))
                            .setAutoCancel(true)
                            .setContentText("Du schreibst morgen eine Klausur!")
                            .setContentIntent(resultPendingIntent)
                            .build();

            notificationManager.notify(ID_KLAUSURPLAN, notification);
        }
    }

    private void messengerNotification() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_messenger", true) && Utils.getController().getMessengerDataBase().hasUnreadMessages() && Utils.getController().getMessengerActivity() == null) {
            Message[] unread = Utils.getController().getMessengerDataBase().getUnreadMessages();

            if (unread.length != unreadMessages) {
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                        .setSummaryText(Utils.getController().getMessengerDataBase().getNotificationString())
                        .setBigContentTitle(getString(R.string.messenger_notification_title));

                for (Message m : unread) {
                    String line = m.uname;

                    if (Utils.getController().getMessengerDataBase().getType(m.cid) == Chat.ChatType.GROUP) {
                        line += " @ " + m.cname;
                    }

                    line += ": " + m.mtext;

                    style.addLine(line);
                }

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra("start_intent", ID_MESSENGER);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                Notification notification =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setLargeIcon(icon)
                                .setVibrate(new long[]{500, 250, 500})
                                .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                                .setContentIntent(resultPendingIntent)
                                .setContentTitle(getString(R.string.messenger_notification_title))
                                .setStyle(style)
                                .build();

                notificationManager.notify(ID_MESSENGER, notification);

                unreadMessages = unread.length;
            }
        }
    }

    private void schwarzesBrettNotification() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_news", true)) {
            SQLiteConnector db     = new SQLiteConnector(getApplicationContext());
            SQLiteDatabase  dbh    = db.getReadableDatabase();
            long            latest = db.getLatestDate(dbh);
            dbh.close();
            db.close();
            if (latest > de.slg.schwarzes_brett.Utils.getLatestSchwarzesBrettDate()) {
                de.slg.schwarzes_brett.Utils.notifiedSchwarzesBrett(latest);
                if (Utils.getController().getSchwarzesBrettActivity() == null) {
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class)
                            .putExtra("start_intent", ID_NEWS);

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    getApplicationContext(),
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    Notification notification =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.ic_event_note_white_24dp)
                                    .setVibrate(new long[]{200})
                                    .setAutoCancel(true)
                                    .setContentTitle("Neue Einträge")
                                    .setContentText("Es gibt Neuigkeiten am Schwarzen Brett")
                                    .setContentIntent(resultPendingIntent)
                                    .build();

                    notificationManager.notify(ID_NEWS, notification);
                }
            }
        }
    }

    private void stimmungsbarometernotification() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_survey", false) && de.slg.stimmungsbarometer.Utils.showVoteOnStartup()) {
            Intent resultIntent = new Intent(getApplicationContext(), AbstimmActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.ic_insert_emoticon_white_24dp)
                            .setVibrate(new long[]{200})
                            .setContentTitle("Du hast noch nicht abgestimmt!")
                            .setContentText("Jetzt abstimmen")
                            .setAutoCancel(true)
                            .setContentIntent(resultPendingIntent)
                            .build();

            notificationManager.notify(ID_BAROMETER, notification);
        }
    }

    private void stundenplanNotification() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_notification_schedule", true)) {
            StringBuilder builder = new StringBuilder();
            if (gibNaechstenWochentag() <= 5) {
                Fach[] faecher = Utils.getController().getStundplanDataBase().gewaehlteFaecherAnTag(gibNaechstenWochentag());
                for (int i = 0; i < faecher.length; i++) {
                    if (faecher[i].getName().length() > 0 && (i == 0 || !faecher[i].getName().equals(faecher[i - 1].getName()))) {
                        builder.append(faecher[i].getName());
                        if (i < faecher.length - 1) {
                            builder.append(", ");
                        }
                    }
                }

                if (builder.length() > 0) {
                    Notification notification =
                            new NotificationCompat.Builder(this)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setLargeIcon(icon)
                                    .setSmallIcon(R.drawable.ic_event_white_24dp)
                                    .setVibrate(new long[]{200})
                                    .setContentTitle("Deine Stunden morgen:")
                                    .setContentText(builder.toString())
                                    .setAutoCancel(true)
                                    .build();

                    notificationManager.notify(ID_STUNDENPLAN, notification);
                }
            }
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

    private class NotificationThread extends Thread {
        @Override
        public void run() {
            running = true;
            while (running) {
                if (Utils.getContext() == null)
                    Utils.getController().setContext(getApplicationContext());
                messengerNotification();
                schwarzesBrettNotification();
                timeCheck();
            }
        }
    }
}
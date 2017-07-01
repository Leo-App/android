package de.slg.leoapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.slg.essensqr.SQLiteHandler;
import de.slg.messenger.Message;
import de.slg.messenger.OverviewWrapper;
import de.slg.startseite.MainActivity;
import de.slg.stundenplan.Fach;
import de.slg.stundenplan.Stundenplanverwalter;
import de.slg.stundenplan.WrapperStundenplanActivity;

@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class NotificationService extends IntentService {

    private NotificationManager notificationManager;

    private static short hours;
    private static short minutes;
    private static short hoursTT;
    private static short minutesTT;

    public NotificationService() {
        super("notification-service-leo");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.wtf("LeoApp", "firstCalled");

        Utils.context = getApplicationContext();
        Start.initPref(getApplicationContext());

        notificationManager = Utils.getNotificationManager();

        boolean loggedin = Start.pref.getBoolean("pref_key_status_loggedin", false);

        if (!loggedin)
            return;

        actualize();
        while (true) {

            Log.wtf("LeoApp", "iterationCall");

            try {
                Thread.sleep(59990);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.wtf("LeoApp", "thread wake call");

            Date d = new Date();

            Log.wtf("LeoApp", "Time: " + d.getHours() + ":" + d.getMinutes() + " Scheduled: " + hours + ":" + minutes);

            if(d.getDay() != 5 && d.getDay() != 6 && d.getHours() == hoursTT && d.getMinutes() == minutesTT) {
                this.stundenplanNotification();
            }

            if (d.getDay() != 0 && d.getDay() != 6 && d.getHours() == hours && d.getMinutes() == minutes) {

                de.slg.essensqr.SQLitePrinter.printDatabase(getApplicationContext());

                SQLiteHandler db = new SQLiteHandler(this);
                SQLiteDatabase dbw = db.getReadableDatabase();

                Cursor c = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);

                if (c.getCount() == 0) {
                    essensqrNotification();
                    return;
                }

                c.moveToFirst();
                int maxid = c.getInt(c.getColumnIndex("id"));

                c.close();

                c = dbw.rawQuery("SELECT o.DATEU as date FROM USERORDERS o JOIN STATISTICS s ON s.LASTORDER = o.ID WHERE s.ID = " + maxid, null);

                if (c.getCount() == 0) {
                    essensqrNotification();
                    return;
                }

                c.moveToFirst();
                String date = c.getString(c.getColumnIndex("date"));
                c.close();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    Date dateD = df.parse(date);
                    if (dateD.before(new Date()))
                        essensqrNotification();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            messengerNotification();
            klausurplanNotification();
            nachhilfeNotification();
            schwarzesBrettNotification();
            stimmungsbarometerNotification();
            vertretungsplanNotification();
        }
    }

    public static void actualize() {
        String time = Start.pref.getString("pref_key_notification_time", "00:00");

        hours = Short.parseShort(time.split(":")[0]);
        minutes = Short.parseShort(time.split(":")[1]);

        String ti = Start.pref.getString("pref_key_notification_time_schedule", "00:00");

        hoursTT = Short.parseShort(ti.split(":")[0]);
        minutesTT = Short.parseShort(ti.split(":")[1]);
    }

    private void essensqrNotification() {
        if (Start.pref.getBoolean("pref_key_notification_essensqr", true)) {
            Intent resultIntent = new Intent(this, MainActivity.class);

            final Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.qrcode)
                            .setVibrate(new long[]{200})
                            .setContentTitle("LeoApp")
                            .setContentText(getString(R.string.notification_summary_notif))
                            .setContentIntent(resultPendingIntent);

            notificationManager.notify(101, mBuilder.build());
        }
    }

    private void klausurplanNotification() {
        if (Start.pref.getBoolean("pref_key_notification_test", true)) {

        }
    }

    public void messengerNotification() {
        if (Start.pref.getBoolean("pref_key_notification_messenger", true) && Utils.getMessengerDBConnection().hasUnreadMessages()) {
            Message[] messages = Utils.getMessengerDBConnection().getUnreadMessages();
            String s = "";
            for (Message m : messages)
                s += m.toString() + System.getProperty("line.separator");
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), OverviewWrapper.class), 0);
            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);
            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setVibrate(new long[]{200, 100, 200})
                            .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                            .setContentTitle(getString(R.string.messenger_notification_title))
                            .setContentText(s)
                            .setContentIntent(pendingIntent)
                            .build();
            notificationManager.notify(5453, notification);
            Utils.notifiedMessenger();
        }
    }

    private void nachhilfeNotification() {

    }

    private void schwarzesBrettNotification() {
        if (Start.pref.getBoolean("pref_key_notification_news", true)) {

        }
    }

    private void stimmungsbarometerNotification() {
        if (Start.pref.getBoolean("pref_key_notification_survey", true)) {

        }
    }

    private void stundenplanNotification() {
        Stundenplanverwalter sv = new Stundenplanverwalter(WrapperStundenplanActivity.c, "meinefaecher.txt");
        String s = "";
        if(this.gibDatum()<5) {
            Fach[] f = sv.gibFaecherKurzTag(this.gibDatum()+1);
            for(int i=0; i<f.length; i++) {
                s = s + ", " + f[i].gibName();
            }
        }
        if (Start.pref.getBoolean("pref_key_notification_schedule", true)) {
            Intent resultIntent = new Intent(this, WrapperStundenplanActivity.class);

            final Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setLargeIcon(icon)
                            .setSmallIcon(R.drawable.qrcode)
                            .setVibrate(new long[]{200})
                            .setContentTitle("LeoApp")
                            .setContentText(s)
                            .setContentIntent(resultPendingIntent);

            notificationManager.notify(101, mBuilder.build());
            //Ich weiß nicht ob das hier läuft aber es zerstört nichts...
        }
    }

    private void vertretungsplanNotification() {
        if (Start.pref.getBoolean("pref_key_notification_subst", true)) {

        }
    }

    private int gibDatum() {
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        int i = c.get(Calendar.DAY_OF_WEEK);
        switch(i) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return 6;
        }

    }
}
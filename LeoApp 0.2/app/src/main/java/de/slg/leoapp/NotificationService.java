package de.slg.leoapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.slg.essensqr.SQLiteHandler;
import de.slg.essensqr.WrapperQRActivity;
import de.slg.startseite.MainActivity;

public class NotificationService extends IntentService {

    private static short hours;
    private static short minutes;

    public NotificationService() {
        super("notification-service-leo");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean loggedin = MainActivity.pref.getBoolean("pref_key_status_loggedin", false);

        if (!loggedin)
            return;

        actualize();

        Log.d("LeoApp", "called");
        while (true) {

            Log.d("LeoApp", "iteration");

            try {
                Thread.sleep(59000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Date d = new Date();

            if (d.getDay() != 0 && d.getDay() != 6 && d.getHours() == hours && d.getMinutes() == minutes) {
                SQLiteHandler db = new SQLiteHandler(this);
                SQLiteDatabase dbw = db.getReadableDatabase();

                Cursor c = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);
                c.moveToFirst();
                int maxid = c.getInt(c.getColumnIndex("id"));

                c.close();

                c = dbw.rawQuery("SELECT o.DATEU as date FROM USERORDERS o JOIN STATISTICS s ON s.LASTORDER = o.ID WHERE s.ID = " + maxid, null);
                c.moveToFirst();
                String date = c.getString(c.getColumnIndex("date"));
                c.close();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    Date dateD = df.parse(date);

                    if (dateD.before(new Date()))
                        showNotification();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void actualize() {
        String time = WrapperQRActivity.sharedPref.getString("pref_key_notification_time", "00:00");

        hours = Short.parseShort(time.split(":")[0]);
        minutes = Short.parseShort(time.split(":")[1]);
    }

    private void showNotification() {
        Intent resultIntent = new Intent(this, WrapperQRActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.qr_code)
                        .setContentTitle("LeoApp")
                        .setContentText(getString(R.string.notification_summary_notif))
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(101, mBuilder.build());
    }
}
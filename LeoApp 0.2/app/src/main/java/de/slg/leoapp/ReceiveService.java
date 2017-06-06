package de.slg.leoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import de.slg.messenger.Message;
import de.slg.messenger.OverviewWrapper;
import de.slg.messenger.ReceiveTask;
import de.slg.startseite.MainActivity;

public class ReceiveService extends Service {

    private LoopThread thread;
    private NotificationManager notificationManager;
    private boolean running;
    private static long intervall;
    private Bitmap icon;

    public ReceiveService() {
        running = true;
        intervall = getIntervall(MainActivity.pref.getInt("pref_key_refresh", 2));
    }

    private static long getIntervall(int selection) {
        switch (selection) {
            case 0:
                return 5000;
            case 1:
                return 10000;
            case 3:
                return 30000;
            case 4:
                return 60000;
            case 5:
                return 120000;
            case 6:
                return 300000;
            default:
                return 15000;
        }
    }

    public static void setIntervall(int selection) {
        intervall = getIntervall(selection);
    }

    private class LoopThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (running) {
                try {
                    new ReceiveTask().execute();
                    for (int i = 0; i < intervall && running; i++) {
                        sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void receive() {
        new ReceiveTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.context = getApplicationContext();
        notificationManager = Utils.getNotificationManager();
        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);
        thread = new LoopThread();
        thread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        thread.interrupt();
    }

    public void showNotification() {
        Message[] messages = Utils.getMessengerDBConnection().getUnreadMessages();
        String s = "";
        for (Message m : messages)
            s += m.toString() + System.getProperty("line.separator");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), OverviewWrapper.class), 0);
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
    }
}
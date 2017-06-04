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

import java.util.concurrent.ExecutionException;

import de.slg.messenger.Message;
import de.slg.messenger.OverviewWrapper;
import de.slg.messenger.ReceiveTask;

public class ReceiveService extends Service {

    public static OverviewWrapper wrapper;
    private static LoopThread thread;
    private NotificationManager notificationManager;
    private ReceiveTask r;


    public ReceiveService() {
        Utils.context = getApplicationContext();
        notificationManager = Utils.getNotificationManager();
    }

    class LoopThread extends Thread {

        private long sleep;
        public boolean b;

        public LoopThread() {
            b = true;
            sleep = 15000;
        }

        @Override
        public void run() {
            Looper.prepare();
            while (b) {
                try {
                    r = new ReceiveTask();
                    r.execute();
                    if (r.get())
                        showNotification();
                    sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void receive() {
        if (thread != null) {
            new ReceiveTask().execute();
        }
    }

    @Override
    public void onCreate() {
        thread = new LoopThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        thread.b = false;
    }

    public void showNotification() {

        final Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);

        wrapper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message[] messages = Utils.getMessengerDBConnection().getUnreadMessages();
                String s = "";
                for (Message m : messages)
                    s += m.toString() + System.getProperty("line.separator");
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), OverviewWrapper.class), 0);
                Notification notification =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setLargeIcon(icon)
                                .setVibrate(new long[]{200,100,200})
                                .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                                .setContentTitle(getString(R.string.messenger_notification_title))
                                .setContentText(s)
                                .setContentIntent(pendingIntent)
                                .build();
                startForeground(0, notification);
                notificationManager.notify(0, notification);
            }
        });
    }
}
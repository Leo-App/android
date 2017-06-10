package de.slg.leoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import de.slg.messenger.ReceiveTask;

public class ReceiveService extends Service {

    private LoopThread thread;
    private boolean running;
    private static long intervall;

    public ReceiveService() {
        running = true;
        intervall = getIntervall(Start.pref.getInt("pref_key_refresh", 2));
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
}
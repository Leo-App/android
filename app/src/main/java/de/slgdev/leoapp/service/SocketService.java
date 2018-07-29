package de.slgdev.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.network.MessageHandler;
import de.slgdev.messenger.network.QueueThread;
import de.slgdev.messenger.network.SocketListener;
import de.slgdev.messenger.utility.Assoziation;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class SocketService extends Service {
    private WebSocket   socket;
    private boolean     socketRunning;
    private QueueThread queue;

    @Override
    public void onCreate() {
        Utils.logDebug("SocketService created");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.getController().setContext(getApplicationContext());
        Utils.getController().registerReceiveService(this);

        new QueueThread().start();

        startSocket();

        Utils.logDebug("SocketService (re)started");
        return START_STICKY;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        socket.close(1000, "Service stopped");
        Utils.getController().closeDatabases();
        Utils.getController().registerReceiveService(null);

        Utils.logError("SocketService stopped");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        socket.close(1000, "Service stopped");
        Utils.getController().closeDatabases();
        Utils.getController().registerReceiveService(null);

        Utils.logError("SocketService removed");
        super.onTaskRemoved(rootIntent);
    }

    public void startSocket() {
        if (!socketRunning) {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            String date = Utils.getController().getMessengerDatabase().getLatestMessage();
            if (date.length() > 3) {
                date = date.substring(0, date.length() - 3);
            }

            final Request request = new Request.Builder()
                    .url(Utils.URL_TOMCAT)
                    //.url(Utils.URL_TOMCAT_DEV)
                    .addHeader("TEST", "successful")
                    .build();
            //TODO Add Authentication

            final MessageHandler messageHandler = new MessageHandler();
            messageHandler.start();

            final SocketListener listener = new SocketListener(this, messageHandler);

            socket = client.newWebSocket(request, listener);

            socket.send("uid=" + Utils.getUserID());
            socket.send("mdate=" + date);
            socket.send("request");
        }
    }

    private void send(String s) {
        startSocket();
        socket.send(s);
    }

    public void send(Message message) {
        String key      = de.slgdev.messenger.utility.Encryption.createKey(message.mtext);
        String vMessage = de.slgdev.messenger.utility.Encryption.encrypt(message.mtext, key);
        String vKey     = de.slgdev.messenger.utility.Encryption.encryptKey(key);
        String s        = "m+ " + message.cid + ';' + vKey + ';' + vMessage;
        for (char c : message.mtext.toCharArray()) {
            Utils.logDebug(c);
            Utils.logDebug((int) c);
        }
        Utils.logDebug(s);
        send(s);
    }

    public void send(Chat chat) {
        String s = "c+ '" + chat.ctype.toString().toUpperCase().charAt(0) + "';" + chat.cname;
        send(s);
    }

    public void send(Assoziation assoziation) {
        String s = "a+ " + assoziation.cid + ';' + assoziation.uid;
        send(s);
    }

    public void sendRemove(Assoziation assoziation) {
        String s = "a- " + assoziation.cid + ';' + assoziation.uid;
        send(s);
    }

    public void notifyQueuedMessages() {
        if (queue != null) {
            queue.interrupt();
        }

        queue = new QueueThread();
        queue.start();
    }

    public void setSocketRunning(boolean socketRunning) {
        this.socketRunning = socketRunning;
    }
}
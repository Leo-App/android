package de.slgdev.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.utility.Assoziation;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class ReceiveService extends Service {
    private WebSocket socket;
    private boolean   socketRunning;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.getController().setContext(getApplicationContext());
        Utils.getController().registerReceiveService(this);

        new QueueThread().start();

        startSocket();

        Utils.logDebug("ReceiveService (re)started!");
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
        Utils.logError("ReceiveService stopped!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Utils.logError("ReceiveService removed");
        socket.close(1000, "Service stopped");
        Utils.getController().closeDatabases();
        Utils.getController().registerReceiveService(null);
        super.onTaskRemoved(rootIntent);
    }

    private void startSocket() {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.start();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Utils.URL_TOMCAT)
//                .url(Utils.URL_TOMCAT_DEV)
                .build();

        SocketListener listener = new SocketListener(this, messageHandler);

        socket = client.newWebSocket(request, listener);

        String date = Utils.getController().getMessengerDatabase().getLatestMessage();
        if (date.length() > 3)
            date = date.substring(0, date.length() - 3);

        socket.send("uid=" + Utils.getUserID());
        socket.send("mdate=" + date);
        socket.send("request");
    }

    private void send(String s) {
        startSocketIfNotRunning();
        socket.send(s);
    }

    public void startSocketIfNotRunning() {
        if (!isSocketRunning()) {
            startSocket();
        }
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
        new QueueThread().start();
    }

    void setSocketRunning(boolean socketRunning) {
        this.socketRunning = socketRunning;
    }

    public boolean isSocketRunning() {
        return socketRunning;
    }
}
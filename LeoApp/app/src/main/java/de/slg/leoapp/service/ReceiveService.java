package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.utility.List;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.utility.Assoziation;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ReceiveService extends Service {
    private WebSocket    socket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.getController().setContext(getApplicationContext());
        Utils.getController().registerReceiveService(this);

        new QueueThread().start();

        startSocket();

        Utils.logError("Service (re)started!");
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
        Utils.logError("Service stopped!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Utils.logError("ReceiveService removed");
        socket.close(1000, "Service stopped");
        Utils.getController().closeDatabases();
        Utils.getController().registerReceiveService(null);
        super.onTaskRemoved(rootIntent);
    }

    public void notifyQueuedMessages() {
        new QueueThread().start();
    }

    private void startSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_TOMCAT)
                //.url("ws://192.168.0.103:8080/leoapp/")
                .build();
        Listener listener = new Listener();
        socket = client.newWebSocket(request, listener);

        socket.send("uid=1008");
        socket.send("mdate=0");
        socket.send("request");
    }

    private void assoziationen() {
        try {
            URLConnection connection = new URL(Utils.BASE_URL_PHP + "messenger/getAssoziationen.php?uid=" + Utils.getUserID())
                    .openConnection();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String        l;
            while ((l = reader.readLine()) != null) {
                builder.append(l);
            }
            reader.close();

            if (builder.toString().startsWith("-")) {
                throw new IOException(builder.toString());
            }

            String[]          result = builder.toString().split(";");
            List<Assoziation> list   = new List<>();
            for (String s : result) {
                String[] current = s.split(",");
                if (current.length == 2) {
                    list.append(new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1])));
                }
            }

            Utils.getController().getMessengerDatabase().insertAssoziationen(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class QueueThread extends Thread {
        @Override
        public void run() {
            while (Utils.getController().getMessengerDatabase().hasQueuedMessages())
                if (Utils.checkNetwork())
                    new SendMessages().execute();
        }
    }

    private class SendMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Message[] array = Utils.getController().getMessengerDatabase().getQueuedMessages();
            for (Message m : array) {
                if (Utils.checkNetwork()) {
                    try {
                        HttpURLConnection connection = (HttpURLConnection)
                                new URL(generateURL(m.mtext, m.cid))
                                        .openConnection();

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                connection.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = reader.readLine()) != null)
                            if (line.startsWith("-")) {
                                reader.close();
                                throw new IOException(line);
                            }
                        reader.close();

                        if (connection.getResponseCode() == 200)
                            Utils.getController().getMessengerDatabase().dequeueMessage(m.mid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private String generateURL(String message, int cid) throws UnsupportedEncodingException {
            message = URLEncoder.encode(message, "UTF-8");
            String key      = de.slg.messenger.utility.Utils.Verschluesseln.createKey(message);
            String vMessage = de.slg.messenger.utility.Utils.Verschluesseln.encrypt(message, key);
            String vKey     = de.slg.messenger.utility.Utils.Verschluesseln.encryptKey(key);
            return Utils.BASE_URL_PHP + "messenger/addMessageEncrypted.php?&uid=" + Utils.getUserID() + "&message=" + vMessage + "&cid=" + cid + "&vKey=" + vKey;
        }
    }

    private class Listener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                String[] parts;
                if (text.contains("_ next _")) {
                    parts = text.substring(1, text.indexOf("_ next _")).split("_ ; _");
                } else {
                    parts = new String[0];
                }

                if (text.startsWith("+")) {
                    Utils.logDebug(text);
                } else if (text.startsWith("m") && parts.length == 6) {
                    int    mid   = Integer.parseInt(parts[0]);
                    String mtext = de.slg.messenger.utility.Utils.Verschluesseln.decrypt(parts[1], de.slg.messenger.utility.Utils.Verschluesseln.decryptKey(parts[2])).replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                    long   mdate = Long.parseLong(parts[3] + "000");
                    int    cid   = Integer.parseInt(parts[4]);
                    int    uid   = Integer.parseInt(parts[5]);

                    Utils.getController().getMessengerDatabase().insertMessage(new Message(mid, mtext, mdate, cid, uid));
                    new NotificationHandler.MessengerNotification().send();
                } else if (text.startsWith("c") && parts.length == 3) {
                    int           cid   = Integer.parseInt(parts[0]);
                    String        cname = parts[1].replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                    Chat.ChatType ctype = Chat.ChatType.valueOf(parts[2].toUpperCase());

                    Utils.getController().getMessengerDatabase().insertChat(new Chat(cid, cname, ctype));
                } else if (text.startsWith("u") && parts.length == 5) {
                    int    uid          = Integer.parseInt(parts[0]);
                    String uname        = parts[1].replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                    String ustufe       = parts[2];
                    int    upermission  = Integer.parseInt(parts[3]);
                    String udefaultname = parts[4];

                    Utils.getController().getMessengerDatabase().insertUser(new User(uid, uname, ustufe, upermission, udefaultname));
                } else if (text.startsWith("a")) {
                    assoziationen();
                } else if (text.startsWith("-")) {
                    Utils.logError(text);
                }

                if (Utils.getController().getMessengerActivity() != null)
                    Utils.getController().getMessengerActivity().notifyUpdate();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            startSocket();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Utils.logError(Log.getStackTraceString(t));
        }
    }
}
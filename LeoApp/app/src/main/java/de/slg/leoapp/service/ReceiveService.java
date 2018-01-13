package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.datastructure.List;
import de.slg.leoapp.utility.datastructure.Queue;
import de.slg.messenger.activity.AddGroupChatActivity;
import de.slg.messenger.activity.ChatActivity;
import de.slg.messenger.utility.Assoziation;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ReceiveService extends Service {
    private WebSocket      socket;
    private MessageHandler messageHandler;
    private boolean        socketRunning;

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

    public void notifyQueuedMessages() {
        new QueueThread().start();
    }

    public void startSocket() {
        messageHandler = new MessageHandler();
        messageHandler.start();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Utils.URL_TOMCAT)
                .build();

        Listener listener = new Listener(messageHandler);

        socket = client.newWebSocket(request, listener);

        String date = Utils.getController().getMessengerDatabase().getLatestMessage();
        if (date.length() > 3)
            date = date.substring(0, date.length() - 3);

        socket.send("uid=" + Utils.getUserID());
        socket.send("mdate=" + date);
        socket.send("request");
    }

    public void startIfNotRunning() {
        if (!isSocketRunning()) {
            startSocket();
        }
    }

    public boolean isSocketRunning() {
        return socketRunning;
    }

    private void send(String s) {
        startIfNotRunning();
        Utils.logDebug(s);
        socket.send(s);
    }

    public void send(Message message) {
        String key      = de.slg.messenger.utility.Utils.Encryption.createKey(message.mtext);
        String vMessage = de.slg.messenger.utility.Utils.Encryption.encrypt(message.mtext, key);
        String vKey     = de.slg.messenger.utility.Utils.Encryption.encryptKey(key);
        String s        = "m+ " + message.cid + ';' + vKey + ';' + vMessage;
        send(s);
    }

    public void send(Chat chat) {
        messageHandler.newChats.clear();
        String s = "c+ " + chat.ctype.toString().toUpperCase().charAt(0) + ';' + chat.cname;
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

    private class MessageHandler extends Thread {
        private Queue<String> messagesQueue;
        private Queue<Chat>   newChats;

        MessageHandler() {
            super();
            messagesQueue = new Queue<>();
            newChats = new Queue<>();
        }

        @Override
        public void run() {
            while (true) {
                if (!messagesQueue.isEmpty()) {
                    String message = messagesQueue.getContent();

                    if (message.startsWith("+")) {
                        Utils.logDebug(message);
                        messagesQueue.remove();

                        continue;
                    }

                    if (message.startsWith("-")) {
                        Utils.logError(message);
                        messagesQueue.remove();

                        continue;
                    }

                    String[] parts = message.substring(1).split("_ ; _");

                    if (message.startsWith("m") && parts.length == 6) {
                        int    mid   = Integer.parseInt(parts[0]);
                        String mtext = de.slg.messenger.utility.Utils.Encryption.decrypt(parts[1], de.slg.messenger.utility.Utils.Encryption.decryptKey(parts[2])).replace("_  ;  _", "_ ; _");
                        long   mdate = Long.parseLong(parts[3] + "000");
                        int    cid   = Integer.parseInt(parts[4]);
                        int    uid   = Integer.parseInt(parts[5]);

                        Utils.getController().getMessengerDatabase().insertMessage(new Message(mid, mtext, mdate, cid, uid));
                        if (uid != Utils.getUserID() && cid != de.slg.messenger.utility.Utils.currentlyDisplayedChat())
                            new NotificationHandler.MessengerNotification().send();

                        refresh();

                        messagesQueue.remove();
                        continue;
                    }

                    if (message.startsWith("c") && parts.length == 3) {
                        int           cid   = Integer.parseInt(parts[0]);
                        String        cname = parts[1].replace("_  ;  _", "_ ; _");
                        Chat.ChatType ctype = Chat.ChatType.valueOf(parts[2].toUpperCase());

                        Chat c = new Chat(cid, cname, ctype);

                        if (!Utils.getController().getMessengerDatabase().contains(c)) {
                            newChats.append(c);
                        }

                        Utils.getController().getMessengerDatabase().insertChat(c);

                        refresh();
                        messagesQueue.remove();
                        continue;
                    }

                    if (message.startsWith("u") && parts.length == 5) {
                        int    uid          = Integer.parseInt(parts[0]);
                        String uname        = parts[1].replace("_  ;  _", "_ ; _");
                        String ustufe       = parts[2];
                        int    upermission  = Integer.parseInt(parts[3]);
                        String udefaultname = parts[4];

                        Utils.getController().getMessengerDatabase().insertUser(new User(uid, uname, ustufe, upermission, udefaultname));

                        refresh();
                        messagesQueue.remove();
                        continue;
                    }

                    if (message.startsWith("a")) {
                        parts = message.substring(1).split(";");
                        List<Assoziation> list = new List<>();
                        for (String s : parts) {
                            String[] current = s.split(",");
                            if (current.length == 2) {
                                list.append(new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1])));
                            }
                        }

                        Utils.getController().getMessengerDatabase().insertAssoziationen(list);

                        refresh();
                        messagesQueue.remove();
                    }
                }
            }
        }

        private void refresh() {
            if (Utils.getController().getMessengerActivity() != null)
                Utils.getController().getMessengerActivity().notifyUpdate();
        }

        public void append(String message) {
            messagesQueue.append(message);
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

    private static class SendMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Message[] array = Utils.getController().getMessengerDatabase().getQueuedMessages();
            for (Message m : array) {
                if (Utils.checkNetwork()) {
                    Utils.getController().getReceiveService().send(m);
                    Utils.getController().getMessengerDatabase().dequeueMessage(m.mid);
                }
            }
            return null;
        }
    }

    private class Listener extends WebSocketListener {
        private MessageHandler messageHandler;

        private Listener(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Utils.logDebug("Socket opened!");
            socketRunning = true;
        }

        @Override
        public void onMessage(WebSocket webSocket, String message) {
            Utils.logDebug(message);
            if (message.startsWith("+OK id")) {
                int cid = Integer.parseInt(
                        message.substring(6)
                );
                ChatActivity chatActivity = Utils.getController().getChatActivity();
                if (chatActivity != null) {
                    chatActivity.setCid(cid);
                }
                AddGroupChatActivity addGroupChatActivity = Utils.getController().getAddGroupChatActivity();
                if (addGroupChatActivity != null) {
                    addGroupChatActivity.setCid(cid);
                }
            } else {
                messageHandler.append(message);
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Utils.logDebug("Socket closed!");
            socketRunning = false;
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Utils.logError("Socket Error");
            Utils.logError(t);
            socketRunning = false;
        }
    }
}
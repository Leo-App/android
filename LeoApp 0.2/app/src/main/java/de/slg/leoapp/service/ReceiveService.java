package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slg.leoapp.utility.List;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.WebSocketClient;
import de.slg.messenger.Assoziation;
import de.slg.messenger.Chat;
import de.slg.messenger.Message;
import de.slg.messenger.Verschluesseln;
import de.slg.leoapp.sqlite.SQLiteConnectorNews;

public class ReceiveService extends Service implements WebSocketClient.MessageHandler {
    public  boolean receiveNews;
    private boolean running, socketRunning, idle;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.getController().setContext(getApplicationContext());
        Utils.getController().registerReceiveService(this);

        running = true;
        socketRunning = false;
        receiveNews = false;
        idle = false;

        new ReceiveThread().start();
        new QueueThread().start();

        Log.i("ReceiveService", "Service (re)started!");
        return START_STICKY;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        Utils.getController().registerReceiveService(null);
        Log.i("ReceiveService", "Service stopped!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ReceiveService", "ReceiveService removed");
        Utils.getController().closeDatabases();
        Utils.getController().registerReceiveService(null);
        super.onTaskRemoved(rootIntent);
    }

    public void notifyQueuedMessages() {
        new QueueThread().start();
    }

    @Override
    public void handleMessage(String message) {
        try {
            String[] parts = message.substring(1, message.indexOf("_ next _")).split("_ ; _");

            if (message.startsWith("m") && parts.length == 6) {
                int    mid   = Integer.parseInt(parts[0]);
                String mtext = Verschluesseln.decrypt(parts[1], Verschluesseln.decryptKey(parts[2])).replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                long   mdate = Long.parseLong(parts[3] + "000");
                int    cid   = Integer.parseInt(parts[4]);
                int    uid   = Integer.parseInt(parts[5]);

                Utils.getController().getMessengerDatabase().insertMessage(new Message(mid, mtext, mdate, cid, uid));
            } else if (message.startsWith("c") && parts.length == 3) {
                int           cid   = Integer.parseInt(parts[0]);
                String        cname = parts[1].replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                Chat.ChatType ctype = Chat.ChatType.valueOf(parts[2].toUpperCase());

                Utils.getController().getMessengerDatabase().insertChat(new Chat(cid, cname, ctype));
            } else if (message.startsWith("u") && parts.length == 5) {
                int    uid          = Integer.parseInt(parts[0]);
                String uname        = parts[1].replace("_  ;  _", "_ ; _").replace("_  next  _", "_ next _");
                String ustufe       = parts[2];
                int    upermission  = Integer.parseInt(parts[3]);
                String udefaultname = parts[4];

                Utils.getController().getMessengerDatabase().insertUser(new User(uid, uname, ustufe, upermission, udefaultname));
            } else if (message.startsWith("a")) {
                assoziationen();
            } else if (message.startsWith("-")) {
                Log.e("SocketError", message);
            }

            if (Utils.getController().getMessengerActivity() != null)
                Utils.getController().getMessengerActivity().notifyUpdate();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void startSocket() {
        try {
            final WebSocketClient client = new WebSocketClient(new URI("wss://ucloud4schools.de:8080/leoapp/"));

            socketRunning = true;

            client.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            client.sendMessage("uid=1008");
            client.sendMessage("mdate=0");
            client.sendMessage("request");
        } catch (Throwable t) {
            t.printStackTrace();
        }
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

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();

            while (running) {
                try {
                    if (Utils.checkNetwork()) {
                        if (!socketRunning)
                            startSocket();

                        new ReceiveNews().execute();
                    }

                    while (idle) {
                        sleep(1);
                    }

                    for (int i = 0; i < 2400 && running && !receiveNews; i++)
                        sleep(25);

                    receiveNews = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class ReceiveNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            idle = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.checkNetwork()) {
                getEntries();
                getSurveys();
            }
            return null;
        }

        private void getEntries() {
            try {
                URLConnection connection = new URL(Utils.BASE_URL_PHP + "schwarzesBrett/meldungen.php")
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null)
                    builder.append(line)
                            .append(System.getProperty("line.separator"));
                reader.close();
                SQLiteConnectorNews db  = new SQLiteConnectorNews(getApplicationContext());
                SQLiteDatabase  dbh = db.getWritableDatabase();
                dbh.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + SQLiteConnectorNews.TABLE_EINTRAEGE + "'");
                dbh.delete(SQLiteConnectorNews.TABLE_EINTRAEGE, null, null);
                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    String[] res = s.split(";");
                    if (res.length == 8) {
                        dbh.insert(SQLiteConnectorNews.TABLE_EINTRAEGE, null, db.getEntryContentValues(
                                res[0],
                                res[1],
                                res[2],
                                Long.parseLong(res[3] + "000"),
                                Long.parseLong(res[4] + "000"),
                                Integer.parseInt(res[5]),
                                Integer.parseInt(res[6]),
                                res[7]
                        ));
                    }
                }
                dbh.close();
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getSurveys() {

            try {
                URL updateURL = new URL(Utils.DOMAIN_DEV + "survey/getSurveys.php");
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                reader.close();

                URL resultURL = new URL(Utils.DOMAIN_DEV + "survey/getSingleResult.php?user=" + Utils.getUserID());
                reader =
                        new BufferedReader(
                                new InputStreamReader(resultURL.openConnection().getInputStream(), "UTF-8"));

                StringBuilder resultBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    resultBuilder.append(line);
                reader.close();

                SQLiteConnectorNews db  = new SQLiteConnectorNews(getApplicationContext());
                SQLiteDatabase  dbh = db.getWritableDatabase();
                dbh.delete(SQLiteConnectorNews.TABLE_SURVEYS, null, null);
                dbh.delete(SQLiteConnectorNews.TABLE_ANSWERS, null, null);
                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    String[] res = s.split("_;_");
                    if (res.length >= 6) {
                        long id = dbh.insert(SQLiteConnectorNews.TABLE_SURVEYS, null, db.getSurveyContentValues(
                                res[1],
                                res[3],
                                res[2],
                                res[0],
                                Short.parseShort(res[4]),
                                Integer.parseInt(res[5])
                        ));

                        for (int i = 6; i < res.length - 1; i += 2) {
                            dbh.insert(SQLiteConnectorNews.TABLE_ANSWERS, null, db.getAnswerContentValues(
                                    Integer.parseInt(res[i]),
                                    res[i + 1],
                                    id,
                                    resultBuilder.toString().contains(res[i]) ? 1 : 0
                            ));
                        }
                    }
                }
                dbh.close();
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (Utils.getController().getActiveActivity().equals(Utils.getController().getSchwarzesBrettActivity()))
                Utils.getController().getSchwarzesBrettActivity().refreshUI();

            if (Utils.getController().getActiveActivity().equals(Utils.getController().getSurveyActivity()))
                Utils.getController().getSurveyActivity().refreshUI();

            idle = false;
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
                        while (reader.readLine() != null)
                            ;
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
            String key      = Verschluesseln.createKey(message);
            String vMessage = Verschluesseln.encrypt(message, key);
            String vKey     = Verschluesseln.encryptKey(key);
            return Utils.BASE_URL_PHP + "messenger/addMessageEncrypted.php?&uid=" + Utils.getUserID() + "&message=" + vMessage + "&cid=" + cid + "&vKey=" + vKey;
        }
    }
}
package de.slg.leoapp;

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
import java.net.URL;

import de.slg.messenger.Assoziation;
import de.slg.messenger.Chat;
import de.slg.messenger.Message;
import de.slg.messenger.Verschluesseln;
import de.slg.schwarzes_brett.SQLiteConnector;

public class ReceiveService extends Service {
    private static long interval;
    boolean receiveMessages, receiveNews;
    private boolean running;

    private static long getInterval(int selection) {
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

    public static void setInterval(int selection) {
        interval = getInterval(selection);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.context = getApplicationContext();
        Start.initPref(getApplicationContext());

        Utils.registerReceiveService(this);

        running = true;
        receiveMessages = false;
        receiveNews = false;

        interval = getInterval(Start.pref.getInt("pref_key_refresh", 2));

        new MessengerThread().start();
        new NewsThread().start();

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
        Utils.registerReceiveService(null);
        Log.i("ReceiveService", "Service stopped!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ReceiveService", "TASK REMOVED!");
        Utils.getMDB().close();
        Utils.invalidateMDB();
        super.onTaskRemoved(rootIntent);
    }

    private enum Operator {
        Nachricht, Chat, Benutzer, Assoziation
    }

    private class MessengerThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (running) {
                try {
                    new SendTask().execute();
                    new ReceiveTask().execute();

                    for (int i = 0; i < interval && running && !receiveMessages; i++)
                        sleep(1);

                    receiveMessages = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class NewsThread extends Thread {
        @Override
        public void run() {
            while (running) {
                try {
                    new EmpfangeDaten().execute();

                    for (int i = 0; i < 60000 && running && !receiveNews; i++)
                        sleep(1);

                    receiveNews = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class ReceiveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            nachricht();
            assoziationen();
            chat();
            benutzer();
            return null;
        }

        private void nachricht() {
            if (Utils.isVerified() && Utils.checkNetwork()) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(Operator.Nachricht))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String l;
                    while ((l = reader.readLine()) != null)
                        builder.append(l).append(System.getProperty("line.separator"));
                    reader.close();
                    String[] result = builder.toString().split("_ next_");
                    for (String s : result) {
                        String[] message = s.split("_ ;_");
                        if (message.length == 6) {
                            int mid = Integer.parseInt(message[0]);
                            String mtext = Verschluesseln.decrypt(message[1], Verschluesseln.decryptKey(message[2])).replace("_  ;_", "_ ;_");
                            long mdate = Long.parseLong(message[3] + "000");
                            int cid = Integer.parseInt(message[4]);
                            int uid = Integer.parseInt(message[5]);
                            Message m = new Message(mid, mtext, mdate, cid, uid, false);
                            Utils.getMDB().insertMessage(m);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void chat() {
            if (Utils.isVerified() && Utils.checkNetwork()) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(Operator.Chat))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String l;
                    while ((l = reader.readLine()) != null)
                        builder.append(l);
                    reader.close();
                    String erg = builder.toString();
                    String[] result = erg.split("_ next_");
                    for (String s : result) {
                        String[] current = s.split("_ ;_");
                        if (current.length == 3) {
                            Chat c = new Chat(Integer.parseInt(current[0]), current[1], false, Chat.Chattype.valueOf(current[2].toUpperCase()));
                            Utils.getMDB().insertChat(c);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void benutzer() {
            if (Utils.isVerified() && Utils.checkNetwork()) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(Operator.Benutzer))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String l;
                    while ((l = reader.readLine()) != null)
                        builder.append(l);
                    reader.close();
                    String erg = builder.toString();
                    String[] result = erg.split("_ next_");
                    for (String s : result) {
                        String[] current = s.split("_ ;_");
                        if (current.length == 5) {
                            User u = new User(Integer.parseInt(current[0]), current[1], current[2].replace("N/A", "Teacher"), Integer.parseInt(current[3]), current[4]);
                            Utils.getMDB().insertUser(u);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void assoziationen() {
            if (Utils.isVerified() && Utils.checkNetwork()) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(Operator.Assoziation))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String l;
                    while ((l = reader.readLine()) != null)
                        builder.append(l);
                    reader.close();
                    String erg = builder.toString();
                    String[] result = erg.split(";");
                    List<Assoziation> list = new List<>();
                    for (String s : result) {
                        String[] current = s.split(",");
                        if (current.length == 2) {
                            list.append(new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1])));
                        }
                    }
                    Utils.getMDB().insertAssoziationen(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String generateURL(Operator o) {
            switch (o) {
                case Nachricht:
                    return "http://moritz.liegmanns.de/messenger/getMessagesEncrypted.php?key=5453&userid=" + Utils.getUserID();
                case Benutzer:
                    return "http://moritz.liegmanns.de/messenger/getUsers.php?key=5453&userid=" + Utils.getUserID();
                case Chat:
                    return "http://moritz.liegmanns.de/messenger/getChats.php?key=5453&userid=" + Utils.getUserID();
                case Assoziation:
                    return "http://moritz.liegmanns.de/messenger/getAssoziationen.php?key=5453&userid=" + Utils.getUserID();
                default:
                    return "";
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (Utils.getOverviewWrapper() != null)
                Utils.getOverviewWrapper().notifyUpdate();
            Log.i("ReceiveService", "received Messages");
        }
    }

    private class SendTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.checkNetwork()) {
                Message[] array = Utils.getMDB().getUnsendMessages();
                for (Message m : array) {
                    try {
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL(generateURL(m.mtext, m.cid))
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
                        while (reader.readLine() != null) ;
                        reader.close();
                        Utils.getMDB().removeUnsendMessage(m.mid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private String generateURL(String message, int cid) {
            return "http://moritz.liegmanns.de/messenger/addMessage.php?key=5453&userid=" + Utils.getUserID() + "&message=" + message.replace(" ", "%20").replace(System.getProperty("line.separator"), "%0A") + "&chatid=" + cid;
        }
    }

    private class EmpfangeDaten extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.checkNetwork()) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL("http://moritz.liegmanns.de/schwarzesBrett/meldungen.php")
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line)
                                .append(System.getProperty("line.seperator"));
                    reader.close();

                    SQLiteConnector db = new SQLiteConnector(getApplicationContext());
                    SQLiteDatabase dbh = db.getWritableDatabase();
                    dbh.delete(SQLiteConnector.TABLE_EINTRAEGE, null, null);

                    String[] result = builder.toString().split("_next_");
                    for (String s : result) {
                        String[] res = s.split(";");
                        if (res.length == 5) {
                            dbh.insert(SQLiteConnector.TABLE_EINTRAEGE, null, db.getContentValues(res[0], res[1], res[2], Long.parseLong(res[3] + "000"), Long.parseLong(res[4] + "000")));
                        }
                    }

                    dbh.close();
                    db.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("ReceiveService", "received News");
        }
    }
}
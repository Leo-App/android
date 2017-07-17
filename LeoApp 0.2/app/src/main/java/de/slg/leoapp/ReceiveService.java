package de.slg.leoapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.messenger.Assoziation;
import de.slg.messenger.Chat;
import de.slg.messenger.DBConnection;
import de.slg.messenger.Message;

public class ReceiveService extends Service {
    private boolean running, receive;
    private static long interval;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.context = getApplicationContext();
        Start.initPref(getApplicationContext());
        interval = getInterval(Start.pref.getInt("pref_key_refresh", 2));
        Utils.registerReceiveService(this);
        running = true;
        receive = false;
        new LoopThread().start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        Utils.registerReceiveService(null);
    }

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

    public void receive() {
        receive = true;
    }

    private class LoopThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (running) {
                try {
                    new SendTask().execute();
                    new ReceiveTask().execute();

                    for (int i = 0; i < interval && running && !receive; i++)
                        sleep(1);

                    receive = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                    String[] result = builder.toString().split("_nextMessage_");
                    for (String s : result) {
                        String[] message = s.split("_;_");
                        if (message.length == 5) {
                            int mid = Integer.parseInt(message[0]);
                            String mtext = message[1];
                            long mdate = Long.parseLong(message[2] + "000");
                            int cid = Integer.parseInt(message[3]);
                            int uid = Integer.parseInt(message[4]);
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
                    String[] result = erg.split("_nextChat_");
                    for (String s : result) {
                        String[] current = s.split("_;_");
                        if (current.length == 3) {
                            Chat c = new Chat(Integer.parseInt(current[0]), current[1], Chat.Chattype.valueOf(current[2].toUpperCase()));
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
                    String[] result = erg.split("_nextUser_");
                    for (String s : result) {
                        String[] current = s.split("_;_");
                        if (current.length == 4) {
                            User u = new User(Integer.parseInt(current[0]), current[1], current[2], Integer.parseInt(current[3]));
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
                    String[] result = erg.split("_nextAssoziation_");
                    Utils.getMDB().clearTable(DBConnection.DBHelper.TABLE_ASSOZIATION);
                    for (String s : result) {
                        String[] current = s.split("_;_");
                        if (current.length == 2) {
                            Assoziation a = new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1]));
                            Utils.getMDB().insertAssoziation(a);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String generateURL(Operator o) {
            switch (o) {
                case Nachricht:
                    return "http://moritz.liegmanns.de/messenger/getMessages.php?key=5453&userid=" + Utils.getUserID();
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

    private enum Operator {
        Nachricht, Chat, Benutzer, Assoziation
    }
}
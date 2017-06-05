package de.slg.messenger;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class ReceiveTask extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... params) {
        assoziationen();
        chat();
        benutzer();
        return nachricht();
    }

    private boolean nachricht() {
        boolean b = false;
        if (Utils.isVerified()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(generateURL(Operator.Nachricht)).openConnection().getInputStream(), "UTF-8"));
                String erg = "";
                String l;
                while ((l = reader.readLine()) != null)
                    erg += l;
                erg = erg.replaceAll("_l_", System.getProperty("line.separator"));
                String[] result = erg.split("_nextMessage_");
                Log.i("Tag", erg);
                for (String s : result) {
                    String[] message = s.split(";");
                    if (message.length == 5) {
                        Message m = new Message(Integer.parseInt(message[0]), message[1], Long.parseLong(message[2]+"000"), Integer.parseInt(message[3]), Integer.parseInt(message[4]), false);
                        Utils.getMessengerDBConnection().insertMessage(m);
                        if (m.senderId != Utils.getUserID())
                            b = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    private void chat() {
        if (Utils.isVerified()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(generateURL(Operator.Chat)).openConnection().getInputStream(), "UTF-8"));
                String erg = "";
                String l;
                while ((l = reader.readLine()) != null)
                    erg += l;
                String[] result = erg.split("_nextChat_");
                for (String s : result) {
                    String[] current = s.split(";");
                    if (current.length == 3) {
                        Chat c = new Chat(Integer.parseInt(current[0]), current[1], Chat.Chattype.valueOf(current[2].toUpperCase()));
                        Utils.getMessengerDBConnection().insertChat(c);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void benutzer() {
        if (Utils.isVerified()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(generateURL(Operator.Benutzer)).openConnection().getInputStream(), "UTF-8"));
                String erg = "";
                String l;
                while ((l = reader.readLine()) != null)
                    erg += l;
                String[] result = erg.split("_nextUser_");
                for (String s : result) {
                    String[] current = s.split(";");
                    if (current.length == 4) {
                        User u = new User(Integer.parseInt(current[0]), current[1], current[2], Integer.parseInt(current[3]));
                        Utils.getMessengerDBConnection().insertUser(u);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void assoziationen() {
        if (Utils.isVerified()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(generateURL(Operator.Assoziation)).openConnection().getInputStream(), "UTF-8"));
                String erg = "";
                String l;
                while ((l = reader.readLine()) != null)
                    erg += l;
                String[] result = erg.split("_nextAssoziation_");
                for (String s : result) {
                    String[] current = s.split(";");
                    if (current.length == 3) {
                        Assoziation a = new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1]), Boolean.parseBoolean(current[2]));
                        Utils.getMessengerDBConnection().insertAssoziation(a);
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
                return "http://moritz.liegmanns.de/messenger/receive.php?key=5453&userid=" + Utils.getUserID();
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
}
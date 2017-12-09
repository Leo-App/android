package de.slg.messenger.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatActivity;
import de.slg.messenger.utility.Assoziation;
import de.slg.messenger.utility.Chat;

public class CreateChat extends AsyncTask<Integer, Void, Intent> {
    private final String   cname;
    private final Activity activity;
    private       int      cid;

    public CreateChat(Activity activity, String cname) {
        this.activity = activity;
        this.cname = cname;
        this.cid = -1;
    }

    @Override
    protected Intent doInBackground(Integer... params) {
        sendChat();

        if (cid != -1) {
            sendAssoziation(new Assoziation(cid, Utils.getUserID()));
            for (Integer i : params) {
                sendAssoziation(new Assoziation(cid, i));
            }
        }

        return new Intent(activity, ChatActivity.class)
                .putExtra("cid", cid)
                .putExtra("cname", cname)
                .putExtra("ctype", Chat.ChatType.GROUP.toString());
    }

    private void sendChat() {
        try {
            URLConnection connection = new URL(generateURL(cname))
                    .openConnection();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String        l;
            while ((l = reader.readLine()) != null)
                builder.append(l);
            reader.close();
            Utils.logDebug(builder);

            cid = Integer.parseInt(builder.toString());

            Utils.getController().getMessengerDatabase().insertChat(new Chat(cid, cname, Chat.ChatType.GROUP));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAssoziation(Assoziation assoziation) {
        if (assoziation != null)
            try {
                URLConnection connection = new URL(generateURL(assoziation))
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        l;
                while ((l = reader.readLine()) != null)
                    builder.append(l);
                reader.close();
                Utils.logDebug(builder);

                Utils.getController().getMessengerDatabase().insertAssoziation(assoziation);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private String generateURL(String cname) throws UnsupportedEncodingException {
        return Utils.BASE_URL_PHP + "messenger/addChat.php?cname=" + URLEncoder.encode(cname, "UTF-8") + "&ctype=" + Chat.ChatType.GROUP.toString().toLowerCase();
    }

    private String generateURL(Assoziation assoziation) {
        return Utils.BASE_URL_PHP + "messenger/addAssoziation.php?uid=" + assoziation.uid + "&cid=" + assoziation.cid;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        activity.startActivity(intent);
        activity.finish();
    }
}

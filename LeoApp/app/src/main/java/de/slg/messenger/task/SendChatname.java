package de.slg.messenger.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatEditActivity;

/**
 * Created by Moritz on 08.12.2017.
 */
public class SendChatname extends AsyncTask<String, Void, Void> {
    private final int              cid;
    private       ChatEditActivity chatEditActivity;

    public SendChatname(ChatEditActivity chatEditActivity, int cid) {
        this.chatEditActivity = chatEditActivity;
        this.cid = cid;
    }

    @Override
    protected void onPreExecute() {
        chatEditActivity.notifyTaskStarted(this);
    }

    @Override
    protected Void doInBackground(String... params) {
        if (Utils.checkNetwork()) {
            try {
                URLConnection connection = new URL(generateURL(params[0]))
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                Utils.logDebug(builder.toString());
                chatEditActivity.setCname(params[0]);
            } catch (Exception e) {
                Utils.logError(e);
            }
        }
        return null;
    }

    private String generateURL(String name) throws UnsupportedEncodingException {
        return Utils.BASE_URL_PHP + "messenger/editChatname.php?cid=" + cid + "&cname=" + URLEncoder.encode(name, "UTF-8");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        chatEditActivity.notifyTaskDone(this);
    }
}

package de.slgdev.messenger.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.activity.ChatEditActivity;

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
        if (Utils.isNetworkAvailable()) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new URL(
                                        generateURL(params[0])
                                )
                                        .openConnection()
                                        .getInputStream(),
                                "UTF-8"
                        )
                );

                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();

                if (builder.charAt(0) != '-')
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

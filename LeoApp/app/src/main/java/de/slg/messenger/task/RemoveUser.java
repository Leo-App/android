package de.slg.messenger.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatEditActivity;

/**
 * Created by Moritz on 08.12.2017.
 */
public class RemoveUser extends AsyncTask<User, Void, Void> {
    private final int              cid;
    private       ChatEditActivity chatEditActivity;

    public RemoveUser(ChatEditActivity chatEditActivity, int cid) {
        this.chatEditActivity = chatEditActivity;
        this.cid = cid;
    }

    @Override
    protected void onPreExecute() {
        chatEditActivity.notifyTaskStarted(this);
    }

    @Override
    protected Void doInBackground(User... params) {
        for (User u : params) {
            removeAssoziation(u.uid);
        }
        return null;
    }

    private void removeAssoziation(int uid) {
        try {
            URLConnection connection = new URL(generateURL(uid))
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
            Utils.getController().getMessengerDatabase().removeUserFormChat(uid, cid);
        } catch (Exception e) {
            Utils.logError(e);
        }
    }

    private String generateURL(int uid) {
        return Utils.BASE_URL_PHP + "messenger/removeAssoziation.php?cid=" + cid + "&uid=" + uid;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        chatEditActivity.notifyTaskDone(this);
    }
}

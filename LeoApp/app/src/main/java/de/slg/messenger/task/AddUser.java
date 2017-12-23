package de.slg.messenger.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatEditActivity;
import de.slg.messenger.utility.Assoziation;

public class AddUser extends AsyncTask<User, Void, Void> {
    private final int              cid;
    private       ChatEditActivity chatEditActivity;

    public AddUser(ChatEditActivity chatEditActivity, int cid) {
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
            sendAssoziation(new Assoziation(cid, u.uid));
        }
        return null;
    }

    private void sendAssoziation(Assoziation assoziation) {
        if (assoziation != null) {
            try {
                URLConnection connection = new URL(generateURL(assoziation))
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
                Utils.getController().getMessengerDatabase().insertAssoziation(assoziation);
            } catch (Exception e) {
                Utils.logError(e);
            }
        }
    }

    private String generateURL(Assoziation assoziation) {
        return Utils.BASE_URL_PHP + "messenger/addAssoziation.php?uid=" + assoziation.uid + "&cid=" + assoziation.cid;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        chatEditActivity.notifyTaskDone(this);
    }
}

package de.slg.messenger.task;

import android.os.AsyncTask;

import de.slg.leoapp.Start;
import de.slg.leoapp.service.ReceiveService;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatEditActivity;

/**
 * Created by Moritz on 08.12.2017.
 */
public class RemoveUser extends AsyncTask<User, Void, Void> {
    private final int              cid;
    private       ReceiveService   service;
    private       ChatEditActivity chatEditActivity;

    public RemoveUser(ChatEditActivity chatEditActivity, int cid) {
        this.chatEditActivity = chatEditActivity;
        this.cid = cid;
        this.service = Utils.getController().getReceiveService();

        if (service == null) {
            Start.startReceiveService();
            this.service = Utils.getController().getReceiveService();
        }
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
        service.startIfNotRunning();

        service.send("a- " + cid + ';' + uid);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        chatEditActivity.notifyTaskDone(this);
    }
}

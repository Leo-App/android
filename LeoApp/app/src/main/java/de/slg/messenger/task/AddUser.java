package de.slg.messenger.task;

import android.os.AsyncTask;

import de.slg.leoapp.Start;
import de.slg.leoapp.service.ReceiveService;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.ChatEditActivity;
import de.slg.messenger.utility.Assoziation;

public class AddUser extends AsyncTask<User, Void, Void> {
    private final int              cid;
    private       ChatEditActivity chatEditActivity;
    private       ReceiveService   service;

    public AddUser(ChatEditActivity chatEditActivity, int cid) {
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
        service.startSocketIfNotRunning();
        for (User u : params) {
            service.send(new Assoziation(cid, u.uid));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        chatEditActivity.notifyTaskDone(this);
    }
}

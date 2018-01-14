package de.slg.leoapp.service;

import android.os.AsyncTask;

import de.slg.leoapp.utility.Utils;
import de.slg.messenger.utility.Message;

class SendMessages extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        Message[] array = Utils.getController().getMessengerDatabase().getQueuedMessages();
        for (Message m : array) {
            if (Utils.checkNetwork()) {
                Utils.getController().getReceiveService().send(m);
                Utils.getController().getMessengerDatabase().dequeueMessage(m.mid);
            }
        }
        return null;
    }
}
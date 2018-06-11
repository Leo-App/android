package de.slgdev.messenger.network;

import android.os.AsyncTask;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.utility.Message;

class SendMessages extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        Message[] array = Utils.getController().getMessengerDatabase().getQueuedMessages();
        for (Message m : array) {
            if (Utils.isNetworkAvailable()) {
                Utils.getController().getSocketService().send(m);
                Utils.getController().getMessengerDatabase().dequeueMessage(m.mid);
            }
        }
        return null;
    }
}
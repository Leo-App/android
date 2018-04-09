package de.slgdev.messenger.network;

import de.slgdev.leoapp.utility.Utils;

public class QueueThread extends Thread {
    @Override
    public void run() {
        while (Utils.getController().getMessengerDatabase().hasQueuedMessages())
            if (Utils.isNetworkAvailable())
                new SendMessages().execute();
    }
}

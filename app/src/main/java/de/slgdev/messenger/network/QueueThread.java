package de.slgdev.messenger.network;

import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.Utils;

public class QueueThread extends Thread {
    @Override
    public void run() {
        while (Utils.getController().getMessengerDatabase().hasQueuedMessages())
            if (NetworkUtils.isNetworkAvailable())
                new SendMessages().execute();
    }
}

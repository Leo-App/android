package de.slgdev.leoapp.service;

import de.slgdev.leoapp.utility.Utils;

class QueueThread extends Thread {
    @Override
    public void run() {
        while (Utils.getController().getMessengerDatabase().hasQueuedMessages())
            if (Utils.checkNetwork())
                new SendMessages().execute();
    }
}

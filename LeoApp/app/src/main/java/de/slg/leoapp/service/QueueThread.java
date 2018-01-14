package de.slg.leoapp.service;

import de.slg.leoapp.utility.Utils;

class QueueThread extends Thread {
    @Override
    public void run() {
        while (Utils.getController().getMessengerDatabase().hasQueuedMessages())
            if (Utils.checkNetwork())
                new SendMessages().execute();
    }
}

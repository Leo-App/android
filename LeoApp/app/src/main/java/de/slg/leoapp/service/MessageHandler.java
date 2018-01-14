package de.slg.leoapp.service;

import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.datastructure.List;
import de.slg.leoapp.utility.datastructure.Queue;
import de.slg.messenger.utility.Assoziation;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;

class MessageHandler extends Thread {
    private Queue<String> messagesQueue;
    private Queue<Chat>   newChats;

    MessageHandler() {
        super();
        messagesQueue = new Queue<>();
        newChats = new Queue<>();
    }

    @Override
    public void run() {
        while (true) {
            if (!messagesQueue.isEmpty()) {
                String message = messagesQueue.getContent();

                if (message.startsWith("+")) {
                    Utils.logDebug(message);
                    messagesQueue.remove();

                    continue;
                }

                if (message.startsWith("-")) {
                    Utils.logError(message);
                    messagesQueue.remove();

                    continue;
                }

                String[] parts = message.substring(1).split("_ ; _");

                if (message.startsWith("m") && parts.length == 6) {
                    Utils.logDebug(message);
                    int    mid   = Integer.parseInt(parts[0]);
                    String mtext = de.slg.messenger.utility.Encryption.decrypt(parts[1], de.slg.messenger.utility.Encryption.decryptKey(parts[2])).replace("_  ;  _", "_ ; _");
                    long   mdate = Long.parseLong(parts[3] + "000");
                    int    cid   = Integer.parseInt(parts[4]);
                    int    uid   = Integer.parseInt(parts[5]);

                    Utils.getController().getMessengerDatabase().insertMessage(new Message(mid, mtext, mdate, cid, uid));
                    if (uid != Utils.getUserID() && cid != de.slg.messenger.utility.Utils.currentlyDisplayedChat())
                        new NotificationHandler.MessengerNotification().send();

                    refresh();

                    messagesQueue.remove();
                    continue;
                }

                if (message.startsWith("c") && parts.length == 3) {
                    int           cid   = Integer.parseInt(parts[0]);
                    String        cname = parts[1].replace("_  ;  _", "_ ; _");
                    Chat.ChatType ctype = Chat.ChatType.valueOf(parts[2].toUpperCase());

                    Chat c = new Chat(cid, cname, ctype);

                    if (!Utils.getController().getMessengerDatabase().contains(c)) {
                        newChats.append(c);
                    }

                    Utils.getController().getMessengerDatabase().insertChat(c);

                    refresh();
                    messagesQueue.remove();
                    continue;
                }

                if (message.startsWith("u") && parts.length == 5) {
                    int    uid          = Integer.parseInt(parts[0]);
                    String uname        = parts[1].replace("_  ;  _", "_ ; _");
                    String ustufe       = parts[2];
                    int    upermission  = Integer.parseInt(parts[3]);
                    String udefaultname = parts[4];

                    Utils.getController().getMessengerDatabase().insertUser(new User(uid, uname, ustufe, upermission, udefaultname));

                    refresh();
                    messagesQueue.remove();
                    continue;
                }

                if (message.startsWith("a")) {
                    parts = message.substring(1).split(";");
                    List<Assoziation> list = new List<>();
                    for (String s : parts) {
                        String[] current = s.split(",");
                        if (current.length == 2) {
                            list.append(new Assoziation(Integer.parseInt(current[0]), Integer.parseInt(current[1])));
                        }
                    }

                    Utils.getController().getMessengerDatabase().insertAssoziationen(list);

                    refresh();
                    messagesQueue.remove();
                }
            }
        }
    }

    private void refresh() {
        if (Utils.getController().getMessengerActivity() != null)
            Utils.getController().getMessengerActivity().notifyUpdate();
    }

    public void append(String message) {
        messagesQueue.append(message);
    }
}
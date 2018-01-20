package de.slg.messenger.utility;

public abstract class MessengerUtils {
    private static int currentlyDisplayedChatId = -1;

    public static int currentlyDisplayedChat() {
        return currentlyDisplayedChatId;
    }

    public static void setCurrentlyDisplayedChat(int cid) {
        currentlyDisplayedChatId = cid;
    }
}
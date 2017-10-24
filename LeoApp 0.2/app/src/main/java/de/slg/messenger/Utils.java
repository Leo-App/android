package de.slg.messenger;

public abstract class Utils {
    private static int currentlyDisplayedChatId = -1;

    static int currentlyDisplayedChat() {
        return currentlyDisplayedChatId;
    }

    static void setCurrentlyDisplayedChat(int cid) {
        currentlyDisplayedChatId = cid;
    }
}

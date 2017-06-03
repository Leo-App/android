package de.slg.messenger;

public class Assoziation {

    public final int chatID;
    public final int userID;
    public final boolean removed;

    public Assoziation(int cid, int uid, boolean removed) {
        this.chatID = cid;
        this.userID = uid;
        this.removed = removed;
    }

    public boolean allAttributesSet() {
        return chatID > 0 && userID > 0;
    }

    @Override
    public String toString() {
        return "chat: " + chatID + ", user: " + userID + ", entfernt: " + removed;
    }
}
package de.slg.messenger;

public class Assoziation {

    final int chatID;
    final int userID;
    final boolean removed;

    public Assoziation(int cid, int uid, boolean removed) {
        this.chatID = cid;
        this.userID = uid;
        this.removed = removed;
    }

    @Override
    public String toString() {
        return "chat: " + chatID + ", user: " + userID + ", entfernt: " + removed;
    }
}
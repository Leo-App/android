package de.slg.messenger;

class Assoziation {

    final int chatID;
    final int userID;
    final boolean removed;

    Assoziation(int cid, int uid, boolean removed) {
        this.chatID = cid;
        this.userID = uid;
        this.removed = removed;
    }

    boolean allAttributesSet() {
        return chatID > 0 && userID > 0;
    }

    @Override
    public String toString() {
        return "chat: " + chatID + ", user: " + userID + ", entfernt: " + removed;
    }
}
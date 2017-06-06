package de.slg.messenger;

class Chat {

    int chatId;
    String chatName;
    final Chattype chatTyp;
    Message letzeNachricht;
    String chatTitle;

    public Chat(int cId, String cName, Chattype cTyp) {
        this.chatId = cId;
        this.chatName = cName;
        this.chatTyp = cTyp;
    }

    @Override
    public String toString() {
        return "id: " + chatId + ", name: " + chatName + ", typ: " + chatTyp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chat))
            return false;
        Chat c = (Chat) o;
        if (chatTyp != Chattype.PRIVATE || c.chatTyp != Chattype.PRIVATE)
            return chatId == c.chatId;
        String[] s1 = c.chatName.split(" - ");
        String[] s2 = chatName.split(" - ");
        return s1.length == 2 && s2.length == 2 && ((s1[0].equals(s2[0]) && s1[1].equals(s2[1])) || (s1[0].equals(s2[1]) && s1[1].equals(s2[0])));
    }

    boolean allAttributesSet() {
        return chatId > 0 && chatName != null && chatTyp != null;
    }

    void setLetzteNachricht(Message m) {
        if (m != null)
            letzeNachricht = m;
    }

    public enum Chattype {
        PRIVATE, GROUP
    }
}
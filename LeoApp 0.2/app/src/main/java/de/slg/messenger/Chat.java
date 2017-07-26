package de.slg.messenger;

public class Chat {
    final boolean mute;
    final Chattype ctype;
    int cid;
    String cname;
    Message m;

    public Chat(int cid, String cname, boolean mute, Chattype ctype) {
        this.cid = cid;
        this.cname = cname;
        this.mute = mute;
        this.ctype = ctype;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chat))
            return false;
        Chat c = (Chat) o;
        if (ctype != Chattype.PRIVATE || c.ctype != Chattype.PRIVATE)
            return cid == c.cid;
        String[] s1 = c.cname.split(" - ");
        String[] s2 = cname.split(" - ");
        return s1.length == 2 && s2.length == 2 && ((s1[0].equals(s2[0]) && s1[1].equals(s2[1])) || (s1[0].equals(s2[1]) && s1[1].equals(s2[0])));
    }

    void setLetzteNachricht(Message m) {
        if (m != null)
            this.m = m;
    }

    public enum Chattype {
        PRIVATE, GROUP
    }
}
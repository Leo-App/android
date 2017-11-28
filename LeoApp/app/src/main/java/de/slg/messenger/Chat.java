package de.slg.messenger;

public class Chat {
    public final int      cid;
    public final String   cname;
    public final ChatType ctype;
    final        boolean  cmute;
    final        Message  m;

    public Chat(int cid, String cname, ChatType ctype) {
        this.cid = cid;
        this.cname = cname;
        this.ctype = ctype;
        this.cmute = false;
        this.m = null;
    }

    public Chat(int cid, String cname, ChatType ctype, boolean cmute) {
        this.cid = cid;
        this.cname = cname;
        this.ctype = ctype;
        this.cmute = cmute;
        this.m = null;
    }

    public Chat(int cid, String cname, ChatType ctype, boolean cmute, Message m) {
        this.cid = cid;
        this.cname = cname;
        this.ctype = ctype;
        this.cmute = cmute;
        this.m = m;
    }

    public enum ChatType {
        PRIVATE, GROUP
    }
}
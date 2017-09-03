package de.slg.messenger;

public class Chat {
    final boolean  cmute;
    final ChatType ctype;
    final int      cid;
    final String   cname;
    final Message  m;

    public Chat(int cid, String cname, ChatType ctype) {
        this.cid = cid;
        this.cname = cname;
        this.ctype = ctype;
        this.cmute = false;
        this.m = null;
    }

    Chat(int cid, String cname, ChatType ctype, boolean cmute) {
        this.cid = cid;
        this.cname = cname;
        this.ctype = ctype;
        this.cmute = cmute;
        this.m = null;
    }

    Chat(int cid, String cname, ChatType ctype, boolean cmute, Message m) {
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
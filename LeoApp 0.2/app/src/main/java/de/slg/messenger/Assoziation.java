package de.slg.messenger;

public class Assoziation {
    final int cid;
    final int uid;
    final boolean aremoved;

    public Assoziation(int cid, int uid, boolean aremoved) {
        this.cid = cid;
        this.uid = uid;
        this.aremoved = aremoved;
    }
}
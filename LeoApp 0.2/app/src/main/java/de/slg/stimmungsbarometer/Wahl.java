package de.slg.stimmungsbarometer;

public class Wahl {

    public final int voteid;
    public final int userid;
    public final String grund;

    public Wahl(int voteid, int userid, String grund) {
        this.voteid = voteid;
        this.userid = userid;
        this.grund = grund;
    }
}
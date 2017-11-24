package de.slg.stimmungsbarometer;

class Wahl {
    final int    voteid;
    final int    userid;
    final String grund;

    Wahl(int voteid, int userid, String grund) {
        this.voteid = voteid;
        this.userid = userid;
        this.grund = grund;
    }
}

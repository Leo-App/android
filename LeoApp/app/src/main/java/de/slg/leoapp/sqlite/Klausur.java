package de.slg.leoapp.sqlite;

import java.util.Date;

public class Klausur {
    private final String titel;
    private final Date   datum;
    private final String notiz;

    public Klausur(String titel, Date datum, String notiz) {
        this.titel = titel;
        this.datum = datum;
        this.notiz = notiz;
    }

    public String getTitel() {
        return titel;
    }

    public Date getDatum() {
        return datum;
    }

    public String getNotiz() {
        return notiz;
    }
}
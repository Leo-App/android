package de.slg.leoapp.sqlite;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Klausur {
    private final int    id;
    private final String titel;
    private final Date   datum;
    private final String notiz;

    public Klausur(int id, String titel, Date datum, String notiz) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId() + "," + getTitel() + ',' + new SimpleDateFormat("dd.MM.yyyy").format(getDatum()) + ',' + getNotiz();
    }
}
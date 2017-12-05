package de.slg.klausurplan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Klausur {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

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

    String getTitel() {
        return titel;
    }

    Date getDatum() {
        return datum;
    }

    String getNotiz() {
        return notiz;
    }

    int getId() {
        return id;
    }
}
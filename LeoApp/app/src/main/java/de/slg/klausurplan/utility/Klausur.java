package de.slg.klausurplan.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Klausur {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

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
}
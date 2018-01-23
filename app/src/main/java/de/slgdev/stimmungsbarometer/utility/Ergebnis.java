package de.slgdev.stimmungsbarometer.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ergebnis {
    public final Date    date;
    public final double  value;
    public final boolean ich;
    public final boolean schueler;
    public final boolean lehrer;
    public final boolean alle;

    public Ergebnis(Date date, double value, boolean ich, boolean schueler, boolean lehrer, boolean alle) {
        this.date = date;
        this.value = value;
        this.ich = ich;
        this.schueler = schueler;
        this.lehrer = lehrer;
        this.alle = alle;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        return "" + format.format(date) + ", " + value + ", " + ich + ", " + schueler + ", " + lehrer + ", " + alle;
    }
}

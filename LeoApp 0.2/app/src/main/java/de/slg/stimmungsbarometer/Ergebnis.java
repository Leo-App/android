package de.slg.stimmungsbarometer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Ergebnis {
    final Date    date;
    final double  value;
    final boolean ich;
    final boolean schueler;
    final boolean lehrer;
    final boolean alle;

    Ergebnis(Date date, double value, boolean ich, boolean schueler, boolean lehrer, boolean alle) {
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

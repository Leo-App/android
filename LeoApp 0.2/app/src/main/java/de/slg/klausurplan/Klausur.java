package de.slg.klausurplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class Klausur {

    Date datum;
    private String fach;
    private String notiz;
    private String note;

    Klausur(String fach, Date datum, String notiz, String note) {
        if (fach == null)
            fach = "";
        this.fach = fach;

        this.datum = datum;

        if (notiz == null || notiz.equals("null"))
            notiz = "";
        this.notiz = notiz;

        if (note == null || note.equals("null"))
            note = "";
        this.note = note;
    }

    public String getFach() {
        if (fach == null)
            return "";
        return fach;
    }

    public void setFach(String fach) {
        if (fach != null)
            this.fach = fach;
    }

    String getDatum(boolean mitWochentag) {
        if (datum == null)
            return "";

        if (mitWochentag)
            return new SimpleDateFormat("E").format(datum).substring(0, 2) + ", " + new SimpleDateFormat("dd.MM.yy").format(datum);

        return new SimpleDateFormat("dd.MM.yy").format(datum);
    }

    String getNotiz() {
        if (notiz == null)
            return "";
        return notiz;
    }

    void setNotiz(String notiz) {
        if (notiz == null)
            notiz = "";
        this.notiz = notiz;
    }

    void setDatum(Date datum) {
        if (datum != null)
            this.datum = datum;
    }

    @Override
    public String toString() {
        return fach + System.getProperty("line.separator") + getDatum(true);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Klausur) {
            Klausur k = (Klausur) o;
            return k.getFach().equals(getFach()) && k.getDatum(false).equals(getDatum(false));
        }
        return false;
    } //true, wenn Fach und Datum übereinstimmen

    boolean after(Klausur klausur) {
        return klausur != null && datum != null && klausur.datum != null && datum.getTime() > klausur.datum.getTime();
    } //wenn das Datum der Klausur später ist als das der anderen

    String getWriterString() {
        if (getFach().equals("") || getDatum(false).equals(""))
            return null;
        String ergebnis = "";
        ergebnis += fach + ";";
        ergebnis += datum.getTime() + ";";
        if (notiz != null && !notiz.equals(""))
            ergebnis += notiz + ";";
        else
            ergebnis += "null;";
        if (note != null && !note.equals(""))
            ergebnis += note;
        else
            ergebnis += "null";
        return ergebnis;
    }

    boolean istEFKlausur() {
        return this.fach.endsWith("EF");
    }

    boolean istQ1Klausur() {
        return this.fach.endsWith("Q1");
    }

    boolean istQ2Klausur() {
        return this.fach.endsWith("Q2");
    }

    boolean istGleicheWoche(Klausur other) {
        Calendar calendar = new GregorianCalendar(), calendar1 = new GregorianCalendar();
        calendar.setTime(this.datum);
        calendar1.setTime(other.datum);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) && calendar.get(Calendar.WEEK_OF_YEAR) == calendar1.get(Calendar.WEEK_OF_YEAR);
    }

    String getWoche() {
        return new SimpleDateFormat("'Woche' w").format(datum);
    }
}

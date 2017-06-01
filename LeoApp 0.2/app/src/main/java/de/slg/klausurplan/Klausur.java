package de.slg.klausurplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Klausur {

    private String fach;
    public Date datum;
    private String notiz;
    private String note;

    public Klausur(String fach, Date datum, String notiz, String note) {
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

    public String getDatum(boolean mitWochentag) {
        if (datum == null)
            return "";
        if (mitWochentag) {
            return new SimpleDateFormat("E").format(datum).substring(0, 2) + ", " + new SimpleDateFormat("dd.MM.yy").format(datum);
        }
        return new SimpleDateFormat("dd.MM.yy").format(datum);
    }

    public String getNotiz() {
        if (notiz == null)
            return "";
        return notiz;
    }

    public String getNote() {
        if (note == null)
            return "";
        return note;
    }

    public void setFach(String fach) {
        if (fach != null)
            this.fach = fach;
    }

    public void setDatum(Date datum) {
        if (datum != null)
            this.datum = datum;
    }

    public void setNotiz(String notiz) {
        if (notiz == null)
            notiz = "";
        this.notiz = notiz;
    }

    public void setNote(String note) {
        if (note == null)
            note = "";
        this.note = note;
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

    public boolean after(Klausur klausur) {
        if (klausur != null && datum != null && klausur.datum != null)
            return datum.getTime() > klausur.datum.getTime();
        return false;
    }//wenn das Datum der Klausur später ist als das der anderen

    public String getWriterString() {
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

    public boolean istEFKlausur() {
        return this.fach.endsWith("EF");
    }

    public boolean istQ1Klausur() {
        return this.fach.endsWith("Q1");
    }

    public boolean istQ2Klausur() {
        return this.fach.endsWith("Q2");
    }

    public boolean istGleicheWoche(Klausur other) {
        Calendar calendar = new GregorianCalendar(), calendar1 = new GregorianCalendar();
        calendar.setTime(this.datum);
        calendar1.setTime(other.datum);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) && calendar.get(Calendar.WEEK_OF_YEAR) == calendar1.get(Calendar.WEEK_OF_YEAR);
    }

    public String getWoche() {
        return new SimpleDateFormat("'Woche' w").format(datum);
    }
}

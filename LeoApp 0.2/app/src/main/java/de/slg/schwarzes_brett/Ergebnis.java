package de.slg.schwarzes_brett;

/**
 * Created by Kim on 29.04.2017.
 */

public class Ergebnis {
    public final String Stufe;
    public final String Inhalt;
    public final String Erstelldatum;
    public final String Ablaufdatum;
    public final String Titel;

    public Ergebnis(String stufe, String titel, String inhalt, String erstelldatum, String ablaufdatum)

    {
        this.Stufe = stufe;
        this.Titel = titel;
        this.Inhalt = inhalt;
        this.Erstelldatum = erstelldatum;
        this.Ablaufdatum = ablaufdatum;
    }

    @Override
    public String toString() {
        return "" + Stufe + ", " + Inhalt + ", " +Erstelldatum + ", " + Ablaufdatum;
    }
}

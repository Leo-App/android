package de.slgdev.vertretungsplan.utility;

public class VertretungsEvent {

    private final String klasse;
    private final String stunde;
    private final String vertreter;
    private final String fach;
    private final String raum;
    private final String lehrer;        //Damit ist der Lehrer gemeint, dessen Stunde vertreten wird.
    private final String anmerkung;
    private final boolean entfall;
    private final String datum;

    public VertretungsEvent(String pKlasse, String pStunde, String pVertreter, String pFach, String pRaum, String pLehrer, String pAnmerkung, boolean pEntfall, String pDatum) {
        klasse = pKlasse;
        stunde = pStunde;
        vertreter = pVertreter;
        fach = pFach;
        raum = pRaum;
        lehrer = pLehrer;
        anmerkung = pAnmerkung;
        entfall = pEntfall;
        datum = pDatum;
    }

    public String getKlasse()   { return klasse;}
    public String getStunde()   { return stunde;}
    public String getVertreter()   { return vertreter;}
    public String getFach()   { return fach;}
    public String getRaum()   { return raum;}
    public String getLehrer()   { return lehrer;}
    public String getAnmerkung()   { return anmerkung;}
    public boolean getEntfall()   { return entfall;}
    public String getDatum()   { return datum;}

    public String toString() {
        return "Klasse: "+klasse+"; Stunde: "+stunde+"; Vertreter: "+vertreter+"; Fach: "+fach+"; Raum: "+raum+"; Lehrer: "+lehrer+"; Anmerkung: "+anmerkung+"; Entfall: "+entfall+"; Datum: "+datum+";";
    }
}

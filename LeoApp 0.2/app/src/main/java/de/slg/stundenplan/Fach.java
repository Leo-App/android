package de.slg.stundenplan;

import android.content.Context;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class Fach {
    private int id;
    private final String fachname;
    private final String fachkuerzel;
    private final String raum;
    private final String lehrer;
    private int tag;
    private int stunde;
    private boolean schriftlich;
    private boolean ende;
    private String notiz;
    private final Context context;

    public Fach(String pFachkurzel, String pRaum, String pLehrer, String pTag, String pStunde, Context pContext) {
        //Erstellt ein Fach
        context = pContext;
        tag = Integer.parseInt(pTag);
        stunde = Integer.parseInt(pStunde);
        fachkuerzel = pFachkurzel;
        fachname = this.gibFachname();
        raum = pRaum;
        lehrer = pLehrer;
        schriftlich = false;
        notiz = "";
        ende = false;
    }

    public Fach(int id, String kurz, String name, String lehrer, String raum, Context pContext) {
        this.id = id;
        this.context = pContext;
        this.fachkuerzel = kurz;
        this.fachname = name;
        this.raum = raum;
        this.lehrer = lehrer;
    }

    private String gibFachname() {
        if (fachkuerzel.length() <= 1) {
            return "";
        }
        String kurzelTeil = fachkuerzel.substring(0, 2);
        String name = this.gibFachnameTeil(kurzelTeil);
        if (fachkuerzel.charAt(2) == 'L') {
            name = name + " " + Utils.getString(R.string.lk);
        }
        return name;
    }

    private String gibStundenName(int pStunde) {
        if (ende) {
            //Log.e("Luzzia", Integer.toString(pStunde));
            return context.getString(R.string.später);
        }
        switch (pStunde) {
            case 1:
                return "08:00-08:45";
            case 2:
                return "08:50-09:35";
            case 3:
                return "09:50-10:35";
            case 4:
                return "10:40-11:25";
            case 5:
                return "11:40-12:25";
            case 6:
                return "12:30-13:15";
            case 7:
                return "13:30-14:15";
            case 8:
                return "14:20-15:05";
            case 9:
                return "15:10-15:55";
            case 10:
                return "16:00-16:45";
            default:
                return Integer.toString(pStunde);
        }
    }

    private String gibFachnameTeil(String pKurzelTeil) {
        //Macht den ersten Teil von MACHEFACHNAME (da switch case nicht ohne return?)
        switch (pKurzelTeil.toUpperCase()) {
            case "M ":
                return context.getString(R.string.mathe);
            case "D ":
                return context.getString(R.string.deutsch);
            case "L ":
                return context.getString(R.string.latein);
            case "F ":
                return context.getString(R.string.franze);
            case "E ":
                return context.getString(R.string.englisch);
            case "S ":
                return context.getString(R.string.spanisch);
            case "GF":
                return context.getString(R.string.bili);
            case "GE":
                return context.getString(R.string.geschichte);
            case "EK":
                return context.getString(R.string.geo);
            case "SW":
                return context.getString(R.string.sowi);
            case "PA":
                return context.getString(R.string.pada);
            case "KR":
                return context.getString(R.string.reliKat);
            case "ER":
                return context.getString(R.string.reliEv);
            case "PL":
                return context.getString(R.string.philo); // ist das so? // TODO: 28.05.2017
            case "IF":
                return context.getString(R.string.info);
            case "CH":
                return context.getString(R.string.chemie);
            case "PH":
                return context.getString(R.string.physik);
            case "BI":
                return context.getString(R.string.bio);
            case "LI":
                return context.getString(R.string.literatur);
            case "KU":
                return context.getString(R.string.kunst);
            case "MU":
                return context.getString(R.string.musik);
            default:
                return pKurzelTeil;
        }
    }

    //Getter und Setter

    void setzeNotiz(String pNotiz) {
        notiz = pNotiz;
        if (id != 0)
            Utils.getStundDB().setzeNotiz(pNotiz, id);
    }

    void setzeSchriftlich(boolean b) {
        schriftlich = b;
        if (id != 0)
            Utils.getStundDB().setzeSchriftlich(b, id);
    }

    public void setzeEnde(boolean b) {
        ende = b;
    }

    public String gibKurz() {
        return fachkuerzel;
    }

    public String gibName() {
        return fachname;
    }

    public String gibLehrer() {
        return lehrer;
    }

    String gibRaum() {
        return raum;
    }

    int gibTag() {
        return tag;
    }

    int gibStunde() {
        return stunde;
    }

    String gibStundenName() {
        return this.gibStundenName(stunde);
    }

    String gibNotiz() {
        return notiz;
    }

    public boolean gibSchriftlich() {
        return schriftlich;
    }
}
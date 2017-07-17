package de.slg.stundenplan;

import android.content.Context;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class Fach {
    public final int id;
    private final String name;
    private final String kuerzel;
    private final String raum;
    private final String lehrer;
    private final int tag;
    private final int stunde;
    private boolean schriftlich;
    private String notiz;
    private final Context context;

    public Fach(String kurz, String raum, String lehrer, String tag, String stunde, Context context) {
        //Erstellt ein Fach
        this.context = context;
        this.tag = Integer.parseInt(tag);
        this.stunde = Integer.parseInt(stunde);
        kuerzel = kurz;
        name = this.gibFachname();
        this.raum = raum;
        this.lehrer = lehrer;
        schriftlich = false;
        notiz = "";
        id = Utils.getStundDB().idVonKuerzel(kurz);
    }

    public Fach(int id, String kurz, String name, String lehrer, String raum, int tag, int stunde, Context context) {
        this.id = id;
        this.context = context;
        this.kuerzel = kurz;
        this.name = name;
        this.raum = raum;
        this.lehrer = lehrer;
        this.tag = tag;
        this.stunde = stunde;
        this.notiz = "";
    }

    private String gibFachname() {
        if (kuerzel.length() <= 1) {
            return "";
        }
        String kurzelTeil = kuerzel.substring(0, 2);
        String name = this.gibFachnameTeil(kurzelTeil);
        if (kuerzel.charAt(2) == 'L') {
            name = name + " " + Utils.getString(R.string.lk);
        }
        return name;
    }

    private String gibStundenName(int pStunde) {
        switch (pStunde) {
            case 1:
                return "08:00 - 08:45";
            case 2:
                return "08:50 - 09:35";
            case 3:
                return "09:50 - 10:35";
            case 4:
                return "10:40 - 11:25";
            case 5:
                return "11:40 - 12:25";
            case 6:
                return "12:30 - 13:15";
            case 7:
                return "13:30 - 14:15";
            case 8:
                return "14:20 - 15:05";
            case 9:
                return "15:10 - 15:55";
            case 10:
                return "16:00 - 16:45";
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

    void setzeNotiz(String notiz) {
        this.notiz = notiz;
        Utils.getStundDB().setzeNotiz(notiz, id);
    }

    void setzeSchriftlich(boolean b) {
        schriftlich = b;
        Utils.getStundDB().setzeSchriftlich(b, id);
    }

    public String gibKurz() {
        return kuerzel;
    }

    public String gibName() {
        return name;
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
package de.slg.stundenplan;

import android.content.Context;

import de.slg.leoapp.R;

public class Fach {

    private String fachname;
    private String fachkuerzel;
    private String raum;
    private String lehrer;
    private int tag;
    private int stunde;
    private boolean schriftlich;
    private boolean ende;
    private String notiz;
    private Context cc;

    public Fach(String pFachkurzel, String pRaum, String pLehrer, String pTag, String pStunde, Context pContext) {
        //Erstellt ein Fach
        cc = pContext;
        tag = Integer.parseInt(pTag);
        stunde = Integer.parseInt(pStunde);
        fachkuerzel = pFachkurzel;
        fachname = this.macheFachname();
        raum = pRaum;
        lehrer = pLehrer;
        schriftlich = false;
        notiz = "notiz";
        ende = false;
    }

    private String macheFachname() {
        //Erstellt aus dem Kürzel einen voll ausgeschriebenen Fachnamen
        //Nutzt MACHEFACHNAMETEIL
        if (fachkuerzel.length() <= 1) {
            //Log.e("Luzzzia", "fachkürzel ist zu klein: "+fachkuerzel.length()+ " "+tag+","+stunde);
            return "";
        } else {
            String kurzelTeil = "";
            for (int i = 0; i < 2; i++) {
                kurzelTeil = kurzelTeil + fachkuerzel.charAt(i);
            }
            String name = this.macheFachnameTeil(kurzelTeil);
            if (fachkuerzel.charAt(2) == 'L') {
                name = name + " " + cc.getString(R.string.lk);
            }
            return name;
        }
        //Funktioniert voll und ganz
    }

    private String macheStundenName(int pStunde) {
        if (ende) {
            //Log.e("Luzzia", Integer.toString(pStunde));
            return cc.getString(R.string.später);
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

    private String macheFachnameTeil(String pKurzelTeil) {
        //Macht den ersten Teil von MACHEFACHNAME (da switch case nicht ohne return?)
        switch (pKurzelTeil.toUpperCase()) {
            case "M ":
                return cc.getString(R.string.mathe);
            case "D ":
                return cc.getString(R.string.deutsch);
            case "L ":
                return cc.getString(R.string.latein);
            case "F ":
                return cc.getString(R.string.franze);
            case "E ":
                return cc.getString(R.string.englisch);
            case "S ":
                return cc.getString(R.string.spanisch);
            case "GF":
                return cc.getString(R.string.bili);
            case "GE":
                return cc.getString(R.string.geschichte);
            case "EK":
                return cc.getString(R.string.geo);
            case "SW":
                return cc.getString(R.string.sowi);
            case "PA":
                return cc.getString(R.string.pada);
            case "KR":
                return cc.getString(R.string.reliKat);
            case "ER":
                return cc.getString(R.string.reliEv);
            case "PL":
                return cc.getString(R.string.philo); // ist das so? // TODO: 28.05.2017
            case "IF":
                return cc.getString(R.string.info);
            case "CH":
                return cc.getString(R.string.chemie);
            case "PH":
                return cc.getString(R.string.physik);
            case "BI":
                return cc.getString(R.string.bio);
            case "LI":
                return cc.getString(R.string.literatur);
            case "KU":
                return cc.getString(R.string.kunst);
            case "MU":
                return cc.getString(R.string.musik);
            default:
                return pKurzelTeil;
        }
    }

    //Getter und Setter

    void setzeNotiz(String pNotiz) {
        notiz = pNotiz;
    }

    void setzeSchriftlich(boolean b) {
        schriftlich = b;
    }

    public void setzeEnde(boolean b) {
        ende = b;
    }

    public String gibKurz() {
        return fachkuerzel;
    }

    String gibName() {
        return fachname;
    }

    public String gibLehrer() {
        return lehrer;
    }

    String gibRaum() {
        return raum;
    }

    String gibTag() {
        return Integer.toString(tag);
    }

    String gibStunde() {
        return Integer.toString(stunde);
    }

    String gibStundenName() {
        return this.macheStundenName(stunde);
    }

    String gibNotiz() {
        return notiz;
    }

    public boolean gibSchriftlich() {
        return schriftlich;
    }
}
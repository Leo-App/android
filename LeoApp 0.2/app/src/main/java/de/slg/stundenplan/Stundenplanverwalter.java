package de.slg.stundenplan;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Stundenplanverwalter {

    private final String dateiName;
    private Fach[] meineFaecher;
    private final Context context;

    /**
     * public Stundenplanverwalter(ArrayList<Fach> f) {
     * //Macht einfach nur einen Stundenplanverwalter schon mit Facharray
     * meineFaecher = this.macheArray(f, f.size());
     * DateiName = "allefaecher.txt";
     * }
     * <p>
     * public Stundenplanverwalter(Context context) {
     * //1. Constructor, erstellt FachArray aus der Datei
     * context = context;
     * dateiName = "meinefaecher.txt";
     * meineFaecher = this.auslesen();
     * }
     */

    public Stundenplanverwalter(Context context, String datei) {
        this.context = context;
        dateiName = datei;
        meineFaecher = this.auslesen();
    }

    /**
     * Liest aus der Datei [DateiName] die Informationen aus
     * speichert diese in einem 2Dim Array
     *
     * @return siehe macheFaecher
     */
    private Fach[] auslesen() {
        //L
        if (dateiName == null) {
            return null; //Das hier beachten wenn Methode in Zwischen/Wrapper Activity
        } else {
            String zeile;
            String[] fach;
            ArrayList<Fach> facher = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(dateiName)));
                int i = 0;
                zeile = br.readLine();
                while (zeile != null) {
                    fach = zeile.split(";");
                    facher.add(i, new Fach(fach[1], fach[2], fach[3], fach[4], fach[5], context));
                    boolean b = false;
                    if (fach[6].equals("schriftlich")) {
                        b = true;
                    }
                    facher.get(i).setzeSchriftlich(b);
                    facher.get(i).setzeNotiz(fach[7]);
                    zeile = br.readLine();
                    i++;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.inTextDatei(context, this.macheArray(facher, facher.size()));
            return this.macheArray(facher, facher.size());
        }
    }

    void inTextDatei(Context ac, Fach[] f) {
        meineFaecher = f; //Achtung verändert mein Fächer
        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(ac.openFileOutput("meinefaecher.txt", Context.MODE_PRIVATE)));
            int i = 0;
            while (i < meineFaecher.length && meineFaecher[i] != null) {
                String s = "nicht";
                if (meineFaecher[i].gibSchriftlich()) {
                    s = "schriftlich";
                }
                br.write(meineFaecher[i].gibName() + ";" + meineFaecher[i].gibKurz() + ";" + meineFaecher[i].gibRaum() + ";" + meineFaecher[i].gibLehrer() + ";" + meineFaecher[i].gibTag() + ";" + meineFaecher[i].gibStunde() + ";" + s + ";" + meineFaecher[i].gibNotiz());
                br.newLine();
                //Log.e("Luzzia",meineFaecher[i].gibNotiz());
                i++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Fach[] generiereFreistunden() {
        //Hier weiterarbeiten...
        meineFaecher = faecherSort(); //Achtung meineFächer wird verändert
        ArrayList<Fach> a = new ArrayList<>();
        int aktuelleStunde = 0;
        int aktuellerTag = 1;
        int i = 0;
        while (i < meineFaecher.length && aktuellerTag <= 5) { //Das Array wird durchgegangen
            if (Integer.parseInt(meineFaecher[i].gibTag()) == aktuellerTag) { //Wenn die Stunden am selben Tag sind
                int frei = (Integer.parseInt(meineFaecher[i].gibStunde())) - aktuelleStunde;
                if (frei > 1) { //Wenn Stunden zwischen der vorherigen und der neuen fehlen
                    aktuelleStunde = aktuelleStunde + 1;
                    a.add(new Fach("", "", "", Integer.toString(aktuellerTag), Integer.toString(aktuelleStunde), context)); //Füge eine Freistunde hinzu
                } else { //Wenn die beiden Stunden direkt aufeinander folgen
                    aktuelleStunde = Integer.parseInt(meineFaecher[i].gibStunde());
                    a.add(meineFaecher[i]); //Füge dieses Fach ein
                    /*if(i<(meineFaecher.length-1) && (Integer.parseInt(meineFaecher[i+1].gibTag()))>aktuellerTag) { //Wenn es die letzte Stunde des Tages ist
                        //Log.e("Luzzia", "bin in IF an "+i);
                        a.add(new Fach("","","",Integer.toString(aktuellerTag),Integer.toString(aktuelleStunde),context)); //Mache eine letzte Freistunde
                        a.get(a.size()-1).setzeEnde(true);
                    } else if(i>=meineFaecher.length-1) {
                        a.add(new Fach("","","",Integer.toString(aktuellerTag),Integer.toString(aktuelleStunde+1),context)); //Neue Freistunde ganz am Ende
                        a.get(a.size()-1).setzeEnde(true);
                    }*/ //Das hier hat das Problem mit der hinzugefügten Stunde gemacht... muss ich mal gucken // TODO: 28.05.2017
                    i++; //Nächstes Fach betrachten
                }
            } else {
                aktuellerTag = aktuellerTag + 1; //Neuer Tag
                aktuelleStunde = 0; //Wieder von vorne
            }
        }
        return this.macheArray(a, a.size()); //Soll der das wirklich mit meineFaecher tun?                                                                                    !!!!!!
    }

    Fach[] gibFaecherSort() {
        //gibt das FachArray mit Freistunden
        if (schonMitFreistunden()) {
            return faecherSort();
        }
        return this.generiereFreistunden();
    }

    Fach[] gibFaecherSortTag(int pTag) {
        Fach[] fach;
        if (schonMitFreistunden()) {
            fach = faecherSort();
        } else {
            fach = this.generiereFreistunden();
        }
        ArrayList<Fach> a = new ArrayList<>();
        for (Fach aFach : fach) {
            if (Integer.parseInt(aFach.gibTag()) == pTag) {
                a.add(aFach);
            }
        }
        return this.macheArray(a, a.size());
    }

    public Fach[] gibFaecherKurzTag(int pTag) {
        Fach[] fach = gibFaecherKurz();
        ArrayList<Fach> a = new ArrayList<>();
        for (Fach aFach : fach) {
            if (Integer.parseInt(aFach.gibTag()) == pTag) {
                a.add(aFach);
            }
        }
        return this.macheArray(a, a.size());
    }

    Fach[] gibFacherSortStunde(int pStunde) {
        Fach[] fach;
        if (schonMitFreistunden()) {
            fach = faecherSort();
        } else {
            fach = this.generiereFreistunden();
        }
        ArrayList<Fach> a = new ArrayList<>();
        for (Fach aFach : fach) {
            if (Integer.parseInt(aFach.gibStunde()) == pStunde) {
                a.add(aFach);
            }
        }
        return this.macheArray(a, a.size());
    }

    private Fach[] faecherSort() {
        ArrayList<Fach> f = new ArrayList<>();
        for (Fach aMeineFaecher : meineFaecher) { //Geht das ursprungsarray durch
            int x = 0;
            int tag = Integer.parseInt(aMeineFaecher.gibTag());
            while (x < f.size() && Integer.parseInt(f.get(x).gibTag()) < tag) { //Geht so lange durch wie der Tag größer ist
                x++;
            }
            if (x >= f.size()) { //Wenn bereits am Ende angelangt...
                f.add(aMeineFaecher);
            } else {
                while (x < f.size() && Integer.parseInt(f.get(x).gibTag()) == tag && Integer.parseInt(f.get(x).gibStunde()) < Integer.parseInt(aMeineFaecher.gibStunde())) { //geht durch solange Stunde größer
                    x++;
                }
                if (x >= f.size()) { //Wenn am Ende angelangt...
                    f.add(aMeineFaecher);
                } else {
                    f.add(x, aMeineFaecher); //Fügt in der Mitte ein
                }
            }
        }
        return this.macheArray(f, f.size());
    } //funktioniert

    public Fach[] gibFaecherKurz() {
        ArrayList<Fach> a = new ArrayList<>();
        //Log.e("Luzzzia", "Im Array sind" + meineFaecher.length + " Fächer");
        for (int i = 0; i < meineFaecher.length; i++) {
            if (ersterFundFach(meineFaecher[i].gibKurz()) >= i) {
                a.add(meineFaecher[i]);
            }
        }
        //Log.e("Luzzzia", "Meine FächerKurz gibt: " + a.size() + " Fächer zurück");
        return this.macheArray(a, a.size());
    }

    private int ersterFundFach(String pKurzel) {
        for (int m = 0; m < meineFaecher.length; m++) {
            if (meineFaecher[m].gibKurz().equals(pKurzel)) {
                return m;
            }
        }
        return -1;
    }

    private Fach[] macheArray(ArrayList<Fach> liste, int pLang) { //Brauche ich den int Parameter überhaupt????               // TODO: 27.05.2017
        Fach[] f = new Fach[pLang];
        for (int i = 0; i < liste.size(); i++) {
            f[i] = liste.get(i);
        }
        return f;
    } //Vielleicht sollte ich diese Methode weniger oft benutzen...

    ArrayList<Fach> sucheFacherKurzel(String pKurzel) {
        ArrayList<Fach> f = new ArrayList<>();
        for (Fach aMeineFaecher : meineFaecher) {
            if (aMeineFaecher.gibKurz().equals(pKurzel)) {
                f.add(aMeineFaecher);
            }
        }
        return f;
    }

    private boolean schonMitFreistunden() {
        for (Fach aMeineFaecher : meineFaecher) {
            if (aMeineFaecher.gibKurz().equals("")) {
                return true;
            }
        }
        return false;
    }

}

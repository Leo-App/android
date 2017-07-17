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

    public Stundenplanverwalter(Context context, String datei) {
        this.context = context;
        dateiName = datei;
        auslesen();
    }

    private void auslesen() {
        if (dateiName != null) {
            try {
                ArrayList<Fach> fachArrayList = new ArrayList<>();
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        context.openFileInput(dateiName)));
                int i = 0;
                for (String zeile = reader.readLine(); zeile != null; zeile = reader.readLine(), i++) {
                    String[] fach = zeile.split(";");

                    Fach f = new Fach(fach[1], fach[2], fach[3], fach[4], fach[5], context);
                    f.setzeSchriftlich(fach[6].equals("s"));
                    if (!fach[7].equals(" "))
                        f.setzeNotiz(fach[7]);

                    fachArrayList.add(i, f);
                }
                inTextDatei(zuArray(fachArrayList));
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void inTextDatei(Fach[] f) {
        meineFaecher = f; //Achtung verändert mein Fächer
        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("meinefaecher.txt", Context.MODE_PRIVATE)));
            int i = 0;
            while (i < meineFaecher.length && meineFaecher[i] != null) {
                char s = 'm';
                if (meineFaecher[i].gibSchriftlich()) {
                    s = 's';
                }
                br.write(meineFaecher[i].gibName() + ";" + meineFaecher[i].gibKurz() + ";" + meineFaecher[i].gibRaum() + ";" + meineFaecher[i].gibLehrer() + ";" + meineFaecher[i].gibTag() + ";" + meineFaecher[i].gibStunde() + ";" + s + ";" + meineFaecher[i].gibNotiz() + " ");
                br.newLine();
                i++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Fach[] generiereFreistunden() {
        meineFaecher = faecherSort(); //Achtung meineFächer wird sortiert
        ArrayList<Fach> arrayList = new ArrayList<>();
        for (int i = 0, vorherStunde = 0, vorherTag = 1; i < meineFaecher.length && vorherTag <= 5; vorherStunde++, i++) { //Das Array wird durchgegangen
            if (meineFaecher[i].gibTag() != vorherTag) {
                vorherTag++; //Nächster Tag
                vorherStunde = 0; //erste Stunde
            }
            while (meineFaecher[i].gibStunde() - vorherStunde > 1) { //Solange Stunden zwischen der vorherigen und der neuen fehlen
                vorherStunde++;
                arrayList.add(new Fach("", "", "", Integer.toString(vorherTag), Integer.toString(vorherStunde), context)); //Füge eine Freistunde hinzu
            } //Wenn die beiden Stunden direkt aufeinander folgen
            arrayList.add(meineFaecher[i]); //Füge dieses Fach ein
        }
        return zuArray(arrayList);
    }

    Fach[] gibFaecherSort() {
        if (schonMitFreistunden()) {
            return faecherSort();
        }
        return generiereFreistunden();
    }

    public Fach[] gibFaecherKurzTag(int pTag) {
        Fach[] facher = gibFaecherKuerzel();
        ArrayList<Fach> arrayList = new ArrayList<>();
        for (Fach fach : facher) {
            if (fach.gibTag() == pTag) {
                arrayList.add(fach);
            }
        }
        return zuArray(arrayList);
    }

    Fach[] gibFacherSortStunde(int pStunde) {
        Fach[] facher = gibFaecherSort();
        ArrayList<Fach> arrayList = new ArrayList<>();
        for (Fach fach : facher) {
            if (fach.gibStunde() == pStunde) {
                arrayList.add(fach);
            }
        }
        return zuArray(arrayList);
    }

    private Fach[] faecherSort() {
        ArrayList<Fach> arrayList = new ArrayList<>();
        for (Fach fach : meineFaecher) { //Geht das ursprungsarray durch
            int x = 0;
            int tag = fach.gibTag();
            while (x < arrayList.size() && arrayList.get(x).gibTag() < tag) { //Geht so lange durch wie der Tag größer ist
                x++;
            }
            if (x >= arrayList.size()) { //Wenn bereits am Ende angelangt...
                arrayList.add(fach);
            } else {
                while (x < arrayList.size() && arrayList.get(x).gibTag() == tag && arrayList.get(x).gibStunde() < fach.gibStunde()) { //geht durch solange Stunde größer
                    x++;
                }
                if (x >= arrayList.size()) { //Wenn am Ende angelangt...
                    arrayList.add(fach);
                } else {
                    arrayList.add(x, fach); //Fügt in der Mitte ein
                }
            }
        }
        return zuArray(arrayList);
    }

    public Fach[] gibFaecherKuerzel() {
        ArrayList<Fach> a = new ArrayList<>();

        for (int i = 0; i < meineFaecher.length; i++) {
            if (ersterFundFach(meineFaecher[i].gibKurz()) >= i) {
                a.add(meineFaecher[i]);
            }
        }

        return zuArray(a);
    }

    private int ersterFundFach(String pKurzel) {
        for (int m = 0; m < meineFaecher.length; m++) {
            if (meineFaecher[m].gibKurz().equals(pKurzel)) {
                return m;
            }
        }
        return -1;
    }

    private Fach[] zuArray(ArrayList<Fach> liste) {
        Fach[] faecher = new Fach[liste.size()];
        for (int i = 0; i < faecher.length; i++) {
            faecher[i] = liste.get(i);
        }
        return faecher;
    }

    ArrayList<Fach> gibFaecherMitKuerzel(String kuerzel) {
        ArrayList<Fach> arrayList = new ArrayList<>();
        for (Fach fach : meineFaecher) {
            if (fach.gibKurz().equals(kuerzel)) {
                arrayList.add(fach);
            }
        }
        return arrayList;
    }

    private boolean schonMitFreistunden() {
        for (Fach fach : meineFaecher) {
            if (fach.gibKurz().equals("")) {
                return true;
            }
        }
        return false;
    }
}
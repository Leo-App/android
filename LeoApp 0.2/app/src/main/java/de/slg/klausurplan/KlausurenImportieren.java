package de.slg.klausurplan;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.net.ssl.HttpsURLConnection;

import de.slg.leoapp.List;
import de.slg.leoapp.Utils;

class KlausurenImportieren extends AsyncTask<Void, Void, List<Klausur>> {

    private final Context        context;
    private final boolean        filtern;
    private final String[]       schriflich;
    private       BufferedReader reader;
    private       int            year, halbjahr;
    private List<Klausur> listeMitHeruntergeladenenKlausuren;

    KlausurenImportieren(Context context) {
        this.context = context;
        this.schriflich = Utils.getController().getStundenplanDatabase().gibSchriftlicheFaecherStrings();
        this.filtern = Utils.getController().getPreferences().getBoolean("pref_key_test_timetable_sync", false);
    }

    @Override
    protected List<Klausur> doInBackground(Void... params) {
        try {
            listeMitHeruntergeladenenKlausuren = new List<>();

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "klausurplan/aktuell.xml")
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);

            FileOutputStream fileOutput  = context.openFileOutput("klausurplan.xml", Context.MODE_PRIVATE);
            InputStream      inputStream = connection.getInputStream();
            byte[]           buffer      = new byte[1024];
            int              bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();

            reader = new BufferedReader(new InputStreamReader(context.openFileInput("klausurplan.xml")));
            year = getYear();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.replace(" ", "").equals("<informaltableframe=\"all\">"))
                    tabelle(reader.readLine());
            }
            reader.close();

            return listeMitHeruntergeladenenKlausuren;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void tabelle(String s) {
        for (int offset = 0; s.substring(offset).contains("<row>"); offset = s.indexOf("</row>", offset) + 6) {
            String substring = s.substring(s.indexOf("<row>", offset) + 5, s.indexOf("</row>", offset)); // Trennen bei <row>, <\row>  Bsp: <entry><para>MO </para></entry><entry><para>20.03.</para></entry><entry><para/></entry><entry><para>GK I: E GK 2  GOM (17), L G1 SUL(1), M G1 REI (23), PH G1 MUL (8), SW G1 SLI (5)    1.-2 </para></entry><entry><para/></entry>
            substring = substring.substring(substring.indexOf("</entry>") + 8); //Trennen nach erstem <\entry> (Wochentag wird nicht benötigt) Bsp: <entry><para>20.03.</para></entry><entry><para/></entry><entry><para>GK I: E GK 2  GOM (17), L G1 SUL(1), M G1 REI (23), PH G1 MUL (8), SW G1 SLI (5)    1.-2 </para></entry><entry><para/></entry>
            substring = substring.replace("</para>", "").replace("<para/>", " ").replace("<entry><para>", "").replace("</entry>", ";").replace("<para>", ", ").replace("<entry>", ""); //entfernen aller para, Anfangs entry, Ersetzen der </entry> Tags durch ; Bsp: 20.03.; ;GK I: E GK 2  GOM (17), L G1 SUL(1), M G1 REI (23), PH G1 MUL (8), SW G1 SLI (5)    1.-2 ; ;

            if (!substring.contains("<entry namest=\"c3\" nameend=\"c5\">")) { //? beim neuen Klausurplan nirgendwo der Fall
                if (!substring.startsWith("EF")) { //nicht benötigte Zeilen Bsp:EF;Q1;Q2;
                    zeile(substring);
                }
            }
        }
    }

    private void zeile(String s) {
        String datesubstring = s.substring(0, s.indexOf(";")).replaceAll("\\s", ""); // erster Teil vor dem Simikolon enthält das Datum Bsp: 20.03.; ;GK I: E GK 2  GOM (17), L G1 SUL(1), M G1 REI (23), PH G1 MUL (8), SW G1 SLI (5)    1.-2 ; ;
        //Log.e("date", datesubstring);
        if (!datesubstring.endsWith("."))
            datesubstring += '.'; //Log.e("date", datesubstring); //fehlende Punkte am Ende werden ergänzt
        Date     datum = getDate(datesubstring + year); //if(datum!=null) Log.e("date",datum.toString() );  Datum wird geparst
        String   rest  = s.substring(s.indexOf(";") + 1); //Log.e("zeile", rest); //Bsp (2) D G2 SNE (27), D G3 POR (27), E G2 DRE (26), M G3 ENS (27);  ; ;
        String[] split = rest.split(";");
        for (int i = 0; i < split.length; i++) {
            String stufe = "", c = split[i];
            switch (i) {
                case 0: //Teil vor dem ersten Semikolon enthält Klausuren der EF
                    stufe = "EF";
                    break;
                case 1://Teil vor dem zweiten Semikolon enthält Klausuren der Q1
                    stufe = "Q1";
                    break;
                case 2://Teil vor dem dritten Semikolon enthält Klausuren der Q2
                    stufe = "Q2";
                    break;
            }
            //Log.e("zeile", c); //Bsp für i = 0 (2) D G2 SNE (27), D G3 POR (27), E G2 DRE (26), M G3 ENS (27)                5.-6.
            if (c.startsWith("LK") || c.startsWith("GK")) { //Bsp:   ;GK I: E GK 2  GOM (17), L G1 SUL(1), M G1 REI (23), PH G1 MUL (8), SW G1 SLI (5)    1.-2 ; ;
                c = c.substring(c.indexOf(':') + 1); //GK/LK entfernen
                //Log.e("GK/LK", c);
            } else if (c.startsWith("Abiturvorklausur LK")) { //Abiturvorklausur LK II(Dauer: 4,25 Zeitstunden + 30 Minuten Auswahlzeit in den Sprachen und Gesellschaftswissenschaften) L4: E RUS (23), F KRE (8), M VOG (19), PA HSR (11)L2: GE KKG (2), PH KKG (6), KU KKG (4)L6: BI COU (2), D COU (5), GE COU (4), SW COU (2)        1.-6. Stunde (der Unterricht in der 7. und 8. Stunde entfällt)  Elternsprechtag: 16 Uhr;
                if (c.contains("wissenschaften)")) {
                    c = c.substring(c.indexOf("wissenschaften)") + 15); //Text entfernen:  L4: E RUS (23), F KRE (8), M VOG (19), PA HSR (11)L2: GE KKG (2), PH KKG (6), KU KKG (4)L6: BI COU (2), D COU (5), GE COU (4), SW COU (2)        1.-6. Stunde (der Unterricht in der 7. und 8. Stunde entfällt)  Elternsprechtag: 16 Uhr;
                    c = c.substring(c.indexOf(":") + 1); // nach dem ersten Doppelpunkt: E RUS (23), F KRE (8), M VOG (19), PA HSR (11)L2: GE KKG (2), PH KKG (6), KU KKG (4)L6: BI COU (2), D COU (5), GE COU (4), SW COU (2)        1.-6. Stunde (der Unterricht in der 7. und 8. Stunde entfällt)  Elternsprechtag: 16 Uhr;
                    c = c.substring(0, c.indexOf(":") - 2);//vor dem nächsten Doppelpunkt trennen(KOOP-Klausuren):  E RUS (23), F KRE (8), M VOG (19), PA HSR (11)L2: GE KKG (2), PH KKG (6), KU KKG (4)
                    // Log.e("AV LK", c);
                }
            } else if (c.startsWith("Abiturklausur GK")) { //Abiturvorklausur GK(Dauer: 3 Zeitstunden + 30 Minuten Auswahlzeit in den Sprachen und Gesellschaftswissenschaften) BI G1 WEI (3), BI G2 VOS (3), BI G3  KIN (4), D G1 SLT (3), D G3 RDZ (1), E G1 WHS (6), E G3 LAN (5), EK G1 HEU (7), GE G1 STL (4), GE G2 STL (3), GEF G1 NIE (2), IF G1 ENS (3), KR G2  KIR (1), M G1 KPS (14), M G2 NIR (12), PA G1 SLT (2), PH G2 KPS (2), SW G1 SLI (3), SW G2  HEU (5)                                                            1.-4. Stunde(danach ist regulärer Unterricht);
                c = c.substring(c.indexOf("wissenschaften)") + 15); //BI G1 WEI (3), BI G2 VOS (3), BI G3  KIN (4), D G1 SLT (3), D G3 RDZ (1), E G1 WHS (6), E G3 LAN (5), EK G1 HEU (7), GE G1 STL (4), GE G2 STL (3), GEF G1 NIE (2), IF G1 ENS (3), KR G2  KIR (1), M G1 KPS (14), M G2 NIR (12), PA G1 SLT (2), PH G2 KPS (2), SW G1 SLI (3), SW G2  HEU (5)     1.-4. Stunde(danach ist regulärer Unterricht);
                //Log.e("AV GK", c);
            }
            List<String> klausurenAusZeile = getKlausurStrings(c, stufe); //sucht in der zeile nach Klausuren
            for (klausurenAusZeile.toFirst(); klausurenAusZeile.hasAccess(); klausurenAusZeile.next())
                if (datum != null && istImStundenplan(klausurenAusZeile.getContent().replace('_', ' ')))
                    listeMitHeruntergeladenenKlausuren.append(new Klausur(klausurenAusZeile.getContent().replace('_', ' '), datum, null, null)); //neue Klausuren(in der Zeile enthaltenes Datum, gefundene Klausuren (Kürzel)) werden angehängt
        }
    }

    private List<String> getKlausurStrings(String s, String stufe) { //E RUS (23), F KRE (8), M VOG (19), PA HSR (11) //(5)  GE G3 HUC (11), GEF G1 TAS (7), SW G3 STO (5) 
        s = s.replace(' ', '_').replace('(', '_').replace(')', '_').replace(',', ';').replaceAll("\\s", ""); //_E_RUS__23_;_F_KRE__8_;_M_VOG__19_;_PA_HSR__11_   //_5__GE_G3_HUC__11_;_GEF_G1_TAS__7_;_SW_G3_STO__5________________1.-2
        String[]     klausuren = s.split(";"); //_E_RUS__23_   //_5__GE_G3_HUC__11_
        List<String> list      = new List<>();
        for (String c : klausuren) {
            while (c.length() > 0 && (c.charAt(0) == '_' || (c.charAt(0) > 47 && c.charAt(0) < 58)))
                c = c.substring(1);//enfernt Zahlen und _ am Anfang //E_RUS__23_   //GE_G3_HUC__11_
            if (c.length() > 0) {
                boolean istGK     = c.matches("[A-Z]{1,3}_*[GLK]{1,2}_*[0-9]_*[A-ZÄÖÜ]{3}_*[0-9]{1,2}.*"); //Format FF_G1_LLL__19_
                boolean istLK     = c.matches("[A-Z]{1,3}_*[A-ZÄÖÜ]{3}_*[0-9]{1,2}.*");//Format FF_LLL_12_
                boolean istKOOPLK = c.matches("LK_[0-9]*+_[COUKG]{3}:_[A-Z]{1,3}.*"); // Format LK_1_COU:_FFF
                if (c.length() >= 12 && istGK) {
                    String klausur = c.substring(0, 12); // etwas zu viel, um mehr Leerzeichen zuzulassen (es gibt jedoch keine kürzeren GK Klausuren, da kürzestes Format: F_G1_LLL__1_)
                    while (klausur.length() > 7 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                        klausur = klausur.substring(0, klausur.length() - 1);//Zahlen und Leerzeichen am Ende entfernen
                    klausur += " " + stufe; // Stufe anhängen
                    list.append(klausur); //GE_G3_HUC EF
                }
                //Log.e("Tag", ""+istLK);
                if (c.length() >= 9 && istLK) {// etwas zu viel, um mehr Leerzeichen zuzulassen (es gibt jedoch keine kürzeren LK Klausuren, da kürzestes Format: F_LLL__1_)
                    String klausur = c.substring(0, 9);
                    while (klausur.length() > 5 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                        klausur = klausur.substring(0, klausur.length() - 1);//Zahlen und Leerzeichen am Ende entfernen
                    klausur += " " + stufe;
                    String teil1 = klausur.substring(0, klausur.indexOf("_"));
                    String teil2 = klausur.substring(klausur.indexOf("_"), klausur.length());
                    klausur = teil1 + " L" + teil2;// L dazwischen einfügen
                    //Log.e("LK", klausur);
                    list.append(klausur);
                }
                if (istKOOPLK) {
                    c = c.substring(5);
                    String schule = c.substring(0, 3);
                    c = c.substring(c.indexOf(':') + 2);
                    if (c.contains("_"))
                        c = c.substring(0, c.indexOf("_"));
                    c = c + ' ' + schule;
                    //                    Log.e("KOOPLK", c);
                    list.append(c);
                }
            }
        }
        return list;// Liste mit den fertigen Klausurnamen zurückgeben
    }

    private int getYear() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.replace(" ", "").startsWith("<para>")) { //<para><inlinegraphic fileref="embedded:SLG Logo sw" width="3.49cm" depth="1.95cm"/>  Klausurplan 2016/17, EF-Q2  2.Halbjahr        Aachen, den 05.01.2017</para>
                int    offset    = line.indexOf("Klausurplan") + 11;
                int    end       = line.indexOf(".Halbjahr");
                String substring = line.substring(offset, end); // 2016/17, EF-Q2  2
                if (substring.charAt(substring.length() - 1) == '1')
                    halbjahr = 1;
                else
                    halbjahr = 2;
                for (int i = 0; i < substring.length(); i++) {
                    if (substring.charAt(i) != ' ') {
                        offset = i;
                        break;
                    }
                }
                String subyear1 = substring.substring(offset, offset + 4), subyear2 = substring.substring(offset, offset + 2) + substring.substring(offset + 5, offset + 7); //1: 2016, 2: 2017
                if (halbjahr == 1)
                    return Integer.parseInt(subyear1);
                else
                    return Integer.parseInt(subyear2);
            }
        }
        return 2017;
    }

    private Date getDate(String s) { //Log.e("date", s);
        String[] parts = s.replace('.', '_').split("_"); //for(int i = 0; i< parts.size; i++) Log.e("date", parts[i]);
        if (parts.length == 3) {
            int day   = Integer.parseInt(parts[0]); //Log.e("date", ""+day);
            int month = Integer.parseInt(parts[1]);// Log.e("date", ""+month);
            int year  = Integer.parseInt(parts[2]);// Log.e("date", ""+year);
            if (halbjahr == 1 && month < 4)
                year++;
            Calendar c = new GregorianCalendar();
            c.set(year, month - 1, day, 0, 0, 0);//Log.e("date", c.getTime().toString());
            return c.getTime();
        }
        return null;
    }

    private boolean istImStundenplan(String klausur) {
        if (filtern) {
            for (String s : schriflich) {
                if (klausur.equals(s))
                    return true;
            }
            return false;
        }
        return true;
    }
}
package de.slgdev.klausurplan.task;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slgdev.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;

public class Importer extends VoidCallbackTask<Void> {
    private final SQLiteConnectorKlausurplan database;

    private BufferedReader reader;
    private InputStream    inputStream;

    private final String[] schriflich;

    private int year, halbjahr;

    public Importer(Context context) {
        this.database = new SQLiteConnectorKlausurplan(context);

        SQLiteConnectorStundenplan databaseStundenplan = new SQLiteConnectorStundenplan(context);
        this.schriflich = databaseStundenplan.gibSchriftlicheFaecherStrings();
        databaseStundenplan.close();

        try {
            inputStream = context
                    .openFileInput(
                            "klausurplan.xml"
                    );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            inputStream = null;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            database.deleteAllDownloaded();

            reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream
                    )
            );

            year = getYear();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.replace(" ", "").equals("<informaltableframe=\"all\">"))
                    tabelle(reader.readLine());
            }

            reader.close();
        } catch (IOException e) {
            Utils.logError(e);
        }
        return null;
    }

    private void tabelle(String s) {
        for (int offset = 0; s.substring(offset).contains("<row>"); offset = s.indexOf("</row>", offset) + 6) {
            String substring = s.substring(
                    s.indexOf("<row>", offset) + 5,
                    s.indexOf("</row>", offset)
            );

            substring = substring.substring(
                    substring.indexOf("</entry>") + 8
            );

            substring = substring
                    .replace(
                            "</para>",
                            ""
                    )
                    .replace(
                            "<para/>",
                            " "
                    )
                    .replace(
                            "<entry><para>",
                            ""
                    )
                    .replace(
                            "</entry>",
                            ";"
                    ).replace(
                            "<para>",
                            ", "
                    )
                    .replace(
                            "<entry>",
                            ""
                    );

            if (!substring.contains("<entry namest=\"c3\" nameend=\"c5\">") && !substring.startsWith("EF")) {
                zeile(substring);
            }
        }
    }

    private void zeile(String s) {
        String datesubstring = s
                .substring(
                        0,
                        s.indexOf(";")
                )
                .replaceAll(
                        "\\s",
                        ""
                );

        if (!datesubstring.endsWith(".")) {
            datesubstring += '.';
        }

        Date datum = getDate(datesubstring + year);

        String rest = s.substring(
                s.indexOf(";") + 1
        );

        String[] split = rest.split(";");
        for (int i = 0; i < split.length; i++) {
            String stufe = "", c = split[i];
            switch (i) {
                case 0:
                    stufe = "EF";
                    break;
                case 1:
                    stufe = "Q1";
                    break;
                case 2:
                    stufe = "Q2";
                    break;
            }

            if (c.startsWith("LK") || c.startsWith("GK")) {
                c = c.substring(c.indexOf(':') + 1);
            } else if (c.startsWith("Abiturvorklausur LK")) {
                if (c.contains("wissenschaften)")) {
                    c = c.substring(
                            c.indexOf("wissenschaften)") + 15
                    );
                    c = c.substring(
                            c.indexOf(":") + 1
                    );
                    c = c.substring(
                            0,
                            c.indexOf(":") - 2
                    );
                }
            } else if (c.startsWith("Abiturklausur GK")) {
                c = c.substring(
                        c.indexOf("wissenschaften)") + 15
                );
            }

            for (String k : getKlausurStrings(c, stufe)) {
                k = k.replace('_', ' ');
                database.insert(k, stufe, datum, "", istImStundenplan(k), true);
            }
        }
    }

    private List<String> getKlausurStrings(String s, String stufe) {
        s = s
                .replace(
                        ' ',
                        '_'
                )
                .replace(
                        '(',
                        '_'
                )
                .replace(
                        ')',
                        '_'
                )
                .replace(
                        ',',
                        ';'
                )
                .replaceAll(
                        "\\s",
                        ""
                );

        List<String> list = new List<>();
        for (String c : s.split(";")) {

            while (c.length() > 0 && (c.charAt(0) == '_' || (c.charAt(0) > 47 && c.charAt(0) < 58)))
                c = c.substring(1);

            if (c.length() > 0) {
                boolean istGK     = c.matches("[A-Z]{1,3}_*[GLK]{1,2}_*[0-9]_*[A-ZÄÖÜ]{3}_*(\\([0-9]{1,2}\\))?.*");
                boolean istLK     = c.matches("[A-Z]{1,3}_*[A-ZÄÖÜ]{3}_*[0-9]{1,2}.*");
                boolean istKOOPLK = c.matches("LK_[0-9]*+_[COUKG]{3}:_[A-Z]{1,3}.*");

                if (c.length() >= 12 && istGK) {
                    String klausur = c.substring(0, 12);
                    while (klausur.length() > 7 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                        klausur = klausur.substring(0, klausur.length() - 1);
                    klausur += " " + stufe;
                    list.append(klausur);
                }

                if (c.length() >= 9 && istLK) {
                    String klausur = c.substring(0, 9);
                    while (klausur.length() > 5 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                        klausur = klausur.substring(0, klausur.length() - 1);
                    klausur += " " + stufe;
                    String teil1 = klausur.substring(0, klausur.indexOf("_"));
                    String teil2 = klausur.substring(klausur.indexOf("_"), klausur.length());
                    klausur = teil1 + " L" + teil2;
                    list.append(klausur);
                }

                if (istKOOPLK) {
                    String koop = s.substring(s.indexOf(c));

                    String schule = c.substring(5, 8);

                    koop = koop.substring(koop.indexOf(':') + 2);
                    if (koop.contains("LK")) {
                        koop = koop.substring(0, koop.indexOf("LK"));
                    }
                    koop = koop
                            .replace(
                                    "_",
                                    ""
                            )
                            .replace(
                                    ".",
                                    ""
                            )
                            .replace(
                                    "-",
                                    ""
                            )
                            .replaceAll(
                                    "\\d",
                                    ""
                            );
                    Utils.logDebug(koop);

                    for (String k : koop.split(";")) {
                        list.append(k + ' ' + schule);
                    }
                }
            }
        }

        return list;
    }

    private int getYear() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.replace(" ", "").startsWith("<para>")) {
                String substring = line.substring(
                        line.indexOf("Klausurplan") + 11,
                        line.indexOf(".Halbjahr")
                );

                if (substring.charAt(substring.length() - 1) == '1') {
                    halbjahr = 1;
                } else {
                    halbjahr = 2;
                }

                for (int i = 0; i < substring.length(); i++) {
                    if (substring.charAt(i) != ' ') {
                        return Integer.parseInt(
                                substring.substring(
                                        i,
                                        i + 4
                                )
                        ) + halbjahr - 1;
                    }
                }
            }
        }

        return new GregorianCalendar().get(Calendar.YEAR);
    }

    private Date getDate(String s) {
        try {
            Date d = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(s);
            if (halbjahr == 1) {
                Calendar c = new GregorianCalendar();
                c.setTime(d);
                if (c.get(Calendar.MONTH) < 4) {
                    c.add(Calendar.YEAR, 1);
                    d = c.getTime();
                }
            }
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] parts = s.replace('.', '_').split("_");
        if (parts.length == 3) {
            int day   = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year  = Integer.parseInt(parts[2]);
            if (halbjahr == 1 && month < 4)
                year++;
            Calendar c = new GregorianCalendar();
            c.set(year, month - 1, day, 0, 0, 0);
            return c.getTime();
        }
        return null;
    }

    private boolean istImStundenplan(String klausur) {
        for (String s : schriflich) {
            if (klausur.startsWith(s))
                return true;
        }
        return false;
    }
}

package de.slg.leoapp.exams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slg.leoapp.core.datastructure.List;

public class KlausurplanImporter {
    private static final String           fileName   = "klausurplan.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    private BufferedReader reader;

    private int year, halbjahr;

    private void importData() throws Exception {
        reader = new BufferedReader(
                new FileReader(
                        fileName
                )
        );

        year = getYear();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.replace(" ", "").equals("<informaltableframe=\"all\">")) {
                tabelle(reader.readLine());
            }
        }

        reader.close();
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
            } else if (c.toLowerCase().startsWith("abiturvorklausur lk")) {
                if (c.contains("wissenschaften)")) {
                    c = c.substring(
                            c.indexOf("wissenschaften)") + 15
                    );
                    c = c.substring(
                            c.indexOf(":") + 2
                    );
                }
            } else if (c.toLowerCase().startsWith("abiturvorklausur")) {
                c = c.substring(
                        c.indexOf("wissenschaften)") + 15
                );
            }

            for (String k : getKlausurStrings(c, stufe)) {
                k = k.replace('_', ' ');
                System.out.println("INSERT INTO klausurplan(id, titel, datum, stufe) " +
                        "VALUES (null," +
                        "'" + k + "'," +
                        "'" + dateFormat.format(datum) + "'," +
                        "'" + stufe + "');");
//                connector.runQuery(
//                        "INSERT INTO klausurplan(id, titel, datum, stufe) " +
//                                "VALUES (null," +
//                                "'" + k + "'," +
//                                "'" + dateFormat.format(datum) + "'," +
//                                "'" + stufe + "')"
//                );
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

            while (c.length() > 0 && !("" + c.charAt(0)).matches("[A-Z]"))
                c = c.substring(1);

            if (c.length() > 0) {
                boolean istGK     = c.matches("[A-Z]{1,3}_*[GLK]{1,2}_*[0-9]_*[A-ZÄÖÜ]{3}_*(\\([0-9]{1,2}\\))?.*");
                boolean istLK     = c.matches("[A-Z]{1,3}_*[A-ZÄÖÜ]{3}_*[0-9]{1,2}.*");
                boolean istKOOPLK = c.matches("LK_[0-9]*+_[COUKG]{3}:_[A-Z]{1,3}.*");

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
                            ).replaceAll(
                                    "[^A-Z;]",
                                    ""
                            );
                    for (String k : koop.split(";")) {
                        list.append(k + ' ' + schule + ' ' + stufe);
                    }
                } else if (c.length() >= 9 && istLK) {
                    String klausur = c.substring(0, 9);
                    while (klausur.length() > 5 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58))) {
                        klausur = klausur.substring(0, klausur.length() - 1);
                    }
                    klausur += " " + stufe;
                    String teil1 = klausur.substring(0, klausur.indexOf("_"));
                    String teil2 = klausur.substring(klausur.indexOf("_"), klausur.length());
                    klausur = teil1 + " L" + teil2;
                    list.append(klausur);
                } else if (c.length() >= 12 && istGK) {
                    String klausur = c.substring(0, 12);
                    while (klausur.length() > 7 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                        klausur = klausur.substring(0, klausur.length() - 1);
                    //klausur += " " + stufe;
                    list.append(klausur);
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
}
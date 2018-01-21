package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.stundenplan.utility.Fach;

public class SQLiteConnectorStundenplan extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stundenplan";

    private static final String TABLE_FAECHER  = "faecher";
    private static final String TABLE_STUNDEN  = "stunden";
    private static final String TABLE_GEWAEHLT = "gewaehlt";

    private static final String FACH_ID     = "fid";
    private static final String FACH_NAME   = "fname";
    private static final String FACH_KURZEL = "fkurz";
    private static final String FACH_LEHRER = "flehrer";
    private static final String FACH_KLASSE = "fklasse";
    private static final String FACH_ART    = "fart";

    private static final String STUNDEN_TAG    = "stag";
    private static final String STUNDEN_STUNDE = "sstunde";
    private static final String STUNDE_RAUM    = "sraum";
    private static final String STUNDE_NOTIZ   = "snotiz";

    private static final String GEWAEHLT_SCHRIFTLICH = "gschriftlich";

    private final SQLiteDatabase database;
    private final Context        context;

    public SQLiteConnectorStundenplan(Context context) {
        super(context, DATABASE_NAME, null, 4);
        database = getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAECHER + " (" +
                FACH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FACH_ART + " TEXT NOT NULL, " +
                FACH_KURZEL + " TEXT NOT NULL, " +
                FACH_NAME + " TEXT NOT NULL, " +
                FACH_LEHRER + " TEXT NOT NULL, " +
                FACH_KLASSE + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STUNDEN + " (" +
                FACH_ID + " INTEGER NOT NULL, " +
                STUNDEN_TAG + " INTEGER NOT NULL, " +
                STUNDEN_STUNDE + " INTEGER NOT NULL, " +
                STUNDE_RAUM + " TEXT NOT NULL, " +
                STUNDE_NOTIZ + " TEXT, " +
                "PRIMARY KEY" +
                " (" + FACH_ID +
                ", " + STUNDEN_TAG +
                ", " + STUNDEN_STUNDE + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GEWAEHLT + " (" +
                FACH_ID + " INTEGER PRIMARY KEY, " +
                GEWAEHLT_SCHRIFTLICH + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAECHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEWAEHLT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUNDEN);
        onCreate(db);
    }

    public long insertFach(String kurz, String lehrer, String klasse) {
        while (kurz.contains("  "))
            kurz = kurz.replace("  ", " ");

        String selection = FACH_KURZEL + " = '" + kurz + "' AND " + FACH_LEHRER + " = '" + lehrer + "' AND " + FACH_KLASSE + " = '" + klasse + "'";
        Cursor cursor    = database.query(TABLE_FAECHER, new String[]{FACH_ID}, selection, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        if (kurz.length() > 2 && (kurz.charAt(2) == 'L' || kurz.charAt(2) == 'Z'))
            values.put(FACH_ART, kurz.charAt(2) + "K");
        else
            values.put(FACH_ART, "GK");
        values.put(FACH_KURZEL, kurz);
        values.put(FACH_NAME, getFachname(kurz)); //Hier brauchen wir jetzt doch das ganze Kürzel!
        values.put(FACH_LEHRER, lehrer);
        values.put(FACH_KLASSE, klasse);
        return database.insert(TABLE_FAECHER, null, values);
    }

    public void insertStunde(long fid, int tag, int stunde, String raum) {
        String selection = FACH_ID + " = " + fid + " AND " + STUNDEN_TAG + " = " + tag + " AND " + STUNDEN_STUNDE + " = " + stunde;
        Cursor cursor    = database.query(TABLE_STUNDEN, new String[]{FACH_ID}, selection, null, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(FACH_ID, fid);
            values.put(STUNDEN_TAG, tag);
            values.put(STUNDEN_STUNDE, stunde);
            values.put(STUNDE_RAUM, raum);
            database.insert(TABLE_STUNDEN, null, values);
        }
        cursor.close();
    }

    public void waehleFach(long fid) {
        ContentValues values = new ContentValues();
        values.put(FACH_ID, fid);
        values.put(GEWAEHLT_SCHRIFTLICH, false);
        database.insert(TABLE_GEWAEHLT, null, values);
    }

    public void setzeNotiz(String notiz, int fid, int tag, int stunde) {
        ContentValues values = new ContentValues();
        values.put(STUNDE_NOTIZ, notiz);
        database.update(TABLE_STUNDEN, values, FACH_ID + " = " + fid + " AND " + STUNDEN_TAG + " = " + tag + " AND " + STUNDEN_STUNDE + " = " + stunde, null);
    }

    public void setzeSchriftlich(boolean schriftlich, long fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAEHLT_SCHRIFTLICH, schriftlich);
        database.update(TABLE_GEWAEHLT, values, FACH_ID + " = " + fid, null);
        SQLiteConnectorKlausurplan klausurplan = new SQLiteConnectorKlausurplan(context);
        klausurplan.updateStundenplan(getFachKurzel(fid), schriftlich);
        klausurplan.close();
    }

    public void loescheWahlen() {
        database.delete(TABLE_GEWAEHLT, null, null);
    }

    public Fach[] getFaecher() {
        String   table     = TABLE_FAECHER + ", " + TABLE_STUNDEN;
        String[] columns   = {TABLE_FAECHER + "." + FACH_ID, FACH_KURZEL, FACH_NAME, FACH_ART, FACH_LEHRER, FACH_KLASSE, STUNDE_RAUM, STUNDEN_TAG, STUNDEN_STUNDE};
        String   selection = TABLE_FAECHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + FACH_ART + " != 'FREI'";
        Cursor   cursor    = database.query(table, columns, selection, null, FACH_KURZEL, null, FACH_NAME);
        Fach[]   faecher   = new Fach[cursor.getCount()];
        int      i         = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            faecher[i] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
        }
        cursor.close();
        return faecher;
    }

    public Fach[] gewaehlteFaecherAnTag(int tag) {
        String table = TABLE_FAECHER + ", " + TABLE_GEWAEHLT + ", " + TABLE_STUNDEN;
        String[] columns = {TABLE_FAECHER + "." + FACH_ID,
                FACH_KURZEL,
                FACH_NAME,
                FACH_ART,
                FACH_LEHRER,
                FACH_KLASSE,
                STUNDE_RAUM,
                STUNDEN_STUNDE,
                GEWAEHLT_SCHRIFTLICH,
                STUNDE_NOTIZ};
        String selection = TABLE_FAECHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + TABLE_FAECHER + "." + FACH_ID + " = " + TABLE_GEWAEHLT + "." + FACH_ID + " AND " + TABLE_STUNDEN + "." + STUNDEN_TAG + " = " + tag;
        Cursor cursor    = database.query(table, columns, selection, null, null, null, STUNDEN_STUNDE);
        Fach[] faecher   = new Fach[0];
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            faecher = new Fach[cursor.getInt(7)];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                faecher[cursor.getInt(7) - 1] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), cursor.getString(6), tag, cursor.getInt(7));
                faecher[cursor.getInt(7) - 1].setzeNotiz(cursor.getString(9));
                faecher[cursor.getInt(7) - 1].setzeSchriftlich(cursor.getInt(8) == 1);
            }
            for (int i = 0; i < faecher.length; i++) {
                if (faecher[i] == null)
                    faecher[i] = new Fach(0, "", "", "", "", "", tag, i + 1);
            }
        }
        cursor.close();
        return faecher;
    }

    public Fach getFach(int tag, int stunde) {
        String table = TABLE_FAECHER + ", " + TABLE_GEWAEHLT + ", " + TABLE_STUNDEN;
        String[] columns = {TABLE_FAECHER + "." + FACH_ID,
                FACH_KURZEL,
                FACH_NAME,
                FACH_ART,
                FACH_LEHRER,
                FACH_KLASSE,
                STUNDE_RAUM,
                STUNDEN_TAG,
                STUNDEN_STUNDE,
                GEWAEHLT_SCHRIFTLICH,
                STUNDE_NOTIZ};
        String selection = TABLE_GEWAEHLT + "." + FACH_ID + " = " + TABLE_FAECHER + "." + FACH_ID
                + " AND " + TABLE_STUNDEN + "." + FACH_ID + " = " + TABLE_FAECHER + "." + FACH_ID
                + " AND " + TABLE_STUNDEN + "." + STUNDEN_TAG + " = " + tag
                + " AND " + TABLE_STUNDEN + "." + STUNDEN_STUNDE + " = " + stunde;
        Cursor cursor = database.query(table, columns, selection, null, null, null, null);
        Fach   f      = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            f = new Fach(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    tag, stunde);
            f.setzeNotiz(cursor.getString(10));
            f.setzeSchriftlich(cursor.getInt(9) == 1);
        }
        cursor.close();
        return f;
    }

    private String getFachKurzel(long fid) {
        Cursor cursor = database.query(TABLE_FAECHER, new String[]{FACH_KURZEL, FACH_LEHRER}, FACH_ID + " = " + fid, null, null, null, null);
        String s      = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String kurzel = cursor.getString(0);
            String lehrer = cursor.getString(1);
            String teil1  = kurzel.substring(0, 2);
            String teil2  = kurzel.substring(2, 4);
            if (teil1.charAt(1) != ' ')
                teil1 += ' ';
            if (teil2.charAt(0) == 'L')
                teil2 = "L";
            s = teil1 + teil2 + " " + lehrer + " " + Utils.getUserStufe();
        }
        cursor.close();
        return s;
    }

    private String getFachname(String kuerzel) {
        String stufe = Utils.getUserStufe();
        if (kuerzel.startsWith("L-") || kuerzel.matches("L[0-9]")) {
            return context.getString(R.string.latein);
        }
        if (!stufe.matches("[0-9]{1,2}")) {
            if (kuerzel.length() > 4) {
                if (kuerzel.equals("GEF")) {
                    return context.getString(R.string.bili);
                }
                if (kuerzel.startsWith("IB")) {
                    if (kuerzel.endsWith("TOK")) {
                        return context.getString(R.string.Tok);
                    }
                    if (kuerzel.endsWith("WLT")) {
                        return context.getString(R.string.Weltlit);
                    }
                }
                String hilfskuerzel = kuerzel.substring(0, kuerzel.length() - 1);
                if (hilfskuerzel.endsWith("ZG")) {
                    return fachnameOberstufe(hilfskuerzel.substring(0, 2)) + ' ' + context.getString(R.string.ZK);
                }
                if (hilfskuerzel.endsWith("P")) {
                    return fachnameOberstufe(hilfskuerzel.substring(0, 2)) + ' ' + context.getString(R.string.Projektk);
                }
                if (hilfskuerzel.endsWith("VTF")) {
                    return fachnameOberstufe(hilfskuerzel.charAt(1) + " ") + ' ' + context.getString(R.string.Vertiefung);
                }
            }
            return this.fachnameOberstufe(kuerzel.substring(0, 2));
        } else if (!stufe.equals("")) {
            if (kuerzel.length() > 2) {
                if (kuerzel.contains("-")) {
                    int i = kuerzel.indexOf('-');
                    return fachnameUnterstufe(kuerzel.substring(0, i));
                }
                if (kuerzel.length() == 3 && kuerzel.endsWith("F")) {
                    return fachnameUnterstufe(kuerzel.substring(0, 2));
                }
                if (kuerzel.toUpperCase().endsWith("DF")) {
                    if (kuerzel.startsWith("PK")) {
                        return context.getString(R.string.PoWi);
                    }
                    return fachnameUnterstufe(kuerzel.substring(0, 2));
                }
                if (kuerzel.endsWith("FÖ")) {
                    int i = kuerzel.indexOf("FÖ");
                    return fachnameUnterstufe(kuerzel.substring(0, i));
                }
                if (kuerzel.startsWith("LÜZ")) {
                    return context.getString(R.string.lüz);
                }
                if (kuerzel.startsWith("AG")) {
                    String teil = kuerzel.substring(2);
                    if (kuerzel.charAt(2) == ' ') {
                        teil = kuerzel.substring(3);
                    }
                    return context.getString(R.string.ag) + fachnameAG(teil);
                }
                if (kuerzel.equals("SOZ")) {
                    return context.getString(R.string.Soz);
                }
                if (kuerzel.equals("SPSW")) {
                    return context.getString(R.string.schwimmen);
                }
            }
            return this.fachnameUnterstufe(kuerzel);
        }
        return kuerzel;
    }

    private String fachnameUnterstufe(String teil) {
        switch (teil.toUpperCase()) {
            case "F":
                return context.getString(R.string.franze);
            case "MA":
                return context.getString(R.string.mathe);
            case "E":
                return context.getString(R.string.englisch);
            case "SP":
                return context.getString(R.string.sport);
            case "DE":
                return context.getString(R.string.deutsch);
            case "BI":
                return context.getString(R.string.bio);
            case "KU":
                return context.getString(R.string.kunst);
            case "GE":
                return context.getString(R.string.geschichte);
            case "PK":
                return context.getString(R.string.politik);
            case "EK":
                return context.getString(R.string.geo);
            case "PH":
                return context.getString(R.string.physik);
            case "CH":
                return context.getString(R.string.chemie);
            case "N":
                return context.getString(R.string.niederländisch);
            case "IF":
                return context.getString(R.string.info);
            case "KR":
                return context.getString(R.string.reliKat);
            case "ER":
                return context.getString(R.string.reliEv);
            case "PP":
                return context.getString(R.string.philo);
            case "MU":
                return context.getString(R.string.musik);
            case "S":
                return context.getString(R.string.spanisch);
        }
        return "";
    }

    private String fachnameAG(String teil) {
        switch (teil.toUpperCase()) {
            case "RT":
                return context.getString(R.string.rt);
            case "KL":
                return context.getString(R.string.klett);
            case "ROB":
                return context.getString(R.string.rob);
            case "AQ":
                return context.getString(R.string.aq);
            case "TS":
                return context.getString(R.string.ten);
            case "SP":
                return context.getString(R.string.fb);
            case "MU":
                return context.getString(R.string.band);
            case "MINT":
                return context.getString(R.string.mint);
            case "SCHA":
                return context.getString(R.string.schach);
            case "CH":
                return context.getString(R.string.exp);
            case "KU":
                return context.getString(R.string.kunst);
            case "TA":
                return context.getString(R.string.hip);
            case "ZTG":
                return context.getString(R.string.ztg);
            case "SAN":
                return context.getString(R.string.sani);
            case "NÄ":
                return context.getString(R.string.näh);
            case "KO":
                return context.getString(R.string.kochen);
            case "JUD":
                return context.getString(R.string.jud);
        }
        return teil;
    }

    private String fachnameOberstufe(String teil) {
        switch (teil.toUpperCase()) {
            case "M ":
                return context.getString(R.string.mathe);
            case "D ":
                return context.getString(R.string.deutsch);
            case "DE":
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
                return context.getString(R.string.philo);
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
            case "MV":
                return context.getString(R.string.vokal);
            case "SP":
                return context.getString(R.string.sport);
        }
        if (teil.matches("S[0-9]"))
            return context.getString(R.string.spanisch);
        return "";
    }

    public String gibZeiten(Fach f) {
        String condition, table;
        if (f.id == 0) {
            table = TABLE_STUNDEN + ", " + TABLE_FAECHER;
            condition = TABLE_FAECHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + FACH_KURZEL + " = '" + f.getKuerzel() + "'";
        } else {
            table = TABLE_STUNDEN;
            condition = FACH_ID + " = " + f.id;
        }
        Cursor        cursor  = database.query(table, new String[]{STUNDEN_TAG, STUNDEN_STUNDE}, condition, null, null, null, STUNDEN_TAG + ", " + STUNDEN_STUNDE);
        StringBuilder builder = new StringBuilder();
        if (cursor.getCount() > 0) {
            int[][] woche = new int[5][10];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                woche[cursor.getInt(0) - 1][cursor.getInt(1) - 1] = 1;
            }
            cursor.close();
            for (int i = 0; i < woche.length; i++) {
                for (int j = 0; j < woche[i].length; j++) {
                    if (woche[i][j] == 1) {
                        if (builder.length() > 0)
                            builder.append(System.getProperty("line.separator"));
                        builder.append(tagToString(i + 1))
                                .append(": ");
                        String zeit    = stundeToString(j + 1);
                        String stunden = " (" + (j + 1) + '.';
                        if (j < woche[i].length - 1 && woche[i][j + 1] == 1) {
                            zeit = zeit.substring(0, 8) + stundeToString(j + 2).substring(8);
                            stunden += " - " + (j + 2) + '.';
                        }
                        stunden += " Stunde)";
                        builder.append(zeit)
                                .append(stunden);
                        break;
                    }
                }
            }
        } else {
            cursor.close();
        }
        return builder.toString();
    }

    public String gibZeit(int tag, int stunde) {
        return tagToString(tag) + ": " + stundeToString(stunde) + " (" + (stunde + 1) + ". Stunde)";
    }

    private String tagToString(int tag) {
        switch (tag) {
            case 1:
                return Utils.getString(R.string.montag);
            case 2:
                return Utils.getString(R.string.dienstag);
            case 3:
                return Utils.getString(R.string.mittwoch);
            case 4:
                return Utils.getString(R.string.donnerstag);
            case 5:
                return Utils.getString(R.string.freitag);
            default:
                return Utils.getString(R.string.montag);
        }
    }

    private String stundeToString(int pStunde) {
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

    public void freistunde(int tag, int stunde) {
        Fach prev = getFach(tag, stunde);
        if (prev == null) {
            ContentValues values = new ContentValues();
            values.put(FACH_NAME, "");
            values.put(FACH_ART, "FREI");
            values.put(FACH_LEHRER, "");
            values.put(FACH_KURZEL, "FREI");
            values.put(FACH_KLASSE, "");
            int fid = (int) database.insert(TABLE_FAECHER, null, values);
            values.clear();
            values.put(FACH_ID, fid);
            values.put(STUNDEN_TAG, tag);
            values.put(STUNDEN_STUNDE, stunde);
            values.put(STUNDE_RAUM, "");
            database.insert(TABLE_STUNDEN, null, values);
            values.clear();
            values.put(FACH_ID, fid);
            values.put(GEWAEHLT_SCHRIFTLICH, 0);
            database.insert(TABLE_GEWAEHLT, null, values);
        }
    }

    public boolean mussSchriftlich(long fid) {
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            return true;

        Cursor cursor = database.query(TABLE_FAECHER, new String[]{FACH_ART, FACH_NAME}, FACH_ID + " = " + fid, null, null, null, null);
        cursor.moveToFirst();
        boolean b = cursor.getCount() > 0 && (cursor.getString(0).equals("LK"));
        cursor.close();
        return b;
    }

    public boolean istGewaehlt(int fid) {
        String  selection = FACH_ID + " = " + fid;
        Cursor  cursor    = database.query(TABLE_GEWAEHLT, new String[]{FACH_ID}, selection, null, null, null, null);
        boolean b         = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public double[] gibStunden(int fid) {
        String   condition = FACH_ID + " = " + fid;
        Cursor   cursor    = database.query(TABLE_STUNDEN, new String[]{STUNDEN_TAG, STUNDEN_STUNDE}, condition, null, null, null, STUNDEN_TAG + ", " + STUNDEN_STUNDE);
        double[] array     = new double[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = cursor.getInt(0) + (cursor.getDouble(1) / 10);
        }
        cursor.close();
        return array;
    }

    public String[] gibSchriftlicheFaecherStrings() {
        String   table     = TABLE_FAECHER + ", " + TABLE_GEWAEHLT;
        String[] columns   = {FACH_KURZEL, FACH_LEHRER};
        String   selection = TABLE_FAECHER + "." + FACH_ID + " = " + TABLE_GEWAEHLT + "." + FACH_ID + " AND " + GEWAEHLT_SCHRIFTLICH + " = 1";
        Cursor   cursor    = database.query(table, columns, selection, null, null, null, null);
        String[] faecher   = new String[0];
        if (cursor.getCount() > 0) {
            faecher = new String[cursor.getCount()];
            String stufe = Utils.getUserStufe();
            Utils.logDebug(stufe);
            cursor.moveToFirst();
            for (int i = 0; !cursor.isAfterLast(); cursor.moveToNext(), i++) {
                String kuerzel = cursor.getString(0);
                String lehrer  = cursor.getString(1);
                String teil1   = kuerzel.substring(0, 2);
                String teil2   = kuerzel.substring(2, 4);
                if (teil1.charAt(1) != ' ')
                    teil1 += ' ';
                if (teil2.charAt(0) == 'L')
                    teil2 = "L";
                kuerzel = teil1 + teil2;
                kuerzel = kuerzel + " " + lehrer;
                if (Utils.getUserPermission() != User.PERMISSION_LEHRER)
                    kuerzel += " " + stufe;
                faecher[i] = kuerzel;
            }
        }
        cursor.close();
        return faecher;
    }

    public boolean hatGewaehlt() {
        Cursor  cursor = database.query(TABLE_GEWAEHLT, new String[]{FACH_ID}, null, null, null, null, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public void clear() {
        database.delete(TABLE_FAECHER, null, null);
        database.delete(TABLE_GEWAEHLT, null, null);
        database.delete(TABLE_STUNDEN, null, null);
    }
}
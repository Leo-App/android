package de.slg.stundenplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class StundenplanDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME       = "stundenplan";
    private static final String TABLE_FACHER        = "faecher";
    private static final String FACH_ID             = "fid";
    private static final String FACH_NAME           = "fname";
    private static final String FACH_KURZEL         = "fkurz";
    private static final String FACH_LEHRER         = "flehrer";
    private static final String FACH_ART            = "fart";
    private static final String TABLE_STUNDEN       = "stunden";
    private static final String STUNDEN_TAG         = "stag";
    private static final String STUNDEN_STUNDE      = "sstunde";
    private static final String STUNDE_RAUM         = "sraum";
    private static final String STUNDE_NOTIZ        = "snotiz";
    private static final String TABLE_GEWAHLT       = "gewaehlt";
    private static final String GEWAHLT_SCHRIFTLICH = "gschriftlich";

    private final SQLiteDatabase database;
    private final Context        context;

    public StundenplanDB(Context context) {
        super(context, DATABASE_NAME, null, 3);
        database = getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FACHER + " (" +
                FACH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FACH_ART + " TEXT NOT NULL, " +
                FACH_KURZEL + " TEXT NOT NULL, " +
                FACH_NAME + " TEXT NOT NULL, " +
                FACH_LEHRER + " TEXT NOT NULL)");
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GEWAHLT + " (" +
                FACH_ID + " INTEGER PRIMARY KEY, " +
                GEWAHLT_SCHRIFTLICH + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEWAHLT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUNDEN);
        onCreate(db);
    }

    long insertFach(String kurz, String lehrer) {
        while (kurz.contains("  "))
            kurz = kurz.replace("  ", " ");

        String selection = FACH_KURZEL + " = '" + kurz + "' AND " + FACH_LEHRER + " = '" + lehrer + "'";
        Cursor cursor    = database.query(TABLE_FACHER, new String[]{FACH_ID}, selection, null, null, null, null);
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
        return database.insert(TABLE_FACHER, null, values);
    }

    void insertStunde(long fid, int tag, int stunde, String raum) {
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

    void waehleFach(long fid) {
        ContentValues values = new ContentValues();
        values.put(FACH_ID, fid);
        values.put(GEWAHLT_SCHRIFTLICH, mussSchriftlich(fid) ? 1 : 0);
        database.insert(TABLE_GEWAHLT, null, values);
    }

    void setzeNotiz(String notiz, int fid, int tag, int stunde) {
        ContentValues values = new ContentValues();
        values.put(STUNDE_NOTIZ, notiz);
        database.update(TABLE_STUNDEN, values, FACH_ID + " = " + fid + " AND " + STUNDEN_TAG + " = " + tag + " AND " + STUNDEN_STUNDE + " = " + stunde, null);
    }

    void setzeSchriftlich(boolean schriftlich, long fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAHLT_SCHRIFTLICH, schriftlich ? 1 : 0);
        database.update(TABLE_GEWAHLT, values, FACH_ID + " = " + fid, null);
    }

    void loescheWahlen() {
        database.delete(TABLE_GEWAHLT, null, null);
    }

    Fach[] getFaecher() {
        String   table     = TABLE_FACHER + ", " + TABLE_STUNDEN;
        String[] columns   = {TABLE_FACHER + "." + FACH_ID, FACH_KURZEL, FACH_NAME, FACH_ART, FACH_LEHRER, STUNDE_RAUM, STUNDEN_TAG, STUNDEN_STUNDE};
        String   selection = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + FACH_ART + " != 'FREI'";
        Cursor   cursor    = database.query(table, columns, selection, null, FACH_KURZEL, null, FACH_ART + " DESC, " + TABLE_FACHER + "." + FACH_ID);
        Fach[]   faecher   = new Fach[cursor.getCount()];
        int      i         = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            faecher[i] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7));
        }
        cursor.close();
        return faecher;
    }

    public Fach[] gewaehlteFaecherAnTag(int tag) {
        String table = TABLE_FACHER + ", " + TABLE_GEWAHLT + ", " + TABLE_STUNDEN;
        String[] columns = {TABLE_FACHER + "." + FACH_ID,
                FACH_KURZEL,
                FACH_NAME,
                FACH_ART,
                FACH_LEHRER,
                STUNDE_RAUM,
                STUNDEN_STUNDE,
                GEWAHLT_SCHRIFTLICH,
                STUNDE_NOTIZ};
        String selection = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + TABLE_FACHER + "." + FACH_ID + " = " + TABLE_GEWAHLT + "." + FACH_ID + " AND " + TABLE_STUNDEN + "." + STUNDEN_TAG + " = " + tag;
        Cursor cursor    = database.query(table, columns, selection, null, null, null, STUNDEN_STUNDE);
        Fach[] faecher   = new Fach[0];
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            faecher = new Fach[cursor.getInt(6)];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                faecher[cursor.getInt(6) - 1] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), tag, cursor.getInt(6));
                faecher[cursor.getInt(6) - 1].setzeNotiz(cursor.getString(8));
                faecher[cursor.getInt(6) - 1].setzeSchriftlich(cursor.getInt(7) == 1);
            }
            for (int i = 0; i < faecher.length; i++) {
                if (faecher[i] == null)
                    faecher[i] = new Fach(0, "", "", "", "", tag, i + 1);
            }
        }
        cursor.close();
        return faecher;
    }

    Fach getFach(int tag, int stunde) {
        String table = TABLE_FACHER + ", " + TABLE_GEWAHLT + ", " + TABLE_STUNDEN;
        String[] columns = {TABLE_FACHER + "." + FACH_ID,
                FACH_KURZEL,
                FACH_NAME,
                FACH_ART,
                FACH_LEHRER,
                STUNDE_RAUM,
                STUNDEN_TAG,
                STUNDEN_STUNDE,
                GEWAHLT_SCHRIFTLICH,
                STUNDE_NOTIZ};
        String selection = TABLE_GEWAHLT + "." + FACH_ID + " = " + TABLE_FACHER + "." + FACH_ID
                + " AND " + TABLE_STUNDEN + "." + FACH_ID + " = " + TABLE_FACHER + "." + FACH_ID
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
                    tag, stunde);
            f.setzeNotiz(cursor.getString(9));
            f.setzeSchriftlich(cursor.getInt(8) == 1);
        }
        cursor.close();
        return f;
    }

    private String getFachname(String kuerzel) {
        String stufe = Utils.getUserStufe();
        if (kuerzel.startsWith("L-") || kuerzel.matches("L[0-9]")) {
            return context.getString(R.string.latein);
        }
        if (stufe.equals("EF") || stufe.equals("Q1") || stufe.equals("Q2")) {
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
                if (kuerzel.endsWith("DF")) {
                    if (kuerzel.startsWith("PK")) {
                        return context.getString(R.string.PoWi) + ' ' + context.getString(R.string.Diff);
                    }
                    return fachnameUnterstufe(kuerzel.substring(0, 2) + context.getString(R.string.Diff));
                }
                if (kuerzel.endsWith("FÖ")) {
                    int i = kuerzel.indexOf("FÖ");
                    return fachnameUnterstufe(kuerzel.substring(0, i)) + context.getString(R.string.Förder);
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
                break;
            case "GE":
                break;
            case "PK":
                break;
            case "EK":
                break;
            case "PH":
                break;
            case "CH":
                break;
            case "N":
                break;
            case "IF":
                break;
            case "KR":
                break;
            case "ER":
                break;
            case "PP":
                break;
            case "MU":
                break;
        }
        return null;
    }

    private String fachnameAG(String teil) {
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
        return null;
    }

    private String getFachart(String kuerzel) {
        if (kuerzel.contains("VTF"))
            return "VTF";
        if (kuerzel.matches("[A-Za-zÄÖÜäöü]{1,3}[ ]?L[0-9]"))
            return "LK";
        if (kuerzel.charAt(kuerzel.length() - 2) == 'P')
            return "PK";
        if (kuerzel.substring(0, kuerzel.length() - 1).endsWith("ZG"))
            return "ZK";
        return "GK";
    }

    String gibZeiten(Fach f) {
        String condition, table;
        if (f.id == 0) {
            table = TABLE_STUNDEN + ", " + TABLE_FACHER;
            condition = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + FACH_KURZEL + " = '" + f.gibKurz() + "'";
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

    String gibZeit(int tag, int stunde) {
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

    void freistunde(int tag, int stunde) {
        Fach prev = getFach(tag, stunde);
        if (prev == null) {
            ContentValues values = new ContentValues();
            values.put(FACH_NAME, "");
            values.put(FACH_ART, "FREI");
            values.put(FACH_LEHRER, "");
            values.put(FACH_KURZEL, "FREI");
            int fid = (int) database.insert(TABLE_FACHER, null, values);
            values.clear();
            values.put(FACH_ID, fid);
            values.put(STUNDEN_TAG, tag);
            values.put(STUNDEN_STUNDE, stunde);
            values.put(STUNDE_RAUM, "");
            database.insert(TABLE_STUNDEN, null, values);
            values.clear();
            values.put(FACH_ID, fid);
            values.put(GEWAHLT_SCHRIFTLICH, 0);
            database.insert(TABLE_GEWAHLT, null, values);
        }
    }

    void deleteFreistunde(int tag, int stunde) {
        String table     = TABLE_FACHER + ", " + TABLE_STUNDEN;
        String selection = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + STUNDEN_STUNDE + " = " + stunde + " AND " + STUNDEN_TAG + " = " + tag;
        Cursor cursor    = database.query(table, new String[]{TABLE_FACHER + "." + FACH_ID, FACH_ART}, selection, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getString(1).equals("FREI")) {
            int fid = cursor.getInt(0);
            database.delete(TABLE_FACHER, FACH_ID + " = " + fid, null);
            database.delete(TABLE_STUNDEN, FACH_ID + " = " + fid, null);
            database.delete(TABLE_GEWAHLT, FACH_ID + " = " + fid, null);
        } else {
            cursor.close();
        }
    }

    boolean mussSchriftlich(long fid) {
        Cursor cursor = database.query(TABLE_FACHER, new String[]{FACH_ART, FACH_NAME}, FACH_ID + " = " + fid, null, null, null, null);
        cursor.moveToFirst();
        boolean b = cursor.getCount() > 0 && (cursor.getString(0).equals("LK") || cursor.getString(1).equals(Utils.getString(R.string.deutsch)) || cursor.getString(1).equals(Utils.getString(R.string.mathe)));
        cursor.close();
        return b;
    }

    boolean istGewaehlt(int fid) {
        String  selection = FACH_ID + " = " + fid;
        Cursor  cursor    = database.query(TABLE_GEWAHLT, new String[]{FACH_ID}, selection, null, null, null, null);
        boolean b         = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    double[] gibStunden(int fid) {
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
        String   table     = TABLE_FACHER + ", " + TABLE_GEWAHLT;
        String[] columns   = {FACH_KURZEL, FACH_LEHRER};
        String   selection = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_GEWAHLT + "." + FACH_ID + " AND " + GEWAHLT_SCHRIFTLICH + " = 1";
        Cursor   cursor    = database.query(table, columns, selection, null, null, null, null);
        String[] faecher   = null;
        if (cursor.getCount() > 0) {
            faecher = new String[cursor.getCount()];
            String stufe = Utils.getUserStufe();
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
                kuerzel = kuerzel + " " + lehrer + " " + stufe;
                faecher[i] = kuerzel;
            }
        }
        cursor.close();
        return faecher;
    }

    boolean hatGewaehlt() {
        Cursor  cursor = database.query(TABLE_GEWAHLT, new String[]{FACH_ID}, null, null, null, null, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }
}
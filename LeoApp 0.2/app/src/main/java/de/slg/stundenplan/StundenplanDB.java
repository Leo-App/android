package de.slg.stundenplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class StundenplanDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stundenplan";
    private static final String TABLE_FACHER = "faecher";
    private static final String FACH_ID = "fid";
    private static final String FACH_NAME = "fname";
    private static final String FACH_KURZEL = "fkurz";
    private static final String FACH_LEHRER = "flehrer";
    private static final String FACH_RAUM = "fraum";
    private static final String FACH_ART = "fart";
    private static final String TABLE_STUNDEN = "stunden";
    private static final String STUNDEN_TAG = "stag";
    private static final String STUNDEN_STUNDE = "sstunde";
    private static final String TABLE_GEWAHLT = "gewaehlt";
    private static final String GEWAHLT_SCHRIFTLICH = "gschriftlich";
    private static final String GEWAHLT_NOTIZ = "gnotiz";

    private SQLiteDatabase database;
    private Context context;

    public StundenplanDB(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
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
                FACH_LEHRER + " TEXT NOT NULL, " +
                FACH_RAUM + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STUNDEN + " (" +
                FACH_ID + " INTEGER NOT NULL, " +
                STUNDEN_TAG + " INTEGER NOT NULL, " +
                STUNDEN_STUNDE + " INTEGER NOT NULL, PRIMARY KEY" +
                " (" + FACH_ID +
                ", " + STUNDEN_TAG +
                ", " + STUNDEN_STUNDE + "))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GEWAHLT + " (" +
                FACH_ID + " INTEGER PRIMARY KEY, " +
                GEWAHLT_SCHRIFTLICH + " INTEGER NOT NULL, " +
                GEWAHLT_NOTIZ + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    long insertFach(String kurz, String lehrer, String raum) {
        ContentValues values = new ContentValues();
        values.put(FACH_ART, kurz.substring(2, 3) + "K");
        values.put(FACH_KURZEL, kurz);
        values.put(FACH_NAME, kurzToFach(kurz.substring(0, 2)));
        values.put(FACH_LEHRER, lehrer);
        values.put(FACH_RAUM, raum);
        return database.insert(TABLE_FACHER, null, values);
    }

    void insertStunde(long fid, int tag, int stunde) {
        ContentValues values = new ContentValues();
        values.put(FACH_ID, fid);
        values.put(STUNDEN_TAG, tag);
        values.put(STUNDEN_STUNDE, stunde);
        database.insert(TABLE_STUNDEN, null, values);
    }

    void waehleFach(int fid) {
        ContentValues values = new ContentValues();
        values.put(FACH_ID, fid);
        values.put(GEWAHLT_NOTIZ, "");
        values.put(GEWAHLT_SCHRIFTLICH, 0);
        database.insert(TABLE_GEWAHLT, null, values);
    }

    void setzeNotiz(String notiz, int fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAHLT_NOTIZ, notiz);
        database.update(TABLE_GEWAHLT, values, FACH_ID + " = " + fid, null);
    }

    void setzeSchriftlich(boolean schriftlich, int fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAHLT_SCHRIFTLICH, schriftlich ? 1 : 0);
        database.update(TABLE_GEWAHLT, values, FACH_ID + " = " + fid, null);
    }

    void loescheWahlen() {
        database.delete(TABLE_GEWAHLT, null, null);
    }

    Fach[] getFaecher() {
        String[] columns = {TABLE_FACHER + "." + FACH_ID, FACH_KURZEL, FACH_NAME, FACH_ART, FACH_LEHRER, FACH_RAUM, STUNDEN_TAG, STUNDEN_STUNDE};
        Cursor cursor = database.query(TABLE_FACHER + ", " + TABLE_STUNDEN, columns, TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID, null, null, null, null);
        Fach[] faecher = new Fach[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            faecher[i] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), context);
        }
        cursor.close();
        return faecher;
    }

    Fach[] gewaehlteFaecherAnTag(int tag) {
        String table = TABLE_FACHER + ", " + TABLE_GEWAHLT + ", " + TABLE_STUNDEN;
        String[] columns = {TABLE_FACHER + "." + FACH_ID,
                FACH_KURZEL,
                FACH_NAME,
                FACH_ART,
                FACH_LEHRER,
                FACH_RAUM,
                STUNDEN_STUNDE,
                GEWAHLT_SCHRIFTLICH,
                GEWAHLT_NOTIZ};
        String selection = TABLE_FACHER + "." + FACH_ID + " = " + TABLE_STUNDEN + "." + FACH_ID + " AND " + TABLE_FACHER + "." + FACH_ID + " = " + TABLE_GEWAHLT + "." + FACH_ID + " AND " + TABLE_STUNDEN + "." + STUNDEN_TAG + " = " + tag;
        Cursor cursor = database.query(table, columns, selection, null, null, null, STUNDEN_STUNDE);
        Fach[] faecher = new Fach[0];
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            faecher = new Fach[cursor.getInt(6)];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                faecher[cursor.getInt(6) - 1] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK" : ""), cursor.getString(4), cursor.getString(5), tag, cursor.getInt(6), context);
                faecher[cursor.getInt(6) - 1].setzeNotiz(cursor.getString(8));
                faecher[cursor.getInt(6) - 1].setzeSchriftlich(cursor.getInt(7) == 1);
            }
            for (int i = 0; i < faecher.length; i++) {
                if (faecher[i] == null)
                    faecher[i] = new Fach(0, "", "", "", "", tag, i + 1, context);
            }
        }
        cursor.close();
        return faecher;
    }

    private String kurzToFach(String pKurzelTeil) {
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
            default:
                return pKurzelTeil;
        }
    }

    int idVonKuerzel(String kuerzel) {
        Cursor cursor = database.query(TABLE_FACHER, new String[]{FACH_ID}, FACH_KURZEL + " = '" + kuerzel + "'", null, null, null, null);
        int fid = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            fid = cursor.getInt(0);
        }
        cursor.close();
        return fid;
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
        Cursor cursor = database.query(table, new String[]{STUNDEN_TAG, STUNDEN_STUNDE}, condition, null, null, null, STUNDEN_TAG + ", " + STUNDEN_STUNDE);
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
                        String zeit = stundeToString(j + 1);
                        if (j < woche[i].length - 1 && woche[i][j + 1] == 1) {
                            zeit = zeit.substring(0, 8) + stundeToString(j + 2).substring(8);
                        }
                        builder.append(zeit);
                        break;
                    }
                }
            }
        } else {
            cursor.close();
        }
        return builder.toString();
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
}
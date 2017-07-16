package de.slg.stundenplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slg.leoapp.R;

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
        values.put(FACH_NAME, macheFachnameTeil(kurz.substring(0, 2)));
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
        values.put(GEWAHLT_SCHRIFTLICH, false);
        database.insert(TABLE_GEWAHLT, null, values);
    }

    void setzeNotiz(String notiz, int fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAHLT_NOTIZ, notiz);
        database.update(TABLE_GEWAHLT, values, FACH_ID + " = " + fid, null);
    }

    void setzeSchriftlich(boolean schriftlich, int fid) {
        ContentValues values = new ContentValues();
        values.put(GEWAHLT_SCHRIFTLICH, schriftlich);
        database.update(TABLE_GEWAHLT, values, FACH_ID + " = " + fid, null);
    }

    Fach[] getFaecher() {
        String[] columns = {FACH_ID, FACH_KURZEL, FACH_NAME, FACH_ART, FACH_LEHRER, FACH_RAUM};
        Cursor cursor = database.query(TABLE_FACHER,columns, null, null, null, null, null);
        Fach[] faecher = new Fach[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            faecher[i] = new Fach(cursor.getInt(0), cursor.getString(1), cursor.getString(2) + (cursor.getString(3).equals("LK") ? " LK": ""), cursor.getString(4), cursor.getString(5), context);
        }
        cursor.close();
        return faecher;
    }

    private String macheFachnameTeil(String pKurzelTeil) {
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
}
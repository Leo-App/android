package de.slg.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConnectorKlausurplan extends SQLiteOpenHelper {
    private final SQLiteDatabase database;

    private final static String DATABASE_NAME = "klausurplan";

    private final static String TABLE_KLAUSUREN         = "klausuren";
    private static final String KLAUSUR_ID              = "id";
    private static final String KLAUSUR_TITEL           = "title";
    private static final String KLAUSUR_STUFE           = "stufe";
    private static final String KLAUSUR_DATUM           = "datum";
    private static final String KLAUSUR_NOTIZ           = "notiz";
    private static final String KLAUSUR_HERUNTERGELADEN = "heruntergeladen";

    public SQLiteConnectorKlausurplan(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXIST " + TABLE_KLAUSUREN + " (" +
                KLAUSUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KLAUSUR_TITEL + " TEXT NOT NULL, " +
                KLAUSUR_STUFE + " TEXT NOT NULL, " +
                KLAUSUR_DATUM + " TEXT NOT NULL, " +
                KLAUSUR_NOTIZ + " INTEGER NOT NULL, " +
                KLAUSUR_HERUNTERGELADEN + " INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_KLAUSUREN);
        onCreate(db);
    }

    public long insert(String titel, String stufe, String datum, boolean heruntergeladen) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_TITEL, titel);
        values.put(KLAUSUR_STUFE, stufe);
        values.put(KLAUSUR_DATUM, datum);
        values.put(KLAUSUR_NOTIZ, "");
        values.put(KLAUSUR_HERUNTERGELADEN, heruntergeladen);
        return database.insert(TABLE_KLAUSUREN, null, values);
    }

    public void setTitel(long id, String titel) {

    }

    public void setDatum(long id, String datum) {

    }

    public void setNotiz (long id, String notiz) {

    }

    public Klausur[] getKlausuren() {
        return new Klausur[0];
    }
}
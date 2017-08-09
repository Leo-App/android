package de.slg.schwarzes_brett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConnector extends SQLiteOpenHelper {
    public static final String TABLE_EINTRAEGE = "Eintraege";
    static final String EINTRAEGE_TITEL = "titel";
    static final String EINTRAEGE_ADRESSAT = "adressat";
    static final String EINTRAEGE_INHALT = "inhalt";
    static final String EINTRAEGE_ERSTELLDATUM = "erstelldatum";
    static final String EINTRAEGE_ABLAUFDATUM = "ablaufdatum";
    private static final String DATABASE_NAME = "entries.db";
    private static final String EINTRAEGE_ID = "id";

    public SQLiteConnector(Context c) {
        super(c, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EINTRAEGE + " (" +
                EINTRAEGE_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                EINTRAEGE_TITEL + " TEXT NOT NULL, " +
                EINTRAEGE_ADRESSAT + " TEXT NOT NULL, " +
                EINTRAEGE_INHALT + " TEXT NOT NULL, " +
                EINTRAEGE_ERSTELLDATUM + " TEXT NOT NULL, " +
                EINTRAEGE_ABLAUFDATUM + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EINTRAEGE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues getContentValues(String titel, String adressat, String inhalt, long erstelldatum, long ablaufdatum) {
        ContentValues values = new ContentValues();
        values.put(EINTRAEGE_TITEL, titel);
        values.put(EINTRAEGE_ADRESSAT, adressat);
        values.put(EINTRAEGE_INHALT, inhalt);
        values.put(EINTRAEGE_ERSTELLDATUM, erstelldatum);
        values.put(EINTRAEGE_ABLAUFDATUM, ablaufdatum);
        return values;
    }

    public long getLatestDate(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_EINTRAEGE, new String[]{EINTRAEGE_ERSTELLDATUM}, null, null, null, null, EINTRAEGE_ERSTELLDATUM + " DESC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long l = cursor.getLong(0);
            cursor.close();
            return l;
        } else {
            cursor.close();
            return 0;
        }
    }
}


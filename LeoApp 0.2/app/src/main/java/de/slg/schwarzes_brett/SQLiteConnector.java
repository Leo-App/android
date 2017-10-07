package de.slg.schwarzes_brett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConnector extends SQLiteOpenHelper {
    public static final  String TABLE_EINTRAEGE        = "Eintraege";
    static final         String EINTRAEGE_TITEL        = "titel";
    static final         String EINTRAEGE_ADRESSAT     = "adressat";
    static final         String EINTRAEGE_INHALT       = "inhalt";
    static final         String EINTRAEGE_ANHANG       = "anhang";
    static final         String EINTRAEGE_ERSTELLDATUM = "erstelldatum";
    static final         String EINTRAEGE_ABLAUFDATUM  = "ablaufdatum";
    static final         String EINTRAEGE_REMOTE_ID    = "remoteid";
    static final         String EINTRAEGE_VIEWS        = "gesehen";
    private static final String EINTRAEGE_ID           = "id";
    private static final String DATABASE_NAME          = "entries.db";

    public static final  String TABLE_SURVEYS        = "Umfragen";
    static final         String SURVEYS_TITEL        = "titel";
    static final         String SURVEYS_ADRESSAT     = "adressat";
    static final         String SURVEYS_BESCHREIBUNG = "inhalt";
    static final         String SURVEYS_ABSENDER     = "absender";
    static final         String SURVEYS_MULTIPLE     = "multiple";
    static final         String SURVEYS_ID           = "id";

    public static final  String TABLE_ANSWERS        = "Antworten";
    static final         String ANSWERS_SID          = "umfrageid";
    static final         String ANSWERS_INHALT       = "inhalt";
    static final         String ANSWERS_REMOTE_ID    = "remoteid";
    static final         String ANSWERS_SELECTED     = "gewaehlt";
    static final         String ANSWERS_ID           = "id";

    public SQLiteConnector(Context c) {
        super(c, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EINTRAEGE + " (" +
                EINTRAEGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                EINTRAEGE_TITEL + " TEXT NOT NULL, " +
                EINTRAEGE_ADRESSAT + " TEXT NOT NULL, " +
                EINTRAEGE_INHALT + " TEXT NOT NULL, " +
                EINTRAEGE_ERSTELLDATUM + " TEXT NOT NULL, " +
                EINTRAEGE_ABLAUFDATUM + " TEXT NOT NULL, " +
                EINTRAEGE_REMOTE_ID + " INTEGER NOT NULL, " +
                EINTRAEGE_VIEWS + " INTEGER NOT NULL, " +
                EINTRAEGE_ANHANG + " VARCHAR" +
                ")");
        db.execSQL("CREATE TABLE " + TABLE_SURVEYS + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL, " +
                SURVEYS_ADRESSAT + " TEXT NOT NULL, " +
                SURVEYS_ABSENDER + " TEXT NOT NULL, " +
                SURVEYS_MULTIPLE + " TINYINT NOT NULL, " +
                SURVEYS_BESCHREIBUNG + " TEXT NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE " + TABLE_ANSWERS + " (" +
                ANSWERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ANSWERS_SID + " INTEGER NOT NULL, " +
                ANSWERS_SELECTED + " TINYINT NOT NULL, " +
                ANSWERS_REMOTE_ID + " INTEGER NOT NULL, " +
                ANSWERS_INHALT + " TEXT NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EINTRAEGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues getEntryContentValues(String titel, String adressat, String inhalt, long erstelldatum, long ablaufdatum, int remoteid, int views, String path) {
        ContentValues values = new ContentValues();
        values.put(EINTRAEGE_TITEL, titel);
        values.put(EINTRAEGE_ADRESSAT, adressat);
        values.put(EINTRAEGE_INHALT, inhalt);
        values.put(EINTRAEGE_ERSTELLDATUM, erstelldatum);
        values.put(EINTRAEGE_ABLAUFDATUM, ablaufdatum);
        values.put(EINTRAEGE_REMOTE_ID, remoteid);
        values.put(EINTRAEGE_VIEWS, views);
        values.put(EINTRAEGE_ANHANG, path);
        return values;
    }

    public ContentValues getSurveyContentValues(String titel, String adressat, String beschreibung, String absender, short multiple) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        values.put(SURVEYS_ADRESSAT, adressat);
        values.put(SURVEYS_ABSENDER, absender);
        values.put(SURVEYS_BESCHREIBUNG, beschreibung);
        values.put(SURVEYS_MULTIPLE, multiple);
        return values;
    }

    public ContentValues getAnswerContentValues(long id, String inhalt, long umfrageId) {
        ContentValues values = new ContentValues();
        values.put(ANSWERS_SID, umfrageId);
        values.put(ANSWERS_INHALT, inhalt);
        values.put(ANSWERS_SELECTED, 0);
        values.put(ANSWERS_REMOTE_ID, id);
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


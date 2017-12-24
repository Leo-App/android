package de.slg.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import de.slg.leoapp.utility.Utils;

public class SQLiteConnectorUmfragen extends SQLiteOpenHelper {

    public static final  String TABLE_SURVEYS        = "Umfragen";
    public static final  String TABLE_ANSWERS        = "Antworten";

    public static final  String SURVEYS_ID           = "id";
    public static final  String SURVEYS_TITEL        = "titel";
    public static final  String SURVEYS_ADRESSAT     = "adressat";
    public static final  String SURVEYS_BESCHREIBUNG = "inhalt";
    public static final  String SURVEYS_ABSENDER     = "absender";
    public static final  String SURVEYS_REMOTE_ID    = "remoteid";
    public static final  String SURVEYS_ERSTELLDATUM = "erstelldatum";
    public static final  String SURVEYS_MULTIPLE     = "multiple";
    public static final  String ANSWERS_SID          = "umfrageid";
    public static final  String ANSWERS_INHALT       = "inhalt";
    public static final  String ANSWERS_REMOTE_ID    = "remoteid";
    public static final  String ANSWERS_SELECTED     = "gewaehlt";
    private static final String ANSWERS_ID           = "id";
    private static final String DATABASE_NAME        = "surveys.db";

    public SQLiteConnectorUmfragen(Context c) {
        super(c, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SURVEYS + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL, " +
                SURVEYS_ADRESSAT + " TEXT NOT NULL, " +
                SURVEYS_ABSENDER + " TEXT NOT NULL, " +
                SURVEYS_REMOTE_ID + " INTEGER NOT NULL, " +
                SURVEYS_MULTIPLE + " TINYINT NOT NULL, " +
                SURVEYS_ERSTELLDATUM + " TEXT NOT NULL, " +
                SURVEYS_BESCHREIBUNG + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ANSWERS + " (" +
                ANSWERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ANSWERS_SID + " INTEGER NOT NULL, " +
                ANSWERS_REMOTE_ID + " INTEGER NOT NULL, " +
                ANSWERS_SELECTED + " TINYINT NOT NULL, " +
                ANSWERS_INHALT + " TEXT NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues getSurveyContentValues(String titel, String adressat, String beschreibung, String absender, short multiple, int remoteId, long erstelldatum) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        values.put(SURVEYS_ADRESSAT, adressat);
        values.put(SURVEYS_ABSENDER, absender);
        values.put(SURVEYS_BESCHREIBUNG, beschreibung);
        values.put(SURVEYS_MULTIPLE, multiple);
        values.put(SURVEYS_ERSTELLDATUM, erstelldatum);
        values.put(SURVEYS_REMOTE_ID, remoteId);
        return values;
    }

    public ContentValues getAnswerContentValues(int id, String inhalt, long umfrageId, int selected) {
        ContentValues values = new ContentValues();
        values.put(ANSWERS_SID, umfrageId);
        values.put(ANSWERS_INHALT, inhalt);
        values.put(ANSWERS_REMOTE_ID, id);
        values.put(ANSWERS_SELECTED, selected);
        return values;
    }

    public long getLatestSurveyDate(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_SURVEYS, new String[]{SURVEYS_ERSTELLDATUM}, null, null, null, null, SURVEYS_ERSTELLDATUM + " DESC");
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

    public boolean getDatabaseAvailable() {
        File dbFile = Utils.getContext().getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }
}

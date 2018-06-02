package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import de.slgdev.leoapp.utility.Utils;

public class SQLiteConnectorUmfragen extends SQLiteOpenHelper {

    public static final String TABLE_SURVEYS = "Umfragen";
    public static final String TABLE_ANSWERS = "Antworten";

    public static final  String SURVEYS_ID           = "id";
    public static final  String SURVEYS_TITEL        = "titel";
    public static final  String SURVEYS_ADRESSAT     = "adressat";
    public static final  String SURVEYS_BESCHREIBUNG = "inhalt";
    public static final  String SURVEYS_ABSENDER     = "absender";
    public static final  String SURVEYS_REMOTE_ID    = "remoteid";
    public static final  String SURVEYS_ERSTELLDATUM = "erstelldatum";
    public static final  String SURVEYS_MULTIPLE     = "multiple";
    public static final  String SURVEYS_VOTEABLE     = "voteable";
    public static final  String ANSWERS_SID          = "umfrageid";
    public static final  String ANSWERS_INHALT       = "inhalt";
    public static final  String ANSWERS_REMOTE_ID    = "remoteid";
    public static final  String EINTRAEGE_ACCESSED   = "accessed";
    public static final  String ANSWERS_SELECTED     = "gewaehlt";
    private static final String ANSWERS_ID           = "id";
    private static final String DATABASE_NAME        = "surveys.db";

    private SQLiteDatabase database;

    public SQLiteConnectorUmfragen(Context c) {
        super(c, DATABASE_NAME, null, 6);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SURVEYS + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL, " +
                SURVEYS_ADRESSAT + " TEXT NOT NULL, " +
                SURVEYS_ABSENDER + " TEXT NOT NULL, " +
                SURVEYS_REMOTE_ID + " INTEGER NOT NULL, " +
                SURVEYS_BESCHREIBUNG + " TEXT NOT NULL, " +
                SURVEYS_MULTIPLE + " TINYINT NOT NULL, " +
                SURVEYS_ERSTELLDATUM + " TEXT NOT NULL, " +
                EINTRAEGE_ACCESSED + " BOOLEAN NOT NULL, " +
                SURVEYS_VOTEABLE + " TINYINT NOT NULL," +
                "CONSTRAINT idswithupdate UNIQUE ("+ SURVEYS_REMOTE_ID + ", " + SURVEYS_ERSTELLDATUM +")" +
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

    @Override
    public synchronized void close() {
        database.close();
        super.close();
    }

    public void deleteSurvey(int remoteID) {
        database.execSQL("DELETE FROM " + SQLiteConnectorUmfragen.TABLE_SURVEYS + " WHERE " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + remoteID);
        database.execSQL("DELETE FROM " + SQLiteConnectorUmfragen.TABLE_ANSWERS + " WHERE " + SQLiteConnectorUmfragen.ANSWERS_SID + " = (SELECT " + SQLiteConnectorUmfragen.SURVEYS_ID + " FROM " + SQLiteConnectorUmfragen.TABLE_SURVEYS + " WHERE " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + remoteID + ")");
    }

    public void setAnswerSelected(int id) {
        database.execSQL("UPDATE " + SQLiteConnectorUmfragen.TABLE_ANSWERS + " SET " + SQLiteConnectorUmfragen.ANSWERS_SELECTED + " = 1" + " WHERE " + SQLiteConnectorUmfragen.ANSWERS_REMOTE_ID + " = " + id);
    }

    public ContentValues getSurveyContentValues(String titel, String adressat, String beschreibung, String absender, short multiple, int remoteId, long erstelldatum, short voteable) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        values.put(SURVEYS_ADRESSAT, adressat);
        values.put(SURVEYS_ABSENDER, absender);
        values.put(SURVEYS_BESCHREIBUNG, beschreibung);
        values.put(SURVEYS_MULTIPLE, multiple);
        values.put(SURVEYS_VOTEABLE, voteable);
        values.put(SURVEYS_ERSTELLDATUM, erstelldatum);
        values.put(SURVEYS_REMOTE_ID, remoteId);
        values.put(EINTRAEGE_ACCESSED, false);
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

        String stufe = Utils.getUserStufe();
        String selection;
        switch (stufe) {
            case "":
            case "TEA":
                selection = null;
                break;
            case "EF":
            case "Q1":
            case "Q2":
                selection = SURVEYS_ADRESSAT
                        + " = '" + stufe +
                        "' OR "
                        + SURVEYS_ADRESSAT +
                        " = 'Sek II' OR " + SURVEYS_ADRESSAT +
                        " = 'Alle'";
                break;
            default:
                selection = SURVEYS_ADRESSAT +
                        " = '" + stufe.charAt(1) +
                        "' OR " +
                        SURVEYS_ADRESSAT +
                        " = 'Sek I' OR " +
                        SURVEYS_ADRESSAT +
                        " = 'Alle'";
                break;
        }

        Cursor cursor = db.query(TABLE_SURVEYS, new String[]{SURVEYS_ERSTELLDATUM}, selection, null, null, null, SURVEYS_ERSTELLDATUM + " DESC");
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

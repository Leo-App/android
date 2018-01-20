package de.slg.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import de.slg.leoapp.utility.Utils;

public class SQLiteConnectorUmfragenSpeichern extends SQLiteOpenHelper {

    public static final String TABLE_ANSWERS = "Antworten";
    public static final String TABLE_SAVED = "Gespeichertes";

    public static final  String SURVEYS_ID           = "id";
    public static final  String SURVEYS_TITEL        = "titel";
    public static final  String ANSWERS_SID          = "umfrageid";
    public static final  String ANSWERS_INHALT       = "inhalt";
    private static final String ANSWERS_ID           = "id";
    private static final String DATABASE_NAME        = "surveysSaved.db";

    public SQLiteConnectorUmfragenSpeichern(Context c) {
        super(c, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SAVED + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL " +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ANSWERS + " (" +
                ANSWERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ANSWERS_SID + " INTEGER NOT NULL, " +
                ANSWERS_INHALT + " TEXT NOT NULL" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues getSurveyContentValues(String titel) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        return values;
    }

    public ContentValues getAnswerContentValues(String inhalt, long umfrageId) {
        ContentValues values = new ContentValues();
        values.put(ANSWERS_SID, umfrageId);
        values.put(ANSWERS_INHALT, inhalt);
        return values;
    }

    public boolean getDatabaseAvailable() {
        File dbFile = Utils.getContext().getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }
}

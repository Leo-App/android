package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.umfragen.utility.ResultListing;
import de.slgdev.umfragen.utility.Survey;

public class SQLiteConnectorUmfragenSpeichern extends SQLiteOpenHelper {

    private static final String TABLE_ANSWERS = "Antworten";
    private static final String TABLE_SAVED   = "Umfragen";

    private static final String SURVEYS_ID     = "id";
    private static final String SURVEYS_TITEL  = "titel";
    private static final String SURVEYS_DESC   = "beschreibung";
    private static final String ANSWERS_SID    = "umfrageid";
    private static final String ANSWERS_INHALT = "inhalt";
    private static final String ANSWERS_VOTES  = "votes";
    private static final String ANSWERS_ID      = "id";

    private static final String DATABASE_NAME = "surveysSaved.db";

    private SQLiteDatabase database;

    public SQLiteConnectorUmfragenSpeichern(Context c) {
        super(c, DATABASE_NAME, null, 3);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SAVED + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL, " +
                SURVEYS_DESC + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ANSWERS + " (" +
                ANSWERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ANSWERS_SID + " INTEGER NOT NULL, " +
                ANSWERS_INHALT + " TEXT NOT NULL, " +
                ANSWERS_VOTES + " INTEGER NOT NULL" +
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

    @Override
    public synchronized void close() {
        database.close();
        super.close();
    }

    public ResultListing[] getSavedInfos() {

        Cursor c = database.query(TABLE_SAVED, null, null, null, null, null, null);
        c.moveToFirst();

        List<ResultListing> list = new List<>();

        while (!c.isAfterLast()) {

            ResultListing listing = new ResultListing(c.getString(1), c.getString(2));
            list.append(listing);

            Cursor cI = database.query(TABLE_ANSWERS, null, ANSWERS_SID + " = ?", new String[]{c.getString(0)}, null, null, null);
            cI.moveToFirst();

            HashMap<String, Integer> map = new HashMap<>();

            while (!cI.isAfterLast()) {
                map.put(cI.getString(2), cI.getInt(3));
                cI.moveToNext();
            }

            listing.setAnswerMap(map);
            cI.close();
            c.moveToNext();
        }

        c.close();

        return null;
    }

    public void addSurvey(ResultListing survey) {
        long id = database.insert(TABLE_SAVED, null, getSurveyContentValues(survey.title, survey.description));
        for (Map.Entry<String, Integer> entry : survey.answers.entrySet()) {
            database.insert(TABLE_ANSWERS, null, getAnswerContentValues(entry.getKey(), id, entry.getValue()));
        }
    }

    private ContentValues getSurveyContentValues(String titel, String description) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        values.put(SURVEYS_DESC, description);
        return values;
    }

    private ContentValues getAnswerContentValues(String inhalt, long umfrageId, int votes) {
        ContentValues values = new ContentValues();
        values.put(ANSWERS_SID, umfrageId);
        values.put(ANSWERS_INHALT, inhalt);
        values.put(ANSWERS_VOTES, votes);
        return values;
    }
}

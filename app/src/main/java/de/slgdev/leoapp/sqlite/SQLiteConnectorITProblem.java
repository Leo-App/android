package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConnectorITProblem extends SQLiteOpenHelper {

    public static final  String TABLE_DECISIONS   = "Entscheidungen";
    public static final  String DECISION_SUBJECT  = "titel";
    public static final  String DECISIONS_CONTENT = "adressat";

    private static final String DATABASE_NAME     = "problems.db";

    public SQLiteConnectorITProblem(Context c) {
        super(c, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DECISIONS
                + " (" +
                DECISION_SUBJECT + " VARCHAR(255) NOT NULL, " +
                DECISIONS_CONTENT + " TEXT NOT NULL," +
                " CONSTRAINT unique_subj UNIQUE ("+DECISION_SUBJECT+") " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECISIONS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues getContentValues(String subject, String content) {
        ContentValues values = new ContentValues();
        values.put(DECISION_SUBJECT, subject);
        values.put(DECISIONS_CONTENT, content);
        return values;
    }

}

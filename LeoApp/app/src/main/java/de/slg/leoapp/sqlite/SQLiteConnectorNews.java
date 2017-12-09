package de.slg.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import de.slg.leoapp.utility.Utils;

public class SQLiteConnectorNews extends SQLiteOpenHelper {
    public static final  String TABLE_EINTRAEGE        = "Eintraege";

    private static final String EINTRAEGE_ID           = "id";
    public static final  String EINTRAEGE_TITEL        = "titel";
    public static final  String EINTRAEGE_ADRESSAT     = "adressat";
    public static final  String EINTRAEGE_INHALT       = "inhalt";
    public static final  String EINTRAEGE_ANHANG       = "anhang";
    public static final  String EINTRAEGE_ERSTELLDATUM = "erstelldatum";
    public static final  String EINTRAEGE_ABLAUFDATUM  = "ablaufdatum";
    public static final  String EINTRAEGE_REMOTE_ID    = "remoteid";
    public static final  String EINTRAEGE_VIEWS        = "gesehen";

    private static final String DATABASE_NAME          = "entries.db";

    public SQLiteConnectorNews(Context c) {
        super(c, DATABASE_NAME, null, 5);
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

    public long getLatestEntryDate(SQLiteDatabase db) {
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

    public boolean getDatabaseAvailable() {
        File dbFile = Utils.getContext().getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }
}
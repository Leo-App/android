package de.slg.schwarzes_brett;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kim on 06.05.2017.
 */

public class SQLiteConnector extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "entries.db";
    private String createTable = "CREATE TABLE " + tableResult.tableName + " (" +
            tableResult.id + " INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            tableResult.titel + " TEXT NOT NULL, " +
            tableResult.adressat + " TEXT NOT NULL, " +
            tableResult.inhalt + " TEXT NOT NULL, " +
            tableResult.erstelldatum + " TEXT NOT NULL, " +
            tableResult.ablaufdatum + " TEXT NOT NULL)";
    private String delete = "DROP TABLE IF EXISTS " + tableResult.tableName;

    public SQLiteConnector(Context c) {
        super(c, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(delete);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);

    }

    public static class tableResult {
        public static final String tableName;
        public static final String titel;
        public static final String adressat;
        public static final String inhalt;
        public static final String erstelldatum;
        public static final String ablaufdatum;
        public static final String id;

        static {
            tableName = "Eintraege";
            titel = "Titel";
            adressat = "Adressat";
            erstelldatum = "Erstelldatum";
            ablaufdatum = "Ablaufdatum";
            inhalt = "inhalt";
            id = "id";
        }
    }

}


package de.slg.schwarzes_brett;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SQLiteConnector extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "entries.db";
    private String createTable = "CREATE TABLE " + tableResult.tableName + " (" +
            tableResult.id + " INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            tableResult.titel + " TEXT NOT NULL, " +
            tableResult.adressat + " TEXT NOT NULL, " +
            tableResult.inhalt + " TEXT NOT NULL, " +
            tableResult.erstelldatum + " TEXT NOT NULL, " +
            tableResult.ablaufdatum + " TEXT NOT NULL)";
    private String delete = "DROP TABLE IF EXISTS " + tableResult.tableName;

    SQLiteConnector(Context c) {
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

    static class tableResult {
        static final String tableName;
        static final String titel;
        static final String adressat;
        static final String inhalt;
        static final String erstelldatum;
        static final String ablaufdatum;
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


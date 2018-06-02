package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.Date;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.schwarzes_brett.utility.Entry;

public class SQLiteConnectorSchwarzesBrett extends SQLiteOpenHelper {

    public static final  String TABLE_EINTRAEGE         = "Eintraege";
    private static final  String EINTRAEGE_TITEL        = "titel";
    private static final  String EINTRAEGE_ADRESSAT     = "adressat";
    private static final  String EINTRAEGE_INHALT       = "inhalt";
    private static final  String EINTRAEGE_ANHANG       = "anhang";
    private static final  String EINTRAEGE_ERSTELLDATUM = "erstelldatum";
    private static final  String EINTRAEGE_ABLAUFDATUM  = "ablaufdatum";
    private static final  String EINTRAEGE_REMOTE_ID    = "remoteid";
    private static final  String EINTRAEGE_VIEWS        = "gesehen";
    private static final  String EINTRAEGE_ACCESSED     = "accessed";
    private static final String EINTRAEGE_ID            = "id";
    private static final String DATABASE_NAME           = "entries.db";

    private SQLiteDatabase database;

    public SQLiteConnectorSchwarzesBrett(Context c) {
        super(c, DATABASE_NAME, null, 6);
        database = getWritableDatabase();
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
                EINTRAEGE_ACCESSED + " INTEGER NOT NULL, " +
                EINTRAEGE_ANHANG + " VARCHAR, " +
                "CONSTRAINT ids UNIQUE ("+ EINTRAEGE_REMOTE_ID +")" +
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

    @Override
    public void close() {
        database.close();
        super.close();
    }

    private ContentValues getEntryContentValues(String titel, String adressat, String inhalt, long erstelldatum, long ablaufdatum, int remoteid, int views, String path) {
        ContentValues values = new ContentValues();
        values.put(EINTRAEGE_TITEL, titel);
        values.put(EINTRAEGE_ADRESSAT, adressat);
        values.put(EINTRAEGE_INHALT, inhalt);
        values.put(EINTRAEGE_ERSTELLDATUM, erstelldatum);
        values.put(EINTRAEGE_ABLAUFDATUM, ablaufdatum);
        values.put(EINTRAEGE_REMOTE_ID, remoteid);
        values.put(EINTRAEGE_VIEWS, views);
        values.put(EINTRAEGE_ANHANG, path);
        values.put(EINTRAEGE_ACCESSED, 0);
        return values;
    }

    public void insertEntry(String entry) {
        String[] res = entry.split(";");
        if (res.length == 8) {
            database.insertWithOnConflict(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, null, getEntryContentValues(
                    res[0],
                    res[1],
                    res[2],
                    Long.parseLong(res[3] + "000"),
                    Long.parseLong(res[4] + "000"),
                    Integer.parseInt(res[5]),
                    Integer.parseInt(res[6]),
                    res[7]
            ), SQLiteDatabase.CONFLICT_IGNORE);

            ContentValues values = new ContentValues();
            values.put(EINTRAEGE_VIEWS, Integer.parseInt(res[6]));

            database.update(TABLE_EINTRAEGE, values, EINTRAEGE_REMOTE_ID + " = " + Integer.parseInt(res[5]), null);

        }
    }

    public void purgeOldEntries() {
        database.delete(TABLE_EINTRAEGE, EINTRAEGE_ABLAUFDATUM + " < " + System.currentTimeMillis(), null);
    }

    public long getLatestEntryDate(SQLiteDatabase db) {

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
                selection = EINTRAEGE_ADRESSAT
                        + " = '" + stufe +
                        "' OR "
                        + EINTRAEGE_ADRESSAT +
                        " = 'Sek II' OR " + EINTRAEGE_ADRESSAT +
                        " = 'Alle'";
                break;
            default:
                selection = EINTRAEGE_ADRESSAT +
                        " = '" + stufe.charAt(1) +
                        "' OR " +
                        EINTRAEGE_ADRESSAT +
                        " = 'Sek I' OR " +
                        EINTRAEGE_ADRESSAT +
                        " = 'Alle'";
                break;
        }

        Cursor cursor = db.query(TABLE_EINTRAEGE, new String[]{EINTRAEGE_ERSTELLDATUM}, selection, null, null, null, EINTRAEGE_ERSTELLDATUM + " DESC");
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

    public List<Entry> getFilteredEntries(String stufe) {

        List<Entry> entries = new List<>();

        Cursor cursor;
        switch (stufe) {
            case "":
            case "TEA":
                cursor = database.query(
                        SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE,
                        new String[]{
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_VIEWS,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_REMOTE_ID},
                        null,
                        null,
                        null,
                        null,
                        EINTRAEGE_ERSTELLDATUM + " DESC"
                );
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = database.query(
                        SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE,
                        new String[]{
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_VIEWS,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_REMOTE_ID},
                        SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT
                                + " = '" + stufe +
                                "' OR "
                                + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT +
                                " = 'Sek II' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT +
                                " = 'Alle'",
                        null,
                        null,
                        null,
                        EINTRAEGE_ERSTELLDATUM + " DESC"
                );
                break;
            default:
                cursor = database.query(
                        SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE,
                        new String[]{
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_VIEWS,
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_REMOTE_ID},
                        SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT +
                                " = '" + stufe.charAt(1) +
                                "' OR " +
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT +
                                " = 'Sek I' OR " +
                                SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT +
                                " = 'Alle'",
                        null,
                        null,
                        null,
                        EINTRAEGE_ERSTELLDATUM + " DESC"
                );
                break;
        }


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            Date erstelldatum = new Date(cursor.getLong(3));
            Date ablaufdatum  = new Date(cursor.getLong(4));

            Entry entry = new Entry(
                    cursor.getInt(7),
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2).replace("\\\\", "\n"),
                    cursor.getInt(6),
                    erstelldatum,
                    ablaufdatum,
                    cursor.getString(5)
            );

            entries.append(entry);
        }
        cursor.close();

        return entries;
    }

    public boolean getDatabaseAvailable() {
        File dbFile = Utils.getContext().getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean getViewed(int remoteid) {
        Cursor c = database.query(
                TABLE_EINTRAEGE,
                new String[]{EINTRAEGE_ACCESSED},
                EINTRAEGE_REMOTE_ID + " = " + remoteid,
                null,
                null,
                null,
                null
        );

        c.moveToFirst();
        int viewed = c.getInt(c.getColumnIndex(EINTRAEGE_ACCESSED));
        c.close();

        return viewed == 1;
    }

    public void setViewed(int remoteid) {
        ContentValues values = new ContentValues();
        values.put(EINTRAEGE_ACCESSED, 1);
        database.update(TABLE_EINTRAEGE, values, EINTRAEGE_REMOTE_ID + " = " + remoteid, null);
    }



}
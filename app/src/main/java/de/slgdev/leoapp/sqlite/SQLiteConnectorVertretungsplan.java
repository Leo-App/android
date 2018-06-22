package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.slgdev.vertretungsplan.utility.VertretungsEvent;


public class SQLiteConnectorVertretungsplan extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vertretungsplan";

    private static final String TABLE_VERTRETUNG1 = "vertretung1";
    private static final String TABLE_VERTRETUNG2 = "vertretung2";

    private static final String VERTRETUNG_ID = "id";
    private static final String VERTRETUNG_KLASSE = "klasse";
    private static final String VERTRETUNG_STUNDE = "stunde";
    private static final String VERTRETUNG_VERTRETER = "vertreter";
    private static final String VERTRETUNG_FACH = "fach";
    private static final String VERTRETUNG_RAUM = "raum";
    private static final String VERTRETUNG_LEHRER = "lehrer";
    private static final String VERTRETUNG_ANMERKUNG = "anmerkung";
    private static final String VERTRETUNG_ENTFALL = "entfall";
    private static final String VERTRETUNG_DATUM = "datum";

    private final SQLiteDatabase database;

    public SQLiteConnectorVertretungsplan(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_VERTRETUNG1+" (" +
                VERTRETUNG_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                VERTRETUNG_KLASSE+" TEXT NOT NULL, "+
                VERTRETUNG_STUNDE+" TEXT NOT NULL, "+
                VERTRETUNG_VERTRETER+" TEXT NOT NULL, "+
                VERTRETUNG_FACH+" TEXT NOT NULL, "+
                VERTRETUNG_RAUM+" TEXT NOT NULL, "+
                VERTRETUNG_LEHRER+" TEXT NOT NULL, "+
                VERTRETUNG_ANMERKUNG+" TEXT NOT NULL, "+
                VERTRETUNG_ENTFALL+" BOOLEAN, "+
                VERTRETUNG_DATUM+" TEXT NOT NULL"+
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_VERTRETUNG2+" (" +
                VERTRETUNG_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                VERTRETUNG_KLASSE+" TEXT NOT NULL, "+
                VERTRETUNG_STUNDE+" TEXT NOT NULL, "+
                VERTRETUNG_VERTRETER+" TEXT NOT NULL, "+
                VERTRETUNG_FACH+" TEXT NOT NULL, "+
                VERTRETUNG_RAUM+" TEXT NOT NULL, "+
                VERTRETUNG_LEHRER+" TEXT NOT NULL, "+
                VERTRETUNG_ANMERKUNG+" TEXT NOT NULL, "+
                VERTRETUNG_ENTFALL+" BOOLEAN, "+
                VERTRETUNG_DATUM+" TEXT NOT NULL"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERTRETUNG1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERTRETUNG2);
        onCreate(db);
    }

    public long insert(VertretungsEvent vE, String dbName)   {
        ContentValues values = new ContentValues();
        values.put(VERTRETUNG_KLASSE, vE.getKlasse());
        values.put(VERTRETUNG_STUNDE, vE.getStunde());
        values.put(VERTRETUNG_VERTRETER, vE.getVertreter());
        values.put(VERTRETUNG_FACH, vE.getFach());
        values.put(VERTRETUNG_RAUM, vE.getRaum());
        values.put(VERTRETUNG_LEHRER, vE.getLehrer());
        values.put(VERTRETUNG_ANMERKUNG, vE.getAnmerkung());
        values.put(VERTRETUNG_ENTFALL, vE.getEntfall());
        values.put(VERTRETUNG_DATUM, vE.getDatum());
        return database.insert(dbName, null, values);
    }


    public void deleteTable() {
        database.delete(TABLE_VERTRETUNG1, null, null);
        database.delete(TABLE_VERTRETUNG2, null, null);
    }

    @Override
    public synchronized void close() {
        super.close();
        database.close();
    }

    public VertretungsEvent[] gibVertretungsplan(int nr)  {
        Cursor cursor;
        if (nr == 1)
            cursor = database.query(TABLE_VERTRETUNG1, new String[]{VERTRETUNG_KLASSE, VERTRETUNG_STUNDE, VERTRETUNG_VERTRETER, VERTRETUNG_FACH, VERTRETUNG_RAUM, VERTRETUNG_LEHRER, VERTRETUNG_ANMERKUNG, VERTRETUNG_ENTFALL, VERTRETUNG_DATUM}, null, null, null, null, VERTRETUNG_KLASSE);
        else
            cursor = database.query(TABLE_VERTRETUNG2, new String[]{VERTRETUNG_KLASSE, VERTRETUNG_STUNDE, VERTRETUNG_VERTRETER, VERTRETUNG_FACH, VERTRETUNG_RAUM, VERTRETUNG_LEHRER, VERTRETUNG_ANMERKUNG, VERTRETUNG_ENTFALL, VERTRETUNG_DATUM}, null, null, null, null, VERTRETUNG_KLASSE);
        VertretungsEvent[] vEvents = new VertretungsEvent[cursor.getCount()];
        int       i         = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            vEvents[i] = new VertretungsEvent(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),(cursor.getInt(7)>0), cursor.getString(8));
        }
        cursor.close();
        return vEvents;
    }

    public String gibDatum(int nr)  {
        Cursor cursor;
        if (nr == 1)    {
            cursor = database.query(TABLE_VERTRETUNG1, new String[]{VERTRETUNG_KLASSE, VERTRETUNG_STUNDE, VERTRETUNG_VERTRETER, VERTRETUNG_FACH, VERTRETUNG_RAUM, VERTRETUNG_LEHRER, VERTRETUNG_ANMERKUNG, VERTRETUNG_ENTFALL, VERTRETUNG_DATUM}, null, null, null, null, VERTRETUNG_KLASSE);
        }
        else {
            cursor = database.query(TABLE_VERTRETUNG2, new String[]{VERTRETUNG_KLASSE, VERTRETUNG_STUNDE, VERTRETUNG_VERTRETER, VERTRETUNG_FACH, VERTRETUNG_RAUM, VERTRETUNG_LEHRER, VERTRETUNG_ANMERKUNG, VERTRETUNG_ENTFALL, VERTRETUNG_DATUM}, null, null, null, null, VERTRETUNG_KLASSE);
        }
        cursor.moveToFirst();
        if (cursor.getCount()!=0)
            return cursor.getString(8);
        return "---";
    }


    public int getWochentagNr(int tagnr)    {
        if (gibDatum(tagnr).contains("Montag"))
            return 1;
        else if (gibDatum(tagnr).contains("Dienstag"))
            return 2;
        else if (gibDatum(tagnr).contains("Mittwoch"))
            return 3;
        else if (gibDatum(tagnr).contains("Donnerstag"))
            return 4;
        else if (gibDatum(tagnr).contains("Freitag"))
            return 5;
        return -1;
    }
}

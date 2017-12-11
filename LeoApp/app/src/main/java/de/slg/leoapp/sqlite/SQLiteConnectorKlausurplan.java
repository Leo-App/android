package de.slg.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slg.klausurplan.utility.Klausur;
import de.slg.leoapp.utility.Utils;

public class SQLiteConnectorKlausurplan extends SQLiteOpenHelper {
    public static final  String           DATABASE_NAME           = "klausurplan";
    private static final SimpleDateFormat dateFormat              = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
    private static final String           TABLE_KLAUSUREN         = "klausuren";
    private static final String           KLAUSUR_ID              = "id";
    private static final String           KLAUSUR_TITEL           = "title";
    private static final String           KLAUSUR_STUFE           = "stufe";
    private static final String           KLAUSUR_DATUM           = "datum";
    private static final String           KLAUSUR_NOTIZ           = "notiz";
    private static final String           KLAUSUR_IN_STUNDENPLAN  = "in_stundenplan";
    private static final String           KLAUSUR_HERUNTERGELADEN = "heruntergeladen";

    public static final String WHERE_ONLY_CREATED   = KLAUSUR_HERUNTERGELADEN + " = 0";
    public static final String WHERE_ONLY_GRADE     = WHERE_ONLY_CREATED + " OR (" + KLAUSUR_STUFE + " = '" + Utils.getUserStufe() + "' AND " + KLAUSUR_DATUM + " > '" + getMinDate() + "')";
    public static final String WHERE_ONLY_TIMETABLE = WHERE_ONLY_CREATED + " OR (" + KLAUSUR_STUFE + " = '" + Utils.getUserStufe() + "' AND " + KLAUSUR_IN_STUNDENPLAN + " = 1 AND " + KLAUSUR_DATUM + " > '" + getMinDate() + "')";

    private final SQLiteDatabase database;

    public SQLiteConnectorKlausurplan(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    private static String getMinDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -Utils.getController().getPreferences().getInt("pref_key_delete", 12));
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_KLAUSUREN + " (" +
                KLAUSUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KLAUSUR_TITEL + " TEXT NOT NULL, " +
                KLAUSUR_STUFE + " TEXT NOT NULL, " +
                KLAUSUR_DATUM + " TEXT NOT NULL, " +
                KLAUSUR_NOTIZ + " INTEGER NOT NULL, " +
                KLAUSUR_IN_STUNDENPLAN + " INTEGER NOT NULL, " +
                KLAUSUR_HERUNTERGELADEN + " INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KLAUSUREN);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
        database.close();
    }

    public long insert(String titel, String stufe, Date datum, String notiz, boolean inStundenplan, boolean heruntergeladen) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_TITEL, titel);
        values.put(KLAUSUR_STUFE, stufe);
        values.put(KLAUSUR_DATUM, dateFormat.format(datum));
        values.put(KLAUSUR_NOTIZ, notiz);
        values.put(KLAUSUR_IN_STUNDENPLAN, inStundenplan);
        values.put(KLAUSUR_HERUNTERGELADEN, heruntergeladen);
        return database.insert(TABLE_KLAUSUREN, null, values);
    }

    public void setTitel(long id, String titel) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_TITEL, titel);
        database.update(TABLE_KLAUSUREN, values, KLAUSUR_ID + " = " + id, null);
    }

    public void setDatum(long id, Date datum) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_DATUM, dateFormat.format(datum));
        database.update(TABLE_KLAUSUREN, values, KLAUSUR_ID + " = " + id, null);
    }

    public void setNotiz(long id, String notiz) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_NOTIZ, notiz);
        database.update(TABLE_KLAUSUREN, values, KLAUSUR_ID + " = " + id, null);
    }

    void updateStundenplan(String fach, boolean schriftlich) {
        ContentValues values = new ContentValues();
        values.put(KLAUSUR_IN_STUNDENPLAN, schriftlich);
        database.update(TABLE_KLAUSUREN, values, KLAUSUR_TITEL + " LIKE '" + fach + "%'", null);
    }

    public Klausur[] getExams(String where) {
        Utils.logDebug(where);
        Cursor    cursor    = database.query(TABLE_KLAUSUREN, new String[]{KLAUSUR_ID, KLAUSUR_TITEL, KLAUSUR_DATUM, KLAUSUR_NOTIZ}, where, null, null, null, KLAUSUR_DATUM);
        Klausur[] klausuren = new Klausur[cursor.getCount()];
        int       i         = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            klausuren[i] = new Klausur(cursor.getInt(0), cursor.getString(1), parse(cursor.getString(2)), cursor.getString(3));
        }
        cursor.close();
        return klausuren;
    }

    public Klausur getNextExam() {
        Cursor  cursor = database.query(TABLE_KLAUSUREN, new String[]{KLAUSUR_ID, KLAUSUR_TITEL, KLAUSUR_DATUM, KLAUSUR_NOTIZ}, KLAUSUR_DATUM + " = '" + getDate() + "' AND " + KLAUSUR_IN_STUNDENPLAN + " = 1 OR " + KLAUSUR_HERUNTERGELADEN + " = 0", null, null, null, KLAUSUR_DATUM, "1");
        Klausur k      = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            k = new Klausur(cursor.getInt(0), cursor.getString(1), parse(cursor.getString(2)), cursor.getString(3));
        }
        cursor.close();
        return k;
    }

    public void deleteAllDownloaded() {
        database.delete(TABLE_KLAUSUREN, KLAUSUR_HERUNTERGELADEN + " = 1", null);
    }

    public void delete(long id) {
        database.delete(TABLE_KLAUSUREN, KLAUSUR_ID + " = " + id, null);
    }

    private Date parse(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDate() {
        Calendar calendar = new GregorianCalendar();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 15) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateFormat.format(calendar.getTime());
    }
}
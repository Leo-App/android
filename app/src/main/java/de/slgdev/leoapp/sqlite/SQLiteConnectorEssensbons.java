package de.slgdev.leoapp.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.slgdev.leoapp.utility.Utils;

public class SQLiteConnectorEssensbons extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "foodmarks.db";

    public static final String TABLE_SCAN       = "Scans";
    public static final String TABLE_ORDERS     = "Userorders";
    public static final String TABLE_STATISTICS = "Statistics";

    private static final String SCAN_ID         = "id";
    public  static final String SCAN_CUSTOMERID = "userid";
    public  static final String SCAN_DATE       = "dateu";

    public static final String ORDER_ID    = "id";
    public static final String ORDER_DATE  = "dateu";
    public static final String ORDER_MENU  = "menu";
    public static final String ORDER_DESCR = "description";

    private static final String STATISTICS_ID        = "id";
    public  static final String STATISTICS_SYNCDATE  = "syncdate";
    public  static final String STATISTICS_AMOUNT    = "amount";
    public  static final String STATISTICS_LASTORDER = "lastorder";

    private final SQLiteDatabase database;

    public SQLiteConnectorEssensbons(Context context) {
        super(context, DATABASE_NAME, null, 4);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_ORDERS + " ("
                + ORDER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ORDER_DATE + " date NOT NULL, "
                + ORDER_MENU + " tinyint NOT NULL, "
                + ORDER_DESCR + " text NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_SCAN + " ("
                + SCAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + SCAN_DATE + " date NOT NULL, "
                + SCAN_CUSTOMERID + " tinyint NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_STATISTICS + " ("
                + STATISTICS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + STATISTICS_SYNCDATE + " date NOT NULL, "
                + STATISTICS_LASTORDER + " int NOT NULL, "
                + STATISTICS_AMOUNT + " int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s, %s, %s", TABLE_ORDERS, TABLE_SCAN, TABLE_STATISTICS));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean hasOrderedForToday() {
        Cursor cursor = database.rawQuery("SELECT MAX(ID) as id FROM " + TABLE_STATISTICS, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }

        cursor.moveToFirst();
        int maxid = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();

        cursor = database.rawQuery("SELECT o." + ORDER_DATE + " as date FROM "
                + TABLE_ORDERS + " o JOIN " + TABLE_STATISTICS + " s ON s." + STATISTICS_LASTORDER
                + " = o.ID WHERE s.ID = " + maxid, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }

        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex("date"));
        cursor.close();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

        try {
            Date dateD = df.parse(date);
            if (dateD.before(new Date()))
                return false;
        } catch (ParseException e) {
            Utils.logError(e);
        }

        return true;
    }

    @Override
    public void close() {
        database.close();
        super.close();
    }

}

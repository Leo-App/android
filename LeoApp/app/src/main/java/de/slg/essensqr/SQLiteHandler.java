package de.slg.essensqr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION        = 1;
    private static final String DATABASE_NAME           = "savebase.db";
    private static final String SQL_CREATE_TABLE_ORDERS = "CREATE TABLE IF NOT EXISTS "
            + OrderEntry.TABLE_NAME + " ("
            + OrderEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + OrderEntry.COLUMN_NAME_DATE + " date NOT NULL, "
            + OrderEntry.COLUMN_NAME_MENU + " tinyint NOT NULL, "
            + OrderEntry.COLUMN_NAME_DESCR + " text NOT NULL)";

    private static final String SQL_CREATE_TABLE_SCANS = "CREATE TABLE IF NOT EXISTS "
            + ScanEntry.TABLE_NAME + " ("
            + ScanEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + ScanEntry.COLUMN_NAME_DATE + " date NOT NULL, "
            + ScanEntry.COLUMN_NAME_CUSTOMERID + " tinyint NOT NULL)";

    private static final String SQL_CREATE_TABLE_STAT = "CREATE TABLE IF NOT EXISTS "
            + StatisticsEntry.TABLE_NAME + " ("
            + StatisticsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + StatisticsEntry.COLUMN_NAME_SYNCDATE + " date NOT NULL, "
            + StatisticsEntry.COLUMN_NAME_LASTORDER + " int NOT NULL, "
            + StatisticsEntry.COLUMN_NAME_AMOUNT + " int)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME + ", " + ScanEntry.TABLE_NAME + ", " + StatisticsEntry.TABLE_NAME;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ORDERS);
        db.execSQL(SQL_CREATE_TABLE_SCANS);
        db.execSQL(SQL_CREATE_TABLE_STAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static class ScanEntry implements BaseColumns {
        public static final String TABLE_NAME;
        public static final String COLUMN_NAME_CUSTOMERID;
        public static final String COLUMN_NAME_DATE;
        static final        String COLUMN_NAME_ID;

        static {
            TABLE_NAME = "SCANS";
            COLUMN_NAME_ID = "ID";
            COLUMN_NAME_CUSTOMERID = "USERID";
            COLUMN_NAME_DATE = "DATEU";
        }
    }

    public static class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME;
        public static final String COLUMN_NAME_DATE;
        public static final String COLUMN_NAME_MENU;
        public static final String COLUMN_NAME_DESCR;
        static final        String COLUMN_NAME_ID;

        static {
            TABLE_NAME = "USERORDERS";
            COLUMN_NAME_ID = "ID";
            COLUMN_NAME_DATE = "DATEU";
            COLUMN_NAME_MENU = "MENU";
            COLUMN_NAME_DESCR = "DESCR";
        }
    }

    public static class StatisticsEntry implements BaseColumns {
        public static final String TABLE_NAME;
        static final        String COLUMN_NAME_ID;
        static final        String COLUMN_NAME_SYNCDATE;
        static final        String COLUMN_NAME_AMOUNT;
        static final        String COLUMN_NAME_LASTORDER;

        static {
            TABLE_NAME = "STATISTICS";
            COLUMN_NAME_ID = "ID";
            COLUMN_NAME_SYNCDATE = "SYNCDATE";
            COLUMN_NAME_AMOUNT = "AMOUNT";
            COLUMN_NAME_LASTORDER = "LASTORDER";
        }
    }
}

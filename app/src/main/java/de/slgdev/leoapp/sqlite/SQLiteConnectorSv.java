package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 29.04.2018.
 */

public class SQLiteConnectorSv extends SQLiteOpenHelper {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    public static final String TABLE_LETTERBOX = "letterbox";
    public static final String LETTERBOX_TOPIC = "topic";
    public static final String LETTERBOX_PROPOSAL1 = "proposal1";
    public static final String LETTERBOX_PROPOSAL2 = "proposal2";
    public static final String LETTERBOX_CREATOR = "creator";
    public static final String DATABASE_NAME = "letterbox";
    public static final String LETTERBOX_DateOfCreation = "dateOfCreation";
    public static final String LETTERBOX_ANHANG = "anhang";

    private final SQLiteDatabase database;


    public SQLiteConnectorSv(Context context) {
        super(context, DATABASE_NAME, null, 7);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LETTERBOX + " (" +
                LETTERBOX_TOPIC + " TEXT NOT NULL, " +
                LETTERBOX_PROPOSAL1 + " TEXT NOT NULL, " +
                LETTERBOX_PROPOSAL2 + " TEXT NOT NULL, " +
                LETTERBOX_DateOfCreation + " TEXT NOT NULL, " +
                LETTERBOX_CREATOR + " TEXT NOT NULL " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LETTERBOX);
        onCreate(db);
    }

    public ContentValues getEntryContentValues(String topic, String proposal1, String proposal2, long dateOfCreation, String creator) {
        ContentValues values = new ContentValues();
        values.put(LETTERBOX_TOPIC, topic);
        values.put(LETTERBOX_PROPOSAL1, proposal1);
        values.put(LETTERBOX_PROPOSAL2, proposal2);
        values.put(LETTERBOX_DateOfCreation, dateOfCreation);
        values.put(LETTERBOX_CREATOR, creator);
        return values;
    }

    public long getLatestEntryDate(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_LETTERBOX, new String[]{LETTERBOX_DateOfCreation}, null, null, null, null, LETTERBOX_DateOfCreation + " DESC");
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

    @Override
    public synchronized void close(){
        super.close();
        database.close();
    }
}


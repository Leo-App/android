package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




/**
 * Created by sili- on 29.04.2018.
 *
 */

public class SQLiteConnectorSv extends SQLiteOpenHelper {

    public static final String TABLE_LETTERBOX = "letterbox";
    public static final String LETTERBOX_TOPIC = "topic";
    public static final String LETTERBOX_PROPOSAL1 = "proposal1";
    public static final String LETTERBOX_PROPOSAL2 = "proposal2";
    public static final String LETTERBOX_CREATOR = "creator";
    public static final String DATABASE_NAME = "letterbox";
    public static final String LETTERBOX_DateOfCreation = "dateOfCreation";
    public static final String LETTERBOX_LIKES = "likes";
    private static final String LETTERBOX_ANHANG = "anhang";


    private final SQLiteDatabase database;


    public SQLiteConnectorSv(Context context) {
        super(context, DATABASE_NAME, null, 18);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LETTERBOX + " (" +
                LETTERBOX_TOPIC + " TEXT NOT NULL, " +
                LETTERBOX_PROPOSAL1 + " TEXT NOT NULL, " +
                LETTERBOX_PROPOSAL2 + " TEXT NOT NULL, " +
                LETTERBOX_DateOfCreation + " TEXT NOT NULL, " +
                LETTERBOX_CREATOR + " TEXT NOT NULL, " +
                LETTERBOX_LIKES + " TEXT NOT NULL, " +
                LETTERBOX_ANHANG + " VARCHAR " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LETTERBOX);
        onCreate(db);
    }

    public ContentValues getEntryContentValues(String topic, String proposal1, String proposal2, String dateOfCreation, String creator, String likes) {
        ContentValues values = new ContentValues();
        values.put(LETTERBOX_TOPIC, topic);
        values.put(LETTERBOX_PROPOSAL1, proposal1);
        values.put(LETTERBOX_PROPOSAL2, proposal2);
        values.put(LETTERBOX_DateOfCreation, dateOfCreation);
        values.put(LETTERBOX_CREATOR, creator);
        values.put(LETTERBOX_LIKES, likes);
        return values;
    }

    @Override
    public synchronized void close(){
        super.close();
        database.close();
    }
}


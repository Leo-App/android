package de.slg.stundenplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

class StundenplanDBDummy extends SQLiteOpenHelper {
    private static final String DATABASE_NAME  = "stundenplan_dummy";
    private static final String TABLE_STUNDEN  = "stunden";
    private static final String STUNDEN_TAG    = "stag";
    private static final String STUNDEN_STUNDE = "sstunde";

    private final SQLiteDatabase database;

    StundenplanDBDummy(Context context) {
        super(context, DATABASE_NAME, null, 4);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STUNDEN + " (" +
                STUNDEN_TAG + " INTEGER NOT NULL, " +
                STUNDEN_STUNDE + " INTEGER NOT NULL, " +
                "PRIMARY KEY" +
                " (" + STUNDEN_TAG + ", " + STUNDEN_STUNDE + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUNDEN);
        onCreate(db);
    }

    void insertStunde(int tag, int stunde) {
        String selection = STUNDEN_TAG + " = " + tag + " AND " + STUNDEN_STUNDE + " = " + stunde;
        Cursor cursor    = database.query(TABLE_STUNDEN, new String[]{STUNDEN_STUNDE}, selection, null, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(STUNDEN_TAG, tag);
            values.put(STUNDEN_STUNDE, stunde);
            database.insert(TABLE_STUNDEN, null, values);
        }
        cursor.close();
    }

    String gibFreistundenZeiten() {
        StringBuilder builder = new StringBuilder();
        Cursor        cursor  = database.query(TABLE_STUNDEN, new String[]{STUNDEN_TAG, STUNDEN_STUNDE}, null, null, null, null, STUNDEN_TAG + ", " + STUNDEN_STUNDE);
        boolean[][]   woche   = new boolean[5][10];
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            woche[cursor.getInt(0) - 1][cursor.getInt(1) - 1] = true;
        }
        cursor.close();
        boolean[] tage = new boolean[5];
        for (int i = 0; i < woche.length; i++) {
            for (int j = 0; j < woche[i].length; j++) {
                woche[i][j] = !woche[i][j];
                if (woche[i][j])
                    tage[i] = true;
            }
        }
        for (int i = 0; i < tage.length; i++) {
            if (tage[i]) {
                builder.append(tagToString(i + 1))
                        .append(": ");
                for (int j = 0; j < 10; j++) {
                    if (woche[i][j]) {
                        builder.append(j + 1)
                                .append(", ");
                    }
                }
                builder.delete(builder.length() - 2, builder.length())
                        .append("\n");
            }
        }
        return builder.toString();
    }

    private String tagToString(int tag) {
        switch (tag) {
            case 1:
                return Utils.getString(R.string.montag);
            case 2:
                return Utils.getString(R.string.dienstag);
            case 3:
                return Utils.getString(R.string.mittwoch);
            case 4:
                return Utils.getString(R.string.donnerstag);
            case 5:
                return Utils.getString(R.string.freitag);
            default:
                return Utils.getString(R.string.montag);
        }
    }

    void clear() {
        database.delete(TABLE_STUNDEN, null, null);
    }
}
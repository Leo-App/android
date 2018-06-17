package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Benno on 30.05.2018.
 */

public class SQLiteConnectorNachhilfeboerse extends SQLiteOpenHelper {

    public static final  String TABLE_NACHHILFEBOERSE        = "NachhilfeBoerse";
    public static final  String NACHHILFE_VORNAME        = "vorname";
    public static final  String NACHHILFE_NACHNAME        = "nachname";
    public static final String DATABASE_NAME            ="nachhilfeBoerse.db";
    public static final String NACHHILFE_STUFE           = "stufe";
    public static final  String NACHHILFE_FAECHER       = "faecher";
    public static final String NACHHILFE_ANHANG         ="anhang";

    public SQLiteConnectorNachhilfeboerse(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NACHHILFEBOERSE + " (" +
                NACHHILFE_VORNAME + " TEXT NOT NULL, " +
                NACHHILFE_NACHNAME + " TEXT NOT NULL, " +
                NACHHILFE_STUFE + " TEXT NOT NULL, " +
                NACHHILFE_FAECHER + " TEXT NOT NULL, " +
                NACHHILFE_ANHANG + "VARCHAR" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NACHHILFEBOERSE);
        onCreate(db);
    }

    public ContentValues getEntryContentValues(String vorname, String nachname, String stufe , String faecher) {
        ContentValues values = new ContentValues();
        values.put(NACHHILFE_VORNAME, vorname);
        values.put(NACHHILFE_NACHNAME, nachname);
        values.put(NACHHILFE_STUFE, stufe);
        values.put(NACHHILFE_FAECHER, faecher);
        return values;
    }
}

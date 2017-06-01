package de.slg.messenger;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "messenger";
    public static final String TABLE_MESSAGES = "messages";
    public static final String MESSAGES_ID = "mid";
    public static final String MESSAGE_TEXT = "mtext";
    public static final String MESSAGE_DATE = "mdate";
    public static final String MESSAGE_READ = "mgelesen";
    public static final String TABLE_CHATS = "chats";
    public static final String CHAT_ID = "cid";
    public static final String CHAT_NAME = "cname";
    public static final String CHAT_TYPE = "ctype";
    public static final String TABLE_ASSOZIATION = "assoziation";
    public static final String ASSOZIATION_REMOVED = "aremoved";
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "uid";
    public static final String USER_NAME = "uname";
    public static final String USER_KLASSE = "uklasse";
    public static final String USER_PERMISSION = "upermission";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "Datenbank wird erstellt");
        try {
            db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" +
                    MESSAGES_ID + " INTEGER PRIMARY KEY, " +
                    MESSAGE_TEXT + " TEXT NOT NULL, " +
                    MESSAGE_DATE + " TEXT NOT NULL, " +
                    CHAT_ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    MESSAGE_READ + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL("CREATE TABLE " + TABLE_CHATS + " (" +
                    CHAT_ID + " INTEGER PRIMARY KEY, " +
                    CHAT_NAME + " TEXT NOT NULL, " +
                    CHAT_TYPE + " TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL("CREATE TABLE " + TABLE_ASSOZIATION + " (" +
                    CHAT_ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    ASSOZIATION_REMOVED + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_KLASSE + " TEXT, " +
                    USER_PERMISSION + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
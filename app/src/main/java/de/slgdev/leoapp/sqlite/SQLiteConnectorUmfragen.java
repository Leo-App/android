package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.slgdev.leoapp.utility.StringUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.umfragen.utility.Survey;

public class SQLiteConnectorUmfragen extends SQLiteOpenHelper {

    private static final String TABLE_SURVEYS = "Umfragen";
    private static final String TABLE_ANSWERS = "Antworten";

    private static final String SURVEYS_ID           = "id";
    private static final String SURVEYS_TITEL        = "titel";
    private static final String SURVEYS_ADRESSAT     = "adressat";
    private static final String SURVEYS_BESCHREIBUNG = "inhalt";
    private static final String SURVEYS_ABSENDER     = "absender";
    private static final String SURVEYS_REMOTE_ID    = "remoteid";
    private static final String SURVEYS_ERSTELLDATUM = "erstelldatum";
    private static final String SURVEYS_MULTIPLE     = "multiple";
    private static final String SURVEYS_ACCESSED     = "accessed";
    private static final String SURVEYS_VOTEABLE     = "voteable";
    private static final String ANSWERS_SID          = "umfrageid";
    private static final String ANSWERS_INHALT       = "inhalt";
    private static final String ANSWERS_REMOTE_ID    = "remoteid";
    private static final String ANSWERS_SELECTED     = "gewaehlt";
    private static final String ANSWERS_ID            = "id";
    private static final String DATABASE_NAME         = "surveys.db";

    private SQLiteDatabase database;

    public SQLiteConnectorUmfragen(Context c) {
        super(c, DATABASE_NAME, null, 9);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SURVEYS + " (" +
                SURVEYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SURVEYS_TITEL + " TEXT NOT NULL, " +
                SURVEYS_ADRESSAT + " TEXT NOT NULL, " +
                SURVEYS_ABSENDER + " TEXT NOT NULL, " +
                SURVEYS_REMOTE_ID + " INTEGER NOT NULL, " +
                SURVEYS_BESCHREIBUNG + " TEXT NOT NULL, " +
                SURVEYS_MULTIPLE + " TINYINT NOT NULL, " +
                SURVEYS_ERSTELLDATUM + " INTEGER NOT NULL, " +
                SURVEYS_ACCESSED + " INTEGER NOT NULL, " +
                SURVEYS_VOTEABLE + " TINYINT NOT NULL, " +
                "CONSTRAINT idswithupdate UNIQUE ("+ SURVEYS_REMOTE_ID + ", " + SURVEYS_ERSTELLDATUM +")" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ANSWERS + " (" +
                ANSWERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ANSWERS_SID + " INTEGER NOT NULL, " +
                ANSWERS_REMOTE_ID + " INTEGER NOT NULL, " +
                ANSWERS_SELECTED + " TINYINT NOT NULL, " +
                ANSWERS_INHALT + " TEXT NOT NULL" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public synchronized void close() {
        database.close();
        super.close();
    }

    public void deleteSurvey(int remoteID) {
        database.execSQL("DELETE FROM " + TABLE_SURVEYS + " WHERE " + SURVEYS_REMOTE_ID + " = " + remoteID);
        database.execSQL("DELETE FROM " + TABLE_ANSWERS + " WHERE " + ANSWERS_SID + " = (SELECT " + SURVEYS_ID + " FROM " + TABLE_SURVEYS + " WHERE " + SURVEYS_REMOTE_ID + " = " + remoteID + ")");
    }

    public void setAnswerSelected(int id) {
        database.execSQL("UPDATE " + TABLE_ANSWERS + " SET " + ANSWERS_SELECTED + " = 1" + " WHERE " + ANSWERS_REMOTE_ID + " = " + id);
    }

    private ContentValues getSurveyContentValues(String titel, String adressat, String beschreibung, String absender, short multiple, int remoteId, long erstelldatum, short voteable) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_TITEL, titel);
        values.put(SURVEYS_ADRESSAT, adressat);
        values.put(SURVEYS_ABSENDER, absender);
        values.put(SURVEYS_BESCHREIBUNG, beschreibung);
        values.put(SURVEYS_MULTIPLE, multiple);
        values.put(SURVEYS_VOTEABLE, voteable);
        values.put(SURVEYS_ERSTELLDATUM, erstelldatum);
        values.put(SURVEYS_REMOTE_ID, remoteId);
        values.put(SURVEYS_ACCESSED, 0);
        return values;
    }

    private ContentValues getAnswerContentValues(int id, String inhalt, long umfrageId, int selected) {
        ContentValues values = new ContentValues();
        values.put(ANSWERS_SID, umfrageId);
        values.put(ANSWERS_INHALT, inhalt);
        values.put(ANSWERS_REMOTE_ID, id);
        values.put(ANSWERS_SELECTED, selected);
        return values;
    }

    public long addSurvey(String survey, String answers) {
        String[] res = survey.split("_;_");

        boolean voteable = res[3].equals("Alle") || ((Utils.getUserStufe().equals("Q1")
                || Utils.getUserStufe().equals("Q2")
                || Utils.getUserStufe().equals("EF")) && res[3].equals("Sek II")) ||
                ((!Utils.getUserStufe().equals("Q1")
                        || !Utils.getUserStufe().equals("Q2")
                        || !Utils.getUserStufe().equals("EF")) && res[3].equals("Sek I")) ||
                res[3].equals(Utils.getUserStufe());

        long id = database.insertWithOnConflict(TABLE_SURVEYS, null, getSurveyContentValues(
                res[1],
                res[3],
                res[2],
                res[0],
                Short.parseShort(res[4]),
                Integer.parseInt(res[5]),
                Long.parseLong(res[6]+ "000"),
                voteable ? (short) 1 : (short) 0
        ), SQLiteDatabase.CONFLICT_IGNORE);

        if (id == -1) { //survey wasn't updated
            Cursor c = database.query(TABLE_SURVEYS, new String[]{SURVEYS_ID}, SURVEYS_REMOTE_ID + " = " + res[5], null, null, null, null);
            c.moveToFirst();
            int i =  c.getInt(0);
            c.close();
            return i;
        }

        for (int i = 7; i < res.length - 1; i += 2) {
            database.insert(TABLE_ANSWERS, null, getAnswerContentValues(
                    Integer.parseInt(res[i]),
                    res[i + 1],
                    id,
                    answers.contains(res[i]) ? 1 : 0
            ));
        }

        return id;
    }

    public void deleteAllSurveysExcept(List<Long> localIds) {

        if (localIds.isEmpty()) {
            database.execSQL("DELETE FROM " + TABLE_SURVEYS);
            return;
        }

        String insertValues = "(" + StringUtils.join("), (", localIds) + ")";

        database.execSQL("CREATE TEMPORARY TABLE tmp (ID INTEGER)");
        database.execSQL("INSERT INTO tmp VALUES " + insertValues);
        database.execSQL("DELETE FROM " + TABLE_SURVEYS + " WHERE " + SURVEYS_ID + " NOT IN " + "(SELECT ID FROM tmp)");
        database.execSQL("DELETE FROM " + TABLE_ANSWERS + " WHERE " + ANSWERS_SID + " NOT IN " + "(SELECT ID FROM tmp)");
    }

    public long getLatestSurveyDate(SQLiteDatabase db) {

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
                selection = SURVEYS_ADRESSAT
                        + " = '" + stufe +
                        "' OR "
                        + SURVEYS_ADRESSAT +
                        " = 'Sek II' OR " + SURVEYS_ADRESSAT +
                        " = 'Alle'";
                break;
            default:
                selection = SURVEYS_ADRESSAT +
                        " = '" + stufe.charAt(1) +
                        "' OR " +
                        SURVEYS_ADRESSAT +
                        " = 'Sek I' OR " +
                        SURVEYS_ADRESSAT +
                        " = 'Alle'";
                break;
        }

        Cursor cursor = db.query(TABLE_SURVEYS, new String[]{SURVEYS_ERSTELLDATUM}, selection, null, null, null, SURVEYS_ERSTELLDATUM + " DESC");
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

    public LinkedHashMap<Integer, Survey> getFilteredEntries(String stufe) {
        Cursor cursor;

        LinkedHashMap<Integer, Survey> entriesMap = new LinkedHashMap<>();

        switch (stufe) {
            case "":
            case "TEA":
                cursor = database.query(
                        TABLE_SURVEYS,
                        new String[]{
                                SURVEYS_ADRESSAT,
                                SURVEYS_TITEL,
                                SURVEYS_BESCHREIBUNG,
                                SURVEYS_ABSENDER,
                                SURVEYS_MULTIPLE,
                                SURVEYS_ID,
                                SURVEYS_REMOTE_ID,
                                SURVEYS_VOTEABLE
                        },
                        null,
                        null,
                        null,
                        null,
                        SURVEYS_ERSTELLDATUM + " DESC"
                );
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = database.query(
                        TABLE_SURVEYS,
                        new String[]{
                                SURVEYS_ADRESSAT,
                                SURVEYS_TITEL,
                                SURVEYS_BESCHREIBUNG,
                                SURVEYS_ABSENDER,
                                SURVEYS_MULTIPLE,
                                SURVEYS_ID,
                                SURVEYS_REMOTE_ID,
                                SURVEYS_VOTEABLE
                        },
                        SURVEYS_ADRESSAT +
                                " = '" + stufe + "'" +
                                " OR " + SURVEYS_ADRESSAT +
                                " = 'Sek II'" +
                                " OR " + SURVEYS_ADRESSAT +
                                " = 'Alle'" +
                                " OR " + SURVEYS_REMOTE_ID +
                                " = " + Utils.getUserID(),
                        null,
                        null,
                        null,
                        SURVEYS_ERSTELLDATUM + " DESC"
                );
                break;
            default:
                cursor = database.query(
                        TABLE_SURVEYS,
                        new String[]{
                                SURVEYS_ADRESSAT,
                                SURVEYS_TITEL,
                                SURVEYS_BESCHREIBUNG,
                                SURVEYS_ABSENDER,
                                SURVEYS_MULTIPLE,
                                SURVEYS_ID,
                                SURVEYS_REMOTE_ID,
                                SURVEYS_VOTEABLE
                        },
                        SURVEYS_ADRESSAT +
                                " = '" + stufe.charAt(1) + "'" +
                                " OR " + SURVEYS_ADRESSAT +
                                " = 'Sek I'" +
                                " OR " + SURVEYS_ADRESSAT +
                                " = 'Alle'" +
                                " OR " + SURVEYS_REMOTE_ID +
                                " = " + Utils.getUserID(),
                        null,
                        null,
                        null,
                        SURVEYS_ERSTELLDATUM + " DESC"
                );
                break;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            Cursor cursorAnswers = database.query(
                    TABLE_ANSWERS,
                    new String[]{
                            ANSWERS_INHALT,
                            ANSWERS_REMOTE_ID,
                            ANSWERS_SELECTED
                    },
                    ANSWERS_SID +
                            " = " + cursor.getInt(5),
                    null,
                    null,
                    null,
                    ANSWERS_REMOTE_ID + " ASC"
            );

            ArrayList<String> answers = new ArrayList<>();

            boolean voted = false;

            for (cursorAnswers.moveToFirst(); !cursorAnswers.isAfterLast(); cursorAnswers.moveToNext()) {
                answers.add(
                        cursorAnswers.getString(0) + "_;_"
                                + cursorAnswers.getString(1) + "_;_"
                                + cursorAnswers.getInt(2)
                );
                voted = voted || cursorAnswers.getInt(2) == 1;
            }

            cursorAnswers.close();

            Survey s = new Survey(
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(4) != 0,
                    voted || cursor.getInt(7) == 0,
                    cursor.getString(0),
                    answers
            );

            entriesMap.put(cursor.getInt(6), s);
        }

        cursor.close();

        return entriesMap;
    }

    public String getSurveyWithId(int id) {
        Cursor c = database.query(TABLE_SURVEYS, new String[]{SURVEYS_TITEL}, SURVEYS_REMOTE_ID + " = " + id, null, null, null, null);

        c.moveToFirst();
        String returnS = c.getCount() == 0 ? null : c.getString(0);
        c.close();

        return returnS;
    }

    public boolean getDatabaseAvailable() {
        File dbFile = Utils.getContext().getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean getViewed(int remoteid) {
        Cursor c = database.query(
                TABLE_SURVEYS,
                new String[]{SURVEYS_ACCESSED},
                SURVEYS_REMOTE_ID + " = " + remoteid,
                null,
                null,
                null,
                null
        );

        c.moveToFirst();
        int viewed = c.getInt(c.getColumnIndex(SURVEYS_ACCESSED));
        c.close();

        return viewed == 1;
    }

    public void setViewed(int remoteid) {
        ContentValues values = new ContentValues();
        values.put(SURVEYS_ACCESSED, 1);
        database.update(TABLE_SURVEYS, values, SURVEYS_REMOTE_ID + " = " + remoteid, null);
    }

}

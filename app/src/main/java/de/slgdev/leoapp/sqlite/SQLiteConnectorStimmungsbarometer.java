package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.stimmungsbarometer.utility.Ergebnis;

public class SQLiteConnectorStimmungsbarometer extends SQLiteOpenHelper {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    private static final String DATABASE_NAME = "Stimmungsbarometer";

    private static final String TABLE_ERGEBNISSE = "ergebnisse";

    private static final String ERGEBNIS_WERT        = "value";
    private static final String ERGEBNIS_ICH         = "ich";
    private static final String ERGEBNIS_SCHUELER    = "schueler";
    private static final String ERGEBNIS_LEHRER      = "lehrer";
    private static final String ERGEBNIS_ALLE        = "alle";
    private static final String ERGEBNIS_DATUM       = "datum";
    private static final String ERGEBNIS_DATUM_JAHR  = "datum_jahr";
    private static final String ERGEBNIS_DATUM_MONAT = "datum_monat";
    private static final String ERGEBNIS_DATUM_TAG   = "datum_tag";

    private final SQLiteDatabase database;

    public SQLiteConnectorStimmungsbarometer(Context context) {
        super(context, DATABASE_NAME, null, 4);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ERGEBNISSE + " ("
                + ERGEBNIS_WERT + " INTEGER NOT NULL, "
                + ERGEBNIS_DATUM + " TEXT NOT NULL, "
                + ERGEBNIS_DATUM_JAHR + " TEXT NOT NULL, "
                + ERGEBNIS_DATUM_MONAT + " TEXT NOT NULL, "
                + ERGEBNIS_DATUM_TAG + " TEXT NOT NULL, "
                + ERGEBNIS_ICH + " INTEGER NOT NULL, "
                + ERGEBNIS_SCHUELER + " INTEGER NOT NULL, "
                + ERGEBNIS_LEHRER + " INTEGER NOT NULL, "
                + ERGEBNIS_ALLE + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ERGEBNISSE);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
        database.close();
    }

    public void insert(Ergebnis ergebnis) {
        String date = dateFormat.format(ergebnis.date);

        Cursor cursor = database.query(
                TABLE_ERGEBNISSE,
                new String[]{ERGEBNIS_WERT},
                ERGEBNIS_DATUM + " = '" + date + "' AND "
                        + ERGEBNIS_ICH + " = " + (ergebnis.ich ? 1 : 0) + " AND "
                        + ERGEBNIS_SCHUELER + " = " + (ergebnis.schueler ? 1 : 0) + " AND "
                        + ERGEBNIS_LEHRER + " = " + (ergebnis.lehrer ? 1 : 0) + " AND "
                        + ERGEBNIS_ALLE + " = " + (ergebnis.alle ? 1 : 0),
                null,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put(ERGEBNIS_WERT, ergebnis.value);
            database.update(
                    TABLE_ERGEBNISSE,
                    values,
                    ERGEBNIS_DATUM + " = '" + date + "' AND "
                            + ERGEBNIS_ICH + " = " + (ergebnis.ich ? 1 : 0) + " AND "
                            + ERGEBNIS_SCHUELER + " = " + (ergebnis.schueler ? 1 : 0) + " AND "
                            + ERGEBNIS_LEHRER + " = " + (ergebnis.lehrer ? 1 : 0) + " AND "
                            + ERGEBNIS_ALLE + " = " + (ergebnis.alle ? 1 : 0),
                    null
            );
        } else {
            ContentValues values = new ContentValues();
            values.put(ERGEBNIS_WERT, ergebnis.value);
            values.put(ERGEBNIS_DATUM, date);
            values.put(ERGEBNIS_DATUM_JAHR, date.substring(0, 4));
            values.put(ERGEBNIS_DATUM_MONAT, date.substring(5, 7));
            values.put(ERGEBNIS_DATUM_TAG, date.substring(8, 10));
            values.put(ERGEBNIS_DATUM, date);
            values.put(ERGEBNIS_ICH, ergebnis.ich);
            values.put(ERGEBNIS_SCHUELER, ergebnis.schueler);
            values.put(ERGEBNIS_LEHRER, ergebnis.lehrer);
            values.put(ERGEBNIS_ALLE, ergebnis.alle);

            database.insert(TABLE_ERGEBNISSE, null, values);
        }

        cursor.close();
    }

    public Ergebnis[][] getData(int zeitraum) {
        String[] columns = new String[]{ERGEBNIS_WERT, ERGEBNIS_DATUM};
        String   where   = ERGEBNIS_DATUM + " > '" + getDate(zeitraum) + "'";
        String   groupBy = null;
        if (zeitraum == 2) {
            groupBy = ERGEBNIS_DATUM_MONAT + ", " + ERGEBNIS_DATUM_JAHR;
            columns = new String[]{"AVG(" + ERGEBNIS_WERT + ")", ERGEBNIS_DATUM_JAHR, ERGEBNIS_DATUM_MONAT};
        }

        Cursor cursorIch = database.query(
                TABLE_ERGEBNISSE,
                columns,
                where + " AND " + ERGEBNIS_ICH + " = 1",
                null,
                groupBy,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorSchueler = database.query(
                TABLE_ERGEBNISSE,
                columns,
                where + " AND " + ERGEBNIS_SCHUELER + " = 1",
                null,
                groupBy,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorLehrer = database.query(
                TABLE_ERGEBNISSE,
                columns,
                where + " AND " + ERGEBNIS_LEHRER + " = 1",
                null,
                groupBy,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorAlle = database.query(
                TABLE_ERGEBNISSE,
                columns,
                where + " AND " + ERGEBNIS_ALLE + " = 1",
                null,
                groupBy,
                null,
                ERGEBNIS_DATUM + " DESC"
        );

        List<Ergebnis> ich      = new List<>();
        List<Ergebnis> schueler = new List<>();
        List<Ergebnis> lehrer   = new List<>();
        List<Ergebnis> alle     = new List<>();

        for (cursorIch.moveToFirst(); !cursorIch.isAfterLast(); cursorIch.moveToNext()) {
            if (zeitraum == 2) {
                ich.append(
                        new Ergebnis(
                                parse(cursorIch.getString(1) + '-' + cursorIch.getString(2) + "-01"),
                                cursorIch.getDouble(0),
                                true,
                                false,
                                false,
                                false
                        )
                );
            } else {
                ich.append(
                        new Ergebnis(
                                parse(cursorIch.getString(1)),
                                cursorIch.getDouble(0),
                                true,
                                false,
                                false,
                                false
                        )
                );
            }
        }
        cursorIch.close();

        for (cursorSchueler.moveToFirst(); !cursorSchueler.isAfterLast(); cursorSchueler.moveToNext()) {
            if (zeitraum == 2) {
                schueler.append(
                        new Ergebnis(
                                parse(cursorSchueler.getString(1) + '-' + cursorSchueler.getString(2) + "-01"),
                                cursorSchueler.getDouble(0),
                                false,
                                true,
                                false,
                                false
                        )
                );
            } else {
                schueler.append(
                        new Ergebnis(
                                parse(cursorSchueler.getString(1)),
                                cursorSchueler.getDouble(0),
                                false,
                                true,
                                false,
                                false
                        )
                );
            }
        }
        cursorSchueler.close();

        for (cursorLehrer.moveToFirst(); !cursorLehrer.isAfterLast(); cursorLehrer.moveToNext()) {
            if (zeitraum == 2) {
                lehrer.append(
                        new Ergebnis(
                                parse(cursorLehrer.getString(1) + '-' + cursorLehrer.getString(2) + "-01"),
                                cursorLehrer.getDouble(0),
                                false,
                                false,
                                true,
                                false
                        )
                );
            } else {
                lehrer.append(
                        new Ergebnis(
                                parse(cursorLehrer.getString(1)),
                                cursorLehrer.getDouble(0),
                                false,
                                false,
                                true,
                                false
                        )
                );
            }
        }
        cursorLehrer.close();

        for (cursorAlle.moveToFirst(); !cursorAlle.isAfterLast(); cursorAlle.moveToNext()) {
            if (zeitraum == 2) {
                alle.append(
                        new Ergebnis(
                                parse(cursorAlle.getString(1) + '-' + cursorAlle.getString(2) + "-01"),
                                cursorAlle.getDouble(0),
                                false,
                                false,
                                false,
                                true
                        )
                );
            } else {
                alle.append(
                        new Ergebnis(
                                parse(cursorAlle.getString(1)),
                                cursorAlle.getDouble(0),
                                false,
                                false,
                                false,
                                true
                        )
                );
            }
        }
        cursorAlle.close();

        Date last;
        if (zeitraum == 3) {
            last = alle.toLast().getContent().date;
        } else {
            last = parse(getDate(zeitraum));
        }

        ich.toFirst();
        schueler.toFirst();
        lehrer.toFirst();
        alle.toFirst();

        int field  = Calendar.DAY_OF_YEAR;
        int amount = -1;
        if (zeitraum == 2) {
            field = Calendar.MONTH;
        }

        for (Calendar c = new GregorianCalendar(); c.getTime().after(last); c.add(field, amount)) {
            if (zeitraum == 2) {
                c.set(Calendar.DAY_OF_MONTH, 1);
            }
            if (ich.hasAccess()) {
                if (!equals(c.getTime(), ich.getContent().date)) {
                    ich.insertBefore(
                            new Ergebnis(
                                    c.getTime(),
                                    -1,
                                    true,
                                    false,
                                    false,
                                    false
                            )
                    );
                } else {
                    ich.next();
                }
            } else {
                ich.append(
                        new Ergebnis(
                                c.getTime(),
                                -1,
                                true,
                                false,
                                false,
                                false
                        )
                );
            }

            if (schueler.hasAccess()) {
                if (!equals(c.getTime(), schueler.getContent().date)) {
                    schueler.insertBefore(
                            new Ergebnis(
                                    c.getTime(),
                                    -1,
                                    false,
                                    true,
                                    false,
                                    false
                            )
                    );
                } else {
                    schueler.next();
                }
            } else {
                schueler.append(
                        new Ergebnis(
                                c.getTime(),
                                -1,
                                false,
                                true,
                                false,
                                false
                        )
                );
            }

            if (lehrer.hasAccess()) {
                if (!equals(c.getTime(), lehrer.getContent().date)) {
                    lehrer.insertBefore(
                            new Ergebnis(
                                    c.getTime(),
                                    -1,
                                    false,
                                    false,
                                    true,
                                    false
                            )
                    );
                } else {
                    lehrer.next();
                }
            } else {
                lehrer.append(
                        new Ergebnis(
                                c.getTime(),
                                -1,
                                false,
                                false,
                                true,
                                false
                        )
                );
            }

            if (alle.hasAccess()) {
                if (!equals(c.getTime(), alle.getContent().date)) {
                    alle.insertBefore(
                            new Ergebnis(
                                    c.getTime(),
                                    -1,
                                    false,
                                    false,
                                    false,
                                    true
                            )
                    );
                } else {
                    alle.next();
                }
            } else {
                alle.append(
                        new Ergebnis(
                                c.getTime(),
                                -1,
                                false,
                                false,
                                false,
                                true
                        )
                );
            }
        }

        Ergebnis[][] ergebnis = new Ergebnis[4][];

        ergebnis[0] = ich.fill(new Ergebnis[ich.size()]);
        ergebnis[1] = schueler.fill(new Ergebnis[schueler.size()]);
        ergebnis[2] = lehrer.fill(new Ergebnis[lehrer.size()]);
        ergebnis[3] = alle.fill(new Ergebnis[alle.size()]);

        return ergebnis;
    }

    public Ergebnis[] getAverage() {
        Cursor cursorIch = database.query(
                TABLE_ERGEBNISSE,
                new String[]{"AVG(" + ERGEBNIS_WERT + ")"},
                ERGEBNIS_ICH + " = 1",
                null,
                null,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorSchueler = database.query(
                TABLE_ERGEBNISSE,
                new String[]{"AVG(" + ERGEBNIS_WERT + ")"},
                ERGEBNIS_SCHUELER + " = 1",
                null,
                null,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorLehrer = database.query(
                TABLE_ERGEBNISSE,
                new String[]{"AVG(" + ERGEBNIS_WERT + ")"},
                ERGEBNIS_LEHRER + " = 1",
                null,
                null,
                null,
                ERGEBNIS_DATUM + " DESC"
        );
        Cursor cursorAlle = database.query(
                TABLE_ERGEBNISSE,
                new String[]{"AVG(" + ERGEBNIS_WERT + ")"},
                ERGEBNIS_ALLE + " = 1",
                null,
                null,
                null,
                ERGEBNIS_DATUM + " DESC"
        );

        Ergebnis[] ergebnis = new Ergebnis[4];

        cursorIch.moveToFirst();
        ergebnis[0] = new Ergebnis(
                null,
                cursorIch.getDouble(0),
                true,
                false,
                false,
                false
        );
        cursorIch.close();

        cursorSchueler.moveToFirst();
        ergebnis[1] = new Ergebnis(
                null,
                cursorSchueler.getDouble(0),
                false,
                true,
                false,
                false
        );
        cursorSchueler.close();

        cursorLehrer.moveToFirst();
        ergebnis[2] = new Ergebnis(
                null,
                cursorLehrer.getDouble(0),
                false,
                false,
                true,
                false
        );
        cursorLehrer.close();

        cursorAlle.moveToFirst();
        ergebnis[3] = new Ergebnis(
                null,
                cursorAlle.getDouble(0),
                false,
                false,
                false,
                true
        );
        cursorAlle.close();

        return ergebnis;
    }

    private String getDate(int zeitraum) {
        Calendar calendar = new GregorianCalendar();
        switch (zeitraum) {
            case 0:
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                break;
            case 1:
                calendar.add(Calendar.DAY_OF_YEAR, -29);
                break;
            case 2:
                calendar.add(Calendar.DAY_OF_YEAR, -364);
                break;
            default:
                return "0000-00-00";
        }
        return dateFormat.format(calendar.getTime());
    }

    private Date parse(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Throwable e) {
            Utils.logError(e);
            Utils.logError(date);
            return null;
        }
    }

    private boolean equals(Date d1, Date d2) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }
}
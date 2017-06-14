package de.slg.essensqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class SQLitePrinter {

    public static void printDatabase(Context c) {

        new SQLitePrinter().new checkTask(c).execute();

    }

    private final class checkTask extends AsyncTask<Void, Void, Void> {

        private Context c;

        checkTask(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SQLiteDatabase readable = new SQLiteHandler(c).getReadableDatabase();

            Cursor c = readable.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            Cursor current;

            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {

                    String table = c.getString(0);

                    c.moveToNext();

                    if (table.equals("android_metadata") || table.equals("sqlite_sequence"))
                        continue;

                    Log.d("SQLITE", "Table: " + table);

                    current = readable.rawQuery("SELECT * FROM " + table, null);

                    if (current.moveToFirst()) {
                        while (!current.isAfterLast()) {

                            StringBuilder printStatement = new StringBuilder();

                            for (int i = 0; i < current.getColumnCount(); i++) {

                                int dataType = current.getType(i);

                                switch (dataType) {

                                    case Cursor.FIELD_TYPE_INTEGER:
                                        printStatement.append(String.valueOf(current.getInt(i))).append("\t");
                                        break;
                                    case Cursor.FIELD_TYPE_FLOAT:
                                        printStatement.append(String.valueOf(current.getFloat(i))).append("\t");
                                        break;
                                    case Cursor.FIELD_TYPE_STRING:
                                        printStatement.append(current.getString(i)).append("\t");
                                        break;
                                    case Cursor.FIELD_TYPE_BLOB:
                                        printStatement.append("blob\t");
                                        break;
                                    default:
                                        printStatement.append("-\t");
                                        break;


                                }

                            }

                            Log.d("SQLITE", printStatement.toString());

                            current.moveToNext();
                        }
                    }

                    current.close();
                }
            }

            c.close();
            return null;
        }

    }

}

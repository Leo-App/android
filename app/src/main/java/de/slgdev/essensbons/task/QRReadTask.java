package de.slgdev.essensbons.task;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.slgdev.leoapp.sqlite.SQLiteConnectorEssensbons;
import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;

public class QRReadTask extends ObjectCallbackTask<Boolean> {

    private int orderedMenu;
    private SQLiteDatabase dbh;

    public QRReadTask() {
        SQLiteConnectorEssensbons db = new SQLiteConnectorEssensbons(Utils.getContext());
        dbh = db.getWritableDatabase();
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        String scanResult = (String) params[0];

        if (checkValid(scanResult)) {

            Utils.logDebug(scanResult);

            String[] parts = scanResult.split("-");
            String day = parts[2].substring(0, 2);
            String month = parts[2].substring(2, 4);
            String year = parts[2].substring(4, 7);

            orderedMenu = Integer.parseInt(String.valueOf(scanResult.charAt(8)));

            String[] projection = {
                    SQLiteConnectorEssensbons.SCAN_DATE,
            };

            String selection = SQLiteConnectorEssensbons.SCAN_DATE + " = ? AND " + SQLiteConnectorEssensbons.SCAN_CUSTOMERID + " = ?";
            String[] selectionArgs = {"2" + year + "-" + month + "-" + day, parts[0]};

            Cursor cursor = dbh.query(
                    SQLiteConnectorEssensbons.TABLE_SCAN,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.getCount() == 0) {
                cursor.close();
                ContentValues values = new ContentValues();
                values.put(SQLiteConnectorEssensbons.SCAN_CUSTOMERID, scanResult.split("-")[0]);
                values.put(SQLiteConnectorEssensbons.SCAN_DATE, "2" + year + "-" + month + "-" + day);
                dbh.insert(SQLiteConnectorEssensbons.TABLE_SCAN, null, values);
                return true;
            } else {
                cursor.close();
                return false;
            }

        } else {
            return false;
        }

    }

    private boolean checkValid(String s) {
        String[] parts = s.split("-");

        Utils.logDebug(s);
        Utils.logDebug("passed no test yet");


        if (parts.length != 4)
            return false;
        Utils.logDebug("passed module test");

        if (parts[1].length() != 2 || parts[1].charAt(0) != 'M' || (parts[1].charAt(1) != '1' && parts[1].charAt(1) != '2'))
            return false;
        Utils.logDebug("passed menu-format test");

        if (parts[2].length() != 7)
            return false;
        Utils.logDebug("passed date size test");

        try {
            int day = Integer.parseInt(parts[2].substring(0, 2));
            int month = Integer.parseInt(parts[2].substring(2, 4));

            Calendar c = Calendar.getInstance();

            Utils.logDebug("QRDay: "+day);
            Utils.logDebug("QRMonth: "+month);


            Utils.logDebug("CDay: "+c.get(Calendar.DAY_OF_MONTH));
            Utils.logDebug("CMonth: "+c.get(Calendar.MONTH));

            if (c.get(Calendar.DAY_OF_MONTH) != day || c.get(Calendar.MONTH)+1 != month)
                return false;

        } catch (NumberFormatException e) {
            return false;
        }
        Utils.logDebug("passed logic date test");

        try {
            int orderId = Integer.parseInt(parts[0]);
            int checksum = Integer.parseInt(parts[2]) + orderId;

            int mod = checksum % 97;
            int fin = 98 - mod;

            if (!String.format(Locale.GERMANY, "%02d", fin).equals(parts[3]))
                return false;

        } catch (NumberFormatException e) {
            return false;
        }
        Utils.logDebug("passed checksum test");

        return true;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    protected void onPostExecute(Boolean result) {
        dbh.close();

        for (TaskStatusListener listener : getListeners()) {
            listener.taskFinished(result, orderedMenu);
        }
    }
}
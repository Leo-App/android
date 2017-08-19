package de.slg.schwarzes_brett;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.SQLitePrinter;
import de.slg.leoapp.Utils;

class UpdateViewTrackerTask extends AsyncTask<Integer, Void, Void> {

    @Override
    protected Void doInBackground(Integer... params) {

        int remote, eintragid = params[0];
        Cursor result = null;

        SQLiteConnector db = new SQLiteConnector(Utils.context);
        SQLiteDatabase dbh = db.getReadableDatabase();

        try {
            result = dbh.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_REMOTE_ID + " FROM " + SQLiteConnector.TABLE_EINTRAEGE +" WHERE " + SQLiteConnector.EINTRAEGE_ID + " = " + eintragid+1, null);
            result.moveToFirst();
            remote = result.getInt(result.getColumnIndexOrThrow(SQLiteConnector.EINTRAEGE_REMOTE_ID));
            URL updateURL = new URL("http://www.moritz.liegmanns.de/updateViewTracker.php?key=5453&remote="+remote);
            updateURL.openConnection().getInputStream();
            result.close();
        } catch (SQLiteException | IOException e) {
            e.printStackTrace();
        } finally {
            result.close();
        }

        return null;
    }

}

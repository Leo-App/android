package de.slgdev.svBriefkasten.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 29.04.2018.
 */

public class SyncTopicTask extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        if (Utils.isNetworkAvailable()) {
            try {
                URLConnection connection = new URL("http://www.moritz.liegmanns.de/leoapp_php/svBriefkasten/sync.php")
                        .openConnection();

                Utils.logError(connection);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null)
                    builder.append(line)
                            .append(System.getProperty("line.separator"));
                reader.close();
                SQLiteConnectorSv db  = new SQLiteConnectorSv(Utils.getContext());
                SQLiteDatabase dbh = db.getWritableDatabase();
                //dbh.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + SQLiteConnectorSv.TABLE_LETTERBOX + "'");
                dbh.delete(SQLiteConnectorSv.TABLE_LETTERBOX, null, null);
                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    String[] res = s.split(";");
                    if (res.length == 5) {
                        dbh.insert(SQLiteConnectorSv.TABLE_LETTERBOX, null, db.getEntryContentValues(
                                res[0],
                                res[1],
                                res[2],
                                Long.parseLong(res[3] + "000"),
                                res[4]
                        ));
                    }
                }
                dbh.close();
                db.close();
            } catch (IOException e) {
                Utils.logError(e);
                return null;
            }
        }
        return null;
    }
    }


package de.slgdev.nachhilfeboerse.task;

/**
 * Created by Benno on 30.05.2018.
 */

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.sqlite.SQLiteConnectorNachhilfeboerse;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;
//package de.slgdev.svBriefkasten.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.sqlite.SQLiteConnectorNachhilfeboerse;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 29.04.2018.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class SyncMaster extends VoidCallbackTask<Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        if (Utils.isNetworkAvailable()) {
            try {
                URLConnection connection = new URL("http://www.moritz.liegmanns.de/leoapp_php/NachhilfeBoerse/sync.php")
                        .openConnection();

                Utils.logError(connection);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                reader.close();
                Utils.logDebug(builder);

                SQLiteConnectorNachhilfeboerse db  = new SQLiteConnectorNachhilfeboerse(Utils.getContext());
                SQLiteDatabase dbh = db.getWritableDatabase();
                dbh.delete(SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE, null, null);
                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    Utils.logDebug("current = " + s);
                    if (s.length() > 0) {
                        String[] res = s.split(";");
                        dbh.insert(SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE, null, db.getEntryContentValues(
                                res[0],
                                res[1],
                                res[2],
                                res[3]
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


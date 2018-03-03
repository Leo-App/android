package de.slgdev.leoapp.sync;

import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.utility.Utils;

/**
 * NewsSynchronizer.
 *
 * Klasse zum Verwalten des News-Synchronisationstasks.
 *
 * @author Gianni
 * @since 0.6.8
 * @version 2017.0712
 */
public class NewsSynchronizer implements Synchronizer {

    @Override
    public boolean run() {

        if(!Utils.isNetworkAvailable())
            return false;

        try {
            URL updateURL = new URL(Utils.BASE_URL_PHP + "schwarzesBrett/meldungen.php");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null)
                builder.append(line)
                        .append(System.getProperty("line.separator"));
            reader.close();

            SQLiteConnectorSchwarzesBrett db  = new SQLiteConnectorSchwarzesBrett(Utils.getContext());
            SQLiteDatabase                dbh = db.getWritableDatabase();

            dbh.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE + "'");
            dbh.delete(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, null, null);
            String[] result = builder.toString().split("_next_");
            for (String s : result) {
                String[] res = s.split(";");
                if (res.length == 8) {
                    dbh.insert(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, null, db.getEntryContentValues(
                            res[0],
                            res[1],
                            res[2],
                            Long.parseLong(res[3] + "000"),
                            Long.parseLong(res[4] + "000"),
                            Integer.parseInt(res[5]),
                            Integer.parseInt(res[6]),
                            res[7]
                    ));
                }
            }
            dbh.close();
            db.close();
        } catch (IOException e) {
            Utils.logError(e);
            return false;
        }
        return true;
    }

    @Override
    public void postUpdate() {
        new NotificationHandler.SchwarzesBrettNotification().send();
    }

}
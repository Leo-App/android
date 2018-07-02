package de.slgdev.leoapp.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.utility.NetworkUtils;
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

        if (!NetworkUtils.isNetworkAvailable())
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

            String[] result = builder.toString().split("_next_");
            for (String s : result) {
                String[] parts = s.split(";");
                if (parts.length == 8)
                    db.insertEntry(parts);
            }
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
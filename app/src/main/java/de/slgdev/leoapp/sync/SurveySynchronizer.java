package de.slgdev.leoapp.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.utility.Utils;

/**
 * SurveySynchronizer.
 *
 * Klasse zum Verwalten des Umfrage-Synchronisationstasks.
 *
 * @author Gianni
 * @since 0.6.8
 * @version 2017.0712
 */

public class SurveySynchronizer implements Synchronizer {

    @Override
    public boolean run() {

        if (!Utils.isNetworkAvailable())
            return false;

        try {
            URL updateURL = new URL(Utils.BASE_URL_PHP + "survey/getSurveys.php");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            reader.close();

            URL resultURL = new URL(Utils.BASE_URL_PHP + "survey/getSingleResult.php?user=" + Utils.getUserID());
            reader =
                    new BufferedReader(
                            new InputStreamReader(resultURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder resultBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                resultBuilder.append(line);
            reader.close();

            SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());

            String[] result = builder.toString().split("_next_");
            for (String s : result) {
                db.addSurvey(s, resultBuilder.toString());
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
        new NotificationHandler.UmfrageNotification().send();
    }
}
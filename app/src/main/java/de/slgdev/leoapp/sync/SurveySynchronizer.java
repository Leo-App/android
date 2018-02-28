package de.slgdev.leoapp.sync;

import android.database.sqlite.SQLiteDatabase;

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

        if(!Utils.checkNetwork())
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
            SQLiteDatabase          dbh = db.getWritableDatabase();

            dbh.delete(SQLiteConnectorUmfragen.TABLE_SURVEYS, null, null);
            dbh.delete(SQLiteConnectorUmfragen.TABLE_ANSWERS, null, null);

            String[] result = builder.toString().split("_next_");
            for (String s : result) {
                String[] res = s.split("_;_");
                if (res.length >= 7) {

                    boolean voteable = res[3].equals("Alle") || ((Utils.getUserStufe().equals("Q1")
                            || Utils.getUserStufe().equals("Q2")
                            || Utils.getUserStufe().equals("EF")) && res[3].equals("Sek II")) ||
                            ((!Utils.getUserStufe().equals("Q1")
                            || !Utils.getUserStufe().equals("Q2")
                            || !Utils.getUserStufe().equals("EF")) && res[3].equals("Sek I")) ||
                            res[3].equals(Utils.getUserStufe());

                    long id = dbh.insert(SQLiteConnectorUmfragen.TABLE_SURVEYS, null, db.getSurveyContentValues(
                            res[1],
                            res[3],
                            res[2],
                            res[0],
                            Short.parseShort(res[4]),
                            Integer.parseInt(res[5]),
                            Long.parseLong(res[6] + "000"),
                            voteable ? (short) 1 : (short) 0
                    ));

                    for (int i = 7; i < res.length - 1; i += 2) {
                        dbh.insert(SQLiteConnectorUmfragen.TABLE_ANSWERS, null, db.getAnswerContentValues(
                                Integer.parseInt(res[i]),
                                res[i + 1],
                                id,
                                resultBuilder.toString().contains(res[i]) ? 1 : 0
                        ));
                    }
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
        new NotificationHandler.UmfrageNotification().send();
    }
}
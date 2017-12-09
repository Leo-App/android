package de.slg.leoapp.sync;

import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.sqlite.SQLiteConnectorSurvey;
import de.slg.leoapp.utility.Utils;

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
            URL updateURL = new URL(Utils.DOMAIN_DEV + "survey/getSurveys.php");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            reader.close();

            URL resultURL = new URL(Utils.DOMAIN_DEV + "survey/getSingleResult.php?user=" + Utils.getUserID());
            reader =
                    new BufferedReader(
                            new InputStreamReader(resultURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder resultBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                resultBuilder.append(line);
            reader.close();

            SQLiteConnectorSurvey db = new SQLiteConnectorSurvey(Utils.getContext());
            SQLiteDatabase dbh       = db.getWritableDatabase();

            dbh.delete(SQLiteConnectorSurvey.TABLE_SURVEYS, null, null);
            dbh.delete(SQLiteConnectorSurvey.TABLE_ANSWERS, null, null);
            String[] result = builder.toString().split("_next_");
            for (String s : result) {
                String[] res = s.split("_;_");
                if (res.length >= 7) {
                    long id = dbh.insert(SQLiteConnectorSurvey.TABLE_SURVEYS, null, db.getSurveyContentValues(
                            res[1],
                            res[3],
                            res[2],
                            res[0],
                            Short.parseShort(res[4]),
                            Integer.parseInt(res[5]),
                            Long.parseLong(res[6] + "000")
                    ));

                    for (int i = 7; i < res.length - 1; i += 2) {
                        dbh.insert(SQLiteConnectorSurvey.TABLE_ANSWERS, null, db.getAnswerContentValues(
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
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void postUpdate() {
        new NotificationHandler.SurveyNotification().send();
    }

}

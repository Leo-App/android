package de.slg.umfragen.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slg.leoapp.utility.Utils;

/**
 * SyncSurveyTask.
 * <p>
 * Von {@link de.slg.leoapp.service.ReceiveService ReceiveService} unabhängiger Task zum aktualisieren der Umfragen, macht ein instantanes Aktualisieren möglich.
 *
 * @author Gianni
 * @version 2017.1211
 * @since 0.6.0
 */

public class SyncSurveyTask extends AsyncTask<Void, Void, Void> {

    private SwipeRefreshLayout layout;

    public SyncSurveyTask(@Nullable SwipeRefreshLayout layout) {
        this.layout = layout;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utils.checkNetwork()) {
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

                SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());
                SQLiteDatabase          dbh = db.getWritableDatabase();
                dbh.delete(SQLiteConnectorUmfragen.TABLE_SURVEYS, null, null);
                dbh.delete(SQLiteConnectorUmfragen.TABLE_ANSWERS, null, null);
                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    String[] res = s.split("_;_");
                    if (res.length >= 7) {
                        long id = dbh.insert(SQLiteConnectorUmfragen.TABLE_SURVEYS, null, db.getSurveyContentValues(
                                res[1],
                                res[3],
                                res[2],
                                res[0],
                                Short.parseShort(res[4]),
                                Integer.parseInt(res[5]),
                                Long.parseLong(res[6]+ "000")
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
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (layout != null) {
            layout.setRefreshing(false);
            Utils.getController().getSurveyActivity().refreshUI();
        }
    }
}

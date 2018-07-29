package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.service.SocketService;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;

/**
 * SyncSurveyTask.
 * <p>
 * Von {@link SocketService SocketService} unabhängiger Task zum Aktualisieren der Umfragen, macht ein instantanes Aktualisieren möglich.
 *
 * @author Gianni
 * @version 2017.1211
 * @since 0.6.0
 */

public class SyncSurveyTask extends VoidCallbackTask<Void> {

    @Override
    protected Void doInBackground(Void... params) {
        if (NetworkUtils.isNetworkAvailable()) {
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

                List<Long> ids = new List<>();

                SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());

                String[] result = builder.toString().split("_next_");
                for (String s : result) {
                    String[] parts = s.split("_;_");

                    if (parts.length >= 7) {
                        long id = db.addSurvey(s, resultBuilder.toString());

                        if (id != -1)
                            ids.append(id);
                    }
                }

                db.deleteAllSurveysExcept(ids);

                db.close();
            } catch (IOException e) {
                Utils.logError(e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {

        if(Utils.getController().getSurveyActivity() != null)
            Utils.getController().getSurveyActivity().refreshUI();

        super.onPostExecute(v);

    }
}

package de.slgdev.schwarzes_brett.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.utility.Utils;

/**
 * SyncNewsTask.
 * <p>
 * Von {@link de.slgdev.leoapp.service.ReceiveService ReceiveService} unabhängiger Task zum aktualisieren des Schwarzes Bretts, macht ein instantanes Aktualisieren möglich.
 *
 * @author Gianni
 * @version 2017.1211
 * @since 0.6.0
 */

public class SyncNewsTask extends AsyncTask<Void, Void, Void> {

    private SwipeRefreshLayout layout;

    public SyncNewsTask(@Nullable SwipeRefreshLayout layout) {
        this.layout = layout;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utils.isNetworkAvailable()) {
            try {
                URLConnection connection = new URL(Utils.BASE_URL_PHP + "schwarzesBrett/meldungen.php")
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
                SQLiteConnectorSchwarzesBrett db  = new SQLiteConnectorSchwarzesBrett(Utils.getContext());

                String[] result = builder.toString().split("_next_");

                for (String s : result)
                    db.insertEntry(s);

                db.purgeOldEntries();

                db.close();
            } catch (IOException e) {
                Utils.logError(e);
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (layout != null) {
            layout.setRefreshing(false);
        }
        if(Utils.getController().getSchwarzesBrettActivity() != null)
            Utils.getController().getSchwarzesBrettActivity().refreshUI();
    }
}

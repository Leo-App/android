package de.slg.schwarzes_brett.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slg.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slg.leoapp.utility.Utils;

/**
 * SyncNewsTask.
 * <p>
 * Von {@link de.slg.leoapp.service.ReceiveService ReceiveService} unabhängiger Task zum aktualisieren des Schwarzes Bretts, macht ein instantanes Aktualisieren möglich.
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
        if (Utils.checkNetwork()) {
            try {
                URLConnection connection = new URL(Utils.DOMAIN_DEV + "schwarzesBrett/meldungen.php")
                        .openConnection();

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
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (layout != null) {
            layout.setRefreshing(false);
            Utils.getController().getSchwarzesBrettActivity().refreshUI();
        }
    }
}

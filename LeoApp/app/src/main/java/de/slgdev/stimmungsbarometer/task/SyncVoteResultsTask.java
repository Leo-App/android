package de.slgdev.stimmungsbarometer.task;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.GregorianCalendar;

import de.slgdev.leoapp.sqlite.SQLiteConnectorStimmungsbarometer;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.stimmungsbarometer.utility.Ergebnis;

public class SyncVoteResultsTask extends VoidCallbackTask<ResponseCode> {
    private SQLiteConnectorStimmungsbarometer database;

    public SyncVoteResultsTask(Context context) {
        database = new SQLiteConnectorStimmungsbarometer(context);
    }

    @Override
    protected ResponseCode doInBackground(Void... params) {
        try {
            URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/ergebnisse.php?uid=" + Utils.getUserID())
                    .openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );

            String        line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            String result = builder.toString();
            if (result.startsWith("-")) {
                throw new IOException(result);
            }

            String[] e = builder.toString().split("_abschnitt_");

            String[] splitI = e[0].split("_next_");
            String[] splitS = e[1].split("_next_");
            String[] splitL = e[2].split("_next_");
            String[] splitA = e[3].split("_next_");

            for (String aSplitI : splitI) {
                String[] current = aSplitI.split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    database.insert(
                            new Ergebnis(
                                    new GregorianCalendar(
                                            Integer.parseInt(date[2]),
                                            Integer.parseInt(date[1]) - 1,
                                            Integer.parseInt(date[0])
                                    ).getTime(),
                                    Double.parseDouble(current[0]),
                                    true,
                                    false,
                                    false,
                                    false
                            )
                    );
                }
            }

            for (String split : splitS) {
                String[] current = split.split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    database.insert(
                            new Ergebnis(
                                    new GregorianCalendar(
                                            Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])
                                    ).getTime(),
                                    Double.parseDouble(current[0]),
                                    false,
                                    true,
                                    false,
                                    false
                            )
                    );
                }
            }

            for (String aSplitL : splitL) {
                String[] current = aSplitL.split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    database.insert(
                            new Ergebnis(
                                    new GregorianCalendar(
                                            Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])
                                    ).getTime(),
                                    Double.parseDouble(current[0]),
                                    false,
                                    false,
                                    true,
                                    false
                            )
                    );
                }
            }

            for (String aSplitA : splitA) {
                String[] current = aSplitA.split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    database.insert(
                            new Ergebnis(
                                    new GregorianCalendar(
                                            Integer.parseInt(date[2]),
                                            Integer.parseInt(date[1]) - 1,
                                            Integer.parseInt(date[0])
                                    ).getTime(),
                                    Double.parseDouble(current[0]),
                                    false,
                                    false,
                                    false,
                                    true
                            )
                    );
                }
            }
        } catch (IOException e) {
            Utils.logError(e);
            e.printStackTrace();
            return ResponseCode.SERVER_FAILED;
        }

        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode result) {
        for (TaskStatusListener listener : getListeners()) {
            listener.taskFinished(result);
        }
    }
}
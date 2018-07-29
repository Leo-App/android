package de.slgdev.it_problem.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

import de.slgdev.it_problem.utility.datastructure.DecisionTree;
import de.slgdev.leoapp.sqlite.SQLiteConnectorITProblem;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;

public class SynchronizerDownstreamTask extends AsyncTask<String, Void, Void> {

    private Hashtable<String, DecisionTree> decisionTreeMap;
    private List<TaskStatusListener>        listeners;

    public SynchronizerDownstreamTask(Hashtable<String, DecisionTree> decisionTreeMap) {
        this.decisionTreeMap = decisionTreeMap;
        listeners = new List<>();
    }

    @Override
    protected Void doInBackground(String... subjects) {
        Utils.logError("BACKGROUND START");

        SQLiteConnectorITProblem db = new SQLiteConnectorITProblem(Utils.getContext());

        if (NetworkUtils.isNetworkAvailable()) {

            SQLiteDatabase dbh = db.getWritableDatabase();

            for (String subject : subjects) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    new URL(
                                            Utils.DOMAIN_DEV + "itbaum/" +
                                                    "get.php?" +
                                                    "subject=" + subject
                                    )
                                            .openConnection()
                                            .getInputStream(),
                                    "UTF-8"
                            )
                    );

                    StringBuilder builder = new StringBuilder();
                    String        line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    String result = builder.toString();

                    if (result.startsWith("-"))
                        continue;

                    dbh.insertWithOnConflict(
                            SQLiteConnectorITProblem.TABLE_DECISIONS,
                            null,
                            db.getContentValues(
                                    subject,
                                    result.substring(
                                            0,
                                            result.length() - 1
                                    )
                            ),
                            SQLiteDatabase.CONFLICT_REPLACE
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            dbh.close();
        }

        SQLiteDatabase dbh = db.getReadableDatabase();

        Cursor c = dbh.rawQuery("SELECT "
                + SQLiteConnectorITProblem.DECISION_SUBJECT
                + ", "
                + SQLiteConnectorITProblem.DECISIONS_CONTENT
                + " FROM "
                        + SQLiteConnectorITProblem.TABLE_DECISIONS,
                null
        );

        c.moveToFirst();

        while (!c.isAfterLast()) {
            decisionTreeMap.put(
                    c.getString(0),
                    new DecisionTree(
                            c.getString(1)
                    )
            );
            c.moveToNext();
        }

        c.close();
        fillMissingTrees(subjects);

        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        Utils.logError("FINISHED TASK");
        for (TaskStatusListener listener : listeners)
            listener.taskFinished();
    }

    public SynchronizerDownstreamTask addListener(TaskStatusListener listener) {
        listeners.append(listener);
        return this;
    }

    private void fillMissingTrees(String[] subjects) {
        Utils.logError("FILLED");
        for (String cur : subjects) {
            if (decisionTreeMap.get(cur) == null) {
                decisionTreeMap.put(cur, new DecisionTree());
            }
            Utils.logError(cur + ": " + decisionTreeMap.get(cur));
        }
    }
}


package de.slg.it_problem.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

import de.slg.it_problem.utility.datastructure.DecisionTree;
import de.slg.leoapp.utility.Utils;

public class SynchronizerDownstreamTask extends AsyncTask<String, Void, Void> {

    private Hashtable<String, DecisionTree> decisionTreeMap;

    public SynchronizerDownstreamTask(Hashtable<String, DecisionTree> decisionTreeMap) {
        this.decisionTreeMap = decisionTreeMap;
    }

    @Override
    protected Void doInBackground(String... subjects) {

        for (String subject : subjects) {

            try {

                URL updateURL = new URL(Utils.DOMAIN_DEV + "get.php?subject=" + subject);
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                reader.close();

                String result = builder.toString();

                if (result.startsWith("-"))
                    return null;

                decisionTreeMap.put(subject, new DecisionTree(result));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}


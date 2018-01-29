package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.umfragen.utility.Survey;

public class SendVoteTask extends ObjectCallbackTask<ResponseCode> {

    private Survey s;
    private int groupId;
    private int tag;

    @Override
    protected ResponseCode doInBackground(Object... params) {

        if (!Utils.checkNetwork())
            return ResponseCode.NO_CONNECTION;

        groupId = (Integer) params[0];
        s = (Survey) params[1];
        tag = (Integer) params[2];

        SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());
        db.setAnswerSelected(tag);
        db.close();

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "survey/" +
                                            "addResult.php?" +
                                            "user=" + Utils.getUserID() + "&" +
                                            "answer=" + tag
                            )
                                    .openConnection()
                                    .getInputStream(),
                            "UTF-8"
                    )
            );

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

            if (builder.toString().startsWith("-"))
                return ResponseCode.SERVER_FAILED;

        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.SERVER_FAILED;
        }
        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode r) {
        for (TaskStatusListener l : getListeners()) {
            l.taskFinished(r, s, tag, groupId);
        }
    }
}

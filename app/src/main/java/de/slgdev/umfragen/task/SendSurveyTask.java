package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

public class SendSurveyTask extends ObjectCallbackTask<ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Object... params) {

        if (!Utils.checkNetwork())
            return ResponseCode.NO_CONNECTION;

        String title = (String) params[0];
        String description = (String) params[1];
        String[] answers = (String[]) params[2];
        Boolean multiple = (Boolean) params[3];
        int to = (int) params[4];

        StringBuilder answerString = new StringBuilder(answers[0]);

        for (int i = 1; i < 5; i++) {
            if (answers[i].equals(""))
                continue;
            answerString.append("_;_").append(answers[i]);
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    (Utils.BASE_URL_PHP + "survey/" +
                                            "addSurvey.php?" +
                                            "id=" + Utils.getUserID() + "&" +
                                            "to=" + to + "&" +
                                            "title=" + title + "&" +
                                            "desc=" + description + "&" +
                                            "mult=" + (multiple ? 1 : 0) + "&" +
                                            "answers=" + answerString)
                                            .replace(
                                                    " ",
                                                    "%20"
                                            )
                            )
                                    .openConnection()
                                    .getInputStream()
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

            return ResponseCode.SUCCESS;

        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.NOT_SENT;
        }
    }

    @Override
    protected void onPostExecute(ResponseCode responseCode) {
        for (TaskStatusListener l : getListeners()) {
            l.taskFinished(responseCode);
        }
    }
}

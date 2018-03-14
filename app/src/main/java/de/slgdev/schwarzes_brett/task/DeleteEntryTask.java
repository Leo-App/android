package de.slgdev.schwarzes_brett.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

public class DeleteEntryTask extends ObjectCallbackTask<ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Object[] objects) {

        try {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    new URL(
                                            Utils.BASE_URL_PHP + "schwarzesBrett/" +
                                                    "deleteEntry.php?" +
                                                    "id=" + objects[0]
                                    )
                                            .openConnection()
                                            .getInputStream()
                            )
                    );

            if (reader.readLine().startsWith("-"))
                return ResponseCode.SERVER_FAILED;

        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.NOT_SENT;
        }
        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode r) {

        for (TaskStatusListener l : getListeners())
            l.taskFinished();

        switch (r) {
            case NO_CONNECTION:
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                break;
            case NOT_SENT:
            case SERVER_FAILED:
                GraphicUtils.sendToast(R.string.error_later);
                break;
            case SUCCESS:
                break;
        }
    }

}

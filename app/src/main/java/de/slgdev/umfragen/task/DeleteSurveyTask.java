package de.slgdev.umfragen.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

public class DeleteSurveyTask extends ObjectCallbackTask<ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Object... params) {

        if (!Utils.checkNetwork())
            return ResponseCode.NO_CONNECTION;

        SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());
        db.deleteSurvey((Integer) params[0]);
        db.close();

        try {
            URL updateURL = new URL(Utils.BASE_URL_PHP + "survey/deleteSurvey.php?survey=" + params[0]);
            Utils.logError(updateURL);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
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
        switch (r) {
            case NO_CONNECTION:
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                break;
            case SERVER_FAILED:
                GraphicUtils.sendToast(R.string.error_later);
                break;
            case SUCCESS:
                break;
        }
    }
}
package de.slgdev.umfragen.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.schwarzes_brett.utility.ResponseCode;

public class DeleteSurveyTask extends AsyncTask<Integer, Void, ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Integer... params) {

        if (!Utils.checkNetwork())
            return ResponseCode.NO_CONNECTION;

        SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(Utils.getContext());
        db.deleteSurvey(params[0]);
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
                return ResponseCode.SERVER_ERROR;
        } catch (IOException e) {
            Utils.logError(e);
            return ResponseCode.SERVER_ERROR;
        }
        return ResponseCode.SUCCESS;
    }

    @Override
    protected void onPostExecute(ResponseCode r) {
        //TODO Maybe replace with Snackbar?
        switch (r) {
            case NO_CONNECTION:
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                break;
            case SERVER_ERROR:
                GraphicUtils.sendToast(R.string.error_later);
                break;
            case SUCCESS:
                break;
        }
    }
}
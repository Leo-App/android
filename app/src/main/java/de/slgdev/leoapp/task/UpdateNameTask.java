package de.slgdev.leoapp.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.ReturnValues;
import de.slgdev.leoapp.utility.Utils;

public class UpdateNameTask extends AsyncTask<String, Void, ReturnValues> {
    private final String old;

    public UpdateNameTask(String oldUsername) {
        old = oldUsername;
    }

    @Override
    protected ReturnValues doInBackground(String... params) {
        if (!Utils.isNetworkAvailable()) {
            return ReturnValues.NO_CONNECTION;
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "user/" +
                                            "updateUsername.php?" +
                                            "uid=" + Utils.getUserID() + "&" +
                                            "uname=" + URLEncoder.encode(Utils.getUserName(), "UTF-8")
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

            String result = builder.toString();

            if (result.startsWith("-")) {
                if (result.startsWith("-username")) {
                    return ReturnValues.USERNAME_TAKEN;
                }

                return ReturnValues.ERROR;
            }

            return ReturnValues.SUCCESSFUL;
        } catch (IOException e) {
            Utils.logError(e);
            return ReturnValues.ERROR;
        }
    }

    @Override
    protected void onPostExecute(ReturnValues b) {
        switch (b) {
            case USERNAME_TAKEN:
                resetName();
                GraphicUtils.sendToast(R.string.settings_snackbar_username_taken);
                break;
            case NO_CONNECTION:
                resetName();
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                break;
            case ERROR:
                resetName();
                GraphicUtils.sendToast(R.string.error);
                break;
            case SUCCESSFUL:
                Toast t = Toast.makeText(Utils.getContext(), Utils.getString(R.string.settings_toast_username_success), Toast.LENGTH_LONG);
                t.show();
                Utils.getController().getProfileActivity().initProfil();
                Utils.getController().getProfileActivity().initNavigationDrawer();
                break;
        }
    }

    private void resetName() {
        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_general_name", old)
                .apply();
    }
}
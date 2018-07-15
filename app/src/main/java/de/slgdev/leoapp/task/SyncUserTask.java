package de.slgdev.leoapp.task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.RequestMethod;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.Utils;

public class SyncUserTask extends VoidCallbackTask<ResponseCode> {

    @Override
    protected ResponseCode doInBackground(Void... params) {

        if (!NetworkUtils.isNetworkAvailable()) {
            return ResponseCode.NO_CONNECTION;
        }

        try {
            //TODO mod_rewrite

            Utils.logError("Checksum: " + NetworkUtils.getAuthenticationToken());

            HttpURLConnection connection = NetworkUtils.openURLConnection(
                    Utils.BASE_URL_PHP + "user/getByName/index.php?p0=" + Utils.getUserDefaultName(),
                    RequestMethod.GET
            );

            Utils.logDebug("Connection: "+connection);

            JSONObject json = NetworkUtils.getJSONResponse(connection).getJSONObject("data");

            Utils.logError("JSONResponse: " + json);

            if (connection.getResponseCode() / 100 == 5)
                return ResponseCode.SERVER_FAILED;

            if (connection.getResponseCode() / 100 == 4)
                return ResponseCode.AUTH_FAILED;

            int uid = json.getInt("id");
            int permission = json.getInt("permission");
            String username = json.getString("name");

            Utils.getController().getPreferences()
                    .edit()
                    .putInt("pref_key_general_id", uid)
                    .putInt("pref_key_general_permission", permission)
                    .putString("pref_key_general_name", username)
                    .apply();

            return ResponseCode.SUCCESS;
        } catch (IOException | JSONException e) {
            Utils.logError(e);
        }
        return ResponseCode.SERVER_FAILED;
    }

    @Override
    protected void onPostExecute(ResponseCode code) {
        for (TaskStatusListener l : getListeners())
            l.taskFinished(code);
    }
}
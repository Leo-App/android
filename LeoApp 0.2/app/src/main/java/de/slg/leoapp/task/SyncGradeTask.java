package de.slg.leoapp.task;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.WebDAVConnector;

public class SyncGradeTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        if (!Utils.checkNetwork())
            return null;

        String name = Utils.getUserDefaultName();
        String pw   = Utils.getController().getPreferences().getString("pref_key_general_password", "");

        WebDAVConnector webDAVConnector = new WebDAVConnector(name, pw);
        webDAVConnector.changeDirectory("PrivatSchueler/Meine Gruppen");

        if(webDAVConnector.getDirContent().isEmpty()) {
            Utils.getController().getPreferences()
                    .edit()
                    .putString("pref_key_general_klasse", "TEA")
                    .apply();
            return null;
        }

        String levelFile = webDAVConnector.getDirContent().getObjectAt(0);
        String grade     = levelFile.split("%20")[0];

        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_general_klasse", levelFile.split("%20")[0])
                .apply();

        try {

            HttpURLConnection connection = (HttpURLConnection)
                    new URL(Utils.BASE_URL_PHP + "user/updateKlasse.php?uid=" + Utils.getUserID() + "&uklasse=" + grade)
                            .openConnection();

            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
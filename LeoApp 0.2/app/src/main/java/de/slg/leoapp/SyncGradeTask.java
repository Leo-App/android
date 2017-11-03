package de.slg.leoapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


class SyncGradeTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        if(!Utils.checkNetwork())
            return null;

        String name = Utils.getUserDefaultName();
        String pw   = Utils.getController().getPreferences().getString("pref_key_general_password", "");

        WebDAVConnector webDAVConnector = new WebDAVConnector(name, pw);
        webDAVConnector.changeDirectory("PrivatSchueler/Meine Gruppen");

        String levelFile = webDAVConnector.getDirContent().getObjectAt(0);
        String grade     = levelFile.split("%20")[0];

        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_general_klasse", levelFile.split("%20")[0])
        .apply();

        try {

            HttpsURLConnection connection = (HttpsURLConnection)
                    new URL(Utils.BASE_URL_PHP + "user/updateKlasse.php?uid=" + Utils.getUserID() + "&uklasse=" + grade)
                            .openConnection();
            connection.setRequestProperty("Authorization", Utils.authorization);

            connection.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
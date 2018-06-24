package de.slgdev.nachhilfeboerse.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.stimmungsbarometer.utility.Vote;

/**
 * Created by Benno on 20.06.2018.
 */

public class addPerson extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... daten) {

        try {
            URLConnection connection = new URL("http://www.moritz.liegmanns.de/leoapp_php/NachhilfeBoerse/addPerson.php?vorname=" + daten[0] + "&geld=" + daten[4] + "&nachname=" + daten[1] + "&stufe=" + daten[2] + "&faecher=" + daten[3])
                    .openConnection();
            Utils.logDebug(connection);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection
                                            .getInputStream(), "UTF-8"));
        } catch (IOException e) {
            Utils.logError(e);
            Utils.logError(e);
        }
        return null;
    }
}

package de.slg.nachhilfe;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.Utils;

class AnzeigeEinreichen extends AsyncTask<String, Void, Void> {
    protected Void doInBackground(String... fach) {
        String s = fach[0];
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(Utils.BaseURL + "nachhilfeboerse/Hinzufuegen1_1.php?f=" + s + "&u=1&d=2017-06-17")
                                    .openConnection()
                                    .getInputStream(), "UTF-8"));
            while (reader.readLine() != null)
                ;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


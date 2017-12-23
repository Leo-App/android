package de.slg.stundenplan.task;

import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;

public class FachImporter extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
        if (Utils.getController().getAuswahlActivity() != null) {
            Utils.getController().getAuswahlActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Utils.getContext()
                                    .openFileInput(
                                            "stundenplan.txt"
                                    )
                    )
            );

            String lastKurzel = "";
            long   lastID     = -1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] fach = line.replace("\"", "").split(",");
                if (fach[1].replace("0", "").startsWith(Utils.getUserStufe())) {
                    if (!fach[3].equals(lastKurzel)) {
                        lastID = Utils.getController().getStundenplanDatabase().insertFach(fach[3], fach[2], fach[1]);
                        lastKurzel = fach[3];
                        if (Utils.getUserPermission() == User.PERMISSION_LEHRER && fach[2].toUpperCase().equals(Utils.getLehrerKuerzel().toUpperCase())) {
                            Utils.getController().getStundenplanDatabase().waehleFach(lastID);
                        }
                    }
                    Utils.getController().getStundenplanDatabase().insertStunde(lastID, Integer.parseInt(fach[5]), Integer.parseInt(fach[6]), fach[4]);
                }
            }

            reader.close();
        } catch (IOException e) {
            Utils.logError(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (Utils.getController().getAuswahlActivity() != null) {
            Utils.getController().getAuswahlActivity().initDB();
            Utils.getController().getAuswahlActivity().initListView();
        } else {
            Utils.getController().getStundenplanActivity().refreshUI();
        }
        Utils.getController().getActiveActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
}

package de.slgdev.stundenplan.task;

import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.SyncFilesTask;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;

public class Importer extends VoidCallbackTask<Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Utils.getController().getActiveActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while (SyncFilesTask.running)
                ;

            SQLiteConnectorStundenplan database = new SQLiteConnectorStundenplan(Utils.getContext());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Utils.getContext()
                                    .openFileInput(
                                            "stundenplan.txt"
                                    )
                    )
            );

            database.clear();

            String letzterKurs   = "";
            String letzterLehrer = "";
            String letzteStufe   = "";
            long   letzteID      = -1;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] fach = line.split(",");

                String stufe  = fach[0];
                String lehrer = fach[1];
                String kurs   = fach[2];
                String raum   = fach[3];
                String tag    = fach[4];
                String stunde = fach[5];

                if (stufe.startsWith(Utils.getUserStufe()) || Utils.getUserPermission() == User.PERMISSION_LEHRER) {
                    if (!letzterKurs.equals(kurs) || !letzterLehrer.equals(lehrer) || !letzteStufe.equals(stufe)) {
                        letzteID = database.insertSubject(
                                kurs,
                                lehrer,
                                stufe
                        );
                        letzterKurs = kurs;
                        letzterLehrer = lehrer;
                        letzteStufe = stufe;

                        if (Utils.getUserPermission() == User.PERMISSION_LEHRER && Utils.getLehrerKuerzel().toUpperCase().equals(lehrer.toUpperCase())) {
                            database.chooseSubject(letzteID);
                            database.setWritten(true, letzteID);
                        }
                    }

                    database.insertLesson(
                            letzteID,
                            Integer.parseInt(
                                    tag
                            ),
                            Integer.parseInt(
                                    stunde
                            ),
                            raum
                    );
                }
            }

            reader.close();
            database.close();
        } catch (IOException e) {
            Utils.logError(e);
        }
        return null;
    }
}
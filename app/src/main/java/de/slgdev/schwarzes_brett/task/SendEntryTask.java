package de.slgdev.schwarzes_brett.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;

/**
 * Nachrichten-Task
 * <p>
 * Sendet neue Nachricht an Remote-Datenbank
 *
 * @version 2017.2711
 * @since 0.5.6
 */
public class SendEntryTask extends ObjectCallbackTask<Boolean> {

    @Override
    protected Boolean doInBackground(Object... params) {

        if (!Utils.isNetworkAvailable())
            return false;

        try {

            String[] input = new String[params.length];

            for (int i = 0; i < params.length; i++) {
                input[i] = ((String) params[i])
                        .replace("ä", "_ae_")
                        .replace("ö", "_oe_")
                        .replace("ü", "_ue_")
                        .replace("Ä", "_Ae_")
                        .replace("Ö", "_Oe_")
                        .replace("Ü", "_Ue_")
                        .replace("ß", "_ss_")
                        .replace(" ", "%20")
                        .replace("%", "%25")
                        .replace("\n", "\\\\\\\\");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "schwarzes_brett/_php/" +
                                            "newEntry.php?" +
                                            "to=" + input[3] + "&" +
                                            "title=" + input[0] + "&" +
                                            "content=" + input[1] + "&" +
                                            "date=" + input[2]
                            )
                                    .openConnection()
                                    .getInputStream()
                    )
            );

            String line;
            while ((line = reader.readLine()) != null) {
                Utils.logError(line);
            }

            reader.close();
        } catch (IOException e) {
            Utils.logError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        for (TaskStatusListener listener : getListeners()) {
            listener.taskFinished(b);
        }
    }
}

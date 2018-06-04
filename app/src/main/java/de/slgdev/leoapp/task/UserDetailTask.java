package de.slgdev.leoapp.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.slgdev.leoapp.task.general.ObjectCallbackTask;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;

/**
 * UserDetailTask.
 *
 * Díeser Task ruft genauere Infos zu einem bestimmten User aus der ucloud Datenbank ab.
 *
 * @author Gianni
 * @version 0406.2018
 * @since 1.0.4
 */
public class UserDetailTask extends ObjectCallbackTask<User> {

    @Override
    protected User doInBackground(Object[] objects) {
        try {
            int id = (int) objects[0];
            URL updateURL = new URL(Utils.DOMAIN_DEV + "user/getUserInfo.php?id=" + id);
            Utils.logError(updateURL);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            reader.close();

            if (reader.toString().startsWith("-ERR"))
                return null;

            String[] info = builder.toString().split("_;_");
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.GERMAN);
            Date createdate = format.parse(info[5]);

            return new User(id, info[2], info[3], Integer.parseInt(info[4]), info[1], createdate);

        } catch (IOException | ClassCastException | ParseException | NumberFormatException e) {
            Utils.logError(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(User result) {
        for (TaskStatusListener listener : getListeners()) {
            listener.taskFinished(result);
        }
    }
}

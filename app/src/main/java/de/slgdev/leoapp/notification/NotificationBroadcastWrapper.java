package de.slgdev.leoapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.slgdev.leoapp.utility.Utils;

/**
 * NotificationBroadcastWrapper.
 *
 * Verwaltet alle BroadcastReceiver, die Notifications starten, für einfachen Zugriff.
 *
 * @author Gianni
 * @version 2018.1302
 * @since 0.7.3
 */
public abstract class NotificationBroadcastWrapper {

    /* Innere Klassen */

    public static class TimetableReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new NotificationHandler.StundenplanNotification().send();
        }

    }

    public static class StimmungsbarometerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new NotificationHandler.StimmungsbarometerNotification().send();
        }

    }

    public static class FoodmarkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new NotificationHandler.EssensbonsNotification().send();
        }

    }

    public static class KlausurplanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new NotificationHandler.KlausurplanNotification().send();
        }

    }

    /*
     *  private void sendNotificationIfNecessary() {
            if (!Utils.getController().getPreferences().getBoolean("pref_key_status_loggedin", false))
                return;
            SQLiteConnectorEssensbons db     = new SQLiteConnectorEssensbons(this);
            SQLiteDatabase            dbw    = db.getReadableDatabase();
            Cursor                    cursor = dbw.rawQuery("SELECT MAX(ID) as id FROM STATISTICS", null);
            if (cursor.getCount() == 0) {
                cursor.close();
                new NotificationHandler.EssensbonsNotification().send();
                return;
            }
            cursor.moveToFirst();
            int maxid = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            cursor = dbw.rawQuery("SELECT o.DATEU as date FROM USERORDERS o JOIN STATISTICS s ON s.LASTORDER = o.ID WHERE s.ID = " + maxid, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                new NotificationHandler.EssensbonsNotification().send();
                return;
            }
            cursor.moveToFirst();
            String date = cursor.getString(cursor.getColumnIndex("date"));
            cursor.close();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
            try {
                Date dateD = df.parse(date);
                if (dateD.before(new Date()))
                    new NotificationHandler.EssensbonsNotification().send();
            } catch (ParseException e) {
                Utils.logError(e);
            }
        }
     */

}

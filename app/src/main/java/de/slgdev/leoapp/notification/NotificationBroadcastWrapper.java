package de.slgdev.leoapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.slgdev.leoapp.sqlite.SQLiteConnectorEssensbons;
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
            SQLiteConnectorEssensbons sqlite = new SQLiteConnectorEssensbons(Utils.getContext());

            if (!sqlite.hasOrderedForToday())
                new NotificationHandler.EssensbonsNotification().send();

            sqlite.close();
        }

    }

    public static class KlausurplanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new NotificationHandler.KlausurplanNotification().send();
        }

    }

}

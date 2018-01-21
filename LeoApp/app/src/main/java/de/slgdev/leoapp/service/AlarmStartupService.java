package de.slgdev.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.slgdev.leoapp.notification.NotificationAlarmHandler;
import de.slgdev.leoapp.utility.Utils;

/**
 * AlarmStartupService.
 * <p>
 * Dieser Service verwaltet das erstmalige Starten der Notifications, die zu einer bestimmten Uhrzeit gesendet
 * werden. Dazu werden Methoden aus {@link NotificationAlarmHandler NotificationAlarmHandler}
 * verwendet.
 *
 * @author Gianni
 * @version 2017.2412
 * @since 0.7.0
 */
public class AlarmStartupService extends Service {

    @Override
    public void onCreate() {
        if (Utils.getContext() == null)
            Utils.getController().setContext(getApplicationContext());
        NotificationAlarmHandler.initAlarmManagerIfNotExists();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

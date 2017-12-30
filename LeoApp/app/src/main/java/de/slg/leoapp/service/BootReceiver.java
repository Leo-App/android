package de.slg.leoapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.slg.leoapp.notification.NotificationAlarmHandler;

public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        NotificationAlarmHandler.initAlarmManagerIfNotExists();
    }

}

package de.slgdev.leoapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.slgdev.leoapp.notification.NotificationAlarmHandler;

public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        NotificationAlarmHandler.initAlarmManagerIfNotExists();
    }

}

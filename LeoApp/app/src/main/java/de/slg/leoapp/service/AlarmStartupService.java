package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.slg.leoapp.Start;

public class AlarmStartupService extends Service {

    @Override
    public void onCreate() {
        Start.initNotificationServices();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * ReceiveSyncService.
 *
 * ...
 *
 * @author Gianni
 * @since 0.6.9
 * @version 2017.0712
 */

public class ReceiveSyncService extends Service {

    private static ReceiveSyncAdapter syncAdapter;
    private static final String TAG = "SyncService";
    private static final Object syncLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncLock) {
            if (syncAdapter == null) {
                syncAdapter = new ReceiveSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}

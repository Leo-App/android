package de.slg.leoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * AuthenticatorService.
 *
 * Instanziiert den {@link StubAuthenticator} bei Servicestart.
 *
 * @author Gianni
 * @since 0.6.9
 * @version 2017.0712
 *
 */

public class AuthenticatorService extends Service {

    private StubAuthenticator syncAuthenticator;

    @Override
    public void onCreate() {
        syncAuthenticator = new StubAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAuthenticator.getIBinder();
    }
}

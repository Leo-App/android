package de.slg.leoapp.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * ReceiveSyncAdapter.
 *
 * Erlaubt ein akkusparendes Synchronisieren von Serverdaten.
 *
 * @author Gianni
 * @since 0.6.8
 * @version 2017.0512
 */

public class ReceiveSyncAdapter extends AbstractThreadedSyncAdapter {

    public ReceiveSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public ReceiveSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }
}

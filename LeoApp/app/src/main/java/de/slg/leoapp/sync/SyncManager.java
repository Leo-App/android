package de.slg.leoapp.sync;

/**
 * SyncManager.
 *
 * Interface für Klassen, die Synchronisationsvorgänge verwalten.
 *
 * @author Gianni
 * @since 0.6.8
 * @version 2017.0512
 */

public interface SyncManager {
    void init();
    boolean run();
}

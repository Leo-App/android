package de.slg.leoapp.sync;

/**
 * Synchronizer.
 *
 * Interface für Klassen, die Synchronisationsvorgänge verwalten.
 *
 * @author Gianni
 * @since 0.6.8
 * @version 2017.0712
 */
public interface Synchronizer {

    /**
     * Startet den eigentlichen Synchronisationsvorgang. Gibt bei Erfolg true zurück.
     *
     * @return Synchronisation erfolgreich?
     */
    boolean run();

    /**
     * Wird aufgerufen, wenn Sync erfolgreich war. Hier können beispielsweise Notifications gesendet werden.
     */
    void postUpdate();
}

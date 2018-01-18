package de.slg.leoapp.task.general;

import android.os.AsyncTask;

import de.slg.leoapp.utility.datastructure.List;

/**
 * VoidCallbackTask.
 *
 * Klasse für AsyncTasks, die ihren Status an Listener mitteilen müssen.
 *
 * @param <ContentType> Datentyp des Ergebnisobjekts
 *
 * @author Gianni
 * @since 0.7.2
 * @version 2018.1601
 */
public abstract class VoidCallbackTask<ContentType> extends AsyncTask<Void, Void, ContentType> {

    private final List<TaskStatusListener> listeners;

    {
        listeners = new List<>();
    }

    public final VoidCallbackTask addListener(TaskStatusListener listener) {
        listeners.append(listener);
        return this;
    }

    protected final List<TaskStatusListener> getListeners() {
        return listeners;
    }

    /**
     * Teilt Listenern das Ende des Tasks mit. Überschreibende Methodens sollten diese Funktionalität
     * implementieren.
     *
     * @param result Ergebnis des Tasks
     */
    @Override
    protected void onPostExecute(ContentType result) {
        for (TaskStatusListener l : listeners)
            l.taskFinished();
    }

    public void execute() {
        super.execute();
    }

}

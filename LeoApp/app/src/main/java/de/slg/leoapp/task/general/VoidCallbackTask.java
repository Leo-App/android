package de.slg.leoapp.task.general;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;

import de.slg.leoapp.utility.datastructure.List;

/**
 * VoidCallbackTask.
 *
 * Klasse für AsyncTasks, die ihren Status an Listener mitteilen müssen.
 *
 * @param <OutputType> Datentyp des Ergebnisobjekts
 *
 * @author Gianni
 * @since 0.7.2
 * @version 2018.1601
 */
public abstract class VoidCallbackTask<OutputType> extends AsyncTask<Void, Void, OutputType> {

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

    @Override
    @CallSuper
    protected void onPreExecute() {
        for (TaskStatusListener l : listeners)
            l.taskStarts();
    }

    /**
     * Teilt Listenern das Ende des Tasks mit. Überschreibende Methodens sollten diese Funktionalität
     * implementieren.
     *
     * @param result Ergebnis des Tasks
     */
    @Override
    protected void onPostExecute(OutputType result) {
        for (TaskStatusListener l : listeners)
            l.taskFinished();
    }

    public void execute() {
        super.execute();
    }

}

@file:Suppress("unused")

package de.leoappslg.core.task

import android.os.AsyncTask
import androidx.annotation.CallSuper

import de.leoappslg.core.datastructure.List

/**
 * StringCallbackTask.
 * <p>
 * Klasse für AsyncTasks, die ihren Status an Listener mitteilen müssen und einen
 * Eingangsdatentyp haben.
 *
 * @param <OutputType> Datentyp des Ergebnisobjekts
 * @author Gianni
 * @version 2018.1601
 * @since 0.7.2
 */
@SuppressWarnings("unused")
abstract class ObjectCallbackTask<OutputType> : AsyncTask<Any, Void, OutputType>() {

    private val listeners: List<TaskStatusListener>
        get() = List()

    fun addListener(listener: TaskStatusListener): ObjectCallbackTask<OutputType> {
        listeners.append(listener)
        return this
    }

    @CallSuper
    override fun onPreExecute() {
        for (l: TaskStatusListener in listeners)
            l.taskStarts()
    }

    /**
     * Teilt Listenern das Ende des Tasks mit. Überschreibende Methodens sollten diese Funktionalität
     * implementieren.
     *
     * @param result Ergebnis des Tasks
     */
    override fun onPostExecute(result: OutputType) {
        for (l: TaskStatusListener in listeners)
            l.taskFinished()
    }

    fun execute() {
        super.execute()
    }
}
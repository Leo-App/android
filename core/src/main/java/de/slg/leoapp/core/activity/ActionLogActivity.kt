@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.slg.leoapp.core.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity

/**
 * ActionLogActivity
 * <p>
 * Für LeoApp-Activities angepasste Subklasse von AppCompatActivity.
 * Loggt den Lifecycle der Activity, sodass der aktuelle Status abgerufen werden kann.
 *
 * @author Gianni
 * @version 2017.2610
 * @since 0.5.7
 */
abstract class ActionLogActivity : AppCompatActivity() {

    var status: ActivityStatus = ActivityStatus.DESTROYED

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        status = ActivityStatus.ACTIVE
    }

    override fun onResume() {
        super.onResume()
        status = ActivityStatus.ACTIVE
    }

    override fun onDestroy() {
        super.onDestroy()
        status = ActivityStatus.DESTROYED
    }

    override fun onPause() {
        super.onPause()
        status = ActivityStatus.PAUSED
    }

    /**
     * Aktualisiert die Activity oder lädt wichtige Informationen neu. Kann/sollte überschrieben werden.
     */
    protected fun restart() {
        finish()
        startActivity(intent)
    }

    /**
     * Abstrakt. Soll die Bezeichnung der aktuellen Activity zurückgeben.
     *
     * @return Activity-Tag: <module>_<type>_<name>
     */
    protected abstract fun getActivityTag(): String

    fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    /**
     * Abstrakt. Muss die Id der Activity Progressbar zurückgeben.
     *
     * @return ProgressBar-ID
     */
    @IdRes
    protected abstract fun getProgressBarId(): Int
}

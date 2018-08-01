package de.leoapp_slg.core.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ActionLogActivity
 * <p>
 * Für LeoApp-Activities angepasste Subklasse von AppCompatActivity.
 * Loggt den Lifecycle der Activity, sodass der aktuelle Status abgerufen werden kann.
 * Loggt zusätzlich den Status von Activitystarts zu Firebase. TODO Progressbar in jeder Activity
 *
 * @author Gianni
 * @version 2017.2610
 * @since 0.5.7
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings("unused")
public abstract class ActionLogActivity extends AppCompatActivity {

    private ActivityStatus status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        status = ActivityStatus.ACTIVE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        status = ActivityStatus.ACTIVE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status = ActivityStatus.DESTROYED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        status = ActivityStatus.PAUSED;
    }

    /**
     * Aktualisiert die Activity oder lädt wichtige Informationen neu. Kann/sollte überschrieben werden.
     */
    protected void restart() {
        finish();
        startActivity(getIntent());
    }

    /**
     * Gibt den aktuellen Status der Activity zurück.
     *
     * @return Activitystatus
     */
    public final ActivityStatus getStatus() {
        return status;
    }

    /**
     * Abstrakt. Soll die Bezeichnung der aktuellen Activity zurückgeben.
     *
     * @return Activity-Tag
     */
    protected abstract String getActivityTag();

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Abstrakt. Muss die Id der Activity Progressbar zurückgeben.
     *
     * @return ProgressBar-ID
     */
    protected abstract @IdRes
    int getProgressBarId();
}

package de.slg.leoview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * ActionLogActivity
 *
 * Für LeoApp-Activities angepasste Subklasse von AppCompatActivity. Loggt den Lifecycle der Activity, sodass der aktuelle Status abgerufen werden kann.
 *
 * @since 0.5.7
 * @version 2017.2610
 * @author Gianni
 */

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
     * Gibt den aktuellen Status der Activity zurück
     *
     * @return Activitystatus
     */
    public ActivityStatus getStatus() {
        return status;
    }

}

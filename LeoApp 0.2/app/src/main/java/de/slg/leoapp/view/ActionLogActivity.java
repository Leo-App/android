package de.slg.leoapp.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ActionLogActivity
 * <p>
 * Für LeoApp-Activities angepasste Subklasse von AppCompatActivity. Loggt den Lifecycle der Activity, sodass der aktuelle Status abgerufen werden kann.
 * Loggt zusätzlich den Status von Activitystarts zu Firebase.
 *
 * @author Gianni
 * @version 2017.2610
 * @since 0.5.7
 */
@SuppressLint("SimpleDateFormat")
public abstract class ActionLogActivity extends AppCompatActivity {

    private ActivityStatus status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        status = ActivityStatus.ACTIVE;

        DateFormat format = new SimpleDateFormat("ddMMhhmmss");

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, format.format(new Date()));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getActivityTag());
        mFirebaseAnalytics.logEvent("ActivityStartEvent", bundle);

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
     * Gibt den aktuellen Status der Activity zurück.
     *
     * @return Activitystatus
     */
    public ActivityStatus getStatus() {
        return status;
    }

    /**
     * Abstrakt. Soll die Bezeichnung der aktuellen Activity zurückgeben.
     *
     * @return Activity-Tag
     */
    protected abstract String getActivityTag();
}

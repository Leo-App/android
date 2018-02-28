package de.slgdev.leoapp.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationAlarmHandler;
import de.slgdev.leoapp.utility.Utils;

@SuppressWarnings("deprecation")
public class NotificationPreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private AppCompatDelegate mDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_notification);
        Utils.getController().registerNotificationPreferenceActivity(this);

        addPreferencesFromResource(R.xml.preferences_notifications);

        initToolbar();
        initPreferenceChanges();
        initPreferenceSummaries();
        
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_key_notification_essensqr":
                Preference qrtime = findPreference("pref_key_notification_time_foodmarks");
                qrtime.setEnabled(sharedPreferences.getBoolean(key, false));
                NotificationAlarmHandler.updateFoodmarkAlarm();
                break;
            case "pref_key_notification_test":
                Preference testtime = findPreference("pref_key_notification_time_test");
                testtime.setEnabled(sharedPreferences.getBoolean(key, false));
                NotificationAlarmHandler.updateKlausurAlarm();
                break;
            case "pref_key_notification_survey":
                Preference surveytime = findPreference("pref_key_notification_time_survey");
                surveytime.setEnabled(sharedPreferences.getBoolean(key, false));
                NotificationAlarmHandler.updateMoodAlarm();
                break;
            case "pref_key_notification_schedule":
                Preference scheduletime = findPreference("pref_key_notification_time_schedule");
                scheduletime.setEnabled(sharedPreferences.getBoolean(key, false));
                NotificationAlarmHandler.updateTimetableAlarm();
                break;
            case "pref_key_notification_time_foodmarks":
                qrtime = findPreference("pref_key_notification_time_foodmarks");
                String value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_foodmarks", "-");
                qrtime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));
                NotificationAlarmHandler.updateFoodmarkAlarm();
                break;
            case "pref_key_notification_time_test":
                testtime = findPreference("pref_key_notification_time_test");
                value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_test", "-");
                testtime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));
                NotificationAlarmHandler.updateKlausurAlarm();
                break;
            case "pref_key_notification_time_survey":
                surveytime = findPreference("pref_key_notification_time_survey");
                value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_survey", "-");
                surveytime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));
                NotificationAlarmHandler.updateMoodAlarm();
                break;
            case "pref_key_notification_time_schedule":
                scheduletime = findPreference("pref_key_notification_time_schedule");
                value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_schedule", "-");
                scheduletime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));
                NotificationAlarmHandler.updateTimetableAlarm();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initPreferenceChanges() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference qrtime = findPreference("pref_key_notification_time_foodmarks");
        qrtime.setEnabled(getPreferenceScreen().getSharedPreferences().getBoolean("pref_key_notification_essensqr", false));

        Preference testtime = findPreference("pref_key_notification_time_test");
        testtime.setEnabled(getPreferenceScreen().getSharedPreferences().getBoolean("pref_key_notification_test", false));

        Preference surveytime = findPreference("pref_key_notification_time_survey");
        surveytime.setEnabled(getPreferenceScreen().getSharedPreferences().getBoolean("pref_key_notification_survey", false));

        Preference scheduletime = findPreference("pref_key_notification_time_schedule");
        scheduletime.setEnabled(getPreferenceScreen().getSharedPreferences().getBoolean("pref_key_notification_schedule", false));
    }

    private void initPreferenceSummaries() {
        Preference qrtime = findPreference("pref_key_notification_time_foodmarks");
        String value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_foodmarks", "-");
        if(!value.equals("-"))
            qrtime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));

        Preference testtime = findPreference("pref_key_notification_time_test");
        value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_test", "-");
        if(!value.equals("-"))
            testtime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));

        Preference surveytime = findPreference("pref_key_notification_time_survey");
        value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_survey", "-");
        if(!value.equals("-"))
            surveytime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));

        Preference scheduletime = findPreference("pref_key_notification_time_schedule");
        value = getPreferenceScreen().getSharedPreferences().getString("pref_key_notification_time_schedule", "-");
        if(!value.equals("-"))
            scheduletime.setSummary(Utils.getContext().getString(R.string.hours_settings_template, toHourFormat(value)));
    }

    private String toHourFormat(String s) {
        String[] parts = s.split(":");

        if(parts[0].length() != 2)
            parts[0] = 0+parts[0];
        if(parts[1].length() != 2)
            parts[1] = 0+parts[1];

        return parts[0]+":"+parts[1];
    }


    private void initToolbar() {
        Toolbar actionBar = findViewById(R.id.toolbar);
        actionBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionBar.setTitle(R.string.title_settings_notifications);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    @NonNull
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerNotificationPreferenceActivity(null);
    }

    private ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }
}

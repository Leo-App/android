package de.slg.leoapp;

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

@SuppressWarnings("deprecation")
public class NotificationPreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private AppCompatDelegate mDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);

        addPreferencesFromResource(R.xml.preferences_notifications);

        findViewById(R.id.progressBar2).setVisibility(View.GONE);

        initToolbar();
        initPreferenceChanges();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_key_notification_essensqr":
                Preference qrtime = findPreference("pref_key_notification_time_foodmarks");
                qrtime.setEnabled(sharedPreferences.getBoolean(key, false));
                break;
            case "pref_key_notification_test":
                Preference testtime = findPreference("pref_key_notification_time_test");
                testtime.setEnabled(sharedPreferences.getBoolean(key, false));
                break;
            case "pref_key_notification_survey":
                Preference surveytime = findPreference("pref_key_notification_time_survey");
                surveytime.setEnabled(sharedPreferences.getBoolean(key, false));
                break;
            case "pref_key_notification_schedule":
                Preference scheduletime = findPreference("pref_key_notification_time_schedule");
                scheduletime.setEnabled(sharedPreferences.getBoolean(key, false));
                break;
        }

        NotificationService.getTimes();
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

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbarSettings);
        actionBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionBar.setTitle(getString(R.string.title_settings_notifications));
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
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

package de.slgdev.leoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.slgdev.essensbons.activity.EssensbonActivity;
import de.slgdev.essensbons.utility.EssensbonUtils;
import de.slgdev.klausurplan.activity.KlausurplanActivity;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.service.ReceiveService;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.activity.MessengerActivity;
import de.slgdev.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slgdev.startseite.activity.MainActivity;
import de.slgdev.stimmungsbarometer.activity.StimmungsbarometerActivity;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.stundenplan.activity.StundenplanActivity;
import de.slgdev.umfragen.activity.SurveyActivity;

@SuppressWarnings("deprecation")
public class PreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences pref;
    private DrawerLayout      drawerLayout;
    private NavigationView    navigationView;

    private AppCompatDelegate mDelegate; //Downwards compatibility

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Utils.getController().registerPreferenceActivity(this);

        addPreferencesFromResource(R.xml.preferences_overview);

        initToolbar();
        initNavigationView();
        initPreferenceChanges();
        initNotificationPreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        initNotificationPreference();
        navigationView.setCheckedItem(R.id.settings);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference        connectionPref;
        SharedPreferences pref = getPreferenceScreen().getSharedPreferences();

        switch (key) {
            case "pref_key_qr_autofade":
                connectionPref = findPreference("pref_key_qr_autofade_time");
                connectionPref.setEnabled(sharedPreferences.getBoolean(key, false));
                break;
            case "pref_key_filter_subst":
                findPreference("pref_key_filterby_level").setEnabled(pref.getBoolean("pref_key_filter_subst", false));
                findPreference("pref_key_filterby_schedule").setEnabled(pref.getBoolean("pref_key_filter_subst", false));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerPreferenceActivity(null);
    }

    private void initNotificationPreference() {
        if (pref.getBoolean("pref_key_notification_essensqr", true)
                && pref.getBoolean("pref_key_notification_test", false)
                && pref.getBoolean("pref_key_notification_messenger", true)
                && pref.getBoolean("pref_key_notification_news", true)
                && pref.getBoolean("pref_key_notification_survey", false)
                && pref.getBoolean("pref_key_notification_schedule", false))
            findPreference("pref_key_notifications").setSummary(getString(R.string.settings_title_notification_all));
        else if (!pref.getBoolean("pref_key_notification_essensqr", true)
                && !pref.getBoolean("pref_key_notification_test", false)
                && !pref.getBoolean("pref_key_notification_messenger", true)
                && !pref.getBoolean("pref_key_notification_news", true)
                && !pref.getBoolean("pref_key_notification_survey", false)
                && !pref.getBoolean("pref_key_notification_schedule", false))
            findPreference("pref_key_notifications").setSummary(getString(R.string.settings_title_notification_none));
        else
            findPreference("pref_key_notifications").setSummary(getString(R.string.settings_title_notification_custom));
    }

    private void initPreferenceChanges() {
        pref = getPreferenceScreen().getSharedPreferences();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        findPreference("pref_key_version_app").setSummary(Utils.getAppVersionName());

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference connectionPref = findPreference("pref_key_qr_autofade_time");
        connectionPref.setEnabled(getPreferenceScreen().getSharedPreferences().getBoolean("pref_key_qr_autofade", false));
        getPreferenceScreen()
                .getSharedPreferences()
                .edit()
                .putBoolean("pref_key_status_loggedin",
                        getPreferenceScreen()
                                .getSharedPreferences()
                                .getBoolean("pref_key_status_loggedin", false))
                .apply();

        Preference syncPref = findPreference("pref_key_sync_messenger");
        syncPref.setOnPreferenceClickListener(preference -> {
            Utils.getController().getMessengerDatabase().clear();
            Intent intent = new Intent(
                    getApplicationContext(),
                    ReceiveService.class
            );
            stopService(intent);
            startService(intent);
            return Utils.isNetworkAvailable();
        });

        Preference email = findPreference("pref_key_email");
        email.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app@leo-ac.de"});
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        });

        Preference notifications = findPreference("pref_key_notifications");
        notifications.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getApplicationContext(), NotificationPreferenceActivity.class));
            return true;
        });

        Preference version = findPreference("pref_key_version_app");
        version.setOnPreferenceClickListener(preference -> {
            AlertDialog dialog = new AlertDialog.Builder(PreferenceActivity.this).create();
            View        view   = getLayoutInflater().inflate(R.layout.dialog_changelog, null);
            ((TextView) view.findViewById(R.id.version_textview)).setText(Utils.getAppVersionName());
            dialog.setView(view);
            dialog.show();
            return true;
        });

        Preference about = findPreference("pref_key_about");
        about.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            return true;
        });

        Preference logout = findPreference("pref_key_logout");
        logout.setEnabled(EssensbonUtils.isLoggedIn());
        logout.setOnPreferenceClickListener(preference -> {
            EssensbonUtils.setLoginStatus(false);
            GraphicUtils.sendToast(R.string.logout_confirm);
            logout.setEnabled(false);
            return true;
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(R.string.title_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initNavigationView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();

            Intent i;
            switch (menuItem.getItemId()) {
                case R.id.foodmarks:
                    i = new Intent(getApplicationContext(), EssensbonActivity.class);
                    break;
                case R.id.messenger:
                    i = new Intent(getApplicationContext(), MessengerActivity.class);
                    break;
                case R.id.newsboard:
                    i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                    break;
                case R.id.stundenplan:
                    i = new Intent(getApplicationContext(), StundenplanActivity.class);
                    break;
                case R.id.barometer:
                    i = new Intent(getApplicationContext(), StimmungsbarometerActivity.class);
                    break;
                case R.id.klausurplan:
                    i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                    break;
                case R.id.startseite:
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case R.id.umfragen:
                    i = new Intent(getApplicationContext(), SurveyActivity.class);
                    break;
                case R.id.settings:
                    return true;
                case R.id.profile:
                    i = new Intent(getApplicationContext(), ProfileActivity.class);
                    break;
                case R.id.about:
                    i = new Intent(getApplicationContext(), InfoActivity.class);
                    break;
                default:
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
            if (i != null)
                startActivity(i);
            return true;
        });

        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(StimmungsbarometerUtils.getCurrentMoodRessource());
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

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
package de.slg.leoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.slg.essensbons.activity.EssensQRActivity;
import de.slg.essensbons.utility.Authenticator;
import de.slg.klausurplan.activity.KlausurplanActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.activity.MessengerActivity;
import de.slg.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slg.startseite.activity.MainActivity;
import de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity;
import de.slg.stundenplan.activity.StundenplanActivity;
import de.slg.umfragen.activity.SurveyActivity;

@SuppressWarnings("deprecation")
public class PreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private ProgressBar       progressBar;
    private SharedPreferences pref;
    private DrawerLayout      drawerLayout;
    private NavigationView    navigationView;

    private AppCompatDelegate mDelegate; //Downwards compatibility

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

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
            case "pref_key_qr_id":
                if (!sharedPreferences.getString("pref_key_qr_id", "").matches("[0-9]{5}")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalidId), Toast.LENGTH_LONG).show();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    preferences.edit()
                            .putString("pref_key_qr_id", "")
                            .apply();
                    connectionPref = findPreference(key);
                    connectionPref.setSummary(getString(R.string.settings_summary_customid));
                } else {
                    connectionPref = findPreference(key);
                    connectionPref.setSummary(sharedPreferences.getString(key, ""));
                    progressBar.setVisibility(View.VISIBLE);
                    PreferenceTask t = new PreferenceTask();
                    t.execute();
                }
                break;
            case "pref_key_qr_pw":
                PreferenceTask t = new PreferenceTask();
                progressBar.setVisibility(View.VISIBLE);
                connectionPref = findPreference(key);
                connectionPref.setSummary(getRepl(sharedPreferences.getString(key, "passwort")));
                t.execute();
                break;
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

    private void initPreferenceChanges() {
        pref = getPreferenceScreen().getSharedPreferences();
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        hideProgressBar();

        findPreference("pref_key_version_app").setSummary(Utils.getAppVersionName());

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (!getPreferenceScreen().getSharedPreferences().getString("pref_key_qr_id", "").equals("")) {
            Preference connectionPref = findPreference("pref_key_qr_id");
            connectionPref.setSummary(getPreferenceScreen().getSharedPreferences().getString("pref_key_qr_id", ""));
        }

        if (!getPreferenceScreen().getSharedPreferences().getString("pref_key_qr_pw", "").equals("")) {
            Preference connectionPref = findPreference("pref_key_qr_pw");
            connectionPref.setSummary(getRepl(getPreferenceScreen().getSharedPreferences().getString("pref_key_qr_pw", "passwort")));
        }

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
        syncPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.getController().getMessengerDatabase().clear();
                return Utils.checkNetwork();
            }
        });

        Preference email = findPreference("pref_key_email");
        email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app@leo-ac.de"});
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            }
        });

        Preference notifications = findPreference("pref_key_notifications");
        notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getApplicationContext(), NotificationPreferenceActivity.class));
                return true;
            }
        });

        Preference about = findPreference("pref_key_about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                return true;
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(R.string.title_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.getMenu().findItem(R.id.newsboard).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.foodmarks).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.barometer).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.umfragen).setEnabled(Utils.isVerified());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();

                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.foodmarks:
                        i = new Intent(getApplicationContext(), EssensQRActivity.class);
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
                    default:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (i != null)
                    startActivity(i);
                return true;
            }
        });

        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.utility.Utils.getCurrentMoodRessource());
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

    private String getRepl(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            b.append("*");
        }
        return b.toString();
    }

    private void showSnackbar() {
        final Snackbar cS = Snackbar.make(findViewById(R.id.coords), R.string.snackbar_no_connection_info_check, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void showSnackbar2() {
        final Snackbar cS = Snackbar.make(findViewById(R.id.coords), R.string.snackbar_not_correct_info, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }

    private void setLogin(boolean b) {
        getPreferenceScreen()
                .getSharedPreferences()
                .edit()
                .putBoolean("pref_key_status_loggedin", b)
                .apply();
    }

    public void hideProgressBar() {
        findViewById(R.id.progressBar2).setVisibility(View.GONE);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    private class PreferenceTask extends AsyncTask<Void, Void, Authenticator> {
        @Override
        protected Authenticator doInBackground(Void... params) {
            if (hasActiveInternetConnection()) {
                String pw = getPreferenceScreen().getSharedPreferences().getString("pref_key_qr_pw", "");
                try {
                    byte[]         contents = pw.getBytes("UTF-8");
                    MessageDigest  md       = MessageDigest.getInstance("MD5");
                    byte[]         enc      = md.digest(contents);
                    BufferedReader in;
                    String         md5      = bytesToHex(enc);
                    Log.d("LeoApp", md5);
                    URL interfaceDB = new URL(Utils.BASE_URL_PHP + "essenqr/qr_checkval.php?id=" + pref.getString("pref_key_qr_id", "00000") + "&auth=RW6SlQ&pw=" + md5);
                    Log.d("LeoApp", interfaceDB.toString());
                    in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("true")) {
                            Log.d("LeoApp", "valid");
                            return Authenticator.VALID;
                        }
                        if (inputLine.contains("false")) {
                            Log.d("LeoApp", "invalid");
                            return Authenticator.NOT_VALID;
                        }
                    }
                    in.close();
                } catch (NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                }
            } else
                return Authenticator.NO_CONNECTION;
            return Authenticator.NOT_VALID;
        }

        boolean hasActiveInternetConnection() {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.lunch.leo-ac.de").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public void onPostExecute(Authenticator result) {
            progressBar.setVisibility(View.INVISIBLE);
            switch (result) {
                case VALID:
                    setLogin(true);
                    Toast t = Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_LONG);
                    t.show();
                    break;
                case NOT_VALID:
                    setLogin(false);
                    showSnackbar2();
                    break;
                case NO_CONNECTION:
                    showSnackbar();
            }
        }
    }
}
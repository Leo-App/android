package de.slg.essensqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.NotificationService;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

@SuppressLint("StaticFieldLeak")
public class EssensQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static SharedPreferences sharedPref;
    public static SQLiteHandler     sqlh;
    public static Button            scan;
    public static boolean runningSync, mensaModeRunning = false;
    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0;
    public  ZXingScannerView     scV;
    private ViewPager            mViewPager;
    private FragmentPagerAdapter adapt;
    private boolean              runningScan;
    private DrawerLayout         drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_qr);
        Utils.registerEssensQRActivity(this);

        runningScan = false;
        runningSync = false;

        initToolbar();
        initNavigationView();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapt = new FragmentPagerAdapter(getSupportFragmentManager()) {

            private final QRFragment fragment1 = new QRFragment();
            private final ScanFragment fragment2 = new ScanFragment();

            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return fragment1;
                else
                    return fragment2;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return getString(R.string.title_foodmarks);
                else
                    return getString(R.string.toolbar_scan);
            }
        };
        mViewPager.setAdapter(adapt);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);

        sqlh = new SQLiteHandler(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scan();
                    }
                });
            }
        };
        handler.postDelayed(r, 100);

        if (!mensaModeRunning && sharedPref.getBoolean("pref_key_mensa_mode", false)) {
            handler.removeCallbacks(r);
            mensaModeRunning = true;
            scan();
        } else
            mensaModeRunning = false;
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.foodmarks).setChecked(true);

        navigationView.getMenu().findItem(R.id.newsboard).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.foodmarks:
                        return true;
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
                        i = null;
                        break;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
                    default:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (i != null)
                    startActivity(i);
                finish();
                return true;
            }
        });

        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_title));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void scan() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            runningScan = true;
            scV = new ZXingScannerView(getApplicationContext());
            setContentView(scV);
            scV.setResultHandler(this);
            int cameraNumber = sharedPref.getBoolean("pref_key_qr_camera", false) ? 1 : 0;
            scV.startCamera(cameraNumber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationService.ID_ESSENSQR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.essensbons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            ((QRFragment) adapt.getItem(0)).synchronize(false);
            mViewPager.setCurrentItem(0);
        } else if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            SQLitePrinter.printDatabase(getApplicationContext());
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("LeoApp", "OnKeyDown");
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (runningScan) {
                runningScan = false;
                scV.stopCamera();
                finish();
                startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
                return false;
            }
            return super.onKeyDown(keyCode, event);
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (scV != null && scV.isActivated()) {
            scV.stopCamera();
            finish();
            startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
        } else {
            super.onPause();
        }
    }

    @Override
    public void handleResult(Result result) {
        QRReadTask task = new QRReadTask(this);
        scV.stopCamera();
        task.execute(result.getText());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runningScan = true;
                    scV = new ZXingScannerView(getApplicationContext());
                    setContentView(scV);
                    scV.setResultHandler(this);
                    int cameraNumber = sharedPref.getBoolean("pref_key_qr_camera", false) ? 1 : 0;
                    scV.startCamera(cameraNumber);
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        Utils.registerEssensQRActivity(null);
    }
}
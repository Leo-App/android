package de.slg.essensbons.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.zxing.Result;

import de.slg.essensbons.activity.fragment.QRFragment;
import de.slg.essensbons.activity.fragment.ScanFragment;
import de.slg.essensbons.task.QRReadTask;
import de.slg.leoapp.R;
import de.slg.leoapp.dialog.InformationDialog;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.sqlite.SQLiteConnectorEssensbons;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

@SuppressLint("StaticFieldLeak")
public class EssensQRActivity extends LeoAppFeatureActivity implements ZXingScannerView.ResultHandler {
    public static SharedPreferences         sharedPref;
    public static SQLiteConnectorEssensbons sqlh;
    public static Button                    scan;
    public static boolean runningSync, mensaModeRunning = false;
    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0;
    public  ZXingScannerView     scV;
    private ViewPager            mViewPager;
    private FragmentPagerAdapter adapt;
    private boolean              runningScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InformationDialog(this).setText(R.string.dialog_qrfoodmarks).show();

        Utils.getController().registerEssensQRActivity(this);

        runningScan = false;
        runningSync = false;

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

        sqlh = new SQLiteConnectorEssensbons(getApplicationContext());
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

    @Override
    protected int getContentView() {
        return R.layout.activity_wrapper_qr;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.toolbar_title;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.foodmarks;
    }

    @Override
    protected String getActivityTag() {
        return "QRActivity";
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
        Utils.getNotificationManager().cancel(NotificationHandler.ID_ESSENSBONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.essensbons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_refresh) {
            ((QRFragment) adapt.getItem(0)).synchronize(false);
            mViewPager.setCurrentItem(0);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        Utils.getController().registerEssensQRActivity(null);
    }
}
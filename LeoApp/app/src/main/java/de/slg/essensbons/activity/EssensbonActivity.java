package de.slg.essensbons.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.view.WindowManager;

import com.google.zxing.Result;

import de.slg.essensbons.activity.fragment.QRFragment;
import de.slg.essensbons.activity.fragment.ScanFragment;
import de.slg.essensbons.intro.EssensbonIntroActivity;
import de.slg.essensbons.task.EssensbonLoginTask;
import de.slg.essensbons.task.QRReadTask;
import de.slg.essensbons.utility.Authenticator;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.leoapp.R;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class EssensbonActivity extends LeoAppFeatureActivity implements ZXingScannerView.ResultHandler, TaskStatusListener {

    public static boolean mensaModeRunning;

    private ZXingScannerView     scannerView;
    private ViewPager            viewPager;
    private FragmentPagerAdapter adapt;

    private boolean runningScan;
    private boolean runningSync;

    static {
        mensaModeRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerEssensbonActivity(this);

        runningScan = false;
        runningSync = false;

        initFragments();
        initIntro();

        if (!mensaModeRunning && EssensbonUtils.mensaModeEnabled()) {
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
            viewPager.setCurrentItem(0);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (runningScan) {
                runningScan = false;
                scannerView.stopCamera();
                finish();
                startActivity(new Intent(getApplicationContext(), EssensbonActivity.class));
                return false;
            }
            return super.onKeyDown(keyCode, event);
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (scannerView != null && scannerView.isActivated()) {
            scannerView.stopCamera();
            finish();
            startActivity(new Intent(getApplicationContext(), EssensbonActivity.class));
        } else {
            super.onPause();
        }
    }

    @Override
    public void handleResult(Result result) {
        QRReadTask task = new QRReadTask();
        scannerView.stopCamera();
        task.execute(result.getText());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    runningScan = true;
                    scannerView = new ZXingScannerView(getApplicationContext());
                    setContentView(scannerView);
                    scannerView.setResultHandler(this);
                    scannerView.startCamera(EssensbonUtils.getPreferredCamera());
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerEssensbonActivity(null);
    }

    @Override
    public void taskFinished(Object... result) {
        if (result[0] == Authenticator.NOT_VALID)
            startActivity(new Intent(this, EssensbonIntroActivity.class));
    }

    public ZXingScannerView getScannerView() {
        return scannerView;
    }

    public boolean isRunningSync() {
        return runningSync;
    }

    public void stopRunningSync() {
        runningSync = false;
    }

    public void startRunningSync() {
        runningSync = true;
    }

    public void scan() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            runningScan = true;
            scannerView = new ZXingScannerView(getApplicationContext());
            setContentView(scannerView);
            scannerView.setResultHandler(this);
            scannerView.startCamera(EssensbonUtils.getPreferredCamera());
        }
    }

    private void initIntro() {
        if (!Utils.getController().getPreferences().getBoolean("intro_shown_qr", true))
            startActivity(new Intent(this, EssensbonIntroActivity.class).putExtra("explanation", true));
        else if (EssensbonUtils.isLoggedIn())
            startActivity(new Intent(this, EssensbonIntroActivity.class));
        else
            initCredentialCheck();
    }

    private void initFragments() {
        viewPager = findViewById(R.id.pager);
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
        viewPager.setAdapter(adapt);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initCredentialCheck() {
        EssensbonLoginTask task = new EssensbonLoginTask();
        task.addListener(this).execute();
    }
}
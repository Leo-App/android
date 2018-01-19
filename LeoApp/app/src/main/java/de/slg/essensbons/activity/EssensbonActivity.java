package de.slg.essensbons.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;

import de.slg.essensbons.activity.fragment.QRFragment;
import de.slg.essensbons.activity.fragment.ScanFragment;
import de.slg.essensbons.intro.EssensbonIntroActivity;
import de.slg.essensbons.task.EssensbonLoginTask;
import de.slg.essensbons.utility.Authenticator;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.leoapp.R;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;

public class EssensbonActivity extends LeoAppFeatureActivity implements TaskStatusListener {

    public static boolean mensaModeRunning;

    private ViewPager            viewPager;
    private FragmentPagerAdapter adapt;

    private boolean runningSync;

    static {
        mensaModeRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerEssensbonActivity(this);

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
    public void finish() {
        super.finish();
        Utils.getController().registerEssensbonActivity(null);
    }

    @Override
    public void taskFinished(Object... result) {
        if (result[0] == Authenticator.NOT_VALID)
            startActivity(new Intent(this, EssensbonIntroActivity.class));
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

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCameraId(EssensbonUtils.getPreferredCamera());
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt("");
        integrator.initiateScan();

    }

    private void initIntro() {
        if (!Utils.getController().getPreferences().getBoolean("intro_shown_qr", true))
            startActivity(new Intent(this, EssensbonIntroActivity.class).putExtra("explanation", true));
        else if (!EssensbonUtils.isLoggedIn())
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
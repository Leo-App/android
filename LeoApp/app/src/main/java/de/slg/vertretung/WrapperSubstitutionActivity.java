package de.slg.vertretung;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.slg.essensbons.activity.EssensbonActivity;
import de.slg.klausurplan.activity.KlausurplanActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.activity.PreferenceActivity;
import de.slg.leoapp.activity.ProfileActivity;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.messenger.activity.MessengerActivity;
import de.slg.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slg.startseite.activity.MainActivity;
import de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity;
import de.slg.stundenplan.activity.StundenplanActivity;

public class WrapperSubstitutionActivity extends ActionLogActivity {

    private DrawerLayout           drawerLayout;
    private SubstitutionFragment[] fragments;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_wrapper_subst);
        initToolbar();
        initTabs();
        initNavigationView();
    }

    @Override
    protected String getActivityTag() {
        return "WrapperSubstitutionActivity";
    }

    private void initTabs() {
        ViewPager viewPager = findViewById(R.id.pagerS);
        TabLayout tabLayout = findViewById(R.id.tablayoutS);
        Date      d         = new Date();
        Calendar  c         = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(d);
        c.add(Calendar.DATE, 1);
        Date dT = c.getTime();
        fragments = new SubstitutionFragment[]{new SubstitutionFragment().setDate(d), new SubstitutionFragment().setDate(dT)};
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getTabString(position == 0);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbarS);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_subst));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        navigationView.getMenu().findItem(R.id.barometer).setChecked(true);
        //        navigationView.getMenu().findItem(R.id.nachhilfe).setEnabled(Utils.isVerified());
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
                        i = new Intent(getApplicationContext(), EssensbonActivity.class);
                        break;
                    case R.id.messenger:
                        i = new Intent(getApplicationContext(), MessengerActivity.class);
                        break;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
                    //                    case R.id.nachhilfe:
                    //                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
                    //                        break;
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
                    //                    case R.id.vertretung:
                    //                        return true;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
                    case R.id.profile:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
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
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());
        ImageView mood = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.utility.Utils.getCurrentMoodRessource());
    }

    private String getTabString(boolean today) {
        Date     d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(d);
        if (!today)
            c.add(Calendar.DATE, 1);
        return new String[]{"SO", "MO", "DI", "MI", "DO", "FR", "SA"}[c.get(Calendar.DAY_OF_WEEK) - 1] + ". " + c.get(Calendar.DAY_OF_MONTH) + "."
                + c.get(Calendar.MONTH) + "."
                + c.get(Calendar.YEAR);
    }
}

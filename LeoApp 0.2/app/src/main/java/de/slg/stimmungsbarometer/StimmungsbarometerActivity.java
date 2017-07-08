package de.slg.stimmungsbarometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class StimmungsbarometerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    static boolean drawIch = true, drawSchueler = true, drawLehrer = true, drawAlle = true;

    private static Ergebnis[][] daten;

    private ZeitraumFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_stimmungsbarometer);

        //initBottomNavigationView();
        initToolbar();
        initTabs();
        initNavigationView();
        initLayouts();
    }

    private void initLayouts() {
        LinearLayout lI = (LinearLayout) findViewById(R.id.linearLayoutIch);
        LinearLayout lS = (LinearLayout) findViewById(R.id.linearLayoutSchueler);
        LinearLayout lL = (LinearLayout) findViewById(R.id.linearLayoutLehrer);
        LinearLayout lA = (LinearLayout) findViewById(R.id.linearLayoutAlle);

        lI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewIch);
                drawIch = !drawIch;
                if (drawIch)
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVerySatisfied));
                else
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                updateFragments();
            }
        });
        lS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewSchueler);
                drawSchueler = !drawSchueler;
                if (drawSchueler)
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNeutral));
                else
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                updateFragments();
            }
        });
        lL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewLehrer);
                drawLehrer = !drawLehrer;
                if (drawLehrer)
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_purple));
                else
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                updateFragments();
            }
        });
        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewAlle);
                drawAlle = !drawAlle;
                if (drawAlle)
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                else
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                updateFragments();
            }
        });
    }

    private void updateFragments() {
        fragments[0].update();
        fragments[1].update();
        fragments[2].update();
        fragments[3].update();
    }

    private void initTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragments = new ZeitraumFragment[]{new ZeitraumFragment(), new ZeitraumFragment(), new ZeitraumFragment(), new ZeitraumFragment()};
        fragments[0].zeitraum = 0;
        fragments[1].zeitraum = 1;
        fragments[2].zeitraum = 2;
        fragments[3].zeitraum = 3;
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Letzte Woche");
        tabLayout.getTabAt(1).setText("Letzter Monat");
        tabLayout.getTabAt(2).setText("Letztes Jahr");
        tabLayout.getTabAt(3).setText("Gesamt");
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarStatistik);
        myToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_survey));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView.getMenu().findItem(R.id.barometer).setChecked(true);

        navigationView.getMenu().findItem(R.id.nachhilfe).setEnabled(Utils.isVerified());
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
                        i = new Intent(getApplicationContext(), WrapperQRActivity.class);
                        break;
                    case R.id.messenger:
                        i = new Intent(getApplicationContext(), OverviewWrapper.class);
                        break;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
                    case R.id.nachhilfe:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), WrapperStundenplanActivity.class);
                        break;
                    case R.id.barometer:
                        return true;
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
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    public static Ergebnis[][] empfangeDaten() {
        if (daten == null) {
            EmpfangeDaten empfangeDaten = new EmpfangeDaten(Utils.getUserID());
            empfangeDaten.execute();
            try {
                daten = empfangeDaten.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return daten;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}
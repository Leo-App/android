package de.slg.stundenplan;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.nachhilfe.NachhilfeboerseActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;

public class WrapperStundenplanActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    public static String akTag;
    public static String akStunde;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!this.fileExistiert()) {
            Log.e("Luzzzia", "Meine Fächer existiert nicht");
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
        }
        Log.e("Luzzzia", "Meine Fächer existiert");

        setContentView(R.layout.activity_wrapper_stundenplan);

        initToolbar();
        initNavigationView();
        initTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stundenplan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
        } else if (item.getItemId() == R.id.action_picture) {
            startActivity(new Intent(getApplicationContext(), StundenplanActivity.class));
        } else if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.stundenplan).setChecked(true);

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
                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
                        break;
                    case R.id.stundenplan:
                        return true;
                    case R.id.barometer:
                        i = new Intent(getApplicationContext(), StimmungsbarometerActivity.class);
                        break;
                    case R.id.klausurplan:
                        i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                        break;
                    case R.id.startseite:
                        i = null;
                        break;
                    case R.id.vertretung:
                        i = new Intent(getApplicationContext(), WrapperSubstitutionActivity.class);
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
        grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_plan));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initTabs() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FragmentMontag();
                    case 1:
                        return new FragmentDienstag();
                    case 2:
                        return new FragmentMittwoch();
                    case 3:
                        return new FragmentDonnerstag();
                    case 4:
                        return new FragmentFreitag();
                    default:
                        return new FragmentMontag();
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.mo);
                    case 1:
                        return getString(R.string.di);
                    case 2:
                        return getString(R.string.mi);
                    case 3:
                        return getString(R.string.don);
                    case 4:
                        return getString(R.string.fr);
                    default:
                        return null;
                }
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.viPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private boolean fileExistiert() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("meinefaecher.txt")));
            if (br.readLine() != null) {
                br.close();
                return true;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deexistiere() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("meinefaecher.txt", MODE_PRIVATE)));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package de.slg.stimmungsbarometer;

import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.GregorianCalendar;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.nachhilfe.NachhilfeboerseActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;

public class StimmungsbarometerActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ZeitraumFragment[] fragments;

    private static Ergebnis[][] daten;
    static boolean drawIch = true, drawSchueler = true, drawLehrer = true, drawAlle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_stimmungsbarometer);

        if (daten == null) {
            daten = new Ergebnis[4][0];
            new EmpfangeDaten().execute();
            initToolbar();
            initTabs();
            initNavigationView();
            initLayouts();
        } else {
            initToolbar();
            initTabs();
            initNavigationView();
            initLayouts();
            for (ZeitraumFragment fragment : fragments) {
                fragment.fillData();
                fragment.update();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
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
                ImageView i = (ImageView) v.findViewById(R.id.imageViewIch);
                drawIch = !drawIch;
                if (drawIch) {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorIch));
                    i.setImageResource(R.color.colorIch);
                } else {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                    i.setImageResource(R.color.colorInactive);
                }
                updateFragments();
            }
        });
        lS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewSchueler);
                ImageView i = (ImageView) v.findViewById(R.id.imageViewSchueler);
                drawSchueler = !drawSchueler;
                if (drawSchueler) {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSchueler));
                    i.setImageResource(R.color.colorSchueler);
                } else {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                    i.setImageResource(R.color.colorInactive);
                }
                updateFragments();
            }
        });
        lL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewLehrer);
                ImageView i = (ImageView) v.findViewById(R.id.imageViewLehrer);
                drawLehrer = !drawLehrer;
                if (drawLehrer) {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorLehrer));
                    i.setImageResource(R.color.colorLehrer);
                } else {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                    i.setImageResource(R.color.colorInactive);
                }
                updateFragments();
            }
        });
        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.textViewAlle);
                ImageView i = (ImageView) v.findViewById(R.id.imageViewAlle);
                drawAlle = !drawAlle;
                if (drawAlle) {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlle));
                    i.setImageResource(R.color.colorAlle);
                } else {
                    t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInactive));
                    i.setImageResource(R.color.colorInactive);
                }
                updateFragments();
            }
        });
    }

    private void updateFragments() {
        for (ZeitraumFragment fragment : fragments)
            fragment.update();
    }

    private void initTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragments = new ZeitraumFragment[4];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new ZeitraumFragment();
            fragments[i].zeitraum = i;
            fragments[i].fillData();
        }
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
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
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
                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
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

    public static Ergebnis[][] getData() {
        return daten;
    }

    private class EmpfangeDaten extends AsyncTask<Void, Void, Void> {
        private String[] splitI, splitS, splitL, splitA;

        EmpfangeDaten() {
            splitI = new String[0];
            splitS = new String[0];
            splitL = new String[0];
            splitA = new String[0];
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new URL("http://moritz.liegmanns.de/stimmungsbarometer/ergebnisse.php?key=5453&userid=" + Utils.getUserID())
                                                .openConnection()
                                                .getInputStream(), "UTF-8"));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                String[] e = builder.toString().split("_abschnitt_");
                reader.close();
                if (!e[0].equals("."))
                    splitI = e[0].split("_next_");
                if (!e[1].equals("."))
                    splitS = e[1].split("_next_");
                if (!e[2].equals("."))
                    splitL = e[2].split("_next_");
                if (!e[3].equals("."))
                    splitA = e[3].split("_next_");
                Ergebnis[][] ergebnisse = new Ergebnis[4][];
                ergebnisse[0] = new Ergebnis[splitI.length];
                ergebnisse[1] = new Ergebnis[splitS.length];
                ergebnisse[2] = new Ergebnis[splitL.length];
                ergebnisse[3] = new Ergebnis[splitA.length];
                for (int i = 0; i < ergebnisse[0].length; i++) {
                    String[] current = splitI[i].split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        ergebnisse[0][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), true, false, false, false);
                    }
                }
                for (int i = 0; i < ergebnisse[1].length; i++) {
                    String[] current = splitS[i].split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        ergebnisse[1][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, true, false, false);
                    }
                }
                for (int i = 0; i < ergebnisse[2].length; i++) {
                    String[] current = splitL[i].split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        ergebnisse[2][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, false, true, false);
                    }
                }
                for (int i = 0; i < ergebnisse[3].length; i++) {
                    String[] current = splitA[i].split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        ergebnisse[3][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, false, false, true);
                    }
                }
                daten = ergebnisse;
                for (ZeitraumFragment f : fragments)
                    f.fillData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            updateFragments();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
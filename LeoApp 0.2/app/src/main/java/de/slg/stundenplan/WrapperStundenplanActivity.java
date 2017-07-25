package de.slg.stundenplan;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
    private WochentagFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!this.fileExistiert()) {
            Log.e("Luzzzia", "Meine Fächer existiert nicht");
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
        } else
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
        fragments = new WochentagFragment[5];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new WochentagFragment();
            fragments[i].setTag(i + 1);
        }

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
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

    public static class WochentagFragment extends Fragment {
        private Fach[] fachArray;
        private int tag;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
            View v = layIn.inflate(R.layout.fragment_wochentag, container, false);

            listView = (ListView) v.findViewById(R.id.listW);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (fachArray[position].id <= 0) {
                        Utils.getStundDB().freistunde(tag, position + 1);
                        fachArray[position] = Utils.getStundDB().getFach(tag, position + 1);
                        view.invalidate();
                    }
                    startActivity(new Intent(getContext(), DetailsActivity.class)
                            .putExtra("tag", tag)
                            .putExtra("stunde", position + 1));
                }
            });

            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            fachArray = Utils.getStundDB().gewaehlteFaecherAnTag(tag);
            listView.setAdapter(new StundenAdapter(getContext(), fachArray));
        }

        void setTag(int tag) {
            this.tag = tag;
        }
    }

    private static class StundenAdapter extends ArrayAdapter<Fach> {
        private final Context cont;
        private final Fach[] fachAd;
        private final View[] viAd;

        StundenAdapter(Context pCont, Fach[] pFach) {
            super(pCont, R.layout.list_item_schulstunde, pFach);
            cont = pCont;
            fachAd = pFach;
            viAd = new View[pFach.length];
        }

        @NonNull
        @Override
        public View getView(int position, View v, @NonNull ViewGroup parent) {
            if (position < fachAd.length && fachAd[0] != null) {
                if (v == null) {
                    LayoutInflater layIn = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    int id2 = R.layout.list_item_schulstunde;
                    v = layIn.inflate(id2, null);
                }

                TextView tvFach = (TextView) v.findViewById(R.id.fach_wt);
                TextView tvLehrer = (TextView) v.findViewById(R.id.lehrer_wt);
                TextView tvRaum = (TextView) v.findViewById(R.id.raum_wt);
                TextView tvStunde = (TextView) v.findViewById(R.id.stunde_wt);

                if (fachAd[position] != null) {
                    if (fachAd[position].gibName().equals("") && !fachAd[position].gibNotiz().equals("")) {
                        String[] sa = fachAd[position].gibNotiz().split(" ");
                        tvFach.setText(sa[0]);
                    } else {
                        tvFach.setText(fachAd[position].gibName());
                    }
                    tvLehrer.setText(fachAd[position].gibLehrer());
                    tvRaum.setText(fachAd[position].gibRaum());
                    tvStunde.setText(fachAd[position].gibStundenName());
                    if (fachAd[position].gibSchriftlich()) {
                        v.findViewById(R.id.iconSchriftlich).setVisibility(View.VISIBLE);
                    }
                }
            }
            viAd[position] = v;
            return v;
        }
    }
}
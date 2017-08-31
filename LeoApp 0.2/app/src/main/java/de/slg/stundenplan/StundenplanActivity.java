package de.slg.stundenplan;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;

public class StundenplanActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private WochentagFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_stundenplan);
        Utils.registerStundenplanActivity(this);

        if (!Utils.getStundDB().hatGewaehlt()) {
            if (Utils.getUserPermission() != 2) {
                startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
            } else {
                new CreateLehrerStundenplan().execute();
            }
        }

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
            startActivity(new Intent(getApplicationContext(), StundenplanBildActivity.class));
        } else if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.registerStundenplanActivity(null);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.stundenplan).setChecked(true);

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

    private void refreshUI() {
        for (WochentagFragment f : fragments)
            f.refreshUI();
    }

    public static class WochentagFragment extends Fragment {
        private View root;
        private Fach[] fachArray;
        private int tag;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
            if (root == null) {
                root = layIn.inflate(R.layout.fragment_wochentag, container, false);

                listView = (ListView) root.findViewById(R.id.listW);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (fachArray[position].id <= 0) {
                            Utils.getStundDB().freistunde(tag, position + 1);
                            fachArray[position] = Utils.getStundDB().getFach(tag, position + 1);
                            view.invalidate();
                        }
                        DetailsDialog dialog = new DetailsDialog(getActivity());
                        dialog.show();
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        dialog.init(Utils.getStundDB().getFach(tag, position+1));
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                refreshUI();
                            }
                        });
                    }
                });
            }

            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshUI();
        }

        private void refreshUI() {
            if (listView != null) {
                fachArray = Utils.getStundDB().gewaehlteFaecherAnTag(tag);
                listView.setAdapter(new StundenAdapter(getContext(), fachArray));
            }
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

    private class CreateLehrerStundenplan extends AsyncTask<Void, Void, Void> {
        private AuswahlActivity.FachImporter importer;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            importer = new AuswahlActivity.FachImporter(getApplicationContext(), "");
            importer.execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                importer.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            refreshUI();
        }
    }
}
package de.slgdev.vertretungsplan.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorVertretungsplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.stundenplan.utility.Fach;
import de.slgdev.vertretungsplan.task.ImporterVertretung;

import static de.slgdev.leoapp.utility.Utils.getContext;

/**
 * Created by Benedikt on 13.03.2018.
 */

public class VertretungsplanActivity extends LeoAppNavigationActivity implements TaskStatusListener{



    private ListenFragment fragment1;
    private ListenFragment fragment2;

    private SQLiteConnectorVertretungsplan db;
    private ImporterVertretung importer;

    public ViewPager viewPager;
    public FragmentPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerVertretungsplanActivity(this);

        db = new SQLiteConnectorVertretungsplan(this);

        initSpinner();
        initTabs();

    }


    @Override
    protected String getActivityTag() {
        return "VertretungsplanActivity";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_vertretungsplan;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
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
        return R.string.title_subst;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.vertretungsplan;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db==null)
            db = new SQLiteConnectorVertretungsplan(this);
    }


        @Override
    public void taskStarts()    {
        fragment1.startRefreshing();
        fragment2.startRefreshing();
    }

    @Override
    public void taskFinished(Object... params) {
        if (db != null) {
            fragment1.updateListView(fragment1.filtern(db.gibVertretungsplan(1)));
            fragment2.updateListView(fragment2.filtern(db.gibVertretungsplan(2)));
            int seite = viewPager.getCurrentItem();
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(seite);
        }
        fragment1.stopRefreshing();
        fragment2.stopRefreshing();
    }

    private void initSpinner()  {
        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        SQLiteConnectorStundenplan stundenplan = new SQLiteConnectorStundenplan(this);
        int filterOptionen;
        if (Utils.getUserStufe().equals("TEA"))
            filterOptionen = R.array.filter_options4;
        else if (!stundenplan.hatGewaehlt())
            filterOptionen = R.array.filter_options2;
        else if (Utils.getUserStufe().contains("EF") || Utils.getUserStufe().contains("Q1") || Utils.getUserStufe().contains("Q2"))
            filterOptionen = R.array.filter_options1;
        else
            filterOptionen = R.array.filter_options3;
        Log.d("test1", Utils.getUserStufe());

        ArrayAdapter<CharSequence> spinnerAdaper = ArrayAdapter.createFromResource(this, filterOptionen, R.layout.spinner_header_vertretung);
        spinnerAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdaper);
        filterSpinner.setSelection(Utils.getController().getPreferences().getInt("pref_key_vertretung_filter", 0));
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    Utils.getController().getPreferences().edit().putInt("pref_key_vertretung_filter", 0).apply();
                else if (i==1 && !(Utils.getUserStufe() == "EF" || Utils.getUserStufe() == "Q1" || Utils.getUserStufe() == "Q2"))
                    Utils.getController().getPreferences().edit().putInt("pref_key_vertretung_filter", 1).apply();
                else
                    Utils.getController().getPreferences().edit().putInt("pref_key_vertretung_filter", 2).apply();
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initTabs() {
        fragment1 = ListenFragment.newInstance(1);
        fragment2 = ListenFragment.newInstance(2);

        viewPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return fragment1;
                return fragment2;
            }


            @Override
            public int getCount() {
                return 2;
            }

            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return db.gibDatum(1).replace("Montag", getString(R.string.monday)).
                            replace("Dienstag", getString(R.string.tuesday)).
                            replace("Mittwoch", getString(R.string.wednesday)).
                            replace("Donnerstag", getString(R.string.thursday)).
                            replace("Freitag", getString(R.string.friday));
                else
                    return db.gibDatum(2).replace("Montag", getString(R.string.monday)).
                            replace("Dienstag", getString(R.string.tuesday)).
                            replace("Mittwoch", getString(R.string.wednesday)).
                            replace("Donnerstag", getString(R.string.thursday)).
                            replace("Freitag", getString(R.string.friday));
            }
        };
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        update();
    }


    private void update(){
        importer = new ImporterVertretung();
        importer.addListener(this).execute();
    }

    @Override
    public void finish()    {
        super.finish();
        Utils.getController().registerVertretungsplanActivity(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        db = null;

    }
}

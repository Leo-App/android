package de.slgdev.essensbestellung.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ExpandableListView;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;


/**
 * Created by Florian on 10.03.2018.
 */

public class EssensbestellungActivity extends LeoAppNavigationActivity implements TaskStatusListener{

    /*ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;*/

    WeekFragment week1;
    WeekFragment week2;
    WeekFragment week3;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    bestellungAsyncTask getGerichte;
    HashMap<String, String[]> gerichteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bestellung);

        getGerichte = new bestellungAsyncTask();
        getGerichte.execute();

        prefs = Utils.getController().getPreferences();
        editor = prefs.edit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bestellung;
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
        return R.string.title_foodordering;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.bestellung;
    }

    @Override
    protected String getActivityTag() {
        return "BestellungActivity";
    }


    /*private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Montag");
        listDataHeader.add("Dienstag");
        listDataHeader.add("Mittwoch");
        listDataHeader.add("Donnerstag");

        List<String> mon = new ArrayList<String>();
        mon.add("test");


        listDataChild.put(listDataHeader.get(0), mon);
    }*/

    private void initTabs() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Gerichte", gerichteData);
        week1 = new WeekFragment();
        week1.setArguments(bundle);
        week1.setWoche(1);

        week2 = new WeekFragment();
        week2.setArguments(bundle);
        week2.setWoche(2);

        week3 = new WeekFragment();
        week3.setArguments(bundle);
        week3.setWoche(3);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return week1;
                if (position == 1)
                    return week2;
                return week3;
            }

            @Override
            public int getCount() {
                return 3;
            }

        };

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.icon_messenger);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search);
    }

    @Override
    public void taskFinished(Object... params) {
        try {
            gerichteData = getGerichte.get();
        }
        catch (CancellationException e) {
            Utils.logError(e);
        }
        catch (ExecutionException e) {
            Utils.logError(e);
        }
        catch (InterruptedException e) {
            Utils.logError(e);
        }
        initTabs();
    }
}
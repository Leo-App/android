package de.slgdev.nachhilfeboerse.activity;

import android.os.Bundle;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.nachhilfeboerse.activity.fragments.ExpandableListViewMainActivity;
import de.slgdev.nachhilfeboerse.activity.fragments.NachhilfeboerseActivitymainFragment;
import de.slgdev.nachhilfeboerse.task.SyncMaster;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse;


public class NachhilfeboerseActivity extends LeoAppNavigationActivity{
    @Override
    protected String getActivityTag() { return  "NachhilfeboerseActivity"; }

    @Override
    protected int getContentView() {
        return activity_nachhilfeboerse;
    }

    @Override
    protected int getDrawerLayoutId() { return R.id.drawerLayout; }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() { return R.string.title_tutoring; }

    @Override
    protected int getNavigationHighlightId() { return R.id.nachhilfeboerse; }


    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(activity_nachhilfeboerse);

        ExpandableListViewMainActivity liste1 = new ExpandableListViewMainActivity();
        NachhilfeboerseActivitymainFragment main1 = new NachhilfeboerseActivitymainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.Liste, liste1).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.button, main1).commit();

        SyncMaster task = new SyncMaster();
        task.addListener(liste1).execute();
        initToolbar();



    }
}

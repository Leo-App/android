package de.slgdev.nachhilfeboerse.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.nachhilfeboerse.activity.fragments.ExpandableListViewMainActivity;
import de.slgdev.nachhilfeboerse.activity.fragments.NachhilfeboerseActivitymainFragment;

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
        initToolbar();
        verbinden();

        ExpandableListViewMainActivity liste1 = new ExpandableListViewMainActivity();
        NachhilfeboerseActivitymainFragment main1 = new NachhilfeboerseActivitymainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.liste, liste1).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.main,main1).commit();

    }



    protected void verbinden() {
        String connectionString = "http://localhost/phpmyadmin/sql.php?db=Nachhilfeboerse&table=NachhilfeLehrer&token=458c4e081b19fe245786798b131ab0f0&pos=0";
        try {
            Connection conn = DriverManager.getConnection(connectionString); //establish connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

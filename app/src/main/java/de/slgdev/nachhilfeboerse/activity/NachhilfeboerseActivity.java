package de.slgdev.nachhilfeboerse.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        Button verbinden = (Button)findViewById(R.id.verbinden);
        verbinden.setOnClickListener(view -> verbinden());



        ExpandableListViewMainActivity liste1 = new ExpandableListViewMainActivity();
        NachhilfeboerseActivitymainFragment main1 = new NachhilfeboerseActivitymainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.Liste, liste1).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.main,main1).commit();

    }



    protected void verbinden() {
        String url = "http://localhost/phpmyadmin/tbl_sql.php?db=nachhilfeboerse&table=nachhilfelehrer&token=36fad40927f3256323cc34347f58545e";
        String user = "Admin" ;
        String pass = "geheim" ;
        Connection con = null;
        TextView text = (TextView)findViewById(R.id.text);

        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(con == null) {
            text.setText(R.string.donnerstag);
        } else{
            try {
                Statement statement = con.createStatement();
                ResultSet resultat = statement.executeQuery("SELECT * FROM nachhilfelehrer ");
                while (resultat.next()) {

                    String result = resultat.getString(1);
                    text.setText(result);


                }
                resultat.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

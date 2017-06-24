package de.slg.schwarzes_brett;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class SchwarzesBrettActivity extends AppCompatActivity {

    private List<String> groupList;
    private List<String> childList;
    private Map<String, List<String>> schwarzesBrett;
    private DrawerLayout drawerLayout;
    private ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schwarzesbrett);

        EmpfangeDaten e = new EmpfangeDaten(getApplicationContext());
        e.execute();

        initToolbar();
        initNavigationView();
        try {
            e.get();
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
        }
        createGroupList();
        initExpandableListView();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarNavDrawer);
        myToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Schwarzes Brett");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView.getMenu().findItem(R.id.newsboard).setChecked(true);

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
                        return true;
                    case R.id.nachhilfe:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), WrapperStundenplanActivity.class);
                        break;
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
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initExpandableListView() {
        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.eintraege);
        expandableListAdapter = new ExpandableListAdapter(getLayoutInflater(), groupList, schwarzesBrett);
        expListView.setAdapter(expandableListAdapter);

        expListView.setOnChildClickListener(new OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expandableListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
    }

    private void createGroupList() {
        groupList = new ArrayList<>();
        SQLiteConnector db = new SQLiteConnector(getBaseContext());
        SQLiteDatabase dbh = db.getWritableDatabase();
        Cursor myCursor = null;
        if(Utils.getUserStufe()!= "") {
            String stufe = Utils.getUserStufe();
            myCursor = dbh.query("Eintraege", new String[]{"adressat", "titel", "inhalt", "erstelldatum", "ablaufdatum"}, "adressat = '" + stufe + "'", null, null, null, null);
        }
        else {
            myCursor = dbh.query("Eintraege", new String[]{"adressat", "titel", "inhalt", "erstelldatum", "ablaufdatum"}, null , null, null, null, null);
        }
            schwarzesBrett = new LinkedHashMap<>();
        for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
            Log.e("Tag", "title: " + myCursor.getString(myCursor.getColumnIndex(SQLiteConnector.tableResult.titel)));
            groupList.add(myCursor.getString(myCursor.getColumnIndex(SQLiteConnector.tableResult.titel)));
            Date erstelldatum = new Date(myCursor.getLong(3));
            Date ablaufdatum = new Date(myCursor.getLong(4));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            String[] s = {myCursor.getString(0), myCursor.getString(2), simpleDateFormat.format(erstelldatum), simpleDateFormat.format(ablaufdatum)};
            loadChild(s);
            schwarzesBrett.put(myCursor.getString(myCursor.getColumnIndex(SQLiteConnector.tableResult.titel)), childList);
        }
        myCursor.close();
        dbh.close();
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<>();
        Collections.addAll(childList, laptopModels);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}
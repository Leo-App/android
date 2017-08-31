package de.slg.schwarzes_brett;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
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

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.NotificationService;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class SchwarzesBrettActivity extends AppCompatActivity {
    private static SQLiteConnector db;
    private static SQLiteDatabase dbh;
    private List<String> groupList;
    private List<String> childList;
    private Map<String, List<String>> schwarzesBrett;
    private DrawerLayout drawerLayout;

    public static int getRemoteId(int position) {

        //Maybe cache already transformed ids to avoid excessive RAM usage

        if (db == null)
            db = new SQLiteConnector(Utils.context);
        if (dbh == null)
            dbh = db.getReadableDatabase();

        String stufe = Utils.getUserStufe();

        Cursor cursor = !stufe.equals("") ?
                dbh.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_REMOTE_ID + " FROM " + SQLiteConnector.TABLE_EINTRAEGE + " WHERE " +
                        " " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "'", null)
                :
                dbh.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_REMOTE_ID + " FROM " + SQLiteConnector.TABLE_EINTRAEGE, null);

        cursor.moveToPosition(position);

        if (cursor.getCount() < position)
            return -1;

        int ret = cursor.getInt(0);
        cursor.close();

        return ret;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schwarzesbrett);

        Utils.registerSchwarzesBrettActivity(this);
        Utils.receiveNews();

        initToolbar();
        initNavigationView();
        initButton();
        initExpandableListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationService.ID_NEWS);
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarSchwarzesBrett);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.title_news);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.newsboard).setChecked(true);

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
                        i = new Intent(getApplicationContext(), MessengerActivity.class);
                        break;
                    case R.id.newsboard:
                        return true;
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

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initExpandableListView() {
        createGroupList();

        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.eintraege);
        ExpandableListAdapter expandableListAdapter = Utils.getUserPermission() > 1
                ? new ExpandableListAdapter(getLayoutInflater(), groupList, schwarzesBrett, createViewList())
                : new ExpandableListAdapter(getLayoutInflater(), groupList, schwarzesBrett);
        expListView.setAdapter(expandableListAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                int remoteID = getRemoteId(groupPosition);

                if(!Utils.isVerified() || Utils.getUserPermission() != 1 || Utils.messageAlreadySeen(remoteID))
                    return false;

                String cache = Start.pref.getString("pref_key_cache_vieweditems", "");

                if(!cache.equals(""))
                    cache+="-";

                Start.pref.edit()
                        .putString("pref_key_cache_vieweditems", cache+"1:"+remoteID)
                        .apply();

                if(Utils.checkNetwork())
                    new UpdateViewTrackerTask().execute(remoteID);

                return false;
            }
        });

        if (groupList.size() == 0) {
            findViewById(R.id.textView6).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textView6).setVisibility(View.GONE);
        }
    }

    private void initButton() {
        if (Utils.getUserPermission() >= 2) {
            View button = findViewById(R.id.floatingActionButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://moritz.liegmanns.de/schwarzes_brett/NeueMeldung.php"));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        } else {
            findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
        }
    }

    private ArrayList<Integer> createViewList() {

        ArrayList<Integer> viewList = new ArrayList<>();
        SQLiteConnector db = new SQLiteConnector(getBaseContext());
        SQLiteDatabase dbh = db.getReadableDatabase();

        Cursor cursor = dbh.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_VIEWS + " FROM " + SQLiteConnector.TABLE_EINTRAEGE, null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            viewList.add(cursor.getInt(0));

        }

        cursor.close();

        return viewList;

    }

    private void createGroupList() {
        groupList = new ArrayList<>();
        SQLiteConnector db = new SQLiteConnector(getBaseContext());
        SQLiteDatabase dbh = db.getReadableDatabase();

        String stufe = Utils.getUserStufe();
        Cursor cursor;
        if (!stufe.equals("")) {
            cursor = dbh.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM}, SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "'", null, null, null, null);
        } else {
            cursor = dbh.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM}, null, null, null, null, null);
        }

        schwarzesBrett = new LinkedHashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getString(1));
            Date erstelldatum = new Date(cursor.getLong(3));
            Date ablaufdatum = new Date(cursor.getLong(4));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
            String[] children = {cursor.getString(0),
                    cursor.getString(2),
                    simpleDateFormat.format(erstelldatum) +
                            " - " + simpleDateFormat.format(ablaufdatum)};
            loadChildren(children);
            schwarzesBrett.put(cursor.getString(1), childList);
        }

        cursor.close();
        dbh.close();
        db.close();
    }

    private void loadChildren(String[] children) {
        childList = new ArrayList<>();
        Collections.addAll(childList, children);
    }

    public void refreshUI() {
        initExpandableListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.registerSchwarzesBrettActivity(null);
    }
}
package de.slg.schwarzes_brett;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.NotificationService;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;

public class SchwarzesBrettActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 42;

    private static SQLiteConnector           db;
    private static SQLiteDatabase            dbh;
    private        List<String>              groupList;
    private        List<String>              childList;
    private        Map<String, List<String>> schwarzesBrett;
    private        DrawerLayout              drawerLayout;

    private String rawLocation;

    private static int getRemoteId(int position) {
        //Maybe cache already transformed ids to avoid excessive RAM usage
        if (db == null)
            db = new SQLiteConnector(Utils.getController().getContext());
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
        Utils.getController().registerSchwarzesBrettActivity(this);

        receive();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new FileDownloadTask().execute(rawLocation);
        }
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
                        i = new Intent(getApplicationContext(), EssensQRActivity.class);
                        break;
                    case R.id.messenger:
                        i = new Intent(getApplicationContext(), MessengerActivity.class);
                        break;
                    case R.id.newsboard:
                        return true;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), StundenplanActivity.class);
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
                ? new ExpandableListAdapter(groupList, schwarzesBrett, createViewList())
                : new ExpandableListAdapter(groupList, schwarzesBrett);
        expListView.setAdapter(expandableListAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int remoteID = getRemoteId(groupPosition);
                if (!Utils.isVerified() || Utils.getUserPermission() != 1 || Utils.messageAlreadySeen(remoteID))
                    return false;
                String cache = Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
                if (!cache.equals(""))
                    cache += "-";
                Utils.getController().getPreferences().edit()
                        .putString("pref_key_cache_vieweditems", cache + "1:" + remoteID)
                        .apply();
                if (Utils.checkNetwork())
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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.BASE_URL + "schwarzes_brett/NeueMeldung.php"));
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
        SQLiteConnector    db       = new SQLiteConnector(getBaseContext());
        SQLiteDatabase     dbh      = db.getReadableDatabase();
        Cursor             cursor   = dbh.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_VIEWS + " FROM " + SQLiteConnector.TABLE_EINTRAEGE, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            viewList.add(cursor.getInt(0));
        }
        cursor.close();
        return viewList;
    }

    private void createGroupList() {
        groupList = new ArrayList<>();
        SQLiteConnector db    = new SQLiteConnector(getBaseContext());
        SQLiteDatabase  dbh   = db.getReadableDatabase();
        String          stufe = Utils.getUserStufe();
        Cursor          cursor;
        if (!stufe.equals("") && !stufe.equals("TEA")) {
            cursor = dbh.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM, SQLiteConnector.EINTRAEGE_ANHANG}, SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "'", null, null, null, null);
        } else {
            cursor = dbh.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM, SQLiteConnector.EINTRAEGE_ANHANG}, null, null, null, null, null);
        }
        schwarzesBrett = new LinkedHashMap<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getString(1));
            Date             erstelldatum     = new Date(cursor.getLong(3));
            Date             ablaufdatum      = new Date(cursor.getLong(4));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

            String[] children;

            if (cursor.getString(5).equals("null")) {

                children = new String[]{cursor.getString(0),
                        cursor.getString(2),
                        simpleDateFormat.format(erstelldatum) +
                                " - " + simpleDateFormat.format(ablaufdatum)
                };
            } else {
                children = new String[]{cursor.getString(0),
                        cursor.getString(2),
                        simpleDateFormat.format(erstelldatum) +
                                " - " + simpleDateFormat.format(ablaufdatum),
                        cursor.getString(5)};
            }
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

    private void receive() {
        if (Utils.getController().getReceiveService() != null)
            Utils.getController().getReceiveService().receiveNews = true;
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
        Utils.getController().registerSchwarzesBrettActivity(null);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Map<String, List<String>> eintraege;
        private final List<String>              titel;
        @Nullable
        private       ArrayList<Integer>        views;

        ExpandableListAdapter(List<String> titel, Map<String, List<String>> eintraege) {
            this.eintraege = eintraege;
            this.titel = titel;
        }

        ExpandableListAdapter(List<String> titel, Map<String, List<String>> eintraege, @Nullable ArrayList<Integer> views) {
            this.eintraege = eintraege;
            this.titel = titel;
            this.views = views;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title, null);
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText((String) getGroup(groupPosition));
            TextView textViewStufe = (TextView) convertView.findViewById(R.id.textViewStufe);
            textViewStufe.setText(eintraege.get(titel.get(groupPosition)).get(0));
            if (views != null) {
                TextView textViewViews = (TextView) convertView.findViewById(R.id.textViewViews);
                textViewViews.setVisibility(View.VISIBLE);
                String viewString = views.get(groupPosition) > 999 ? "999+" : String.valueOf(views.get(groupPosition));
                textViewViews.setText(viewString);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textViewStufe.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                textViewStufe.setLayoutParams(params);
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (isLastChild) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt, null);

                final TextView textViewDate = (TextView) convertView.findViewById(R.id.textView);
                textViewDate.setText(eintraege.get(titel.get(groupPosition)).get(2));
            } else if (childPosition == 0) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child, null);

                final TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(eintraege.get(titel.get(groupPosition)).get(1));
            } else {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt, null);

                final String location = eintraege.get(titel.get(groupPosition)).get(3);

                final View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rawLocation = location;

                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SchwarzesBrettActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        } else {
                            new FileDownloadTask().execute(rawLocation);
                        }
                    }
                };

                final ImageView iv = (ImageView) convertView.findViewById(R.id.imageViewIcon);
                iv.setImageResource(R.drawable.ic_file_download_black_24dp);
                iv.setColorFilter(Color.rgb(0x00, 0x91, 0xea));
                iv.setOnClickListener(listener);

                final TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(location.substring(location.lastIndexOf('/') + 1));
                textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                textView.setOnClickListener(listener);
            }
            return convertView;
        }

        @Override
        public int getGroupCount() {
            return titel.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return eintraege.get(titel.get(groupPosition)).size() - 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return titel.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return eintraege.get(titel.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
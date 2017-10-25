package de.slg.schwarzes_brett;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
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

    private static SQLiteConnector           sqLiteConnector;
    private static SQLiteDatabase            sqLiteDatabase;

    private        List<String>              groupList;
    private        List<String>              childList;
    private        Map<String, List<String>> entriesMap;
    private        DrawerLayout              drawerLayout;

    private int surveyBegin;


    private String rawLocation;

    private static int getRemoteId(int position) {
        //Maybe cache already transformed ids to avoid excessive RAM usage
        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnector(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
        String stufe = Utils.getUserStufe();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + SQLiteConnector.EINTRAEGE_REMOTE_ID +
                        " FROM " + SQLiteConnector.TABLE_EINTRAEGE + (!stufe.equals("") ?
                        " WHERE " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "'" :
                        ""), null);
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

        surveyBegin = 0;

        Utils.getController().registerSchwarzesBrettActivity(this);

        receive();

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnector(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

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
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());
    }

    private void initExpandableListView() {
        createGroupList();

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.eintraege);

        ExpandableListAdapter expandableListAdapter = Utils.getUserPermission() > 1
                ? new ExpandableListAdapter(entriesMap, groupList, createViewList())
                : new ExpandableListAdapter(entriesMap, groupList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition >= surveyBegin)
                    return false;
                int remoteID = getRemoteId(groupPosition);
                if (!Utils.isVerified() || Utils.getUserPermission() != 1 || de.slg.schwarzes_brett.Utils.messageAlreadySeen(remoteID))
                    return false;
                String cache = Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
                if (!cache.equals(""))
                    cache += "-";
                Utils.getController().getPreferences()
                        .edit()
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
        View button = findViewById(R.id.floatingActionButton);
        View button2 = findViewById(R.id.floatingActionButtonSurvey);
        if (Utils.getUserPermission() >= 2) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NewEntryDialog(SchwarzesBrettActivity.this).show();
                }
            });
        }

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewSurveyDialog(SchwarzesBrettActivity.this).show();
            }
        });
    }

    private ArrayList<Integer> createViewList() {
        ArrayList<Integer> viewList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + SQLiteConnector.EINTRAEGE_VIEWS + " FROM " + SQLiteConnector.TABLE_EINTRAEGE, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            viewList.add(cursor.getInt(0));
        }
        cursor.close();
        return viewList;
    }

    private void createGroupList() {
        groupList = new ArrayList<>();

        String stufe = Utils.getUserStufe();
        Cursor cursor;
        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM, SQLiteConnector.EINTRAEGE_ANHANG}, null, null, null, null, null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM, SQLiteConnector.EINTRAEGE_ANHANG}, SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = 'Sek II' OR " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_EINTRAEGE, new String[]{SQLiteConnector.EINTRAEGE_ADRESSAT, SQLiteConnector.EINTRAEGE_TITEL, SQLiteConnector.EINTRAEGE_INHALT, SQLiteConnector.EINTRAEGE_ERSTELLDATUM, SQLiteConnector.EINTRAEGE_ABLAUFDATUM, SQLiteConnector.EINTRAEGE_ANHANG}, SQLiteConnector.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = 'Sek I' OR " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
        }

        entriesMap = new LinkedHashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getString(1));

            Date erstelldatum = new Date(cursor.getLong(3));
            Date ablaufdatum  = new Date(cursor.getLong(4));

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
            entriesMap.put(cursor.getString(1), childList);
            surveyBegin++;
        }
        cursor.close();

        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, null, null, null, null, null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, SQLiteConnector.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.EINTRAEGE_ADRESSAT + " = 'Sek II' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, SQLiteConnector.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Sek I' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getString(1));

            String[] children;
            children = new String[]{
                    cursor.getString(2), //Beschreibung
                    ((cursor.getInt(4) == 0) ? "false" : "true") + "_;_" + cursor.getString(0) + "_;_" + cursor.getInt(6), //Umfrage Metadaten
            };

            Cursor cursorAnswers = sqLiteDatabase.query(SQLiteConnector.TABLE_ANSWERS, new String[]{SQLiteConnector.ANSWERS_INHALT, SQLiteConnector.ANSWERS_REMOTE_ID, SQLiteConnector.ANSWERS_SELECTED}, SQLiteConnector.ANSWERS_SID + " = " + cursor.getInt(5), null, null, null, null);
            ArrayList<String> answers = new ArrayList<>();

            boolean voted = false;

            for (cursorAnswers.moveToFirst(); !cursorAnswers.isAfterLast(); cursorAnswers.moveToNext()) {
                answers.add(cursorAnswers.getString(0) + "_;_" + cursorAnswers.getString(1) + "_;_" + cursorAnswers.getInt(2));
                voted = voted || cursorAnswers.getInt(2) == 1;
            }

            children[1] += "_;_" + voted;

            cursorAnswers.close();
            loadChildren(children);
            childList.addAll(answers);
            childList.add("-");
            childList.add("-");
            entriesMap.put(cursor.getString(1), childList);
        }

        cursor.close();
        sqLiteDatabase.close();
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
        private final List<String> titel;
        @Nullable
        private ArrayList<Integer> views;
        private HashMap<Integer, List<TextView>> checkboxes;


        ExpandableListAdapter(Map<String, List<String>> eintraege, List<String> titel) {
            this.eintraege = eintraege;
            this.titel = titel;
            this.checkboxes = new HashMap<>();
        }

        ExpandableListAdapter(Map<String, List<String>> eintraege, List<String> titel, @Nullable ArrayList<Integer> views) {
            this.eintraege = eintraege;
            this.titel = titel;
            this.views = views;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (groupPosition >= surveyBegin) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title_alt, null);
                TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText((String) getGroup(groupPosition));
                TextView textViewStufe = (TextView) convertView.findViewById(R.id.textViewStufe);
                textViewStufe.setText(eintraege.get(titel.get(groupPosition)).get(1).split("_;_")[1]);
            } else {
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
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (groupPosition >= surveyBegin) {

                final String[] metadata = eintraege.get(titel.get(groupPosition)).get(1).split("_;_");

                if (isLastChild) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt2, null);

                    if (Integer.parseInt(metadata[2]) == Utils.getUserID())
                        convertView.findViewById(R.id.delete).setVisibility(View.VISIBLE);

                    final Button button = (Button) convertView.findViewById(R.id.button);
                    final ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);
                    final ImageButton share = (ImageButton) convertView.findViewById(R.id.share);

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eintraege.remove(titel.get(groupPosition));
                            titel.remove(groupPosition);
                            notifyDataSetChanged();
                            final Snackbar snackbar2 = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.survey_deleted), Snackbar.LENGTH_SHORT);
                            snackbar2.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                            snackbar2.setAction(Utils.getContext().getString(R.string.snackbar_undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar2.dismiss();
                                }
                            });
                            snackbar2.addCallback(new Snackbar.Callback() {

                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if(event == DISMISS_EVENT_TIMEOUT) {
                                        new deleteTask().execute(Integer.parseInt(metadata[3]));
                                    } else {
                                        initExpandableListView();
                                    }
                                }

                                @Override
                                public void onShown(Snackbar snackbar) {

                                }
                            });
                            snackbar2.show();

                        }
                    });

                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO share
                        }
                    });

                    if (!Boolean.parseBoolean(metadata[3])) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (TextView textView : checkboxes.get(groupPosition)) {
                                    RadioButton rb = (RadioButton) textView;
                                    if (rb.isChecked())
                                        new sendVoteTask(button).execute((Integer) rb.getTag());
                                }
                            }
                        });
                    } else {
                        button.setText(Utils.getString(R.string.result));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showResultDialog(Integer.parseInt(metadata[3]));
                            }
                        });

                    }
                } else if (childPosition == 0) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_survey_meta, null);
                    ((TextView) convertView.findViewById(R.id.metadata)).setText(getString(R.string.meta_id_placeholder, metadata[2]));
                } else if (childPosition == 1) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child, null);
                    ((TextView) convertView.findViewById(R.id.textView)).setText(eintraege.get(titel.get(groupPosition)).get(0));
                } else {
                    final boolean multiple = Boolean.parseBoolean(metadata[0]);
                    convertView = getLayoutInflater().inflate(multiple ?
                            R.layout.list_item_expandable_child_survey_multiple :
                            R.layout.list_item_expandable_child_survey_single, null);

                    String option = eintraege.get(titel.get(groupPosition)).get(childPosition);
                    final TextView t = (TextView) convertView.findViewById(R.id.checkBox);

                    t.setText(option.split("_;_")[0]);
                    t.setTag(Integer.parseInt(option.split("_;_")[1]));
                    ((CompoundButton) t).setChecked(option.split("_;_")[2].equals("1"));
                    t.setEnabled(!Boolean.parseBoolean(metadata[3]));
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!multiple) {
                                for (TextView textView : checkboxes.get(groupPosition)) {
                                    RadioButton rb = (RadioButton) textView;
                                    if (!rb.equals(t))
                                        rb.setChecked(false);
                                }
                            }
                        }
                    });

                    List<TextView> checkboxList;

                    if ((checkboxList = checkboxes.get(groupPosition)) == null)
                        checkboxes.put(groupPosition, (checkboxList = new ArrayList<>()));

                    checkboxList.add(t);

                }

            } else {

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
            }
            return convertView;
        }

        private void showResultDialog(int id) {
            new ResultDialog(Utils.getContext(), id).show();
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

        private class sendVoteTask extends AsyncTask<Integer, Void, ResponseCode> {

            private Button b;
            private int id;

            sendVoteTask(Button b) {
                this.b = b;
            }

            @Override
            protected ResponseCode doInBackground(Integer... params) {

                if (!Utils.checkNetwork())
                    return ResponseCode.NO_CONNECTION;

                id = params[1];

                SQLiteConnector db = new SQLiteConnector(getApplicationContext());
                SQLiteDatabase dbh = db.getWritableDatabase();

                dbh.execSQL("UPDATE " + SQLiteConnector.TABLE_ANSWERS + " SET " + SQLiteConnector.ANSWERS_SELECTED + " = 1 WHERE " + SQLiteConnector.ANSWERS_REMOTE_ID + " = " + params[0]);

                dbh.close();

                try {
                    URL updateURL = new URL("http://moritz.liegmanns.de/survey/addResult.php?user=" + Utils.getUserID() + "&answer=" + params[0]);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    if (builder.toString().startsWith("-"))
                        return ResponseCode.SERVER_ERROR;

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseCode.SERVER_ERROR;
                }
                return ResponseCode.SUCCESS;
            }

            @Override
            protected void onPostExecute(ResponseCode r) {
                switch (r) {
                    case NO_CONNECTION:
                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.snackbar_no_connection_info), Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                        snackbar.setAction(Utils.getContext().getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                        break;
                    case SERVER_ERROR:
                        final Snackbar snackbar2 = Snackbar.make(findViewById(R.id.snackbar), "Es ist etwas schiefgelaufen, versuche es später erneut", Snackbar.LENGTH_SHORT);
                        snackbar2.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                        snackbar2.setAction(Utils.getContext().getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar2.dismiss();
                            }
                        });
                        snackbar2.show();
                        break;
                    case SUCCESS:
                        b.setText(Utils.getString(R.string.result));
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showResultDialog(id);
                            }
                        });
                        Toast.makeText(Utils.getContext(), "Erfolgreich abgestimmt", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }

        private class deleteTask extends AsyncTask<Integer, Void, ResponseCode> {

            @Override
            protected ResponseCode doInBackground(Integer... params) {

                if (!Utils.checkNetwork())
                    return ResponseCode.NO_CONNECTION;

                SQLiteConnector db = new SQLiteConnector(getApplicationContext());
                SQLiteDatabase dbh = db.getWritableDatabase();

                dbh.execSQL("DELETE FROM " + SQLiteConnector.TABLE_SURVEYS + " WHERE " + SQLiteConnector.SURVEYS_REMOTE_ID + " = "+params[0]);
                dbh.execSQL("DELETE FROM " + SQLiteConnector.TABLE_ANSWERS + " WHERE " + SQLiteConnector.ANSWERS_SID + " = (SELECT "+SQLiteConnector.SURVEYS_ID+" FROM "+SQLiteConnector.TABLE_SURVEYS+" WHERE "+SQLiteConnector.SURVEYS_REMOTE_ID+" = "+params[0]+")");

                dbh.close();

                try {
                    URL updateURL = new URL("http://moritz.liegmanns.de/survey/deleteSurvey.php?survey=" + params[0]);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    if (builder.toString().startsWith("-"))
                        return ResponseCode.SERVER_ERROR;

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseCode.SERVER_ERROR;
                }
                return ResponseCode.SUCCESS;
            }

            @Override
            protected void onPostExecute(ResponseCode r) {
                switch (r) {
                    case NO_CONNECTION:
                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.snackbar_no_connection_info), Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                        snackbar.setAction(Utils.getContext().getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                        break;
                    case SERVER_ERROR:
                        final Snackbar snackbar2 = Snackbar.make(findViewById(R.id.snackbar), "Es ist etwas schiefgelaufen, versuche es später erneut", Snackbar.LENGTH_SHORT);
                        snackbar2.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                        snackbar2.setAction(Utils.getContext().getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar2.dismiss();
                            }
                        });
                        snackbar2.show();
                        break;
                    case SUCCESS:
                        break;
                }
            }

        }

    }
}
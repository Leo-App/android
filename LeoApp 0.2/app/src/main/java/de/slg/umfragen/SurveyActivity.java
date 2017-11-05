package de.slg.umfragen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.leoview.ActionLogActivity;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.ResponseCode;
import de.slg.schwarzes_brett.SQLiteConnector;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;

public class SurveyActivity extends ActionLogActivity {

    private static SQLiteConnector           sqLiteConnector;
    private static SQLiteDatabase            sqLiteDatabase;
    private        DrawerLayout              drawerLayout;
    private        List<String>              groupList;
    private        List<String>              childList;
    private        Map<String, List<String>> entriesMap;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_umfragen);

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnector(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        receive();

        initToolbar();
        initNavigationView();
        initButton();
        initExpandableListView();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarUmfragen);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.title_survey_news);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.umfragen).setChecked(true);
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
                    case R.id.umfragen:
                        return true;
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

        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(entriesMap, groupList);
        expandableListView.setAdapter(expandableListAdapter);

        if (groupList.size() == 0) {
            findViewById(R.id.textView6).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textView6).setVisibility(View.GONE);
        }
    }

    private void initButton() {
        View button2 = findViewById(R.id.floatingActionButtonSurvey);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewSurveyDialog(SurveyActivity.this).show();
            }
        });
    }

    private void createGroupList() {
        groupList = new ArrayList<>();

        String stufe = Utils.getUserStufe();
        Cursor cursor;

        entriesMap = new LinkedHashMap<>();

        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, null, null, null, null, null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, SQLiteConnector.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Sek II' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Alle' OR " + SQLiteConnector.SURVEYS_REMOTE_ID + " = "+Utils.getUserID(), null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnector.TABLE_SURVEYS, new String[]{SQLiteConnector.SURVEYS_ADRESSAT, SQLiteConnector.SURVEYS_TITEL, SQLiteConnector.SURVEYS_BESCHREIBUNG, SQLiteConnector.SURVEYS_ABSENDER, SQLiteConnector.SURVEYS_MULTIPLE, SQLiteConnector.SURVEYS_ID, SQLiteConnector.SURVEYS_REMOTE_ID}, SQLiteConnector.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Sek I' OR " + SQLiteConnector.SURVEYS_ADRESSAT + " = 'Alle' OR " + SQLiteConnector.SURVEYS_REMOTE_ID + " = "+Utils.getUserID(), null, null, null, null);
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
        private LinkedHashMap<Integer, List<TextView>> checkboxes;

        ExpandableListAdapter(Map<String, List<String>> eintraege, List<String> titel) {
            this.eintraege = eintraege;
            this.titel = titel;
            this.checkboxes = new LinkedHashMap<>();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


            convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title_alt, null);
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText((String) getGroup(groupPosition));
            TextView textViewStufe = (TextView) convertView.findViewById(R.id.textViewStufe);
            textViewStufe.setText(eintraege.get(titel.get(groupPosition)).get(1).split("_;_")[1]);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

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
                                if (event == DISMISS_EVENT_TIMEOUT) {
                                    new ExpandableListAdapter.deleteTask().execute(Integer.parseInt(metadata[2]));
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
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Utils.getContext().getString(R.string.share_text, titel.get(groupPosition)));
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, Utils.getString(R.string.share)));
                    }
                });

                if (!Boolean.parseBoolean(metadata[3])) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (TextView textView : checkboxes.get(groupPosition)) {
                                CompoundButton rb = (CompoundButton) textView;
                                if (rb.isChecked())
                                    new ExpandableListAdapter.sendVoteTask(button).execute((Integer) rb.getTag());
                            }
                        }
                    });
                } else {
                    button.setText(Utils.getString(R.string.result));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResultDialog(Integer.parseInt(metadata[2]));
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

            return convertView;
        }

        private void showResultDialog(int id) {
            new ResultDialog(SurveyActivity.this, id).show();
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

                id = params[0];

                SQLiteConnector db = new SQLiteConnector(getApplicationContext());
                SQLiteDatabase dbh = db.getWritableDatabase();

                dbh.execSQL("UPDATE " + SQLiteConnector.TABLE_ANSWERS + " SET " + SQLiteConnector.ANSWERS_SELECTED + " = 1 WHERE " + SQLiteConnector.ANSWERS_REMOTE_ID + " = " + params[0]);

                dbh.close();

                try {
                    URL updateURL = new URL(Utils.BASE_URL_PHP + "survey/addResult.php?user=" + Utils.getUserID() + "&answer=" + params[0]);
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

                dbh.execSQL("DELETE FROM " + SQLiteConnector.TABLE_SURVEYS + " WHERE " + SQLiteConnector.SURVEYS_REMOTE_ID + " = " + params[0]);
                dbh.execSQL("DELETE FROM " + SQLiteConnector.TABLE_ANSWERS + " WHERE " + SQLiteConnector.ANSWERS_SID + " = (SELECT " + SQLiteConnector.SURVEYS_ID + " FROM " + SQLiteConnector.TABLE_SURVEYS + " WHERE " + SQLiteConnector.SURVEYS_REMOTE_ID + " = " + params[0] + ")");

                dbh.close();

                try {
                    URL updateURL = new URL(Utils.BASE_URL_PHP + "survey/deleteSurvey.php?survey=" + params[0]);
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

package de.slg.umfragen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.slg.leoapp.R;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.schwarzes_brett.utility.ResponseCode;
import de.slg.umfragen.dialog.NewSurveyDialog;
import de.slg.umfragen.dialog.ResultDialog;
import de.slg.umfragen.task.SyncSurveyTask;
import de.slg.umfragen.utility.Survey;

/**
 * SurveyActivity.
 * <p>
 * Diese Activity zeigt eine ExpandableListView mit allen aktuellen Umfragen an. Gefiltert wird nach Stufe. Bei Ausklappen eines Listitems werden
 * die Fragestellung, eine Beschreibung, RadioButtons/Checkboxes zum Abstimmen, ein Teilen- sowie (ggf.) Löschen-Button und einen Abstimmen- bzw. Ergebnis-Button.
 *
 * @author Gianni
 * @version 2017.1111
 * @since 0.5.6
 */
public class SurveyActivity extends LeoAppFeatureActivity {

    private static SQLiteConnectorUmfragen sqLiteConnector;
    private static SQLiteDatabase          sqLiteDatabase;
    private        ExpandableListView      expandableListView;
    private        List<Integer>           groupList;
    private        Map<Integer, Survey>    entriesMap;
    private        View                    button2;

    private int previousVisibleItem = 0;

    @Override
    public void onCreate(final Bundle b) {
        super.onCreate(b);

        Utils.getNotificationManager().cancel(NotificationHandler.ID_UMFRAGEN);
        Utils.getController().registerSurveyActivity(this);

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorUmfragen(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        receive();

        initButton();
        initExpandableListView();
        initSwipeToRefresh();

        if (getIntent().getExtras() != null) {
            int i = 0;
            for (Map.Entry<Integer, Survey> entry : entriesMap.entrySet()) {
                if (entry.getValue().remoteId == Utils.getUserID()) {
                    expandableListView.expandGroup(i);
                    break;
                }
                i++;
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_umfragen;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.actionBarUmfragen;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_survey_news;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.umfragen;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationHandler.ID_UMFRAGEN);
        receive();
    }

    @Override
    protected String getActivityTag() {
        return "SurveyActivity";
    }

    private void initExpandableListView() {
        createGroupList();

        expandableListView = findViewById(R.id.eintraege);

        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(entriesMap, groupList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != previousVisibleItem) {
                    if (firstVisibleItem < previousVisibleItem) {
                        button2.setVisibility(View.VISIBLE);
                    } else {
                        button2.setVisibility(View.INVISIBLE);
                    }
                    previousVisibleItem = firstVisibleItem;
                }
            }
        });

        if (groupList.size() == 0) {
            findViewById(R.id.textView6).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textView6).setVisibility(View.GONE);
        }
    }

    private void initButton() {

        button2 = findViewById(R.id.floatingActionButtonSurvey);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewSurveyDialog(SurveyActivity.this).show();
            }
        });
    }

    private void initSwipeToRefresh() {
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncSurveyTask(swipeLayout).execute();
            }
        });

        swipeLayout.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
    }

    private void createGroupList() {
        groupList = new ArrayList<>();

        String stufe = Utils.getUserStufe();
        Cursor cursor;

        entriesMap = new LinkedHashMap<>();

        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(SQLiteConnectorUmfragen.TABLE_SURVEYS, new String[]{SQLiteConnectorUmfragen.SURVEYS_ADRESSAT, SQLiteConnectorUmfragen.SURVEYS_TITEL, SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG, SQLiteConnectorUmfragen.SURVEYS_ABSENDER, SQLiteConnectorUmfragen.SURVEYS_MULTIPLE, SQLiteConnectorUmfragen.SURVEYS_ID, SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID}, null, null, null, null, null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnectorUmfragen.TABLE_SURVEYS, new String[]{SQLiteConnectorUmfragen.SURVEYS_ADRESSAT, SQLiteConnectorUmfragen.SURVEYS_TITEL, SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG, SQLiteConnectorUmfragen.SURVEYS_ABSENDER, SQLiteConnectorUmfragen.SURVEYS_MULTIPLE, SQLiteConnectorUmfragen.SURVEYS_ID, SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID}, SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = 'Sek II' OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = 'Alle' OR " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + Utils.getUserID(), null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnectorUmfragen.TABLE_SURVEYS, new String[]{SQLiteConnectorUmfragen.SURVEYS_ADRESSAT, SQLiteConnectorUmfragen.SURVEYS_TITEL, SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG, SQLiteConnectorUmfragen.SURVEYS_ABSENDER, SQLiteConnectorUmfragen.SURVEYS_MULTIPLE, SQLiteConnectorUmfragen.SURVEYS_ID, SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID}, SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = 'Sek I' OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT + " = 'Alle' OR " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + Utils.getUserID(), null, null, null, null);
                break;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getInt(6));

            Cursor            cursorAnswers = sqLiteDatabase.query(SQLiteConnectorUmfragen.TABLE_ANSWERS, new String[]{SQLiteConnectorUmfragen.ANSWERS_INHALT, SQLiteConnectorUmfragen.ANSWERS_REMOTE_ID, SQLiteConnectorUmfragen.ANSWERS_SELECTED}, SQLiteConnectorUmfragen.ANSWERS_SID + " = " + cursor.getInt(5), null, null, null, null);
            ArrayList<String> answers       = new ArrayList<>();

            boolean voted = false;

            for (cursorAnswers.moveToFirst(); !cursorAnswers.isAfterLast(); cursorAnswers.moveToNext()) {
                answers.add(cursorAnswers.getString(0) + "_;_" + cursorAnswers.getString(1) + "_;_" + cursorAnswers.getInt(2));
                voted = voted || cursorAnswers.getInt(2) == 1;
            }

            cursorAnswers.close();

            Survey s = new Survey(cursor.getInt(5), cursor.getInt(6), cursor.getString(1), cursor.getString(2), cursor.getInt(4) != 0, voted, cursor.getString(0), answers);
            entriesMap.put(cursor.getInt(6), s);
        }

        cursor.close();
    }

    public void refreshUI() {
        initExpandableListView();
    }

    private void receive() {
        new SyncSurveyTask(null).execute();
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerSurveyActivity(null);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final Map<Integer, Survey> umfragen;
        private final List<Integer>        ids;

        private LinkedHashMap<Integer, List<CompoundButton>> checkboxes;

        ExpandableListAdapter(Map<Integer, Survey> umfragen, List<Integer> ids) {
            this.umfragen = umfragen;
            this.ids = ids;
            this.checkboxes = new LinkedHashMap<>();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title_alt, null);
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText((String) getGroup(groupPosition));
            TextView textViewStufe = convertView.findViewById(R.id.textViewStufe);
            textViewStufe.setText(getSurvey(groupPosition).to);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (isLastChild) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt2, null);

                if (getSurvey(groupPosition).remoteId == Utils.getUserID())
                    convertView.findViewById(R.id.delete).setVisibility(View.VISIBLE);

                final Button      button = convertView.findViewById(R.id.button);
                final ImageButton delete = convertView.findViewById(R.id.delete);
                final ImageButton share  = convertView.findViewById(R.id.share);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Survey toBeDeleted = getSurvey(groupPosition);

                        umfragen.remove(ids.get(groupPosition));
                        ids.remove(groupPosition);
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
                                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE) {
                                    new ExpandableListAdapter.deleteTask().execute(toBeDeleted.remoteId);
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
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Utils.getContext().getString(R.string.share_text, getSurvey(groupPosition).title));
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, Utils.getString(R.string.share)));
                    }
                });

                if (!getSurvey(groupPosition).voted) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (TextView textView : checkboxes.get(getSurvey(groupPosition).remoteId)) {
                                CompoundButton rb = (CompoundButton) textView;
                                if (rb.isChecked())
                                    new SendVoteTask(button, getSurvey(groupPosition)).execute((Integer) rb.getTag(), getSurvey(groupPosition).remoteId);
                            }
                        }
                    });
                } else {
                    button.setText(Utils.getString(R.string.result));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResultDialog(getSurvey(groupPosition).remoteId);
                        }
                    });
                }
            } else if (childPosition == 0) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_survey_meta, null);
                ((TextView) convertView.findViewById(R.id.metadata)).setText(getString(R.string.meta_id_placeholder, getSurvey(groupPosition).remoteId));
            } else if (childPosition == 1) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child, null);
                ((TextView) convertView.findViewById(R.id.textView)).setText(getSurvey(groupPosition).description);
            } else {
                convertView = getLayoutInflater().inflate(getSurvey(groupPosition).multiple ?
                        R.layout.list_item_expandable_child_survey_multiple :
                        R.layout.list_item_expandable_child_survey_single, null);

                String option = getSurvey(groupPosition).answers[childPosition - 2];

                final CompoundButton t = convertView.findViewById(R.id.checkBox);

                t.setText(option.split("_;_")[0]);
                t.setTag(Integer.parseInt(option.split("_;_")[1]));
                t.setChecked(option.split("_;_")[2].equals("1"));
                t.setEnabled(!getSurvey(groupPosition).voted);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!getSurvey(groupPosition).voted && !getSurvey(groupPosition).multiple) {
                            for (TextView textView : checkboxes.get(getSurvey(groupPosition).remoteId)) {
                                RadioButton rb = (RadioButton) textView;
                                if (!rb.equals(t))
                                    rb.setChecked(false);
                            }
                        }
                    }
                });

                List<CompoundButton> checkboxList;

                if ((checkboxList = checkboxes.get(getSurvey(groupPosition).remoteId)) == null)
                    checkboxes.put(getSurvey(groupPosition).remoteId, (checkboxList = new ArrayList<>()));

                checkboxList.add(t);
            }

            return convertView;
        }

        private void showResultDialog(int id) {
            new ResultDialog(SurveyActivity.this, id).show();
        }

        @Override
        public int getGroupCount() {
            return ids.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getSurvey(groupPosition).getAnswerAmount() + 3;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return umfragen.get(ids.get(groupPosition)).title;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return umfragen.get(ids.get(groupPosition)).description;
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

        private Survey getSurvey(int groupPosition) {
            return umfragen.get(ids.get(groupPosition));
        }

        private class SendVoteTask extends AsyncTask<Integer, Void, ResponseCode> {

            private Button b;
            private Survey s;
            private int    id;
            private int    remoteid;

            SendVoteTask(Button b, Survey s) {
                this.b = b;
                this.s = s;
            }

            @Override
            protected ResponseCode doInBackground(Integer... params) {

                if (!Utils.checkNetwork())
                    return ResponseCode.NO_CONNECTION;

                id = params[0];
                remoteid = params[1];

                SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(getApplicationContext());
                SQLiteDatabase          dbh = db.getWritableDatabase();

                dbh.execSQL("UPDATE " + SQLiteConnectorUmfragen.TABLE_ANSWERS
                        + " SET " + SQLiteConnectorUmfragen.ANSWERS_SELECTED + " = 1"
                        + " WHERE " + SQLiteConnectorUmfragen.ANSWERS_REMOTE_ID + " = " + id);

                dbh.close();

                try {
                    URL updateURL = new URL(Utils.DOMAIN_DEV + "ic_create_survey/addResult.php?user=" + Utils.getUserID() + "&answer=" + params[0]);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                    StringBuilder builder = new StringBuilder();
                    String        line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    if (builder.toString().startsWith("-"))
                        return ResponseCode.SERVER_ERROR;
                } catch (IOException e) {
                    Utils.logError(e);
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
                                showResultDialog(remoteid);
                            }
                        });
                        Toast.makeText(Utils.getContext(), "Erfolgreich abgestimmt", Toast.LENGTH_SHORT).show();

                        for (CompoundButton c : checkboxes.get(remoteid)) {
                            c.setEnabled(false);
                        }

                        s.voted = true;

                        for(int i = 0; i < s.answers.length; i++) {
                            String cur = s.answers[i];
                            String[] parts = cur.split("_;_");
                            if(Integer.parseInt(parts[1]) == id)
                                s.answers[i] = parts[0]+"_;_"+parts[1]+"_;_"+1;
                        }

                        break;
                }
            }
        }

        private class deleteTask extends AsyncTask<Integer, Void, ResponseCode> {

            @Override
            protected ResponseCode doInBackground(Integer... params) {

                if (!Utils.checkNetwork())
                    return ResponseCode.NO_CONNECTION;

                SQLiteConnectorUmfragen db  = new SQLiteConnectorUmfragen(getApplicationContext());
                SQLiteDatabase          dbh = db.getWritableDatabase();

                dbh.execSQL("DELETE FROM " + SQLiteConnectorUmfragen.TABLE_SURVEYS + " WHERE " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + params[0]);
                dbh.execSQL("DELETE FROM " + SQLiteConnectorUmfragen.TABLE_ANSWERS + " WHERE " + SQLiteConnectorUmfragen.ANSWERS_SID + " = (SELECT " + SQLiteConnectorUmfragen.SURVEYS_ID + " FROM " + SQLiteConnectorUmfragen.TABLE_SURVEYS + " WHERE " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID + " = " + params[0] + ")");

                dbh.close();

                try {
                    URL updateURL = new URL(Utils.DOMAIN_DEV + "ic_create_survey/deleteSurvey.php?ic_create_survey=" + params[0]);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(updateURL.openConnection().getInputStream(), "UTF-8"));

                    StringBuilder builder = new StringBuilder();
                    String        line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    if (builder.toString().startsWith("-"))
                        return ResponseCode.SERVER_ERROR;
                } catch (IOException e) {
                    Utils.logError(e);
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
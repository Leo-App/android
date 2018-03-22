package de.slgdev.umfragen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragenSpeichern;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.umfragen.dialog.NewSurveyDialog;
import de.slgdev.umfragen.dialog.ResultDialog;
import de.slgdev.umfragen.task.SaveResultTask;
import de.slgdev.umfragen.task.SendVoteTask;
import de.slgdev.umfragen.task.SyncSurveyTask;
import de.slgdev.umfragen.utility.Survey;

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
public class SurveyActivity extends LeoAppNavigationActivity implements TaskStatusListener {

    private static SQLiteConnectorUmfragen sqLiteConnector;
    private static SQLiteDatabase          sqLiteDatabase;

    private ExpandableListView   expandableListView;
    private List<Integer>        groupList;
    private Map<Integer, Survey> entriesMap;
    private View                 button2;

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
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_umfragen;
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
        return R.string.title_survey_news;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.umfragen;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.umfragen, menu);
        menu.findItem(R.id.action_save).setVisible(false);

        SQLiteConnectorUmfragenSpeichern db = new SQLiteConnectorUmfragenSpeichern(this);

        if (db.getAmount() > 0) {
            menu.findItem(R.id.action_save).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_save) {
            startActivity(new Intent(this, ResultActivity.class));
        }
        return super.onOptionsItemSelected(mi);
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

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerSurveyActivity(null);
    }

    @Override
    public void taskFinished(Object... params) {
        SwipeRefreshLayout swipeLayout = findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setRefreshing(false);
    }


    private void initExpandableListView() {
        createGroupList();

        expandableListView = findViewById(R.id.expandableListView);

        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(entriesMap, groupList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //STUB
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
            findViewById(R.id.noEntries).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noEntries).setVisibility(View.GONE);
        }
    }

    private void initButton() {
        button2 = findViewById(R.id.floatingActionButton);
        button2.setOnClickListener(v -> new NewSurveyDialog(SurveyActivity.this).show());
    }

    private void initSwipeToRefresh() {
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(() -> new SyncSurveyTask().addListener(this).execute());

        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.colorPrimary
                )
        );
    }

    private void createGroupList() {
        groupList = new ArrayList<>();

        String stufe = Utils.getUserStufe();
        Cursor cursor;

        entriesMap = new LinkedHashMap<>();

        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(
                        SQLiteConnectorUmfragen.TABLE_SURVEYS,
                        new String[]{
                                SQLiteConnectorUmfragen.SURVEYS_ADRESSAT,
                                SQLiteConnectorUmfragen.SURVEYS_TITEL,
                                SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG,
                                SQLiteConnectorUmfragen.SURVEYS_ABSENDER,
                                SQLiteConnectorUmfragen.SURVEYS_MULTIPLE,
                                SQLiteConnectorUmfragen.SURVEYS_ID,
                                SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID,
                                SQLiteConnectorUmfragen.SURVEYS_VOTEABLE
                        },
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(
                        SQLiteConnectorUmfragen.TABLE_SURVEYS,
                        new String[]{
                                SQLiteConnectorUmfragen.SURVEYS_ADRESSAT,
                                SQLiteConnectorUmfragen.SURVEYS_TITEL,
                                SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG,
                                SQLiteConnectorUmfragen.SURVEYS_ABSENDER,
                                SQLiteConnectorUmfragen.SURVEYS_MULTIPLE,
                                SQLiteConnectorUmfragen.SURVEYS_ID,
                                SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID,
                                SQLiteConnectorUmfragen.SURVEYS_VOTEABLE
                        },
                        SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = '" + stufe + "'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = 'Sek II'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = 'Alle'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID +
                                " = " + Utils.getUserID(),
                        null,
                        null,
                        null,
                        null
                );
                break;
            default:
                cursor = sqLiteDatabase.query(
                        SQLiteConnectorUmfragen.TABLE_SURVEYS,
                        new String[]{
                                SQLiteConnectorUmfragen.SURVEYS_ADRESSAT,
                                SQLiteConnectorUmfragen.SURVEYS_TITEL,
                                SQLiteConnectorUmfragen.SURVEYS_BESCHREIBUNG,
                                SQLiteConnectorUmfragen.SURVEYS_ABSENDER,
                                SQLiteConnectorUmfragen.SURVEYS_MULTIPLE,
                                SQLiteConnectorUmfragen.SURVEYS_ID,
                                SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID,
                                SQLiteConnectorUmfragen.SURVEYS_VOTEABLE
                        },
                        SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = '" + stufe.charAt(1) + "'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = 'Sek I'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_ADRESSAT +
                                " = 'Alle'" +
                                " OR " + SQLiteConnectorUmfragen.SURVEYS_REMOTE_ID +
                                " = " + Utils.getUserID(),
                        null,
                        null,
                        null,
                        null
                );
                break;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getInt(6));
            Utils.logError(cursor.getInt(6));
            Cursor cursorAnswers = sqLiteDatabase.query(
                    SQLiteConnectorUmfragen.TABLE_ANSWERS,
                    new String[]{
                            SQLiteConnectorUmfragen.ANSWERS_INHALT,
                            SQLiteConnectorUmfragen.ANSWERS_REMOTE_ID,
                            SQLiteConnectorUmfragen.ANSWERS_SELECTED
                    },
                    SQLiteConnectorUmfragen.ANSWERS_SID +
                            " = " + cursor.getInt(5),
                    null,
                    null,
                    null,
                    null
            );
            ArrayList<String> answers = new ArrayList<>();

            boolean voted = false;

            for (cursorAnswers.moveToFirst(); !cursorAnswers.isAfterLast(); cursorAnswers.moveToNext()) {
                answers.add(
                        cursorAnswers.getString(0) + "_;_"
                                + cursorAnswers.getString(1) + "_;_"
                                + cursorAnswers.getInt(2)
                );
                voted = voted || cursorAnswers.getInt(2) == 1;
            }

            cursorAnswers.close();

            Survey s = new Survey(
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(4) != 0,
                    voted || cursor.getInt(7) == 0,
                    cursor.getString(0),
                    answers
            );
            entriesMap.put(cursor.getInt(6), s);
        }

        cursor.close();
    }

    public void refreshUI() {
        initExpandableListView();
    }

    private void receive() {
        new SyncSurveyTask()
                .addListener(params -> {
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
                })
                .execute();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter implements TaskStatusListener {

        private final Map<Integer, Survey> umfragen;
        private final List<Integer>        ids;

        private LinkedHashMap<Integer, List<CompoundButton>> checkboxes;
        private LinkedHashMap<Integer, Button> buttons;

        ExpandableListAdapter(Map<Integer, Survey> umfragen, List<Integer> ids) {
            this.umfragen = umfragen;
            this.ids = ids;
            this.checkboxes = new LinkedHashMap<>();
            this.buttons    = new LinkedHashMap<>();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title_alt, null);
            TextView textView = convertView.findViewById(R.id.titleKlausur);
            textView.setText((String) getGroup(groupPosition));
            TextView textViewStufe = convertView.findViewById(R.id.textViewStufe);
            textViewStufe.setText(getSurvey(groupPosition).to);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (isLastChild) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt2, null);

                if (getSurvey(groupPosition).remoteId == Utils.getUserID() || Utils.getUserPermission() >= User.PERMISSION_LEHRER)
                    convertView.findViewById(R.id.delete).setVisibility(View.VISIBLE);

                final Button      button = convertView.findViewById(R.id.button);
                final ImageButton delete = convertView.findViewById(R.id.delete);
                final ImageButton share  = convertView.findViewById(R.id.share);

                buttons.put(groupPosition, button);

                delete.setOnClickListener(v -> {
                    final Survey toBeDeleted = getSurvey(groupPosition);

                    umfragen.remove(ids.get(groupPosition));
                    ids.remove(groupPosition);
                    notifyDataSetChanged();

                    final Snackbar snackbar2 = Snackbar.make(findViewById(R.id.coordinatorLayout), Utils.getString(R.string.survey_deleted), Snackbar.LENGTH_SHORT);
                    snackbar2.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                    snackbar2.setAction(Utils.getContext().getString(R.string.snackbar_undo), v1 -> {});
                    snackbar2.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {

                            if (event == DISMISS_EVENT_ACTION)
                                initExpandableListView();
                            else
                                new SaveResultTask(toBeDeleted)
                                        .addListener(params -> {
                                            if (Utils.getController().getSurveyActivity() == null) {
                                                sqLiteDatabase.close();
                                                sqLiteConnector.close();
                                                sqLiteDatabase = null;
                                                sqLiteConnector = null;
                                            }
                                        })
                                        .execute();

                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            //STUB
                        }
                    });
                    snackbar2.show();
                });

                share.setOnClickListener(v -> {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Utils.getContext().getString(R.string.share_text, getSurvey(groupPosition).title));
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, Utils.getString(R.string.share)));
                });

                if (!getSurvey(groupPosition).voted) {
                    button.setOnClickListener(v -> {
                        for (TextView textView : checkboxes.get(getSurvey(groupPosition).remoteId)) {
                            CompoundButton rb = (CompoundButton) textView;
                            if (rb.isChecked())
                                new SendVoteTask().addListener(this).execute(groupPosition, getSurvey(groupPosition), rb.getTag());
                        }
                    });
                } else {
                    button.setText(Utils.getString(R.string.result));
                    button.setOnClickListener(v -> showResultDialog(getSurvey(groupPosition).remoteId, getSurvey(groupPosition).to));
                }
            } else if (childPosition == 0) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_survey_meta, null);
                ((TextView) convertView.findViewById(R.id.metadata)).setText(getString(R.string.meta_id_placeholder, getSurvey(groupPosition).remoteId));
            } else if (childPosition == 1) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child, null);
                ((TextView) convertView.findViewById(R.id.titleKlausur)).setText(getSurvey(groupPosition).description);
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
                t.setOnClickListener(v -> {
                    if (!getSurvey(groupPosition).voted && !getSurvey(groupPosition).multiple) {
                        for (TextView textView : checkboxes.get(getSurvey(groupPosition).remoteId)) {
                            RadioButton rb = (RadioButton) textView;
                            if (!rb.equals(t))
                                rb.setChecked(false);
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

        private void showResultDialog(int id, String to) {
            new ResultDialog(SurveyActivity.this, id, to).show();
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

        @Override
        public void taskFinished(Object... params) {

            ResponseCode r = (ResponseCode) params[0];
            Survey s = (Survey) params[1];
            int tag     = (int) params[2];
            int groupId = (int) params[3];

            switch (r) {
                case NO_CONNECTION:
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar), Utils.getString(R.string.snackbar_no_connection_info), Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                    snackbar.setAction(Utils.getContext().getString(R.string.confirm), v -> snackbar.dismiss());
                    snackbar.show();
                    break;
                case SERVER_FAILED:
                    final Snackbar snackbar2 = Snackbar.make(findViewById(R.id.snackbar), R.string.error_later, Snackbar.LENGTH_SHORT);
                    snackbar2.setActionTextColor(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary));
                    snackbar2.setAction(Utils.getContext().getString(R.string.confirm), v -> snackbar2.dismiss());
                    snackbar2.show();
                    break;
                case SUCCESS:

                    Button b = buttons.get(groupId);

                    b.setText(Utils.getString(R.string.result));
                    b.setOnClickListener(v -> showResultDialog(s.remoteId, s.to));
                    Toast.makeText(Utils.getContext(), R.string.voted_sucessfully, Toast.LENGTH_SHORT).show();

                    for (CompoundButton c : checkboxes.get(s.remoteId)) {
                        c.setEnabled(false);
                    }

                    s.voted = true;

                    for (int i = 0; i < s.answers.length; i++) {
                        String   cur   = s.answers[i];
                        String[] parts = cur.split("_;_");
                        if (Integer.parseInt(parts[1]) == tag)
                            s.answers[i] = parts[0] + "_;_" + parts[1] + "_;_" + 1;
                    }

                    break;
            }
        }
    }
}
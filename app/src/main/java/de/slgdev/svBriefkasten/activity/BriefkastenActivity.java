package de.slgdev.svBriefkasten.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.svBriefkasten.task.RemoveTopic;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class BriefkastenActivity extends LeoAppNavigationActivity implements TaskStatusListener {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private Button createTopic;
    private Button results;
    private CheckBox like;
    private SharedPreferences sharedPref;
    private String lastAdded;
    private List<Integer> position;
    private SwipeRefreshLayout swipeRefresh;
    private String[] topics;

    List<Boolean> geliked;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefkasten);

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                receiveData();
            }
        });

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        position = new ArrayList<>();

        expandableListView = (ExpandableListView) findViewById(R.id.topic);
        sqLiteConnector.insertLiked(sqLiteDatabase, "Test", true);
        sqLiteConnector.insertLiked(sqLiteDatabase, "Ein kleiner zweiter Test", false);


        new SyncTopicTask().addListener(this).execute();

        initButtons();
    }

    public void receiveData(){new SyncTopicTask().addListener(this).execute();}

    public void initButtons() {
        createTopic = findViewById(R.id.createTopic);
        results = findViewById(R.id.result);

        createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Thema.class));
            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ResultActivity.class));
            }
        });
    }

    private void initData() {
        Cursor cursor;
        cursor = sqLiteDatabase.query(false,SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},null, null, null, null,null, null);
        cursor.moveToFirst();
        Utils.logDebug(cursor.getCount());

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        geliked = new ArrayList<>();
        position = new ArrayList<>();


        Cursor cursor2 = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LIKED, new String[]{SQLiteConnectorSv.LIKED_CHECKED}, null, null, null, null, null);
        cursor2.moveToFirst();

        for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext()){
            Utils.logDebug(Boolean.valueOf(cursor2.getString(0)));
            boolean liked = Boolean.valueOf(cursor2.getString(0));
            geliked.add(liked);
        }

        topics = new String[cursor.getCount()];

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);
            Cursor tmp = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LIKED, new String[]{SQLiteConnectorSv.LIKED_TOPIC, SQLiteConnectorSv.LIKED_CHECKED}, SQLiteConnectorSv.LIKED_TOPIC + "='" + topic + "'", null, null, null, null);
            if(tmp.getCount()==0) {
                ContentValues tmpv = new ContentValues();
                tmpv.put(SQLiteConnectorSv.LIKED_TOPIC, topic);
                tmpv.put(SQLiteConnectorSv.LIKED_CHECKED, false);
                sqLiteDatabase.insert(SQLiteConnectorSv.TABLE_LIKED, null, tmpv);
            }

            topics[cursor.getPosition()] = topic;

            position.add(cursor.getPosition());
            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && !proposal1.equals(""))
                loesungen.add(proposal1);
            if (proposal2 != null && !proposal2.equals(""))
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);
            lastAdded = topic;
        }
        cursor2.close();
        cursor.close();
    }

    public void initCheckbox(){


        like = findViewById(R.id.check);
        like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String topic = like.getTag().toString();
                ContentValues values = new ContentValues();
                values.put(SQLiteConnectorSv.LIKED_TOPIC, topic);
                values.put(SQLiteConnectorSv.LIKED_CHECKED, like.isChecked());
                sqLiteDatabase.update(SQLiteConnectorSv.TABLE_LIKED, values, SQLiteConnectorSv.LIKED_TOPIC + "='" + topic + "'", null);


                Cursor cursor;



                cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null, null, null, null);
                int likes = Integer.parseInt(cursor.getString(6));
                if(like.isChecked())
                    likes++;
                else
                    likes--;
                values = new ContentValues();
                values.put(SQLiteConnectorSv.LETTERBOX_TOPIC, cursor.getString(0));
                values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL1, cursor.getString(1));
                values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL2, cursor.getString(2));
                values.put(SQLiteConnectorSv.LETTERBOX_DateOfCreation, cursor.getString(3));
                values.put(SQLiteConnectorSv.LETTERBOX_CREATOR, cursor.getString(4));
                values.put(SQLiteConnectorSv.LETTERBOX_LIKES, likes);


                sqLiteDatabase.update(SQLiteConnectorSv.TABLE_LETTERBOX, values, SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_briefkasten;
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
        return R.string.title_activity_briefkasten;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.letterbox;
    }

    @Override
    protected String getActivityTag() {
        return "SV-Activity";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        sqLiteConnector.close();
        sqLiteDatabase = null;
        sqLiteConnector = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sqLiteDatabase.close();
        sqLiteConnector.close();
        sqLiteDatabase = null;
        sqLiteConnector = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (sqLiteConnector == null)
        sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
    }

    @Override
    public void taskFinished(Object... params) {
        Utils.logDebug("done");
        initData();
        listAdapter = new de.slgdev.svBriefkasten.Adapter.ExpandableListAdapter(this, listDataHeader, listHash, geliked, position);
        expandableListView.setAdapter(listAdapter);
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(expandableListView.getContext());

                final EditText et = new EditText(expandableListView.getContext());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.logDebug("LongClick Builder");
                        Utils.logDebug(topics[i]);
                        Utils.logDebug(et.getText().toString());
                        if(et.getText().toString().equals("sLg?2018")) {
                            new RemoveTopic().execute(topics[i]);

                        }
                    }
                });

                alertDialogBuilder.setCancelable(false).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            }
        });
        swipeRefresh.setRefreshing(false);
    }

}

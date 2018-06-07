package de.slgdev.svBriefkasten.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class BriefkastenActivity extends LeoAppNavigationActivity {

    /**private static SQListeConnectorBriefkasten sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;*/
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private Button createTopic;
    private Button results;
    private RadioButton likeButton;
    private SharedPreferences sharedPref;
    private String lastAdded;

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



        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        receive();

        initExpandableListView();
        initButtons();
    }

    public void initButtons() {
        createTopic = findViewById(R.id.createTopic);
        results = findViewById(R.id.result);

        createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Thema.class));
            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
            }
        });
    }

    public void initExpandableListView() {
         expandableListView = findViewById(R.id.topic);
         initData();
         listAdapter = new de.slgdev.svBriefkasten.ExpandableListAdapter(this, listDataHeader,listHash);
         expandableListView.setAdapter(listAdapter);

    }

    private void initData() {

        String thema = "Thema";
        List<String> eins = new ArrayList<>();
        eins.add("Eine alternative Lösung");
        listDataHeader.add(thema);
        listHash.put(listDataHeader.get(listDataHeader.size()-1), eins);


        Cursor cursor;
        cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR},null, null, null, null ,null);
        //cursor = sqLiteDatabase.rawQuery("Select * From " + sqLiteConnector.TABLE_LETTERBOX, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);

            startActivity(new Intent(getApplicationContext(),ResultActivity.class));

            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && !proposal1.equals(""))
                loesungen.add(proposal1);
            if (proposal2 != null && !proposal2.equals(""))
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);
            lastAdded = topic;
        }

        cursor.close();
    }

    private void receive() {
        new SyncTopicTask().execute();
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
}

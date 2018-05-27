package de.slgdev.svBriefkasten.activity;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.slgdev.klausurplan.activity.KlausurplanActivity;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.schwarzes_brett.task.SyncNewsTask;
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

        receive();

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

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

         expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
             @Override
             public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                 return true;
             }
         });

    }

    private void initData() {

        /**listDataHeader.add("Hallo");


        List<String> eins = new ArrayList<>();
        eins.add("Ein kleiner Test");
        eins.add("Ein zweiter Test");

        listHash.put(listDataHeader.get(listDataHeader.size()-1), eins);

        listDataHeader.add("i");
        listDataHeader.add("bims");
        listDataHeader.add("1");

        List<String> zwei = new ArrayList<>();
        zwei.add("Klappt das hier auch?");

        List<String> drei = new ArrayList<>();
        drei.add("Fast geschafft");

        List<String> vier = new ArrayList<>();
        vier.add("Ende");


        listHash.put(listDataHeader.get(1), zwei);
        listHash.put(listDataHeader.get(2), drei);
        listHash.put(listDataHeader.get(3), vier);*/

        Cursor cursor;
        cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{sqLiteConnector.LETTERBOX_TOPIC, sqLiteConnector.LETTERBOX_PROPOSAL1, sqLiteConnector.LETTERBOX_PROPOSAL2, sqLiteConnector.LETTERBOX_DateOfCreation, sqLiteConnector.LETTERBOX_CREATOR},null, null, null, null ,null);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);

            //startActivity(new Intent(getApplicationContext(),ResultActivity.class));

            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && proposal1 != "")
                loesungen.add(proposal1);
            if (proposal2 != null && proposal2 != "")
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);
            lastAdded = topic;
        }
    }

   /**public void addTopic(String s, String solution1, String solution2)
    {
        listDataHeader.add(s);
        List<String> add = new ArrayList<>();
        if(solution1!=null && solution1!="")
            add.add(solution1);
        if(solution2!=null && solution2!="");
            add.add(solution2);
        listHash.put(listDataHeader.get(0), add);
        lastAdded = s;
    }*/


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

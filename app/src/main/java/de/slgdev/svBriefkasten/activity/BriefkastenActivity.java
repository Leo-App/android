package de.slgdev.svBriefkasten.activity;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.klausurplan.activity.KlausurplanActivity;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;

public class BriefkastenActivity extends LeoAppNavigationActivity {

    /**private static SQListeConnectorBriefkasten sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;*/
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private Button createTopic;
    private Button results;
    private Button likeButton;
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
                 likeButton = (Button) findViewById(R.id.likeButton);
                 likeButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                         startActivity(i);
                     }
                 });
                 return true;
             }
         });

    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Hallo");
        listDataHeader.add("i");
        listDataHeader.add("bims");
        listDataHeader.add("1");

        List<String> eins = new ArrayList<>();
        eins.add("Ein kleiner Test");
        eins.add("Ein zweiter Test");

        List<String> zwei = new ArrayList<>();
        zwei.add("Klappt das hier auch?");

        List<String> drei = new ArrayList<>();
        drei.add("Fast geschafft");

        List<String> vier = new ArrayList<>();
        vier.add("Ende");

        listHash.put(listDataHeader.get(0), eins);
        listHash.put(listDataHeader.get(1), zwei);
        listHash.put(listDataHeader.get(2), drei);
        listHash.put(listDataHeader.get(3), vier);

        Cursor cursor;
        cursor = sqLiteDatabase.query(sqLiteConnector.TABLE_LETTERBOX, new String[]{sqLiteConnector.LETTERBOX_TOPIC, sqLiteConnector.LETTERBOX_PROPOSAL1, sqLiteConnector.LETTERBOX_PROPOSAL2},null, null, null, null ,null);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);

            listDataHeader.add(topic);

            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && proposal1 != "")
                loesungen.add(proposal1);
            if (proposal2 != null && proposal2 != "")
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()),loesungen);
        }
    }

    public void addTopic(String s, String solution)
    {
        listDataHeader.add(s);
        List<String> add = new ArrayList<>();
        add.add(s);
        listHash.put(listDataHeader.get(0), add);
        lastAdded = s;
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

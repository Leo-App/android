package de.slgdev.svBriefkasten.activity;

import android.app.ExpandableListActivity;
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

import de.slgdev.leoapp.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefkasten);

        initExpandableListView();
        initButtons();
    }

    public void initButtons() {
        createTopic = findViewById(R.id.createTopic);
        results = findViewById(R.id.result);

        createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Hallo");
        listDataHeader.add("i");
        listDataHeader.add("bims");
        listDataHeader.add("1");

        List<String> eins = new ArrayList<>();
        eins.add("Ein kleiner Test");

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
    }

    public void addTopic(String s)
    {
        List<String> add = new ArrayList<>();
        add.add(s);
        listHash.put(listDataHeader.get(0), add);
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
}

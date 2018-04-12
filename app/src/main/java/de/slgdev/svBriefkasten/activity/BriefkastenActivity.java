package de.slgdev.svBriefkasten.activity;

import android.app.ExpandableListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;

public class BriefkastenActivity extends LeoAppNavigationActivity {

    /**private static SQListeConnectorBriefkasten sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;*/
    private ExpandableListView topic;
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
         createGroupList();

         topic = findViewById(R.id.topic);

    }

    public void createGroupList(){

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

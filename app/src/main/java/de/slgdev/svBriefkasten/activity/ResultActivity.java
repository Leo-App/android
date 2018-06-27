package de.slgdev.svBriefkasten.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.svBriefkasten.Adapter.SecondExpandableListAdapter;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class ResultActivity extends AppCompatActivity implements TaskStatusListener {

    private ExpandableListView resultsELW;
    private SwipeRefreshLayout swipeRefresh;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultsELW = findViewById(R.id.result);
        if(sqLiteConnector==null)
            sqLiteConnector = new SQLiteConnectorSv(this);
        if(sqLiteDatabase==null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        receive();                                                              //Die Daten werden geladen

        swipeRefresh = findViewById(R.id.refresh);                              //SwipeRefreshLayout wird initialisiert und die Funktion bestimmt
        swipeRefresh.setOnRefreshListener(this::receive);



    }

    @Override
    protected void onResume(){
        super.onResume();
        if(sqLiteConnector==null)
            sqLiteConnector = new SQLiteConnectorSv(this);
        if(sqLiteDatabase==null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
    }

    /**
     * Die vorhandenen Daten werden ausgelesen und sortiert nach Likes in die ExpandableListView eingefügt
     * zusätzlich wird für die Liste ein Adapter erstellt und dieser wird für die Liste verwendet
     */
    public void initELW(){
        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listHash = new HashMap<>();
        List<String> likes = new ArrayList<>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},null, null, null, null , SQLiteConnectorSv.LETTERBOX_LIKES + " DESC", null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);
            String like = cursor.getString(5);
            Utils.logDebug("current: " + topic);
            likes.add(like);

            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && !proposal1.equals(""))
                loesungen.add(proposal1);
            if (proposal2 != null && !proposal2.equals(""))
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);

        }
        cursor.close();

        SecondExpandableListAdapter adapter = new SecondExpandableListAdapter(this, listDataHeader, listHash, likes);
        resultsELW.setAdapter(adapter);
    }

    /**
     * Die Daten werden bei vorhandener Internetverbindung aus dem Internet geladen und durch SyncTopic in die SQLite geschrieben
     */
    public void receive(){
        if(Utils.isNetworkAvailable())
            new SyncTopicTask().addListener(this).execute();
        else
            Toast.makeText(getApplicationContext(), R.string.connection, Toast.LENGTH_LONG).show();
    }

    /**
     * Die Liste wird nach erfolgreichem Laden der Daten erstellt
     */
    @Override
    public void taskFinished(Object... params) {
        initELW();
        swipeRefresh.setRefreshing(false);
    }
}

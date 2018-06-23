package de.slgdev.svBriefkasten.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.svBriefkasten.Adapter.SecondExpandableListAdapter;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class ResultActivity extends AppCompatActivity implements TaskStatusListener {

    private ExpandableListView resultsELW;
    private SecondExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private List<String> likes;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultsELW = (ExpandableListView) findViewById(R.id.result);
        if(sqLiteConnector==null)
            sqLiteConnector = new SQLiteConnectorSv(this);
        if(sqLiteDatabase==null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        likes = new ArrayList<>();

    }

    public void initELW(){
        Cursor cursor;
        cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},null, null, null, null ,sqLiteConnector.LETTERBOX_LIKES + " DESC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);
            String like = cursor.getString(5);

            cursor.close();

            likes.add(like);

            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && proposal1 != "")
                loesungen.add(proposal1);
            if (proposal2 != null && proposal2 != "")
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);

        }

        SecondExpandableListAdapter adapter = new SecondExpandableListAdapter(this, listDataHeader, listHash, likes);
        resultsELW.setAdapter(adapter);
    }

    public void receive(){
        new SyncTopicTask().execute();
    }

    @Override
    public void taskFinished(Object... params) {
        initELW();
    }
}

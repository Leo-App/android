package de.slgdev.svBriefkasten.activity;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

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
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    private SwipeRefreshLayout swipeRefresh;
    private String[] topics;
    private SharedPreferences sharedPref;

    List<Boolean> geliked;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefkasten);

        sharedPref = getSharedPreferences("Briefkasten", MODE_PRIVATE);

        if (sqLiteConnector == null)                                           //Verbindung mit Datenbank wird hergestellt
            sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        swipeRefresh = findViewById(R.id.swipeRefresh);                     //Das SwipeRefreshLayout wird initialisiert
        swipeRefresh.setOnRefreshListener(this::receiveData);               //Und seine Funktion zugeordnet

        expandableListView = findViewById(R.id.topic);                      //ExpandableListView wird initialisiert

        receiveData();

        initData();
        initButtons();
    }


    /**
     * receiveData() sorgt dafür, dass die aktuellen Daten aus dem Internet geladen werden, sofern eine Internetverbindung besteht
     */
    public void receiveData(){
        if(Utils.isNetworkAvailable())
            new SyncTopicTask().addListener(this).execute();
        else
            Toast.makeText(getApplicationContext(), R.string.connection, Toast.LENGTH_LONG).show();
    }

    /**
     * Die Buttons werden erstellt und deren OnClickAufträge bestimmt
     */
    public void initButtons() {
        Button createTopic = findViewById(R.id.createTopic);
        Button results = findViewById(R.id.result);

        createTopic.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Thema.class)));

        results.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),ResultActivity.class)));
    }

    /**
     * Die ExpandableListView bekommt hier ihre aus dem Internet gezogenen Daten, oder die die schon vorher aus dem Internet geladen worden sind
     * und diese werden am Ende für den ListAdapter mitgegeben und anschließend die Liste mit der Methode initElw() erstellt
     */
    private void initData() {
        Cursor cursor;
        cursor = sqLiteDatabase.query(false,SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},null, null, null, null,null, null);
        cursor.moveToFirst();

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        geliked = new ArrayList<>();
        topics = new String[cursor.getCount()];

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String topic = cursor.getString(0);
            String proposal1=cursor.getString(1);
            String proposal2=cursor.getString(2);

            Boolean b = false;
            geliked.add(sharedPref.getBoolean(topic, b));
            topics[cursor.getPosition()] = topic;

            listDataHeader.add(topic);
            List<String> loesungen = new ArrayList<>();
            if (proposal1 != null && !proposal1.equals(""))
                loesungen.add(proposal1);
            if (proposal2 != null && !proposal2.equals(""))
                loesungen.add(proposal2);

            listHash.put(listDataHeader.get(listDataHeader.size()-1),loesungen);
        }

        cursor.close();
        initElw();
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
        if(sqLiteDatabase!=null)
            sqLiteDatabase.close();
        if(sqLiteConnector!=null)
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

    /**
     * Hier wird die Liste erstellt und die Funktion für langes drücken auf einen Eintrag deklariert
     */
    public void initElw(){
        ExpandableListAdapter listAdapter = new de.slgdev.svBriefkasten.Adapter.ExpandableListAdapter(this, listDataHeader, listHash, geliked);
        expandableListView.setAdapter(listAdapter);
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            /**Ab hier wird ein DialogBuilder erstellt, durch den man die Möglichkeit bekommt einen neuen Vorschlag abzugeben, sofern der Benutzer auf den Button vorschlag klickt
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(expandableListView.getContext());

                final EditText et = new EditText(expandableListView.getContext());
                et.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alertDialogBuilder.setView(et);

                alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.delete, (dialog, id) -> {                //AbsendenButton
                    if(et.getText().toString().equals("sLg?2018")) {
                        if(Utils.isNetworkAvailable())
                            new RemoveTopic().execute(topics[i]);
                        else
                            Toast.makeText(getApplicationContext(), R.string.connection, Toast.LENGTH_LONG).show();

                    }
                });

                alertDialogBuilder.setCancelable(false).setNegativeButton(R.string.cancel, (dialogInterface, i1) -> {       //Cancel-Button

                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            }
        });
    }

    /**
     * Wenn die Daten erfolgreich aus dem Internet geladen worden sind, hört der Aktualisierungskreis auf sich zu drehen und die Liste wird erstellt
     */
    @Override
    public void taskFinished(Object... params) {
        Utils.logDebug("done");
        initData();
        swipeRefresh.setRefreshing(false);
    }
}

package de.slgdev.svBriefkasten.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.svBriefkasten.task.AddTopic;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class Thema extends AppCompatActivity implements TaskStatusListener {

    private Button create;
    private android.widget.EditText thema;
    private EditText loesung;
    private SharedPreferences sharedPref;
    private String topic;
    private String proposal;
    private boolean con;
    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thema);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        con=false;

        thema =  findViewById(R.id.thema);
        thema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thema.setText("");
            }
        });

        loesung =  findViewById(R.id.solution);
        loesung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loesung.setText("");
            }
        });

        create = findViewById(R.id.button_createTopic);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thema.getText().length()!=0 && loesung.getText().length()!=0) {
                    topic = thema.getText().toString();
                    proposal = loesung.getText().toString();

                    checkTopic();

                }
            }
        });
    }

    public void checkTopic(){
        new SyncTopicTask().addListener(this).execute();
    }

    @Override
    public void taskFinished(Object... params) {
            if(con) {
                con=false;
                startActivity(new Intent(getApplicationContext(), BriefkastenActivity.class));
            }
            else {
                if (sqLiteConnector == null)
                    sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
                if (sqLiteDatabase == null)
                    sqLiteDatabase = sqLiteConnector.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.query(false, SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC}, SQLiteConnectorSv.LETTERBOX_TOPIC + " = ' " + topic + "'", null, null, null, null, null);

                con = true;
                Utils.logDebug(topic);
                Utils.logDebug(proposal);
                if (cursor.getCount() == 0) {
                    new AddTopic().execute(topic,proposal);

                }
                cursor.close();

            }

    }
}

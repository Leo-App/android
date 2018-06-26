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
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.svBriefkasten.task.AddTopic;
import de.slgdev.svBriefkasten.task.SyncTopicTask;

public class Thema extends AppCompatActivity implements TaskStatusListener {

    private android.widget.EditText thema;
    private EditText loesung;
    private String topic;
    private String proposal;
    private boolean con;
    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thema);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        con=false;

        thema =  findViewById(R.id.thema);
        thema.setOnClickListener(view -> thema.setText(""));                            //Wenn die EditTextViews angeklickt werden, wird der Text zurückgesetzt

        loesung =  findViewById(R.id.solution);
        loesung.setOnClickListener(view -> loesung.setText(""));

        Button create = findViewById(R.id.button_createTopic);                          //Wenn der Button erstellen gedrückt wird, wird die Methode checkTopic() aufgerufen
        create.setOnClickListener(view -> {
            if(thema.getText().length()!=0 && loesung.getText().length()!=0) {
                topic = thema.getText().toString();
                proposal = loesung.getText().toString();

                checkTopic();

            }
        });
    }

    /**
     * Die Methode lädt die Daten erstmal aus dem Internet und später wird geprüft, ob das Thema schon vorhanden ist
     */
    public void checkTopic(){
        if(Utils.isNetworkAvailable())
            new SyncTopicTask().addListener(this).execute();
        else
            Toast.makeText(getApplicationContext(), R.string.connection, Toast.LENGTH_LONG).show();
    }

    /**
     *Sind die Daten aus dem Internet geladen, wird geprüft, ob ein Thema schon vorhanden ist und nach erfolgreichem einfügen in die Datenbank wird die Activity gewechselt
     */
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

                Cursor cursor = sqLiteDatabase.query(false, SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC}, SQLiteConnectorSv.LETTERBOX_TOPIC + " = '" + topic + "'", null, null, null, null, null);

                if (cursor.getCount() == 0) {
                    con = true;
                    if(Utils.isNetworkAvailable())
                        new AddTopic().addListener(this).execute(topic,proposal);
                    else
                        Toast.makeText(getApplicationContext(), R.string.connection, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.exists, Toast.LENGTH_LONG).show();
                cursor.close();
            }

    }
}

package de.slgdev.svBriefkasten.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class Thema extends AppCompatActivity {

    private Button create;
    private android.widget.EditText thema;
    private EditText loesung;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thema);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thema = (EditText) findViewById(R.id.thema);

        loesung = (EditText) findViewById(R.id.solution);

        create = (Button) findViewById(R.id.button_createTopic);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thema.getText().length()!=0 && loesung.getText().length()!=0) {


                }
            }
        });
    }

}

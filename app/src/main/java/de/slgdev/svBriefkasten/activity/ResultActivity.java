package de.slgdev.svBriefkasten.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import de.slgdev.leoapp.R;

public class ResultActivity extends AppCompatActivity {

    private ListView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        results = (ListView)findViewById(R.id.result);
    }
}

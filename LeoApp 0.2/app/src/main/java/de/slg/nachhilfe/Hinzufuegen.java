package de.slg.nachhilfe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;


public class Hinzufuegen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String fach;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hinzufuegen);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Fach_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        initToolbar();

    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBarNavDrawer1);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.tutoring_title_new));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hinzufuegen, menu);
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        fach = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        fach = "Mathe";
    }

    public boolean ueberpruefe() {
        EmpfangeFaecherUser toll = new EmpfangeFaecherUser();
        toll.execute();
        setContentView(R.layout.activity_nachhilfeboerse);
        String[] faecher = new String[0];
        try {
            faecher = toll.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (String s : faecher) {
            if (s.equals(fach))
                return true;
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {

        if (mi.getItemId() == R.id.Aktion1) {
            Intent intent = new Intent(this, Nachhilfeboerse.class);
            startActivity(intent);
        } else {
            if (mi.getItemId() == R.id.Aktion2) {
                if (ueberpruefe()) {

                } else {
                    AnzeigeEinreichen s = new AnzeigeEinreichen();
                    s.execute(fach);
                    Intent intent = new Intent(this, Nachhilfeboerse.class);
                    startActivity(intent);

                }
            }
        }

        return true;
    }
}

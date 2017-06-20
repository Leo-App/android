package de.slg.stundenplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;

public class AuswahlActivity extends AppCompatActivity {

    private Menu menu;
    private AuswahlAdapter auswahlAdapter;
    private Stundenplanverwalter sv;
    private FachImporter importer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);

        SharedPreferences shaPre = getSharedPreferences("", MODE_PRIVATE); //Jaja, hier fehlt noch der Name... TODO: 27.05.2017
        String stufe = shaPre.getString("pref_key_level_general", "Q1"); //Ähm ich würde vllt nicht Q1 nehmen, wenn noch keine Stufe eingestellt ist ^Gianni TODO Lesen

        if (!fileExistiert()) {
            importer = new FachImporter(getApplicationContext(), stufe);
            importer.execute();
        }

        initToolbar();
        initSV();
        initListView();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.stunden));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.listA);
        auswahlAdapter = new AuswahlAdapter(getApplicationContext(), sv.gibFaecherKurz(), sv);
        listView.setAdapter(auswahlAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.isEnabled()) {
                    CheckBox c = (CheckBox) view.findViewById(R.id.checkBox);
                    c.setChecked(!c.isChecked());
                    auswahlAdapter.refresh();
                    if (auswahlAdapter.isOneSelected()) {
                        MenuItem item = menu.findItem(R.id.action_speichern);
                        item.setVisible(true);
                        item.setEnabled(true);
                    }
                }
            }
        });
    }

    private void initSV() {
        if (importer != null)
            try {
                importer.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        sv = new Stundenplanverwalter(getApplicationContext(), "allefaecher.txt");
        if (sv.gibFaecherSort().length == 0) {
            Snackbar snack = Snackbar.make(findViewById(R.id.relative), R.string.SnackBarMes, Snackbar.LENGTH_SHORT);
            snack.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.stundenplan_auswahl, this.menu);
        MenuItem menuItem = this.menu.findItem(R.id.action_speichern);
        menuItem.setVisible(false);
        menuItem.setEnabled(false);
        return true;
    }

    /**
     * Speichert alle ausgewählten Fächer in einem Array und diese mit
     * der Methode SPEICHERN in einem Textdokument und
     * Ruft die STUNDENPLAN ACTIVITY auf
     * Ruft SPEICHERN und ALLEMARKIERTEN auf
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_speichern) {
            sv.inTextDatei(getApplicationContext(), auswahlAdapter.gibAlleMarkierten());
            startActivity(new Intent(getApplicationContext(), WrapperStundenplanActivity.class));
        } else if (mi.getItemId() == R.id.action_refresh) {
            this.deexistiere();
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class)); //Auch hässlich // TODO: 27.05.2017
        }
        finish();
        return true;
    }

    private boolean fileExistiert() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(openFileInput("allefaecher.txt")));
            if (br.readLine() != null) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deexistiere() {
        try {
            BufferedWriter bw =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    openFileOutput("allefaecher.txt", MODE_PRIVATE)));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
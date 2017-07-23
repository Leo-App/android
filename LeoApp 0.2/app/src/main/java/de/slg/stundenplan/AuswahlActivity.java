package de.slg.stundenplan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class AuswahlActivity extends AppCompatActivity {
    private Menu menu;
    private AuswahlAdapter adapter;
    private StundenplanDB db;
    private FachImporter importer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);

<<<<<<< HEAD
        SharedPreferences shaPre = getSharedPreferences("", MODE_PRIVATE); //Ach, hier ist der Fehler TODO: 27.05.2017
        String stufe = shaPre.getString("pref_key_level_general", null);
        stufe = "Q1"; //Nur solange wie oben noch der Name fehlt....
=======
        String stufe = Utils.getUserStufe();
>>>>>>> b818f2b420c437c2b4ca1c6a0ce23d9c25dcff4a

        if (!fileExistiert() && stufe != null) {
            importer = new FachImporter(getApplicationContext(), stufe);
            importer.execute();
        }

        initToolbar();

        if (stufe.equals("")) {
            Snackbar.make(findViewById(R.id.relative), R.string.SnackBarMes2, Snackbar.LENGTH_SHORT).show();
//            TODO: Button zu den Einstellungen
        } else {
            initSV();
            initListView();
        }
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.stunden));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.listA);
        adapter = new AuswahlAdapter(getApplicationContext(), db.getFaecher());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.isEnabled()) {
                    boolean checked = adapter.toggleCheck(position);
                    Fach f = adapter.fachArray[position];
                    double[] stunden = db.gibStunden(f.id);
                    for (double d : stunden) {
                        adapter.ausgewaehlteStunden[(int) (d) - 1][(int) (d * 10 % 10) - 1] = checked;
                    }
                    if (checked)
                        adapter.ausgewaehlteFaecher.append(f.gibKurz().substring(0, 2));
                    else {
                        adapter.ausgewaehlteFaecher.contains(f.gibKurz().substring(0, 2));
                        adapter.ausgewaehlteFaecher.remove();
                    }
                    refresh();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 100);
    }

    private void refresh() {
        adapter.refresh();
        int anzahl = adapter.gibAnzahlAusgewaehlte();
        if (anzahl > 0) {
            MenuItem item = menu.findItem(R.id.action_speichern);
            item.setVisible(true);
            item.setEnabled(true);
        }
        if (anzahl == 1)
            getSupportActionBar().setTitle("1 Kurs ausgewählt");
        else
            getSupportActionBar().setTitle(anzahl + " Kurse ausgewählt");
    }

    private void initSV() {
        db = Utils.getStundDB();
        try {
            if (importer != null)
                importer.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (db.getFaecher().length == 0) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_speichern) {
            db.loescheWahlen();
            for (int id : adapter.gibMarkierteIds()) {
                db.waehleFach(id);
            }
            new Stundenplanverwalter(getApplicationContext(), "allefaecher.txt").inTextDatei(Utils.getStundDB().getGewaehlteFaecher());
        } else if (mi.getItemId() == R.id.action_refresh) {
            deleteFile("allefaecher.txt");
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
        }
        finish();
        return true;
    }

    private boolean fileExistiert() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("allefaecher.txt")));
            if (reader.readLine() != null) {
                reader.close();
                return true;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
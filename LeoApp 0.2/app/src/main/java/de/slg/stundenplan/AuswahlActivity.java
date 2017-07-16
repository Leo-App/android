package de.slg.stundenplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class AuswahlActivity extends AppCompatActivity {
    private Menu menu;
    private AuswahlAdapter auswahlAdapter;
    private Stundenplanverwalter sv;
    private FachImporter importer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);

        String stufe = Utils.getUserStufe();

        if (!fileExistiert() && stufe != null) {
            importer = new FachImporter(getApplicationContext(), stufe);
            importer.execute();
        }

        initToolbar();

        if (stufe == null) {
            Snackbar.make(findViewById(R.id.relative), R.string.SnackBarMes2, Snackbar.LENGTH_SHORT).show();
            //TODO Ich will einen Button der direkt zu den Einstellungen geht!!!!
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
        auswahlAdapter = new AuswahlAdapter(getApplicationContext(), sv.gibFaecherKuerzel(), sv);
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
        try {
            if (importer != null)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_speichern) {
            sv.inTextDatei(auswahlAdapter.gibAlleMarkierten());
            startActivity(new Intent(getApplicationContext(), WrapperStundenplanActivity.class));
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
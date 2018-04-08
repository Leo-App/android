package de.slgdev.stundenplan.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.leoapp.view.ActivityStatus;
import de.slgdev.stundenplan.task.Importer;
import de.slgdev.stundenplan.view.KursAdapter;

public class AuswahlActivity extends ActionLogActivity implements TaskStatusListener {

    private Menu                       menu;
    private KursAdapter                adapter;
    private SQLiteConnectorStundenplan database;
    private int                        anzahl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);

        Utils.getController().registerAuswahlActivity(this);

        database = new SQLiteConnectorStundenplan(getApplicationContext());

        initToolbar();

        if (!database.hatGewaehlt()) {
            new Importer().addListener(this).execute();
        } else {
            initListView();
        }
    }

    @Override
    protected String getActivityTag() {
        return "AuswahlActivity";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stundenplan_auswahl, menu);
        MenuItem menuItem = menu.findItem(R.id.action_speichern);
        menuItem.setVisible(anzahl > 0);

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_speichern) {
            database.deleteSelection();
            for (int id : adapter.gibMarkierteIds()) {
                database.chooseSubject(id);
                if (database.mussSchriftlich(id))
                    database.setWritten(true, id);
            }
        }
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerAuswahlActivity(null);

        Utils.getController().getStundenplanActivity().refreshUI();
        if (!database.hatGewaehlt()) {
            Utils.getController().getStundenplanActivity().finish();
        }

        database.close();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.stunden));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void initListView() {
        ListView listView = findViewById(R.id.listView);

        adapter = new KursAdapter(database, getApplicationContext(), database.getSubjects());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (view.isEnabled()) {
                adapter.toggleGewaehlt(position);
                refresh();
            }
        });

        refresh();
    }

    protected void refresh() {
        adapter.refresh();
        anzahl = adapter.gibAnzahlAusgewaehlte();

        if (menu != null) {
            menu.findItem(R.id.action_speichern).setVisible(anzahl > 0);
        }

        if (anzahl == 1)
            getSupportActionBar().setTitle(R.string.one_course_sel);
        else if (anzahl == 0)
            getSupportActionBar().setTitle(getString(R.string.stunden));
        else
            getSupportActionBar().setTitle(getString(R.string.multiple_course_selected, anzahl));
    }

    @Override
    public void taskFinished(Object... params) {
        if (getStatus() == ActivityStatus.ACTIVE) {
            initListView();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

}
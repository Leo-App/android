package de.slgdev.stundenplan.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.stundenplan.task.Importer;
import de.slgdev.stundenplan.utility.Fach;

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
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.stunden));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void initListView() {
        ListView listView = findViewById(R.id.listView);

        adapter = new KursAdapter(getApplicationContext(), database.getSubjects());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.isEnabled()) {
                    boolean  checked = adapter.toggleCheckBox(position);
                    Fach     f       = adapter.fachArray[position];
                    double[] stunden = database.gibStunden(f.id);
                    for (double d : stunden) {
                        adapter.ausgewaehlteStunden[(int) (d - 1)][(int) (d * 10 % 10 - 1)] = checked;
                    }
                    if (checked)
                        if (!f.getKuerzel().startsWith("IB"))
                            adapter.ausgewaehlteFaecher.append(f.getKuerzel().substring(0, 2));
                        else
                            adapter.ausgewaehlteFaecher.append(f.getKuerzel().substring(3, 6));
                    else {
                        if (!f.getKuerzel().startsWith("IB"))
                            adapter.ausgewaehlteFaecher.contains(f.getKuerzel().substring(0, 2));
                        else
                            adapter.ausgewaehlteFaecher.contains(f.getKuerzel().substring(3, 6));
                        adapter.ausgewaehlteFaecher.remove();
                    }
                    refresh();
                }
            }
        });

        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getView(i, null, listView);
        }
        refresh();
    }

    private void refresh() {
        adapter.refresh();
        anzahl = adapter.gibAnzahlAusgewaehlte();
        if (menu != null) {
            menu.findItem(R.id.action_speichern).setVisible(anzahl > 0);
        }
        if (anzahl == 1)
            getSupportActionBar().setTitle(R.string.one_course_sel);
        else
            getSupportActionBar().setTitle(getString(R.string.multiple_course_selected, anzahl));
    }

    @Override
    public void taskStarts() {
        //STUB
    }

    @Override
    public void taskFinished(Object... params) {
        initListView();
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private class KursAdapter extends ArrayAdapter<Fach> {
        final         Fach[]       fachArray;
        final         List<String> ausgewaehlteFaecher;
        final         boolean[][]  ausgewaehlteStunden;
        private final View[]       views;
        private final CheckBox[]   cbs;

        KursAdapter(Context context, Fach[] array) {
            super(context, R.layout.list_item_kurs, array);
            fachArray = array;
            views = new View[array.length];
            cbs = new CheckBox[array.length];
            ausgewaehlteFaecher = new List<>();
            ausgewaehlteStunden = new boolean[5][10];
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            if (views[position] == null) {
                views[position] = getLayoutInflater().inflate(R.layout.list_item_kurs, null);
                views[position].setEnabled(true);

                TextView tvFach    = views[position].findViewById(R.id.fach_auswahl);
                TextView tvKuerzel = views[position].findViewById(R.id.kürzel_auswahl);
                TextView tvLehrer  = views[position].findViewById(R.id.lehrer_auswahl);
                TextView tvKlasse  = views[position].findViewById(R.id.klasse_auswahl);
                CheckBox checkBox  = views[position].findViewById(R.id.checkBox);
                Fach     current   = fachArray[position];

                tvFach.setText(current.getName());
                tvKuerzel.setText(current.getKuerzel());
                tvLehrer.setText(current.getLehrer());
                checkBox.setChecked(database.istGewaehlt(current.id));

                if (Utils.getUserStufe().matches("[0-9]+")) {
                    tvKlasse.setVisibility(View.VISIBLE);
                    tvKlasse.setText(current.getKlasse());
                }

                if (checkBox.isChecked()) {
                    if (!current.getKuerzel().startsWith("IB"))
                        ausgewaehlteFaecher.append(current.getKuerzel().substring(0, 2));
                    else
                        ausgewaehlteFaecher.append(current.getKuerzel().substring(3, 6));
                    double[] stunden = database.gibStunden(current.id);
                    for (double d : stunden) {
                        ausgewaehlteStunden[(int) (d) - 1][(int) (d * 10 % 10) - 1] = true;
                    }
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else if (ausgewaehlteStunden[current.getTag() - 1][current.getStunde() - 1] || (current.getKuerzel().startsWith("IB") ? ausgewaehlteFaecher.contains(current.getKuerzel().substring(3, 6)) : ausgewaehlteFaecher.contains(current.getKuerzel().substring(0, 2)))) {
                    views[position].setEnabled(false);
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                }
                cbs[position] = checkBox;
            }
            return views[position];
        }

        void refresh() {
            for (int i = 0; i < views.length; i++) {
                if (views[i] != null) {
                    Fach     current   = fachArray[i];
                    CheckBox c         = cbs[i];
                    TextView tvFach    = views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer  = views[i].findViewById(R.id.lehrer_auswahl);
                    if (c.isChecked()) {
                        views[i].setEnabled(true);
                        tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    } else if (ausgewaehlteStunden[current.getTag() - 1][current.getStunde() - 1] || (current.getKuerzel().startsWith("IB") ? ausgewaehlteFaecher.contains(current.getKuerzel().substring(3, 6)) : ausgewaehlteFaecher.contains(current.getKuerzel().substring(0, 2)))) {
                        views[i].setEnabled(false);
                        tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                        tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                        tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                    } else {
                        views[i].setEnabled(true);
                        tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                        tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                        tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                    }
                }
            }
        }

        int gibAnzahlAusgewaehlte() {
            int anzahl = 0;
            for (CheckBox c : cbs)
                if (c != null && c.isChecked())
                    anzahl++;
            return anzahl;
        }

        int[] gibMarkierteIds() {
            List<Integer> liste = new List<>();
            for (int i = 0; i < fachArray.length; i++)
                if (views[i] != null && ((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked())
                    liste.append(fachArray[i].id);
            int[] ids = new int[liste.size()];
            liste.toFirst();
            for (int i = 0; i < ids.length; i++, liste.next()) {
                ids[i] = liste.getContent();
            }
            return ids;
        }

        boolean toggleCheckBox(int position) {
            if (cbs[position] != null) {
                cbs[position].setChecked(!cbs[position].isChecked());
                return cbs[position].isChecked();
            }
            return false;
        }
    }
}
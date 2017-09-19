package de.slg.stundenplan;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.List;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class AuswahlActivity extends AppCompatActivity {
    private Menu          menu;
    private KursAdapter   adapter;
    private StundenplanDB db;
    private FachImporter  importer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);
        Utils.getController().registerAuswahlActivity(this);
        String stufe = Utils.getUserStufe();
        if (!stufe.equals("")) {
            importer = new FachImporter(getApplicationContext(), stufe);
            importer.execute();
        }
        initToolbar();
        if (stufe.equals("")) {
            initSnackbarNoGrade();
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
        }
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerAuswahlActivity(null);
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

        adapter = new KursAdapter(getApplicationContext(), db.getFaecher());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.isEnabled()) {
                    boolean  checked = adapter.toggleCheckBox(position);
                    Fach     f       = adapter.fachArray[position];
                    double[] stunden = db.gibStunden(f.id);
                    for (double d : stunden) {
                        Log.e("TAG", String.valueOf(d));
                        adapter.ausgewaehlteStunden[(int) (d - 1)][(int) (d * 10 % 10 - 1)] = checked;
                    }
                    if (checked)
                        if (!f.gibKurz().startsWith("IB"))
                            adapter.ausgewaehlteFaecher.append(f.gibKurz().substring(0, 2));
                        else
                            adapter.ausgewaehlteFaecher.append(f.gibKurz().substring(3, 6));
                    else {
                        if (!f.gibKurz().startsWith("IB"))
                            adapter.ausgewaehlteFaecher.contains(f.gibKurz().substring(0, 2));
                        else
                            adapter.ausgewaehlteFaecher.contains(f.gibKurz().substring(3, 6));
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

    private void initSnackbarNoGrade() {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.relative), R.string.SnackBarMes2, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.snackbar_select), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });
        snackbar.show();
    }

    private void refresh() {
        adapter.refresh();
        int anzahl = adapter.gibAnzahlAusgewaehlte();
        if (anzahl > 0 && menu != null) {
            MenuItem item = menu.findItem(R.id.action_speichern);
            item.setVisible(true);
            item.setEnabled(true);
        }
        if (anzahl == 1)
            getSupportActionBar().setTitle("1 Kurs ausgewählt");
        else
            getSupportActionBar().setTitle(anzahl + " Kurse ausgewählt");
    }

    private void initDB() {
        db = Utils.getController().getStundplanDataBase();
        if (db.getFaecher().length == 0) {
            Snackbar snack = Snackbar.make(findViewById(R.id.relative), R.string.SnackBarMes, Snackbar.LENGTH_SHORT);
            snack.show();
        }
    }

    static class FachImporter extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final String  stufe;

        FachImporter(Context c, String pStufe) {
            this.context = c;
            stufe = pStufe;
        }

        @Override
        protected void onPreExecute() {
            if (Utils.getController().getAuswahlActivity() != null) {
                Utils.getController().getAuswahlActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.checkNetwork()) {
                Log.e("FachImporter", "started");
                try {
                    HttpURLConnection connection = (HttpURLConnection)
                            new URL("http://moritz.liegmanns.de/" + "stundenplan_neu.txt")
                                    .openConnection();
                    connection.setRequestProperty("Authorization", Utils.authorization);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getInputStream()));
                    BufferedWriter writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            context.openFileOutput(
                                                    "testdaten.txt",
                                                    Context.MODE_PRIVATE)));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        writer.write(line.replace("L�Z", "LÜZ").replace("CH�", "CHÜ").replace("BI�", "BIÜ"));
                        writer.newLine();
                    }
                    reader.close();
                    writer.close();

                    reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            context.openFileInput("testdaten.txt")));
                    String lastKurzel = "";
                    long   lastID     = -1;
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        String[] fach = line.replace("\"", "").split(",");
                        if (fach[1].replace("0", "").startsWith(stufe)) {
                            Log.e("TAG", line);
                            if (!fach[3].equals(lastKurzel)) {
                                lastID = Utils.getController().getStundplanDataBase().insertFach(fach[3], fach[2]);
                                lastKurzel = fach[3];
                                if (Utils.getUserPermission() == 2 && fach[2].equals(Utils.getLehrerKuerzel().toUpperCase())) {
                                    Utils.getController().getStundplanDataBase().waehleFach(lastID);
                                    Utils.getController().getStundplanDataBase().setzeSchriftlich(true, lastID);
                                }
                            }
                            Utils.getController().getStundplanDataBase().insertStunde(lastID, Integer.parseInt(fach[5]), Integer.parseInt(fach[6]), fach[4]);
                        }
                    }
                    reader.close();
                    Log.e("FachImporter", "done!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (Utils.getController().getAuswahlActivity() != null) {
                Utils.getController().getAuswahlActivity().initDB();
                Utils.getController().getAuswahlActivity().initListView();
                Utils.getController().getAuswahlActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        }
    }

    private class KursAdapter extends ArrayAdapter<Fach> {
        final         Fach[]        fachArray;
        final         List<String>  ausgewaehlteFaecher;
        final         boolean[][]   ausgewaehlteStunden;
        private final View[]        views;
        private final CheckBox[]    cbs;
        private final StundenplanDB db;

        KursAdapter(Context context, Fach[] array) {
            super(context, R.layout.list_item_kurs, array);
            db = Utils.getController().getStundplanDataBase();
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

                TextView tvFach    = (TextView) views[position].findViewById(R.id.fach_auswahl);
                TextView tvKuerzel = (TextView) views[position].findViewById(R.id.kürzel_auswahl);
                TextView tvLehrer  = (TextView) views[position].findViewById(R.id.lehrer_auswahl);
                CheckBox checkBox  = (CheckBox) views[position].findViewById(R.id.checkBox);
                Fach     current   = fachArray[position];

                tvFach.setText(current.gibName());
                tvKuerzel.setText(current.gibKurz());
                tvLehrer.setText(current.gibLehrer());
                checkBox.setChecked(db.istGewaehlt(current.id));

                if (checkBox.isChecked()) {
                    if (!current.gibKurz().startsWith("IB"))
                        ausgewaehlteFaecher.append(current.gibKurz().substring(0, 2));
                    else
                        ausgewaehlteFaecher.append(current.gibKurz().substring(3, 6));
                    double[] stunden = db.gibStunden(current.id);
                    for (double d : stunden) {
                        ausgewaehlteStunden[(int) (d) - 1][(int) (d * 10 % 10) - 1] = true;
                    }
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || (current.gibKurz().startsWith("IB") ? ausgewaehlteFaecher.contains(current.gibKurz().substring(3, 6)) : ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2)))) {
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
                    TextView tvFach    = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer  = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    if (c.isChecked()) {
                        views[i].setEnabled(true);
                        tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || (current.gibKurz().startsWith("IB") ? ausgewaehlteFaecher.contains(current.gibKurz().substring(3, 6)) : ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2)))) {
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
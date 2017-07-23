package de.slg.klausurplan;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class KlausurActivity extends AppCompatActivity {

    static Klausur currentKlausur;
    private EditText eingabeFach;
    private EditText eingabeDatum;
    private EditText eingabeNotiz;
    private EditText eingabeNote;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_klausur);

        initToolbar();
        initEditTexts();
        initSnackbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.klausur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(eingabeFach.getWindowToken(), 0);
        if (item.getItemId() == R.id.action_save && currentKlausur != null) {
            if (eingabeFach.getText().length() == 0 || eingabeDatum.getText().length() < 8 || !istDatumFormat(eingabeDatum.getText().toString())) {
                snackbar.show();
                return false;
            }
            klausurSpeichern();
        } else if (item.getItemId() == R.id.action_delete) {
            klausurLöschen();
        }
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(getString(R.string.title_activity));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.snack), getString(R.string.snackbar_ungültig), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        snackbar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
    }

    private void initEditTexts() {
        eingabeFach = (EditText) findViewById(R.id.eingabeFach);
        eingabeDatum = (EditText) findViewById(R.id.eingabeDatum);
        eingabeNotiz = (EditText) findViewById(R.id.eingabeNotiz);
        eingabeNote = (EditText) findViewById(R.id.eingabeNote);
        if (currentKlausur != null) {
            eingabeFach.setText(currentKlausur.getFach());
            eingabeDatum.setText(currentKlausur.getDatum(false));
            eingabeNotiz.setText(currentKlausur.getNotiz());
            eingabeNote.setText(currentKlausur.getNote());
        }
    }

    private void klausurSpeichern() {
        currentKlausur.setDatum(getDate(eingabeDatum.getText().toString()));
        currentKlausur.setNotiz(eingabeNotiz.getText().toString());
        currentKlausur.setNote(eingabeNote.getText().toString());
        if (currentKlausur.getFach().equals("")) {
            currentKlausur.setFach(eingabeFach.getText().toString());
            Utils.getKlausurplanActivity().add(currentKlausur, true);
        } else {
            Utils.getKlausurplanActivity().remove(currentKlausur);
            currentKlausur.setFach(eingabeFach.getText().toString());
            Utils.getKlausurplanActivity().add(currentKlausur, true);
        }
    }

    private void klausurLöschen() {
        if (!currentKlausur.getFach().equals(""))
            Utils.getKlausurplanActivity().remove(currentKlausur);
    }

    private boolean istDatumFormat(String s) {
        String[] parts = s.replace('.', '_').split("_");
        if (parts.length != 3)
            return false;
        int day, month;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return !(month < 1 || day < 1 || day > 31 || month > 12);
    }

    private Date getDate(String s) {
        if (istDatumFormat(s)) {
            String[] parts = s.replace('.', '_').split("_");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = 2000 + Integer.parseInt(parts[2]);
                Calendar c = new GregorianCalendar();
                c.set(year, month, day, 0, 0, 0);
                return c.getTime();
            }
        }
        return null;
    }
}
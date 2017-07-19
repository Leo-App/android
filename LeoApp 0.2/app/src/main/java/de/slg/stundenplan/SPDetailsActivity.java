package de.slg.stundenplan;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class SPDetailsActivity extends AppCompatActivity {
    private Fach fach;

    private EditText etNotiz;
    private CheckBox cbSchrift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_details);

        fach = Utils.getStundDB().getFach(getIntent().getIntExtra("tag", 0), getIntent().getIntExtra("stunde", 0));

        initToolbar();
        initDetails();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_plan));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDetails() {
        TextView tvZeit = (TextView) findViewById(R.id.uhrzeit_details);
        TextView tvRaum = (TextView) findViewById(R.id.raumnr_details);
        TextView tvLehrer = (TextView) findViewById(R.id.lehrerK_details);
        etNotiz = (EditText) findViewById(R.id.notizFeld_details);
        cbSchrift = (CheckBox) findViewById(R.id.checkBox_schriftlich);

        if (!fach.gibKurz().equals("FREI")) {
            getSupportActionBar().setTitle(fach.gibName() + " " + fach.gibKurz().substring(2));
            tvZeit.setText(Utils.getStundDB().gibZeiten(fach));
            tvRaum.setText(fach.gibRaum());
            tvLehrer.setText(fach.gibLehrer());
            etNotiz.setText(fach.gibNotiz());
            cbSchrift.setChecked(fach.gibSchriftlich());
        } else {
            getSupportActionBar().setTitle("Freistunde");
            tvRaum.setVisibility(View.GONE);
            tvLehrer.setVisibility(View.GONE);
            cbSchrift.setVisibility(View.GONE);
            findViewById(R.id.raum_details).setVisibility(View.GONE);
            findViewById(R.id.lehrer_details).setVisibility(View.GONE);
            tvZeit.setText(Utils.getStundDB().gibZeit(fach.gibTag(), fach.gibStunde()));
            etNotiz.setText(fach.gibNotiz());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_det_speichern) {
            String notiz = etNotiz.getText().toString();
            if (!fach.gibKurz().equals("FREI")) {
                boolean b = cbSchrift.isChecked();
                fach.setzeSchriftlich(b);
                Utils.getStundDB().setzeSchriftlich(b, fach.id);
            }
            fach.setzeNotiz(notiz);
            Utils.getStundDB().setzeNotiz(notiz, fach.id);
            new Stundenplanverwalter(getApplicationContext(), "meinefaecher.txt")
                    .inTextDatei(Utils.getStundDB().getGewaehlteFaecher());
        }
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        if (fach.gibNotiz().equals(""))
            Utils.getStundDB().deleteFreistunde(fach.gibTag(), fach.gibStunde());
    }
}
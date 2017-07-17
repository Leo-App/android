package de.slg.stundenplan;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

        fach = Utils.getStundDB().getFach(getIntent().getIntExtra("fid", 0));

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
        TextView twZeit = (TextView) findViewById(R.id.uhrzeit_details);
        TextView twRaum = (TextView) findViewById(R.id.raumnr_details);
        TextView twLehrer = (TextView) findViewById(R.id.lehrerK_details);
        etNotiz = (EditText) findViewById(R.id.notizFeld_details);
        cbSchrift = (CheckBox) findViewById(R.id.checkBox_schriftlich);

        getSupportActionBar().setTitle(fach.gibName() + " " + fach.gibKurz().substring(2));
        twZeit.setText(Utils.getStundDB().gibZeiten(fach));
        twRaum.setText(fach.gibRaum());
        twLehrer.setText(fach.gibLehrer());
        etNotiz.setText(fach.gibNotiz());
        if (fach.gibSchriftlich()) {
            cbSchrift.setChecked(true);
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
            if (cbSchrift.isChecked() != fach.gibSchriftlich()) {
                fach.setzeSchriftlich(cbSchrift.isChecked());
            }
            fach.setzeNotiz(etNotiz.getText().toString());
            Fach[] mon = Utils.getStundDB().gewaehlteFaecherAnTag(1), die = Utils.getStundDB().gewaehlteFaecherAnTag(2), mit = Utils.getStundDB().gewaehlteFaecherAnTag(3), don = Utils.getStundDB().gewaehlteFaecherAnTag(4), fre = Utils.getStundDB().gewaehlteFaecherAnTag(5);
            Fach[] alle = new Fach[mon.length + die.length + mit.length + don.length + fre.length];
            int i = 0;
            for (int iMo = 0; iMo < mon.length; iMo++, i++) {
                alle[i] = mon[iMo];
            }
            for (int iDi = 0; iDi < die.length; iDi++, i++) {
                alle[i] = die[iDi];
            }
            for (int iMi = 0; iMi < mit.length; iMi++, i++) {
                alle[i] = mit[iMi];
            }
            for (int iDo = 0; iDo < don.length; iDo++, i++) {
                alle[i] = don[iDo];
            }
            for (int iFr = 0; iFr < fre.length; iFr++, i++) {
                alle[i] = fre[iFr];
            }
            new Stundenplanverwalter(getApplicationContext(), "meinefaecher.txt").inTextDatei(alle);
        }
        finish();
        return true;
    }
}
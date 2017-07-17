package de.slg.stundenplan;

import android.content.Intent;
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
    private Stundenplanverwalter stundenplanverwalter;
    private Fach[] faecherSP;

    private EditText etNotiz;
    private CheckBox cbSchrift;

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_plan));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        stundenplanverwalter = new Stundenplanverwalter(getApplicationContext(), "meinefaecher.txt");
        faecherSP = stundenplanverwalter.gibFaecherSort();

        pos = sucheFachPos(WrapperStundenplanActivity.akTag, WrapperStundenplanActivity.akStunde);

        TextView twName = (TextView) findViewById(R.id.name_details);
        TextView twZeit = (TextView) findViewById(R.id.uhrzeit_details);
        TextView twRaum = (TextView) findViewById(R.id.raumnr_details);
        TextView twLehrer = (TextView) findViewById(R.id.lehrerK_details);
        etNotiz = (EditText) findViewById(R.id.notizFeld_details);
        cbSchrift = (CheckBox) findViewById(R.id.checkBox_schriftlich);

        if (pos != -1) {
            twName.setText(faecherSP[pos].gibName() + " " + faecherSP[pos].gibKurz().substring(2));
            twZeit.setText(Utils.getStundDB().gibZeiten(faecherSP[pos]));
            twRaum.setText(faecherSP[pos].gibRaum());
            twLehrer.setText(faecherSP[pos].gibLehrer());
            etNotiz.setText(faecherSP[pos].gibNotiz());
            if (faecherSP[pos].gibSchriftlich()) {
                cbSchrift.setChecked(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu me) {
        getMenuInflater().inflate(R.menu.details, me);
        return super.onCreateOptionsMenu(me);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_det_speichern) {
            if (cbSchrift.isChecked() != faecherSP[pos].gibSchriftlich()) {
                faecherSP[pos].setzeSchriftlich(cbSchrift.isChecked());
            }
            faecherSP[pos].setzeNotiz(etNotiz.getText().toString());
            stundenplanverwalter.inTextDatei(faecherSP);
            startActivity(new Intent(getApplicationContext(), WrapperStundenplanActivity.class));
        }
        finish();
        return true;
    }

    private String macheTag(int tag) {
        switch (tag) {
            case 1:
                return getString(R.string.montag);
            case 2:
                return getString(R.string.dienstag);
            case 3:
                return getString(R.string.mittwoch);
            case 4:
                return getString(R.string.donnerstag);
            case 5:
                return getString(R.string.freitag);
            default:
                return getString(R.string.montag);
        }
    }

    private int sucheFachPos(int tag, int stunde) {
        for (int c = 0; c < faecherSP.length; c++) {
            if (faecherSP[c].gibTag() == tag && faecherSP[c].gibStunde() == stunde) {
                return c;
            }
        }
        return -1;
    }
}
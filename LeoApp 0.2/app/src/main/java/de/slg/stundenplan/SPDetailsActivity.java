package de.slg.stundenplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.slg.leoapp.R;

public class SPDetailsActivity extends AppCompatActivity {
    Stundenplanverwalter stuVe;
    Fach[] faecherSP;
    String tag;
    String stunde;

    EditText etNotiz;
    CheckBox cbSchrift;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_plan));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Log.e("Luzzzia","Ich bin in SP-Details");
        tag = WrapperStundenplanActivity.akTag;
        stunde = WrapperStundenplanActivity.akStunde;
        stuVe = new Stundenplanverwalter(getApplicationContext(), "meinefaecher.txt");
        faecherSP = stuVe.gibFaecherSort();

        pos = this.sucheFachPos(tag, stunde);

        TextView twName = (TextView) this.findViewById(R.id.name_detail);
        TextView twTag = (TextView) this.findViewById(R.id.tag_details);
        TextView twZeit = (TextView) this.findViewById(R.id.uhrzeit_details);
        TextView twRaum = (TextView) this.findViewById(R.id.raumnr_details);
        TextView twLehrer = (TextView) this.findViewById(R.id.lehrerK_details);
        etNotiz = (EditText) this.findViewById(R.id.notizFeld_details);
        cbSchrift = (CheckBox) this.findViewById(R.id.checkBox_schriftlich);

        if (pos != -1) {
            //Log.e("Luzzzia", "Name ist: " + faecherSP[pos].gibName() + "Raum: " + faecherSP[pos].gibRaum() );
            twName.setText(faecherSP[pos].gibName() + " - " + faecherSP[pos].gibKurz());
            twTag.setText(this.macheTag(Integer.parseInt(faecherSP[pos].gibTag())));
            twZeit.setText(faecherSP[pos].gibStundenName());
            twRaum.setText(faecherSP[pos].gibRaum());
            twLehrer.setText(faecherSP[pos].gibLehrer());
            etNotiz.setText(faecherSP[pos].gibNotiz());
            if (faecherSP[pos].gibSchriftlich()) {
                cbSchrift.setChecked(true);
            }
        }

        etNotiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNotiz.getText().toString().equals("notiz")) {
                    etNotiz.setText("");
                }
            }
        });
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
            Log.e("Luzzzia", "Output: " + etNotiz.getText() + "Länge: " + etNotiz.getText().length());
            if (!etNotiz.getText().toString().equals("")) {
                faecherSP[pos].setzeNotiz("" + etNotiz.getText());
            } else {
                faecherSP[pos].setzeNotiz("notiz");
            }
            stuVe.inTextDatei(getApplicationContext(), faecherSP);
        }
        onBackPressed();
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

    private int sucheFachPos(String pTag, String pStunde) {
        for (int c = 0; c < faecherSP.length; c++) {
            if (faecherSP[c].gibTag().equals(pTag) && faecherSP[c].gibStunde().equals(pStunde)) {
                return c;
            }
        }
        return -1; //Wenn nicht gefunden
    }

    private void deexistiere() {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("meinefaecher.txt", MODE_PRIVATE)));
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package de.slg.stimmungsbarometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.startseite.MainActivity;

public class AbstimmActivity extends AppCompatActivity {

    private int userid;
    private int voteid = 0;
    private String ausgewählterGrund = "";

    private Button weiter;

    private ImageButton very_satisfied;
    private ImageButton satisfied;
    private ImageButton neutral;
    private ImageButton dissatisfied;
    private ImageButton bad_mood;

    private ListView listView;
    private String[] gruende = {"Wetter", "Fächer", "Lehrer", "Freunde/Bekannte", "Arbeiten/Klausuren", "besonderer Anlass", "Sonstiges"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstimmen);

        userid = Utils.getUserID();

        initToolbar();
        initListView();
        initSmileys();
        initSendButton();
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarAbstimmen);
        myToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Wie geht's dir?");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSmileys() {
        very_satisfied = (ImageButton) findViewById(R.id.imageButtonVS);
        satisfied = (ImageButton) findViewById(R.id.imageButtonS);
        neutral = (ImageButton) findViewById(R.id.imageButtonN);
        dissatisfied = (ImageButton) findViewById(R.id.imageButtonD);
        bad_mood = (ImageButton) findViewById(R.id.imageButtonB);

        very_satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiter.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                very_satisfied.setEnabled(false);
                voteid = 1;
            }
        });

        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiter.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                satisfied.setEnabled(false);
                voteid = 2;
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiter.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                neutral.setEnabled(false);
                voteid = 3;
            }
        });

        dissatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiter.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                dissatisfied.setEnabled(false);
                voteid = 4;
            }
        });

        bad_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiter.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                bad_mood.setEnabled(false);
                voteid = 5;
            }
        });
    }

    private void initSendButton() {
        weiter = (Button) findViewById(R.id.button3);
        weiter.setEnabled(false);
        weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weiter.isEnabled()) {
                    new SendeDaten().execute(new Wahl(voteid, userid, ausgewählterGrund));

                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM");
                    MainActivity.pref.edit()
                            .putString("pref_key_general_last_vote", format.format(d))
                            .putInt("pref_key_general_vote_id", voteid)
                            .apply();

                    finish();
                }
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setClickable(false);
        listView.setAdapter(new ListAdapterGrund(getApplicationContext(), gruende));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listView.isClickable()) {
                    if (ausgewählterGrund.equals(gruende[i])) {
                        view.setSelected(false);
                        ausgewählterGrund = "";
                        view.findViewById(R.id.textViewGrund).setSelected(false);
                    } else {
                        view.setSelected(true);
                        ausgewählterGrund = gruende[i];
                        view.findViewById(R.id.textViewGrund).setSelected(true);
                    }
                }
            }
        });
    }

    private void refreshButtons() {
        very_satisfied.setEnabled(true);
        satisfied.setEnabled(true);
        neutral.setEnabled(true);
        dissatisfied.setEnabled(true);
        bad_mood.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
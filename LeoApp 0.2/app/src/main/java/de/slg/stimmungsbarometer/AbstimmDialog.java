package de.slg.stimmungsbarometer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

public class AbstimmDialog extends AlertDialog {
    private int voteid = 0;
    private String ausgewählterGrund = "";

    private View confirm;

    private ImageButton very_satisfied;
    private ImageButton satisfied;
    private ImageButton neutral;
    private ImageButton dissatisfied;
    private ImageButton bad_mood;

    private ListView listView;
    private String[] gruende = {"Wetter", "Fächer", "Lehrer", "Freunde/Bekannte", "Arbeiten/Klausuren", "besonderer Anlass", "Sonstiges"};

    public AbstimmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_abstimmen);

        initListView();
        initSmileys();
        initSendButton();
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
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                very_satisfied.setEnabled(false);
                voteid = 1;
            }
        });

        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                satisfied.setEnabled(false);
                voteid = 2;
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                neutral.setEnabled(false);
                voteid = 3;
            }
        });

        dissatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                dissatisfied.setEnabled(false);
                voteid = 4;
            }
        });

        bad_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                bad_mood.setEnabled(false);
                voteid = 5;
            }
        });
    }

    private void initSendButton() {
        confirm = findViewById(R.id.buttonDialog2);
        confirm.setEnabled(false);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirm.isEnabled()) {
                    new SendeDaten().execute(new Wahl(voteid, Utils.getUserID(), ausgewählterGrund));
                    Utils.setLastVote(voteid);
                    dismiss();
                }
            }
        });

        View cancel = findViewById(R.id.buttonDialog1);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        if (Start.pref.getBoolean("pref_key_show_reasons_survey", false)) {
            listView.setClickable(false);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new ListAdapterGrund(getContext(), gruende));
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
        } else {
            findViewById(R.id.relativeLayout).setVisibility(View.GONE);
        }
    }

    private void refreshButtons() {
        very_satisfied.setEnabled(true);
        satisfied.setEnabled(true);
        neutral.setEnabled(true);
        dissatisfied.setEnabled(true);
        bad_mood.setEnabled(true);
    }

    class Wahl {

        final int voteid;
        final int userid;
        final String grund;

        Wahl(int voteid, int userid, String grund) {
            this.voteid = voteid;
            this.userid = userid;
            this.grund = grund;
        }
    }
}
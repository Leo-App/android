package de.slg.stimmungsbarometer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.stimmungsbarometer.task.SendeDaten;
import de.slg.stimmungsbarometer.utility.Wahl;

public class AbstimmActivity extends ActionLogActivity {
    private final String[] gruende           = {"Wetter", "Fächer", "Lehrer", "Freunde/Bekannte", "Arbeiten/Klausuren", "besonderer Anlass", "Sonstiges"};
    private final int      userid            = Utils.getUserID();
    private       int      voteid            = 0;
    private       String   ausgewählterGrund = "";
    private View        confirm;
    private ImageButton very_satisfied;
    private ImageButton satisfied;
    private ImageButton neutral;
    private ImageButton dissatisfied;
    private ImageButton bad_mood;
    private ListView    listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_abstimmen);

        Utils.getController().setContext(getApplicationContext());

        Utils.getNotificationManager().cancel(NotificationHandler.ID_STIMMUNGSBAROMETER);
        if (Utils.getController().getMainActivity() != null && Utils.getController().getMainActivity().abstimmDialog != null)
            Utils.getController().getMainActivity().abstimmDialog.dismiss();

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(Utils.getController().getPreferences().getString("stimmungsbarometer_frage", "Wie geht es dir?"));

        initListView();
        initSmileys();
        initSendButton();
    }

    @Override
    protected String getActivityTag() {
        return "AbstimmActivity";
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
                    new SendeDaten().execute(new Wahl(voteid, userid));
                    de.slg.stimmungsbarometer.utility.Utils.setLastVote(voteid);
                    finish();
                }
            }
        });

        findViewById(R.id.buttonDialog1).setVisibility(View.GONE);
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        if (Utils.getController().getPreferences().getBoolean("pref_key_show_reasons_survey", false)) {
            listView.setClickable(false);
            listView.setVisibility(View.VISIBLE);
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

    private class ListAdapterGrund extends ArrayAdapter<String> {
        private final Context  context;
        private final String[] gruende;

        ListAdapterGrund(Context context, String[] gruende) {
            super(context, R.layout.list_item_grund, gruende);
            this.gruende = gruende;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View v, @NonNull ViewGroup group) {
            if (v == null) {
                v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_grund, null);
            }

            final TextView grund = (TextView) v.findViewById(R.id.textViewGrund);
            grund.setText(gruende[position]);

            return v;
        }
    }
}
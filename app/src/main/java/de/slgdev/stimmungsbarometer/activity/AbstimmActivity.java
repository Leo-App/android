package de.slgdev.stimmungsbarometer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.stimmungsbarometer.task.VoteTask;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.stimmungsbarometer.utility.Vote;

public class AbstimmActivity extends ActionLogActivity {

    private final String[] gruende           = {getString(R.string.weather), getString(R.string.course), getString(R.string.lehrer), getString(R.string.friends), getString(R.string.exam), getString(R.string.particular_occasion), getString(R.string.other)};
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

        TextView textView = findViewById(R.id.titleKlausur);
        textView.setText(Utils.getController().getPreferences().getString("stimmungsbarometer_frage", getString(R.string.how_are_you)));

        initListView();
        initSmileys();
        initSendButton();
    }

    @Override
    protected String getActivityTag() {
        return "AbstimmActivity";
    }

    private void initSmileys() {
        very_satisfied = findViewById(R.id.imageButtonVS);
        satisfied = findViewById(R.id.imageButtonS);
        neutral = findViewById(R.id.imageButtonN);
        dissatisfied = findViewById(R.id.imageButtonD);
        bad_mood = findViewById(R.id.imageButtonB);

        very_satisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            listView.setClickable(true);
            refreshButtons();
            very_satisfied.setEnabled(false);
            voteid = 1;
        });
        satisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            listView.setClickable(true);
            refreshButtons();
            satisfied.setEnabled(false);
            voteid = 2;
        });
        neutral.setOnClickListener(v -> {
            confirm.setEnabled(true);
            listView.setClickable(true);
            refreshButtons();
            neutral.setEnabled(false);
            voteid = 3;
        });
        dissatisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            listView.setClickable(true);
            refreshButtons();
            dissatisfied.setEnabled(false);
            voteid = 4;
        });
        bad_mood.setOnClickListener(v -> {
            confirm.setEnabled(true);
            listView.setClickable(true);
            refreshButtons();
            bad_mood.setEnabled(false);
            voteid = 5;
        });
    }

    private void initSendButton() {
        confirm = findViewById(R.id.buttonDialog2);
        confirm.setEnabled(false);
        confirm.setOnClickListener(view -> {
            if (confirm.isEnabled()) {
                new VoteTask().execute(new Vote(voteid, userid));
                StimmungsbarometerUtils.setLastVote(voteid);
                finish();
            }
        });

        findViewById(R.id.buttonDialog1).setVisibility(View.GONE);
    }

    private void initListView() {
        listView = findViewById(R.id.listView);
        if (Utils.getController().getPreferences().getBoolean("pref_key_show_reasons_survey", false)) {
            listView.setClickable(false);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new ListAdapterGrund(getApplicationContext(), gruende));
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
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

            final TextView grund = v.findViewById(R.id.textViewGrund);
            grund.setText(gruende[position]);

            return v;
        }
    }
}
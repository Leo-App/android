package de.slgdev.stimmungsbarometer.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.stimmungsbarometer.task.VoteTask;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.stimmungsbarometer.utility.Vote;

public class AbstimmDialog extends AlertDialog {
    private int voteid = 0;
    private View        confirm;
    private ImageButton very_satisfied;
    private ImageButton satisfied;
    private ImageButton neutral;
    private ImageButton dissatisfied;
    private ImageButton bad_mood;

    public AbstimmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_abstimmen);
        initSmileys();
        initSendButton();

        TextView textView = findViewById(R.id.titleKlausur);
        textView.setText(StimmungsbarometerUtils.getCurrentQuestion());
    }

    private void initSmileys() {
        very_satisfied = findViewById(R.id.imageButtonVS);
        satisfied = findViewById(R.id.imageButtonS);
        neutral = findViewById(R.id.imageButtonN);
        dissatisfied = findViewById(R.id.imageButtonD);
        bad_mood = findViewById(R.id.imageButtonB);

        very_satisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            refreshButtons();
            very_satisfied.setEnabled(false);
            voteid = 1;
        });
        satisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            refreshButtons();
            satisfied.setEnabled(false);
            voteid = 2;
        });
        neutral.setOnClickListener(v -> {
            confirm.setEnabled(true);
            refreshButtons();
            neutral.setEnabled(false);
            voteid = 3;
        });
        dissatisfied.setOnClickListener(v -> {
            confirm.setEnabled(true);
            refreshButtons();
            dissatisfied.setEnabled(false);
            voteid = 4;
        });
        bad_mood.setOnClickListener(v -> {
            confirm.setEnabled(true);
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
                new VoteTask().execute(new Vote(voteid, Utils.getUserID()));
                StimmungsbarometerUtils.setLastVote(voteid);
                dismiss();
            }
        });

        findViewById(R.id.buttonDialog1).setOnClickListener(v -> dismiss());
    }

    private void refreshButtons() {
        very_satisfied.setEnabled(true);
        satisfied.setEnabled(true);
        neutral.setEnabled(true);
        dissatisfied.setEnabled(true);
        bad_mood.setEnabled(true);
    }
}
package de.slgdev.stimmungsbarometer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.dialog.EditTextDialog;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStimmungsbarometer;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.stimmungsbarometer.task.SendQuestionTask;
import de.slgdev.stimmungsbarometer.task.SyncVoteResultsTask;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.stimmungsbarometer.view.ColumnView;
import de.slgdev.stimmungsbarometer.view.GraphView;

public class StimmungsbarometerActivity extends LeoAppNavigationActivity implements TaskStatusListener {
    public static boolean drawI;
    public static boolean drawS;
    public static boolean drawL;
    public static boolean drawA;

    private SQLiteConnectorStimmungsbarometer database;

    private GraphView  viewWoche;
    private GraphView  viewMonat;
    private GraphView  viewJahr;
    private ColumnView viewGesamt;

    private EditTextDialog dialog;

    private TextView frage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerStimmungsbarometerActivity(this);

        drawI = true;
        drawS = true;
        drawL = true;
        drawA = true;

        database = new SQLiteConnectorStimmungsbarometer(getApplicationContext());
        if(!database.isEmpty()) {
            ProgressBar pb = findViewById(R.id.progressBarL1);
            pb.setVisibility(View.GONE);
        }

        initStatisticViews();
        initCheckboxes();
        initEditButton();

        frage = findViewById(R.id.textViewFrage2);
        frage.setText(de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils.getCurrentQuestion());

        new SyncVoteResultsTask(getApplicationContext()).addListener(this).execute();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_stimmungsbarometer;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_survey;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.barometer;
    }

    @Override
    protected String getActivityTag() {
        return "StimmungsbarometerActivity";
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerStimmungsbarometerActivity(null);
    }

    @Override
    public void taskFinished(Object... params) {
        if (params[0] == ResponseCode.SERVER_FAILED) {
            Toast.makeText(getApplicationContext(), R.string.error_sync, Toast.LENGTH_SHORT).show();
        } else {
            ProgressBar pb = findViewById(R.id.progressBarL1);
            pb.setVisibility(View.GONE);
            updateViews();
        }
    }

    private void initCheckboxes() {
        final CheckBox lI = findViewById(R.id.layoutIch);
        final CheckBox lS = findViewById(R.id.layoutSchueler);
        final CheckBox lL = findViewById(R.id.layoutLehrer);
        final CheckBox lA = findViewById(R.id.layoutAlle);

        lI.setChecked(drawI);
        lS.setChecked(drawS);
        lL.setChecked(drawL);
        lA.setChecked(drawA);

        lI.setOnClickListener(v -> {
            drawI = !drawI;
            lI.setBackgroundColor(drawI ? getResources().getColor(R.color.colorIch) : getResources().getColor(android.R.color.darker_gray));
            refreshViews();
        });
        lS.setOnClickListener(v -> {
            drawS = !drawS;
            lS.setBackgroundColor(drawS ? getResources().getColor(R.color.colorSchueler) : getResources().getColor(android.R.color.darker_gray));
            refreshViews();
        });
        lL.setOnClickListener(v -> {
            drawL = !drawL;
            lL.setBackgroundColor(drawL ? getResources().getColor(R.color.colorLehrer) : getResources().getColor(android.R.color.darker_gray));
            refreshViews();
        });
        lA.setOnClickListener(v -> {
            drawA = !drawA;
            lA.setBackgroundColor(drawA ? getResources().getColor(R.color.colorAlle) : getResources().getColor(android.R.color.darker_gray));
            refreshViews();
        });
    }

    private void initStatisticViews() {
        viewWoche = findViewById(R.id.viewWoche);
        viewWoche.setData(database.getData(0));

        viewMonat = findViewById(R.id.viewMonat);
        viewMonat.setData(database.getData(1));

        viewJahr = findViewById(R.id.viewJahr);
        viewJahr.setData(database.getData(2));

        viewGesamt = findViewById(R.id.viewGesamt);
        viewGesamt.setData(database.getAverage());
    }

    private void initEditButton() {
        if (Utils.getUserPermission() >= User.PERMISSION_LEHRER) {
            View edit = findViewById(R.id.changeQuestion);
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(v -> {
                dialog = new EditTextDialog(
                        StimmungsbarometerActivity.this,
                        getString(R.string.change_question),
                        getString(R.string.new_question),
                        v1 -> {
                            if (!dialog.getTextInput().equals(StimmungsbarometerUtils.getCurrentQuestion()))
                                new SendQuestionTask().execute();
                            dialog.dismiss();
                        }
                );
                dialog.show();
                dialog.setTextInput(StimmungsbarometerUtils.getCurrentQuestion());
            });
        }
    }

    private void updateViews() {
        viewWoche.setData(database.getData(0));
        viewMonat.setData(database.getData(1));
        viewJahr.setData(database.getData(2));
        viewGesamt.setData(database.getAverage());
        refreshViews();
    }

    private void refreshViews() {
        viewWoche.invalidate();
        viewMonat.invalidate();
        viewJahr.invalidate();
        viewGesamt.invalidate();
    }
}
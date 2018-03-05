package de.slgdev.stimmungsbarometer.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.dialog.EditTextDialog;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStimmungsbarometer;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.GraphicUtils;
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
    private ColumnView viewAlles;

    private EditTextDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerStimmungsbarometerActivity(this);

        drawI = Utils.isVerified();
        drawS = true;
        drawL = true;
        drawA = true;

        database = new SQLiteConnectorStimmungsbarometer(getApplicationContext());

        initScrollView();
        initLayouts();
        initEditButton();

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
    public void taskStarts() {

    }

    @Override
    public void taskFinished(Object... params) {
        if (params[0] == ResponseCode.SERVER_FAILED) {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            updateViews();
        }
    }

    private void initLayouts() {
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

    private void initScrollView() {
        viewWoche = new GraphView(getApplicationContext());
        viewWoche.setData(database.getData(0));
        viewMonat = new GraphView(getApplicationContext());
        viewMonat.setData(database.getData(1));
        viewJahr = new GraphView(getApplicationContext());
        viewJahr.setData(database.getData(2));
        viewAlles = new ColumnView(getApplicationContext());
        viewAlles.setData(database.getAverage());

        ViewGroup container = findViewById(R.id.linearLayout);

        CardView cardWoche = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, container, false);
        cardWoche.setCardElevation(GraphicUtils.dpToPx(4));
        TextView titleWoche = cardWoche.findViewById(R.id.title);
        titleWoche.setText(R.string.last_week);
        ViewGroup layoutWoche = cardWoche.findViewById(R.id.layout);
        layoutWoche.addView(viewWoche);
        container.addView(cardWoche);

        CardView cardMonat = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, container, false);
        cardMonat.setCardElevation(GraphicUtils.dpToPx(4));
        TextView titleMonat = cardMonat.findViewById(R.id.title);
        titleMonat.setText(R.string.last_month);
        ViewGroup layoutMonat = cardMonat.findViewById(R.id.layout);
        layoutMonat.addView(viewMonat);
        container.addView(cardMonat);

        CardView cardJahr = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, container, false);
        cardJahr.setCardElevation(GraphicUtils.dpToPx(4));
        TextView titleJahr = cardJahr.findViewById(R.id.title);
        titleJahr.setText(R.string.last_year);
        ViewGroup layoutJahr = cardJahr.findViewById(R.id.layout);
        layoutJahr.addView(viewJahr);
        container.addView(cardJahr);

        CardView cardAlles = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, container, false);
        cardAlles.setCardElevation(GraphicUtils.dpToPx(4));
        TextView titleAlles = cardAlles.findViewById(R.id.title);
        titleAlles.setText(R.string.total);
        ViewGroup layoutAlles = cardAlles.findViewById(R.id.layout);
        layoutAlles.addView(viewAlles);
        container.addView(cardAlles);
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
        viewAlles.setData(database.getAverage());
        refreshViews();
    }

    private void refreshViews() {
        viewWoche.invalidate();
        viewMonat.invalidate();
        viewJahr.invalidate();
        viewAlles.invalidate();
    }
}
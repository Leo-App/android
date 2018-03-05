package de.slgdev.stimmungsbarometer.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
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
        final View lI = findViewById(R.id.layoutIch);
        final View lS = findViewById(R.id.layoutSchueler);
        final View lL = findViewById(R.id.layoutLehrer);
        final View lA = findViewById(R.id.layoutAlle);
        lI.findViewById(R.id.textViewIch).setEnabled(drawI);
        lI.findViewById(R.id.imageViewIch).setEnabled(drawI);
        lI.setOnClickListener(v -> {
            if (Utils.isVerified()) {
                drawI = !drawI;
                v.findViewById(R.id.textViewIch).setEnabled(drawI);
                v.findViewById(R.id.imageViewIch).setEnabled(drawI);
                refreshViews();
            }
        });
        lS.setOnClickListener(v -> {
            drawS = !drawS;
            v.findViewById(R.id.textViewSchueler).setEnabled(drawS);
            v.findViewById(R.id.imageViewSchueler).setEnabled(drawS);
            refreshViews();
        });
        lL.setOnClickListener(v -> {
            drawL = !drawL;
            v.findViewById(R.id.textViewLehrer).setEnabled(drawL);
            v.findViewById(R.id.imageViewLehrer).setEnabled(drawL);
            refreshViews();
        });
        lA.setOnClickListener(v -> {
            drawA = !drawA;
            v.findViewById(R.id.textViewAlle).setEnabled(drawA);
            v.findViewById(R.id.imageViewAlle).setEnabled(drawA);
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
                            new SendQuestionTask().execute(dialog.getTextInput());
                            dialog.dismiss();
                        }
                );
                dialog.show();
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
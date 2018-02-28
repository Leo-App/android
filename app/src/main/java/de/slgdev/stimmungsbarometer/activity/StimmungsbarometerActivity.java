package de.slgdev.stimmungsbarometer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import de.slgdev.stimmungsbarometer.view.StatistikView;
import de.slgdev.stimmungsbarometer.view.StatistikViewBalken;

public class StimmungsbarometerActivity extends LeoAppNavigationActivity implements TaskStatusListener {
    public static boolean drawI;
    public static boolean drawS;
    public static boolean drawL;
    public static boolean drawA;

    private SQLiteConnectorStimmungsbarometer database;

    private StatistikView       viewWoche;
    private StatistikView       viewMonat;
    private StatistikView       viewJahr;
    private StatistikViewBalken viewAlles;

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
        viewWoche = new StatistikView(getApplicationContext());
        viewWoche.setData(database.getData(0));
        viewMonat = new StatistikView(getApplicationContext());
        viewMonat.setData(database.getData(1));
        viewJahr = new StatistikView(getApplicationContext());
        viewJahr.setData(database.getData(2));
        viewAlles = new StatistikViewBalken(getApplicationContext());
        viewAlles.setData(database.getAverage());

        final CardView cardWoche = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardWoche.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardMonat = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardMonat.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardJahr = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardJahr.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardAlles = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardAlles.setCardElevation(GraphicUtils.dpToPx(4));

        final TextView titleWoche = cardWoche.findViewById(R.id.title);
        titleWoche.setText(R.string.last_week);
        final TextView titleMonat = cardMonat.findViewById(R.id.title);
        titleMonat.setText(R.string.last_month);
        final TextView titleJahr = cardJahr.findViewById(R.id.title);
        titleJahr.setText(R.string.last_year);
        final TextView titleAlles = cardAlles.findViewById(R.id.title);
        titleAlles.setText(R.string.total);

        final ViewGroup layoutWoche = cardWoche.findViewById(R.id.layout);
        layoutWoche.addView(viewWoche);
        final ViewGroup layoutMonat = cardMonat.findViewById(R.id.layout);
        layoutMonat.addView(viewMonat);
        final ViewGroup layoutJahr = cardJahr.findViewById(R.id.layout);
        layoutJahr.addView(viewJahr);
        final ViewGroup layoutAlles = cardAlles.findViewById(R.id.layout);
        layoutAlles.addView(viewAlles);

        new Handler().post(() -> {
            View scrollView = findViewById(R.id.scrollView);
            int  height, width;
            while ((height = scrollView.getHeight()) == 0 || (width = scrollView.getWidth()) == 0)
                ;

            viewWoche.setMinimumHeight(height * 4 / 5);
            viewMonat.setMinimumHeight(height * 4 / 5);
            viewJahr.setMinimumHeight(height * 4 / 5);
            viewAlles.setMinimumHeight(height * 4 / 5);

            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, height / 64, 0, height / 64);

            final LinearLayout container = findViewById(R.id.linearLayout);
            container.addView(cardWoche, layoutParams);
            container.addView(cardMonat, layoutParams);
            container.addView(cardJahr, layoutParams);
            container.addView(cardAlles, layoutParams);

            final LinearLayout.LayoutParams layoutParamsTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsTitle.setMargins(width / 128, height / 128, width / 128, height / 128);
            titleWoche.setLayoutParams(layoutParamsTitle);
            titleMonat.setLayoutParams(layoutParamsTitle);
            titleJahr.setLayoutParams(layoutParamsTitle);
            titleAlles.setLayoutParams(layoutParamsTitle);
        });
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
        updateViewData();
        refreshViews();
    }

    private void updateViewData() {
        viewWoche.setData(database.getData(0));
        viewMonat.setData(database.getData(1));
        viewJahr.setData(database.getData(2));
        viewAlles.setData(database.getAverage());
    }

    private void refreshViews() {
        viewWoche.invalidate();
        viewMonat.invalidate();
        viewJahr.invalidate();
        viewAlles.invalidate();
    }
}
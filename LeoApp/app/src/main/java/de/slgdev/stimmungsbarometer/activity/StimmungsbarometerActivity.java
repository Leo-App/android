package de.slgdev.stimmungsbarometer.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.GregorianCalendar;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.dialog.EditTextDialog;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStimmungsbarometer;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppFeatureActivity;
import de.slgdev.stimmungsbarometer.utility.Ergebnis;
import de.slgdev.stimmungsbarometer.view.StatistikView;
import de.slgdev.stimmungsbarometer.view.StatistikViewBalken;

public class StimmungsbarometerActivity extends LeoAppFeatureActivity {
    public static boolean drawI;
    public static boolean drawS;
    public static boolean drawL;
    public static boolean drawA;

    private SQLiteConnectorStimmungsbarometer sqLiteConnector;

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

        sqLiteConnector = new SQLiteConnectorStimmungsbarometer(getApplicationContext());

        initScrollView();
        initLayouts();
        initEditButton();

        new StartTask().execute();
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

    private void initLayouts() {
        final View lI = findViewById(R.id.layoutIch);
        final View lS = findViewById(R.id.layoutSchueler);
        final View lL = findViewById(R.id.layoutLehrer);
        final View lA = findViewById(R.id.layoutAlle);
        lI.findViewById(R.id.textViewIch).setEnabled(drawI);
        lI.findViewById(R.id.imageViewIch).setEnabled(drawI);
        lI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isVerified()) {
                    drawI = !drawI;
                    v.findViewById(R.id.textViewIch).setEnabled(drawI);
                    v.findViewById(R.id.imageViewIch).setEnabled(drawI);
                    updateViews();
                }
            }
        });
        lS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawS = !drawS;
                v.findViewById(R.id.textViewSchueler).setEnabled(drawS);
                v.findViewById(R.id.imageViewSchueler).setEnabled(drawS);
                updateViews();
            }
        });
        lL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawL = !drawL;
                v.findViewById(R.id.textViewLehrer).setEnabled(drawL);
                v.findViewById(R.id.imageViewLehrer).setEnabled(drawL);
                updateViews();
            }
        });
        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawA = !drawA;
                v.findViewById(R.id.textViewAlle).setEnabled(drawA);
                v.findViewById(R.id.imageViewAlle).setEnabled(drawA);
                updateViews();
            }
        });
    }

    private void initScrollView() {
        viewWoche = new StatistikView(getApplicationContext());
        viewWoche.setData(sqLiteConnector.getData(0));
        viewMonat = new StatistikView(getApplicationContext());
        viewMonat.setData(sqLiteConnector.getData(1));
        viewJahr = new StatistikView(getApplicationContext());
        viewJahr.setData(sqLiteConnector.getData(2));
        viewAlles = new StatistikViewBalken(getApplicationContext());
        viewAlles.setData(sqLiteConnector.getAverage());

        final CardView cardWoche = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardWoche.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardMonat = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardMonat.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardJahr = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardJahr.setCardElevation(GraphicUtils.dpToPx(4));
        final CardView cardAlles = (CardView) getLayoutInflater().inflate(R.layout.card_view_vertical, null);
        cardAlles.setCardElevation(GraphicUtils.dpToPx(4));

        final TextView titleWoche = cardWoche.findViewById(R.id.titleKlausur);
        titleWoche.setText(R.string.last_week);
        final TextView titleMonat = cardMonat.findViewById(R.id.titleKlausur);
        titleMonat.setText(R.string.last_month);
        final TextView titleJahr = cardJahr.findViewById(R.id.titleKlausur);
        titleJahr.setText(R.string.last_year);
        final TextView titleAlles = cardAlles.findViewById(R.id.titleKlausur);
        titleAlles.setText(R.string.total);

        final ViewGroup layoutWoche = cardWoche.findViewById(R.id.layout);
        layoutWoche.addView(viewWoche);
        final ViewGroup layoutMonat = cardMonat.findViewById(R.id.layout);
        layoutMonat.addView(viewMonat);
        final ViewGroup layoutJahr = cardJahr.findViewById(R.id.layout);
        layoutJahr.addView(viewJahr);
        final ViewGroup layoutAlles = cardAlles.findViewById(R.id.layout);
        layoutAlles.addView(viewAlles);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    private void initEditButton() {
        if (Utils.getUserPermission() >= User.PERMISSION_LEHRER) {
            View edit = findViewById(R.id.changeQuestion);
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new EditTextDialog(
                            StimmungsbarometerActivity.this,
                            getString(R.string.change_question),
                            getString(R.string.new_question),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SendQuestionTask().execute(dialog.getTextInput());
                                    dialog.dismiss();
                                }
                            }
                    );
                    dialog.show();
                }
            });
        }
    }

    private void updateViews() {
        viewWoche.setData(sqLiteConnector.getData(0));
        viewWoche.invalidate();

        viewMonat.setData(sqLiteConnector.getData(1));
        viewMonat.invalidate();

        viewJahr.setData(sqLiteConnector.getData(2));
        viewJahr.invalidate();

        viewAlles.setData(sqLiteConnector.getAverage());
        viewAlles.invalidate();
    }

    private class StartTask extends AsyncTask<Void, Void, ResponseCode> {
        @Override
        protected ResponseCode doInBackground(Void... params) {
            try {
                URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/ergebnisse.php?uid=" + Utils.getUserID())
                        .openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

                String        line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                String result = builder.toString();
                if (result.startsWith("-")) {
                    throw new IOException(result);
                }

                String[] e = builder.toString().split("_abschnitt_");

                String[] splitI = e[0].split("_next_");
                String[] splitS = e[1].split("_next_");
                String[] splitL = e[2].split("_next_");
                String[] splitA = e[3].split("_next_");

                for (String aSplitI : splitI) {
                    String[] current = aSplitI.split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        sqLiteConnector.insert(
                                new Ergebnis(
                                        new GregorianCalendar(
                                                Integer.parseInt(date[2]),
                                                Integer.parseInt(date[1]) - 1,
                                                Integer.parseInt(date[0])
                                        ).getTime(),
                                        Double.parseDouble(current[0]),
                                        true,
                                        false,
                                        false,
                                        false
                                )
                        );
                    }
                }

                for (String split : splitS) {
                    String[] current = split.split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        sqLiteConnector.insert(
                                new Ergebnis(
                                        new GregorianCalendar(
                                                Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])
                                        ).getTime(),
                                        Double.parseDouble(current[0]),
                                        false,
                                        true,
                                        false,
                                        false
                                )
                        );
                    }
                }

                for (String aSplitL : splitL) {
                    String[] current = aSplitL.split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        sqLiteConnector.insert(
                                new Ergebnis(
                                        new GregorianCalendar(
                                                Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])
                                        ).getTime(),
                                        Double.parseDouble(current[0]),
                                        false,
                                        false,
                                        true,
                                        false
                                )
                        );
                    }
                }

                for (String aSplitA : splitA) {
                    String[] current = aSplitA.split(";");
                    if (current.length == 2) {
                        String[] date = current[1].replace('.', '_').split("_");
                        sqLiteConnector.insert(
                                new Ergebnis(
                                        new GregorianCalendar(
                                                Integer.parseInt(date[2]),
                                                Integer.parseInt(date[1]) - 1,
                                                Integer.parseInt(date[0])
                                        ).getTime(),
                                        Double.parseDouble(current[0]),
                                        false,
                                        false,
                                        false,
                                        true
                                )
                        );
                    }
                }
            } catch (IOException e) {
                Utils.logError(e);
                e.printStackTrace();
                return ResponseCode.SERVER_FAILED;
            }

            return ResponseCode.SUCCESS;
        }

        @Override
        protected void onPostExecute(ResponseCode v) {
            if (v == ResponseCode.SERVER_FAILED) {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
            updateViews();
        }
    }

    private class SendQuestionTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URLConnection connection = new URL(Utils.BASE_URL_PHP + "stimmungsbarometer/newQuestion.php?qtext=" + URLEncoder.encode(params[0], "UTF-8"))
                        .openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8"));

                String        line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();

                Utils.logDebug(builder);
            } catch (IOException e) {
                Utils.logError(e);
            }
            return null;
        }
    }
}
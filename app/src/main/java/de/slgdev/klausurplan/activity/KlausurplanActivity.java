package de.slgdev.klausurplan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slgdev.klausurplan.KlausurenAdapter;
import de.slgdev.klausurplan.dialog.KlausurDialog;
import de.slgdev.klausurplan.utility.Klausur;
import de.slgdev.klausurplan.utility.KlausurplanUtils;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;

public class KlausurplanActivity extends LeoAppNavigationActivity implements TaskStatusListener {
    private ListView                   listView;
    private Klausur[]                  klausuren;
    private Snackbar                   snackbar;
    private KlausurDialog              dialog;
    private boolean                    confirmDelete;
    private SQLiteConnectorKlausurplan database;
    private SQLiteConnectorStundenplan databaseStundenplan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerKlausurplanActivity(this);

        databaseStundenplan = new SQLiteConnectorStundenplan(getApplicationContext());
        if (!KlausurplanUtils.databaseExists(getApplicationContext())) {
            database = new SQLiteConnectorKlausurplan(getApplicationContext());
            new Importer(getApplicationContext()).addListener(this).execute();
        } else {
            database = new SQLiteConnectorKlausurplan(getApplicationContext());
        }

        initListView();
        initAddButton();
        initSnackbar();

        refresh();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_klausurplan;
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
        return R.string.title_testplan;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.klausurplan;
    }

    @Override
    protected String getActivityTag() {
        return "KlausurplanActivity";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        snackbar.dismiss();
        super.onOptionsItemSelected(mi);
        if (mi.getItemId() == R.id.action_load) {
            if (snackbar.isShown())
                snackbar.dismiss();
            new Importer(getApplicationContext())
                    .addListener(this)
                    .execute();
        }
        if (mi.getItemId() == R.id.action_delete) {
            confirmDelete = true;
            snackbar.show();
            listView.setAdapter(
                    new KlausurenAdapter(
                            getApplicationContext(),
                            database.getExams(
                                    SQLiteConnectorKlausurplan.WHERE_ONLY_CREATED
                            ),
                            KlausurplanUtils.findeNächsteKlausur(
                                    klausuren
                            )
                    )
            );
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.klausurplan, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationHandler.ID_KLAUSURPLAN);
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerKlausurplanActivity(null);
        if (dialog != null)
            dialog.dismiss();
        if (database != null)
            database.close();
        if (databaseStundenplan != null)
            databaseStundenplan.close();
    }

    @Override
    public void taskStarts() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    public void taskFinished(Object... params) {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        refresh();
    }

    private void initListView() {
        listView = findViewById(R.id.listViewKlausuren);
        listView.setVerticalScrollBarEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                dialog = new KlausurDialog(KlausurplanActivity.this, klausuren[position]);
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        KlausurplanActivity.this.dialog = null;
                        refresh();
                    }
                });
            }
        });
    }

    private void initSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.snackbar_gelöscht), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        snackbar.setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete = false;
                snackbar.dismiss();
            }
        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (confirmDelete) {
                    database.deleteAllDownloaded();
                    refreshArray();
                } else {
                    refresh();
                }
            }
        });
    }

    private void initAddButton() {
        FloatingActionButton fabAdd = findViewById(R.id.floatingActionButton);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();

                dialog = new KlausurDialog(KlausurplanActivity.this, new Klausur(0, "", new Date(), ""));
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        KlausurplanActivity.this.dialog = null;
                        refresh();
                    }
                });
            }
        });
    }

    private void refresh() {
        refreshArray();
        listView.setAdapter(new KlausurenAdapter(getApplicationContext(), klausuren, KlausurplanUtils.findeNächsteKlausur(klausuren)));

        listView.setSelection(KlausurplanUtils.findeNächsteWoche(klausuren));
    }

    private void refreshArray() {
        if (Utils.getController().getPreferences().getBoolean("pref_key_test_timetable_sync", true) && databaseStundenplan.hatGewaehlt()) {
            if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
                klausuren = database.getExams(SQLiteConnectorKlausurplan.WHERE_ONLY_TIMETABLE + SQLiteConnectorKlausurplan.getMinDate() + ')');
            else
                klausuren = database.getExams(SQLiteConnectorKlausurplan.WHERE_GRADE_TIMETABLE + SQLiteConnectorKlausurplan.getMinDate() + ')');
        } else {
            if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
                klausuren = database.getExams(SQLiteConnectorKlausurplan.WHERE_ALL + SQLiteConnectorKlausurplan.getMinDate());
            else
                klausuren = database.getExams(SQLiteConnectorKlausurplan.WHERE_ONLY_GRADE + SQLiteConnectorKlausurplan.getMinDate() + ')');
        }
    }

    private static class Importer extends VoidCallbackTask<Void> {
        private final SQLiteConnectorKlausurplan database;

        private BufferedReader reader;
        private InputStream    inputStream;

        private final String[] schriflich;

        private int year, halbjahr;

        private Importer(Context context) {
            this.database = new SQLiteConnectorKlausurplan(context);

            SQLiteConnectorStundenplan databaseStundenplan = new SQLiteConnectorStundenplan(context);
            this.schriflich = databaseStundenplan.gibSchriftlicheFaecherStrings();
            databaseStundenplan.close();

            try {
                inputStream = context
                        .openFileInput(
                                "klausurplan.xml"
                        );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                inputStream = null;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                database.deleteAllDownloaded();

                reader = new BufferedReader(
                        new InputStreamReader(
                                inputStream
                        )
                );

                year = getYear();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.replace(" ", "").equals("<informaltableframe=\"all\">"))
                        tabelle(reader.readLine());
                }

                reader.close();
            } catch (IOException e) {
                Utils.logError(e);
            }
            return null;
        }

        private void tabelle(String s) {
            for (int offset = 0; s.substring(offset).contains("<row>"); offset = s.indexOf("</row>", offset) + 6) {
                String substring = s.substring(
                        s.indexOf("<row>", offset) + 5,
                        s.indexOf("</row>", offset)
                );

                substring = substring.substring(
                        substring.indexOf("</entry>") + 8
                );

                substring = substring
                        .replace(
                                "</para>",
                                ""
                        )
                        .replace(
                                "<para/>",
                                " "
                        )
                        .replace(
                                "<entry><para>",
                                ""
                        )
                        .replace(
                                "</entry>",
                                ";"
                        ).replace(
                                "<para>",
                                ", "
                        )
                        .replace(
                                "<entry>",
                                ""
                        );

                if (!substring.contains("<entry namest=\"c3\" nameend=\"c5\">") && !substring.startsWith("EF")) {
                    zeile(substring);
                }
            }
        }

        private void zeile(String s) {
            String datesubstring = s
                    .substring(
                            0,
                            s.indexOf(";")
                    )
                    .replaceAll(
                            "\\s",
                            ""
                    );

            if (!datesubstring.endsWith(".")) {
                datesubstring += '.';
            }

            Date datum = getDate(datesubstring + year);

            String rest = s.substring(
                    s.indexOf(";") + 1
            );

            String[] split = rest.split(";");
            for (int i = 0; i < split.length; i++) {
                String stufe = "", c = split[i];
                switch (i) {
                    case 0:
                        stufe = "EF";
                        break;
                    case 1:
                        stufe = "Q1";
                        break;
                    case 2:
                        stufe = "Q2";
                        break;
                }

                if (c.startsWith("LK") || c.startsWith("GK")) {
                    c = c.substring(c.indexOf(':') + 1);
                } else if (c.startsWith("Abiturvorklausur LK")) {
                    if (c.contains("wissenschaften)")) {
                        c = c.substring(
                                c.indexOf("wissenschaften)") + 15
                        );
                        c = c.substring(
                                c.indexOf(":") + 1
                        );
                        c = c.substring(
                                0,
                                c.indexOf(":") - 2
                        );
                    }
                } else if (c.startsWith("Abiturklausur GK")) {
                    c = c.substring(
                            c.indexOf("wissenschaften)") + 15
                    );
                }

                for (String k : getKlausurStrings(c, stufe)) {
                    k = k.replace('_', ' ');
                    database.insert(k, stufe, datum, "", istImStundenplan(k), true);
                }
            }
        }

        private List<String> getKlausurStrings(String s, String stufe) {
            s = s
                    .replace(
                            ' ',
                            '_'
                    )
                    .replace(
                            '(',
                            '_'
                    )
                    .replace(
                            ')',
                            '_'
                    )
                    .replace(
                            ',',
                            ';'
                    )
                    .replaceAll(
                            "\\s",
                            ""
                    );

            if (s.contains("COU") || s.contains("KKG"))
                Utils.logDebug(s);

            List<String> list = new List<>();
            for (String c : s.split(";")) {

                while (c.length() > 0 && (c.charAt(0) == '_' || (c.charAt(0) > 47 && c.charAt(0) < 58)))
                    c = c.substring(1);

                if (c.length() > 0) {
                    boolean istGK     = c.matches("[A-Z]{1,3}_*[GLK]{1,2}_*[0-9]_*[A-ZÄÖÜ]{3}_*(\\([0-9]{1,2}\\))?.*");
                    boolean istLK     = c.matches("[A-Z]{1,3}_*[A-ZÄÖÜ]{3}_*[0-9]{1,2}.*");
                    boolean istKOOPLK = c.matches("LK_[0-9]*+_[COUKG]{3}:_[A-Z]{1,3}.*");

                    if (c.length() >= 12 && istGK) {
                        String klausur = c.substring(0, 12);
                        while (klausur.length() > 7 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                            klausur = klausur.substring(0, klausur.length() - 1);
                        klausur += " " + stufe;
                        list.append(klausur);
                    }

                    if (c.length() >= 9 && istLK) {
                        String klausur = c.substring(0, 9);
                        while (klausur.length() > 5 && (klausur.charAt(klausur.length() - 1) == '_' || (klausur.charAt(klausur.length() - 1) > 47 && klausur.charAt(klausur.length() - 1) < 58)))
                            klausur = klausur.substring(0, klausur.length() - 1);
                        klausur += " " + stufe;
                        String teil1 = klausur.substring(0, klausur.indexOf("_"));
                        String teil2 = klausur.substring(klausur.indexOf("_"), klausur.length());
                        klausur = teil1 + " L" + teil2;
                        list.append(klausur);
                    }

                    if (istKOOPLK) {
                        c = c.substring(5);
                        String schule = c.substring(0, 3);
                        c = c.substring(c.indexOf(':') + 2);
                        if (c.contains("_"))
                            c = c.substring(0, c.indexOf("_"));
                        c = c + ' ' + schule;
                        list.append(c);
                    }
                }
            }

            return list;
        }

        private int getYear() throws IOException {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.replace(" ", "").startsWith("<para>")) {
                    String substring = line.substring(
                            line.indexOf("Klausurplan") + 11,
                            line.indexOf(".Halbjahr")
                    );

                    if (substring.charAt(substring.length() - 1) == '1') {
                        halbjahr = 1;
                    } else {
                        halbjahr = 2;
                    }

                    for (int i = 0; i < substring.length(); i++) {
                        if (substring.charAt(i) != ' ') {
                            return Integer.parseInt(
                                    substring.substring(
                                            i,
                                            i + 4
                                    )
                            ) + halbjahr - 1;
                        }
                    }
                }
            }

            return new GregorianCalendar().get(Calendar.YEAR);
        }

        private Date getDate(String s) {
            try {
                Date d = new SimpleDateFormat("dd.MM.yyyy").parse(s);
                if (halbjahr == 1) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(d);
                    if (c.get(Calendar.MONTH) < 4) {
                        c.add(Calendar.YEAR, 1);
                        d = c.getTime();
                    }
                }
                return d;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String[] parts = s.replace('.', '_').split("_");
            if (parts.length == 3) {
                int day   = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year  = Integer.parseInt(parts[2]);
                if (halbjahr == 1 && month < 4)
                    year++;
                Calendar c = new GregorianCalendar();
                c.set(year, month - 1, day, 0, 0, 0);
                return c.getTime();
            }
            return null;
        }

        private boolean istImStundenplan(String klausur) {
            for (String s : schriflich) {
                if (klausur.startsWith(s))
                    return true;
            }
            return false;
        }
    }
}
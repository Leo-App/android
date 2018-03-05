package de.slgdev.klausurplan.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.Date;

import de.slgdev.klausurplan.KlausurenAdapter;
import de.slgdev.klausurplan.dialog.KlausurDialog;
import de.slgdev.klausurplan.task.Importer;
import de.slgdev.klausurplan.utility.Klausur;
import de.slgdev.klausurplan.utility.KlausurplanUtils;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
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

        initTextViewInfo();
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
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = new KlausurDialog(KlausurplanActivity.this, klausuren[position]);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.setOnDismissListener(dialog -> {
                KlausurplanActivity.this.dialog = null;
                refresh();
            });
        });
    }

    private void initSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.snackbar_gelöscht), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        snackbar.setAction(getString(R.string.snackbar_undo), v -> {
            confirmDelete = false;
            snackbar.dismiss();
        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                if (!Utils.getController().getActiveActivity().equals(Utils.getController().getKlausurplanActivity()))
                    return;

                if (confirmDelete) {
                    database.deleteAllDownloaded();
                    refreshArray();
                    initTextViewInfo();
                } else {
                    refresh();
                }
            }
        });
    }

    private void initAddButton() {
        FloatingActionButton fabAdd = findViewById(R.id.floatingActionButton);
        fabAdd.setOnClickListener(v -> {
            snackbar.dismiss();

            dialog = new KlausurDialog(KlausurplanActivity.this, new Klausur(0, "", new Date(), ""));
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.setOnDismissListener(dialog -> {
                KlausurplanActivity.this.dialog = null;
                refresh();
            });
        });
    }

    private void initTextViewInfo() {
        if (listView.getAdapter().getCount() == 0)
            findViewById(R.id.emptyexams).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.emptyexams).setVisibility(View.GONE);
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
}
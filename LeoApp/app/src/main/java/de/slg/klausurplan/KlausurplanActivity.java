package de.slg.klausurplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import de.slg.essensqr.EssensQRActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.ProfileActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.service.NotificationService;
import de.slg.leoapp.utility.List;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.umfragen.SurveyActivity;

public class KlausurplanActivity extends LeoAppFeatureActivity {
    private ListView      lvKlausuren;
    private List<Klausur> klausurList;
    private Snackbar      snackbar;
    private KlausurDialog dialog;
    private boolean       confirmDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerKlausurplanActivity(this);

        initList();
        initListView();
        initAddButton();
        initSnackbar();

        loescheAlteKlausuren(Utils.getController().getPreferences().getInt("pref_key_delete", -1));
        filternNachStufe(Utils.getUserStufe());
        refresh();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_klausurplan;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer;
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
            ladeKlausuren();
        }
        if (mi.getItemId() == R.id.action_delete) {
            confirmDelete = true;
            this.löscheAlleKlausuren();
            snackbar.show();
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
        Utils.getNotificationManager().cancel(NotificationService.ID_KLAUSURPLAN);
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerKlausurplanActivity(null);
        if (dialog != null)
            dialog.dismiss();
    }

    private void ladeKlausuren() {
        new Load().execute();
    }

    private void löscheAlleKlausuren() {
        lvKlausuren.setAdapter(new KlausurenAdapter(getApplicationContext(), new List<Klausur>(), -1));
    }

    private void initListView() {
        lvKlausuren = (ListView) findViewById(R.id.listViewKlausuren);
        lvKlausuren.setVerticalScrollBarEnabled(true);
        lvKlausuren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KlausurDialog.currentKlausur = klausurList.getObjectAt(position);
                dialog = new KlausurDialog(KlausurplanActivity.this);
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        KlausurplanActivity.this.dialog = null;
                    }
                });
            }
        });
    }

    private void initSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.snack), getString(R.string.snackbar_gelöscht), Snackbar.LENGTH_LONG);
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
                    klausurList = new List<>();
                    writeToFile();
                }
                refresh();
            }
        });
    }

    private void initAddButton() {
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                KlausurDialog.currentKlausur = new Klausur("", null, "", "");
                KlausurDialog klausurDialog = new KlausurDialog(KlausurplanActivity.this);
                klausurDialog.show();

                klausurDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                klausurDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

    private void initList() {
        klausurList = new List<>();
        readFromFile();
    }

    private void refresh() {
        writeToFile();
        lvKlausuren.setAdapter(new KlausurenAdapter(getApplicationContext(), klausurList, findeNächsteKlausur()));
        // new Handler().postDelayed(new Runnable() {
        //    @Override
        //   public void run() {
        //         lvKlausuren.smoothScrollToPositionFromTop(findeNächsteWoche(), 0);
        //     }
        //}, 100);

        lvKlausuren.setSelection(findeNächsteWoche());
    }

    public void add(Klausur k, boolean refresh) {
        if (!klausurList.contains(k)) {
            for (klausurList.toFirst(); klausurList.hasAccess(); klausurList.next())
                if (klausurList.getContent().after(k)) {
                    klausurList.insertBefore(k);
                    if (refresh)
                        refresh();
                    return;
                }
            klausurList.append(k);
            if (refresh)
                refresh();
        }
    } //fügt Klausuren an der richtigen Stelle in die Klausurliste

    private long findeNächsteKlausur() {
        Date heute = new Date();
        for (klausurList.toFirst(); klausurList.hasAccess() && heute.after(klausurList.getContent().datum); klausurList.next());
        if (klausurList.hasAccess())
            return klausurList.getContent().datum.getTime();
        return -1;
    }

    private int findeNächsteWoche() {
        Date heute = new Date();
        int  i     = 0;
        klausurList.toFirst();
        while (klausurList.hasAccess() && heute.after(klausurList.getContent().datum)) {
            klausurList.next();
            i++;
        }
        while (klausurList.hasPrevious() && klausurList.getContent().isSameWeek(klausurList.getPrevious())) {
            i--;
            klausurList.previous();
        }
        return i;
    }

    public void remove(Klausur k) {
        for (klausurList.toFirst(); klausurList.hasAccess(); klausurList.next())
            if (klausurList.getContent().equals(k)) {
                klausurList.remove();
                refresh();
                break;
            }
    }

    private void loescheAlteKlausuren(int monate) {
        if (monate < 0)
            return;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -monate);
        for (klausurList.toFirst(); klausurList.hasAccess(); ) {
            if (calendar.getTime().after(klausurList.getContent().datum))
                klausurList.remove();
            else
                klausurList.next();
        }
    }

    private void filternNachStufe(String stufe) {
        for (klausurList.toFirst(); klausurList.hasAccess(); ) {
            if (stufe.equals("EF") && (klausurList.getContent().istQ1Klausur() || klausurList.getContent().istQ2Klausur()))
                klausurList.remove();
            else if (stufe.equals("Q1") && (klausurList.getContent().istEFKlausur() || klausurList.getContent().istQ2Klausur()))
                klausurList.remove();
            else if (stufe.equals("Q2") && (klausurList.getContent().istEFKlausur() || klausurList.getContent().istQ1Klausur()))
                klausurList.remove();
            else
                klausurList.next();
        }
    }

    private void readFromFile() {
        try {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput(getString(R.string.klausuren_filename))));
            StringBuilder builder = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('_');
            }
            reader.close();
            String[] split = builder.toString().split("_");
            for (String s : split) {
                String[] current = s.split(";");
                if (current.length == 4) {
                    add(new Klausur(current[0], new Date(Long.parseLong(current[1])), current[2], current[3]), false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //holt die Klausuren aus der Textdatei

    private void writeToFile() {
        try {
            BufferedWriter writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    openFileOutput(getString(R.string.klausuren_filename), MODE_PRIVATE)));
            for (klausurList.toFirst(); klausurList.hasAccess(); klausurList.next()) {
                writer.write(klausurList.getContent().getWriterString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //schreibt die Klausuren in die Textdatei

    private class Load extends AsyncTask<Void, Void, Void> {
        KlausurenImportieren k;

        @Override
        protected void onPreExecute() {
            snackbar.dismiss();
            findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
            k = new KlausurenImportieren(getApplicationContext());
            k.execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.checkNetwork()) {
                try {
                    List<Klausur> result = k.get();
                    for (result.toFirst(); result.hasAccess(); result.next())
                        add(result.getContent(), false);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.snackbar_no_connection_info, Toast.LENGTH_SHORT).show();
            }
            filternNachStufe(Utils.getUserStufe());
            loescheAlteKlausuren(Utils.getController().getPreferences().getInt("pref_key_delete", -1));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.progressBar4).setVisibility(View.GONE);
            refresh();
        }
    }
}
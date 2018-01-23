package de.slgdev.stundenplan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppFeatureActivity;
import de.slgdev.stundenplan.dialog.DetailsDialog;
import de.slgdev.stundenplan.dialog.FinderDalog;
import de.slgdev.stundenplan.task.Importer;
import de.slgdev.stundenplan.utility.Fach;

public class StundenplanActivity extends LeoAppFeatureActivity implements TaskStatusListener {
    private WochentagFragment[]        fragments;
    private SQLiteConnectorStundenplan database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerStundenplanActivity(this);

        database = new SQLiteConnectorStundenplan(getApplicationContext());

        if (!database.hatGewaehlt()) {
            if (Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                startActivity(
                        new Intent(
                                getApplicationContext(),
                                AuswahlActivity.class
                        )
                );
            } else {
                new Importer().addListener(this).execute();
            }
        }

        initTabs();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wrapper_stundenplan;
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
        return R.string.title_plan;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.stundenplan;
    }

    @Override
    protected String getActivityTag() {
        return "StundenplanActivity";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stundenplan, menu);
        if (Utils.getUserPermission() == User.PERMISSION_SCHUELER) {
            menu.findItem(R.id.action_randstunde).setVisible(false);
        }
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit) {
            startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
        } else if (item.getItemId() == R.id.action_picture) {
            startActivity(new Intent(getApplicationContext(), StundenplanBildActivity.class));
        } else if (item.getItemId() == R.id.action_randstunde) {
            AlertDialog dialog = new FinderDalog(this);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerStundenplanActivity(null);
        if (database != null) {
            database.close();
        }
    }

    private void initTabs() {
        fragments = new WochentagFragment[5];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new WochentagFragment();
            fragments[i].setTag(i + 1);
            fragments[i].setDatabase(database);
        }
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.mo);
                    case 1:
                        return getString(R.string.di);
                    case 2:
                        return getString(R.string.mi);
                    case 3:
                        return getString(R.string.don);
                    case 4:
                        return getString(R.string.fr);
                    default:
                        return null;
                }
            }
        };
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        int dayOfWeek = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek > Calendar.SUNDAY && dayOfWeek < Calendar.SATURDAY)
            viewPager.setCurrentItem(new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 2);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void refreshUI() {
        for (WochentagFragment f : fragments)
            f.refreshUI();
    }

    @Override
    public void taskStarts() {

    }

    @Override
    public void taskFinished(Object... params) {
        refreshUI();
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    public static class WochentagFragment extends Fragment {
        private View                       root;
        private Fach[]                     fachArray;
        private int                        tag;
        private ListView                   listView;
        private SQLiteConnectorStundenplan database;

        @Override
        public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
            if (root == null) {
                root = layIn.inflate(R.layout.fragment_wochentag, container, false);

                listView = root.findViewById(R.id.listW);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (fachArray[position].id <= 0) {
                            database.freistunde(tag, position + 1);
                            fachArray[position] = database.getSubject(tag, position + 1);
                            view.invalidate();
                        }
                        DetailsDialog dialog = new DetailsDialog(getActivity());
                        dialog.show();
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        dialog.init(database.getSubject(tag, position + 1));
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Utils.getController().getStundenplanActivity().refreshUI();
                            }
                        });
                    }
                });
            }
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshUI();
        }

        private void refreshUI() {
            if (listView != null) {
                fachArray = database.getChosenSubjectsAtDay(tag);
                if (fachArray.length == 0)
                    root.findViewById(R.id.nolessons).setVisibility(View.VISIBLE);
                else
                    root.findViewById(R.id.nolessons).setVisibility(View.GONE);

                listView.setAdapter(new StundenAdapter(getContext(), fachArray));
            }
        }

        void setTag(int tag) {
            this.tag = tag;
        }

        public void setDatabase(SQLiteConnectorStundenplan database) {
            this.database = database;
        }
    }

    private static class StundenAdapter extends ArrayAdapter<Fach> {
        private final Context cont;
        private final Fach[]  faecher;

        StundenAdapter(Context pCont, Fach[] pFach) {
            super(pCont, R.layout.list_item_schulstunde, pFach);
            cont = pCont;
            faecher = pFach;
        }

        @NonNull
        @Override
        public View getView(int position, View v, @NonNull ViewGroup parent) {
            if (position < faecher.length && faecher[0] != null) {
                if (v == null) {
                    LayoutInflater layoutInflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = layoutInflater.inflate(R.layout.list_item_schulstunde, null);
                }

                TextView tvFach   = v.findViewById(R.id.fach_wt);
                TextView tvLehrer = v.findViewById(R.id.lehrer_wt);
                TextView tvRaum   = v.findViewById(R.id.raum_wt);
                TextView tvStunde = v.findViewById(R.id.stunde_wt);
                TextView tvNotiz  = v.findViewById(R.id.notiz);

                Fach f          = faecher[position];
                int  permission = Utils.getUserPermission();

                if (f != null) {
                    if (f.getName() != null && f.getNotiz() != null && f.getName().equals("") && !f.getNotiz().equals("")) {
                        tvNotiz.setText(f.getNotiz());
                        tvFach.setVisibility(View.INVISIBLE);
                        tvLehrer.setVisibility(View.INVISIBLE);
                        tvRaum.setVisibility(View.INVISIBLE);
                        tvNotiz.setVisibility(View.VISIBLE);
                    } else {
                        if (permission == User.PERMISSION_LEHRER) {
                            tvFach.setText(f.getName() + ' ' + f.getKuerzel());
                        } else {
                            tvFach.setText(f.getName());
                        }
                    }
                    if (permission == User.PERMISSION_LEHRER) {
                        tvLehrer.setText(f.getKlasse());
                    } else {
                        tvLehrer.setText(f.getLehrer());
                    }
                    tvRaum.setText(f.getRaum());
                    tvStunde.setText(f.getStundenName());
                    if (f.getSchriftlich() && Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                        v.findViewById(R.id.iconSchriftlich).setVisibility(View.VISIBLE);
                    }
                }
            }
            return v;
        }
    }
}
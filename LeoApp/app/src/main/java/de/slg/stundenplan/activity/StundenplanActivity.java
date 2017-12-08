package de.slg.stundenplan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.stundenplan.dialog.DetailsDialog;
import de.slg.stundenplan.dialog.FinderDalog;
import de.slg.stundenplan.utility.Fach;

public class StundenplanActivity extends LeoAppFeatureActivity {
    private WochentagFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getController().registerStundenplanActivity(this);
        if (!Utils.getController().getStundenplanDatabase().hatGewaehlt()) {
            if (Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                startActivity(new Intent(getApplicationContext(), AuswahlActivity.class));
            } else {
                new CreateLehrerStundenplan().execute();
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
    }

    private void initTabs() {
        fragments = new WochentagFragment[5];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new WochentagFragment();
            fragments[i].setTag(i + 1);
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.viPager);
        viewPager.setAdapter(adapter);

        int dayOfWeek = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek > Calendar.SUNDAY && dayOfWeek < Calendar.SATURDAY)
            viewPager.setCurrentItem(new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void refreshUI() {
        for (WochentagFragment f : fragments)
            f.refreshUI();
    }

    public static class WochentagFragment extends Fragment {
        private View     root;
        private Fach[]   fachArray;
        private int      tag;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
            if (root == null) {
                root = layIn.inflate(R.layout.fragment_wochentag, container, false);
                listView = (ListView) root.findViewById(R.id.listW);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (fachArray[position].id <= 0) {
                            Utils.getController().getStundenplanDatabase().freistunde(tag, position + 1);
                            fachArray[position] = Utils.getController().getStundenplanDatabase().getFach(tag, position + 1);
                            view.invalidate();
                        }
                        DetailsDialog dialog = new DetailsDialog(getActivity());
                        dialog.show();
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        dialog.init(Utils.getController().getStundenplanDatabase().getFach(tag, position + 1));
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
                fachArray = Utils.getController().getStundenplanDatabase().gewaehlteFaecherAnTag(tag);
                listView.setAdapter(new StundenAdapter(getContext(), fachArray));
            }
        }

        void setTag(int tag) {
            this.tag = tag;
        }
    }

    private static class StundenAdapter extends ArrayAdapter<Fach> {
        private final Context cont;
        private final Fach[]  fachAd;
        private final View[]  viAd;

        StundenAdapter(Context pCont, Fach[] pFach) {
            super(pCont, R.layout.list_item_schulstunde, pFach);
            cont = pCont;
            fachAd = pFach;
            viAd = new View[pFach.length];
        }

        @NonNull
        @Override
        public View getView(int position, View v, @NonNull ViewGroup parent) {
            if (position < fachAd.length && fachAd[0] != null) {
                if (v == null) {
                    LayoutInflater layoutInflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = layoutInflater.inflate(R.layout.list_item_schulstunde, null);
                }
                TextView tvFach   = (TextView) v.findViewById(R.id.fach_wt);
                TextView tvLehrer = (TextView) v.findViewById(R.id.lehrer_wt);
                TextView tvRaum   = (TextView) v.findViewById(R.id.raum_wt);
                TextView tvStunde = (TextView) v.findViewById(R.id.stunde_wt);
                TextView tvNotiz  = (TextView) v.findViewById(R.id.notiz);
                if (fachAd[position] != null) {
                    if (fachAd[position].getName() != null && fachAd[position].getNotiz() != null && fachAd[position].getName().equals("") && !fachAd[position].getNotiz().equals("")) {
                        tvNotiz.setText(fachAd[position].getNotiz());
                        tvFach.setVisibility(View.INVISIBLE);
                        tvLehrer.setVisibility(View.INVISIBLE);
                        tvRaum.setVisibility(View.INVISIBLE);
                        tvNotiz.setVisibility(View.VISIBLE);
                    } else {
                        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
                            tvFach.setText(fachAd[position].getName() + ' ' + fachAd[position].getKuerzel());
                        else
                            tvFach.setText(fachAd[position].getName());
                    }
                    if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
                        tvLehrer.setText(fachAd[position].getKlasse());
                    } else {
                        tvLehrer.setText(fachAd[position].getLehrer());
                    }
                    tvRaum.setText(fachAd[position].getRaum());
                    tvStunde.setText(fachAd[position].getStundenName());
                    if (fachAd[position].getSchriftlich()) {
                        v.findViewById(R.id.iconSchriftlich).setVisibility(View.VISIBLE);
                    }
                }
            }
            viAd[position] = v;
            return v;
        }
    }

    private class CreateLehrerStundenplan extends AsyncTask<Void, Void, Void> {
        private AuswahlActivity.FachImporter importer;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            importer = new AuswahlActivity.FachImporter();
            importer.execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                importer.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            refreshUI();
        }
    }
}
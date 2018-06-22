package de.slgdev.vertretungsplan.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorVertretungsplan;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.stundenplan.utility.Fach;
import de.slgdev.vertretungsplan.task.ImporterVertretung;
import de.slgdev.vertretungsplan.utility.VertretungListAdapter;
import de.slgdev.vertretungsplan.utility.VertretungsEvent;


public class ListenFragment extends Fragment {
    private static final String ARG_PARAM1 = "tagnr";

    private int tagnr;
    public boolean refreshing;

    private View view;
    private SwipeRefreshLayout swipeLayout;

    private ExpandableListView listView2;
    private VertretungListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    private SQLiteConnectorVertretungsplan db;
    private SQLiteConnectorStundenplan stundenplan;



    public ListenFragment() {
        // Required empty public constructor
    }


    public static ListenFragment newInstance(int tagnr) {
        ListenFragment fragment = new ListenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tagnr);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            tagnr = getArguments().getInt(ARG_PARAM1);
        } else {
            tagnr = 1;
        }
        refreshing=false;
        view = inflater.inflate(R.layout.fragment_vertretung, container, false);

        initSwipeToRefresh(view);

        db = new SQLiteConnectorVertretungsplan(getActivity());
        stundenplan = new SQLiteConnectorStundenplan(getActivity());

        listView2 = view.findViewById(R.id.vertretungListView);

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        loadEvents(filtern(db.gibVertretungsplan(tagnr)));

        listAdapter = new VertretungListAdapter(getActivity(), listDataHeader, listHash);
        listView2.setAdapter(listAdapter);
        return view;
    }


    private void loadEvents(VertretungsEvent[] vEvents) {
        for (int i=0; i<vEvents.length; i++)    {
            String entfall = getString(R.string.substitution);
            if (vEvents[i].getEntfall())
                entfall = getString(R.string.cancellation);
            String stufeKlasse = getString(R.string.substitution_class);
            if (vEvents[i].getKlasse().contains("EF") || vEvents[i].getKlasse().contains("Q1") || vEvents[i].getKlasse().contains("Q2"))
                stufeKlasse = getString(R.string.substitution_grade);
            listDataHeader.add(stufeKlasse+": "+vEvents[i].getKlasse()+" |"+getString(R.string.lesson)+": "+vEvents[i].getStunde()+" *"+getString(R.string.teacher)+": "+vEvents[i].getLehrer()+ " +"+entfall);

            List<String> zusatzInfo = new ArrayList<>();
            zusatzInfo.clear();
            if (!vEvents[i].getEntfall()) {
                zusatzInfo.add(getString(R.string.supply_teacher)+": " + vEvents[i].getVertreter());
                zusatzInfo.add(getString(R.string.room)+": " + vEvents[i].getRaum());
                zusatzInfo.add(getString(R.string.subject)+": "+vEvents[i].getFach());
            }
            if (!vEvents[i].getAnmerkung().equals(""))
                zusatzInfo.add(getString(R.string.annotation)+": "+vEvents[i].getAnmerkung());
            else
                zusatzInfo.add(getString(R.string.annotation)+": /");
            listHash.put(listDataHeader.get(i), zusatzInfo);
        }
    }

    public void updateListView(VertretungsEvent[] vEvents)   {
        listDataHeader.clear();
        listHash.clear();
        TextView t = view.findViewById(R.id.noSubstitution);

        loadEvents(vEvents);
        listAdapter.update(listDataHeader, listHash);
        listView2.setAdapter(listAdapter);

//        if (vEvents.length>0) {
//            Log.d("test1", String.valueOf(tagnr)+": invisible");
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    t.setVisibility(View.GONE);
//                    swipeLayout.setVisibility(View.VISIBLE);
//                }
//            });
//        }
//        else {
//            Log.d("test1", String.valueOf(tagnr)+": visible");
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    t.setVisibility(View.VISIBLE);
//                    swipeLayout.setVisibility(View.GONE);
//                }
//            });
//        }

        if (vEvents.length>0)   {
            Log.d("test1", String.valueOf(tagnr)+": kein Text");
            t.setText("");
        }
        else {
            Log.d("test1", String.valueOf(tagnr)+": Text");
            t.setText(R.string.no_substitution);
        }

    }


    public VertretungsEvent[] filtern(VertretungsEvent[] vEvents) {

        // 0 -> ganzer Plan
        // 1(Schüler) -> nur eigene Stufe
        // 2(Schüler) -> nur eigener Plan
        // 1(Lehrer) -> eigenes Kürzel

        int filterkriterium = Utils.getController().getPreferences().getInt("pref_key_vertretung_filter", 0);



        List<VertretungsEvent> ret = new ArrayList<VertretungsEvent>();

        if (!Utils.getUserStufe().equals("TEA")) {
            for (int i=0; i<vEvents.length; i++) {
                if (!vEvents[i].getKlasse().equals("")) {
                    if (filterkriterium == 0)
                        ret.add(vEvents[i]);
                    else if (filterkriterium == 1) {
                        if (vEvents[i].getKlasse().contains(Utils.getUserStufe()))
                            ret.add(vEvents[i]);
                    }
                    else if (filterkriterium == 2)  {

                        if (stundenplan.getSubject(db.getWochentagNr(tagnr), Integer.parseInt(String.valueOf(vEvents[i].getStunde().charAt(0)))) != null && stundenplan.getSubject(db.getWochentagNr(tagnr), Integer.parseInt(String.valueOf(vEvents[i].getStunde().charAt(0)))).getLehrer().equals(vEvents[i].getLehrer()))    {
                            ret.add(vEvents[i]);
                        }
                    }
                }
            }
        }
        else if (Utils.getUserStufe().equals("TEA"))   {
            for (int i=0; i<vEvents.length; i++) {
                if (filterkriterium == 0)
                    ret.add(vEvents[i]);
                else if (filterkriterium == 1)   {
                    if (vEvents[i].getLehrer().equals(Utils.getLehrerKuerzel()) || vEvents[i].getVertreter().equals(Utils.getLehrerKuerzel()))  {
                        ret.add(vEvents[i]);
                    }
                }
            }
        }

        VertretungsEvent[] retArray = new VertretungsEvent[ret.size()];
        for (int i=0; i<ret.size(); i++)    {
            retArray[i] = ret.get(i);
        }
        return retArray;
    }



    private void initSwipeToRefresh(View view) {
        swipeLayout = view.findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing)
                    new ImporterVertretung().addListener((TaskStatusListener)getActivity()).execute();
            }
        });
        swipeLayout.setProgressViewEndTarget(false, 320);
        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(
                        getActivity().getApplicationContext(),
                        R.color.colorPrimary
                )
        );
    }

    public void startRefreshing()   {
        refreshing=true;
        if (view!=null) {
            SwipeRefreshLayout swipeLayout = view.findViewById(R.id.swipeToRefresh);
            swipeLayout.setRefreshing(true);
        }
    }

    public void stopRefreshing()    {
        refreshing=false;
        if (view!=null) {
            SwipeRefreshLayout swipeLayout = view.findViewById(R.id.swipeToRefresh);
            swipeLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        db.close();
        stundenplan.close();
    }
}

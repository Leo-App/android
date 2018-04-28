package de.slgdev.essensbestellung.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
//import de.slgdev.leoapp.utility.datastructure.List;
import java.util.List;
import android.app.Activity;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public WeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // preparing list data
        prepareListData();
        View rootView = inflater.inflate(R.layout.fragment_subst, container, false);
        expListView = (ExpandableListView) rootView.findViewById(R.id.expandable_subst);

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        return inflater.inflate(R.layout.fragment_subst, container, false);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Montag");
        //listDataHeader.add("Dienstag");
        //listDataHeader.add("Mittwoch");
        //listDataHeader.add("Donnerstag");

        List<String> mon = new ArrayList<String>();
        mon.add("test");
        mon.add("test2");


        listDataChild.put(listDataHeader.get(0), mon);
    }

    // https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

}

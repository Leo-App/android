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

        View rootView = inflater.inflate(R.layout.fragment_subst, container, false);
        ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.expandable_subst);

        return inflater.inflate(R.layout.fragment_subst, container, false);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // get the listview
       // ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.expandable_subst);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(Utils.getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }


    private void prepareListData() {
        //listDataHeader = new List<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Montag");
        listDataHeader.add("Dienstag");
        listDataHeader.add("Mittwoch");
        listDataHeader.add("Donnerstag");
    }

    // https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

}

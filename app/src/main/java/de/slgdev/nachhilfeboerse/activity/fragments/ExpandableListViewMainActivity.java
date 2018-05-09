package de.slgdev.nachhilfeboerse.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.nachhilfeboerse.activity.Adapter.ExpendableListViewAdapter;

/**
 * Created by Benno on 08.04.2018.
 */

public class ExpandableListViewMainActivity extends Fragment {

    private ExpendableListViewAdapter listAdapter ;
    private ExpandableListView listView ;
    private List<String> listDataHeader ;
    private HashMap<String,List<String>> listHash ;

    public ExpandableListViewMainActivity(){

    }


    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstance) {

        View rootView = inflator.inflate(R.layout.activity_nachhilfeboerse_expendablelistview, container, false);

        listView = (ExpandableListView)rootView.findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpendableListViewAdapter(Utils.getContext(),listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Toast.makeText(
                        Utils.getContext(),
                        listDataHeader.get(i)
                                + " : "
                                + listHash.get(
                                listDataHeader.get(i)).get(
                                i1), Toast.LENGTH_SHORT)
                        .show();
                return false ;
            }
        });
        return rootView ;
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Mathe");
        listDataHeader.add("Deutsch");
        listDataHeader.add("Englisch");
        listDataHeader.add("Spanisch");

        List<String> mathe = new ArrayList<>();
        mathe.add("Benno Dill");
        mathe.add("Mauri");
        mathe.add("Test");

        List<String> deutsch = new ArrayList<>();
        deutsch.add("Silas Wiennemöller");

        List<String> englisch = new ArrayList<>();
        englisch.add("Benedikt Vidic");

        List<String> spanisch = new ArrayList<>();
        spanisch.add("Luisa Federsel");

        listHash.put(listDataHeader.get(0),mathe);
        listHash.put(listDataHeader.get(1),deutsch);
        listHash.put(listDataHeader.get(2),englisch);
        listHash.put(listDataHeader.get(3),spanisch);



    }
}





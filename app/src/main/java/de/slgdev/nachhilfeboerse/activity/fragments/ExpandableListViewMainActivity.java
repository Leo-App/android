package de.slgdev.nachhilfeboerse.activity.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.nachhilfeboerse.activity.Adapter.ExpendableListViewAdapter;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_expendablelistview;

/**
 * Created by Benno on 08.04.2018.
 */

public class ExpandableListViewMainActivity extends Fragment{

    private ExpendableListViewAdapter listAdapter ;
    private ExpandableListView listView ;
    private List<String> listDataHeader ;
    private HashMap<String,List<String>> listHash ;

    public void ExpandableListViewMainActivity(){

    }


    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstance) {

        View rootView = inflator.inflate(R.layout.activity_nachhilfeboerse_expendablelistview, container, false);

        listView = (ExpandableListView)rootView.findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpendableListViewAdapter(Utils.getContext(),listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        return rootView ;
    }

    private void initData() {
        listDataHeader = new List<String>();
        listHash = new HashMap<>();



        listDataHeader.append("Mathe");
        listDataHeader.append("Deutsch");
        listDataHeader.append("Englisch");
        listDataHeader.append("Französisch");

        List<String> Mathe = new List<>();
        Mathe.append("Benno Dill");
        Mathe.append("Benedikt Vidic");

        List<String> Deutsch = new List<>();
        Deutsch.append("Silas Wiennemöller");
        Deutsch.append("Luisa Federsel");

        List<String> Englisch = new List<>();
        Englisch.append("sadasf");
        Englisch.append("asdasfasd");

        List<String> Franzoesisch = new List<>();
        Franzoesisch.append("dasdasd");
        Franzoesisch.append("fdasdasd");

        listHash.put(listDataHeader.getObjectAt(0),Mathe);
        listHash.put(listDataHeader.getObjectAt(1),Deutsch);
        listHash.put(listDataHeader.getObjectAt(2),Englisch);
        listHash.put(listDataHeader.getObjectAt(3),Franzoesisch);


    }
}





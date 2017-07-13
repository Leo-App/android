package de.slg.stundenplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.slg.leoapp.R;

public class FragmentMittwoch extends Fragment {

    private Fach[] fachArray;

    @Override
    public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
        View v = layIn.inflate(R.layout.fragment_wochentag, container, false);
        ListView listW = (ListView) v.findViewById(R.id.listW);

        Stundenplanverwalter sv = new Stundenplanverwalter(getContext(), "meinefaecher.txt");
        fachArray = sv.gibFaecherSortTag(3);

        WochentagAdapter wtAdapter = new WochentagAdapter(getContext(), fachArray);
        listW.setAdapter(wtAdapter);


        listW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WrapperStundenplanActivity.akTag = "3";
                WrapperStundenplanActivity.akStunde = fachArray[position].gibStunde();
                startActivity(new Intent(getContext(), SPDetailsActivity.class));
            }
        });

        return v;
    }
}
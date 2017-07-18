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
import de.slg.leoapp.Utils;

public class WochentagFragment extends Fragment {
    private Fach[] fachArray;
    private int tag;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater layIn, ViewGroup container, Bundle savedInstanceState) {
        View v = layIn.inflate(R.layout.fragment_wochentag, container, false);

        listView = (ListView) v.findViewById(R.id.listW);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fachArray[position].id <= 0) {
                    Utils.getStundDB().neueFreistunde(tag, position + 1);
                    fachArray[position] = Utils.getStundDB().getFach(tag, position + 1);
                    view.invalidate();
                }
                startActivity(new Intent(getContext(), SPDetailsActivity.class)
                        .putExtra("tag", tag)
                        .putExtra("stunde", position + 1));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fachArray = Utils.getStundDB().gewaehlteFaecherAnTag(tag);
        listView.setAdapter(new WochentagAdapter(getContext(), fachArray));
    }

    void setTag(int tag) {
        this.tag = tag;
    }
}
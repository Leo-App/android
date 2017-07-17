package de.slg.vertretung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import de.slg.leoapp.R;


public class SubstitutionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_subst, container, false);


        return rootView;

    }

    SubstitutionFragment setDate(Date d) {

        Date d1 = d;
        return this;

    }

}

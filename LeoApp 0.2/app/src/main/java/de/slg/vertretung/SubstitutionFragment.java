package de.slg.vertretung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.slg.leoapp.R;


public class SubstitutionFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(
                R.layout.fragment_qr, container, false);



        return rootView;

    }

}

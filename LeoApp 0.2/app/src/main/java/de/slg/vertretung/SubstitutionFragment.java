package de.slg.vertretung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import de.slg.leoapp.R;


public class SubstitutionFragment extends Fragment {

    private View rootView;
    private Date d;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(
                R.layout.fragment_subst, container, false);


        return rootView;

    }

    void setDate(Date d) {

        this.d = d;

    }

}

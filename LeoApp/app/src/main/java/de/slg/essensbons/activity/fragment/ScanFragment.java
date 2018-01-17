package de.slg.essensbons.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import de.slg.essensbons.activity.EssensbonActivity;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.leoapp.R;

public class ScanFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_scan, container, false);

        final EssensbonActivity activityReference = (EssensbonActivity) getActivity();
        final Button scan = rootView.findViewById(R.id.scan_button);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityReference.scan();
                    }
                });
            }
        };
        handler.postDelayed(r, 100);

        if (EssensbonUtils.mensaModeEnabled())
            handler.removeCallbacks(r);

        return rootView;
    }
}
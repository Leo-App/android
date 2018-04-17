package de.slgdev.essensbons.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import de.slgdev.essensbons.activity.EssensbonActivity;
import de.slgdev.essensbons.utility.EssensbonUtils;
import de.slgdev.leoapp.R;

public class ScanFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_scan, container, false);

        final EssensbonActivity activityReference = (EssensbonActivity) getActivity();
        final Button scan = rootView.findViewById(R.id.scan_button);

        final Handler handler = new Handler();
        final Runnable r = () -> scan.setOnClickListener(v -> activityReference.scan());
        handler.postDelayed(r, 100);

        if (EssensbonUtils.mensaModeEnabled())
            handler.removeCallbacks(r);

        return rootView;
    }
}
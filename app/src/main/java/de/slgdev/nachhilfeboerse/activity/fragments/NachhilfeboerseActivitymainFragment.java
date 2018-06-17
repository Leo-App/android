package de.slgdev.nachhilfeboerse.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.nachhilfeboerse.activity.Nachhilfeboerse_nachhilfegebenActivity;

/**
 * Created by Benno on 08.04.2018.
 */

public class NachhilfeboerseActivitymainFragment extends Fragment {

    public NachhilfeboerseActivitymainFragment(){

    }

    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstance) {

        View rootView = inflator.inflate(R.layout.activity_nachhilfeboerse_mainactivity_fragment, container, false);
        Button anbieten = (Button)rootView.findViewById(R.id.wechseln);
        Intent intentNachhilfegeben = new Intent(Utils.getContext(), Nachhilfeboerse_nachhilfegebenActivity.class);
        anbieten.setOnClickListener(view -> startActivity(intentNachhilfegeben));
        return rootView ;
    }
}

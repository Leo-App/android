package de.slgdev.nachhilfeboerse.activity.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.nachhilfeboerse.activity.NachhilfeboerseActivity;
import de.slgdev.nachhilfeboerse.activity.Nachhilfeboerse_nachhilfegebenActivity;

/**
 * Created by Benno on 08.04.2018.
 */

public class NachhilfeboerseActivitymainFragment extends Fragment {

    public void NachhilfeboerseActivitymainFragment(){

    }

    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstance) {

        View rootView = inflator.inflate(R.layout.activity_nachhilfeboerse_mainactivity_fragment, container, false);
        Button anbieten = (Button)rootView.findViewById(R.id.anbieten);
        Intent intentNachhilfegeben = new Intent(Utils.getContext(), Nachhilfeboerse_nachhilfegebenActivity.class);
        anbieten.setOnClickListener(view -> startActivity(intentNachhilfegeben));
        return rootView ;
    }
}

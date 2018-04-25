package de.slgdev.essensbestellung.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ExpandableListView;

import de.slgdev.leoapp.R;


/**
 * Created by Florian on 10.03.2018.
 */

public class EssensbestellungActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestellung);
        //final GestureDetector gdt = GestureDetector(this, new GestureListener());

        WeekFragment week = new WeekFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container_weekfragment, week).commit();

    }

    @Override
    public void onClick(View view) {

    }
}

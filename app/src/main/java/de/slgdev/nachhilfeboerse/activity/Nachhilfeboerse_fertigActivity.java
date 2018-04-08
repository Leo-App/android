package de.slgdev.nachhilfeboerse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.startseite.activity.MainActivity;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse;
import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_fertig;
import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_nachhilfegeben;

/**
 * Created by Benno on 08.04.2018.
 */

public class Nachhilfeboerse_fertigActivity extends LeoAppNavigationActivity {
    @Override
    protected String getActivityTag() { return  "NachhilfeboerseActivity"; }

    @Override
    protected int getContentView() { return activity_nachhilfeboerse; }

    @Override
    protected int getDrawerLayoutId() { return R.id.drawerLayout; }

    @Override
    protected int getNavigationId() { return R.id.navigationView; }

    @Override
    protected int getToolbarId() { return R.id.toolbar; }

    @Override
    protected int getToolbarTextId() { return R.string.title_tutoring; }

    @Override
    protected int getNavigationHighlightId() { return R.id.nachhilfeboerse; }

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(activity_nachhilfeboerse_fertig);

        Button hauptmenue = (Button) findViewById(R.id.hauptmenue);

        Intent hauptIntent = new Intent(Nachhilfeboerse_fertigActivity.this , MainActivity.class);
        hauptmenue.setOnClickListener(view -> startActivity(hauptIntent));

    }
}

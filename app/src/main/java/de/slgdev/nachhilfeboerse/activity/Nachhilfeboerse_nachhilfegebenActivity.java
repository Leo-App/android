package de.slgdev.nachhilfeboerse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse;
import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_nachhilfegeben;

/**
 * Created by Benno on 08.04.2018.
 */

public class Nachhilfeboerse_nachhilfegebenActivity extends LeoAppNavigationActivity {
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
        setContentView(activity_nachhilfeboerse_nachhilfegeben);

        Button weiter = (Button) findViewById(R.id.goon);
        EditText geldgetrag = (EditText) findViewById(R.id.geldeingabe);

        Intent weiterIntent = new Intent(Nachhilfeboerse_nachhilfegebenActivity.this , Nachhilfeboerse_fertigActivity.class);
        weiter.setOnClickListener(View -> startActivity(weiterIntent));

    }
}

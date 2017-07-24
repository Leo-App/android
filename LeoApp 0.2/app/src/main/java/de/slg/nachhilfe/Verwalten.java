package de.slg.nachhilfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import de.slg.leoapp.R;

public class Verwalten extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmpfangeFaecherUser toll = new EmpfangeFaecherUser();
        toll.execute();
        setContentView(R.layout.activity_verwalten);
        String[] result = new String[0];
        try {
            result = toll.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        NachhilfeAdapter adapter = new NachhilfeAdapter(getApplicationContext(), result);
        ListView vl = (ListView) findViewById(R.id.ListView2);
        vl.setAdapter(adapter);
        vl.setClickable(true);
        Toolbar hans = (Toolbar) findViewById(R.id.actionBarNavDrawer2);
        hans.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(hans);
        getSupportActionBar().setTitle("Ihre Anzeigen"); //TODO: String Ressource
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem mi) {

        if(mi.getItemId() == R.id.action_clear) {

            Intent intent = new Intent(this, NachhilfeboerseActivity.class);
            startActivity(intent);

        }
        return true;

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_verwalten,menu);
        return true;
    }

}


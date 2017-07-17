package de.slg.stundenplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StundenplanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new StundenplanView(getApplicationContext()));
    }
}
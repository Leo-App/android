package de.slg.stundenplan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import de.slg.leoapp.R;

class RandstundenDialog extends AlertDialog {
    RandstundenDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_randstunden);

        findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add1).setVisibility(View.INVISIBLE);
                findViewById(R.id.text1).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add2).setVisibility(View.INVISIBLE);
                findViewById(R.id.text2).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add3).setVisibility(View.INVISIBLE);
                findViewById(R.id.text3).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add4).setVisibility(View.INVISIBLE);
                findViewById(R.id.text4).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add5).setVisibility(View.INVISIBLE);
                findViewById(R.id.text5).setVisibility(View.VISIBLE);
            }
        });

        View ok = findViewById(R.id.buttonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
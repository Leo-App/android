package de.slg.stundenplan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.slg.leoapp.utility.List;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

class FinderDalog extends AlertDialog {
    private List<String> kürzel;

    FinderDalog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_randstunden);

        final TextView t1 = (TextView) findViewById(R.id.text1);
        final TextView t2 = (TextView) findViewById(R.id.text2);
        final TextView t3 = (TextView) findViewById(R.id.text3);
        final TextView t4 = (TextView) findViewById(R.id.text4);
        final TextView t5 = (TextView) findViewById(R.id.text5);

        findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add1).setVisibility(View.INVISIBLE);
                t1.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add2).setVisibility(View.INVISIBLE);
                t2.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add3).setVisibility(View.INVISIBLE);
                t3.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add4).setVisibility(View.INVISIBLE);
                t4.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add5).setVisibility(View.INVISIBLE);
                t5.setVisibility(View.VISIBLE);
            }
        });

        final View ok = findViewById(R.id.buttonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kürzel = new List<>();
                if (t1.getText().length() > 0) {
                    kürzel.append(t1.getText().toString());
                }
                if (t2.getText().length() > 0) {
                    kürzel.append(t2.getText().toString());
                }
                if (t3.getText().length() > 0) {
                    kürzel.append(t3.getText().toString());
                }
                if (t4.getText().length() > 0) {
                    kürzel.append(t4.getText().toString());
                }
                if (t5.getText().length() > 0) {
                    kürzel.append(t5.getText().toString());
                }

                try {
                    StundenplanDBDummy db = new StundenplanDBDummy(getContext());

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            Utils.getContext().openFileInput("stundenplan.txt")));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        String[] fach = line.replace("\"", "").split(",");
                        if (kürzel.contains(fach[2])) {
                            db.insertStunde(Integer.parseInt(fach[5]), Integer.parseInt(fach[6]));
                        }
                    }
                    reader.close();

                    findViewById(R.id.add1).setVisibility(View.GONE);
                    findViewById(R.id.add2).setVisibility(View.GONE);
                    findViewById(R.id.add3).setVisibility(View.GONE);
                    findViewById(R.id.add4).setVisibility(View.GONE);
                    findViewById(R.id.add5).setVisibility(View.GONE);
                    t1.setVisibility(View.GONE);
                    t2.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                    t4.setVisibility(View.GONE);
                    t5.setVisibility(View.GONE);
                    ok.setVisibility(View.GONE);

                    TextView t = (TextView) findViewById(R.id.textView);
                    t.setText(db.gibFreistundenZeiten());

                    db.clear();
                    db.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
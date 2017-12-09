package de.slg.stundenplan.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorStundenplanFinder;
import de.slg.leoapp.utility.List;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;

public class FinderDalog extends AlertDialog {
    private List<String> kürzel;

    public FinderDalog(Activity context) {
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

        final TextView a2 = (TextView) findViewById(R.id.add2);
        final TextView a3 = (TextView) findViewById(R.id.add3);
        final TextView a4 = (TextView) findViewById(R.id.add4);
        final TextView a5 = (TextView) findViewById(R.id.add5);

        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            t1.setText(Utils.getLehrerKuerzel());

        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add2).setVisibility(View.INVISIBLE);
                t2.setVisibility(View.VISIBLE);
                InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(t2, InputMethodManager.SHOW_IMPLICIT);
                t2.requestFocus();
            }
        });

        a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t2.getVisibility() != View.VISIBLE) {
                    findViewById(R.id.add3).setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(t3, InputMethodManager.SHOW_IMPLICIT);
                    t3.requestFocus();
                } else {
                    a2.callOnClick();
                }
            }
        });

        a4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t3.getVisibility() != View.VISIBLE) {
                    findViewById(R.id.add4).setVisibility(View.INVISIBLE);
                    t4.setVisibility(View.VISIBLE);
                    InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(t4, InputMethodManager.SHOW_IMPLICIT);
                    t4.requestFocus();
                } else {
                    a3.callOnClick();
                }
            }
        });

        a5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t4.getVisibility() != View.VISIBLE) {
                    findViewById(R.id.add5).setVisibility(View.INVISIBLE);
                    t5.setVisibility(View.VISIBLE);
                    InputMethodManager manager = (InputMethodManager) Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(t5, InputMethodManager.SHOW_IMPLICIT);
                    t5.requestFocus();
                } else {
                    a4.callOnClick();
                }
            }
        });

        a2.getCompoundDrawables()[0].setColorFilter(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        a3.getCompoundDrawables()[0].setColorFilter(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        a4.getCompoundDrawables()[0].setColorFilter(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        a5.getCompoundDrawables()[0].setColorFilter(ContextCompat.getColor(Utils.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        final View ok = findViewById(R.id.buttonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kürzel = new List<>();
                if (t1.getText().length() > 0) {
                    kürzel.append(t1.getText().toString().toUpperCase());
                }
                if (t2.getText().length() > 0) {
                    kürzel.append(t2.getText().toString().toUpperCase());
                }
                if (t3.getText().length() > 0) {
                    kürzel.append(t3.getText().toString().toUpperCase());
                }
                if (t4.getText().length() > 0) {
                    kürzel.append(t4.getText().toString().toUpperCase());
                }
                if (t5.getText().length() > 0) {
                    kürzel.append(t5.getText().toString().toUpperCase());
                }

                try {
                    SQLiteConnectorStundenplanFinder db = new SQLiteConnectorStundenplanFinder(getContext());

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
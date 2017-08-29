package de.slg.klausurplan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;


class KlausurDialog extends AlertDialog {

    static Klausur currentKlausur;
    private EditText eingabeFach;
    private EditText eingabeDatum;
    private EditText eingabeNotiz;

    private Snackbar snackbarDate;
    private Snackbar snackbarTitle;

    KlausurDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.dialog_klausur);

        initEditTexts();
        initButtons();
        initSnackbarTitel();
        initSnackbarDatum();

        findViewById(R.id.buttonExamDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                klausurLöschen();
                dismiss();
            }
        });

        findViewById(R.id.buttonExamDis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.buttonExamSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(eingabeFach.getWindowToken(), 0);
                if (currentKlausur != null) {
                    if (eingabeFach.getText().length() == 0) {
                        snackbarTitle.show();
                    } else if (eingabeDatum.getText().length() < 8 || !istDatumFormat(eingabeDatum.getText().toString())) {
                        snackbarDate.show();
                    } else {
                        klausurSpeichern();
                        dismiss();
                    }

                }
            }
        });


    }

    private void initSnackbarTitel() {
        snackbarTitle = Snackbar.make(findViewById(R.id.snack), getContext().getString(R.string.snackbar_missing_title), Snackbar.LENGTH_LONG);
        snackbarTitle.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        snackbarTitle.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarTitle.dismiss();
            }
        });
    }

    private void initSnackbarDatum() {
        snackbarDate = Snackbar.make(findViewById(R.id.snack), getContext().getString(R.string.snackbar_date_invalid), Snackbar.LENGTH_LONG);
        snackbarDate.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        snackbarDate.setAction(getContext().getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarDate.dismiss();
            }
        });
    }

    private void initButtons() {
        if (!currentKlausur.getFach().equals("")) {
            Button buttonDel = (Button) findViewById(R.id.buttonExamDel);
            buttonDel.setEnabled(true);
        }
    }

    private void initEditTexts() {
        eingabeFach = (EditText) findViewById(R.id.eingabeFach);
        eingabeDatum = (EditText) findViewById(R.id.eingabeDatum);
        eingabeNotiz = (EditText) findViewById(R.id.eingabeNotiz);
        if (currentKlausur != null) {
            eingabeFach.setText(currentKlausur.getFach());
            eingabeDatum.setText(currentKlausur.getDatum(false));
            eingabeNotiz.setText(currentKlausur.getNotiz());
        }
    }

    private void klausurSpeichern() {
        currentKlausur.setDatum(getDate(eingabeDatum.getText().toString()));
        currentKlausur.setNotiz(eingabeNotiz.getText().toString());
        if (currentKlausur.getFach().equals("")) {
            currentKlausur.setFach(eingabeFach.getText().toString());
            Utils.getKlausurplanActivity().add(currentKlausur, true);
        } else {
            Utils.getKlausurplanActivity().remove(currentKlausur);
            currentKlausur.setFach(eingabeFach.getText().toString());
            Utils.getKlausurplanActivity().add(currentKlausur, true);
        }
    }

    private void klausurLöschen() {
        if (!currentKlausur.getFach().equals(""))
            Utils.getKlausurplanActivity().remove(currentKlausur);
    }

    private boolean istDatumFormat(String s) {
        String[] parts = s.replace('.', '_').split("_");
        if (parts.length != 3)
            return false;
        int day, month;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return !(month < 1 || day < 1 || day > 31 || month > 12);
    }

    private Date getDate(String s) {
        if (istDatumFormat(s)) {
            String[] parts = s.replace('.', '_').split("_");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = 2000 + Integer.parseInt(parts[2]);
                Calendar c = new GregorianCalendar();
                c.set(year, month, day, 0, 0, 0);
                return c.getTime();
            }
        }
        return null;
    }
}
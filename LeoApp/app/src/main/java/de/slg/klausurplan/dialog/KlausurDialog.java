package de.slg.klausurplan.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.slg.klausurplan.utility.Klausur;
import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorKlausurplan;

public class KlausurDialog extends AppCompatDialog {

    private Klausur  currentKlausur;
    private EditText eingabeFach;
    private EditText eingabeDatum;
    private EditText eingabeNotiz;

    private Snackbar snackbarDate;
    private Snackbar snackbarTitle;

    public KlausurDialog(@NonNull Activity context, @NonNull Klausur klausur) {
        super(context);
        currentKlausur = klausur;
    }

    @Override
    public void onCreate(final Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.dialog_klausur);

        initEditTexts();
        initSnackbarTitel();
        initSnackbarDatum();

        findViewById(R.id.buttonExamDel).setEnabled(currentKlausur.getId() != 0);
        findViewById(R.id.buttonExamDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
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
                    } else if (eingabeDatum.getText().length() < 8 || getDate(eingabeDatum.getText().toString()) == null) {
                        snackbarDate.show();
                    } else {
                        save();
                        dismiss();
                    }
                }
            }
        });

        findViewById(R.id.calendarPickerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c      = Calendar.getInstance();
                int      mYear  = c.get(Calendar.YEAR);
                int      mMonth = c.get(Calendar.MONTH);
                int      mDay   = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();
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

    private void initEditTexts() {
        eingabeFach = (EditText) findViewById(R.id.eingabeFach);
        eingabeDatum = (EditText) findViewById(R.id.eingabeDatum);
        eingabeNotiz = (EditText) findViewById(R.id.eingabeNotiz);

        eingabeFach.setText(currentKlausur.getTitel());
        eingabeDatum.setText(Klausur.dateFormat.format(currentKlausur.getDatum()));
        eingabeNotiz.setText(currentKlausur.getNotiz());
    }

    private void save() {
        SQLiteConnectorKlausurplan database = new SQLiteConnectorKlausurplan(getContext());
        if (currentKlausur.getId() != 0) {
            database.setTitel(currentKlausur.getId(), eingabeFach.getText().toString());
            database.setDatum(currentKlausur.getId(), getDate(eingabeDatum.getText().toString()));
            database.setNotiz(currentKlausur.getId(), eingabeNotiz.getText().toString());
        } else {
            database.insert(eingabeFach.getText().toString(), "", getDate(eingabeDatum.getText().toString()), eingabeNotiz.getText().toString(), false, false);
        }
    }

    private void delete() {
        SQLiteConnectorKlausurplan database = new SQLiteConnectorKlausurplan(getContext());
        database.delete(currentKlausur.getId());
    }

    private Date getDate(String s) {
        if (!s.matches("[0-9]{2}.[0-9]{2}.[0-9]{2}"))
            return null;
        try {
            return Klausur.dateFormat.parse(s.substring(0, 6) + "20" + s.substring(6));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class mDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int yearL, int monthL, int dayL) {
            String dayS = String.valueOf(dayL);
            if (dayS.length() == 1) {
                dayS = "0" + dayS;
            }
            String monthS = String.valueOf(monthL + 1); //month is zero based!
            if (monthS.length() == 1) {
                monthS = "0" + monthS;
            }

            String yearS = String.valueOf(yearL);
            yearS = yearS.substring(2, 4);

            eingabeDatum.setText(dayS + "." + monthS + "." + yearS);
        }
    }
}
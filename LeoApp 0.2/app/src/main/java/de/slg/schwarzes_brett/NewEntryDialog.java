package de.slg.schwarzes_brett;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

class NewEntryDialog extends AlertDialog {

    private DatePickerDialog datePickerDialog;

    NewEntryDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.dialog_create_entry);
        initDatePicker();
        initSpinner();
        initTextViews();
        initButtons();

    }

    private void initTextViews() {
        final Button submit = (Button) findViewById(R.id.buttonSave);
        final TextView t1 = (TextView) findViewById(R.id.title_edittext);
        final TextView t2 = (TextView) findViewById(R.id.eingabeDatum);
        final TextView t3 = (TextView) findViewById(R.id.content);
        final Spinner t4 = (Spinner) findViewById(R.id.spinner2);

        TextWatcher listener = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submit.setEnabled(t1.getText().length() > 0 && t2.getText().length() > 0 && t3.getText().length() > 0 && t4.isSelected());
            }
        };

        t1.addTextChangedListener(listener);
        t2.addTextChangedListener(listener);
        t3.addTextChangedListener(listener);

    }

    private void initButtons() {

        findViewById(R.id.buttonDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendEntryTask().execute();
            }
        });
    }

    private void initDatePicker() {
        ImageButton dateButton = (ImageButton) findViewById(R.id.imageButton);
        TextView dateText = (TextView) findViewById(R.id.eingabeDatum);
        setDateTimeField(dateText);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        };
        dateButton.setOnClickListener(listener);
        dateText.setOnClickListener(listener);
    }

    private void setDateTimeField(final TextView t) {
        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        datePickerDialog = new DatePickerDialog(Utils.context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                t.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initSpinner() {

        Spinner s = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Utils.context,
                R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

    }

    private class sendEntryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

}

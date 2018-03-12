package de.slgdev.stundenplan.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.stundenplan.utility.Fach;

public class DetailsDialog extends AlertDialog {
    private Fach fach;

    private EditText etNotiz;
    private CheckBox cbSchrift;
    private TextView tvZeit;
    private TextView tvRaum;
    private TextView tvLehrer;
    private TextView title;

    private SQLiteConnectorStundenplan database;

    public DetailsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_stundenplan_details);

        database = new SQLiteConnectorStundenplan(getContext());

        tvZeit = findViewById(R.id.uhrzeit_details);
        tvRaum = findViewById(R.id.raumnr_details);
        tvLehrer = findViewById(R.id.lehrerK_details);
        etNotiz = findViewById(R.id.notizFeld_details);
        cbSchrift = findViewById(R.id.checkBox_schriftlich);
        title = findViewById(R.id.title_details);

        findViewById(R.id.buttonSav).setOnClickListener(view -> {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etNotiz.getWindowToken(), 0);
            String notiz = etNotiz.getText().toString();
            if (!fach.getKuerzel().equals("FREI")) {
                boolean b = cbSchrift.isChecked();
                fach.setzeSchriftlich(b);
                database.setWritten(b, fach.id);
            }
            fach.setzeNotiz(notiz);
            database.setNote(notiz, fach.id, fach.getTag(), fach.getStunde());
            dismiss();
        });

        setOnDismissListener(dialogInterface -> database.close());
    }

    public void init(Fach inFach) {
        fach = inFach;
        initDetails();
    }

    private void initDetails() {
        if (!fach.getKuerzel().equals("FREI")) {
            title.setText(fach.getName() + " " + fach.getKuerzel().substring(2));
            tvZeit.setText(database.getTimes(fach));
            tvRaum.setText(fach.getRaum());
            tvLehrer.setText(fach.getLehrer());
            etNotiz.setText(fach.getNotiz());
            cbSchrift.setChecked(fach.getSchriftlich());
            cbSchrift.setClickable(!database.mussSchriftlich(fach.id));
        } else {
            title.setText(getContext().getString(R.string.free_hour));
            tvRaum.setVisibility(View.GONE);
            tvLehrer.setVisibility(View.GONE);
            cbSchrift.setVisibility(View.GONE);
            findViewById(R.id.raum_details).setVisibility(View.GONE);
            findViewById(R.id.lehrer_details).setVisibility(View.GONE);
            tvZeit.setText(database.getTime(fach.getTag(), fach.getStunde()));
            etNotiz.setText(fach.getNotiz());
        }
    }
}
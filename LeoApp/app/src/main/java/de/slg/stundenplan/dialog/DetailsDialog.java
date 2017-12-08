package de.slg.stundenplan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slg.leoapp.utility.Utils;
import de.slg.stundenplan.utility.Fach;

public class DetailsDialog extends AlertDialog {
    private Fach fach;

    private EditText etNotiz;
    private CheckBox cbSchrift;
    private TextView tvZeit;
    private TextView tvRaum;
    private TextView tvLehrer;
    private TextView title;

    public DetailsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_stundenplan_details);
        tvZeit = (TextView) findViewById(R.id.uhrzeit_details);
        tvRaum = (TextView) findViewById(R.id.raumnr_details);
        tvLehrer = (TextView) findViewById(R.id.lehrerK_details);
        etNotiz = (EditText) findViewById(R.id.notizFeld_details);
        cbSchrift = (CheckBox) findViewById(R.id.checkBox_schriftlich);
        title = (TextView) findViewById(R.id.title_details);
        findViewById(R.id.buttonDis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.buttonSav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etNotiz.getWindowToken(), 0);
                String notiz = etNotiz.getText().toString();
                if (!fach.getKuerzel().equals("FREI")) {
                    boolean b = cbSchrift.isChecked();
                    fach.setzeSchriftlich(b);
                    Utils.getController().getStundenplanDatabase().setzeSchriftlich(b, fach.id);
                    SQLiteConnectorKlausurplan klausurplan = new SQLiteConnectorKlausurplan(getContext());
                    klausurplan.updateStundenplan(fach.getKlausurString(), b);
                }
                fach.setzeNotiz(notiz);
                Utils.getController().getStundenplanDatabase().setzeNotiz(notiz, fach.id, fach.getTag(), fach.getStunde());
                dismiss();
            }
        });
    }

    public void init(Fach inFach) {
        fach = inFach;
        initDetails();
    }

    private void initDetails() {
        if (!fach.getKuerzel().equals("FREI")) {
            title.setText(fach.getName() + " " + fach.getKuerzel().substring(2));
            tvZeit.setText(Utils.getController().getStundenplanDatabase().gibZeiten(fach));
            tvRaum.setText(fach.getRaum());
            tvLehrer.setText(fach.getLehrer());
            etNotiz.setText(fach.getNotiz());
            cbSchrift.setChecked(fach.getSchriftlich());
            cbSchrift.setClickable(!Utils.getController().getStundenplanDatabase().mussSchriftlich(fach.id));
        } else {
            title.setText(getContext().getString(R.string.free_hour));
            tvRaum.setVisibility(View.GONE);
            tvLehrer.setVisibility(View.GONE);
            cbSchrift.setVisibility(View.GONE);
            findViewById(R.id.raum_details).setVisibility(View.GONE);
            findViewById(R.id.lehrer_details).setVisibility(View.GONE);
            tvZeit.setText(Utils.getController().getStundenplanDatabase().gibZeit(fach.getTag(), fach.getStunde()));
//            etNotiz.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//            etNotiz.setLines(5);;
            etNotiz.setText(fach.getNotiz());
        }
    }

}
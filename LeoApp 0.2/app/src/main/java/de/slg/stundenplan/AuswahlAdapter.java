package de.slg.stundenplan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import de.slg.leoapp.List;
import de.slg.leoapp.R;

class AuswahlAdapter extends ArrayAdapter<Fach> {

    private final Context context;
    private final Fach[] fachArray;
    private final View[] views;
    private final Stundenplanverwalter sv;

    AuswahlAdapter(Context context, Fach[] pFacher, Stundenplanverwalter psv) {
        super(context, R.layout.list_item_kurs, pFacher);
        this.context = context;
        fachArray = pFacher;
        views = new View[fachArray.length];
        sv = psv;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (position < fachArray.length && fachArray[0] != null) {
            if (views[position] == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                int id = R.layout.list_item_kurs;
                view = layoutInflater.inflate(id, null);
            }
            view.setEnabled(true);
            Log.e("Test", "Fach: " + fachArray[position].gibName());
            Log.e("Test", "Kuerzel: " + fachArray[position].gibKurz());
            Log.e("Test", "Lehrer: " + fachArray[position].gibLehrer());
            Log.e("Test", "Stunden: " + fachArray[position].gibStunde());
            TextView twFach = (TextView) view.findViewById(R.id.fach_auswahl);
            TextView twKuerzel = (TextView) view.findViewById(R.id.kürzel_auswahl);
            TextView twLehrer = (TextView) view.findViewById(R.id.lehrer_auswahl);

            twFach.setText(fachArray[position].gibName());
            twKuerzel.setText(fachArray[position].gibKurz());
            twLehrer.setText(fachArray[position].gibLehrer());
            refresh();
        }
        views[position] = view;
        return view;
    }

    void refresh() {
        List<String> ausgewählteFächer = new List<>();
        List<String> ausgewählteStunden = new List<>();
        int[] selected = getSelectedIndices();
        for (int i : selected) {
            ausgewählteFächer.append(fachArray[i].gibName().split(" ")[0]);
            ArrayList<Fach> faecherInKurs = sv.sucheFacherKurzel(fachArray[i].gibKurz());
            for (Fach f : faecherInKurs) {
                ausgewählteStunden.append(f.gibStunde() + "." + f.gibTag());
            }
        }
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                CheckBox c = (CheckBox) views[i].findViewById(R.id.checkBox);
                if (c.isChecked()) {
                    views[i].setEnabled(true);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                } else if (ausgewählteFächer.contains(fachArray[i].gibName().split(" ")[0]) || ausgewählteStunden.contains(fachArray[i].gibStunde() + "." + fachArray[i].gibTag())) {
                    views[i].setEnabled(false);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                } else {
                    views[i].setEnabled(true);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                }
            }
        }
    }

    private int[] getSelectedIndices() {
        List<Integer> list = new List<>();
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                CheckBox c = (CheckBox) views[i].findViewById(R.id.checkBox);
                if (c.isChecked()) {
                    list.append(i);
                }
            }
        }
        int[] indices = new int[list.length()];
        list.toFirst();
        for (int i = 0; i < indices.length; i++, list.next()) {
            indices[i] = list.getContent();
        }
        return indices;
    }

    boolean isOneSelected() {
        for (View v : views) {
            if (v != null && ((CheckBox) v.findViewById(R.id.checkBox)).isChecked())
                return true;
        }
        return false;
    }

    Fach[] gibAlleMarkierten() {
        Fach[] mark = new Fach[gibAnzahlMarkierte()];
        int c = 0;
        for (int i = 0; i < fachArray.length; i++) {
            if (views[i] != null) {
                if (((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked()) {
                    ArrayList<Fach> f = sv.sucheFacherKurzel(fachArray[i].gibKurz());
                    for (int x = 0; x < f.size(); x++) {
                        mark[c] = f.get(x);
                        c++;
                    }
                }
            }
        }
        return mark;
    }

    private int gibAnzahlMarkierte() {
        int markierte = 0;
        for (int i = 0; i < fachArray.length; i++) {
            if (views[i] != null) {
                if (((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked()) {
                    ArrayList<Fach> f = sv.sucheFacherKurzel(fachArray[i].gibKurz());
                    markierte = markierte + f.size();
                }
            }
        }
        return markierte;
    }
}